package cn.virtual.coin.broker.htx;

import cn.virtual.coin.broker.htx.utils.CandlestickInterval;
import cn.virtual.coin.broker.htx.utils.ServiceException;
import cn.virtual.coin.broker.property.CollectorProperties;
import cn.virtual.coin.broker.service.DatabaseInitializeHandler;
import cn.virtual.coin.websocket.WebSocketConnection;
import cn.virtual.coin.websocket.WebSocketHandler;
import cn.virtual.coin.websocket.chain.FilterContext;
import cn.virtual.coin.websocket.chain.WebSocketFilterChain;
import cn.virtual.coin.websocket.code.Encoder;
import com.alibaba.fastjson.JSONObject;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;
import java.util.Arrays;

/**
 * @author gdyang
 * @since 2025/2/26 23:30
 */
@Slf4j
public class HuobiWebSocketHandler implements WebSocketHandler<String>, Encoder, InitializingBean {
    private final FilterContext<?> filterContext;
    @Setter
    private boolean enabledSubscribe;

    private final CollectorProperties collectorProperties;
    private final DatabaseInitializeHandler databaseInitializeHandler;
    public HuobiWebSocketHandler(FilterContext<?> filterContext, DatabaseInitializeHandler databaseInitializeHandler, CollectorProperties collectorProperties) {
        this.filterContext = filterContext;
        this.databaseInitializeHandler = databaseInitializeHandler;
        this.collectorProperties = collectorProperties;
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
            throw new ServiceException("ws解析数据异常", e);
        }
    }

    @Override
    public void onOpen(WebSocketConnection connection) {
        if(enabledSubscribe){
            Arrays.stream(collectorProperties.getSymbols()).forEach(symbol ->
                    Arrays.stream(CandlestickInterval.values()).forEach(interval ->
                            connection.send(subTopic(symbol, interval))));
        }
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

    private JSONObject subTopic(String symbol, CandlestickInterval interval){
        String topic = WEBSOCKET_CANDLESTICK_TOPIC
                .replace("$symbol$", symbol)
                .replace("$period$", interval.getCode());

        JSONObject command = new JSONObject();
        command.put("sub", topic);
        command.put("id", System.nanoTime());
        log.info("[Connection Send]{}", command);
        return command;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        if(collectorProperties.getSymbols() != null){
            Arrays.stream(collectorProperties.getSymbols()).forEach(symbol ->{
                Arrays.stream(CandlestickInterval.values()).forEach(period ->{
                    databaseInitializeHandler.createTable(collectorProperties.getDialect(),collectorProperties.getLogicTable(),  symbol, period.getCode());
                });
            });
        }
    }
}
