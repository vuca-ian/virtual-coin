package cn.virtual.coin.broker.htx.filter;

import cn.virtual.coin.broker.htx.utils.WebSocketConstants;
import cn.virtual.coin.websocket.WebSocketConnection;
import cn.virtual.coin.websocket.chain.Filter;
import cn.virtual.coin.websocket.chain.FilterChain;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;

/**
 * @author gdyang
 * @since  2021/8/2 3:28 下午
 */
@Slf4j
public class ActionFilter implements Filter<JSONObject> {
    @Override
    public void doFilter(JSONObject data, WebSocketConnection connection, FilterChain chain) {
        if(data.containsKey(WebSocketConstants.ACTION)){
            if(data.getString(WebSocketConstants.ACTION).equals(WebSocketConstants.REQ)){
                log.error("auth success:{}.", data);
                String subTopic = "orders#*";
                JSONObject command = new JSONObject();
                command.put("action", "sub");
                command.put("ch", subTopic);
                command.put("id", System.nanoTime());
                log.info("[Connection Send]{}", command.toJSONString());
                connection.send(command.toJSONString());
                return;
            }
        }
        chain.doFilter(data, connection);
    }


}
