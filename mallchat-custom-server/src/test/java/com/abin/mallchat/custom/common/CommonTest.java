package com.abin.mallchat.custom.common;

import org.junit.Test;

import static com.abin.mallchat.custom.user.service.adapter.FriendAdapter.buildFriendRoomKey;

/**
 * Description:
 * Author: <a href="https://github.com/zongzibinbin">abin</a>
 * Date: 2023-07-22
 */
public class CommonTest {
    @Test
    public void test() {
        System.out.println(buildFriendRoomKey(100L, 102L));
    }

}
