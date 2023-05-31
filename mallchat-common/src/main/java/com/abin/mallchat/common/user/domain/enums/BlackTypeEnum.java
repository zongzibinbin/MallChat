package com.abin.mallchat.common.user.domain.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * <p>
 * 物品枚举
 * </p>
 *
 * @author <a href="https://github.com/zongzibinbin">abin</a>
 * @since 2023-03-19
 */
@AllArgsConstructor
@Getter
public enum BlackTypeEnum {

    IP(1),
    UID(2),
    ;

    private final Integer type;

}
