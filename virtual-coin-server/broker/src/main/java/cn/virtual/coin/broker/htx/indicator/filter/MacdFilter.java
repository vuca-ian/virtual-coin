package cn.virtual.coin.broker.htx.indicator.filter;

import cn.virtual.coin.websocket.WebSocketConnection;
import cn.virtual.coin.websocket.chain.Filter;
import cn.virtual.coin.websocket.chain.FilterChain;
import com.alibaba.fastjson.JSONArray;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import lombok.extern.slf4j.Slf4j;

/**
 * @author gdyang
 * @since 2021/11/3 11:48 上午
 */
@Slf4j
public class MacdFilter implements Filter<JSONArray> {
    @Override
    public void doFilter(JSONArray data, WebSocketConnection connection, FilterChain chain) {
        if(CollectionUtils.isEmpty(data)){
            return;
        }
        log.info("macd indicator segement start...");
        data.forEach(arr ->{

        });
    }


}
