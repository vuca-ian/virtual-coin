package cn.virtual.coin.websocket;

/**
 * @author gdyang
 * @since 2025/2/24 23:06
 */
public interface WebSocketHandler <T> {
    /**
     * 读取数据
     * @param connection 连接
     * @param data 数据
     */
    void doRead(WebSocketConnection connection, T data);
    /**
     *  连接成功
     * @param connection 连接
     */
    void onOpen(WebSocketConnection connection);

    /**
     * 关闭连接
     * @param connection 连接
     * @param t 异常
     */
    void onClose(WebSocketConnection connection, Throwable t);
}
