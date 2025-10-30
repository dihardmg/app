package com.nutech.digitalservice.repository;

import com.nutech.digitalservice.entity.User;

import java.util.Optional;

public interface UserRepositoryCustom {
    Optional<User> findUserByIdRaw(Long userId);
    Optional<User> findByEmailWithRawQuery(String email);
    User insertUserWithRawQuery(String email, String password, String firstName, String lastName);
    boolean existsByEmailWithRawQuery(String email);
    User updateUserProfileWithRawQuery(Long userId, String firstName, String lastName);
}