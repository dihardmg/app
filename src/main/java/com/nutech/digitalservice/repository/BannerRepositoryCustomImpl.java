package com.nutech.digitalservice.repository;

import com.nutech.digitalservice.entity.Banner;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class BannerRepositoryCustomImpl implements BannerRepositoryCustom {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public List<Banner> findAllBannersRaw() {
        // Raw query dengan prepared statement untuk performance yang lebih baik
        // Selective field selection - hanya field yang diperlukan untuk response
        String sql = "SELECT id, banner_name, banner_image, description, created_at, updated_at " +
                    "FROM banners " +
                    "ORDER BY id ASC";

        Query query = entityManager.createNativeQuery(sql, Banner.class);

        // Optimasi: Set fetch size untuk performance
        query.setHint("org.hibernate.fetchSize", 50);

        return query.getResultList();
    }
}