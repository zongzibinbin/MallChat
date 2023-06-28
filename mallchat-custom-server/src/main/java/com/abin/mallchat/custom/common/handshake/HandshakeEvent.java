package com.abin.mallchat.custom.common.handshake;

import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.QueryStringDecoder;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;

import java.util.List;

/**
 * <p>
 *  websocket客户端握手请求信息
 *  用于在握手阶段鉴权
 * </p>
 *
 * @Title: HandshakeEvent
 * @Author Macro Chen
 * @PACKAGE com.abin.mallchat.custom.common.handshake.HandshakeEvent
 * @Date 2023-06-28 22:05:51
 */
public class HandshakeEvent {

    /**
     * 请求的uri
     *
     * @Author Macro Chen
     * @see String
     */
    private final String uri;

    /**
     * 请求头
     *
     * @Author Macro Chen
     * @see HttpHeaders
     */
    private final HttpHeaders header;

    public HandshakeEvent(String uri, HttpHeaders header) {
        this.uri = uri;
        this.header = header;
    }

    public String getHeader(String name){
        return header.get(name);
    }

    public List<String> getHeaders(String name){
        return header.getAll(name);
    }

    public Integer getIntHeader(String name){
        return header.getInt(name);
    }

    public String getUri() {
        return uri;
    }

    /**
     * WebSocketServerProtocolHandler.HandshakeComplete 包装 HandshakeEvent鉴权事件
     *
     * @param event 事件
     * @return {@link HandshakeEvent }
     * @Name: of
     * @Author Macro Chen
     */
    public static HandshakeEvent of(WebSocketServerProtocolHandler.HandshakeComplete event){
        return new HandshakeEvent(event.requestUri(),event.requestHeaders());
    }

    /**
     * 解析获取指定请求参数
     *
     * @param name 参数名称
     * @return {@link String }
     * @Name: getParameter
     * @Author Macro Chen
     */
    public String getParameter(String name){
        QueryStringDecoder decoder = new QueryStringDecoder(uri);
        List<String> valueList = decoder.parameters().get(name);
        return valueList == null || valueList.isEmpty() ? null : valueList.get(0);
    }

    /**
     * 解析获取指定请求参数
     *
     * @param name 参数名称
     * @return {@link List }<{@link String }>
     * @Name: getParameters
     * @Author Macro Chen
     */
    public List<String> getParameters(String name){
        QueryStringDecoder decoder = new QueryStringDecoder(uri);
        return decoder.parameters().get(name);
    }

}
