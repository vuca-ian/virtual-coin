package cn.virtual.coin.websocket.chain;

import cn.virtual.coin.websocket.WebSocketConnection;

/**
 * @author gdyang
 * @since 2025/2/24 23:07
 */
public interface Filter<T> {

    void doFilter(T data, WebSocketConnection connection, FilterChain chain);


    default boolean supports(T data){
        return true;
    }
}
