/*
 * PublicCatalogService.java         2015年12月5日 <br/>
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
import cn.lonsun.publicInfo.vo.OrganCatalogQueryVO;
import cn.lonsun.publicInfo.vo.OrganCatalogVO;

import java.util.List;
import java.util.Map;

/**
 * TODO <br/>
 *
 * @author fangtinghua <br/>
 * @version v1.0 <br/>
 * @date 2015年12月5日 <br/>
 */
public interface IPublicCatalogService extends IMockService<PublicCatalogEO> {

    public Long saveEntity(PublicCatalogEO publicCatalogEO, Long organId);

    public void delete(Long id, Long organId);

    public void saveImportCatalogList(Map<String, List<PublicCatalogEO>> mapList, int startRow);

    public List<PublicCatalogEO> getAllChildListByCatId(Long catId);

    public List<PublicCatalogEO> getAllLeafListByCatId(Long catId);

    public List<OrganCatalogVO> getOrganCatalogTree(OrganCatalogQueryVO queryVO);

    public List<OrganCatalogVO> getWebOrganCatalogTree(OrganCatalogQueryVO queryVO);

    public List<Long> updateShowOrHideCatalog(PublicCatalogOrganRelEO eo, boolean join);

    public List<Object> getAllCatIdHaveContent(Long organId,Long parentId);

    public List<PublicCatalogEO> getOrganCatalog(Long organId);
}