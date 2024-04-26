package org.example.admin;

import com.google.inject.Guice;
import com.google.inject.Injector;
import org.example.TestDatabaseConnectorModule;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AdminMySQLTransactionManagerTest {
    static AdminMySQLTransactionManager manager;
    @BeforeAll
    static void setUp() {
        TestDatabaseConnectorModule cm = new TestDatabaseConnectorModule();
        Injector connInject = Guice.createInjector(cm);
        manager = connInject.getInstance(AdminMySQLTransactionManager.class);
    }
    @Test
    void createAccount() {
    }

    @Test
    void deleteAccountById() {
    }

    @Test
    void getAccountById() {
    }

    @Test
    void checkIfExistId() {
    }

    @Test
    void checkIfExistLogin() {
    }

    @Test
    void updateAccount() {
    }
}