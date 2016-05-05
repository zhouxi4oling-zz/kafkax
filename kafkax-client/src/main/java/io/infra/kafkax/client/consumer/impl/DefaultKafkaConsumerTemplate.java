package io.infra.kafkax.client.consumer.impl;

import io.infra.kafkax.client.message.recorder.MessageRecorder;
import io.infra.kafkax.client.template.CloseableKafkaTemplate;
import io.infra.kafkax.client.config.KafkaConfigs;
import io.infra.kafkax.client.config.KafkaConsumerConfig;
import io.infra.kafkax.client.consumer.KafkaConsumerTemplate;
import io.infra.kafkax.client.exception.KafkaRuntimeException;
import io.infra.kafkax.client.message.Message;
import org.apache.kafka.clients.consumer.*;
import org.apache.kafka.common.TopicPartition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created by zhouxiaoling on 16/3/21.
 */
public class DefaultKafkaConsumerTemplate implements KafkaConsumerTemplate, CloseableKafkaTemplate {

    private final Logger logger = LoggerFactory.getLogger(DefaultKafkaConsumerTemplate.class);

    private KafkaConsumerRunner kafkaConsumerRunner;

    private boolean closed = false;
    private KafkaConfigs configs;

    public DefaultKafkaConsumerTemplate(KafkaConfigs configs) {
        this.configs = configs;
        this.kafkaConsumerRunner = new KafkaConsumerRunner();
        new Thread(this.kafkaConsumerRunner).start();
    }

    public synchronized void close() {
        if (!closed) {
            logger.info("Consumer is going to be closed.");
            this.kafkaConsumerRunner.shutdown();
            closed = true;
        }
    }

    class KafkaConsumerRunner implements Runnable {

        private AtomicBoolean closed = new AtomicBoolean(false);

        private KafkaConsumer<String, byte[]> consumer;
        private KafkaMessageProcessor[] kafkaMessageProcessors;

        private Map<TopicPartition, OffsetAndMetadata> map;

        public KafkaConsumerRunner() {
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
                kafkaMessageProcessors[i] = new KafkaMessageProcessor();
            }
        }

        public void run() {
            try {
                while (!closed.get()) {
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

        class KafkaMessageProcessor {

            private ExecutorService executorService;

            public KafkaMessageProcessor() {
                executorService = Executors.newSingleThreadExecutor();
            }

            public void shutdown() {
                executorService.shutdown();
            }

            public void process(ConsumerRecord<String, byte[]> record) {
                try {
                    Future<Boolean> f = executorService.submit(new Processor(record));
                    // TODO: 与业务端耦合的Offset提交机制
                } catch (Exception e) {
                    logger.error("Executor exception", e);
                }
            }

            class Processor implements Callable<Boolean> {

                private ConsumerRecord<String, byte[]> consumerRecord;

                public Processor(ConsumerRecord<String, byte[]> consumerRecord) {
                    this.consumerRecord = consumerRecord;
                }

                @Override
                public Boolean call() throws Exception {

                    // TODO: 待改造KafkaRecord结构,支持自定义MetaData即可.
                    Message message = MessageRecorder.getInstance().unrecord(consumerRecord);

                    // TODO: 不严谨但错误概率不大
                    map.put(new TopicPartition(message.getTopic(), message.getPartition()), new OffsetAndMetadata(message.getOffset()));

                    KafkaConsumerConfig kafkaConsumerConfig = configs.getKafkaConsumerConfig(message.getTopic(), message.getSelectKey());

                    if (kafkaConsumerConfig == null) {
                        logger.warn("No processor found, message discarded.");
                        return Boolean.TRUE;
                    }

                    Method method = kafkaConsumerConfig.getAnnotatedMethod();
                    Type type = method.getGenericParameterTypes()[0];
                    message = MessageRecorder.getInstance().unrecord(consumerRecord, type);

                    try {
                        method.invoke(kafkaConsumerConfig.getBean(), message);
                    } catch (Exception e) {
                        logger.error("Error on message processing", e);
                    }

                    return Boolean.TRUE;
                }

            }

        }

    }

}