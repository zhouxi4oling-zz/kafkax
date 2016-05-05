package io.infra.kafkax.client.config.impl;

import io.infra.kafkax.client.config.ConfigManager;
import io.infra.kafkax.client.config.ConfigManagerFactory;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by zhouxiaoling on 16/3/22.
 */
public class DefaultConfigManagerFactory implements ConfigManagerFactory {

    private Map<String, ConfigManager> map = new HashMap<String, ConfigManager>();

    {
        map.put("local", new LocalConfigManager());
        map.put("remote", new RemoteConfigManager());
    }

    public ConfigManager getConfigManager(String key) {
        return map.get(key);
    }

}
