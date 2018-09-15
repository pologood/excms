/*
 * PublicContentDaoImpl.java         2015年12月16日 <br/>
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

import cn.lonsun.common.util.AppUtil;
import cn.lonsun.content.vo.SortVO;
import cn.lonsun.core.base.entity.AMockEntity;
import cn.lonsun.core.util.Pagination;
import cn.lonsun.core.util.SqlUtil;
import cn.lonsun.publicInfo.vo.PublicContentMobileVO;
import cn.lonsun.publicInfo.vo.PublicContentRetrievalVO;
import cn.lonsun.publicInfo.vo.PublicContentVO;
import cn.lonsun.util.ModelConfigUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

/**
 * 信息公开内容dao <br/>
 * @date 2016年8月26日 <br/>
 * @author liukun <br/>
 * @version v1.0 <br/>
 */
@Repository("publicContentMySqlDao")
public class PublicContentMySqlDaoImpl extends PublicContentDaoImpl {

    /**
     * 获取信息公开查询hql
     *
     * @author liukun
     * @return
     */
    @Override
    public SortVO getMaxNum(PublicContentVO vo) {
        StringBuffer hql = new StringBuffer();
        hql.append("select ifnull(max(p.sortNum),0)+1 as sortNum from PublicContentEO p ");
//        hql.append("where p.siteId = ? and p.organId = ? and p.type = ? and p.recordStatus = ? ");
        hql.append("where p.recordStatus = ? ");
        // 主动公开需要序号
//        if (null != vo.getCatId() && vo.getCatId() > 0L) {
//            hql.append(" and p.catId = " + vo.getCatId());
//        }
        SortVO sortVO = new SortVO();
        Integer sortNum = (Integer) getObject(hql.toString(), new Object[] {AMockEntity.RecordStatus.Normal.toString() });
        sortVO.setSortNum(Long.valueOf(sortNum.toString()));
        return sortVO;
    }


    public Pagination getRetrievalPagination(PublicContentRetrievalVO queryVO) {
        StringBuffer sql = new StringBuffer();
        sql.append("select p.id as id,b.title as title,b.sub_title as subTitle,b.publish_date as publishDate,");
        sql.append("p.file_num as fileNum,b.redirect_link as redirectLink");
        sql.append(" from Cms_Public_Content  p,CMS_BASE_CONTENT  b where p.content_id = b.id");
        sql.append(" and b.record_status='Normal' and b.is_publish=1");
        if (null != queryVO.getSiteId()) {
            sql.append(" and b.site_id = " + queryVO.getSiteId());
        }
        if (!StringUtils.isEmpty(queryVO.getClassIds())) {
            sql.append(" and (p.class_ids like '%" + SqlUtil.prepareParam4Query(queryVO.getClassIds()) + "%'");
            sql.append(" or p.parent_class_ids like '%" + SqlUtil.prepareParam4Query(queryVO.getClassIds()) + "%')");
        }
        // 目录id，只有主动公开会传入
        if (null != queryVO.getCatId() && queryVO.getCatId() > 0L) {
            if (null != queryVO.getCatIds() && queryVO.getCatIds().length > 0) {
                sql.append(" and p.CAT_ID in (" + StringUtils.join(queryVO.getCatIds(), ",") + ")");
            } else {
                sql.append(" and p.CAT_ID = ").append(queryVO.getCatId());
            }
        }
        if (!StringUtils.isEmpty(queryVO.getFileNum())) {
            sql.append(" and p.file_num like '%" + SqlUtil.prepareParam4Query(queryVO.getFileNum()) + "%'");
        }
        if (null != queryVO.getOrganId()) {// 单位id不为空
            sql.append(" and p.organ_id = ").append(queryVO.getOrganId());
        } else if (!StringUtils.isEmpty(queryVO.getOrganName())) {
            sql.append(" and p.organ_id in (case when (select r.organ_id from RBAC_ORGAN r where r.name_='" + queryVO.getOrganName() + "')is null"
                    + " then 0 else (select r.organ_id from RBAC_ORGAN r where r.name_='" + queryVO.getOrganName() + "') end)");
        }
        if (!StringUtils.isEmpty(queryVO.getKeyWord())) {
            sql.append(" and (b.title like '%" + SqlUtil.prepareParam4Query(queryVO.getKeyWord()) + "%'");
            sql.append(" or b.sub_title like '%" + SqlUtil.prepareParam4Query(queryVO.getKeyWord()) + "%'");
            sql.append(" or b.remarks like '%" + SqlUtil.prepareParam4Query(queryVO.getKeyWord()) + "%')");
        }
        if (!AppUtil.isEmpty(queryVO.getStartDate())) {
            String startDate = queryVO.getStartDate() + " 00:00:00";
            sql.append(" and b.publish_date>=str_to_date('"+startDate+"','%Y-%m-%d %H:%i:%s')");
        }
        if (!AppUtil.isEmpty(queryVO.getEndDate())) {
            String endDate = queryVO.getEndDate() + " 23:59:59";
            sql.append(" and b.publish_date<=str_to_date('"+endDate+"','%Y-%m-%d %H:%i:%s')");
        }
        // 排序
        String sort = ModelConfigUtil.getOrderByHqlForPublic(queryVO.getOrganId(), queryVO.getSiteId());
        sort = sort.replace("sortNum", "sort_Num").replace("isTop", "is_top").replace("publishDate", "publish_date");
        sql.append(sort);
        // 放入参数对象
        Object[] values = new Object[] {};
        List<String> fields = new ArrayList<String>();
        fields.add("id");
        fields.add("title");
        fields.add("subTitle");
        fields.add("publishDate");
        fields.add("fileNum");
        fields.add("redirectLink");
        String[] arr = new String[fields.size()];
        return getPaginationBySql(queryVO.getPageIndex(), queryVO.getPageSize(), sql.toString(), values, PublicContentMobileVO.class, fields.toArray(arr));
    }

}