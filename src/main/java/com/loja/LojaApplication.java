package com.loja;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.util.Properties;

@SpringBootApplication
public class LojaApplication {

    public static void main(String[] args) {
        createDatabaseIfNotExists();
        SpringApplication.run(LojaApplication.class, args);
    }

    private static void createDatabaseIfNotExists() {
        try {
            Properties props = new Properties();
            try (InputStream is = LojaApplication.class.getResourceAsStream("/application.properties")) {
                if (is != null) props.load(is);
            }

            String url = props.getProperty("spring.datasource.url", "jdbc:postgresql://localhost:5432/manas_beauty");
            String username = props.getProperty("spring.datasource.username", "postgres");
            String password = props.getProperty("spring.datasource.password", "");

            String dbName = url.substring(url.lastIndexOf('/') + 1);
            if (dbName.contains("?")) dbName = dbName.substring(0, dbName.indexOf('?'));
            String adminUrl = url.substring(0, url.lastIndexOf('/')) + "/postgres";

            try (Connection conn = DriverManager.getConnection(adminUrl, username, password)) {
                conn.setAutoCommit(true);

                boolean exists = false;
                ResultSet rs = conn.getMetaData().getCatalogs();
                while (rs.next()) {
                    if (dbName.equalsIgnoreCase(rs.getString(1))) {
                        exists = true;
                        break;
                    }
                }

                if (!exists) {
                    conn.createStatement().execute("CREATE DATABASE \"" + dbName + "\"");
                    System.out.println("[DatabaseInit] Banco '" + dbName + "' criado com sucesso.");
                } else {
                    System.out.println("[DatabaseInit] Banco '" + dbName + "' já existe.");
                }
            }
        } catch (Exception e) {
            System.err.println("[DatabaseInit] Não foi possível verificar/criar o banco: " + e.getMessage());
        }
    }
}
