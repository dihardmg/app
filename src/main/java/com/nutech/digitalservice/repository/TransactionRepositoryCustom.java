package com.nutech.digitalservice.repository;

import com.nutech.digitalservice.entity.Transaction;
import com.nutech.digitalservice.entity.User;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public interface TransactionRepositoryCustom {
    List<Transaction> findTransactionHistoryByUser(User user, Integer limit, Integer offset);
    Transaction insertTransactionWithRawQuery(User user, String invoiceNumber, String transactionType,
                                             String serviceCode, String description, BigDecimal totalAmount);
    String generateInvoiceNumber();
    BigDecimal getCurrentBalance(User user);
    void updateBalanceWithRawQuery(User user, BigDecimal newBalance);
    Optional<String> getServiceNameByCode(String serviceCode);
    Optional<BigDecimal> getServiceTariffByCode(String serviceCode);
}