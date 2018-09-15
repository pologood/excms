package cn.lonsun.content.messageBoard.dao.Impl;

import cn.lonsun.common.util.AppUtil;
import cn.lonsun.content.messageBoard.controller.internal.entity.MessageBoardApplyEO;
import cn.lonsun.content.messageBoard.controller.vo.MessageBoardApplyVO;
import cn.lonsun.content.messageBoard.dao.IMessageBoardApplyDao;
import cn.lonsun.core.base.dao.impl.MockDao;
import cn.lonsun.core.base.entity.AMockEntity;
import cn.lonsun.core.base.util.StringUtils;
import cn.lonsun.core.util.Pagination;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;


@Repository("messageBoardApplyDao")
public class MessageBoardApplyDaoImpl extends MockDao<MessageBoardApplyEO> implements IMessageBoardApplyDao {

    @Override
    public Pagination getRecord(Long pageIndex, Integer pageSize,Long id) {
        StringBuffer hql = new StringBuffer(" from MessageBoardApplyEO where 1=1")
                .append(" and messageBoardId="+id)
                .append(" and disposeStatus='"+MessageBoardApplyEO.DisposeStatus.disposeWait.toString()+"'")
                .append(" and recordStatus='" + AMockEntity.RecordStatus.Normal.toString() + "'")
                .append(" order by createDate desc");
        Pagination page = getPagination(pageIndex,pageSize,hql.toString(),new Object[]{});
        return page;
    }


    @Override
    public Pagination getPage(MessageBoardApplyVO pageVO) {
        StringBuffer hql = new StringBuffer("select c.title as title, c.columnId as columnId,c.siteId as siteId,c.id as baseContentId,v.id as id")
                .append(",m.disposeStatus as disposeStatus,m.updateDate as updateDate,m.applyName as applyName,m.disposeReason as disposeReason,v.addDate as createDate ")
                .append(" from BaseContentEO c,MessageBoardApplyEO m,MessageBoardEO v")
                .append(" where m.messageBoardId=v.id and v.baseContentId=c.id")
                .append(" and c.recordStatus='"+AMockEntity.RecordStatus.Normal.toString()+"' ")
                .append(" and m.recordStatus='"+AMockEntity.RecordStatus.Normal.toString()+"' ")
                .append(" and v.recordStatus='"+AMockEntity.RecordStatus.Normal.toString()+"' ");

        if(StringUtils.isEmpty(pageVO.getDisposeStatus())){
            hql.append(" and m.disposeStatus !='"+MessageBoardApplyEO.DisposeStatus.disposeWait.toString()+"'");
        }else{
            hql.append(" and m.disposeStatus ='"+pageVO.getDisposeStatus()+"'");
        }
        if (!AppUtil.isEmpty(pageVO.getTitle())) {
            hql.append(" and c.title like '%" + pageVO.getTitle() + "%' ");
        }
        hql.append(" order by m.updateDate desc");
        Pagination page = getPagination(pageVO.getPageIndex(), pageVO.getPageSize(), hql.toString(), new Object[]{}, MessageBoardApplyVO.class);
        return page;
    }

    @Override
    public Long getCount(String status) {
        StringBuffer hql = new StringBuffer("select m.id as id,m.messageBoardId as messageBoardId")
                .append(" from MessageBoardApplyEO m")
                .append(" where m.recordStatus=? and m.disposeStatus !='"+MessageBoardApplyEO.DisposeStatus.disposeWait.toString()+"'");
        if(StringUtils.isEmpty(status)){
            hql.append(" and m.disposeStatus !='"+MessageBoardApplyEO.DisposeStatus.disposeWait.toString()+"'");
        }else{
            hql.append(" and m.disposeStatus ='"+MessageBoardApplyEO.DisposeStatus.disposeWait.toString()+"'");
        }
        List<Object> values = new ArrayList<Object>();
        values.add(AMockEntity.RecordStatus.Normal.toString());
        Long count = getCount(hql.toString(), values.toArray());
        if (count == null) {
            count = 0L;
        }
        return count;
    }

    @Override
    public List<MessageBoardApplyVO> getAllApply(Long messageBoardId) {

        StringBuffer hql = new StringBuffer("select c.title as title, c.columnId as columnId,c.siteId as siteId,c.id as baseContentId,m.applyDays as applyDays")
                .append(",m.disposeStatus as disposeStatus,m.createDate as createDate,m.applyName as applyName")
                .append(" from BaseContentEO c,MessageBoardApplyEO m,MessageBoardEO v")
                .append(" where m.messageBoardId=v.id and v.baseContentId=c.id")
                .append(" and m.disposeStatus !='"+MessageBoardApplyEO.DisposeStatus.disposePass.toString()+"'");
        List<MessageBoardApplyVO> list = (List<MessageBoardApplyVO>) getBeansByHql(hql.toString(), new Object[]{}, MessageBoardApplyVO.class);
        return list;
    }

}
