package io.infra.kafkax.client.sample.x.message.recorder;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import io.infra.kafkax.client.message.Message;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.Date;

/**
 * Created by zhouxiaoling on 16/9/22.
 */
public class ObjectMapperTest {

    private ObjectMapper mapper = new ObjectMapper();

    {
        mapper.disable(SerializationFeature.FAIL_ON_EMPTY_BEANS);
        mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
        mapper.configure(JsonGenerator.Feature.ESCAPE_NON_ASCII, true);
    }

    public byte[] write(Object o) throws Exception {
        byte[] bytes = mapper.writeValueAsBytes(o);
        System.out.println(new String(bytes));
        return bytes;
    }

    public Object read(byte[] bytes, Type type) throws Exception {
        Field field = TypeReference.class.getDeclaredField("_type");
        ReflectionUtils.makeAccessible(field);
        TypeReference<Object> typeReference = new TypeReference<Object>() {
        };
        ReflectionUtils.setField(field, typeReference, type);
        return mapper.readValue(bytes, typeReference);
    }

    public ObjectMapper getMapper() {
        return mapper;
    }

    public static void main(String[] args) throws Exception {
        ObjectMapperTest test = new ObjectMapperTest();
        Message<A> message = new Message<>(new ObjectMapperTest.A("中文字符测试", new Date()));
        byte[] bytes = test.write(message);
        Object o = test.read(bytes, ObjectMapperTest.class.getMethod("x", new Class[]{Message.class}).getGenericParameterTypes()[0]);
        System.out.println(o);
    }

    public void x(Message<B> message) {
    }

    static class A {
        private String s;
        private Date d;

        public A() {
        }

        public A(String s, Date d) {
            this.s = s;
            this.d = d;
        }

        public String getS() {
            return s;
        }

        public void setS(String s) {
            this.s = s;
        }

        public Date getD() {
            return d;
        }

        public void setD(Date d) {
            this.d = d;
        }

        @Override
        public String toString() {
            return "A{" +
                    "s='" + s + '\'' +
                    ", d=" + d +
                    '}';
        }
    }

    static class B {
        private String s;
        private Date e;

        public B() {
        }

        public B(String s, Date e) {
            this.s = s;
            this.e = e;
        }

        public String getS() {
            return s;
        }

        public void setS(String s) {
            this.s = s;
        }

        public Date getE() {
            return e;
        }

        public void setE(Date e) {
            this.e = e;
        }

        @Override
        public String toString() {
            return "B{" +
                    "s='" + s + '\'' +
                    ", e=" + e +
                    '}';
        }
    }

}
