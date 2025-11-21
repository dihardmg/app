package com.nutech.digitalservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.time.Instant;
import java.util.Locale;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TransactionResponse {
    private String invoiceNumber;
    private String description;
    private String transactionType;
    private String totalAmount;
    private Instant createdOn;

    // Static method to create TransactionResponse from Transaction entity
    public static TransactionResponse fromEntity(
            String invoiceNumber,
            String description,
            String transactionType,
            BigDecimal totalAmount,
            Instant createdOn) {

        if (totalAmount == null) {
            return TransactionResponse.builder()
                    .invoiceNumber(invoiceNumber)
                    .description(description)
                    .transactionType(transactionType)
                    .totalAmount("0.00")
                    .createdOn(createdOn)
                    .build();
        }

        DecimalFormat df = new DecimalFormat("#0.00", new DecimalFormatSymbols(Locale.US));
        return TransactionResponse.builder()
                .invoiceNumber(invoiceNumber)
                .description(description)
                .transactionType(transactionType)
                .totalAmount(df.format(totalAmount.setScale(2, RoundingMode.HALF_UP)))
                .createdOn(createdOn)
                .build();
    }
}