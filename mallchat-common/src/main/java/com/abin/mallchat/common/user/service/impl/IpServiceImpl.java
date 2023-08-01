package com.abin.mallchat.common.user.service.impl;

import cn.hutool.core.lang.TypeReference;
import cn.hutool.core.thread.NamedThreadFactory;
import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONUtil;
import com.abin.mallchat.common.common.handler.GlobalUncaughtExceptionHandler;
import com.abin.mallchat.common.user.dao.UserDao;
import com.abin.mallchat.common.user.domain.dto.IpResult;
import com.abin.mallchat.common.user.domain.entity.IpDetail;
import com.abin.mallchat.common.user.domain.entity.IpInfo;
import com.abin.mallchat.common.user.domain.entity.User;
import com.abin.mallchat.common.user.service.IpService;
import com.abin.mallchat.common.user.service.cache.UserCache;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Description: ip
 * Author: <a href="https://github.com/zongzibinbin">abin</a>
 * Date: 2023-04-18
 */
@Service
@Slf4j
public class IpServiceImpl implements IpService, DisposableBean {
    private static final ExecutorService EXECUTOR = new ThreadPoolExecutor(1, 1,
            0L, TimeUnit.MILLISECONDS,
            new LinkedBlockingQueue<>(500),
            new NamedThreadFactory("refresh-ipDetail", null, false,
                    GlobalUncaughtExceptionHandler.getInstance()));

    @Autowired
    private UserDao userDao;
    @Autowired
    private UserCache userCache;


    @Override
    public void refreshIpDetailAsync(Long uid) {
        EXECUTOR.execute(() -> {
            User user = userDao.getById(uid);
            IpInfo ipInfo = user.getIpInfo();
            if (Objects.isNull(ipInfo)) {
                return;
            }
            String ip = ipInfo.needRefreshIp();
            if (StrUtil.isBlank(ip)) {
                return;
            }
            IpDetail ipDetail = TryGetIpDetailOrNullTreeTimes(ip);
            if (Objects.nonNull(ipDetail)) {
                ipInfo.refreshIpDetail(ipDetail);
                User update = new User();
                update.setId(uid);
                update.setIpInfo(ipInfo);
                userDao.updateById(update);
                userCache.userInfoChange(uid);
            } else {
                log.error("get ip detail fail ip:{},uid:{}", ip, uid);
            }

        });
    }

    private static IpDetail TryGetIpDetailOrNullTreeTimes(String ip) {
        for (int i = 0; i < 3; i++) {
            IpDetail ipDetail = getIpDetailOrNull(ip);
            if (Objects.nonNull(ipDetail)) {
                return ipDetail;
            }
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    public static IpDetail getIpDetailOrNull(String ip) {
        String body = HttpUtil.get("https://ip.taobao.com/outGetIpInfo?ip=" + ip + "&accessKey=alibaba-inc");
        try {
            IpResult<IpDetail> result = JSONUtil.toBean(body, new TypeReference<IpResult<IpDetail>>() {
            }, false);
            if (result.isSuccess()) {
                return result.getData();
            }
        } catch (Exception ignored) {
        }
        return null;
    }

    //测试耗时结果 100次查询总耗时约100s，平均一次成功查询需要1s,可以接受
    //第99次成功,目前耗时：99545ms
    public static void main(String[] args) {
        Date begin = new Date();
        for (int i = 0; i < 100; i++) {
            int finalI = i;
            EXECUTOR.execute(() -> {
                IpDetail ipDetail = TryGetIpDetailOrNullTreeTimes("113.90.36.126");
                if (Objects.nonNull(ipDetail)) {
                    Date date = new Date();
                    System.out.println(String.format("第%d次成功,目前耗时：%dms", finalI, (date.getTime() - begin.getTime())));
                }
            });
        }
    }

    @Override
    public void destroy() throws InterruptedException {
        EXECUTOR.shutdown();
        if (!EXECUTOR.awaitTermination(30, TimeUnit.SECONDS)) {//最多等30秒，处理不完就拉倒
            if (log.isErrorEnabled()) {
                log.error("Timed out while waiting for executor [{}] to terminate", EXECUTOR);
            }
        }
    }

}
