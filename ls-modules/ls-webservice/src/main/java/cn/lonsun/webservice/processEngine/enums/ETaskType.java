/*
 * EActivityType.java         2014-9-11 <br/>
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

package cn.lonsun.webservice.processEngine.enums;

/**
 * 任务类型 <br/>
 * 
 * @date 2014-9-11 <br/>
 * @author lonsun_01 <br/>
 * @version v1.0
 */
public enum ETaskType {
    /**
     * 普通任务
     */
    common,
    /**
     * 代理任务
     */
    agent,
    /**
     * 委托任务
     */
    delegate,

    /**
     * 阅件任务
     */
    reader
}
