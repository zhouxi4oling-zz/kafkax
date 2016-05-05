package io.infra.kafkax.client.producer;

/**
 * Created by zhouxiaoling on 16/3/22.
 */
public interface TopicBindedKafkaProducerTemplate extends KafkaProducerTemplate {

	String getBindedTopic();

}
