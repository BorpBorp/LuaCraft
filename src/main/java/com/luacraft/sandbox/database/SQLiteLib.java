package com.luacraft.sandbox.database;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

public final class SQLiteLib {
    private static SQLiteLib instance;
    private HikariDataSource dataSource;

    private SQLiteLib(String path) {
        HikariConfig config = new HikariConfig();

        config.setJdbcUrl("jdbc:sqlite:" + path);
        config.setPoolName("LuaCraftPool");
        config.setMaximumPoolSize(10);

        config.addDataSourceProperty("cachePrepStmts", "true");
        config.setConnectionInitSql("PRAGMA journal_mode=WAL; PRAGMA synchronous=NORMAL;");

        this.dataSource = new HikariDataSource(config);
        setupTable();
    }

    public static void initialize(String path) {
        if (instance == null) {
            instance = new SQLiteLib(path);
        }
    }

    public static SQLiteLib getInstance() {
        return instance;
    }

    public HikariDataSource getDataSource() {
        return dataSource;
    }

    private void setupTable() {
        String sql = "CREATE TABLE IF NOT EXISTS lua_data (" +
                     "uuid TEXT, " +
                     "key TEXT, " +
                     "value TEXT, " +
                     "PRIMARY KEY (uuid, key))";
        try (Connection conn = dataSource.getConnection(); Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void close() {
        if (dataSource != null && !dataSource.isClosed()) {
            dataSource.close();
        }
    }
}
