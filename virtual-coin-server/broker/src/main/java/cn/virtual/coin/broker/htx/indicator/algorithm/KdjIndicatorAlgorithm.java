package cn.virtual.coin.broker.htx.indicator.algorithm;

import cn.virtual.coin.broker.htx.indicator.AlgorithmContext;
import cn.virtual.coin.broker.htx.indicator.IndicatorAlgorithm;
import cn.virtual.coin.broker.htx.utils.CollectionUtils;
import cn.virtual.coin.domain.dal.po.Candlestick;
import com.alibaba.fastjson.JSONObject;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author gdyang
 * @since 2021/10/31 7:56 下午
 */
@Setter
@NoArgsConstructor
public class KdjIndicatorAlgorithm implements IndicatorAlgorithm<Map<String, Double>> {

    private double K;
    private double D;
    private double J;
    private double rsv;
    private int[] p = new int[]{9,1,3};
    public KdjIndicatorAlgorithm(double k, double d) {
        this.K = k;
        this.D = d;
    }

    /**
     * K = rsv的m天移动平均值
     * D = K的m1天的移动平均值
     * J = 3K - 2D
     * D%>80，市场超买；D%<20，市场超卖。
     * J%>100，市场超买；J%<10，市场超卖。
     * KD金叉：K%上穿D%，为买进信号。
     * KD死叉：K%下破D%，为卖出信号。
     * @param context 计算参数
     * @return
     */
    @Override
    public Map<String, Double> execute(AlgorithmContext context) {
        List<Candlestick> data = context.get(DATA);
        Candlestick pre = CollectionUtils.findFirst(data);
        if(data.size() > 1){
            pre = data.get(data.size() -2);
        }
        JSONObject obj = get(pre);
        KdjIndicatorAlgorithm kdj = obj.containsKey("K") ? obj.toJavaObject(KdjIndicatorAlgorithm.class) : new KdjIndicatorAlgorithm(100.0, 100.0);
        this.rsv = RsvIndicatorAlgorithm.getRsv(data, quota());
        this.K = (kdj.K * (p[1] - 1)  + this.rsv)/ p[1];
        double ksum = this.K;
        if(data.size() > 2){
            for(int i = data.size() - 2; i>=data.size() - p[2]; i--){
                ksum += getIndicator(get(data.get(i)), "K", 100.0);
            }
        }
        this.D = ksum/ p[2];
        this.J = 3 * this.K - 2 * this.D;
        return get();
    }

    @Override
    public int quota() {
        return p[0];
    }

    public Map<String, Double> get(){
        Map<String, Double> res = new HashMap<>(3);
        res.put("K", round(this.K,2));
        res.put("D", round(this.D, 2));
        res.put("J", round(this.J,2));
        return res;
    }
}
