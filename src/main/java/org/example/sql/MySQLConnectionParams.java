package org.example.sql;

public class MySQLConnectionParams {
    String http;
    String database;
    String user;
    String password;

    public MySQLConnectionParams(String http, String database, String user, String password) {
        this.http = http;
        this.database = database;
        this.user = user;
        this.password = password;
    }
}
