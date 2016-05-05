package io.infra.kafkax.client.config;

import java.util.Map;

/**
 * Created by zhouxiaoling on 16/3/10.
 */
public interface ConfigManager {

	KafkaConfigs config(Object bean, String beanName);

	KafkaConfigs config(Map<String, String> configs);

	boolean init();

	KafkaConfigs configs();

	void setConfigLocation(String location);

}
