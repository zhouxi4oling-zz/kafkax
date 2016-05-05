package io.infra.kafkax.client.config.parser.impl;

import io.infra.kafkax.client.config.KafkaConfigs;
import io.infra.kafkax.client.config.KafkaConsumerConfig;
import io.infra.kafkax.client.config.annotation.KafkaConsumer;
import io.infra.kafkax.client.config.parser.ConsumerConfigResourceParser;
import io.infra.kafkax.client.exception.KafkaRuntimeException;
import io.infra.kafkax.client.message.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Created by zhouxiaoling on 16/3/21.
 */
public class DefaultConsumerConfigResourceParser implements ConsumerConfigResourceParser {

    private final Logger logger = LoggerFactory.getLogger(DefaultConsumerConfigResourceParser.class);

    private KafkaConfigs configs;

    public DefaultConsumerConfigResourceParser(KafkaConfigs configs) {
        this.configs = configs;
    }

    public Set<KafkaConsumerConfig> parse(Object bean, String beanName) {

        Set<KafkaConsumerConfig> kafkaConsumerConfigs = new HashSet<KafkaConsumerConfig>();
        Class<?> targetClazz = bean.getClass();
        Method[] methods = targetClazz.getDeclaredMethods();

        Set<String> topicSelectKeySet = new HashSet<String>();

        for (int i = 0; i < methods.length; i++) {
            Method method = methods[i];
            KafkaConsumer annotation = method.getAnnotation(KafkaConsumer.class);

            if (annotation != null) {
                String topic = StringUtils.trimWhitespace(annotation.topic());
                String selectKey = StringUtils.trimWhitespace(annotation.selectKey());

                if (!StringUtils.hasText(topic)) {
                    throw new KafkaRuntimeException("@KafkaConsumer's topic is required [" + method + "]");
                }

                if (!StringUtils.hasText(selectKey)) {
                    throw new KafkaRuntimeException("@KafkaConsumer's selectKey is required [" + method + "]");
                }

                Type[] types = method.getGenericParameterTypes();
                if (types.length != 1 && !Message.class.isAssignableFrom(types[0].getClass())) {
                    throw new KafkaRuntimeException("@KafkaConsumer method [" + method + "] should only have 1 parameter and which type supposed to be Message<?>");
                }

                // 重复注解
                String str = topic + "&" + selectKey;
                if (topicSelectKeySet.contains(str)) {
                    throw new KafkaRuntimeException("duplicated definition: @KafkaConsumer(topic='" + topic + "', selectKey='" + selectKey + "')");
                }
                topicSelectKeySet.add(str);

                KafkaConsumerConfig config = new KafkaConsumerConfig();
                config.setBean(bean);
                config.setBeanName(beanName);
                config.setTopic(topic);
                config.setSelectKey(selectKey);
                config.setAnnotatedMethod(method);
                kafkaConsumerConfigs.add(config);
            }
        }

        return kafkaConsumerConfigs;

    }

    public Set<KafkaConsumerConfig> parse(Map<String, String> configs) {
        return null;
    }

}
