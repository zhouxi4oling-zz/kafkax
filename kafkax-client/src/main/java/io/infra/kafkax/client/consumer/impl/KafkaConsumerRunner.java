package io.infra.kafkax.client.consumer.impl;

import io.infra.kafkax.client.config.KafkaConfigs;
import io.infra.kafkax.client.exception.KafkaRuntimeException;
import org.apache.kafka.clients.consumer.*;
import org.apache.kafka.common.TopicPartition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created by zhouxiaoling on 16/5/6.
 */
public class KafkaConsumerRunner implements Runnable {

    private final Logger logger = LoggerFactory.getLogger(KafkaConsumerRunner.class);

    private KafkaConfigs configs;

    private AtomicBoolean closed = new AtomicBoolean(false);

    private KafkaConsumer<String, byte[]> consumer;
    private KafkaMessageProcessor[] kafkaMessageProcessors;

    private Map<TopicPartition, OffsetAndMetadata> map;

    public KafkaConsumerRunner(KafkaConfigs configs) {
        this.configs = configs;
        this.map = new ConcurrentHashMap<TopicPartition, OffsetAndMetadata>();
        this.consumer = new KafkaConsumer<String, byte[]>(configs.getKafkaConsumerGlobalConfigs());
        this.consumer.subscribe(configs.getSubscribedTopics(), new ConsumerRebalanceListener() {
            @Override
            public void onPartitionsRevoked(Collection<TopicPartition> partitions) {
                try {
                    consumer.commitSync(map);
                } catch (Exception e) {
                    logger.error("Commit failed for offsets {}", map, e);
                }
            }

            @Override
            public void onPartitionsAssigned(Collection<TopicPartition> partitions) {
            }
        });
        this.kafkaMessageProcessors = new KafkaMessageProcessor[configs.getKafkaConsumerConcurrency()];
        for (int i = 0; i < kafkaMessageProcessors.length; i++) {
            kafkaMessageProcessors[i] = new KafkaMessageProcessor(configs, map);
        }
    }

    public void run() {
        try {
            while (!closed.get()) {
                logger.debug("start polling messages");
                ConsumerRecords<String, byte[]> records = consumer.poll(configs.getKafkaConsumerPollingTimeout());
                for (ConsumerRecord<String, byte[]> record : records) {
                    int x = (new TopicPartition(record.topic(), record.partition()).hashCode()) % (kafkaMessageProcessors.length);
                    kafkaMessageProcessors[x].process(record);
                }
                Thread.sleep(configs.getKafkaConsumerPollingInterval());
                consumer.commitAsync(map, new OffsetCommitCallback() {
                    @Override
                    public void onComplete(Map<TopicPartition, OffsetAndMetadata> offsets, Exception exception) {
                        if (exception != null) {
                            logger.error("Commit failed for offsets {}", offsets, exception);
                        }
                    }
                });
            }
        } catch (Exception e) {
            logger.error("error", e);
            if (!closed.get()) {
                throw new KafkaRuntimeException(e);
            }
        } finally {
            try {
                consumer.commitSync(map);
            } catch (Exception e) {
                logger.error("Commit failed for offsets {}", map, e);
            } finally {
                consumer.close();
            }
        }
    }

    public void shutdown() {
        for (KafkaMessageProcessor kafkaMessageProcessor : kafkaMessageProcessors) {
            kafkaMessageProcessor.shutdown();
        }
        closed.set(true);
        consumer.wakeup();
    }


}
