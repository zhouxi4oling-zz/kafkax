# kafkax-client

## 1 介绍

kafkax-client是基于Kafka 0.9.0.1版本进行封装的客户端，目的是为了更加易于使用，同时扩展了原生客户端的功能，并加入一些必要的监控。

注意：

- 由于Kafka 0.9.0.1原生客户端采用的是JDK7版本开发，所以在使用时务必采用JDK7开发系统。

## 2 使用指南

### 2.1 依赖说明

|Group Id|Artifact Id|Version|备注|
|---|---|---|---|
|org.slf4j|slf4j-api|1.7.21|日志Facade框架|
|org.springframework|spring-context|3.2.16.RELEASE|需结合Spring容器使用|
|org.apache.kafka|kafka-clients|0.9.0.1|原生客户端|
|com.fasterxml.jackson.core|jackson-core|2.7.4|Jackson核心处理|
|com.fasterxml.jackson.core|jackson-databind|2.7.4|Jackson对象转换|

### 2.2 配置说明

#### 2.2.1 系统配置

在部署工程类路径中创建`system.properties`文件，以Maven结构的项目为例，路径为`/src/main/resources/system.properties`。

``` properties
# 系统归属组ID
system.groupId=infra

# 系统ID(组内唯一)
system.appId=kafka-client
```

`groupId` + `appId`唯一的标识了本工程，该文件的创建粒度为一个部署工程一份，该标志将被基础服务组件用于区分各个不同的使用方，从而做到使用隔离。

#### 2.1.2 客户端配置

在部署工程类路径中创建`kafka.properties`文件，以Maven结构的项目为例，默认路径为`/src/main/resources/kafka.properties`，也可在Spring配置文件中通过BootStrap类进行自定义路径设置。

``` properties
# Kafka消息服务器地址,多个地址以逗号分隔.
kafka.servers=127.0.0.1:9092

# 消费端从服务器多次拉取时间间隔(毫秒)
kafka.consumer.pollingInterval=1000

# 消费端从服务器一次拉取持续时间(毫秒)
kafka.consumer.pollingTimeout=100

# 消息并发处理线程数
kafka.consumer.concurrency=5
```

消费端的设计采用生产者消费者模式。单消费者线程轮询从服务器拉取消息，此处的`pollingInterval`设置的是轮询间隔，而`pollingTimeout`设置的是单次拉取持续时间。`concurrency`设置对拉回的消息处理时的并行度，消息会以TopicPartition发配到指定的处理线程进行处理。

注意，`pollingInterval + pollingTimeout`应小于等于30000，即30秒，这是默认的客户端与服务器之间的心跳超时时间。

#### 2.1.3 Spring配置

客户端跟随Spring容器启动，所以需要在Spring配置中增加消息客户端的启动引导配置。

``` xml
<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns="http://www.springframework.org/schema/beans"
       xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
       xsi:schemaLocation="http://www.springframework.org/schema/beans http://www.springframework.org/schema/beans/spring-beans.xsd">
    <bean class="io.infra.kafkax.client.Bootstrap">
        <property name="configLocation" value="classpath:kafka.properties"/>
    </bean>
</beans>
```

其中`configLocation`为可选配置，用来覆盖默认的`kafka.properties`配置文件路径，如使用默认位置，可省略该属性的配置。

### 2.2 消息生产者

#### 2.2.1 基于注解

``` java
package io.infra.kafkax.client.sample;

import io.infra.kafkax.client.config.annotation.KafkaProducer;
import io.infra.kafkax.client.message.Message;
import io.infra.kafkax.client.producer.KafkaProducerTemplate;

import java.util.Date;

/**
 * Created by zhouxiaoling on 16/3/14.
 */
public class KafkaProducerServiceTest {

    @KafkaProducer(topic = "test")
    private KafkaProducerTemplate kafkaProducerTemplate;

    public void test() {
        Message<Bean> message = new Message<Bean>("select.test", new Bean("hello world", new Date()));
        kafkaProducerTemplate.sendSync(message);
    }

}
```

`KafkaProducerServiceTest`类需要在Spring容器中注册后相应的注解字段才会被设置。

消息生产者支持三种消息发送行为：

`sendAndForget`：Fire-and-forget模式，调用此方法后，消息并不会第一时间就发送到服务器，而是会先被缓存在本地队列中，而调用端默认此消息已经发送成功。因此，这种方式拥有最优的性能，缺点也很明显，如果消息发送失败，无法得到通知。

`sendSync`：同步调用，调用此方法时，调用端阻塞直到消息被发送到服务器并且收到服务器的确认回执后才返回，同时Message参数中的Offset和Partition将会被填充设值。这种方式比较损耗性能，但是保证了消息传输的成功率。

`sendAsync`：异步调用，结合上面两种方式，消息被存储于本地队列，同时调用方需要提供一个Callback（实现io.infra.kafkax.client.producer.callback.ProducerCallback接口），待消息发送完成时回调。

#### 2.2.2 基于API（待）

### 2.3 消息消费者

#### 2.3.1 基于注解

``` java
package io.infra.kafkax.client.sample;

import io.infra.kafkax.client.config.annotation.KafkaConsumer;
import io.infra.kafkax.client.message.Message;

/**
 * Created by zhouxiaoling on 16/3/14.
 */
public class KafkaConsumerServiceTest {

    @KafkaConsumer(topic = "test", selectKey = "select.test")
    public void test(Message<Bean> message) {
        System.out.println(message);
    }

}
```

监听指定的`topic`和`selectKey`，目前方法入参必须是`Message<?>`类型，`?`为消息内容的类型，不支持该类型内部定义**泛型**成员。

注意，目前同样的**topic + selectKey**的组合不支持在一个应用中被注册多次！

#### 2.3.2 基于API（待）

### 2.4 消息

``` java
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

	// getters and setters

}
```

`topic`：消息被投递的Topic，如使用生产者注解配置方式，发送时，这个字段不起作用。

`partition`：区，一个Topic被分为多个区，区为消息实际的传送管道，如指定区，消息将被发送到指定的Topic的指定区中，如非高度定制需求，不推荐使用。

`offset`：索引，消息在区中的位置，消息一旦发送成功，索引就会被分配。

`key`：关键字，用于语义分区，如非高度定制需求，不推荐使用。

`selectKey`：选择关键字，消费者会处理自己指定的关键字的消息，而忽略其他的消息。

`data`：消息内容，泛型类型。

## 3 使用规约

### 3.1 `system.groupId`和`system.appId`

每个团队的leader给自己的团队定义一个groupId（公司唯一）作为本团队下所有应用和组件的顶层namespace，在这个groupId之下是appId，appId的粒度为一个部署工程，可以利用这个工程的工程名作为appId，但是此时需要注意groupId下的所有appId不应该有重复。

`groupId` + `appId`在Kafka中扮演者消费者群组的角色，相对于传统的JMS类消息服务的Queue和Topic结构，Kafka只提供了Topic结构。但是，可以配合利用群组的形式来实现JMS的Queue和Topic语义。同一群组的所有消费者（应用集群）订阅一个主题，则一条消息只能被其中一个消费者消费（Queue）。不同群组的所有消费者（多个应用集群）订阅一个主题，则一条消息会被各个组中的一个消费者消费（Topic）。

### 3.2 `topic`和`selectKey`

topic由消息的生产方申请。由于Kafka的topic从使用形式上推荐复用，所以topic名字应能描述一批共性的业务（粗粒度）。selectKey是topic的下一级筛选，topic + selectKey唯一的决定了这条消息的处理器。selectKey的命名应是一个具体的操作业务名，应在一个topic中不重复。topic和selectKey的组合关系由消息生产方维护并告知消息消费方开发组。

命名上，譬如，按照系统的业务模块划分，可以得到如下五个全名定义：

- group-id.module1.sub-module1.sub-module1.operation1
- group-id.module1.sub-module1.sub-module1.operation2
- group-id.module1.sub-module1.sub-module2.operation1
- group-id.module1.sub-module1.sub-module2.operation2
- group-id.module1.sub-module1.sub-module3.operation1

那就可以这样来命名Topic和SelectKey：

一个Topic：`group-id.module1.sub-module1`

五个SelectKey：`sub-module1.operation1`、`sub-module1.operation2`、`sub-module2.operation1`、`sub-module2.operation2`、`sub-module3.operation1`。

### 3.3 topic/selectKey等配置数据维护

由于目前相关的Console还没提供（正在计划中），所以大家需要自己先维护一份完整的配置参数文档，后续导入。

## 4 FAQ

### 4.1 如何实现消息的“Exactly Once”消费

由于Kafka本身实现的机制，容易造成消息的重复消费，这也是Kafka官方文档中声明的Kafka默认保证消息的“At Least Once”消费语义。但是在实际使用中，重复的消息消费并不是使用方需求的，大多数情况下，我们更需要“Exactly Once”的消费语义。

针对这个问题，目前版本的客户端做了一个workaround，消息本身自带一个ID，这个ID保证在全局范围内都是唯一的，因此只要使用方在使用时针对这个ID连带进行业务操作，就能保证不会消费到重复消息。（后期针对这块逻辑，会有改造计划。）