package com.abin.mallchat.custom.chat.domain.vo.response;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Description:
 * Author: <a href="https://github.com/zongzibinbin">abin</a>
 * Date: 2023-07-17
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MemberResp {
    @ApiModelProperty("群id")
    private Long id;
    @ApiModelProperty("群名称")
    private Long groupName;
    @ApiModelProperty("在线人数")
    private Long onlineNum;//在线人数
    @ApiModelProperty("群聊描述")
    private String desc;//在线人数
    @ApiModelProperty("成员角色 1群主 2管理员 3普通成员 4踢出群聊")
    private Integer role;//在线人数
}
