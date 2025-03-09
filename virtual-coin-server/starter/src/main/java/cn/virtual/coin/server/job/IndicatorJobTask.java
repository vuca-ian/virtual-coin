package cn.virtual.coin.server.job;

import cn.virtual.coin.broker.htx.indicator.AlgorithmContext;
import cn.virtual.coin.broker.htx.indicator.IndicatorAlgorithm;
import cn.virtual.coin.broker.htx.indicator.IndicatorFactory;
import cn.virtual.coin.broker.htx.indicator.algorithm.MacdIndicatorAlgorithm;
import cn.virtual.coin.broker.htx.utils.CollectionUtils;
import cn.virtual.coin.broker.htx.utils.ServiceException;
import cn.virtual.coin.domain.dal.po.Candlestick;
import cn.virtual.coin.domain.dal.po.Indicator;
import cn.virtual.coin.domain.dal.po.JobHistory;
import cn.virtual.coin.domain.service.ICandlestickService;
import cn.virtual.coin.domain.service.IIndicatorService;
import cn.virtual.coin.domain.service.IJobHistoryService;
import cn.virtual.coin.server.configuration.JobTask;
import cn.vuca.cloud.commons.utils.StringUtils;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import jakarta.annotation.Resource;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.scheduling.quartz.QuartzJobBean;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executor;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * @author gdyang
 * @since 2025/2/26 21:35
 */
@Slf4j
//@JobTask(cron = "0 0/2 * * * ?", name = "IndicatorJobTask")
public class IndicatorJobTask extends QuartzJobBean {

    private static final Pattern PATTERN = Pattern.compile("(?!=[a-zA-Z]+)(\\d)*");
    private final DateTimeFormatter DEFAULT_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private final Lock lock = new ReentrantLock();
    @Resource
    private IJobHistoryService jobHistoryService;
    @Resource
    private IIndicatorService indicatorService;
    @Resource
    private ICandlestickService candlestickService;
    @Resource
    private Executor executor;
    @Override
    protected void executeInternal(@NonNull JobExecutionContext context) throws JobExecutionException {
        List<JobHistory> history = jobHistoryService.select(Wrappers.emptyWrapper());
        if (!CollectionUtils.isNotEmpty(history)) {
            return;
        }
        List<Indicator> indicators = indicatorService.select(Wrappers.query());
        if(!CollectionUtils.isNotEmpty(indicators)){
            return;
        }
        log.info("job history task: {}, {}", history, indicators.stream().map(Indicator::getIndicator).collect(Collectors.joining(",")));
        history.forEach(job -> run(job, indicators));
    }


    @SuppressWarnings("unchecked")
    private void run(JobHistory job, List<Indicator> indicators) {
        AtomicInteger counter = new AtomicInteger(job.getLoopCount());
        AtomicLong lastDataId = new AtomicLong(null == job.getLastDataId() ? 0L : job.getLastDataId());
        while (counter.getAndDecrement() > 0) {
            CountDownLatch latch = new CountDownLatch(indicators.size());
            try {
                Candlestick tick = candlestickService.selectOne(Wrappers.<Candlestick>lambdaQuery().eq(Candlestick::getSymbol, job.getSymbol())
                        .eq(Candlestick::getPeriod, job.getPeriod()).ge(Candlestick::getId, lastDataId.get()).last(" limit  1"));
                if (null == tick) {
                    break;
                }
                JSONObject obj = get(tick);
                indicators.forEach(indicator -> executor.execute(() -> {
                    Object result = execute(tick, indicator.getIndicator().toUpperCase(), job);
                    lock.lock();
                    try {
                        Thread.sleep(100);
                        if (result instanceof Map) {
                            obj.putAll((Map<String, Object>) result);
                        } else {
                            obj.put(indicator.getIndicator(), result);
                        }
                    } catch (Exception e) {
                        log.error("系统异常:{}", tick , e);
                    }
                    finally {
                        lock.unlock();
                    }
                    latch.countDown();
                }));
                latch.await();
                lastDataId.set(tick.getId());
                log.info("[{}]calc indicator for tick:{}#{}, indicator:{}", DEFAULT_FORMATTER.format(Instant.ofEpochSecond(lastDataId.get()).atZone(ZoneId.systemDefault())),
                        tick.getSymbol(), tick.getPeriod(), obj.toJSONString());
                tick.setIndicator(obj.toJSONString());
//                candlestickService.update(tick, Wrappers.<Candlestick>lambdaUpdate().eq(Candlestick::getSymbol, job.getSymbol())
//                        .eq(Candlestick::getPeriod, job.getPeriod()).eq(Candlestick::getId, tick.getId()));
            } catch (InterruptedException e) {
                throw new ServiceException("计算指标异常", e);
            }
        }
        job.setLastDataId(lastDataId.get());
        jobHistoryService.update(job);
    }

    private <R> R execute(Candlestick tick, String indicator, JobHistory job) {
        IndicatorAlgorithm<R> algo = IndicatorFactory.Default.get(indicator);
        int quota = match(indicator);
        quota = (quota > 0 ? quota : algo.quota());
        List<Candlestick> list = candlestickService.select(Wrappers.<Candlestick>lambdaQuery().eq(Candlestick::getSymbol, job.getSymbol())
                .eq(Candlestick::getPeriod, job.getPeriod()).le(Candlestick::getId, tick.getId()).orderByDesc(Candlestick::getId).last(" limit  " + quota));
        list = list.stream().sorted(Comparator.comparing(Candlestick::getId)).collect(Collectors.toList());
        Candlestick pre = CollectionUtils.findFirst(list);
        if(list.size() > 1){
            pre = list.get(list.size() -2);
        }
        JSONObject obj = get(pre);
        R result = null;
        if (CollectionUtils.isNotEmpty(list)) {
            if (algo instanceof MacdIndicatorAlgorithm) {
                AlgorithmContext context = AlgorithmContext.of(indicator).put(IndicatorAlgorithm.DATA, new MacdIndicatorAlgorithm(tick.getClose().doubleValue(),
                        obj.containsKey("DEA") ? obj.getDoubleValue("DEA") : 0.0,
                        getEma(pre, obj, "EMA12"), getEma(pre, obj, "EMA26")));
                return algo.execute(context);
            } else {
                Double preValue = getEma(pre, obj,indicator.toUpperCase());
                result = algo.execute(AlgorithmContext.of(indicator)
                        .put(IndicatorAlgorithm.DATA, list)
                        .put(IndicatorAlgorithm.PREV, preValue)
                        .put(IndicatorAlgorithm.QUOTA, quota));
            }
        }
        return result;
    }

    private Double getEma(Candlestick tick, JSONObject obj, String indicator){
        if(obj.containsKey(indicator)){
            return obj.getDoubleValue(indicator);
        }
        return tick.getClose().doubleValue();
    }

    private int match(String indicator) {
        Matcher matcher = PATTERN.matcher(indicator);
        int quota = 0;
        while (matcher.find()) {
            if (StringUtils.isNotEmpty(matcher.group())) {
                quota = Integer.parseInt(matcher.group());
                break;
            }
        }
        return quota;
    }

    private JSONObject get(Candlestick tick) {
        if (null != tick) {
            if (StringUtils.isNotEmpty(tick.getIndicator())) {
                return JSON.parseObject(tick.getIndicator());
            }
        }
        return new JSONObject();
    }
}
