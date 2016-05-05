package io.infra.kafkax.client.config;

/**
 * Created by zhouxiaoling on 16/3/23.
 */
public interface ConfigManagerFactory {

	ConfigManager getConfigManager(String key);

}
