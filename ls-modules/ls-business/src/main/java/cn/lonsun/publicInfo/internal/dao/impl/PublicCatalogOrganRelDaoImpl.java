/*
 * PublicCatalogOrganRelDaoImpl.java         2015年12月10日 <br/>
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

package cn.lonsun.publicInfo.internal.dao.impl;

import cn.lonsun.core.base.dao.impl.MockDao;
import org.springframework.stereotype.Repository;

import cn.lonsun.core.base.dao.impl.BaseDao;
import cn.lonsun.publicInfo.internal.dao.IPublicCatalogOrganRelDao;
import cn.lonsun.publicInfo.internal.entity.PublicCatalogOrganRelEO;

/**
 * TODO <br/>
 *
 * @author fangtinghua <br/>
 * @version v1.0 <br/>
 * @date 2015年12月10日 <br/>
 */
@Repository
public class PublicCatalogOrganRelDaoImpl extends MockDao<PublicCatalogOrganRelEO> implements IPublicCatalogOrganRelDao {

    @Override
    public void delete(Long catId, Long organId) {
        String hql = "delete from PublicCatalogOrganRelEO where organId = ? and catId = ?";
        this.executeUpdateByHql(hql, new Object[]{organId, catId});
    }

    public void deleteAll() {
        String hql = "delete PublicCatalogOrganRelEO";
        this.executeUpdateByHql(hql, null);
    }

    @Override
    public Long getSourceColumnCount(String referColumnId){
        String hql = "select 1 from PublicCatalogOrganRelEO r where r.referColumnIds like ? ";
        return getCount(hql,new Object[]{"%".concat(referColumnId).concat("%")});
    }

    /**
     * 查询引用目录的源栏目
     * @param referOrganCatId
     * @return
     */
    @Override
    public Long getSourceOrganCatCount(String referOrganCatId){
        String hql = "select 1 from PublicCatalogOrganRelEO r where r.referOrganCatIds like ? ";
        return getCount(hql,new Object[]{"%".concat(referOrganCatId).concat("%")});
    }
}