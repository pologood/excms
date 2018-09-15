/*
 * IPublicClassService.java         2015年12月11日 <br/>
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

import cn.lonsun.core.base.service.IMockService;
import cn.lonsun.publicInfo.internal.entity.PublicClassEO;
import cn.lonsun.publicInfo.vo.PublicClassMobileVO;
import cn.lonsun.publicInfo.vo.PublicContentRetrievalVO;

import java.util.List;

/**
 * TODO <br/>
 *
 * @date 2015年12月11日 <br/>
 * @author fangtinghua <br/>
 * @version v1.0 <br/>
 */
public interface IPublicClassService extends IMockService<PublicClassEO> {

    public List<PublicClassMobileVO> getPublicClassify(PublicContentRetrievalVO vo);

    public List<PublicClassEO> getChildClass(String classIds,Long siteId);

}