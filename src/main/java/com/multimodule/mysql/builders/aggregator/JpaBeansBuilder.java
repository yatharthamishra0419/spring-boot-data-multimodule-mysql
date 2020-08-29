package com.multimodule.mysql.builders.aggregator;

import com.multimodule.mysql.builders.DataSourceBuilder;
import com.multimodule.mysql.builders.EntityManagerBuilder;
import com.multimodule.mysql.builders.TransactionManagerBuilder;
import com.multimodule.mysql.builders.jpaRepositoryBuilder.JpaRepositoryBuilder;

import java.util.Map;
import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;

import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.ApplicationContext;

public class JpaBeansBuilder {

  public static void buildEntityManager(Map<String, String> dataSourceProperties,
                                        Map<String, String> jpaProperties,
                                        Map<String, String> jpaEntityProperties, String beanAlias,
                                        ConfigurableBeanFactory beanFactory) {
    if (dataSourceProperties == null || dataSourceProperties.size() == 0) {
      return;
    }
    DataSource dataSource = DataSourceBuilder.getDataSource(dataSourceProperties);
    EntityManagerFactory entityManagerFactory =
        EntityManagerBuilder
            .buildEntityManagerFactoryBean(dataSource, jpaProperties, jpaEntityProperties,
                beanAlias, beanFactory);
    TransactionManagerBuilder
            .setTransactionBean(entityManagerFactory, dataSource, beanAlias, beanFactory);

  }

  public static void enableJpaRepositoryScan(String transactionManagerBeanName,
                                             Map<String, String> jpaProperties,
                                             String entityManagerBean,ApplicationContext applicationContext,
                                             BeanDefinitionRegistry beanDefinitionRegistry){
    JpaRepositoryBuilder jpaRepositoryBuilder=new JpaRepositoryBuilder();
    jpaRepositoryBuilder.enableJpaRepositoryScan(transactionManagerBeanName,
            entityManagerBean,
            applicationContext,beanDefinitionRegistry,jpaProperties);
  }
}
