package org.example.customer;

import static org.junit.jupiter.api.Assertions.*;

import com.google.inject.Guice;
import com.google.inject.Injector;
import org.example.TestDatabaseConnectorModule;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class CustomerMySQLTransactionManagerTest {
    static CustomerMySQLTransactionManager manager;

    @BeforeAll
    static void setUp() {
        TestDatabaseConnectorModule cm = new TestDatabaseConnectorModule();
        Injector connInject = Guice.createInjector(cm);
        manager = connInject.getInstance(CustomerMySQLTransactionManager.class);
    }

    @Test
    @DisplayName("Test all customer action in one function")
    void getCash() {
        // 1 is the admin account, so we should start from 2 here
        Integer userId = 2;
        Integer startAmount = 100;
        Integer delta = 10;
        assertEquals(manager.getCash(userId), startAmount);
        assertEquals(manager.getAccountById(userId).balance, startAmount);
        assertDoesNotThrow(
                () -> {
                    manager.withdrawCash(userId, delta);
                });
        assertDoesNotThrow(
                () -> {
                    manager.withdrawCash(userId, delta);
                });
        assertEquals(manager.getAccountById(userId).balance, startAmount - 2 * delta);
        assertThrows(
                InvalidAmountException.class,
                () -> {
                    manager.withdrawCash(userId, startAmount);
                });
        assertEquals(manager.getAccountById(userId).balance, startAmount - 2 * delta);
        assertDoesNotThrow(
                () -> {
                    manager.depositCash(userId, delta);
                });
        assertEquals(manager.getCash(userId), startAmount - delta);
    }
}
