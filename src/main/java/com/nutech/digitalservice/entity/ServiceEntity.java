package com.nutech.digitalservice.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "services", indexes = {
    @Index(name = "idx_services_service_code", columnList = "service_code", unique = true),
    @Index(name = "idx_services_tariff", columnList = "service_tariff")
})
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ServiceEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "service_code", unique = true, nullable = false, length = 50)
    private String serviceCode;

    @Column(name = "service_name", nullable = false, length = 100)
    private String serviceName;

    @Column(name = "service_icon", length = 500)
    private String serviceIcon;

    @Column(name = "service_tariff", nullable = false)
    private Long serviceTariff;

    @Builder.Default
    @Column(nullable = true)
    private Boolean active = true;
}