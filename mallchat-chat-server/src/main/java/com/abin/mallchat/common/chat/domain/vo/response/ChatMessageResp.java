package com.abin.mallchat.common.chat.domain.vo.response;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * Description: 消息
 * Author: <a href="https://github.com/zongzibinbin">abin</a>
 * Date: 2023-03-23
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ChatMessageResp {

    @ApiModelProperty("发送者信息")
    private UserInfo fromUser;
    @ApiModelProperty("消息详情")
    private Message message;

    @Data
    public static class UserInfo {
        @ApiModelProperty("用户id")
        private Long uid;
    }

    @Data
    public static class Message {
        @ApiModelProperty("消息id")
        private Long id;
        @ApiModelProperty("房间id")
        private Long roomId;
        @ApiModelProperty("消息发送时间")
        private Date sendTime;
        @ApiModelProperty("消息类型 1正常文本 2.撤回消息")
        private Integer type;
        @ApiModelProperty("消息内容不同的消息类型，内容体不同，见https://www.yuque.com/snab/mallcaht/rkb2uz5k1qqdmcmd")
        private Object body;
        @ApiModelProperty("消息标记")
        private MessageMark messageMark;
    }

    @Data
    public static class MessageMark {
        @ApiModelProperty("点赞数")
        private Integer likeCount;
        @ApiModelProperty("该用户是否已经点赞 0否 1是")
        private Integer userLike;
        @ApiModelProperty("点踩数")
        private Integer dislikeCount;
        @ApiModelProperty("该用户是否已经点踩 0否 1是")
        private Integer userDislike;
        @ApiModelProperty("爱心数")
        private Integer loveCount;
        @ApiModelProperty("该用户是否已经打爱心 0否 1是")
        private Integer userLove;
        @ApiModelProperty("大笑数")
        private Integer hahaCount;
        @ApiModelProperty("该用户是否已经打大笑 0否 1是")
        private Integer userHaha;
        @ApiModelProperty("惊讶数")
        private Integer wowCount;
        @ApiModelProperty("该用户是否已经打惊讶 0否 1是")
        private Integer userWow;
        @ApiModelProperty("哭数")
        private Integer cryCount;
        @ApiModelProperty("该用户是否已经打哭 0否 1是")
        private Integer userCry;
    }
}
