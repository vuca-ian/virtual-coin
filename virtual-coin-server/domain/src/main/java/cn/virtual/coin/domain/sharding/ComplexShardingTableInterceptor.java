package cn.virtual.coin.domain.sharding;

import cn.virtual.coin.domain.parser.SqlParser;
import com.baomidou.mybatisplus.core.plugins.InterceptorIgnoreHelper;
import com.baomidou.mybatisplus.core.toolkit.PluginUtils;
import com.baomidou.mybatisplus.extension.plugins.inner.InnerInterceptor;
import lombok.RequiredArgsConstructor;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;
import org.springframework.stereotype.Component;

import java.sql.SQLException;

/**
 * @author gdyang
 * @since 2025/3/2 16:40
 */
@Component
@RequiredArgsConstructor
public class ComplexShardingTableInterceptor implements InnerInterceptor {

    private final ComplexShardingTableAlgorithm  complexShardingTableAlgorithm;


    public void beforeQuery(Executor executor, MappedStatement ms, Object parameter, RowBounds rowBounds, ResultHandler resultHandler, BoundSql boundSql) throws SQLException {
        if (!InterceptorIgnoreHelper.willIgnoreDynamicTableName(ms.getId())) {
            PluginUtils.MPBoundSql mpBs = PluginUtils.mpBoundSql(boundSql);
            mpBs.sql(new SqlParser(mpBs, ms, parameter, complexShardingTableAlgorithm).parse());
        }
    }
}
