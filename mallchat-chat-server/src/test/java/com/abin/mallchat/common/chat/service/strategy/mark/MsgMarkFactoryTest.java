package com.abin.mallchat.common.chat.service.strategy.mark;

import com.abin.mallchat.common.chat.domain.enums.MessageMarkTypeEnum;
import com.abin.mallchat.common.common.exception.BusinessException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.*;

@RunWith(MockitoJUnitRunner.class)
public class MsgMarkFactoryTest {

    @Mock
    private LikeStrategy likeStrategy;

    @Mock
    private DisLikeStrategy disLikeStrategy;

    private Map<Integer, AbstractMsgMarkStrategy> originalStrategies;

    @Before
    public void setUp() throws Exception {
        originalStrategies = new HashMap<>();
        Map<Integer, AbstractMsgMarkStrategy> strategyMap = getStrategyMap();
        originalStrategies.putAll(strategyMap);
        strategyMap.clear();
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
    public void testRegisterAndGetStrategy() {
        MsgMarkFactory.register(MessageMarkTypeEnum.LIKE.getType(), likeStrategy);
        MsgMarkFactory.register(MessageMarkTypeEnum.DISLIKE.getType(), disLikeStrategy);

        AbstractMsgMarkStrategy retrievedLikeStrategy = MsgMarkFactory.getStrategyNoNull(MessageMarkTypeEnum.LIKE.getType());
        AbstractMsgMarkStrategy retrievedDisLikeStrategy = MsgMarkFactory.getStrategyNoNull(MessageMarkTypeEnum.DISLIKE.getType());

        assertSame(likeStrategy, retrievedLikeStrategy);
        assertSame(disLikeStrategy, retrievedDisLikeStrategy);
    }

    @Test(expected = BusinessException.class)
    public void testGetStrategyWithNullType() {
        MsgMarkFactory.getStrategyNoNull(null);
    }

    @Test(expected = BusinessException.class)
    public void testGetStrategyWithNonExistentType() {
        MsgMarkFactory.getStrategyNoNull(999);
    }

    @Test
    public void testRegisterSameTypeTwice() {
        MsgMarkFactory.register(MessageMarkTypeEnum.LIKE.getType(), likeStrategy);
        MsgMarkFactory.register(MessageMarkTypeEnum.LIKE.getType(), disLikeStrategy);

        AbstractMsgMarkStrategy retrieved = MsgMarkFactory.getStrategyNoNull(MessageMarkTypeEnum.LIKE.getType());
        assertSame(disLikeStrategy, retrieved);
    }
}
