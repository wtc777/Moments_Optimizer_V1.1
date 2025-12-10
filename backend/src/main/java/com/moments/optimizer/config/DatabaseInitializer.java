package com.moments.optimizer.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.net.URISyntaxException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

@Component
public class DatabaseInitializer implements ApplicationRunner {

    private static final Logger log = LoggerFactory.getLogger(DatabaseInitializer.class);

    @Value("${spring.datasource.url}")
    private String dataSourceUrl;

    @Value("${spring.datasource.username}")
    private String username;

    @Value("${spring.datasource.password:}")
    private String password;

    @Value("${moments.db.auto-create:true}")
    private boolean autoCreate;

    @Override
    public void run(ApplicationArguments args) {
        if (!autoCreate) {
            log.info("Database auto-create is disabled (moments.db.auto-create=false)");
            return;
        }
        try {
            ParsedUrl parsed = parseJdbcUrl(dataSourceUrl);
            createDatabaseIfMissing(parsed);
        } catch (Exception ex) {
            log.warn("Database auto-create failed: {}", ex.getMessage());
        }
    }

    private void createDatabaseIfMissing(ParsedUrl parsed) throws SQLException, ClassNotFoundException {
        Class.forName("com.mysql.cj.jdbc.Driver");
        String adminUrl = parsed.baseUrl;
        String dbName = parsed.database;
        String sql = "CREATE DATABASE IF NOT EXISTS `" + dbName + "` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci";
        try (Connection conn = DriverManager.getConnection(adminUrl, username, password);
             Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
            log.info("Verified database exists (created if missing): {}", dbName);
        }
    }

    private ParsedUrl parseJdbcUrl(String url) throws URISyntaxException {
        if (url == null || !url.startsWith("jdbc:mysql://")) {
            throw new IllegalArgumentException("Unsupported JDBC URL: " + url);
        }
        String withoutPrefix = url.substring("jdbc:mysql://".length());
        String hostPortAndPath;
        String query = "";
        int qIndex = withoutPrefix.indexOf("?");
        if (qIndex >= 0) {
            hostPortAndPath = withoutPrefix.substring(0, qIndex);
            query = withoutPrefix.substring(qIndex);
        } else {
            hostPortAndPath = withoutPrefix;
        }
        int slashIndex = hostPortAndPath.indexOf("/");
        if (slashIndex < 0 || slashIndex == hostPortAndPath.length() - 1) {
            throw new IllegalArgumentException("Database name not found in URL: " + url);
        }
        String hostPort = hostPortAndPath.substring(0, slashIndex);
        String database = hostPortAndPath.substring(slashIndex + 1);
        String baseUrl = "jdbc:mysql://" + hostPort + "/" + query;
        return new ParsedUrl(baseUrl, database);
    }

    private record ParsedUrl(String baseUrl, String database) {
    }
}
