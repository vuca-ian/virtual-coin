package cn.virtual.coin.broker.htx.model;

import java.util.Map;

/**
 * @author gdyang
 * @since 2025/2/25 20:53
 */
public interface ApiModel<T> {
    String path();
    /**
     * 请求方式
     * @return POST、GET
     */
    default String method(){
        return Method.GET.toString();
    }

    /**
     * 参数
     * @return 参数Map
     */
    Map<String, Object> paramMap();

    enum Method{
        /**
         * 请求方式
         */
        GET, POST;

        public String string(){
            return toString();
        }
    }
}
