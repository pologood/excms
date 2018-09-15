/*
 * PublicWorksServiceImpl.java         2016年9月22日 <br/>
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

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import cn.lonsun.core.base.service.impl.BaseService;
import cn.lonsun.core.util.Pagination;
import cn.lonsun.publicInfo.internal.dao.IPublicWorksDao;
import cn.lonsun.publicInfo.internal.entity.PublicWorksEO;
import cn.lonsun.publicInfo.internal.service.IPublicWorksService;
import cn.lonsun.publicInfo.vo.PublicWorksQueryVO;

/**
 * TODO <br/>
 * 
 * @date 2016年9月22日 <br/>
 * @author fangtinghua <br/>
 * @version v1.0 <br/>
 */
@Service
public class PublicWorksServiceImpl extends BaseService<PublicWorksEO> implements IPublicWorksService {

    @Resource
    private IPublicWorksDao publicWorksDao;

    @Override
    public Pagination getPagination(PublicWorksQueryVO queryVO) {
        return publicWorksDao.getPagination(queryVO);
    }
}