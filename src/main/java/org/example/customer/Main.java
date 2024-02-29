package org.example.customer;

import com.google.inject.*;
import org.example.admin.AdminMySQLTransactionManager;
import org.example.admin.AdminTransactionManager;
import org.example.provider.CustomerTransactionManagerProvider;
import org.example.provider.ConnectorProvider;
import org.example.provider.MySQLConnectionParamsProvider;
import org.example.provider.MySQLConnectorProvider;
import org.example.sql.Connector;
import org.example.sql.MySQLConnectionParams;
import org.example.sql.MySQLConnector;
import org.example.type.Account;

import static com.google.inject.name.Names.named;


class CustomerIdProvider implements Provider<Integer> {
    public Integer get() {
        // it would even be better if you could use singletons here.
        return 2;
    }
}

class TestModule extends AbstractModule {
    @Provides
    @MySQLConnectionParamsProvider
    static MySQLConnectionParams provideMySQLConnectionParams() {
        return new MySQLConnectionParams("jdbc:mysql://localhost:3306", "account", "chenningcong", "12345678");
    }

    @Provides
    @ConnectorProvider
    static Connector provideConnector(@MySQLConnectionParamsProvider MySQLConnectionParams _params) {
        MySQLConnector conn = new MySQLConnector(_params);
        conn.dropAccountTable();
        conn = new MySQLConnector(_params);
        return conn;
    }

    @Provides
    @CustomerTransactionManagerProvider
    static CustomerTransactionManager provideCustomerTransactionManager(@MySQLConnectorProvider MySQLConnector connector) {
        return new CustomerMySQLTransactionManager(connector);
    }
    @Override
    protected void configure() {
        bind(Integer.class).annotatedWith(named("id")).toProvider(CustomerIdProvider.class);
    }
}


class TestModule2 extends AbstractModule {
    @Provides
    @MySQLConnectionParamsProvider
    static MySQLConnectionParams provideMySQLConnectionParams() {
        return new MySQLConnectionParams("jdbc:mysql://localhost:3306", "account", "chenningcong", "12345678");
    }

    @Provides
    @ConnectorProvider
    static Connector provideConnector(@MySQLConnectionParamsProvider MySQLConnectionParams _params) {
        MySQLConnector conn = new MySQLConnector(_params);
        conn.dropAccountTable();
        conn = new MySQLConnector(_params);
        return conn;
    }

    @Provides
    @CustomerTransactionManagerProvider
    static CustomerTransactionManager provideAdminTransactionManager(@ConnectorProvider Connector connector) {
        AdminTransactionManager manager = new AdminMySQLTransactionManager(connector);
        Account account1 = new Account(
                "UserName1",
                "99999",
                "XYZ",
                6000,
                true
        );
        manager.createAccount(account1);
        Account account2 = new Account(
                "UserName2",
                "88888",
                "ABC",
                100,
                false
        );
        manager.createAccount(account2);
        return new CustomerMySQLTransactionManager(connector);
    }
    @Override
    protected void configure() {
        bind(Integer.class).annotatedWith(named("id")).toProvider(CustomerIdProvider.class);
    }
}

public class Main {
    public static void main(String[] args) {
        Injector injector1 = Guice.createInjector(
                new TestModule2());
        Account account = new Account(
                "UserName",
                "99999",
                "XYZ",
                6000,
                true
        );
        CustomerActor manager = injector1.getInstance(CustomerActor.class);
        manager.action();
    }

    public static void main1(String[] args) {
        Injector injector1 = Guice.createInjector(
                new TestModule());
        Account account = new Account(
                "UserName",
                "99999",
                "XYZ",
                6000,
                true
        );
        AdminMySQLTransactionManager manager = injector1.getInstance(AdminMySQLTransactionManager.class);
        manager.createAccount(account);
        manager.deleteAccountById(1);
        manager.createAccount(account);
        System.out.println(manager.getAccountById(2).toString());
        account = new Account(
                "x",
                "88888",
                "abc",
                6000,
                true
        );
        manager.updateAccount(2, account.login, account.pinCode, account.holdersName, account.status);
        manager.updateAccount(2, "ewew", account.pinCode, account.holdersName, account.status);
        System.out.println(manager.getAccountById(2).toString());
    }
}