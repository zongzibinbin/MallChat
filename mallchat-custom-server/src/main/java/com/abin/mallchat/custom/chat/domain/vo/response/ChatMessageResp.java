package com.abin.mallchat.custom.chat.domain.vo.response;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.Map;

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
        @ApiModelProperty("用户名称")
        private String username;
        @ApiModelProperty("用户id")
        private Long uid;
        @ApiModelProperty("头像")
        private String avatar;
        @ApiModelProperty("归属地")
        private String locPlace;
        @ApiModelProperty("徽章标识，如果没有展示null")
        private Badge badge;
    }

    @Data
    public static class Message {
        @ApiModelProperty("消息id")
        private Long id;
        @ApiModelProperty("消息发送时间")
        private Date sendTime;
        @ApiModelProperty("消息内容-废弃")
        @Deprecated
        private String content;
        @ApiModelProperty("消息链接映射-废弃")
        @Deprecated
        private Map<String, String> urlTitleMap;
        @ApiModelProperty("消息类型 1正常文本 2.撤回消息")
        private Integer type;
        @ApiModelProperty("消息内容不同的消息类型，内容体不同，见https://www.yuque.com/snab/mallcaht/rkb2uz5k1qqdmcmd")
        private Object body;
        @ApiModelProperty("消息标记")
        private MessageMark messageMark;
        @ApiModelProperty("父消息，如果没有父消息，返回的是null")
        private ReplyMsg reply;

    }

    @Data
    @Deprecated
    public static class ReplyMsg {
        @ApiModelProperty("消息id")
        private Long id;
        @ApiModelProperty("用户名称")
        private String username;
        @ApiModelProperty("消息内容")
        private String content;
        @ApiModelProperty("是否可消息跳转 0否 1是")
        private Integer canCallback;
        @ApiModelProperty("跳转间隔的消息条数")
        private Integer gapCount;
    }

    @Data
    public static class MessageMark {
        @ApiModelProperty("点赞数")
        private Integer likeCount;
        @ApiModelProperty("该用户是否已经点赞 0否 1是")
        private Integer userLike;
        @ApiModelProperty("举报数")
        private Integer dislikeCount;
        @ApiModelProperty("该用户是否已经举报 0否 1是")
        private Integer userDislike;
    }

    @Data
    public static class Badge {
        @ApiModelProperty("徽章图像")
        private String img;
        @ApiModelProperty("徽章说明")
        private String describe;
    }
}
