package cn.virtual.coin.domain.sharding;


import org.apache.shardingsphere.sharding.api.sharding.ShardingValue;
import org.apache.shardingsphere.sharding.api.sharding.complex.ComplexKeysShardingAlgorithm;
import org.apache.shardingsphere.sharding.api.sharding.complex.ComplexKeysShardingValue;

import java.util.Collection;
import java.util.List;

/**
 * cn.virtual.coin.domain.sharding
 *
 * @author yang guo dong
 * @since 2025/2/27 10:58
 */
public class SymbolPeriodMultiplesTableShardingAlgorithm implements ComplexKeysShardingAlgorithm<String> {



    @Override
    public Collection<String> doSharding(Collection collection, ComplexKeysShardingValue complexKeysShardingValue) {
        return collection;
    }
}
