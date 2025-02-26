//package cn.virtual.coin.broker.htx.filter;
//
//import com.alibaba.fastjson.JSONObject;
//import com.bitiany.scs.common.enums.CandlestickInterval;
//import com.bitiany.scs.dal.po.Candlestick;
//import com.bitiany.scs.event.CandlestickEvent;
//import com.bitiany.scs.websocket.WebSocketConnection;
//import com.bitiany.scs.websocket.chain.Filter;
//import com.bitiany.scs.websocket.chain.FilterChain;
//import com.bitiany.scs.websocket.common.WebSocketConstants;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.context.ApplicationEventPublisher;
//import org.springframework.context.ApplicationEventPublisherAware;
//
//import javax.annotation.Resource;
//import java.util.List;
//import java.util.concurrent.Executor;
//
///**
// * @author gdyang
// * @date 2021/8/2 1:36 下午
// */
//@Slf4j
//public class CandlestickHistoryFilter implements Filter<JSONObject>, ApplicationEventPublisherAware {
//    private ApplicationEventPublisher applicationEventPublisher;
//    @Resource
//    private Executor executor;
//    @Override
//    public void doFilter(JSONObject data, WebSocketConnection connection, FilterChain chain) {
//        if (data.containsKey("err-msg")) {
//            log.error("request data: {}", data);
//        }
//        if (data.containsKey(WebSocketConstants.REP) && data.getString(WebSocketConstants.REP).contains(WebSocketConstants.K_LINE)) {
//            try {
//                String ch = data.getString(WebSocketConstants.REP);
//                List<Candlestick> list = data.getJSONArray("data").toJavaList(Candlestick.class);
//                String type = ch.substring(ch.lastIndexOf(".") + 1);
//                CandlestickInterval interval = CandlestickInterval.accept(type);
//                String symbol = ch.substring(7, ch.indexOf("kline") - 1);
//                if(log.isDebugEnabled()) {
//                    log.info("{}, {}, {}", symbol, type, list);
//                }
//                executor.execute(()-> {
//                    list.forEach(candlestick -> {
//                        candlestick.setSymbol(symbol);
//                        candlestick.setPeriod(interval.getCode());
//                        applicationEventPublisher.publishEvent(new CandlestickEvent(candlestick, interval));
//                    });
//                });
//            } catch (Exception e) {
//                log.error("request candlestick error", e);
//                e.printStackTrace();
//            }
//        } else {
//            chain.doFilter(data, connection);
//        }
//    }
//
//    @Override
//    public void setApplicationEventPublisher(ApplicationEventPublisher applicationEventPublisher) {
//        this.applicationEventPublisher = applicationEventPublisher;
//    }
//}
