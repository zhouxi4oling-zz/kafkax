package io.infra.kafkax.client.message.recorder;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import io.infra.kafkax.client.exception.KafkaRuntimeException;
import io.infra.kafkax.client.message.Message;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Field;
import java.lang.reflect.Type;

/**
 * Created by zhouxiaoling on 16/3/21.
 */
public class MessageRecorder {

    final private Logger logger = LoggerFactory.getLogger(MessageRecorder.class);

    private static MessageRecorder instance = new MessageRecorder();

    private MessageRecorder() {
    }

    public static MessageRecorder getInstance() {
        return instance;
    }

    private ObjectMapper mapper = new ObjectMapper();

    {
        mapper.disable(SerializationFeature.FAIL_ON_EMPTY_BEANS);
        mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
        mapper.configure(JsonGenerator.Feature.ESCAPE_NON_ASCII, true);
    }

    public ProducerRecord<String, byte[]> record(Message message) {
        try {
            byte[] bytes = mapper.writeValueAsBytes(message);
            return new ProducerRecord<>(message.getTopic(), message.getPartition(), message.getKey(), bytes);
        } catch (Exception e) {
            logger.error("error on JSON write", e);
            throw new KafkaRuntimeException(e);
        }
    }

    public Message unrecord(ConsumerRecord<String, byte[]> consumerRecord) {
        try {
            Message message = mapper.readValue(consumerRecord.value(), Message.class);
            message.setKey(consumerRecord.key());
            message.setOffset(consumerRecord.offset());
            message.setPartition(consumerRecord.partition());
            message.setTopic(consumerRecord.topic());
            return message;
        } catch (Exception e) {
            logger.error("error on JSON read", e);
            throw new KafkaRuntimeException(e);
        }
    }

    public Message unrecord(ConsumerRecord<String, byte[]> consumerRecord, Type type) {
        try {
            Field field = TypeReference.class.getDeclaredField("_type");
            ReflectionUtils.makeAccessible(field);
            TypeReference<Object> typeReference = new TypeReference<Object>() {
            };
            ReflectionUtils.setField(field, typeReference, type);
            Message message = mapper.readValue(consumerRecord.value(), typeReference);
            message.setKey(consumerRecord.key());
            message.setOffset(consumerRecord.offset());
            message.setPartition(consumerRecord.partition());
            message.setTopic(consumerRecord.topic());
            return message;
        } catch (Exception e) {
            logger.error("error on JSON read", e);
            throw new KafkaRuntimeException(e);
        }
    }

}
