package com.abin.mallchat.custom.chat.domain.vo.response;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * <p>
 * 群成员列表的成员信息
 * </p>
 *
 * @author <a href="https://github.com/zongzibinbin">abin</a>
 * @since 2023-03-23
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ChatMemberResp {

    @ApiModelProperty("uid")
    private Long uid;

    @ApiModelProperty("用户名称")
    private String name;

    @ApiModelProperty("头像")
    private String avatar;

    /**
     * @see com.abin.mallchat.common.user.domain.enums.ChatActiveStatusEnum
     */
    @ApiModelProperty("在线状态 1在线 2离线")
    private Integer activeStatus;

    @ApiModelProperty("最后一次上下线时间")
    private Date lastOptTime;

}
