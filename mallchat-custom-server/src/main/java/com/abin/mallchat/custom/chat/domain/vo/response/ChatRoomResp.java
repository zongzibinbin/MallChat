package com.abin.mallchat.custom.chat.domain.vo.response;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * Description: 群成员列表的成员信息
 * Author: <a href="https://github.com/zongzibinbin">abin</a>
 * Date: 2023-03-23
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ChatRoomResp {
    @ApiModelProperty("会话id")
    private Long id;
    @ApiModelProperty("会话名称")
    private String name;
    @ApiModelProperty("会话类型 1大群聊 2沸点")
    private Integer type;
    @ApiModelProperty("房间最后活跃时间")
    private Date lastActiveTime;
}
