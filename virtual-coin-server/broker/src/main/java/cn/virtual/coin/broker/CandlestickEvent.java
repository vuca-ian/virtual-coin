package cn.virtual.coin.broker;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;
import org.springframework.lang.NonNull;

/**
 * cn.virtual.coin.broker
 *
 * @author yang guo dong
 * @since 2025/3/3 16:30
 */
@Getter
public class CandlestickEvent extends ApplicationEvent {

    private final EventType type;

    public CandlestickEvent(@NonNull Object source, EventType type) {
        super(source);
        this.type = type;
    }


    public enum EventType{
        calc,
        batch;
    }
}
