package io.infra.kafkax.client.consumer.impl;

import io.infra.kafkax.client.consumer.KafkaConsumerTemplate;
import io.infra.kafkax.client.template.CloseableKafkaTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by zhouxiaoling on 16/3/21.
 */
public class DefaultKafkaConsumerTemplate implements KafkaConsumerTemplate, CloseableKafkaTemplate {

    private final Logger logger = LoggerFactory.getLogger(DefaultKafkaConsumerTemplate.class);

    private KafkaConsumerRunner kafkaConsumerRunner;

    private boolean closed = false;

    public DefaultKafkaConsumerTemplate() {
        this.kafkaConsumerRunner = new KafkaConsumerRunner();
        new Thread(this.kafkaConsumerRunner).start();
    }

    @Override
    public void refreshSubscribedTopics() {
        this.kafkaConsumerRunner.updateSubscribedTopics();
    }

    public synchronized void close() {
        if (!closed) {
            logger.info("Consumer is going to be closed.");
            this.kafkaConsumerRunner.shutdown();
            closed = true;
        }
    }

}