package cn.virtual.coin.server.configuration;

import com.zaxxer.hikari.HikariDataSource;
import liquibase.UpdateSummaryEnum;
import liquibase.UpdateSummaryOutputEnum;
import liquibase.integration.spring.SpringLiquibase;
import liquibase.ui.UIServiceEnum;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.autoconfigure.liquibase.DataSourceClosingSpringLiquibase;
import org.springframework.boot.autoconfigure.liquibase.LiquibaseConnectionDetails;
import org.springframework.boot.autoconfigure.liquibase.LiquibaseDataSource;
import org.springframework.boot.autoconfigure.liquibase.LiquibaseProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.datasource.SimpleDriverDataSource;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import javax.sql.DataSource;

/**
 * @author gdyang
 * @since 2025/2/27 21:01
 */
@Configuration
public class LiquibaseDatasourceConfiguration {


    @Bean
    @ConfigurationProperties(prefix = "spring.liquibase", ignoreUnknownFields = false)
    public LiquibaseProperties properties(){
        return new LiquibaseProperties();
    }
    @Bean
    @ConditionalOnMissingBean(LiquibaseConnectionDetails.class)
    PropertiesLiquibaseConnectionDetails liquibaseConnectionDetails(LiquibaseProperties properties) {
        return new PropertiesLiquibaseConnectionDetails(properties);
    }

    public static final class PropertiesLiquibaseConnectionDetails implements LiquibaseConnectionDetails {

        private final LiquibaseProperties properties;

        PropertiesLiquibaseConnectionDetails(LiquibaseProperties properties) {
            this.properties = properties;
        }

        @Override
        public String getUsername() {
            return this.properties.getUser();
        }

        @Override
        public String getPassword() {
            return this.properties.getPassword();
        }

        @Override
        public String getJdbcUrl() {
            return this.properties.getUrl();
        }

        @Override
        public String getDriverClassName() {
            String driverClassName = this.properties.getDriverClassName();
            return (driverClassName != null) ? driverClassName : LiquibaseConnectionDetails.super.getDriverClassName();
        }

    }

//    @Bean("liquibase")
//    public DataSource buildDataSource(DataSourceProperties properties){
//        return properties.initializeDataSourceBuilder().type(HikariDataSource.class).build();
//    }

    @Bean
    @Primary
    public SpringLiquibase liquibase(DataSourceProperties dataSourceProperties, @LiquibaseDataSource ObjectProvider<DataSource> liquibaseDataSource,  PropertiesLiquibaseConnectionDetails liquibaseConnectionDetails, LiquibaseProperties properties) {
        SpringLiquibase liquibase = createSpringLiquibase(liquibaseDataSource.getIfAvailable(), dataSourceProperties.initializeDataSourceBuilder().type(HikariDataSource.class).build(), liquibaseConnectionDetails);
        liquibase.setChangeLog(properties.getChangeLog());
        liquibase.setClearCheckSums(properties.isClearChecksums());
        liquibase.setContexts(properties.getContexts());
        liquibase.setDefaultSchema(properties.getDefaultSchema());
        liquibase.setLiquibaseSchema(properties.getLiquibaseSchema());
        liquibase.setLiquibaseTablespace(properties.getLiquibaseTablespace());
        liquibase.setDatabaseChangeLogTable(properties.getDatabaseChangeLogTable());
        liquibase.setDatabaseChangeLogLockTable(properties.getDatabaseChangeLogLockTable());
        liquibase.setDropFirst(properties.isDropFirst());
        liquibase.setShouldRun(properties.isEnabled());
        liquibase.setLabelFilter(properties.getLabelFilter());
        liquibase.setChangeLogParameters(properties.getParameters());
        liquibase.setRollbackFile(properties.getRollbackFile());
        liquibase.setTestRollbackOnUpdate(properties.isTestRollbackOnUpdate());
        liquibase.setTag(properties.getTag());
        if (properties.getShowSummary() != null) {
            liquibase.setShowSummary(UpdateSummaryEnum.valueOf(properties.getShowSummary().name()));
        }
        if (properties.getShowSummaryOutput() != null) {
            liquibase
                    .setShowSummaryOutput(UpdateSummaryOutputEnum.valueOf(properties.getShowSummaryOutput().name()));
        }
        if (properties.getUiService() != null) {
            liquibase.setUiService(UIServiceEnum.valueOf(properties.getUiService().name()));
        }
        return liquibase;
    }

    private SpringLiquibase createSpringLiquibase(DataSource liquibaseDataSource, DataSource dataSource,
                                                  LiquibaseConnectionDetails connectionDetails) {
        DataSource migrationDataSource = getMigrationDataSource(liquibaseDataSource, dataSource, connectionDetails);
        SpringLiquibase liquibase = (migrationDataSource == liquibaseDataSource
                || migrationDataSource == dataSource) ? new SpringLiquibase()
                : new DataSourceClosingSpringLiquibase();
        liquibase.setDataSource(migrationDataSource);
        return liquibase;
    }

    private DataSource getMigrationDataSource(DataSource liquibaseDataSource, DataSource dataSource,
                                              LiquibaseConnectionDetails connectionDetails) {
        if (liquibaseDataSource != null) {
            return liquibaseDataSource;
        }
        String url = connectionDetails.getJdbcUrl();
        if (url != null) {
            DataSourceBuilder<?> builder = DataSourceBuilder.create().type(SimpleDriverDataSource.class);
            builder.url(url);
            applyConnectionDetails(connectionDetails, builder);
            return builder.build();
        }
        String user = connectionDetails.getUsername();
        if (user != null && dataSource != null) {
            DataSourceBuilder<?> builder = DataSourceBuilder.derivedFrom(dataSource)
                    .type(SimpleDriverDataSource.class);
            applyConnectionDetails(connectionDetails, builder);
            return builder.build();
        }
        Assert.state(dataSource != null, "Liquibase migration DataSource missing");
        return dataSource;
    }

    private void applyConnectionDetails(LiquibaseConnectionDetails connectionDetails,
                                        DataSourceBuilder<?> builder) {
        builder.username(connectionDetails.getUsername());
        builder.password(connectionDetails.getPassword());
        String driverClassName = connectionDetails.getDriverClassName();
        if (StringUtils.hasText(driverClassName)) {
            builder.driverClassName(driverClassName);
        }
    }
}
