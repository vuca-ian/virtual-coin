package cn.virtual.coin.websocket.chain;

import cn.virtual.coin.websocket.WebSocketConnection;

/**
 * @author gdyang
 * @since 2025/2/24 23:07
 */
public interface FilterChain {

    /**
     * 执行下一个filter
     * @param data data
     * @param connection 连接
     */
    <T>void doFilter(T data, WebSocketConnection connection);
}
