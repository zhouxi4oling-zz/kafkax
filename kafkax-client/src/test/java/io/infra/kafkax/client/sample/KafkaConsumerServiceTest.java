package io.infra.kafkax.client.sample;

import io.infra.kafkax.client.config.annotation.KafkaConsumer;
import io.infra.kafkax.client.message.Message;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * Created by zhouxiaoling on 16/3/14.
 */
public class KafkaConsumerServiceTest {

    @KafkaConsumer(topic = "test", selectKey = "select.test.1")
    public void test1(Message<Bean> message) {
        System.out.println(message);
    }

    @KafkaConsumer(topic = "test", selectKey = "select.test.2")
    public void test2(Message<Bean> message) {
        System.out.println(message);
    }

    public static void main(String[] args) throws Exception {
        ApplicationContext context = new ClassPathXmlApplicationContext("test-kafkax-client-consumer.xml");
        System.in.read();
    }

}
