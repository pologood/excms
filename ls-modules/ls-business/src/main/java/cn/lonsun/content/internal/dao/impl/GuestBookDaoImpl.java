package cn.lonsun.content.internal.dao.impl;

import cn.lonsun.common.util.AppUtil;
import cn.lonsun.content.internal.dao.IGuestBookDao;
import cn.lonsun.content.internal.entity.BaseContentEO;
import cn.lonsun.content.internal.entity.GuestBookEO;
import cn.lonsun.content.vo.*;
import cn.lonsun.core.base.dao.impl.MockDao;
import cn.lonsun.core.base.entity.AMockEntity;
import cn.lonsun.core.util.Pagination;
import cn.lonsun.site.contentModel.vo.ColumnTypeConfigVO;
import cn.lonsun.statistics.ContentChartVO;
import cn.lonsun.statistics.GuestListVO;
import cn.lonsun.statistics.StatisticsQueryVO;
import cn.lonsun.system.role.internal.util.RoleAuthUtil;
import cn.lonsun.util.DateUtil;
import cn.lonsun.util.LoginPersonUtil;
import cn.lonsun.util.ModelConfigUtil;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.text.SimpleDateFormat;
import java.util.*;


@Repository("guestBookDao")
public class GuestBookDaoImpl extends MockDao<GuestBookEO> implements IGuestBookDao {

    private static SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    @Autowired
    private SessionFactory sessionFactory;

    public void setSessionFactory(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    public SessionFactory getSessionFactory() {
        return sessionFactory;
    }


    public Session getCurrentSession() {
        return sessionFactory.getCurrentSession();
    }


    @Override
    public Pagination getPage(GuestBookPageVO pageVO) {
        StringBuffer hql = new StringBuffer("select c.title as title,c.publishDate as publishDate,c.createDate as createDate,c.isPublish as isPublish,c.columnId as columnId,c.siteId as siteId,c.workFlowStatus as workFlowStatus ")
                .append(",c.id as baseContentId,g.id as id,g.responseContent as responseContent,g.guestBookContent as guestBookContent,g.isResponse as isResponse,g.addDate as addDate")
                .append(",g.resourceType as resourceType,g.openId as openId,g.personIp as personIp,g.personPhone as personPhone,g.personName as personName,g.replyDate as replyDate,g.userId as userId")
                .append(",g.userName as userName,g.commentCode as commentCode,g.dealStatus as dealStatus,g.docNum as docNum,g.replyUserId as replyUserId,g.replyUserName as replyUserName")
                .append(",g.receiveId as receiveId,g.receiveName as receiveName,g.receiveUserCode as receiveUserCode,g.recType as recType,g.classCode as classCode,g.randomCode as randomCode")
                .append(",g.replyUnitId as replyUnitId,g.replyUnitName as replyUnitName,g.isPublic as isPublic,g.isPublicInfo as isPublicInfo")
                .append(" from BaseContentEO c,GuestBookEO g where g.baseContentId=c.id and c.recordStatus=? and g.recordStatus=? ");

        List<Object> values = new ArrayList<Object>();
        values.add(AMockEntity.RecordStatus.Normal.toString());
        values.add(AMockEntity.RecordStatus.Normal.toString());

        if (pageVO.getIsPublish() != null) {
            hql.append(" and c.isPublish=?");
            values.add(pageVO.getIsPublish());
        }

        if (!StringUtils.isEmpty(pageVO.getDealStatus())) {
            if(pageVO.getDealStatus().equals("unreply")||pageVO.getDealStatus().equals("unhandle")){
                hql.append(" and ( g.dealStatus=? or g.dealStatus is null )");
            }else{
                hql.append(" and g.dealStatus=?");
            }
            values.add(pageVO.getDealStatus());

        }
        if (!AppUtil.isEmpty(pageVO.getSiteId())) {
            hql.append(" and c.siteId=?");
            values.add(pageVO.getSiteId());
        }
        if (!AppUtil.isEmpty(pageVO.getColumnId())) {
            hql.append(" and c.columnId=?");
            values.add(pageVO.getColumnId());
        }

        if (null != pageVO.getType()) {
            hql.append(" and g.type =?");
            values.add(pageVO.getType());
        }
        if (!AppUtil.isEmpty(pageVO.getTitle())) {
            hql.append(" and c.title like ?");
            values.add("%".concat(pageVO.getTitle().trim()).concat("%"));
        }
        if (!AppUtil.isEmpty(pageVO.getTypeCode())) {
            hql.append(" and c.typeCode =?");
            values.add(pageVO.getTypeCode());
        }
        if (!AppUtil.isEmpty(pageVO.getOpenId())) {
            hql.append(" and g.openId =?");
            values.add(pageVO.getOpenId());
        }
        if (!AppUtil.isEmpty(pageVO.getSt())) {
            hql.append(" and g.addDate>=?");
            values.add(pageVO.getSt());
        }
        if (!AppUtil.isEmpty(pageVO.getEd())) {
            hql.append(" and g.addDate<?");
            values.add(pageVO.getEd());
        }
        ColumnTypeConfigVO configVO = ModelConfigUtil.getCongfigVO(pageVO.getColumnId(), pageVO.getSiteId());
        if (configVO == null) {
            return null;
        }
        if (!RoleAuthUtil.isCurUserColumnAdmin(pageVO.getColumnId())&&!LoginPersonUtil.isSiteAdmin() && !LoginPersonUtil.isSuperAdmin()) {
            hql.append(" and( g.receiveId=?  or g.createUnitId=?)");
            values.add(LoginPersonUtil.getUnitId());
            values.add(LoginPersonUtil.getUnitId());
        }
        if (!AppUtil.isEmpty(pageVO.getReceiveId())) {
            hql.append(" and g.receiveId =?");
            values.add(pageVO.getReceiveId());
        }
        if (!AppUtil.isEmpty(pageVO.getRecUserId())) {
            hql.append(" and g.receiveUserCode =?");
            values.add(pageVO.getRecUserId());
        }
        if (!StringUtils.isEmpty(pageVO.getClassCode())) {
            hql.append(" and g.classCode =?");
            values.add(pageVO.getClassCode());
        }

        hql.append(" order by g.addDate desc,g.docNum desc");
        Pagination page = getPagination(pageVO.getPageIndex(), pageVO.getPageSize(), hql.toString(), values.toArray(), GuestBookEditVO.class);
        return page;
    }


    @Override
    public Object queryOrgan() {

        return null;
    }

    @Override
    public void changeOrganId(Long id, Long receiveId) {
        GuestBookEO eo = getEntity(GuestBookEO.class, id);
        eo.setReceiveId(id);
        eo.setForwardDate(new Date());
        //eo.setRemarks(remarks);
        getCurrentSession().save(eo);

    }

    @Override
    public GuestBookEO getByContentId(Long contentId) {
        String hql = "from GuestBookEO where baseContentId = ?";
        return this.getEntityByHql(hql.toString(), new Object[]{contentId});
    }

    @Override
    public GuestBookEO queryAuditMark(Long guestBookId) {
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("guestBookId", guestBookId);
        String hql = "from GuestBookEO where id = 'guestBookId'";
        return (GuestBookEO) getCurrentSession().createQuery(hql);
    }

    @Override
    public void saveGuestBook(GuestBookEO eo) {
        save(eo);
    }


    //单个删除留言
    @Override
    public void remove(Long id) {
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("Removed", "Removed");
        map.put("id", id);
        String hql = "update GuestBookEO set recordStatus=:Removed where baseContentId = :id";
        executeUpdateByJpql(hql, map);
    }

    //批量删除从表留言
    @Override
    public void batchDelete(Long[] ids) {
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("Removed", "Removed");
        map.put("ids", ids);
        String hql = "update GuestBookEO set recordStatus=:Removed where baseContentId in (:ids)";
        executeUpdateByJpql(hql, map);

    }

    //批量发布&单个发布
    @Override
    public void publish(Long[] ids, Integer status) {
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("status", status);
        map.put("ids", ids);
        String hql = "update BaseContentEO set isPublish=:status where id in (:ids)";
        executeUpdateByJpql(hql, map);

    }

    @Override
    public Long count(Integer i) {
        //0为未回复的
        if (i == 1) {
            List<Object> values = new ArrayList<Object>();
            Calendar calendar = Calendar.getInstance();
            calendar.add(Calendar.DATE, -7);
            Date date = calendar.getTime();
            values.add(date);
            values.add(new Date());
            String sql = " from GuestBookEO g where g.replyDate>? and g.replyDate<? and  g.dealStatus in('handled','replyed')";
            Long count = getCount(sql, values.toArray());
            return count;
        } else {
            String sql = " from GuestBookEO g where g.createDate>? and g.createDate<? and  g.dealStatus not in('handled','replyed')";
            List<Object> values = new ArrayList<Object>();
            Calendar calendar = Calendar.getInstance();
            calendar.add(Calendar.DATE, -7);
            Date date = calendar.getTime();
            values.add(date);
            values.add(new Date());
            return getCount(sql, values.toArray());
        }


    }

    @Override
    public Long checkDelete(Long columnId) {
        StringBuffer sql = new StringBuffer("select * from cms_base_content c,guestbook_table g "
                + "where g.base_content_id=c.id and c.record_status=? and g.record_status=? and c.column_id=?");
        List<Object> values = new ArrayList<Object>();
        values.add(AMockEntity.RecordStatus.Normal.toString());
        values.add(AMockEntity.RecordStatus.Normal.toString());
        values.add(columnId);
        return getCountBySql(sql.toString(), values.toArray());
    }


    //回收站查询被删除留言的详细信息
    @Override
    public Object queryRemoved(Long id) {
        String sql = "select c.title,c.create_date,g.guestbook_content,g.person_ip,g.person_name from cms_base_content c,guestbook_table g where g.base_content_id=c.id and c.id=? ";
        List<Object> values = new ArrayList<Object>();
        values.add(id);

        List<String> fields = new ArrayList<String>();
        fields.add("title");
        fields.add("create_date");
        fields.add("guestbook_content");
        fields.add("person_ip");
        fields.add("person_name");
        String[] str = new String[fields.size()];
        return getBeanBySql(sql, values.toArray(), GuestBookVO.class, fields.toArray(str));
    }

    @Override
    public void recovery(Long id) {
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("Normal", "Normal");
        map.put("id", id);
        String hql = "update BaseContentEO set recordStatus=:Normal where id=:id";
        executeUpdateByJpql(hql, map);

    }

    @Override
    public void completelyDelete(Long id) {
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("id", id);
        String hql = "delete from BaseContentEO where id=:id";
        executeUpdateByJpql(hql, map);
    }

    @Override
    public void batchRecovery(Long[] ids) {
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("Normal", "Normal");
        map.put("ids", ids);
        String hql = "update BaseContentEO set recordStatus=:Normal where id in (:ids)";
        executeUpdateByJpql(hql, map);
    }

    @Override
    public void batchCompletelyDelete(Long[] ids) {
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("ids", ids);
        String hql = "delete from GuestBookEO where baseContentId in (:ids)";
        executeUpdateByJpql(hql, map);

    }

    @Override
    public GuestBookEO noAuditGuestBook(Long id) {
        String hql = "from GuestBookEO where baseContentId=?";
        return getEntityByHql(hql, new Object[]{id});
    }

    @Override
    public List<ContentChartVO> getStatisticsList(ContentChartQueryVO queryVO) {
        StringBuffer sql = new StringBuffer("select 100*(v.c/u.c) as rate,u.receive_id as organId from(")
                .append("select count(*) c,g.receive_id  from cms_base_content b,guestbook_table g ")
                .append("where b.id=g.base_content_id and b.record_status=? and b.site_id=?")
                .append(" and b.type_code=? and b.is_publish=1");
        List<Object> values = new ArrayList<Object>();
        values.add( AMockEntity.RecordStatus.Normal.toString());
        values.add(queryVO.getSiteId());
        values.add( BaseContentEO.TypeCode.guestBook.toString());
        if (!StringUtils.isEmpty(queryVO.getStartStr()) && !StringUtils.isEmpty(queryVO.getEndStr())) {
            sql.append(" and b.publish_date>=to_date(?,'yyyy-mm-dd hh24:mi:ss') and b.publish_date<=to_date(?,'yyyy-mm-dd hh24:mi:ss') ");
            values.add( queryVO.getStartStr());
            values.add( queryVO.getEndStr());
        }
        sql.append("group by g.receive_id) u , (select count(*) c,g.receive_id  from cms_base_content b,guestbook_table g ")
                .append("where b.id=g.base_content_id and b.record_status=? and  g.deal_status in('handled','replyed') ")
                .append("and b.site_id=? and b.type_code=? and b.is_publish=1");

        values.add( AMockEntity.RecordStatus.Normal.toString());
        values.add(queryVO.getSiteId());
        values.add( BaseContentEO.TypeCode.guestBook.toString());
        if (!StringUtils.isEmpty(queryVO.getStartStr()) && !StringUtils.isEmpty(queryVO.getEndStr())) {
            sql.append(" and b.publish_date>=to_date(?,'yyyy-mm-dd hh24:mi:ss') and b.publish_date<=to_date(?,'yyyy-mm-dd hh24:mi:ss') ");
            values.add( queryVO.getStartStr());
            values.add( queryVO.getEndStr());
        }
        sql.append(" group by g.receive_id)v where u.receive_id=v.receive_id order by v.c/u.c desc ");

        List<String> fields = new ArrayList<String>();
        fields.add("rate");
        fields.add("organId");
        String[] arr = new String[fields.size()];
        return (List<ContentChartVO>) getBeansBySql(sql.toString(), values.toArray(), ContentChartVO.class, fields.toArray(arr));
    }

    @Override
    public List<ContentChartVO> getTypeList(ContentChartQueryVO queryVO) {
        StringBuffer sql = new StringBuffer(" select o.class_Code as type, count(*) as count")
                .append(" from cms_base_content c, guestbook_table o where c.id=o.base_content_Id and c.type_Code=?")
                .append(" and c.record_Status=? and c.is_publish=1 and c.site_id=? ");
        List<Object> values = new ArrayList<Object>();
        values.add(BaseContentEO.TypeCode.guestBook.toString());
        values.add(AMockEntity.RecordStatus.Normal.toString());
        values.add(queryVO.getSiteId());
        if (!StringUtils.isEmpty(queryVO.getStartStr()) && !StringUtils.isEmpty(queryVO.getEndStr())) {
            sql.append("  and c.publish_date>=to_date(?,'yyyy-mm-dd hh24:mi:ss') and c.publish_date<=to_date(?,'yyyy-mm-dd hh24:mi:ss') ");
            values.add(queryVO.getStartStr());
            values.add(queryVO.getEndStr());
        }
        sql.append(" group by o.class_Code");
        List<String> fields = new ArrayList<String>();
        fields.add("type");
        fields.add("count");
        String[] arr = new String[fields.size()];
        List<ContentChartVO> list = (List<ContentChartVO>) getBeansBySql(sql.toString(), values.toArray(), ContentChartVO.class, fields.toArray(arr));
        return list;
    }

    @Override
    public Pagination getGuestPage(StatisticsQueryVO vo) {
        StringBuffer sql = new StringBuffer("select u.c recCount,v.c dealCount,(u.c-v.c) undoCount,100*(v.c/u.c) rate,u.organId as organId from(")
                .append(" select count(*) c,g.receive_id organId from cms_base_content c, guestbook_table g")
                .append(" where c.id=g.base_content_id and c.record_status=? and c.is_publish=1 and c.site_id=? and c.type_code=?");
        List<Object> values = new ArrayList<Object>();
        values.add(AMockEntity.RecordStatus.Normal.toString());
        values.add(vo.getSiteId());
        values.add(vo.getTypeCode());
        if (!StringUtils.isEmpty(vo.getStartDate())) {
            sql.append("  and c.create_date>=to_date(?,'yyyy-mm-dd:hh24:mi:ss')");
            values.add(vo.getStartDate());
        }
        if (!StringUtils.isEmpty(vo.getEndDate())) {
            sql.append("  and c.create_date<=to_date(?,'yyyy-mm-dd:hh24:mi:ss')");
            values.add(vo.getEndDate());
        }
        sql.append(" group by g.create_organ_id) u,(")
                .append("select count(*) c,g.receive_id organId from cms_base_content c, guestbook_table g ")
                .append(" where c.id=g.base_content_id and c.record_status=? and c.is_publish=1 and c.site_id=? and c.type_code=?");
        values.add(AMockEntity.RecordStatus.Normal.toString());
        values.add(vo.getSiteId());
        values.add(vo.getTypeCode());

        if (!StringUtils.isEmpty(vo.getStartDate())) {
            sql.append("  and c.create_date>=to_date(?,'yyyy-mm-dd:hh24:mi:ss')");
            sql.append("  and c.publish_date>=to_date(?,'yyyy-mm-dd:hh24:mi:ss')");
            values.add( vo.getStartDate());
            values.add( vo.getStartDate());
        }
        if (!StringUtils.isEmpty(vo.getEndDate())) {
            sql.append("  and c.create_date<=to_date(?,'yyyy-mm-dd:hh24:mi:ss')");
            sql.append("  and c.publish_date<=to_date(?,'yyyy-mm-dd:hh24:mi:ss')");
            values.add( vo.getEndDate());
            values.add( vo.getEndDate());
        }
        sql.append(" and  g.deal_status in('handled','replyed') group by g.receive_id ) v ")
                .append(" where u.organId=v.organId");
        List<String> fields = new ArrayList<String>();
        fields.add("recCount");
        fields.add("dealCount");
        fields.add("undoCount");
        fields.add("rate");
        fields.add("organId");
        String[] arr = new String[fields.size()];
        return getPaginationBySql(vo.getPageIndex(), vo.getPageSize(), sql.toString(), values.toArray(), GuestListVO.class, fields.toArray(arr));
    }

    @Override
    public List<GuestListVO> getGuestList(StatisticsQueryVO vo) {

        StringBuffer sql = new StringBuffer("select u.c recCount,v.c dealCount,(u.c-v.c) undoCount,100*(v.c/u.c) rate,u.organId as organId from(")
                .append(" select count(*) c,g.receive_id organId from cms_base_content c, guestbook_table g")
                .append(" where c.id=g.base_content_id and  c.record_status=? and c.is_publish=1 and c.site_id=? and c.type_code=?");
        List<Object> values = new ArrayList<Object>();
        values.add(AMockEntity.RecordStatus.Normal.toString());
        values.add(vo.getSiteId());
        values.add(vo.getTypeCode());
        if (!StringUtils.isEmpty(vo.getStartDate())) {
            sql.append("  and c.create_date>=to_date(?,'yyyy-mm-dd:hh24:mi:ss')");
            values.add( vo.getStartDate());
        }
        if (!StringUtils.isEmpty(vo.getEndDate())) {
            sql.append("  and c.create_date<=to_date(?,'yyyy-mm-dd:hh24:mi:ss')");
            values.add( vo.getEndDate());
        }
        if (null!=vo.getOrganId()) {
            sql.append("  and g.receive_id=?");
            values.add( vo.getOrganId());
        }
        if (!StringUtils.isEmpty(vo.getColumnIds())) {
            sql.append(" and c.column_id in ('" + vo.getColumnIds() + "')");
        }
        sql.append(" group by g.receive_id) u left join (")
                .append("select count(*) c,g.receive_id organId from cms_base_content c, guestbook_table g ")
                .append(" where c.id=g.base_content_id and c.record_status=? and  c.is_publish=1 and c.site_id=? and c.type_code=?");
        values.add(AMockEntity.RecordStatus.Normal.toString());
        values.add(vo.getSiteId());
        values.add(vo.getTypeCode());
        if (!StringUtils.isEmpty(vo.getStartDate())) {
            sql.append("  and c.create_date>=to_date(?,'yyyy-mm-dd:hh24:mi:ss')");
            values.add( vo.getStartDate());
        }
        if (!StringUtils.isEmpty(vo.getEndDate())) {
            sql.append("  and c.create_date<=to_date(?,'yyyy-mm-dd:hh24:mi:ss')");
            values.add( vo.getEndDate());
        }
        if (null!=vo.getOrganId()) {
            sql.append("  and g.receive_id=?");
            values.add( vo.getOrganId());
        }
        if (!StringUtils.isEmpty(vo.getColumnIds())) {
            sql.append(" and c.column_id in ('" + vo.getColumnIds() + "')");
        }
        sql.append(" and  g.deal_status in('handled','replyed') group by g.receive_id ) v ")
                .append(" on u.organId=v.organId order by v.c desc");
        List<String> fields = new ArrayList<String>();
        fields.add("recCount");
        fields.add("dealCount");
        fields.add("undoCount");
        fields.add("rate");
        fields.add("organId");
        String[] arr = new String[fields.size()];
        return (List<GuestListVO>) getBeansBySql(sql.toString(), values.toArray(), GuestListVO.class, fields.toArray(arr));
    }

    @Override
    public Long getUnReplyCount(Long columnId, int day) {
        StringBuffer hql = new StringBuffer(" select b.id as id,g.addDate as createDate from BaseContentEO b,GuestBookEO g where b.id=g.baseContentId ")
                .append(" and b.recordStatus='" + AMockEntity.RecordStatus.Normal.toString())
                .append("' and ( g.dealStatus is null or g.dealStatus not in ('handled','replyed'))");
        if (columnId != null) {
            hql.append(" and b.columnId=" + columnId);
        }
        List<BaseContentEO> list = (List<BaseContentEO>) getBeansByHql(hql.toString(), new Object[]{}, BaseContentEO.class);
        Long count = 0L;
        if (list != null && list.size() > 0) {
            if (day > 0) {
                Long time = day * 24 * 60 * 60 * 1000L;
                Long now = new Date().getTime();
                Long createTime = null;
                for (BaseContentEO eo : list) {
                    createTime = eo.getCreateDate().getTime();
                    if (now - createTime > time) {
                        count += 1;
                    }
                }
            }
        }
        return count;
    }

    @Override
    public List<GuestBookEditVO> getUnReply(Long columnId, int day) {
        StringBuffer hql = new StringBuffer(" select g.id as id, b.id as baseContentId,g.addDate as addDate,b.siteId as siteId,")
                .append(" g.guestBookContent as guestBookContent,g.responseContent as responseContent,g.personName as personName,g.type as type")
                .append(",g.receiveId as receiveId,g.receiveName as receiveName from BaseContentEO b,GuestBookEO g where b.id=g.baseContentId")
                .append(" and b.recordStatus='" + AMockEntity.RecordStatus.Normal.toString() + "'")
                .append(" and ( g.dealStatus is null or g.dealStatus not in ('handled','replyed'))");
        if (columnId != null) {
            hql.append(" and b.columnId=" + columnId);
        }
        List<GuestBookEditVO> list = (List<GuestBookEditVO>) getBeansByHql(hql.toString(), new Object[]{}, GuestBookEditVO.class);
        if (list != null && list.size() > 0) {
            if (day > 0) {
                Long time = day * 24 * 60 * 60 * 1000L;
                Long now = new Date().getTime();
                for (Iterator<GuestBookEditVO> iter = list.iterator(); iter.hasNext();) {
                    GuestBookEditVO eo = iter.next();
                    Long createTime = eo.getAddDate().getTime();
                    if (now - createTime <= time) {
                        iter.remove();
                    }
                }
            }
        }
        return list;
    }

    @Override
    public List<GuestBookEditVO> getGuestBookBySiteId(Long siteId) {
        StringBuffer hql = new StringBuffer(" select g.id as id, b.id as baseContentId,b.createDate as createDate,b.siteId as siteId,")
                .append(" g.guestBookContent as guestBookContent,g.responseContent as responseContent")
                .append(" from BaseContentEO b,GuestBookEO g where b.id=g.baseContentId and b.isPublish=1")
                .append(" and b.recordStatus='" + AMockEntity.RecordStatus.Normal.toString() + "'");
        if (siteId != null) {
            hql.append(" and b.siteId=" + siteId);
        }
        List<GuestBookEditVO> list = (List<GuestBookEditVO>) getBeansByHql(hql.toString(), new Object[]{}, GuestBookEditVO.class);

        return list;
    }

    @Override
    public List<GuestBookEO> getGuestBookList(Long[] ids) {
        String hql = "from GuestBookEO where baseContentId in (:ids)";
        List<Object> values = new ArrayList<Object>();
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("ids", ids);
        List<GuestBookEO> list = (List<GuestBookEO>) getEntitiesByHql(hql.toString(), map);
        return list;
    }

    @Override
    public Long countGuestbook(Long siteId, String columnIds, Date startDate, Integer isPublish, Integer isResponse) {
        StringBuffer hql = new StringBuffer(" select g.id as id, b.id as baseContentId ")
                .append(" from BaseContentEO b,GuestBookEO g where b.id=g.baseContentId ")
                .append(" and b.recordStatus='" + AMockEntity.RecordStatus.Normal.toString() + "'");
        List<Object> values = new ArrayList<Object>();
        if (siteId != null) {
            hql.append(" and b.siteId=?");
            values.add(siteId);
        }
        if (!StringUtils.isEmpty(columnIds)) {
            hql.append(" and b.columnId in(" + columnIds + ")");
        }
        if (!AppUtil.isEmpty(startDate)) {
            hql.append(" and g.addDate>=?");
            values.add(startDate);
        }
        if (isPublish != null) {
            hql.append(" and b.isPublish=?");
            values.add(isPublish);
        }
        if (isResponse != null) {
            if (isResponse == 1) {
                hql.append(" and g.dealStatus in ('handled','replyed')");
            } else {
                hql.append(" and g.dealStatus not in ('handled','replyed')");
            }

        }
        Long count = getCount(hql.toString(), values.toArray());
        if (count == null) {
            count = 0L;
        }
        return count;
    }

    @Override
    public Long countGuestbook2(Long siteId, String columnIds, Date startDate, Integer isPublish, Integer isResponse) {
        StringBuffer hql = new StringBuffer(" select g.id as id, b.id as baseContentId ")
                .append(" from BaseContentEO b,GuestBookEO g where b.id=g.baseContentId ");
        List<Object> values = new ArrayList<Object>();
        if (siteId != null) {
            hql.append(" and b.siteId=?");
            values.add(siteId);
        }
        if (!StringUtils.isEmpty(columnIds)) {
            hql.append(" and b.columnId in(" + columnIds + ")");
        }
        if (!AppUtil.isEmpty(startDate)) {
            hql.append(" and g.addDate>=?");
            values.add(startDate);
        }
        if (isPublish != null) {
            hql.append(" and b.isPublish=?");
            values.add(isPublish);
        }
        if (isResponse != null) {
            if (isResponse == 1) {
                hql.append(" and g.dealStatus in ('handled','replyed')");
            } else {
                hql.append(" and g.dealStatus not in ('handled','replyed')");
            }

        }
        Long count = getCount(hql.toString(), values.toArray());
        if (count == null) {
            count = 0L;
        }
        return count;
    }

    @Override
    public List<GuestListVO> replyOKRank(Long siteId, String columnIds, Integer isPublish) {
        StringBuffer sql = new StringBuffer("select w.c  as unSatCount,u.receive_id as organId,u.c recCount,v.c dealCount,(u.c-v.c) undoCount from(")
                .append("select count(*) c,g.receive_id  from cms_base_content b,guestbook_table g　")
                .append("where b.id=g.base_content_id and b.record_status=? and b.site_id=? and b.type_code=?");
        List<Object> values = new ArrayList<Object>();
        values.add(AMockEntity.RecordStatus.Normal.toString());
        values.add(siteId);
        values.add(BaseContentEO.TypeCode.guestBook.toString());
        if (StringUtils.isEmpty(columnIds)) {
            sql.append(" and b.column_id in(" + columnIds + ")");
        }
        if (isPublish != null) {
            sql.append(" and b.is_publish=? ");
            values.add(isPublish);
        }
        sql.append(" group by g.receive_id) u left join (select count(*) c,g.receive_id  from cms_base_content b,guestbook_table g ")
                .append("where b.id=g.base_content_id and b.record_status=? and  g.deal_status in('handled','replyed') ")
                .append("and b.site_id=? and b.type_code=? ");
        values.add(AMockEntity.RecordStatus.Normal.toString());
        values.add(siteId);
        values.add(BaseContentEO.TypeCode.guestBook.toString());

        if (StringUtils.isEmpty(columnIds)) {
            sql.append(" and b.column_id in(" + columnIds + ")");
        }
        if (isPublish != null) {
            sql.append(" and b.is_publish=? ");
            values.add(isPublish);
        }
        sql.append(" group by g.receive_id)v on u.receive_id=v.receive_id left join (select count(*) c,g.receive_id  from cms_base_content b,guestbook_table g")
                .append(" where b.id=g.base_content_id and b.record_status=? and  g.deal_status in('handled','replyed')")
                .append(" and b.site_id=? and b.type_code=? and g.comment_code='unsatisfactory'");

        values.add(AMockEntity.RecordStatus.Normal.toString());
        values.add(siteId);
        values.add(BaseContentEO.TypeCode.guestBook.toString());
        if (StringUtils.isEmpty(columnIds)) {
            sql.append(" and b.column_id in(" + columnIds + ")");
        }
        if (isPublish != null) {
            sql.append(" and b.is_publish=? ");
            values.add(isPublish);
        }
        sql.append(" group by g.receive_id)w on  v.receive_id=w.receive_id where u.receive_id is not null order by w.c/u.c asc ");

        List<String> fields = new ArrayList<String>();
        fields.add("unSatCount");
        fields.add("organId");
        fields.add("recCount");
        fields.add("dealCount");
        fields.add("undoCount");
        String[] arr = new String[fields.size()];
        return (List<GuestListVO>) getBeansBySql(sql.toString(), values.toArray(), GuestListVO.class, fields.toArray(arr));
    }

    @Override
    public Pagination getMobilePage(GuestBookPageVO pageVO) {
        StringBuffer hql = new StringBuffer("select c.title as title,c.publishDate as publishDate,c.createDate as createDate,c.isPublish as isPublish,c.columnId as columnId,c.siteId as siteId")
                .append(",c.id as baseContentId,g.id as id,g.responseContent as responseContent,g.guestBookContent as guestBookContent,g.isResponse as isResponse,g.addDate as addDate")
                .append(",g.resourceType as resourceType,g.openId as openId,g.personIp as personIp,g.personName as personName,g.replyDate as replyDate,g.userId as userId,g.userName as userName")
                .append(",g.receiveId as receiveId,g.receiveUserCode as receiveUserCode,g.recType as recType,g.classCode as classCode,g.docNum as docNum,g.randomCode as randomCode")
                .append(",g.receiveName as receiveName, g.isPublic as isPublic,g.isPublicInfo as isPublicInfo,g.dealStatus as dealStatus")
                .append(" from BaseContentEO c,GuestBookEO g where g.baseContentId=c.id and c.recordStatus=? and g.recordStatus=?");

        List<Object> values = new ArrayList<Object>();
        values.add(AMockEntity.RecordStatus.Normal.toString());
        values.add(AMockEntity.RecordStatus.Normal.toString());

        if (!AppUtil.isEmpty(pageVO.getSiteId())) {
            hql.append(" and c.siteId=?");
            values.add(pageVO.getSiteId());
        }
        if (!AppUtil.isEmpty(pageVO.getCreateUserId())) {
            hql.append(" and c.createUserId=?");
            values.add(pageVO.getCreateUserId());
        }else{
            hql.append(" and g.isPublic=1 ");
        }

        if (!AppUtil.isEmpty(pageVO.getIsPublish())) {
            hql.append(" and c.isPublish=?");
            values.add(pageVO.getIsPublish());
        }

        if (!AppUtil.isEmpty(pageVO.getTypeCode())) {
            hql.append(" and c.typeCode =?");
            values.add(pageVO.getTypeCode());
        }

        if (!StringUtils.isEmpty(pageVO.getClassCode())) {
            hql.append(" and g.classCode =?");
            values.add(pageVO.getClassCode());
        }

        if (!StringUtils.isEmpty(pageVO.getColumnIds())) {
            hql.append(" and c.columnId in ("+pageVO.getColumnIds()+")");
        }
        if (!AppUtil.isEmpty(pageVO.getColumnId())) {
            hql.append(" and c.columnId=?");
            values.add(pageVO.getColumnId());
        }

        if (!AppUtil.isEmpty(pageVO.getIsReply())) {
            if (pageVO.getIsReply() == 1) {
                hql.append(" and g.dealStatus in ('handled','replyed')");
            } else if (pageVO.getIsReply() == 2) {
                hql.append(" and ( g.dealStatus is null or g.dealStatus not in('handled','replyed') )");
            }else if(pageVO.getIsReply()==3){
                hql.append(" and g.dealStatus in('handled','replyed','handling') ");
            }
        }

        if(pageVO.getOrderType()==null){
            hql.append(" order by  g.addDate desc,g.docNum desc");
        }else if(pageVO.getOrderType()==1){
            hql.append(" order by  g.replyDate desc,g.docNum desc");
        }else if(pageVO.getOrderType()==2){
            hql.append(" order by  c.publishDate desc,g.docNum desc");
        }else{
            hql.append(" order by  g.addDate desc,g.docNum desc");
        }

        Pagination page = getPagination(pageVO.getPageIndex(), pageVO.getPageSize(), hql.toString(), values.toArray(), GuestBookEditVO.class);
        return page;
    }

    @Override
    public GuestBookNumVO getAnalys(GuestBookStatusVO vo) {
        StringBuffer sql = new StringBuffer();
        String cs = "";
        Long[] columnIds = vo.getColumnIds();
        Long siteId =vo.getSiteId();
        Long organId =vo.getOrganId();
        if (columnIds != null && columnIds.length > 0) {
            List list = Arrays.asList(vo.getColumnIds());
            cs = StringUtils.join(list, ",");
        }
        sql.append("select sum(t.total)amount,sum(t.reply)replyNum,round(case when sum(t.total)=0 then 0 " +
                "else 100*(sum(t.reply)/sum(t.total))end,2)replyRate from (" +
                "select count(*)as total,0 as reply from cms_base_content c,guestbook_table g where g.base_content_id=c.id and c.is_publish=1");
        if (!StringUtils.isEmpty(cs)) {
            if(columnIds.length==1){
                sql.append(" and c.column_id=" + columnIds[0]);
            }else{
                sql.append(" and c.column_id in(" + cs + ") ");
            }
        }
        if (siteId != null) {
            sql.append(" and c.site_id=" + siteId);
        }
        if (organId != null) {
            sql.append(" and g.receive_id=" + organId);
        }
        sql.append(" and c.record_status='" + AMockEntity.RecordStatus.Normal.toString() +
                "' union all select 0,count(*) from cms_base_content c,guestbook_table g where g.base_content_id=c.id and c.is_publish=1");
        if (!StringUtils.isEmpty(cs)) {
            if(columnIds.length==1){
                sql.append(" and c.column_id=" + columnIds[0]);
            }else{
                sql.append(" and c.column_id in(" + cs + ") ");
            }
        }
        if (siteId != null) {
            sql.append(" and c.site_id=" + siteId);
        }
        if (organId != null) {
            sql.append(" and g.receive_id=" + organId);
        }
        sql.append(" and g.deal_status in('replyed','handled') and c.record_status='" + AMockEntity.RecordStatus.Normal.toString() + "') t");

        GuestBookNumVO gv = (GuestBookNumVO) this.getBeanBySql(sql.toString(), new Object[]{}, GuestBookNumVO.class);
        return gv;
    }

    @Override
    public List<GuestBookTypeVO> getAnalysType(Long[] columnIds, Long siteId, Integer isReply) {
        StringBuffer sql = new StringBuffer();
        sql.append("select count(*) as count,g.class_code as classCode from cms_base_content c,guestbook_table g where g.base_content_id=c.id and c.is_publish=1" +
                "and g.class_code is not null ");
        if (siteId != null) {
            sql.append(" and c.site_id=:siteId");
        }
        if (columnIds != null && columnIds.length > 0) {
            sql.append(" and c.column_id in(:ids) ");
        }
        if (isReply != null && isReply == 1) {
            sql.append(" and g.deal_status in('handled','replyed')");
        }
        sql.append("and c.record_status=:recordStatus group by g.class_code");
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("siteId", siteId);
        map.put("ids", columnIds);
        map.put("recordStatus", AMockEntity.RecordStatus.Normal.toString());
        List<GuestBookTypeVO> list = (List<GuestBookTypeVO>) getBeansBySql(sql.toString(), map, GuestBookTypeVO.class, null);
        //this.getObjectsBySql(sql.toString(),map);
        return list;
    }

    @Override
    public Long getDateTotalCount(Long[] columnIds, String Date, Long siteId, String monthDate) {
        StringBuffer sql = new StringBuffer();
        String todayDate = Date;
        List<Object> values = new ArrayList<Object>();
        sql.append(" from BaseContentEO c,GuestBookEO g where g.baseContentId=c.id and c.isPublish=1");
        if (columnIds != null && columnIds.length > 0) {
            if(columnIds.length==1){
                sql.append(" and c.columnId =? ");
                values.add(columnIds[0]);
            }else{
                String columnIdStr="";
                for(int i=0; i<columnIds.length-1;i++){
                    columnIdStr+=columnIds[i]+",";
                }
                columnIdStr+=columnIds[columnIds.length-1];
                sql.append(" and c.columnId in("+columnIdStr+ ")");
            }
        }
        if (siteId != null) {
            sql.append(" and c.siteId=? ");
            values.add(siteId);
        }
        sql.append("and g.recordStatus=? and c.recordStatus=? " +
                " and g.addDate >=? ");
        values.add(AMockEntity.RecordStatus.Normal.toString());
        values.add(AMockEntity.RecordStatus.Normal.toString());
        values.add(DateUtil.getStrToDate(todayDate,format));
        if (monthDate != null && !monthDate.equals("")) {
            sql.append(" and g.addDate <= ? ");
            values.add(DateUtil.getStrToDate(monthDate,format));
        }

        return this.getCount(sql.toString(), values.toArray());
    }

    @Override
    public Long getDateReplyCount(Long[] columnIds, String Date, Long siteId, String monthDate) {
        StringBuffer sql = new StringBuffer();
        String todayDate = Date;
        List<Object> values = new ArrayList<Object>();
        sql.append(" from BaseContentEO c,GuestBookEO g where g.baseContentId=c.id and c.isPublish=1");
        if (columnIds != null && columnIds.length > 0) {
            if(columnIds.length==1){
                sql.append(" and c.columnId =? ");
                values.add(columnIds[0]);
            }else{
                String columnIdStr="";
                for(int i=0; i<columnIds.length-1;i++){
                    columnIdStr+=columnIds[i]+",";
                }
                columnIdStr+=columnIds[columnIds.length-1];
                sql.append(" and c.columnId in("+columnIdStr+ ")");
            }
        }
        if (siteId != null) {
            sql.append(" and c.siteId=? ");
            values.add(siteId);
        }
        sql.append("and g.dealStatus in('replyed','handled') and g.recordStatus=? and c.recordStatus=? " +
                " and g.replyDate >=? ");
        values.add(AMockEntity.RecordStatus.Normal.toString());
        values.add(AMockEntity.RecordStatus.Normal.toString());
        values.add(DateUtil.getStrToDate(todayDate,format));
        if (monthDate != null && !monthDate.equals("")) {
            sql.append(" and g.replyDate <= ? ");
            values.add(DateUtil.getStrToDate(monthDate,format));
        }

        return this.getCount(sql.toString(), values.toArray());
    }

    @Override
    public Long getDateHandingCount(Long[] columnIds, String Date, Long siteId, String monthDate) {
        StringBuffer sql = new StringBuffer();
        String todayDate = Date;
        List<Object> values = new ArrayList<Object>();
        sql.append(" from BaseContentEO c,GuestBookEO g where g.baseContentId=c.id and c.isPublish=1");
        if (columnIds != null && columnIds.length > 0) {
            if(columnIds.length==1){
                sql.append(" and c.columnId =? ");
                values.add(columnIds[0]);
            }else{
                String columnIdStr="";
                for(int i=0; i<columnIds.length-1;i++){
                    columnIdStr+=columnIds[i]+",";
                }
                columnIdStr+=columnIds[columnIds.length-1];
                sql.append(" and c.columnId in("+columnIdStr+ ")");
            }
        }
        if (siteId != null) {
            sql.append(" and c.siteId=? ");
            values.add(siteId);
        }
        sql.append("and g.dealStatus='handling' and g.recordStatus=? and c.recordStatus=? ");
        values.add(AMockEntity.RecordStatus.Normal.toString());
        values.add(AMockEntity.RecordStatus.Normal.toString());

        if (todayDate != null && !todayDate.equals("")) {
            sql.append(" and g.replyDate >=? ");
            values.add(DateUtil.getStrToDate(todayDate,format));
        }
        if (monthDate != null && !monthDate.equals("")) {
            sql.append(" and g.replyDate <= ? ");
            values.add(DateUtil.getStrToDate(monthDate,format));
        }

        return this.getCount(sql.toString(), values.toArray());
    }

    @Override
    public List<GuestBookSearchVO> getAllGuestBook() {
        StringBuffer hql = new StringBuffer(" select g.id as id, b.id as baseContentId,b.title as title,b.publishDate as publishDate")
                .append(",b.siteId as siteId,b.columnId as columnId,g.guestBookContent as guestBookContent,g.classCode as classCode")
                .append(" from BaseContentEO b,GuestBookEO g where b.id=g.baseContentId and g.isPublic=1 and b.isPublish=1 ")
                .append(" and b.recordStatus='" + AMockEntity.RecordStatus.Normal.toString() + "'");
        List<GuestBookSearchVO> list = (List<GuestBookSearchVO>) getBeansByHql(hql.toString(), new Object[]{}, GuestBookSearchVO.class);
        return list;
    }

    @Override
    public List<GuestListVO> replyOrderByOrgan(StatisticsQueryVO vo) {
        StringBuffer sql = new StringBuffer("select count(*) as dealCount,g.receiveId as organId from BaseContentEO c, GuestBookEO g ")
                .append(" where c.id=g.baseContentId and c.isPublish=1 and c.siteId=?" );
        List<Object> values = new ArrayList<Object>();
        values.add(vo.getSiteId());
        if (!StringUtils.isEmpty(vo.getStartDate())) {
            sql.append("  and c.publishDate>=? ");
            values.add(DateUtil.getStrToDate(vo.getStartDate(),format));
        }
        if (!StringUtils.isEmpty(vo.getEndDate())) {
            sql.append("  and c.publishDate<=? ");
            values.add(DateUtil.getStrToDate(vo.getEndDate(),format));
        }
        if (!StringUtils.isEmpty(vo.getColumnIds())) {
            sql.append(" and c.columnId in (" + vo.getColumnIds() + ")");
        }
        sql.append(" and c.recordStatus='Normal' and  g.dealStatus in('handled','replyed') and g.receiveId is not null group by g.receiveId order by count(*) desc");

        return (List<GuestListVO>) getBeansByHql(sql.toString(), values.toArray(), GuestListVO.class);
    }

    @Override
    public GuestBookEditVO searchEO(String randomCode, String docNum, Long siteId) {
        StringBuffer hql = new StringBuffer("select c.title as title,c.publishDate as publishDate,c.createDate as createDate,c.isPublish as isPublish,c.columnId as columnId,c.siteId as siteId")
                .append(",c.id as baseContentId,g.id as id,g.responseContent as responseContent,g.guestBookContent as guestBookContent,g.isResponse as isResponse,g.addDate as addDate")
                .append(",g.resourceType as resourceType,g.openId as openId,g.personIp as personIp,g.personName as personName,g.replyDate as replyDate,g.dealStatus as dealStatus")
                .append(",g.userName as userName,g.commentCode as commentCode,g.dealStatus as dealStatus,g.docNum as docNum,g.personPhone as personPhone,g.receiveName as receiveName")
                .append(",g.receiveId as receiveId,g.receiveUserCode as receiveUserCode,g.recType as recType,g.classCode as classCode,g.randomCode as randomCode")
                .append(",g.replyUnitId as replyUnitId,g.replyUnitName as replyUnitName,g.isPublic as isPublic,g.isPublicInfo as isPublicInfo")
                .append(" from BaseContentEO c,GuestBookEO g where g.baseContentId=c.id and c.recordStatus='" + AMockEntity.RecordStatus.Normal.toString() + "'");

        if (!StringUtils.isEmpty(randomCode)) {
            hql.append(" and g.randomCode='" + randomCode + "'");
        }

        if (!StringUtils.isEmpty(docNum)) {
            hql.append(" and g.docNum='" + docNum + "'");
        }
        if (siteId != null) {
            hql.append(" and c.siteId=" + siteId);
        }
        List<GuestBookEditVO> list = (List<GuestBookEditVO>) getBeansByHql(hql.toString(), new Object[]{}, GuestBookEditVO.class);
        GuestBookEditVO vo = null;
        if (list != null && list.size() > 0) {
            vo = list.get(0);
        }
        return vo;
    }

    @Override
    public List<GuestBookEditVO> listGuestBook(Long siteId, String columnIds, Integer isReply, String createUserId, Integer num) {
        StringBuffer hql = new StringBuffer("select c.title as title,c.publishDate as publishDate,c.createDate as createDate,c.isPublish as isPublish")
                .append(",c.id as baseContentId,c.siteId as siteId,c.columnId as columnId,c.hit as hit,g.id as id,g.responseContent as responseContent")
                .append(",g.personIp as personIp,g.personName as personName,g.replyDate as replyDate,g.userId as userId,g.userName as userName")
                .append(",g.dealStatus as dealStatus,g.docNum as docNum")
                .append(",g.recType as recType,g.receiveId as receiveId,g.receiveUserCode as receiveUserCode,g.commentCode as commentCode")
                .append(",g.guestBookContent as guestBookContent,g.replyUnitId as replyUnitId,g.replyUnitName as replyUnitName")
                .append(" from BaseContentEO c,GuestBookEO g where g.baseContentId=c.id and c.recordStatus=:recordStatus ");

        hql.append("  and c.siteId=:siteId and c.isPublish=1 and g.isPublic=1");

        if (isReply != null && isReply == 0) {
            hql.append(" and g.dealStatus not in('handled','replyed') ");
        }

        if (isReply != null && isReply == 1) {
            hql.append(" and g.dealStatus in('handled','replyed') ");
        }
        if (isReply != null && isReply == 2) {
            hql.append(" and g.dealStatus in('handled','replyed','handling') ");
        }
        if (!StringUtils.isEmpty(createUserId)) {
            hql.append(" and g.createUserId in(" + createUserId + ")");
        }
        if (!StringUtils.isEmpty(columnIds)) {
            hql.append(" and c.columnId in(" + columnIds + ")");
        }
        hql.append(" order by g.addDate desc,g.docNum desc ");
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("recordStatus", AMockEntity.RecordStatus.Normal.toString());
        map.put("siteId", siteId);
        return (List<GuestBookEditVO>) getBeansByHql(hql.toString(), map, GuestBookEditVO.class, num);
    }

    @Override
    public Long getSelectNum(Long siteId, Long[] columnIds) {
        StringBuffer hql = new StringBuffer(" from cms_base_content c,guestbook_table g where g.base_content_id=c.id")
                .append(" and c.is_publish=1 and g.is_public=1 and c.record_status='" + AMockEntity.RecordStatus.Normal.toString() + "'");
        Map map = new HashMap();
        if (columnIds != null && columnIds.length > 0) {
            hql.append(" and c.column_id in(:ids) ");
            map.put("ids", columnIds);
        }
        if (!AppUtil.isEmpty(siteId)) {
            hql.append(" and c.site_id=:siteId ");
            map.put("siteId", siteId);
        }
        return getCountBySql(hql.toString(), map);
    }

    @Override
    public Long getUnReadNum(Long siteId, Long columnId, Long createUserId) {
        StringBuffer hql = new StringBuffer(" from cms_base_content c,guestbook_table g where g.base_content_id=c.id")
                .append(" and c.record_status='" + AMockEntity.RecordStatus.Normal.toString() + "' and g.is_read=0 ");
        Map map = new HashMap();
        if (columnId != null) {
            hql.append(" and c.column_id=:columnId ");
            map.put("columnId", columnId);
        }
        if (!AppUtil.isEmpty(siteId)) {
            hql.append(" and c.site_id=:siteId ");
            map.put("siteId", siteId);
        }
        if (!AppUtil.isEmpty(createUserId)) {
            hql.append(" and c.create_user_id=:createUserId ");
            map.put("createUserId", createUserId);
        }
        return getCountBySql(hql.toString(), map);
    }

    public List<GuestBookEditVO> getFromSite(Long siteId, Long recUnitId, Integer num) {
        StringBuffer hql = new StringBuffer("select c.title as title,c.publishDate as publishDate,c.createDate as createDate,c.isPublish as isPublish")
                .append(",c.id as baseContentId,c.siteId as siteId,c.columnId as columnId,c.hit as hit,g.id as id,g.responseContent as responseContent")
                .append(",g.personIp as personIp,g.personName as personName,g.replyDate as replyDate,g.userId as userId,g.userName as userName")
                .append(",g.dealStatus as dealStatus,g.docNum as docNum")
                .append(",g.recType as recType,g.receiveId as receiveId,g.receiveUserCode as receiveUserCode,g.commentCode as commentCode")
                .append(",g.guestBookContent as guestBookContent,g.replyUnitId as replyUnitId,g.replyUnitName as replyUnitName")
                .append(" from BaseContentEO c,GuestBookEO g where g.baseContentId=c.id and c.recordStatus=:recordStatus ");
        Map map = new HashMap();
        map.put("recordStatus", AMockEntity.RecordStatus.Normal.toString());
        if (siteId != null) {
            hql.append(" and c.siteId=:siteId");
            map.put("siteId", siteId);
        }
        if (recUnitId != null) {
            hql.append(" and g.receiveId=:recUnitId");
            map.put("recUnitId", recUnitId);
        }
        return (List<GuestBookEditVO>) getBeansByHql(hql.toString(), map, GuestBookEditVO.class, num);

    }

    @Override
    public GuestBookEO getGuestBookByContentId(Long contentId) {
        String hql = "from GuestBookEO where baseContentId = ?";
        return getEntityByHql(hql,new Object[]{contentId});
    }

    @Override
    public Pagination getMobilePage2(ContentPageVO pageVO) {
        StringBuffer hql = new StringBuffer("select c.title as title,c.publishDate as publishDate,c.isPublish as isPublish")
                .append(",c.id as id,c.columnId as columnId,c.siteId as siteId,c.typeCode as typeCode,c.imageLink as imageLink");
        if(pageVO.getOrderType()==null){
            hql.append(" ,g.addDate as createDate");
        }else if(pageVO.getOrderType()==1){
            hql.append(" ,g.replyDate as createDate");
        }else if(pageVO.getOrderType()==2){
            hql.append(" ,c.publishDate as createDate");
        }else{
            hql.append(" ,g.addDate as createDate");
        }

        hql.append(" from BaseContentEO c,GuestBookEO g where g.baseContentId=c.id and c.recordStatus=? ")
                .append("and c.isPublish=1  and g.isPublic=1 ");

        List<Object> values = new ArrayList<Object>();
        values.add( AMockEntity.RecordStatus.Normal.toString());
        if (!AppUtil.isEmpty(pageVO.getTitle())) {
            hql.append(" and c.title like ?");
            values.add("%".concat(pageVO.getTitle().trim()).concat("%"));
        }
        if (!AppUtil.isEmpty(pageVO.getSiteId())) {
            hql.append(" and c.siteId=?");
            values.add(pageVO.getSiteId());
        }
        if (!AppUtil.isEmpty(pageVO.getColumnId())) {
            hql.append(" and c.columnId=?");
            values.add(pageVO.getColumnId());
        }
        if (!AppUtil.isEmpty(pageVO.getCondition()) && pageVO.getCondition().equals("reply")) {
            hql.append(" and c." + pageVO.getCondition().trim() + "=?");
            values.add(pageVO.getStatus());
        }
        if (!AppUtil.isEmpty(pageVO.getTypeCode())) {
            hql.append(" and c.typeCode=?");
            values.add( pageVO.getTypeCode());
        }

        if (!AppUtil.isEmpty(pageVO.getIsReply())) {
            if (pageVO.getIsReply() == 1) {
                hql.append(" and g.dealStatus in ('handled','replyed')");
            } else if (pageVO.getIsReply() == 2) {
                hql.append(" and ( g.dealStatus is null or g.dealStatus not in('handled','replyed') )");
            }else if(pageVO.getIsReply()==3){
                hql.append(" and g.dealStatus in('handled','replyed','handling') ");
            }
        }
        if(pageVO.getOrderType()==null){
            hql.append(" order by  g.addDate desc,g.docNum desc");
        }else if(pageVO.getOrderType()==1){
            hql.append(" order by  g.replyDate desc,g.docNum desc");
        }else if(pageVO.getOrderType()==2){
            hql.append(" order by  c.publishDate desc,g.docNum desc");
        }else{
            hql.append(" order by  g.addDate desc,g.docNum desc");
        }

        Pagination page = getPagination(pageVO.getPageIndex(), pageVO.getPageSize(), hql.toString(), values.toArray(), BaseContentEO.class);
        return page;
    }


}
