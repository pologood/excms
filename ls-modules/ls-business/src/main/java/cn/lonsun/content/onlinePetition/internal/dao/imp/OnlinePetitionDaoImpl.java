package cn.lonsun.content.onlinePetition.internal.dao.imp;

import cn.lonsun.common.util.AppUtil;
import cn.lonsun.content.internal.entity.BaseContentEO;
import cn.lonsun.content.onlinePetition.internal.dao.IOnlinePetitionDao;
import cn.lonsun.content.onlinePetition.internal.entity.OnlinePetitionEO;
import cn.lonsun.content.onlinePetition.vo.OnlinePetitionVO;
import cn.lonsun.content.onlinePetition.vo.PetitionPageVO;
import cn.lonsun.content.onlinePetition.vo.PetitionQueryVO;
import cn.lonsun.content.vo.ContentChartQueryVO;
import cn.lonsun.core.base.dao.impl.MockDao;
import cn.lonsun.core.base.entity.AMockEntity;
import cn.lonsun.core.util.Pagination;
import cn.lonsun.statistics.ContentChartVO;
import cn.lonsun.statistics.GuestListVO;
import cn.lonsun.statistics.StatisticsQueryVO;
import cn.lonsun.system.role.internal.util.RoleAuthUtil;
import cn.lonsun.util.LoginPersonUtil;
import cn.lonsun.util.ModelConfigUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

/**
 * <br/>
 *
 * @author wangss <br/>
 * @version v1.0 <br/>
 * @date 2015-11-27<br/>
 */
@Repository("onlinePetitionDao")
public class OnlinePetitionDaoImpl extends MockDao<OnlinePetitionEO> implements IOnlinePetitionDao{

    @Override
    public Pagination getPage(PetitionQueryVO pageVO) {

        StringBuffer sql=new StringBuffer("select c.id as id, c.title as title,c.column_id as columnId,c.site_id as siteId " )
                .append(" ,c.publish_date as publishDate,c.is_publish as isPublish,c.author as author,o.is_public as isPublic,o.create_date as createDate")
                .append(" ,o.id as petitionId,o.deal_status as dealStatus,o.content as content,o.ip as ip,o.attach_id as attachId,o.attach_name as attachName ")
                .append(" ,r.create_date as replyDate,r.id as replyId,r.reply_content as replyContent,r.reply_ip as replyIp ,r.reply_user_name as replyUserName" )
                .append("  from cms_base_content c inner join cms_online_petition o on c.id=o.content_id left join cms_petition_rec r on o.id=r.petition_id where 1=1")
                .append(" and c.record_status=? and o.record_status=?");
        List<Object> values = new ArrayList<Object>();
        values.add(AMockEntity.RecordStatus.Normal.toString());
        values.add(AMockEntity.RecordStatus.Normal.toString());
        if(!RoleAuthUtil.isCurUserColumnAdmin(pageVO.getColumnId())&&!LoginPersonUtil.isSuperAdmin()&&!LoginPersonUtil.isSiteAdmin()){
            Long unitId= LoginPersonUtil.getUnitId();
            sql.append(" and (o.rec_unit_id=? or o.rec_unit_id is null or o.create_unit_id=?)");
            values.add(unitId);
            values.add(unitId);
        }
        if (!AppUtil.isEmpty(pageVO.getTitle())) {
            sql.append(" and c.title like ?");
            values.add("%".concat(pageVO.getTitle().trim()).concat("%"));
        }
        if (!AppUtil.isEmpty(pageVO.getColumnId())) {
            sql.append(" and c.column_id=?");
            values.add(pageVO.getColumnId());
        } else {
            sql.append(" and c.column_id is null");
        }
        if (!AppUtil.isEmpty(pageVO.getPurposeCode())) {
            sql.append(" and o.purpose_code=?");
            values.add(pageVO.getPurposeCode());
        }
        if (!AppUtil.isEmpty(pageVO.getCategoryCode())) {
            sql.append(" and o.category_code=?");
            values.add(pageVO.getCategoryCode());
        }

        if(pageVO.getIsPublish()!=null){
            sql.append(" and c.is_publish=?");
            values.add(pageVO.getIsPublish());
        }
        if(!StringUtils.isEmpty(pageVO.getDealStatus())){
            if(pageVO.getDealStatus().equals("unreply")||pageVO.getDealStatus().equals("unhandle")){
                sql.append(" and ( o.deal_status=? or o.deal_status is null )");
            }else{
                sql.append(" and o.deal_status=?");
            }
            values.add(pageVO.getDealStatus());
        }

        sql.append(" order by ").append(ModelConfigUtil.getOrderTypeValue(pageVO.getColumnId(), LoginPersonUtil.getSiteId()));
        List<String> fields = new ArrayList<String>();
        fields.add("id");
        fields.add("title");
        fields.add("columnId");
        fields.add("siteId");
        fields.add("publishDate");
        fields.add("isPublish");
        fields.add("author");
        fields.add("isPublic");
        fields.add("createDate");
        fields.add("petitionId");
        fields.add("dealStatus");
        fields.add("content");
        fields.add("ip");
        fields.add("attachId");
        fields.add("attachName");
        fields.add("replyDate");
        fields.add("replyId");
        fields.add("replyContent");
        fields.add("replyIp");
        fields.add("replyUserName");
        String[] str = new String[fields.size()];
        return getPaginationBySql(pageVO.getPageIndex(), pageVO.getPageSize(),sql.toString(),values.toArray(),PetitionPageVO.class,fields.toArray(str));

    }


    @Override
    public OnlinePetitionVO getVO(Long id) {
//        StringBuffer hql=new StringBuffer(" select c.id as id, c.title as title,c.columnId as columnId,c.siteId as siteId,c.author as author,c.typeCode as typeCode ")
//                .append(" ,c.publishDate as publishDate,c.isPublish as isPublish,c.createDate as createDate,o.isPublic as isPublic")
//                .append(" ,o.purposeCode as purposeCode,o.id as petitionId,o.dealStatus as dealStatus,o.occupation as occupation,o.phoneNum as phoneNum,o.address as address" )
//                .append(",o.attachName as attachName,o.attachId as attachId,o.content as content,o.recUnitId as recUnitId,o.recUnitName as recUnitName,o.categoryCode as categoryCode")
//                .append(" from BaseContentEO c, OnlinePetitionEO o where c.id=o.contentId and c.typeCode='" + BaseContentEO.TypeCode.onlinePetition.toString() + "'")
//                .append(" and c.recordStatus='"+ AMockEntity.RecordStatus.Normal.toString()+"' and o.recordStatus='"+AMockEntity.RecordStatus.Normal.toString()+"' ")
//                .append(" and c.id="+id);
        StringBuffer sql=new StringBuffer("select c.id as id, c.title as title,c.column_id as columnId,c.site_id as siteId,c.publish_date as publishDate " )
                .append(" ,c.is_publish as isPublish,c.author as author,o.is_public as isPublic,o.create_date as createDate")
                .append(" ,o.id as petitionId,o.deal_status as dealStatus,o.content as content,o.ip as ip,o.attach_id as attachId,o.attach_name as attachName ")
                .append(" ,r.create_date as replyDate,r.id as replyId,r.reply_content as replyContent,r.reply_ip as replyIp ,r.reply_user_name as replyUserName" )
                .append(" ,o.occupation as occupation,o.phone_num as phoneNum,o.address as address,o.rec_unit_id as recUnitId,o.category_code as categoryCode,o.purpose_code as purposeCode")
                .append("  from cms_base_content c inner join cms_online_petition o on c.id=o.content_id left join cms_petition_rec r on o.id=r.petition_id where 1=1")
                .append(" and c.record_status='" + AMockEntity.RecordStatus.Normal.toString() + "' and o.record_status='" + AMockEntity.RecordStatus.Normal.toString() + "'")
                .append(" and c.type_code='" + BaseContentEO.TypeCode.onlinePetition.toString() + "' and c.id="+id);
        List<String> fields = new ArrayList<String>();
        fields.add("id");
        fields.add("title");
        fields.add("columnId");
        fields.add("siteId");
        fields.add("publishDate");
        fields.add("isPublish");
        fields.add("author");
        fields.add("isPublic");
        fields.add("createDate");
        fields.add("petitionId");
        fields.add("dealStatus");
        fields.add("content");
        fields.add("ip");
        fields.add("attachId");
        fields.add("attachName");
        fields.add("replyDate");
        fields.add("replyId");
        fields.add("replyContent");
        fields.add("replyIp");
        fields.add("replyUserName");
        fields.add("occupation");
        fields.add("phoneNum");
        fields.add("address");
        fields.add("recUnitId");
        fields.add("categoryCode");
        fields.add("purposeCode");
        String[] str = new String[fields.size()];
        List<OnlinePetitionVO> list=( List<OnlinePetitionVO> )  getBeansBySql(sql.toString(), new Object[]{}, OnlinePetitionVO.class,fields.toArray(str));
        OnlinePetitionVO vo=new OnlinePetitionVO();
        if(list!=null&&list.size()>0){
            vo=list.get(0);
        }
        return vo;

    }

    @Override
    public Long countData(Long columnId) {
        StringBuffer hql = new StringBuffer("select * from BaseContentEO c, OnlinePetitionEO o "
                + "where o.contentId=c.id and c.recordStatus=? and o.recordStatus=? and c.columnId=?");
        List<Object> values = new ArrayList<Object>();
        values.add(AMockEntity.RecordStatus.Normal.toString());
        values.add(AMockEntity.RecordStatus.Normal.toString());
        values.add(columnId);
        return getCount(hql.toString(),values.toArray());
    }

    @Override
    public List<ContentChartVO> getStatisticsList(ContentChartQueryVO queryVO) {
        StringBuffer sql=new StringBuffer("select (v.c/u.c)*100 as rate,u.rec_unit_id as organId from(")
                .append("select count(*) c,o.rec_unit_id from cms_base_content b, cms_online_petition o ")
                .append("where b.id=o.content_id  and b.record_status=? and b.site_id=? and b.type_code=? and b.is_publish=1 ");
        List<Object> values = new ArrayList<Object>();
        if(!StringUtils.isEmpty(queryVO.getStartStr())&&!StringUtils.isEmpty(queryVO.getEndStr())){
            sql.append("  and b.publish_date>=to_date('" + queryVO.getStartStr()+ "','yyyy-mm-dd hh24:mi:ss') and b.publish_date<=to_date('" + queryVO.getEndStr() + "','yyyy-mm-dd hh24:mi:ss') ");
        }
        sql.append("group by o.rec_unit_id) u,(select count(*) c,o.rec_unit_id from cms_base_content b,cms_online_petition o ")
                .append("where b.id=o.content_id and b.record_status=? and o.deal_status in('handled','replyed') and b.site_id=? and b.type_code=? and b.is_publish=1 ");
        if(!StringUtils.isEmpty(queryVO.getStartStr())&&!StringUtils.isEmpty(queryVO.getEndStr())){
            sql.append("  and b.publish_date>=to_date('" + queryVO.getStartStr()+ "','yyyy-mm-dd hh24:mi:ss') and b.publish_date<=to_date('" + queryVO.getEndStr() + "','yyyy-mm-dd hh24:mi:ss') ");
        }
        sql.append(" group by o.rec_unit_id)v where u.rec_unit_id=v.rec_unit_id order by v.c/u.c desc");

        values.add(AMockEntity.RecordStatus.Normal.toString());
        values.add(queryVO.getSiteId());
        values.add(BaseContentEO.TypeCode.onlinePetition.toString());

        values.add(AMockEntity.RecordStatus.Normal.toString());
        values.add(queryVO.getSiteId());
        values.add(BaseContentEO.TypeCode.onlinePetition.toString());
        List<String> fields = new ArrayList<String>();
        fields.add("rate");
        fields.add("organId");
        String[] arr = new String[fields.size()];
        return (List<ContentChartVO>)getBeansBySql(sql.toString(), values.toArray(), ContentChartVO.class, fields.toArray(arr));
    }

    @Override
    public List<ContentChartVO> getTypeList(ContentChartQueryVO queryVO) {
        StringBuffer sql=new StringBuffer(" select o.purpose_Code as type, count(*) as count")
                .append(" from cms_base_content c, cms_Online_Petition o where c.id=o.content_Id and c.type_Code=?")
                .append(" and c.record_Status=? and c.is_publish=1 and c.site_id=? ");
        List<Object> values = new ArrayList<Object>();
        values.add(BaseContentEO.TypeCode.onlinePetition.toString());
        values.add(AMockEntity.RecordStatus.Normal.toString());
        values.add(queryVO.getSiteId());
        if(!StringUtils.isEmpty(queryVO.getStartStr())&&!StringUtils.isEmpty(queryVO.getEndStr())){
            sql.append("  and c.publish_date>=to_date(?,'yyyy-mm-dd hh24:mi:ss') and c.publish_date<=to_date(?,'yyyy-mm-dd hh24:mi:ss') ");
            values.add(queryVO.getStartStr());
            values.add(queryVO.getEndStr());
        }
        sql.append("group by o.purpose_Code");
        List<String> fields = new ArrayList<String>();
        fields.add("type");
        fields.add("count");
        String[] arr = new String[fields.size()];
        return (List<ContentChartVO>)getBeansBySql(sql.toString(), values.toArray(), ContentChartVO.class, fields.toArray(arr));
    }


    @Override
    public Pagination getPetitionPage(StatisticsQueryVO vo){
        StringBuffer sql=new StringBuffer("select u.c recCount,v.c dealCount,(u.c-v.c) undoCount,(v.c/u.c)*100 rate,u.organId as organId from(")
                .append(" select count(*) c,o.rec_unit_id organId from cms_base_content c, cms_online_petition o  ")
                .append(" where  c.id=o.content_id and c.record_status=? and c.site_id=? and c.type_code=?");
        List<Object> values=new ArrayList<Object>();
        values.add(AMockEntity.RecordStatus.Normal.toString());
        values.add(vo.getSiteId());
        values.add(BaseContentEO.TypeCode.onlinePetition.toString());

        if(!StringUtils.isEmpty(vo.getStartDate())){
            sql.append("  and c.create_date>=to_date(?,'yyyy-mm-dd:hh24:mi:ss')");
            values.add(vo.getStartDate());
        }
        if(!StringUtils.isEmpty(vo.getEndDate())){
            sql.append("  and c.create_date<=to_date(?,'yyyy-mm-dd:hh24:mi:ss')");
            values.add(vo.getEndDate());
        }
        sql.append("  group by o.rec_unit_id) u left join (")
                .append("select count(*) c,o.rec_unit_id organId from cms_base_content c, cms_online_petition o where c.id=o.content_id and c.is_publish=1 ")
                .append(" and c.record_status=? and c.site_id=? and c.type_code=?");

        values.add(AMockEntity.RecordStatus.Normal.toString());
        values.add(vo.getSiteId());
        values.add(BaseContentEO.TypeCode.onlinePetition.toString());
        if(!StringUtils.isEmpty(vo.getStartDate())){
            sql.append("  and c.create_date>=to_date(?,'yyyy-mm-dd:hh24:mi:ss')");
            sql.append("  and c.publish_date>=to_date(?,'yyyy-mm-dd:hh24:mi:ss')");
            values.add(vo.getStartDate());
            values.add(vo.getStartDate());
        }
        if(!StringUtils.isEmpty(vo.getEndDate())){
            sql.append("  and c.create_date<=to_date(?,'yyyy-mm-dd:hh24:mi:ss')");
            sql.append("  and c.publish_date<=to_date(?,'yyyy-mm-dd:hh24:mi:ss')");
            values.add(vo.getEndDate());
            values.add(vo.getEndDate());
        }
        sql.append(" and o.deal_status in('handled','replyed') group by o.rec_unit_id ) v")
                .append(" on u.organId=v.organId");
        List<String> fields = new ArrayList<String>();
        fields.add("recCount");
        fields.add("dealCount");
        fields.add("undoCount");
        fields.add("rate");
        fields.add("organId");
        String[] arr = new String[fields.size()];
        return getPaginationBySql(vo.getPageIndex(), vo.getPageSize(),sql.toString(), values.toArray(), GuestListVO.class, fields.toArray(arr));
    }
    @Override
    public List<GuestListVO> getPetitionList(StatisticsQueryVO vo) {
        StringBuffer sql=new StringBuffer("select u.c recCount,v.c dealCount,(u.c-v.c) undoCount,(v.c/u.c)*100 rate,u.organId as organId from(")
                .append(" select count(*) c,o.rec_unit_id organId from cms_base_content c, cms_online_petition o where c.id=o.content_id  ")
                .append(" and c.record_status=? and c.site_id=? and c.type_code=?");

        List<Object> values=new ArrayList<Object>();
        values.add(AMockEntity.RecordStatus.Normal.toString());
        values.add(vo.getSiteId());
        values.add(BaseContentEO.TypeCode.onlinePetition.toString());

        if(!StringUtils.isEmpty(vo.getStartDate())){
            sql.append("  and c.publish_date>=to_date(?,'yyyy-mm-dd:hh24:mi:ss')");
            values.add(vo.getStartDate());
        }
        if(!StringUtils.isEmpty(vo.getEndDate())){
            sql.append("  and c.publish_date<=to_date(?,'yyyy-mm-dd:hh24:mi:ss')");
            values.add(vo.getEndDate());
        }
        sql.append(" group by o.rec_unit_id ) u left join(")
                .append("select count(*) c,o.rec_unit_id organId from cms_base_content c, cms_online_petition o where c.id=o.content_id and c.is_publish=1 ")
                .append(" and c.record_status=? and c.site_id=? and c.type_code=?");
        values.add(AMockEntity.RecordStatus.Normal.toString());
        values.add(vo.getSiteId());
        values.add(BaseContentEO.TypeCode.onlinePetition.toString());

        if(!StringUtils.isEmpty(vo.getStartDate())){
            sql.append("  and c.create_date>=to_date(?,'yyyy-mm-dd:hh24:mi:ss')");
            sql.append("  and c.publish_date>=to_date(?,'yyyy-mm-dd:hh24:mi:ss')");
            values.add(vo.getStartDate());
            values.add(vo.getStartDate());

        }
        if(!StringUtils.isEmpty(vo.getEndDate())){
            sql.append("  and c.create_date>=to_date(?,'yyyy-mm-dd:hh24:mi:ss')");
            sql.append("  and c.publish_date<=to_date(?,'yyyy-mm-dd:hh24:mi:ss')");
            values.add(vo.getEndDate());
            values.add(vo.getEndDate());
        }
        sql.append(" and o.deal_status in('handled','replyed') group by o.rec_unit_id) v")
                .append(" on u.organId=v.organId");
        List<String> fields = new ArrayList<String>();
        fields.add("recCount");
        fields.add("dealCount");
        fields.add("undoCount");
        fields.add("rate");
        fields.add("organId");
        String[] arr = new String[fields.size()];
        return (List<GuestListVO> )getBeansBySql(sql.toString(), values.toArray(), GuestListVO.class, fields.toArray(arr));
    }

    @Override
    public OnlinePetitionVO getByCheckCode(String checkCode, Long siteId) {
        StringBuffer sql=new StringBuffer("select c.id as id, c.title as title,c.column_id as columnId,c.site_id as siteId " )
                .append(" ,c.publish_date as publishDate,c.is_publish as isPublish,c.author as author,o.is_public as isPublic,o.create_date as createDate")
                .append(" ,o.id as petitionId,o.deal_status as dealStatus,o.content as content,o.ip as ip,o.attach_id as attachId,o.attach_name as attachName ")
                .append(" ,r.create_date as replyDate,r.id as replyId,r.reply_content as replyContent,r.reply_ip as replyIp ,r.reply_user_name as replyUserName" )
                .append("  from cms_base_content c inner join cms_online_petition o on c.id=o.content_id left join cms_petition_rec r on o.id=r.petition_id where 1=1")
                .append(" and c.record_status='" + AMockEntity.RecordStatus.Normal.toString() + "' and o.record_status='" + AMockEntity.RecordStatus.Normal.toString() + "'");
        if(!StringUtils.isEmpty(checkCode)){
            sql.append(" and o.check_code='"+checkCode+"'");
        }
        if(siteId!=null){
            sql.append(" and c.site_id="+siteId);
        }
        List<String> fields = new ArrayList<String>();
        fields.add("id");
        fields.add("title");
        fields.add("columnId");
        fields.add("siteId");
        fields.add("publishDate");
        fields.add("isPublish");
        fields.add("author");
        fields.add("isPublic");
        fields.add("createDate");
        fields.add("petitionId");
        fields.add("dealStatus");
        fields.add("content");
        fields.add("ip");
        fields.add("attachId");
        fields.add("attachName");
        fields.add("replyDate");
        fields.add("replyId");
        fields.add("replyContent");
        fields.add("replyIp");
        fields.add("replyUserName");
        String[] str = new String[fields.size()];
        List<OnlinePetitionVO> list= (List<OnlinePetitionVO>)getBeansBySql(sql.toString(),new Object[]{},OnlinePetitionVO.class, fields.toArray(str));
        OnlinePetitionVO vo=null;
        if(list!=null&&list.size()>0){
            vo=list.get(0);
        }
        return vo;
    }

}
