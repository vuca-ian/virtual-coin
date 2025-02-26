package cn.virtual.coin.websocket.chain;

import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @author gdyang
 * @since 2025/2/24 23:09
 */
@Slf4j
public class FilterContext<T> {

    private final AtomicBoolean initialized = new AtomicBoolean(false);
    private final Map<String, FilterConfig<T>> filterConfigMap = new HashMap<>();

    public boolean filterStart(){
        if(initialized.compareAndSet(false, true)){
            log.info("Starting filters...");

        }
        return initialized.get();
    }

    public void addFilterConfig(String name, Filter<T> filter){
        filterConfigMap.put(name, new WebSocketFilterConfig<T>(this, name, filter));
    }

    public FilterConfig<T> buildFilterConfig(String filterName){
        if(filterConfigMap.containsKey(filterName)) {
            return filterConfigMap.get(filterName);
        }
        return null;
    }

    public List<FilterConfig<T>> findFilterConfigs(){
        return new ArrayList<>(filterConfigMap.values());
    }
}
