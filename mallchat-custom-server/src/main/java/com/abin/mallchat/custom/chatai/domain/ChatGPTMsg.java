package com.abin.mallchat.custom.chatai.domain;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;

@Getter
@Setter
@ToString
public class ChatGPTMsg implements Serializable {

    private String role;

    private String content;


}
