package cn.virtual.coin.broker.htx.utils;

import java.util.Collection;

/**
 * @author gdyang
 * @since 2025/3/3 22:35
 */
public class CollectionUtils {

    public static <T>T findFirst(Collection<T> collection){
        return collection.stream().findFirst().orElse(null);
    }

    public static <T>boolean isNotEmpty(Collection<T> collection){
        return collection != null && !collection.isEmpty();
    }
}
