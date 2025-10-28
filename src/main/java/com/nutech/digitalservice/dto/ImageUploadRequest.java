package com.nutech.digitalservice.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
@Schema(description = "Request untuk upload profile image")
public class ImageUploadRequest {

    @NotNull(message = "File tidak boleh kosong")
    @Schema(
        description = "File gambar profile (JPEG/PNG only, max 5MB)",
        required = true,
        format = "binary",
        allowableValues = {"image/jpeg", "image/png"}
    )
    private MultipartFile file;
}