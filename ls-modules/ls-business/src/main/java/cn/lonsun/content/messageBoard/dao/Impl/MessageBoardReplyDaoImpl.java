package cn.lonsun.content.messageBoard.dao.Impl;

import cn.lonsun.content.messageBoard.controller.internal.entity.MessageBoardReplyEO;
import cn.lonsun.content.messageBoard.controller.vo.MessageBoardReplyDaysVO;
import cn.lonsun.content.messageBoard.controller.vo.MessageBoardReplyVO;
import cn.lonsun.content.messageBoard.dao.IMessageBoardReplyDao;
import cn.lonsun.core.base.dao.impl.MockDao;
import cn.lonsun.core.base.entity.AMockEntity;
import cn.lonsun.system.role.internal.util.RoleAuthUtil;
import cn.lonsun.util.LoginPersonUtil;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository("messageBoardReplyDao")
public class MessageBoardReplyDaoImpl extends MockDao<MessageBoardReplyEO> implements IMessageBoardReplyDao {

    @Override
    public List<MessageBoardReplyVO> getAllReply(Long messageBoardId) {
        StringBuffer hql = new StringBuffer("select r.id as id,r.messageBoardId as messageBoardId,r.ip as ip,r.username as username," +
                "r.replyContent as replyContent,r.receiveName as receiveName,r.receiveUserCode as receiveUserCode,r.dealStatus as dealStatus,r.commentCode as commentCode," +
                "r.attachId as attachId,r.attachName as attachName,r.recordStatus as recordStatus,r.createDate as createDate,r.updateDate as updateDate," +
                "r.createUserId as createUserId,r.createOrganId as createOrganId,r.updateUserId as updateUserId,r.isSuper as isSuper from MessageBoardReplyEO r where 1=1");
        if (messageBoardId != null) {
            hql.append(" and r.messageBoardId=" + messageBoardId );
        }
        hql.append(" and r.recordStatus='" + AMockEntity.RecordStatus.Normal.toString() + "' ");

        try{
            if (LoginPersonUtil.getUserId() != null) {
                if (!RoleAuthUtil.isCurUserColumnAdmin()&&!LoginPersonUtil.isSiteAdmin() && !LoginPersonUtil.isSuperAdmin()) {
                    hql.append(" and r.createOrganId="+LoginPersonUtil.getUnitId());
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }


        hql.append(" order by r.isSuper desc,r.createDate desc");
        List<MessageBoardReplyVO> list = (List<MessageBoardReplyVO>) getBeansByHql(hql.toString(), new Object[]{}, MessageBoardReplyVO.class);
        return list;
    }

    @Override
    public List<MessageBoardReplyVO> getAllDealReply(Long messageBoardId) {
        StringBuffer hql = new StringBuffer("select r.id as id,r.messageBoardId as messageBoardId,r.ip as ip,r.username as username,isSuper as isSuper,r.replyDate as replyDate," +
                "r.replyContent as replyContent,r.receiveName as receiveName,r.receiveUserCode as receiveUserCode,r.dealStatus as dealStatus,r.commentCode as commentCode," +
                "r.attachId as attachId,r.attachName as attachName,r.recordStatus as recordStatus,r.createDate as createDate,r.updateDate as updateDate," +
                "r.createUserId as createUserId,r.createOrganId as createOrganId,r.updateUserId as updateUserId from MessageBoardReplyEO r where 1=1");
        if (messageBoardId != null) {
            hql.append(" and r.messageBoardId=" + messageBoardId );
        }
        hql.append(" and r.dealStatus in('handled','replyed') ");
        hql.append(" and r.recordStatus='" + AMockEntity.RecordStatus.Normal.toString() + "' order by r.createDate desc");
        List<MessageBoardReplyVO> list = (List<MessageBoardReplyVO>) getBeansByHql(hql.toString(), new Object[]{}, MessageBoardReplyVO.class);
        return list;
    }

    @Override
    public List<MessageBoardReplyDaysVO> getReplyDays(Long messageBoardId) {
        StringBuffer hql = new StringBuffer("select (trunc(to_date(TO_CHAR(r.create_date, 'YYYY-MM-DD HH24:MI:SS'),'YYYY-MM-DD HH24:MI:SS') - to_date(TO_CHAR(m.create_date, 'YYYY-MM-DD HH24:MI:SS'),'YYYY-MM-DD HH24:MI:SS')) -   " +
                "         ((case  " +
                "         WHEN (8 - to_number(to_char(to_date(TO_CHAR(m.create_date, 'YYYY-MM-DD HH24:MI:SS'),'YYYY-MM-DD HH24:MI:SS'),'D'))) > trunc(to_date(TO_CHAR(r.create_date, 'YYYY-MM-DD HH24:MI:SS'),'YYYY-MM-DD HH24:MI:SS')  - to_date(TO_CHAR(m.create_date, 'YYYY-MM-DD HH24:MI:SS'),'YYYY-MM-DD HH24:MI:SS')) + 1 THEN 0  " +
                "         ELSE  " +
                "          trunc((trunc(to_date(TO_CHAR(r.create_date, 'YYYY-MM-DD HH24:MI:SS'),'YYYY-MM-DD HH24:MI:SS')  - to_date(TO_CHAR(m.create_date, 'YYYY-MM-DD HH24:MI:SS'),'YYYY-MM-DD HH24:MI:SS')) -  " +
                "                (8 - to_number(to_char(to_date(TO_CHAR(m.create_date, 'YYYY-MM-DD HH24:MI:SS'),'YYYY-MM-DD HH24:MI:SS'),'D'))))/7) + 1 END) +   " +
                "         (case  " +
                "         WHEN mod(8 - to_char(to_date(TO_CHAR(m.create_date, 'YYYY-MM-DD HH24:MI:SS'),'YYYY-MM-DD HH24:MI:SS'), 'D'), 7) > trunc(to_date(TO_CHAR(r.create_date, 'YYYY-MM-DD HH24:MI:SS'),'YYYY-MM-DD HH24:MI:SS') - to_date(TO_CHAR(m.create_date, 'YYYY-MM-DD HH24:MI:SS'),'YYYY-MM-DD HH24:MI:SS')) - 1 THEN 0  " +
                "         ELSE  " +
                "          trunc((trunc(to_date(TO_CHAR(r.create_date, 'YYYY-MM-DD HH24:MI:SS'),'YYYY-MM-DD HH24:MI:SS') - to_date(TO_CHAR(m.create_date, 'YYYY-MM-DD HH24:MI:SS'),'YYYY-MM-DD HH24:MI:SS')) - (mod(8 - to_char(to_date(TO_CHAR(m.create_date, 'YYYY-MM-DD HH24:MI:SS'),'YYYY-MM-DD HH24:MI:SS'),'D'),7) + 1))/7) + 1  END))) replyDays" +
                "          " +
                "  from cms_message_board_reply r left join  cms_message_board m on r.message_board_id = m.id");
        if (messageBoardId != null) {
            hql.append(" and r.message_Board_Id=" + messageBoardId );
        }
        hql.append(" and r.record_Status='" + AMockEntity.RecordStatus.Normal.toString() + "' ");
        if (!LoginPersonUtil.isSiteAdmin() && !LoginPersonUtil.isSuperAdmin()) {
            hql.append(" and r.create_Organ_Id="+LoginPersonUtil.getUnitId());
        }
        List<MessageBoardReplyDaysVO> list = (List<MessageBoardReplyDaysVO>) getBeansBySql(hql.toString(), new Object[]{}, MessageBoardReplyDaysVO.class);
        return list;
    }

    @Override
    public List<MessageBoardReplyEO> getAll() {
        StringBuffer hql = new StringBuffer("from MessageBoardReplyEO m where m.recordStatus = ? ");
        return getEntitiesByHql(hql.toString(),new Object[]{AMockEntity.RecordStatus.Normal.toString()});
    }

}
