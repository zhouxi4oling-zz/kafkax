package io.infra.kafkax.client.sample;

import io.infra.kafkax.client.config.annotation.KafkaConsumer;
import io.infra.kafkax.client.message.Message;

/**
 * Created by zhouxiaoling on 16/3/14.
 */
public class LazyKafkaConsumerServiceTest {

    @KafkaConsumer(topic = "test2", selectKey = "select.test2.1")
    public void test1(Message<Bean> message) {
        System.out.println(message);
    }

    @KafkaConsumer(topic = "test2", selectKey = "select.test2.2")
    public void test2(Message<Bean> message) {
        System.out.println(message);
    }

}
