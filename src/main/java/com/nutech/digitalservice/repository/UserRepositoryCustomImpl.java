package com.nutech.digitalservice.repository;

import com.nutech.digitalservice.entity.User;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import org.springframework.stereotype.Repository;

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
}