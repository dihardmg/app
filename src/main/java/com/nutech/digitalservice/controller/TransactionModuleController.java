package com.nutech.digitalservice.controller;

import com.nutech.digitalservice.dto.BalanceResponse;
import com.nutech.digitalservice.dto.TopUpRequest;
import com.nutech.digitalservice.dto.TransactionHistoryResponse;
import com.nutech.digitalservice.dto.TransactionRequest;
import com.nutech.digitalservice.dto.TransactionResponse;
import com.nutech.digitalservice.dto.WebResponse;
import com.nutech.digitalservice.entity.Balance;
import com.nutech.digitalservice.entity.User;
import com.nutech.digitalservice.service.BalanceService;
import com.nutech.digitalservice.service.TransactionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
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

    @Operation(summary = "Get Balance", description = "Digunakan untuk mendapatkan informasi balance / saldo terakhir dari User")
    @ApiResponses(value = {
        // @ApiResponse(responseCode = "200", description = "Get Balance / Saldo Berhasil"),
            @ApiResponse(responseCode = "200", description = "Request Successfully",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = WebResponse.class),
                            examples = @ExampleObject(
                                    value = """
                                            {
                                               "status": 0,
                                               "message": "Sukses",
                                               "data": {
                                                 "balance": 20010
                                               }
                                             }
                                            """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "401",
                     description = "Unauthorized",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = WebResponse.class),
                            examples = @ExampleObject(
                                    //  name = "Invalid Credentials",
                                    value = """
                                            {
                                              "status": 108,
                                              "message": "Token tidak tidak valid atau kadaluwarsa",
                                              "data": null
                                            }
                                            """
                            )
                    )
            )
    })
    @GetMapping("/balance")
    public ResponseEntity<WebResponse<BalanceResponse>> getBalance(@AuthenticationPrincipal User user) {
        Balance balance = balanceService.getBalance(user);

        BalanceResponse balanceResponse = BalanceResponse.builder()
                .balance(balance.getBalance())
                .build();

        WebResponse<BalanceResponse> response = WebResponse.<BalanceResponse>builder()
                .status(0)
                .message("Sukses")
                .data(balanceResponse)
                .build();

        return ResponseEntity.ok()
                .body(response);
    }

    @Operation(summary = "Top Up", description = "Digunakan untuk melakukan top up balance / saldo dari User")
    @io.swagger.v3.oas.annotations.security.SecurityRequirements()
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "Topup ",
            content = @Content(
                    mediaType = "application/json",
                    examples = @ExampleObject(
                            //name = "Login Example",
                            value = """
                                    {
                                       "top_up_amount": 150000
                                     }
                                    """
                    )
            )
    )
    @ApiResponses(value = {
        //@ApiResponse(responseCode = "200", description = "Request Successfully 200"),
            @ApiResponse(responseCode = "200", description = "Request Successfully",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = WebResponse.class),
                            examples = @ExampleObject(
                                    value = """
                                            {
                                                "status": 0,
                                                "message": "Top Up Balance berhasil",
                                                "data": {
                                                  "balance": 150000
                                                }
                                              }
                                            """
                            )
                    )
            ),
            @ApiResponse(responseCode = "400", description = "Bad Request 400"),
            @ApiResponse(
                    responseCode = "400",
                    description = "BAD_REQUEST",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = WebResponse.class),
                            examples = @ExampleObject(
                                    //  name = "Invalid Credentials",
                                    value = """
                                            {
                                               "status": 102,
                                               "message": "Paramter amount hanya boleh angka dan tidak boleh lebih kecil dari 0",
                                               "data": null
                                             }
                                            """
                            )
                    )
            ),
        @ApiResponse(responseCode = "401", description = "Bad Request 400"),
            @ApiResponse(
                    responseCode = "401",
                    description = "Unauthorized",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = WebResponse.class),
                            examples = @ExampleObject(
                                    //  name = "Invalid Credentials",
                                    value = """
                                            {
                                              "status": 108,
                                              "message": "Token tidak tidak valid atau kadaluwarsa",
                                              "data": null
                                            }
                                            """
                            )
                    )
            )
    })
    @PostMapping("/topup")
    public ResponseEntity<WebResponse<BalanceResponse>> topUp(
            @AuthenticationPrincipal User user,
            @Valid @RequestBody TopUpRequest request) {

        Balance balance = balanceService.topUp(user, request.getTop_up_amount());

        BalanceResponse balanceResponse = BalanceResponse.builder()
                .balance(request.getTop_up_amount())
                .build();

        WebResponse<BalanceResponse> response = WebResponse.<BalanceResponse>builder()
                .status(0)
                .message("Top Up Balance berhasil")
                .data(balanceResponse)
                .build();

        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Transaction", description = "Digunakan untuk melakukan transaksi dari services / layanan yang tersedia")
    @io.swagger.v3.oas.annotations.security.SecurityRequirements()
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "Transasi ",
            content = @Content(
                    mediaType = "application/json",
                    examples = @ExampleObject(
                            //name = "Login Example",
                            value = """
                                    {
                                         "serviceCode": "PLN"
                                       }
                                    """
                    )
            )
    )
    @ApiResponses(value = {
        //@ApiResponse(responseCode = "200", description = "Transaksi Berhasil 200"),
            @ApiResponse(
                    responseCode = "200",
                    description = "Transaksi Berhasil 200",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = WebResponse.class),
                            examples = @ExampleObject(
                                    //  name = "Invalid Credentials",
                                    value = """
                                            {
                                                "status": 0,
                                                "message": "Transaksi berhasil",
                                                "data": {
                                                  "invoiceNumber": "INV20251030-000003",
                                                  "description": "Pulsa Indosat",
                                                  "transactionType": "PAYMENT",
                                                  "totalAmount": 40000,
                                                  "createdOn": "2025-10-30T21:38:33.237312Z"
                                                }
                                              }
                                            """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "BAD_REQUEST",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = WebResponse.class),
                            examples = @ExampleObject(
                                    //  name = "Invalid Credentials",
                                    value = """
                                            {
                                               "status": 102,
                                               "message": "Service status Layanan tidak ditemukan",
                                               "data": null
                                             }
                                            """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "BAD_REQUEST",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = WebResponse.class),
                            examples = @ExampleObject(
                                    //  name = "Invalid Credentials",
                                    value = """
                                            {
                                                 "status": 103,
                                                 "message": "Saldo tidak mencukupi",
                                                 "data": null
                                               }
                                            """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Unauthorized",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = WebResponse.class),
                            examples = @ExampleObject(
                                    //  name = "Invalid Credentials",
                                    value = """
                                            {
                                              "status": 108,
                                              "message": "Token tidak tidak valid atau kadaluwarsa",
                                              "data": null
                                            }
                                            """
                            )
                    )
            )
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

    @Operation(summary = "Transaction History", description = "Digunakan untuk mendapatkan informasi history transaksi")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Get History Transaksi berhasil 200"),
            @ApiResponse(
                    responseCode = "200",
                    description = "Get History Transaksi berhasil 200",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = WebResponse.class),
                            examples = @ExampleObject(
                                    //  name = "Invalid Credentials",
                                    value = """
                                            {
                                                 "status": 0,
                                                 "message": "Get History Berhasil",
                                                 "data": {
                                                   "offset": "0",
                                                   "limit": "null",
                                                   "records": [
                                                     {
                                                       "invoiceNumber": "INV20251030-000003",
                                                       "description": "Pulsa Indosat",
                                                       "transactionType": "PAYMENT",
                                                       "totalAmount": 40000,
                                                       "createdOn": "2025-10-30T21:38:33.237312Z"
                                                     },
                                                     {
                                                       "invoiceNumber": "INV20251030-000002",
                                                       "description": "Top Up balance",
                                                       "transactionType": "TOPUP",
                                                       "totalAmount": 150000,
                                                       "createdOn": "2025-10-30T21:35:14.265253Z"
                                                     },
                                                     {
                                                       "invoiceNumber": "INV20251030-000001",
                                                       "description": "Top Up balance",
                                                       "transactionType": "TOPUP",
                                                       "totalAmount": 150000,
                                                       "createdOn": "2025-10-30T21:27:03.954312Z"
                                                     }
                                                   ]
                                                 }
                                               }
                                            """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Unauthorized",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = WebResponse.class),
                            examples = @ExampleObject(
                                    //  name = "Invalid Credentials",
                                    value = """
                                            {
                                              "status": 108,
                                              "message": "Token tidak tidak valid atau kadaluwarsa",
                                              "data": null
                                            }
                                            """
                            )
                    )
            )
    })
    @GetMapping("/transaction/history")
    public ResponseEntity<WebResponse<TransactionHistoryResponse>> getTransactionHistory(
            @AuthenticationPrincipal User user,
            @RequestParam(required = false) Integer limit,
            @RequestParam(required = false, defaultValue = "0") Integer offset) {

        TransactionHistoryResponse transactionHistory = transactionService.getTransactionHistory(user, limit, offset);

        WebResponse<TransactionHistoryResponse> response = WebResponse.<TransactionHistoryResponse>builder()
                .status(0)
                .message("Get History Berhasil")
                .data(transactionHistory)
                .build();

        return ResponseEntity.ok(response);
    }

    }