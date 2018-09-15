package cn.lonsun.content.internal.dao.impl;

import cn.lonsun.common.util.AppUtil;
import cn.lonsun.content.internal.dao.IGuestBookDao;
import cn.lonsun.content.internal.entity.BaseContentEO;
import cn.lonsun.content.vo.BaseContentVO;
import cn.lonsun.content.vo.ColumnTypeVO;
import cn.lonsun.content.vo.GuestBookCountVO;
import cn.lonsun.content.vo.UnAuditContentsVO;
import cn.lonsun.core.base.entity.AMockEntity;
import cn.lonsun.core.base.util.StringUtils;
import cn.lonsun.core.util.Pagination;
import cn.lonsun.statistics.StatisticsQueryVO;
import cn.lonsun.statistics.WordListVO;
import cn.lonsun.supervise.util.DateUtil;
import cn.lonsun.util.ColumnUtil;
import cn.lonsun.util.LoginPersonUtil;
import cn.lonsun.util.ModelConfigUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Hewbing
 * @ClassName: ContentDaoImpl
 * @Description: The news Data persistence layer
 * @date 2015年10月15日 上午11:22:37
 */
@Repository("baseContentMySqlDao")
public class BaseContentMySqlDaoImpl extends BaseContentDaoImpl {
    @Autowired
    private IGuestBookDao guestBookDao;

    @Override
    public Long noAuditCount(Long siteId, String typeCode, List<Long> columnIds) {
        StringBuffer hql = new StringBuffer("from BaseContentEO where recordStatus='Normal' and (workFlowStatus is null or workFlowStatus <> 0) ");
        List<Object> values = new ArrayList<Object>();
        if (!AppUtil.isEmpty(siteId)) {
            hql.append(" and siteId=?");
            values.add(siteId);
        }
        if (!AppUtil.isEmpty(typeCode)) {

            if (typeCode.equals("todayPublish")) {
                hql.append(" and isPublish=1 ");
                hql.append(" and (typeCode='articleNews' or typeCode='pictureNews' or typeCode='videoNews')");
                hql.append(" and publishDate >= str_to_date('" + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(DateUtil.getTodayStartDate())
                        + "','%Y-%m-%d %H:%i:%s')");
                hql.append(" and publishDate <= str_to_date('" + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(DateUtil.getTodayEndDate())
                        + "','%Y-%m-%d %H:%i:%s')");
            } else {
                hql.append(" and isPublish=0 ");
                hql.append(" and typeCode=?");
                values.add(typeCode);
            }
        }

        if (!AppUtil.isEmpty(columnIds)) {
            String columns = null;
            for (Long coulumnId : columnIds) {
                if (null == columns) {
                    columns = String.valueOf(coulumnId);
                } else {
                    columns += "," + String.valueOf(coulumnId);
                }
            }
            hql.append(" and columnId in (" + columns + ")");
        }

        if (typeCode.equals(BaseContentEO.TypeCode.guestBook.toString()) && !LoginPersonUtil.isRoot() && !LoginPersonUtil.isSuperAdmin()
                && !LoginPersonUtil.isSiteAdmin()) {
            StringBuffer stringBuffer = new StringBuffer();
            stringBuffer.append(" select b.createOrganId as createOrganId,g.receiveId as receiveId  from BaseContentEO b,GuestBookEO  g where  g.baseContentId =b.id  and   b.recordStatus='Normal' and (b.workFlowStatus is null or b.workFlowStatus <> 0)");
            List<Object> guestValues = new ArrayList<Object>();
            if (!AppUtil.isEmpty(siteId)) {
                stringBuffer.append(" and b.siteId=?");
                guestValues.add(siteId);
            }
            if (!AppUtil.isEmpty(typeCode)) {

                if (typeCode.equals("todayPublish")) {
                    stringBuffer.append(" and b.isPublish=1 ");
                    stringBuffer.append(" and (b.typeCode='articleNews' or b.typeCode='pictureNews' or b.typeCode='videoNews')");

                    stringBuffer.append(" and b.publishDate >= str_to_date('" + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(DateUtil.getTodayStartDate())
                            + "','%Y-%m-%d %H:%i:%s')");
                    stringBuffer.append(" and b.publishDate <= str_to_date('" + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(DateUtil.getTodayEndDate())
                            + "','%Y-%m-%d %H:%i:%s')");
                } else {
                    stringBuffer.append(" and b.isPublish=0 ");
                    stringBuffer.append(" and b.typeCode=?");
                    guestValues.add(typeCode);
                }
            }

            if (!AppUtil.isEmpty(columnIds)) {
                String columns = null;
                for (Long coulumnId : columnIds) {
                    if (null == columns) {
                        columns = String.valueOf(coulumnId);
                    } else {
                        columns += "," + String.valueOf(coulumnId);
                    }
                }
                stringBuffer.append(" and b.columnId in (" + columns + ")");
            }


            List<GuestBookCountVO> eos = (List<GuestBookCountVO>) getBeansByHql(stringBuffer.toString(), guestValues.toArray(), GuestBookCountVO.class);
            if (null != eos) {
                Long count = 0L;
                Long organId = LoginPersonUtil.getUnitId();
                for (GuestBookCountVO eo : eos) {
                    if (null != eo.getCreateOrganId() && eo.getCreateOrganId().intValue() == LoginPersonUtil.getOrganId()) {
                        count++;
                    } else if (null != eo.getReceiveId() && eo.getReceiveId().intValue() == organId.intValue()) {
                        count++;
                    }
                }

                return count;
            }

            return 0L;
        }

        if (!LoginPersonUtil.isRoot() && !LoginPersonUtil.isSuperAdmin() && !LoginPersonUtil.isSiteAdmin()) {
            hql.append(" and createOrganId = " + LoginPersonUtil.getOrganId());
        }

        return getCount(hql.toString(), values.toArray());
    }


    @Override
    public List<ColumnTypeVO> getUnAuditColumnIds(UnAuditContentsVO contentVO) {
        StringBuffer hql =
                new StringBuffer("select distinct columnId from BaseContentEO where (typeCode = 'guestBook' or "
                        + "typeCode = 'articleNews'or typeCode = 'pictureNews'or typeCode = 'videoNews') and recordStatus='Normal' ");
        if (null != contentVO.getSiteId()) {
            hql.append(" and siteId=" + contentVO.getSiteId());
        }
        if (null != contentVO.getColumnIds()) {
            String columnIds = null;
            for (Long l : contentVO.getColumnIds()) {
                if (columnIds == null) {
                    columnIds = String.valueOf(l);
                } else {
                    columnIds += "," + String.valueOf(l);
                }
            }
            hql.append(" and columnId in (" + columnIds + ")");
        }
        if (!AppUtil.isEmpty(contentVO.getTypeCode())) {

            if (contentVO.getTypeCode().equals("todayPublish")) {
                hql.append(" and isPublish=1 ");
                hql.append(" and (typeCode='articleNews' or typeCode='pictureNews' or typeCode='videoNews')");
                hql.append(" and publishDate >= str_to_date('" + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(DateUtil.getTodayStartDate())
                        + "','%Y-%m-%d %H:%i:%s')");
                hql.append(" and publishDate <= str_to_date('" + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(DateUtil.getTodayEndDate())
                        + "','%Y-%m-%d %H:%i:%s')");
            } else {
                hql.append(" and isPublish=0 ");
                hql.append(" and typeCode='" + contentVO.getTypeCode() + "'");
            }
        }

        if (!AppUtil.isEmpty(contentVO.getTitle())) {
            hql.append(" and title like " + "'%".concat(contentVO.getTitle()).concat("%") + "' ");
        }

        List<Long> list = (List<Long>) this.getObjects(hql.toString(), new Object[]{});
        List<ColumnTypeVO> vos = new ArrayList<ColumnTypeVO>();
        if (list != null) {
            for (Long l : list) {
                String columName = ColumnUtil.getColumnName(l, contentVO.getSiteId());
                if (AppUtil.isEmpty(columName)) {
                    continue;
                }
                ColumnTypeVO vo = new ColumnTypeVO();
                vo.setColumnId(l);
                vo.setSiteId(contentVO.getSiteId());
                vo.setColumnName(columName);
                vos.add(vo);
            }
        }
        return vos;
    }


    @Override
    public Pagination getUnAuditContents(UnAuditContentsVO contentVO) {

        Map<String, Object> map = new HashMap<String, Object>();
        StringBuffer hql =
                new StringBuffer(
                        "from BaseContentEO b where (b.typeCode = 'guestBook' or "
                                + "b.typeCode = 'articleNews' or b.typeCode = 'pictureNews' or b.typeCode = 'videoNews' or b.typeCode = 'onlineDeclaration') and b.recordStatus='Normal' and (b.workFlowStatus is null or b.workFlowStatus <> 0) ");

        if (contentVO.getTypeCode().equals(BaseContentEO.TypeCode.guestBook.toString())) {
            hql =
                    new StringBuffer(
                            "select b.id as id,b.title as title,b.columnId as columnId,b.siteId as siteId,b.typeCode as typeCode,b.createDate as createDate from BaseContentEO b,GuestBookEO g where g.baseContentId=b.id  and b.recordStatus='Normal' and b.isPublish=0 and (b.workFlowStatus is null or b.workFlowStatus <> 0) ");
            if (!LoginPersonUtil.isSiteAdmin() && !LoginPersonUtil.isSuperAdmin()) {
                hql.append(" and( g.receiveId=" + LoginPersonUtil.getUnitId() + "  or b.createOrganId=" + LoginPersonUtil.getOrganId() + ")");
            }
        }

        List<Object> values = new ArrayList<Object>();

        if (null != contentVO.getSiteId()) {
            hql.append(" and b.siteId=?");
            values.add(contentVO.getSiteId());
            // map.put("siteId", contentVO.getSiteId());
        }
        if (null != contentVO.getColumnIds()) {
            String columnIds = null;
            Long[] ids = contentVO.getColumnIds();
            for (Long l : ids) {
                if (null == columnIds) {
                    columnIds = l + "";
                } else {
                    columnIds += "," + l;
                }
            }
            hql.append(" and b.columnId in (" + columnIds + ")");
            // values.add(contentVO.getColumnIds());
            // map.put("columnIds", contentVO.getColumnIds());
        }

        if (null != contentVO.getColumnId()) {
            hql.append(" and b.columnId=?");
            values.add(contentVO.getColumnId());
            // map.put("columnId", contentVO.getColumnId());
        }

        if (!AppUtil.isEmpty(contentVO.getTypeCode())) {
            if (contentVO.getTypeCode().equals("todayPublish")) {
                hql.append(" and isPublish=1 ");
                hql.append(" and (typeCode='articleNews' or typeCode='pictureNews' or typeCode='videoNews')");
                hql.append(" and publishDate >= str_to_date('" + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(DateUtil.getTodayStartDate())
                        + "','%Y-%m-%d %H:%i:%s')");
                hql.append(" and publishDate <= str_to_date('" + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(DateUtil.getTodayEndDate())
                        + "','%Y-%m-%d %H:%i:%s')");
            } else {
                hql.append(" and b.isPublish=? ");
                hql.append(" and b.typeCode=?");
                values.add(contentVO.getIsPublish());
                values.add(contentVO.getTypeCode());

                if (!AppUtil.isEmpty(contentVO.getStartTime())) {
                    hql.append(" and publishDate >= str_to_date('" + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(contentVO.getStartTime())
                            + "','%Y-%m-%d %H:%i:%s')");
                }

                if (!AppUtil.isEmpty(contentVO.getEndTime())) {
                    hql.append(" and publishDate <= str_to_date('" + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(contentVO.getEndTime())
                            + "','%Y-%m-%d %H:%i:%s')");
                }
            }
            // map.put("typeCode", contentVO.getTypeCode());
        }
        if (!AppUtil.isEmpty(contentVO.getTitle())) {
            hql.append(" and title like " + "'%".concat(contentVO.getTitle()).concat("%'"));
        }

        hql.append(" order by b.createDate desc");

        if (contentVO.getTypeCode().equals(BaseContentEO.TypeCode.guestBook.toString())) {
            return getPagination(contentVO.getPageIndex(), contentVO.getPageSize(), hql.toString(), values.toArray(), BaseContentEO.class);
        }
        return getPagination(contentVO.getPageIndex(), contentVO.getPageSize(), hql.toString(), values.toArray());
    }

    @Override
    public List<WordListVO> getWordList(StatisticsQueryVO vo) {
        StringBuffer sql = new StringBuffer();
        if (!AppUtil.isEmpty(vo.getIsOrgan()) && vo.getIsOrgan().equals("1")) {//根据部门统计
            sql.append("select u.c count,v.c publishCount,(u.c-v.c) unPublishCount,(v.c/u.c)*100 rate,u.organId as organId from(").append(
                    " select count(*) c,c.create_organ_id organId from cms_base_content c,rbac_indicator r ").append(
                    " where c.column_id=r.indicator_id and c.site_id=" + vo.getSiteId());
        } else if (!AppUtil.isEmpty(vo.getIsUser()) && vo.getIsUser().equals("1")) {//根据人员统计
            sql.append("select u.c count,v.c publishCount,(u.c-v.c) unPublishCount,(v.c/u.c)*100 rate,u.userId as organId from(").append(
                    " select count(*) c,c.create_user_id userId from cms_base_content c,rbac_indicator r ").append(
                    " where c.column_id=r.indicator_id and c.site_id=" + vo.getSiteId());
        } else if (!AppUtil.isEmpty(vo.getIsColumn()) && vo.getIsColumn().equals("1")) {//根据栏目统计
            sql.append("select u.c count,v.c publishCount,(u.c-v.c) unPublishCount,(v.c/u.c)*100 rate,u.columnId as organId from(").append(
                    " select count(*) c,c.column_id columnId from cms_base_content c,rbac_indicator r ").append(
                    " where c.column_id=r.indicator_id and c.site_id=" + vo.getSiteId());
        } else {
            sql.append("select u.c count,v.c publishCount,(u.c-v.c) unPublishCount,(v.c/u.c)*100 rate,u.organId as organId from(").append(
                    " select count(*) c,c.unit_id organId from cms_base_content c,rbac_indicator r ").append(
                    " where c.column_id=r.indicator_id and c.site_id=" + vo.getSiteId());
        }
        if (BaseContentEO.TypeCode.workGuide.toString().equals(vo.getTypeCode())) {
            sql.append(" and c.type_code in ('workGuide','sceneService')");
        } else if (BaseContentEO.TypeCode.articleNews.toString().equals(vo.getTypeCode())) {
            sql.append(" and c.type_code in ('articleNews','pictureNews')");
        } else {
            sql.append(" and c.type_code='" + vo.getTypeCode() + "'");
        }
        if (!StringUtils.isEmpty(vo.getColumnName())) {
            sql.append(" and r.name like '%" + vo.getColumnName() + "%'");
        }
        if (!StringUtils.isEmpty(vo.getStartDate())) {
            sql.append("  and c.create_date>=str_to_date('" + vo.getStartDate() + "','%Y-%m-%d %H:%i:%s')");
        }
        if (!StringUtils.isEmpty(vo.getEndDate())) {
            sql.append("  and c.create_date<=str_to_date('" + vo.getEndDate() + "','%Y-%m-%d %H:%i:%s')");
        }

        if (!AppUtil.isEmpty(vo.getIsOrgan()) && vo.getIsOrgan().equals("1")) {//根据部门统计
            sql.append(" and c.record_status='Normal' group by c.create_organ_id) u left join (")
                    .append("select count(*) c,c.create_organ_id organId from cms_base_content c ,rbac_indicator r")
                    .append(" where c.column_id=r.indicator_id and c.is_publish=1 ").append(" and c.site_id=" + vo.getSiteId());
        } else if (!AppUtil.isEmpty(vo.getIsUser()) && vo.getIsUser().equals("1")) {//根据人员统计
            sql.append(" and not exists (select 1 from cms_content_refer_relation f where f.refer_id = c.id ) ");//过滤引用新闻
            sql.append(" and c.record_status='Normal' group by c.create_user_id) u left join (")
                    .append("select count(*) c,c.create_user_id userId from cms_base_content c ,rbac_indicator r")
                    .append(" where c.column_id=r.indicator_id and c.is_publish=1 ").append(" and c.site_id=" + vo.getSiteId());
        } else if (!AppUtil.isEmpty(vo.getIsColumn()) && vo.getIsColumn().equals("1")) {//根据栏目统计
            sql.append(" and c.record_status='Normal' group by c.column_id) u left join (")
                    .append("select count(*) c,c.column_id columnId from cms_base_content c ,rbac_indicator r")
                    .append(" where c.column_id=r.indicator_id and c.is_publish=1 ").append(" and c.site_id=" + vo.getSiteId());
        } else {
            sql.append(" and c.record_status='Normal' group by c.unit_id) u left join (")
                    .append("select count(*) c,c.unit_id organId from cms_base_content c ,rbac_indicator r")
                    .append(" where c.column_id=r.indicator_id and c.is_publish=1 ").append(" and c.site_id=" + vo.getSiteId());
        }
        if (BaseContentEO.TypeCode.workGuide.toString().equals(vo.getTypeCode())) {
            sql.append(" and c.type_code in ('workGuide','sceneService')");
        } else if (BaseContentEO.TypeCode.articleNews.toString().equals(vo.getTypeCode())) {
            sql.append(" and c.type_code in ('articleNews','pictureNews')");
        } else {
            sql.append(" and c.type_code='" + vo.getTypeCode() + "'");
        }

        if (!StringUtils.isEmpty(vo.getColumnName())) {
            sql.append(" and r.name like '%" + vo.getColumnName() + "%'");
        }
        if (!StringUtils.isEmpty(vo.getStartDate())) {
            sql.append("  and c.create_date>=str_to_date('" + vo.getStartDate() + "','%Y-%m-%d %H:%i:%s')");
            sql.append("  and c.publish_date>=str_to_date('" + vo.getStartDate() + "','%Y-%m-%d %H:%i:%s')");
        }
        if (!StringUtils.isEmpty(vo.getEndDate())) {
            sql.append("  and c.create_date<=str_to_date('" + vo.getEndDate() + "','%Y-%m-%d %H:%i:%s')");
            sql.append("  and c.publish_date<=str_to_date('" + vo.getEndDate() + "','%Y-%m-%d %H:%i:%s')");
        }

        if (!AppUtil.isEmpty(vo.getIsOrgan()) && vo.getIsOrgan().equals("1")) {//根据部门统计
            sql.append(" and c.record_status='Normal'  group by c.create_organ_id ) v ").append("on u.organId=v.organId");
        } else if (!AppUtil.isEmpty(vo.getIsUser()) && vo.getIsUser().equals("1")) {//根据人员统计
            sql.append(" and not exists (select 1 from cms_content_refer_relation f where f.refer_id = c.id ) ");//过滤引用新闻
            sql.append(" and c.record_status='Normal'  group by c.create_user_id ) v ").append("on u.userId=v.userId");
        } else if (!AppUtil.isEmpty(vo.getIsColumn()) && vo.getIsColumn().equals("1")) {//根据栏目统计
            sql.append(" and c.record_status='Normal'  group by c.column_id ) v ").append("on u.columnId=v.columnId");
        } else {
            sql.append(" and c.record_status='Normal'  group by c.unit_id ) v ").append("on u.organId=v.organId");
        }
        List<String> fields = new ArrayList<String>();
        fields.add("count");
        fields.add("publishCount");
        fields.add("unPublishCount");
        fields.add("rate");
        fields.add("organId");
        String[] arr = new String[fields.size()];
        return (List<WordListVO>) getBeansBySql(sql.toString(), new Object[]{}, WordListVO.class, fields.toArray(arr));
    }

    @Override
    public List<WordListVO> getWordList1(StatisticsQueryVO vo) {
        StringBuffer sql =
                new StringBuffer("select u.c count,v.c publishCount,(u.c-v.c) unPublishCount,（v.c/u.c）*100 rate,u.organId as organId from(").append(
                        " select count(*) c,c.create_organ_id organId from cms_base_content c").append(" where  c.site_id=" + vo.getSiteId());
        if (BaseContentEO.TypeCode.workGuide.toString().equals(vo.getTypeCode())) {
            sql.append(" and c.type_code in ('workGuide','sceneService')");
        } else {
            sql.append(" and c.type_code='" + vo.getTypeCode() + "'");
        }

        if (!StringUtils.isEmpty(vo.getStartDate())) {
            sql.append("  and c.create_date>=str_to_date('" + vo.getStartDate() + "','%Y-%m-%d %H:%i:%s')");
        }
        if (!StringUtils.isEmpty(vo.getEndDate())) {
            sql.append("  and c.create_date<=str_to_date('" + vo.getEndDate() + "','%Y-%m-%d %H:%i:%s')");
        }

        sql.append(" and c.record_status='Normal' group by c.create_organ_id) u left join (")
                .append("select count(*) c,c.create_organ_id organId from cms_base_content c ").append(" where c.is_publish=1 ")
                .append(" and c.site_id=" + vo.getSiteId());
        if (BaseContentEO.TypeCode.workGuide.toString().equals(vo.getTypeCode())) {
            sql.append(" and c.type_code in ('workGuide','sceneService')");
        } else {
            sql.append(" and c.type_code='" + vo.getTypeCode() + "'");
        }
        if (!StringUtils.isEmpty(vo.getStartDate())) {
            sql.append("  and c.create_date>=str_to_date('" + vo.getStartDate() + "','%Y-%m-%d %H:%i:%s')");
            sql.append("  and c.publish_date>=str_to_date('" + vo.getStartDate() + "','%Y-%m-%d %H:%i:%s')");
        }
        if (!StringUtils.isEmpty(vo.getEndDate())) {
            sql.append("  and c.create_date<=str_to_date('" + vo.getEndDate() + "','%Y-%m-%d %H:%i:%s')");
            sql.append("  and c.publish_date<=str_to_date('" + vo.getEndDate() + "','%Y-%m-%d %H:%i:%s')");
        }
        sql.append(" and c.record_status='Normal'  group by c.create_organ_id ) v ").append("on u.organId=v.organId");
        List<String> fields = new ArrayList<String>();
        fields.add("count");
        fields.add("publishCount");
        fields.add("unPublishCount");
        fields.add("rate");
        fields.add("organId");
        String[] arr = new String[fields.size()];
        return (List<WordListVO>) getBeansBySql(sql.toString(), new Object[]{}, WordListVO.class, fields.toArray(arr));
    }

    @Override
    public Pagination getWordPage(StatisticsQueryVO vo) {
        StringBuffer sql =
                new StringBuffer("select u.c count,v.c publishCount,(u.c-v.c) unPublishCount,(v.c/u.c)*100 rate,u.organId as organId from(").append(
                        " select count(*) c,c.create_organ_id organId from cms_base_content c ").append(" where c.site_id=" + vo.getSiteId());
        if (BaseContentEO.TypeCode.workGuide.toString().equals(vo.getTypeCode())) {
            sql.append(" and c.type_code in ('workGuide','sceneService')");
        } else {
            sql.append(" and c.type_code='" + vo.getTypeCode() + "'");
        }

        if (!StringUtils.isEmpty(vo.getStartDate())) {
            sql.append("  and c.create_date>=str_to_date('" + vo.getStartDate() + "','%Y-%m-%d %H:%i:%s')");
        }
        if (!StringUtils.isEmpty(vo.getEndDate())) {
            sql.append("  and c.create_date<=str_to_date('" + vo.getEndDate() + "','%Y-%m-%d %H:%i:%s')");
        }
        sql.append(" and c.record_status='Normal' group by c.create_organ_id) u,(")
                .append("select count(*) c,c.create_organ_id organId from cms_base_content c where and c.is_publish=1 ")
                .append(" and c.site_id=" + vo.getSiteId());
        if (BaseContentEO.TypeCode.workGuide.toString().equals(vo.getTypeCode())) {
            sql.append(" and c.type_code in ('workGuide','sceneService')");
        } else {
            sql.append(" and c.type_code='" + vo.getTypeCode() + "'");
        }

        if (!StringUtils.isEmpty(vo.getStartDate())) {
            sql.append("  and c.create_date>=str_to_date('" + vo.getStartDate() + "','%Y-%m-%d %H:%i:%s')");
            sql.append("  and c.publish_date>=str_to_date('" + vo.getStartDate() + "','%Y-%m-%d %H:%i:%s')");
        }
        if (!StringUtils.isEmpty(vo.getEndDate())) {
            sql.append("  and c.create_date<=str_to_date('" + vo.getEndDate() + "','%Y-%m-%d %H:%i:%s')");
            sql.append("  and c.publish_date<=str_to_date('" + vo.getEndDate() + "','%Y-%m-%d %H:%i:%s')");
        }
        sql.append(" and c.record_status='Normal'  group by c.create_organ_id ) v").append("where u.organId=v.organId");
        List<String> fields = new ArrayList<String>();
        fields.add("count");
        fields.add("publishCount");
        fields.add("unPublishCount");
        fields.add("rate");
        fields.add("organId");
        String[] arr = new String[fields.size()];
        return getPaginationBySql(vo.getPageIndex(), vo.getPageSize(), sql.toString(), new Object[]{}, WordListVO.class, fields.toArray(arr));
    }

    @Override
    public BaseContentVO getLastNextVO(Long columnId, Long siteId, String typeCode, Long contentId, boolean allOrPublish) {
        StringBuffer sql = new StringBuffer();
        String order = ModelConfigUtil.getOrderByHql(columnId, siteId, typeCode);
        String commonCondition = " c.column_id = ? and c.record_status = ? ";
        if (!allOrPublish) {// 查询已发布
            commonCondition += " and c.is_publish = 1 ";
        }
        // 替换hql语句 成sql 后期添加徐改动
        order = order.replace("id", "ID").replace("isNew", "IS_NEW").replace("num", "NUM").replace("isHot", "IS_HOT").replace("createDate", "CREATE_DATE")
                .replace("isTop", "IS_TOP").replace("topValidDate", "TOP_VALID_DATE").replace("updateDate", "UPDATE_DATE")
                .replace("isPublish", "IS_PUBLISH").replace("isTitle", "IS_TITLE").replace("publishDate", "PUBLISH_DATE").replace("hit", "HIT")
                .replace("quoteStatus", "QUOTE_STATUS").replace("isAllowComments", "IS_ALLOW_COMMENTS");
        sql.append("select a.row_no from (select @row_num \\:=@row_num + 1 AS row_no,c.* from cms_base_content c,(SELECT(@row_num \\:= 0)) r where ").
                append(commonCondition).append(order).append(" ) a where a.id = ? ");// 查询rownum，从1开始
        Number result = (Number) this.getCurrentSession().createSQLQuery(sql.toString()).setParameter(0, columnId).
                setParameter(1, AMockEntity.RecordStatus.Normal.toString()).setParameter(2, contentId).uniqueResult();
        int num = result.intValue();// 当前行的rownum
        StringBuffer resultSql = new StringBuffer();
        resultSql.append("select a.id,a.title,a.row_no as num from (select @row_num \\:=@row_num + 1 AS row_no,c.* from cms_base_content c,(SELECT(@row_num \\:= 0)) r where ")
                .append(commonCondition).append(order).append(" ) a where a.row_no = ? or a.row_no = ? ");
        List<BaseContentEO> resultList = (List<BaseContentEO>) getBeansBySql(resultSql.toString(), new Object[]{columnId,
                AMockEntity.RecordStatus.Normal.toString(), num - 1, num + 1}, BaseContentEO.class, new String[]{"id", "title", "num"});
        if (null == resultList || resultList.isEmpty()) {//前后都没有数据时
            BaseContentVO baseContentVO = new BaseContentVO();
            baseContentVO.setLastId(0L);
            baseContentVO.setNextId(0L);
            return baseContentVO;
        }
        if (resultList.size() == 1) { //  单条数据
            BaseContentEO eo = resultList.get(0);
            if (eo.getNum() > num) {//  下一条
                return setBaseContentVO(contentId, 0L, "", eo.getId(), eo.getTitle());
            }
            return setBaseContentVO(contentId, eo.getId(), eo.getTitle(), 0L, "");
        }
        BaseContentEO last = resultList.get(0);
        BaseContentEO next = resultList.get(1);
        return setBaseContentVO(contentId, last.getId(), last.getTitle(), next.getId(), next.getTitle());
    }

    /**
     * 设置值
     *
     * @return
     */
    private BaseContentVO setBaseContentVO(Long id, Long lastId, String lastTitle, Long nextId, String nextTitle) {
        BaseContentVO vo = new BaseContentVO();
        vo.setId(id);
        vo.setLastId(lastId);
        vo.setLastTitle(lastTitle);
        vo.setNextId(nextId);
        vo.setNextTitle(nextTitle);
        return vo;
    }
}