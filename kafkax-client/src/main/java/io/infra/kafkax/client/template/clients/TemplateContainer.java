package io.infra.kafkax.client.template.clients;

import io.infra.kafkax.client.config.KafkaConsumerConfig;
import io.infra.kafkax.client.config.KafkaProducerConfig;
import io.infra.kafkax.client.consumer.KafkaConsumerTemplate;
import io.infra.kafkax.client.consumer.impl.DefaultKafkaConsumerTemplate;
import io.infra.kafkax.client.producer.TopicBindedKafkaProducerTemplate;
import io.infra.kafkax.client.producer.impl.DefaultTopicBindedKafkaProducerTemplate;
import io.infra.kafkax.client.template.CloseableKafkaTemplate;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ReflectionUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Created by zhouxiaoling on 16/3/22.
 */
public class TemplateContainer implements KafkaClients, InnerKafkaClients {

    private static TemplateContainer instance = new TemplateContainer();

    private TemplateContainer() {
    }

    public static TemplateContainer get() {
        return instance;
    }

    private KafkaConsumerTemplate kafkaConsumerTemplate;
    private Map<String, TopicBindedKafkaProducerTemplate> topicBindedKafkaProducerTemplateCache = new HashMap<String, TopicBindedKafkaProducerTemplate>();

    private synchronized void buildTopicBindedKafkaProducerTemplate(Set<KafkaProducerConfig> configs) {
        for (KafkaProducerConfig config : configs) {
            TopicBindedKafkaProducerTemplate template = topicBindedKafkaProducerTemplateCache.get(config.getTopic());
            if (template == null) {
                template = new DefaultTopicBindedKafkaProducerTemplate(config.getTopic());
                topicBindedKafkaProducerTemplateCache.put(config.getTopic(), template);
            }
            ReflectionUtils.makeAccessible(config.getAnnotatedField());
            ReflectionUtils.setField(config.getAnnotatedField(), config.getBean(), template);
        }
    }

    @Override
    public synchronized void buildKafkaProducerTemplates(Set<KafkaProducerConfig> configs) {
        if (!CollectionUtils.isEmpty(configs)) {
            buildTopicBindedKafkaProducerTemplate(configs);
        }
    }

    @Override
    public synchronized void buildKafkaConsumerTemplates(Set<KafkaConsumerConfig> configs) {
        if (!CollectionUtils.isEmpty(configs)) {
            if (kafkaConsumerTemplate == null) {
                kafkaConsumerTemplate = new DefaultKafkaConsumerTemplate();
            } else {
                kafkaConsumerTemplate.refreshSubscribedTopics();
            }
        }
    }

    public void shutdown() {
        for (TopicBindedKafkaProducerTemplate topicBindedKafkaProducerTemplate : topicBindedKafkaProducerTemplateCache.values()) {
            ((CloseableKafkaTemplate) topicBindedKafkaProducerTemplate).close();
        }
        if (kafkaConsumerTemplate != null) {
            ((CloseableKafkaTemplate) kafkaConsumerTemplate).close();
        }
    }

}
