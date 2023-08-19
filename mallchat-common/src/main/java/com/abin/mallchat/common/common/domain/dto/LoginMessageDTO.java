package com.abin.mallchat.common.common.domain.dto;

import com.abin.mallchat.common.user.domain.enums.WSBaseResp;
import com.abin.mallchat.common.user.domain.enums.WSPushTypeEnum;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import me.chanjar.weixin.mp.bean.message.WxMpXmlMessage;

import java.io.Serializable;

/**
 * Description: 将扫码登录返回信息推送给所有横向扩展的服务
 * Author: zjy
 * Date: 2023-08-12
 */
@Data
@NoArgsConstructor
public class LoginMessageDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 微信公众号获得扫码事件后,发送给我方的回调信息
     */
    private WxMpXmlMessage wxMpXmlMessage ;

    public LoginMessageDTO(WxMpXmlMessage wxMpXmlMessage) {
         this.wxMpXmlMessage = wxMpXmlMessage;
    }

}
