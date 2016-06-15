package io.infra.kafkax.client.sample.x.producer;

import io.infra.kafkax.client.config.annotation.KafkaProducer;
import io.infra.kafkax.client.message.Message;
import io.infra.kafkax.client.producer.KafkaProducerTemplate;
import io.infra.kafkax.client.sample.x.Bean;

import java.util.Date;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by zhouxiaoling on 16/3/14.
 */
public class LazyKafkaProducerServiceTest implements ProducerService {

    @KafkaProducer(topic = "test2")
    private KafkaProducerTemplate kafkaProducerTemplate;

    private AtomicInteger i = new AtomicInteger(1);

    @Override
    public void send() {
        Message<Bean> message = new Message<>("select.test2." + (i.intValue() % 2 + 1), new Bean("hello world[" + i + "]", new Date()));
        kafkaProducerTemplate.sendSync(message);
        System.out.println(message);
        i.incrementAndGet();
    }

}
