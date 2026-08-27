package com.ohgiraffers.store.common.config;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public final class DBConnection {

    private static final String PROPERTIES_FILE = "database.properties";
    private static final Properties PROPERTIES = loadProperties();

    private DBConnection() {
    }

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(
                requiredProperty("db.url"),
                requiredProperty("db.user"),
                requiredProperty("db.password")
        );
    }

    private static Properties loadProperties() {
        Properties properties = new Properties();

        try (InputStream inputStream = DBConnection.class
                .getClassLoader()
                .getResourceAsStream(PROPERTIES_FILE)) {

            if (inputStream == null) {
                throw new IllegalStateException(
                        PROPERTIES_FILE + " 파일을 찾을 수 없습니다. "
                                + "database.properties.example을 복사해 생성하세요."
                );
            }

            properties.load(inputStream);
            return properties;
        } catch (IOException exception) {
            throw new IllegalStateException(
                    PROPERTIES_FILE + " 파일을 읽을 수 없습니다.",
                    exception
            );
        }
    }

    private static String requiredProperty(String key) {
        String value = PROPERTIES.getProperty(key);

        if (value == null || value.isBlank()) {
            throw new IllegalStateException(
                    PROPERTIES_FILE + "의 " + key + " 설정이 비어 있습니다."
            );
        }

        return value;
    }
}
