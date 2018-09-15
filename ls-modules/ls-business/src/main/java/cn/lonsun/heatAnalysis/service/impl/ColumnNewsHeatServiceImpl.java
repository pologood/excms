/*
 * ColumnNewsHeatServiceImpl.java         2016年4月5日 <br/>
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

package cn.lonsun.heatAnalysis.service.impl;

import java.util.*;

import javax.annotation.Resource;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import cn.lonsun.cache.client.CacheHandler;
import cn.lonsun.content.internal.entity.BaseContentEO;
import cn.lonsun.content.vo.ContentPageVO;
import cn.lonsun.core.base.service.impl.MockService;
import cn.lonsun.core.util.Pagination;
import cn.lonsun.heatAnalysis.dao.IColumnNewsHeatDao;
import cn.lonsun.heatAnalysis.service.IColumnNewsHeatService;
import cn.lonsun.heatAnalysis.vo.ColumnHeatVO;
import cn.lonsun.indicator.internal.entity.IndicatorEO;
import cn.lonsun.rbac.internal.entity.OrganEO;

/**
 * TODO <br/>
 *
 * @date 2016年4月5日 <br/>
 * @author fangtinghua <br/>
 * @version v1.0 <br/>
 */
@Service
public class ColumnNewsHeatServiceImpl extends MockService<BaseContentEO> implements IColumnNewsHeatService {

    @Resource
    private IColumnNewsHeatDao columnNewsHeatDao;

    @Override
    public Pagination getColumnHeatPage(ContentPageVO contentPageVO) {
        Pagination p = columnNewsHeatDao.getColumnHeatPage(contentPageVO);
        if (null != p) {
            List<?> dataList = p.getData();
            for (Object o : dataList) {
                ColumnHeatVO vo = (ColumnHeatVO) o;
                List<String> resultList = new ArrayList<String>();
                this.getParent(resultList, vo.getColumnId());
                Collections.reverse(resultList);
                vo.setColumnName(StringUtils.join(resultList.toArray(), " > "));
            }
        }
        return p;
    }

    @Override
    public Pagination getNewsHeatPage(ContentPageVO contentPageVO) {
        Pagination p = columnNewsHeatDao.getNewsHeatPage(contentPageVO);
        if (null != p) {
            List<?> dataList = p.getData();
            for (Object o : dataList) {
                BaseContentEO vo = (BaseContentEO) o;
                List<String> resultList = new ArrayList<String>();
                this.getParent(resultList, vo.getColumnId());
                Collections.reverse(resultList);
                vo.setColumnName(StringUtils.join(resultList.toArray(), " > "));
            }
        }
        return p;
    }

    /**
     * 文章部门点击排行
     *
     * @author fangtinghua
     * @param contentPageVO
     * @return
     */
    public Pagination getOrganHeatPage(ContentPageVO contentPageVO) {
        Pagination p = columnNewsHeatDao.getOrganHeatPage(contentPageVO);
        if (null != p) {
            List<?> dataList = p.getData();
            for (Object o : dataList) {
                BaseContentEO vo = (BaseContentEO) o;
                OrganEO eo = CacheHandler.getEntity(OrganEO.class, vo.getCreateOrganId());
                if (null != eo) {
                    vo.setOrganName(eo.getName());
                }
            }
        }
        return p;
    }

    @Override
    public List<BaseContentEO> getNewsHeatList(ContentPageVO contentPageVO) {
        Long start = System.currentTimeMillis();
        List<BaseContentEO> result = columnNewsHeatDao.getNewsHeatList(contentPageVO);
        if (null != result) {
            for (BaseContentEO vo : result) {
                vo.setColumnName(getColumnName(vo.getColumnId()));
            }
        }
        return result;
    }

    /**
     * 获取栏目名称
     *
     * @author fangtinghua
     * @param resultList
     * @param parentId
     */
    private void getParent(List<String> resultList, Long parentId) {
        if (null == parentId) {
            return;
        }
        IndicatorEO eo = CacheHandler.getEntity(IndicatorEO.class, parentId);
        if (null != eo && IndicatorEO.Type.CMS_Section.toString().equals(eo.getType())) {
            resultList.add(eo.getName());
            this.getParent(resultList, eo.getParentId());
        }
    }


    private static Map<Long, String> columnNameCache = new LinkedHashMap<Long, String>();

    /**
     * 性能优化，增加到缓存
     * @param columnId
     * @return
     * @author zhongjun
     */
    public synchronized String getColumnName(Long columnId){
        if(columnNameCache.containsKey(columnId)){
            return columnNameCache.get(columnId);
        }
        List<String> resultList = new ArrayList<String>();
        getParent(resultList, columnId);
        Collections.reverse(resultList);
        String columnName = StringUtils.join(resultList.toArray(), " > ");
        columnNameCache.put(columnId, columnName);
        return columnName;
    }

}