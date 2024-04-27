package org.example;

import com.google.inject.Inject;
import org.example.provider.ConnectorProvider;
import org.example.sql.Connector;

public class LoginMySQLTransactionManager extends LoginTransactionManager {
    @Inject
    public LoginMySQLTransactionManager(@ConnectorProvider Connector _connector) {
        super(_connector);
    }

    @Override
    public Integer tryLogin(String login, String pinCode) {
        return checkLoginAndPincode(login, pinCode);
    }
}
