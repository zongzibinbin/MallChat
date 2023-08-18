package com.abin.mallchat.common.common.domain.dto;

import com.abin.mallchat.common.user.domain.enums.WSBaseResp;
import com.abin.mallchat.common.user.domain.enums.WSPushTypeEnum;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * Description: 扫码成功对象，推送给用户的消息对象
 * Author: <a href="https://github.com/zongzibinbin">abin</a>
 * Date: 2023-08-12
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ScanSuccessMessageDTO implements Serializable {
    /**
     * 推送的ws消息
     */
    private WSBaseResp<?> wsBaseMsg;
    /**
     * 推送的uid
     */
    private Integer loginCode;

    public ScanSuccessMessageDTO(Integer loginCode, WSBaseResp<?> wsBaseMsg) {
        this.loginCode = loginCode;
        this.wsBaseMsg = wsBaseMsg;
    }
}
