package cn.virtual.coin.domain.parser;

import cn.virtual.coin.domain.sharding.ComplexShardingTableAlgorithm;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.core.toolkit.PluginUtils;
import com.baomidou.mybatisplus.extension.parser.JsqlParserSupport;
import lombok.extern.slf4j.Slf4j;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.operators.conditional.AndExpression;
import net.sf.jsqlparser.expression.operators.relational.EqualsTo;
import net.sf.jsqlparser.expression.operators.relational.ParenthesedExpressionList;
import net.sf.jsqlparser.schema.Column;
import net.sf.jsqlparser.schema.Table;
import net.sf.jsqlparser.statement.insert.Insert;
import net.sf.jsqlparser.statement.select.FromItem;
import net.sf.jsqlparser.statement.select.ParenthesedFromItem;
import net.sf.jsqlparser.statement.select.PlainSelect;
import net.sf.jsqlparser.statement.select.Select;
import net.sf.jsqlparser.statement.update.Update;
import org.apache.ibatis.binding.MapperMethod;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.SqlCommandType;
import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.session.Configuration;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author gdyang
 * @since 2025/3/2 16:35
 */
@Slf4j
public class SqlParser extends JsqlParserSupport {

    private static final Pattern PATTERN_LAMBDA_PARAMETER = Pattern.compile("(([\"`])?\\w+[\"`]?)\\s*(=|LIKE|IN)\\s*#\\{ew\\.paramNameValuePairs\\.(\\w+)}");
    private final PluginUtils.MPBoundSql boundSql;

    private final MappedStatement ms;

    private final Object parameter;

    private final ComplexShardingTableAlgorithm  complexShardingTableAlgorithm;
    private final Configuration configuration;

    public SqlParser(PluginUtils.MPBoundSql boundSql, MappedStatement ms, Object parameter, ComplexShardingTableAlgorithm  complexShardingTableAlgorithm) {
        this.boundSql = boundSql;
        this.ms = ms;
        this.parameter = parameter;
        this.configuration = ms.getConfiguration();
        this.complexShardingTableAlgorithm = complexShardingTableAlgorithm;
    }

    public String parse(){
        SqlCommandType sct = ms.getSqlCommandType();
        if (sct == SqlCommandType.INSERT || sct == SqlCommandType.UPDATE || sct == SqlCommandType.DELETE) {
            return super.parserMulti(boundSql.sql(), parameter);
        }
        return parserSingle(boundSql.sql(), parameter);
    }

    protected void processInsert(Insert insert, int index, String sql, Object obj) {
        Table table = insert.getTable();
        if(obj instanceof Collection<?> && !((Collection<?>) obj).isEmpty()){
            Object first = ((Collection<?>) obj).stream().findFirst().get();
            table.setName(this.complexShardingTableAlgorithm.doSharding(table.getName(), configuration.newMetaObject(first)));
        }else if(obj instanceof MapperMethod.ParamMap<?> paramMap) {
            Object list =  paramMap.get("list");
            if(list instanceof Collection<?> && !((Collection<?>) list).isEmpty()){
                Object first = ((Collection<?>) list).stream().findFirst().get();
                table.setName(this.complexShardingTableAlgorithm.doSharding(table.getName(), configuration.newMetaObject(first)));
            }
        }else{
            MetaObject object = configuration.newMetaObject(obj);
            table.setName(this.complexShardingTableAlgorithm.doSharding(table.getName(), object));
        }
    }

    protected void processUpdate(Update update, int index, String sql, Object obj) {
        final Table table = update.getTable();
        if(obj instanceof MapperMethod.ParamMap<?> paramMap){
//            LambdaUpdateWrapper<?> query = (LambdaUpdateWrapper<?>) paramMap.get("ew");
//            String fieldName = StrUtil.toCamelCase(column);
            Object value = paramMap.getOrDefault("et", null);
            if(null != value){
                MetaObject object = configuration.newMetaObject(value);
                table.setName(this.complexShardingTableAlgorithm.doSharding(table.getName(), object));
            }else{
                Expression expression = update.getWhere();
                Map<String, Object> parameter = new HashMap<>();
                parserExpression(expression, paramMap, parameter);
            }
        }else{
            MetaObject object = configuration.newMetaObject(obj);
            table.setName(this.complexShardingTableAlgorithm.doSharding(table.getName(), object));
        }
    }

    @Override
    protected void processSelect(Select select, int index, String sql, Object obj) {
        Select selectBody = select.getSelectBody();
        if(selectBody instanceof PlainSelect){
            processPlainSelect((PlainSelect) selectBody, obj);
        }
    }

    private void processPlainSelect(PlainSelect plainSelect, Object obj) {
        List<Table> mainTables = new ArrayList<>();

        FromItem fromItem = plainSelect.getFromItem();
        // 处理括号括起来的表达式
        while (fromItem instanceof ParenthesedFromItem) {
            fromItem = ((ParenthesedFromItem) fromItem).getFromItem();
        }
        if (fromItem instanceof Table fromTable) {
            mainTables.add(fromTable);
        }
        // 处理 where 中的子查询
        Expression where = plainSelect.getWhere();
        if (CollectionUtils.isNotEmpty(mainTables)) {
            plainSelect.setWhere(builderExpression(where, mainTables, obj));
        }
    }

    protected Expression builderExpression(Expression currentExpression, List<Table> tables, Object obj) {
        // 没有表需要处理直接返回
        if (CollectionUtils.isEmpty(tables)) {
            return currentExpression;
        }
        if(null == currentExpression){
            return null;
        }
        MapperMethod.ParamMap<?> paramMap = (MapperMethod.ParamMap<?>) obj;
        Map<String, Object> parameter = new HashMap<>();
        parserExpression(currentExpression, paramMap, parameter);

        tables.forEach(t -> t.setName(this.complexShardingTableAlgorithm.doSharding(t.getName(), configuration.newMetaObject(parameter))));
        return currentExpression;
    }


    private void parserExpression(Expression expression, MapperMethod.ParamMap<?> paramMap, Map<String, Object> parameter){
        if(expression instanceof ParenthesedExpressionList<?> parenthesed){
            parenthesed.forEach(e -> parserExpression(e, paramMap, parameter));
        }
        if(expression instanceof AndExpression){
            parserExpression(((AndExpression) expression).getLeftExpression(), paramMap, parameter);
            parserExpression(((AndExpression) expression).getRightExpression(), paramMap, parameter);
        }else if(expression instanceof EqualsTo){
            Column column = (Column) ((EqualsTo) expression).getLeftExpression();
            Object value = getParameterValue(column,paramMap);
            if(null != value){
                log.debug("column:{}, value:{}", column, value);
                parameter.put(column.getColumnName().replace("\"", ""), value);
            }
        }
    }

    private Object getParameterValue(Column column, MapperMethod.ParamMap<?> paramMap){
        if(paramMap.containsKey("ew") && paramMap.get("ew") instanceof LambdaQueryWrapper<?> queryWrapper){
            String variable = extractLambdaParameter(queryWrapper.getExpression().getSqlSegment(), column.getColumnName());
            if(variable != null){
                return ((LambdaQueryWrapper<?>) paramMap.get("ew")).getParamNameValuePairs().get(variable);
            }
        }
        return null;
    }

    public static String extractLambdaParameter(String parameter, String fieldName){
        Matcher matcher = PATTERN_LAMBDA_PARAMETER.matcher(parameter);
        while(matcher.find()) {
            String operator = matcher.group(1);
            String variable = matcher.group(4);
            if(operator.equals(fieldName)){
                return variable;
            }
        }
        return null;
    }
}
