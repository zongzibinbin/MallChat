package com.abin.mallchat.custom.chatai.utils;

import cn.hutool.http.HttpResponse;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import com.abin.mallchat.common.common.exception.BusinessException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;
import java.util.Map;

@Slf4j
public class ChatGPTUtils {

    private static final String URL = "https://api.openai.com/v1/completions";

    private String model = "text-davinci-003";

    private final Map<String, String> headers;
    /**
     * 超时30秒
     */
    private Integer timeout = 30 * 1000;
    /**
     * 参数用于指定生成文本的最大长度。
     * 它表示生成的文本中最多包含多少个 token。一个 token 可以是一个单词、一个标点符号或一个空格。
     */
    private int maxTokens = 2048;
    /**
     * 用于控制生成文本的多样性。
     * 较高的温度会导致更多的随机性和多样性，但可能会降低生成文本的质量。默认值为 1，建议在 0.7 到 1.3 之间调整。
     */
    private Object temperature = 1;
    /**
     * 用于控制生成文本的多样性。
     * 它会根据概率选择最高的几个单词，而不是选择概率最高的单词。默认值为 1，建议在 0.7 到 0.9 之间调整。
     */
    private Object topP = 0.9;
    /**
     * 用于控制生成文本中重复单词的数量。
     * 较高的惩罚值会导致更少的重复单词，但可能会降低生成文本的流畅性。默认值为 0，建议在 0 到 2 之间调整。
     */
    private Object frequencyPenalty = 0.0;
    /**
     * 用于控制生成文本中出现特定单词的数量。
     * 较高的惩罚值会导致更少的特定单词，但可能会降低生成文本的流畅性。默认值为 0，建议在 0 到 2 之间调整。
     */
    private Object presencePenalty = 0.6;

    /**
     * 提示词
     */
    private String prompt;

    private String proxyUrl;

    public ChatGPTUtils(String key) {
        HashMap<String, String> _headers_ = new HashMap<>();
        _headers_.put("Content-Type", "application/json");
        if (StringUtils.isBlank(key)) {
            throw new BusinessException("openAi key is blank");
        }
        _headers_.put("Authorization", "Bearer " + key);
        this.headers = _headers_;
    }

    public static ChatGPTUtils create(String key) {
        return new ChatGPTUtils(key);
    }

    public static String parseText(HttpResponse response) {
        return parseText(response.body());
    }

    public static String parseText(String body) {
        log.info("body >>> " + body);
        JSONObject jsonObj = new JSONObject(body);
        JSONObject error = jsonObj.getJSONObject("error");
        if (error != null) {
            log.error("error >>> " + error);
           return "闹脾气了，等会再试试吧~";
        }
        JSONArray choicesArr = jsonObj.getJSONArray("choices");
        JSONObject choiceObj = choicesArr.getJSONObject(0);
        return choiceObj.getStr("text");
    }

    public ChatGPTUtils model(String model) {
        this.model = model;
        return this;
    }

    public ChatGPTUtils timeout(int timeout) {
        this.timeout = timeout;
        return this;
    }

    public ChatGPTUtils maxTokens(int maxTokens) {
        this.maxTokens = maxTokens;
        return this;
    }

    public ChatGPTUtils temperature(int temperature) {
        this.temperature = temperature;
        return this;
    }

    public ChatGPTUtils topP(int topP) {
        this.topP = topP;
        return this;
    }

    public ChatGPTUtils frequencyPenalty(int frequencyPenalty) {
        this.frequencyPenalty = frequencyPenalty;
        return this;
    }

    public ChatGPTUtils presencePenalty(int presencePenalty) {
        this.presencePenalty = presencePenalty;
        return this;
    }

    public ChatGPTUtils prompt(String prompt) {
        this.prompt = prompt;
        return this;
    }

    public ChatGPTUtils proxyUrl(String proxyUrl) {
        this.proxyUrl = proxyUrl;
        return this;
    }

    public HttpResponse send() {
        JSONObject param = new JSONObject();
        param.set("model", model);
        param.set("prompt", prompt);
        param.set("max_tokens", maxTokens);
        param.set("temperature", temperature);
        param.set("top_p", topP);
        param.set("frequency_penalty", frequencyPenalty);
        param.set("presence_penalty", presencePenalty);
        log.info("headers >>> " + headers);
        log.info("param >>> " + param);
        return HttpUtil.createPost(StringUtils.isNotBlank(proxyUrl) ? proxyUrl : URL)
                .addHeaders(headers)
                .body(param.toString())
                .timeout(timeout)
                .execute();
    }

    public static void main(String[] args) {
        HttpResponse send = ChatGPTUtils.create("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")
                .timeout(30 * 1000)
                .prompt("Spring的启动流程是什么")
                .send();
        System.out.println("send = " + send);
        // JSON 数据
        // JSON 数据
        JSONObject jsonObj = new JSONObject(send.body());
        JSONArray choicesArr = jsonObj.getJSONArray("choices");
        JSONObject choiceObj = choicesArr.getJSONObject(0);
        String text = choiceObj.getStr("text");
        System.out.println("text = " + text);

    }
}