package org.example;

import com.google.inject.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.io.PrintStream;

import static org.junit.jupiter.api.Assertions.*;


class LoginMySQLTransactionManagerTest {
    LoginActor loginActor;
    @BeforeEach
    void setUp() {
        TestDatabaseConnectorModule cm = new TestDatabaseConnectorModule();
        AdminModule am = new AdminModule();
        LoginModule lm = new LoginModule();
        Injector connInject = Guice.createInjector(cm);
        Injector adminInjector = connInject.createChildInjector(am);
        Injector loginInjector = adminInjector.createChildInjector(lm);
        loginActor = loginInjector.getInstance(LoginActor.class);
    }
    @Test
    void tryAdminLogin() {
        setInputOutput(new String[]{"admin", "12345"});
        loginActor.action();
        assertTrue(loginActor.isAdmin());
    }
    @Test
    void tryCustomerLogin() {
        setInputOutput(new String[]{"UserName2", "88888"});
        loginActor.action();
        assertFalse(loginActor.isAdmin());
    }
    @Test
    void tryFailedAdminLogin() {
        OutputStream os = setInputOutput(new String[]{"admin", "00000"});
        loginActor.action();
        assertTrue(os.toString().contains("incorrect"));
        assertNull(loginActor.getSessionId());
    }
    @Test
    void tryFailedCustomerLogin() {
        OutputStream os = setInputOutput(new String[]{"UserName2", "00000"});
        loginActor.action();
        assertTrue(os.toString().contains("Fail"));
        assertNull(loginActor.getSessionId());
    }
    OutputStream setInputOutput(String[] prompt) {
        String userInput = String.join("\n", prompt);
        System.out.println(userInput);
        ByteArrayInputStream bais = new ByteArrayInputStream(userInput.getBytes());
        System.setIn(bais);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PrintStream printStream = new PrintStream(baos);
        System.setOut(printStream);
        return baos;
    }
}