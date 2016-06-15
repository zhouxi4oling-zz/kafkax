package io.infra.kafkax.client.sample.x.consumer;

import io.infra.kafkax.client.config.annotation.KafkaConsumer;
import io.infra.kafkax.client.message.Message;
import io.infra.kafkax.client.sample.x.Bean;

/**
 * Created by zhouxiaoling on 16/3/14.
 */
public class LazyKafkaConsumerServiceTest implements ConsumerService {

    @Override
    @KafkaConsumer(topic = "test2", selectKey = "select.test2.1")
    public void receive1(Message<Bean> message) {
        System.out.println(message);
    }

    @Override
    @KafkaConsumer(topic = "test2", selectKey = "select.test2.2")
    public void receive2(Message<Bean> message) {
        System.out.println(message);
    }

}
