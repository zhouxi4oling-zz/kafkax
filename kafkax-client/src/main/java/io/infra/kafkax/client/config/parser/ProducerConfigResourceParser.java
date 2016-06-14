package io.infra.kafkax.client.config.parser;

import io.infra.kafkax.client.config.KafkaProducerConfig;

import java.util.Set;

/**
 * Created by zhouxiaoling on 16/3/21.
 */
public interface ProducerConfigResourceParser extends ConfigResourceParser {

    Set<KafkaProducerConfig> parse(Object bean, String beanName);

}
