package cn.virtual.coin.broker.htx.filter;

import cn.virtual.coin.broker.htx.utils.WebSocketConstants;
import cn.virtual.coin.websocket.WebSocketConnection;
import cn.virtual.coin.websocket.chain.Filter;
import cn.virtual.coin.websocket.chain.FilterChain;
import com.alibaba.fastjson.JSONObject;

/**
 * @author gdyang
 * @since 2021/8/2 1:45 下午
 */
public class SubbedFilter implements Filter<JSONObject> {
    @Override
    public void doFilter(JSONObject data, WebSocketConnection connection, FilterChain chain) {
        if(!data.containsKey(WebSocketConstants.SUBBED)){
            chain.doFilter(data, connection);
        }
    }
}
