package io.infra.kafkax.client.sample;

import io.infra.kafkax.client.config.annotation.KafkaProducer;
import io.infra.kafkax.client.message.Message;
import io.infra.kafkax.client.producer.KafkaProducerTemplate;
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
        for (int i = 0; i < 100; i++) {
            Message<Bean> message = new Message<Bean>("select.test", new Bean("hello world[" + i + "]", new Date()));
            kafkaProducerTemplate.sendSync(message);
        }
    }

    public static void main(String[] args) throws Exception {
        ApplicationContext context = new ClassPathXmlApplicationContext("test-kafkax-client.xml");
        KafkaProducerServiceTest producerServiceTest = context.getBean("kafkaProducerServiceTest", KafkaProducerServiceTest.class);
        Thread.sleep(5000);
        System.out.println("sending...");
        producerServiceTest.test();
    }

}
