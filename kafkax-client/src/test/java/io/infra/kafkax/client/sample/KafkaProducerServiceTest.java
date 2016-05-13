package io.infra.kafkax.client.sample;

import io.infra.kafkax.client.config.annotation.KafkaProducer;
import io.infra.kafkax.client.message.Message;
import io.infra.kafkax.client.producer.KafkaProducerTemplate;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.util.Date;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by zhouxiaoling on 16/3/14.
 */
public class KafkaProducerServiceTest {

    @KafkaProducer(topic = "test")
    private KafkaProducerTemplate kafkaProducerTemplate;

    private AtomicInteger i = new AtomicInteger(1);

    public void test() {
        Message<Bean> message = new Message<Bean>("select.test." + (i.intValue() % 2 + 1), new Bean("hello world[" + i + "]", new Date()));
        kafkaProducerTemplate.sendSync(message);
        i.incrementAndGet();
    }

    public static void main(String[] args) throws Exception {
        ApplicationContext context = new ClassPathXmlApplicationContext("test-kafkax-client-producer.xml");
        KafkaProducerServiceTest producerServiceTest = context.getBean("kafkaProducerServiceTest", KafkaProducerServiceTest.class);
        while (true) {
            producerServiceTest.test();
            Thread.sleep(1000);
        }
    }

}
