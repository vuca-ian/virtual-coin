package cn.virtual.coin.domain;

import cn.virtual.coin.domain.sharding.ShardingDataSourceRegistrar;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import javax.sql.DataSource;
import javax.xml.crypto.Data;

/**
 * @author gdyang
 * @since 2025/2/25 22:38
 */
@ComponentScan
@Configuration
@MapperScan("cn.virtual.coin.domain.dal.mapper")
public class DomainAutoConfiguration {

    @Bean
    public ShardingDataSourceRegistrar shardingDataSourceRegistrar() throws Exception {
        return new ShardingDataSourceRegistrar();
    }

    @Bean
    @Primary
    public DataSource dataSource(ShardingDataSourceRegistrar registrar) throws Exception {
        return registrar.getObject();
    }
}
