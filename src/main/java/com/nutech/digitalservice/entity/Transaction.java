package com.nutech.digitalservice.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "transactions", indexes = {
    @Index(name = "idx_transactions_user_id", columnList = "user_id"),
    @Index(name = "idx_transactions_created_at", columnList = "created_at"),
    @Index(name = "idx_transactions_transaction_time", columnList = "transaction_time"),
    @Index(name = "idx_transactions_code", columnList = "transaction_code", unique = true),
    @Index(name = "idx_transactions_type", columnList = "transaction_type")
})
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "transaction_code", unique = true, nullable = false, length = 50)
    private String transactionCode;

    @Enumerated(EnumType.STRING)
    @Column(name = "transaction_type", nullable = false, length = 20)
    private TransactionType transactionType;

    @Column(name = "service_name", length = 100)
    private String serviceName;

    @Column(name = "nominal", precision = 15, scale = 2)
    private BigDecimal nominal;

    @Column(name = "transaction_time", nullable = false)
    private LocalDateTime transactionTime;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        if (transactionTime == null) {
            transactionTime = LocalDateTime.now();
        }
        if (transactionCode == null) {
            transactionCode = generateTransactionCode();
        }
    }

    private String generateTransactionCode() {
        return "TRX" + System.currentTimeMillis();
    }

    public enum TransactionType {
        TOPUP,
        PAYMENT
    }
}