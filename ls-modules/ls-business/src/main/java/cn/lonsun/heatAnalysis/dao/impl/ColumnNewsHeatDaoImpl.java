/*
 * ColumnNewsHeatDaoImpl.java         2016年4月5日 <br/>
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

import cn.lonsun.content.internal.entity.BaseContentEO;
import cn.lonsun.content.vo.ContentPageVO;
import cn.lonsun.core.base.dao.impl.MockDao;
import cn.lonsun.core.base.entity.AMockEntity;
import cn.lonsun.core.util.Pagination;
import cn.lonsun.heatAnalysis.dao.IColumnNewsHeatDao;
import cn.lonsun.heatAnalysis.vo.ColumnHeatVO;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * TODO <br/>
 *
 * @date 2016年4月5日 <br/>
 * @author fangtinghua <br/>
 * @version v1.0 <br/>
 */
@Repository
public class ColumnNewsHeatDaoImpl extends MockDao<BaseContentEO> implements IColumnNewsHeatDao {

    @Override
    public Pagination getColumnHeatPage(ContentPageVO contentPageVO) {
        StringBuffer hql = new StringBuffer();
       String types[]=contentPageVO.getTypeCodes();
        hql.append("select columnId as columnId,SUM(hit) as hit from BaseContentEO where recordStatus = ? and isPublish = ? and siteId = ?");
        hql.append(" and columnId is not null and (");
        for(int i=0;i<types.length;i++){
          hql.append("  typeCode ='"+types[i]+"'");

          if(i<types.length-1){
              hql.append(" or ");
          }

        }
        hql.append(" ) ");
        hql.append(" group by columnId order by hit desc");


        return getPagination(contentPageVO.getPageIndex(), contentPageVO.getPageSize(), hql.toString(),
                new Object[] { AMockEntity.RecordStatus.Normal.toString(), 1, contentPageVO.getSiteId() },
                ColumnHeatVO.class);
    }

    @Override
    public Pagination getNewsHeatPage(ContentPageVO contentPageVO) {
        StringBuffer hql = new StringBuffer();
        Map<String, Object> map = new HashMap<String, Object>();
        hql.append("from BaseContentEO where recordStatus = :recordStatus and siteId = :siteId");
        hql.append(" and isPublish = :isPublish and typeCode in (:typeCodes) order by hit desc");
        map.put("recordStatus", AMockEntity.RecordStatus.Normal.toString());
        map.put("siteId", contentPageVO.getSiteId());
        map.put("isPublish",1);
        map.put("typeCodes", contentPageVO.getTypeCodes());
        return getPagination(contentPageVO.getPageIndex(), contentPageVO.getPageSize(), hql.toString(), map);
    }

    /**
     * 区别于getNewsHeatPage，此方法只根据pageSize取最新的条数
     * @param contentPageVO
     * @return
     */
    @Override
    public List<BaseContentEO> getNewsHeatList(ContentPageVO contentPageVO) {
        StringBuffer hql = new StringBuffer();
        Map<String, Object> map = new HashMap<String, Object>();
        hql.append("from BaseContentEO where recordStatus = :recordStatus and siteId = :siteId");
        hql.append(" and isPublish = :isPublish and typeCode in (:typeCodes) order by hit desc");
        map.put("recordStatus", AMockEntity.RecordStatus.Normal.toString());
        map.put("siteId", contentPageVO.getSiteId());
        map.put("isPublish",1);
        map.put("typeCodes", contentPageVO.getTypeCodes());
        return getEntities(hql.toString(), map, contentPageVO.getPageSize());
    }

    @Override
    public Pagination getOrganHeatPage(ContentPageVO contentPageVO) {
        StringBuffer hql = new StringBuffer();
        Map<String, Object> map = new HashMap<String, Object>();
        hql.append("select createOrganId as createOrganId,SUM(hit) as hit from BaseContentEO where recordStatus = :recordStatus and siteId = :siteId");
        hql.append(" and isPublish = :isPublish and typeCode in (:typeCodes) group by createOrganId order by hit desc");
        map.put("recordStatus", AMockEntity.RecordStatus.Normal.toString());
        map.put("siteId", contentPageVO.getSiteId());
        map.put("isPublish",1);
        map.put("typeCodes", contentPageVO.getTypeCodes());
        return getPagination(contentPageVO.getPageIndex(), contentPageVO.getPageSize(), hql.toString(), map);
    }
}