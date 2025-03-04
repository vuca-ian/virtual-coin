package cn.virtual.coin.broker.htx.indicator.algorithm;
import cn.virtual.coin.broker.htx.indicator.AlgorithmContext;
import cn.virtual.coin.broker.htx.indicator.IndicatorAlgorithm;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.Map;

/**
 * @author gdyang
 * @since 2021/10/22 5:13 下午
 */
@Slf4j
@Data
public class MacdIndicatorAlgorithm implements IndicatorAlgorithm<Map<String, Double>> {
    private static final int DEA_QUOTA = 9;
    private Double slowEma;

    private Double fastEma;

    private Double diff;

    private Double dea;

    private Double macd;

    private Double data;

    public MacdIndicatorAlgorithm() {
    }

    public MacdIndicatorAlgorithm(Double data, Double dea, Double slowEma, Double fastEma) {
        this.dea = dea;
        this.data = data;
        this.slowEma = slowEma;
        this.fastEma = fastEma;
    }

    @Override
    public Map<String, Double> execute(AlgorithmContext context) {
        MacdIndicatorAlgorithm macd = context.get(DATA);
        this.data = macd.getData();
        this.slowEma = ExponentialMovingAverageIndicatorAlgorithm.getEma(12, macd.getData(), macd.getSlowEma());
        this.fastEma = ExponentialMovingAverageIndicatorAlgorithm.getEma(26, macd.getData(), macd.getFastEma());
        this.diff = this.getSlowEma() - this.getFastEma();
        this.dea = ExponentialMovingAverageIndicatorAlgorithm.getEma(DEA_QUOTA, this.diff, macd.getDea());
        this.macd = (this.diff - this.dea);
        return this.get();
    }

    @Override
    public int quota() {
        return 2;
    }

    public Map<String, Double> get(){
        Map<String, Double> res = new HashMap<>(3);
        res.put("DIFF", round(this.diff,2));
        res.put("DEA", round(this.dea,2));
        res.put("MACD", round(this.macd,2));
        return res;
    }
}
