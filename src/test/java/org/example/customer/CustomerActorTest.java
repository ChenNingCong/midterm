package org.example.customer;

import static com.google.inject.name.Names.named;
import static org.junit.jupiter.api.Assertions.*;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Provides;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import org.example.TestDatabaseConnectorModule;
import org.example.provider.ConnectorProvider;
import org.example.provider.CustomerTransactionManagerProvider;
import org.example.sql.Connector;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

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

class CustomerActorTest {
    CustomerActor customerActor;
    CustomerMySQLTransactionManager manager;

    @BeforeEach
    void setUp() {
        TestDatabaseConnectorModule cm = new TestDatabaseConnectorModule();
        Injector connInject = Guice.createInjector(cm);
        CustomerModule am = new CustomerModule(2);
        Injector adminInjector = connInject.createChildInjector(am);
        customerActor = adminInjector.getInstance(CustomerActor.class);
        manager = connInject.getInstance(CustomerMySQLTransactionManager.class);
    }

    @Test
    void saveAndWithdrawMoney() {
        setInputOutput(new String[] {"1", "10", "4"});
        customerActor.prompt();
        assertEquals(manager.getCash(2), 90);
        setInputOutput(new String[] {"2", "10", "4"});
        customerActor.prompt();
        assertEquals(manager.getCash(2), 100);
    }

    @Test
    void showBalance() {
        setInputOutput(new String[] {"3", "4"});
        customerActor.prompt();
    }

    @Test
    void exit() {
        setInputOutput(new String[] {"4"});
        customerActor.action();
        setInputOutput(new String[] {"4"});
        customerActor.prompt();
        setInputOutput(new String[] {"5"});
        customerActor.prompt();
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
