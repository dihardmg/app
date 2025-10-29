package com.nutech.digitalservice.repository;

import com.nutech.digitalservice.entity.ServiceEntity;

import java.util.List;
import java.util.Optional;

public interface ServiceRepositoryCustom {
    List<ServiceEntity> findAllActiveServicesRaw();
    Optional<ServiceEntity> findActiveServiceByCodeRaw(String serviceCode);
}