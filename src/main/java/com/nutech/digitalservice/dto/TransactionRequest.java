package com.nutech.digitalservice.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class TransactionRequest {

    @NotBlank(message = "Service code tidak boleh kosong")
    private String serviceCode;
}