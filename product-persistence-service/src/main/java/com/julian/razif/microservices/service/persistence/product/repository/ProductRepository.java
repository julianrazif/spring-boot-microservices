package com.julian.razif.microservices.service.persistence.product.repository;

import com.julian.razif.microservices.service.persistence.product.model.Product;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.Repository;
import org.springframework.data.repository.RepositoryDefinition;

import java.util.UUID;

@RepositoryDefinition(domainClass = Product.class, idClass = UUID.class)
public interface ProductRepository extends Repository<Product, UUID>, JpaSpecificationExecutor<Product> {
}