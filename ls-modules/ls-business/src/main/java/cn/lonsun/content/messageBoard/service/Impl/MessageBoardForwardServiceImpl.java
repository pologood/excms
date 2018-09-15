package cn.lonsun.content.messageBoard.service.Impl;

import cn.lonsun.content.messageBoard.controller.internal.entity.MessageBoardApplyEO;
import cn.lonsun.content.messageBoard.controller.internal.entity.MessageBoardEO;
import cn.lonsun.content.messageBoard.controller.internal.entity.MessageBoardForwardEO;
import cn.lonsun.content.messageBoard.controller.vo.MessageBoardEditVO;
import cn.lonsun.content.messageBoard.controller.vo.MessageBoardForwardVO;
import cn.lonsun.content.messageBoard.controller.vo.MessageBoardOperationVO;
import cn.lonsun.content.messageBoard.controller.vo.MessageBoardVO;
import cn.lonsun.content.messageBoard.dao.IMessageBoarForwardDao;
import cn.lonsun.content.messageBoard.dao.IMessageBoardApplyDao;
import cn.lonsun.content.messageBoard.service.IMessageBoardApplyService;
import cn.lonsun.content.messageBoard.service.IMessageBoardForwardService;
import cn.lonsun.content.messageBoard.service.IMessageBoardOperationService;
import cn.lonsun.content.messageBoard.service.IMessageBoardService;
import cn.lonsun.core.base.entity.AMockEntity;
import cn.lonsun.core.base.service.impl.MockService;
import cn.lonsun.core.exception.BaseRunTimeException;
import cn.lonsun.core.util.Pagination;
import cn.lonsun.core.util.TipsMode;
import cn.lonsun.system.role.internal.util.RoleAuthUtil;
import cn.lonsun.util.ChuZhouMessageBoardOpenUtil;
import cn.lonsun.util.LoginPersonUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * @ClassName: MessageBoardForwardServiceImpl
 * @Description: 留言管理业务逻辑
 */
@Service("messageBoardForwardService")
public class MessageBoardForwardServiceImpl extends MockService<MessageBoardForwardEO> implements IMessageBoardForwardService {

    @Autowired
    private IMessageBoarForwardDao forwardRecordDao;

    @Autowired
    private IMessageBoardOperationService operationService;

    @Autowired
    private IMessageBoardApplyService applyService;

    @Autowired
    private IMessageBoardService messageBoardService;

    @Override
    public Pagination getRecord(Long pageIndex, Integer pageSize, Long id) {
        Pagination page = forwardRecordDao.getRecord(pageIndex, pageSize, id);
        return page;
    }

    @Override
    public void goBack(MessageBoardForwardVO forwardVO) {
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("id", forwardVO.getId());
        map.put("recordStatus", AMockEntity.RecordStatus.Normal.toString());
        map.put("operationStatus", MessageBoardForwardEO.RecordStatus.Normal.toString());
        MessageBoardForwardEO forwardEO = getEntity(MessageBoardForwardEO.class, map);
        if (forwardEO == null) {
            throw new BaseRunTimeException(TipsMode.Message.toString(), "退回出错");
        } else {
            forwardVO.setId(forwardEO.getId());
            forwardEO.setOperationStatus(MessageBoardForwardEO.OperationStatus.Back.toString());
            updateEntity(forwardEO);
            MessageBoardOperationVO operationVO = new MessageBoardOperationVO();
            operationVO.setForwardId(forwardVO.getId());
            operationVO.setRemarks(forwardVO.getRemarks());
            operationService.goBack(operationVO);
            MessageBoardEO messageBoardEO = messageBoardService.getEntity(MessageBoardEO.class, forwardEO.getMessageBoardId());
            Integer count = 0;
            if(messageBoardEO!=null&&messageBoardEO.getForwardCount()!=null){
                count = messageBoardEO.getForwardCount();
            }
            if(count>0) {
                messageBoardEO.setForwardCount(count - 1);
                messageBoardService.updateEntity(messageBoardEO);
            }
        }

    }

    @Override
    public void recover(MessageBoardOperationVO operationVO) {
        MessageBoardForwardEO forwardEO = getEntity(MessageBoardForwardEO.class, operationVO.getForwardId());
        if (forwardEO == null) {
            throw new BaseRunTimeException(TipsMode.Message.toString(), "收回出错");
        } else {
            forwardEO.setOperationStatus(MessageBoardForwardEO.OperationStatus.Recover.toString());
            updateEntity(forwardEO);
            operationService.recover(operationVO);
            MessageBoardEO messageBoardEO = messageBoardService.getEntity(MessageBoardEO.class, forwardEO.getMessageBoardId());
            Integer count = 0;
            if(messageBoardEO!=null&&messageBoardEO.getForwardCount()!=null){
                count = messageBoardEO.getForwardCount();
            }
            if(count>0) {
                messageBoardEO.setForwardCount(count - 1);
                messageBoardService.updateEntity(messageBoardEO);
            }
        }
    }

    @Override
    public List<MessageBoardForwardVO> getAllForwardByMessageBoardId(Long messageBoardId) {
        return forwardRecordDao.getAllForwardByMessageBoardId(messageBoardId);
    }

    @Override
    public Long getCountByMessageBoardId(Long messageBoardId) {
        return forwardRecordDao.getCountByMessageBoardId(messageBoardId);
    }

    @Override
    public void forward(MessageBoardForwardEO forwardEO,Long columnId,MessageBoardEO messageBoardEO) {
        System.out.println("========================方法执行开始");
        if(RoleAuthUtil.isCurUserColumnAdmin(columnId)||LoginPersonUtil.isSiteAdmin()||LoginPersonUtil.isSuperAdmin()){
            saveEntity(forwardEO);
        }else{
            saveEntity(forwardEO);
            Map<String, Object> map = new HashMap<String, Object>();
            map.put("receiveOrganId", LoginPersonUtil.getUnitId());
            map.put("messageBoardId", forwardEO.getMessageBoardId());
            map.put("operationStatus", MessageBoardForwardEO.OperationStatus.Normal.toString());
            MessageBoardForwardEO messageBoardForwardEO = getEntity(MessageBoardForwardEO.class, map);
            //如果是普通单位转办给其他单位的话这条留言就不显示在本单位
            if (messageBoardForwardEO != null) {
                messageBoardForwardEO.setOperationStatus(MessageBoardForwardEO.OperationStatus.Forward.toString());
                updateEntity(messageBoardForwardEO);
            }
        }
        /*MessageBoardEO messageBoardEO = messageBoardService.getEntity(MessageBoardEO.class, forwardEO.getMessageBoardId());*/
        messageBoardEO.setDefaultDays(forwardEO.getDefaultDays());
        messageBoardEO.setDueDate(forwardEO.getDueDate());
        if(!StringUtils.isEmpty(forwardEO.getReceiveUnitName())||forwardEO.getReceiveOrganId()!=null){
            Integer count = 0;
            if(messageBoardEO!=null&&messageBoardEO.getForwardCount()!=null){
                count = messageBoardEO.getForwardCount();
            }
            messageBoardEO.setForwardCount(count+1);
        }
        messageBoardService.updateEntity(messageBoardEO);
        System.out.println("========================方法执行结束");
    }

    @Override
    public List<MessageBoardForwardVO> getAllUnit(Long id) {
        return forwardRecordDao.getAllUnit(id);
    }

}
