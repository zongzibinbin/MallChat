package com.abin.mallchat.common.chat.service.strategy.mark;

import com.abin.mallchat.common.chat.dao.MessageMarkDao;
import com.abin.mallchat.common.chat.domain.entity.MessageMark;
import com.abin.mallchat.common.chat.domain.enums.MessageMarkTypeEnum;
import com.abin.mallchat.common.common.domain.enums.NormalOrNoEnum;
import com.abin.mallchat.common.common.event.MessageMarkEvent;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.context.ApplicationEventPublisher;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class ConcurrentMsgMarkTest {

    @InjectMocks
    private LikeStrategy likeStrategy;

    @InjectMocks
    private DisLikeStrategy disLikeStrategy;

    @Mock
    private MessageMarkDao messageMarkDao;

    @Mock
    private ApplicationEventPublisher applicationEventPublisher;

    @Mock
    private LikeStrategy mockLikeStrategy;

    @Mock
    private DisLikeStrategy mockDisLikeStrategy;

    private Map<Integer, AbstractMsgMarkStrategy> originalStrategies;

    @Before
    public void setUp() throws Exception {
        originalStrategies = new HashMap<>();
        Map<Integer, AbstractMsgMarkStrategy> strategyMap = getStrategyMap();
        originalStrategies.putAll(strategyMap);
        strategyMap.clear();
        strategyMap.put(MessageMarkTypeEnum.LIKE.getType(), mockLikeStrategy);
        strategyMap.put(MessageMarkTypeEnum.DISLIKE.getType(), mockDisLikeStrategy);
    }

    @After
    public void tearDown() throws Exception {
        Map<Integer, AbstractMsgMarkStrategy> strategyMap = getStrategyMap();
        strategyMap.clear();
        strategyMap.putAll(originalStrategies);
    }

    private Map<Integer, AbstractMsgMarkStrategy> getStrategyMap() throws Exception {
        Field field = MsgMarkFactory.class.getDeclaredField("STRATEGY_MAP");
        field.setAccessible(true);
        return (Map<Integer, AbstractMsgMarkStrategy>) field.get(null);
    }

    @Test
    public void testConcurrentLike_MultipleUsers() throws InterruptedException {
        int threadCount = 10;
        Long msgId = 1L;

        CountDownLatch startLatch = new CountDownLatch(1);
        CountDownLatch endLatch = new CountDownLatch(threadCount);
        AtomicInteger successCount = new AtomicInteger(0);

        when(messageMarkDao.get(anyLong(), anyLong(), anyInt())).thenReturn(null);
        when(messageMarkDao.saveOrUpdate(any(MessageMark.class))).thenReturn(true);

        ExecutorService executorService = Executors.newFixedThreadPool(threadCount);

        for (int i = 0; i < threadCount; i++) {
            final long userId = 1000L + i;
            executorService.submit(() -> {
                try {
                    startLatch.await();
                    likeStrategy.mark(userId, msgId);
                    successCount.incrementAndGet();
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    endLatch.countDown();
                }
            });
        }

        startLatch.countDown();
        endLatch.await();
        executorService.shutdown();

        assertEquals(threadCount, successCount.get());
        verify(messageMarkDao, times(threadCount)).saveOrUpdate(any(MessageMark.class));
        verify(applicationEventPublisher, times(threadCount)).publishEvent(any(MessageMarkEvent.class));
    }

    @Test
    public void testConcurrentLike_SameUser() throws InterruptedException {
        int threadCount = 10;
        Long uid = 1001L;
        Long msgId = 1L;

        CountDownLatch startLatch = new CountDownLatch(1);
        CountDownLatch endLatch = new CountDownLatch(threadCount);
        AtomicInteger successCount = new AtomicInteger(0);

        MessageMark existingMark = MessageMark.builder()
                .id(1L)
                .uid(uid)
                .msgId(msgId)
                .type(MessageMarkTypeEnum.LIKE.getType())
                .status(NormalOrNoEnum.NORMAL.getStatus())
                .build();

        when(messageMarkDao.get(uid, msgId, MessageMarkTypeEnum.LIKE.getType())).thenReturn(existingMark);
        when(messageMarkDao.saveOrUpdate(any(MessageMark.class))).thenReturn(true);

        ExecutorService executorService = Executors.newFixedThreadPool(threadCount);

        for (int i = 0; i < threadCount; i++) {
            executorService.submit(() -> {
                try {
                    startLatch.await();
                    likeStrategy.mark(uid, msgId);
                    successCount.incrementAndGet();
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    endLatch.countDown();
                }
            });
        }

        startLatch.countDown();
        endLatch.await();
        executorService.shutdown();

        assertEquals(threadCount, successCount.get());
        verify(messageMarkDao, times(threadCount)).saveOrUpdate(any(MessageMark.class));
    }

    @Test
    public void testConcurrentLikeAndDisLike() throws InterruptedException {
        int threadCount = 10;
        Long uid = 1001L;
        Long msgId = 1L;

        CountDownLatch startLatch = new CountDownLatch(1);
        CountDownLatch endLatch = new CountDownLatch(threadCount * 2);
        AtomicInteger likeSuccessCount = new AtomicInteger(0);
        AtomicInteger dislikeSuccessCount = new AtomicInteger(0);

        when(messageMarkDao.get(anyLong(), anyLong(), anyInt())).thenReturn(null);
        when(messageMarkDao.saveOrUpdate(any(MessageMark.class))).thenReturn(true);

        ExecutorService executorService = Executors.newFixedThreadPool(threadCount * 2);

        for (int i = 0; i < threadCount; i++) {
            executorService.submit(() -> {
                try {
                    startLatch.await();
                    likeStrategy.mark(uid, msgId);
                    likeSuccessCount.incrementAndGet();
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    endLatch.countDown();
                }
            });

            executorService.submit(() -> {
                try {
                    startLatch.await();
                    disLikeStrategy.mark(uid, msgId);
                    dislikeSuccessCount.incrementAndGet();
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    endLatch.countDown();
                }
            });
        }

        startLatch.countDown();
        endLatch.await();
        executorService.shutdown();

        assertEquals(threadCount, likeSuccessCount.get());
        assertEquals(threadCount, dislikeSuccessCount.get());
        verify(messageMarkDao, atLeast(threadCount * 2)).saveOrUpdate(any(MessageMark.class));
    }

    @Test
    public void testConcurrentMarkAndUnMark() throws InterruptedException {
        int threadCount = 10;
        Long uid = 1001L;
        Long msgId = 1L;

        CountDownLatch startLatch = new CountDownLatch(1);
        CountDownLatch endLatch = new CountDownLatch(threadCount * 2);
        AtomicInteger markSuccessCount = new AtomicInteger(0);
        AtomicInteger unMarkSuccessCount = new AtomicInteger(0);

        MessageMark existingMark = MessageMark.builder()
                .id(1L)
                .uid(uid)
                .msgId(msgId)
                .type(MessageMarkTypeEnum.LIKE.getType())
                .status(NormalOrNoEnum.NORMAL.getStatus())
                .build();

        when(messageMarkDao.get(uid, msgId, MessageMarkTypeEnum.LIKE.getType())).thenReturn(existingMark);
        when(messageMarkDao.saveOrUpdate(any(MessageMark.class))).thenReturn(true);

        ExecutorService executorService = Executors.newFixedThreadPool(threadCount * 2);

        for (int i = 0; i < threadCount; i++) {
            executorService.submit(() -> {
                try {
                    startLatch.await();
                    likeStrategy.mark(uid, msgId);
                    markSuccessCount.incrementAndGet();
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    endLatch.countDown();
                }
            });

            executorService.submit(() -> {
                try {
                    startLatch.await();
                    likeStrategy.unMark(uid, msgId);
                    unMarkSuccessCount.incrementAndGet();
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    endLatch.countDown();
                }
            });
        }

        startLatch.countDown();
        endLatch.await();
        executorService.shutdown();

        assertEquals(threadCount, markSuccessCount.get());
        assertEquals(threadCount, unMarkSuccessCount.get());
        verify(messageMarkDao, atLeast(threadCount * 2)).saveOrUpdate(any(MessageMark.class));
    }
}
