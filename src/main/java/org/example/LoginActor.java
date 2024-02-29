package org.example;

import com.google.inject.Inject;
import org.example.provider.LoginTransactionManagerProvider;
import org.example.type.Actor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;


public class LoginActor implements Actor {
    private LoginTransactionManager manager;
    private Integer id;
    @Inject
    public LoginActor(@LoginTransactionManagerProvider LoginTransactionManager _manager) {
        manager = _manager;
    }
    public Integer getSessionId() {
        return id;
    }
    public boolean isAdmin() {
        return id == 1;
    }
    @Override
    public void action() {
        Scanner scanner = new Scanner(System.in);
        List<String> prompts = Arrays.asList("Enter Login", "Enter Pin Code");
        List<String> inputs = new ArrayList<String>();
        for (String prompt : prompts) {
            System.out.print(prompt + ":");
            inputs.add(scanner.nextLine());
        }
        String login = inputs.get(0);
        String pinCode = inputs.get(1);
        id = manager.tryLogin(login, pinCode);
        if (id == null) {
            if (login.equals("admin")) {
                System.out.println("Password is incorrect.");
            }
            else {
                System.out.println("Fail to login. Please check your login and pin code!");
            }
        }
        else {
            if (login.equals("admin")) {
                System.out.println("Login as administrator.");
            }
            else {
                System.out.printf("Login as customer %d%n", id);
            }
        }
    }
}
