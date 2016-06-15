package io.infra.kafkax.client.sample.x.consumer;

import io.infra.kafkax.client.message.Message;
import io.infra.kafkax.client.sample.x.Bean;

/**
 * Created by zhouxiaoling on 16/6/15.
 */
public interface ConsumerService {

    void receive1(Message<Bean> message);

    void receive2(Message<Bean> message);

}
