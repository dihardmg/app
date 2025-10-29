package com.nutech.digitalservice.repository;

import com.nutech.digitalservice.entity.ServiceEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ServiceRepository extends JpaRepository<ServiceEntity, Long>, ServiceRepositoryCustom {

    // Hanya mewarisi JpaRepository dan ServiceRepositoryCustom
    // Semua method query menggunakan raw query dengan prepared statement
}