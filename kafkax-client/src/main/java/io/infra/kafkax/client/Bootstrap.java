package io.infra.kafkax.client;

import io.infra.kafkax.client.config.ConfigManager;
import io.infra.kafkax.client.config.ConfigManagerFactory;
import io.infra.kafkax.client.config.impl.DefaultConfigManagerFactory;
import io.infra.kafkax.client.template.clients.InnerKafkaClients;
import io.infra.kafkax.client.template.clients.TemplateContainer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.config.BeanPostProcessor;

/**
 * Created by zhouxiaoling on 16/3/9.
 */
public class Bootstrap implements InitializingBean, BeanPostProcessor, DisposableBean {

    private final Logger logger = LoggerFactory.getLogger(Bootstrap.class);

    private ConfigManagerFactory configManagerFactory = new DefaultConfigManagerFactory();
    private ConfigManager manager = configManagerFactory.getConfigManager("local");

    private InnerKafkaClients clients = TemplateContainer.get();

    public void setConfigLocation(String configLocation) {
        logger.info("kafka properties file [{}]", configLocation);
        manager.setConfigLocation(configLocation);
    }

    public void afterPropertiesSet() throws Exception {
        manager.init();
    }

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        return bean;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        clients.buildKafkaProducerTemplates(manager.configKafkaProducers(bean, beanName));
        clients.buildKafkaConsumerTemplates(manager.configKafkaConsumers(bean, beanName));
        return bean;
    }

    public void destroy() throws Exception {
        logger.debug("system is going to shutdown");
        clients.shutdown();
    }

}
