package com.nutech.digitalservice.repository;

import com.nutech.digitalservice.entity.Banner;

import java.util.List;

public interface BannerRepositoryCustom {
    List<Banner> findAllBannersRaw();
}