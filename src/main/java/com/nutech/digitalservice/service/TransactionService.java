package com.nutech.digitalservice.service;

import com.nutech.digitalservice.dto.ServiceResponse;
import com.nutech.digitalservice.dto.TransactionResponse;
import com.nutech.digitalservice.entity.Balance;
import com.nutech.digitalservice.entity.ServiceEntity;
import com.nutech.digitalservice.entity.Transaction;
import com.nutech.digitalservice.entity.User;
import com.nutech.digitalservice.repository.BalanceRepository;
import com.nutech.digitalservice.repository.ServiceRepository;
import com.nutech.digitalservice.repository.TransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class TransactionService {

    @Autowired
    private ServiceRepository serviceRepository;

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private BalanceRepository balanceRepository;

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
        // Menggunakan raw query dengan prepared statement untuk mencari service
        ServiceEntity service = serviceRepository.findActiveServiceByCodeRaw(serviceCode)
                .orElseThrow(() -> new RuntimeException("Service tidak ditemukan"));

        Balance balance = balanceRepository.findByUser(user)
                .orElseThrow(() -> new RuntimeException("Balance not found for user"));

        if (balance.getBalance().compareTo(service.getServiceTariff()) < 0) {
            throw new RuntimeException("Saldo tidak mencukupi");
        }

        balance.setBalance(balance.getBalance().subtract(service.getServiceTariff()));
        balanceRepository.save(balance);

        Transaction transaction = Transaction.builder()
                .user(user)
                .transactionType(Transaction.TransactionType.PAYMENT)
                .serviceName(service.getServiceName())
                .nominal(service.getServiceTariff())
                .build();

        transaction = transactionRepository.save(transaction);

        return TransactionResponse.builder()
                .transactionCode(transaction.getTransactionCode())
                .serviceName(transaction.getServiceName())
                .nominal(transaction.getNominal().intValue())
                .transactionTime(transaction.getTransactionTime())
                .build();
    }

    /**
     * Method untuk refresh cache services secara manual
     */
    @CacheEvict(value = "services", allEntries = true)
    public void refreshServicesCache() {
        // Cache akan dihapus dan direfresh pada pemanggilan berikutnya
    }

    public List<TransactionResponse> getTransactionHistory(User user) {
        List<Transaction> transactions = transactionRepository.findByUserOrderByTransactionTimeDesc(user);

        return transactions.stream()
                .map(this::convertToTransactionResponse)
                .collect(Collectors.toList());
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
                .transactionCode(transaction.getTransactionCode())
                .serviceName(transaction.getServiceName())
                .nominal(transaction.getNominal().intValue())
                .transactionTime(transaction.getTransactionTime())
                .build();
    }
}