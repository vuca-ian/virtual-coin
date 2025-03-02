package cn.virtual.coin.domain;

import cn.virtual.coin.domain.sharding.ComplexShardingTableInterceptor;
import cn.virtual.coin.domain.sharding.ShardingAlgorithmProperty;
import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.autoconfigure.MybatisPlusPropertiesCustomizer;
import com.baomidou.mybatisplus.core.config.GlobalConfig;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * @author gdyang
 * @since 2025/2/25 22:38
 */
@ComponentScan
@Configuration
@MapperScan("cn.virtual.coin.domain.dal.mapper")
public class DomainAutoConfiguration {

    @Bean
    @ConfigurationProperties(prefix = "sharding.rule.tables")
    public ShardingAlgorithmProperty shardingAlgorithmProperty(){
        return new ShardingAlgorithmProperty();
    }


    @Bean
    public MybatisPlusInterceptor mybatisPlusInterceptor(ComplexShardingTableInterceptor complexShardingTableInterceptor) {
        MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();
        interceptor.addInnerInterceptor(new PaginationInnerInterceptor(DbType.POSTGRE_SQL));
        interceptor.addInnerInterceptor(complexShardingTableInterceptor);
        return interceptor;
    }


    @Bean
    public MybatisPlusPropertiesCustomizer plusPropertiesCustomizer() {
        return plusProperties -> {
            GlobalConfig.DbConfig dbConfig = new GlobalConfig.DbConfig();
            dbConfig.setColumnFormat("\"%s\""); // 自动添加反引号
            plusProperties.setGlobalConfig(new GlobalConfig().setDbConfig(dbConfig));
        };
    }
}
