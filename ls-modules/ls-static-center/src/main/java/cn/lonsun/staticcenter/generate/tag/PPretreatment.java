/*
 * PropertyPretreatment.java         2015年8月14日 <br/>
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

package cn.lonsun.staticcenter.generate.tag;

import cn.lonsun.staticcenter.generate.GenerateException;

/**
 * 属性预处理接口 <br/>
 *
 * @date 2015年8月14日 <br/>
 * @author fangtinghua <br/>
 * @version v1.0 <br/>
 */
public interface PPretreatment {

    /**
     * 预处理
     *
     * @author fangtinghua
     * @param value
     * @param param
     * @return
     * @throws GenerateException
     */
    String getValue(String value, String param) throws GenerateException;
}