package cn.lonsun.content.messageBoard.dao;

import cn.lonsun.content.messageBoard.controller.internal.entity.MessageBoardOperationEO;
import cn.lonsun.content.messageBoard.controller.vo.MessageBoardOperationVO;
import cn.lonsun.core.base.dao.IMockDao;
import cn.lonsun.core.util.Pagination;
/**
 * @author chenchao
 */
public interface IMessageBoardOperationDao extends IMockDao<MessageBoardOperationEO> {
    //获取留言分页
    public Pagination getPage(MessageBoardOperationVO pageVO);

    //获取退回或收回记录的数目
    public Long getCount(String type);

}
