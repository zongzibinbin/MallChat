package com.abin.mallchat.common.user.domain.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

import java.io.Serializable;
import java.util.Objects;
import java.util.Optional;

/**
 * <p>
 * 用户ip信息
 * </p>
 *
 * @author <a href="https://github.com/zongzibinbin">abin</a>
 * @since 2023-03-19
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class IpInfo implements Serializable {

    private static final long serialVersionUID = 1L;
    //注册时的ip
    private String createIp;
    //注册时的ip详情
    private IpDetail createIpDetail;
    //最新登录的ip
    private String updateIp;
    //最新登录的ip详情
    private IpDetail updateIpDetail;

    public void refreshIp(String ip) {
        if (Objects.isNull(ip)) {
            return;
        }
        updateIp = ip;
        if (createIp == null) {
            createIp = ip;
        }
    }

    /**
     * 需要刷新的ip，这里判断更新ip就够，初始化的时候ip也是相同的，只需要设置的时候多设置进去就行
     *
     * @return
     */
    public String needRefreshIp() {
        boolean notNeedRefresh = Optional.ofNullable(updateIpDetail)
                .map(IpDetail::getIp)
                .filter(ip -> ip.equals(updateIp))
                .isPresent();
        return notNeedRefresh ? null : updateIp;
    }

    public void refreshIpDetail(IpDetail ipDetail) {
        if (Objects.equals(createIp, ipDetail)) {
            createIpDetail = ipDetail;
        }
        if (Objects.equals(updateIp, ipDetail)) {
            updateIpDetail = ipDetail;
        }
    }
}