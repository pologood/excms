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

import cn.lonsun.cache.client.CacheGroup;
import cn.lonsun.cache.client.CacheHandler;
import cn.lonsun.common.util.AppUtil;
import cn.lonsun.content.vo.ContentChartQueryVO;
import cn.lonsun.content.vo.SortUpdateVO;
import cn.lonsun.content.vo.SortVO;
import cn.lonsun.core.base.dao.impl.MockDao;
import cn.lonsun.core.base.entity.AMockEntity;
import cn.lonsun.core.util.Pagination;
import cn.lonsun.core.util.SqlUtil;
import cn.lonsun.publicInfo.internal.dao.IPublicContentDao;
import cn.lonsun.publicInfo.internal.entity.PublicCatalogOrganRelEO;
import cn.lonsun.publicInfo.internal.entity.PublicContentEO;
import cn.lonsun.publicInfo.vo.*;
import cn.lonsun.rbac.internal.entity.OrganEO;
import cn.lonsun.statistics.ContentChartVO;
import cn.lonsun.statistics.PublicListVO;
import cn.lonsun.statistics.StatisticsQueryVO;
import cn.lonsun.system.role.internal.util.RoleAuthUtil;
import cn.lonsun.util.DateUtil;
import cn.lonsun.util.LoginPersonUtil;
import cn.lonsun.util.ModelConfigUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.transform.Transformers;
import org.springframework.stereotype.Repository;

import java.text.ParseException;
import java.util.*;

/**
 * 信息公开内容dao <br/>
 *
 * @author fangtinghua <br/>
 * @version v1.0 <br/>
 * @date 2015年12月16日 <br/>
 */
@Repository("publicContentDao")
public class PublicContentDaoImpl extends MockDao<PublicContentEO> implements IPublicContentDao {

    /**
     * 根据目录获取查询Hql
     *
     * @param organId
     * @param catId
     * @param catIds
     * @param paramList
     * @param paramMap
     * @return
     */
    private String getQueryHql(Long organId, Long catId, Long[] catIds, List<Object> paramList, Map<String, Object> paramMap) {
        boolean paramIsList = null != paramList;
        StringBuffer sb = new StringBuffer();
        if (null != organId && null != catId) {
            String l = " p.organId = " + (paramIsList ? "? " : " :organId ");
            if (paramIsList) {
                paramList.add(organId);
            } else {
                paramMap.put("organId", organId);
            }
            String relHql = "";
            if (null != catIds && catIds.length > 0) {
                if (catIds.length == 1) {
                    l += " and p.catId = " + (paramIsList ? catIds[0] : " :catIds ");
                    if (!paramIsList) {
                        paramMap.put("catIds", catIds);
                    }
                } else {
                    //l += " and p.catId in ( " + (paramIsList ? StringUtils.join(catIds, ",") : " :catIds ") + " ) ";
                    l += " and (" +getOracleSQLIn(catIds,999,"p.catId")+" )";
                }
                relHql = getCatIdsQueryHql(organId, catIds);
            } else {
                l += " and p.catId = " + (paramIsList ? " ? " : " :catId ");
                if (paramIsList) {
                    paramList.add(catId);
                } else {
                    paramMap.put("catId", catId);
                }
                relHql = getCatIdQueryHql(organId, catId);
            }
            if (StringUtils.isNotEmpty(relHql)) {
                sb.append(" and (( ").append(l).append(") ").append(relHql).append(" ) ");
            } else {
                sb.append(" and ").append(l);
            }
        } else {
            if (null != organId) {
                sb.append(" and p.organId = ").append(paramIsList ? " ? " : " :organId ");
                if (paramIsList) {
                    paramList.add(organId);
                } else {
                    paramMap.put("organId", organId);
                }
            }
            if (null != catId) {
                if (null != catIds && catIds.length > 0) {
                    if (catIds.length == 1) {
                        sb.append(" and p.catId = " + (paramIsList ? catIds[0] : " :catIds "));
                        if (!paramIsList) {
                            paramMap.put("catIds", catIds);
                        }
                    } else {
                        //sb.append(" and p.catId in ( " + (paramIsList ? StringUtils.join(catIds, ",") : " :catIds ") + " ) ");
                        sb.append(" and (" +getOracleSQLIn(catIds,999,"p.catId")+" )");
                    }
                } else {
                    sb.append(" and p.catId = ").append(paramIsList ? " ? " : " :catId ");
                    if (paramIsList) {
                        paramList.add(catId);
                    } else {
                        paramMap.put("catId", catId);
                    }
                }
            }
        }
        return sb.toString();
    }


    private String getOracleSQLIn(Long[] ids, int count, String field) {
        List<Long> idList = Arrays.asList(ids);
        count = Math.min(count, 1000);
        int len = ids.length;
        int size = len % count;
        if (size == 0) {
            size = len / count;
        } else {
            size = (len / count) + 1;
        }
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < size; i++) {
            int fromIndex = i * count;
            int toIndex = Math.min(fromIndex + count, len);
            String id = StringUtils.defaultIfEmpty(StringUtils.join(idList.subList(fromIndex, toIndex), ","), "");
            if (i != 0) {
                builder.append(" or ");
            }
            builder.append(field).append(" in (").append(id).append(")");
        }
        return StringUtils.defaultIfEmpty(builder.toString(), field + " in ()");
    }

    private String getCatIdQueryHql(Long organId, Long catId) {
        if (null == organId || null == catId) {
            return "";
        }
        PublicCatalogOrganRelEO relEO = CacheHandler.getEntity(PublicCatalogOrganRelEO.class, CacheGroup.CMS_JOIN_ID, organId, catId);
        if (null == relEO || StringUtils.isEmpty(relEO.getRelCatIds())) {
            return "";
        }
        int index = 0;
        StringBuffer sb = new StringBuffer();
        String catIds = relEO.getRelCatIds();
        String[] organCatIds = catIds.split("\\|");
        for (String organCatId : organCatIds) {
//            if (index++ > 0) {
//                sb.append(" or ");
//            }
            String[] organIdCatId = organCatId.split("-");
            sb.append(" or (p.organId = ").append(organIdCatId[0]);
            String catIds_ = organIdCatId[1];
            if (catIds_.indexOf(",") > -1) {
                sb.append(" and p.catId in (").append(catIds_).append(")) ");
            } else {
                sb.append(" and p.catId = ").append(catIds_).append(") ");
            }
        }
        return sb.toString();
    }

    private String getCatIdsQueryHql(Long organId, Long[] catIds) {
        if (null == organId || null == catIds || catIds.length == 0) {
            return "";
        }
        StringBuffer sb = new StringBuffer();
        for (Long catId : catIds) {
            sb.append(getCatIdQueryHql(organId, catId));
        }
        return sb.toString();
    }

    /**
     * 获取信息公开查询hql
     *
     * @return
     * @author fangtinghua
     */
    private String getPublicQueryHql() {
        StringBuffer hql = new StringBuffer();
        hql.append("select b.title as title,b.createDate as createDate,b.publishDate as publishDate,b.siteId as siteId,b.isPublish as isPublish,p.catId as catId,b.author as author,b.isTop as isTop,");
        hql.append("p.sortNum as sortNum,p.parentClassIds as parentClassIds,p.classIds as classIds,b.isPublish as isPublish,p.effectiveDate as effectiveDate,p.repealDate as repealDate,b.redirectLink as redirectLink,");
        hql.append("p.contentId as contentId,p.synColumnIds as synColumnIds,p.synOrganCatIds as synOrganCatIds,p.synMsgCatIds as synMsgCatIds,p.organId as organId,b.resources as resources,p.isInvalid as isInvalid,p.invalidReason as invalidReason,");
        hql.append("p.classIds as classIds,p.indexNum as indexNum,p.fileNum as fileNum,p.keyWords as keyWords,p.summarize as summarize,p.id as id,p.type as type,p.filePath as filePath,p.relContentId as relContentId,");
        hql.append("b.attachSavedName as attachSavedName,b.attachRealName as attachRealName,b.attachSize as attachSize ");
        hql.append(" from PublicContentEO as p,BaseContentEO as b where p.contentId = b.id");
        return hql.toString();
    }

    @Override
    public void updateCatIdByContentIds(Long organId, Long catId, Long targetOrganId, Long targetId, Long[] contentIds) {
        if (null == contentIds || contentIds.length == 0) {
            String hql = "update PublicContentEO p set p.catId = ?,p.organId = ? where p.organId = ? and p.catId = ? and p.recordStatus = ?";
            super.executeUpdateByHql(hql, new Object[]{targetId, targetOrganId, organId, catId, AMockEntity.RecordStatus.Normal.toString()});
        } else {
            String hql = "update PublicContentEO p set p.catId = ?,p.organId = ? where p.id in (" + StringUtils.join(contentIds, ",") + ")";
            super.executeUpdateByHql(hql, new Object[]{targetId, targetOrganId});
        }
    }

    @Override
    public void exchangeColumnId(Long columnId, Long catId, Long targetId, Long[] contentIds, Integer isPublish) {
        if (null == contentIds || contentIds.length == 0) {
            String hql = "update BaseContentEO b set b.columnId = ?,b.isPublish = ? where b.recordStatus = ? and exists ";
            hql += " (select 1 from PublicContentEO p where b.id = p.contentId and p.organId = ? and p.catId = ?)";
            super.executeUpdateByHql(hql, new Object[]{targetId, isPublish, AMockEntity.RecordStatus.Normal.toString(), columnId, catId});
        } else {
            String hql = "update BaseContentEO b set b.columnId = ?,b.isPublish = ? where b.id in (" + StringUtils.join(contentIds, ",") + ")";
            super.executeUpdateByHql(hql, new Object[]{targetId, isPublish});
        }
    }

    @Override
    public void updateContentSort(Long organId, Long catId) {
        // 信息公开暂无排序
    }

    @Override
    public Pagination getPagination(PublicContentQueryVO queryVO) {
        StringBuffer hql = new StringBuffer();
        hql.append(this.getPublicQueryHql());
        List<Object> paramList = new ArrayList<Object>();
        if (null != LoginPersonUtil.getUserId()) {
            if (!RoleAuthUtil.isCurUserOrganPublicInfoAdmin(queryVO.getOrganId(),queryVO.getCatId()) && !LoginPersonUtil.isSiteAdmin() && !LoginPersonUtil.isSuperAdmin()) {
                if (null != LoginPersonUtil.getOrganId()) {
                    hql.append(" and p.createOrganId=" + LoginPersonUtil.getOrganId());
                }
            }
        }
        hql.append(" and p.siteId = ? and p.type = ? and p.recordStatus = ? and b.recordStatus = ?");
        paramList.add(queryVO.getSiteId());
        paramList.add(queryVO.getType());
        paramList.add(AMockEntity.RecordStatus.Normal.toString());
        paramList.add(AMockEntity.RecordStatus.Normal.toString());
        // 目录查询
        hql.append(getQueryHql(queryVO.getOrganId(), queryVO.getCatId(), queryVO.getCatIds(), paramList, null));

        // 主动公开需要序号
        if (null != queryVO.getClassId() && queryVO.getClassId() > 0L) {
            hql.append(" and p.classIds like ?");
            paramList.add("%" + SqlUtil.prepareParam4Query(String.valueOf(queryVO.getClassId())) + "%");
        }

        if (null != queryVO.getIsPublish()) {
            hql.append(" and b.isPublish = ?");
            paramList.add(queryVO.getIsPublish());
        }

        if (!StringUtils.isEmpty(queryVO.getTitle())) {// 标题
            hql.append(" and b.title like ? ");
            paramList.add("%" + SqlUtil.prepareParam4Query(queryVO.getTitle().trim()) + "%");
        }
        if (!StringUtils.isEmpty(queryVO.getFileNum())) {// 文号
            hql.append(" and p.fileNum like ? ");
            paramList.add("%" + SqlUtil.prepareParam4Query(queryVO.getFileNum().trim()) + "%");
        }
        if (!StringUtils.isEmpty(queryVO.getIndexNum())) {// 索引号
            hql.append(" and p.indexNum like ? ");
            paramList.add("%" + SqlUtil.prepareParam4Query(queryVO.getIndexNum().trim()) + "%");
        }
        if (null != queryVO.getStartDate()) {// 开始时间
            hql.append(" and b.publishDate >= ?");
            paramList.add(queryVO.getStartDate());
        }
        if (null != queryVO.getEndDate()) {// 结束时间
            hql.append(" and b.publishDate <= ?");
            paramList.add(queryVO.getEndDate());
        }
        // 标题、索引号、文号搜索
        String key = queryVO.getKey();
        if (!StringUtils.isEmpty(key)) {
            hql.append(" and (b.title like '%" + SqlUtil.prepareParam4Query(key.trim()) + "%'");
            hql.append(" or p.indexNum like '%" + SqlUtil.prepareParam4Query(key.trim()) + "%'");
            hql.append(" or p.fileNum like '%" + SqlUtil.prepareParam4Query(key.trim()) + "%')");
        }
        String relContentIds = queryVO.getRelContentId();
        if(!StringUtils.isEmpty(relContentIds)){
            String[] arr = queryVO.getRelContentId().split(",");
            if(arr.length==1){
                hql.append(" and p.contentId = "+relContentIds);
            }
            if(arr.length>1){
                hql.append("and (p.contentId=");
                for(int i = 0;i<arr.length;i++){
                    if(i==0){
                        hql.append(""+Long.valueOf(arr[i]));
                    }else {
                        hql.append(" or p.contentId= "+Long.valueOf(arr[i]));
                    }
                }
                hql.append(")");
            }
        }
        // 排序
        hql.append(ModelConfigUtil.getOrderByHqlForPublic(queryVO.getOrganId(), queryVO.getSiteId()));
        return getPagination(queryVO.getPageIndex(), queryVO.getPageSize(), hql.toString(), paramList.toArray(), PublicContentVO.class);
    }

    @Override
    public PublicContentVO getPublicContent(Long id) {
        StringBuffer hql = new StringBuffer();
        hql.append(this.getPublicQueryHql());
        hql.append(" and p.id = ?");
        return (PublicContentVO) getBean(hql.toString(), new Object[]{id}, PublicContentVO.class);
    }

    @Override
    public PublicContentVO getPublicContentByBaseContentId(Long id) {
        StringBuffer hql = new StringBuffer();
        hql.append(this.getPublicQueryHql());
        hql.append(" and b.id = ?");
        return (PublicContentVO) getBean(hql.toString(), new Object[]{id}, PublicContentVO.class);
    }

    @Override
    public PublicContentVO getPublicContent(PublicContentQueryVO queryVO) {
        StringBuffer hql = new StringBuffer();
        hql.append(this.getPublicQueryHql());
        List<Object> paramList = new ArrayList<Object>();
        hql.append(" and p.siteId = ? and p.recordStatus = ? and b.recordStatus = ?");
        paramList.add(queryVO.getSiteId());
        paramList.add(AMockEntity.RecordStatus.Normal.toString());
        paramList.add(AMockEntity.RecordStatus.Normal.toString());
        // 目录查询
        hql.append(getQueryHql(queryVO.getOrganId(), queryVO.getCatId(), queryVO.getCatIds(), paramList, null));
        if (StringUtils.isNotEmpty(queryVO.getType())) {
            hql.append(" and p.type = ?");
            paramList.add(queryVO.getType());
        }
        return (PublicContentVO) this.getBean(hql.toString(), paramList.toArray(), PublicContentVO.class);
    }

    @Override
    public SortVO getMaxNum(PublicContentVO vo) {
        StringBuffer hql = new StringBuffer();
        hql.append("select nvl(max(sortNum),0)+1 as sortNum from PublicContentEO p ");
        // hql.append("where p.siteId = ? and p.organId = ? and p.type = ? and p.recordStatus = ? ");
        hql.append("where p.recordStatus = ? ");
        // 主动公开需要序号
        // if (null != vo.getCatId() && vo.getCatId() > 0L) {
        // hql.append(" and p.catId = " + vo.getCatId());
        // }
        // return (SortVO) getBean(hql.toString(), new Object[] {
        // vo.getSiteId(), vo.getOrganId(), vo.getType(),
        // AMockEntity.RecordStatus.Normal.toString() },
        // SortVO.class);
        return (SortVO) getBean(hql.toString(), new Object[]{AMockEntity.RecordStatus.Normal.toString()}, SortVO.class);
    }

    @Override
    public Pagination getAllListForPage(Long pageIndex, Integer pageSize) {
        StringBuffer hql = new StringBuffer();
        hql.append(this.getPublicQueryHql());
        hql.append(" and p.type = '").append(PublicContentEO.Type.DRIVING_PUBLIC.toString());
        hql.append("' and p.recordStatus = ? and b.recordStatus = ?  and b.isPublish = 1");
        // 放入参数对象
        Object[] values = new Object[]{AMockEntity.RecordStatus.Normal.toString(), AMockEntity.RecordStatus.Normal.toString()};
        return getPagination(pageIndex, pageSize, hql.toString(), values, PublicContentVO.class);
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<PublicContentVO> getList(PublicContentQueryVO queryVO, Integer num) {
        StringBuffer hql = new StringBuffer();
        Map<String, Object> paramMap = new HashMap<String, Object>();
        hql.append(this.getPublicQueryHql());
        hql.append(" and p.siteId = :siteId and p.type = :type");
        hql.append(" and p.recordStatus = :recordStatus and b.recordStatus = :recordStatus and b.isPublish = 1");
        paramMap.put("siteId", queryVO.getSiteId());
        paramMap.put("type", queryVO.getType());
        paramMap.put("recordStatus", AMockEntity.RecordStatus.Normal.toString());
        // 目录查询
        hql.append(getQueryHql(queryVO.getOrganId(), queryVO.getCatId(), queryVO.getCatIds(), null, paramMap));
        // 主动公开需要序号
        if (null != queryVO.getClassId() && queryVO.getClassId() > 0L) {
            hql.append(" and p.classIds = :classIds");
            paramMap.put("classIds", String.valueOf(queryVO.getClassId()));
        }
        hql.append(ModelConfigUtil.getOrderByHqlForPublic(queryVO.getOrganId(), queryVO.getSiteId()));
        return (List<PublicContentVO>) getBeansByHql(hql.toString(), paramMap, PublicContentVO.class, num);
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<ContentChartVO> getChartList(ContentChartQueryVO queryVO, String type) {
        StringBuffer hql = new StringBuffer();
        hql.append("select b.createOrganId as organId ,count(b.id) as count from BaseContentEO b,PublicContentEO p ").append(
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
        if (!StringUtils.isEmpty(type)) {
            hql.append(" and p.type=:type");
            map.put("type", type);
        }
        hql.append(" group by b.createOrganId");
        hql.append(" order by count(b.id) desc");
        return (List<ContentChartVO>) getBeansByHql(hql.toString(), map, ContentChartVO.class, queryVO.getNum());
    }

    @Override
    public Long getTypeCount(StatisticsQueryVO queryVO, String type) {
        StringBuffer hql = new StringBuffer();
        hql.append("select b.createOrganId as organId ,count(b.id) as count from BaseContentEO b,PublicContentEO p ").append(
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
        if (!StringUtils.isEmpty(type)) {
            hql.append(" and p.type=:type");
            map.put("type", type);
        }
        if (queryVO.getOrganId() != null) {
            hql.append(" and b.createOrganId=:organId");
            map.put("organId", queryVO.getOrganId());
        }
        hql.append(" group by b.createOrganId");
        hql.append(" order by count(b.id) desc");
        List<?> list = getBeansByHql(hql.toString(), map, PublicListVO.class, null);
        return null == list || list.isEmpty() ? 0L : ((PublicListVO) list.get(0)).getCount();
    }

    public Pagination getRetrievalPagination(PublicContentRetrievalVO queryVO) {
        StringBuffer sql = new StringBuffer();
        sql.append("select p.id as id,b.title as title,b.sub_title as subTitle,b.publish_date as publishDate,p.sort_Num as sortNum,");
        sql.append("p.file_num as fileNum,b.redirect_link as redirectLink");
        sql.append(" from Cms_Public_Content p,CMS_BASE_CONTENT b where p.content_id = b.id");
        sql.append(" and b.record_status='Normal' and b.is_publish=1");
        if (null != queryVO.getSiteId()) {
            sql.append(" and b.site_id = " + queryVO.getSiteId());
        }
        if (!StringUtils.isEmpty(queryVO.getClassIds())) {
            sql.append(" and (p.class_ids like '%" + SqlUtil.prepareParam4Query(queryVO.getClassIds()) + "%'");
            sql.append(" or p.parent_class_ids like '%" + SqlUtil.prepareParam4Query(queryVO.getClassIds()) + "%')");
        }
        if (!StringUtils.isEmpty(queryVO.getFileNum())) {
            sql.append(" and p.file_num like '%" + SqlUtil.prepareParam4Query(queryVO.getFileNum()) + "%'");
        }
        // 目录查询
        List<Object> paramList = new ArrayList<Object>();
        if (null != queryVO.getOrganId()) {// 单位id不为空
            String sql_ = getQueryHql(queryVO.getOrganId(), queryVO.getCatId(), queryVO.getCatIds(), paramList, null);
            if (StringUtils.isNotEmpty(sql_)) {
                sql_ = sql_.replace("organId", "organ_id").replace("catId", "cat_id");
                sql.append(sql_);
            }
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
            sql.append(" and b.publish_date>=to_date('" + startDate + "','yyyy-mm-dd:hh24:mi:ss')");
        }
        if (!AppUtil.isEmpty(queryVO.getEndDate())) {
            String endDate = queryVO.getEndDate() + " 23:59:59";
            sql.append(" and b.publish_date<=to_date('" + endDate + "','yyyy-mm-dd:hh24:mi:ss')");
        }
        // 排序
        String sort = ModelConfigUtil.getOrderByHqlForPublic(queryVO.getOrganId(), queryVO.getSiteId());
        sort = sort.replace("sortNum", "sort_Num").replace("isTop", "is_top").replace("publishDate", "publish_date");
        sql.append(sort);
        // 放入参数对象
        Object[] values = paramList.toArray();
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

    @Override
    public Pagination getPublicGuide(Long siteId, Long organId, Long pageIndex, Integer pageSize) {
        StringBuffer hql = new StringBuffer();
        hql.append("select b.title as title,b.publishDate as publishDate,b.siteId as siteId,b.isPublish as isPublish,p.catId as catId,");
        hql.append("p.sortNum as sortNum,p.classIds as classIds,b.isPublish as isPublish,");
        hql.append("p.contentId as contentId,p.synColumnIds as synColumnIds,p.synOrganCatIds as synOrganCatIds,p.synMsgCatIds as synMsgCatIds,p.organId as organId,");
        hql.append("p.classIds as classIds,p.indexNum as indexNum,p.fileNum as fileNum,p.keyWords as keyWords,p.summarize as summarize,p.id as id,p.type as type");
        hql.append(" from PublicContentEO as p,BaseContentEO as b where p.contentId = b.id");
        if (siteId != null) {
            hql.append(" and p.siteId =" + siteId);
        }
        if (organId != null) {
            hql.append(" and p.organId =" + organId);
        }
        hql.append(" and p.type = ? and p.recordStatus = ? and b.recordStatus = ?");
        hql.append(ModelConfigUtil.getOrderByHqlForPublic(organId, siteId));
        Object[] values =
                new Object[]{PublicContentEO.Type.PUBLIC_GUIDE.toString(), AMockEntity.RecordStatus.Normal.toString(),
                        AMockEntity.RecordStatus.Normal.toString()};
        return getPagination(pageIndex, pageSize, hql.toString(), values, PublicContentVO.class);
    }

    @Override
    public Pagination getPublicAnnualReport(Long siteId, Long organId, Long pageIndex, Integer pageSize) {
        StringBuffer hql = new StringBuffer();
        hql.append("select b.title as title,b.publishDate as publishDate,b.siteId as siteId,b.isPublish as isPublish,p.catId as catId,");
        hql.append("p.sortNum as sortNum,p.classIds as classIds,b.isPublish as isPublish,");
        hql.append("p.contentId as contentId,p.synColumnIds as synColumnIds,p.synOrganCatIds as synOrganCatIds,p.synMsgCatIds as synMsgCatIds,p.organId as organId,");
        hql.append("p.classIds as classIds,p.indexNum as indexNum,p.fileNum as fileNum,p.keyWords as keyWords,p.summarize as summarize,p.id as id,p.type as type");
        hql.append(" from PublicContentEO as p,BaseContentEO as b where p.contentId = b.id");
        if (siteId != null) {
            hql.append(" and p.siteId =" + siteId);
        }
        if (organId != null) {
            hql.append(" and p.organId =" + organId);
        }
        hql.append(" and p.type = ? and p.recordStatus = ? and b.recordStatus = ?");
        hql.append(ModelConfigUtil.getOrderByHqlForPublic(organId, siteId));
        Object[] values =
                new Object[]{PublicContentEO.Type.PUBLIC_ANNUAL_REPORT.toString(), AMockEntity.RecordStatus.Normal.toString(),
                        AMockEntity.RecordStatus.Normal.toString()};
        return getPagination(pageIndex, pageSize, hql.toString(), values, PublicContentVO.class);
    }

    @Override
    public PublicContentVO getPublicContentInfo(Long id) {
        StringBuffer hql = new StringBuffer();
        hql.append("select b.title as title,b.publishDate as publishDate,b.siteId as siteId,b.isPublish as isPublish,p.catId as catId,");
        hql.append("p.sortNum as sortNum,p.classIds as classIds,b.isPublish as isPublish,");
        hql.append("p.contentId as contentId,p.synColumnIds as synColumnIds,p.synOrganCatIds as synOrganCatIds,p.synMsgCatIds as synMsgCatIds,p.organId as organId,");
        hql.append("p.classIds as classIds,p.indexNum as indexNum,p.fileNum as fileNum,p.keyWords as keyWords,p.summarize as summarize,p.id as id,p.type as type");
        hql.append(" from PublicContentEO as p,BaseContentEO as b where p.contentId = b.id");
        if (id != null) {
            hql.append(" and p.id =" + id);
        }
        return (PublicContentVO) getBean(hql.toString(), new Object[]{}, PublicContentVO.class);
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<PublicContentVO> getCounts(PublicContentQueryVO queryVO, Integer limit) {
        StringBuffer hql = new StringBuffer();
        Map<String, Object> paramMap = new HashMap<String, Object>();
        if (queryVO.isOrgan()) {
            hql.append("select p.organId as organId ");
        } else {
            hql.append("select p.createOrganId as organId ");
        }
        hql.append(", o.name as organName ,count(p.organId) as counts from PublicContentEO as p,BaseContentEO as b,OrganEO as o where p.contentId = b.id ");
        if (queryVO.isOrgan()) {
            hql.append(" and p.organId = o.organId ");
        } else {
            hql.append(" and p.createOrganId = o.organId and p.createOrganId is not null ");
        }
        hql.append(" and p.siteId = :siteId and p.type = :type");
        hql.append(" and p.recordStatus = :recordStatus and b.recordStatus = :recordStatus and o.recordStatus = :recordStatus and b.isPublish = 1");
        paramMap.put("siteId", queryVO.getSiteId());
        paramMap.put("type", queryVO.getType());
        paramMap.put("recordStatus", AMockEntity.RecordStatus.Normal.toString());
        //查询目录
        hql.append(getQueryHql(queryVO.getOrganId(), queryVO.getCatId(), queryVO.getCatIds(), null, paramMap));
        if (null != queryVO.getStartDate()) {// 开始时间
            hql.append(" and b.publishDate >= :startDate");
            paramMap.put("startDate", queryVO.getStartDate());
        }
        if (null != queryVO.getEndDate()) {// 结束时间
            hql.append(" and b.publishDate <= :endDate");
            paramMap.put("endDate", queryVO.getEndDate());
        }
        if (queryVO.isOrgan()) {
            hql.append(" group by p.organId ");
        } else {
            hql.append(" group by p.createOrganId ");
        }
        hql.append(" ,o.name order by count(p.organId) desc ");
        return (List<PublicContentVO>) getBeansByHql(hql.toString(), paramMap, PublicContentVO.class, limit);
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<PublicTjVO> getPublicTjList(PublicContentQueryVO queryVO) {
        List<Object> paramList = new ArrayList<Object>();
        StringBuffer hql = new StringBuffer();
        hql.append(" select p.organId as organId,count(p.id) as total," +
                "sum(CASE WHEN b.isPublish = 1 THEN 1 else 0 END) as publishCount," +
                "sum(CASE WHEN b.isPublish = 0 THEN 1 else 0 END) as notPublishCount");
        hql.append(" from PublicContentEO p,BaseContentEO b ");
        hql.append(" where p.contentId = b.id and p.recordStatus = ? and b.recordStatus = ? ");
        paramList.add(AMockEntity.RecordStatus.Normal.toString());
        paramList.add(AMockEntity.RecordStatus.Normal.toString());
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
        hql.append(" group by p.organId order by total desc");
        return (List<PublicTjVO>) getBeansByHql(hql.toString(), paramList.toArray(), PublicTjVO.class);
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<PublicTjVO> getPublicTjByCatIdList(PublicContentQueryVO queryVO) {
        List<Object> paramList = new ArrayList<Object>();
        StringBuffer hql = new StringBuffer();
        hql.append(" select p.catId as catId,count(p.id) as total");
        hql.append(" from PublicContentEO p,BaseContentEO b");
        hql.append(" where p.contentId = b.id and p.recordStatus = ? and b.recordStatus = ? and b.isPublish = 1");
        paramList.add(AMockEntity.RecordStatus.Normal.toString());
        paramList.add(AMockEntity.RecordStatus.Normal.toString());
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
        //查询目录
        hql.append(getQueryHql(queryVO.getOrganId(), queryVO.getCatId(), queryVO.getCatIds(), paramList, null));
        hql.append(" group by p.catId order by total desc");
        return (List<PublicTjVO>) getBeansByHql(hql.toString(), paramList.toArray(), PublicTjVO.class);
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<PublicTjForDateVO> getPublicTjListByDate(PublicContentQueryVO queryVO) {
        StringBuffer sql = new StringBuffer();
        sql.append(" select o.organ_id organId, o.name_ organName, nvl(l.num, 0) monthCount, nvl(ll.num, 0) seasonCount, nvl(lll.num, 0) yearCount");
        sql.append(" from rbac_organ o left join (select b.column_id organ_id, count(1) num from cms_base_content b left join cms_public_content p");
//        sql.append(" on b.id = p.content_id  where b.is_publish = :isPublish and to_char(b.publish_date, 'MM') = to_char(sysdate, 'MM')");
        if (null != queryVO.getStartDate() && null != queryVO.getEndDate()) {//开始结束时间都不为空时，本月结果按时间条件查询
            sql.append(" on b.id = p.content_id  where b.is_publish = :isPublish");
        } else {
            sql.append(" on b.id = p.content_id  where b.is_publish = :isPublish and to_char(b.publish_date, 'MM') = :month");
        }
        sql.append(" and b.record_status = :recordStatus and p.type = :publicType and p.record_status = :recordStatus and b.publish_date >= :startDate");
        sql.append(" and b.publish_date <= :endDate group by b.column_id) l on o.organ_id = l.organ_id");
        sql.append(" left join (select b.column_id organ_id, count(1) num from cms_base_content b left join cms_public_content p");
//        sql.append(" on b.id = p.content_id  where b.is_publish = :isPublish and to_char(b.publish_date, 'q') = to_char(sysdate, 'q')");
        sql.append(" on b.id = p.content_id  where b.is_publish = :isPublish and to_char(b.publish_date, 'q') = :season ");
        sql.append(" and b.record_status = :recordStatus and p.type = :publicType and p.record_status = :recordStatus and b.publish_date >= :startDate");
        sql.append(" and b.publish_date <= :endDate group by b.column_id) ll on o.organ_id = ll.organ_id");
        sql.append(" left join (select b.column_id organ_id, count(1) num from cms_base_content b left join cms_public_content p");
//        sql.append(" on b.id = p.content_id  where b.is_publish = :isPublish and to_char(b.publish_date, 'yyyy') = to_char(sysdate, 'yyyy')");
        sql.append(" on b.id = p.content_id  where b.is_publish = :isPublish and to_char(b.publish_date, 'yyyy') = :year ");
        sql.append(" and b.record_status = :recordStatus and p.type = :publicType and p.record_status = :recordStatus and b.publish_date >= :startDate");
        sql.append(" and b.publish_date <= :endDate group by b.column_id) lll on o.organ_id = lll.organ_id");
        sql.append(" where o.type_ = :organType and o.is_Public = :isPublic and o.record_status = :recordStatus order by yearCount desc");
        // 参数
        Map<String, Object> paramsMap = new HashMap<String, Object>();
        paramsMap.put("isPublish", 1);// 已发布
        paramsMap.put("recordStatus", AMockEntity.RecordStatus.Normal.toString());
        paramsMap.put("publicType", PublicContentEO.Type.DRIVING_PUBLIC.toString());
        Date now = new Date();

        //获取月份，季度，年份
        if (null == queryVO.getStartDate() && null == queryVO.getEndDate()) {
            //没有穿时间时默认查当前时间的月，季度，年
            Date date = new Date();
            String month = DateUtil.getMonth(date).toString();
            paramsMap.put("month", month.length() == 2 ? month : "0" + month);
            paramsMap.put("season", DateUtil.getSeason(date));
            paramsMap.put("year", DateUtil.getYear(date));
        } else if (null == queryVO.getStartDate() && null != queryVO.getEndDate()) {
            String month = DateUtil.getMonth(queryVO.getEndDate()).toString();
            paramsMap.put("month", month.length() == 2 ? month : "0" + month);
            paramsMap.put("season", DateUtil.getSeason(queryVO.getEndDate()));
            paramsMap.put("year", DateUtil.getYear(queryVO.getEndDate()));
        } else if (null != queryVO.getStartDate() && null == queryVO.getEndDate()) {
            String month = DateUtil.getMonth(queryVO.getStartDate()).toString();
            paramsMap.put("month", month.length() == 2 ? month : "0" + month);
            paramsMap.put("season", DateUtil.getSeason(queryVO.getStartDate()));
            paramsMap.put("year", DateUtil.getYear(queryVO.getStartDate()));
        } else {
            paramsMap.put("season", DateUtil.getSeason(queryVO.getStartDate()));
            paramsMap.put("year", DateUtil.getYear(queryVO.getStartDate()));
        }


        if (null == queryVO.getStartDate()) {// 开始时间
            String year = DateFormatUtils.format(now, "yyyy");
            try {
                paramsMap.put("startDate", DateUtils.parseDate(year + "-01-01", "yyyy-MM-dd"));
            } catch (ParseException e) {
            }
        } else {
            paramsMap.put("startDate", queryVO.getStartDate());
        }
        if (null == queryVO.getEndDate()) {// 结束时间
            paramsMap.put("endDate", now);
        } else {
            paramsMap.put("endDate", queryVO.getEndDate());
        }
        paramsMap.put("organType", OrganEO.Type.Organ.toString());
        paramsMap.put("isPublic", 1);// 信息公开单位
        return (List<PublicTjForDateVO>) getBeansBySql(sql.toString(), paramsMap, PublicTjForDateVO.class, new String[]{"organId", "organName", "monthCount",
                "seasonCount", "yearCount"});
    }

    @Override
    public PublicContentEO getSort(SortUpdateVO sortVo) {
        List<Object> values = new ArrayList<Object>();
        String hql = "from PublicContentEO s where s.recordStatus= ? and s.siteId = ? and s.catId = ?";
        values.add(AMockEntity.RecordStatus.Normal.toString());
        values.add(sortVo.getSiteId());
        values.add(sortVo.getColumnId());
        if ("up".equals(sortVo.getOperate())) {
            hql +=
                    "  and s.sortNum = (select min(t.sortNum) from PublicContentEO t where t.recordStatus= ? and t.siteId = ? and t.organId=? and t.catId = ? and t.sortNum > ?)";
        } else {
            hql +=
                    "  and s.sortNum = (select max(t.sortNum) from PublicContentEO t where t.recordStatus= ? and t.siteId = ? and t.organId=? and t.catId = ? and t.sortNum < ?)";
        }
        values.add(AMockEntity.RecordStatus.Normal.toString());
        values.add(sortVo.getSiteId());
        values.add(sortVo.getOrganId());
        values.add(sortVo.getColumnId());
        values.add(sortVo.getSortNum());
        return getEntityByHql(hql, values.toArray());
    }

    @Override
    public List<Long> getCatalogIdByOrganId(Long organId) {
        String hql = " SELECT distinct p.catId from PublicContentEO p where p.organId = ? and p.recordStatus = ? ";
        return (List<Long>) this.getObjects(hql, new Object[]{organId, AMockEntity.RecordStatus.Normal.toString()});
    }

    @Override
    public String getMaxIndexNumByOrganId(Long organId, String codeAndMonth) {
        List<Object> paramList = new ArrayList<Object>();
        String hql = "SELECT P.indexNum as indexNum FROM PublicContentEO P WHERE P.id = (";
        hql += "SELECT MAX(T.id) FROM PublicContentEO T WHERE T.organId = ? AND T.indexNum LIKE ? )";
        paramList.add(organId);
        paramList.add(SqlUtil.prepareParam4Query(codeAndMonth) + "%");
        return (String) getObject(hql, paramList.toArray());
    }

    @Override
    public List<Map<String, Object>> getPublicContentStatisByCatalogAndOrganId(PublicContentQueryVO queryVO) {
        List<Object> params = new ArrayList<Object>();
        Map<String,Object> map = new HashMap<String, Object>();
        StringBuffer sql = new StringBuffer();
        sql.append("select c.id as catId,c.is_parent as isParent,c.parent_id as parentId,c.NAME as catalogName,c.SORT_NUM as sortNum,a.* " +
                " FROM cms_public_catalog c LEFT JOIN ( select t.cat_id as cat_id,sum(1) as totalCount");
        if(null != queryVO.getOrganIds() && queryVO.getOrganIds().size()>0){
            for(int i=0;i<queryVO.getOrganIds().size();i++){
                Long organId = queryVO.getOrganIds().get(i);
                sql.append(" , sum( CASE WHEN (t.organ_id = :organId"+i+") THEN 1 ELSE 0 END) AS c_"+organId);
                map.put("organId"+i,organId);
            }
        }

        sql.append(" FROM" +
                "   CMS_PUBLIC_CONTENT t," +
                "   cms_base_content b" +
                "  WHERE" +
                "   t.content_id = b.id" +
                "  AND t.record_status = :recordStatus" +
                "  AND b.record_status = :recordStatus" +
                "  AND t.site_id = :siteId" +
                "  AND t.type = 'DRIVING_PUBLIC'" );

        map.put("recordStatus",AMockEntity.RecordStatus.Normal.toString());
        map.put("siteId",queryVO.getSiteId());

        if (null != queryVO.getStartDate()) {
            sql.append(" and t.CREATE_DATE >= :startDate");
            map.put("startDate",queryVO.getStartDate());
        }
        if (null != queryVO.getEndDate()) {
            sql.append(" and t.CREATE_DATE <= :endDate");
            map.put("endDate",queryVO.getEndDate());
        }
        sql.append("  GROUP BY t.cat_id ) a ON c.id = a.cat_Id where c.record_status = :recordStatus and c.IS_SHOW = 1 and c.type=1 ");
        return getMapBySql(sql.toString(),map);
    }

    public List<Map<String,Object>> getMapBySql(String sql, Map<String,Object> values) {
        if(sql != null && !"".equals(sql)) {
            if(!sql.startsWith("select")) {
                sql = "select * ".concat(sql);
            }

            SQLQuery q = this.getCurrentSession().createSQLQuery(sql);
            this.setParameters(values, q);
            q.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP);
            return q.list();
        } else {
            return null;
        }
    }

    private void setParameters(Map<String, Object> params, Query q) {
        if(null != params && params.size() > 0) {
            Set keys = params.keySet();
            Iterator i$ = keys.iterator();

            while(i$.hasNext()) {
                String key = (String)i$.next();
                Object value = params.get(key);
                if(value instanceof Object[]) {
                    q.setParameterList(key, (Object[])((Object[])value));
                } else if(value instanceof Collection) {
                    q.setParameterList(key, (Collection)value);
                } else if(value instanceof List) {
                    q.setParameterList(key, (List)value);
                } else if(value instanceof Date) {
                    q.setTimestamp(key, (Date)value);
                } else {
                    q.setParameter(key, value);
                }
            }
        }
    }
}