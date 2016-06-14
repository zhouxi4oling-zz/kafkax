package io.infra.kafkax.client.sample;

import io.infra.kafkax.client.config.annotation.KafkaConsumer;
import io.infra.kafkax.client.message.Message;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * Created by zhouxiaoling on 16/3/14.
 */
public class KafkaConsumerServiceTest {

    @KafkaConsumer(topic = "test1", selectKey = "select.test1.1")
    public void test1(Message<Bean> message) {
        System.out.println(message);
    }

    @KafkaConsumer(topic = "test1", selectKey = "select.test1.2")
    public void test2(Message<Bean> message) {
        System.out.println(message);
    }

}
