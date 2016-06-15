package io.infra.kafkax.client.producer.impl;

import io.infra.kafkax.client.exception.KafkaRuntimeException;
import io.infra.kafkax.client.message.Message;
import io.infra.kafkax.client.message.recorder.MessageRecorder;
import io.infra.kafkax.client.producer.KafkaProducerFactory;
import io.infra.kafkax.client.producer.KafkaProducerTemplate;
import io.infra.kafkax.client.producer.callback.ProducerCallback;
import io.infra.kafkax.client.template.CloseableKafkaTemplate;
import org.apache.kafka.clients.producer.Callback;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;

import java.util.concurrent.Future;

/**
 * Created by zhouxiaoling on 16/3/21.
 */
public class DefaultKafkaProducerTemplate implements KafkaProducerTemplate, CloseableKafkaTemplate {

    private final Logger logger = LoggerFactory.getLogger(DefaultKafkaProducerTemplate.class);

    private KafkaProducer<String, byte[]> producer;
    private boolean closed = false;

    public DefaultKafkaProducerTemplate() {
        this.producer = KafkaProducerFactory.createKafkaProducer();
    }

    @Override
    public synchronized void close() {
        if (!closed) {
            logger.info("Producer is going to be closed.");
            producer.close();
            closed = true;
        }
    }

    @Override
    public void sendAndForget(Message message) {
        Assert.notNull(message, "message is null");
        Assert.hasText(message.getTopic(), "topic is required");
        try {
            producer.send(MessageRecorder.getInstance().record(message));
        } catch (Exception e) {
            throw new KafkaRuntimeException(e);
        }
    }

    @Override
    public void sendSync(Message message) {
        Assert.notNull(message, "message is null");
        Assert.hasText(message.getTopic(), "topic is required");
        try {
            Future<RecordMetadata> future = producer.send(MessageRecorder.getInstance().record(message));
            RecordMetadata metadata = future.get();
            message.setOffset(metadata.offset());
            message.setPartition(metadata.partition());
        } catch (Exception e) {
            throw new KafkaRuntimeException(e);
        }
    }

    @Override
    public void sendAsync(final Message message, final ProducerCallback callback) {
        Assert.notNull(message, "message is null");
        Assert.hasText(message.getTopic(), "topic is required");
        try {
            producer.send(MessageRecorder.getInstance().record(message), new Callback() {
                @Override
                public void onCompletion(RecordMetadata metadata, Exception exception) {
                    if (exception != null) {
                        callback.onFailure(exception);
                    } else {
                        message.setOffset(metadata.offset());
                        message.setPartition(metadata.partition());
                        callback.onSuccess(message);
                    }
                }
            });
        } catch (Exception e) {
            throw new KafkaRuntimeException(e);
        }
    }

}
