/*
 * IOrganConfigService.java         2015年12月12日 <br/>
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

package cn.lonsun.publicInfo.internal.service;

import cn.lonsun.core.base.service.IBaseService;
import cn.lonsun.publicInfo.internal.entity.OrganConfigEO;

import java.util.List;

/**
 * TODO <br/>
 * 
 * @date 2015年12月12日 <br/>
 * @author fangtinghua <br/>
 * @version v1.0 <br/>
 */
public interface IOrganConfigService extends IBaseService<OrganConfigEO> {
    /**
     * 通过catid获取配置改目录的单位
     * 
     * @param catId
     * @return
     */
    List<Long> getOrganIdsByCatId(Long catId);
}