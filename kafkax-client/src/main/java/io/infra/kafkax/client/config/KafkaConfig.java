package io.infra.kafkax.client.config;

/**
 * Created by zhouxiaoling on 16/3/21.
 */
public abstract class KafkaConfig {

    private Object bean;
    private String beanName;
    private String topic;

    public Object getBean() {
        return bean;
    }

    public void setBean(Object bean) {
        this.bean = bean;
    }

    public String getBeanName() {
        return beanName;
    }

    public void setBeanName(String beanName) {
        this.beanName = beanName;
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

}
