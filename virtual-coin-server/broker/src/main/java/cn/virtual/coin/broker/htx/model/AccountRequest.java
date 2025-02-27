package cn.virtual.coin.broker.htx.model;

import cn.vuca.cloud.commons.beans.OrikaBeanMapper;
import lombok.Data;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author gdyang
 * @since 2025/2/25 21:09
 */
@Data
public class AccountRequest implements ApiModel<List<AccountRequest>>, Serializable {

    private Long id;
    private String type;
    private String subtype;
    private String state;
    @Override
    public String path() {
        return "/v1/account/accounts";
    }

    @Override
    public Map<String, Object> paramMap() {
        return new HashMap<>();
    }

    public AccountModel convertTo(String userId){
        AccountModel account = OrikaBeanMapper.convert(this, AccountModel.class);
        account.setUserId(userId);
        account.setAccountId(this.id);
        return account;
    }
}
