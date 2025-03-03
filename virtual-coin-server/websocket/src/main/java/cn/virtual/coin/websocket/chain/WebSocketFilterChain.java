package cn.virtual.coin.websocket.chain;

import cn.virtual.coin.websocket.WebSocketConnection;
import cn.virtual.coin.websocket.exception.WebSocketException;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.Arrays;
import java.util.List;

/**
 * @author gdyang
 * @since 2025/2/24 23:10
 */
@Slf4j
@SuppressWarnings("all")
@NoArgsConstructor
public class WebSocketFilterChain implements FilterChain{
    public static final int INCREMENT = 10;
    private WebSocketFilterConfig[] filters = new WebSocketFilterConfig[0];
    private FilterContext context;
    private int pos = 0;
    private int n = 0;



    public static WebSocketFilterChain createFilterChain(FilterContext context){
        return new WebSocketFilterChain(context);
    }

    public WebSocketFilterChain(FilterContext context) {
        this.context = context;
        initialize();
    }

    public void initialize(){
        List<FilterConfig> filterConfigs = context.findFilterConfigs();
        filterConfigs.forEach(config -> {
            if(config instanceof WebSocketFilterConfig) {
                this.addFilter((WebSocketFilterConfig) config);
            }
        });
    }

    @Override
    public <T> void doFilter(T data, WebSocketConnection connection) {
        if(pos < n){
            WebSocketFilterConfig<T> filterConfig = filters[pos++];
            try {
                log.debug("data:{}", data);
                Filter<T> filter = filterConfig.getFilter();
                if(filter.supports(data)){
                    filter.doFilter(data, connection, this);
                }else{
                    this.doFilter(data, connection);
                }
            }catch (Exception e){
                throw new WebSocketException("FilterChain.filter is stoped due to error", e);
            }
        }
    }

    public void addFilter(WebSocketFilterConfig filterConfig){
        if(Arrays.stream(filters).anyMatch(filter -> filter == filterConfig)){
            return;
        }
        if(n == filters.length){
            WebSocketFilterConfig[] filterConfigs = new WebSocketFilterConfig[n + INCREMENT];
            System.arraycopy(filters, 0, filterConfigs, 0, n);
            filters = filterConfigs;
        }
        filters[n++] = filterConfig;
    }
}
