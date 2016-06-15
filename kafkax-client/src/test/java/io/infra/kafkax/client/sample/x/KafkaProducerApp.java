package io.infra.kafkax.client.sample.x;

import io.infra.kafkax.client.sample.x.producer.KafkaProducerServiceTest;
import io.infra.kafkax.client.sample.x.producer.LazyKafkaProducerServiceTest;
import io.infra.kafkax.client.sample.x.producer.ProducerService;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * Created by zhouxiaoling on 16/6/14.
 */
public class KafkaProducerApp {

    public static void main(String[] args) throws Exception {
        ApplicationContext context = new ClassPathXmlApplicationContext("test-kafkax-client-producer.xml");

        ProducerServiceTask task1 = new ProducerServiceTask(context.getBean("kafkaProducerServiceTest", KafkaProducerServiceTest.class));
        new Thread(task1).start();

        Thread.sleep(1000);

        ProducerServiceTask task2 = new ProducerServiceTask(context.getBean("lazyKafkaProducerServiceTest", LazyKafkaProducerServiceTest.class));
        new Thread(task2).start();

        Thread.sleep(60 * 1000);

        task1.stop();
        task2.stop();

        ((ConfigurableApplicationContext) context).close();
    }

    static class ProducerServiceTask implements Runnable {

        private ProducerService service;
        private volatile boolean stop = false;

        public ProducerServiceTask(ProducerService service) {
            this.service = service;
        }

        @Override
        public void run() {
            while (!stop)
                service.send();
        }

        public void stop() {
            stop = true;
        }

    }

}
