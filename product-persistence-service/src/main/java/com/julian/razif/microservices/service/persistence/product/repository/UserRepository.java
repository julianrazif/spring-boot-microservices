package com.julian.razif.microservices.service.persistence.product.repository;

import com.julian.razif.microservices.service.persistence.product.model.User;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.Repository;
import org.springframework.data.repository.RepositoryDefinition;

import java.util.UUID;

@RepositoryDefinition(domainClass = User.class, idClass = UUID.class)
public interface UserRepository extends Repository<User, UUID>, JpaSpecificationExecutor<User> {
}