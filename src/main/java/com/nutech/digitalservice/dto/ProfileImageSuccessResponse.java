package com.nutech.digitalservice.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "Response untuk upload profile image yang berhasil")
public class ProfileImageSuccessResponse {

    @Schema(example = "0", description = "Status code untuk success")
    private int status;

    @Schema(example = "Update Profile Image berhasil", description = "Pesan sukses")
    private String message;

    @Schema(
        example = "/uploads/profile-images/123e4567-e89b-12d3-a456-426614174000.jpg",
        description = "URL path dari profile image yang diupload"
    )
    private String data;
}