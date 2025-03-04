package cn.virtual.coin.broker.htx.indicator.algorithm;


import cn.virtual.coin.broker.htx.indicator.AlgorithmContext;
import cn.virtual.coin.broker.htx.indicator.IndicatorAlgorithm;
import cn.virtual.coin.domain.dal.po.Candlestick;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;

import java.util.List;

/**
 * @author gdyang
 * @since 2021/10/22 3:58 下午
 */
public class RelativeStrengthIndicatorAlgorithm implements IndicatorAlgorithm<Double> {
    /**
     * * 一个三周期的RSI（相对强弱指标），康纳RSI是一个由三部分组成的复合指标。其中两个使用相对强弱指数（RSI）计算，第三个以0到100范围来排列最新的价格变化。
     *      * 这三个因素一起形成了一个动量振荡指标，其波动范围在0到100之间以表示一个证券的超买（高值）或超卖（低值）的水平
     *      * RSI公式
     *      * RS = 14天内收市价上涨数之和的平均值/14天内收市价下跌数之和的平均值
     *      * RSI=100×RS/(1+RS) 或者，RSI=100－100÷（1+RS）
     * @param context 上下文
     * @return 结果
     */
    @Override
    public Double execute(AlgorithmContext context) {
        List<Candlestick> data = context.get(DATA);
        if(CollectionUtils.isEmpty(data)){
            return 0.0;
        }
        double incr =0.0, decr = 0.0;
        double d = 0.0;
        for(Candlestick tick: data){
            d = tick.getClose().subtract(tick.getOpen()).doubleValue();
            if(d >= 0){
                incr += d;
            }
            if(d < 0){
                decr += d;
            }
        }
        int quota = context.get(QUOTA);
        double rs = (Math.abs(incr) / quota) / (Math.abs(decr) /quota);
        return (1- 1/(1+rs)) * 100;
    }

    @Override
    public int quota() {
        return 14;
    }
}
