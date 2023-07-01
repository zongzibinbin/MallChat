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
     * 机器人名称
     */
    private String AIUserName;
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
     * 用户每天条数限制
     */
    private Integer limit = 5;

}
