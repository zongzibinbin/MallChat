package com.abin.mallchat.custom.chatai.domain;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class ChatGPTContext implements Serializable {

    private Long roomId;

    private Long uid;

    private List<ChatGPTMsg> msg = new ArrayList<>();

    public void addMsg(ChatGPTMsg msg) {
        this.msg.add(msg);
    }
}
