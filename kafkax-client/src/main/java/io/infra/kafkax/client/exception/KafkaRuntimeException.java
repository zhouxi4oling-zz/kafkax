package io.infra.kafkax.client.exception;

/**
 * Created by zhouxiaoling on 16/3/22.
 */
public class KafkaRuntimeException extends RuntimeException {

	private static final long serialVersionUID = 5663827218960830768L;

	public KafkaRuntimeException(String paramString) {
		super(paramString);
	}

	public KafkaRuntimeException(String paramString, Throwable paramThrowable) {
		super(paramString, paramThrowable);
	}

	public KafkaRuntimeException(Throwable paramThrowable) {
		super(paramThrowable);
	}

}
