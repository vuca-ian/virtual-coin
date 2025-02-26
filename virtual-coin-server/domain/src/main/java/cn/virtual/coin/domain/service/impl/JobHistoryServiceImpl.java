package cn.virtual.coin.domain.service.impl;

import cn.virtual.coin.domain.dal.po.JobHistory;
import cn.virtual.coin.domain.service.IJobHistoryService;
import cn.vuca.cloud.dal.service.AbstractBaseServiceImpl;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import org.springframework.stereotype.Service;

/**
 * @author gdyang
 * @since 2025/2/26 23:10
 */
@Service
public class JobHistoryServiceImpl extends AbstractBaseServiceImpl<JobHistory> implements IJobHistoryService {

    @Override
    public JobHistory getHistoryBySymbolAndPeriod(String symbol, String period){
        return this.selectOne(Wrappers.<JobHistory>lambdaQuery().eq(JobHistory::getSymbol, symbol).eq(JobHistory::getPeriod, period));
    }
}
