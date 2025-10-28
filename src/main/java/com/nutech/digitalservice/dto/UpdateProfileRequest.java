package com.nutech.digitalservice.dto;

import jakarta.validation.constraints.*;
import lombok.Data;


@Data
public class UpdateProfileRequest {

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