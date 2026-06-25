package com.abin.mallchat.common.common.domain.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * 接口埋点日志
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName(value = "web_log", autoResultMap = true)
public class WebLog {

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 链路追踪id，对应MDC中的tid
     */
    @TableField("trace_id")
    private String traceId;

    /**
     * 用户id，未登录为空
     */
    @TableField("uid")
    private Long uid;

    /**
     * 客户端ip
     */
    @TableField("ip")
    private String ip;

    /**
     * 请求方法
     */
    @TableField("http_method")
    private String httpMethod;

    /**
     * 请求路径
     */
    @TableField("uri")
    private String uri;

    /**
     * Controller类名#方法名
     */
    @TableField("class_method")
    private String classMethod;

    /**
     * 请求参数快照
     */
    @TableField(value = "request_param", typeHandler = JacksonTypeHandler.class)
    private Object requestParam;

    /**
     * 响应结果快照
     */
    @TableField(value = "response_result", typeHandler = JacksonTypeHandler.class)
    private Object responseResult;

    /**
     * 是否成功
     */
    @TableField("success")
    private Boolean success;

    /**
     * 错误码
     */
    @TableField("err_code")
    private Integer errCode;

    /**
     * 错误信息
     */
    @TableField("err_msg")
    private String errMsg;

    /**
     * 异常类名
     */
    @TableField("exception_name")
    private String exceptionName;

    /**
     * 异常信息
     */
    @TableField("exception_msg")
    private String exceptionMsg;

    /**
     * 接口耗时，单位毫秒
     */
    @TableField("cost_ms")
    private Long costMs;

    /**
     * User-Agent
     */
    @TableField("user_agent")
    private String userAgent;

    /**
     * Referer
     */
    @TableField("referer")
    private String referer;

    @TableField("create_time")
    private Date createTime;

    @TableField("update_time")
    private Date updateTime;
}
