package org.example.customer;

import com.google.inject.Inject;
import org.example.provider.ConnectorProvider;
import org.example.sql.Connector;

public class CustomerMySQLTransactionManager extends CustomerTransactionManager {
    @Inject
    public CustomerMySQLTransactionManager(@ConnectorProvider Connector _connector) {
        super(_connector);
    }

    public Integer getCash(Integer id) {
        return getAccountById(id).balance;
    }
    public void withdrawCash(Integer id, Integer delta) {
        assert delta > 0;
        updateBalance(id, -delta);
    }
    public void depositCash(Integer id, Integer delta) {
        assert delta > 0;
        updateBalance(id, delta);
    }
    private void updateBalance(Integer id, Integer delta) {
        if (!((getAccountById(id).balance + delta) >= 0)){
            throw new RuntimeException("Balance will be negative!");
        }
        else {
            String cmd = String.format("""
                UPDATE accounts
                SET balance = balance + (%d)
                WHERE id=%d;
                """, delta, id);
            connector.executeUpdate(cmd);
        }
    }
}
