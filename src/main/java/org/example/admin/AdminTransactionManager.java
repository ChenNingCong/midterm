package org.example.admin;

import org.example.sql.Connector;
import org.example.type.Account;
import org.example.type.TransactionManager;

public abstract class AdminTransactionManager extends TransactionManager {
    public AdminTransactionManager(Connector _connector) {
        super(_connector);
    }

    public abstract void createAccount(Account account) throws InvalidAccountIdException;

    public abstract void deleteAccountById(Integer id) throws InvalidAccountIdException;

    public abstract Account getAccountById(Integer id);

    public abstract void updateAccount(
            Integer id, String login, String pinCode, String holderNames, Boolean status)
            throws InvalidAccountIdException;

    public abstract boolean checkIfExistId(Integer id);

    public abstract boolean checkIfExistLogin(String login);
}
