package io.infra.kafkax.client.sample;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * Created by zhouxiaoling on 16/6/14.
 */
public class KafkaConsumerApp {

    public static void main(String[] args) throws Exception {
        ApplicationContext context = new ClassPathXmlApplicationContext("test-kafkax-client-consumer.xml");

        Thread.sleep(10000);
        context.getBean("lazyKafkaConsumerServiceTest");

        System.in.read();
    }

}
