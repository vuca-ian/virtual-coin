package cn.virtual.coin.websocket.utils;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author gdyang
 * @since 2025/2/24 23:15
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class NetworkLatency {

    private String path;

    private Long startNanoTime;

    private Long endNanoTime;
}
