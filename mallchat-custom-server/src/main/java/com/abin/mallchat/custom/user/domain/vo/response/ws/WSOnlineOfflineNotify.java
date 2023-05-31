package com.abin.mallchat.custom.user.domain.vo.response.ws;

import com.abin.mallchat.custom.chat.domain.vo.response.ChatMemberResp;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 * 用户上下线变动的推送类
 * </p>
 *
 * @author <a href="https://github.com/zongzibinbin">abin</a>
 * @since 2023-03-19
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class WSOnlineOfflineNotify {

    /**
     * 新的上下线用户
     */
    private List<ChatMemberResp> changeList = new ArrayList<>();

    /**
     * 在线人数
     */
    private Long onlineNum;

    /**
     * 总人数
     */
    private Long totalNum;

}
