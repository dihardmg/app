package com.nutech.digitalservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TransactionResponse {
    private String invoiceNumber;
    private String description;
    private String transactionType;
    private Long totalAmount;
    private Instant createdOn;
}