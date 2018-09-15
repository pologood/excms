/*
 * PublicClassServiceImpl.java         2015年12月11日 <br/>
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

import cn.lonsun.core.base.service.impl.MockService;
import cn.lonsun.publicInfo.internal.dao.IPublicClassDao;
import cn.lonsun.publicInfo.internal.entity.PublicClassEO;
import cn.lonsun.publicInfo.internal.service.IPublicClassService;
import cn.lonsun.publicInfo.vo.PublicClassMobileVO;
import cn.lonsun.publicInfo.vo.PublicContentRetrievalVO;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * TODO <br/>
 *
 * @date 2015年12月11日 <br/>
 * @author fangtinghua <br/>
 * @version v1.0 <br/>
 */
@Service
public class PublicClassServiceImpl extends MockService<PublicClassEO> implements IPublicClassService {

   @Resource
   private IPublicClassDao publicClassDao;

    @Override
    public List<PublicClassMobileVO> getPublicClassify(PublicContentRetrievalVO vo) {
        return publicClassDao.getPublicClassify(vo);
    }

    @Override
    public List<PublicClassEO> getChildClass(String classIds,Long siteId) {
        if(!StringUtils.isEmpty(classIds)){
            Long pid=Long.parseLong(classIds);
            return publicClassDao.getChildClass(pid,siteId);
        }
        return null;
    }
}