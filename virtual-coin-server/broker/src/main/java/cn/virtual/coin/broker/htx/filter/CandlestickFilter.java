package cn.virtual.coin.broker.htx.filter;

import cn.virtual.coin.broker.htx.utils.CandlestickInterval;
import cn.virtual.coin.broker.htx.utils.ServiceException;
import cn.virtual.coin.broker.htx.utils.WebSocketConstants;
import cn.virtual.coin.domain.dal.po.Candlestick;
import cn.virtual.coin.domain.service.ICandlestickService;
import cn.virtual.coin.websocket.WebSocketConnection;
import cn.virtual.coin.websocket.chain.Filter;
import cn.virtual.coin.websocket.chain.FilterChain;
import com.alibaba.fastjson.JSONObject;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;

/**
 * @author gdyang
 * @since 2021/8/2 10:44 上午
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class CandlestickFilter implements Filter<JSONObject>, ApplicationEventPublisherAware {

//    private final Map<String, Object> PERIOD = new ConcurrentHashMap<>();
//    private final Map<String, Candlestick> candlestickMap = new ConcurrentHashMap<>();
    private ApplicationEventPublisher applicationEventPublisher;
    private final ICandlestickService candlestickService;
    @Override
    public void doFilter(JSONObject data, WebSocketConnection connection, FilterChain chain) {
        if(data.containsKey(WebSocketConstants.CH) && data.getString(WebSocketConstants.CH).contains(WebSocketConstants.K_LINE)){
            try{
                String ch = data.getString(WebSocketConstants.CH);
                String type = ch.substring(ch.lastIndexOf(".") + 1);
                CandlestickInterval interval = CandlestickInterval.accept(type);
                String symbol = ch.substring(7, ch.indexOf(WebSocketConstants.K_LINE) - 1);
                JSONObject tick = data.getJSONObject(WebSocketConstants.TICK);
                Candlestick candlestick = tick.toJavaObject(Candlestick.class);
                candlestick.setSymbol(symbol);
//                candlestick.setOpenTime(candlestick.getId() * 1000);
                candlestick.setPeriod(interval.getCode());
                candlestickService.saveCandlestick(candlestick);
//                if(!PERIOD.containsKey(interval.getCode())){
//                    PERIOD.put(interval.getCode(), candlestick.getId());
//                }
//                String key = interval.getCode() + ":" + candlestick.getId();
//                if(!candlestickMap.containsKey(key)){
//                    String lastKey = interval.getCode() + ":" + PERIOD.get(interval.getCode());
//                    Candlestick lastCandlestick = candlestickMap.get(lastKey);
//                    if(null != lastCandlestick){
//                        applicationEventPublisher.publishEvent(new CandlestickEvent(lastCandlestick, interval));
//                        candlestickMap.remove(lastKey);
//                        PERIOD.put(interval.getCode(), candlestick.getId());
//                    }
//                }
//                candlestickMap.put(key, candlestick);
                return;
            }catch (Exception e){
                throw new ServiceException("查询失败！",e);
            }
        }
        chain.doFilter(data, connection);
    }

    @Override
    public void setApplicationEventPublisher(@NonNull ApplicationEventPublisher applicationEventPublisher) {
        this.applicationEventPublisher = applicationEventPublisher;
    }
}
