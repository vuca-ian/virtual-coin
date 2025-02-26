package cn.virtual.coin.broker.htx;

import cn.virtual.coin.websocket.Options;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author gdyang
 * @since  2021/8/1 11:26 下午
 */
@Data
@NoArgsConstructor
public class HuobiOptions implements Options {
    private String host = "wss://api.huobi.pro";
    private String path = "/ws";
    private Boolean authentic;
    private String apiKey;

    private String secretKey;

    public HuobiOptions(String host, String path) {
        this(host, path, false);
    }

    public HuobiOptions(String host, String path, Boolean authentic) {
        this.host = host;
        this.path = path;
        this.authentic = authentic;
    }

    @Override
    public String getPath() {
        return this.path;
    }

    @Override
    public String getHost() {
        return this.host;
    }

    @Override
    public boolean authentic() {
        return this.authentic;
    }

    @Override
    public String getApiKey() {
        return this.apiKey;
    }

    @Override
    public String getSecretKey() {
        return this.secretKey;
    }
}
