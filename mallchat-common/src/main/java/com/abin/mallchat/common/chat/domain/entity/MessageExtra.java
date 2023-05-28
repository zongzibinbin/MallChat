package com.abin.mallchat.common.chat.domain.entity;

import com.abin.mallchat.common.user.domain.entity.IpDetail;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Map;

/**
 * Description: 消息扩展属性
 * Author: <a href="https://github.com/zongzibinbin">abin</a>
 * Date: 2023-05-28
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MessageExtra implements Serializable {
    private static final long serialVersionUID = 1L;
    //注册时的ip
    private Map<String, String> urlTitleMap;
}
