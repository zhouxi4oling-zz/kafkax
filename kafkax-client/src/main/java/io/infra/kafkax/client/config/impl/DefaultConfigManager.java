package io.infra.kafkax.client.config.impl;

import io.infra.kafkax.client.config.ConfigManager;
import io.infra.kafkax.client.config.KafkaConfigs;
import io.infra.kafkax.client.config.KafkaConsumerConfig;
import io.infra.kafkax.client.config.KafkaProducerConfig;
import io.infra.kafkax.client.config.parser.ConsumerConfigResourceParser;
import io.infra.kafkax.client.config.parser.ProducerConfigResourceParser;
import io.infra.kafkax.client.config.parser.impl.DefaultConsumerConfigResourceParser;
import io.infra.kafkax.client.config.parser.impl.DefaultProducerConfigResourceParser;
import io.infra.kafkax.client.exception.KafkaRuntimeException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.Set;

/**
 * Created by zhouxiaoling on 16/3/21.
 */
public abstract class DefaultConfigManager implements ConfigManager {

    private final Logger logger = LoggerFactory.getLogger(DefaultConfigManager.class);

    private static final String SYSTEM_PROPERTIES = "classpath:system.properties";

    private Properties config;

    private String configLocation;

    private ProducerConfigResourceParser producerParser;
    private ConsumerConfigResourceParser consumerParser;

    public void init() {
        ResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
        Resource resource = resolver.getResource(SYSTEM_PROPERTIES);
        InputStream in = null;
        config = new Properties();
        try {
            in = resource.getInputStream();
            config.load(in);
        } catch (IOException e) {
            logger.error("error on loading system configs", e);
        } finally {
            try {
                if (in != null) {
                    in.close();
                }
            } catch (IOException e) {
                logger.error("error", e);
            }
        }

        try {
            // 局部配置覆盖全局配置
            config.putAll(loadConfig(configLocation));
        } catch (IOException e) {
            logger.error("error on loading config at " + configLocation, e);
            throw new KafkaRuntimeException(e);
        }

        KafkaConfigs.get().init(config);

        this.producerParser = new DefaultProducerConfigResourceParser();
        this.consumerParser = new DefaultConsumerConfigResourceParser();
    }

    public void setConfigLocation(String configLocation) {
        this.configLocation = configLocation;
    }

    @Override
    public Set<KafkaProducerConfig> configKafkaProducers(Object bean, String beanName) {
        Set<KafkaProducerConfig> set = producerParser.parse(bean, beanName);
        KafkaConfigs.get().addProducerConfigs(set);
        return set;
    }

    @Override
    public Set<KafkaConsumerConfig> configKafkaConsumers(Object bean, String beanName) {
        Set<KafkaConsumerConfig> set = consumerParser.parse(bean, beanName);
        KafkaConfigs.get().addConsumerConfigs(set);
        return set;
    }

    protected abstract Properties loadConfig(String location) throws IOException;

}
