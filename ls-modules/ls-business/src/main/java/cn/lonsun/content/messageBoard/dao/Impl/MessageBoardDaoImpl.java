package cn.lonsun.content.messageBoard.dao.Impl;

import cn.lonsun.common.util.AppUtil;
import cn.lonsun.content.internal.entity.BaseContentEO;
import cn.lonsun.content.messageBoard.controller.internal.entity.MessageBoardEO;
import cn.lonsun.content.messageBoard.controller.internal.entity.MessageBoardForwardEO;
import cn.lonsun.content.messageBoard.controller.vo.MessageBoardEditVO;
import cn.lonsun.content.messageBoard.controller.vo.MessageBoardPageVO;
import cn.lonsun.content.messageBoard.controller.vo.MessageBoardVO;
import cn.lonsun.content.messageBoard.dao.IMessageBoardDao;
import cn.lonsun.content.vo.*;
import cn.lonsun.core.base.dao.impl.MockDao;
import cn.lonsun.core.base.entity.AMockEntity;
import cn.lonsun.core.util.Pagination;
import cn.lonsun.statistics.ContentChartVO;
import cn.lonsun.statistics.MessageBoardListVO;
import cn.lonsun.statistics.StatisticsQueryVO;
import cn.lonsun.system.role.internal.util.RoleAuthUtil;
import cn.lonsun.system.sitechart.vo.KVL;
import cn.lonsun.system.sitechart.vo.KVP;
import cn.lonsun.util.DateUtil;
import cn.lonsun.util.LoginPersonUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Repository;

import java.text.SimpleDateFormat;
import java.util.*;


@Repository("messageBoardDao")
public class MessageBoardDaoImpl extends MockDao<MessageBoardEO> implements IMessageBoardDao {

    private static SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    @Override
    public Pagination getPage(MessageBoardPageVO pageVO) {
        StringBuffer hql = new StringBuffer("select c.title as title,c.publishDate as publishDate,c.createDate as createDate, c.isPublish as isPublish")
                .append(",c.columnId as columnId,c.siteId as siteId,c.id as baseContentId,m.id as id,m.messageBoardContent as messageBoardContent,m.addDate as addDate")
                .append(",m.knowledgeBaseId as knowledgeBaseId,m.resourceType as resourceType,m.openId as openId,m.personPhone as personPhone,m.personIp as personIp,m.personName as personName")
                .append(",m.attachName as attachName,m.attachId as attachId,m.docNum as docNum,m.dealStatus as dealStatus")
                .append(",m.classCode as classCode,m.randomCode as randomCode,m.email as email,m.address as address,m.commentCode as commentCode")
                .append(",m.isPublic as isPublic,m.isPublicInfo as isPublicInfo")
                .append(" from BaseContentEO c,MessageBoardEO m where m.baseContentId=c.id and c.recordStatus=? and m.recordStatus=? ");

        List<Object> values = new ArrayList<Object>();
        values.add(AMockEntity.RecordStatus.Normal.toString());
        values.add(AMockEntity.RecordStatus.Normal.toString());

        if (pageVO.getIsPublish() != null) {
            hql.append(" and c.isPublish=?");
            values.add(pageVO.getIsPublish());
        }

        if (!StringUtils.isEmpty(pageVO.getDealStatus())) {
            if(pageVO.getDealStatus().equals("replyed")||pageVO.getDealStatus().equals("handled")||pageVO.getDealStatus().equals("handling")) {
                hql.append(" and m.dealStatus=?");
                values.add(pageVO.getDealStatus());
            }else{
                hql.append(" and m.dealStatus is null");
            }
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
            hql.append(" and m.type =?");
            values.add(pageVO.getType());
        }

        if (null != pageVO.getCreateUserId()) {
            hql.append(" and m.createUserId =?");
            values.add(pageVO.getCreateUserId());
        }

        if (!AppUtil.isEmpty(pageVO.getTitle())) {
            hql.append(" and c.title like ? ");
            values.add("%" + pageVO.getTitle() + "%");
        }

        if (!AppUtil.isEmpty(pageVO.getTypeCode())) {
            hql.append(" and c.typeCode =?");
            values.add(pageVO.getTypeCode());
        }
        if (!AppUtil.isEmpty(pageVO.getOpenId())) {
            hql.append(" and m.openId =?");
            values.add(pageVO.getOpenId());
        }
        if (!AppUtil.isEmpty(pageVO.getSt())) {
            hql.append(" and m.addDate>=?");
            values.add(pageVO.getSt());
        }
        if (!AppUtil.isEmpty(pageVO.getEd())) {
            hql.append(" and m.addDate<?");
            values.add(pageVO.getEd());
        }

        if (!AppUtil.isEmpty(pageVO.getSatisfactoryType())) {
            if (pageVO.getSatisfactoryType().intValue() == 1) {//满意
                hql.append("and m.commentCode = 'satisfactory'");
            }else {//不满意
                hql.append("and m.commentCode != 'satisfactory'");
            }
        }

        if ((RoleAuthUtil.isCurUserColumnAdmin(pageVO.getColumnId()) || LoginPersonUtil.isSiteAdmin() || LoginPersonUtil.isSuperAdmin()) && pageVO.getIsAssign() != null && pageVO.getIsAssign() == 0) {
            hql.append(" and c.isPublish=0 and  m.forwardCount > 0 ");
        }

        if ((RoleAuthUtil.isCurUserColumnAdmin(pageVO.getColumnId()) || LoginPersonUtil.isSiteAdmin() || LoginPersonUtil.isSuperAdmin()) && pageVO.getIsAssign() != null && pageVO.getIsAssign() == 1) {
            hql.append(" and c.isPublish=0 and (m.forwardCount is null or m.forwardCount < 1)");
        }

        if (!AppUtil.isEmpty(pageVO.getReceiveUnitId())) {
            hql.append(" and (m.id in(select f.messageBoardId from MessageBoardForwardEO f where f.receiveOrganId =? and f.operationStatus = ? and f.recordStatus='Normal'))");
            values.add(pageVO.getReceiveUnitId());
            values.add(MessageBoardForwardEO.OperationStatus.Normal.toString());
        }
        if (!AppUtil.isEmpty(pageVO.getReceiveUserCode())) {
            hql.append(" and m.receiveUserCode =?");
            values.add(pageVO.getReceiveUserCode());
        }
        if (!StringUtils.isEmpty(pageVO.getClassCode())) {
            hql.append(" and m.classCode =?");
            values.add(pageVO.getClassCode());
        }

        hql.append(" order by m.createDate desc");

        Pagination page = getPagination(pageVO.getPageIndex(), pageVO.getPageSize(), hql.toString(), values.toArray(), MessageBoardEditVO.class);

        return page;
    }

    @Override
    public Long countMessageBoard(Long siteId, String columnIds, Date startDate, Integer isPublish, Integer isResponse) {
        StringBuffer hql = new StringBuffer(" select m.id as id, b.id as baseContentId ")
                .append(" from BaseContentEO b,MessageBoardEO m where b.id=m.baseContentId ");
               // .append(" and b.recordStatus='" + AMockEntity.RecordStatus.Normal.toString() + "'");
        List<Object> values = new ArrayList<Object>();
        if (siteId != null) {
            hql.append(" and b.siteId=?");
            values.add(siteId);
        }
        if (!StringUtils.isEmpty(columnIds)) {
            hql.append(" and b.columnId in(" + columnIds + ")");
        }
        if (!AppUtil.isEmpty(startDate)) {
            hql.append(" and m.addDate>=?");
            values.add(startDate);
        }
        if (isPublish != null) {
            hql.append(" and b.isPublish=?");
            values.add(isPublish);
        }
        Long count = getCount(hql.toString(), values.toArray());
        if (count == null) {
            count = 0L;
        }
        return count;
    }

    @Override
    public List<KVP> getSatisfactoryTypeCount(MessageBoardPageVO pageVO) {
        List<Object> values = new ArrayList<Object>();
        //按类别
        StringBuffer hql = new StringBuffer(" select count(1) as value,m.classCode as name ")
                .append(" from BaseContentEO b,MessageBoardEO m where b.id=m.baseContentId and b.recordStatus=? and m.recordStatus=? ");
        values.add(AMockEntity.RecordStatus.Normal.toString());
        values.add(AMockEntity.RecordStatus.Normal.toString());

        if (!AppUtil.isEmpty(pageVO.getSatisfactoryType())) {
            if (pageVO.getSatisfactoryType().intValue() == 1) {//满意
                hql.append("and m.commentCode = 'satisfactory'");
            }else {//不满意
                hql.append("and m.commentCode != 'satisfactory'");
            }
        }

        if (!AppUtil.isEmpty(pageVO.getColumnId())) {
            hql.append(" and b.columnId=?");
            values.add(pageVO.getColumnId());
        }

        if (!AppUtil.isEmpty(pageVO.getSt())) {
            hql.append(" and m.addDate>=?");
            values.add(pageVO.getSt());
        }
        if (!AppUtil.isEmpty(pageVO.getEd())) {
            hql.append(" and m.addDate<?");
            values.add(pageVO.getEd());
        }
        //判断类别,加入类别限制
        if (!AppUtil.isEmpty(pageVO.getClassCode())) {
            hql.append(" and m.classCode = ?");
            values.add(pageVO.getClassCode());
        }
        //判断单位,加入单位限制
        if (!AppUtil.isEmpty(pageVO.getReceiveUnitId())) {
            hql.append(" and (m.id in(select f.messageBoardId from MessageBoardForwardEO f where f.receiveOrganId =? and f.operationStatus = ? and f.recordStatus='Normal'))");
            values.add(pageVO.getReceiveUnitId());
            values.add(MessageBoardForwardEO.OperationStatus.Normal.toString());
        }

        hql.append(" group by m.classCode");
        return (List<KVP>) getBeansByHql(hql.toString(), values.toArray(), KVP.class);
    }

    @Override
    public List<KVL> getSatisfactoryUnitCount(MessageBoardPageVO pageVO) {
        List<Object> values = new ArrayList<Object>();
        //按单位
        StringBuffer hql = new StringBuffer(" select count(1) as value,f.receiveOrganId as name ")
                .append(" from BaseContentEO b,MessageBoardEO m,MessageBoardForwardEO f where b.id=m.baseContentId and m.id=f.messageBoardId  and b.recordStatus=? and m.recordStatus=? ");
        values.add(AMockEntity.RecordStatus.Normal.toString());
        values.add(AMockEntity.RecordStatus.Normal.toString());

        if (!AppUtil.isEmpty(pageVO.getSatisfactoryType())) {
            if (pageVO.getSatisfactoryType().intValue() == 1) {//满意
                hql.append("and m.commentCode = 'satisfactory'");
            }else {//不满意
                hql.append("and m.commentCode != 'satisfactory'");
            }
        }

        if (!AppUtil.isEmpty(pageVO.getColumnId())) {
            hql.append(" and b.columnId=?");
            values.add(pageVO.getColumnId());
        }

        if (!AppUtil.isEmpty(pageVO.getSt())) {
            hql.append(" and m.addDate>=?");
            values.add(pageVO.getSt());
        }
        if (!AppUtil.isEmpty(pageVO.getEd())) {
            hql.append(" and m.addDate<?");
            values.add(pageVO.getEd());
        }
        //判断类别,加入类别限制
        if (!AppUtil.isEmpty(pageVO.getClassCode())) {
            hql.append(" and m.classCode = ?");
            values.add(pageVO.getClassCode());
        }
        //判断单位,加入单位限制
        if (!AppUtil.isEmpty(pageVO.getReceiveUnitId())) {
            hql.append(" and f.receiveOrganId = ?");
            values.add(pageVO.getReceiveUnitId());
        }

        hql.append(" and f.receiveOrganId is not null and f.operationStatus = 'Normal' and f.recordStatus='Normal' group by f.receiveOrganId");
        return (List<KVL>) getBeansByHql(hql.toString(), values.toArray(), KVL.class);
    }

    @Override
    public Long getCount(String type, Long columnId) {
        StringBuffer hql = new StringBuffer(" select m.id as id, b.id as baseContentId ")
                .append(" from BaseContentEO b,MessageBoardEO m where b.id=m.baseContentId ")
                .append(" and b.recordStatus='" + AMockEntity.RecordStatus.Normal.toString() + "'");
        List<Object> values = new ArrayList<Object>();
        if (type == "assigned") {
            hql.append(" and b.isPublish=0 and m.forwardCount > 0");
        }

        if (type == "noAssign") {
            hql.append(" and b.isPublish=0 and (m.forwardCount is null or m.forwardCount < 1)");
        }

        if (!AppUtil.isEmpty(columnId)) {
            hql.append(" and b.columnId=?");
            values.add(columnId);
        }

        Long count = getCount(hql.toString(), values.toArray());
        if (count == null) {
            count = 0L;
        }
        return count;
    }

    @Override
    public List<ContentChartVO> getStatisticsList(ContentChartQueryVO queryVO) {
        StringBuffer sql = new StringBuffer("select  f.receive_organ_id as organId, 100*(count(r.create_organ_id)/count(f.receive_organ_id)) rate ")
                .append(" from CMS_MESSAGE_BOARD_FORWARD f")
                .append(" left join (select * from  cms_message_board_reply y  where deal_status in('handled','replyed') and  record_status='Normal') r")
                .append(" on f.message_board_id =r.message_board_id")
                .append(" left join  cms_message_board m on f.message_board_id = m.id ")
                .append(" left join  cms_base_content b on m.base_content_id = b.id")
                .append(" where  b.record_status='" + AMockEntity.RecordStatus.Normal.toString() + "'")
                .append(" and f.operation_Status='" + AMockEntity.RecordStatus.Normal.toString() + "'")
                .append(" and b.type_code='" + BaseContentEO.TypeCode.messageBoard.toString() + "' and b.is_publish=1")
                .append(" and f.receive_organ_id = r.create_organ_id");
        List<Object> values = new ArrayList<Object>();

        sql.append(" and b.site_id=?");
        values.add(queryVO.getSiteId());

        if (!StringUtils.isEmpty(queryVO.getStartStr()) && !StringUtils.isEmpty(queryVO.getEndStr())) {
            sql.append(" and b.publish_date>=? and b.publish_date<=? ");
            values.add(DateUtil.getStrToDate(queryVO.getStartStr(),format));
            values.add(DateUtil.getStrToDate(queryVO.getEndStr(),format));
        }
        sql.append(" group by f.receive_organ_id order by count(r.create_organ_id)/count(f.receive_organ_id) desc");

        List<String> fields = new ArrayList<String>();
        fields.add("rate");
        fields.add("organId");
        String[] arr = new String[fields.size()];
        return (List<ContentChartVO>) getBeansBySql(sql.toString(), values.toArray(), ContentChartVO.class, fields.toArray(arr));
    }

    @Override
    public List<ContentChartVO> getSatisfactoryList(ContentChartQueryVO queryVO) {
        StringBuffer sql = new StringBuffer("select  f.receive_organ_id as organId, 100*((count(f.receive_organ_id)-count(r.create_organ_id))/count(f.receive_organ_id)) rate ")
                .append(" from (select f.* from CMS_MESSAGE_BOARD_FORWARD f left join CMS_MESSAGE_BOARD_REPLY r on f.message_board_id = r.message_board_id where r.deal_status in('handled','replyed') and f.receive_organ_id = r.create_organ_id and f.operation_Status='Normal') f")
                .append(" left join (select y.* from  CMS_MESSAGE_BOARD_REPLY y left join CMS_MESSAGE_BOARD_FORWARD f on f.message_board_id = y.message_board_id where y.comment_code='unsatisfactory' and y.deal_status in('handled','replyed') and f.receive_organ_id = y.create_organ_id and f.record_status='Normal') r on f.message_board_id = r.message_board_id")
                .append(" left join  cms_message_board m on f.message_board_id = m.id ")
                .append(" left join  cms_base_content b on m.base_content_id = b.id")
                .append(" where  b.record_status='" + AMockEntity.RecordStatus.Normal.toString() + "' and  f.operation_Status='" + AMockEntity.RecordStatus.Normal.toString() + "'")
                .append(" and b.type_code='" + BaseContentEO.TypeCode.messageBoard.toString() + "' and b.is_publish=1");
        List<Object> values = new ArrayList<Object>();

        sql.append(" and b.site_id=?");
        values.add(queryVO.getSiteId());

        if (!StringUtils.isEmpty(queryVO.getStartStr()) && !StringUtils.isEmpty(queryVO.getEndStr())) {
            sql.append(" and b.publish_date>? and b.publish_date<=?");
            values.add(DateUtil.getStrToDate(queryVO.getStartStr(),format));
            values.add(DateUtil.getStrToDate(queryVO.getEndStr(),format));
        }
        sql.append(" group by f.receive_organ_id order by (count(f.receive_organ_id)-count(r.create_organ_id))/count(f.receive_organ_id) desc");

        List<String> fields = new ArrayList<String>();
        fields.add("rate");
        fields.add("organId");
        String[] arr = new String[fields.size()];
        return (List<ContentChartVO>) getBeansBySql(sql.toString(), values.toArray(), ContentChartVO.class, fields.toArray(arr));
    }

    @Override
    public List<MessageBoardEditVO> getColumnIdByTypeCode() {
        StringBuffer hql = new StringBuffer("select columnId as columnId from BaseContentEO  where typeCode='messageBoard' group by columnId ");

        List<MessageBoardEditVO> list = (List<MessageBoardEditVO>) getBeansByHql(hql.toString(), new Object[]{}, MessageBoardEditVO.class);

        return list;
    }


    @Override
    public GuestBookNumVO getAnalys(Long[] columnIds, Long siteId) {
        StringBuffer sql = new StringBuffer();
        String cs = "";
        if (columnIds != null && columnIds.length > 0) {
            List list = Arrays.asList(columnIds);
            cs = StringUtils.join(list, ",");
        }
//        sql.append("select sum(total)amount,sum(reply)replyNum,round(case when sum(total)=0 then 0 " +
//                "else 100*(sum(reply)/sum(total))end,2)replyRate from (" +
//                "select count(*)as total,0 as reply from cms_base_content c,CMS_MESSAGE_BOARD g where g.base_content_id=c.id and c.is_publish=1");
//        List<Object> values = new ArrayList<Object>();
//        if (!StringUtils.isEmpty(cs)) {
//            sql.append(" and c.column_id in(" + cs + ") ");
//        }
//        if (siteId != null) {
//            sql.append(" and c.site_id=?");
//            values.add(siteId);
//        }
//        sql.append("and c.record_status='" + AMockEntity.RecordStatus.Normal.toString() +
//                "' union all select 0,count(*) from cms_base_content c,CMS_MESSAGE_BOARD g where g.base_content_id=c.id and c.is_publish=1");
//        if (!StringUtils.isEmpty(cs)) {
//            sql.append(" and c.column_id in(" + cs + ") ");
//        }
//        if (siteId != null) {
//            sql.append(" and c.site_id=?");
//            values.add(siteId);
//        }
//        sql.append("and g.deal_status in('replyed','handled') and c.record_status='" + AMockEntity.RecordStatus.Normal.toString() + "') cc");

        sql.append(" SELECT count(*) AS amount,sum(CASE WHEN g.deal_status IN ('replyed', 'handled') THEN 1 ELSE 0 END) AS replyNum,0 as replyRate")
                .append(" FROM cms_base_content c,CMS_MESSAGE_BOARD g WHERE g.base_content_id = c.id AND c.is_publish = 1 and c.record_status='"+ AMockEntity.RecordStatus.Normal.toString() +"'");
        List<Object> values = new ArrayList<Object>();
        if (!StringUtils.isEmpty(cs)) {
            sql.append(" and c.column_id in(" + cs + ") ");
        }
        if (siteId != null) {
            sql.append(" and c.site_id=?");
            values.add(siteId);
        }
        GuestBookNumVO gv = (GuestBookNumVO) this.getBeanBySql(sql.toString(), values.toArray(), GuestBookNumVO.class);
        return gv;
    }

    @Override
    public List<GuestBookTypeVO> getAnalysType(Long[] columnIds, Long siteId, Integer isReply) {
        StringBuffer sql = new StringBuffer();
        sql.append("select count(*)as count,g.class_code as classCode from cms_base_content c,CMS_MESSAGE_BOARD g where g.base_content_id=c.id and c.is_publish=1" +
                " and g.class_code is not null ");
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
    public Pagination getMemberPage(MessageBoardPageVO pageVO) {

        StringBuffer hql = new StringBuffer("select c.title as title,c.publishDate as publishDate,c.createDate as createDate, c.isPublish as isPublish,c.columnId as columnId,c.siteId as siteId")
                .append(",c.id as baseContentId,m.id as id,m.messageBoardContent as messageBoardContent,m.addDate as addDate")
                .append(",m.resourceType as resourceType,m.openId as openId,m.personIp as personIp,m.personName as personName")
                .append(",m.docNum as docNum,m.attachId as attachId,m.dealStatus as dealStatus")
                .append(",m.classCode as classCode,m.randomCode as randomCode")
                .append(",m.isPublic as isPublic,m.isPublicInfo as isPublicInfo,m.email as email,m.address as address")
                .append(" from BaseContentEO c,MessageBoardEO m where m.baseContentId=c.id and c.recordStatus=? and m.recordStatus=? ");

        List<Object> values = new ArrayList<Object>();
        values.add(AMockEntity.RecordStatus.Normal.toString());
        values.add(AMockEntity.RecordStatus.Normal.toString());

        if (pageVO.getIsPublish() != null) {
            hql.append(" and c.isPublish=?");
            values.add(pageVO.getIsPublish());
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
            hql.append(" and m.type =?");
            values.add(pageVO.getType());
        }

        if (null != pageVO.getCreateUserId()) {
            hql.append(" and m.createUserId =?");
            values.add(pageVO.getCreateUserId());
        }

        if (!AppUtil.isEmpty(pageVO.getTitle())) {
            hql.append(" and c.title like ? ");
            values.add("%" + pageVO.getTitle() + "%");
        }
        if (!AppUtil.isEmpty(pageVO.getTypeCode())) {
            hql.append(" and c.typeCode =?");
            values.add(pageVO.getTypeCode());
        }
        if (!AppUtil.isEmpty(pageVO.getOpenId())) {
            hql.append(" and m.openId =?");
            values.add(pageVO.getOpenId());
        }
        if (!AppUtil.isEmpty(pageVO.getSt())) {
            hql.append(" and m.addDate>=?");
            values.add(pageVO.getSt());
        }
        if (!AppUtil.isEmpty(pageVO.getEd())) {
            hql.append(" and m.addDate<?");
            values.add(pageVO.getEd());
        }


        if (!AppUtil.isEmpty(pageVO.getReceiveUnitId())) {
            hql.append(" and m.receiveUnitId =?");
            values.add(pageVO.getReceiveUnitId());
        }
        if (!AppUtil.isEmpty(pageVO.getReceiveUserCode())) {
            hql.append(" and m.receiveUserCode =?");
            values.add(pageVO.getReceiveUserCode());
        }
        if (!StringUtils.isEmpty(pageVO.getClassCode())) {
            hql.append(" and m.classCode =?");
            values.add(pageVO.getClassCode());
        }

        hql.append(" order by m.addDate desc");

        Pagination page = getPagination(pageVO.getPageIndex(), pageVO.getPageSize(), hql.toString(), values.toArray(), MessageBoardVO.class);

        return page;
    }

    @Override
    public List<MessageBoardEditVO> listMessageBoard(Long siteId, String columnIds, Integer isReply, String createUserId, Integer num) {
        StringBuffer hql = new StringBuffer("select c.title as title,c.publishDate as publishDate,c.createDate as createDate,c.isPublish as isPublish")
                .append(",c.id as baseContentId,c.siteId as siteId,c.columnId as columnId,g.id as id")
                .append(",g.personIp as personIp,g.personName as personName")
                .append(",g.dealStatus as dealStatus,g.docNum as docNum")
                .append(",g.receiveUnitId as receiveUnitId,g.receiveUserCode as receiveUserCode")
                .append(",g.messageBoardContent as messageBoardContent")
                .append(" from BaseContentEO c,MessageBoardEO g where g.baseContentId=c.id and c.recordStatus=:recordStatus ");

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
            hql.append(" and g.createUserId in(:createUserId)");
        }
        if (!StringUtils.isEmpty(columnIds)) {
            hql.append(" and c.columnId in(:columnIds)");
        }
        hql.append(" order by g.addDate desc,g.docNum desc ");
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("recordStatus", AMockEntity.RecordStatus.Normal.toString());
        map.put("siteId", siteId);
        map.put("createUserId", createUserId);
        map.put("columnIds", columnIds);
        return (List<MessageBoardEditVO>) getBeansByHql(hql.toString(), map, MessageBoardEditVO.class, num);
    }

    @Override
    public MessageBoardEditVO searchEO(String randomCode, String docNum, Long siteId) {
        StringBuffer hql = new StringBuffer("select c.title as title,c.publishDate as publishDate,c.createDate as createDate,c.isPublish as isPublish,c.columnId as columnId,c.siteId as siteId")
                .append(",c.id as baseContentId,g.id as id,g.messageBoardContent as messageBoardContent,g.addDate as addDate")
                .append(",g.resourceType as resourceType,g.openId as openId,g.personIp as personIp,g.personName as personName,g.dealStatus as dealStatus")
                .append(",g.dealStatus as dealStatus,g.docNum as docNum,g.personPhone as personPhone")
                .append(",g.classCode as classCode,g.randomCode as randomCode")
                .append(",g.isPublic as isPublic,g.isPublicInfo as isPublicInfo")
                .append(" from BaseContentEO c,MessageBoardEO g where g.baseContentId=c.id ")
                .append(" and c.recordStatus='" + AMockEntity.RecordStatus.Normal.toString() + "'");

        List<Object> values = new ArrayList<Object>();
        if (!StringUtils.isEmpty(randomCode)) {
            hql.append(" and g.randomCode=?");
            values.add(randomCode);
        }

        if (!StringUtils.isEmpty(docNum)) {
            hql.append(" and g.docNum=?");
            values.add(docNum);
        }
        if (siteId != null) {
            hql.append(" and c.siteId=?");
            values.add(siteId);
        }
        List<MessageBoardEditVO> list = (List<MessageBoardEditVO>) getBeansByHql(hql.toString(), values.toArray(), MessageBoardEditVO.class);
        MessageBoardEditVO vo = null;
        if (list != null && list.size() > 0) {
            vo = list.get(0);
        }
        return vo;
    }

    @Override
    public Long getDateTotalCount(Long[] columnIds, String Date, Long siteId, String monthDate) {
        StringBuffer sql = new StringBuffer();
        String todayDate = Date;
        Map<String, Object> map = new HashMap<String, Object>();
        sql.append(" from cms_base_content c,cms_message_board g where g.base_content_id=c.id and c.is_publish=1");
        if (columnIds != null && columnIds.length > 0) {
            sql.append(" and c.column_id in(:ids) ");
        }
        if (siteId != null) {
            sql.append(" and c.site_id=:siteId ");
        }
        sql.append("and g.record_status=:recordStatus and c.record_status=:recordStatus " +
                " and g.add_date >=:todayData");

        if (monthDate != null && !monthDate.equals("")) {
            sql.append(" and g.add_date <=:todayData");
            map.put("monthDate", DateUtil.getStrToDate(monthDate,format));
        }
        map.put("siteId", siteId);
        map.put("ids", columnIds);
        map.put("recordStatus", AMockEntity.RecordStatus.Normal.toString());
        map.put("todayData", DateUtil.getStrToDate(todayDate,format));
        return this.getCountBySql(sql.toString(), map);
    }

    @Override
    public Long getDateReplyCount(Long[] columnIds, String Date, Long siteId, String monthDate) {
        StringBuffer sql = new StringBuffer();
        String todayData = Date;
        Map<String, Object> map = new HashMap<String, Object>();
        sql.append("select * from cms_base_content c,cms_message_board g,cms_message_board_reply r where g.base_content_id=c.id and g.id = r.message_board_id and c.is_publish=1 ");
        if (columnIds != null && columnIds.length > 0) {
            sql.append(" and c.column_id in(:ids) ");
        }
        if (siteId != null) {
            sql.append(" and c.site_id=:siteId ");
        }
        sql.append("and g.deal_status in('replyed','handled') and g.record_status=:recordStatus and c.record_status=:recordStatus " +
                " and r.record_status=:recordStatus  and r.create_date >=:todayData");

        map.put("siteId", siteId);
        map.put("ids", columnIds);
        map.put("recordStatus", AMockEntity.RecordStatus.Normal.toString());
        map.put("todayData", DateUtil.getStrToDate(todayData,format));
        return this.getCountBySql(sql.toString(), map);
    }

    @Override
    public Pagination getMobilePage(MessageBoardPageVO pageVO) {
        StringBuffer hql = new StringBuffer("select c.title as title,c.publishDate as publishDate,c.createDate as createDate,c.isPublish as isPublish,c.columnId as columnId,c.siteId as siteId")
                .append(",c.id as baseContentId,g.id as id,g.messageBoardContent as messageBoardContent,g.addDate as addDate")
                .append(",g.resourceType as resourceType,g.openId as openId,g.personIp as personIp,g.personName as personName")
                .append(",g.receiveUserCode as receiveUserCode,g.classCode as classCode,g.docNum as docNum,g.randomCode as randomCode")
                .append(", g.isPublic as isPublic,g.isPublicInfo as isPublicInfo,g.email as email,g.address as address")
                .append(" from BaseContentEO c,MessageBoardEO g where g.baseContentId=c.id and c.recordStatus=? and g.recordStatus=?");
               /* .append(" and g.isPublic=1 and c.isPublish=1 ");*/

        List<Object> values = new ArrayList<Object>();
        values.add(AMockEntity.RecordStatus.Normal.toString());
        values.add(AMockEntity.RecordStatus.Normal.toString());

        if (!AppUtil.isEmpty(pageVO.getSiteId())) {
            hql.append(" and c.siteId=?");
            values.add(pageVO.getSiteId());
        }
        if (!AppUtil.isEmpty(pageVO.getCreateUserId())) {
            hql.append(" and g.createUserId=?");
            values.add(pageVO.getCreateUserId());
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
            hql.append(" and c.columnId in (" + pageVO.getColumnIds() + ")");
        }

        if (!AppUtil.isEmpty(pageVO.getIsReply())) {
            if (pageVO.getIsReply() == 1) {
                hql.append(" and g.dealStatus in ('handled','replyed')");
            } else if (pageVO.getIsReply() == 2) {
                hql.append(" and( g.dealStatus is null or g.dealStatus not in ('handled','replyed'))");
            }
        }


        hql.append(" order by g.createDate desc");

        Pagination page = getPagination(pageVO.getPageIndex(), pageVO.getPageSize(), hql.toString(), values.toArray(), MessageBoardEditVO.class);
        return page;
    }

    @Override
    public List<MessageBoardEditVO> getMessageBoardBySiteId(Long siteId) {
        StringBuffer hql = new StringBuffer("select c.title as title,c.publishDate as publishDate,c.createDate as createDate,c.isPublish as isPublish")
                .append(",c.id as baseContentId,c.siteId as siteId,c.columnId as columnId,g.id as id")
                .append(",g.personIp as personIp,g.personName as personName")
                .append(",g.dealStatus as dealStatus,g.docNum as docNum")
                .append(",g.receiveUnitId as receiveUnitId,g.receiveUserCode as receiveUserCode")
                .append(",g.messageBoardContent as messageBoardContent")
                .append(" from BaseContentEO c,MessageBoardEO g where g.baseContentId=c.id and c.recordStatus=:recordStatus ");

        hql.append("  and c.siteId=:siteId ");


        hql.append(" order by g.addDate desc,g.docNum desc ");
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("recordStatus", AMockEntity.RecordStatus.Normal.toString());
        map.put("siteId", siteId);
        return (List<MessageBoardEditVO>) getBeansByHql(hql.toString(), map, MessageBoardEditVO.class, null);
    }

    @Override
    public MessageBoardEO getMessageBoardByKnowledgeBaseId(Long knowledgeBaseId) {
        StringBuffer hql = new StringBuffer("from MessageBoardEO g where 1=1 ");

        if (!AppUtil.isEmpty(knowledgeBaseId)) {
            hql.append("  and g.knowledgeBaseId= ").append(knowledgeBaseId);
        }

        return getEntityByHql(hql.toString(),new Object[]{});
    }

    @Override
    public Long getUnReadNum(Long siteId, Long columnId, Long createUserId) {
        StringBuffer hql = new StringBuffer(" from cms_base_content c,cms_message_board g where g.base_content_id=c.id")
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

    @Override
    public Pagination getUnitPage(MessageBoardPageVO pageVO) {
        StringBuffer hql = new StringBuffer("select c.title as title,c.publishDate as publishDate,c.createDate as createDate, c.isPublish as isPublish")
                .append(",c.columnId as columnId,c.siteId as siteId,c.id as baseContentId,m.id as id,m.messageBoardContent as messageBoardContent,m.addDate as addDate")
                .append(",m.knowledgeBaseId as knowledgeBaseId,m.resourceType as resourceType,m.openId as openId,m.personPhone as personPhone,m.personIp as personIp,m.personName as personName")
                .append(",m.attachName as attachName,m.attachId as attachId,m.docNum as docNum,m.dealStatus as dealStatus")
                .append(",m.classCode as classCode,m.randomCode as randomCode,m.email as email,m.address as address,m.commentCode as commentCode")
                .append(",m.isPublic as isPublic,m.isPublicInfo as isPublicInfo")
                .append(" from MessageBoardForwardEO f,MessageBoardEO m,BaseContentEO c where f.messageBoardId = m.id and c.recordStatus=? and  m.baseContentId = c.id and m.recordStatus=?");

        List<Object> values = new ArrayList<Object>();
        values.add(AMockEntity.RecordStatus.Normal.toString());
        values.add(AMockEntity.RecordStatus.Normal.toString());

        if (pageVO.getIsPublish() != null) {
            hql.append(" and c.isPublish=?");
            values.add(pageVO.getIsPublish());
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
            hql.append(" and m.type =?");
            values.add(pageVO.getType());
        }

        if (null != pageVO.getCreateUserId()) {
            hql.append(" and m.createUserId =?");
            values.add(pageVO.getCreateUserId());
        }

        if (!AppUtil.isEmpty(pageVO.getTitle())) {
            hql.append(" and c.title like ?");
            values.add("%" + pageVO.getTitle() + "%");
        }

        if (!AppUtil.isEmpty(pageVO.getTypeCode())) {
            hql.append(" and c.typeCode =?");
            values.add(pageVO.getTypeCode());
        }
        if (!AppUtil.isEmpty(pageVO.getOpenId())) {
            hql.append(" and m.openId =?");
            values.add(pageVO.getOpenId());
        }
        if (!AppUtil.isEmpty(pageVO.getSt())) {
            hql.append(" and m.addDate>=?");
            values.add(pageVO.getSt());
        }
        if (!AppUtil.isEmpty(pageVO.getEd())) {
            hql.append(" and m.addDate<?");
            values.add(pageVO.getEd());
        }

        if (!AppUtil.isEmpty(pageVO.getSatisfactoryType())) {
            if (pageVO.getSatisfactoryType().intValue() == 1) {//满意
                hql.append("and m.commentCode = 'satisfactory'");
            }else {//不满意
                hql.append("and (m.commentCode != 'satisfactory' or m.commentCode is null)");
            }
        }

        hql.append(" and ((f.receiveOrganId=? and f.operationStatus = ?) or m.createUserId=?)");
        values.add(LoginPersonUtil.getUnitId());
        values.add(MessageBoardForwardEO.OperationStatus.Normal.toString());
        values.add(LoginPersonUtil.getPersonId());

        if ((RoleAuthUtil.isCurUserColumnAdmin(pageVO.getColumnId()) || LoginPersonUtil.isSiteAdmin() || LoginPersonUtil.isSuperAdmin()) && pageVO.getIsAssign() != null && pageVO.getIsAssign() == 0) {
            hql.append(" and c.isPublish=0 and(m.id in(select f.messageBoardId from MessageBoardForwardEO f where f.operationStatus = ?))");
            values.add(MessageBoardForwardEO.OperationStatus.Normal.toString());
        }

        if ((RoleAuthUtil.isCurUserColumnAdmin(pageVO.getColumnId()) || LoginPersonUtil.isSiteAdmin() || LoginPersonUtil.isSuperAdmin()) && pageVO.getIsAssign() != null && pageVO.getIsAssign() == 1) {
            hql.append(" and c.isPublish=0 and(m.id not in(select f.messageBoardId from MessageBoardForwardEO f where f.operationStatus = ?))");
            values.add(MessageBoardForwardEO.OperationStatus.Normal.toString());
        }

        if (!AppUtil.isEmpty(pageVO.getReceiveUnitId())) {
            hql.append(" and m.receiveUnitId =?");
            values.add(pageVO.getReceiveUnitId());
        }
        if (!AppUtil.isEmpty(pageVO.getReceiveUserCode())) {
            hql.append(" and m.receiveUserCode =?");
            values.add(pageVO.getReceiveUserCode());
        }
        if (!StringUtils.isEmpty(pageVO.getClassCode())) {
            hql.append(" and m.classCode =?");
            values.add(pageVO.getClassCode());
        }

        hql.append(" order by m.createDate desc");

        Pagination page = getPagination(pageVO.getPageIndex(), pageVO.getPageSize(), hql.toString(), values.toArray(), MessageBoardEditVO.class);

        return page;
    }

    @Override
    public Pagination getUnAuditCount(UnAuditContentsVO contentVO) {

        StringBuffer hql = new StringBuffer("select c.title as title,c.publishDate as publishDate,c.createDate as createDate, c.isPublish as isPublish,c.columnId as columnId,c.siteId as siteId")
                .append(",c.id as baseContentId,m.id as id,m.messageBoardContent as messageBoardContent,m.addDate as addDate")
                .append(",m.classCode as classCode,m.randomCode as randomCode")
                .append(",m.isPublic as isPublic,m.isPublicInfo as isPublicInfo")
                .append(" from BaseContentEO c, MessageBoardEO m where m.baseContentId = c.id and c.recordStatus=? and m.recordStatus=? ")
                .append(" and  m.dealStatus is null");


        List<Object> values = new ArrayList<Object>();
        values.add(AMockEntity.RecordStatus.Normal.toString());
        values.add(AMockEntity.RecordStatus.Normal.toString());

        if(!(LoginPersonUtil.isSuperAdmin()||LoginPersonUtil.isSiteAdmin())){
            hql.append(" and (m.id in(select f.messageBoardId from MessageBoardForwardEO f where f.receiveOrganId =? and f.operationStatus = ? and f.recordStatus= ? ))");
            values.add(LoginPersonUtil.getUnitId());
            values.add(MessageBoardForwardEO.OperationStatus.Normal.toString());
            values.add(AMockEntity.RecordStatus.Normal.toString());
        }

        if (null != contentVO.getSiteId()) {
            hql.append(" and c.siteId=?");
            values.add(contentVO.getSiteId());
        }

        if (null != contentVO.getColumnId()) {
            hql.append(" and c.columnId=?");
            values.add(contentVO.getColumnId());
        }

//        if (null != contentVO.getIsPublish()) {
//            hql.append(" and c.isPublish=?");
//            values.add(contentVO.getIsPublish());
//        }


        if (!AppUtil.isEmpty(contentVO.getTitle())) {
            hql.append(" and c.title like ?");
            values.add("%" + contentVO.getTitle() + "%");
        }

        hql.append(" order by c.createDate desc");

        return getPagination(contentVO.getPageIndex(), contentVO.getPageSize(), hql.toString(), values.toArray(), MessageBoardEditVO.class);

    }

    @Override
    public List<MessageBoardSearchVO> getAllPulishMessageBoard() {
        StringBuffer hql = new StringBuffer(" select g.id as id, b.id as baseContentId,b.title as title,b.publishDate as publishDate")
                .append(",b.siteId as siteId,b.columnId as columnId,g.messageBoardContent as messageBoardContent,g.classCode as classCode")
                .append(" from BaseContentEO b,MessageBoardEO g where b.id=g.baseContentId and b.isPublish =1 and g.isPublic=1")
                .append(" and b.recordStatus='" + AMockEntity.RecordStatus.Normal.toString() + "'");
        List<MessageBoardSearchVO> list = (List<MessageBoardSearchVO>) getBeansByHql(hql.toString(), new Object[]{}, MessageBoardSearchVO.class);
        return list;
    }

    @Override
    public Long getNoDealCount() {
        StringBuffer hql = new StringBuffer(" select m.id as id, b.id as baseContentId ")
                .append(" from BaseContentEO b,MessageBoardEO m where b.id=m.baseContentId ")
                .append(" and b.recordStatus= ? and m.recordStatus=? ")
                .append(" and m.dealStatus is null and b.siteId=? ");
        //    .append(" and b.isPublish = 0");
        List<Object> values = new ArrayList<Object>();
        values.add(AMockEntity.RecordStatus.Normal.toString());
        values.add(AMockEntity.RecordStatus.Normal.toString());
        values.add(LoginPersonUtil.getSiteId());
        if(!(LoginPersonUtil.isSuperAdmin()||LoginPersonUtil.isSiteAdmin())){
            hql.append(" and (m.id in(select f.messageBoardId from MessageBoardForwardEO f where f.receiveOrganId =? and f.operationStatus = ? and f.recordStatus= ? ))");
            values.add(LoginPersonUtil.getUnitId());
            values.add(MessageBoardForwardEO.OperationStatus.Normal.toString());
            values.add(AMockEntity.RecordStatus.Normal.toString());
        }

        Long count = getCount(hql.toString(), values.toArray());
        if (count == null) {
            count = 0L;
        }
        return count;
    }

    @Override
    public Pagination getMobilePage2(ContentPageVO pageVO) {
        StringBuffer hql = new StringBuffer("select c.title as title,c.publishDate as publishDate,c.isPublish as isPublish")
                .append(",c.id as id,c.columnId as columnId,c.siteId as siteId,c.typeCode as typeCode,c.imageLink as imageLink,c.createDate as createDate")
                .append(" from BaseContentEO c,MessageBoardEO g where g.baseContentId=c.id and c.recordStatus=? and g.recordStatus=?")
                .append(" and g.isPublic=1 and c.isPublish=1 ");

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
        }

        if (!AppUtil.isEmpty(pageVO.getTypeCode())) {
            hql.append(" and c.typeCode =?");
            values.add(pageVO.getTypeCode());
        }

        if (pageVO.getColumnId() != null) {
            hql.append(" and c.columnId=" + pageVO.getColumnId());
        }

        if (!AppUtil.isEmpty(pageVO.getIsReply())) {
            if (pageVO.getIsReply() == 1) {
                hql.append(" and g.dealStatus in ('handled','replyed')");
            } else if (pageVO.getIsReply() == 2) {
                hql.append(" and( g.dealStatus is null or g.dealStatus not in ('handled','replyed'))");
            }
        }


        hql.append(" order by g.createDate desc");

        Pagination page = getPagination(pageVO.getPageIndex(), pageVO.getPageSize(), hql.toString(), values.toArray(), BaseContentEO.class);
        return page;
    }

    @Override
    public Object getCallbackPage(MessageBoardPageVO pageVO){
        StringBuffer hql = new StringBuffer("select c.title as title,c.publishDate as publishDate,c.isPublish as isPublish")
                .append(",c.id as id,c.columnId as columnId,c.siteId as siteId,c.typeCode as typeCode,c.imageLink as imageLink,c.createDate as createDate")
                .append(" from BaseContentEO c,MessageBoardEO g where g.baseContentId=c.id and c.recordStatus=? and g.recordStatus=?")
                .append(" and g.isPublic=1 and c.isPublish=1 ");

        List<Object> values = new ArrayList<Object>();
        values.add(AMockEntity.RecordStatus.Normal.toString());
        values.add(AMockEntity.RecordStatus.Normal.toString());

        if (!AppUtil.isEmpty(pageVO.getSiteId())) {
            hql.append(" and c.siteId=?");
            values.add(pageVO.getSiteId());
        }

        if (!AppUtil.isEmpty(pageVO.getReceiveUnitId())) {
            hql.append(" and (g.id in(select f.messageBoardId from MessageBoardForwardEO f where f.receiveOrganId =? and f.operationStatus = ?))");
            values.add(pageVO.getReceiveUnitId());
            values.add(MessageBoardForwardEO.OperationStatus.Normal.toString());
        }

        if (!AppUtil.isEmpty(pageVO.getTypeCode())) {
            hql.append(" and c.typeCode =?");
            values.add(pageVO.getTypeCode());
        }

        if (pageVO.getColumnId() != null) {
            hql.append(" and c.columnId=" + pageVO.getColumnId());
        }

        if (!AppUtil.isEmpty(pageVO.getIsReply())) {
            if (pageVO.getIsReply() == 1) {
                hql.append(" and g.dealStatus in ('handled','replyed')");
            } else if (pageVO.getIsReply() == 2) {
                hql.append(" and( g.dealStatus is null or g.dealStatus not in ('handled','replyed'))");
            }
        }


        hql.append(" order by g.createDate desc");

        Object object = getPagination(pageVO.getPageIndex(), pageVO.getPageSize(), hql.toString(), values.toArray(), MessageBoardEditVO.class);
        return object;
    }

    public List<MessageBoardListVO> getMessageBoardUnitList(StatisticsQueryVO queryVO){
        List<Object> values = new ArrayList<Object>();
        StringBuffer sql = new StringBuffer("select  m.receive_organ_id as organId,m.fcount as receiveCount,rcount as replyCount,100*(n.rcount/m.fcount) as replyRate from")
                .append(" (select f.receive_organ_id , count(1) as fcount from cms_message_board_forward f left join CMS_MESSAGE_BOARD m on f.message_board_id = m.id ")
                .append(" left join cms_base_content c on c.id=m.base_content_id where 1=1 ");

        if (!StringUtils.isEmpty(queryVO.getStartDate())) {
            sql.append(" and m.create_date>=?");
            values.add(DateUtil.getStrToDate(queryVO.getStartDate(),format));
        }

        if (!StringUtils.isEmpty(queryVO.getEndDate())) {
            sql.append(" and m.create_date<=?");
            values.add(DateUtil.getStrToDate(queryVO.getEndDate(),format));

        }

        sql.append(" and f.operation_Status='Normal' and f.record_status='Normal' and  m.record_status='Normal' and  c.record_status='Normal' and c.type_code='"+BaseContentEO.TypeCode.messageBoard.toString()+"' ")
                .append("  group by f.receive_organ_id) m left join")
                .append(" (select r.create_organ_id , count(distinct(r.message_board_id)) as  rcount  from cms_message_board_reply r left join CMS_MESSAGE_BOARD m on r.message_board_id = m.id ")
                .append(" left join cms_base_content c on c.id=m.base_content_id where 1=1 ");

        if (!StringUtils.isEmpty(queryVO.getStartDate())) {
            sql.append(" and m.create_date>=?");
            values.add(DateUtil.getStrToDate(queryVO.getStartDate(),format));
        }

        if (!StringUtils.isEmpty(queryVO.getEndDate())) {
            sql.append(" and m.create_date<=?");
            values.add(DateUtil.getStrToDate(queryVO.getEndDate(),format));
        }

        sql.append(" and r.record_status='Normal' and  m.record_status='Normal' and  c.record_status='Normal' and  c.type_code='"+BaseContentEO.TypeCode.messageBoard.toString()+"' group by r.create_organ_id ) n")
                .append(" on m.receive_organ_id = n.create_organ_id ");

        sql.append(" where 100*(n.rcount/m.fcount) is not null order by replyRate desc ");

        List<String> fields = new ArrayList<String>();
        fields.add("organId");
        fields.add("receiveCount");
        fields.add("replyCount");
        fields.add("replyRate");
        String[] arr = new String[fields.size()];
        return (List<MessageBoardListVO>) getBeansBySql(sql.toString(), values.toArray(), MessageBoardListVO.class, fields.toArray(arr));
    }

    @Override
    public List<MessageBoardEditVO> getUnReply(Long columnId, int day) {
        StringBuffer hql = new StringBuffer(" select g.id as id, b.id as baseContentId,g.addDate as addDate,b.siteId as siteId,")
                .append(" g.messageBoardContent as messageBoardContent,g.personName as personName,g.type as type")
                .append(",g.receiveUnitId as receiveUnitId,g.receiveUnitName as receiveUnitName from BaseContentEO b,MessageBoardEO g where b.id=g.baseContentId")
                .append(" and b.recordStatus='" + AMockEntity.RecordStatus.Normal.toString() + "'")
                .append(" and ( g.dealStatus is null or g.dealStatus not in ('handled','replyed'))");
        if (columnId != null) {
            hql.append(" and b.columnId=" + columnId);
        }
        List<MessageBoardEditVO> list = (List<MessageBoardEditVO>) getBeansByHql(hql.toString(), new Object[]{}, MessageBoardEditVO.class);
        if (list != null && list.size() > 0) {
            if (day > 0) {
                Long time = day * 24 * 60 * 60 * 1000L;
                Long now = new Date().getTime();
                for (Iterator<MessageBoardEditVO> iter = list.iterator(); iter.hasNext();) {
                    MessageBoardEditVO eo = iter.next();
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
    public List<ContentChartVO> getTypeList(ContentChartQueryVO queryVO) {
        StringBuffer sql = new StringBuffer(" select o.class_Code as type, count(*) as count")
                .append(" from cms_base_content c, cms_message_board o where c.id=o.base_content_Id and c.type_Code=?")
                .append(" and c.record_Status=? and c.is_publish=1 and c.site_id=? ");
        List<Object> values = new ArrayList<Object>();
        values.add(BaseContentEO.TypeCode.messageBoard.toString());
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
    public Long getSelectNum(Long siteId, Long[] columnIds) {
        StringBuffer hql = new StringBuffer(" from cms_base_content c,cms_message_board g where g.base_content_id=c.id")
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
    public MessageBoardEditVO queryRemoved(Long id) {
        StringBuffer hql = new StringBuffer(" select b.id as baseContentId,g.addDate as addDate,b.siteId as siteId,b.title as title")
                .append(",g.messageBoardContent as messageBoardContent,g.personName as personName,g.classCode as classCode,g.id as id")
                .append(",g.personIp as personIp from BaseContentEO b,MessageBoardEO g where b.id=g.baseContentId")
                .append(" and b.recordStatus='" + AMockEntity.RecordStatus.Removed.toString() + "'");
        if (id != null) {
            hql.append(" and b.id=" + id);
        }
        List<MessageBoardEditVO> list = (List<MessageBoardEditVO>) getBeansByHql(hql.toString(), new Object[]{}, MessageBoardEditVO.class);
        if(list!=null&&list.size()>0){
            return list.get(0);
        }else{
            return null;
        }
    }

    @Override
    public void batchCompletelyDelete(Long[] messageBoardIds) {
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("ids", messageBoardIds);
        String hql = "delete from MessageBoardEO where baseContentId in (:ids)";
        executeUpdateByJpql(hql, map);
    }

    @Override
    public Pagination getPageByQuery(MessageBoardSearchVO searchVO) {
        StringBuffer hql = new StringBuffer("select c.title as title,c.publishDate as publishDate,c.createDate as createDate, c.isPublish as isPublish,c.columnId as columnId,c.siteId as siteId")
                .append(",c.id as baseContentId,m.id as id,m.messageBoardContent as messageBoardContent,m.addDate as addDate")
                .append(",m.resourceType as resourceType,m.openId as openId,m.personIp as personIp,m.personName as personName,m.dealStatus as dealStatus")
                .append(",m.docNum as docNum,m.className as className,c.hit as hit")
                .append(",m.classCode as classCode,m.randomCode as randomCode")
                .append(",m.isPublic as isPublic,m.isPublicInfo as isPublicInfo")
                .append(" from BaseContentEO c,MessageBoardEO m where m.baseContentId=c.id and c.recordStatus='" + AMockEntity.RecordStatus.Normal.toString() + "'")
                .append(" and c.isPublish=1 and m.isPublic =1")
                .append(" and m.recordStatus='" + AMockEntity.RecordStatus.Normal.toString() + "'")
                .append(" and c.typeCode = '" + BaseContentEO.TypeCode.messageBoard.toString() + "'");
        List<Object> values = new ArrayList<Object>();
        if (searchVO.getSiteId() != null) {
            hql.append(" and c.siteId =?");
            values.add(searchVO.getSiteId());
        }
        if (searchVO.getColumnId() != null) {
            hql.append(" and c.columnId =?");
            values.add(searchVO.getColumnId());
        }
        if (!org.apache.commons.lang3.StringUtils.isEmpty(searchVO.getTitle())) {
            hql.append(" and c.title like ? ");
            values.add("%" + searchVO.getTitle() + "%");
            /*hql.append("%' escape '\\'");*/
        }

        if (!org.apache.commons.lang3.StringUtils.isEmpty(searchVO.getPersonPhone())) {
            hql.append(" and m.personPhone=?");
            values.add(searchVO.getPersonPhone());
        }

        if (!org.apache.commons.lang3.StringUtils.isEmpty(searchVO.getClassCode())) {
            hql.append(" and m.classCode=?");
            values.add(searchVO.getClassCode());
        }
        if (!AppUtil.isEmpty(searchVO.getStartTime())) {
            hql.append(" and m.createDate>=? ");
            values.add(searchVO.getStartTime());
        }
        if (!AppUtil.isEmpty(searchVO.getEndTime())) {
            hql.append(" and m.createDate<=? ");
            Date date =searchVO.getEndTime();
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);
            calendar.set(Calendar.DATE, calendar.get(Calendar.DATE) + 1);//结束日期增加一天
            values.add(calendar.getTime());
        }
        String type=searchVO.getType();
        if (!org.apache.commons.lang3.StringUtils.isEmpty(type)&&type.equals("1")) {
            hql.append(" order by c.hit desc");
        } else if (!org.apache.commons.lang3.StringUtils.isEmpty(type)&&type.equals("2")) {
            hql.append(" and  m.dealStatus in('handled','replyed')");
            hql.append(" order by m.createDate desc");
        } else {
            hql.append(" order by m.createDate desc");
        }
        Pagination page = getPagination(searchVO.getPageIndex(), searchVO.getPageSize(), hql.toString(), values.toArray(), MessageBoardEditVO.class);
        return page;
    }
    @Override
    public Long getSummaryCount(StatisticsQueryVO queryVO) {
        StringBuffer hql = new StringBuffer();
        List values = new ArrayList();
        hql.append("select count(*)")
                .append(" from MessageBoardEO m,BaseContentEO b")
                .append(" where m.baseContentId = b.id and b.recordStatus = ? ");
        values.add(AMockEntity.RecordStatus.Normal.toString());

        if(!AppUtil.isEmpty(queryVO.getStartDate())) {
            hql.append(" and to_char(m.addDate,'yyyyMMdd') = ?");
            values.add(queryVO.getStartDate());
        }
        if(!AppUtil.isEmpty(queryVO.getTypeCode())) {
            hql.append(" and b.typeCode = ?");
            values.add(queryVO.getTypeCode());
        }
        if(MessageBoardEO.DealStatus.unhandle.toString().equals(queryVO.getDealStatus())) {
            hql.append(" and (m.dealStatus = ? or m.dealStatus = ? or m.dealStatus is null)");
            values.add(MessageBoardEO.DealStatus.unhandle.toString());
            values.add(MessageBoardEO.DealStatus.unreply.toString());
        }
        if(MessageBoardEO.DealStatus.handled.toString().equals(queryVO.getDealStatus())) {
            hql.append(" and (m.dealStatus = ? or m.dealStatus = ?)");
            values.add(MessageBoardEO.DealStatus.handled.toString());
            values.add(MessageBoardEO.DealStatus.replyed.toString());
        }

        return (Long)getObject(hql.toString(),values.toArray());
    }
}
