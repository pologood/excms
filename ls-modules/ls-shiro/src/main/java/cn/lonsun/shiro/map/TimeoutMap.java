/*
 * TimeoutMap.java         2016年6月2日 <br/>
 *
 * Copyright (c) 1994-1999 AnHui LonSun, Inc. <br/>
 * All rights reserved.	<br/>
 *
 * This software is the confidential and proprietary information of AnHui	<br/>
 * LonSun, Inc. ("Confidential Information").  You shall not	<br/>
 * disclose such Confidential Information and shall use it only in	<br/>
 * accordance with the terms of the license agreement you entered into	<br/>
 * with Sun. <br/>
 */

package cn.lonsun.shiro.map;

import java.util.HashMap;
import java.util.concurrent.DelayQueue;
import java.util.concurrent.TimeUnit;

/**
 * 超时map实现 <br/>
 *
 * @date 2016年6月2日 <br/>
 * @author fangtinghua <br/>
 * @version v1.0 <br/>
 */
public class TimeoutMap<K, V> extends HashMap<K, V> {

    /**
     * serialVersionUID
     */
    private static final long serialVersionUID = 1L;

    private TimeUnit defaultTimeUnit;
    private long defaultTimeOut;
    private TimeoutCallback<K, V> defaultCallback = null;
    private DelayQueue<DelayItem<K>> timeoutQueue = new DelayQueue<DelayItem<K>>();

    public TimeoutMap(TimeUnit timeUnit, long timeOut) {
        this(timeUnit, timeOut, null);
    }

    public TimeoutMap(TimeUnit timeUnit, long timeOut, TimeoutCallback<K, V> callback) {
        this.defaultTimeUnit = timeUnit;
        this.defaultTimeOut = timeOut;
        this.defaultCallback = callback;

        final TimeoutMap<K, V> that = this;
        Thread timeout = new Thread(new Runnable() {
            @Override
            public void run() {
                that.timeOutCheck();
            }
        });
        // 设置为守护线程
        timeout.setDaemon(true);
        timeout.start();
    }

    private void timeOutCheck() {
        while (true) {
            try {
                DelayItem<K> item = timeoutQueue.take();
                K k = item.getItem();
                V v = super.remove(k);
                if (this.defaultCallback != null) {
                    this.defaultCallback.onTimeout(k, v);
                }
            } catch (Throwable e) {
                e.printStackTrace();
            }
        }
    }

    private void addToTimeoutQueue(K k, TimeUnit timeUnit, long timeOut) {
        DelayItem<K> item = new DelayItem<K>(k, timeUnit, timeOut);
        timeoutQueue.remove(item);
        timeoutQueue.add(item);
    }

    @Override
    public V put(K key, V value) {
        return this.put(key, value, this.defaultTimeUnit, this.defaultTimeOut);
    }

    public V put(K key, V value, TimeUnit timeUnit, long timeOut) {
        this.addToTimeoutQueue(key, timeUnit, timeOut);
        return super.put(key, value);
    }

    @Override
    public void clear() {
        this.timeoutQueue.clear();
        super.clear();
    }

    @Override
    @SuppressWarnings("unchecked")
    public V remove(Object obj) {
        this.timeoutQueue.remove(new DelayItem<K>((K) obj, this.defaultTimeUnit, this.defaultTimeOut));
        return super.remove(obj);
    }
}