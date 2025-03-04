package cn.virtual.coin.domain.dal.po;

import cn.vuca.cloud.api.model.Model;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

/**
 * @author gdyang
 * @since 2025/2/26 23:06
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@TableName("t_job_history")
public class JobHistory extends Model<String> {
    @TableId(type = IdType.ASSIGN_ID)
    private String id;

    private String symbol;

    private String period;

    private String lastDataTime;

    private Integer loopCount;

    private Long lastDataId;

    public JobHistory(String symbol, String period) {
        this.symbol = symbol;
        this.period = period;
    }
}
