package com.nutech.digitalservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Locale;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BalanceResponse {
    private String balance;

    // Static method to create BalanceResponse from BigDecimal
    public static BalanceResponse fromBigDecimal(BigDecimal balance) {
        if (balance == null) {
            return BalanceResponse.builder().balance("0.00").build();
        }
        BigDecimal roundedBalance = balance.setScale(2, RoundingMode.HALF_UP);

        // Manual formatting to ensure 2 decimal places
        String formattedBalance = String.format(Locale.US, "%.2f", roundedBalance);

        System.out.println("DEBUG: Original balance: " + balance);
        System.out.println("DEBUG: Rounded balance: " + roundedBalance);
        System.out.println("DEBUG: Formatted balance: " + formattedBalance);

        return BalanceResponse.builder()
                .balance(formattedBalance)
                .build();
    }
}