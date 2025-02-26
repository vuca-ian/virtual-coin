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
 * @since 2025/2/25 21:43
 */
@EqualsAndHashCode(callSuper = true)
@Data
@TableName("t_user_access")
public class UserAccess extends Model<String> {

    @TableId(type = IdType.ASSIGN_ID)
    private String id;

    private String userName;

    private String accessKey;

    private String secretKey;

    private String state;

    private String createdBy;

    private String modifiedBy;

    private Date createdTime;

    private Date modifiedTime;

    private Boolean deleted;
}
