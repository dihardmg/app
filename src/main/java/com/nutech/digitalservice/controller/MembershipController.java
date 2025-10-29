package com.nutech.digitalservice.controller;

import com.nutech.digitalservice.dto.ImageUploadRequest;
import com.nutech.digitalservice.dto.LoginRequest;
import com.nutech.digitalservice.dto.LoginResponse;
import com.nutech.digitalservice.dto.ProfileResponse;
import com.nutech.digitalservice.dto.RegistrationRequest;
import com.nutech.digitalservice.dto.UpdateProfileRequest;
import com.nutech.digitalservice.dto.WebResponse;
import com.nutech.digitalservice.entity.User;
import com.nutech.digitalservice.service.AuthService;
import com.nutech.digitalservice.service.ProfileService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1")
@Tag(name = "1. Module Membership", description = "API untuk user registration, login, dan profile management")
@SecurityRequirement(name = "bearerAuth")
public class MembershipController {

    @Autowired
    private AuthService authService;

    @Autowired
    private ProfileService profileService;

    @Operation(summary = "Register user", description = "**API Registration Public (Tidak perlu Token untuk mengaksesnya)**\n" +
            "\n" +
            "Digunakan untuk melakukan registrasi User agar bisa Login kedalam aplikasi\n" +
            "\n" +
            "*Ketentuan :*\n" +
            "\n" +
            "1.Parameter request **email** harus terdapat validasi format email\n" +
            "\n" +
            "2.Parameter request **password** Length minimal 8 karakter\n" +
            "\n" +
            "3.Handling First name dan LastName min 2 karakter\n" +
            "\n" +
            "4.Handling First name dan LastName hanya boleh mengandung huruf alfabet (A-Z, a-z).\n")
    @io.swagger.v3.oas.annotations.security.SecurityRequirements()
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "User registration data",
            content = @Content(
                    mediaType = "application/json",
                    examples = @ExampleObject(
                            // name = "Registration Example",
                            value = """
                                    {
                                      "email": "johndoe@gmail.com",
                                      "password": "Password123!",
                                      "firstName": "John",
                                      "lastName": "Doe"
                                    }
                                    """
                    )
            )
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Request Successfully",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = WebResponse.class),
                            examples = @ExampleObject(
                                    value = """
                                            {
                                              "status": 0,
                                              "message": "Registrasi berhasil silahkan login",
                                              "data": null
                                            }
                                            """
                            )
                    )
            ),
            @ApiResponse(responseCode = "400", description = "Bad Request",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = WebResponse.class),
                            examples = @ExampleObject(
                                    value = """
                                            {
                                              "status": 102,
                                              "message": "Paramter email tidak sesuai format",
                                              "data": null
                                            }
                                            """
                            )
                    )
            )
    })
    @PostMapping("/registration")
    public ResponseEntity<WebResponse<Object>> register(@Valid @org.springframework.web.bind.annotation.RequestBody RegistrationRequest request) {
        authService.register(request);

        WebResponse<Object> response = WebResponse.<Object>builder()
                .status(0)
                .message("Registrasi berhasil silahkan login")
                .data(null)
                .build();

        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Login user dan mendapatkan JWT token", description = "Login user dan mendapatkan JWT token")
    @io.swagger.v3.oas.annotations.security.SecurityRequirements()
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "User login credentials",
            content = @Content(
                    mediaType = "application/json",
                    examples = @ExampleObject(
                            name = "Login Example",
                            value = """
                                    {
                                      "email": "johndoe@gmail.com",
                                      "password": "Password123!"
                                    }
                                    """
                    )
            )
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    //description = "Berhasil Login",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = WebResponse.class),
                            examples = @ExampleObject(
                                    // name = "Success Login",
                                    value = """
                                            {
                                              "status": 0,
                                              "message": "Login Sukses",
                                              "data": {
                                                "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJkYXRhIjoiNTRVdXRjYTdCS0ZPX0ZUZGZ1bXlJem9zSTRKa1VxUGZVZ0ROSTUwelRTQlo2aHoyY0hKZ1VMb1loM09HUUd0ekQxV3dTX194aHBNZTE2SGFscVRzcEhjS21UclJ3S2FYYmZob3AzdzFFUHJ2NFdBQmk1c0RpdV9DSnZTSWt2MDFTbEU0QU5pbVB0bUx5azZoUzlOalVQNEZaVVpfRVBtcEk4Y3pNc3ZWa2JFPSIsImlhdCI6MTYyNjkyODk3MSwiZXhwIjoyNTU2MTE4Nzk4fQ.9C9NvhZYKivhGWnrjo4Wr1Rv-wur1wCm0jqfK9XDD8U"
                                              }
                                            }
                                            """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    //description = "Bad Request - Email tidak sesuai format",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = WebResponse.class),
                            examples = @ExampleObject(
                                    // name = "Invalid Email Format",
                                    value = """
                                            {
                                              "status": 102,
                                              "message": "Parameter email tidak sesuai format",
                                              "data": null
                                            }
                                            """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "401",
                    //  description = "Unauthorized - Username atau password salah",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = WebResponse.class),
                            examples = @ExampleObject(
                                    //  name = "Invalid Credentials",
                                    value = """
                                            {
                                              "status": 103,
                                              "message": "Username atau password salah",
                                              "data": null
                                            }
                                            """
                            )
                    )
            )
    })
    @PostMapping("/login")
    public ResponseEntity<WebResponse<LoginResponse>> login(@Valid @org.springframework.web.bind.annotation.RequestBody LoginRequest request) {
        LoginResponse loginResponse = authService.login(request);

        WebResponse<LoginResponse> response = WebResponse.<LoginResponse>builder()
                .status(0)
                .message("Login Sukses")
                .data(loginResponse)
                .build();

        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Get data Profile", description = "**API Profile Private (memerlukan Token untuk mengaksesnya**)\n" +
            "\n" +
            "Digunakan untuk mendapatkan informasi profile User\n" +
            "\n" +
            "*Ketentuan* :\n" +
            "\n" +
            "1.Service ini harus menggunakan Bearer Token JWT untuk mengaksesnya\n" +
            "\n" +
            "2.Tidak ada parameter email di query param url ataupun request body, parameter email diambil dari payload JWT yang didapatkan dari hasil login\n")
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
                                                                                "data": {
                                                                                  "email": "johndoe@gmail.com",
                                                                                  "firstName": "hadi",
                                                                                  "lastName": "Sudarsono",
                                                                                  "profileImage": "http://localhost:8081/uploads/profile-images/984b8dfa-d6fd-475f-b830-36ad54a8ddb2.png"
                                                                                }
                                                                              }
                                            """
                            )
                    )
            ),
            @ApiResponse(responseCode = "400", description = "Bad Request",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = WebResponse.class),
                            examples = @ExampleObject(
                                    value = """
                                            {
                                              "status": 102,
                                              "message": "Paramter email tidak sesuai format",
                                              "data": null
                                            }
                                            """
                            )
                    )
            )
    })
    @GetMapping("/profile")
    public ResponseEntity<WebResponse<ProfileResponse>> getProfile(@AuthenticationPrincipal User user) {
        ProfileResponse profileResponse = profileService.getProfile(user);

        WebResponse<ProfileResponse> response = WebResponse.<ProfileResponse>builder()
                .status(0)
                .message("Sukses")
                .data(profileResponse)
                .build();

        return ResponseEntity.ok(response);
    }


    @Operation(summary = "Update Profile ", description = "**API Update Profile Private (memerlukan Token untuk mengaksesnya)**\n" +
            "\n" +
            "Digunakan untuk mengupdate data profile User\n" +
            "\n" +
            "*Ketentuan* :\n" +
            "\n" +
            "1.Service ini harus menggunakan Bearer Token JWT untuk mengaksesnya\n" +
            "\n" +
            "2.Tidak ada parameter email di query param url ataupun request body, parameter email diambil dari payload JWT yang didapatkan dari hasil login\n" +
            "\n" +
            "3.Handling First name dan LastName min 2 karakter\n" +
            "\n" +
            "4.Handling First name dan LastName hanya boleh mengandung huruf alfabet (A-Z, a-z)."
    )

    @io.swagger.v3.oas.annotations.security.SecurityRequirements()
    @io.swagger.v3.oas.annotations.parameters.RequestBody(

            content = @Content(
                    mediaType = "application/json",
                    examples = @ExampleObject(

                            value = """
                                                    {
                                                      "firstName": "Agus",
                                                      "lastName": "Hari"
                                                    }
                                    """
                    )
            )
    )

    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    //description = "Berhasil Login",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = WebResponse.class),
                            examples = @ExampleObject(
                                    // name = "Success Login",
                                    value = """
                                            
                                              {
                                                                                         "status": 0,
                                                                                         "message": "Update Pofile berhasil",
                                                                                         "data": {
                                                                                           "email": "johndoe@gmail.com",
                                                                                           "firstName": "hadi",
                                                                                           "lastName": "Sudarsono",
                                                                                           "profileImage": "http://localhost:8081/uploads/profile-images/984b8dfa-d6fd-475f-b830-36ad54a8ddb2.png"
                                                                                         }
                                                                                         }
                                            
                                            
                                            """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    //description = "Bad Request - Email tidak sesuai format",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = WebResponse.class),
                            examples = @ExampleObject(
                                    // name = "Invalid Email Format",
                                    value = """
                                                                {
                                                                   "status": 102,
                                                                   "message": "First name harus minimal 2 karakter",
                                                                   "data": null
                                                                 }
                                            """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "401",
                    //  description = "Unauthorized - Username atau password salah",
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

    @PutMapping("/profile/update")
    public ResponseEntity<WebResponse<ProfileResponse>> updateProfile(
            @AuthenticationPrincipal User user,
            @Valid @org.springframework.web.bind.annotation.RequestBody UpdateProfileRequest request) {

        ProfileResponse profileResponse = profileService.updateProfile(user, request);

        WebResponse<ProfileResponse> response = WebResponse.<ProfileResponse>builder()
                .status(0)
                .message("Update Profile berhasil")
                .data(profileResponse)
                .build();

        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "Update Profile Image",
            description = "Upload profile image untuk user. Endpoint ini memerlukan Bearer Token JWT untuk mengaksesnya. " +
                    "Parameter email diambil dari payload JWT. \n" +
                    "**Format yang diperbolehkan:** JPEG (.jpg, .jpeg) dan PNG (.png)\n" +
                    "**Ukuran maksimal:** 5MB",
            tags = {"1. Module Membership"},
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "Profile image file to upload",
            content = @Content(
                    mediaType = "multipart/form-data",
                    schema = @Schema(implementation = ImageUploadRequest.class)
            )
    )
    @ApiResponses(value = {

            @ApiResponse(
                    responseCode = "200",
                    description = "Request Successfully - Profile image berhasil diupdate",
                    content = @Content(
                            mediaType = "application/json",
                            //schema = @Schema(implementation = ProfileImageSuccessResponse.class)
                            examples = @ExampleObject(
                                    //name = "Invalid Format",
                                    value = """
                                                        {
                                                                                   "status": 0,
                                                                                   "message": "Update Profile Image berhasil",
                                                                                   "data": {
                                                                                     "email": "johndoe@gmail.com",
                                                                                     "firstName": "fJwgEohVjVwizIQXMIUfQkchddFgPiuikQrDzFtAeuayqRuYFZu",
                                                                                     "lastName": "LrLMgOJzqUXKEVgqFLCLWuMRJBMNVRmTeUrnoxdWcGJNYJTtVNyfnQ",
                                                                                     "profileImage": "http://localhost:8081/uploads/profile-images/984b8dfa-d6fd-475f-b830-36ad54a8ddb2.png"
                                                                                   }
                                                                                 }
                                            """
                            )
                    )
            ), @ApiResponse(
            responseCode = "400",
            description = "Bad Request - Invalid image format",
            content = @Content(
                    mediaType = "application/json",
                    examples = @ExampleObject(
                                    //name = "Invalid Format",
                                    value = """
                                                {
                                                   "status": 102,
                                                   "message": "Format image tidak valid. Hanya JPEG dan PNG yang diperbolehkan. Content type yang dikirim: image/tiff",
                                                   "data": null
                                                 }
                                            """
                            )
                    )
            )
        })
    @PutMapping(value = "/profile/image", consumes = "multipart/form-data")
    public ResponseEntity<WebResponse<ProfileResponse>> updateProfileImage(
            @Parameter(
                    hidden = true
            ) @AuthenticationPrincipal User user,
            @Parameter(
                    name = "file",
                    description = "File gambar profile (JPEG/PNG, max 5MB)",
                    required = true,
                    in = ParameterIn.DEFAULT,
                    schema = @Schema(type = "string", format = "binary")
            )
            @Valid @ModelAttribute ImageUploadRequest request) {

        try {
            ProfileResponse profileResponse = profileService.updateProfileImage(user, request);

            WebResponse<ProfileResponse> response = WebResponse.<ProfileResponse>builder()
                    .status(0)
                    .message("Update Profile Image berhasil")
                    .data(profileResponse)
                    .build();

            return ResponseEntity.ok()
                    .contentType(org.springframework.http.MediaType.APPLICATION_JSON)
                    .body(response);
        } catch (Exception ex) {
            WebResponse<ProfileResponse> response = WebResponse.<ProfileResponse>builder()
                    .status(102)
                    .message(ex.getMessage())
                    .data(null)
                    .build();

            return ResponseEntity.badRequest()
                    .contentType(org.springframework.http.MediaType.APPLICATION_JSON)
                    .body(response);
        }
    }

}
