package cn.virtual.coin.domain.service.impl;

import cn.virtual.coin.broker.htx.model.AccountModel;
import cn.virtual.coin.broker.htx.model.AccountRequest;
import cn.virtual.coin.broker.htx.utils.HttpService;
import cn.virtual.coin.broker.htx.utils.ServiceException;
import cn.virtual.coin.domain.dal.po.Account;
import cn.virtual.coin.domain.dal.po.UserAccess;
import cn.virtual.coin.domain.service.IAccountService;
import cn.virtual.coin.domain.service.IUserAccessService;
import cn.vuca.cloud.dal.service.AbstractBaseServiceImpl;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author gdyang
 * @since 2025/2/25 22:56
 */
@Service
@RequiredArgsConstructor
public class AccountServiceImpl extends AbstractBaseServiceImpl<Account> implements IAccountService {

    private final HttpService httpService;
    private final IUserAccessService userAccessService;


    @Override
    public AccountModel getAccount(String userName) {
        UserAccess userAccess = userAccessService.selectOne(Wrappers.<UserAccess>lambdaQuery().eq(UserAccess::getUserName, userName));
        if(null == userAccess){
            throw new ServiceException("用户不存在");
        }
        List<AccountRequest> list = httpService.doGet(new AccountRequest(), userAccess.getAccessKey(), userAccess.getSecretKey());
        return list.stream().map(acc -> acc.convertTo(userName)).collect(Collectors.toList()).get(0);
    }
}
