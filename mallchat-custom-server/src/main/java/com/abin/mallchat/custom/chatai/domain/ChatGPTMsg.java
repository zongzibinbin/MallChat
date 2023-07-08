package com.abin.mallchat.custom.chatai.domain;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
public class ChatGPTMsg implements Serializable {

    private String role;

    private String content;


}
