/*
 * HttpEnum.java         2016年1月14日 <br/>
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

package cn.lonsun.webservice;

/**
 * http调用编码 ADD REASON. <br/>
 *
 * @date: 2016年1月13日 下午2:48:03 <br/>
 * @author fangtinghua
 */
public enum HttpEnum {

    HTML, // 动态请求
    CONTENT_HIT, // 文章点击数
    CONTENT_LAST_NEXT // 上一篇下一篇
}