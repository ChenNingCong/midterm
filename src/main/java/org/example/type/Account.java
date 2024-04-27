package org.example.type;

public class Account {
    public Integer id;
    public String login;
    public String pinCode;
    public String holdersName;
    public Integer balance;
    public Boolean status;

    public Account(
            Integer id,
            String login,
            String pinCode,
            String holdersName,
            Integer balance,
            Boolean status) {
        this.id = id;
        this.login = login;
        this.pinCode = pinCode;
        this.holdersName = holdersName;
        this.balance = balance;
        this.status = status;
        sanityParams();
    }

    public Account(
            String login, String pinCode, String holdersName, Integer balance, Boolean status) {
        this.id = -1;
        this.login = login;
        this.pinCode = pinCode;
        this.holdersName = holdersName;
        this.balance = balance;
        this.status = status;
        sanityParams();
    }

    private void sanityParams() {
        assert pinCode.length() == 5;
        assert !login.isEmpty() && login.length() <= 255;
        // holder name can be empty
        assert !holdersName.isEmpty() && holdersName.length() <= 255;
    }

    public static String validateAccount(
            String login, String pinCode, String holdersName, String balance, String status) {
        if (!(pinCode.length() == 5)) {
            return "pin code must be a 5 number digits.";
        }
        if (login.length() > 255) {
            return "Login name can have at most 255 characters";
        }
        if (holdersName.length() > 255) {
            return "Holders name can have at most 255 characters";
        }
        if (!(status.equals("Active") || !status.equals("Disabled"))) {
            return "Invalid status";
        }
        return null;
    }

    @Override
    public String toString() {
        return String.format(
                "Account(login=%s, pinCode=%s, holdersName=%s, balance=%d, status=%s)",
                login, pinCode, holdersName, balance, status ? "Active" : "Disabled");
    }
}
