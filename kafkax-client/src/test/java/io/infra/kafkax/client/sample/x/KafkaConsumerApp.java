package io.infra.kafkax.client.sample.x;

import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * Created by zhouxiaoling on 16/6/14.
 */
public class KafkaConsumerApp {

    public static void main(String[] args) throws Exception {
        ApplicationContext context = new ClassPathXmlApplicationContext("test-kafkax-client-consumer.xml");

        Thread.sleep(1000);

        context.getBean("lazyKafkaConsumerServiceTest");

        Thread.sleep(5000);

        ((ConfigurableApplicationContext) context).close();
    }

}
