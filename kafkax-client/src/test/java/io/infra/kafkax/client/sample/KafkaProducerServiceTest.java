package io.infra.kafkax.client.sample;

import io.infra.kafkax.client.config.annotation.KafkaProducer;
import io.infra.kafkax.client.producer.KafkaProducerTemplate;
import io.infra.kafkax.client.message.Message;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.util.Date;

/**
 * Created by zhouxiaoling on 16/3/14.
 */
public class KafkaProducerServiceTest {

    @KafkaProducer(topic = "test")
    private KafkaProducerTemplate kafkaProducerTemplate;

    public void test() {
        Message<Bean> message = new Message<Bean>("select.test", new Bean("hello world", new Date()));
        long l = System.currentTimeMillis();
        kafkaProducerTemplate.sendSync(message);
        System.out.println("send: " + (System.currentTimeMillis() - l) + " ms");
    }

    public static void main(String[] args) {
        ApplicationContext context = new ClassPathXmlApplicationContext("test-lafka-client.xml");
        KafkaProducerServiceTest producerServiceTest = context.getBean("kafkaProducerServiceTest", KafkaProducerServiceTest.class);
        producerServiceTest.test();
    }

}
