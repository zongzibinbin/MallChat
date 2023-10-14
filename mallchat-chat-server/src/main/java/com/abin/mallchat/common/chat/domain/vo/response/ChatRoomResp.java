package com.abin.mallchat.common.chat.domain.vo.response;

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
    @ApiModelProperty("房间id")
    private Long roomId;
    @ApiModelProperty("房间类型 1群聊 2单聊")
    private Integer type;
    @ApiModelProperty("是否全员展示的会话 0否 1是")
    private Integer hot_Flag;
    @ApiModelProperty("最新消息")
    private String text;
    @ApiModelProperty("会话名称")
    private String name;
    @ApiModelProperty("会话头像")
    private String avatar;
    @ApiModelProperty("房间最后活跃时间(用来排序)")
    private Date activeTime;
    @ApiModelProperty("未读数")
    private Integer unreadCount;
}
