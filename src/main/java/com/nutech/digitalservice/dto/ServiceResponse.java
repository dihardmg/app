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
public class ServiceResponse {
    private String serviceCode;
    private String serviceName;
    private String serviceIcon;
    private String serviceTariff;

    // Static method to create ServiceResponse from BigDecimal
    public static ServiceResponse fromBigDecimal(
            String serviceCode,
            String serviceName,
            String serviceIcon,
            BigDecimal serviceTariff) {

        if (serviceTariff == null) {
            return ServiceResponse.builder()
                    .serviceCode(serviceCode)
                    .serviceName(serviceName)
                    .serviceIcon(serviceIcon)
                    .serviceTariff("0.00")
                    .build();
        }

        // Manual formatting to ensure 2 decimal places
        String formattedTariff = String.format(Locale.US, "%.2f", serviceTariff.setScale(2, RoundingMode.HALF_UP));

        return ServiceResponse.builder()
                .serviceCode(serviceCode)
                .serviceName(serviceName)
                .serviceIcon(serviceIcon)
                .serviceTariff(formattedTariff)
                .build();
    }
}