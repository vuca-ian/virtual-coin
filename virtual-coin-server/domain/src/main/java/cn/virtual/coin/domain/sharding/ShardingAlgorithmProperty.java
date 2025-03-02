package cn.virtual.coin.domain.sharding;

import lombok.Data;

/**
 * @author gdyang
 * @since 2025/3/2 17:19
 */
@Data
public class ShardingAlgorithmProperty {

    private String logicTable;

    private String[] algorithmColumns;

    private String[] actualTables;
}
