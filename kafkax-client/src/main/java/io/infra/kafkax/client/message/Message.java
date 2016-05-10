package io.infra.kafkax.client.message;


import java.util.UUID;

/**
 * Created by zhouxiaoling on 16/3/9.
 */
public class Message<T> {

    // 消息标识
    private String id;

    // 消息所属Topic
    private String topic;

    // 消息所属Partition
    private Integer partition;

    // 消息所处Offset
    private long offset;

    // 消息Key
    private String key;

    // 消息筛选Key
    private String selectKey;

    // 消息体
    private T data;

    public Message() {
        this.id = UUID.randomUUID().toString();
    }

    public Message(T data) {
        this();
        this.data = data;
    }

    public Message(String selectKey, T data) {
        this();
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

    public String getId() {
        return id;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Message<?> message = (Message<?>) o;
        return id.equals(message.id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }

    @Override
    public String toString() {
        return "Message{" +
                "id='" + id + '\'' +
                ", topic='" + topic + '\'' +
                ", partition=" + partition +
                ", offset=" + offset +
                ", key='" + key + '\'' +
                ", selectKey='" + selectKey + '\'' +
                ", data=" + data +
                '}';
    }

}
