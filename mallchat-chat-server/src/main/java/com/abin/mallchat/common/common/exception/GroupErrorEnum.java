package com.abin.mallchat.common.common.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @Author Kkuil
 * @Date 2023/10/24 15:50
 * @Description 群异常码
 */
@AllArgsConstructor
@Getter
public enum GroupErrorEnum implements ErrorEnum {
    /**
     *
     */
    GROUP_NOT_EXIST(9001, "该群不存在~"),
    NOT_ALLOWED_OPERATION(9002, "您无权操作~"),
    MANAGE_COUNT_EXCEED(9003, "群管理员数量达到上限，请先删除后再操作~"),
    USER_NOT_IN_GROUP(9004, "非法操作，用户不存在群聊中~"),
    NOT_ALLOWED_FOR_REMOVE(9005, "非法操作，你没有移除该成员的权限"),
    NOT_ALLOWED_FOR_EXIT_GROUP(9006, "非法操作，不允许退出大群聊"),
    ;
    private final Integer code;
    private final String msg;

    @Override
    public Integer getErrorCode() {
        return this.code;
    }

    @Override
    public String getErrorMsg() {
        return this.msg;
    }
}
