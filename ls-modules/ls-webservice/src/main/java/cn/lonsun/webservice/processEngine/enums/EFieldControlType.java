/*
 * EFieldControlType.java         2014-9-23 <br/>
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
 * TODO <br/>
 * 
 * @date 2014-9-23 <br/>
 * @author lonsun_01 <br/>
 * @version v1.0
 */
public enum EFieldControlType {
    /** 可读不可写 */
    readable,
    /** 可写字段 */
    writeable,
    /** 批示意见字段 */
    comment,
    /** 保护字段 */
    protectable
}
