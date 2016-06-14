package io.infra.kafkax.client.consumer.impl;

import io.infra.kafkax.client.config.KafkaConfigs;
import io.infra.kafkax.client.config.KafkaConsumerConfig;
import io.infra.kafkax.client.message.Message;
import io.infra.kafkax.client.message.recorder.MessageRecorder;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.OffsetAndMetadata;
import org.apache.kafka.common.TopicPartition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * Created by zhouxiaoling on 16/5/6.
 */
public class KafkaMessageProcessor {

    private final Logger logger = LoggerFactory.getLogger(KafkaMessageProcessor.class);

    private ExecutorService executorService;
    private Map<TopicPartition, OffsetAndMetadata> map;

    public KafkaMessageProcessor(Map<TopicPartition, OffsetAndMetadata> map) {
        this.map = map;
        executorService = Executors.newSingleThreadExecutor();
        logger.debug("new message processor initialized");
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
            map.put(new TopicPartition(message.getTopic(), message.getPartition()), new OffsetAndMetadata(message.getOffset() + 1));

            KafkaConsumerConfig kafkaConsumerConfig = KafkaConfigs.get().getKafkaConsumerConfig(message.getTopic(), message.getSelectKey());

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