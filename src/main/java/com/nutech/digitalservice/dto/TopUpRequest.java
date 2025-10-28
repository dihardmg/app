package com.nutech.digitalservice.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class TopUpRequest {

    @NotNull(message = "Top up amount tidak boleh kosong")
    @DecimalMin(value = "0.01", message = "Top up amount harus lebih besar dari 0")
    private BigDecimal topUpAmount;
}