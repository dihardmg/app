package com.nutech.digitalservice.service;

import com.nutech.digitalservice.entity.Balance;
import com.nutech.digitalservice.entity.Transaction;
import com.nutech.digitalservice.entity.User;
import com.nutech.digitalservice.repository.BalanceRepository;
import com.nutech.digitalservice.repository.BalanceRepositoryCustom;
import com.nutech.digitalservice.repository.TransactionRepository;
import com.nutech.digitalservice.repository.TransactionRepositoryCustom;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@ConditionalOnProperty(name = "app.database.enabled", havingValue = "true", matchIfMissing = true)
public class BalanceService {

    @Autowired
    private BalanceRepository balanceRepository;

    @Autowired
    private BalanceRepositoryCustom balanceRepositoryCustom;

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private TransactionRepositoryCustom transactionRepositoryCustom;

    public Balance getBalance(User user) {
        // Try to find existing balance
        Optional<Balance> existingBalance = balanceRepositoryCustom.findByUserWithRawQuery(user);

        if (existingBalance.isPresent()) {
            return existingBalance.get();
        }

        // If no balance record exists, create one with 0 balance
        Balance newBalance = balanceRepositoryCustom.insertBalanceWithRawQuery(user, 0L);
        return newBalance;
    }

    @Transactional
    public Balance topUp(User user, Long amount) {
        if (amount <= 0) {
            throw new RuntimeException("Paramter amount hanya boleh angka dan tidak boleh lebih kecil dari 0");
        }

        Long currentBalance = transactionRepositoryCustom.getCurrentBalance(user);
        Long newBalance = currentBalance + amount;

        // Update balance using raw query
        transactionRepositoryCustom.updateBalanceWithRawQuery(user, newBalance);

        // Insert transaction record using raw query
        String invoiceNumber = transactionRepositoryCustom.generateInvoiceNumber();
        transactionRepositoryCustom.insertTransactionWithRawQuery(
                user,
                invoiceNumber,
                "TOPUP",
                null,
                "Top Up balance",
                amount
        );

        // Return updated balance
        return balanceRepositoryCustom.findByUserWithRawQuery(user)
                .orElseThrow(() -> new RuntimeException("Balance not found for user after update"));
    }
}