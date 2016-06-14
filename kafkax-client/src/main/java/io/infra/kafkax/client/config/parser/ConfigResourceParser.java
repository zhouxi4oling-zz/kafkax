package io.infra.kafkax.client.config.parser;

import io.infra.kafkax.client.config.KafkaConfig;

import java.util.Set;

/**
 * Created by zhouxiaoling on 16/3/21.
 */
public interface ConfigResourceParser {

    Set<? extends KafkaConfig> parse(Object bean, String beanName);

}
