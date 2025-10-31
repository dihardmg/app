package com.nutech.digitalservice.service;

import com.nutech.digitalservice.dto.ServiceResponse;
import com.nutech.digitalservice.dto.TransactionHistoryResponse;
import com.nutech.digitalservice.dto.TransactionResponse;
import com.nutech.digitalservice.entity.ServiceEntity;
import com.nutech.digitalservice.entity.Transaction;
import com.nutech.digitalservice.entity.User;
import com.nutech.digitalservice.repository.BalanceRepositoryCustom;
import com.nutech.digitalservice.repository.ServiceRepository;
import com.nutech.digitalservice.repository.TransactionRepositoryCustom;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@ConditionalOnProperty(name = "app.database.enabled", havingValue = "true", matchIfMissing = true)
public class TransactionService {

    @Autowired
    private ServiceRepository serviceRepository;

    @Autowired
    private TransactionRepositoryCustom transactionRepositoryCustom;

    @Autowired
    private BalanceRepositoryCustom balanceRepositoryCustom;

    @Cacheable(value = "services", key = "'allServices'")
    public List<ServiceResponse> getAllServices() {
        // Menggunakan raw query dengan prepared statement
        List<ServiceEntity> services = serviceRepository.findAllActiveServicesRaw();

        return services.stream()
                .map(this::convertToServiceResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    @CacheEvict(value = "services", allEntries = true) // Clear cache when transaction occurs
    public TransactionResponse makeTransaction(User user, String serviceCode) {
        // Check if service exists using raw query
        Optional<String> serviceNameOpt = transactionRepositoryCustom.getServiceNameByCode(serviceCode);
        if (serviceNameOpt.isEmpty()) {
            throw new RuntimeException("Service ataus Layanan tidak ditemukan");
        }

        Optional<Long> serviceTariffOpt = transactionRepositoryCustom.getServiceTariffByCode(serviceCode);
        if (serviceTariffOpt.isEmpty()) {
            throw new RuntimeException("Service ataus Layanan tidak ditemukan");
        }

        Long serviceTariff = serviceTariffOpt.get();
        String serviceName = serviceNameOpt.get();

        // Check current balance using raw query
        Long currentBalance = transactionRepositoryCustom.getCurrentBalance(user);
        if (currentBalance < serviceTariff) {
            throw new RuntimeException("Saldo tidak mencukupi");
        }

        // Update balance using raw query
        Long newBalance = currentBalance - serviceTariff;
        transactionRepositoryCustom.updateBalanceWithRawQuery(user, newBalance);

        // Generate invoice number
        String invoiceNumber = transactionRepositoryCustom.generateInvoiceNumber();

        // Insert transaction using raw query
        Transaction transaction = transactionRepositoryCustom.insertTransactionWithRawQuery(
                user,
                invoiceNumber,
                "PAYMENT",
                serviceCode,
                serviceName,
                serviceTariff
        );

        return convertToTransactionResponse(transaction);
    }

    /**
     * Method untuk refresh cache services secara manual
     */
    @CacheEvict(value = "services", allEntries = true)
    public void refreshServicesCache() {
        // Cache akan dihapus dan direfresh pada pemanggilan berikutnya
    }

    public TransactionHistoryResponse getTransactionHistory(User user, Integer limit, Integer offset) {
        List<Transaction> transactions = transactionRepositoryCustom.findTransactionHistoryByUser(user, limit, offset);

        List<TransactionResponse> transactionResponses = transactions.stream()
                .map(this::convertToTransactionResponse)
                .collect(Collectors.toList());

        return TransactionHistoryResponse.builder()
                .limit(limit != null ? limit.toString() : "null")
                .offset(offset != null ? offset.toString() : "0")
                .records(transactionResponses)
                .build();
    }

    private ServiceResponse convertToServiceResponse(ServiceEntity service) {
        return ServiceResponse.builder()
                .serviceCode(service.getServiceCode())
                .serviceName(service.getServiceName())
                .serviceIcon(service.getServiceIcon())
                .serviceTariff(service.getServiceTariff().intValue())
                .build();
    }

    private TransactionResponse convertToTransactionResponse(Transaction transaction) {
        return TransactionResponse.builder()
                .invoiceNumber(transaction.getInvoiceNumber())
                .description(transaction.getDescription())
                .transactionType(transaction.getTransactionType().toString())
                .totalAmount(transaction.getTotalAmount())
                .createdOn(transaction.getCreatedOn())
                .build();
    }
}