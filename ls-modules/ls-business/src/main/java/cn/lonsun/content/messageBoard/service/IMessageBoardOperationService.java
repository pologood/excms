package cn.lonsun.content.messageBoard.service;

import cn.lonsun.content.messageBoard.controller.internal.entity.MessageBoardOperationEO;
import cn.lonsun.content.messageBoard.controller.vo.MessageBoardOperationVO;
import cn.lonsun.core.base.service.IMockService;
import cn.lonsun.core.util.Pagination;

/**
 * @ClassName: IMessageBoardOperationService
 * @Description: 留言管理操作记录
 */
public interface IMessageBoardOperationService extends IMockService<MessageBoardOperationEO> {

    //退回
    void goBack(MessageBoardOperationVO operationVO);

    //收回
    void recover(MessageBoardOperationVO operationVO);

    //获取分页数据
    Pagination getPage(MessageBoardOperationVO pageVO);

    //获取退回和收回留言记录数
    public Long getCount(String type);
}
