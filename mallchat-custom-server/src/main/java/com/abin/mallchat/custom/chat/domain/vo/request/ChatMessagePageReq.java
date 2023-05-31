package com.abin.mallchat.custom.chat.domain.vo.request;

import com.abin.mallchat.common.common.domain.vo.request.CursorPageBaseReq;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

import javax.validation.constraints.NotNull;

/**
 * <p>
 * 消息列表请求
 * </p>
 *
 * @author <a href="https://github.com/zongzibinbin">abin</a>
 * @since 2023-03-29
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class ChatMessagePageReq extends CursorPageBaseReq {

    @NotNull
    @ApiModelProperty("会话id")
    private Long roomId;

}
