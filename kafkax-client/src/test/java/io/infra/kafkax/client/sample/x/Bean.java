package io.infra.kafkax.client.sample.x;

import java.util.Date;

/**
 * Created by zhouxiaoling on 16/5/4.
 */
public class Bean {

    private String s;
    private Date d;

    public Bean() {
    }

    public Bean(String s, Date d) {
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
        return "Bean{" +
                "s='" + s + '\'' +
                ", d=" + d +
                '}';
    }

}
