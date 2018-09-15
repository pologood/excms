/*
 * PublicApplyDaoImpl.java         2015年12月25日 <br/>
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Repository;

import cn.lonsun.common.util.AppUtil;
import cn.lonsun.content.vo.ContentChartQueryVO;
import cn.lonsun.core.base.dao.impl.MockDao;
import cn.lonsun.core.base.entity.AMockEntity;
import cn.lonsun.core.util.Pagination;
import cn.lonsun.core.util.SqlUtil;
import cn.lonsun.publicInfo.internal.dao.IPublicApplyDao;
import cn.lonsun.publicInfo.internal.entity.PublicApplyEO;
import cn.lonsun.publicInfo.vo.PublicApplyQueryVO;
import cn.lonsun.publicInfo.vo.PublicApplyVO;
import cn.lonsun.publicInfo.vo.PublicContentQueryVO;
import cn.lonsun.publicInfo.vo.PublicTjVO;
import cn.lonsun.statistics.ContentChartVO;
import cn.lonsun.statistics.PublicListVO;
import cn.lonsun.statistics.StatisticsQueryVO;

/**
 * TODO <br/>
 *
 * @author fangtinghua <br/>
 * @version v1.0 <br/>
 * @date 2015年12月25日 <br/>
 */
@Repository
public class PublicApplyDaoImpl extends MockDao<PublicApplyEO> implements IPublicApplyDao {

    /**
     * 获取hql
     *
     * @return
     * @author fangtinghua
     */
    private String getHql() {
        StringBuffer hql = new StringBuffer();
        hql.append("select b.title as title,b.publishDate as publishDate,b.siteId as siteId,b.isPublish as isPublish,b.remarks as remarks,p.code as code,p.relId as relId,p.id as id,");
        hql.append("p.content as content,p.contentId as contentId,p.use as use,p.receiveType as receiveType,p.provideType as provideType,p.organId as organId,p.type as type,");
        hql.append("p.replyContent as replyContent,p.replyStatus as replyStatus,p.replyDate as replyDate,p.applyDate as applyDate,p.isPublic as isPublic,p.queryCode as queryCode,p.queryPassword as queryPassword");
        hql.append(" from PublicApplyEO  p,BaseContentEO  b where p.contentId = b.id");
        return hql.toString();
    }

    @Override
    public Pagination getPagination(PublicApplyQueryVO queryVO) {
        StringBuffer hql = new StringBuffer();
        List<Object> paramList = new ArrayList<Object>();
        hql.append(getHql());
        hql.append(" and p.siteId = ? and p.recordStatus = ? and b.recordStatus = ?");
        paramList.add(queryVO.getSiteId());
        paramList.add(AMockEntity.RecordStatus.Normal.toString());
        paramList.add(AMockEntity.RecordStatus.Normal.toString());
        if (null != queryVO.getOrganId()) {
            hql.append(" and p.organId = ? ");
            paramList.add(queryVO.getOrganId());
        }

        if (null != queryVO.getIsPublish()) {
            hql.append(" and b.isPublish = ?");
            paramList.add(queryVO.getIsPublish());
        }
        // 标题
        String title = queryVO.getTitle();
        if (!StringUtils.isEmpty(title)) {
            hql.append(" and p.content like ? escape'\\'");
            paramList.add("%" + SqlUtil.prepareParam4Query(title.trim()) + "%");
        }
        // 类型
        String type = queryVO.getType();
        if (!StringUtils.isEmpty(type)) {
            hql.append(" and p.type = ?");
            paramList.add(type);
        }
        hql.append(" order by b.createDate desc");
        return getPagination(queryVO.getPageIndex(), queryVO.getPageSize(), hql.toString(), paramList.toArray(), PublicApplyVO.class);
    }

    @Override
    public PublicApplyVO getPublicApply(Long id) {
        StringBuffer hql = new StringBuffer();
        hql.append(getHql());
        hql.append(" and p.id = ? ");
        return (PublicApplyVO) getBean(hql.toString(), new Object[]{id}, PublicApplyVO.class);
    }

    @Override
    public PublicApplyVO getPublicApplyByQueryCode(PublicApplyQueryVO queryVO) {
        StringBuffer hql = new StringBuffer();
        List<Object> paramList = new ArrayList<Object>();
        hql.append(getHql());
        hql.append(" and p.siteId = ? and p.recordStatus = ? and b.recordStatus = ?");
        paramList.add(queryVO.getSiteId());
        paramList.add(AMockEntity.RecordStatus.Normal.toString());
        paramList.add(AMockEntity.RecordStatus.Normal.toString());

        if (null != queryVO.getOrganId()) {
            hql.append(" and p.organId = ? ");
            paramList.add(queryVO.getOrganId());
        }

        if (null != queryVO.getIsPublish()) {
            hql.append(" and b.isPublish = ?");
            paramList.add(queryVO.getIsPublish());
        }
        // 类型
        String type = queryVO.getType();
        if (!StringUtils.isEmpty(type)) {
            hql.append(" and p.type = ?");
            paramList.add(type);
        }
        hql.append(" and p.queryCode = ? and p.queryPassword = ?");
        paramList.add(queryVO.getQueryCode());
        paramList.add(queryVO.getQueryPassword());
        return (PublicApplyVO) getBean(hql.toString(), paramList.toArray(), PublicApplyVO.class);
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<PublicTjVO> getPublicTjList(PublicContentQueryVO queryVO) {
        List<Object> paramList = new ArrayList<Object>();
        StringBuffer hql = new StringBuffer();
        if (queryVO.isOrgan()) {
            hql.append("select p.organId as organId ");
        } else {
            hql.append("select p.createOrganId as organId ");
        }
        hql.append(" ,count(p.id) as applyCount,");
        hql.append(" sum(case when b.isPublish = 1 then 1 else 0 end) as publishCount,");
        hql.append(" sum(case when b.isPublish = 0 then 1 else 0 end) as notPublishCount,");
        hql.append(" sum(case when p.replyStatus is null or b.isPublish <> 1 then 0 else 1 end) as replyCount");
        hql.append(" from PublicApplyEO p,BaseContentEO b,OrganEO o ");
        hql.append(" where p.contentId = b.id ");
        if (queryVO.isOrgan()) {
            hql.append(" and p.organId = o.organId ");
        } else {
            hql.append(" and p.createOrganId = o.organId and p.createOrganId is not null ");
        }
        hql.append(" and p.recordStatus = ? and b.recordStatus = ? and o.recordStatus = ? ");
        paramList.add(AMockEntity.RecordStatus.Normal.toString());
        paramList.add(AMockEntity.RecordStatus.Normal.toString());
        paramList.add(AMockEntity.RecordStatus.Normal.toString());
        if (null != queryVO.getIsPublish()) {
            hql.append(" and b.isPublish = ? ");
            paramList.add(queryVO.getIsPublish());
        }
        if (null != queryVO.getOrganId()) {
            hql.append(" and p.organId = ?");
            paramList.add(queryVO.getOrganId());
        }
        if (null != queryVO.getSiteId()) {
            hql.append(" and p.siteId = ?");
            paramList.add(queryVO.getSiteId());
        }
        if (null != queryVO.getStartDate()) {
            hql.append(" and p.createDate >= ?");
            paramList.add(queryVO.getStartDate());
        }
        if (null != queryVO.getEndDate()) {
            hql.append(" and p.createDate <= ?");
            paramList.add(queryVO.getEndDate());
        }
        if (queryVO.isOrgan()) {
            hql.append(" group by p.organId ");
        } else {
            hql.append(" group by p.createOrganId ");
        }
        hql.append(" order by applyCount desc");
        return (List<PublicTjVO>) getBeansByHql(hql.toString(), paramList.toArray(), PublicTjVO.class);
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<PublicTjVO> getPublicTjByApplyStatus(PublicContentQueryVO queryVO) {
        List<Object> paramList = new ArrayList<Object>();
        StringBuffer hql = new StringBuffer();
        hql.append(" select p.organId as organId,p.replyStatus as replyStatus,count(p.id) as total");
        hql.append(" from PublicApplyEO p,BaseContentEO b,OrganEO o ");
        hql.append(" where p.contentId = b.id and p.organId = o.organId and p.recordStatus = ? ");
        hql.append(" and b.recordStatus = ? and o.recordStatus = ? and p.replyStatus is not null");
        paramList.add(AMockEntity.RecordStatus.Normal.toString());
        paramList.add(AMockEntity.RecordStatus.Normal.toString());
        paramList.add(AMockEntity.RecordStatus.Normal.toString());
        if (null != queryVO.getIsPublish()) {
            hql.append(" and b.isPublish = ? ");
            paramList.add(queryVO.getIsPublish());
        }
        if (StringUtils.isNotEmpty(queryVO.getReplyStatus())) {
            hql.append(" and p.replyStatus = ? ");
            paramList.add(queryVO.getReplyStatus());
        }
        if (null != queryVO.getOrganId()) {
            hql.append(" and p.organId = ?");
            paramList.add(queryVO.getOrganId());
        }
        if (null != queryVO.getSiteId()) {
            hql.append(" and p.siteId = ?");
            paramList.add(queryVO.getSiteId());
        }
        if (null != queryVO.getStartDate()) {
            hql.append(" and p.createDate >= ?");
            paramList.add(queryVO.getStartDate());
        }
        if (null != queryVO.getEndDate()) {
            hql.append(" and p.createDate <= ?");
            paramList.add(queryVO.getEndDate());
        }
        hql.append(" group by p.organId,p.replyStatus order by total desc");
        return (List<PublicTjVO>) getBeansByHql(hql.toString(), paramList.toArray(), PublicTjVO.class);
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<ContentChartVO> getChartList(ContentChartQueryVO queryVO) {
        StringBuffer hql = new StringBuffer();
        hql.append("select b.createOrganId as organId ,count(b.id) as count from PublicApplyEO p,BaseContentEO b ").append(
                "where  p.contentId = b.id and b.recordStatus='Normal' and b.isPublish=1 ");
        Map<String, Object> map = new HashMap<String, Object>();
        if (null != queryVO.getSiteId()) {
            hql.append(" and b.siteId=:siteId");
            map.put("siteId", queryVO.getSiteId());
        }
        if (!StringUtils.isEmpty(queryVO.getTypeCode())) {
            hql.append(" and b.typeCode=:typeCode");
            map.put("typeCode", queryVO.getTypeCode());
        }
        if (!AppUtil.isEmpty(queryVO.getStartDate()) && !AppUtil.isEmpty(queryVO.getEndDate())) {
            hql.append(" and b.publishDate>=:startDate and b.publishDate<=:endDate");
            map.put("startDate", queryVO.getStartDate());
            map.put("endDate", queryVO.getEndDate());
        }
        hql.append(" group by b.createOrganId");
        hql.append(" order by count(b.id) desc");
        return (List<ContentChartVO>) getBeansByHql(hql.toString(), map, ContentChartVO.class, queryVO.getNum());
    }

    @Override
    public Long getTypeCount(StatisticsQueryVO queryVO) {
        StringBuffer hql = new StringBuffer();
        hql.append("select b.createOrganId as organId ,count(b.id) as count from PublicApplyEO p,BaseContentEO b ").append(
                "where  p.contentId = b.id and b.recordStatus='Normal' and b.isPublish=1 ");
        Map<String, Object> map = new HashMap<String, Object>();
        if (null != queryVO.getSiteId()) {
            hql.append(" and b.siteId=:siteId");
            map.put("siteId", queryVO.getSiteId());
        }
        if (!StringUtils.isEmpty(queryVO.getTypeCode())) {
            hql.append(" and b.typeCode=:typeCode");
            map.put("typeCode", queryVO.getTypeCode());
        }
        if (!AppUtil.isEmpty(queryVO.getStartDate()) && !AppUtil.isEmpty(queryVO.getEndDate())) {
            hql.append(" and b.publishDate>=:startDate and b.publishDate<=:endDate");
            map.put("startDate", queryVO.getStartDate());
            map.put("endDate", queryVO.getEndDate());
        }
        if (queryVO.getOrganId() != null) {
            hql.append(" and b.createOrganId=:organId");
            map.put("organId", queryVO.getOrganId());
        }
        hql.append(" group by b.createOrganId");
        hql.append(" order by count(b.id) desc");
        List<?> list = getBeansByHql(hql.toString(), map, PublicListVO.class, null);
        return (null == list || list.isEmpty()) ? 0L : ((PublicListVO) list.get(0)).getCount();
    }

    @Override
    public PublicApplyVO getPublicApplyByCardNum(PublicApplyQueryVO queryVO) {
        StringBuffer hql = new StringBuffer();
        List<Object> paramList = new ArrayList<Object>();
        hql.append("select b.title as title,b.publishDate as publishDate,b.siteId as siteId,b.isPublish as isPublish,b.remarks as remarks,p.code as code,p.relId as relId,p.id as id,");
        hql.append("p.content as content,p.contentId as contentId,p.use as use,p.receiveType as receiveType,p.provideType as provideType,p.organId as organId,p.type as type,");
        hql.append("p.replyContent as replyContent,p.replyStatus as replyStatus,p.replyDate as replyDate,p.applyDate as applyDate,p.isPublic as isPublic,p.queryCode as queryCode,p.queryPassword as queryPassword");
        hql.append(" from PublicApplyEO  p,BaseContentEO  b,NationalEO n where p.contentId = b.id and p.relId = n.id");
        hql.append(" and p.siteId = ? and p.recordStatus = ? and b.recordStatus = ? ");
        paramList.add(queryVO.getSiteId());
        paramList.add(AMockEntity.RecordStatus.Normal.toString());
        paramList.add(AMockEntity.RecordStatus.Normal.toString());

        if (null != queryVO.getIsPublish()) {
            hql.append(" and b.isPublish = ?");
            paramList.add(queryVO.getIsPublish());
        }

        if (null != queryVO.getOrganId()) {
            hql.append(" and p.organId = ? ");
            paramList.add(queryVO.getOrganId());
        }
        // 类型
        String type = queryVO.getType();
        if (!StringUtils.isEmpty(type)) {
            hql.append(" and p.type = ?");
            paramList.add(type);
        }
        hql.append(" and n.cardNum = ? and p.queryPassword = ?");
        paramList.add(queryVO.getCardNum());
        paramList.add(queryVO.getQueryPassword());
        return (PublicApplyVO) getBean(hql.toString(), paramList.toArray(), PublicApplyVO.class);
    }
}