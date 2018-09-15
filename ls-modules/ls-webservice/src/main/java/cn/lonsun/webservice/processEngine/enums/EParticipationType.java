/*
 * EParticipationType.java         2014-9-11 <br/>
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
 * 参与者类型 <br/>
 * 
 * @date 2014-9-11 <br/>
 * @author lonsun_01 <br/>
 * @version v1.0
 */
public enum EParticipationType {
    // 单位内用户
    inDepUser,
    // 跨单位用户
    crossDepUser,
    // 角色
    role,
    //候选人
    candidate,
    //指定单位
    dept,
    //外部指定
    externalAssign,
    //启动人
    promoter,
    EParticipationType, //角色用户
    roleUser
}
