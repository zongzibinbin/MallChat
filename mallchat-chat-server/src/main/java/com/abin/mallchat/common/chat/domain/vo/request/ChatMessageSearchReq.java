package com.abin.mallchat.common.chat.domain.vo.request;

import com.abin.mallchat.common.common.domain.vo.request.CursorPageBaseReq;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import java.util.Date;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ChatMessageSearchReq extends CursorPageBaseReq {
    @NotNull
    @ApiModelProperty("会话id")
    private Long roomId;

    @ApiModelProperty("搜索关键字")
    private String keyword;

    @ApiModelProperty("开始时间")
    private Date startTime;

    @ApiModelProperty("结束时间")
    private Date endTime;
}
