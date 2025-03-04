package cn.virtual.coin.broker.htx.indicator.algorithm;


import cn.virtual.coin.broker.htx.indicator.AlgorithmContext;
import cn.virtual.coin.broker.htx.indicator.IndicatorAlgorithm;
import cn.virtual.coin.domain.dal.po.Candlestick;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;

/**
 * @author gdyang
 * @since 2021/10/22 4:08 下午
 */
public class ExponentialMovingAverageIndicatorAlgorithm implements IndicatorAlgorithm<Double> {
    @Override
    public Double execute(AlgorithmContext context) {
        List<Candlestick> data = context.get(DATA);
        Double current = data.get(data.size() - 1).getClose().doubleValue();
        Double prev = context.get(PREV);
        int quota = context.get(QUOTA);
        double k = 2 /(quota + 1.0);
        return round((current - prev) * k + prev, 2);
    }

    @Override
    public int quota() {
        return 2;
    }

    public static Double  getEma(int quota, Double current, Double prev){
        AlgorithmContext context = AlgorithmContext.of("ema").put(DATA, Collections.singletonList(new Candlestick(BigDecimal.valueOf(current)))).put(PREV, prev).put(QUOTA, quota);
        return new ExponentialMovingAverageIndicatorAlgorithm().execute(context);
    }
}
