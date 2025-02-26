package cn.virtual.coin.websocket.chain;

/**
 * @author gdyang
 * @since 2025/2/24 23:08
 */
public interface FilterConfig<T> {
    /**
     * filter名称
     * @return filterName
     */
    String getFilterName();
}
