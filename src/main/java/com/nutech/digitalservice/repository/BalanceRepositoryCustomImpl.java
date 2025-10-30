package com.nutech.digitalservice.repository;

import com.nutech.digitalservice.entity.Balance;
import com.nutech.digitalservice.entity.User;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public class BalanceRepositoryCustomImpl implements BalanceRepositoryCustom {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public Optional<Balance> findByUserWithRawQuery(User user) {
        String sql = "SELECT id, user_id, balance, created_at, updated_at " +
                    "FROM balances WHERE user_id = ?";

        try {
            Object[] result = (Object[]) entityManager.createNativeQuery(sql)
                    .setParameter(1, user.getId())
                    .getSingleResult();

            if (result != null) {
                Balance balance = new Balance();
                balance.setId(((Number) result[0]).longValue());
                balance.setUser(user);
                balance.setBalance(((Number) result[2]).longValue());
                balance.setCreatedAt(((Timestamp) result[3]).toLocalDateTime());
                balance.setUpdatedAt(((Timestamp) result[4]).toLocalDateTime());
                return Optional.of(balance);
            }
        } catch (Exception e) {
            // Return empty if not found - this will create a new balance with 0
        }

        return Optional.empty();
    }

    @Override
    public Balance updateBalanceWithRawQuery(User user, Long newBalance) {
        // First update the balance
        String updateSql = "UPDATE balances " +
                         "SET balance = ?, updated_at = ? " +
                         "WHERE user_id = ?";

        entityManager.createNativeQuery(updateSql)
                .setParameter(1, newBalance)
                .setParameter(2, Timestamp.valueOf(LocalDateTime.now()))
                .setParameter(3, user.getId())
                .executeUpdate();

        // Then fetch the updated record
        String selectSql = "SELECT id, user_id, balance, created_at, updated_at " +
                          "FROM balances WHERE user_id = ?";

        Object[] result = (Object[]) entityManager.createNativeQuery(selectSql)
                .setParameter(1, user.getId())
                .getSingleResult();

        Balance balance = new Balance();
        balance.setId(((Number) result[0]).longValue());
        balance.setUser(user);
        balance.setBalance(((Number) result[2]).longValue());
        balance.setCreatedAt(((Timestamp) result[3]).toLocalDateTime());
        balance.setUpdatedAt(((Timestamp) result[4]).toLocalDateTime());

        return balance;
    }

    @Override
    public Balance insertBalanceWithRawQuery(User user, Long initialBalance) {
        // First insert the balance
        String insertSql = "INSERT INTO balances (user_id, balance, created_at, updated_at) " +
                          "VALUES (?, ?, ?, ?)";

        LocalDateTime now = LocalDateTime.now();
        entityManager.createNativeQuery(insertSql)
                .setParameter(1, user.getId())
                .setParameter(2, initialBalance)
                .setParameter(3, Timestamp.valueOf(now))
                .setParameter(4, Timestamp.valueOf(now))
                .executeUpdate();

        // Then fetch the inserted record
        String selectSql = "SELECT id, user_id, balance, created_at, updated_at " +
                          "FROM balances WHERE user_id = ?";

        Object[] result = (Object[]) entityManager.createNativeQuery(selectSql)
                .setParameter(1, user.getId())
                .getSingleResult();

        Balance balance = new Balance();
        balance.setId(((Number) result[0]).longValue());
        balance.setUser(user);
        balance.setBalance(((Number) result[2]).longValue());
        balance.setCreatedAt(((Timestamp) result[3]).toLocalDateTime());
        balance.setUpdatedAt(((Timestamp) result[4]).toLocalDateTime());

        return balance;
    }
}