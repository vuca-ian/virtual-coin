package cn.virtual.coin.server.controller;

import cn.virtual.coin.domain.dal.po.Candlestick;
import cn.virtual.coin.domain.service.ICandlestickService;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * cn.virtual.coin.server.controller
 *
 * @author yang guo dong
 * @since 2025/2/27 15:16
 */
@RestController
@RequestMapping("/{version}/candlestick")
@RequiredArgsConstructor
public class CandlestickController {

    private final ICandlestickService candlestickService;

    @GetMapping
    public List<Candlestick> query(@RequestParam("symbol") String symbol, @RequestParam("period") String period){
        return candlestickService.select(Wrappers.<Candlestick>lambdaQuery().eq(Candlestick::getSymbol,symbol).eq(Candlestick::getPeriod,period).eq(Candlestick::getIndicator, "123"));
    }

    @PutMapping
    public void save(@RequestBody Candlestick candlestick){
        candlestickService.save(candlestick);
    }
}
