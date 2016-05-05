package io.infra.kafkax.client.message;


/**
 * Created by zhouxiaoling on 16/3/9.
 */
public class Message<T> {

    private String topic;
    private Integer partition;
    private long offset;
    private String key;
    private String selectKey;
    private T data;

    public Message() {
    }

    public Message(T data) {
        this.data = data;
    }

    public Message(String selectKey, T data) {
        this.selectKey = selectKey;
        this.data = data;
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public Integer getPartition() {
        return partition;
    }

    public void setPartition(Integer partition) {
        this.partition = partition;
    }

    public long getOffset() {
        return offset;
    }

    public void setOffset(long offset) {
        this.offset = offset;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getSelectKey() {
        return selectKey;
    }

    public void setSelectKey(String selectKey) {
        this.selectKey = selectKey;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "Message{" +
                "topic='" + topic + '\'' +
                ", partition=" + partition +
                ", offset=" + offset +
                ", key='" + key + '\'' +
                ", selectKey='" + selectKey + '\'' +
                ", data=" + data +
                '}';
    }

}
