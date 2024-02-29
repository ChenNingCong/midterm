package org.example.sql;

import java.sql.ResultSet;

public interface Connector {
    void executeUpdate(String cmd);
    ResultSet executeQuery(String cmd);
}
