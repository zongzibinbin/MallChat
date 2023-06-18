package com.abin.mallchat.common.common.utils;

import com.abin.mallchat.common.common.algorithm.ac.ACTrie;
import com.abin.mallchat.common.common.algorithm.ac.MatchResult;
import org.apache.commons.lang3.StringUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


/**
 * 敏感词过滤
 *
 * @author zhaoyuhang
 * @since 2023/06/11
 */
public final class SensitiveWordUtils {
    private final static char mask_char = '*'; // 替代字符

    private static ACTrie ac_trie = null;

    /**
     * 有敏感词
     *
     * @param text 文本
     * @return boolean
     */
    public static boolean hasSensitiveWord(String text) {
        if (StringUtils.isBlank(text)) return false;
        return !Objects.equals(filter(text), text);
    }

    /**
     * 敏感词替换
     *
     * @param text 待替换文本
     * @return 替换后的文本
     */
    public static String filter(String text) {
        if (StringUtils.isBlank(text)) return text;
        List<MatchResult> matchResults = ac_trie.matches(text);
        StringBuffer result = new StringBuffer(text);
        for (MatchResult matchResult : matchResults) {
            replaceBetween(result, matchResult.getStartIndex(), matchResult.getEndIndex());
        }
        return result.toString();
    }

    private static void replaceBetween(StringBuffer buffer, int startIndex, int endIndex) {
        for (int i = startIndex; i < endIndex; i++) {
            buffer.setCharAt(i, mask_char);
        }
    }

    /**
     * 加载敏感词列表
     *
     * @param words 敏感词数组
     */
    public static void loadWord(List<String> words) {
        if (words == null) return;
        ac_trie = new ACTrie(words);
    }

    /**
     * 加载敏感词txt文件，每个敏感词独占一行，不可出现空格，空行，逗号等非文字内容,必须使用UTF-8编码
     *
     * @param path txt文件的绝对地址
     */
    public static void loadWordFromFile(String path) {
        String encoding = "UTF-8";
        File file = new File(path);
        try {
            if (file.isFile() && file.exists()) {
                InputStreamReader inputStreamReader = new InputStreamReader(
                        Files.newInputStream(file.toPath()), encoding
                );
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                String line;
                ArrayList<String> list = new ArrayList<>();
                while ((line = bufferedReader.readLine()) != null) {
                    list.add(line);
                }
                bufferedReader.close();
                inputStreamReader.close();
                loadWord(list);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }



}


