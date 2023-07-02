package com.abin.mallchat.common.common.domain.dto;

import lombok.*;

import java.util.concurrent.TimeUnit;

@Data
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FrequencyControlDTO {
    /**
     * 代表频控的前缀
     */
    private String prefix;
    /**
     * 代表频控的Key 如果target为Key的话 这里要传值用于构建redis的Key target为Ip或者UID的话会从上下文取值 Key字段无需传值
     */
    private String key;
    /**
     * 频控对象，默认el表达指定具体的频控对象
     * 对于ip 和uid模式，需要是http入口的对象，保证RequestHolder里有值
     *
     * @return 对象
     */
    private Target target;
    /**
     * 频控时间范围，默认单位秒
     *
     * @return 时间范围
     */
    private int time;

    /**
     * 频控时间单位，默认秒
     *
     * @return 单位
     */
    private TimeUnit unit;

    /**
     * 单位时间内最大访问次数
     *
     * @return 次数
     */
    private int count;
    /**
     * 代表频控的序号
     */
    private int order;





    public enum Target {
        UID, IP, STRING
    }
}
