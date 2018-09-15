package cn.lonsun.content.messageBoard.service;

import cn.lonsun.content.messageBoard.controller.internal.entity.MessageBoardReplyEO;
import cn.lonsun.content.messageBoard.controller.vo.MessageBoardReplyVO;
import cn.lonsun.core.base.service.IMockService;

import java.util.List;

/**
 * @ClassName: IGuestBookService
 * @Description: 多回复留言管理接口
 */
public interface IMessageBoardReplyService extends IMockService<MessageBoardReplyEO> {

    //回复留言
    public MessageBoardReplyEO reply(MessageBoardReplyVO eo);


    List<MessageBoardReplyVO> getAllDealReply(Long id);

    List<MessageBoardReplyEO> getAll();
}
