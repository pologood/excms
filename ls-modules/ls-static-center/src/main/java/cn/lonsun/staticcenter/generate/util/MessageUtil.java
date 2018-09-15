/*
 * MessageUtil.java         2016年3月2日 <br/>
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

package cn.lonsun.staticcenter.generate.util;

import cn.lonsun.activemq.MessageSender;
import cn.lonsun.message.internal.entity.Message;
import cn.lonsun.message.internal.entity.MessageSystemEO;
import cn.lonsun.staticcenter.generate.GenerateRecord;
import cn.lonsun.staticcenter.generate.thread.Context;
import cn.lonsun.staticcenter.generate.thread.ContextHolder;
import cn.lonsun.statictask.internal.entity.StaticTaskEO;

/**
 * 生成静态消息推送 <br/>
 *
 * @date 2016年3月2日 <br/>
 * @author fangtinghua <br/>
 * @version v1.0 <br/>
 */
public class MessageUtil {

    /**
     * 发送消息
     *
     * @author fangtinghua
     */
    public static void sendMessage() {
        Context context = ContextHolder.getContext();// 获取上下文
        // 发送消息
        MessageSystemEO eo = new MessageSystemEO();
        eo.setTodb(false); // 进度消息不需要入库
        eo.setSiteId(context.getSiteId());
        eo.setRecUserIds(String.valueOf(context.getUserId()));
        eo.setMessageType(MessageSystemEO.OTHER);
        eo.setMessageStatus(MessageSystemEO.MessageStatus.success.toString());
        // 构建具体数据
        Message message = new Message();
        GenerateRecord generateRecord = context.getGenerateRecord();
        message.setName(generateRecord.getName());
        message.setTitle(context.getTitle());
        message.setTotal(generateRecord.getTotal());
        message.setComplete(generateRecord.getComplete());
        message.setError(generateRecord.getError());
        message.setColumnId(context.getColumnId());
        message.setContentId(context.getContentId());
        message.setScope(context.getScope());
        message.setTaskId(context.getTaskId());
        message.setLink(context.getLink());
        message.setStatus(StaticTaskEO.DOING);
        eo.setData(message);
        MessageSender.sendMessage(eo);
    }
}