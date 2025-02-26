package cn.virtual.coin.broker.htx.utils;

import cn.virtual.coin.broker.htx.model.ApiModel;
import cn.virtual.coin.websocket.utils.OkHttpUtils;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.lang.reflect.WildcardType;
import java.nio.charset.StandardCharsets;


/**
 * @author gdyang
 * @since 2025/2/25 20:56
 */
@Slf4j
public class HttpService {
    private String host;
    private static final String PATH_v2 = "/v1/account/accounts";
    private static final String apiKey = "e074f418-vf25treb80-2fc83207-68aa2";
    private static final String secretKey ="c836dcb4-1bb16b87-b8c46cbc-4c467";

    public HttpService() {
    }

    public HttpService(String host) {
        this.host = host;
    }

    @SuppressWarnings("deprecation")
    public <T> T doPost(ApiModel<T> model, String accessKey, String secretKey) throws ServiceException {
        Class<T> clazz = getParameterizedTypeClass(model.getClass(), 0);
        String requestUrl = this.host + model.path();
        try {
            ApiSignature apiSignature = new ApiSignature();
            String param = apiSignature.createSignature(this.host, accessKey, secretKey, model);
            log.info("request due to post, signature:{},\n  body: {}", param, JSON.toJSONString(model.paramMap()));
            Request executeRequest =
                    new Request.Builder().url(requestUrl + "?" + param).post(RequestBody.create(MediaType.parse("application/json"), JSON.toJSONString(model.paramMap()).getBytes(StandardCharsets.UTF_8)))
                            .addHeader("Content-Type", "application/json").build();
            return doRequest(executeRequest, clazz);
        } catch (Exception e) {
            throw new ServiceException("请求失败!", e);
        }
    }


    public <T>T  doGet(ApiModel<T> model, String accessKey, String secretKey) throws ServiceException {
        Class<T> clazz = getParameterizedTypeClass(model.getClass(), 0);
        String requestUrl = this.host + model.path();
        try {
            ApiSignature apiSignature = new ApiSignature();
            String param = apiSignature.createSignature(this.host, accessKey, secretKey, model);
            log.info("request due to get :{}", requestUrl);
            Request executeRequest = new Request.Builder().url(requestUrl + "?" + param).get()
                    .addHeader("Content-Type", "application/json").build();
            return doRequest(executeRequest, clazz);
        } catch (Exception e) {
            throw new ServiceException("请求失败!", e);
        }
    }

    @SuppressWarnings("unchecked")
    private <T>T doRequest(Request request, Class<T> clazz) throws Exception {
        try(Response response = OkHttpUtils.buildClient().newCall(request).execute()){
            String result = null;
            if (response.body() != null) {
                result = response.body().string();
            }
            log.info("response data: {}", result);
            JSONObject res = JSON.parseObject(result);
            if(isSuccess(res)){
                if(res.get(WebSocketConstants.DATA) instanceof JSONArray) {
                    return (T) JSON.parseArray(res.getString(WebSocketConstants.DATA), clazz);
                }else{
                    return res.getJSONObject(WebSocketConstants.DATA).toJavaObject((Type) clazz);
                }
            }else{
                log.error("request error: {}", res);
                throw new Exception(res.getString("message"));
            }
        }catch (Exception e){
            throw new ServiceException(e.getMessage(), e);
        }
    }
    public boolean isSuccess(JSONObject result){
        if(result.containsKey(WebSocketConstants.STATUS) && result.getString(WebSocketConstants.STATUS).equals(WebSocketConstants.OK)){
            return true;
        }
        return result.containsKey(WebSocketConstants.CODE) && result.getInteger(WebSocketConstants.CODE).equals(200);
    }

    @SuppressWarnings(("all"))
    public static <M>Class<M> getParameterizedTypeClass(Class<?> clazz, int parameterizedIndex) {
        if (clazz == null) {
            return null;
        } else {
            Type parameterizedType = getGenericInterface(clazz);
            if (null == parameterizedType || parameterizedType == Object.class) {
                return null;
            } else {
                Type[] actualTypeArguments = ((ParameterizedType)parameterizedType).getActualTypeArguments();
                if (0 != actualTypeArguments.length) {
                    Type type = actualTypeArguments[parameterizedIndex];
                    if(type instanceof ParameterizedType ){
                        actualTypeArguments = ((ParameterizedType) type).getActualTypeArguments();
                        if(0 != actualTypeArguments.length){
                            type = actualTypeArguments[parameterizedIndex];
                        }
                    }
                    return (Class<M>)((type instanceof ParameterizedType ? ((ParameterizedType)type).getRawType() : type));
                } else {
                    return null;
                }
            }
        }
    }

    @SuppressWarnings(("unchekced"))
    private static Type getGenericInterface(Class<?> clazz) {
        for (Type target: clazz.getGenericInterfaces()) {
            if (!(target instanceof ParameterizedType)) {
                continue;
            }
            Type[] array = ((ParameterizedType) target).getActualTypeArguments();
            for (Type type: array) {
                if (type instanceof TypeVariable || type instanceof WildcardType) {
                    break;
                }
                return target;
            }
        }
        return null;
    }
}
