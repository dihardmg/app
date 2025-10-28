package com.nutech.digitalservice.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class LoginRequest {

    @Email(message = "Parameter email tidak sesuai format")
    @NotBlank(message = "Email tidak boleh kosong")
    private String email;

    @Size(min = 8, message = "Password harus minimal 8 karakter")
    @NotBlank(message = "Password tidak boleh kosong")
    private String password;
}