package cn.lonsun.content.messageBoard.dao;

import cn.lonsun.content.messageBoard.controller.internal.entity.MessageBoardForwardEO;
import cn.lonsun.content.messageBoard.controller.vo.MessageBoardForwardVO;
import cn.lonsun.core.base.dao.IMockDao;
import cn.lonsun.core.util.Pagination;

import java.util.List;

public interface IMessageBoarForwardDao extends IMockDao<MessageBoardForwardEO> {
    //获取留言分页
    public Pagination getRecord(Long pageIndex, Integer pageSize,Long id);

    //根据messageBoardId查询转办记录表
    List<MessageBoardForwardVO> getAllForwardByMessageBoardId(Long messageBoardId);

    Long getCountByMessageBoardId(Long messageBoardId);

    public List<MessageBoardForwardVO> getAllUnit(Long id);
}
