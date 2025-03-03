package cn.virtual.coin.server.job;

import cn.virtual.coin.broker.htx.utils.CandlestickInterval;
import cn.virtual.coin.domain.dal.po.JobHistory;
import cn.virtual.coin.domain.service.IJobHistoryService;
import cn.virtual.coin.server.configuration.JobTask;
import cn.virtual.coin.websocket.ConnectionState;
import cn.virtual.coin.websocket.WebSocketConnection;
import com.alibaba.fastjson.JSONObject;
import jakarta.annotation.Resource;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.quartz.QuartzJobBean;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;

/**
 * @author gdyang
 * @since 2025/2/26 23:03
 */
@EqualsAndHashCode(callSuper = true)
@Slf4j
@Data
@RequiredArgsConstructor
@JobTask(name = "DataFetcher", cron = "0 0/1 * * * ?")
public class DataFetcherJobTask extends QuartzJobBean {
    public static final String WEBSOCKET_CANDLESTICK_TOPIC = "market.$symbol$.kline.$period$";
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH");
    private final DateTimeFormatter DEFAULT_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    @Value("${broker.symbol.startTime:2017-10-25 00:00:00}")
    private String startTime;
    @Value("${broker.support.symbols:ethusdt}")
    private String[] supportSymbol = new String[]{"ethusdt"};

    private final IJobHistoryService jobHistoryService;
    @Resource(name = "ws_v1")
    private WebSocketConnection connection;
    @Override
    protected void executeInternal(@NonNull JobExecutionContext context) throws JobExecutionException {
        if(connection.getState() != ConnectionState.CONNECTED){
            return;
        }
        log.info("start fetch history data.");
        if(null == supportSymbol || supportSymbol.length == 0){
            return;
        }
        Arrays.stream(supportSymbol).forEach(this::fetch);
    }

    private void fetch(String symbol){
        Arrays.stream(CandlestickInterval.values()).forEach(interval -> fetch(symbol, interval));
    }

    private void fetch(String symbol, CandlestickInterval interval){
        JobHistory jobHistory = jobHistoryService.getHistoryBySymbolAndPeriod(symbol, interval.getCode());
        LocalDateTime next = null;
        if(null == jobHistory){
            jobHistory = JobHistory.builder().lastDataTime(startTime).symbol(symbol).period(interval.getCode()).loopCount(1).build();
            jobHistoryService.save(jobHistory);
        }
        next = LocalDateTime.from(DEFAULT_FORMATTER.parse(jobHistory.getLastDataTime()));
        String topic = WEBSOCKET_CANDLESTICK_TOPIC
                .replace("$symbol$", jobHistory.getSymbol())
                .replace("$period$", interval.getCode());
        int nextMinus = 60 * interval.getNum();
        long to = next.plusMinutes(nextMinus + 1).atZone(ZoneId.systemDefault()).toInstant().getEpochSecond();
        if(next.atZone(ZoneId.systemDefault()).toInstant().getEpochSecond() * 1000 > System.currentTimeMillis()){
            return;
        }
        if(to * 1000 > System.currentTimeMillis()){
            to  = System.currentTimeMillis() /1000;
        }
        JSONObject command = new JSONObject();
        command.put("req", topic);
        command.put("id", System.nanoTime());
        command.put("from", next.minusMinutes(1).atZone(ZoneId.systemDefault()).toInstant().getEpochSecond());
        command.put("to", to);
        log.info("send command to fetch data of leaking,{}, {}",
                DEFAULT_FORMATTER.format(next), command.toJSONString());
        connection.send(command.toJSONString());
    }
}
