package cn.virtual.coin.domain.service;

import cn.virtual.coin.domain.dal.po.JobHistory;
import cn.vuca.cloud.dal.service.BaseService;

/**
 * @author gdyang
 * @since 2025/2/26 23:10
 */
public interface IJobHistoryService extends BaseService<JobHistory> {
    JobHistory getHistoryBySymbolAndPeriod(String symbol, String period);
}
