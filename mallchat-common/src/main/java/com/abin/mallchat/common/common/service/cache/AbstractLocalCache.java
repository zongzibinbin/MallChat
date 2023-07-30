package com.abin.mallchat.common.common.service.cache;

import com.github.benmanes.caffeine.cache.CacheLoader;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.LoadingCache;
import com.google.common.collect.Iterables;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.lang.reflect.ParameterizedType;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * Description: redis string类型的批量缓存框架
 * Author: <a href="https://github.com/zongzibinbin">abin</a>
 * Date: 2023-06-10
 */
public abstract class AbstractLocalCache<IN, OUT> implements BatchCache<IN, OUT> {

    private Class<OUT> outClass;
    private Class<IN> inClass;
    private LoadingCache<IN, OUT> cache;

    protected AbstractLocalCache() {
        init(60, 10 * 60, 1024);
    }

    protected AbstractLocalCache(long refreshSeconds, long expireSeconds, int maxSize) {
        init(refreshSeconds, expireSeconds, maxSize);
    }

    private void init(long refreshSeconds, long expireSeconds, int maxSize) {
        ParameterizedType genericSuperclass = (ParameterizedType) this.getClass().getGenericSuperclass();
        this.outClass = (Class<OUT>) genericSuperclass.getActualTypeArguments()[1];
        this.inClass = (Class<IN>) genericSuperclass.getActualTypeArguments()[0];
        cache = Caffeine.newBuilder()
                //自动刷新,不会阻塞线程,其他线程返回旧值
                .refreshAfterWrite(refreshSeconds, TimeUnit.SECONDS)
                .expireAfterWrite(expireSeconds, TimeUnit.SECONDS)
                .maximumSize(maxSize)
                .build(new CacheLoader<IN, OUT>() {
                    @Nullable
                    @Override
                    public OUT load(@NonNull IN in) throws Exception {
                        return AbstractLocalCache.this.load(Collections.singletonList(in)).get(in);
                    }

                    @Override
                    public @NonNull Map<IN, OUT> loadAll(@NonNull Iterable<? extends IN> keys) throws Exception {
                        IN[] ins = Iterables.toArray(keys, inClass);
                        return AbstractLocalCache.this.load(Arrays.asList(ins));
                    }
                });
    }

    protected abstract Map<IN, OUT> load(List<IN> req);

    @Override
    public OUT get(IN req) {
        return cache.get(req);
    }

    @Override
    public Map<IN, OUT> getBatch(List<IN> req) {
        return cache.getAll(req);
    }

    @Override
    public void delete(IN req) {
        cache.invalidate(req);
    }

    @Override
    public void deleteBatch(List<IN> req) {
        cache.invalidateAll(req);
    }
}
