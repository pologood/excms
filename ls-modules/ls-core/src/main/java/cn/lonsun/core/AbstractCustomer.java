/*
 * AbstractCustomer.java         2015年8月19日 <br/>
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

package cn.lonsun.core;

import java.util.Collection;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;

/**
 * 消费者抽象实现 <br/>
 *
 * @date 2015年8月19日 <br/>
 * @author fangtinghua <br/>
 * @version v1.0 <br/>
 */
public abstract class AbstractCustomer<T> implements ICustomer<T>, DisposableBean {

    private Logger log;

    private BlockingQueue<T> queue = new LinkedBlockingQueue<T>();
    private int maxContentSize = Integer.MAX_VALUE;
    private Boolean isStarted = false;
    private ExecutorService es = null;
    private volatile boolean flag = true;// 线程标志

    public abstract void execute(T t) throws Throwable;// 业务处理方法

    /**
     * 设置消费者个数，默认1（在任务启动前生效）
     * 
     * @param customerSize
     */
    public int getCustomerNum() {
        return 1;
    }

    public void add(Collection<T> contents) {
        if (null != contents && !contents.isEmpty()) {
            for (T t : contents) {
                this.add(t);
            }
        }
    }

    @Override
    public void add(T content) {
        if (content == null) {
            return;
        }

        if (!isStarted) {
            synchronized (isStarted) {
                if (!isStarted) {
                    // 初始化线程池
                    es = Executors.newFixedThreadPool(this.getCustomerNum());
                    this.start();
                    isStarted = true;
                }
            }
        }
        this.put(content);
    }

    /**
     * 把对象放到队列中 当对象池满了，默认处理为移除最旧的对象
     * 
     * @param content
     */
    protected void put(T content) {
        this.putWithPoll(content);
    }

    /**
     * 把对象放到队列中 当对象池满了，默认处理为移除最旧的对象
     * 
     * @param content
     */
    protected void putWithPoll(T content) {
        synchronized (this.getQueue()) {
            if (this.getQueue().size() >= this.getMaxContentSize()) {
                this.getQueue().poll();
            }
            this.getQueue().add(content);
        }
    }

    /**
     * 把对象放到队列中 当对象池满了，阻塞
     */
    protected void putWithBlock(T content) {
        synchronized (this.getQueue()) {
            try {
                this.getQueue().put(content);
            } catch (InterruptedException e) {
                this.getLogger().error("", e);
            }
        }
    }

    /**
     * 启动消费者线程
     */
    protected void start() {
        final Logger log = this.getLogger();
        for (int i = 0; i < this.getCustomerNum(); i++) {
            es.execute(new Runnable() {
                public void run() {
                    while (flag) {
                        try {
                            T content = getQueue().take();// 阻塞等待
                            execute(content);// 由具体业务对象来实现
                        } catch (Throwable e) {// 确保线程不会崩溃退出
                            log.error("execute error ", e);

                        }
                    }
                }
            });
        }
    }

    /**
     * 设置对象池实现集合，默认LinkedBlockingQueue（在任务启动前生效）
     * 
     * @param queue
     */
    public BlockingQueue<T> getQueue() {
        return this.queue;
    }

    /**
     * 设置对象池最大容量，和queue的实现方式相关,默认Integer.MAX_VALUE
     * 
     * @return maxContentSize
     */
    public int getMaxContentSize() {
        return maxContentSize;
    }

    @Override
    public void destroy() throws Exception {
        if (null != es) {// 销毁线程池
            flag = false;// 设置标志位
            es.shutdownNow();
            while (!es.awaitTermination(1, TimeUnit.SECONDS))
                ;// 等待所有任务完成
            this.getLogger().debug("线程池关闭成功.");
        }
    }

    public Logger getLogger() {
        if (log == null) {
            log = LoggerFactory.getLogger(this.getClass());
        }
        return log;
    }
}