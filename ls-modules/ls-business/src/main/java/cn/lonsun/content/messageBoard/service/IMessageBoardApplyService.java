package cn.lonsun.content.messageBoard.service;

import cn.lonsun.content.messageBoard.controller.internal.entity.MessageBoardApplyEO;
import cn.lonsun.content.messageBoard.controller.vo.MessageBoardApplyVO;
import cn.lonsun.core.base.service.IMockService;
import cn.lonsun.core.util.Pagination;

/**
 * @ClassName: IMessageBoardApplyService
 * @Description: 申请延期记录管理
 */
public interface IMessageBoardApplyService extends IMockService<MessageBoardApplyEO> {

    //申请延期
    public MessageBoardApplyEO apply(MessageBoardApplyVO eo);

    //获取待审核记录数据
    public Pagination getRecord(Long pageIndex, Integer pageSize, Long id);

    //审核
    void dispose(MessageBoardApplyVO applyVO);

    //获取审核通过和未通过分页
    public Pagination getPage(MessageBoardApplyVO pageVO);

    //获取审核通过和未通过的记录数
    public Long getCount(String status);

}
