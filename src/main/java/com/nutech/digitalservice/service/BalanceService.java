package com.nutech.digitalservice.service;

import com.nutech.digitalservice.entity.Balance;
import com.nutech.digitalservice.entity.Transaction;
import com.nutech.digitalservice.entity.User;
import com.nutech.digitalservice.repository.BalanceRepository;
import com.nutech.digitalservice.repository.TransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
public class BalanceService {

    @Autowired
    private BalanceRepository balanceRepository;

    @Autowired
    private TransactionRepository transactionRepository;

    public Balance getBalance(User user) {
        return balanceRepository.findByUser(user)
                .orElseThrow(() -> new RuntimeException("Balance not found for user"));
    }

    @Transactional
    public Balance topUp(User user, BigDecimal amount) {
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new RuntimeException("Top up amount must be greater than zero");
        }

        Balance balance = balanceRepository.findByUser(user)
                .orElseThrow(() -> new RuntimeException("Balance not found for user"));

        balance.setBalance(balance.getBalance().add(amount));
        balance = balanceRepository.save(balance);

        Transaction transaction = Transaction.builder()
                .user(user)
                .transactionType(Transaction.TransactionType.TOPUP)
                .serviceName("Top Up Balance")
                .nominal(amount)
                .build();

        transactionRepository.save(transaction);

        return balance;
    }
}