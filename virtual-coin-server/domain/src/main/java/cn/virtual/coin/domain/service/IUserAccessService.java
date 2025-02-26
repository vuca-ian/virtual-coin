package cn.virtual.coin.domain.service;

import cn.virtual.coin.domain.dal.po.UserAccess;
import cn.vuca.cloud.dal.service.BaseService;

/**
 * @author gdyang
 * @since 2025/2/25 21:57
 */
public interface IUserAccessService extends BaseService<UserAccess> {
    UserAccess saveOrUpdateUser(UserAccess userAccess);
}
