package cn.virtual.coin.domain.service.impl;

import cn.virtual.coin.domain.dal.po.Candlestick;
import cn.virtual.coin.domain.service.ICandlestickService;
import cn.vuca.cloud.api.exception.ServiceException;
import cn.vuca.cloud.dal.service.AbstractBaseServiceImpl;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import lombok.extern.slf4j.Slf4j;
import org.mybatis.spring.MyBatisSystemException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

/**
 * cn.virtual.coin.domain.service.impl
 *
 * @author yang guo dong
 * @since 2025/2/27 15:05
 */
@Slf4j
@Service
public class CandlestickServiceImpl extends AbstractBaseServiceImpl<Candlestick> implements ICandlestickService {
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    @Override
    public void saveCandlestick(Candlestick candlestick){
        boolean update = false;
        try {
            this.save(candlestick);
        }catch (MyBatisSystemException | DuplicateKeyException se) {
            log.debug("数据主键冲突，数据进行更新操作！{}", candlestick);
            this.update(candlestick);
            update = true;
        } catch (Exception e){
            log.error("save candlestick error:{}", candlestick, e);
        }finally {
            log.info("receive and {} at {}, close: {}, tick {}",
                    update? "updated": "saved",
                    formatter.format(Instant.ofEpochMilli(candlestick.getId()*1000).atZone(ZoneId.systemDefault()).toLocalDateTime()),
                    candlestick.getClose(), candlestick);
        }
    }
}
