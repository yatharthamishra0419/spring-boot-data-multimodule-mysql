package com.multimodule.mysql.config;

import com.multimodule.mysql.ModuleAwarePropertiesUtils;
import com.multimodule.mysql.builders.EntityManagerBuilder;
import com.multimodule.mysql.builders.TransactionManagerBuilder;
import com.multimodule.mysql.builders.aggregator.JpaBeansBuilder;
import java.util.HashMap;
import java.util.Map;
import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.cloud.autoconfigure.RefreshAutoConfiguration;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.context.weaving.LoadTimeWeaverAware;
import org.springframework.core.env.Environment;
import org.springframework.instrument.classloading.LoadTimeWeaver;

/**
 * @author Yathartha Mishra
 * @created Jan 16, 2017 4:46:11 PM
 */
@Configuration
@AutoConfigureBefore(RefreshAutoConfiguration.class)
public class JpaMasterSlaveConfiguration extends DataSourceProperties implements BeanFactoryAware,
    LoadTimeWeaverAware {

  static final String DB_SHARD_NAMES_PROP_KEY = "jpa.shard.names";
  private static final String DB_SHARD_DEFAULT_KEY_PREFIX = "jpa.shard.default.datasource.";
  private static final String DB_SHARD_DEFAULT_MASTER_KEY_PREFIX = "jpa.shard.masters.default.datasource.";
  private static final String DB_SHARD_DEFAULT_SLAVE_KEY_PREFIX = "jpa.shard.slaves.default.datasource.";
  private static final String DB_SHARD_MASTER_KEY_PREFIX = "jpa.shard.%s.masters.datasource.";
  private static final String SPRING_EXCLUDE_PROPERTY = "spring.autoconfigure.exclude";
  private static final String DB_SHARD_SLAVE_KEY_PREFIX = "jpa.shard.%s.slaves.datasource.";
  private static final String DB_SHARD_JPA_PROPERTIES_SLAVE_KEY_PREFIX = "jpa.properties.shard.%s.slaves.jpa.";
  private static final String DB_SHARD_JPA_PROPERTIES_MASTER_KEY_PREFIX = "jpa.properties.shard.%s.masters.jpa.";
  private static final String DB_SHARD_JPA_MASTERS_KEY_PREFIX = "jpa.shard.%s.masters.jpa.";
  private static final String DB_SHARD_JPA_SLAVE_KEY_PREFIX = "jpa.shard.%s.slaves.jpa.";
  private static final String DB_SHARD_TRANSACTION_MASTER_KEY_PREFIX = "jpa.shard.%s.masters.transaction.";
  private static final String DB_SHARD_TRANSACTION_SLAVE_KEY_PREFIX = "jpa.shard.%s.slaves.transaction.";
  private static final String DB_SHARD_STRATEGY_KEY = "jpa.shard.strategy";
  private static final String DB_SHARD_TRANSACTION_CONSTANT = "name";
  private static ConfigurableBeanFactory beanFactoryStatic;
  @Inject
  private Environment environment;
  @Inject
  private AbstractApplicationContext applicationContext;

  @Autowired
  private ApplicationContextConfiguration applicationContextConfiguration;


  private Map<String, Map<String, String>> getModuleShardDBProperties(String keyPrefix
      , Map<String, Map<String, String>> defaultProperties) {
    final Map<String, Map<String, String>> moduleWiseDBProps = ModuleAwarePropertiesUtils
        .readModuleWiseSubProperties(environment, keyPrefix);

    if (defaultProperties == null) {
      return moduleWiseDBProps;
    }

    final Map<String, Map<String, String>> mergedModWiseDbProps = new HashMap<>(defaultProperties);

    moduleWiseDBProps.forEach((moduleName, props) -> {
      mergedModWiseDbProps.merge(moduleName, props, (oldProps, newProps) -> {
        final Map<String, String> mergedAttrs = new HashMap<>(oldProps);
        mergedAttrs.putAll(newProps);
        return mergedAttrs;
      });
    });

    return mergedModWiseDbProps;
  }

  private Map<String, String> getModuleShardDBProperties(String moduleName, String keyPrefix
      , Map<String, Map<String, String>> defaultProperties) {
    final Map<String, Map<String, String>> moduleShardProps = getModuleShardDBProperties(keyPrefix,
        defaultProperties);
    return moduleShardProps.containsKey(moduleName) ? moduleShardProps.get(moduleName)
        : new HashMap<>();
  }

  @PostConstruct
  public void registerJpaEnvironment() {
    final Map<String, String> moduleWiseShardNames
        = ModuleAwarePropertiesUtils
        .readModuleWisePropertyValues(environment, DB_SHARD_NAMES_PROP_KEY);
    final Map<String, Map<String, String>> moduleWiseDefaultProps
        = getModuleShardDBProperties(DB_SHARD_DEFAULT_KEY_PREFIX, null);

    final Map<String, Map<String, String>> moduleWiseDefaultMasterProps
        = getModuleShardDBProperties(DB_SHARD_DEFAULT_MASTER_KEY_PREFIX, moduleWiseDefaultProps);
    final Map<String, Map<String, String>> moduleWiseDefaultSlaveProps
        = getModuleShardDBProperties(DB_SHARD_DEFAULT_SLAVE_KEY_PREFIX, moduleWiseDefaultProps);
    moduleWiseShardNames.forEach((moduleName, shardNamesObj) -> {
      final String[] shardNames = shardNamesObj.split("\\s*,\\s*");

      for (String shardName : shardNames) {

                /*
                    Master Properties
                 */
        final Map<String, String> moduleShardMasterProps
            = getModuleShardDBProperties(moduleName,
            String.format(DB_SHARD_MASTER_KEY_PREFIX, shardName)
            , moduleWiseDefaultMasterProps);
        final Map<String, String> moduleShardMasterJpaProps =
            getModuleShardDBProperties(moduleName,
                String.format(DB_SHARD_JPA_PROPERTIES_MASTER_KEY_PREFIX, shardName),
                null);
        final Map<String, String> moduleShardMasterJpa =
            getModuleShardDBProperties(moduleName,
                String.format(DB_SHARD_JPA_MASTERS_KEY_PREFIX, shardName),
                null);
        final Map<String, String> moduleShardTransactionProperties =
            getModuleShardDBProperties(moduleName,
                String.format(DB_SHARD_TRANSACTION_MASTER_KEY_PREFIX, shardName),
                null);

        if (moduleShardMasterProps == null) {
          throw new IllegalArgumentException(
              "Master Datasource required for shard Name" + shardName);
        }
        /**
         * Slave Properties
         */
        final Map<String, String> moduleShardSlaveJpa =
            getModuleShardDBProperties(moduleName,
                String.format(DB_SHARD_JPA_SLAVE_KEY_PREFIX, shardName),
                null);

        final Map<String, String> moduleShardSlaveProps
            = getModuleShardDBProperties(moduleName,
            String.format(DB_SHARD_SLAVE_KEY_PREFIX, shardName)
            , moduleWiseDefaultSlaveProps);

        final Map<String, String> moduleShardSlaveJpaProps =
            getModuleShardDBProperties(moduleName,
                String.format(DB_SHARD_JPA_PROPERTIES_SLAVE_KEY_PREFIX, shardName),
                null);

        final Map<String, String> moduleShardSlaveTransactionProperties =
            getModuleShardDBProperties(moduleName,
                String.format(DB_SHARD_TRANSACTION_SLAVE_KEY_PREFIX, shardName),
                null);

        String beanAlias=moduleName + "-" + shardName + "-master";
        JpaBeansBuilder.buildEntityManager(moduleShardMasterProps, moduleShardMasterJpaProps,
                moduleShardMasterJpa, beanAlias, beanFactoryStatic);
        JpaBeansBuilder.enableJpaRepositoryScan(
                beanAlias + "-"+ TransactionManagerBuilder.TRANSACTION_BEAN_IDENTIFIER,
                moduleShardMasterJpa,beanAlias + "-" + EntityManagerBuilder.ENTITY_IDENTIFIER,
                applicationContextConfiguration.getApplicationContext(),BeansConfiguration.beanDefinitionRegistryStatic);
        beanAlias=moduleName + "-" + shardName + "-slave";
        JpaBeansBuilder.buildEntityManager(moduleShardSlaveProps, moduleShardSlaveJpaProps,
                moduleShardSlaveJpa, beanAlias, beanFactoryStatic);
        JpaBeansBuilder.enableJpaRepositoryScan(
                beanAlias + "-"+ TransactionManagerBuilder.TRANSACTION_BEAN_IDENTIFIER,
                moduleShardSlaveJpaProps,beanAlias + "-" + EntityManagerBuilder.ENTITY_IDENTIFIER,
                applicationContextConfiguration.getApplicationContext(),BeansConfiguration.beanDefinitionRegistryStatic);
      }

    });
    BeanDefinitionRegistry factory =
            (BeanDefinitionRegistry) applicationContext.getAutowireCapableBeanFactory();
    try {
      factory.removeBeanDefinition("jpaContext");
    }catch (Exception e){

    }

  }

  @Override
  public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
    beanFactoryStatic = (ConfigurableBeanFactory) beanFactory;
  }

  @Override
  public void setLoadTimeWeaver(LoadTimeWeaver loadTimeWeaver) {

  }
}