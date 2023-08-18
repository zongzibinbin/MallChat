package com.abin.mallchat.custom.user.service;

import cn.hutool.core.util.RandomUtil;
import com.abin.mallchat.common.common.constant.MQConstant;
import com.abin.mallchat.common.common.constant.RedisKey;
import com.abin.mallchat.common.common.domain.dto.LoginMessageDTO;
import com.abin.mallchat.common.common.utils.CacheHolder;
import com.abin.mallchat.common.common.utils.RedisUtils;
import com.abin.mallchat.common.user.dao.UserDao;
import com.abin.mallchat.common.user.domain.entity.User;
import com.abin.mallchat.custom.user.service.adapter.TextBuilder;
import com.abin.mallchat.custom.user.service.adapter.UserAdapter;
import com.abin.mallchat.transaction.service.MQProducer;
import io.netty.channel.Channel;
import lombok.extern.slf4j.Slf4j;
import me.chanjar.weixin.common.bean.WxOAuth2UserInfo;
import me.chanjar.weixin.mp.api.WxMpService;
import me.chanjar.weixin.mp.bean.message.WxMpXmlMessage;
import me.chanjar.weixin.mp.bean.message.WxMpXmlOutMessage;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;

import java.net.URLEncoder;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

/**
 * Description: 处理与微信api的交互逻辑
 * Author: <a href="https://github.com/zongzibinbin">abin</a>
 * Date: 2023-03-19
 */
@Service
@Slf4j
public class WxMsgService {
    /**
     * 用户的openId和前端登录场景code的映射关系
     */
    private static final ConcurrentHashMap<String, Integer> OPENID_EVENT_CODE_MAP = new ConcurrentHashMap<>();
    private static final String URL = "https://open.weixin.qq.com/connect/oauth2/authorize?appid=%s&redirect_uri=%s&response_type=code&scope=snsapi_userinfo&state=STATE#wechat_redirect";
    @Value("${wx.mp.callback}")
    private String callback;
    @Autowired
    private UserDao userDao;
    @Autowired
    @Lazy
    private WebSocketService webSocketService;
    @Autowired
    private LoginService loginService;
    @Autowired
    private UserService userService;
    @Autowired
    private ThreadPoolTaskExecutor threadPoolTaskExecutor;
    @Autowired
    private MQProducer mqProducer;

    public WxMpXmlOutMessage scan(WxMpService wxMpService, WxMpXmlMessage wxMpXmlMessage) {
        String openid = wxMpXmlMessage.getFromUser();
        Integer loginCode = Integer.parseInt(getEventKey(wxMpXmlMessage));
        User user = userDao.getByOpenId(openid);
        //如果已经注册,直接登录成功
        if (Objects.nonNull(user) && StringUtils.isNotEmpty(user.getAvatar())) {
            Channel channel = CacheHolder.WAIT_LOGIN_MAP.getIfPresent(loginCode);
            //要么在本地登录,否则利用mq广播到到所有服务上尝试登录
            if (Objects.nonNull(channel)) {
                String token = loginService.login(user.getId());
                webSocketService.scanLoginSuccess(loginCode, user, token);
            }else {
                mqProducer.sendMsg(MQConstant.LOGIN_MSG_TOPIC, new LoginMessageDTO(wxMpXmlMessage));
            }
            return null;
        }

        //user为空先注册,手动生成,以保存uid
        if (Objects.isNull(user)) {
            user = User.builder().openId(openid).build();
            userService.register(user);
        }
        Long uid = user.getId();

        //在 redis中保存openid和场景code的关系，后续才能通知到前端,旧版数据没有清除,这里设置了过期时间
        RedisUtils.set(RedisKey.getKey(RedisKey.OPEN_ID_STRING, openid), loginCode, 60, TimeUnit.MINUTES);
        //授权流程,给用户发送授权消息，并且异步通知前端扫码成功(如非本地channel,使用MQ通知某服务对前端进行通知扫码成功)
        threadPoolTaskExecutor.execute(() -> webSocketService.scanSuccess(loginCode, uid));
        String skipUrl = String.format(URL, wxMpService.getWxMpConfigStorage().getAppId(), URLEncoder.encode(callback + "/wx/portal/public/callBack"));
        WxMpXmlOutMessage.TEXT().build();
        return new TextBuilder().build("请点击链接授权：<a href=\"" + skipUrl + "\">登录</a>", wxMpXmlMessage, wxMpService);
    }

    private String getEventKey(WxMpXmlMessage wxMpXmlMessage) {
        //扫码关注的渠道事件有前缀，需要去除
        return wxMpXmlMessage.getEventKey().replace("qrscene_", "");
    }

    /**
     * 用户授权
     *
     * @param userInfo
     */
    public void authorize(WxOAuth2UserInfo userInfo) {
        User user = userDao.getByOpenId(userInfo.getOpenid());
        //更新用户信息
        if (StringUtils.isEmpty(user.getName())) {
            fillUserInfo(user.getId(), userInfo);
        }
        //找到对应的
        Integer eventKey = RedisUtils.get(RedisKey.getKey(RedisKey.OPEN_ID_STRING, userInfo.getOpenid()), Integer.class);
        //如果channel就在本地，直接登录
        Channel channel = CacheHolder.WAIT_LOGIN_MAP.getIfPresent(eventKey);
        if (Objects.nonNull(channel)) {
            login(user.getId(), eventKey);
        }else {
            //如果channel不在本地，利用mq广播到到所有服务上,尝试进行登录
            //手动生成一个WxMpXmlMessage
            WxMpXmlMessage wxMpXmlMessage = new WxMpXmlMessage();
            wxMpXmlMessage.setFromUser(userInfo.getOpenid());
            wxMpXmlMessage.setEventKey("qrscene_"+eventKey);
            mqProducer.sendMsg(MQConstant.LOGIN_MSG_TOPIC, new LoginMessageDTO(wxMpXmlMessage));
        }
    }

    private void fillUserInfo(Long uid, WxOAuth2UserInfo userInfo) {
        User update = UserAdapter.buildAuthorizeUser(uid, userInfo);
        for (int i = 0; i < 5; i++) {
            try {
                userDao.updateById(update);
                return;
            } catch (DuplicateKeyException e) {
                log.info("fill userInfo duplicate uid:{},info:{}", uid, userInfo);
            } catch (Exception e) {
                log.error("fill userInfo fail uid:{},info:{}", uid, userInfo);
            }
            update.setName("名字重置" + RandomUtil.randomInt(100000));
        }
    }

    public void login(Long uid, Integer eventKey) {
        User user = userDao.getById(uid);
        //调用用户登录模块
        String token = loginService.login(uid);
        //推送前端登录成功
        webSocketService.scanLoginSuccess(eventKey, user, token);
    }
}
