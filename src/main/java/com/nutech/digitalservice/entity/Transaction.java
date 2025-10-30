package com.nutech.digitalservice.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Entity
@Table(name = "transactions", indexes = {
    @Index(name = "idx_transactions_user_id", columnList = "user_id"),
    @Index(name = "idx_transactions_created_on", columnList = "created_on"),
    @Index(name = "idx_transactions_invoice_number", columnList = "invoice_number", unique = true),
    @Index(name = "idx_transactions_type", columnList = "transaction_type"),
    @Index(name = "idx_transactions_service_code", columnList = "service_code")
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

    @Column(name = "invoice_number", unique = true, nullable = false, length = 50)
    private String invoiceNumber;

    @Enumerated(EnumType.STRING)
    @Column(name = "transaction_type", nullable = false, length = 20)
    private TransactionType transactionType;

    @Column(name = "service_code", length = 50)
    private String serviceCode;

    @Column(name = "description", length = 200)
    private String description;

    @Column(name = "total_amount", nullable = false)
    private Long totalAmount;

    @Column(name = "created_on", nullable = false)
    private Instant createdOn;

    @PrePersist
    protected void onCreate() {
        createdOn = Instant.now();
    }

    public enum TransactionType {
        TOPUP,
        PAYMENT
    }
}