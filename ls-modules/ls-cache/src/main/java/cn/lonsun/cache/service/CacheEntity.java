/*
 * CacheEntity.java         2015年10月22日 <br/>
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

package cn.lonsun.cache.service;

import cn.lonsun.cache.client.CacheGroup;

import java.util.HashMap;
import java.util.Map;

/**
 * 缓存实体关系类				<br/>
 *	 
 * @date     2015年10月22日 	<br/>
 * @author 	 fangtinghua	<br/>
 * @version	 v1.0 		<br/>
 */
/**
 * 
 * 每种存储结构为一个实体对象 ADD REASON. <br/>
 *
 * @date: 2015年10月22日 下午4:21:28 <br/>
 * @author fangtinghua
 */
public class CacheEntity {

    /** 由两部分组成，第一部分为对象id，第二部分为缓存指定，当没指定时，默认空 */
//    private String[] id; // 第二键值，第一部分，字段
    private Map<String, Class> id = new HashMap<String, Class>();
    private String field = CacheGroup.CMS_ID;// 第二键值，第二部分，不会重复
    /** 由两部分组成，第一部分为对象id，第二部分为缓存指定，当没指定时，默认空 */
    private boolean isGroup = false;// 是否是一对多关系，默认是一对一关系

    public CacheEntity() {
        super();
    }

    public CacheEntity(String field, boolean isGroup, Map<String, Class> id) {
        super();
        this.id = id;
        this.field = field;
        this.isGroup = isGroup;
    }

    public  Map<String, Class> getId() {
        return id;
    }

    public void setId(Map<String, Class> id) {
        this.id = id;
    }

    public String getField() {
        return field;
    }

    public void setField(String field) {
        this.field = field;
    }

    public boolean isGroup() {
        return isGroup;
    }

    public void setGroup(boolean isGroup) {
        this.isGroup = isGroup;
    }
}