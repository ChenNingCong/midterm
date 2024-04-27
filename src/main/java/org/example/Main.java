package org.example;

import static com.google.inject.name.Names.named;

import com.google.inject.*;
import java.sql.ResultSet;
import java.sql.SQLException;
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

class ConnectorModule extends AbstractModule {
    @Provides
    @Singleton
    @MySQLConnectionParamsProvider
    static MySQLConnectionParams provideMySQLConnectionParams() {
        return new MySQLConnectionParams(
                "jdbc:mysql://localhost:3306", "account", "chenningcong", "12345678");
    }

    @Provides
    @Singleton
    @ConnectorProvider
    static Connector provideConnector(
            @MySQLConnectionParamsProvider MySQLConnectionParams _params) {
        MySQLConnector conn = new MySQLConnector(_params);
        AdminMySQLTransactionManager manager = new AdminMySQLTransactionManager(conn);
        Boolean isInit = false;
        try {
            ResultSet rs = conn.executeQuery("SHOW TABLES LIKE 'accounts';");
            while (rs.next()) {
                isInit = true;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        if (!isInit) {
            System.out.println("Initializing database.");
            conn.createAccountTable();
            try {
                Account account1 = new Account("admin", "12345", "XYZ", 6000, true);
                manager.createAccount(account1);
                Account account2 = new Account("UserName2", "88888", "ABC", 100, false);
                manager.createAccount(account2);
            } catch (Exception e) {
                throw new RuntimeException();
            }
        }
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

    public CustomerModule(Integer _id) {
        id = _id;
    }

    @Provides
    @CustomerTransactionManagerProvider
    static CustomerTransactionManager provideCustomerTransactionManager(
            @ConnectorProvider Connector connector) {
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
        // TODO : must happen before login manager, otherwise loginManager will construct the value
        // by itself
        AdminActor adminActor = adminInjector.getInstance(AdminActor.class);
        LoginActor loginActor = loginInjector.getInstance(LoginActor.class);
        loginActor.action();
        Integer id = loginActor.getSessionId();
        if (id == null) {
        } else {
            if (loginActor.isAdmin()) {
                adminActor.action();
            } else {
                Injector customerInjector = connInject.createChildInjector(new CustomerModule(id));
                CustomerActor customerActor = customerInjector.getInstance(CustomerActor.class);
                customerActor.action();
            }
        }
    }
}
