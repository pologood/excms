/*
 * PublicWorksDaoImpl.java         2016年9月22日 <br/>
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

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Repository;

import cn.lonsun.core.base.dao.impl.BaseDao;
import cn.lonsun.core.base.entity.AMockEntity;
import cn.lonsun.core.util.Pagination;
import cn.lonsun.core.util.SqlUtil;
import cn.lonsun.publicInfo.internal.dao.IPublicWorksDao;
import cn.lonsun.publicInfo.internal.entity.PublicWorksEO;
import cn.lonsun.publicInfo.vo.PublicWorksQueryVO;
import cn.lonsun.rbac.internal.entity.OrganEO;

/**
 * TODO <br/>
 * 
 * @date 2016年9月22日 <br/>
 * @author fangtinghua <br/>
 * @version v1.0 <br/>
 */
@Repository
public class PublicWorksDaoImpl extends BaseDao<PublicWorksEO> implements IPublicWorksDao {

    @Override
    public Pagination getPagination(PublicWorksQueryVO queryVO) {
        StringBuffer hql = new StringBuffer();
        if (null != queryVO.getOrganId()) {// 单位id优先级最高
            hql.append("select p from PublicWorksEO as p where p.siteId = :siteId");
        } else {
            hql.append("select p from PublicWorksEO as p,OrganEO o where p.leaders.organId = o.organId and p.siteId = :siteId");
        }
        Map<String, Object> paramMap = new HashMap<String, Object>();
        paramMap.put("siteId", queryVO.getSiteId());
        if (StringUtils.isNotEmpty(queryVO.getLeadersName())) {
            hql.append(" and p.leaders.leadersName like :leadersName escape '\\'");
            paramMap.put("leadersName", "%" + SqlUtil.prepareParam4Query(queryVO.getLeadersName()) + "%");
        }
        if (null != queryVO.getEnable()) {
            hql.append(" and p.enanle = :enanle");
            paramMap.put("enanle", queryVO.getEnable());
        }
        if (null != queryVO.getOrganId()) {// 单位id优先级最高
            hql.append(" and p.leaders.organId = :organId");
            paramMap.put("organId", queryVO.getOrganId());
            hql.append(" order by p.workDate desc,p.leaders.sortNum desc");
        } else {
            hql.append(" and o.type = :organType and o.recordStatus = :recordStatus");
            paramMap.put("organType", OrganEO.Type.Organ.toString());
            paramMap.put("recordStatus", AMockEntity.RecordStatus.Normal.toString());
            // 加上单位名称模糊查询
            if (StringUtils.isNotEmpty(queryVO.getOrganName())) {
                hql.append(" and o.name like :organName escape '\\'");
                paramMap.put("organName", "%" + SqlUtil.prepareParam4Query(queryVO.getOrganName()) + "%");
            }
            // 排序，要加上单位的排序值，单位的排序是从小到大
            hql.append(" order by p.workDate desc,o.sortNum,p.leaders.sortNum desc");
        }
        return getPagination(queryVO.getPageIndex(), queryVO.getPageSize(), hql.toString(), paramMap);
    }
}