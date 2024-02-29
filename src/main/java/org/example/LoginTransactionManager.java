package org.example;

import org.example.sql.Connector;
import org.example.type.TransactionManager;

public abstract class LoginTransactionManager extends TransactionManager {
    public LoginTransactionManager(Connector _connector) {
        super(_connector);
    }
    public abstract Integer tryLogin(String login, String pinCode);
}
