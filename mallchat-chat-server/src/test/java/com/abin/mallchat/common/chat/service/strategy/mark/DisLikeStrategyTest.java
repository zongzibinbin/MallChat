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
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.context.ApplicationEventPublisher;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class DisLikeStrategyTest {

    @InjectMocks
    private DisLikeStrategy disLikeStrategy;

    @Mock
    private MessageMarkDao messageMarkDao;

    @Mock
    private ApplicationEventPublisher applicationEventPublisher;

    @Mock
    private LikeStrategy likeStrategy;

    private Map<Integer, AbstractMsgMarkStrategy> originalStrategies;

    @Before
    public void setUp() throws Exception {
        originalStrategies = new HashMap<>();
        Map<Integer, AbstractMsgMarkStrategy> strategyMap = getStrategyMap();
        originalStrategies.putAll(strategyMap);
        strategyMap.clear();
        strategyMap.put(MessageMarkTypeEnum.LIKE.getType(), likeStrategy);
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
    public void testMark_Success() {
        Long uid = 1001L;
        Long msgId = 1L;

        when(messageMarkDao.get(uid, msgId, MessageMarkTypeEnum.DISLIKE.getType())).thenReturn(null);
        when(messageMarkDao.saveOrUpdate(any(MessageMark.class))).thenReturn(true);

        disLikeStrategy.mark(uid, msgId);

        ArgumentCaptor<MessageMark> messageMarkCaptor = ArgumentCaptor.forClass(MessageMark.class);
        verify(messageMarkDao).saveOrUpdate(messageMarkCaptor.capture());

        MessageMark capturedMark = messageMarkCaptor.getValue();
        assertEquals(uid, capturedMark.getUid());
        assertEquals(msgId, capturedMark.getMsgId());
        assertEquals(MessageMarkTypeEnum.DISLIKE.getType(), capturedMark.getType());
        assertEquals(NormalOrNoEnum.NORMAL.getStatus(), capturedMark.getStatus());

        verify(applicationEventPublisher).publishEvent(any(MessageMarkEvent.class));
        verify(likeStrategy).unMark(uid, msgId);
    }

    @Test
    public void testMark_DuplicateDisLike() {
        Long uid = 1001L;
        Long msgId = 1L;

        MessageMark existingMark = MessageMark.builder()
                .id(1L)
                .uid(uid)
                .msgId(msgId)
                .type(MessageMarkTypeEnum.DISLIKE.getType())
                .status(NormalOrNoEnum.NORMAL.getStatus())
                .build();

        when(messageMarkDao.get(uid, msgId, MessageMarkTypeEnum.DISLIKE.getType())).thenReturn(existingMark);
        when(messageMarkDao.saveOrUpdate(any(MessageMark.class))).thenReturn(false);

        disLikeStrategy.mark(uid, msgId);

        ArgumentCaptor<MessageMark> messageMarkCaptor = ArgumentCaptor.forClass(MessageMark.class);
        verify(messageMarkDao).saveOrUpdate(messageMarkCaptor.capture());

        MessageMark capturedMark = messageMarkCaptor.getValue();
        assertEquals(uid, capturedMark.getUid());
        assertEquals(msgId, capturedMark.getMsgId());
        assertEquals(MessageMarkTypeEnum.DISLIKE.getType(), capturedMark.getType());
        assertEquals(NormalOrNoEnum.NORMAL.getStatus(), capturedMark.getStatus());

        verify(applicationEventPublisher, never()).publishEvent(any(MessageMarkEvent.class));
        verify(likeStrategy).unMark(uid, msgId);
    }

    @Test
    public void testMark_SelfDisLike() {
        Long uid = 1001L;
        Long msgId = 1L;

        when(messageMarkDao.get(uid, msgId, MessageMarkTypeEnum.DISLIKE.getType())).thenReturn(null);
        when(messageMarkDao.saveOrUpdate(any(MessageMark.class))).thenReturn(true);

        disLikeStrategy.mark(uid, msgId);

        ArgumentCaptor<MessageMark> messageMarkCaptor = ArgumentCaptor.forClass(MessageMark.class);
        verify(messageMarkDao).saveOrUpdate(messageMarkCaptor.capture());

        MessageMark capturedMark = messageMarkCaptor.getValue();
        assertEquals(uid, capturedMark.getUid());
        assertEquals(msgId, capturedMark.getMsgId());
        assertEquals(MessageMarkTypeEnum.DISLIKE.getType(), capturedMark.getType());
        assertEquals(NormalOrNoEnum.NORMAL.getStatus(), capturedMark.getStatus());

        verify(applicationEventPublisher).publishEvent(any(MessageMarkEvent.class));
        verify(likeStrategy).unMark(uid, msgId);
    }

    @Test
    public void testUnMark_Success() {
        Long uid = 1001L;
        Long msgId = 1L;

        MessageMark existingMark = MessageMark.builder()
                .id(1L)
                .uid(uid)
                .msgId(msgId)
                .type(MessageMarkTypeEnum.DISLIKE.getType())
                .status(NormalOrNoEnum.NORMAL.getStatus())
                .build();

        when(messageMarkDao.get(uid, msgId, MessageMarkTypeEnum.DISLIKE.getType())).thenReturn(existingMark);
        when(messageMarkDao.saveOrUpdate(any(MessageMark.class))).thenReturn(true);

        disLikeStrategy.unMark(uid, msgId);

        ArgumentCaptor<MessageMark> messageMarkCaptor = ArgumentCaptor.forClass(MessageMark.class);
        verify(messageMarkDao).saveOrUpdate(messageMarkCaptor.capture());

        MessageMark capturedMark = messageMarkCaptor.getValue();
        assertEquals(uid, capturedMark.getUid());
        assertEquals(msgId, capturedMark.getMsgId());
        assertEquals(MessageMarkTypeEnum.DISLIKE.getType(), capturedMark.getType());
        assertEquals(NormalOrNoEnum.NOT_NORMAL.getStatus(), capturedMark.getStatus());

        verify(applicationEventPublisher).publishEvent(any(MessageMarkEvent.class));
    }

    @Test
    public void testUnMark_NoExistingMark() {
        Long uid = 1001L;
        Long msgId = 1L;

        when(messageMarkDao.get(uid, msgId, MessageMarkTypeEnum.DISLIKE.getType())).thenReturn(null);

        disLikeStrategy.unMark(uid, msgId);

        verify(messageMarkDao, never()).saveOrUpdate(any(MessageMark.class));
        verify(applicationEventPublisher, never()).publishEvent(any(MessageMarkEvent.class));
    }

    @Test
    public void testGetTypeEnum() {
        assertEquals(MessageMarkTypeEnum.DISLIKE, disLikeStrategy.getTypeEnum());
    }
}
