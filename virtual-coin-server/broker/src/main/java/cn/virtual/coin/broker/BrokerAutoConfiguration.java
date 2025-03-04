package cn.virtual.coin.broker;

import cn.virtual.coin.broker.htx.HuobiOptions;
import cn.virtual.coin.broker.htx.HuobiWebSocketHandler;
import cn.virtual.coin.broker.htx.WebSocketAuthenticManage;
import cn.virtual.coin.broker.htx.WebSocketConnectionFactoryBean;
import cn.virtual.coin.broker.htx.filter.*;
import cn.virtual.coin.broker.htx.utils.HttpService;
import cn.virtual.coin.broker.property.CollectorProperties;
import cn.virtual.coin.broker.service.DatabaseInitializeHandler;
import cn.virtual.coin.websocket.WebSocketConnection;
import cn.virtual.coin.websocket.WebSocketHandler;
import cn.virtual.coin.websocket.chain.FilterContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

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
    @ConfigurationProperties(prefix = "coin.collector")
    public CollectorProperties collectorProperties(){
        return new CollectorProperties();
    }



    @Bean
    public HttpService httpService(){
        return new HttpService("https://api.huobi.pro");
    }

    @Configuration
    @ConditionalOnProperty(prefix = "server.websocket", name = "enabled", havingValue = "true")
    static class WebSocketConfiguration{
        @Bean
        public WebSocketAuthenticManage authenticManage(){
            return new WebSocketAuthenticManage();
        }

        @Bean
        public WebSocketHandler<String> huobiWebSocketHandler(@Autowired(required = false) FilterContext<?> filterContext, DatabaseInitializeHandler databaseInitializeHandler,CollectorProperties collectorProperties){
            return new HuobiWebSocketHandler(filterContext, databaseInitializeHandler, collectorProperties);
        }

        @Bean("ws_v1")
        @ConditionalOnBean(WebSocketAuthenticManage.class)
        public WebSocketConnection webSocketConnection (@Autowired(required = false) WebSocketHandler<String> handler,@Autowired(required = false) WebSocketAuthenticManage authenticManage) throws Exception {
            return new WebSocketConnectionFactoryBean((HuobiWebSocketHandler) handler, authenticManage, new HuobiOptions(HOST, PATH, false), true).getObject();
        }

        @Bean
        public CandlestickHistoryFilter candlestickHistoryFilter(){
            return new CandlestickHistoryFilter();
        }

        @Bean
        @SuppressWarnings("rawtypes, unchecked")
        public FilterContext context(CandlestickFilter candlestickFilter){
            FilterContext context = new FilterContext<>();
            context.addFilterConfig("ping", new PingPongFilter());
            context.addFilterConfig("candlestick", candlestickFilter);
            context.addFilterConfig("candlestickHistory", candlestickHistoryFilter());
            context.addFilterConfig("subbed", new SubbedFilter());
            context.addFilterConfig("auth", new ActionFilter());
            context.addFilterConfig("orderSub", new OrderSubFilter());
    //        context.addFilterConfig("OrderPush", orderPushFilter());
            context.addFilterConfig("error", new ErrorFilter());
            return context;
        }
    }

}
