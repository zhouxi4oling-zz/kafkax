package io.infra.kafkax.client.config.impl;

import io.infra.kafkax.client.config.ConfigManager;
import io.infra.kafkax.client.config.parser.impl.DefaultConsumerConfigResourceParser;
import io.infra.kafkax.client.config.KafkaConfigs;
import io.infra.kafkax.client.config.parser.ConsumerConfigResourceParser;
import io.infra.kafkax.client.config.parser.ProducerConfigResourceParser;
import io.infra.kafkax.client.config.parser.impl.DefaultProducerConfigResourceParser;
import io.infra.kafkax.client.exception.KafkaRuntimeException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.Properties;

/**
 * Created by zhouxiaoling on 16/3/21.
 */
public abstract class DefaultConfigManager implements ConfigManager {

    private final Logger logger = LoggerFactory.getLogger(DefaultConfigManager.class);

    private static final String SYSTEM_PROPERTIES = "classpath:system.properties";

    /**
     * 原始配置，只读。
     */
    private Properties config;

    private String configLocation;

    /**
     * 全局只读配置，单例，线程安全。
     */
    private KafkaConfigs configs;

    private ProducerConfigResourceParser producerParser;
    private ConsumerConfigResourceParser consumerParser;

    private boolean initialized = false;

    /**
     * 启动时初始化，只初始化一次。
     */
    public synchronized boolean init() {
        if (this.initialized)
            return this.initialized;

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

        this.configs = new KafkaConfigs(config);
        this.producerParser = new DefaultProducerConfigResourceParser(configs);
        this.consumerParser = new DefaultConsumerConfigResourceParser(configs);
        this.initialized = true;
        return this.initialized;
    }

    /**
     * 设置配置文件位置，可能是本地也可能是远程，该方法在系统启动时被调用。
     */
    public void setConfigLocation(String configLocation) {
        this.configLocation = configLocation;
    }

    protected abstract Properties loadConfig(String location) throws IOException;

    /**
     * 解析静态配置（代码或配置文件中的配置），该方法在系统启动时被调用。
     */
    public KafkaConfigs config(Object bean, String beanName) {
        KafkaConfigs kafkaConfigs = new KafkaConfigs(config);
        kafkaConfigs.addProducerConfigs(producerParser.parse(bean, beanName));
        kafkaConfigs.addConsumerConfigs(consumerParser.parse(bean, beanName));
        this.configs.merge(kafkaConfigs);
        return kafkaConfigs;
    }

    /**
     * 解析动态配置（运行时，通过API配置），该方法在系统运行期间按需调用。
     */
    public KafkaConfigs config(Map<String, String> configs) {
        KafkaConfigs kafkaConfigs = new KafkaConfigs(config);
        kafkaConfigs.addProducerConfigs(producerParser.parse(configs));
        kafkaConfigs.addConsumerConfigs(consumerParser.parse(configs));
        this.configs.merge(kafkaConfigs);
        return kafkaConfigs;
    }

    public KafkaConfigs configs() {
        return this.configs;
    }

}
