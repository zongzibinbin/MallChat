package com.abin.mallchat.common.common.utils;

import java.math.BigDecimal;
import java.util.*;
import java.util.function.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * 集合Stream常用操作工具类
 * 包含：转新集合，过滤，分组，排序，求和，平均，最大值，最小值，分隔，合并，去重
 *
 * @author Zee
 * @date 2023年5月20日
 */
public class CollStreamUtil {
    /**
     * 转换成一个新的集合
     *
     * @param collection 集合
     * @param function   表达式
     * @param <T>        泛型
     * @param <R>        泛型
     * @return 新的集合
     */
    public static <T, R> List<R> toList(Collection<T> collection, Function<? super T, ? extends R> function) {
        return nullDefaultEmpty(collection).stream().map(function).collect(Collectors.toList());
    }

    /**
     * 转换成一个新的集合
     *
     * @param collection 集合
     * @param function   表达式
     * @param <T>        泛型
     * @param <R>        泛型
     * @return 新的集合
     */
    public static <T, R> Set<R> toSet(Collection<T> collection, Function<? super T, ? extends R> function) {
        return nullDefaultEmpty(collection).stream().map(function).collect(Collectors.toSet());
    }

    /**
     * 集合排序正序
     *
     * @param list         要排序的集合
     * @param keyExtractor 排序字段
     * @param <T>          泛型
     * @param <U>          泛型
     */
    public static <T, U extends Comparable<? super U>> void sortAsc(List<T> list, Function<? super T, ? extends U> keyExtractor) {
        ((List<T>) nullDefaultEmpty(list)).sort(Comparator.comparing(keyExtractor));
    }

    /**
     * 集合排序倒序
     *
     * @param list         要排序的集合
     * @param keyExtractor 排序字段
     * @param <T>          泛型
     * @param <U>          泛型
     */
    public static <T, U extends Comparable<? super U>> void sortDesc(List<T> list, Function<? super T, ? extends U> keyExtractor) {
        ((List<T>) nullDefaultEmpty(list)).sort(Comparator.comparing(keyExtractor).reversed());
    }

    /**
     * 集合转换成map
     *
     * @param collection  集合
     * @param keyMapper   转换mapKey
     * @param valueMapper 转换mapValue
     * @param <T>         源
     * @param <K>         key
     * @param <U>         value
     * @return map
     */
    public static <T, K, U> Map<K, U> toMap(Collection<T> collection, Function<? super T, ? extends K> keyMapper, Function<? super T, ? extends U> valueMapper) {
        return nullDefaultEmpty(collection).stream().collect(Collectors.toMap(keyMapper, valueMapper, (k1, k2) -> k1));
    }


    /**
     * 集合分组
     *
     * @param collection 集合
     * @param classifier key
     * @param <T>        泛型
     * @return Map
     */
    public static <T, K> Map<K, List<T>> groupBy(Collection<T> collection, Function<? super T, K> classifier) {
        return nullDefaultEmpty(collection).stream().collect(Collectors.groupingBy(classifier));
    }

    /**
     * 集合分组统计Map Size
     *
     * @param collection 集合
     * @param classifier key
     * @param <T>        泛型
     * @return Map Size
     */
    public static <T, K> Integer groupBySize(Collection<T> collection, Function<? super T, K> classifier) {
        return nullDefaultEmpty(collection).stream().collect(Collectors.groupingBy(classifier)).size();
    }

    /**
     * 集合分组统计条数
     *
     * @param collection 集合
     * @param classifier key
     * @param <T>        泛型
     * @return map
     */
    public static <T, K> Map<K, Long> groupByCounting(Collection<T> collection, Function<? super T, K> classifier) {
        return nullDefaultEmpty(collection).stream().collect(Collectors.groupingBy(classifier, Collectors.counting()));
    }

    /**
     * 集合分组求和Long
     *
     * @param collection 集合
     * @param classifier key
     * @param <T>        泛型
     * @return Map
     */
    public static <T, K> Map<K, Long> groupBySummingLong(Collection<T> collection, Function<? super T, K> classifier, ToLongFunction<? super T> mapper) {
        return nullDefaultEmpty(collection).stream().collect(Collectors.groupingBy(classifier, Collectors.summingLong(mapper)));
    }

    /**
     * 集合分组求和Int
     *
     * @param collection 集合
     * @param classifier key
     * @param <T>        泛型
     * @return Map
     */
    public static <T, K> Map<K, Integer> groupBySummingInt(Collection<T> collection, Function<? super T, K> classifier, ToIntFunction<? super T> mapper) {
        return nullDefaultEmpty(collection).stream().collect(Collectors.groupingBy(classifier, Collectors.summingInt(mapper)));
    }

    /**
     * 集合分组求和double
     *
     * @param collection 集合
     * @param classifier key
     * @param <T>        泛型
     * @return Map
     */
    public static <T, K> Map<K, Double> groupBySummingDouble(Collection<T> collection, Function<? super T, K> classifier, ToDoubleFunction<? super T> mapper) {
        return nullDefaultEmpty(collection).stream().collect(Collectors.groupingBy(classifier, Collectors.summingDouble(mapper)));
    }


    /**
     * 集合分组后转成新list
     *
     * @param collection 集合
     * @param classifier key
     * @param <T>        泛型
     * @return Map
     */
    public static <T, K, R> List<R> groupByThenToList(Collection<T> collection, Function<? super T, K> classifier, Function<? super Map.Entry<K, List<T>>, R> mapper) {
        return nullDefaultEmpty(collection).stream().collect(Collectors.groupingBy(classifier))
                .entrySet()
                .stream()
                .map(mapper)
                .collect(Collectors.toList());
    }

    /**
     * 集合分组后转成新Set
     *
     * @param collection 集合
     * @param classifier key
     * @param <T>        泛型
     * @return Map
     */
    public static <T, K, R> Set<R> groupByThenToSet(Collection<T> collection, Function<? super T, K> classifier, Function<? super Map.Entry<K, List<T>>, R> mapper) {
        return nullDefaultEmpty(collection).stream().collect(Collectors.groupingBy(classifier))
                .entrySet()
                .stream()
                .map(mapper)
                .collect(Collectors.toSet());
    }

    /**
     * 集合过滤返回List
     *
     * @param collection 源集合
     * @param predicate  表达试
     * @param <T>        泛型
     * @return 过滤后的List
     */
    public static <T> List<T> filterList(Collection<T> collection, Predicate<? super T> predicate) {
        return nullDefaultEmpty(collection).stream().filter(predicate).collect(Collectors.toList());
    }

    /**
     * 集合过滤返回Set
     *
     * @param collection 源集合
     * @param predicate  表达试
     * @param <T>        泛型
     * @return 过滤后的Set
     */
    public static <T> Set<T> filterSet(Collection<T> collection, Predicate<? super T> predicate) {
        return nullDefaultEmpty(collection).stream().filter(predicate).collect(Collectors.toSet());
    }

    /**
     * 先过滤再Map转新List
     *
     * @param collection 源集合
     * @param predicate  filter表达式
     * @param function   map表达式
     * @param <T>        泛型
     * @param <R>        泛型
     * @return 新的list
     */
    public static <T, R> List<R> filterThenToList(Collection<T> collection, Predicate<? super T> predicate, Function<? super T, ? extends R> function) {
        return nullDefaultEmpty(collection).stream().filter(predicate).map(function).collect(Collectors.toList());
    }

    /**
     * 先过滤再Map转新Set
     *
     * @param collection 源集合
     * @param predicate  filter表达式
     * @param function   map表达式
     * @param <T>        泛型
     * @param <R>        泛型
     * @return 新的Set
     */
    public static <T, R> Set<R> filterThenToSet(Collection<T> collection, Predicate<? super T> predicate, Function<? super T, ? extends R> function) {
        return nullDefaultEmpty(collection).stream().filter(predicate).map(function).collect(Collectors.toSet());
    }


    /**
     * int求和
     *
     * @param collection 源集合
     * @param mapper     表达试
     * @param <T>        泛型
     * @return 求和
     */
    public static <T> Integer sumInt(Collection<T> collection, ToIntFunction<? super T> mapper) {
        return nullDefaultEmpty(collection).stream().mapToInt(mapper).sum();
    }

    /**
     * long求和
     *
     * @param collection 源集合
     * @param mapper     表达试
     * @param <T>        泛型
     * @return 求和
     */
    public static <T> Long sumLong(Collection<T> collection, ToLongFunction<? super T> mapper) {
        return nullDefaultEmpty(collection).stream().mapToLong(mapper).sum();
    }

    /**
     * double求和
     *
     * @param collection 源集合
     * @param mapper     表达试
     * @param <T>        泛型
     * @return 求和
     */
    public static <T> Double sumDouble(Collection<T> collection, ToDoubleFunction<? super T> mapper) {
        return nullDefaultEmpty(collection).stream().mapToDouble(mapper).sum();
    }

    /**
     * Decimal求和
     *
     * @param collection 源集合
     * @param function   表达式
     * @param <T>        泛型
     * @return Decimal求和
     */
    public static <T> BigDecimal sumDecimal(Collection<T> collection, Function<? super T, BigDecimal> function) {
        return nullDefaultEmpty(collection).stream().map(function).reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    /**
     * 平均值double
     *
     * @param collection 源集合
     * @param mapper     表达试
     * @param <T>        泛型
     * @return 平均值
     */
    public static <T> Double avgInt(Collection<T> collection, ToDoubleFunction<? super T> mapper) {
        return nullDefaultEmpty(collection).stream().collect(Collectors.averagingDouble(mapper));
    }

    /**
     * 平均值int
     *
     * @param collection 源集合
     * @param mapper     表达式
     * @param <T>        泛型
     * @return 平均值
     */
    public static <T> Double avgDouble(Collection<T> collection, ToIntFunction<? super T> mapper) {
        return nullDefaultEmpty(collection).stream().collect(Collectors.averagingInt(mapper));
    }

    /**
     * 平均值BigDecimal
     *
     * @param collection 源集合
     * @param function   表达式
     * @param <T>        泛型
     * @return 平均值BigDecimal
     */
    public static <T> BigDecimal avgDecimal(Collection<T> collection, Function<T, BigDecimal> function) {
        return nullDefaultEmpty(collection).stream().map(function).reduce(BigDecimal::subtract).orElse(BigDecimal.ZERO);
    }

    /**
     * 乘积BigDecimal
     *
     * @param collection 源集合
     * @param function   表达式
     * @param <T>        泛型
     * @return 乘积BigDecimal
     */
    public static <T> BigDecimal multiplyDecimal(Collection<T> collection, Function<T, BigDecimal> function) {
        return nullDefaultEmpty(collection).stream().map(function).reduce(BigDecimal::multiply).orElse(BigDecimal.ZERO);
    }

    /**
     * 将集合中的某个字段用delimiter分隔 包含前缀后缀
     *
     * @param delimiter  分隔符
     * @param prefix     前缀
     * @param suffix     后缀
     * @param collection 源集合
     * @param function   表达式
     * @param <T>        泛型
     * @return 分隔后的字符串
     */
    public static <T> String joiner(Collection<T> collection, Function<T, String> function, CharSequence delimiter, CharSequence prefix, CharSequence suffix) {
        return nullDefaultEmpty(collection).stream().map(function).collect(Collectors.joining(delimiter, prefix, suffix));
    }

    /**
     * 将集合中的某个字段用delimiter分隔
     *
     * @param delimiter  分隔符
     * @param collection 源集合
     * @param function   表达式
     * @param <T>        泛型
     * @return 分隔后的字符串
     */
    public static <T> String joiner(Collection<T> collection, Function<T, String> function, CharSequence delimiter) {
        return joiner(collection, function, delimiter, "", "");
    }

    /**
     * 集合中的int类型最大值
     *
     * @param collection 源集合
     * @param function   表达式
     * @param <T>        泛型
     * @return maxInt
     */
    public static <T> Integer maxInt(Collection<T> collection, Function<T, Integer> function) {
        Optional<Integer> optional = nullDefaultEmpty(collection).stream().map(function).max(Integer::compare);
        return optional.orElse(0);
    }

    /**
     * 集合中的double类型最大值
     *
     * @param collection 源集合
     * @param function   表达式
     * @param <T>        泛型
     * @return maxDouble
     */
    public static <T> Double maxDouble(Collection<T> collection, Function<T, Double> function) {
        Optional<Double> optional = nullDefaultEmpty(collection).stream().map(function).max(Double::compare);
        return optional.orElse(0D);
    }

    /**
     * 集合中的BigDecimal类型最大值
     *
     * @param collection 源集合
     * @param function   表达式
     * @param <T>        泛型
     * @return maxBigDecimal
     */
    public static <T> BigDecimal maxBigDecimal(Collection<T> collection, Function<T, BigDecimal> function) {
        Optional<BigDecimal> optional = nullDefaultEmpty(collection).stream().map(function).max(BigDecimal::compareTo);
        return optional.orElse(BigDecimal.ZERO);
    }

    /**
     * 集合中的int类型最小值
     *
     * @param collection 源集合
     * @param function   表达式
     * @param <T>        泛型
     * @return minInt
     */
    public static <T> Integer minInt(Collection<T> collection, Function<T, Integer> function) {
        Optional<Integer> optional = nullDefaultEmpty(collection).stream().map(function).min(Integer::compare);
        return optional.orElse(0);
    }

    /**
     * 集合中的double类型最小值
     *
     * @param collection 源集合
     * @param function   表达式
     * @param <T>        泛型
     * @return minDouble
     */
    public static <T> Double minDouble(Collection<T> collection, Function<T, Double> function) {
        Optional<Double> optional = nullDefaultEmpty(collection).stream().map(function).min(Double::compare);
        return optional.orElse(0D);
    }

    /**
     * 集合中的BigDecimal类型最小值
     *
     * @param collection 源集合
     * @param function   表达式
     * @param <T>        泛型
     * @return minBigDecimal
     */
    public static <T> BigDecimal minBigDecimal(Collection<T> collection, Function<T, BigDecimal> function) {
        Optional<BigDecimal> optional = nullDefaultEmpty(collection).stream().map(function).min(BigDecimal::compareTo);
        return optional.orElse(BigDecimal.ZERO);
    }

    /**
     * 合并集合
     *
     * @param collections 集合
     * @param <T>
     * @return 合并合的集合
     */
    @SafeVarargs
    public static <T> List<T> mergeAll(Collection<T>... collections) {
        return Stream.of(collections).flatMap(Collection::stream).collect(Collectors.toList());
    }

    /**
     * 合并集合（重复了丢弃后面的）
     *
     * @param collections 集合多个
     * @param function    匹配function
     * @param <T>         泛型
     * @return 合并后的集合
     */
    @SafeVarargs
    public static <T> List<T> leftMerge(Function<? super T, ?> function, Collection<T>... collections) {
        return merge(function, (o1, o2) -> o1, collections);
    }


    /**
     * 合并集合（重复了覆盖前面的）
     *
     * @param collections 集合多个
     * @param function    匹配function
     * @param <T>         泛型
     * @return 合并后的集合
     */
    @SafeVarargs
    public static <T> List<T> rightMerge(Function<? super T, ?> function, Collection<T>... collections) {
        return merge(function, (o1, o2) -> o2, collections);
    }

    /**
     * 自定义条件合并集合
     *
     * @param collections 集合多个
     * @param function    匹配function
     * @param <T>         泛型
     * @return 合并后的集合
     */
    @SafeVarargs
    public static <T> List<T> merge(Function<? super T, ?> function, BinaryOperator<T> mergeFunction, Collection<T>... collections) {
        return new ArrayList<>(toStream(collections)
                .flatMap(Collection::stream)
                .collect(Collectors.toMap(function, Function.identity(), mergeFunction))
                .values());
    }

    /**
     * list根据对像某个字段进行去重
     *
     * @param collection 源集合
     * @param function   去重的字段
     * @param <T>        泛型
     * @return 去重后的list
     */
    public static <T> List<T> distinct(Collection<T> collection, Function<? super T, ?> function) {
        return (List<T>) nullDefaultEmpty(collection).stream().collect(Collectors.toMap(function, Function.identity(), (o1, o2) -> o1)).values();
    }

    /**
     * 转换成stream
     *
     * @param t   源始类型
     * @param <T> 泛型
     * @return Stream
     */
    @SafeVarargs
    public static <T> Stream<T> toStream(T... t) {
        return Stream.of(t);
    }

    /**
     * 判断是否为空 防止空指针
     *
     * @param list 源list
     * @param <T>  泛型
     * @return 为空返回空list否则返回原list
     */
    private static <T> Collection<T> nullDefaultEmpty(Collection<T> list) {
        return Optional.ofNullable(list).orElse(Collections.emptyList());
    }

}
