package org.example.customer;

import org.example.sql.Connector;
import org.example.type.TransactionManager;

public abstract class CustomerTransactionManager extends TransactionManager {
    public CustomerTransactionManager(Connector _connector) {
        super(_connector);
    }
    public abstract Integer getCash(Integer id);
    public abstract void withdrawCash(Integer id, Integer delta) throws InvalidAmountException;
    public abstract void depositCash(Integer id, Integer delta) throws InvalidAmountException;
}
