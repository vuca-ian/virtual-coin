package cn.virtual.coin.websocket.code;

/**
 * @author gdyang
 * @since 2025/2/24 23:01
 */
public interface Encoder {

    /**
     * 解码器
     * @param data data
     * @param <T> 数据类型
     * @return string
     */
    <T>String encode(T data);
}
