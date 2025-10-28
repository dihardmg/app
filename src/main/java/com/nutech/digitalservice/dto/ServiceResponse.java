package com.nutech.digitalservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ServiceResponse {
    private String serviceCode;
    private String serviceName;
    private String serviceIcon;
    private Integer serviceTariff;
}