package cn.virtual.coin.broker.htx.indicator;


import cn.virtual.coin.broker.htx.indicator.algorithm.*;

/**
 * @author gdyang
 * @since  2021/10/22 3:16 下午
 */
public interface IndicatorFactory {

    IndicatorAlgorithm<?> getIndicatorAlgorithm(String algoName);

    class Default<R> implements IndicatorFactory{


        @Override
        public IndicatorAlgorithm<?> getIndicatorAlgorithm(String algoName) {
            if(algoName.contains("MACD")){
                return new MacdIndicatorAlgorithm();
            }
            if(algoName.contains("EMA")){
                return new ExponentialMovingAverageIndicatorAlgorithm();
            }
            if(algoName.contains("MA")){
                return new MoveAverageIndicatorAlgorithm();
            }
            if(algoName.contains("RSI")){
                return new RelativeStrengthIndicatorAlgorithm();
            }
            if(algoName.equals(IndicatorAlgorithm.KDJ)){
                return new KdjIndicatorAlgorithm();
            }
            if(algoName.equals("BOLL")){
                return new BollIndicatorAlgorithm();
            }
            return null;
        }

        @SuppressWarnings("unchecked")
        public static <R> IndicatorAlgorithm<R> get(String algoName){
            return (IndicatorAlgorithm<R>) new Default<R>().getIndicatorAlgorithm(algoName);
        }
    }
}
