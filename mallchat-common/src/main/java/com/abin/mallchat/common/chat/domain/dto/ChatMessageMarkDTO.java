package com.abin.mallchat.common.chat.domain.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Description: 消息标记请求
 * Author: <a href="https://github.com/zongzibinbin">abin</a>
 * Date: 2023-03-29
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ChatMessageMarkDTO {

    @ApiModelProperty("操作者")
    private Long uid;

    @ApiModelProperty("消息id")
    private Long msgId;

    /**
     * @see com.abin.mallchat.common.chat.domain.enums.MessageMarkTypeEnum
     */
    @ApiModelProperty("标记类型 1点赞 2举报")
    private Integer markType;

    /**
     * @see com.abin.mallchat.common.chat.domain.enums.MessageMarkActTypeEnum
     */
    @ApiModelProperty("动作类型 1确认 2取消")
    private Integer actType;
}
