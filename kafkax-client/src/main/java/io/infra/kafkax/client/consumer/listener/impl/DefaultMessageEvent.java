package io.infra.kafkax.client.consumer.listener.impl;

import io.infra.kafkax.client.consumer.listener.MessageEvent;
import io.infra.kafkax.client.message.Message;

/**
 * Created by zhouxiaoling on 16/3/9.
 */
public class DefaultMessageEvent implements MessageEvent {

    private Message message;

    public DefaultMessageEvent(Message message) {
        this.message = message;
    }

    public Message getMessage() {
        return message;
    }

}
