/*
 * ContentMongoServiceImpl.java         2015年10月27日 <br/>
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

package cn.lonsun.mongodb.service;

import org.springframework.stereotype.Service;

import cn.lonsun.mongodb.base.impl.MongoDbBaseDao;
import cn.lonsun.mongodb.entity.ContentMongoEO;

/**
 * TODO <br/>
 *
 * @date 2015年10月27日 <br/>
 * @author fangtinghua <br/>
 * @version v1.0 <br/>
 */
@Service
public class ContentMongoServiceImpl extends MongoDbBaseDao<ContentMongoEO> {

    @Override
    protected Class<ContentMongoEO> getEntityClass() {
        return ContentMongoEO.class;
    }
}