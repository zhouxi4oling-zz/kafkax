package io.infra.kafkax.client.config;

import java.lang.reflect.Field;

/**
 * Created by zhouxiaoling on 16/3/21.
 */
public class KafkaProducerConfig extends KafkaConfig {

    private Field annotatedField;

    public Field getAnnotatedField() {
        return annotatedField;
    }

    public void setAnnotatedField(Field annotatedField) {
        this.annotatedField = annotatedField;
    }

}
