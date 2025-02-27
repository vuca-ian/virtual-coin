package cn.virtual.coin.domain.sharding;

import com.dangdang.ddframe.rdb.sharding.api.ShardingValue;
import com.dangdang.ddframe.rdb.sharding.api.strategy.table.MultipleKeysTableShardingAlgorithm;
import com.google.common.collect.Sets;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author gdyang
 * @since  2021/8/16 3:52 下午
 */
public class SymbolPeriodMultiplesTableShardingAlgorithm  implements MultipleKeysTableShardingAlgorithm {
    private final String[]  actualColumns;

    public SymbolPeriodMultiplesTableShardingAlgorithm(String[] actualColumns) {
        this.actualColumns = actualColumns;
    }

    @Override
    public Collection<String> doSharding(Collection<String> availableTargetNames, Collection<ShardingValue<?>> shardingValues) {
//        Arrays.stream(actualColumns).map(col -> getShardingValue(shardingValues, col)).collect(Collectors.toSet())
        Set<List<Object>> values = Sets.cartesianProduct(getShardingValue(shardingValues, "symbol"),getShardingValue(shardingValues, "period"));
        List<String> result = new ArrayList<>();
        values.forEach(val -> {
            String suffix = val.stream().map(Object::toString).collect(Collectors.joining("_", "", ""));
            availableTargetNames.forEach(table -> {
                if(table.endsWith(suffix)){
                    result.add(table);
                }
            });
        });
        return result;
    }

    private Set<Object> getShardingValue(final Collection<ShardingValue<?>> shardingValues, final String shardingKey) {
        Set<Object> valueSet = new HashSet<>();
        ShardingValue<?> shardingValue = null;
        for (ShardingValue<?> each : shardingValues) {
            if (each.getColumnName().equals(shardingKey)) {
                shardingValue = each;
                break;
            }
        }
        if (null == shardingValue) {
            return valueSet;
        }
        switch (shardingValue.getType()) {
            case SINGLE:
                valueSet.add(shardingValue.getValue());
                break;
            case LIST:
                valueSet.addAll(shardingValue.getValues());
                break;
            case RANGE:
                for (Long i = (Long) shardingValue.getValueRange().lowerEndpoint(); i <= (Long)shardingValue.getValueRange().upperEndpoint(); i++) {
                    valueSet.add(i);
                }
                break;
            default:
                throw new UnsupportedOperationException();
        }
        return valueSet;
    }
}
