package com.nutech.digitalservice.repository;

import com.nutech.digitalservice.entity.User;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.annotation.Propagation;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public class UserRepositoryCustomImpl implements UserRepositoryCustom {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public Optional<User> findUserByIdRaw(Long userId) {
        // Raw query dengan prepared statement untuk performance yang lebih baik
        // Selective field selection - hanya field yang diperlukan untuk profile response
        String sql = "SELECT id, email, password, first_name, last_name, profile_image, created_at, updated_at " +
                    "FROM users " +
                    "WHERE id = ?";

        Query query = entityManager.createNativeQuery(sql, User.class);
        query.setParameter(1, userId); // Prepared statement parameter

        // Optimasi: Set fetch size untuk performance (meskipun untuk single record)
        query.setHint("org.hibernate.fetchSize", 1);

        try {
            User result = (User) query.getSingleResult();
            return Optional.of(result);
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    @Override
    public Optional<User> findByEmailWithRawQuery(String email) {
        // Raw query dengan prepared statement untuk security dan performance
        String sql = "SELECT id, email, password, first_name, last_name, profile_image, created_at, updated_at " +
                    "FROM users " +
                    "WHERE email = ?";

        Query query = entityManager.createNativeQuery(sql, User.class);
        query.setParameter(1, email); // Prepared statement parameter

        try {
            User result = (User) query.getSingleResult();
            return Optional.of(result);
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public User insertUserWithRawQuery(String email, String password, String firstName, String lastName) {
        // Raw query dengan prepared statement untuk insert user baru
        // Gunakan Spring transaction management dengan REQUIRES_NEW
        String sql = "INSERT INTO users (email, password, first_name, last_name, created_at, updated_at) " +
                    "VALUES (?, ?, ?, ?, ?, ?)";

        LocalDateTime now = LocalDateTime.now();

        try {
            // Gunakan createNativeQuery tanpa explicit transaction handling
            int result = entityManager.createNativeQuery(sql)
                    .setParameter(1, email)                    // Prepared statement parameter 1
                    .setParameter(2, password)                 // Prepared statement parameter 2 (hashed password)
                    .setParameter(3, firstName)                 // Prepared statement parameter 3
                    .setParameter(4, lastName)                  // Prepared statement parameter 4
                    .setParameter(5, Timestamp.valueOf(now))    // Prepared statement parameter 5
                    .setParameter(6, Timestamp.valueOf(now))    // Prepared statement parameter 6
                    .executeUpdate();

            if (result > 0) {
                // Setelah insert berhasil, fetch user yang baru dibuat
                // Tidak perlu flush/clear karena Spring transaction akan handle
                return findByEmailWithRawQuery(email)
                        .orElseThrow(() -> new RuntimeException("Failed to retrieve newly created user"));
            } else {
                throw new RuntimeException("Failed to insert user: No rows affected");
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to insert user: " + e.getMessage(), e);
        }
    }

    @Override
    public boolean existsByEmailWithRawQuery(String email) {
        // Raw query dengan prepared statement untuk cek keberadaan email
        String sql = "SELECT COUNT(*) FROM users WHERE email = ?";

        Query query = entityManager.createNativeQuery(sql);
        query.setParameter(1, email); // Prepared statement parameter

        try {
            Long count = ((Number) query.getSingleResult()).longValue();
            return count > 0;
        } catch (Exception e) {
            return false;
        }
    }
}