package com.abin.mallchat.common.chat.domain.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Date;

/**
 * 用户表情包
 *
 * @author: WuShiJie
 * @createTime: 2023/7/2 22:00
 */
@Data
@TableName(value = "user_emojis")
public class UserEmojis implements Serializable {
    private static final long serialVersionUID = -7690290707154737263L;

    /**
     * id
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 用户表ID
     */
    @TableField(value = "uid")
    private Long uid;


    /**
     * 表情地址
     */
    @NotNull
    @TableField(value = "expression_url")
    private String expressionUrl;

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

    @TableField(value = "delete_status")
    @TableLogic(value = "0",delval = "1")
    private Integer deleteStatus;


}
