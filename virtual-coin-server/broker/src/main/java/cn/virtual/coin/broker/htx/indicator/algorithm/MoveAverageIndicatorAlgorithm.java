package cn.virtual.coin.broker.htx.indicator.algorithm;


import cn.virtual.coin.broker.htx.indicator.AlgorithmContext;
import cn.virtual.coin.broker.htx.indicator.IndicatorAlgorithm;
import cn.virtual.coin.domain.dal.po.Candlestick;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;

import java.util.List;

/**
 * @author gdyang
 * @since 2021/10/22 3:20 下午
 */
public class MoveAverageIndicatorAlgorithm implements IndicatorAlgorithm<Double> {

    public static double getMA(List<Candlestick> data, int quota){
        return new MoveAverageIndicatorAlgorithm().execute(AlgorithmContext.of("MA" + quota).put(DATA, data).put(QUOTA, quota));
    }

    @Override
    public Double execute(AlgorithmContext context) {
        List<Candlestick> data = context.get(DATA);
        if(CollectionUtils.isEmpty(data)){
            return 0.0;
        }
        int quota = context.get(QUOTA);
        if(data.size() < quota){
            quota = data.size();
        }
        double sum = data.stream().mapToDouble(d -> d.getClose().doubleValue()).sum();
        return round(sum / quota , 2);
    }
}
