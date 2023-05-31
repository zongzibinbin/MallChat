package com.abin.mallchat.custom.chat.domain.vo.response;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * <p>
 * 群成员统计信息
 * </p>
 *
 * @author <a href="https://github.com/zongzibinbin">abin</a>
 * @since 2023-03-23
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ChatMemberStatisticResp {

    /**
     * 在线人数
     */
    @ApiModelProperty("在线人数")
    private Long onlineNum;

    /**
     * 总人数
     */
    @ApiModelProperty("总人数")
    private Long totalNum;

}
