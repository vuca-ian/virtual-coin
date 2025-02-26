package cn.virtual.coin.server.controller;

import cn.virtual.coin.broker.htx.model.AccountModel;
import cn.virtual.coin.broker.htx.utils.HttpService;
import cn.virtual.coin.domain.dal.po.UserAccess;
import cn.virtual.coin.domain.service.IAccountService;
import cn.virtual.coin.domain.service.IUserAccessService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * @author gdyang
 * @since 2025/2/25 21:14
 */
@RestController
@RequestMapping("/{version}/account")
@RequiredArgsConstructor
public class AccountController {

    private final HttpService httpService;
    private final IUserAccessService userAccessService;
    private final IAccountService accountService;

    @PutMapping("/user")
    public void saveUser(@RequestBody UserAccess userAccess){
        userAccessService.saveOrUpdateUser(userAccess);
    }

    @GetMapping
    public AccountModel getAccount(@RequestParam("userName") String userName){
        return accountService.getAccount(userName);
    }
}
