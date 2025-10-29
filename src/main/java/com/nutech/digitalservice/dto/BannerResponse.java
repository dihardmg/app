package com.nutech.digitalservice.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BannerResponse {
    @JsonProperty("banner_name")
    private String bannerName;

    @JsonProperty("banner_image")
    private String bannerImage;

    private String description;
}