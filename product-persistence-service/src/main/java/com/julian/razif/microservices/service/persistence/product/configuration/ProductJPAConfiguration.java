package com.julian.razif.microservices.service.persistence.product.configuration;

import com.zaxxer.hikari.HikariDataSource;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import javax.sql.DataSource;

@Configuration
@EnableJpaRepositories(
  basePackages = "com.julian.razif.microservices.service.persistence.product.repository",
  entityManagerFactoryRef = "productEntityManagerFactory",
  transactionManagerRef = "productTransactionManager"
)
@EnableTransactionManagement
public class ProductJPAConfiguration {

  @Value("${spring.datasource.product.url}")
  private String url;
  @Value("${spring.datasource.product.username}")
  private String username;
  @Value("${spring.datasource.product.password}")
  private String password;

  @Bean(name = "productDataSource")
  @ConfigurationProperties("spring.datasource.product.hikari")
  public HikariDataSource productDataSource() {
    return DataSourceBuilder.create()
      .type(HikariDataSource.class)
      .url(url)
      .username(username)
      .password(password)
      .build();
  }

  @Bean(name = "productEntityManagerFactory")
  public LocalContainerEntityManagerFactoryBean productEntityManagerFactory(
    EntityManagerFactoryBuilder builder,
    @Qualifier("productDataSource") DataSource dataSource) {
    // Build the entity manager factory with the provided data source
    // This configures JPA to use the specified packages for entity scanning
    LocalContainerEntityManagerFactoryBean em = builder
      .dataSource(dataSource)
      .packages(packagesToScan())
      .persistenceUnit("product-pu")
      .build();

    // Configure Hibernate as the JPA vendor with PostgreSQL dialect
    // ShowSql is disabled for production performance
    // GenerateDdl is disabled to prevent automatic schema generation
    HibernateJpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
    vendorAdapter.setDatabasePlatform("org.hibernate.dialect.PostgreSQL10Dialect");
    vendorAdapter.setShowSql(false);
    vendorAdapter.setGenerateDdl(false);

    em.setJpaVendorAdapter(vendorAdapter);

    return em;
  }

  @Bean(name = "productEntityManager")
  public EntityManager productEntityManager(
    @Qualifier("productEntityManagerFactory") EntityManagerFactory entityManagerFactory) {
    return entityManagerFactory.createEntityManager();
  }

  @Bean(name = "productTransactionManager")
  public PlatformTransactionManager productTransactionManager(
    @Qualifier("productEntityManagerFactory") EntityManagerFactory entityManagerFactory) {
    return new JpaTransactionManager(entityManagerFactory);
  }

  private String[] packagesToScan() {
    return new String[]{
      "com.julian.razif.microservices.service.persistence.product.model",
    };
  }

}
