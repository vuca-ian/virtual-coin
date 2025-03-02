package cn.virtual.coin.domain.sharding;

import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author gdyang
 * @since 2025/3/2 17:22
 */
@Component
public class ComplexShardingTableAlgorithm {
    private final ShardingAlgorithmProperty property;

    public ComplexShardingTableAlgorithm(ShardingAlgorithmProperty property) {
        this.property = property;
    }

    public String doSharding(String tableName, Map<String, Object> parameters) {
        String a = Arrays.stream(property.getAlgorithmColumns()).filter(parameters::containsKey).map(parameters::get).map(Object::toString).collect(Collectors.joining("_"));
        return tableName.equals(property.getLogicTable()) ? tableName + "_" + a : tableName;
    }
}
