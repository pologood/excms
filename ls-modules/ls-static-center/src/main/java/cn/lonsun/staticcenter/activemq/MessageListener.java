/*
 * MessageListener.java         2015年8月19日 <br/>
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
package cn.lonsun.staticcenter.activemq;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.ObjectMessage;

import cn.lonsun.activemq.MessageChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.task.TaskExecutor;
import org.springframework.jms.core.JmsTemplate;

import cn.lonsun.activemq.MessageConfig;
import cn.lonsun.message.internal.entity.MessageStaticEO;
import cn.lonsun.staticcenter.generate.GenerateCustomer;
import cn.lonsun.staticcenter.generate.thread.ThreadCount;
import cn.lonsun.staticcenter.generate.util.ContextUtil;
import cn.lonsun.statictask.internal.entity.StaticTaskEO;
import cn.lonsun.statictask.internal.service.IStaticTaskService;

import com.alibaba.fastjson.JSON;

/**
 * 静态化mq监听. <br/>
 *
 * @author fangtinghua
 * @date: 2015年8月19日 下午2:58:45 <br/>
 * @update 2018年2月2日 改成spring代理配置，不使用自定义的控制
 */
@Deprecated
public class MessageListener {

    // 日志
    private static final Logger logger = LoggerFactory.getLogger(MessageListener.class);

    @Resource
    private JmsTemplate jmsTemplate;
    // 线程池执行接口
    @Resource
    private TaskExecutor taskExecutor;
    @Resource
    private IStaticTaskService staticTaskService;
    @Resource
    private GenerateCustomer customer;

    @Resource
    private javax.jms.MessageListener staticCancelQueueListener;

    @Deprecated
    @PostConstruct
    public void init() {
        // 监听取消生成静态消息
        taskExecutor.execute(new Runnable() {
            @Override
            public void run() {
                do {
                    try {
                        logger.debug("MQ监听终止队列消息...");
                        Message message = jmsTemplate.receive(MessageChannel.STATIC_CANCEL_QUEUE.toString());
                        logger.debug("MQ获取终止队列消息成功...");
                        staticCancelQueueListener.onMessage(message);
                    } catch (Throwable e) {
                        logger.error("生成静态线程异常，请检查！", e);
                        break;
                    }
                } while (true);
            }
        });
        // 监听生成静态消息
        //update by zhongjun 拆分成static
        taskExecutor.execute(new Runnable() {
            @Override
            public void run() {
                do {
                    try {
                        if (ThreadCount.getCount() < customer.getCustomerNum()) {// 当消费者小于线程数才去取任务
                            logger.debug("MQ监听生成队列消息...");
                            ObjectMessage message = (ObjectMessage)jmsTemplate.receive(MessageChannel.STATIC_GENERATE_QUEUE.toString());
                            logger.debug("MQ获取生成队列消息成功...");
                            customer.add((StaticTaskEO) message.getObject());// 消费
                        }
                        Thread.sleep(1000);// 每隔一秒取一次任务
                    } catch (JMSException e) {
                        logger.error("activemq连接失败，请检查其是否启动！", e);
                    } catch (Throwable e) {
                        logger.error("生成静态线程异常，请检查！", e);
                        break;
                    }
                } while (true);
            }
        });
    }
}