package cn.virtual.coin.domain.sharding;

import com.dangdang.ddframe.rdb.sharding.api.rule.BindingTableRule;
import com.dangdang.ddframe.rdb.sharding.api.rule.DataSourceRule;
import com.dangdang.ddframe.rdb.sharding.api.rule.ShardingRule;
import com.dangdang.ddframe.rdb.sharding.api.rule.TableRule;
import com.dangdang.ddframe.rdb.sharding.api.strategy.table.TableShardingStrategy;
import com.dangdang.ddframe.rdb.sharding.config.ShardingPropertiesConstant;
import com.dangdang.ddframe.rdb.sharding.jdbc.core.datasource.ShardingDataSource;
import com.zaxxer.hikari.HikariDataSource;
import lombok.SneakyThrows;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.bind.Binder;
import org.springframework.context.EnvironmentAware;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author gdyang
 * @since  2021/7/23 10:36 上午
 */
public class ShardingDataSourceRegistrar implements FactoryBean<ShardingDataSource>, EnvironmentAware {
    private static final String SYMBOL = "symbol";

    private Environment environment;
    private ShardingDataSource shardingDataSource;


    private List<TableConfig> tables;
    private String[] symbols;
    @Override
    public ShardingDataSource getObject() throws Exception {
        return shardingDataSource;
    }

    private DataSource buildDataSource(){
        DataSourceProperties dataSourceProperties = Binder.get(environment).bind("spring.datasource", DataSourceProperties.class).get();
        return dataSourceProperties.initializeDataSourceBuilder().type(HikariDataSource.class).build();
    }

    @Override
    public Class<?> getObjectType() {
        return ShardingDataSource.class;
    }

    @SneakyThrows
    @Override
    public void setEnvironment(Environment environment) {
        this.environment = environment;
        DataSource dataSource = buildDataSource();
        Map<String,DataSource> dsMap = new HashMap<>();
        dsMap.put("master", dataSource);
        DataSourceRule dataSourceRule = new DataSourceRule(dsMap, "master");
        ShardingDataSourceRegistrar rules = Binder.get(environment).bind("sharding.rule", ShardingDataSourceRegistrar.class).get();


        List<TableRule> tableRuleList = rules.getTables().stream().map(config ->
            TableRule.builder(config.getLogicTable()).actualTables(buildTableRule(config, rules.getSymbols()))
                        .tableShardingStrategy(new TableShardingStrategy(Arrays.asList(config.getActualColumns()), new SymbolPeriodMultiplesTableShardingAlgorithm(config.getActualColumns())))
                    .dataSourceRule(dataSourceRule).build()
        ).collect(Collectors.toList());
        ShardingRule.ShardingRuleBuilder builder = ShardingRule.builder()
                .dataSourceRule(dataSourceRule)
                .tableRules(tableRuleList)
                .bindingTableRules(Collections.singletonList(new BindingTableRule(tableRuleList)));
        Properties prop = new Properties();
        prop.setProperty(ShardingPropertiesConstant.SQL_SHOW.getKey(), "false");
        shardingDataSource = new ShardingDataSource(builder.build(), prop);
    }

    private List<String> buildTableRule(TableConfig tableConfig, String[] symbols){
        List<String> actualTables = new ArrayList<>();
        Arrays.stream(tableConfig.getActualTables()).forEach(tab -> {
            Arrays.stream(symbols).forEach(symbol -> {
                actualTables.add(tab.replaceAll(SYMBOL, symbol));
            });
        });
        return actualTables;
    }

    public List<TableConfig> getTables() {
        return tables;
    }

    public void setTables(List<TableConfig> tables) {
        this.tables = tables;
    }

    public String[] getSymbols() {
        return symbols;
    }

    public void setSymbols(String[] symbols) {
        this.symbols = symbols;
    }
}
