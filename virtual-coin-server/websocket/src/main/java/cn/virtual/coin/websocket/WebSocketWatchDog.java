package cn.virtual.coin.websocket;

import lombok.extern.slf4j.Slf4j;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author gdyang
 * @since 2025/2/24 23:05
 */
@Slf4j
public class WebSocketWatchDog {

    private static final int DELAY_ON_FAILURE = 10;
    public static final long RECEIVE_LIMIT_TS = 300_000;
    private static final Map<String, WebSocketConnection> WATCH_CONNECTION_MAP = new ConcurrentHashMap<>();

    private static final ScheduledExecutorService EXEC = new ScheduledThreadPoolExecutor(1, r -> new Thread(r, "Thread-Websocket-watch"));

    static {
        long t = 1_000;
        EXEC.scheduleAtFixedRate(() -> {
            WATCH_CONNECTION_MAP.forEach((id, connection) -> {
                if(ConnectionState.CONNECTED == connection.getState()){
                    if(System.currentTimeMillis() - connection.getLastReceivedTime() > RECEIVE_LIMIT_TS){
                        log.warn("[Connection:{}] No response from server.",  connection.getConnectionId());
                        connection.retryConnect(DELAY_ON_FAILURE);
                    }
                }else if(ConnectionState.DELAY_CONNECT == connection.getState()){
                    connection.connect();
                }else if(ConnectionState.CLOSED_ON_ERROR == connection.getState()){
                    log.warn("[Connection:{}] Closed on error from server.",  connection.getConnectionId());
                    connection.retryConnect(DELAY_ON_FAILURE);
                }
            });
        }, t, t, TimeUnit.MILLISECONDS);
        Runtime.getRuntime().addShutdownHook(new Thread(EXEC::shutdown));
    }

    public static void onConnected(WebSocketConnection connection){
        WATCH_CONNECTION_MAP.put(connection.getConnectionId(), connection);
    }

    public static Map<String, WebSocketConnection> getConnectionMap(){
        return WATCH_CONNECTION_MAP;
    }
}
