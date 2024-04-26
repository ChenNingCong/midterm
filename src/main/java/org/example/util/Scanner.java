package org.example.util;

import java.io.InputStream;

public class Scanner {
    java.util.Scanner scanner;
    public Scanner(InputStream source) {
        scanner = new java.util.Scanner(source);
    }

    public Integer parsePositiveNumber() throws NumberFormatException {
        Integer id = Integer.parseInt(scanner.nextLine());
        if (id < 0) {
            throw new NumberFormatException();
        }
        return id;
    }
    public String nextLine() {
        return scanner.nextLine();
    }
}