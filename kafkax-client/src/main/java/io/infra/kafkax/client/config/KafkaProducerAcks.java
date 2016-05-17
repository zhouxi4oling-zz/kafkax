package io.infra.kafkax.client.config;

/**
 * Created by zhouxiaoling on 16/5/17.
 */
public enum KafkaProducerAcks {

    IGNORE("0"), LEADER("1"), ALL("all");

    private String value;

    KafkaProducerAcks(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

}
