package cn.virtual.coin.broker.htx.indicator;

import lombok.Data;

import java.util.HashMap;
import java.util.Map;

/**
 * @author gdyang
 * @since  2021/10/22 11:12 上午
 */
@Data
public class AlgorithmContext {
    private String algoName;
    private final Map<String, Object> contextMap;

    public AlgorithmContext() {
        this.contextMap = new HashMap<>();
    }

    public AlgorithmContext algoName(String algoName){
        this.algoName = algoName;
        return this;
    }

    public <V>AlgorithmContext put(String key, V v){
        this.contextMap.put(key, v);
        return this;
    }

    public <V>V get(String key){
        return (V) this.contextMap.get(key);
    }

    public static AlgorithmContext of(String algoName){
        return new AlgorithmContext().algoName(algoName);
    }
}
