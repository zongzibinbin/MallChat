package com.abin.mallchat.common.sensitive.service.impl;

import com.abin.mallchat.common.common.utils.SensitiveWordUtils;
import com.abin.mallchat.common.sensitive.dao.SensitiveWordDao;
import com.abin.mallchat.common.sensitive.domain.SensitiveWord;
import com.abin.mallchat.common.sensitive.service.ISensitiveWordService;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class SensitiveWordServiceImpl implements ISensitiveWordService {
    @Autowired
    private SensitiveWordDao sensitiveWordDao;

    @PostConstruct
    public void initSensitiveWord() {
        List<SensitiveWord> list = sensitiveWordDao.list();
        if (!CollectionUtils.isEmpty(list)) {
            List<String> wordList = list.stream()
                    .map(SensitiveWord::getWord)
                    .collect(Collectors.toList());
            SensitiveWordUtils.loadWord(wordList);
        }
    }
}
