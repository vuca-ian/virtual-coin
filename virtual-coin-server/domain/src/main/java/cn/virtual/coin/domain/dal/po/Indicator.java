package cn.virtual.coin.domain.dal.po;

import cn.vuca.cloud.api.model.Model;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author gdyang
 * @since 2025/2/26 22:56
 */

@EqualsAndHashCode(callSuper = true)
@Data
@TableName("t_indicator")
public class Indicator extends Model<String> {
    @TableId(type = IdType.ASSIGN_ID)
    private String id;

    private String indicator;

    public Indicator(String indicator) {
        this.indicator = indicator;
    }
}
