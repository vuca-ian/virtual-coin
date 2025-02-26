package cn.virtual.coin.broker.htx.filter;

import cn.virtual.coin.websocket.WebSocketConnection;
import cn.virtual.coin.websocket.chain.Filter;
import cn.virtual.coin.websocket.chain.FilterChain;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;

/**
 * @author gdyang
 * @since  2021/8/2 3:30 下午
 */
@Slf4j
public class DefaultFilter implements Filter<JSONObject> {
    @Override
    public void doFilter(JSONObject data, WebSocketConnection connection, FilterChain chain) {
      log.error("data:{}", data);
      chain.doFilter(data, connection);
    }
}
