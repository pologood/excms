/*
 * KeyWordsHeatDaoImpl.java         2016年4月6日 <br/>
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

package cn.lonsun.heatAnalysis.dao.impl;

import org.springframework.stereotype.Repository;

import cn.lonsun.content.vo.ContentPageVO;
import cn.lonsun.core.base.dao.impl.BaseDao;
import cn.lonsun.core.base.entity.AMockEntity;
import cn.lonsun.core.util.Pagination;
import cn.lonsun.heatAnalysis.dao.IKeyWordsHeatDao;
import cn.lonsun.heatAnalysis.entity.KeyWordsHeatEO;
import cn.lonsun.heatAnalysis.vo.KeyWordsHeatVO;

import java.util.List;

/**
 * TODO <br/>
 *
 * @date 2016年4月6日 <br/>
 * @author fangtinghua <br/>
 * @version v1.0 <br/>
 */
@Repository("keyWordsHeatDao")
public class KeyWordsHeatDaoImpl extends BaseDao<KeyWordsHeatEO> implements IKeyWordsHeatDao {

    @Override
    public Pagination getKeyWordsHeatPage(ContentPageVO contentPageVO) {
        StringBuffer hql = new StringBuffer();
        hql.append("select max(siteId) as siteId,keyWords as keyWords,sum(1) as searchTimes from IndexKeyWordsEO");
        hql.append(" where recordStatus = ? and siteId = ? group by keyWords order by searchTimes desc");
        return getPagination(contentPageVO.getPageIndex(), contentPageVO.getPageSize(), hql.toString(),
                new Object[] { AMockEntity.RecordStatus.Normal.toString(), contentPageVO.getSiteId() }, KeyWordsHeatVO.class);
    }

    @Override
    public Pagination getKeyWordsSortPage(ContentPageVO contentPageVO) {
        String hql = "from KeyWordsHeatEO where siteId = ? order by sortNum";
        return getPagination(contentPageVO.getPageIndex(), contentPageVO.getPageSize(), hql, new Object[] { contentPageVO.getSiteId() });
    }

    @Override
    public Long getMinSortNum(Long siteId) {
        String hql = "select nvl(min(sortNum),1) as c from KeyWordsHeatEO where siteId = ?";
        return getCount(hql, new Object[] { siteId });
    }

    @Override
    public void updateSortNumBySiteId(Long siteId) {
        String hql = "update KeyWordsHeatEO set sortNum = sortNum + 20 where siteId = ?";
        executeUpdateByHql(hql, new Object[] { siteId });
    }

    @Override
    public List<KeyWordsHeatEO> getKeyWordsHeatList(Long siteId, Integer topCount, Integer sort) {
        StringBuilder hql = new StringBuilder("select t.id as id,t.siteId as siteId,t.keyWords as keyWords,t.sortNum as sortNum from KeyWordsHeatEO t where t.siteId = ?");
        if(Integer.valueOf(0).equals(sort)){
            hql.append(" order by t.sortNum desc ");
        }else if(Integer.valueOf(1).equals(sort)){
            hql.append(" order by t.sortNum ");
        }
        return (List<KeyWordsHeatEO>)getBeansByHql(hql.toString(),new Object[]{siteId},KeyWordsHeatEO.class,topCount);
    }
}