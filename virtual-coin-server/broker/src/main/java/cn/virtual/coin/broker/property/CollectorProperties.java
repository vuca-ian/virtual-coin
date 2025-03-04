package cn.virtual.coin.broker.property;

import lombok.Data;

/**
 * @author gdyang
 * @since 2025/3/3 23:02
 */
@Data
public class CollectorProperties {

    private String[] symbols;

    private String dialect;

    private String logicTable;
}
