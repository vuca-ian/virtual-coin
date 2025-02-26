package cn.virtual.coin.broker.htx.filter;

import cn.virtual.coin.websocket.WebSocketConnection;
import cn.virtual.coin.websocket.chain.Filter;
import cn.virtual.coin.websocket.chain.FilterChain;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;

/**
 * @author gdyang
 * @since  2021/8/2 11:16 上午
 */
@Slf4j
public class ErrorFilter implements Filter<JSONObject> {
    private static final String ERR_CODE = "err_code";
    @Override
    public void doFilter(JSONObject data, WebSocketConnection connection, FilterChain chain) {
        if(data.containsKey(ERR_CODE)) {
            log.error("error:{}", data);
        }else {
            chain.doFilter(data, connection);
        }
    }
}
