package com.multimodule.mysql.config;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.context.weaving.LoadTimeWeaverAware;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.instrument.classloading.LoadTimeWeaver;

@Configuration
public class BeansConfiguration implements ImportBeanDefinitionRegistrar {

    public static BeanDefinitionRegistry beanDefinitionRegistryStatic;

    @Override
    public void registerBeanDefinitions(AnnotationMetadata annotationMetadata, BeanDefinitionRegistry beanDefinitionRegistry) {
        beanDefinitionRegistryStatic=beanDefinitionRegistry;
    }
}
