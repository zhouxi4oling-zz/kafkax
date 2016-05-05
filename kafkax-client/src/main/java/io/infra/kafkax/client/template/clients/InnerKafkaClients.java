package io.infra.kafkax.client.template.clients;

import io.infra.kafkax.client.config.KafkaConfigs;
import io.infra.kafkax.client.consumer.KafkaConsumerTemplate;
import io.infra.kafkax.client.producer.KafkaProducerTemplate;

/**
 * Created by zhouxiaoling on 16/3/9.
 */
public interface InnerKafkaClients {

	KafkaProducerTemplate buildKafkaProducerTemplate(KafkaConfigs configs);

	KafkaConsumerTemplate buildKafkaConsumerTemplate(KafkaConfigs configs);

	void shutdown();

}
