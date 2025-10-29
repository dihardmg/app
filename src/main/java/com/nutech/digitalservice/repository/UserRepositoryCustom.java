package com.nutech.digitalservice.repository;

import com.nutech.digitalservice.entity.User;

import java.util.Optional;

public interface UserRepositoryCustom {
    Optional<User> findUserByIdRaw(Long userId);
}