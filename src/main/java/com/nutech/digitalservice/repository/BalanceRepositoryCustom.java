package com.nutech.digitalservice.repository;

import com.nutech.digitalservice.entity.Balance;
import com.nutech.digitalservice.entity.User;

import java.util.Optional;

public interface BalanceRepositoryCustom {
    Optional<Balance> findByUserWithRawQuery(User user);
    Balance updateBalanceWithRawQuery(User user, Long newBalance);
    Balance insertBalanceWithRawQuery(User user, Long initialBalance);
}