package cn.lonsun.content.internal.dao.impl;

import cn.lonsun.base.anno.DbInject;
import cn.lonsun.common.util.AppUtil;
import cn.lonsun.content.internal.dao.IBaseContentDao;
import cn.lonsun.content.internal.dao.IGuestBookDao;
import cn.lonsun.content.internal.entity.BaseContentEO;
import cn.lonsun.content.vo.*;
import cn.lonsun.core.base.dao.impl.MockDao;
import cn.lonsun.core.base.entity.AMockEntity;
import cn.lonsun.core.base.util.StringUtils;
import cn.lonsun.core.util.Pagination;
import cn.lonsun.rbac.internal.entity.OrganEO;
import cn.lonsun.statistics.ContentChartVO;
import cn.lonsun.statistics.StatisticsQueryVO;
import cn.lonsun.statistics.WordListVO;
import cn.lonsun.supervise.util.DateUtil;
import cn.lonsun.system.role.internal.util.RoleAuthUtil;
import cn.lonsun.util.ColumnUtil;
import cn.lonsun.util.LoginPersonUtil;
import cn.lonsun.util.ModelConfigUtil;
import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.transform.Transformers;
import org.springframework.stereotype.Repository;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @author Hewbing
 * @ClassName: ContentDaoImpl
 * @Description: The news Data persistence layer
 * @date 2015年10月15日 上午11:22:37
 */
@Repository("baseContentDao")
public class BaseContentDaoImpl extends MockDao<BaseContentEO> implements IBaseContentDao {

    @DbInject("guestBook")
    private IGuestBookDao guestBookDao;

    @Override
    public Pagination getPage(ContentPageVO pageVO) {
        StringBuffer hql = new StringBuffer("from BaseContentEO c where c.recordStatus='Normal'");

        if (null != LoginPersonUtil.getUserId()) {
            if (!RoleAuthUtil.isCurUserColumnAdmin(LoginPersonUtil.getOrganId(),LoginPersonUtil.getUserId()) && !LoginPersonUtil.isSiteAdmin() && !LoginPersonUtil.isSuperAdmin()) {
                if (null != LoginPersonUtil.getOrganId()) {
                    hql.append(" and createOrganId=" + LoginPersonUtil.getOrganId());
                }
            }
        }

        Map<String, Object> map = new HashMap<String, Object>();
        if(!AppUtil.isEmpty(pageVO.getIsPublish())){
            hql.append(" and c.isPublish=:isPublish");
            map.put("isPublish", pageVO.getIsPublish());
        }

        if (!AppUtil.isEmpty(pageVO.getSiteId())) {
            hql.append(" and c.siteId=:siteId");
            map.put("siteId", pageVO.getSiteId());
        }
        if (!AppUtil.isEmpty(pageVO.getColumnId())) {
            hql.append(" and c.columnId=:columnId");
            map.put("columnId", pageVO.getColumnId());
        }
        if (!AppUtil.isEmpty(pageVO.getColumnIds())) {
            hql.append(" and c.columnId in (").append(pageVO.getColumnIds()).append(")");
        }
        if (!AppUtil.isEmpty(pageVO.getTypeCode())) {
            hql.append(" and c.typeCode=:typeCode");
            map.put("typeCode", pageVO.getTypeCode());
        }
        if (pageVO.getIsMember()) {
            if (pageVO.getMember() == null) {
                hql.append(" and c.memberConStu > 0");
                if (!AppUtil.isEmpty(pageVO.getTitle())) {
                    hql.append(" and (c.title like :title or c.member like :member)");
                    map.put("title", "%".concat(pageVO.getTitle()).concat("%"));
                    map.put("member", "%".concat(pageVO.getTitle()).concat("%"));
                } else {
                    hql.append(" and c.member is not null and c.member !=''");
                }
            } else {
                hql.append(" and c.member =:member");
                map.put("member", pageVO.getMember());
                if (!AppUtil.isEmpty(pageVO.getTitle())) {
                    hql.append(" and c.title like :title ");
                    map.put("title", "%".concat(pageVO.getTitle()).concat("%"));
                }
            }
            if (!AppUtil.isEmpty(pageVO.getCondition()) && pageVO.getStatus() != null) {
                hql.append(" and c.memberConStu=:status");
                map.put("status", pageVO.getStatus());
            }
            // 发送开始
            if (!AppUtil.isEmpty(pageVO.getStartTime())) {
                Date start = pageVO.getStartTime();
                hql.append(" and c.memPutDate >= :startDate");
                map.put("startDate", start);
            }
            // 结束时间
            if (!AppUtil.isEmpty(pageVO.getEndTime())) {
                Date end = pageVO.getEndTime();
                Calendar date = Calendar.getInstance();
                date.setTime(end);
                date.set(Calendar.DATE, date.get(Calendar.DATE) + 1);// 结束日期增加一天
                end = date.getTime();
                hql.append(" and c.memPutDate <= :endDate");
                map.put("endDate", end);
            }
        } else {
            //关键字
            if (!AppUtil.isEmpty(pageVO.getTitle())) {
                hql.append(" and c.title like :title ");
                map.put("title", "%".concat(pageVO.getTitle()).concat("%"));
            }
            //来源
            if (!AppUtil.isEmpty(pageVO.getResources())) {
                hql.append(" and c.resources like :resources ");
                map.put("resources", "%".concat(pageVO.getResources()).concat("%"));
            }
            //作者
            if (!AppUtil.isEmpty(pageVO.getAuthor())) {
                hql.append(" and c.author like :author ");
                map.put("author", "%".concat(pageVO.getAuthor()).concat("%"));
            }

            if (!AppUtil.isEmpty(pageVO.getCondition()) && pageVO.getStatus() != null) {
                hql.append(" and c." + pageVO.getCondition().trim() + "=:status");
                map.put("status", pageVO.getStatus());
            }

            //多个筛选条件
            if (pageVO.getConditionMap() != null) {
                String key;
                for (Map.Entry<String,Integer> entry:pageVO.getConditionMap().entrySet()) {
                    key = entry.getKey().trim();
                    hql.append(" and c.").append(key).append("=:").append(key);
                    map.put(key, entry.getValue());
                }
            }
            // 发送开始
            if (!AppUtil.isEmpty(pageVO.getStartTime())) {
                Date start = pageVO.getStartTime();
                hql.append(" and c.publishDate >= :startDate");
                map.put("startDate", start);
            }
            // 结束时间
            if (!AppUtil.isEmpty(pageVO.getEndTime())) {
                Date end = pageVO.getEndTime();
                Calendar date = Calendar.getInstance();
                date.setTime(end);
                date.set(Calendar.DATE, date.get(Calendar.DATE) + 1);// 结束日期增加一天
                end = date.getTime();
                hql.append(" and c.publishDate <= :endDate");
                map.put("endDate", end);
            }
        }
        //////////////////////////////////start  信息公开中查询已关联文章
        String ids = pageVO.getIds();
        if (!StringUtils.isEmpty(ids)) {
            String[] arr = pageVO.getIds().split(",");
            if (arr.length == 1) {
                hql.append(" and c.id = " + ids);
            }
            if (arr.length > 1) {
                hql.append(" and (c.id=");
                for (int i = 0; i < arr.length; i++) {
                    if (i == 0) {
                        hql.append("" + Long.valueOf(arr[i]));
                    } else {
                        hql.append(" or c.id= " + Long.valueOf(arr[i]));
                    }
                }
                hql.append(")");
            }
        }
        ///////////////////////////////////end
        //如果是父节点点击事件，则columnId为空，这是拿不到排序，所以修改下逻辑
        if (!AppUtil.isEmpty(pageVO.getColumnIds())) {
            hql.append(" order by c.createDate desc,c.id desc");
        }else {
            hql.append(ModelConfigUtil.getOrderByHql(pageVO.getColumnId(), pageVO.getSiteId(), BaseContentEO.TypeCode.articleNews.toString()));
        }
        return getPagination(pageVO.getPageIndex(), pageVO.getPageSize(), hql.toString(), map);
    }

    @Override
    public List<BaseContentEO> getList(ContentPageVO pageVO) {
        StringBuffer hql = new StringBuffer("from BaseContentEO c where c.recordStatus='Normal'");
        try{
            if (null != LoginPersonUtil.getOrganId()) {
                if (!LoginPersonUtil.isSiteAdmin() && !LoginPersonUtil.isSuperAdmin()) {
                    hql.append(" and createOrganId=" + LoginPersonUtil.getOrganId());
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }

        Map<String, Object> map = new HashMap<String, Object>();
        if (!AppUtil.isEmpty(pageVO.getTitle())) {
            hql.append(" and c.title like :title ");
            map.put("title", "%".concat(pageVO.getTitle()).concat("%"));
        }
        if (!AppUtil.isEmpty(pageVO.getSiteId())) {
            hql.append(" and c.siteId=:siteId");
            map.put("siteId", pageVO.getSiteId());
        }
        if (!AppUtil.isEmpty(pageVO.getColumnId())) {
            hql.append(" and c.columnId=:columnId");
            map.put("columnId", pageVO.getColumnId());
        }
        if (!AppUtil.isEmpty(pageVO.getColumnIds())) {
            hql.append(" and c.columnId in (").append(pageVO.getColumnIds()).append(")");
        }
        if (!AppUtil.isEmpty(pageVO.getCondition()) && pageVO.getStatus() != null) {
            hql.append(" and c." + pageVO.getCondition().trim() + "=:status");
            map.put("status", pageVO.getStatus());
        }
        if (!AppUtil.isEmpty(pageVO.getTypeCode())) {
            hql.append(" and c.typeCode=:typeCode");
            map.put("typeCode", pageVO.getTypeCode());
        }
        // 发送开始
        if (!AppUtil.isEmpty(pageVO.getStartTime())) {
            Date start = pageVO.getStartTime();
            hql.append(" and c.publishDate >= :startDate");
            map.put("startDate", start);
        }
        // 结束时间
        if (!AppUtil.isEmpty(pageVO.getEndTime())) {
            Date end = pageVO.getEndTime();
            Calendar date = Calendar.getInstance();
            date.setTime(end);
            date.set(Calendar.DATE, date.get(Calendar.DATE) + 1);// 结束日期增加一天
            end = date.getTime();
            hql.append(" and c.publishDate <= :endDate");
            map.put("endDate", end);
        }
        if (pageVO.getIsPublish() != null) {
            hql.append(" and c.isPublish=:isPublish");
            map.put("isPublish", pageVO.getIsPublish());
        }
        hql.append(ModelConfigUtil.getOrderByHql(pageVO.getColumnId(), pageVO.getSiteId(), BaseContentEO.TypeCode.articleNews.toString()));
        if (pageVO.getNum() == null)
            return getEntitiesByHql(hql.toString(), map);
        else
            return getEntities(hql.toString(), map, pageVO.getNum());
    }

    @Override
    public void changePublish(ContentPageVO pageVO) {
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("isPublish", pageVO.getStatus());
        StringBuffer hql = new StringBuffer("update BaseContentEO c set c.isPublish=:isPublish where c.recordStatus='Normal'");
        if (!AppUtil.isEmpty(pageVO.getSiteId())) {
            hql.append(" and c.siteId=:siteId");
            map.put("siteId", pageVO.getSiteId());
        }
        if (!AppUtil.isEmpty(pageVO.getColumnId())) {
            hql.append(" and c.columnId=:columnId");
            map.put("columnId", pageVO.getColumnId());
        }
        if (!AppUtil.isEmpty(pageVO.getTypeCode())) {
            hql.append(" and c.typeCode=:typeCode");
            map.put("typeCode", pageVO.getTypeCode());
        }
        if (!AppUtil.isEmpty(pageVO.getIdArray()) && pageVO.getIdArray().length > 0) {
            hql.append(" and c.id in (:idArray)");
            map.put("idArray", pageVO.getIdArray());
        }
        executeUpdateByJpql(hql.toString(), map);
    }

    /*@Override
    public void publishByColumn(Long columnId, Integer status) {
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("isPublish", status);
        // map.put("publishDate", new Date());
        map.put("columnId", columnId);
        String hql = "update BaseContentEO set isPublish=:isPublish where columnId=:columnId";
        executeUpdateByJpql(hql, map);

    }

    @Override
    public void publishBySite(Long siteId, Integer status) {
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("isPublish", status);
        // map.put("publishDate", new Date());
        map.put("siteId", siteId);
        String hql = "update BaseContentEO set isPublish=:isPublish where siteId=:siteId";
        executeUpdateByJpql(hql, map);
    }*/

    @Override
    public Long getCountByColumnId(Long columnId) {
        Long siteId = LoginPersonUtil.getSiteId();
        String hql = "from BaseContentEO where recordStatus='Normal' and columnId=? and siteId=?";
        return getCount(hql, new Object[]{columnId, siteId});
    }

    @Override
    public Long getCountBySiteId(Long siteId) {
        String hql = "from BaseContentEO where recordStatus='Normal' and siteId=?";
        return getCount(hql, new Object[]{siteId});
    }

    @Override
    public void changeTopStatus(Long[] ids, Integer status) {
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("isTop", status);
        map.put("ids", ids);
        String hql = "update BaseContentEO set isTop=:isTop where id in (:ids)";
        executeUpdateByJpql(hql, map);
    }

    @Override
    public void changeHotStatus(Long[] ids, Integer status) {
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("isHot", status);
        map.put("ids", ids);
        String hql = "update BaseContentEO set isHot=:isHot where id in (:ids)";
        executeUpdateByJpql(hql, map);
    }

    @Override
    public int changeImg(Long id, String imgPath) {
        String hql = "update BaseContentEO set imageLink=? where id=?";
        int i = executeUpdateByHql(hql, new Object[]{imgPath, id});
        return i;
    }

    @Override
    public void changeTitltStatus(Long[] ids, Integer status) {
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("isTitle", status);
        map.put("ids", ids);
        String hql = "update BaseContentEO set isTitle=:isTitle where id in (:ids)";
        executeUpdateByJpql(hql, map);
    }

    @Override
    public void changeNewStatus(Long[] ids, Integer status) {
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("isNew", status);
        map.put("ids", ids);
        String hql = "update BaseContentEO set isNew=:isNew where id in (:ids)";
        executeUpdateByJpql(hql, map);
    }

    @Override
    public void setNum(Long id, Long num) {
        String hql = "update BaseContentEO set num=? where id=?";
        executeUpdateByHql(hql, new Object[]{num, id});
    }

    @Override
    public SortVO getMaxNumByColumn(Long columnId) {
        String hql = "select max(num) as sortNum from BaseContentEO where columnId=?";
        return (SortVO) getBean(hql, new Object[]{columnId}, SortVO.class);
    }

    @Override
    public SortVO getMaxNumBySite(Long siteId) {
        String hql = "select max(num) as sortNum from BaseContentEO where siteId=? and typeCode != ? ";
        return (SortVO) getBean(hql, new Object[]{siteId, BaseContentEO.TypeCode.public_content.toString()}, SortVO.class);
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<SortVO> getNextSort(String opr, Long sortNum, ContentPageVO pageVO) {
        StringBuffer hql = new StringBuffer("select id as id, num as sortNum from BaseContentEO where recordStatus='Normal'");
        Map<String, Object> map = new HashMap<String, Object>();
        if (!AppUtil.isEmpty(pageVO.getTitle())) {
            hql.append(" and title like :title ");
            map.put("title", "%".concat(pageVO.getTitle()).concat("%"));
        }
        if (!AppUtil.isEmpty(pageVO.getColumnId())) {
            hql.append(" and columnId=:columnId");
            map.put("columnId", pageVO.getColumnId());
        } else {
            hql.append(" and columnId is null");
        }
        if (!AppUtil.isEmpty(pageVO.getCondition()) && pageVO.getStatus() != null) {
            hql.append(" and " + pageVO.getCondition() + "=:status");
            map.put("status", pageVO.getStatus());
        }
        if ("up".equals(opr)) {
            hql.append(" and num>:sortNum");
            hql.append(" order by isTop desc,num asc,createDate desc,id desc ,isPublish desc,publishDate desc, isNew desc, createDate desc, topValidDate desc, updateDate desc");
            map.put("sortNum", sortNum);
        } else {
            hql.append(" and num<:sortNum");
            hql.append(" order by isTop desc,num desc,createDate desc,id desc ,isPublish desc,publishDate desc, isNew desc, createDate desc, topValidDate desc, updateDate desc");
            map.put("sortNum", sortNum);
        }
        return (List<SortVO>) getBeansByHql(hql.toString(), map, SortVO.class, 1);
    }

    @Override
    public void markQuote(Long id, Integer status) {
        String hql = "update BaseContentEO set quoteStatus=? where id=?";
        executeUpdateByHql(hql, new Object[]{status, id});
    }

    @Override
    public void setHit(Long id) {
        String hql = "update BaseContentEO set hit=hit+1 where id=?";
        executeUpdateByHql(hql, new Object[]{id});
    }

    @Override
    public List<BaseContentEO> getPageLink(Long columnId, Long sortNum, String opr) {
        Map<String, Object> map = new HashMap<String, Object>();
        StringBuffer hql = new StringBuffer("from BaseContentEO where recordStatus='Normal' and columnId=?");
        map.put("columnId", columnId);
        if ("Before".equals(opr)) {
            hql.append(" and num>?");
            hql.append(" order by isTop desc,num asc,createDate desc,id desc ,isPublish desc,publishDate desc, isNew desc, createDate desc, topValidDate desc, updateDate desc");
            map.put("sortNum", sortNum);
        } else {
            hql.append(" and num<?");
            hql.append(" order by isTop desc,num desc,createDate desc,id desc ,isPublish desc,publishDate desc, isNew desc, createDate desc, topValidDate desc, updateDate desc");
            map.put("sortNum", sortNum);
        }

        return (List<BaseContentEO>) getEntities(hql.toString(), new Object[]{columnId, sortNum}, 1);
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

        //排除的栏目id
        if (null != contentVO.getExceptColumnIds()) {
            String columnIds = null;
            Long[] ids = contentVO.getExceptColumnIds();
            for (Long l : ids) {
                if (null == columnIds) {
                    columnIds = l + "";
                } else {
                    columnIds += "," + l;
                }
            }
            hql.append(" and b.columnId not in (" + columnIds + ")");
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
                hql.append(" and publishDate >= to_date('" + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(DateUtil.getTodayStartDate())
                        + "','yyyy-MM-dd HH24:mi:ss')");
                hql.append(" and publishDate <= to_date('" + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(DateUtil.getTodayEndDate())
                        + "','yyyy-MM-dd HH24:mi:ss')");
            } else {
                hql.append(" and b.isPublish=? ");
                hql.append(" and b.typeCode=?");
                values.add(contentVO.getIsPublish());
                values.add(contentVO.getTypeCode());

                if (!AppUtil.isEmpty(contentVO.getStartTime())) {
                    hql.append(" and publishDate >= to_date('" + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(contentVO.getStartTime())
                            + "','yyyy-MM-dd HH24:mi:ss')");
                }

                if (!AppUtil.isEmpty(contentVO.getEndTime())) {
                    hql.append(" and publishDate <= to_date('" + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(contentVO.getEndTime())
                            + "','yyyy-MM-dd HH24:mi:ss')");
                }


            }
            // map.put("typeCode", contentVO.getTypeCode());
        }
        if (!AppUtil.isEmpty(contentVO.getTitle())) {
            hql.append(" and title like " + "'%".concat(contentVO.getTitle()).concat("%") + "' ");
        }

//        if (!LoginPersonUtil.isRoot() && !LoginPersonUtil.isSuperAdmin() && !LoginPersonUtil.isSiteAdmin()) {
//            hql.append(" and b.createOrganId = " + LoginPersonUtil.getOrganId());
//        }

        hql.append(" order by b.createDate desc");

        if (contentVO.getTypeCode().equals(BaseContentEO.TypeCode.guestBook.toString())) {
            return getPagination(contentVO.getPageIndex(), contentVO.getPageSize(), hql.toString(), values.toArray(), BaseContentEO.class);
        }

        return getPagination(contentVO.getPageIndex(), contentVO.getPageSize(), hql.toString(), values.toArray());
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
                hql.append(" and publishDate >= to_date('" + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(DateUtil.getTodayStartDate())
                        + "','yyyy-MM-dd HH24:mi:ss')");
                hql.append(" and publishDate <= to_date('" + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(DateUtil.getTodayEndDate())
                        + "','yyyy-MM-dd HH24:mi:ss')");
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
    public Pagination getPageBySortNum(QueryVO query) {
        List<Object> values = new ArrayList<Object>();
        StringBuffer hql =
                new StringBuffer(
                        "select id as id,title as title,columnId as columnId,siteId as siteId,typeCode as typeCode,num as num,imageLink as imageLink,isPublish as isPublish,"
                                + "publishDate as publishDate " + "from BaseContentEO where recordStatus= ? and columnId = ? and siteId = ?");
        values.add(AMockEntity.RecordStatus.Normal.toString());
        values.add(query.getColumnId());
        values.add(query.getSiteId());
        if (!StringUtils.isEmpty(query.getTypeCode())) {
            hql.append(" and typeCode = ?");
            values.add(query.getTypeCode());
        }
        if (!StringUtils.isEmpty(query.getSearchText())) {
            hql.append(" and title like ?");
            values.add("%".concat(query.getSearchText()).concat("%"));
        }
        if (query.getIsPublish() != null) {
            hql.append(" and isPublish = ?");
            values.add(query.getIsPublish());
        }
        hql.append(" order by num desc");
        return getPagination(query.getPageIndex(), query.getPageSize(), hql.toString(), values.toArray(), BaseContentVO.class);
    }

    @Override
    public BaseContentEO getSort(SortUpdateVO sortVo) {
        List<Object> values = new ArrayList<Object>();
        String hql = "from BaseContentEO s where s.recordStatus= ? and s.siteId = ? and s.columnId = ? and s.typeCode = ?";
        values.add(AMockEntity.RecordStatus.Normal.toString());
        values.add(sortVo.getSiteId());
        values.add(sortVo.getColumnId());
        values.add(sortVo.getTypeCode());
        if ("up".equals(sortVo.getOperate())) {
            hql +=
                    "  and s.num = (select min(t.num) from BaseContentEO t where t.recordStatus= ? and t.siteId = ? and t.columnId = ? and t.typeCode = ? and t.num > ?)";
        } else {
            hql +=
                    "  and s.num = (select max(t.num) from BaseContentEO t where t.recordStatus= ? and t.siteId = ? and t.columnId = ? and t.typeCode = ? and t.num < ?)";
        }
        values.add(AMockEntity.RecordStatus.Normal.toString());
        values.add(sortVo.getSiteId());
        values.add(sortVo.getColumnId());
        values.add(sortVo.getTypeCode());
        values.add(sortVo.getSortNum());
        return getEntityByHql(hql, values.toArray());
    }

    @Override
    public Long getMaxSortNum(Long siteId, Long columnId, String typeCode) {
        Long maxSortNum = null;
        StringBuffer sb =
                new StringBuffer("select max(o.num) from BaseContentEO as o where o.recordStatus= ? and o.siteId = ? and o.columnId = ? and o.typeCode = ?");
        Query query =
                getCurrentSession().createQuery(sb.toString()).setParameter(0, AMockEntity.RecordStatus.Normal.toString()).setParameter(1, siteId)
                        .setParameter(2, columnId).setParameter(3, typeCode);
        @SuppressWarnings("rawtypes")
        List list = query.list();
        if (list != null && list.size() > 0) {
            maxSortNum = Long.valueOf(list.get(0) == null ? "0" : list.get(0).toString());
        }
        return maxSortNum;
    }

    @Override
    public Pagination getOpenCommentContent(ContentPageVO pageVO) {
        StringBuffer hql =
                new StringBuffer("from BaseContentEO where recordStatus='Normal' and isPublish=1 and isAllowComments=1 and siteId=" + pageVO.getSiteId());
        Map<String, Object> map = new HashMap<String, Object>();
        if (!AppUtil.isEmpty(pageVO.getTitle())) {
            hql.append(" and title like :title ");
            map.put("title", "%".concat(pageVO.getTitle()).concat("%"));
        }
        if (!AppUtil.isEmpty(pageVO.getColumnId())) {
            hql.append(" and columnId=:columnId");
            map.put("columnId", pageVO.getColumnId());
        }
        hql.append(" order by isTop desc,num desc,createDate desc,id desc ,isPublish desc,publishDate desc, isNew desc, topValidDate desc, updateDate desc");
        return getPagination(pageVO.getPageIndex(), pageVO.getPageSize(), hql.toString(), map);
    }

    @Override
    public BaseContentEO getRemoved(Long id) {
        String hql = "from BaseContentEO where id=?";
        return getEntityByHql(hql, new Object[]{id});
    }

    @Override
    public Pagination getRecycleContentPage(UnAuditContentsVO contentVO) {

        Map<String, Object> map = new HashMap<String, Object>();
        StringBuffer hql =
                new StringBuffer("from BaseContentEO where (typeCode = 'guestBook' or typeCode = 'messageBoard' or "
                        + "typeCode = 'articleNews' or typeCode = 'knowledgeBase' or typeCode = 'pictureNews' or typeCode = 'videoNews' or typeCode = 'leaderInfo' " + ") and recordStatus='Removed'");
        if (null != contentVO.getSiteId()) {
            hql.append(" and siteId=:siteId");
            map.put("siteId", contentVO.getSiteId());
        }
        if (null != contentVO.getColumnIds()) {
            hql.append(" and columnId in (:columnIds)");
            map.put("columnIds", contentVO.getColumnIds());
        }
        if (!AppUtil.isEmpty(contentVO.getTypeCode())) {
            hql.append(" and typeCode=:typeCode");
            map.put("typeCode", contentVO.getTypeCode());
        }
        if (!AppUtil.isEmpty(contentVO.getTitle())) {
            hql.append(" and title like :title ");
            map.put("title", "%".concat(contentVO.getTitle()).concat("%"));
        }

        if (LoginPersonUtil.isSiteAdmin() || LoginPersonUtil.isSuperAdmin()) {

        } else {
            // 超管站管只能看到自己创建的被删除的数据
            hql.append(" and createUserId = " + LoginPersonUtil.getUserId());
        }

        hql.append(" order by createDate desc");
        return getPagination(contentVO.getPageIndex(), contentVO.getPageSize(), hql.toString(), map);
    }

    @Override
    public Pagination getStaticPage(Long pageIndex, Integer pageSize, Long columnId, Long siteId) {
        String hql =
                "from BaseContentEO where recordStatus='Normal' and isPublish=1 and columnId=? and siteId=?"
                        + "  order by isTop desc,num desc,createDate desc,id desc ,isPublish desc,publishDate desc, isNew desc, topValidDate desc, updateDate desc";
        return getPagination(pageIndex, pageSize, hql, new Object[]{columnId, siteId});
    }

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
                hql.append(" and publishDate >= to_date('" + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(DateUtil.getTodayStartDate())
                        + "','yyyy-MM-dd HH24:mi:ss')");
                hql.append(" and publishDate <= to_date('" + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(DateUtil.getTodayEndDate())
                        + "','yyyy-MM-dd HH24:mi:ss')");
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
                    stringBuffer.append(" and b.publishDate >= to_date('" + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(DateUtil.getTodayStartDate())
                            + "','yyyy-MM-dd HH24:mi:ss')");
                    stringBuffer.append(" and b.publishDate <= to_date('" + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(DateUtil.getTodayEndDate())
                            + "','yyyy-MM-dd HH24:mi:ss')");
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

//        if (!LoginPersonUtil.isRoot() && !LoginPersonUtil.isSuperAdmin() && !LoginPersonUtil.isSiteAdmin()) {
//            hql.append(" and createOrganId = " + LoginPersonUtil.getOrganId());
//        }

        return getCount(hql.toString(), values.toArray());
    }

    @Override
    public BaseContentVO getLastNextVO(Long columnId, Long siteId, String typeCode, Long contentId, boolean allOrPublish) {
        StringBuffer sql = new StringBuffer();
        String order = ModelConfigUtil.getOrderByHql(columnId, siteId, typeCode);
        // 替换hql语句 成sql 后期添加徐改动
        order =
                order.replace("id", "ID").replace("isNew", "IS_NEW").replace("num", "NUM").replace("isHot", "IS_HOT").replace("createDate", "CREATE_DATE")
                        .replace("isTop", "IS_TOP").replace("topValidDate", "TOP_VALID_DATE").replace("updateDate", "UPDATE_DATE")
                        .replace("isPublish", "IS_PUBLISH").replace("isTitle", "IS_TITLE").replace("publishDate", "PUBLISH_DATE").replace("hit", "HIT")
                        .replace("quoteStatus", "QUOTE_STATUS").replace("isAllowComments", "IS_ALLOW_COMMENTS");

        sql.append("select b.id as id, b.lastId as lastId, b.lastTitle as lastTitle, b.nextId as nextId, b.nextTitle as nextTitle");
        sql.append(" from(select c.id,lag(c.id,1,0) over(" + order + ") lastId,lag(c.title) over(" + order + ") lastTitle,");
        sql.append("lead(c.id,1,0) over(" + order + ") nextId, lead(c.title) over(" + order + ") nextTitle from cms_base_content c where c.column_id = ? ");
        if (!allOrPublish) {// 查询已发布
            sql.append(" and c.is_publish = 1 ");
        }
        sql.append(" and c.record_status = 'Normal') b  where b.id = ?");
        return (BaseContentVO) getBeanBySql(sql.toString(), new Object[]{columnId, contentId}, BaseContentVO.class, new String[]{"id", "lastId",
                "lastTitle", "nextId", "nextTitle"});
    }

    @Override
    public void recovery(Long[] ids) {
        String idsn = null;
        for (Long id : ids) {
            if (null == idsn) {
                idsn = id + "";
            } else {
                idsn += "," + id;
            }
        }
        StringBuffer hql = new StringBuffer("update BaseContentEO set recordStatus='Normal' where id in(" + idsn + ")");
        this.executeUpdateByHql(hql.toString(), new Object[]{});
    }

    /*@Override
    public List<BaseContentEO> getContents(String typeCode, Long siteId) {
        String hql = "from BaseContentEO where typeCode=? and siteId=? and recordStatus='Normal'";
        return getEntitiesByHql(hql, new Object[]{typeCode, siteId});
    }

    @Override
    public List<BaseContentEO> getContents(String typeCode) {
        String hql = "from BaseContentEO where typeCode=? and recordStatus='Normal' and isPublish = 1";
        return getEntitiesByHql(hql, new Object[]{typeCode});
    }*/

    @Override
    public List<String> getExistTypeCode(Long siteId) {
        String sql =
                "SELECT TYPE_CODE FROM CMS_BASE_CONTENT WHERE SITE_ID = ? AND TYPE_CODE IS NOT NULL AND  " + " RECORD_STATUS = '"
                        + AMockEntity.RecordStatus.Normal.toString() + "' GROUP BY  TYPE_CODE ORDER BY TYPE_CODE";
        return (List<String>) getObjectsBySql(sql, new Object[]{siteId});
    }

    @Override
    public BaseContentEO getContent(Long id, String status) {
        String hql = "from BaseContentEO where id=? and recordStatus=?";
        return getEntityByHql(hql, new Object[]{id, status});
    }

    @Override
    public List<BaseContentEO> getContentsByIds(Long[] ids) {
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("ids", ids);
        String hql = "from BaseContentEO where id in (:ids)";
        return getEntitiesByHql(hql, map);
    }

    @Override
    public Pagination getContentForWeChat(String title, String typeCode, Long siteId, Long pageIndex, Integer pageSize) {
        StringBuffer hql = new StringBuffer("from BaseContentEO where recordStatus='Normal' and isPublish=1");
        Map<String, Object> map = new HashMap<String, Object>();
        if (!AppUtil.isEmpty(title)) {
            hql.append(" and title like :title ");
            map.put("title", "%".concat(title).concat("%"));
        }
        if ("news".equals(typeCode)) {
            hql.append("and (typeCode='" + BaseContentEO.TypeCode.articleNews.toString() + "' or typeCode='" + BaseContentEO.TypeCode.pictureNews.toString()
                    + "' or typeCode='" + BaseContentEO.TypeCode.videoNews.toString() + "')");
        }
        if (siteId != null) {
            hql.append(" and siteId=:siteId");
            map.put("siteId", siteId);
        }
        hql.append(" order by isTop desc ,publishDate desc");
        return getPagination(pageIndex, pageSize, hql.toString(), map);
    }

    @Override
    public List<ContentChartVO> getContentChart(ContentChartQueryVO queryVO, String ids) {
        StringBuffer hql = new StringBuffer();

        if (!AppUtil.isEmpty(queryVO.getIsOrgan()) && queryVO.getIsOrgan().equals("1")) {//根据部门统计
            hql.append("select t.createOrganId as organId ,o.name as organName,count(t.id) as count from OrganEO o,BaseContentEO t ").append(
                    "where t.createOrganId=o.organId and t.recordStatus='Normal' and t.isPublish=1 and o.type='" + OrganEO.Type.OrganUnit.toString() + "'").append(
                    " and o.organId in(" + ids + ")");
        } else if (!AppUtil.isEmpty(queryVO.getIsUser()) && queryVO.getIsUser().equals("1")) {//根据人员统计
            hql.append("select t.createUserId as organId ,o.name as organName,count(t.id) as count from PersonEO o,BaseContentEO t ").append(
                    "where t.createUserId=o.userId and t.recordStatus='Normal' and t.isPublish=1 ");
        } else if (!AppUtil.isEmpty(queryVO.getIsColumn()) && queryVO.getIsColumn().equals("1")) {//根据栏目统计
            hql.append("select t.columnId as organId ,o.name as organName,count(t.id) as count from IndicatorEO o,BaseContentEO t ").append(
                    "where t.columnId=o.indicatorId and t.recordStatus='Normal' and t.isPublish=1 ");
        } else {
            hql.append("select t.unitId as organId ,o.name as organName,count(t.id) as count from OrganEO o,BaseContentEO t ").append(
                    "where t.unitId=o.organId and t.recordStatus='Normal' and t.isPublish=1 and o.type='" + OrganEO.Type.Organ.toString() + "'").append(
                    " and o.organId in(" + ids + ")");
        }
        Map<String, Object> map = new HashMap<String, Object>();
        if (null != queryVO.getSiteId()) {
            hql.append(" and t.siteId=:siteId");
            map.put("siteId", queryVO.getSiteId());
        }
        if (!StringUtils.isEmpty(queryVO.getTypeCode())) {
            if (BaseContentEO.TypeCode.workGuide.toString().equals(queryVO.getTypeCode())) {
                hql.append(" and t.typeCode in ('workGuide','sceneService')");
            } else {
                hql.append(" and t.typeCode=:typeCode");
                map.put("typeCode", queryVO.getTypeCode());
            }
        }
        if (!AppUtil.isEmpty(queryVO.getStartDate()) && !AppUtil.isEmpty(queryVO.getEndDate())) {
            hql.append(" and t.publishDate>=:startDate and t.publishDate<=:endDate");
            map.put("startDate", queryVO.getStartDate());
            map.put("endDate", queryVO.getEndDate());
        }
        if (!AppUtil.isEmpty(queryVO.getIsOrgan()) && queryVO.getIsOrgan().equals("1")) {//根据部门统计
            hql.append(" group by t.createOrganId,o.name");
        } else if (!AppUtil.isEmpty(queryVO.getIsUser()) && queryVO.getIsUser().equals("1")) {//根据人员统计
            hql.append(" and not exists (select 1 from ContentReferRelationEO f where f.referId = t.id ) ");//过滤引用新闻
            hql.append(" group by t.createUserId,o.name");
        } else if (!AppUtil.isEmpty(queryVO.getIsColumn()) && queryVO.getIsColumn().equals("1")) {//根据栏目统计
            hql.append(" group by t.columnId,o.name");
        } else {
            hql.append(" group by t.unitId,o.name");
        }
        hql.append(" order by count(t.id) desc");
        return (List<ContentChartVO>) getBeansByHql(hql.toString(), map, ContentChartVO.class, queryVO.getNum());
    }

    @Override
    public Long getCountChart(ContentChartQueryVO queryVO) {
        StringBuffer hql = new StringBuffer("select t.id,t.title from BaseContentEO t where t.recordStatus='Normal' ");
        Map<String, Object> map = new HashMap<String, Object>();
        if (null != queryVO.getSiteId()) {
            hql.append(" and t.siteId=:siteId");
            map.put("siteId", queryVO.getSiteId());
        }
        if (!StringUtils.isEmpty(queryVO.getTypeCode())) {
            if (BaseContentEO.TypeCode.workGuide.toString().equals(queryVO.getTypeCode())) {
                hql.append(" and t.typeCode in ('workGuide','sceneService')");
            } else {
                hql.append(" and t.typeCode=:typeCode");
                map.put("typeCode", queryVO.getTypeCode());
            }

        }
        if (!AppUtil.isEmpty(queryVO.getStartDate()) && !AppUtil.isEmpty(queryVO.getEndDate())) {
            hql.append(" and t.publishDate>=:startDate and t.publishDate<=:endDate");
            map.put("startDate", queryVO.getStartDate());
            map.put("endDate", queryVO.getEndDate());
        }
        Long count = getCount(hql.toString(), map);
        return count;
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
            sql.append("  and c.create_date>=to_date('" + vo.getStartDate() + "','yyyy-mm-dd:hh24:mi:ss')");
        }
        if (!StringUtils.isEmpty(vo.getEndDate())) {
            sql.append("  and c.create_date<=to_date('" + vo.getEndDate() + "','yyyy-mm-dd:hh24:mi:ss')");
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
            sql.append("  and c.create_date>=to_date('" + vo.getStartDate() + "','yyyy-mm-dd:hh24:mi:ss')");
            sql.append("  and c.publish_date>=to_date('" + vo.getStartDate() + "','yyyy-mm-dd:hh24:mi:ss')");
        }
        if (!StringUtils.isEmpty(vo.getEndDate())) {
            sql.append("  and c.create_date<=to_date('" + vo.getEndDate() + "','yyyy-mm-dd:hh24:mi:ss')");
            sql.append("  and c.publish_date<=to_date('" + vo.getEndDate() + "','yyyy-mm-dd:hh24:mi:ss')");
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
    public List<WordListVO> getWordListByColumn(StatisticsQueryVO vo) {
        StringBuffer sql = new StringBuffer();

        sql.append("select u.c count,v.c publishCount,(u.c-v.c) unPublishCount,(v.c/u.c)*100 rate,u.columnId as columnId from(").append(
                " select count(*) c,c.column_id columnId from cms_base_content c,rbac_indicator r ").append(
                " where c.column_id=r.indicator_id and c.site_id=" + vo.getSiteId());

        if (BaseContentEO.TypeCode.workGuide.toString().equals(vo.getTypeCode())) {
            sql.append(" and c.type_code in ('workGuide','sceneService')");
        } else {
            sql.append(" and c.type_code='" + vo.getTypeCode() + "'");
        }
        if (!StringUtils.isEmpty(vo.getColumnName())) {
            sql.append(" and r.name like '%" + vo.getColumnName() + "%'");
        }
        if (!StringUtils.isEmpty(vo.getStartDate())) {
            sql.append("  and c.create_date>=to_date('" + vo.getStartDate() + "','yyyy-mm-dd:hh24:mi:ss')");
        }
        if (!StringUtils.isEmpty(vo.getEndDate())) {
            sql.append("  and c.create_date<=to_date('" + vo.getEndDate() + "','yyyy-mm-dd:hh24:mi:ss')");
        }

        sql.append(" and c.record_status='Normal' group by c.column_id) u left join (")
                .append("select count(*) c,c.column_id columnId from cms_base_content c ,rbac_indicator r")
                .append(" where c.column_id=r.indicator_id and c.is_publish=1 ").append(" and c.site_id=" + vo.getSiteId());

        if (BaseContentEO.TypeCode.workGuide.toString().equals(vo.getTypeCode())) {
            sql.append(" and c.type_code in ('workGuide','sceneService')");
        } else {
            sql.append(" and c.type_code='" + vo.getTypeCode() + "'");
        }
        if (!StringUtils.isEmpty(vo.getColumnName())) {
            sql.append(" and r.name like '%" + vo.getColumnName() + "%'");
        }
        if (!StringUtils.isEmpty(vo.getStartDate())) {
            sql.append("  and c.create_date>=to_date('" + vo.getStartDate() + "','yyyy-mm-dd:hh24:mi:ss')");
            sql.append("  and c.publish_date>=to_date('" + vo.getStartDate() + "','yyyy-mm-dd:hh24:mi:ss')");
        }
        if (!StringUtils.isEmpty(vo.getEndDate())) {
            sql.append("  and c.create_date<=to_date('" + vo.getEndDate() + "','yyyy-mm-dd:hh24:mi:ss')");
            sql.append("  and c.publish_date<=to_date('" + vo.getEndDate() + "','yyyy-mm-dd:hh24:mi:ss')");
        }

        sql.append(" and c.record_status='Normal'  group by c.column_id ) v ").append("on u.columnId=v.columnId");

        List<String> fields = new ArrayList<String>();
        fields.add("count");
        fields.add("publishCount");
        fields.add("unPublishCount");
        fields.add("rate");
        fields.add("columnId");
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
            sql.append("  and c.create_date>=to_date('" + vo.getStartDate() + "','yyyy-mm-dd:hh24:mi:ss')");
        }
        if (!StringUtils.isEmpty(vo.getEndDate())) {
            sql.append("  and c.create_date<=to_date('" + vo.getEndDate() + "','yyyy-mm-dd:hh24:mi:ss')");
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
            sql.append("  and c.create_date>=to_date('" + vo.getStartDate() + "','yyyy-mm-dd:hh24:mi:ss')");
            sql.append("  and c.publish_date>=to_date('" + vo.getStartDate() + "','yyyy-mm-dd:hh24:mi:ss')");
        }
        if (!StringUtils.isEmpty(vo.getEndDate())) {
            sql.append("  and c.create_date<=to_date('" + vo.getEndDate() + "','yyyy-mm-dd:hh24:mi:ss')");
            sql.append("  and c.publish_date<=to_date('" + vo.getEndDate() + "','yyyy-mm-dd:hh24:mi:ss')");
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
            sql.append("  and c.create_date>=to_date('" + vo.getStartDate() + "','yyyy-mm-dd:hh24:mi:ss')");
        }
        if (!StringUtils.isEmpty(vo.getEndDate())) {
            sql.append("  and c.create_date<=to_date('" + vo.getEndDate() + "','yyyy-mm-dd:hh24:mi:ss')");
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
            sql.append("  and c.create_date>=to_date('" + vo.getStartDate() + "','yyyy-mm-dd:hh24:mi:ss')");
            sql.append("  and c.publish_date>=to_date('" + vo.getStartDate() + "','yyyy-mm-dd:hh24:mi:ss')");
        }
        if (!StringUtils.isEmpty(vo.getEndDate())) {
            sql.append("  and c.create_date<=to_date('" + vo.getEndDate() + "','yyyy-mm-dd:hh24:mi:ss')");
            sql.append("  and c.publish_date<=to_date('" + vo.getEndDate() + "','yyyy-mm-dd:hh24:mi:ss')");
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
    public Long getCountByCondition(Long columnId, Integer isPublish, Date st, Date ed, String recordStatus) {
        StringBuffer hql = new StringBuffer("from BaseContentEO where 1=1");
        Map<String, Object> map = new HashMap<String, Object>();
        if (null != columnId) {
            hql.append(" and columnId=:columnId");
            map.put("columnId", columnId);
        }
        if (null != isPublish) {
            hql.append(" and isPublish=:isPublish");
            map.put("isPublish", isPublish);
        }
        if (AppUtil.isEmpty(recordStatus)) {
            hql.append(" and recordStatus=:recordStatus");
            map.put("recordStatus", recordStatus);
        }
        if (null != st) {
            hql.append(" and publishDate>=:st");
            map.put("st", st);
        }
        if (null != ed) {
            hql.append(" and publishDate<:ed");
            map.put("ed", ed);
        }
        return getCount(hql.toString(), map);
    }

    @Override
    public List<ContentTjVO> getCountByCondition(Long siteId, String typeCode, Integer isPublish, Date st, Date ed) {
        StringBuffer hql =
                new StringBuffer(
                        "select b.createOrganId as organId, o.name as organName ,count(b.createOrganId) as total  from BaseContentEO as b,OrganEO as o where b.createOrganId = o.organId ");
        hql.append(" and b.siteId = :siteId and b.typeCode = :typeCode");
        hql.append(" and b.recordStatus = :recordStatus and o.recordStatus = :recordStatus and b.isPublish = :isPublish");
        Map<String, Object> paramMap = new HashMap<String, Object>();
        paramMap.put("siteId", siteId);
        paramMap.put("typeCode", typeCode);
        paramMap.put("recordStatus", AMockEntity.RecordStatus.Normal.toString());
        paramMap.put("isPublish", isPublish);

        if (null != st) {// 开始时间
            hql.append(" and b.publishDate >= :startDate");
            paramMap.put("startDate", st);
        }
        if (null != ed) {// 结束时间
            hql.append(" and b.publishDate < :endDate");
            paramMap.put("endDate", ed);
        }

        hql.append(" group by b.createOrganId,o.name order by count(b.createOrganId) desc ");
        return (List<ContentTjVO>) getBeansByHql(hql.toString(), paramMap, ContentTjVO.class, null);
    }

    @Override
    public Pagination getQueryPage(ContentPageVO pageVO, Long[] optColumns) {
        StringBuffer hql = new StringBuffer("select b from BaseContentEO b,IndicatorEO e where b.columnId = e.indicatorId and e.recordStatus = 'Normal' and b.recordStatus='Normal'");
        Map<String, Object> map = new HashMap<String, Object>();
        if (!AppUtil.isEmpty(pageVO.getTitle())) {
            hql.append(" and b.title like :title ");
            map.put("title", "%".concat(pageVO.getTitle()).concat("%"));
        }
        if (!AppUtil.isEmpty(pageVO.getSiteId())) {
            hql.append(" and b.siteId=:siteId");
            map.put("siteId", pageVO.getSiteId());
        }
        if (!AppUtil.isEmpty(pageVO.getColumnId())) {
            hql.append(" and b.columnId=:columnId");
            map.put("columnId", pageVO.getColumnId());
        }
        if (!AppUtil.isEmpty(pageVO.getCondition()) && pageVO.getStatus() != null) {
            hql.append(" and b." + pageVO.getCondition() + "=:status");
            map.put("status", pageVO.getStatus());
        }
        if (!AppUtil.isEmpty(pageVO.getTypeCodes()) && pageVO.getTypeCodes().length > 0) {
            hql.append(" and b.typeCode in(:typeCode)");
            map.put("typeCode", pageVO.getTypeCodes());
        }
        if (optColumns.length > 0) {
            hql.append(" and b.columnId in (:optColumns)");
            map.put("optColumns", optColumns);
        }
        if (null != pageVO.getIsPublish()) {
            hql.append(" and b.isPublish in (:isPublish)");
            map.put("isPublish", pageVO.getIsPublish());
        }
        if (!AppUtil.isEmpty(pageVO.getSortField())) {
            hql.append(" order by ").append(pageVO.getSortField()).append(" ").append(null != pageVO.getSortOrder() ? pageVO.getSortOrder() : " desc ");
        } else {
            hql.append(" order by b.isTop desc,b.num desc,b.createDate desc,b.id desc ,b.isPublish desc,b.publishDate desc, b.isNew desc, b.topValidDate desc, b.updateDate desc");
        }
        return getPagination(pageVO.getPageIndex(), pageVO.getPageSize(), hql.toString(), map);
    }

    @Override
    public List<BaseContentEO> deleteList(Long columnId) {
        String dateStr = "2016-07-11 00:00:00";
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd 00:00:00");
        Date date = null;
        try {
            date = format.parse(dateStr);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        StringBuffer sql = new StringBuffer("select id as id ,title as title,columnId as columnId from BaseContentEO where recordStatus='Normal'").append(
                // " and createDate <:createDate").append(" and columnId=:columnId");
                " and columnId=:columnId");

        Map<String, Object> map = new HashMap<String, Object>();
        // map.put("createDate", date);
        map.put("columnId", columnId);
        List<BaseContentEO> list = (List<BaseContentEO>) getBeansByHql(sql.toString(), map, BaseContentEO.class, 30000);
        return list;
    }

    public List<BaseContentVO> getCounts(ContentPageVO vo, Integer limit) {
        StringBuffer hql =
                new StringBuffer("select o.organId as organId, o.name as organName,count(o.organId) as counts  from BaseContentEO c,OrganEO o "
                        + "where c.createOrganId=o.organId and c.recordStatus='Normal' and o.recordStatus='Normal' ");

        Map<String, Object> map = new HashMap<String, Object>();

        if (!AppUtil.isEmpty(vo.getSiteId())) {
            hql.append(" and c.siteId=:siteId");
            map.put("siteId", vo.getSiteId());
        }
        if (!AppUtil.isEmpty(vo.getColumnId())) {
            hql.append(" and c.columnId=:columnId");
            map.put("columnId", vo.getColumnId());
        }
        if (!AppUtil.isEmpty(vo.getTypeCode())) {
            hql.append(" and c.typeCode=:typeCode");
            map.put("typeCode", vo.getTypeCode());
        }
        hql.append(" group by o.organId,o.name order by count(o.organId) desc ");

        return (List<BaseContentVO>) getBeansByHql(hql.toString(), map, BaseContentVO.class, limit);
    }

    @Override
    public Pagination getPageByMobile(ContentPageVO pageVO) {

        StringBuffer hql = new StringBuffer("from BaseContentEO c where c.recordStatus='Normal' and c.isPublish = 1 ");
        Map<String, Object> map = new HashMap<String, Object>();
        if (!AppUtil.isEmpty(pageVO.getTitle())) {
            hql.append(" and c.title like :title ");
            map.put("title", "%".concat(pageVO.getTitle()).concat("%"));
        }
        if (!AppUtil.isEmpty(pageVO.getSiteId())) {
            hql.append(" and c.siteId=:siteId");
            map.put("siteId", pageVO.getSiteId());
        }
        if (!AppUtil.isEmpty(pageVO.getColumnId())) {
            hql.append(" and c.columnId=:columnId");
            map.put("columnId", pageVO.getColumnId());
        }
        if (!AppUtil.isEmpty(pageVO.getCondition()) && pageVO.getStatus() != null) {
            hql.append(" and c." + pageVO.getCondition().trim() + "=:status");
            map.put("status", pageVO.getStatus());
        }
        if (!AppUtil.isEmpty(pageVO.getTypeCode())) {
            hql.append(" and c.typeCode=:typeCode");
            map.put("typeCode", pageVO.getTypeCode());
        }

        hql.append(ModelConfigUtil.getOrderByHql(pageVO.getColumnId(), pageVO.getSiteId(), BaseContentEO.TypeCode.articleNews.toString()));
        return getPagination(pageVO.getPageIndex(), pageVO.getPageSize(), hql.toString(), map);
    }

    @Override
    public Pagination getPageByOrganIds(List<Long> organIds, Long siteId, String typeCode, Long pageIndex, Integer pageSize) {
        StringBuffer hql =
                new StringBuffer("from BaseContentEO c where  c.recordStatus = :recordStatus and c.createOrganId in (:createOrganId) and c.isPublish = 1 ");
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("recordStatus", AMockEntity.RecordStatus.Normal.toString());
        map.put("createOrganId", organIds.toArray());
        if (!AppUtil.isEmpty(siteId)) {
            hql.append(" and c.siteId = :siteId ");
            map.put("siteId", siteId);
        }

        if (!AppUtil.isEmpty(typeCode)) {
            hql.append(" and c.typeCode = :typeCode ");
            map.put("typeCode", typeCode);
        }

        hql.append(" order by " + ModelConfigUtil.PIC_OR_ARTICLE_ORDER_DEFAULT);
        return getPagination(pageIndex, pageSize, hql.toString(), map);
    }

    @Override
    public List<Object> getStatisticCounts(List<Long> ids) {
        String sql = "select ";
        if (ids != null && ids.size() > 0) {
            for (int i = 0; i < ids.size(); i++) {
                sql += "SUM(CASE WHEN t.column_id = " + ids.get(i) + " THEN 1 ELSE 0 END)";
                if (i != (ids.size() - 1)) {
                    sql += ",";
                }
            }
        }
        sql += " from  CMS_BASE_CONTENT t  where t.site_id = " + LoginPersonUtil.getSiteId() + " and t.type_code is not null and t.record_status = 'Normal'";
        return (List<Object>) getObjectsBySql(sql, new Object[]{});
    }

    @Override
    public List<BaseContentEO> getBaseContents(Long siteId, Long columnId) {
        String hql = "from BaseContentEO c where  c.recordStatus = ? and c.siteId = ? and c.columnId = ? order by createDate asc";
        return getEntitiesByHql(hql, new Object[]{AMockEntity.RecordStatus.Normal.toString(), siteId, columnId});
    }

    @Override
    public Long getCountByTypeAndStatus(Long siteId, Long columnId, String type, Integer isPublish) {
        String hql = "";
        if (!LoginPersonUtil.isSuperAdmin() && !LoginPersonUtil.isSiteAdmin()) {
            hql = "from BaseContentEO c where c.recordStatus = ? and c.siteId = ? and c.columnId = ? and c.typeCode = ? and c.isPublish = ? ";
            if (null != LoginPersonUtil.getUserId() && null != LoginPersonUtil.getOrganId()) {// 普通用户查询自己单位的信息
                hql += " and c.createOrganId = " + LoginPersonUtil.getOrganId();
            }
            return getCount(hql, new Object[]{AMockEntity.RecordStatus.Normal.toString(), siteId, columnId, type, isPublish});
        }
        hql = "from BaseContentEO c where c.recordStatus = ? and c.siteId = ? and c.typeCode = ? and c.isPublish = ?";
        return getCount(hql, new Object[]{AMockEntity.RecordStatus.Normal.toString(), siteId, type, isPublish});
    }

    @Override
    public List<BaseContentEO> getBaseContents(Long siteId, String title) {
        String hql = "from BaseContentEO c where typeCode=? and c.siteId = ? and c.title = ?";
        return getEntitiesByHql(hql, new Object[]{BaseContentEO.TypeCode.articleNews.toString(), siteId, title});
    }

    @Override
    public void deleteNewsByColumnId(Long columnId) {
        if (columnId == null) {
            return;
        }
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("recordStatus", BaseContentEO.RecordStatus.Removed.toString());
        map.put("columnId", columnId);
        String hql = "update BaseContentEO set recordStatus = :recordStatus where columnId = :columnId";
        executeUpdateByJpql(hql, map);
    }

    @Override
    public List<BaseContentEO> getWorkGuidXYContent(Long siteId, Long columnId) {
        String hql = "from BaseContentEO c where c.columnId=? and c.siteId = ? and c.recordStatus = ? and c.isPublish=1 order by c.num desc";
        List<Object> param = new ArrayList<Object>();
        param.add(columnId);
        param.add(siteId);
        param.add(AMockEntity.RecordStatus.Normal.toString());
        return getEntitiesByHql(hql.toString(), param.toArray());
    }

    @Override
    public Long getSummaryCount(StatisticsQueryVO vo) {
        StringBuffer hql = new StringBuffer();
        List values = new ArrayList();
        hql.append("select count(*)")
                .append(" from BaseContentEO b")
                .append(" where b.recordStatus = ?");
        values.add(AMockEntity.RecordStatus.Normal.toString());

        hql.append(" and b.typeCode in ('articleNews','pictureNews','videoNews')");
        if("create".equals(vo.getOpType()) && !AppUtil.isEmpty(vo.getStartDate())) {//新增
            hql.append(" and to_char(b.createDate,'yyyyMMdd') = ?");
            values.add(vo.getStartDate());
        }
        if("update".equals(vo.getOpType()) && !AppUtil.isEmpty(vo.getStartDate())) {//更新
            hql.append(" and to_char(b.updateDate,'yyyyMMdd') = ? and b.createDate != b.updateDate");
            values.add(vo.getStartDate());
        }
        if("publish".equals(vo.getOpType()) && !AppUtil.isEmpty(vo.getStartDate())) {//发布
            hql.append(" and to_char(b.publishDate,'yyyyMMdd') = ?");
            values.add(vo.getStartDate());
        }
        return (Long)getObject(hql.toString(),values.toArray());
    }

    @Override
    public List<OrganEO> getCreateOrganByColumnIds(List<Long> columnIds, Long siteId) {
        StringBuffer sql = new StringBuffer("select a.organId as organId, a.name as name from(select t.CREATE_ORGAN_ID AS organId, u.NAME_ AS name  " +
                "FROM cms_base_content t LEFT JOIN rbac_organ u ON t.CREATE_ORGAN_ID = u.ORGAN_ID WHERE t.RECORD_STATUS = :recordStatus ");

        Map<String,Object> map = new HashMap<String, Object>();
        map.put("recordStatus", AMockEntity.RecordStatus.Normal.toString());
        if (null != columnIds && columnIds.size() > 0) {
            sql.append(" and t.COLUMN_ID in(:columnIds)");
            map.put("columnIds",columnIds);
        }
        if (!AppUtil.isEmpty(siteId)) {
            sql.append(" and t.SITE_ID = :siteId ");
            map.put("siteId", siteId);
        }
        sql.append(" and t.IS_PUBLISH = 1 )a");
        sql.append(" GROUP BY a.organId,a.name");
        String[] fieldNames = new String[]{"organId","name"};
        return (List<OrganEO>) getBeansBySql(sql.toString(),map,OrganEO.class,fieldNames);
    }

    @Override
    public List<Map<String,Object>> statisticsByColumnIdsAndOrganId(StatisticsQueryVO queryVO) {
        Map<String,Object> map = new HashMap<String, Object>();

        StringBuffer sql = new StringBuffer("select r.indicator_id as columnId,r.name as columnName,r.PARENT_ID as parentId,r.IS_PARENT as isParent,a.* from " +
                " RBAC_INDICATOR r  left join ( select t.COLUMN_ID as column_id,sum(1) as totalCount");
        if(null != queryVO.getOrganIds() && queryVO.getOrganIds().size()>0){
            for(int i=0;i<queryVO.getOrganIds().size();i++){
                Long organId = queryVO.getOrganIds().get(i);
                if(null != organId){
                    sql.append(" ,sum(case when (t.CREATE_ORGAN_ID =:organId"+i+") THEN 1 ELSE 0 END) AS c_"+organId);
                    map.put("organId"+i,organId);
                }
            }
        }

        sql.append(" FROM cms_base_content t WHERE t.RECORD_STATUS = :recordStatus and t.IS_PUBLISH =1 AND t.SITE_ID = :siteId ");
        map.put("recordStatus",AMockEntity.RecordStatus.Normal.toString());
        map.put("siteId",queryVO.getSiteId());

        if (null != queryVO.getStartTime()) {
            sql.append(" and t.CREATE_DATE >= :startDate");
            map.put("startDate",queryVO.getStartTime());
        }
        if (null != queryVO.getEndTime()) {
            sql.append(" and t.CREATE_DATE <= :endDate");
            map.put("endDate",queryVO.getEndTime());
        }

        sql.append(" GROUP BY t.COLUMN_ID)a  on a.column_id=r.INDICATOR_ID where r.RECORD_STATUS = :recordStatus ");

        if(null != queryVO.getColumnIdList() && queryVO.getColumnIdList().size()>0){
            sql.append(" AND r.indicator_id in(:columnIds)");
            map.put("columnIds",queryVO.getColumnIdList());
        }

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