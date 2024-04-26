package org.example.admin;

import com.google.inject.*;
import org.example.TestDatabaseConnectorModule;
import org.example.provider.AdminTransactionManagerProvider;
import org.example.provider.ConnectorProvider;
import org.example.sql.Connector;
import org.example.type.Account;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.io.PrintStream;

import static org.junit.jupiter.api.Assertions.*;

class AdminModule extends AbstractModule {
    @Provides
    @Singleton
    @AdminTransactionManagerProvider
    AdminTransactionManager provideAdminTransactionManager(@ConnectorProvider Connector connector) {
        return new AdminMySQLTransactionManager(connector);
    }
}

class AdminActorTest {
    AdminActor adminActor;
    AdminMySQLTransactionManager manager;

    @BeforeEach
    void setUp() {
        TestDatabaseConnectorModule cm = new TestDatabaseConnectorModule();
        Injector connInject = Guice.createInjector(cm);
        AdminModule am = new AdminModule();
        Injector adminInjector = connInject.createChildInjector(am);
        adminActor = adminInjector.getInstance(AdminActor.class);
        manager = connInject.getInstance(AdminMySQLTransactionManager.class);
    }
    @Test
    void createInvalidAccount() {
        OutputStream os = null;
        // bad pin code
        os = setInputOutput(new String[]{"1", "UserName2",
                "888",
                "ABC",
                "100",
                "Active", "5"});
        adminActor.prompt();
        assertTrue(getOutputString(os).contains("5"));

        os = setInputOutput(new String[]{"1", "UserName2",
                "88888",
                "ABC",
                "100",
                "Active", "5"});
        adminActor.prompt();
        assertTrue(getOutputString(os).contains("InvalidAccountIdException"));
    }

    @Test
    void createAccount() {
        OutputStream os = null;
        // bad pin code
        os = setInputOutput(new String[]{"1", "UserName3",
                "88888",
                "ABC",
                "100",
                "Active", "5"});
        adminActor.prompt();
        Account account = assertDoesNotThrow(()-> manager.getAccountById(3));
        assertEquals(account.login, "UserName3");
    }


    @Test
    void deleteAccount() {
        OutputStream os = setInputOutput(new String[]{"2", "2", "2", "5"});
        adminActor.prompt();
        assertTrue(getOutputString(os).contains("succ"));
    }

    @Test
    void deleteAccountFailedConfirm() {
        OutputStream os = setInputOutput(new String[]{"2", "2", "3", "5"});
        adminActor.prompt();
        assertTrue(getOutputString(os).contains("match"));
    }

    @Test
    void deleteAccountInvalidId() {
        OutputStream os = setInputOutput(new String[]{"2", "-1", "2", "2", "-1", "5"});
        adminActor.prompt();
        assertTrue(getOutputString(os).contains("positive"));
    }

    @Test
    void deleteNonexistentAccount() {
        OutputStream os = setInputOutput(new String[]{"2", "3", "3", "5"});
        adminActor.prompt();
        assertTrue(getOutputString(os).contains("not"));
    }
    @Test
    void updateInvalidAccount() {
        OutputStream os = null;
        os = setInputOutput(new String[]{"3", "3", "5"});
        adminActor.prompt();
        assertTrue(getOutputString(os).contains("exist"));

        os = setInputOutput(new String[]{"3", "-1", "5"});
        adminActor.prompt();
        assertTrue(getOutputString(os).contains("positive"));
    }

    @Test
    void updateAccount() {
        PrintStream stdout = System.out;
        OutputStream os = null;
        // bad pin code
        os = setInputOutput(new String[]{"3", "2",
                "UserName3",
                "77777",
                "ABCDEF",
                "Active", "5"});
        adminActor.prompt();
        Account account = assertDoesNotThrow(()-> manager.getAccountById(2));
        System.setOut(stdout);
        System.out.println(os.toString());

        assertEquals(account.login, "UserName3");
        assertEquals(account.status, true);
        assertEquals(account.pinCode, "77777");


        os = setInputOutput(new String[]{"3", "2",
                "UserName4",
                "77777",
                "ABCDEF",
                "Disabled", "5"});
        adminActor.prompt();
        account = assertDoesNotThrow(()-> manager.getAccountById(2));
        System.setOut(stdout);
        System.out.println(os.toString());

        assertEquals(account.login, "UserName4");
        assertEquals(account.status, false);
        assertEquals(account.pinCode, "77777");
    }


    @Test
    void testAction() {
        OutputStream os = setInputOutput(new String[]{"5"});
        adminActor.action();
    }

    @Test
    void showAccount() {
        OutputStream os = setInputOutput(new String[]{"4", "2", "5"});
        adminActor.prompt();
        os = setInputOutput(new String[]{"4", "3", "5"});
        adminActor.prompt();
    }

    OutputStream setInputOutput(String[] prompt) {
        String userInput = String.join("\n", prompt);
        ByteArrayInputStream bais = new ByteArrayInputStream(userInput.getBytes());
        System.setIn(bais);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PrintStream printStream = new PrintStream(baos);
        System.setOut(printStream);
        return baos;
    }

    String getOutputString(OutputStream os) {
        return os.toString();
    }
}