package cn.lonsun.content.messageBoard.service.Impl;

import cn.lonsun.cache.client.CacheHandler;
import cn.lonsun.content.internal.entity.BaseContentEO;
import cn.lonsun.content.internal.job.GuestBookTaskImpl;
import cn.lonsun.content.messageBoard.controller.internal.entity.MessageBoardEO;
import cn.lonsun.content.messageBoard.controller.internal.entity.MessageBoardForwardEO;
import cn.lonsun.content.messageBoard.controller.internal.entity.MessageBoardReplyEO;
import cn.lonsun.content.messageBoard.controller.vo.MessageBoardReplyVO;
import cn.lonsun.content.messageBoard.dao.IMessageBoardReplyDao;
import cn.lonsun.content.messageBoard.job.MessageBoardReplyTaskImpl;
import cn.lonsun.content.messageBoard.service.IMessageBoardForwardService;
import cn.lonsun.content.messageBoard.service.IMessageBoardReplyService;
import cn.lonsun.content.messageBoard.service.IMessageBoardService;
import cn.lonsun.core.base.entity.AMockEntity;
import cn.lonsun.core.base.service.impl.MockService;
import cn.lonsun.core.exception.BaseRunTimeException;
import cn.lonsun.core.util.TipsMode;
import cn.lonsun.job.util.ScheduleJobUtil;
import cn.lonsun.util.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * @ClassName: MessageBoardServiceImpl
 * @Description: 留言管理业务逻辑
 */
@Service("messageBoardReplyService")
public class MessageBoardReplyServiceImpl extends MockService<MessageBoardReplyEO> implements IMessageBoardReplyService {
    @Autowired
    private IMessageBoardService messageBoardService;

    @Autowired
    private IMessageBoardForwardService forwardRecordService;

    @Autowired
    private IMessageBoardReplyDao messageBoardReplyDao;


    @Override
    public MessageBoardReplyEO reply(MessageBoardReplyVO vo) {
        if ("handled,replyed".contains(vo.getDealStatus())) {
            BaseContentEO contentEO = CacheHandler.getEntity(BaseContentEO.class, vo.getBaseContentId());
            if (contentEO != null) {
                ScheduleJobUtil.addScheduleJob("留言定时任务", MessageBoardReplyTaskImpl.class.getName(), "0 0 0 * * ?", vo.getId() + "");
            }
        }
        MessageBoardReplyEO eo1 = new MessageBoardReplyEO();
        if (vo.getId() != null) {
            eo1 = getEntity(MessageBoardReplyEO.class, vo.getId());
            eo1.setMessageBoardId(vo.getMessageBoardId());
            //eo1.setUsername(LoginPersonUtil.getPersonName());
            eo1.setDealStatus(vo.getDealStatus());
            eo1.setReplyContent(vo.getReplyContent());
            eo1.setAttachName(vo.getAttachName());
            eo1.setAttachId(vo.getAttachId());
            eo1.setReplyDate(vo.getReplyDate());
            //eo1.setReceiveName(LoginPersonUtil.getUnitName());
            updateEntity(eo1);
            if(eo1.getForwardId()!=null){
                MessageBoardForwardEO forwardEO=forwardRecordService.getEntity(MessageBoardForwardEO.class, eo1.getForwardId());
                if (forwardEO != null) {
                    forwardEO.setDealStatus(vo.getDealStatus());
                    forwardEO.setReplyDate(vo.getReplyDate());
                    forwardRecordService.updateEntity(forwardEO);
                }
            }
        } else {
            Map<String, Object> result = new HashMap<String, Object>();
            result.put("messageBoardId", vo.getMessageBoardId());
            result.put("receiveOrganId", LoginPersonUtil.getUnitId());
            result.put("operationStatus", MessageBoardForwardEO.OperationStatus.Normal.toString());
            MessageBoardForwardEO forwardEO = forwardRecordService.getEntity(MessageBoardForwardEO.class, result);
            if (forwardEO != null) {
                eo1.setForwardId(forwardEO.getId());
                forwardEO.setDealStatus(vo.getDealStatus());
                forwardEO.setReplyDate(vo.getReplyDate());
                forwardRecordService.updateEntity(forwardEO);
            }
            eo1.setMessageBoardId(vo.getMessageBoardId());
            eo1.setUsername(LoginPersonUtil.getPersonName());
            eo1.setCreateOrganId(LoginPersonUtil.getUnitId());
            eo1.setDealStatus(vo.getDealStatus());
            eo1.setReplyContent(vo.getReplyContent());
            eo1.setAttachName(vo.getAttachName());
            eo1.setAttachId(vo.getAttachId());
            eo1.setReceiveName(LoginPersonUtil.getUnitName());
            if (vo.getIsSuper() != null && vo.getIsSuper() == 0) {
                eo1.setIsSuper(0);//0表示普通单位回复  1表示管理员回复
            } else {
                eo1.setIsSuper(1);//0表示普通单位回复  1表示管理员回复
            }
            eo1.setReplyDate(vo.getReplyDate());
            saveEntity(eo1);
        }
        return eo1;
    }

    @Override
    public List<MessageBoardReplyVO> getAllDealReply(Long messageBoardId) {
        return messageBoardReplyDao.getAllDealReply(messageBoardId);
    }

    @Override
    public List<MessageBoardReplyEO> getAll() {
        return messageBoardReplyDao.getAll();
    }

}
