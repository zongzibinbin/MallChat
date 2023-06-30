package com.abin.mallchat.common.sensitive.service.impl;

import com.abin.mallchat.common.common.utils.SensitiveWordUtils;
import com.abin.mallchat.common.sensitive.dao.SensitiveWordDao;
import com.abin.mallchat.common.sensitive.domain.SensitiveWord;
import com.abin.mallchat.common.sensitive.service.ISensitiveWordService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class SensitiveWordServiceImpl implements ISensitiveWordService {
    @Autowired
    private SensitiveWordDao sensitiveWordDao;
    @Autowired
    private ThreadPoolTaskExecutor threadPoolTaskExecutor;

    @PostConstruct
    public void initSensitiveWord() {
        threadPoolTaskExecutor.execute(() -> {
            log.info("[initSensitiveWord] start");
            List<SensitiveWord> list = sensitiveWordDao.list();
            if (!CollectionUtils.isEmpty(list)) {
                List<String> wordList = list.stream()
                        .map(SensitiveWord::getWord)
                        .collect(Collectors.toList());
                SensitiveWordUtils.loadWord(wordList);
            }
            log.info("[initSensitiveWord] end; loading sensitiveWords num:{}", list.size());
        });
    }
}
