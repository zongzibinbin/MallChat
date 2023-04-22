package com.abin.mallchat.custom.user.service.handler;

import cn.hutool.core.util.URLUtil;
import com.abin.mallchat.common.user.dao.UserDao;
import com.abin.mallchat.common.user.domain.entity.User;
import com.abin.mallchat.custom.user.service.LoginService;
import com.abin.mallchat.custom.user.service.WebSocketService;
import com.abin.mallchat.custom.user.service.WxMsgService;
import com.abin.mallchat.custom.user.service.adapter.TextBuilder;
import com.abin.mallchat.custom.user.service.adapter.UserAdapter;
import me.chanjar.weixin.common.error.WxErrorException;
import me.chanjar.weixin.common.session.WxSessionManager;
import me.chanjar.weixin.mp.api.WxMpService;
import me.chanjar.weixin.mp.bean.message.WxMpXmlMessage;
import me.chanjar.weixin.mp.bean.message.WxMpXmlOutMessage;
import me.chanjar.weixin.mp.bean.result.WxMpUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.net.URLEncoder;
import java.util.Map;
import java.util.Objects;

@Component
public class ScanHandler extends AbstractHandler {


    @Autowired
    private WxMsgService wxMsgService;
    @Override
    public WxMpXmlOutMessage handle(WxMpXmlMessage wxMpXmlMessage, Map<String, Object> map,
                                    WxMpService wxMpService, WxSessionManager wxSessionManager) throws WxErrorException {
        // 扫码事件处理
        return wxMsgService.scan(wxMpService,wxMpXmlMessage);


    }

}
