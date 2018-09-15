/*
 * Process.java         2015年9月11日 <br/>
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

package cn.lonsun.staticcenter.generate.special;

/**
 * 属性字段特殊处理，比如link获取连接的时候 <br/>
 *
 * @date 2015年9月11日 <br/>
 * @author fangtinghua <br/>
 * @version v1.0 <br/>
 */
public interface Process {

    /**
     * 
     * 处理
     *
     * @author fangtinghua
     * @param Object obj
     * @return
     */
    String getValue(Object obj);
}