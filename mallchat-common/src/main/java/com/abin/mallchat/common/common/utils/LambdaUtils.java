package com.abin.mallchat.common.common.utils;

import cn.hutool.core.map.WeakConcurrentMap;
import cn.hutool.core.util.ReflectUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.toolkit.support.ColumnCache;
import com.baomidou.mybatisplus.core.toolkit.support.SFunction;
import lombok.SneakyThrows;
import org.apache.ibatis.reflection.property.PropertyNamer;

import java.io.Serializable;
import java.lang.invoke.SerializedLambda;
import java.lang.ref.WeakReference;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * Description:
 * Author: <a href="https://github.com/zongzibinbin">abin</a>
 * Date: 2023-08-02
 */
public class LambdaUtils {
    /**
     * 字段映射
     */
    private static final Map<String, Map<String, ColumnCache>> COLUMN_CACHE_MAP = new ConcurrentHashMap<>();

    /**
     * SerializedLambda 反序列化缓存
     */
    private static final Map<String, WeakReference<com.baomidou.mybatisplus.core.toolkit.support.SerializedLambda>> FUNC_CACHE = new ConcurrentHashMap<>();

    private static Pattern RETURN_TYPE_PATTERN = Pattern.compile("\\(.*\\)L(.*);");
    private static Pattern PARAMETER_TYPE_PATTERN = Pattern.compile("\\((.*)\\).*");
    private static final WeakConcurrentMap<String, SerializedLambda> cache = new WeakConcurrentMap<>();

    /**
     * 获取Lambda表达式返回类型
     */
    public static Class<?> getReturnType(Serializable serializable) {
        String expr = _resolve(serializable).getInstantiatedMethodType();
        Matcher matcher = RETURN_TYPE_PATTERN.matcher(expr);
        if (!matcher.find() || matcher.groupCount() != 1) {
            throw new RuntimeException("获取Lambda信息失败");
        }
        String className = matcher.group(1).replace("/", ".");
        try {
            return Class.forName(className);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("无法加载类", e);
        }
    }

    @SneakyThrows
    public static <T> Class<?> getReturnType(SFunction<T, ?> func) {
        com.baomidou.mybatisplus.core.toolkit.support.SerializedLambda lambda = com.baomidou.mybatisplus.core.toolkit.LambdaUtils.resolve(func);
        Class<?> aClass = lambda.getInstantiatedType();
        String fieldName = PropertyNamer.methodToProperty(lambda.getImplMethodName());
        Field field = aClass.getDeclaredField(fieldName);
        field.setAccessible(true);
        return field.getType();
    }

    /**
     * 获取Lambda表达式的参数类型
     */
    public static List<Class<?>> getParameterTypes(Serializable serializable) {
        String expr = _resolve(serializable).getInstantiatedMethodType();
        Matcher matcher = PARAMETER_TYPE_PATTERN.matcher(expr);
        if (!matcher.find() || matcher.groupCount() != 1) {
            throw new RuntimeException("获取Lambda信息失败");
        }
        expr = matcher.group(1);

        return Arrays.stream(expr.split(";"))
                .filter(StrUtil::isNotBlank)
                .map(s -> s.replace("L", "").replace("/", "."))
                .map(s -> {
                    try {
                        return Class.forName(s);
                    } catch (ClassNotFoundException e) {
                        throw new RuntimeException("无法加载类", e);
                    }
                })
                .collect(Collectors.toList());
    }

    /**
     * 解析lambda表达式,加了缓存。
     * 该缓存可能会在任意不定的时间被清除。
     *
     * <p>
     * 通过反射调用实现序列化接口函数对象的writeReplace方法，从而拿到{@link SerializedLambda}<br>
     * 该对象中包含了lambda表达式的所有信息。
     * </p>
     *
     * @param func 需要解析的 lambda 对象
     * @return 返回解析后的结果
     */
    private static SerializedLambda _resolve(Serializable func) {
        return cache.computeIfAbsent(func.getClass().getName(), (key)
                -> ReflectUtil.invoke(func, "writeReplace"));
    }

}
