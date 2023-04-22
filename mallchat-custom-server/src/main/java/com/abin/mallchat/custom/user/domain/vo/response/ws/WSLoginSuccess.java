package com.abin.mallchat.custom.user.domain.vo.response.ws;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * Description:
 * Author: <a href="https://github.com/zongzibinbin">abin</a>
 * Date: 2023-03-19
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class WSLoginSuccess {
    private Long uid;
    private String avatar;
    private String token;
    private String name;
}
