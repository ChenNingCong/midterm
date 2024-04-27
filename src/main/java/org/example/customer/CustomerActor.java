package org.example.customer;

import com.google.inject.Inject;
import com.google.inject.name.Named;
import java.util.Scanner;
import org.example.provider.CustomerTransactionManagerProvider;
import org.example.type.Actor;

public class CustomerActor implements Actor {
    Integer id;
    private CustomerTransactionManager manager;

    @Inject
    public CustomerActor(
            @CustomerTransactionManagerProvider CustomerTransactionManager _manager,
            @Named("id") Integer _id) {
        id = _id;
        manager = _manager;
    }

    @Override
    public void action() {
        boolean isExiting = false;
        while (!isExiting) {
            isExiting = prompt();
        }
    }

    public boolean prompt() {
        boolean isExiting = false;
        System.out.println(
                """
                1----Withdraw Cash
                2----Deposit Cash
                3----Display Balance
                4----Exit""");
        Scanner scanner = new Scanner(System.in);
        String input = scanner.nextLine();
        switch (input) {
            case "1", "2" -> {
                boolean isWithdrawal = input.equals("1");
                if (isWithdrawal) {
                    System.out.println("Enter the withdrawal amount:");
                } else {
                    System.out.println("Enter the cash amount to deposit:");
                }
                String amountString = scanner.nextLine();
                try {
                    Integer amount;
                    try {
                        amount = Integer.parseInt(amountString);
                    } catch (NumberFormatException e) {
                        throw new InvalidAmountException("Input amount must be positive number.");
                    }
                    if (amount <= 0) {
                        System.out.println("Withdrawal amount must be positive number.");
                    } else {
                        if (isWithdrawal) {
                            manager.withdrawCash(id, amount);
                            System.out.println("Cash Successfully Withdrawn.");
                        } else {
                            manager.depositCash(id, amount);
                            System.out.println("Cash Deposited Successfully.");
                        }
                        Integer balance = manager.getCash(id);
                        System.out.println(
                                String.format(
                                        """
                                Account #%d
                                Date: %s
                                %s:%d
                                Balance: %d
                                """,
                                        id,
                                        getFormatCurrentDate(),
                                        isWithdrawal ? "Withdrawn" : "Deposited",
                                        amount,
                                        balance));
                    }
                } catch (InvalidAmountException e) {
                    System.out.println(e.reason);
                }
            }
            case "3" -> {
                Integer balance = manager.getCash(id);
                System.out.println(
                        String.format(
                                """
                        Account #%d
                        Date: %s
                        Balance: %d
                        """,
                                id, getFormatCurrentDate(), balance));
            }
            case "4" -> {
                System.out.println("Exit.");
                isExiting = true;
            }
            default -> System.out.println("Invalid option. Input must be from 1 to 4.");
        }
        return isExiting;
    }
}
