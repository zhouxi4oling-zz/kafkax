package io.infra.kafkax.client.consumer.listener;

/**
 * Created by zhouxiaoling on 16/3/9.
 */
public interface MessageListener {

	public void onMessage(MessageEvent event);

}
