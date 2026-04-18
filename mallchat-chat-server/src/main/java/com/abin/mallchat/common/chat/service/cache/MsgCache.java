package com.abin.mallchat.common.chat.service.cache;

import com.abin.mallchat.common.chat.dao.MessageDao;
import com.abin.mallchat.common.chat.domain.entity.Message;
import com.abin.mallchat.common.common.constant.RedisKey;
import com.abin.mallchat.common.common.service.cache.AbstractRedisStringCache;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Description: 消息相关缓存
 * Author: <a href="https://github.com/zongzibinbin">abin</a>
 * Date: 2023-03-27
 */
@Component
public class MsgCache extends AbstractRedisStringCache<Long, Message> {

    @Autowired
    private MessageDao messageDao;

    @Override
    protected String getKey(Long msgId) {
        return RedisKey.getKey(RedisKey.MSG_INFO_STRING, msgId);
    }

    @Override
    protected Long getExpireSeconds() {
        return 5 * 60L;
    }

    @Override
    protected Map<Long, Message> load(List<Long> msgIds) {
        List<Message> messages = messageDao.listByIds(msgIds);
        return messages.stream().collect(Collectors.toMap(Message::getId, Function.identity()));
    }

    public Message getMsg(Long msgId) {
        return get(msgId);
    }

    public void evictMsg(Long msgId) {
        delete(msgId);
    }
}
