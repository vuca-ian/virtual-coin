package cn.virtual.coin.websocket;

public enum ConnectionState {

    /**
     * 状态
     */
    IDLE,
    DELAY_CONNECT,
    CONNECTING,
    CONNECTED,
    CLOSED_ON_ERROR
}
