package org.example.admin;

import static org.junit.jupiter.api.Assertions.*;

import com.google.inject.Guice;
import com.google.inject.Injector;
import org.example.TestDatabaseConnectorModule;
import org.example.type.Account;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class AdminMySQLTransactionManagerTest {
    AdminMySQLTransactionManager manager;

    @BeforeEach
    void setUp() {
        TestDatabaseConnectorModule cm = new TestDatabaseConnectorModule();
        Injector connInject = Guice.createInjector(cm);
        manager = connInject.getInstance(AdminMySQLTransactionManager.class);
    }

    @Test
    void testAllOperation() {
        Account account2 = new Account("UserName2", "88888", "ABC", 100, false);
        assertTrue(manager.checkIfExistId(2));
        assertFalse(manager.checkIfExistId(3));
        assertThrows(
                AssertionError.class,
                () -> {
                    manager.getAccountById(3);
                });
        Account account2Indb = manager.getAccountById(2);
        // nothing is updated here
        assertDoesNotThrow(
                () -> {
                    manager.updateAccount(2, null, account2.pinCode, null, null);
                });
        assertThrows(
                InvalidAccountIdException.class,
                () -> {
                    manager.updateAccount(2, account2.login, null, null, null);
                });
        assertEquals(account2.balance, account2Indb.balance);
        assertEquals(account2.pinCode, account2Indb.pinCode);
        assertEquals(account2.login, account2Indb.login);
        assertEquals(account2.status, account2Indb.status);
        // we recreate an account with identity information
        assertThrows(
                InvalidAccountIdException.class,
                () -> {
                    manager.createAccount(account2);
                });
        Account account3 = new Account("userName3", "12345", "XYZ", 1000, true);

        assertDoesNotThrow(
                () -> {
                    manager.createAccount(account3);
                });
        assertTrue(manager.checkIfExistId(3));
        assertDoesNotThrow(
                () -> {
                    manager.deleteAccountById(3);
                });
        assertThrows(
                InvalidAccountIdException.class,
                () -> {
                    manager.deleteAccountById(3);
                });
        assertThrows(
                InvalidAccountIdException.class,
                () -> {
                    manager.updateAccount(
                            3,
                            account3.login,
                            account3.pinCode,
                            account3.holdersName,
                            account3.status);
                });
        assertDoesNotThrow(
                () -> {
                    manager.updateAccount(
                            2,
                            account3.login,
                            account3.pinCode,
                            account3.holdersName,
                            account3.status);
                });
        assertDoesNotThrow(
                () -> {
                    Account account2Indb2 = manager.getAccountById(2);
                    // the balance is not changed
                    assertEquals(account2.balance, account2Indb2.balance);
                    assertEquals(account3.pinCode, account2Indb2.pinCode);
                    assertEquals(account3.login, account2Indb2.login);
                    assertEquals(account3.status, account2Indb2.status);
                });
    }
}
