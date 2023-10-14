package com.abin.mallchat.common.chatai.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@AllArgsConstructor
@Getter
public enum ChatGPTModelEnum {
    // chat
    GPT_35_TURBO("gpt-3.5-turbo", 3, 40000),
    GPT_35_TURBO_0301("gpt-3.5-turbo-0301", 3, 40000),
    GPT_35_TURBO_0613("gpt-3.5-turbo-0613", 3, 40000),
    GPT_35_TURBO_16K("gpt-3.5-turbo-16k", 3, 40000),
    GPT_35_TURBO_16K_0613("gpt-3.5-turbo-16k-0613", 3, 40000),
    // text
    ADA("ada", 60, 150000),
    ADA_CODE_SEARCH_CODE("ada-code-search-code", 60, 150000),
    ADA_CODE_SEARCH_TEXT("ada-code-search-text", 60, 150000),
    ADA_SEARCH_DOCUMENT("ada-search-document", 60, 150000),
    ADA_SEARCH_QUERY("ada-search-query", 60, 150000),
    ADA_SIMILARITY("ada-similarity", 60, 150000),
    BABBAGE("babbage", 60, 150000),
    BABBAGE_CODE_SEARCH_CODE("babbage-code-search-code", 60, 150000),
    BABBAGE_CODE_SEARCH_TEXT("babbage-code-search-text", 60, 150000),
    BABBAGE_SEARCH_DOCUMENT("babbage-search-document", 60, 150000),
    BABBAGE_SEARCH_QUERY("babbage-search-query", 60, 150000),
    BABBAGE_SIMILARITY("babbage-similarity", 60, 150000),
    CODE_DAVINCI_EDIT_001("code-davinci-edit-001", 20, 150000),
    CODE_SEARCH_ADA_CODE_001("code-search-ada-code-001", 60, 150000),
    CODE_SEARCH_ADA_TEXT_001("code-search-ada-text-001", 60, 150000),
    CODE_SEARCH_BABBAGE_CODE_001("code-search-babbage-code-001", 60, 150000),
    CODE_SEARCH_BABBAGE_TEXT_001("code-search-babbage-text-001", 60, 150000),
    CURIE("curie", 60, 150000),
    CURIE_INSTRUCT_BETA("curie-instruct-beta", 60, 150000),
    CURIE_SEARCH_DOCUMENT("curie-search-document", 60, 150000),
    CURIE_SEARCH_QUERY("curie-search-query", 60, 150000),
    CURIE_SIMILARITY("curie-similarity", 60, 150000),
    DAVINCI("davinci", 60, 150000),
    DAVINCI_INSTRUCT_BETA("davinci-instruct-beta", 60, 150000),
    DAVINCI_SEARCH_DOCUMENT("davinci-search-document", 60, 150000),
    DAVINCI_SEARCH_QUERY("davinci-search-query", 60, 150000),
    DAVINCI_SIMILARITY("davinci-similarity", 60, 150000),
    TEXT_ADA_001("text-ada-001", 60, 150000),
    TEXT_BABBAGE_001("text-babbage-001", 60, 150000),
    TEXT_CURIE_001("text-curie-001", 60, 150000),
    TEXT_DAVINCI_001("text-davinci-001", 60, 150000),
    TEXT_DAVINCI_002("text-davinci-002", 60, 150000),
    TEXT_DAVINCI_003("text-davinci-003", 60, 150000),
    TEXT_DAVINCI_EDIT_001("text-davinci-edit-001", 20, 150000),
    TEXT_EMBEDDING_ADA_002("text-embedding-ada-002", 60, 150000),
    TEXT_SEARCH_ADA_DOC_001("text-search-ada-doc-001", 60, 150000),
    TEXT_SEARCH_ADA_QUERY_001("text-search-ada-query-001", 60, 150000),
    TEXT_SEARCH_BABBAGE_DOC_001("text-search-babbage-doc-001", 60, 150000),
    TEXT_SEARCH_BABBAGE_QUERY_001("text-search-babbage-query-001", 60, 150000),
    TEXT_SEARCH_CURIE_DOC_001("text-search-curie-doc-001", 60, 150000),
    TEXT_SEARCH_CURIE_QUERY_001("text-search-curie-query-001", 60, 150000),
    TEXT_SEARCH_DAVINCI_DOC_001("text-search-davinci-doc-001", 60, 150000),
    TEXT_SEARCH_DAVINCI_QUERY_001("text-search-davinci-query-001", 60, 150000),
    TEXT_SIMILARITY_ADA_001("text-similarity-ada-001", 60, 150000),
    TEXT_SIMILARITY_BABBAGE_001("text-similarity-babbage-001", 60, 150000),
    TEXT_SIMILARITY_CURIE_001("text-similarity-curie-001", 60, 150000),
    TEXT_SIMILARITY_DAVINCI_001("text-similarity-davinci-001", 60, 150000);

    /**
     * 名字
     */
    private final String name;
    /**
     * 每分钟请求数
     */
    private final Integer RPM;
    /**
     * 每分钟令牌数
     */
    private final Integer TPM;

    private static final Map<String, ChatGPTModelEnum> cache;

    static {
        cache = Arrays.stream(ChatGPTModelEnum.values()).collect(Collectors.toMap(ChatGPTModelEnum::getName, Function.identity()));
    }

    public static ChatGPTModelEnum of(String name) {
        return cache.get(name);
    }

}
