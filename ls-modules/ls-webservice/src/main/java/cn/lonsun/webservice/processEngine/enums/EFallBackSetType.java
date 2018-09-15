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
 * 回退类型
 *  notAllow（不允许）, beforeStep（上一步）, allHistoryStep（所有历史节点）
 * 
 * @date 2014-9-11
 * @author Lee
 */
public enum EFallBackSetType {
    
    notAllow,
    
    beforeStep,

    allHistoryStep
}
