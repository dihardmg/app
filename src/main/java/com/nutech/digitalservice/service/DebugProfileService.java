package com.nutech.digitalservice.service;

import com.nutech.digitalservice.dto.ImageUploadRequest;
import com.nutech.digitalservice.dto.ProfileResponse;
import com.nutech.digitalservice.dto.UpdateProfileRequest;
import com.nutech.digitalservice.entity.User;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@ConditionalOnProperty(name = "app.database.enabled", havingValue = "false")
public class DebugProfileService {

    public ProfileResponse getProfile(User user) {
        // Return mock profile for debugging
        return ProfileResponse.builder()
                .email("debug@example.com")
                .firstName("Debug")
                .lastName("User")
                .profileImage(null)
                .build();
    }

    public ProfileResponse updateProfile(User user, UpdateProfileRequest request) {
        // Return mock updated profile for debugging
        return ProfileResponse.builder()
                .email("debug@example.com")
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .profileImage(null)
                .build();
    }

    public ProfileResponse updateProfileImage(User user, ImageUploadRequest request) {
        // Return mock profile with updated image for debugging
        return ProfileResponse.builder()
                .email("debug@example.com")
                .firstName("Debug")
                .lastName("User")
                .profileImage("/uploads/profile-images/debug.jpg")
                .build();
    }
}