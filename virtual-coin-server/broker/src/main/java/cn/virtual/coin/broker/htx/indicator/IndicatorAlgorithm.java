package cn.virtual.coin.broker.htx.indicator;

import cn.virtual.coin.domain.dal.po.Candlestick;
import cn.vuca.cloud.commons.utils.StringUtils;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

/**
 * @author gdyang
 * @since 2021/10/22 11:08 上午
 */
public interface IndicatorAlgorithm<R> extends Algorithm {
    String DATA = "data";
    String QUOTA = "quota";
    String PREV = "prev";
    String KDJ = "KDJ";
    /**
     * 计算指标
     * @param context 计算参数
     * @return 指标
     */
    R execute(AlgorithmContext context);

    default int quota(){
        return 5;
    }

    default JSONObject get(Candlestick tick) {
        if (null != tick) {
            if (StringUtils.isNotEmpty(tick.getIndicator())) {
                return JSON.parseObject(tick.getIndicator());
            }
        }
        return new JSONObject();
    }

    default  double getIndicator(JSONObject obj, String indicator, Double defaultData){
        if(obj.containsKey(indicator)){
            return obj.getDoubleValue(indicator);
        }
        return defaultData;
    }

    default double round(double data, int scale){
        double a = Math.pow(10, scale);
        return Math.round(data * a)/a;
    }
}
