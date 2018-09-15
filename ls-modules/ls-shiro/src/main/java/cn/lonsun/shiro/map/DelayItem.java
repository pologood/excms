/*
 * DelayItem.java         2016年6月2日 <br/>
 *
 * Copyright (c) 1994-1999 AnHui LonSun, Inc. <br/>
 * All rights reserved. <br/>
 *
 * This software is the confidential and proprietary information of AnHui   <br/>
 * LonSun, Inc. ("Confidential Information").  You shall not    <br/>
 * disclose such Confidential Information and shall use it only in  <br/>
 * accordance with the terms of the license agreement you entered into  <br/>
 * with Sun. <br/>
 */
package cn.lonsun.shiro.map;

import java.util.concurrent.Delayed;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang.builder.EqualsBuilder;

/**
 * DelayQueue ADD REASON. <br/>
 *
 * @date: 2016年6月2日 下午3:05:13 <br/>
 * @author fangtinghua
 */
public class DelayItem<E> implements Delayed {

    private E item;
    private long time;

    private static final long NANO_ORIGIN = System.nanoTime();

    final static long now() {
        return System.nanoTime() - NANO_ORIGIN;
    }

    /**
     * @param item
     * @param timeUnit
     * @param timeOut
     */
    public DelayItem(E item, TimeUnit timeUnit, long timeOut) {
        super();
        this.item = item;
        this.time = now() + TimeUnit.NANOSECONDS.convert(timeOut, timeUnit);
    }

    /**
     * fangtinghua
     * 
     * @see java.lang.Comparable#compareTo(java.lang.Object)
     */
    @Override
    public int compareTo(Delayed o) {
        if (o == this) {
            return 0;
        }
        long d = this.getDelay(TimeUnit.NANOSECONDS) - o.getDelay(TimeUnit.NANOSECONDS);
        return d > 0 ? 1 : -1;
    }

    /**
     * fangtinghua
     * 
     * @see java.util.concurrent.Delayed#getDelay(java.util.concurrent.TimeUnit)
     */
    @Override
    public long getDelay(TimeUnit unit) {
        return unit.convert(this.time - now(), TimeUnit.NANOSECONDS);
    }

    public E getItem() {
        return item;
    }

    /**
     * 
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj) {
        return EqualsBuilder.reflectionEquals(this, obj, new String[] { "item", "time" });
    }
}