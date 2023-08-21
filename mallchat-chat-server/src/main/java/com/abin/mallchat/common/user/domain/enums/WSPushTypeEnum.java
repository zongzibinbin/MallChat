package com.abin.mallchat.common.user.domain.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Description:
 * Author: <a href="https://github.com/zongzibinbin">abin</a>
 * Date: 2023-07-29
 */
@AllArgsConstructor
@Getter
public enum WSPushTypeEnum {
    USER(1, "个人"),
    ALL(2, "全部连接用户"),
    ;

    private final Integer type;
    private final String desc;

    private static Map<Integer, WSPushTypeEnum> cache;

    static {
        cache = Arrays.stream(WSPushTypeEnum.values()).collect(Collectors.toMap(WSPushTypeEnum::getType, Function.identity()));
    }

    public static WSPushTypeEnum of(Integer type) {
        return cache.get(type);
    }
}
