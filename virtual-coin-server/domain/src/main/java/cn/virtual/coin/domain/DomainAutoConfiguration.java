package cn.virtual.coin.domain;

import org.mybatis.spring.annotation.MapperScan;
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
}
