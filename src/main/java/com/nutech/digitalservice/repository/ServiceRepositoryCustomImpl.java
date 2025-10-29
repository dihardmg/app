package com.nutech.digitalservice.repository;

import com.nutech.digitalservice.entity.ServiceEntity;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class ServiceRepositoryCustomImpl implements ServiceRepositoryCustom {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public List<ServiceEntity> findAllActiveServicesRaw() {
        // Raw query dengan prepared statement untuk performance yang lebih baik
        // Selective field selection - hanya field yang diperlukan untuk response
        String sql = "SELECT id, service_code, service_name, service_icon, service_tariff, active " +
                    "FROM services " +
                    "WHERE active = true " +
                    "ORDER BY service_name";

        Query query = entityManager.createNativeQuery(sql, ServiceEntity.class);

        // Optimasi: Set fetch size untuk performance
        query.setHint("org.hibernate.fetchSize", 50);

        return query.getResultList();
    }

    @Override
    public Optional<ServiceEntity> findActiveServiceByCodeRaw(String serviceCode) {
        // Raw query dengan prepared statement dan parameter
        String sql = "SELECT id, service_code, service_name, service_icon, service_tariff, active " +
                    "FROM services " +
                    "WHERE service_code = ? AND active = true";

        Query query = entityManager.createNativeQuery(sql, ServiceEntity.class);
        query.setParameter(1, serviceCode);

        try {
            ServiceEntity result = (ServiceEntity) query.getSingleResult();
            return Optional.of(result);
        } catch (Exception e) {
            return Optional.empty();
        }
    }
}