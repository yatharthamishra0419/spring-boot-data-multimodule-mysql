package com.multimodule.mysql.builders;

import java.util.Map;
import java.util.Properties;
import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Configuration;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.Database;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;

@Configuration
public class EntityManagerBuilder {

  private static String PACKAGES_TO_SCAN = "packagesToScan";

  private static String PERSISTANT_UNIT_NAME = "persistence.unit.name";

  private static String HIBERNATE_DIALECT_CONSTANT = "hibernate.dialect";

  private static String HIBERNATE_DIALECT_DEFAULT_VALUE = "org.hibernate.dialect.MySQL";

  private static String HIBERNATE_HBM2DDL_CONSTANT = "hibernate.hbm2ddl.auto";

  private static String HIBERNATE_HBM2DDL_DEFAULT_VALUE = "none";

  private static Logger logger = LoggerFactory.getLogger(EntityManagerBuilder.class);

  public static String ENTITY_IDENTIFIER = "entity";

  public static EntityManagerFactory buildEntityManagerFactoryBean(DataSource dataSource,
      Map<String, String> jpaProperties,
      Map<String, String> jpaEntityInfo,
      String beanAlias,
      ConfigurableBeanFactory configurableBeanFactory) {
    LocalContainerEntityManagerFactoryBean localContainerEntityManagerFactoryBean
        = new LocalContainerEntityManagerFactoryBean();
    Properties properties = new Properties();
    properties.putAll(jpaProperties);
    localContainerEntityManagerFactoryBean.setJpaVendorAdapter(getVendorAdaptor());
    if (jpaEntityInfo == null || jpaEntityInfo.get(PACKAGES_TO_SCAN) == null) {
      throw new IllegalArgumentException("Packages to scan required in jpa configuration ");
    }
    if (jpaProperties.get(HIBERNATE_DIALECT_CONSTANT) == null) {
      logger.warn("Hibernate dialect not set choosing default as null");
      jpaProperties.put(HIBERNATE_DIALECT_CONSTANT, HIBERNATE_DIALECT_DEFAULT_VALUE);
    }
    if (jpaProperties.get(HIBERNATE_HBM2DDL_CONSTANT) == null) {
      logger.warn("Hibernate HB2DDL not set choosing mysql as default");
      jpaProperties.put(HIBERNATE_HBM2DDL_CONSTANT, HIBERNATE_HBM2DDL_DEFAULT_VALUE);
    }
    localContainerEntityManagerFactoryBean.setPackagesToScan(jpaEntityInfo.get(PACKAGES_TO_SCAN));
    localContainerEntityManagerFactoryBean
        .setPersistenceUnitName(beanAlias + "-" + ENTITY_IDENTIFIER);
    localContainerEntityManagerFactoryBean.setJpaProperties(properties);
    localContainerEntityManagerFactoryBean.setDataSource(dataSource);
    localContainerEntityManagerFactoryBean.afterPropertiesSet();
    configurableBeanFactory.registerSingleton(beanAlias + "-" + ENTITY_IDENTIFIER,
        localContainerEntityManagerFactoryBean.getObject());
    return localContainerEntityManagerFactoryBean.getObject();
  }

  private static HibernateJpaVendorAdapter getVendorAdaptor() {
    HibernateJpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
    vendorAdapter.setDatabase(Database.MYSQL);
    vendorAdapter.setShowSql(true);
    return vendorAdapter;
  }
}
