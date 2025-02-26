package cn.virtual.coin.websocket.utils;

import okhttp3.*;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @author gdyang
 * @since 2025/2/24 23:14
 */
public class OkHttpUtils {
    private static final AtomicBoolean LATENCY_DEBUG_SWATCH = new AtomicBoolean(false);
    private static final LinkedBlockingQueue<NetworkLatency> LATENCY_DEBUG_QUEUE = new LinkedBlockingQueue<>();

    public static OkHttpClient buildClient() {
        return new OkHttpClient.Builder()
                .followSslRedirects(false)
                .followRedirects(false)
                .connectTimeout(5000, TimeUnit.MILLISECONDS)
                .readTimeout(5000, TimeUnit.MILLISECONDS)
                .writeTimeout(5000, TimeUnit.MILLISECONDS)
                .connectionPool(new ConnectionPool(20, 300, TimeUnit.SECONDS))
                .addNetworkInterceptor(chain -> {
                    Request request = chain.request();
                    Long startNano = System.nanoTime();
                    Response response = chain.proceed(request);
                    Long endNano = System.nanoTime();

                    if (LATENCY_DEBUG_SWATCH.get()) {
                        LATENCY_DEBUG_QUEUE.add(new NetworkLatency(request.url().url().getPath(), startNano, endNano));
                    }
                    return response;
                }).build();
    }

    public static WebSocket buildWebSocket(String url, WebSocketListener listener) {
        Request request = new Request.Builder().url(url).build();
        return buildClient().newWebSocket(request,listener);
    }

    public static void clear(){
        LATENCY_DEBUG_QUEUE.clear();
    }
}
