package com.nutech.digitalservice.service;

import com.nutech.digitalservice.entity.Balance;
import com.nutech.digitalservice.entity.Transaction;
import com.nutech.digitalservice.entity.User;
import com.nutech.digitalservice.repository.BalanceRepository;
import com.nutech.digitalservice.repository.BalanceRepositoryCustom;
import com.nutech.digitalservice.repository.TransactionRepository;
import com.nutech.digitalservice.repository.TransactionRepositoryCustom;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Optional;

@Service
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
        Balance newBalance = balanceRepositoryCustom.insertBalanceWithRawQuery(user, BigDecimal.ZERO);
        return newBalance;
    }

    @Transactional
    public Balance topUp(User user, BigDecimal amount) {
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new RuntimeException("Parameter amount hanya boleh angka dan tidak boleh lebih kecil dari 0");
        }

        BigDecimal currentBalance = transactionRepositoryCustom.getCurrentBalance(user);
        BigDecimal roundedAmount = amount.setScale(2, RoundingMode.HALF_UP);
        BigDecimal newBalance = currentBalance.add(roundedAmount).setScale(2, RoundingMode.HALF_UP);

        // Debug logging
        System.out.println("DEBUG TOPUP:");
        System.out.println("  Amount requested: " + amount);
        System.out.println("  Current balance: " + currentBalance);
        System.out.println("  Rounded amount: " + roundedAmount);
        System.out.println("  New balance: " + newBalance);

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
                roundedAmount
        );

        // Return updated balance
        Balance updatedBalance = balanceRepositoryCustom.findByUserWithRawQuery(user)
                .orElseThrow(() -> new RuntimeException("Balance not found for user after update"));

        System.out.println("DEBUG - Final balance after topup: " + updatedBalance.getBalance());
        return updatedBalance;
    }
}