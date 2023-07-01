package com.abin.mallchat.custom.chat.domain.vo.request.msg;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.util.List;

/**
 * Description: 文本消息入参
 * Author: <a href="https://github.com/zongzibinbin">abin</a>
 * Date: 2023-06-04
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TextMsgReq {

    @NotBlank(message = "内容不能为空")
    @Size(max = 5,message = "消息内容过长，服务器扛不住啊，兄dei")
    @ApiModelProperty("消息内容")
    private String content;

    @ApiModelProperty("回复的消息id,如果没有别传就好")
    private Long replyMsgId;

    @ApiModelProperty("艾特的uid")
    private List<Long> atUidList;
}
