package org.example.admin;

import com.google.inject.Inject;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import org.example.provider.ConnectorProvider;
import org.example.sql.Connector;
import org.example.type.Account;
import org.example.util.Convert2Account;

public class AdminMySQLTransactionManager extends AdminTransactionManager {
    @Inject
    public AdminMySQLTransactionManager(@ConnectorProvider Connector _connector) {
        super(_connector);
    }

    public void createAccount(Account account) throws InvalidAccountIdException {
        /*
        * `id` int NOT NULL AUTO_INCREMENT,
                  `login` varchar(255) NOT NULL,
                  `pinCode` varchar(5) NOT NULL,
                  `holdersName` varchar(255) NOT NULL,
                  `balance` int unsigned NOT NULL,
                  `status` boolean NOT NULL,
                  PRIMARY KEY (`Id`)*/
        if (checkIfExistLogin(account.login)) {
            throw new InvalidAccountIdException(
                    "Login is used by another customer, use another name!");
        }
        String cmd =
                String.format(
                        """
                INSERT INTO accounts (login, pinCode, holdersName, balance, status)
                VALUES ('%s', '%s', '%s', %d, %s);
                """,
                        account.login,
                        account.pinCode,
                        account.holdersName,
                        account.balance,
                        account.status ? "TRUE" : "FALSE");
        connector.executeUpdate(cmd);
    }

    public void deleteAccountById(Integer id) throws InvalidAccountIdException {
        assert id >= 0;
        if (!checkIfExistId(id)) {
            throw new InvalidAccountIdException("Account doesn't exists!.");
        }
        // TODO : we should actually directly perform deletion
        // and see whether the id exists
        String cmd =
                String.format(
                        """
                DELETE FROM accounts WHERE id=%d
                """,
                        id);
        connector.executeUpdate(cmd);
    }

    public Account getAccountById(Integer id) {
        assert id >= 0;
        String cmd =
                String.format(
                        """
                SELECT * FROM accounts WHERE id=%d
                """,
                        id);
        ResultSet rs = connector.executeQuery(cmd);
        try {
            Account[] accounts = Convert2Account.convert(rs);
            assert accounts.length == 1;
            return accounts[0];
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public boolean checkIfExistId(Integer id) {
        assert id >= 0;
        String cmd =
                String.format(
                        """
                SELECT * FROM accounts WHERE id=%d
                """,
                        id);
        ResultSet rs = connector.executeQuery(cmd);
        try {
            Account[] accounts = Convert2Account.convert(rs);
            assert accounts.length <= 1;
            return accounts.length == 1;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public boolean checkIfExistLogin(String login) {
        String cmd =
                String.format(
                        """
                SELECT * FROM accounts WHERE login="%s"
                """,
                        login);
        ResultSet rs = connector.executeQuery(cmd);
        try {
            Account[] accounts = Convert2Account.convert(rs);
            assert accounts.length <= 1;
            return accounts.length == 1;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void updateAccount(
            Integer id, String login, String pinCode, String holderNames, Boolean status)
            throws InvalidAccountIdException {

        List<String> params = new ArrayList<>();
        if (!checkIfExistId(id)) {
            throw new InvalidAccountIdException("User doesn't exist");
        }
        if (login != null) {
            // TODO : use a function to test this
            assert !login.isEmpty() && login.length() <= 255;
            params.add(String.format("login = \"%s\"", login));
            // TODO :
            if (checkIfExistLogin(login)) {
                throw new InvalidAccountIdException("Login id is not unique! Please use a new id");
            }
        }
        if (holderNames != null) {
            // TODO : use a function to test this
            assert !holderNames.isEmpty() && holderNames.length() <= 255;
            params.add(String.format("holdersName = \"%s\"", holderNames));
        }
        if (pinCode != null) {
            // TODO : use a function to test this
            assert pinCode.length() == 5;
            params.add(String.format(Locale.US, "pinCode = \"%s\"", pinCode));
        }
        if (status != null) {
            String statusString = null;
            if (status) {
                statusString = "1";
            } else {
                statusString = "0";
            }
            params.add(String.format(Locale.US, "status = \"%s\"", statusString));
        }
        String p = String.join(", ", params);
        String cmd =
                String.format(
                        """
                UPDATE accounts
                SET %s
                WHERE id=%d;
                """,
                        p, id);
        connector.executeUpdate(cmd);
    }
}
