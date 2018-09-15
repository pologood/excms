package cn.lonsun.content.messageBoard.dao.Impl;


import cn.lonsun.common.util.AppUtil;
import cn.lonsun.content.messageBoard.controller.internal.entity.MessageBoardForwardEO;
import cn.lonsun.content.messageBoard.controller.internal.entity.MessageBoardOperationEO;
import cn.lonsun.content.messageBoard.controller.vo.MessageBoardOperationVO;
import cn.lonsun.content.messageBoard.dao.IMessageBoardOperationDao;
import cn.lonsun.core.base.dao.impl.MockDao;
import cn.lonsun.core.base.entity.AMockEntity;
import cn.lonsun.core.util.Pagination;
import cn.lonsun.system.role.internal.util.RoleAuthUtil;
import cn.lonsun.util.LoginPersonUtil;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;


@Repository("messageBoardOperationDao")
public class MessageBoardOperationDaoImpl extends MockDao<MessageBoardOperationEO> implements IMessageBoardOperationDao {



    @Override
    public Pagination getPage(MessageBoardOperationVO pageVO) {

        StringBuffer hql = new StringBuffer("select c.title as title, c.columnId as columnId,c.siteId as siteId,c.id as baseContentId")
                .append(",m.createDate as createDate,m.username as username,v.receiveUnitName as receiveUnitName,m.remarks as remarks")
                .append(" from BaseContentEO c,MessageBoardOperationEO m,MessageBoardForwardEO v,MessageBoardEO b")
                .append(" where m.forwardId=v.id and v.messageBoardId=b.id and c.id=b.baseContentId and c.recordStatus=? and m.recordStatus=?");
        List<Object> values = new ArrayList<Object>();
        values.add(AMockEntity.RecordStatus.Normal.toString());
        values.add(AMockEntity.RecordStatus.Normal.toString());
        if (!AppUtil.isEmpty(pageVO.getTitle())) {
            hql.append(" and c.title like '%" + pageVO.getTitle() + "%' ");
        }
        if ((RoleAuthUtil.isCurUserColumnAdmin()||LoginPersonUtil.isSiteAdmin() || LoginPersonUtil.isSuperAdmin())&&pageVO.getIsAssign()!=null&&pageVO.getIsAssign()==2) {
            hql.append(" and m.operationStatus='Back'");
        }
        if ((RoleAuthUtil.isCurUserColumnAdmin()||LoginPersonUtil.isSiteAdmin() || LoginPersonUtil.isSuperAdmin())&&pageVO.getIsAssign()!=null&&pageVO.getIsAssign()==3) {
            hql.append(" and m.operationStatus='Recover'");
        }
        hql.append(" order by m.createDate desc");
        Pagination page = getPagination(pageVO.getPageIndex(), pageVO.getPageSize(), hql.toString(), values.toArray(), MessageBoardOperationVO.class);
        return page;
    }


    @Override
    public Long getCount(String type) {
        StringBuffer hql = new StringBuffer("select o.id as id,o.forwardId as forwardId")
                .append(" from MessageBoardOperationEO o ,MessageBoardForwardEO f,MessageBoardEO m,BaseContentEO c ")
                .append(" where  o.forwardId=f.id and f.messageBoardId=m.id and c.id=m.baseContentId and m.recordStatus=? and c.recordStatus=?");
        List<Object> values = new ArrayList<Object>();
        values.add(AMockEntity.RecordStatus.Normal.toString());
        values.add(AMockEntity.RecordStatus.Normal.toString());
        if (type=="back") {
            hql.append(" and o.operationStatus = ?");
            values.add(MessageBoardForwardEO.OperationStatus.Back.toString());
        }

        if (type=="recover") {
            hql.append(" and o.operationStatus = ?");
            values.add(MessageBoardForwardEO.OperationStatus.Recover.toString());
        }
        Long count = getCount(hql.toString(), values.toArray());
        if (count == null) {
            count = 0L;
        }
        return count;
    }

}
