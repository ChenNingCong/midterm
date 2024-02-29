package org.example.admin;

import com.google.inject.Inject;
import org.example.provider.AdminTransactionManagerProvider;
import org.example.type.Account;
import org.example.type.Actor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;


public class AdminActor implements Actor {
    private AdminTransactionManager manager;
    @Inject
    public AdminActor(@AdminTransactionManagerProvider AdminTransactionManager _manager) {
        manager = _manager;
    }
    public void action() {
        boolean isExiting = false;
        while (!isExiting) {
            isExiting = prompt();
        }
    }

    public boolean prompt() {
        System.out.println("""
                1----Create New Account
                2----Delete Existing Account
                3----Update Account Information
                4----Search for Account
                5----Exit\s""");
        Scanner scanner = new Scanner(System.in);
        String input = scanner.nextLine();
        boolean isExiting = false;
        switch (input) {
            case "1" -> {
                System.out.println("Enter the account information you want to create");
                List<String> prompts = Arrays.asList("Login", "Pin Code", "Holders Name", "Starting Balance", "Status");
                List<String> inputs = new ArrayList<String>();
                for (String prompt : prompts) {
                    System.out.print(prompt + ":");
                    inputs.add(scanner.nextLine());
                }
                // TODO : sanity check
                Account account = new Account(inputs.get(0), inputs.get(1), inputs.get(2), Integer.parseInt(inputs.get(3)), inputs.get(4) == "Active");
                manager.createAccount(account);
            }
            case "2" -> {
                System.out.println("Enter the account number to which you want to delete:");
                Integer id = Integer.parseInt(scanner.nextLine());
                Account account = manager.getAccountById(id);
                System.out.println(String.format("You wish to delete the account held by %s. If this information is correct, please re-enter the account number:", account.holdersName));
                Integer id2 = Integer.parseInt(scanner.nextLine());
                if (id == id2) {
                    manager.deleteAccountById(account.id);
                    System.out.println("Account Deleted Successfully.");
                } else {
                    System.out.println("Account id doesn't match, aborted.");
                }

            }
            case "3" -> {
                System.out.println("Enter Account number:");
                Integer id = Integer.parseInt(scanner.nextLine());
                if (!manager.checkIfExistId(id)) {
                    System.out.println("Id doesn't exist.");
                } else {
                    System.out.println("Enter the account information you want to update");
                    List<String> prompts = Arrays.asList("Login", "Pin Code", "Holders Name", "Status");
                    List<String> inputs = new ArrayList<String>();
                    for (String prompt : prompts) {
                        System.out.print(prompt + ":");
                        inputs.add(scanner.nextLine());
                    }
                    String login = inputs.get(0).isEmpty() ? null : inputs.get(0);
                    String pinCode = inputs.get(1).isEmpty() ? null : inputs.get(1);
                    String holdersName = inputs.get(2).isEmpty() ? null : inputs.get(2);
                    Boolean status = null;
                    if (inputs.get(3) == "Active") {
                        status = true;
                    } else if (inputs.get(3) == "Disabled") {
                        status = false;
                    } else if (inputs.get(3).isEmpty()) {
                        status = null;
                    } else {
                        System.out.println("Status can only be Active or Disabled.");
                        return isExiting;
                    }
                    manager.updateAccount(id, login, pinCode, holdersName, status);
                }
            }
            case "4" -> {
                System.out.println("Enter Account number:");
                Integer id = Integer.parseInt(scanner.nextLine());
                if (!manager.checkIfExistId(id)) {
                    System.out.println("Id doesn't exist.");
                } else {
                    Account account = manager.getAccountById(id);
                    System.out.println(String.format("""
                            The account information is:
                            Account # %d
                            Holder:%s
                            Balance:%d
                            Status:%s
                            Login:%s
                            Pin Code:%s
                            """, account.id, account.holdersName, account.balance, account.status, account.login, account.pinCode));
                }
            }
            case "5" -> {
                System.out.println("Exist.");
                isExiting = true;
            }
            default -> System.out.println("Invalid input.");
        }
        return isExiting;
    }
}
