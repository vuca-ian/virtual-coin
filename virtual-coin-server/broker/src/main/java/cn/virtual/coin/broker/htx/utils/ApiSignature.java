package cn.virtual.coin.broker.htx.utils;

import cn.virtual.coin.broker.htx.model.ApiModel;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Base64;
import java.util.Map;
import java.util.TreeMap;

/**
 * @author gdyang
 * @since 2025/2/25 20:54
 */
@Slf4j
@Data
public class ApiSignature {

    private static final String ACCESS_KEY_ID = "AccessKeyId";
    private static final String SIGNATURE_METHOD = "SignatureMethod";
    private static final String SIGNATURE_METHOD_VALUE = "HmacSHA256";
    private static final String SIGNATURE_VERSION = "SignatureVersion";
    private static final String SIGNATURE_VERSION_VALUE = "2";
    private static final String TIMESTAMP = "Timestamp";
    private static final String SIGNATURE = "Signature";
    private static final DateTimeFormatter DT_FORMAT = DateTimeFormatter.ofPattern("uuuu-MM-dd'T'HH:mm:ss");
    private static final ZoneId ZONE_GMT = ZoneId.of("Z");

    private String key(String uri, String key){
//        return uri.contains("v2") ? StringUtils.lowerCaseInitials(key) : key;
        return key;
    }

    public <T>String createSignature(String host, String accessKey, String secretKey, ApiModel<T> model) throws MalformedURLException {
        StringBuilder sb = new StringBuilder(1024);
        sb.append(model.method().toUpperCase()).append('\n')
                .append(new URL(host.toLowerCase()).getHost()).append('\n')
                .append(model.path()).append('\n');
//        Map<String, Object> pa = model.paramMap();
        Map<String, Object> paramMap = new TreeMap<>(model.paramMap());

        paramMap.put(ACCESS_KEY_ID, accessKey);
        paramMap.put(SIGNATURE_VERSION, "2");
        paramMap.put(SIGNATURE_METHOD, SIGNATURE_METHOD_VALUE);
        paramMap.put(TIMESTAMP, Instant.ofEpochSecond(Instant.now().getEpochSecond()).atZone(ZONE_GMT).format(DT_FORMAT));

        sb.append(buildUrlParam(model.path(),paramMap));
        if(log.isDebugEnabled()) {
            log.info("signature data:\n{}", sb.toString());
        }
        try {
            Mac hmacSha256 = Mac.getInstance(SIGNATURE_METHOD_VALUE);
            SecretKeySpec secKey = new SecretKeySpec(secretKey.getBytes(StandardCharsets.UTF_8), SIGNATURE_METHOD_VALUE);
            hmacSha256.init(secKey);
            String payload = sb.toString();
            byte[] hash = hmacSha256.doFinal(payload.getBytes(StandardCharsets.UTF_8));

            String actualSign = Base64.getEncoder().encodeToString(hash);
            paramMap.put(SIGNATURE, actualSign);
//            pa.put(SIGNATURE, actualSign);
        } catch (NoSuchAlgorithmException e) {
            throw new SDKException(SDKException.RUNTIME_ERROR,
                    "[Signature] No such algorithm: " + e.getMessage());
        } catch (InvalidKeyException e) {
            throw new SDKException(SDKException.RUNTIME_ERROR,
                    "[Signature] Invalid key: " + e.getMessage());
        }
        return buildUrlParam(model.path(), paramMap);
    }

    private String buildUrlParam(String path, Map<String, Object> map) {
        StringBuilder stringBuilder = new StringBuilder();
        for (Map.Entry<String, Object> entry : map.entrySet()) {
            if(null != entry.getValue()) {
                if (!stringBuilder.toString().isEmpty()) {
                    stringBuilder.append("&");
                }
                stringBuilder.append(key(path, entry.getKey()));
                stringBuilder.append("=");
                stringBuilder.append(urlEncode(entry.getValue().toString()));
            }
        }
        return stringBuilder.toString();
    }

    /**
     * 使用标准URL Encode编码。注意和JDK默认的不同，空格被编码为%20而不是+。
     *
     * @param s String字符串
     * @return URL编码后的字符串
     */
    private static String urlEncode(String s) {
        try {
            return URLEncoder.encode(s, "UTF-8").replaceAll("\\+", "%20");
        } catch (UnsupportedEncodingException e) {
            throw new SDKException(SDKException.RUNTIME_ERROR,
                    "[URL] UTF-8 encoding not supported!");
        }
    }
}
