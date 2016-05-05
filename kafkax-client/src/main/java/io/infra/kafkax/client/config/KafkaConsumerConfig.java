package io.infra.kafkax.client.config;

import java.lang.reflect.Method;

/**
 * Created by zhouxiaoling on 16/3/21.
 */
public class KafkaConsumerConfig extends KafkaConfig {

    private String selectKey;
    private Method annotatedMethod;

    public String getSelectKey() {
        return selectKey;
    }

    public void setSelectKey(String selectKey) {
        this.selectKey = selectKey;
    }

    public Method getAnnotatedMethod() {
        return annotatedMethod;
    }

    public void setAnnotatedMethod(Method annotatedMethod) {
        this.annotatedMethod = annotatedMethod;
    }

}
