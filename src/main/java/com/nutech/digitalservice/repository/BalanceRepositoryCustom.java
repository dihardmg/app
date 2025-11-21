package com.nutech.digitalservice.repository;

import com.nutech.digitalservice.entity.Balance;
import com.nutech.digitalservice.entity.User;

import java.math.BigDecimal;
import java.util.Optional;

public interface BalanceRepositoryCustom {
    Optional<Balance> findByUserWithRawQuery(User user);
    Balance updateBalanceWithRawQuery(User user, BigDecimal newBalance);
    Balance insertBalanceWithRawQuery(User user, BigDecimal initialBalance);
}