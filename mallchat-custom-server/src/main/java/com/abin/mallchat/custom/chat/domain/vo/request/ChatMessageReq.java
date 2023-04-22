package com.abin.mallchat.custom.chat.domain.vo.request;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotNull;


/**
 * Description: 消息发送请求体
 * Author: <a href="https://github.com/zongzibinbin">abin</a>
 * Date: 2023-03-23
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ChatMessageReq {

    @NotNull
    @Length( max = 10000,message = "消息内容过长，服务器扛不住啊，兄dei")
    @ApiModelProperty("消息内容")
    private String content;

    @NotNull
    @ApiModelProperty("会话id")
    private Long roomId;

    @ApiModelProperty("回复的消息id,如果没有别传就好")
    private Long replyMsgId;
}
