package com.abin.mallchat.custom.spring;

import com.abin.mallchat.custom.chat.service.WeChatMsgOperationService;
import com.abin.mallchat.custom.chat.service.impl.ChatServiceImpl;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Collections;

/**
 * Description: 微信模板测试
 * Author: <a href="https://github.com/zongzibinbin">abin</a>
 * Date: 2023-07-06
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class WXTemplate {

    @Autowired
    private WeChatMsgOperationService chatMsgOperationService;
    @Autowired
    private ChatServiceImpl chatService;

    @Test
    public void test() {
        chatMsgOperationService.publishChatMsgToWeChatUser(1L, Collections.singletonList(10008L), "你家规下去");
    }
}
