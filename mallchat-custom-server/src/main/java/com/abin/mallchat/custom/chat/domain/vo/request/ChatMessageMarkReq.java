package com.abin.mallchat.custom.chat.domain.vo.request;

import com.abin.mallchat.common.common.domain.vo.request.CursorPageBaseReq;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotNull;

/**
 * Description: 消息标记请求
 * Author: <a href="https://github.com/zongzibinbin">abin</a>
 * Date: 2023-03-29
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ChatMessageMarkReq{
    @NotNull
    @ApiModelProperty("消息id")
    private Long msgId;

    @NotNull
    @ApiModelProperty("标记类型 1点赞 2举报")
    private Integer markType;

    @NotNull
    @ApiModelProperty("动作类型 1确认 2取消")
    private Integer actType;
}
