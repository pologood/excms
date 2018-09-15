/*
 * IPublicContentService.java         2015年12月15日 <br/>
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

import java.util.List;

import cn.lonsun.core.base.service.IMockService;
import cn.lonsun.core.util.Pagination;
import cn.lonsun.publicInfo.internal.entity.PublicLeadersEO;
import cn.lonsun.publicInfo.vo.PublicLeadersQueryVO;

/**
 * 单位领导service <br/>
 * 
 * @date 2016年9月19日 <br/>
 * @author liukun <br/>
 * @version v1.0 <br/>
 */
public interface IPublicLeadersService extends IMockService<PublicLeadersEO> {

    Pagination getPagination(PublicLeadersQueryVO queryVO);

    List<PublicLeadersEO> getPublicLeadersList(PublicLeadersQueryVO queryVO);

    Long saveOrUpdateLeaders(PublicLeadersEO publicLeadersEO);

    Long admin_saveOrUpdateLeaders(PublicLeadersEO publicLeadersEO);
}