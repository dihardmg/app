package com.nutech.digitalservice.repository;

import com.nutech.digitalservice.entity.Balance;
import com.nutech.digitalservice.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BalanceRepository extends JpaRepository<Balance, Long> {
    Optional<Balance> findByUser(User user);
}