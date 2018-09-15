/*
 * Centerl.java         2016年4月25日 <br/>
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

package cn.lonsun.staticcenter.core;

import javax.servlet.ServletContextEvent;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.context.ContextLoaderListener;

import cn.lonsun.activemq.MessageSender;
import cn.lonsun.core.util.SpringContextHolder;
import cn.lonsun.message.internal.entity.Message;
import cn.lonsun.message.internal.entity.MessageSystemEO;
import cn.lonsun.staticcenter.generate.CallBack;
import cn.lonsun.staticcenter.generate.GenerateException;
import cn.lonsun.staticcenter.generate.GenerateRecord;
import cn.lonsun.staticcenter.generate.thread.Context;
import cn.lonsun.staticcenter.generate.util.ContextUtil;
import cn.lonsun.statictask.internal.entity.StaticTaskEO;
import cn.lonsun.statictask.internal.service.IStaticTaskService;
import cn.lonsun.util.HibernateHandler;
import cn.lonsun.util.HibernateSessionUtil;

/**
 * 生成静态上下文加载监听重写，在销毁的时候把所有正在处理的任务删除 <br/>
 *
 * @date 2016年4月25日 <br/>
 * @author fangtinghua <br/>
 * @version v1.0 <br/>
 */
public class CenterContextLoaderListener extends ContextLoaderListener {

    // 日志
    private static final Logger logger = LoggerFactory.getLogger(CenterContextLoaderListener.class);

    @Override
    public void contextInitialized(ServletContextEvent event) {
        try {
            // 初始化
            super.contextInitialized(event);
            // 删除所有未完成的、正在进行的任务
            SpringContextHolder.getBean(IStaticTaskService.class).deleteInitDoingTask();
        } catch (Throwable e) {
            logger.error("容器初始化失败.", e);
        }
    }

    @Override
    public void contextDestroyed(ServletContextEvent event) {
        // 对象
        final ServletContextEvent servletContextEvent = event;
        // 任务service
        final IStaticTaskService staticTaskService = SpringContextHolder.getBean(IStaticTaskService.class);
        // 执行方法
        HibernateSessionUtil.execute(new HibernateHandler<Boolean>() {

            @Override
            public Boolean execute() throws Throwable {

                // 异常结束
                return ContextUtil.exceptionTask(new CallBack<Context>() {

                    @Override
                    public String execute(Context context) throws GenerateException {
                        // 发送消息
                        MessageSystemEO eo = new MessageSystemEO();
                        eo.setTitle("生成静态");
                        eo.setContent("生成静态服务崩溃，请检查.");
                        eo.setMessageType(MessageSystemEO.TIP);
                        eo.setMessageStatus(MessageSystemEO.MessageStatus.error.toString());
                        eo.setSiteId(context.getSiteId());
                        eo.setRecUserIds(String.valueOf(context.getUserId()));
                        // 构建消息
                        Long taskId = context.getTaskId();
                        GenerateRecord generateRecord = context.getGenerateRecord();// 生成记录
                        Message message = new Message();
                        message.setTaskId(taskId);
                        message.setTotal(generateRecord.getTotal());
                        message.setComplete(generateRecord.getComplete());
                        message.setError(generateRecord.getError());
                        message.setStatus(StaticTaskEO.EXCEPTION);
                        eo.setData(message);
                        MessageSender.sendMessage(eo);// 发送消息
                        // 如果任务id不为空，更新数据库
                        if (null != taskId) {
                            staticTaskService.delete(StaticTaskEO.class, taskId);
                        }
                        return null;
                    }
                });
            }

            @Override
            public Boolean complete(Boolean result, Throwable exception) {
                try {
                    // 执行清空容器方法
                    CenterContextLoaderListener.super.contextDestroyed(servletContextEvent);
                } catch (Throwable e) {
                    logger.error("容器销毁失败.", e);
                }
                return result;
            }
        });
    }
}