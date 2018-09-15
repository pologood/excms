/*
 * IPublicCatalogDao.java         2015年12月23日 <br/>
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

import cn.lonsun.core.base.dao.IMockDao;
import cn.lonsun.publicInfo.internal.entity.PublicCatalogEO;

import java.util.List;

/**
 * 目录dao <br/>
 *
 * @author fangtinghua <br/>
 * @version v1.0 <br/>
 * @date 2015年12月23日 <br/>
 */
public interface IPublicCatalogDao extends IMockDao<PublicCatalogEO> {

    public void insertBySql(List<PublicCatalogEO> list);

    List<PublicCatalogEO> getAllChildListByCatId(Long catId);

    List<PublicCatalogEO> getAllLeafListByCatId(Long catId);

    List<PublicCatalogEO> getChildListByCatId(Long catId);

    void deleteAll();

    List<Object> getAllCatIdHaveContent(Long organId,Long parentId);
}