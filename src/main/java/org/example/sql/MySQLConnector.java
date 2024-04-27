package org.example.sql;

import com.google.inject.Inject;
import java.sql.*;
import javax.sql.rowset.CachedRowSet;
import javax.sql.rowset.RowSetProvider;
import org.example.provider.MySQLConnectionParamsProvider;

public class MySQLConnector implements Connector {
    MySQLConnectionParams params;

    @Inject
    public MySQLConnector(@MySQLConnectionParamsProvider MySQLConnectionParams _params) {
        params = _params;
    }

    private Connection createConnection() throws SQLException {
        return DriverManager.getConnection(
                params.http + '/' + params.database, params.user, params.password);
    }

    public void createAccountTable() {
        String cmd =
                """
                CREATE TABLE IF NOT EXISTS `accounts` (
                  `id` int NOT NULL AUTO_INCREMENT,
                  `login` varchar(255) NOT NULL UNIQUE,
                  `pinCode` char(5) NOT NULL,
                  `holdersName` varchar(255) NOT NULL,
                  `balance` int unsigned NOT NULL,
                  `status` boolean NOT NULL,
                  PRIMARY KEY (`Id`)
                ) ENGINE=InnoDB AUTO_INCREMENT=0 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
                """;
        executeUpdate(cmd);
    }

    public void dropAccountTable() {
        String cmd = "DROP TABLE IF EXISTS `accounts`;";
        executeUpdate(cmd);
    }

    @Override
    public void executeUpdate(String cmd) {
        Connection con = null;
        Statement stmt = null;
        try {
            con = createConnection();
            stmt = con.createStatement();
            stmt.executeUpdate(cmd);
        } catch (SQLException e) {
            System.out.println(e);
            throw new RuntimeException(e);
        } finally {
            try {
                if (con != null) {
                    con.close();
                }
            } catch (SQLException e) {
                System.out.println(e);
                throw new RuntimeException(e);
            } finally {
                try {
                    if (stmt != null) {
                        stmt.close();
                    }
                } catch (SQLException e) {
                    System.out.println(e);
                    throw new RuntimeException(e);
                }
            }
        }
    }

    @Override
    public ResultSet executeQuery(String cmd) {
        Connection con = null;
        Statement stmt = null;
        ResultSet rs;
        try {
            con = createConnection();
            stmt = con.createStatement();
            rs = stmt.executeQuery(cmd);
            CachedRowSet crs = RowSetProvider.newFactory().createCachedRowSet();
            crs.populate(rs);
            rs = crs;
        } catch (SQLException e) {
            System.out.println(e);
            throw new RuntimeException(e);
        } finally {
            try {
                if (con != null) {
                    con.close();
                }
            } catch (SQLException e) {
                System.out.println(e);
                throw new RuntimeException(e);
            } finally {
                try {
                    if (stmt != null) {
                        stmt.close();
                    }
                } catch (SQLException e) {
                    System.out.println(e);
                    throw new RuntimeException(e);
                }
            }
        }
        return rs;
    }
}
