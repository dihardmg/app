package com.nutech.digitalservice.repository;

import com.nutech.digitalservice.entity.Banner;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BannerRepository extends JpaRepository<Banner, Long> {

    @Query("SELECT b FROM Banner b ORDER BY b.id")
    List<Banner> findAllOrderByBannerId();
}