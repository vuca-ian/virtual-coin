package cn.virtual.coin.websocket.exception;

import lombok.Getter;

/**
 * @author gdyang
 * @since 2025/2/24 23:02
 */
public class WebSocketException extends RuntimeException {

    @Getter
    private String errCode;

    public WebSocketException(String errCode, String errMsg) {
            super(errMsg);
            this.errCode = errCode;
        }

    public WebSocketException(Throwable cause) {
            super(cause);
        }

    public WebSocketException(String message, Throwable cause) {
            super(message, cause);
        }
}
