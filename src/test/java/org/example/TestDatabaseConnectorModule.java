package org.example;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import org.example.admin.AdminMySQLTransactionManager;
import org.example.provider.ConnectorProvider;
import org.example.provider.MySQLConnectionParamsProvider;
import org.example.sql.Connector;
import org.example.sql.MySQLConnectionParams;
import org.example.sql.MySQLConnector;
import org.example.type.Account;

public class TestDatabaseConnectorModule extends AbstractModule {
    @Provides
    @Singleton
    @MySQLConnectionParamsProvider
    static MySQLConnectionParams provideMySQLConnectionParams() {
        return new MySQLConnectionParams(
                "jdbc:mysql://localhost:3306", "account_test", "chenningcong", "12345678");
    }

    @Provides
    @Singleton
    @ConnectorProvider
    static Connector provideConnector(
            @MySQLConnectionParamsProvider MySQLConnectionParams _params) {
        MySQLConnector conn = new MySQLConnector(_params);
        conn.dropAccountTable();
        conn.createAccountTable();
        System.out.println("Create table successfully");
        AdminMySQLTransactionManager manager = new AdminMySQLTransactionManager(conn);
        try {
            Account account1 = new Account("admin", "12345", "XYZ", 6000, true);
            manager.createAccount(account1);
            Account account2 = new Account("UserName2", "88888", "ABC", 100, false);
            manager.createAccount(account2);
        } catch (Exception e) {
            throw new RuntimeException();
        }
        return conn;
    }
}
