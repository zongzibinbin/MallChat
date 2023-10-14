package com.abin.mallchat.common.user.domain.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.Date;

/**
 * <p>
 * 用户申请表
 * </p>
 *
 * @author <a href="https://github.com/zongzibinbin">abin</a>
 * @since 2023-07-16
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("user_apply")
public class UserApply implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * id
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 申请人uid
     */
    @TableField("uid")
    private Long uid;

    /**
     * 申请类型 1加好友
     */
    @TableField("type")
    private Integer type;

    /**
     * 接收人uid
     */
    @TableField("target_id")
    private Long targetId;

    /**
     * 申请信息
     */
    @TableField("msg")
    private String msg;

    /**
     * 申请状态 1待审批 2同意
     */
    @TableField("status")
    private Integer status;

    /**
     * 阅读状态 1未读 2已读
     */
    @TableField("read_status")
    private Integer readStatus;

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
