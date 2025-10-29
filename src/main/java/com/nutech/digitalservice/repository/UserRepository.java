package com.nutech.digitalservice.repository;

import com.nutech.digitalservice.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long>, UserRepositoryCustom {

    // Legacy methods untuk compatibility
    Optional<User> findByEmail(String email);
    boolean existsByEmail(String email);

    // Custom method dengan raw query dan prepared statement
    // Optional<User> findUserByIdRaw(Long userId); - diwarisi dari UserRepositoryCustom
}