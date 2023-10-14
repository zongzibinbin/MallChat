package com.abin.mallchat.common.user.domain.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Description: 物品枚举
 * Author: <a href="https://github.com/zongzibinbin">abin</a>
 * Date: 2023-03-19
 */
@AllArgsConstructor
@Getter
public enum ItemEnum {
    MODIFY_NAME_CARD(1L, ItemTypeEnum.MODIFY_NAME_CARD, "改名卡"),
    LIKE_BADGE(2L, ItemTypeEnum.BADGE, "爆赞徽章"),
    REG_TOP10_BADGE(3L, ItemTypeEnum.BADGE, "前十注册徽章"),
    REG_TOP100_BADGE(4L, ItemTypeEnum.BADGE, "前100注册徽章"),
    PLANET(5L, ItemTypeEnum.BADGE, "知识星球"),
    CONTRIBUTOR(6L, ItemTypeEnum.BADGE, "代码贡献者"),
    ;

    private final Long id;
    private final ItemTypeEnum typeEnum;
    private final String desc;

    private static Map<Long, ItemEnum> cache;

    static {
        cache = Arrays.stream(ItemEnum.values()).collect(Collectors.toMap(ItemEnum::getId, Function.identity()));
    }

    public static ItemEnum of(Long type) {
        return cache.get(type);
    }
}
