package cn.virtual.coin.broker.htx.model;

import lombok.Data;

/**
 * @author gdyang
 * @since 2025/2/25 21:10
 */
@Data
public class AccountModel {
    private Long id;

    private String userId;

    private Long accountId;

    private String type;

    private String subtype;

    private String state;
}
