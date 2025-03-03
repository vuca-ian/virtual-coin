package cn.virtual.coin.websocket;

import cn.virtual.coin.websocket.code.Decoder;
import cn.virtual.coin.websocket.code.Encoder;
import cn.virtual.coin.websocket.utils.OkHttpUtils;
import lombok.extern.slf4j.Slf4j;
import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;
import okio.ByteString;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

@Slf4j
public class WebSocketClientConnection extends WebSocketListener implements WebSocketConnection {
    private final Options options;
    private final Encoder encoder;
    private final Decoder decoder;
    private final WebSocketHandler<String> webSocketHandler;
    private final AuthenticManage authenticManage;
    private WebSocket webSocket;
    private ConnectionState state;
    private final String connectionId;
    private long lasReceivedTime = 0L;
    private int delay = 0;


    public WebSocketClientConnection(Options options, Encoder encoder, Decoder decoder, WebSocketHandler<String> webSocketHandler) {
        this(options, encoder, decoder, webSocketHandler, null, true);
    }

    public WebSocketClientConnection(Options options, Encoder encoder, Decoder decoder, WebSocketHandler<String> webSocketHandler, AuthenticManage authenticManage, boolean enabledSubscribe) {
        this.options = options;
        this.connectionId = UUID.randomUUID().toString().replace("-","");
        this.encoder = encoder;
        if(null == decoder){
            this.decoder = new Decoder.Default();
        }else {
            this.decoder = decoder;
        }
        this.webSocketHandler = webSocketHandler;
        this.authenticManage = authenticManage;
        if(enabledSubscribe) {
            connect();
        }
    }

    @Override
    public void onMessage(@NotNull WebSocket webSocket, @NotNull String text) {
        super.onMessage(webSocket, text);
        lasReceivedTime = System.currentTimeMillis();
        try {
            webSocketHandler.doRead(this, text);
        } catch (Exception e) {
            log.error("[Connection:{}] Receive message error!", getConnectionId(), e);
            closeOnError();
        }
    }

    @Override
    public void onMessage(@NotNull WebSocket webSocket, @NotNull ByteString bytes) {
        super.onMessage(webSocket, bytes);
        lasReceivedTime = System.currentTimeMillis();
        try {
            webSocketHandler.doRead(this, decoder.decode(bytes.toByteArray()));
        } catch (Exception e) {
            log.error("[Connection:{}] Receive message error!", getConnectionId(), e);
            closeOnError();
        }
    }

    @Override
    public void onOpen(@NotNull WebSocket webSocket, @NotNull Response response) {
        super.onOpen(webSocket, response);
        log.info("Connection[{}] Connected to server, {}", getConnectionId(), response);
        state = ConnectionState.CONNECTED;
        if(options.authentic() && null != authenticManage){
            Object data = authenticManage.authentic(options);
            log.debug("authentic data: {}", data);
            send(encoder.encode(data));
        }
        webSocketHandler.onOpen(this);
        WebSocketWatchDog.onConnected(this);
    }


    @Override
    public void onFailure(@NotNull WebSocket webSocket, @NotNull Throwable t, @Nullable Response response) {
        super.onFailure(webSocket, t, response);
        if(null != this.webSocket){
            log.error("Connection[{}] Connection is failure due to error ", getConnectionId(),  t);
            state = ConnectionState.CLOSED_ON_ERROR;
            this.webSocket.cancel();
            this.webSocket = null;
            webSocketHandler.onClose(this,  t);
        }
    }

    @Override
    public void onClosed(@NotNull WebSocket webSocket, int code, @NotNull String reason) {
        super.onClosed(webSocket, code, reason);
        log.error("Connection[{}] Connection is closed due to error: {}", getConnectionId(),  reason);
        if(ConnectionState.CONNECTED == state){
            state = ConnectionState.IDLE;
            webSocketHandler.onClose(this, new Exception(reason));
        }
    }

    @Override
    public void connect() {
        if(ConnectionState.CONNECTED == state){
            log.warn("[Connection][{}] Already connected, {}", this.getConnectionId(), webSocket);
            return;
        }
        if(delay >0){
            delay--;
            return;
        }
        this.state = ConnectionState.CONNECTING;
        String url = this.options.getHost() + this.options.getPath();
        log.info("[Connection][{}] Connecting to {}", getConnectionId(), url);
        this.webSocket = OkHttpUtils.buildWebSocket(url, this);
        WebSocketWatchDog.onConnected(this);
    }

    @Override
    public ConnectionState getState() {
        return state;
    }

    @Override
    public String getConnectionId() {
        return connectionId;
    }

    @Override
    public <T> void send(T message) {
        if(webSocket == null){
            return;
        }
        if(message instanceof String){
            webSocket.send((String) message);
            return;
        }
        webSocket.send(encoder.encode(message));
    }

    @Override
    public long getLastReceivedTime() {
        return this.lasReceivedTime;
    }

    @Override
    public void retryConnect(int delay) {
        log.warn("{}}] Reconnecting after {} seconds later", getConnectionId(), delay);
        if(null != webSocket){
            webSocket.cancel();
            webSocket = null;
        }
        this.delay = delay;
        state = ConnectionState.DELAY_CONNECT;

    }

    @Override
    public String connectionName() {
        return this.options.getHost() + this.options.getPath();
    }

    private void closeOnError() {
        if (webSocket != null) {
            this.webSocket.cancel();
            this.webSocket = null;
            state = ConnectionState.CLOSED_ON_ERROR;
            log.error("[Connection:{}] Connection is closing due to error", getConnectionId());
        }
    }
}
