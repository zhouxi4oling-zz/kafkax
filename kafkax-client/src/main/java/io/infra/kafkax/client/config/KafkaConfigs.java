package io.infra.kafkax.client.config;

import io.infra.kafkax.client.constants.Constants;
import io.infra.kafkax.client.exception.KafkaRuntimeException;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * Created by zhouxiaoling on 16/3/21.
 */
public class KafkaConfigs {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    // system.properties
    private String systemGroupId;
    private String systemAppId;
    // ----- system.properties

    // kafka.properties
    private String kafkaServers;
    private int kafkaConsumerPollingInterval;
    private int kafkaConsumerPollingTimeout;
    private int kafkaConsumerConcurrency;
    // ----- kafka.properties

    private String kafkaGroupId;

    private Set<KafkaProducerConfig> producerConfigs = new HashSet<KafkaProducerConfig>();
    private ReadWriteLock producerConfigLock = new ReentrantReadWriteLock();

    private Set<KafkaConsumerConfig> consumerConfigs = new HashSet<KafkaConsumerConfig>();
    private ReadWriteLock consumerConfigLock = new ReentrantReadWriteLock();

    public KafkaConfigs(Properties config) {
        setSystemAppId(config.getProperty(Constants.SYSTEM_APPID));
        setSystemGroupId(config.getProperty(Constants.SYSTEM_GROUPID));
        setKafkaGroupId(getSystemGroupId() + "." + getSystemAppId());
        setKafkaServers(config.getProperty(Constants.KAFKA_SERVERS));
        setKafkaConsumerPollingInterval(Integer.valueOf(config.getProperty(Constants.KAFKA_CONSUMER_POLLINGINTERVAL, "1000")));
        setKafkaConsumerPollingTimeout(Integer.valueOf(config.getProperty(Constants.KAFKA_CONSUMER_POLLINGTIMEOUT, "100")));
        setKafkaConsumerConcurrency(Integer.valueOf(config.getProperty(Constants.KAFKA_CONSUMER_CONCURRENCY, "5")));
        validate();
    }

    private void validate() {
        // TODO: 16/5/9 默认心跳超时时间30000,后续开放配置.
        if ((getKafkaConsumerPollingTimeout() + getKafkaConsumerPollingInterval()) >= 30000) {
            throw new KafkaRuntimeException("pollingInterval + pollingTimeout should <= 30000");
        }
    }

    public KafkaConsumerConfig getKafkaConsumerConfig(String topic, String selectKey) {
        KafkaConsumerConfig kafkaConsumerConfig = null;
        consumerConfigLock.readLock().lock();
        try {
            for (KafkaConsumerConfig cfg : consumerConfigs) {
                if (topic.equals(cfg.getTopic()) && selectKey.equals(cfg.getSelectKey())) {
                    kafkaConsumerConfig = cfg;
                    break;
                }
            }
        } finally {
            consumerConfigLock.readLock().unlock();
        }
        return kafkaConsumerConfig;
    }

    public Map<String, Object> getKafkaProducerGlobalConfigs() {
        Map<String, Object> map = new HashMap<String, Object>();
        map.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, getKafkaServers());
        map.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringSerializer");
        map.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.ByteArraySerializer");
        map.put(ProducerConfig.ACKS_CONFIG, "1");
        return map;
    }

    public Map<String, Object> getKafkaConsumerGlobalConfigs() {
        Map<String, Object> map = new HashMap<String, Object>();
        map.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, getKafkaServers());
        map.put(ConsumerConfig.GROUP_ID_CONFIG, getKafkaGroupId());
        map.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringDeserializer");
        map.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.ByteArrayDeserializer");
        map.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, "false");
        return map;
    }

    public List<String> getSubscribedTopics() {
        List<String> topics = new ArrayList<String>();
        consumerConfigLock.readLock().lock();
        try {
            for (KafkaConsumerConfig config : consumerConfigs) {
                topics.add(config.getTopic());
            }
        } finally {
            consumerConfigLock.readLock().unlock();
        }
        return topics;
    }

    public KafkaConfigs merge(KafkaConfigs configs) {
        addProducerConfigs(configs.producerConfigs);
        addConsumerConfigs(configs.consumerConfigs);
        return this;
    }

    public void addProducerConfig(KafkaProducerConfig config) {
        producerConfigLock.writeLock().lock();
        try {
            producerConfigs.add(config);
        } finally {
            producerConfigLock.writeLock().unlock();
        }
    }

    public void addConsumerConfig(KafkaConsumerConfig config) {
        consumerConfigLock.writeLock().lock();
        try {
            consumerConfigs.add(config);
        } finally {
            consumerConfigLock.writeLock().unlock();
        }
    }

    public void addProducerConfigs(Set<KafkaProducerConfig> configs) {
        producerConfigLock.writeLock().lock();
        try {
            producerConfigs.addAll(configs);
        } finally {
            producerConfigLock.writeLock().unlock();
        }
    }

    public void addConsumerConfigs(Set<KafkaConsumerConfig> configs) {
        consumerConfigLock.writeLock().lock();
        try {
            consumerConfigs.addAll(configs);
        } finally {
            consumerConfigLock.writeLock().unlock();
        }
    }

    public Set<KafkaConsumerConfig> getConsumerConfigs() {
        return Collections.unmodifiableSet(consumerConfigs);
    }

    public Set<KafkaProducerConfig> getProducerConfigs() {
        return Collections.unmodifiableSet(producerConfigs);
    }

    public String getSystemGroupId() {
        return systemGroupId;
    }

    public void setSystemGroupId(String systemGroupId) {
        this.systemGroupId = systemGroupId;
    }

    public String getSystemAppId() {
        return systemAppId;
    }

    private void setSystemAppId(String systemAppId) {
        this.systemAppId = systemAppId;
    }

    public String getKafkaServers() {
        return kafkaServers;
    }

    private void setKafkaServers(String kafkaServers) {
        this.kafkaServers = kafkaServers;
    }

    public int getKafkaConsumerConcurrency() {
        return kafkaConsumerConcurrency;
    }

    public void setKafkaConsumerConcurrency(int kafkaConsumerConcurrency) {
        this.kafkaConsumerConcurrency = kafkaConsumerConcurrency;
    }

    public int getKafkaConsumerPollingInterval() {
        return kafkaConsumerPollingInterval;
    }

    public void setKafkaConsumerPollingInterval(int kafkaConsumerPollingInterval) {
        this.kafkaConsumerPollingInterval = kafkaConsumerPollingInterval;
    }

    public int getKafkaConsumerPollingTimeout() {
        return kafkaConsumerPollingTimeout;
    }

    public void setKafkaConsumerPollingTimeout(int kafkaConsumerPollingTimeout) {
        this.kafkaConsumerPollingTimeout = kafkaConsumerPollingTimeout;
    }

    public String getKafkaGroupId() {
        return kafkaGroupId;
    }

    public void setKafkaGroupId(String kafkaGroupId) {
        this.kafkaGroupId = kafkaGroupId;
    }

}
