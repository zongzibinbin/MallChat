package com.abin.mallchat.common.common.utils;

import cn.hutool.core.util.ObjectUtil;
import com.abin.mallchat.common.common.exception.BusinessErrorEnum;
import com.abin.mallchat.common.common.exception.BusinessException;
import com.abin.mallchat.common.common.exception.ErrorEnum;

import java.text.MessageFormat;
import java.util.Objects;

/**
 * <p>
 * 校验工具类
 * </p>
 *
 * @author <a href="https://github.com/zongzibinbin">abin</a>
 * @since 2023-05-27
 */
public class AssertUtil {

    /**
     * 如果不是true，则抛异常
     *
     * @param expression 表达式
     * @param msg        错误消息
     */
    public static void isTrue(boolean expression, String msg) {
        if (!expression) {
            throwException(msg);
        }
    }

    public static void isTrue(boolean expression, ErrorEnum errorEnum, Object... args) {
        if (!expression) {
            throwException(errorEnum, args);
        }
    }

    /**
     * 如果是true，则抛异常
     *
     * @param expression 表达式
     * @param msg        错误消息
     */
    public static void isFalse(boolean expression, String msg) {
        if (expression) {
            throwException(msg);
        }
    }

    /**
     * 如果是true，则抛异常
     *
     * @param expression 表达式
     * @param errorEnum  错误枚举
     * @param args       参数
     */
    public static void isFalse(boolean expression, ErrorEnum errorEnum, Object... args) {
        if (expression) {
            throwException(errorEnum, args);
        }
    }

    /**
     * 如果不是非空对象，则抛异常
     *
     * @param obj 对象
     * @param msg 错误消息
     */
    public static void isNotEmpty(Object obj, String msg) {
        if (isEmpty(obj)) {
            throwException(msg);
        }
    }

    /**
     * 如果不是非空对象，则抛异常
     *
     * @param obj       对象
     * @param errorEnum 错误枚举
     * @param args      参数
     */
    public static void isNotEmpty(Object obj, ErrorEnum errorEnum, Object... args) {
        if (isEmpty(obj)) {
            throwException(errorEnum, args);
        }
    }

    /**
     * 如果不是非空对象，则抛异常
     *
     * @param obj 对象
     * @param msg 错误消息
     */
    public static void isEmpty(Object obj, String msg) {
        if (!isEmpty(obj)) {
            throwException(msg);
        }
    }

    public static void equal(Object o1, Object o2, String msg) {
        if (!ObjectUtil.equal(o1, o2)) {
            throwException(msg);
        }
    }

    private static boolean isEmpty(Object obj) {
        return ObjectUtil.isEmpty(obj);
    }

    private static void throwException(String msg) {
        throwException(null, msg);
    }

    private static void throwException(ErrorEnum errorEnum, Object... arg) {
        if (Objects.isNull(errorEnum)) {
            errorEnum = BusinessErrorEnum.BUSINESS_ERROR;
        }
        throw new BusinessException(errorEnum.getErrorCode(), MessageFormat.format(errorEnum.getErrorMsg(), arg));
    }

}
