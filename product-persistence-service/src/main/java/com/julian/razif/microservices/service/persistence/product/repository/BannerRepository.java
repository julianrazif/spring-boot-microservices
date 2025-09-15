package com.julian.razif.microservices.service.persistence.product.repository;

import com.julian.razif.microservices.service.persistence.product.model.Banner;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.Repository;
import org.springframework.data.repository.RepositoryDefinition;

import java.util.UUID;

@RepositoryDefinition(domainClass = Banner.class, idClass = UUID.class)
public interface BannerRepository extends Repository<Banner, UUID>, JpaSpecificationExecutor<Banner> {
}