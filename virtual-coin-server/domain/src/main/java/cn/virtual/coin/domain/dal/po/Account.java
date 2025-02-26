package cn.virtual.coin.domain.dal.po;

import cn.vuca.cloud.api.model.Model;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;

/**
 * @author gdyang
 * @since 2025/2/25 22:57
 */
@EqualsAndHashCode(callSuper = true)
@Data
@TableName("t_account")
public class Account extends Model<Long> {

    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    private String userId;

    private Long accountId;

    private String type;

    private String subtype;

    private String state;

    private String createdBy;

    private String modifiedBy;

    private Date createdTime;

    private Date modifiedTime;

    private Boolean deleted;
}
