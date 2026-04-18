package com.abin.mallchat.common.chat.domain.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Description: 消息标记类型
 * Author: <a href="https://github.com/zongzibinbin">abin</a>
 * Date: 2023-03-19
 */
@AllArgsConstructor
@Getter
public enum MessageMarkTypeEnum {
    LIKE(1, "点赞", 10),
    DISLIKE(2, "点踩", 5),
    LOVE(3, "爱心", 10),
    HAHA(4, "大笑", 10),
    WOW(5, "惊讶", 10),
    CRY(6, "哭", 10),
    ;

    private final Integer type;
    private final String desc;
    private final Integer riseNum;//需要多少个标记升级

    private static final int MARK_TYPE_LIKE_DISLIKE = 0;
    private static final int MARK_TYPE_EMOJI = 1;

    public static int getMarkGroup(Integer type) {
        if (type == null) {
            return -1;
        }
        if (type >= LIKE.getType() && type <= DISLIKE.getType()) {
            return MARK_TYPE_LIKE_DISLIKE;
        }
        if (type >= LOVE.getType() && type <= CRY.getType()) {
            return MARK_TYPE_EMOJI;
        }
        return -1;
    }

    public static boolean isEmojiMark(Integer type) {
        if (type == null) {
            return false;
        }
        return type >= LOVE.getType() && type <= CRY.getType();
    }

    public static boolean isLikeDislikeMark(Integer type) {
        if (type == null) {
            return false;
        }
        return type >= LIKE.getType() && type <= DISLIKE.getType();
    }

    private static Map<Integer, MessageMarkTypeEnum> cache;

    static {
        cache = Arrays.stream(MessageMarkTypeEnum.values()).collect(Collectors.toMap(MessageMarkTypeEnum::getType, Function.identity()));
    }

    public static MessageMarkTypeEnum of(Integer type) {
        return cache.get(type);
    }
}
