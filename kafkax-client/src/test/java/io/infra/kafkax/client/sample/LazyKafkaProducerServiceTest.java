package io.infra.kafkax.client.sample;

import io.infra.kafkax.client.config.annotation.KafkaProducer;
import io.infra.kafkax.client.message.Message;
import io.infra.kafkax.client.producer.KafkaProducerTemplate;

import java.util.Date;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by zhouxiaoling on 16/3/14.
 */
public class LazyKafkaProducerServiceTest {

    @KafkaProducer(topic = "test2")
    private KafkaProducerTemplate kafkaProducerTemplate;

    private AtomicInteger i = new AtomicInteger(1);

    public void test() {
        Message<Bean> message = new Message<Bean>("select.test2." + (i.intValue() % 2 + 1), new Bean("hello world[" + i + "]", new Date()));
        kafkaProducerTemplate.sendSync(message);
        i.incrementAndGet();
    }

}
