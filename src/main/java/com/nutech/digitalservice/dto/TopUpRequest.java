package com.nutech.digitalservice.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class TopUpRequest {

    @NotNull(message = "Top up amount tidak boleh kosong")
    @Min(value = 1, message = "Paramter amount hanya boleh angka dan tidak boleh lebih kecil dari 0")
    private Long top_up_amount;
}