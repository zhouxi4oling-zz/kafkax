package io.infra.kafkax.client.config.impl;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.util.StringUtils;

/**
 * Created by zhouxiaoling on 16/3/21.
 */
public class LocalConfigManager extends DefaultConfigManager {

	private final Logger logger = LoggerFactory.getLogger(LocalConfigManager.class);

	private static final String KAFKA_PROPERTIES = "classpath:kafka.properties";

	@Override
	protected Properties loadConfig(String location) throws IOException {
		if (!StringUtils.hasText(location)) {
			location = KAFKA_PROPERTIES;
		}
		ResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
		Resource resource = resolver.getResource(location);
		InputStream in = null;
		Properties properties = new Properties();
		try {
			in = resource.getInputStream();
			properties.load(in);
		} finally {
			try {
				if (in != null) {
					in.close();
				}
			} catch (IOException e) {
				logger.error("error on loading configs", e);
			}
		}
		return properties;
	}

}
