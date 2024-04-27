package org.example.util;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import org.example.type.Account;

public class Convert2Account {
    public static Account[] convert(ResultSet rs) throws SQLException {
        List<Account> account = new ArrayList<>();
        while (rs.next()) {
            account.add(
                    new Account(
                            rs.getInt(1),
                            rs.getString(2),
                            rs.getString(3),
                            rs.getString(4),
                            rs.getInt(5),
                            rs.getBoolean(6)));
        }
        return account.toArray(new Account[0]);
    }
}
