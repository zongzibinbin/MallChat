package com.abin.mallchat.common.user.domain.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.Date;

/**
 * <p>
 * 用户联系人表
 * </p>
 *
 * @author <a href="https://github.com/zongzibinbin">abin</a>
 * @since 2023-07-16
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("user_friend")
public class UserFriend implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * id
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * uid
     */
    @TableField("uid")
    private Long uid;

    /**
     * 好友uid
     */
    @TableField("friend_uid")
    private Long friendUid;

    /**
     * 逻辑删除(0-正常,1-删除)
     */
    @TableField("delete_status")
    @TableLogic(value = "0", delval = "1")
    private Integer deleteStatus;

    /**
     * 创建时间
     */
    @TableField("create_time")
    private Date createTime;

    /**
     * 修改时间
     */
    @TableField("update_time")
    private Date updateTime;


}
