package com.multimodule.mysql.utils;

import java.util.Map;
import org.apache.tomcat.jdbc.pool.PoolProperties;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.PropertyAccessorFactory;
import org.springframework.util.CollectionUtils;

public class PoolPropertiesFactory {

  private PoolPropertiesFactory() {
  }

  public static PoolProperties convertToPoolProperties(Map<String, String> properties) {
    if (CollectionUtils.isEmpty(properties)) {
      return null;
    }

    final PoolProperties poolProperties = new PoolProperties();

    // Set default values
    poolProperties.setJmxEnabled(true);
    poolProperties.setTestOnBorrow(true);
    poolProperties.setValidationQuery("SELECT 1");

    final BeanWrapper beanWrapper = PropertyAccessorFactory.forBeanPropertyAccess(poolProperties);
    beanWrapper.setPropertyValues(properties);

    return poolProperties;
  }
}