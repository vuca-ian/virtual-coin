package cn.virtual.coin.websocket;

public interface WebSocketConnection {

    /**
     * 连接
     */
    void connect();
    /**
     * websocket状态
     * @return 状态
     */
    ConnectionState getState();

    /**
     *  连接ID
     * @return id
     */
    String getConnectionId();

    /**
     * 发送消息
     * @param message 消息
     */
    <T>void send(T message);

    /**
     * 最近一次接收消息时间
     * @return 时间戳
     */
    long getLastReceivedTime();

    /**
     * 重试连接
     * @param delay 延迟
     */
    void retryConnect(int delay);

    String connectionName();
}
