package com.nutech.digitalservice.controller;

import com.nutech.digitalservice.dto.ServiceResponse;
import com.nutech.digitalservice.dto.WebResponse;
import com.nutech.digitalservice.entity.User;
import com.nutech.digitalservice.service.TransactionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1")
@Tag(name = "2. Module Information", description = "API untuk mendapatkan informasi banner dan layanan")
@SecurityRequirement(name = "bearerAuth")
public class InformationController {

    @Autowired
    private TransactionService transactionService;

    @Operation(summary = "Mendapatkan daftar Banner yang tersedia", description = "Mendapatkan daftar Banner yang tersedia N/A")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Request Successfully"),
        @ApiResponse(responseCode = "401", description = "Unauthorized"),
        @ApiResponse(responseCode = "501", description = "Not Implemented - N/A")
    })
    @GetMapping("/banner")
    public ResponseEntity<WebResponse<String>> getBanners(@AuthenticationPrincipal User user) {
        WebResponse<String> response = WebResponse.<String>builder()
                .status(501)
                .message("Feature not available - N/A")
                .data("Banner endpoint not implemented")
                .build();

        return ResponseEntity.status(501).body(response);
    }

    @Operation(summary = "Mendapatkan daftar layanan yang tersedia", description = "Mendapatkan daftar layanan yang tersedia")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Request Successfully"),
        @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    @GetMapping("/services")
    public ResponseEntity<WebResponse<List<ServiceResponse>>> getServices(@AuthenticationPrincipal User user) {
        List<ServiceResponse> services = transactionService.getAllServices();

        WebResponse<List<ServiceResponse>> response = WebResponse.<List<ServiceResponse>>builder()
                .status(0)
                .message("Sukses")
                .data(services)
                .build();

        return ResponseEntity.ok(response);
    }
}