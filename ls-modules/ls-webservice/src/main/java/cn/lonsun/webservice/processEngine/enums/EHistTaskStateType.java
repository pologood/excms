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
 * 活动类型 <br/>
 * 
 * @date 2014-9-11 <br/>
 * @author lonsun_01 <br/>
 * @version v1.0
 */
public enum EHistTaskStateType {
    
    /**
     * 完成;
     */
    done,
    
    /**
     * transfer-转办(流程管理员)
     */
    transfer,

    /**
     * 退办
     */
    fallback,

    /**
     * 取消
     */
    cancel,

    /**
     * 已阅
     */
    readDone,

    /**
     * 终止
     */
    terminate
}
