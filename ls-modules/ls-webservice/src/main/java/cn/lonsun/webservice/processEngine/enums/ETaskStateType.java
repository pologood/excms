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
 * TODO				
 * @date     2014-9-16
 * @author 	 lonsun_01
 * @version	 v1.0
 */
public enum ETaskStateType {

    /**
     * 待办
     */
    waitDeal,

    /**处理中*/
    inhand,

    /**暂停*/
    paused,

    /** 待阅 */
    waitRead,

    /** 已办 */
    doneDeal,

    /** 已阅 */
    doneRead,

    /**
     * 退办
     */
    fallback,

    /** 结束 */
    end,

    /** 终止 */
    terminate,

    /** 转办 */
    transfer
}

