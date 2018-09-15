package cn.lonsun.content.onlineDeclaration.internal.dao.impl;

import cn.lonsun.cache.client.CacheHandler;
import cn.lonsun.common.util.AppUtil;
import cn.lonsun.content.internal.entity.BaseContentEO;
import cn.lonsun.content.onlineDeclaration.internal.dao.IOnlineDeclarationDao;
import cn.lonsun.content.onlineDeclaration.internal.entity.OnlineDeclarationEO;
import cn.lonsun.content.onlineDeclaration.vo.DeclaQueryVO;
import cn.lonsun.content.onlineDeclaration.vo.OnlineDeclarationVO;
import cn.lonsun.core.base.dao.impl.MockDao;
import cn.lonsun.core.base.entity.AMockEntity;
import cn.lonsun.core.util.Pagination;
import cn.lonsun.rbac.internal.entity.OrganEO;
import cn.lonsun.system.role.internal.util.RoleAuthUtil;
import cn.lonsun.util.LoginPersonUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * <br/>
 *
 * @author wangss <br/>
 * @version v1.0 <br/>
 * @date 2016-6-12<br/>
 */
@Repository("onlineDeclarationDao")
public class OnlineDeclarationDaoImpl extends MockDao<OnlineDeclarationEO> implements IOnlineDeclarationDao {

    @Override
    public Pagination getPage(DeclaQueryVO pageVO) {
        StringBuffer hql = new StringBuffer("select c.title as title,c.publishDate as publishDate,c.createDate as createDate,c.isPublish as isPublish,c.columnId as columnId")
                .append(",c.id as baseContentId,c.workFlowStatus as workFlowStatus,g.id as id,g.replyContent as replyContent,g.factReason as factReason,g.dealStatus as dealStatus")
                .append(",g.personName as personName,g.replyDate as replyDate,g.attachId as attachId,g.attachName as attachName")
                .append(",g.recUnitId as recUnitId,g.docNum as docNum,g.randomCode as randomCode")
                .append(" from BaseContentEO c,OnlineDeclarationEO g where g.baseContentId=c.id and c.recordStatus=? and g.recordStatus=? ");

        List<Object> values = new ArrayList<Object>();
        values.add(AMockEntity.RecordStatus.Normal.toString());
        values.add(AMockEntity.RecordStatus.Normal.toString());

        if (!AppUtil.isEmpty(pageVO.getSiteId())) {
            hql.append(" and c.siteId=?");
            values.add(pageVO.getSiteId());
        }
        if (!AppUtil.isEmpty(pageVO.getColumnId())) {
            hql.append(" and c.columnId=?");
            values.add(pageVO.getColumnId());
        }

        if (!AppUtil.isEmpty(pageVO.getTitle())) {
            hql.append(" and c.title like ?");
            values.add("%".concat(pageVO.getTitle().trim()).concat("%"));
        }

        if(pageVO.getIsPublish()!=null){
            hql.append(" and c.isPublish=?");
            values.add(pageVO.getIsPublish());
        }

        if(!StringUtils.isEmpty(pageVO.getDealStatus())){
            hql.append(" and g.dealStatus=?");
            values.add(pageVO.getDealStatus());
        }

        if (!RoleAuthUtil.isCurUserColumnAdmin(pageVO.getColumnId())&&!LoginPersonUtil.isSiteAdmin() && !LoginPersonUtil.isSuperAdmin()) {
            hql.append(" and( g.recUnitId=? or g.recUnitId is null or g.createUnitId=?)");
            values.add(LoginPersonUtil.getUnitId());
            values.add(LoginPersonUtil.getUnitId());
        }

        hql.append(" order by c.createDate desc");

        Pagination page = getPagination(pageVO.getPageIndex(), pageVO.getPageSize(), hql.toString(), values.toArray(), OnlineDeclarationVO.class);

        return page;
    }

    @Override
    public Pagination getFrontPage(DeclaQueryVO pageVO) {
        StringBuffer hql = new StringBuffer("select c.title as title,c.publishDate as publishDate,c.createDate as createDate,c.isPublish as isPublish,c.columnId as columnId")
                .append(",c.id as baseContentId,g.id as id,g.replyContent as replyContent,g.factReason as factReason,g.dealStatus as dealStatus")
                .append(",g.personName as personName,g.replyDate as replyDate,g.attachId as attachId,g.attachName as attachName,g.recUnitId as recUnitId")
                .append(" from BaseContentEO c,OnlineDeclarationEO g where g.baseContentId=c.id and c.recordStatus=? and g.recordStatus=? ");

        List<Object> values = new ArrayList<Object>();
        values.add(AMockEntity.RecordStatus.Normal.toString());
        values.add(AMockEntity.RecordStatus.Normal.toString());

        if (!AppUtil.isEmpty(pageVO.getSiteId())) {
            hql.append(" and c.siteId=?");
            values.add(pageVO.getSiteId());
        }
        if (!AppUtil.isEmpty(pageVO.getColumnId())) {
            hql.append(" and c.columnId=?");
            values.add(pageVO.getColumnId());
        }

        if (!AppUtil.isEmpty(pageVO.getTitle())) {
            hql.append(" and c.title like '%?%' ");
            values.add(pageVO.getTitle());
        }

        if(pageVO.getIsPublish()!=null){
            hql.append(" and c.isPublish=?");
            values.add(pageVO.getIsPublish());
        }


        if(!StringUtils.isEmpty(pageVO.getDealStatus())){
            hql.append(" and g.dealStatus=?");
            values.add(pageVO.getDealStatus());
        }

        hql.append(" order by c.createDate desc");

        Pagination page = getPagination(pageVO.getPageIndex(), pageVO.getPageSize(), hql.toString(), values.toArray(), OnlineDeclarationVO.class);

        return page;
    }

    @Override
    public OnlineDeclarationVO getVO(Long id) {
        StringBuffer hql=new StringBuffer(" select c.id as baseContentId, c.title as title,c.columnId as columnId,c.siteId as siteId ")
                .append(" ,c.publishDate as publishDate,c.isPublish as isPublish,c.createDate as createDate,o.replyDate as replyDate")
                .append(" ,o.id as id,o.dealStatus as dealStatus,o.phoneNum as phoneNum,o.address as address,o.personName as personName,o.unitName as unitName" )
                .append(",o.attachName as attachName,o.attachId as attachId,o.factReason as factReason,o.recUnitId as recUnitId,o.downUrl as downUrl")
                .append(" from BaseContentEO c, OnlineDeclarationEO o where c.id=o.baseContentId and c.typeCode='" + BaseContentEO.TypeCode.onlineDeclaration.toString() + "'")
                .append(" and c.recordStatus='"+ AMockEntity.RecordStatus.Normal.toString()+"' and o.recordStatus='"+AMockEntity.RecordStatus.Normal.toString()+"' ")
                .append(" and c.id="+id);

        OnlineDeclarationVO vo=(OnlineDeclarationVO)  getBean(hql.toString(), new Object[]{}, OnlineDeclarationVO.class);
        if(vo!=null&&vo.getRecUnitId()!=null){
            OrganEO organEO=CacheHandler.getEntity(OrganEO.class,vo.getRecUnitId());
            if(organEO!=null){
                vo.setRecUnitName(organEO.getName());
            }
        }
        return vo;
    }

    @Override
    public Long countList(Long siteId, String columnIds, Date startDate, Integer isPublish, Integer isResponse) {
        StringBuffer hql = new StringBuffer(" select g.id as id, b.id as baseContentId ")
                .append(" from BaseContentEO b,OnlineDeclarationEO g where b.id=g.baseContentId ")
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
            hql.append(" and b.createDate>=?");
            values.add(startDate);
        }
        if (isPublish != null) {
            hql.append(" and b.isPublish=?");
            values.add(isPublish);
        }
        if (isResponse != null) {
            if(isResponse==1){
                hql.append(" and g.dealStatus in ('handled','replyed')");
            }else{
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
    public OnlineDeclarationVO searchByCode(String randomCode, String docNum, Long siteId) {
        StringBuffer hql=new StringBuffer(" select c.id as baseContentId, c.title as title,c.columnId as columnId,c.siteId as siteId ")
                .append(" ,c.publishDate as publishDate,c.isPublish as isPublish,c.createDate as createDate,o.docNum as docNum,o.randomCode as randomCode")
                .append(" ,o.id as id,o.dealStatus as dealStatus,o.phoneNum as phoneNum,o.address as address,o.personName as personName,o.unitName as unitName" )
                .append(",o.attachName as attachName,o.attachId as attachId,o.factReason as factReason,o.recUnitId as recUnitId,o.replyContent as replyContent")
                .append(",o.replyUnitId as replyUnitId,o.replyDate as replyDate ")
                .append(" from BaseContentEO c, OnlineDeclarationEO o where c.id=o.baseContentId and c.typeCode='" + BaseContentEO.TypeCode.onlineDeclaration.toString() + "'")
                .append(" and c.recordStatus='"+ AMockEntity.RecordStatus.Normal.toString()+"' and o.recordStatus='"+AMockEntity.RecordStatus.Normal.toString()+"' ");

        if(!StringUtils.isEmpty(randomCode)){
            hql.append(" and o.randomCode='"+randomCode+"'");
        }
        if(!StringUtils.isEmpty(docNum)){
            hql.append(" and o.docNum='"+docNum+"'");
        }
        if(siteId!=null){
            hql.append(" and c.siteId="+siteId);
        }
        List<OnlineDeclarationVO> list = (List<OnlineDeclarationVO>) getBeansByHql(hql.toString(), new Object[]{}, OnlineDeclarationVO.class);
        OnlineDeclarationVO vo=null;
        if(list!=null&&list.size()>0){
            vo=list.get(0);
        }
        return vo;
    }
}
