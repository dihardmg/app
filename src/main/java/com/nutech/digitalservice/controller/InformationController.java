package com.nutech.digitalservice.controller;

import com.nutech.digitalservice.dto.BannerResponse;
import com.nutech.digitalservice.dto.ServiceResponse;
import com.nutech.digitalservice.dto.WebResponse;
import com.nutech.digitalservice.entity.User;
import com.nutech.digitalservice.service.BannerService;
import com.nutech.digitalservice.service.TransactionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.nio.charset.StandardCharsets;
import java.util.List;

@RestController
@RequestMapping("/api/v1")
@Tag(name = "2. Module Information", description = "API untuk mendapatkan informasi banner dan layanan")
public class InformationController {

    @Autowired
    private TransactionService transactionService;

    @Autowired
    private BannerService bannerService;

    @Operation(
        summary = "Mendapatkan daftar Banner yang tersedia (Public)",
        description = "**API Banner Public (Tidak perlu Token untuk mengaksesnya)**\n\n" +
                    "Digunakan untuk mendapatkan daftar banner yang tersedia di aplikasi.\n\n" +
                    "**Response:**\n" +
                    "- Mengembalikan daftar banner dengan informasi nama, gambar, dan deskripsi"
    )
    @io.swagger.v3.oas.annotations.security.SecurityRequirements()
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Request Successfully",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = WebResponse.class),
                            examples = @ExampleObject(
                                    value = """
                                                            {
                                                                                "status": 0,
                                                                                "message": "Sukses",
                                                                                "data": [
                                                                                  {
                                                                                    "banner_name": "Banner 1",
                                                                                    "banner_image": "https://minio.nutech-integrasi.com/take-home-test/banner/Banner-1.png",
                                                                                    "description": "Lerem Ipsum Dolor sit amet"
                                                                                  },
                                                                                  {
                                                                                    "banner_name": "Banner 2",
                                                                                    "banner_image": "https://minio.nutech-integrasi.com/take-home-test/banner/Banner-2.png",
                                                                                    "description": "Lerem Ipsum Dolor sit amet"
                                                                                  },
                                                                                  {
                                                                                    "banner_name": "Banner 3",
                                                                                    "banner_image": "https://minio.nutech-integrasi.com/take-home-test/banner/Banner-3.png",
                                                                                    "description": "Lerem Ipsum Dolor sit amet"
                                                                                  },
                                                                                  {
                                                                                    "banner_name": "Banner 4",
                                                                                    "banner_image": "https://minio.nutech-integrasi.com/take-home-test/banner/Banner-4.png",
                                                                                    "description": "Lerem Ipsum Dolor sit amet"
                                                                                  },
                                                                                  {
                                                                                    "banner_name": "Banner 5",
                                                                                    "banner_image": "https://minio.nutech-integrasi.com/take-home-test/banner/Banner-5.png",
                                                                                    "description": "Lerem Ipsum Dolor sit amet"
                                                                                  }
                                                                                ]
                                                                              }
                                            """
                            )
                    )
            )
    })
    @GetMapping("/banner")
    public ResponseEntity<WebResponse<List<BannerResponse>>> getBanners() {
         List<BannerResponse> banners = bannerService.getAllBanners();

         WebResponse<List<BannerResponse>> response = WebResponse.<List<BannerResponse>>builder()
                 .status(0)
                 .message("Sukses")
                 .data(banners)
                 .build();

         // Set headers sesuai spesifikasi
         HttpHeaders headers = new HttpHeaders();
         headers.setContentType(MediaType.valueOf("application/json; charset=utf-8"));

         // Convert response to JSON string to calculate content length
         try {
             String jsonResponse = "{\"status\":0,\"message\":\"Sukses\",\"data\":[";
             boolean first = true;
             for (BannerResponse banner : banners) {
                 if (!first) jsonResponse += ",";
                 jsonResponse += String.format("{\"banner_name\":\"%s\",\"banner_image\":\"%s\",\"description\":\"%s\"}",
                     banner.getBannerName().replace("\"", "\\\""),
                     banner.getBannerImage().replace("\"", "\\\""),
                     banner.getDescription().replace("\"", "\\\""));
                 first = false;
             }
             jsonResponse += "]}";

             headers.setContentLength(jsonResponse.getBytes(StandardCharsets.UTF_8).length);

             return ResponseEntity.ok()
                     .headers(headers)
                     .body(response);
         } catch (Exception e) {
             return ResponseEntity.ok()
                     .headers(headers)
                     .body(response);
         }
     }

    @Operation(summary = "Mendapatkan daftar layanan yang tersedia", description = "Mendapatkan daftar layanan yang tersedia")
    @SecurityRequirement(name = "bearerAuth")
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