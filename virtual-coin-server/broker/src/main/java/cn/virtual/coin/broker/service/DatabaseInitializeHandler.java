package cn.virtual.coin.broker.service;

import cn.virtual.coin.broker.htx.utils.CollectionUtils;
import cn.virtual.coin.broker.property.CollectorProperties;
import cn.vuca.cloud.api.exception.ServiceException;
import cn.vuca.cloud.commons.utils.StringUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.datasource.DataSourceException;
import org.springframework.beans.factory.BeanClassLoaderAware;
import org.springframework.core.io.ClassPathResource;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.function.Function;

/**
 * @author gdyang
 * @since 2025/3/3 22:59
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class DatabaseInitializeHandler implements BeanClassLoaderAware{

    private final DataSource dataSource;


    private ClassLoader classLoader;

    private final static String DDL_SQL_FILE  = "META-INF/sql/%s/candlestick.sql.template";


    public void createTable(String dialect, String tablePrefix, String symbol, String period){
        if(check(dialect,tablePrefix, symbol, period)){
            return;
        }
        String template = getSqlFile(dialect.toLowerCase(Locale.ROOT));
        String sql = template.replaceAll("\\{table}", tablePrefix).replaceAll("\\{symbol}", symbol).replaceAll("\\{period}", period);
        log.info("[Connection Send]{}", sql);
        execute(sql);
    }

    public boolean check(String dialect,String tablePrefix, String symbol, String period){
        Dialect  dialects = Dialect.valueOf(dialect.toUpperCase(Locale.ROOT));
        List<Object> names = (List<Object>) execute(dialects.checkTable(), resultSet -> getValue(resultSet, "name"), String.join("_", tablePrefix, symbol, period));
        return CollectionUtils.isNotEmpty(names) && StringUtils.isNotBlank(Optional.ofNullable(names.getFirst()).map(Object::toString).orElse(null));
    }

    private String getSqlFile(String dialect){
        ClassPathResource classPathResource = new ClassPathResource(String.format(DDL_SQL_FILE, dialect), classLoader);
        try(BufferedReader br = new BufferedReader(new FileReader(classPathResource.getFile()))){
            StringBuilder content = new StringBuilder();
            String line = null;
            while((line = br.readLine()) != null){
                content.append(line).append("\n");
            }
            return content.toString();
        } catch (IOException e) {
            log.error("读取kms.sql文件失败!", e);
            throw new ServiceException("读取kms.sql文件失败!", e);
        }
    }

    private void execute(final String sql){
        try(Connection connection = dataSource.getConnection()){
            log.debug("final sql:\n{}", sql);
            connection.createStatement().executeUpdate(sql);
        }catch (SQLException e){
            log.error("连接数据源失败", e);
            throw new DataSourceException("连接数据源失败", e);
        }
    }

    private <T> Collection<T> execute(final String sql, Function<ResultSet, T> function, Object ... args){
        try(Connection connection = dataSource.getConnection()){
            PreparedStatement statement =  connection.prepareStatement(sql);
            if(null != args && args.length > 0){
                for(int i = 0; i < args.length; ++i) {
                    Object arg = args[i];
                    statement.setObject(i + 1, arg);
                }
            }
            List<T> list = new ArrayList<>();
            ResultSet resultSet = statement.executeQuery();
            while(resultSet.next()){
                list.add(function.apply(resultSet));
            }
            return list;
        }catch (SQLException e){
            log.error("连接数据源失败", e);
            throw new DataSourceException("连接数据源失败", e);
        }
    }

    private Object getValue(ResultSet set, String column){
        try {
            return set.getObject(column);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void setBeanClassLoader(@NonNull ClassLoader classLoader) {
        this.classLoader = classLoader;
    }

    enum Dialect {
        POSTGRESQL{
            @Override
            public String checkTable() {
                return "SELECT tablename as name FROM pg_tables WHERE tablename = ? ";
            }
        },
        MYSQL{
            @Override
            public String checkTable() {
                return "";
            }
        };

        public abstract String checkTable();
    }
}
