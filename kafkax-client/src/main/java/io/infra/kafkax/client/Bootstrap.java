package io.infra.kafkax.client;

import io.infra.kafkax.client.config.ConfigManager;
import io.infra.kafkax.client.config.ConfigManagerFactory;
import io.infra.kafkax.client.config.KafkaConfigs;
import io.infra.kafkax.client.config.impl.DefaultConfigManagerFactory;
import io.infra.kafkax.client.template.clients.InnerKafkaClients;
import io.infra.kafkax.client.template.clients.TemplateContainer;
import org.apache.kafka.common.KafkaException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

/**
 * Created by zhouxiaoling on 16/3/9.
 */
public class Bootstrap implements ApplicationContextAware, InitializingBean, DisposableBean {

    private final Logger logger = LoggerFactory.getLogger(Bootstrap.class);

    private ConfigManagerFactory configManagerFactory = new DefaultConfigManagerFactory();
    private ConfigManager manager = configManagerFactory.getConfigManager("local");

    private ApplicationContext applicationContext;

    private InnerKafkaClients clients = TemplateContainer.getInstance();

    public void setConfigLocation(String configLocation) {
        logger.debug("kafka properties file [{}]", configLocation);
        manager.setConfigLocation(configLocation);
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    public void afterPropertiesSet() throws Exception {
        boolean initialized = manager.init();
        logger.debug("config manager init [{}]", initialized);
        if (!initialized) {
            throw new KafkaException("fail to start up kafka clients");
        }

        for (String beanName : applicationContext.getBeanDefinitionNames()) {
            manager.config(applicationContext.getBean(beanName), beanName);
        }

        logger.debug("all beans in spring container have been configed");

        KafkaConfigs configs = manager.configs();

        clients.buildKafkaConsumerTemplate(configs);
        logger.debug("consumers have been built");

        clients.buildKafkaProducerTemplate(configs);
        logger.debug("producers have been built");
    }

    public void destroy() throws Exception {
        logger.debug("system is going to shutdown");
        clients.shutdown();
    }

}
