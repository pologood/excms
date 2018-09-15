/*
 * MessageConfig.java         2015年11月28日 <br/>
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

/**
 * 消息队列配置信息 <br/>
 *
 * @date 2015年11月28日 <br/>
 * @author fangtinghua <br/>
 * @version v1.0 <br/>
 * @update by 2018-01-11 请使用MessageChannel
 */
@Deprecated
public class MessageConfig {

    // 生成静态队列名称
    public static final String STATIC_GENERATE_QUEUE = "EX8.STATIC.GENERATE.QUEUE";
    // 生成静态取消队列名称
    public static final String STATIC_CANCEL_QUEUE = "EX8.STATIC.CANCEL.QUEUE";
    // 系统消息队列名称
    public static final String MESSAGE_QUEUE = "EX8.MESSAGE.QUEUE";
}