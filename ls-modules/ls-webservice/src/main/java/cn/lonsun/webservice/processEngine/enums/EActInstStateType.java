/*
 * ERuTaskStateType.java         2014-9-16 <br/>
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
 * 活动任务实例
 *
 * @author lonsun_01
 * @date 2014-9-16
 * @version v1.0
 */
public enum EActInstStateType {

    /**
     * 待办
     */
    waiting,

    /**
     * 继续（多人并办或串并）;
     */
    goingon,

    /**
     * -取消(发起人取消);
     */
    canceled,

    /**
     * 退回(审批人退回),
     */
    fallback,

    /**
     * 暂停
     */
    paused,

    /**
     * 结束
     */
    completed,

    /**
     * transfer-转办(流程管理员)
     */
    transfer,

    /**
     * 终止
     */
    terminate
}

