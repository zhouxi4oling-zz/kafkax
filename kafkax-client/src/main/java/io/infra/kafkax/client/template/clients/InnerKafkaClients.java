package io.infra.kafkax.client.template.clients;

import io.infra.kafkax.client.config.KafkaConsumerConfig;
import io.infra.kafkax.client.config.KafkaProducerConfig;

import java.util.Set;

/**
 * Created by zhouxiaoling on 16/3/9.
 */
public interface InnerKafkaClients {

    void buildKafkaProducerTemplates(Set<KafkaProducerConfig> configs);

    void buildKafkaConsumerTemplates(Set<KafkaConsumerConfig> configs);

    void shutdown();

}
