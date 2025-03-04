package cn.virtual.coin.domain.service;

import cn.virtual.coin.domain.dal.po.Candlestick;
import cn.vuca.cloud.dal.service.BaseService;

/**
 * cn.virtual.coin.domain.service
 *
 * @author yang guo dong
 * @since 2025/2/27 15:04
 */
public interface ICandlestickService extends BaseService<Candlestick> {
    void saveCandlestick(Candlestick candlestick);
}
