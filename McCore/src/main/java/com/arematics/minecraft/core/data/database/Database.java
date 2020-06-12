package com.arematics.minecraft.core.data.database;

import java.sql.Connection;
import java.sql.SQLException;

public interface Database {

    Connection getConnection();

    void connect() throws SQLException;

    void close() throws SQLException;

    void reconnect() throws SQLException;
}
