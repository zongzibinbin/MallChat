package com.abin.mallchat.custom.common.handshake;

import org.springframework.stereotype.Component;

import java.util.function.Predicate;


/**
 * <p>
 *  WS链接握手鉴权验证
 * </p>
 *
 * @Title: HandshakePredicate
 * @Author Macro Chen
 * @PACKAGE com.abin.mallchat.custom.common.handshake.HandshakePredicate
 * @Date 2023-06-28 22:06:05
 */
@Component
public class HandshakePredicate implements Predicate<HandshakeEvent> {

    /**
     * 验证身份信息，本方法切勿进行耗时操作！！！
     *
     * @param event
     * @return true验证通过 false验证失败
     */
    @Override
    public boolean test(HandshakeEvent event) {

        /**
         * 可通过header或者uri传递参数
         *  String token = event.getHeader("token");
         *  String token = event.getParameter("token");
         *  do auth....
         */

        return true;
    }
}
