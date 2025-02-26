package cn.virtual.coin.websocket.chain;

/**
 * @author gdyang
 * @since 2025/2/24 23:08
 */
public class WebSocketFilterConfig <T>  implements FilterConfig<T>{
    private final String filterName;
    private final transient FilterContext<T> context;
    private transient Filter<T> filter;

    public WebSocketFilterConfig(FilterContext<T> context, String filterName, Filter<T> filter) {
        this.filterName = filterName;
        this.context = context;
        this.filter = filter;
    }

    Filter<T> getFilter(){
        if(null != filter) {
            return this.filter;
        }
        //todo 加载filter
        WebSocketFilterConfig<T> config = (WebSocketFilterConfig<T>) context.buildFilterConfig(this.filterName);
        if(null != config){
            this.filter = config.getFilter();
        }
        return this.filter;
    }

    @Override
    public String getFilterName() {
        return this.filterName;
    }
}
