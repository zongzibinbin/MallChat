package com.abin.mallchat.common.common.config;

import com.github.yitter.contract.IdGeneratorOptions;
import com.github.yitter.idgen.YitIdHelper;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;

/**
 * Description: UUID生成器的配置类
 * Author: <a href="https://www.ahao.homes">ahao: https://github.com/HowlsLee</a>
 * <p>
 * <p>
 * <p>
 * 采用开源的yitter-idgenerator生成唯一Id
 * <p>
 * 1. 可以代替自带的UUID: UUID.randomUUID()
 * 2. 优化了传统的雪花算法：
 * 整形数字，随时间单调递增（不一定连续），长度更短，用50年都不会超过 js Number类型最大值。（默认配置）
 * 速度更快，是传统雪花算法的2-5倍，0.1秒可生成50万个（基于8代低压i7）。
 * ...
 * 参考：https://github.com/yitter/IdGenerator
 */
@Configuration
public class UUIDConfig {

    /**
     * workerId表示该服务实例的唯一标识(可以为0, 1, 2, 3 , ..., n),
     * 关联配置项中的"WorkerIdBitLength"字段
     * <p>
     * 单机部署下workerId可以不设置或者设置成全局唯一的值即可(目前为0)
     * 多节点部署下，workerId可以从分布式中心比如Redis分配得到：
     * 内部维护2个key:
     * 1. 已注册的实例集合
     * 2. 当前已分配的workerId的最大值(初始值可以为0)
     * 获取workerId步骤：
     * 1. 采用(Mac地址 + 进程号)作为key到Redis中查询该实例是否注册
     * 如果没注册则将当前最大workerId分配，并且Redis中最大workerId + 1
     * 否则获取之前分配的workerId
     **/
    private static final short workerId = (short) (0);

    @PostConstruct
    public void init() {
        // 创建 IdGeneratorOptions 对象，可在构造函数中输入 WorkerId：
        IdGeneratorOptions options = new IdGeneratorOptions(workerId);
        // options.WorkerIdBitLength = 10; // 默认值6，限定 WorkerId 最大值为2^6-1，即默认最多支持64个节点。
        // options.SeqBitLength = 6; // 默认值6，限制每毫秒生成的ID个数。若生成速度超过5万个/秒，建议加大 SeqBitLength 到 10。
        // options.BaseTime = Your_Base_Time; // 如果要兼容老系统的雪花算法，此处应设置为老系统的BaseTime。
        // ...... 其它参数参考 IdGeneratorOptions 定义。
        // 保存参数（务必调用，否则参数设置不生效）：
        YitIdHelper.setIdGenerator(options);
        // 以上过程只需全局一次，且应在生成ID之前完成。
    }
}
