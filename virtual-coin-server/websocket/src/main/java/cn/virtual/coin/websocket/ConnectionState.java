package cn.virtual.coin.websocket;

public enum ConnectionState {

    /**
     * 状态
     */
    IDLE,
    DELAY_CONNECT,
    CONNECTED,
    CLOSED_ON_ERROR
}
