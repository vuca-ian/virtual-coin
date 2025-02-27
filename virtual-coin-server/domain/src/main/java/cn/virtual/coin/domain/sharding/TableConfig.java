package cn.virtual.coin.domain.sharding;

import lombok.Data;

/**
 * cn.virtual.coin.domain.sharding
 *
 * @author yang guo dong
 * @since 2025/2/27 15:30
 */
@Data
public class TableConfig{
    private String logicTable;
    private String[] actualColumns;
    private String[] actualTables;
}
