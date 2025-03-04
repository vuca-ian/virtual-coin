package cn.virtual.coin.broker.htx.indicator.algorithm;

import cn.virtual.coin.broker.htx.indicator.AlgorithmContext;
import cn.virtual.coin.broker.htx.indicator.IndicatorAlgorithm;
import cn.virtual.coin.domain.dal.po.Candlestick;
import lombok.Data;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author gdyang
 * @since 2021/11/1 11:14 上午
 */
@Data
public class BollIndicatorAlgorithm implements IndicatorAlgorithm<Map<String, Double>> {
    private static final int P = 2;
    /**
     * 标准差
     */
    private double md;
    private double ub;
    private double mb;
    private double lb;

    @Override
    public Map<String, Double> execute(AlgorithmContext context) {
        List<Candlestick> data = context.get(DATA);
        this.mb = MoveAverageIndicatorAlgorithm.getMA(data, quota());
        double std = 0.0;
        for(Candlestick tick : data){
            std += Math.pow(tick.getClose().doubleValue() - this.mb, 2);
        }
        this.md = Math.sqrt((std)/ quota());
        this.ub = this.mb + P * this.md;
        this.lb = this.mb - P * this.md;
        return get();
    }

    @Override
    public int quota() {
        return 20;
    }

    public Map<String, Double> get(){
        Map<String, Double> res = new HashMap<>(3);
        res.put("UB",round(this.ub,2));
        res.put("MB", round(this.mb,2));
        res.put("LB", round(this.lb,2));
        return res;
    }
}
