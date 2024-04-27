package org.example.admin;

import static org.example.type.Account.validateAccount;

import com.google.inject.Inject;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.example.provider.AdminTransactionManagerProvider;
import org.example.type.Account;
import org.example.type.Actor;
import org.example.util.Scanner;

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
        System.out.println(
                """
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
                List<String> prompts =
                        Arrays.asList(
                                "Login", "Pin Code", "Holders Name", "Starting Balance", "Status");
                List<String> inputs = new ArrayList<String>();
                for (String prompt : prompts) {
                    System.out.print(prompt + ":");
                    inputs.add(scanner.nextLine());
                }
                // TODO : sanity check
                if (inputs.get(1).length() != 5) {
                    System.out.println("pin code must be a 5 number digits.");
                    break;
                }
                String err =
                        validateAccount(
                                inputs.get(0),
                                inputs.get(1),
                                inputs.get(2),
                                inputs.get(3),
                                inputs.get(4));
                if (err != null) {
                    System.out.println(err);
                    break;
                }
                Account account =
                        new Account(
                                inputs.get(0),
                                inputs.get(1),
                                inputs.get(2),
                                Integer.parseInt(inputs.get(3)),
                                inputs.get(4).equals("Active"));
                try {
                    manager.createAccount(account);
                } catch (InvalidAccountIdException e) {
                    System.out.println(e);
                }
            }
            case "2" -> {
                System.out.println("Enter the account number to which you want to delete:");
                Integer id;
                Integer id2;
                try {
                    id = scanner.parsePositiveNumber();
                } catch (NumberFormatException e) {
                    System.out.println("Not a positive number");
                    break;
                }
                if (!manager.checkIfExistId(id)) {
                    System.out.println(String.format("Account %s does not exist", id));
                    break;
                }
                Account account = manager.getAccountById(id);
                System.out.println(
                        String.format(
                                "You wish to delete the account held by %s. If this information is"
                                        + " correct, please re-enter the account number:",
                                account.holdersName));
                try {
                    id2 = scanner.parsePositiveNumber();
                } catch (NumberFormatException e) {
                    System.out.println("Not a positive number");
                    break;
                }
                if (id.equals(id2)) {
                    try {
                        manager.deleteAccountById(account.id);
                    } catch (InvalidAccountIdException e) {
                        // unreachable...we have already check before
                        System.out.println(e);
                        break;
                    }
                    System.out.println("Account deleted successfully.");
                } else {
                    System.out.println("Account id doesn't match, aborted.");
                }
            }
            case "3" -> {
                System.out.println("Enter Account number:");
                Integer id;
                try {
                    id = scanner.parsePositiveNumber();
                } catch (NumberFormatException e) {
                    System.out.println("Not a positive number");
                    break;
                }
                if (!manager.checkIfExistId(id)) {
                    System.out.println("Id doesn't exist.");
                } else {
                    System.out.println("Enter the account information you want to update");
                    List<String> prompts =
                            Arrays.asList("Login", "Pin Code", "Holders Name", "Status");
                    List<String> inputs = new ArrayList<String>();
                    for (String prompt : prompts) {
                        System.out.print(prompt + ":");
                        inputs.add(scanner.nextLine());
                    }
                    String login = inputs.get(0).isEmpty() ? null : inputs.get(0);
                    String pinCode = inputs.get(1).isEmpty() ? null : inputs.get(1);
                    String holdersName = inputs.get(2).isEmpty() ? null : inputs.get(2);
                    Boolean status;
                    if (inputs.get(3).equals("Active")) {
                        status = true;
                    } else if (inputs.get(3).equals("Disabled")) {
                        status = false;
                    } else {
                        System.out.println("Status can only be Active or Disabled.");
                        return false;
                    }
                    try {
                        manager.updateAccount(id, login, pinCode, holdersName, status);
                    } catch (InvalidAccountIdException e) {
                        System.out.println(e);
                    }
                }
            }
            case "4" -> {
                System.out.println("Enter Account number:");
                Integer id = Integer.parseInt(scanner.nextLine());
                if (!manager.checkIfExistId(id)) {
                    System.out.println("Id doesn't exist.");
                } else {
                    Account account = manager.getAccountById(id);
                    System.out.println(
                            String.format(
                                    """
                            The account information is:
                            Account # %d
                            Holder:%s
                            Balance:%d
                            Status:%s
                            Login:%s
                            Pin Code:%s
                            """,
                                    account.id,
                                    account.holdersName,
                                    account.balance,
                                    account.status,
                                    account.login,
                                    account.pinCode));
                }
            }
            case "5" -> {
                System.out.println("Exit.");
                isExiting = true;
            }
            default -> System.out.println("Invalid input.");
        }
        return isExiting;
    }
}
