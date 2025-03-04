package cn.virtual.coin.broker.htx.filter;

import cn.virtual.coin.broker.CandlestickEvent;
import cn.virtual.coin.broker.htx.utils.CandlestickInterval;
import cn.virtual.coin.broker.htx.utils.WebSocketConstants;
import cn.virtual.coin.domain.dal.po.Candlestick;
import cn.virtual.coin.domain.dal.po.JobHistory;
import cn.virtual.coin.domain.service.ICandlestickService;
import cn.virtual.coin.websocket.WebSocketConnection;
import cn.virtual.coin.websocket.chain.Filter;
import cn.virtual.coin.websocket.chain.FilterChain;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;
import org.springframework.lang.NonNull;

import java.util.List;
import java.util.concurrent.Executor;

/**
 * @author gdyang
 * @since  2021/8/2 1:36 下午
 */
@Slf4j
public class CandlestickHistoryFilter implements Filter<JSONObject>, ApplicationEventPublisherAware {
    private ApplicationEventPublisher applicationEventPublisher;
    @Resource
    private Executor executor;
    @Resource
    private ICandlestickService candlestickService;
    @Override
    public void doFilter(JSONObject data, WebSocketConnection connection, FilterChain chain) {
        if (data.containsKey("err-msg")) {
            log.error("request data: {}", data);
        }
        if (data.containsKey(WebSocketConstants.REP) && data.getString(WebSocketConstants.REP).contains(WebSocketConstants.K_LINE)) {
            try {
                String ch = data.getString(WebSocketConstants.REP);
                List<Candlestick> list = data.getJSONArray("data").toJavaList(Candlestick.class);
                String type = ch.substring(ch.lastIndexOf(".") + 1);
                CandlestickInterval interval = CandlestickInterval.accept(type);
                String symbol = ch.substring(7, ch.indexOf("kline") - 1);
//                log.info("{}, {}, {}", symbol, type, list);

                    executor.execute(()-> {
                        if(CollectionUtils.isNotEmpty(list)){
                            list.forEach(candlestick -> {
                                candlestick.setSymbol(symbol);
                                candlestick.setPeriod(interval.getCode());
                                candlestickService.saveCandlestick(candlestick);
                            });
                            applicationEventPublisher.publishEvent(new CandlestickEvent(new JobHistory(symbol, interval.getCode()), CandlestickEvent.EventType.batch));
                        }
                    });
            } catch (Exception e) {
                log.error("request candlestick error", e);
                throw new RuntimeException(e);
            }
        } else {
            chain.doFilter(data, connection);
        }
    }


    @Override
    public boolean supports(JSONObject data) {
        return data.containsKey(WebSocketConstants.REP) && data.getString(WebSocketConstants.REP).contains(WebSocketConstants.K_LINE);
    }

    @Override
    public void setApplicationEventPublisher(@NonNull ApplicationEventPublisher applicationEventPublisher) {
        this.applicationEventPublisher = applicationEventPublisher;
    }
}
