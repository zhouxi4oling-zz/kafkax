package io.infra.kafkax.client.config;

import java.util.Set;

/**
 * Created by zhouxiaoling on 16/3/10.
 */
public interface ConfigManager {

    void init();

    void setConfigLocation(String location);

    Set<KafkaProducerConfig> configKafkaProducers(Object bean, String beanName);

    Set<KafkaConsumerConfig> configKafkaConsumers(Object bean, String beanName);

}
