package com.multimodule.mysql.builders;

import com.multimodule.mysql.utils.PoolPropertiesFactory;

import java.util.Map;
import javax.sql.DataSource;

import org.apache.tomcat.jdbc.pool.PoolProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DataSourceBuilder {

  public static DataSource getDataSource(Map<String, String> dbProperties) {

    final PoolProperties poolProperties = PoolPropertiesFactory
        .convertToPoolProperties(dbProperties);
    if (poolProperties == null) {
      return null;
    }

    final String unresolvedUrl = poolProperties.getUrl();
    if (unresolvedUrl == null || unresolvedUrl.isEmpty()) {
      throw new IllegalArgumentException("Datasource url cant be empty");
    }
    return new org.apache.tomcat.jdbc.pool.DataSource(poolProperties) {
    };
  }
}
