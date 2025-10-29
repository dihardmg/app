package com.nutech.digitalservice.repository;

import com.nutech.digitalservice.entity.Banner;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BannerRepository extends JpaRepository<Banner, Long>, BannerRepositoryCustom {

    // Hanya mewarisi JpaRepository dan BannerRepositoryCustom
    // Semua method query menggunakan raw query dengan prepared statement untuk performance yang lebih baik
}