package io.infra.kafkax.client.producer.impl;

import io.infra.kafkax.client.config.KafkaConfigs;
import io.infra.kafkax.client.message.Message;
import io.infra.kafkax.client.producer.TopicBindedKafkaProducerTemplate;
import io.infra.kafkax.client.producer.callback.ProducerCallback;
import org.springframework.util.Assert;

public class DefaultTopicBindedKafkaProducerTemplate extends DefaultKafkaProducerTemplate implements TopicBindedKafkaProducerTemplate {

    private String topic;

    public DefaultTopicBindedKafkaProducerTemplate(String topic) {
        super();
        this.topic = topic;
    }

    @Override
    public String getBindedTopic() {
        return topic;
    }

    @Override
    public void sendAndForget(Message message) {
        Assert.notNull(message, "message is null");
        message.setTopic(this.topic);
        super.sendAndForget(message);
    }

    @Override
    public void sendSync(Message message) {
        Assert.notNull(message, "message is null");
        message.setTopic(this.topic);
        super.sendSync(message);
    }

    @Override
    public void sendAsync(Message message, ProducerCallback callback) {
        Assert.notNull(message, "message is null");
        message.setTopic(this.topic);
        super.sendAsync(message, callback);
    }

}
