package com.ohgiraffers.store.common.config;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * DAO별 query.xml을 읽어 SQL 문자열을 제공하는 클래스이다.
 *
 * DAO는 SQL 내용을 직접 가지고 있지 않고, 이 클래스에 쿼리 키를 전달해
 * 필요한 SQL을 가져간다.
 */
public final class QueryProvider {

    /* 객체를 만들 필요가 없는 유틸리티 클래스이므로 외부 생성을 막는다. */
    private QueryProvider() {
    }

    /**
     * 지정한 query.xml에서 key와 일치하는 SQL을 반환한다.
     * 존재하지 않는 key를 사용하면 잘못된 쿼리 이름을 바로 알 수 있도록 예외를 발생시킨다.
     */
    public static String getQuery(Properties queries, String key) {
        String query = queries.getProperty(key);

        if (query == null || query.isBlank()) {
            throw new IllegalArgumentException("query.xml에 없는 쿼리 키입니다: " + key);
        }

        return query;
    }

    /** DAO가 지정한 classpath의 XML을 읽어 Properties로 반환한다. */
    public static Properties loadQueries(String queryFile) {
        Properties queries = new Properties();

        try (InputStream inputStream = QueryProvider.class
                .getClassLoader()
                .getResourceAsStream(queryFile)) {

            if (inputStream == null) {
                throw new IllegalStateException("쿼리 파일을 찾을 수 없습니다: " + queryFile);
            }

            queries.loadFromXML(inputStream);
            return queries;
        } catch (IOException e) {
            throw new IllegalStateException("쿼리 파일을 읽을 수 없습니다: " + queryFile, e);
        }
    }
}
