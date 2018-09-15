/*
 * IPublicApplyDao.java         2015年12月25日 <br/>
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

package cn.lonsun.publicInfo.internal.dao;

import cn.lonsun.core.base.dao.IBaseDao;
import cn.lonsun.publicInfo.internal.entity.OrganConfigEO;

import java.util.List;

/**
 * TODO <br/>
 *
 * @date 2016年9月13日 <br/>
 * @author liuk <br/>
 * @version v1.0 <br/>
 */
public interface IOrganConfigDao extends IBaseDao<OrganConfigEO> {

    /**
     * 通过catid获取配置改目录的单位
     * @param catId
     * @return
     */
    List<Long> getOrganIdsByCatId(Long catId);
}