package cn.lonsun.content.messageBoard.dao;

import cn.lonsun.content.messageBoard.controller.internal.entity.MessageBoardApplyEO;
import cn.lonsun.content.messageBoard.controller.vo.MessageBoardApplyVO;
import cn.lonsun.core.base.dao.IMockDao;
import cn.lonsun.core.util.Pagination;

import java.util.List;

public interface IMessageBoardApplyDao extends IMockDao<MessageBoardApplyEO> {
    //获取待审核分页
    public Pagination getRecord(Long pageIndex, Integer pageSize, Long id);

    //获取审核通过和未通过分页
    public Pagination getPage(MessageBoardApplyVO pageVO);

    //获取审核通过和未通过的记录数
    public Long getCount(String status);

    List<MessageBoardApplyVO> getAllApply(Long messageBoardId);
}
