//package cn.virtual.coin.domain.sharding;
//
//import cn.vuca.cloud.commons.maps.Maps;
//import com.zaxxer.hikari.HikariDataSource;
//
//import lombok.Data;
//import lombok.Getter;
//import lombok.RequiredArgsConstructor;
//import lombok.Setter;
//import org.apache.shardingsphere.driver.jdbc.core.datasource.ShardingSphereDataSource;
//import org.apache.shardingsphere.sharding.route.strategy.ShardingStrategy;
//import org.apache.shardingsphere.sharding.route.strategy.type.complex.ComplexShardingStrategy;
//import org.apache.shardingsphere.sharding.rule.ShardingRule;
//import org.apache.shardingsphere.sharding.rule.TableRule;
//import org.springframework.beans.factory.FactoryBean;
//import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
//import org.springframework.boot.context.properties.bind.Binder;
//import org.springframework.context.EnvironmentAware;
//import org.springframework.core.env.Environment;
//import org.springframework.lang.NonNull;
//
//import javax.sql.DataSource;
//import java.sql.SQLException;
//import java.util.*;
//import java.util.stream.Collectors;
//
///**
// * cn.virtual.coin.domain.sharding
// *
// * @author yang guo dong
// * @since 2025/2/27 11:11
// */
//public class ShardingDataSourceRegistrar implements FactoryBean<ShardingSphereDataSource>, EnvironmentAware {
//    private static final String SYMBOL = "symbol";
////    private final KeyGenerator keyGenerator = new DefaultKeyGenerator();
////    private final ShardingStrategy noneShardingStrategy = new NoneShardingStrategy();
////    private final DataSource dataSource;
//    @Getter
//    @Setter
//    private String[] symbols;
//    @Getter
//    @Setter
//    private List<TableConfig> tables;
//    private Environment environment;
//    private ShardingSphereDataSource shardingDataSource;
////    public ShardingDataSourceRegistrar(DataSource dataSource) {
////        this.dataSource = dataSource;
////    }
//
//    private DataSource buildDataSource(){
//        DataSourceProperties dataSourceProperties = Binder.get(environment).bind("spring.datasource", DataSourceProperties.class).get();
//        return dataSourceProperties.initializeDataSourceBuilder().type(HikariDataSource.class).build();
//    }
//
//    @Override
//    public ShardingSphereDataSource getObject() throws Exception {
//        return shardingDataSource;
//    }
//
////    private TableRule buildTableRule(TableConfig tableConfig, Map<String, DataSource> dataSourceMap){
////        List<String> tables = buildTable(tableConfig, this.getSymbols());
////        ShardingStrategy tableShardingStrategy = new ComplexShardingStrategy(String.join(",", tables), new SymbolPeriodMultiplesTableShardingAlgorithm());
////        return new TableRule(tableConfig.getLogicTable(), tables, dataSourceMap, noneShardingStrategy ,tableShardingStrategy, "", keyGenerator, "");
////    }
//
//    private List<String> buildTable(TableConfig tableConfig, String[] symbols){
//        List<String> actualTables = new ArrayList<>();
//        Arrays.stream(tableConfig.getActualTables()).forEach(tab -> {
//            Arrays.stream(symbols).forEach(symbol -> {
//                actualTables.add(tab.replaceAll(SYMBOL, symbol));
//            });
//        });
//        return actualTables;
//    }
//
//    @Override
//    public Class<?> getObjectType() {
//        return ShardingSphereDataSource.class;
//    }
//
//    @Override
//    public void setEnvironment(@NonNull Environment environment) {
//        this.environment = environment;
//        try {
//            ShardingDataSourceRegistrar rules = Binder.get(environment).bind("sharding.rule", ShardingDataSourceRegistrar.class).get();
//            this.symbols = rules.getSymbols();
//            Map<String, DataSource> dataSourceMap = Maps.of("master", buildDataSource()).build();
////            List<TableRule> tableRules = rules.getTables().stream().map(t -> buildTableRule(t, dataSourceMap)).collect(Collectors.toList());
////            ShardingRule rule = new ShardingRule(dataSourceMap, "master", tableRules, Collections.singletonList("default"),this.noneShardingStrategy, null, this.keyGenerator);
////            shardingDataSource = new ShardingSphereDataSource(rule);
//        } catch (Exception e) {
//            throw new RuntimeException(e);
//        }
//    }
//}
