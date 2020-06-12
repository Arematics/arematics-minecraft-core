package com.arematics.minecraft.core.data.database;

import org.apache.commons.codec.binary.Base64;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class Maria implements Database{

    private final String prefix;
    private Connection connection;
    private final Properties properties = new Properties();


    public Maria(File file, String prefix) throws IOException {
        this.prefix = prefix;
        properties.load(new FileInputStream(file));
    }

    @Override
    public Connection getConnection() {
        return this.connection;
    }

    @Override
    public void connect() throws SQLException {
        try {
            Class.forName("org.mariadb.jdbc.Driver");
            String address = "jdbc:mariadb://"
                    + properties.getProperty(prefix + "-host") + ":"
                    + properties.getProperty(prefix + "-port") + "/"
                    + properties.getProperty(prefix + "-database");
            this.connection = DriverManager.getConnection(address,
                    properties.getProperty(prefix + "-user"),
                    new String(Base64.decodeBase64(properties.getProperty(prefix + "-password"))));
            System.out.println("Connection is up");
        }catch (ClassNotFoundException cnfe){
            cnfe.printStackTrace();
        }
    }

    @Override
    public void close() throws SQLException {
        if(this.connection != null && !this.connection.isClosed())
            this.connection.close();
    }

    @Override
    public void reconnect() throws SQLException {
        close();
        try{
            connect();
        }catch (Exception e){
            connect();
        }
    }
}
