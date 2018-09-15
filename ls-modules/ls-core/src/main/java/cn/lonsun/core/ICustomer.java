/*
 * ICustomer.java         2015年8月19日 <br/>
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

package cn.lonsun.core;

/**
 * 消费者 <br/>
 *
 * @date 2015年8月19日 <br/>
 * @author fangtinghua <br/>
 * @version v1.0 <br/>
 */
public interface ICustomer<T> {

    /**
     * 
     * 进行消息消费
     *
     * @author fangtinghua
     * @param content
     */
    void add(T content);
}