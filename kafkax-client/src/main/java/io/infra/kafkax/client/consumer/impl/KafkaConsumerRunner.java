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
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * Created by zhouxiaoling on 16/5/6.
 */
public class KafkaConsumerRunner implements Runnable {

    private final Logger logger = LoggerFactory.getLogger(KafkaConsumerRunner.class);

    private AtomicBoolean closed = new AtomicBoolean(false);

    private KafkaConsumer<String, byte[]> consumer;
    private KafkaMessageProcessor[] kafkaMessageProcessors;

    private Map<TopicPartition, OffsetAndMetadata> map;

    private ReadWriteLock lock = new ReentrantReadWriteLock();

    public KafkaConsumerRunner() {
        this.map = new ConcurrentHashMap<>();
        this.consumer = new KafkaConsumer<>(KafkaConfigs.get().getKafkaConsumerGlobalConfigs());
        subscribe();
        this.kafkaMessageProcessors = new KafkaMessageProcessor[KafkaConfigs.get().getKafkaConsumerConcurrency()];
        for (int i = 0; i < kafkaMessageProcessors.length; i++) {
            kafkaMessageProcessors[i] = new KafkaMessageProcessor(map);
        }
    }

    private void subscribe() {
        this.consumer.subscribe(KafkaConfigs.get().getSubscribedTopics(), new ConsumerRebalanceListener() {
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
    }

    public void run() {
        try {
            while (!closed.get()) {
                lock.readLock().lock();
                try {
                    logger.debug("start polling messages");
                    ConsumerRecords<String, byte[]> records = consumer.poll(KafkaConfigs.get().getKafkaConsumerPollingTimeout());
                    for (ConsumerRecord<String, byte[]> record : records) {
                        int x = (new TopicPartition(record.topic(), record.partition()).hashCode()) % (kafkaMessageProcessors.length);
                        kafkaMessageProcessors[x].process(record);
                    }
                    Thread.sleep(KafkaConfigs.get().getKafkaConsumerPollingInterval());
                    consumer.commitAsync(map, new OffsetCommitCallback() {
                        @Override
                        public void onComplete(Map<TopicPartition, OffsetAndMetadata> offsets, Exception exception) {
                            if (exception != null) {
                                logger.error("Commit failed for offsets {}", offsets, exception);
                            }
                        }
                    });
                } finally {
                    lock.readLock().unlock();
                }
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

    public void updateSubscribedTopics() {
        lock.writeLock().lock();
        try {
            subscribe();
        } finally {
            lock.writeLock().unlock();
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
