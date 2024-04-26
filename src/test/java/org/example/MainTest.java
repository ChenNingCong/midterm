package org.example;

import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.io.PrintStream;

import static org.junit.jupiter.api.Assertions.*;

class MainTest {
    @Test
    void main() {
        setInputOutput(new String[]{"admin", "12345", "5"});
        try {
            Main.main(new String[]{});
        } catch (Exception e) {

        }
    }
    OutputStream setInputOutput(String[] prompt) {
        String userInput = String.join("\n", prompt);
        System.out.println(userInput);
        ByteArrayInputStream bais = new ByteArrayInputStream(userInput.getBytes());
        System.setIn(bais);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PrintStream printStream = new PrintStream(baos);
        System.setOut(printStream);
        return baos;
    }
}