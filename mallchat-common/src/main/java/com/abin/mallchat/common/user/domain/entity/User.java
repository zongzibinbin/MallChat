package com.abin.mallchat.common.user.domain.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;

import java.time.LocalDateTime;

import com.baomidou.mybatisplus.annotation.TableField;

import java.io.Serializable;
import java.util.Date;

import com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler;
import lombok.*;
import lombok.experimental.Accessors;

/**
 * <p>
 * 用户表
 * </p>
 *
 * @author <a href="https://github.com/zongzibinbin">abin</a>
 * @since 2023-03-19
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Builder
@AllArgsConstructor
@NoArgsConstructor
@TableName(value = "user", autoResultMap = true)
public class User implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 用户id
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 用户昵称
     */
    @TableField("name")
    private String name;

    /**
     * 用户头像
     */
    @TableField("avatar")
    private String avatar;

    /**
     * 性别 1为男性，2为女性
     */
    @TableField("sex")
    private Integer sex;

    /**
     * 微信openid用户标识
     */
    @TableField("open_id")
    private String openId;

    /**
     * 最后上下线时间
     */
    @TableField("last_opt_time")
    private Date lastOptTime;

    /**
     * 最后上下线时间
     */
    @TableField(value = "ip_info", typeHandler = JacksonTypeHandler.class)
    private IpInfo ipInfo;

    /**
     * 佩戴的徽章id
     */
    @TableField("item_id")
    private Long itemId;

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

    public IpInfo getIpInfo() {
        if (ipInfo == null) {
            ipInfo = new IpInfo();
        }
        return ipInfo;
    }

}
