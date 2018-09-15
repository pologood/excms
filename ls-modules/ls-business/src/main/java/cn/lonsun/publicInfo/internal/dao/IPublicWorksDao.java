/*
 * IPublicWorksDao.java         2016年9月22日 <br/>
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
import cn.lonsun.core.util.Pagination;
import cn.lonsun.publicInfo.internal.entity.PublicWorksEO;
import cn.lonsun.publicInfo.vo.PublicWorksQueryVO;

/**
 * TODO <br/>
 * 
 * @date 2016年9月22日 <br/>
 * @author fangtinghua <br/>
 * @version v1.0 <br/>
 */
public interface IPublicWorksDao extends IBaseDao<PublicWorksEO> {

    /**
     * 查询分页
     * 
     * @author fangtinghua
     * @param queryVO
     * @return
     */
    Pagination getPagination(PublicWorksQueryVO queryVO);
}