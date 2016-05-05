package io.infra.kafkax.client.consumer.listener;

import io.infra.kafkax.client.message.Message;

/**
 * Created by zhouxiaoling on 16/3/9.
 */
public interface MessageEvent {

    Message getMessage();

}
