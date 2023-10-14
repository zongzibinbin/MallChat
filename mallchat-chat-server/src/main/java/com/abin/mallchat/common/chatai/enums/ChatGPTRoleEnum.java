package com.abin.mallchat.common.chatai.enums;

public enum ChatGPTRoleEnum {
    SYSTEM("system"),
    USER("user"),
    ASSISTANT("assistant");

    private final String role;

    ChatGPTRoleEnum(String role) {
        this.role = role;
    }

    public String getRole() {
        return role;
    }
}