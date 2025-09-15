package com.julian.razif.microservices.service.persistence.product.repository;

import com.julian.razif.microservices.service.persistence.product.model.Cart;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.Repository;
import org.springframework.data.repository.RepositoryDefinition;

import java.util.UUID;

@RepositoryDefinition(domainClass = Cart.class, idClass = UUID.class)
public interface CartRepository extends Repository<Cart, UUID>, JpaSpecificationExecutor<Cart> {
}