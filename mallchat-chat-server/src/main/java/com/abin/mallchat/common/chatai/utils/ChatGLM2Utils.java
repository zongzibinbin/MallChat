package com.abin.mallchat.common.chatai.utils;

import cn.hutool.http.HttpResponse;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONObject;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
public class ChatGLM2Utils {


    private final Map<String, String> headers;
    /**
     * 超时30秒
     */
    private Integer timeout = 60 * 1000;

    private String url;
    /**
     * 提示词
     */
    private String prompt;

    /**
     * 历史
     */
    private List<Object> history;


    public ChatGLM2Utils() {
        HashMap<String, String> _headers_ = new HashMap<>();
        _headers_.put("Content-Type", "application/json");
        this.headers = _headers_;
    }

    public static ChatGLM2Utils create() {
        return new ChatGLM2Utils();
    }


    public ChatGLM2Utils url(String url) {
        this.url = url;
        return this;
    }


    public ChatGLM2Utils timeout(int timeout) {
        this.timeout = timeout;
        return this;
    }

    public ChatGLM2Utils prompt(String prompt) {
        this.prompt = prompt;
        return this;
    }

    public HttpResponse send() {
        JSONObject param = new JSONObject();
        param.set("prompt", prompt);
        log.info("headers >>> " + headers);
        log.info("param >>> " + param);
        return HttpUtil.createPost(url)
                .addHeaders(headers)
                .body(param.toString())
                .timeout(timeout)
                .execute();
    }

    public static String parseText(String body) {
        log.info("body >>> " + body);
        JSONObject jsonObj = new JSONObject(body);
        if (200 != jsonObj.getInt("status")) {
            log.error("status >>> " + jsonObj.getInt("status"));
            return "闹脾气了，等会再试试吧~";
        }
        return jsonObj.getStr("response");
    }

    public static String parseText(HttpResponse response) {
        return parseText(response.body());
    }


    public static void main(String[] args) {
        HttpResponse send = null;
        try {
            send = ChatGLM2Utils
                    .create()
                    .url("http://vastmiao.natapp1.cc")
                    .timeout(60 * 1000)
                    .prompt("Spring的启动流程是什么")
                    .send();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        System.out.println("send = " + send);

        System.out.println("parseText(send) = " + parseText(send));
    }


}