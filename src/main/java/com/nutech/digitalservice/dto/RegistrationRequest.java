package com.nutech.digitalservice.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class RegistrationRequest {

    @Email(message = "Parameter email tidak sesuai format")
    @NotBlank(message = "Email tidak boleh kosong")
    private String email;

    @Size(min = 8, message = "Password harus minimal 8 karakter")
    @NotBlank(message = "Password tidak boleh kosong")
    private String password;

    @Size(min = 2, message = "First name harus minimal 2 karakter")
    @NotBlank(message = "First name tidak boleh kosong")
    @Pattern(regexp = "^[a-zA-Z]*$",
            message = "First name hanya boleh mengandung huruf alfabet (A-Z, a-z).")
    private String firstName;

    @Size(min = 2, message = "Last name harus minimal 2 karakter")
    @NotBlank(message = "Last name tidak boleh kosong")
    @Pattern(regexp = "^[a-zA-Z]*$",
            message = "Last name hanya boleh mengandung huruf alfabet (A-Z, a-z).")
    private String lastName;
}