package com.abin.mallchat.custom.ac;

import com.abin.mallchat.common.common.algorithm.ac.ACTrie;
import com.abin.mallchat.common.common.algorithm.ac.MatchResult;
import com.google.common.collect.Lists;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertEquals;

/**
 * Created by berg on 2023/6/18.
 */
public class ACTrieTest {

    private final static List<String> ALPHABET = Lists.newArrayList("abc", "bcd", "cde");

    private static ACTrie trie(List<String> keywords) {
        return new ACTrie(keywords);
    }

    @Test
    public void test_TextIsLongerThanKeyword() {
        final ACTrie trie = trie(ALPHABET);
        final String text = " " + ALPHABET.get(0);
        List<MatchResult> matchResults = trie.matches(text);
        checkResult(matchResults.get(0), 1, 4, ALPHABET.get(0), text);
    }

    @Test
    public void test_VariousKeywordsOneMatch() {
        final ACTrie trie = trie(ALPHABET);
        final String text = "bcd";
        List<MatchResult> matchResults = trie.matches(text);
        checkResult(matchResults.get(0), 0, 3, ALPHABET.get(1), text);
    }

    @Test
    public void test_VariousKeywordsMultiMatch() {
        final ACTrie trie = trie(ALPHABET);
        final String text = "abcd";
        List<MatchResult> matchResults = trie.matches(text);
        assertEquals(2, matchResults.size());
        checkResult(matchResults.get(0), 0, 3, ALPHABET.get(0), text);
        checkResult(matchResults.get(1), 1, 4, ALPHABET.get(1), text);
    }

    @Test
    public void test_VariousKeywordsMultiMatch2() {
        final ACTrie trie = trie(ALPHABET);
        final String text = "abcde";
        List<MatchResult> matchResults = trie.matches(text);
        assertEquals(3, matchResults.size());
        checkResult(matchResults.get(0), 0, 3, ALPHABET.get(0), text);
        checkResult(matchResults.get(1), 1, 4, ALPHABET.get(1), text);
        checkResult(matchResults.get(2), 2, 5, ALPHABET.get(2), text);
    }

    private void checkResult(MatchResult matchResult, int expectedStart, int expectedEnd, String expectedKeyword, String text) {
        assertEquals("Start of match should have been " + expectedStart, expectedStart, matchResult.getStartIndex());
        assertEquals("End of match should have been " + expectedEnd, expectedEnd, matchResult.getEndIndex());
        assertEquals(expectedKeyword, text.substring(expectedStart, expectedEnd));
    }
}
