package com.multimodule.mysql.builders;

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Configuration;
import org.springframework.orm.jpa.JpaTransactionManager;

@Configuration
public class TransactionManagerBuilder {

  public static String TRANSACTION_BEAN_IDENTIFIER = "transaction";

  public static void   setTransactionBean(EntityManagerFactory entityManagerFactory,
      DataSource dataSource,
      String beanIdentifier, ConfigurableBeanFactory configurableBeanFactory) {
    JpaTransactionManager jpaTransactionManager = new JpaTransactionManager();
    jpaTransactionManager.setEntityManagerFactory(entityManagerFactory);
    jpaTransactionManager.setDataSource(dataSource);
    jpaTransactionManager.afterPropertiesSet();
    configurableBeanFactory.registerSingleton(beanIdentifier + "-" + TRANSACTION_BEAN_IDENTIFIER,
        jpaTransactionManager);
  }
}
