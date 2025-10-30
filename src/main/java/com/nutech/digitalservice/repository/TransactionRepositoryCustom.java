package com.nutech.digitalservice.repository;

import com.nutech.digitalservice.entity.Transaction;
import com.nutech.digitalservice.entity.User;

import java.util.List;
import java.util.Optional;

public interface TransactionRepositoryCustom {
    List<Transaction> findTransactionHistoryByUser(User user, Integer limit, Integer offset);
    Transaction insertTransactionWithRawQuery(User user, String invoiceNumber, String transactionType,
                                             String serviceCode, String description, Long totalAmount);
    String generateInvoiceNumber();
    Long getCurrentBalance(User user);
    void updateBalanceWithRawQuery(User user, Long newBalance);
    Optional<String> getServiceNameByCode(String serviceCode);
    Optional<Long> getServiceTariffByCode(String serviceCode);
}