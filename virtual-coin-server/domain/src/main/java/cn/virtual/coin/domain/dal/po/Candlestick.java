package cn.virtual.coin.domain.dal.po;


import cn.vuca.cloud.api.model.Model;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Date;

/**
 * cn.virtual.coin.domain.dal.po
 *
 * @author yang guo dong
 * @since 2025/2/27 15:02
 */
@Data
@TableName("t_candlestick")
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class Candlestick extends Model<Long> {

    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    private String symbol;

    private String period;

    private Integer count;

    private String amount;
    private BigDecimal open;
    private BigDecimal close;
    private BigDecimal low;
    private BigDecimal high;

    private String vol;

    private String analysis;


    private String indicator;

    private Long openTime;

    public Candlestick(BigDecimal close) {
        this.close = close;
    }
}
