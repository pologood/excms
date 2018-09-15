/*
 * OrganConfigServiceImpl.java         2015年12月12日 <br/>
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

package cn.lonsun.publicInfo.internal.service.impl;

import cn.lonsun.publicInfo.internal.dao.IOrganConfigDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cn.lonsun.core.base.service.impl.BaseService;
import cn.lonsun.publicInfo.internal.entity.OrganConfigEO;
import cn.lonsun.publicInfo.internal.service.IOrganConfigService;

import java.util.List;

/**
 * TODO <br/>
 *
 * @date 2015年12月12日 <br/>
 * @author fangtinghua <br/>
 * @version v1.0 <br/>
 */
@Service
public class OrganConfigServiceImpl extends BaseService<OrganConfigEO> implements IOrganConfigService {

    @Autowired
    private IOrganConfigDao organConfigDao;

    @Override
    public List<Long> getOrganIdsByCatId(Long catId) {
        return organConfigDao.getOrganIdsByCatId(catId);
    }
}