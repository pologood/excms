/*
 * EProcInstState.java         2014-9-28 <br/>
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
 * 流程状态,对应reStatus记录 <br/>
 * 
 * date 2014-9-28 <br/>
 * @author lonsun_01 <br/>
 * @version v1.0
 */
public enum EProcInstState {
    /** 开始 */
    start,
    /** 结束 */
    end,
    /**挂起*/
    suspend,

    /*流程终止*/
    terminate
}
