package io.infra.kafkax.client.template.clients;

import java.util.Map;

import io.infra.kafkax.client.consumer.KafkaConsumerTemplate;
import io.infra.kafkax.client.producer.KafkaProducerTemplate;

/**
 * Created by zhouxiaoling on 16/3/22.
 */
public interface KafkaClients {

	KafkaProducerTemplate buildKafkaProducerTemplate(Map<String, String> configs);

	KafkaConsumerTemplate buildKafkaConsumerTemplate(Map<String, String> configs);

}
