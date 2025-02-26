package cn.virtual.coin.broker.htx.model;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author gdyang
 * @since 2025/2/25 21:08
 */
public class AccountBalanceRequest implements ApiModel<AccountBalanceRequest> {
    private Long id;
    private String state;
    private String type;
    private List<Balance> list;

    @Override
    public String path() {
        return String.format("/v1/account/accounts/%d/balance", id);
    }

    @Override
    public Map<String, Object> paramMap() {
        return new HashMap<>();
    }
}
