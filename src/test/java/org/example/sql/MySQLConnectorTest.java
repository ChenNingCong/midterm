package org.example.sql;

import static org.junit.jupiter.api.Assertions.*;

import com.google.inject.Guice;
import com.google.inject.Injector;
import java.sql.ResultSet;
import org.example.TestDatabaseConnectorModule;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class MySQLConnectorTest {
    // we didn't test executeQuery here, since it's a bit of difficult to construct a right query
    @Test
    @DisplayName("Test all dataset operation in one function")
    void testAllOperation() {
        TestDatabaseConnectorModule cm = new TestDatabaseConnectorModule();
        Injector connInject = Guice.createInjector(cm);
        MySQLConnector connector = connInject.getInstance(MySQLConnector.class);
        connector.dropAccountTable();
        connector.createAccountTable();
        // a simple query
        ResultSet rs = connector.executeQuery("SELECT 1");
        Integer value =
                assertDoesNotThrow(
                        () -> {
                            rs.next();
                            return rs.getInt(1);
                        });
        assertEquals(value, 1);
        // test exception handling
        assertThrows(
                RuntimeException.class,
                () -> {
                    connector.executeQuery("SELECT 1 FROM non_exist_database");
                });
        assertThrows(
                RuntimeException.class,
                () -> {
                    connector.executeUpdate(
                            "INSERT INTO non_exist_database (CustomerName, ContactName, Address,"
                                + " City, PostalCode, Country)\n"
                                + "VALUES ('Cardinal', 'Tom B. Erichsen', 'Skagen 21', 'Stavanger',"
                                + " '4006', 'Norway');");
                });
        connector.dropAccountTable();
    }
}
