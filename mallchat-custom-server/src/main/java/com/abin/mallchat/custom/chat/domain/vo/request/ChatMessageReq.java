package com.abin.mallchat.custom.chat.domain.vo.request;

import com.abin.mallchat.common.chat.domain.enums.MessageTypeEnum;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

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
    @ApiModelProperty("会话id")
    private Long roomId;

    @ApiModelProperty("消息类型")
    @NotNull
    private Integer msgType = MessageTypeEnum.TEXT.getType();

    @ApiModelProperty("消息内容，类型不同传值不同，见https://www.yuque.com/snab/mallcaht/rkb2uz5k1qqdmcmd")
    @NotNull
    private Object body;

}
