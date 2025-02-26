package cn.virtual.coin.server.job;

import cn.virtual.coin.server.configuration.JobTask;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.scheduling.quartz.QuartzJobBean;

/**
 * @author gdyang
 * @since 2025/2/26 21:35
 */
@Slf4j
@JobTask(cron = "0 0/2 * * * ?", name = "IndicatorJobTask")
public class IndicatorJobTask extends QuartzJobBean {
    @Override
    protected void executeInternal(@NonNull JobExecutionContext context) throws JobExecutionException {
        log.info("任务开始执行。。。");
    }
}
