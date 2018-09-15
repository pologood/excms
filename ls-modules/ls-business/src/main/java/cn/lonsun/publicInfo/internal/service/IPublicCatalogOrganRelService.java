/*
 * IPublicCatalogOrganRelService.java         2015年12月5日 <br/>
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
import cn.lonsun.publicInfo.internal.entity.PublicCatalogEO;
import cn.lonsun.publicInfo.internal.entity.PublicCatalogOrganRelEO;

import java.util.List;

/**
 * TODO <br/>
 *
 * @author fangtinghua <br/>
 * @version v1.0 <br/>
 * @date 2015年12月5日 <br/>
 */
public interface IPublicCatalogOrganRelService extends IMockService<PublicCatalogOrganRelEO> {

    void delete(Long catId, Long organId);

    void deleteAll();

    List<PublicCatalogOrganRelEO> getHideRelByCatId(Long catId);

    PublicCatalogOrganRelEO getByOrganIdCatId(Long organId, Long catId);

    void savePrivateCatalog(Long organId, PublicCatalogEO publicCatalogEO, Boolean isShow, Boolean isParent);

    boolean updatePrivateCatalog(Long organId, PublicCatalogEO publicCatalogEO, Boolean isShow, Boolean isParent);

    List<Long> getListByOrganId(Long organId, boolean all); // 是否查询所有

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