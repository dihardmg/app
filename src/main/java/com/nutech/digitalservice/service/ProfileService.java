package com.nutech.digitalservice.service;

import com.nutech.digitalservice.dto.ImageUploadRequest;
import com.nutech.digitalservice.dto.ProfileResponse;
import com.nutech.digitalservice.dto.UpdateProfileRequest;
import com.nutech.digitalservice.entity.User;
import com.nutech.digitalservice.exception.FileValidationException;
import com.nutech.digitalservice.repository.UserRepository;
import com.nutech.digitalservice.repository.UserRepositoryCustom;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class ProfileService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    @Qualifier("userRepositoryCustomImpl")
    private UserRepositoryCustom userRepositoryCustom;

    @Value("${app.upload.dir:uploads}")
    private String uploadDir;

    @Value("${app.base.url:http://localhost:8081}")
    private String baseUrl;

    private static final List<String> ALLOWED_CONTENT_TYPES = Arrays.asList(
            "image/jpeg",
            "image/png"
    );

    private static final List<String> ALLOWED_EXTENSIONS = Arrays.asList(
            ".jpg", ".jpeg", ".png"
    );

    @Cacheable(value = "profiles", key = "#user.id")
    public ProfileResponse getProfile(User user) {
        // Menggunakan raw query dengan prepared statement untuk performance yang lebih baik
        Optional<User> userOpt = userRepository.findUserByIdRaw(user.getId());

        // Fallback ke existing user object jika raw query gagal
        User userProfile = userOpt.orElse(user);

        String fullProfileImageUrl = buildFullImageUrl(userProfile.getProfileImage());
        return ProfileResponse.builder()
                .email(userProfile.getEmail())
                .firstName(userProfile.getFirstName())
                .lastName(userProfile.getLastName())
                .profileImage(fullProfileImageUrl)
                .build();
    }

    @CacheEvict(value = "profiles", key = "#user.id")
    public ProfileResponse updateProfile(User user, UpdateProfileRequest request) {
        // Update profile menggunakan raw query dengan prepared statement untuk performance dan security
        User updatedUser = userRepositoryCustom.updateUserProfileWithRawQuery(
                user.getId(),
                request.getFirstName(),
                request.getLastName()
        );

        String fullProfileImageUrl = buildFullImageUrl(updatedUser.getProfileImage());
        return ProfileResponse.builder()
                .email(updatedUser.getEmail())
                .firstName(updatedUser.getFirstName())
                .lastName(updatedUser.getLastName())
                .profileImage(fullProfileImageUrl)
                .build();
    }

    @CacheEvict(value = "profiles", key = "#user.id")
    public ProfileResponse updateProfileImage(User user, ImageUploadRequest request) {
        MultipartFile file = request.getFile();

        // Validasi file
        validateImageFile(file);

        try {
            // Buat direktori upload jika belum ada
            Path uploadPath = Paths.get(uploadDir, "profile-images");
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            // Generate nama file unik
            String originalFilename = file.getOriginalFilename();
            String fileExtension = getFileExtension(originalFilename);
            String newFilename = UUID.randomUUID().toString() + fileExtension;

            // Simpan file
            Path filePath = uploadPath.resolve(newFilename);
            Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

            // Update profile image di database
            String imageUrl = "/uploads/profile-images/" + newFilename;
            user.setProfileImage(imageUrl);
            userRepository.save(user);

            // Build full URL for response
            String fullImageUrl = buildFullImageUrl(imageUrl);

            // Return profile response with updated data
            return ProfileResponse.builder()
                    .email(user.getEmail())
                    .firstName(user.getFirstName())
                    .lastName(user.getLastName())
                    .profileImage(fullImageUrl)
                    .build();

        } catch (IOException e) {
            throw new RuntimeException("Gagal mengupload profile image", e);
        }
    }

    private void validateImageFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new FileValidationException("File tidak boleh kosong");
        }

        // Validasi content type
        String contentType = file.getContentType();
        if (contentType == null || !ALLOWED_CONTENT_TYPES.contains(contentType)) {
            throw new FileValidationException("Format image tidak valid. Hanya JPEG dan PNG yang diperbolehkan. Content type yang dikirim: " + contentType);
        }

        // Validasi ekstensi file dari original filename
        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null || originalFilename.trim().isEmpty()) {
            throw new FileValidationException("Nama file tidak valid");
        }

        String fileExtension = getFileExtension(originalFilename).toLowerCase();
        if (!ALLOWED_EXTENSIONS.contains(fileExtension)) {
            throw new FileValidationException("Ekstensi file tidak valid. Hanya .jpg, .jpeg, dan .png yang diperbolehkan");
        }

        // Validasi tambahan: pastikan ekstensi file cocok dengan content type
        validateExtensionMatchesContentType(fileExtension, contentType);

        // Validasi konten file sebenarnya (magic numbers)
        validateActualFileContent(file);

        // Validasi ukuran file (max 5MB)
        long maxSize = 5 * 1024 * 1024; // 5MB
        if (file.getSize() > maxSize) {
            throw new FileValidationException("Ukuran file terlalu besar. Maksimal 5MB");
        }
    }

    private void validateExtensionMatchesContentType(String extension, String contentType) {
        // Validasi kecocokan ekstensi dengan content type untuk mencegah spoofing
        boolean isJpeg = (extension.equals(".jpg") || extension.equals(".jpeg")) && contentType.equals("image/jpeg");
        boolean isPng = extension.equals(".png") && contentType.equals("image/png");

        if (!isJpeg && !isPng) {
            throw new FileValidationException("Ekstensi file tidak cocok dengan format file. Pastikan ekstensi dan format file sesuai");
        }
    }

    private void validateActualFileContent(MultipartFile file) {
        try {
            byte[] fileBytes = file.getBytes();

            // Validasi magic numbers untuk memastikan tipe file sebenarnya
            if (isJpegFile(fileBytes)) {
                // Pastikan content type juga JPEG
                if (!"image/jpeg".equals(file.getContentType())) {
                    throw new FileValidationException("Tipe file sebenarnya adalah JPEG, tetapi content type tidak sesuai");
                }
            } else if (isPngFile(fileBytes)) {
                // Pastikan content type juga PNG
                if (!"image/png".equals(file.getContentType())) {
                    throw new FileValidationException("Tipe file sebenarnya adalah PNG, tetapi content type tidak sesuai");
                }
            } else {
                throw new FileValidationException("Format file tidak valid. Hanya JPEG dan PNG yang diperbolehkan");
            }
        } catch (IOException e) {
            throw new FileValidationException("Gagal membaca konten file untuk validasi");
        }
    }

    private boolean isJpegFile(byte[] fileBytes) {
        // JPEG magic numbers: FF D8 FF
        return fileBytes.length >= 3 &&
               fileBytes[0] == (byte) 0xFF &&
               fileBytes[1] == (byte) 0xD8 &&
               fileBytes[2] == (byte) 0xFF;
    }

    private boolean isPngFile(byte[] fileBytes) {
        // PNG magic numbers: 89 50 4E 47 0D 0A 1A 0A
        return fileBytes.length >= 8 &&
               fileBytes[0] == (byte) 0x89 &&
               fileBytes[1] == 0x50 &&
               fileBytes[2] == 0x4E &&
               fileBytes[3] == 0x47 &&
               fileBytes[4] == 0x0D &&
               fileBytes[5] == 0x0A &&
               fileBytes[6] == 0x1A &&
               fileBytes[7] == 0x0A;
    }

    private String getFileExtension(String filename) {
        if (filename == null || filename.isEmpty()) {
            return "";
        }
        return filename.substring(filename.lastIndexOf("."));
    }

    private String buildFullImageUrl(String relativePath) {
        if (relativePath == null || relativePath.trim().isEmpty()) {
            return null;
        }

        // Remove leading slash if present to avoid double slashes
        String cleanPath = relativePath.startsWith("/") ? relativePath.substring(1) : relativePath;
        return baseUrl + "/" + cleanPath;
    }
}