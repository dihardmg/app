package com.nutech.digitalservice.service;

import com.nutech.digitalservice.dto.BannerResponse;
import com.nutech.digitalservice.entity.Banner;
import com.nutech.digitalservice.repository.BannerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class BannerService {

    @Autowired
    private BannerRepository bannerRepository;

    @Cacheable(value = "banners", key = "'allBanners'")
    public List<BannerResponse> getAllBanners() {
        // Menggunakan raw query dengan prepared statement untuk performance yang lebih baik
        List<Banner> banners = bannerRepository.findAllBannersRaw();
        return banners.stream()
                .map(this::convertToBannerResponse)
                .collect(Collectors.toList());
    }

    private BannerResponse convertToBannerResponse(Banner banner) {
        return BannerResponse.builder()
                .bannerName(banner.getBannerName())
                .bannerImage(banner.getBannerImage())
                .description(banner.getDescription())
                .build();
    }
}