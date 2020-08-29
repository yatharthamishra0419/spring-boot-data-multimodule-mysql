package com.multimodule.mysql.builders.jpaRepositoryBuilder;

import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.ApplicationContext;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.jpa.repository.config.JpaRepositoryConfigExtension;
import org.springframework.data.repository.config.AnnotationRepositoryConfigurationSource;
import org.springframework.data.repository.config.RepositoryConfigurationDelegate;
import org.springframework.data.repository.config.RepositoryConfigurationExtension;
import org.springframework.data.repository.config.RepositoryConfigurationUtils;

import java.util.HashMap;
import java.util.Map;

public class JpaRepositoryBuilder {

    public void enableJpaRepositoryScan(String transactionManagerBean,
                                        String entityManagerFactoryBean,
                                        ApplicationContext applicationContext,
                                        BeanDefinitionRegistry registry,
                                        Map<String, String> jpaProperties){
        AnnotationMetadata enableJpaRepositoriesData = null;
        try {
            String repositoryPackagesToScan=jpaProperties.get("repositorypackages");
            if(repositoryPackagesToScan==null)
                return;
            String[] basePackages=repositoryPackagesToScan.split(",");
            Map<String,Object> data=new HashMap<>();
            data.put("basePackages",basePackages);
            data.put("transactionManagerRef",transactionManagerBean);
            data.put("entityManagerFactoryRef",entityManagerFactoryBean);
            enableJpaRepositoriesData = new EnableJpaRepositoriesData(this.getClass().getClassLoader(),data);
        } catch (NoSuchMethodException e) {
            throw new ExceptionInInitializerError(e);
        } catch (ClassNotFoundException e) {
            throw new ExceptionInInitializerError(e);
        }

        AnnotationRepositoryConfigurationSource configurationSource = new AnnotationRepositoryConfigurationSource(
                enableJpaRepositoriesData, EnableJpaRepositories.class, applicationContext, applicationContext.getEnvironment(), registry
        );

        RepositoryConfigurationExtension extension = new JpaRepositoryConfigExtension();

        RepositoryConfigurationUtils.exposeRegistration(extension, registry, configurationSource);

        RepositoryConfigurationDelegate delegate = new RepositoryConfigurationDelegate(configurationSource, applicationContext, applicationContext.getEnvironment());
        delegate.registerRepositoriesIn(registry, extension);
    }
}