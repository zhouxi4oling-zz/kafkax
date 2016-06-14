package io.infra.kafkax.client.producer;

import io.infra.kafkax.client.config.KafkaConfigs;
import org.apache.kafka.clients.producer.KafkaProducer;

/*
 * 此类设计待改进
 */
public class KafkaProducerFactory {

    private static KafkaProducer<String, byte[]> producer;

    public synchronized static KafkaProducer<String, byte[]> createKafkaProducer() {
        if (producer == null) {
            producer = new KafkaProducer<>(KafkaConfigs.get().getKafkaProducerGlobalConfigs());
        }
        return producer;
    }

}
