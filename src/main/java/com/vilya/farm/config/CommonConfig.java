package com.vilya.farm.config;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MapPropertySource;
import org.springframework.core.env.MutablePropertySources;
import org.springframework.core.env.PropertySource;
@Configuration
@Order(Ordered.HIGHEST_PRECEDENCE + 1)
public class CommonConfig {
  private static final String PROPERTY_SOURCE_NAME = "mostPreferredProperties";
  private final String instanceId;
  public CommonConfig() {
    instanceId = UUID.randomUUID().toString();
  }
  @Bean
  public BeanDefinitionRegistryPostProcessor postProcessEnvironment(
      ConfigurableEnvironment environment) {
    Map<String, Object> props = new HashMap<>();
    props.put("app.instance.id", instanceId);
    addOrReplace(environment.getPropertySources(), props);
    return new BeanDefinitionRegistryPostProcessor() {
      @Override
      public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry registry)
          throws BeansException {
        AbstractBeanDefinition bd =
            BeanDefinitionBuilder.genericBeanDefinition(String.class)
                .addConstructorArgValue(instanceId)
                .getBeanDefinition();
        registry.registerBeanDefinition("instanceId", bd);
      }
      @Override
      public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory)
          throws BeansException {}
    };
  }
  private void addOrReplace(MutablePropertySources propertySources, Map<String, Object> map) {
    MapPropertySource target = null;
    if (propertySources.contains(PROPERTY_SOURCE_NAME)) {
      PropertySource<?> source = propertySources.get(PROPERTY_SOURCE_NAME);
      if (source instanceof MapPropertySource) {
        target = (MapPropertySource) source;
        for (String key : map.keySet()) {
          if (!target.containsProperty(key)) {
            target.getSource().put(key, map.get(key));
          }
        }
      }
    }
    if (target == null) {
      target = new MapPropertySource(PROPERTY_SOURCE_NAME, map);
    }
    if (!propertySources.contains(PROPERTY_SOURCE_NAME)) {
      propertySources.addFirst(target);
    }
  }
}


