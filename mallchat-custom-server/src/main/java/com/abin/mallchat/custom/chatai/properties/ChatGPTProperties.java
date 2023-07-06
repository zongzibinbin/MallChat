package com.abin.mallchat.custom.chatai.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;


@Data
@Component
@ConfigurationProperties(prefix = "chatai.chatgpt")
public class ChatGPTProperties {

    /**
     * 是否使用openAI
     */
    private boolean use;
    /**
     * 机器人 id
     */
    private Long AIUserId;
    /**
     * 模型名称
     */
    private String modelName = "text-davinci-003";
    /**
     * openAI key
     */
    private String key;
    /**
     * 代理地址
     */
    private String proxyUrl;

    /**
     * 超时
     */
    private Integer timeout = 60*1000;

    /**
     * 用户每天条数限制
     */
    private Integer limit = 5;

}
