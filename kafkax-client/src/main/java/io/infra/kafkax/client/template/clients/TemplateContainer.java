package io.infra.kafkax.client.template.clients;

import io.infra.kafkax.client.config.ConfigManager;
import io.infra.kafkax.client.config.ConfigManagerFactory;
import io.infra.kafkax.client.config.KafkaConfigs;
import io.infra.kafkax.client.config.KafkaProducerConfig;
import io.infra.kafkax.client.config.impl.DefaultConfigManagerFactory;
import io.infra.kafkax.client.consumer.KafkaConsumerTemplate;
import io.infra.kafkax.client.consumer.impl.DefaultKafkaConsumerTemplate;
import io.infra.kafkax.client.producer.KafkaProducerTemplate;
import io.infra.kafkax.client.producer.TopicBindedKafkaProducerTemplate;
import io.infra.kafkax.client.producer.impl.DefaultKafkaProducerTemplate;
import io.infra.kafkax.client.producer.impl.DefaultTopicBindedKafkaProducerTemplate;
import io.infra.kafkax.client.template.CloseableKafkaTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ReflectionUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by zhouxiaoling on 16/3/22.
 */
public class TemplateContainer implements KafkaClients, InnerKafkaClients {

    private static TemplateContainer instance = new TemplateContainer();

    private TemplateContainer() {
    }

    public static TemplateContainer getInstance() {
        return instance;
    }

    private final Logger logger = LoggerFactory.getLogger(TemplateContainer.class);

    private ConfigManagerFactory configManagerFactory = new DefaultConfigManagerFactory();
    private ConfigManager configManager = configManagerFactory.getConfigManager("local");

    private KafkaProducerTemplate kafkaProducerTemplate;
    private KafkaConsumerTemplate kafkaConsumerTemplate;
    private Map<String, TopicBindedKafkaProducerTemplate> topicBindedKafkaProducerTemplateCache = new HashMap<String, TopicBindedKafkaProducerTemplate>();

    public synchronized KafkaProducerTemplate buildKafkaProducerTemplate(Map<String, String> configs) {
        KafkaConfigs kafkaConfigs = configManager.config(configs);
        return buildKafkaProducerTemplate(kafkaConfigs);
    }

    public synchronized KafkaConsumerTemplate buildKafkaConsumerTemplate(Map<String, String> configs) {
        KafkaConfigs kafkaConfigs = configManager.config(configs);
        return buildKafkaConsumerTemplate(kafkaConfigs);
    }

    public synchronized KafkaProducerTemplate buildKafkaProducerTemplate(KafkaConfigs configs) {
        if (CollectionUtils.isEmpty(configs.getProducerConfigs())) {
            return null;
        }
        buildTopicBindedKafkaProducerTemplate(configs);
        if (kafkaProducerTemplate == null) {
            kafkaProducerTemplate = new DefaultKafkaProducerTemplate(configs);
        }
        return kafkaProducerTemplate;
    }

    public synchronized KafkaConsumerTemplate buildKafkaConsumerTemplate(KafkaConfigs configs) {
        if (CollectionUtils.isEmpty(configs.getConsumerConfigs())) {
            return null;
        }
        if (kafkaConsumerTemplate == null) {
            kafkaConsumerTemplate = new DefaultKafkaConsumerTemplate(configs);
        }
        return kafkaConsumerTemplate;
    }

    private synchronized void buildTopicBindedKafkaProducerTemplate(KafkaConfigs configs) {
        for (KafkaProducerConfig config : configs.getProducerConfigs()) {
            TopicBindedKafkaProducerTemplate template = topicBindedKafkaProducerTemplateCache.get(config.getTopic());
            if (template == null) {
                template = new DefaultTopicBindedKafkaProducerTemplate(config.getTopic(), configs);
                topicBindedKafkaProducerTemplateCache.put(config.getTopic(), template);
            }
            ReflectionUtils.makeAccessible(config.getAnnotatedField());
            ReflectionUtils.setField(config.getAnnotatedField(), config.getBean(), template);
        }
    }

    public void shutdown() {
        if (kafkaProducerTemplate != null) {
            ((CloseableKafkaTemplate) kafkaProducerTemplate).close();
        }
        for (TopicBindedKafkaProducerTemplate topicBindedKafkaProducerTemplate : topicBindedKafkaProducerTemplateCache.values()) {
            ((CloseableKafkaTemplate) topicBindedKafkaProducerTemplate).close();
        }
        if (kafkaConsumerTemplate != null) {
            ((CloseableKafkaTemplate) kafkaConsumerTemplate).close();
        }
    }

}
