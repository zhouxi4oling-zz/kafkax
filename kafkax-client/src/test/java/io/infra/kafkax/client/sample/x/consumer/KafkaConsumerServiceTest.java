package io.infra.kafkax.client.sample.x.consumer;

import io.infra.kafkax.client.config.annotation.KafkaConsumer;
import io.infra.kafkax.client.message.Message;
import io.infra.kafkax.client.sample.x.Bean;

/**
 * Created by zhouxiaoling on 16/3/14.
 */
public class KafkaConsumerServiceTest implements ConsumerService {

    @Override
    @KafkaConsumer(topic = "test1", selectKey = "select.test1.1")
    public void receive1(Message<Bean> message) {
        System.out.println(message);
    }

    @Override
    @KafkaConsumer(topic = "test1", selectKey = "select.test1.2")
    public void receive2(Message<Bean> message) {
        System.out.println(message);
    }

}
