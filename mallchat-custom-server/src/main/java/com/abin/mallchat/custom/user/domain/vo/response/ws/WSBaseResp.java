package com.abin.mallchat.custom.user.domain.vo.response.ws;

import lombok.Data;

/**
 * <p>
 * ws的基本返回信息体
 * </p>
 *
 * @author <a href="https://github.com/zongzibinbin">abin</a>
 * @since 2023-03-19
 */
@Data
public class WSBaseResp<T> {

    /**
     * ws推送给前端的消息
     *
     * @see com.abin.mallchat.custom.user.domain.enums.WSRespTypeEnum
     */
    private Integer type;

    private T data;

}
