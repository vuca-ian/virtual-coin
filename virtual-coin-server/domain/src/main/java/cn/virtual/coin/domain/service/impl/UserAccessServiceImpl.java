package cn.virtual.coin.domain.service.impl;

import cn.virtual.coin.domain.dal.po.UserAccess;
import cn.virtual.coin.domain.service.IUserAccessService;
import cn.vuca.cloud.api.exception.ServiceException;
import cn.vuca.cloud.commons.utils.StringUtils;
import cn.vuca.cloud.dal.service.AbstractBaseServiceImpl;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

/**
 * @author gdyang
 * @since 2025/2/25 21:58
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UserAccessServiceImpl extends AbstractBaseServiceImpl<UserAccess> implements IUserAccessService {

    @Override
    public UserAccess saveOrUpdateUser(UserAccess userAccess){
        List<UserAccess> checkIfExists = this.select(Wrappers.<UserAccess>lambdaQuery().eq(UserAccess::getUserName, userAccess.getUserName()).ne(StringUtils.isNotBlank(userAccess.getId()), UserAccess::getId, userAccess.getId()));
        if(CollectionUtils.isNotEmpty(checkIfExists)){
            throw new ServiceException("用户已存在！");
        }
        if(userAccess.getId() == null){
            userAccess.setCreatedBy("admin");
            userAccess.setCreatedTime(new Date());
        }
        userAccess.setModifiedBy("admin");
        userAccess.setModifiedTime(new Date());
        userAccess.setDeleted(false);
        super.saveOrUpdate(userAccess);
        return userAccess;
    }
}
