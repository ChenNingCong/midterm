package org.example.type;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Collections;
import org.junit.jupiter.api.Test;

class AccountTest {

    @Test
    void validateAccount() {
        assertNotNull(Account.validateAccount("", "", "", "", ""));
        assertNotNull(
                Account.validateAccount(
                        String.join("", Collections.nCopies(300, "a")), "12345", "", "", ""));
        assertNotNull(
                Account.validateAccount(
                        "12345", "12345", String.join("", Collections.nCopies(300, "a")), "", ""));
    }
}
