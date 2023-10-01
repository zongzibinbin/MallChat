package com.abin.mallchat.common;

import com.abin.mallchat.common.common.domain.enums.IdempotentEnum;
import com.abin.mallchat.common.common.utils.JwtUtils;
import com.abin.mallchat.common.user.domain.enums.ItemEnum;
import com.abin.mallchat.common.user.service.IUserBackpackService;
import com.abin.mallchat.common.user.service.LoginService;
import lombok.extern.slf4j.Slf4j;
import me.chanjar.weixin.common.error.WxErrorException;
import me.chanjar.weixin.mp.api.WxMpService;
import me.chanjar.weixin.mp.bean.result.WxMpQrCodeTicket;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * Description:
 * Author: <a href="https://github.com/zongzibinbin">abin</a>
 * Date: 2023-08-27
 */

@SpringBootTest
@RunWith(SpringRunner.class)
@Slf4j
public class DaoTest {
    public static final long UID = 12717L;
    @Autowired
    private WxMpService wxMpService;
    @Autowired
    private JwtUtils jwtUtils;


    @Test
    public void jwt() {
        String login = loginService.login(UID);
        System.out.println(login);
    }

    @Autowired
    private RedissonClient redissonClient;
    @Autowired
    private LoginService loginService;

    @Test
    public void redis() {
        String s = "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJ1aWQiOjExMDAyLCJjcmVhdGVUaW1lIjoxNjkzNjYzOTU1fQ.qISTe8UDzggilWqz0HKtGLrkgiG1IRGafS10qHih9iM";
        Long validUid = loginService.getValidUid(s);
        System.out.println(validUid);
    }

    @Autowired
    private IUserBackpackService iUserBackpackService;

    @Test
    public void acquireItem() {
        iUserBackpackService.acquireItem(UID, ItemEnum.REG_TOP100_BADGE.getId(), IdempotentEnum.UID, UID + "");
    }

    @Autowired
    private ThreadPoolTaskExecutor threadPoolTaskExecutor;

    @Test
    public void thread() throws InterruptedException {
        threadPoolTaskExecutor.execute(() -> {
            if (1 == 1) {
                log.error("123");
                throw new RuntimeException("1243");
            }
        });
        Thread.sleep(200);
    }

    @Test
    public void test() throws WxErrorException {
        WxMpQrCodeTicket wxMpQrCodeTicket = wxMpService.getQrcodeService().qrCodeCreateTmpTicket(1, 10000);
        String url = wxMpQrCodeTicket.getUrl();
        System.out.println(url);
    }
}
