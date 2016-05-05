package io.infra.kafkax.client.config.parser;

import io.infra.kafkax.client.config.KafkaConsumerConfig;

import java.util.Map;
import java.util.Set;

/**
 * Created by zhouxiaoling on 16/3/21.
 */
public interface ConsumerConfigResourceParser extends ConfigResourceParser {

    Set<KafkaConsumerConfig> parse(Object bean, String beanName);

    Set<KafkaConsumerConfig> parse(Map<String, String> configs);

}
