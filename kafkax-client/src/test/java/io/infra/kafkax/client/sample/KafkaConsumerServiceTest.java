package io.infra.kafkax.client.sample;

import io.infra.kafkax.client.config.annotation.KafkaConsumer;
import io.infra.kafkax.client.message.Message;

/**
 * Created by zhouxiaoling on 16/3/14.
 */
public class KafkaConsumerServiceTest {

    @KafkaConsumer(topic = "test", selectKey = "select.test")
    public void test(Message<Bean> message) {
        System.out.println(message);
    }

}
