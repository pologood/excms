package cn.lonsun.content.messageBoard.service;

import cn.lonsun.content.messageBoard.controller.internal.entity.MessageBoardEO;
import cn.lonsun.content.messageBoard.controller.internal.entity.MessageBoardForwardEO;
import cn.lonsun.content.messageBoard.controller.vo.MessageBoardForwardVO;
import cn.lonsun.content.messageBoard.controller.vo.MessageBoardOperationVO;
import cn.lonsun.core.base.service.IMockService;
import cn.lonsun.core.util.Pagination;

import java.util.List;

/**
 * @ClassName: IMessageBoardForwardService
 */
public interface IMessageBoardForwardService extends IMockService<MessageBoardForwardEO> {

    //取留言记录数据
    public Pagination getRecord(Long pageIndex, Integer pageSize, Long id);

    //退回
    void goBack(MessageBoardForwardVO forwardVO);

    //收回
    void recover(MessageBoardOperationVO operationVO);

    //根据messageBoardId查询转办记录表
    List<MessageBoardForwardVO> getAllForwardByMessageBoardId(Long messageBoardId);

    //根据messageBoardId查询转办记录数
    Long getCountByMessageBoardId(Long messageBoardId);

    void forward(MessageBoardForwardEO forwardEO,Long columnId,MessageBoardEO messageBoardEO);

    List<MessageBoardForwardVO> getAllUnit(Long id);
}
