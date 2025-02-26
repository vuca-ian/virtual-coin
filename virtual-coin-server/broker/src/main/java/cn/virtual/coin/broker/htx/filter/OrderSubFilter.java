package cn.virtual.coin.broker.htx.filter;

import cn.virtual.coin.broker.htx.utils.WebSocketConstants;
import cn.virtual.coin.websocket.WebSocketConnection;
import cn.virtual.coin.websocket.chain.Filter;
import cn.virtual.coin.websocket.chain.FilterChain;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;

/**
 * @author gdyang
 * @since  2021/8/2 1:51 下午
 */
@Slf4j
public class OrderSubFilter implements Filter<JSONObject> {
    @Override
    public void doFilter(JSONObject data, WebSocketConnection connection, FilterChain chain) {
        if(data.containsKey(WebSocketConstants.ACTION) && data.getString(WebSocketConstants.ACTION).equals(WebSocketConstants.SUB)){
            log.debug("order sub {}", data);
        }else{
            chain.doFilter(data, connection);
        }
    }
}
