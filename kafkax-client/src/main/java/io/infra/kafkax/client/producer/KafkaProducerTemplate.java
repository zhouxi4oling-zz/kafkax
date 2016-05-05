package io.infra.kafkax.client.producer;

import io.infra.kafkax.client.template.KafkaTemplate;
import io.infra.kafkax.client.message.Message;
import io.infra.kafkax.client.producer.callback.ProducerCallback;

/**
 * Created by zhouxiaoling on 16/3/21.
 */
public interface KafkaProducerTemplate extends KafkaTemplate {

    void sendAndForget(Message message);

    void sendSync(Message message);

    void sendAsync(Message message, ProducerCallback callback);

}
