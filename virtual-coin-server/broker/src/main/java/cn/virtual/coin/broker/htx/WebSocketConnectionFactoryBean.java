package cn.virtual.coin.broker.htx;

import cn.virtual.coin.websocket.AuthenticManage;
import cn.virtual.coin.websocket.Options;
import cn.virtual.coin.websocket.WebSocketClientConnection;
import cn.virtual.coin.websocket.WebSocketConnection;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;

/**
 * @author gdyang
 * @since 2025/2/26 23:31
 */
public class WebSocketConnectionFactoryBean implements FactoryBean<WebSocketConnection>, InitializingBean {

    private final HuobiWebSocketHandler handler;
    private AuthenticManage authenticManage;
    private final Options options;
    private final Boolean enabledSubscribe;


    public WebSocketConnectionFactoryBean(HuobiWebSocketHandler handler, Options options, Boolean enabledSubscribe) {
        this.handler = handler;
        this.options = options;
        this.enabledSubscribe = enabledSubscribe;
    }

    public WebSocketConnectionFactoryBean(HuobiWebSocketHandler handler, AuthenticManage authenticManage, Options options, Boolean enabledSubscribe) {
        this.handler = handler;
        this.authenticManage = authenticManage;
        this.options = options;
        this.enabledSubscribe = enabledSubscribe;
        handler.setEnabledSubscribe(enabledSubscribe);
    }

    @Override
    public WebSocketConnection getObject() throws Exception {
        return new WebSocketClientConnection(options, handler,null, handler, authenticManage,enabledSubscribe);
    }

    @Override
    public Class<?> getObjectType() {
        return WebSocketConnection.class;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
    }
}
