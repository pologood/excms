/*
 * IPublicCatalogOrganRelDao.java         2015年12月10日 <br/>
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
import cn.lonsun.core.base.dao.IMockDao;
import cn.lonsun.publicInfo.internal.entity.PublicCatalogOrganRelEO;

/**
 * TODO <br/>
 *
 * @author fangtinghua <br/>
 * @version v1.0 <br/>
 * @date 2015年12月10日 <br/>
 */
public interface IPublicCatalogOrganRelDao extends IMockDao<PublicCatalogOrganRelEO> {

    public void delete(Long catId, Long organId);

    public void deleteAll();

    /**
     * 查询引用栏目的源目录
     * @param referColumnId
     * @return
     */
    Long getSourceColumnCount(String referColumnId);

    /**
     * 查询引用目录的源栏目
     * @param referOrganCatId
     * @return
     */
    Long getSourceOrganCatCount(String referOrganCatId);
}