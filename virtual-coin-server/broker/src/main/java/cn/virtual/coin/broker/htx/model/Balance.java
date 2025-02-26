package cn.virtual.coin.broker.htx.model;

import lombok.Data;

/**
 * @author gdyang
 * @since 2025/2/25 21:08
 */
@Data
public class Balance {
    private Long accountId;
    private String currency;
    private String balance;
    private String type;
}
