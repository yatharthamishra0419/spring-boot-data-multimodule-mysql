package com.multimodule.mysql;

import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.EnumerablePropertySource;
import org.springframework.core.env.Environment;
import org.springframework.core.env.PropertySource;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class ModuleAwarePropertiesUtils {

    public static Map<String, Map<String, String>> readModuleWiseSubProperties(Environment environment, String keyPrefix) {
        Map<String, Map<String, String>> moduleWiseSubProps = new HashMap();
        ConfigurableEnvironment confEnv = (ConfigurableEnvironment)environment;
        Iterator var4 = confEnv.getPropertySources().iterator();

        while(true) {
            PropertySource propertySource;
            do {
                if (!var4.hasNext()) {
                    return moduleWiseSubProps;
                }

                propertySource = (PropertySource)var4.next();
            } while(!(propertySource instanceof EnumerablePropertySource));

            String[] var6 = ((EnumerablePropertySource)propertySource).getPropertyNames();
            int var7 = var6.length;

            for(int var8 = 0; var8 < var7; ++var8) {
                String name = var6[var8];
                int separatorIndex = name.indexOf(46);
                String propName;
                if (separatorIndex > 0 && (propName = name.substring(separatorIndex + 1)).startsWith(keyPrefix)) {
                    String moduleName = name.substring(0, separatorIndex);
                    String propNameWithoutPrefix = propName.substring(keyPrefix.length());
                    moduleWiseSubProps.computeIfAbsent(moduleName, (key) -> {
                        return new HashMap();
                    });
                    ((Map)moduleWiseSubProps.get(moduleName)).put(propNameWithoutPrefix, confEnv.getProperty(name));
                }
            }
        }
    }

    public static Map<String, String> readModuleWisePropertyValues(Environment environment, String key) {
        Map<String, String> moduleWisePropValues = new HashMap();
        ConfigurableEnvironment confEnv = (ConfigurableEnvironment)environment;
        Iterator var4 = confEnv.getPropertySources().iterator();

        while(true) {
            PropertySource propertySource;
            do {
                if (!var4.hasNext()) {
                    return moduleWisePropValues;
                }

                propertySource = (PropertySource)var4.next();
            } while(!(propertySource instanceof EnumerablePropertySource));

            String[] var6 = ((EnumerablePropertySource)propertySource).getPropertyNames();
            int var7 = var6.length;

            for(int var8 = 0; var8 < var7; ++var8) {
                String name = var6[var8];
                if (name.endsWith(key)) {
                    int keyPrefixIndex = name.lastIndexOf(key);
                    String moduleName = name.substring(0, keyPrefixIndex - 1);
                    moduleWisePropValues.put(moduleName, confEnv.getProperty(name));
                }
            }
        }
    }
}
