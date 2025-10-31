package com.nutech.digitalservice.service;

import com.nutech.digitalservice.entity.Balance;
import com.nutech.digitalservice.entity.User;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

@Service
@ConditionalOnProperty(name = "app.database.enabled", havingValue = "false")
public class DebugBalanceService {

    public Balance getBalance(User user) {
        // Return mock balance for debugging
        Balance mockBalance = new Balance();
        mockBalance.setId(1L);
        mockBalance.setUser(user);
        mockBalance.setBalance(1000000L); // Mock 1 million balance
        return mockBalance;
    }

    public Balance topUp(User user, Long amount) {
        // Return mock balance after topup for debugging
        Balance mockBalance = new Balance();
        mockBalance.setId(1L);
        mockBalance.setUser(user);
        mockBalance.setBalance(1000000L + amount); // Add amount to mock balance
        return mockBalance;
    }
}