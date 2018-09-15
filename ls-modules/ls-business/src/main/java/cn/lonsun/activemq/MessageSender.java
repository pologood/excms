/*
 * MessageSender.java         2015年9月9日 <br/>
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

package cn.lonsun.activemq;

import cn.lonsun.core.util.SpringContextHolder;
import cn.lonsun.message.internal.entity.MessageEnum;
import cn.lonsun.message.internal.entity.MessageStaticEO;
import cn.lonsun.message.internal.entity.MessageSystemEO;
import cn.lonsun.statictask.internal.entity.StaticTaskEO;
import cn.lonsun.statictask.internal.service.IStaticTaskService;
import cn.lonsun.util.LoginPersonUtil;
import com.alibaba.fastjson.JSON;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jms.JmsException;

import java.io.Serializable;

/**
 * 消息发送 ADD REASON. <br/>
 *
 * @author fangtinghua
 * @date: 2015年9月9日 下午4:30:22 <br/>
 */
public class MessageSender {

    // 日志
    private static final Logger logger = LoggerFactory.getLogger(MessageSender.class);

    private static final IStaticTaskService staticTaskService = SpringContextHolder.getBean(IStaticTaskService.class);
    /**
     * 系统消息发送接口
     */
    private static final MqMessageSender systemMessageSender = SpringContextHolder.getBean("systemMessageSender");
    /**
     * 生成静态消息发送接口
     */
    private static final MqMessageSender staticGenerateMessageSender = SpringContextHolder.getBean("staticGenerateMessageSender");
    /**
     * 取消生成静态消息接口
     */
    private static final MqMessageSender staticCancelMessageSender = SpringContextHolder.getBean("staticCancelMessageSender");


    /**
     * 发送消息
     *
     * @param object
     * @return
     * @author fangtinghua
     */
    private static boolean sendMessage(MqMessageSender messageSender, final Serializable object) {
        try {
            messageSender.send(object);
        } catch (JmsException e) {
            logger.error("发送消息失败，请检查mq是否正常启动.", e);
            return false;
        }
        return true;
    }

    /**
     * 发送生成静态消息
     * @param eo
     * @return
     * @author fangtinghua
     */
    public static boolean sendMessage(MessageStaticEO eo) {
        if (null == eo.getUserId()) {
            eo.setUserId(LoginPersonUtil.getUserId());// 设置用户id
        }
        MqMessageSender destination = staticGenerateMessageSender;// 默认队列
        StaticTaskEO staticTaskEO = new StaticTaskEO();
        if (eo.isTodb()) {// 要入库
            // 判断taskId是否存在
            Long taskId = eo.getTaskId();
            if (null == taskId) {
                StaticMessageUtil.initStaticEO(staticTaskEO, eo);
                staticTaskEO.setJson(JSON.toJSONString(eo));
                staticTaskService.saveEntity(staticTaskEO);
            } else if (MessageEnum.RESTART.value().equals(eo.getType())) {// 重新生成需要重置数据库
                staticTaskEO = staticTaskService.getEntity(StaticTaskEO.class, taskId);
                if (null == staticTaskEO) {
                    logger.error("生成静态任务id:" + taskId + "不存在数据.");
                    return false;
                }
                staticTaskEO.setStatus(StaticTaskEO.INIT);
                staticTaskEO.setCount(0L);
                staticTaskEO.setDoneCount(0L);
                staticTaskEO.setFailCount(0L);
                staticTaskService.updateEntity(staticTaskEO);
            } else if (MessageEnum.OVER.value().equals(eo.getType())) {// 终止
                destination = staticCancelMessageSender;
                staticTaskEO.setJson(JSON.toJSONString(eo));
            }
        } else {// 设置标题
            staticTaskEO.setType(eo.getType());
            staticTaskEO.setJson(JSON.toJSONString(eo));
            staticTaskEO.setTitle(StaticMessageUtil.getColumnTitle(eo.getColumnId(), eo.getSource()));
        }
        logger.info("=========发送消息start==========\n{}\n==========消息体end==========", staticTaskEO.getJson());
        return sendMessage(destination, staticTaskEO);
    }

    /**
     * 发送系统消息
     *
     * @param eo
     * @return
     * @author fangtinghua
     */
    public static boolean sendMessage(MessageSystemEO eo) {
        return sendMessage(systemMessageSender, eo);
    }
}