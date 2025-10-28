package com.nutech.digitalservice.controller;

import com.nutech.digitalservice.dto.BalanceResponse;
import com.nutech.digitalservice.dto.TopUpRequest;
import com.nutech.digitalservice.dto.TransactionRequest;
import com.nutech.digitalservice.dto.TransactionResponse;
import com.nutech.digitalservice.dto.WebResponse;
import com.nutech.digitalservice.entity.Balance;
import com.nutech.digitalservice.entity.User;
import com.nutech.digitalservice.service.BalanceService;
import com.nutech.digitalservice.service.TransactionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1")
@Tag(name = "3. Module Transaction", description = "API untuk balance check, top up, dan transaksi")
@SecurityRequirement(name = "bearerAuth")
public class TransactionModuleController {

    @Autowired
    private BalanceService balanceService;

    @Autowired
    private TransactionService transactionService;

    @Operation(summary = "Cek saldo user", description = "Cek saldo user")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Request Successfully"),
        @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    @GetMapping("/balance")
    public ResponseEntity<WebResponse<BalanceResponse>> getBalance(@AuthenticationPrincipal User user) {
        Balance balance = balanceService.getBalance(user);

        BalanceResponse balanceResponse = BalanceResponse.builder()
                .balance(balance.getBalance())
                .build();

        WebResponse<BalanceResponse> response = WebResponse.<BalanceResponse>builder()
                .status(0)
                .message("Get balance berhasil")
                .data(balanceResponse)
                .build();

        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Top up saldo", description = "Top up saldo")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Request Successfully"),
        @ApiResponse(responseCode = "400", description = "Bad Request"),
        @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    @PostMapping("/topup")
    public ResponseEntity<WebResponse<BalanceResponse>> topUp(
            @AuthenticationPrincipal User user,
            @Valid @RequestBody TopUpRequest request) {

        Balance balance = balanceService.topUp(user, request.getTopUpAmount());

        BalanceResponse balanceResponse = BalanceResponse.builder()
                .balance(balance.getBalance())
                .build();

        WebResponse<BalanceResponse> response = WebResponse.<BalanceResponse>builder()
                .status(0)
                .message("Top Up Balance berhasil")
                .data(balanceResponse)
                .build();

        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Melakukan transaksi pembayaran", description = "Melakukan transaksi pembayaran")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Request Successfully"),
        @ApiResponse(responseCode = "400", description = "Bad Request"),
        @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    @PostMapping("/transaction")
    public ResponseEntity<WebResponse<TransactionResponse>> makeTransaction(
            @AuthenticationPrincipal User user,
            @Valid @RequestBody TransactionRequest request) {

        TransactionResponse transactionResponse = transactionService.makeTransaction(user, request.getServiceCode());

        WebResponse<TransactionResponse> response = WebResponse.<TransactionResponse>builder()
                .status(0)
                .message("Transaksi berhasil")
                .data(transactionResponse)
                .build();

        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Mendapatkan riwayat transaksi", description = "Mendapatkan riwayat transaksi")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Request Successfully"),
        @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    @GetMapping("/transaction/history")
    public ResponseEntity<WebResponse<List<TransactionResponse>>> getTransactionHistory(
            @AuthenticationPrincipal User user) {

        List<TransactionResponse> transactions = transactionService.getTransactionHistory(user);

        WebResponse<List<TransactionResponse>> response = WebResponse.<List<TransactionResponse>>builder()
                .status(0)
                .message("Get History Berhasil")
                .data(transactions)
                .build();

        return ResponseEntity.ok(response);
    }

    }