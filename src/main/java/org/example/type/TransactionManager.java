package org.example.type;

import org.example.provider.ConnectorProvider;
import org.example.sql.Connector;
import org.example.util.Convert2Account;

import java.sql.ResultSet;
import java.sql.SQLException;

public abstract class TransactionManager {
    protected Connector connector;
    public TransactionManager(@ConnectorProvider Connector _connector) {
        connector = _connector;
    }
    public Account getAccountById(Integer id) {
        assert id >= 0;
        String cmd = String.format("""
                SELECT * FROM accounts WHERE id=%d
                """, id);
        ResultSet rs = connector.executeQuery(cmd);
        try {
            Account[] accounts = Convert2Account.convert(rs);
            assert accounts.length == 1;
            return accounts[0];
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public Integer checkLoginAndPincode(String login, String pinCode) {
        String cmd = String.format("""
                SELECT * FROM accounts WHERE login="%s" and pinCode="%s"
                """, login, pinCode);
        ResultSet rs = connector.executeQuery(cmd);
        try {
            Account[] accounts = Convert2Account.convert(rs);
            assert accounts.length <= 1;
            if (accounts.length == 0) {
                return null;
            }
            else {
                return accounts[0].id;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
