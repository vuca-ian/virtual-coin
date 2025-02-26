package cn.virtual.coin.websocket;

public interface Options {

    /**
     * 获取路径
     * @return path
     */
    String getPath();

    /**
     * host
     * @return host
     */
    String getHost();

    /**
     * need auth?
     * @return auth
     */
    boolean authentic();

    String getApiKey();

    String getSecretKey();
}
