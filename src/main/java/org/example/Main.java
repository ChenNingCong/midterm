package org.example;


import com.google.inject.*;
import org.example.admin.AdminActor;
import org.example.admin.AdminMySQLTransactionManager;
import org.example.admin.AdminTransactionManager;
import org.example.customer.CustomerActor;
import org.example.customer.CustomerMySQLTransactionManager;
import org.example.customer.CustomerTransactionManager;
import org.example.provider.*;
import org.example.sql.Connector;
import org.example.sql.MySQLConnectionParams;
import org.example.sql.MySQLConnector;
import org.example.type.Account;

import static com.google.inject.name.Names.named;


class ConnectorModule extends AbstractModule {
    @Provides
    @Singleton
    @MySQLConnectionParamsProvider
    static MySQLConnectionParams provideMySQLConnectionParams() {
        return new MySQLConnectionParams("jdbc:mysql://localhost:3306", "account", "chenningcong", "12345678");
    }

    @Provides
    @Singleton
    @ConnectorProvider
    static Connector provideConnector(@MySQLConnectionParamsProvider MySQLConnectionParams _params) {
        System.out.println("Initializing database and connection");
        MySQLConnector conn = new MySQLConnector(_params);
        conn.dropAccountTable();
        conn = new MySQLConnector(_params);
        AdminMySQLTransactionManager manager = new AdminMySQLTransactionManager(conn);
        Account account1 = new Account(
                "admin",
                "12345",
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
        return conn;
    }
}


class LoginModule extends AbstractModule {
    @Provides
    @Singleton
    @LoginTransactionManagerProvider
    LoginTransactionManager provideloginTransactionManager(@ConnectorProvider Connector connector) {
        return new LoginMySQLTransactionManager(connector);
    }
}


class AdminModule extends AbstractModule {
    @Provides
    @Singleton
    @AdminTransactionManagerProvider
    AdminTransactionManager provideAdminTransactionManager(@ConnectorProvider Connector connector) {
        return new AdminMySQLTransactionManager(connector);
    }
}

class CustomerModule extends AbstractModule {
    Integer id;
    public  CustomerModule(Integer _id) {
        id = _id;
    }
    @Provides
    @CustomerTransactionManagerProvider
    static CustomerTransactionManager provideCustomerTransactionManager(@ConnectorProvider Connector connector) {
        return new CustomerMySQLTransactionManager(connector);
    }

    @Override
    protected void configure() {
        bind(Integer.class).annotatedWith(named("id")).toInstance(id);
    }
}


public class Main {

    public static void main(String[] args) {
        ConnectorModule cm = new ConnectorModule();
        AdminModule am = new AdminModule();
        LoginModule lm = new LoginModule();
        Injector connInject = Guice.createInjector(cm);
        Injector adminInjector = connInject.createChildInjector(am);
        Injector loginInjector = adminInjector.createChildInjector(lm);
        // TODO : must happen before login manager, otherwise loginManager will construct the value by itself
        AdminActor adminActor = adminInjector.getInstance(AdminActor.class);
        LoginActor loginActor = loginInjector.getInstance(LoginActor.class);
        loginActor.action();
        Integer id = loginActor.getSessionId();
        if (id == null) {
        }
        else {
            if (loginActor.isAdmin()) {
                adminActor.action();
            }
            else {
                Injector customerInjector = connInject.createChildInjector(new CustomerModule(id));
                CustomerActor customerActor = customerInjector.getInstance(CustomerActor.class);
                customerActor.action();
            }
        }
    }
}