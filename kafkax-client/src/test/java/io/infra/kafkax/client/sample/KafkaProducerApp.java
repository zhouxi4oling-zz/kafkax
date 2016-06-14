package io.infra.kafkax.client.sample;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * Created by zhouxiaoling on 16/6/14.
 */
public class KafkaProducerApp {

    public static void main(String[] args) throws Exception {
        final ApplicationContext context = new ClassPathXmlApplicationContext("test-kafkax-client-producer.xml");

        new Thread(new Runnable() {
            @Override
            public void run() {
                KafkaProducerServiceTest kafkaProducerServiceTest = context.getBean("kafkaProducerServiceTest", KafkaProducerServiceTest.class);
                while (true) {
                    kafkaProducerServiceTest.test();
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();

        Thread.sleep(5000);

        new Thread(new Runnable() {
            @Override
            public void run() {
                LazyKafkaProducerServiceTest lazyKafkaProducerServiceTest = context.getBean("lazyKafkaProducerServiceTest", LazyKafkaProducerServiceTest.class);
                while (true) {
                    lazyKafkaProducerServiceTest.test();
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }

}
