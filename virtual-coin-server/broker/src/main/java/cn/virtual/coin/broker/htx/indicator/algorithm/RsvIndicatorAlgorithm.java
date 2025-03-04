package cn.virtual.coin.broker.htx.indicator.algorithm;

import cn.virtual.coin.broker.htx.indicator.AlgorithmContext;
import cn.virtual.coin.broker.htx.indicator.IndicatorAlgorithm;
import cn.virtual.coin.domain.dal.po.Candlestick;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

/**
 * @author gdyang
 * @since 2021/10/31 7:58 下午
 */
@Slf4j
@NoArgsConstructor
public class RsvIndicatorAlgorithm implements IndicatorAlgorithm<Double> {
    private int quota = 9;

    public RsvIndicatorAlgorithm(int quota) {
        this.quota = quota;
    }
    public static RsvIndicatorAlgorithm of(int quota){
        return new RsvIndicatorAlgorithm(quota);
    }

    /**
     *  rsv =(收盘价 – n日内最低价)/(n日内最高价 – n日内最低价)×100
     * @param context 计算参数
     */
    @Override
    public Double execute(AlgorithmContext context) {
        List<Candlestick> data = context.get(DATA);
//        if(data.size() < quota){
//            return 100.0;
//        }
        double high = 0.0, low = Double.MAX_VALUE;
        for (Candlestick datum : data) {
            high = Math.max(high, datum.getHigh().doubleValue());
            low = Math.min(low, datum.getLow().doubleValue());
        }
        double current = data.get(data.size() - 1).getClose().doubleValue();
        return (current - low) /(high - low) * 100;
    }

    @Override
    public int quota() {
        return quota;
    }

    public static double getRsv(List<Candlestick> data, int quota){
        return RsvIndicatorAlgorithm.of(quota).execute(AlgorithmContext.of("RSV").put(DATA, data));
    }
}
