package com.nutech.digitalservice.service;

import com.nutech.digitalservice.dto.ServiceResponse;
import com.nutech.digitalservice.dto.TransactionHistoryResponse;
import com.nutech.digitalservice.dto.TransactionResponse;
import com.nutech.digitalservice.entity.User;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@ConditionalOnProperty(name = "app.database.enabled", havingValue = "false")
public class DebugTransactionService {

    public List<ServiceResponse> getAllServices() {
        // Return mock services for debugging
        List<ServiceResponse> services = new ArrayList<>();
        services.add(ServiceResponse.builder()
                .serviceCode("PAJAK")
                .serviceName("Bayar Pajak")
                .serviceIcon("/images/pajak.png")
                .serviceTariff(50000)
                .build());
        services.add(ServiceResponse.builder()
                .serviceCode("LISTRIK")
                .serviceName("Token Listrik")
                .serviceIcon("/images/listrik.png")
                .serviceTariff(100000)
                .build());
        return services;
    }

    public TransactionResponse makeTransaction(User user, String serviceCode) {
        // Return mock transaction for debugging
        return TransactionResponse.builder()
                .invoiceNumber("INV" + System.currentTimeMillis())
                .description("Mock transaction for " + serviceCode)
                .transactionType("PAYMENT")
                .totalAmount(50000L)
                .createdOn(LocalDateTime.now())
                .build();
    }

    public TransactionHistoryResponse getTransactionHistory(User user, Integer limit, Integer offset) {
        // Return mock transaction history for debugging
        List<TransactionResponse> transactions = new ArrayList<>();
        transactions.add(TransactionResponse.builder()
                .invoiceNumber("INV001")
                .description("Mock Transaction 1")
                .transactionType("PAYMENT")
                .totalAmount(50000L)
                .createdOn(LocalDateTime.now())
                .build());

        return TransactionHistoryResponse.builder()
                .limit(limit != null ? limit.toString() : "5")
                .offset(offset != null ? offset.toString() : "0")
                .records(transactions)
                .build();
    }

    public void refreshServicesCache() {
        // Mock method - does nothing
    }
}