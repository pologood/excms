package cn.lonsun.content.messageBoard.dao.Impl;

import cn.lonsun.common.util.AppUtil;
import cn.lonsun.content.internal.dao.IForwardRecordDao;
import cn.lonsun.content.internal.entity.GuestBookForwardRecordEO;
import cn.lonsun.content.messageBoard.controller.internal.entity.MessageBoardEO;
import cn.lonsun.content.messageBoard.controller.internal.entity.MessageBoardForwardEO;
import cn.lonsun.content.messageBoard.controller.vo.MessageBoardEditVO;
import cn.lonsun.content.messageBoard.controller.vo.MessageBoardForwardVO;
import cn.lonsun.content.messageBoard.controller.vo.MessageBoardPageVO;
import cn.lonsun.content.messageBoard.controller.vo.MessageBoardReplyVO;
import cn.lonsun.content.messageBoard.dao.IMessageBoarForwardDao;
import cn.lonsun.content.messageBoard.dao.IMessageBoardDao;
import cn.lonsun.core.base.dao.impl.MockDao;
import cn.lonsun.core.base.entity.AMockEntity;
import cn.lonsun.core.util.Pagination;
import cn.lonsun.util.LoginPersonUtil;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;



@Repository("messageBoardForwardDao")
public class MessageBoardForwardDaoImpl extends MockDao<MessageBoardForwardEO> implements IMessageBoarForwardDao {

    @Override
    public Pagination getRecord(Long pageIndex, Integer pageSize,Long id) {
        StringBuffer hql = new StringBuffer("from MessageBoardForwardEO where recordStatus='Normal' and messageBoardId="+id);
        hql.append(" and operationStatus='" + MessageBoardForwardEO.RecordStatus.Normal.toString() + "' ");
        hql.append(" order by createDate desc");
        Pagination page = getPagination(pageIndex,pageSize,hql.toString(),new Object[]{});
        return page;
    }

    @Override
    public List<MessageBoardForwardVO> getAllForwardByMessageBoardId(Long messageBoardId) {
        StringBuffer sql = new StringBuffer("select f.receiveOrganId as receiveOrganId,f.username as username,f.receiveUnitName as receiveUnitName," )
        .append("f.receiveUserName as receiveUserName,f.receiveUserCode as receiveUserCode from MessageBoardForwardEO f where 1=1");
        List<Object> values = new ArrayList<Object>();
        if (messageBoardId != null) {
            sql.append(" and f.messageBoardId='" + messageBoardId + "'");
        }
        sql.append(" and f.operationStatus='" + MessageBoardForwardEO.RecordStatus.Normal.toString() + "' ");
        sql.append(" and f.recordStatus='" + AMockEntity.RecordStatus.Normal.toString() + "'  order by f.createDate desc");
        List<MessageBoardForwardVO> list = (List<MessageBoardForwardVO>) getBeansByHql(sql.toString(), new Object[]{}, MessageBoardForwardVO.class);
        return list;
    }

    @Override
    public Long getCountByMessageBoardId(Long messageBoardId) {
        StringBuffer sql = new StringBuffer("from MessageBoardForwardEO r where 1=1");
        List<Object> values = new ArrayList<Object>();
        if (messageBoardId != null) {
            sql.append(" and r.messageBoardId='" + messageBoardId + "'");
        }
        sql.append(" and r.operationStatus='" + MessageBoardForwardEO.RecordStatus.Normal.toString() + "' ");
        sql.append(" and r.recordStatus='" + AMockEntity.RecordStatus.Normal.toString() + "' ");
        Long count= getCount(sql.toString(), new Object[]{});
        return count;
    }

    @Override
    public  List<MessageBoardForwardVO>  getAllUnit(Long id) {
            StringBuffer hql = new StringBuffer("select f.receiveUnitName as receiveUnitName,f.receiveOrganId as receiveOrganId")
                    .append(" from MessageBoardForwardEO f where f.messageBoardId="+id +"and f.operationStatus='"+AMockEntity.RecordStatus.Normal.toString()+"'")
                    .append(" and f.recordStatus='"+AMockEntity.RecordStatus.Normal.toString()+"' order by f.createDate desc");
            List<MessageBoardForwardVO> forwardVOList = (List<MessageBoardForwardVO>) getBeansByHql(hql.toString(), new Object[]{},MessageBoardForwardVO.class);
            return  forwardVOList;
   }
}
