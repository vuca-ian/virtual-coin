package cn.virtual.coin.server.listener;

import cn.virtual.coin.broker.CandlestickEvent;
import cn.virtual.coin.broker.htx.utils.CandlestickInterval;
import cn.virtual.coin.domain.dal.po.JobHistory;
import cn.virtual.coin.domain.service.IJobHistoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

/**
 * cn.virtual.coin.server.listener
 *
 * @author yang guo dong
 * @since 2025/3/3 16:34
 */
@Component
@RequiredArgsConstructor
public class CandlestickListener implements ApplicationListener<CandlestickEvent> {
    private final DateTimeFormatter DEFAULT_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private final IJobHistoryService jobHistoryService;
    @Override
    public void onApplicationEvent(CandlestickEvent event) {
        CandlestickEvent.EventType type = event.getType();

        switch (type){
            case batch -> updateJobHistory((JobHistory) event.getSource());
            case null, default -> {}
        }
    }


    private void updateJobHistory(JobHistory jobHistory) {
        try {
            CandlestickInterval interval = CandlestickInterval.accept(jobHistory.getPeriod());
            int nextMinus = 60 * interval.getNum();
            JobHistory history = jobHistoryService.getHistoryBySymbolAndPeriod(jobHistory.getSymbol(), interval.getCode());
            LocalDateTime next = LocalDateTime.from(DEFAULT_FORMATTER.parse(history.getLastDataTime()));;
            if(next.atZone(ZoneId.systemDefault()).toInstant().getEpochSecond() * 1000 > System.currentTimeMillis()){
                return;
            }
            history.setLastDataTime(DEFAULT_FORMATTER.format(next.plusMinutes(nextMinus)));
            jobHistoryService.update(history);

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


}
