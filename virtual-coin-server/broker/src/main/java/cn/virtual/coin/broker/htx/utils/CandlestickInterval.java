package cn.virtual.coin.broker.htx.utils;

import lombok.Getter;

import java.util.Arrays;

/**
 * @author gdyang
 * @since 2025/2/26 23:14
 */
@Getter
public enum CandlestickInterval {

    /**
     *
     */
    MIN1("1min", 6,1, "1分钟K线"),
    MIN5("5min", 12,1, "5分钟K线"),
    MIN15("15min", 24,1, "15分钟K线"),
    MIN30("30min", 24*15,1, "30分钟K线"),
    MIN60("60min", 24*30,1, "1小时K线"),
    HOUR4("4hour", 24*30,24, "4小时K线"),
    DAY1("1day", 720,1, "1日K线"),
    MON1("1mon", 720,1, "1月K线"),
    WEEK1("1week", 720,1, "1周K线"),
    YEAR1("1year", 720,1, "1年K线")
    ;

    private final String code;
    private final Integer num;
    private final Integer  hours;
    private final String desc;

    CandlestickInterval(String code, Integer num, Integer hours, String desc) {
        this.code = code;
        this.num = num;
        this.hours = hours;
        this.desc = desc;

    }

    public static CandlestickInterval accept(String code) throws Exception {
        return Arrays.stream(CandlestickInterval.values()).filter(ci -> ci.getCode().equals(code)).findFirst().orElseThrow(()->new Exception("不支持"));
    }
}
