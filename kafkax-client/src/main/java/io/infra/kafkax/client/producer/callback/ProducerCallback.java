package io.infra.kafkax.client.producer.callback;

import io.infra.kafkax.client.message.Message;

/**
 * Created by zhouxiaoling on 16/5/5.
 */
public interface ProducerCallback {

    void onSuccess(Message message);

    void onFailure(Exception exception);

}
