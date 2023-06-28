package com.abin.mallchat.custom.user.websocket;

import com.abin.mallchat.custom.common.handshake.HandshakeHandler;
import com.abin.mallchat.custom.common.handshake.HandshakePredicate;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * <p>
 *     netty公众组件注册
 * </p>
 *
 * @Title: NettyComponentConfig
 * @Author Macro Chen
 * @PACKAGE com.abin.mallchat.custom.user.websocket.NettyComponentConfig
 * @Date 2023-06-28 22:11:32
 */
@Configuration
public class NettyComponentConfig {

    /**
     * 鉴权逻辑类
     *
     * @return {@link HandshakePredicate }
     * @Name: handshakePredicate
     * @Author Macro Chen
     */
    @Bean
    public HandshakePredicate handshakePredicate() {
        return new HandshakePredicate();
    }

    /**
     * 握手鉴权处理类
     *
     * @return {@link HandshakeHandler }
     * @Name: handshakeHandler
     * @Author Macro Chen
     */
    @Bean
    public HandshakeHandler handshakeHandler() {
        return new HandshakeHandler(handshakePredicate());
    }
}
