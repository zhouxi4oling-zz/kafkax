package io.infra.kafkax.client.config.parser.impl;

import io.infra.kafkax.client.config.KafkaConfigs;
import io.infra.kafkax.client.config.KafkaProducerConfig;
import io.infra.kafkax.client.config.annotation.KafkaProducer;
import io.infra.kafkax.client.config.parser.ProducerConfigResourceParser;
import io.infra.kafkax.client.exception.KafkaRuntimeException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import java.lang.reflect.Field;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Created by zhouxiaoling on 16/3/21.
 */
public class DefaultProducerConfigResourceParser implements ProducerConfigResourceParser {

    private final Logger logger = LoggerFactory.getLogger(DefaultProducerConfigResourceParser.class);

    private KafkaConfigs configs;

    public DefaultProducerConfigResourceParser(KafkaConfigs configs) {
        this.configs = configs;
    }

    public Set<KafkaProducerConfig> parse(Object bean, String beanName) {

        Set<KafkaProducerConfig> producerConfigs = new HashSet<KafkaProducerConfig>();

        Field fields[] = bean.getClass().getDeclaredFields();

        for (Field field : fields) {
            KafkaProducer annotation = field.getAnnotation(KafkaProducer.class);

            if (annotation != null) {
                String topic = StringUtils.trimWhitespace(annotation.topic());
                if (!StringUtils.hasText(topic)) {
                    throw new KafkaRuntimeException("@KafkaProducer's topic is required [" + field + "]");
                }

                KafkaProducerConfig config = new KafkaProducerConfig();
                config.setBean(bean);
                config.setBeanName(beanName);
                config.setTopic(topic);
                config.setAnnotatedField(field);
                producerConfigs.add(config);
            }
        }

        return producerConfigs;

    }

    public Set<KafkaProducerConfig> parse(Map<String, String> configs) {
        return null;
    }

}
