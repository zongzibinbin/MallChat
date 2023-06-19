package com.abin.mallchat.common.common.utils;

import org.apache.commons.lang3.StringUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.util.*;
import java.util.stream.Collectors;

public final class SensitiveWordUtils {
    private static Map<Character, Word> wordMap; // 敏感词Map
    private final static char replace = '*'; // 替代字符
    private final static char[] skip = new char[]{ // 遇到这些字符就会跳过
            ' ', '!', '*', '-', '+', '_', '=', ',', '，', '.', '@', ';', ':', '；', '：'
    };

    /**
     * 判断文本中是否存在敏感词
     *
     * @param text 文本
     * @return true: 存在敏感词, false: 不存在敏感词
     */
    public static boolean hasSensitiveWord(String text) {
        if (StringUtils.isBlank(text)) return false;
        return !Objects.equals(filter(text), text);
    }

    /**
     * 过滤敏感词并替换为指定字符
     *
     * @param text 待替换文本
     * @return 替换后的文本
     */
    /**
     * 敏感词替换
     *
     * @param text 待替换文本
     * @return 替换后的文本
     */
    public static String filter(String text) {
        if (wordMap == null || wordMap.isEmpty() || StringUtils.isBlank(text)) return text;
        char[] chars = text.toCharArray(); // 将文本转换为字符数组
        int length = chars.length; // 文本长度
        StringBuilder result = new StringBuilder(length); // 存储替换后的结果
        int i = 0; // 当前遍历的字符索引
        while (i < length) {
            char c = chars[i]; // 当前字符
            if (skip(c)) { // 如果是需要跳过的字符，则直接追加到结果中
                i++;
                continue;
            }
            int startIndex = i; // 敏感词匹配的起始索引
            Map<Character, Word> currentMap = wordMap; // 当前层级的敏感词字典
            int matchLength = 0; // 匹配到的敏感词长度
            for (int j = i; j < length; j++) {
                char ch = chars[j]; // 当前遍历的字符
                if (skip(ch)) { // 如果是需要跳过的字符，则直接追加到结果中
                    continue;
                }
                Word word = currentMap.get(ch); // 获取当前字符在当前层级的敏感词字典中对应的敏感词节点
                if (word == null) { // 如果未匹配到敏感词节点，则终止循环
                    break;
                }
                if (word.end) { // 如果当前节点是敏感词的最后一个节点，则记录匹配长度
                    matchLength = j - startIndex + 1;
                }
                currentMap = word.next; // 进入下一层级的敏感词字典
                if (word.next == null) { // 如果当前节点是敏感词的最后一个节点，则记录匹配长度
                    matchLength = j - startIndex + 1;
                }
            }
            if (matchLength > 0) { // 如果匹配到敏感词，则将对应的字符替换为指定替代字符
                for (int j = startIndex; j < startIndex + matchLength; j++) {
                    chars[j] = replace;
                }
            }
            i += matchLength > 0 ? matchLength : 1; // 更新当前索引，跳过匹配到的敏感词
        }
        result.append(chars); // 将匹配到的敏感词追加到结果中
        return result.toString();
    }


    /**
     * 加载敏感词列表
     *
     * @param words 敏感词数组
     */
    public static void loadWord(List<String> words) {
        if (words == null) return;
        words = words.stream().distinct().collect(Collectors.toList()); // 去重
        wordMap = new HashMap<>(); // 创建敏感词字典的根节点
        for (String word : words) {
            if (word == null) continue;
            char[] chars = word.toCharArray();
            Map<Character, Word> currentMap = wordMap; // 当前层级的敏感词字典
            for (int i = 0; i < chars.length; i++) {
                char c = chars[i];
                Word currentWord = currentMap.get(c);
                if (currentWord == null) {
                    Word newWord = new Word(c); // 创建新的敏感词节点
                    currentMap.put(c, newWord); // 将节点添加到当前层级的敏感词字典中
                    if (i == chars.length - 1) {
                        newWord.end = true; // 添加结束标志
                    }
                    currentMap = newWord.next = new HashMap<>(); // 进入下一层级
                } else {
                    currentMap = currentWord.next; // 存在该字符的节点，则进入下一层级
                }
            }
        }
    }


    /**
     * 从文本文件中加载敏感词列表
     *
     * @param path 文本文件的绝对路径
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

    /**
     * 判断是否需要跳过当前字符
     *
     * @param c 待检测字符
     * @return true: 需要跳过, false: 不需要跳过
     */
    private static boolean skip(char c) {
        for (char skipChar : skip) {
            if (skipChar == c) return true;
        }
        return false;
    }

    /**
     * 敏感词类
     */
    private static class Word {
        // 当前字符
        private char c;

        // 结束标识
        private boolean end;

        // 下一层级的敏感词字典
        private Map<Character, Word> next;

        public Word(char c) {
            this.c = c;
        }
    }

    public static void main(String[] args) {
        List<String> strings = Arrays.asList("白日梦", "白痴", "白痴是你","TMD");
        loadWord(strings);
        System.out.println(filter("TMD,白痴是你吗"));
    }
}
