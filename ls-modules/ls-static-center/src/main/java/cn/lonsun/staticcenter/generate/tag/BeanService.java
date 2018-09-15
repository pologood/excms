/*
 * Service.java         2015年8月13日 <br/>
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

import com.alibaba.fastjson.JSONObject;

/**
 * 标签处理最上层接口 <br/>
 *
 * @date 2015年8月13日 <br/>
 * @author fangtinghua <br/>
 * @version v1.0 <br/>
 */
public interface BeanService {

    /**
     * 前置查询
     *
     * @author fangtinghua
     * @param paramObj
     * @return
     */
    boolean before(JSONObject paramObj) throws GenerateException;

    /**
     * 查询
     *
     * @author fangtinghua
     * @param paramObj
     * @return
     */
    Object getObject(JSONObject paramObj) throws GenerateException;

    /**
     * 将查询结果转换为字符串
     *
     * @author fangtinghua
     * @param content
     *            标签中间的内容
     * @param resultObj
     *            查询结果对象
     * @param paramObj
     *            查询参数
     * @return
     */
    String objToStr(String content, Object resultObj, JSONObject paramObj) throws GenerateException;
}