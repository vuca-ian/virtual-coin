package cn.virtual.coin.broker.htx;

import cn.virtual.coin.broker.htx.utils.ApiSignatureV2;
import cn.virtual.coin.broker.htx.utils.UrlParamsBuilder;
import cn.virtual.coin.websocket.AuthenticManage;
import cn.virtual.coin.websocket.Options;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;

import java.net.URL;

/**
 * @author gdyang
 * @since 2021/8/2 2:14 下午
 */
@Slf4j
public class WebSocketAuthenticManage implements AuthenticManage {
    String restHost = "https://api.huobi.pro";
    @Override
    public String authentic(Options options) {
//        ApiSignature as = new ApiSignature();
//        UrlParamsBuilder builder = UrlParamsBuilder.build();
//        try {
//            as.createSignature(options.getApiKey(), options.getSecretKey(), "GET", "api.huobi.pro", options.getPath(), builder);
//        } catch (Exception e) {
//
//        }
//        builder.putToUrl(ApiSignature.op, ApiSignature.opValue)
//                .putToUrl("cid", System.currentTimeMillis());
//        return builder.buildUrlToJsonString();
        ApiSignatureV2 as = new ApiSignatureV2();
        UrlParamsBuilder builder = UrlParamsBuilder.build();
        try {
            as.createSignature(options.getApiKey(), options.getSecretKey(), "GET", new URL(restHost).getHost(), options.getPath(), builder);
        } catch (Exception e) {
            log.error("Unexpected error when create the signature v2: {}", e.getMessage(), e);
            e.printStackTrace();
        }

        JSONObject signObj = JSON.parseObject(builder.buildUrlToJsonString());
        signObj.put("authType", "api");

        JSONObject json = new JSONObject();
        json.put("action", "req");
        json.put("ch", "auth");
        json.put("params", signObj);
        return json.toJSONString();
    }
}
