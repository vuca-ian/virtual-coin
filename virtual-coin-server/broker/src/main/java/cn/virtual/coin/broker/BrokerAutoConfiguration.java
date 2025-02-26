package cn.virtual.coin.broker;

import cn.virtual.coin.broker.htx.HuobiOptions;
import cn.virtual.coin.broker.htx.HuobiWebSocketHandler;
import cn.virtual.coin.broker.htx.WebSocketAuthenticManage;
import cn.virtual.coin.broker.htx.WebSocketConnectionFactoryBean;
import cn.virtual.coin.broker.htx.filter.*;
import cn.virtual.coin.broker.htx.utils.HttpService;
import cn.virtual.coin.websocket.WebSocketConnection;
import cn.virtual.coin.websocket.WebSocketHandler;
import cn.virtual.coin.websocket.chain.FilterContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * @author gdyang
 * @since 2025/2/25 21:19
 */
@ComponentScan
@Configuration
public class BrokerAutoConfiguration {

    private static final String HOST = "wss://api.huobi.pro";
    private static final String PATH = "/ws";
    @Bean
    public HttpService httpService(){
        return new HttpService("https://api.huobi.pro");
    }

    @Bean("ws_v1")
    public WebSocketConnection webSocketConnection (WebSocketHandler<String> handler,WebSocketAuthenticManage authenticManage) throws Exception {
        return new WebSocketConnectionFactoryBean((HuobiWebSocketHandler) handler, authenticManage, new HuobiOptions(HOST, PATH), true).getObject();
    }
    @Bean
    public WebSocketAuthenticManage authenticManage(){
        return new WebSocketAuthenticManage();
    }

    @Bean
    @SuppressWarnings("rawtypes, unchecked")
    public FilterContext context(CandlestickFilter candlestickFilter){
        FilterContext context = new FilterContext<>();
        context.addFilterConfig("ping", new PingPongFilter());
        context.addFilterConfig("candlestick", candlestickFilter);
//        context.addFilterConfig("candlestickHistory", candlestickHistoryFilter());
        context.addFilterConfig("subbed", new SubbedFilter());
        context.addFilterConfig("auth", new ActionFilter());
        context.addFilterConfig("orderSub", new OrderSubFilter());
//        context.addFilterConfig("OrderPush", orderPushFilter());
        context.addFilterConfig("error", new ErrorFilter());
        return context;
    }
}
