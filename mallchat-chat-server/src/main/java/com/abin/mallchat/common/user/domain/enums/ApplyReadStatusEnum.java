package com.abin.mallchat.common.user.domain.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author : limeng
 * @description : 申请阅读状态枚举
 * @date : 2023/07/20
 */
@Getter
@AllArgsConstructor
public enum ApplyReadStatusEnum {

    UNREAD(1, "未读"),

    READ(2, "已读");

    private final Integer code;

    private final String desc;
}
