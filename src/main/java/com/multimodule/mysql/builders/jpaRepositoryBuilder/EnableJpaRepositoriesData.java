package com.multimodule.mysql.builders.jpaRepositoryBuilder;


import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.core.type.classreading.AnnotationMetadataReadingVisitor;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Indexed;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.transaction.annotation.TransactionManagementConfigurationSelector;

import java.lang.reflect.Method;
import java.util.*;


public class EnableJpaRepositoriesData extends AnnotationMetadataReadingVisitor implements AnnotationMetadata {

    private Map<String, ?> data;

    private void initIt() throws NoSuchMethodException, ClassNotFoundException {

        //################## protected final Set<String> annotationSet = new LinkedHashSet<>(4);
        annotationSet.add(Configuration.class.getCanonicalName());
        annotationSet.add(EnableTransactionManagement.class.getCanonicalName());
        annotationSet.add(EnableJpaRepositories.class.getCanonicalName());

        //################## protected final Map<String, Set<String>> metaAnnotationMap = new LinkedHashMap<>(4);
        metaAnnotationMap.put(Configuration.class.getCanonicalName(),
                new LinkedHashSet<>(Arrays.asList(
                        Component.class.getCanonicalName(),
                        Indexed.class.getCanonicalName()
                )));
        metaAnnotationMap.put(EnableTransactionManagement.class.getCanonicalName(),
                new LinkedHashSet<>(Arrays.asList(
                        Import.class.getCanonicalName()
                )));
        metaAnnotationMap.put(EnableJpaRepositories.class.getCanonicalName(),
                new LinkedHashSet<>(Arrays.asList(
                        Import.class.getCanonicalName()
                )));

        //################## protected final LinkedMultiValueMap<String, AnnotationAttributes> attributesMap = new LinkedMultiValueMap<>(4);
        attributesMap.put(Configuration.class.getCanonicalName(),
                new LinkedList<AnnotationAttributes>() {{
                    add(new AnnotationAttributes(new LinkedHashMap<String, Object>() {{
                        put("value", defaultFor(Configuration.class, "value"));
                    }}));
                }});

        attributesMap.put(Component.class.getCanonicalName(),
                new LinkedList<AnnotationAttributes>() {{
                    add(new AnnotationAttributes(new LinkedHashMap<String, Object>() {{
                        put("value", defaultFor(Component.class, "value"));
                    }}));
                }});
        attributesMap.put(Indexed.class.getCanonicalName(),
                new LinkedList<AnnotationAttributes>() {{
                    add(new AnnotationAttributes(new LinkedHashMap<String, Object>() {{
                    }}));
                }});
        attributesMap.put(EnableTransactionManagement.class.getCanonicalName(),
                new LinkedList<AnnotationAttributes>() {{
                    add(new AnnotationAttributes(new LinkedHashMap<String, Object>() {{
                        put("order", defaultFor(EnableTransactionManagement.class, "order"));
                        put("mode", defaultFor(EnableTransactionManagement.class, "mode"));
                        put("proxyTargetClass", defaultFor(EnableTransactionManagement.class, "proxyTargetClass"));
                    }}));
                }});
        attributesMap.put(Import.class.getCanonicalName(),
                new LinkedList<AnnotationAttributes>() {{
                    add(new AnnotationAttributes(new LinkedHashMap<String, Object>() {{
                        put("value", new Class<?>[]{TransactionManagementConfigurationSelector.class});
                    }}));
                    add(new AnnotationAttributes(new LinkedHashMap<String, Object>() {{
                        put("value", new Class<?>[]{Class.forName("org.springframework.data.jpa.repository.config.JpaRepositoriesRegistrar")});
                    }}));
                }});

        attributesMap.put(EnableJpaRepositories.class.getCanonicalName(),
                new LinkedList<AnnotationAttributes>() {{
                    add(new AnnotationAttributes(new LinkedHashMap<String, Object>() {{
                        put("basePackages", data.get("basePackages"));
                        put("value", defaultFor(EnableJpaRepositories.class, "value"));
                        put("excludeFilters", new AnnotationAttributes[]{});
                        put("includeFilters", new AnnotationAttributes[]{});
                        put("basePackageClasses", defaultFor(EnableJpaRepositories.class, "basePackageClasses"));
                        put("transactionManagerRef", data.get("transactionManagerRef"));
                        put("considerNestedRepositories", defaultFor(EnableJpaRepositories.class, "considerNestedRepositories"));
                        put("namedQueriesLocation", defaultFor(EnableJpaRepositories.class, "namedQueriesLocation"));
                        put("queryLookupStrategy", defaultFor(EnableJpaRepositories.class, "queryLookupStrategy"));
                        put("entityManagerFactoryRef", data.get("entityManagerFactoryRef"));
                        put("enableDefaultTransactions", defaultFor(EnableJpaRepositories.class, "enableDefaultTransactions"));
                        put("repositoryImplementationPostfix", defaultFor(EnableJpaRepositories.class, "repositoryImplementationPostfix"));
                        put("repositoryFactoryBeanClass", defaultFor(EnableJpaRepositories.class, "repositoryFactoryBeanClass"));

                    }}));
                }});


        //##################
    }

    public EnableJpaRepositoriesData(@Nullable ClassLoader classLoader, Map<String, ?> data) throws NoSuchMethodException, ClassNotFoundException {
        super(classLoader);
        this.data = data;
        this.initIt();
    }

    private Object defaultFor(Class<?> clazz, String methodName) throws NoSuchMethodException {
        Method method = clazz.getDeclaredMethod(methodName);
        return method.getDefaultValue();
    }
}