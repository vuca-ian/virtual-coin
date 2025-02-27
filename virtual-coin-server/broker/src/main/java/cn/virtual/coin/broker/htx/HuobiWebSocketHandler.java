package cn.virtual.coin.broker.htx;

import cn.virtual.coin.websocket.WebSocketConnection;
import cn.virtual.coin.websocket.WebSocketHandler;
import cn.virtual.coin.websocket.chain.FilterContext;
import cn.virtual.coin.websocket.chain.WebSocketFilterChain;
import cn.virtual.coin.websocket.code.Encoder;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * @author gdyang
 * @since 2025/2/26 23:30
 */
@Slf4j
public class HuobiWebSocketHandler implements WebSocketHandler<String>, Encoder {
    private final FilterContext filterContext;
    private boolean enabledSubscribe;

    public HuobiWebSocketHandler(FilterContext filterContext) {
        this.filterContext = filterContext;
    }

    /**
     * {"action":"push","ch":"orders#ethusdt","data":{"eventType":"deletion",
     * "symbol":"ethusdt","clientOrderId":"algo_89bjb162789368993500001","orderSide":"buy","orderStatus":"canceled","lastActTime":1627893721385}}
     * @param connection 连接
     * @param data 数据
     */
    @Override
    public void doRead(WebSocketConnection connection, String data) {
        WebSocketFilterChain filterChain = WebSocketFilterChain.createFilterChain(filterContext);
        try {
            filterChain.doFilter(JSONObject.parseObject(data), connection);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onOpen(WebSocketConnection connection) {
//        if(enabledSubscribe){
//            connection.send(subTopic(CandlestickRequest.CandlestickInterval.MIN1));
//            connection.send(subTopic(CandlestickRequest.CandlestickInterval.MIN5));
//            connection.send(subTopic(CandlestickRequest.CandlestickInterval.MIN15));
//            connection.send(subTopic(CandlestickRequest.CandlestickInterval.MIN30));
//            connection.send(subTopic(CandlestickRequest.CandlestickInterval.MIN60));
//            connection.send(subTopic(CandlestickRequest.CandlestickInterval.HOUR4));
//            connection.send(subTopic(CandlestickRequest.CandlestickInterval.DAY1));
//            connection.send(subTopic(CandlestickRequest.CandlestickInterval.MON1));
//            connection.send(subTopic(CandlestickRequest.CandlestickInterval.WEEK1));
//            connection.send(subTopic(CandlestickRequest.CandlestickInterval.YEAR1));
//        }
    }

    @Override
    public void onClose(WebSocketConnection connection, Throwable t) {

    }

    @Override
    public <T> String encode(T data) {
        if(data instanceof  String) {
            return (String) data;
        }
        return JSONObject.toJSONString(data);
    }
    public static final String WEBSOCKET_CANDLESTICK_TOPIC = "market.$symbol$.kline.$period$";
//
//    private JSONObject subTopic(CandlestickRequest.CandlestickInterval interval){
//        String topic = WEBSOCKET_CANDLESTICK_TOPIC
//                .replace("$symbol$", "ethusdt")
//                .replace("$period$", interval.getCode());
//
//        JSONObject command = new JSONObject();
//        command.put("sub", topic);
//        command.put("id", System.nanoTime());
//        log.info("[Connection Send]{}", command);
//        return command;
//    }

    public void setEnabledSubscribe(boolean enabledSubscribe) {
        this.enabledSubscribe = enabledSubscribe;
    }
}
