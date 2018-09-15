package cn.lonsun.content.messageBoard.dao;

import cn.lonsun.content.messageBoard.controller.internal.entity.MessageBoardReplyEO;
import cn.lonsun.content.messageBoard.controller.vo.MessageBoardReplyDaysVO;
import cn.lonsun.content.messageBoard.controller.vo.MessageBoardReplyVO;
import cn.lonsun.core.base.dao.IMockDao;

import java.util.List;

public interface IMessageBoardReplyDao extends IMockDao<MessageBoardReplyEO> {

    //根据站点Id获取所有回复
    public List<MessageBoardReplyVO> getAllReply(Long messageBoardId);

    //根据站点Id获取所有已办理回复
    public List<MessageBoardReplyVO> getAllDealReply(Long messageBoardId);

    List<MessageBoardReplyDaysVO> getReplyDays(Long messageBoardId);

    List<MessageBoardReplyEO> getAll();
}
