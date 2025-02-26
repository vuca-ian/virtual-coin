package cn.virtual.coin.broker.htx.filter;

import cn.virtual.coin.broker.htx.utils.WebSocketConstants;
import cn.virtual.coin.websocket.WebSocketConnection;
import cn.virtual.coin.websocket.chain.Filter;
import cn.virtual.coin.websocket.chain.FilterChain;
import com.alibaba.fastjson.JSONObject;

/**
 * @author gdyang
 * @since 2021/8/2 10:43 上午
 */
public class PingPongFilter implements Filter<JSONObject> {
    private static final String PING = "ping";
    @Override
    public void doFilter(JSONObject data, WebSocketConnection connection, FilterChain chain) {
        if(data.containsKey(PING)){
            long ts = data.getLong(PING);
            connection.send(String.format("{\"pong\":%d}", ts));
            return;
        }
        if(data.containsKey(WebSocketConstants.ACTION)){
            if(data.getString(WebSocketConstants.ACTION).equals(PING)){
                long ts = data.getJSONObject("data").getLong("ts");
                String pong = String.format("{\"action\": \"pong\",\"params\": {\"ts\": %d}}", ts);
                connection.send(pong);
                return;
            }
        }
        chain.doFilter(data, connection);
    }
}
