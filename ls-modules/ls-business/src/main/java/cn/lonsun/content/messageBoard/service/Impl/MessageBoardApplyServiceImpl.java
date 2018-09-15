package cn.lonsun.content.messageBoard.service.Impl;

import cn.lonsun.content.messageBoard.controller.internal.entity.MessageBoardApplyEO;
import cn.lonsun.content.messageBoard.controller.internal.entity.MessageBoardEO;
import cn.lonsun.content.messageBoard.controller.internal.entity.MessageBoardForwardEO;
import cn.lonsun.content.messageBoard.controller.vo.MessageBoardApplyVO;
import cn.lonsun.content.messageBoard.dao.IMessageBoardApplyDao;
import cn.lonsun.content.messageBoard.service.IMessageBoardApplyService;
import cn.lonsun.content.messageBoard.service.IMessageBoardForwardService;
import cn.lonsun.content.messageBoard.service.IMessageBoardService;
import cn.lonsun.core.base.entity.AMockEntity;
import cn.lonsun.core.base.service.impl.MockService;
import cn.lonsun.core.exception.BaseRunTimeException;
import cn.lonsun.core.util.IpUtil;
import cn.lonsun.core.util.Pagination;
import cn.lonsun.core.util.TipsMode;
import cn.lonsun.util.ColumnUtil;
import cn.lonsun.util.LoginPersonUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * @ClassName: MessageBoardApplyServiceImpl
 * @Description: 申请延期
 */
@Service("messageBoardApplyService")
public class MessageBoardApplyServiceImpl extends MockService<MessageBoardApplyEO> implements IMessageBoardApplyService {

    @Autowired
    private IMessageBoardApplyDao applyDao;

    @Autowired
    private IMessageBoardForwardService forwardService;

    @Autowired
    private IMessageBoardService messageBoardService;

    @Override
    public Pagination getRecord(Long pageIndex, Integer pageSize, Long id) {
        return applyDao.getRecord(pageIndex, pageSize,id);
    }

    @Override
    public void dispose(MessageBoardApplyVO applyVO) {
        MessageBoardApplyEO eo1 = getEntity(MessageBoardApplyEO.class,applyVO.getId());
        if(eo1!=null){
            eo1.setDisposeReason(applyVO.getDisposeReason());
            eo1.setUpdateDate(applyVO.getUpdateDate());
            eo1.setDisposeStatus(applyVO.getDisposeStatus());
            eo1.setDisposeIp(IpUtil.getIpAddr(LoginPersonUtil.getRequest()));
            eo1.setUpdateUserId(LoginPersonUtil.getUserId());
            eo1.setUpdateUserName(LoginPersonUtil.getPersonName());
            updateEntity(eo1);
        }else{
            throw new BaseRunTimeException(TipsMode.Message.toString(), "修改出错");
        }
        MessageBoardForwardEO forwardEO=forwardService.getEntity(MessageBoardForwardEO.class,eo1.getForwardId());
        if(forwardEO!=null&&forwardEO.getDueDate()!=null){
            Date dueDate=forwardEO.getDueDate();
            Calendar cal = Calendar.getInstance();
            cal.setTime(dueDate);
            int day = 0;
            while (day<eo1.getApplyDays()) {
                if (dueDate.getDay() != 6 && dueDate.getDay() != 0){
                    day++;
                }
                dueDate.setDate(dueDate.getDate() + 1);
            }
            forwardEO.setDueDate(dueDate);
            forwardService.updateEntity(forwardEO);
        }
        MessageBoardEO messageBoardEO=messageBoardService.getEntity(MessageBoardEO.class,forwardEO.getMessageBoardId());
        if(messageBoardEO!=null){
            messageBoardEO.setDefaultDays(forwardEO.getDefaultDays());
            messageBoardEO.setDueDate(forwardEO.getDueDate());
            messageBoardService.updateEntity(messageBoardEO);
        }
    }

    @Override
    public Pagination getPage(MessageBoardApplyVO pageVO) {
        Pagination page = applyDao.getPage(pageVO);
        if (page != null && page.getData() != null && page.getData().size() > 0) {
            List<MessageBoardApplyVO> list = (List<MessageBoardApplyVO>) page.getData();
            if (list!=null &&list.size()>0) {
                for (int i = 0; i < list.size(); i++) {
                    list.get(i).setColumnName(ColumnUtil.getColumnName(list.get(i).getColumnId(), list.get(i).getSiteId()));
                }
            }
        }
        return page;
    }

    @Override
    public Long getCount(String status) {
        return applyDao.getCount(status);
    }

    @Override
    public MessageBoardApplyEO apply(MessageBoardApplyVO vo) {

        Map<String ,Object> parmas = new HashMap<String ,Object>();
        parmas.put("messageBoardId",vo.getMessageBoardId());
        parmas.put("createUserId",LoginPersonUtil.getUserId());
        parmas.put("recordStatus", AMockEntity.RecordStatus.Normal.toString());

        MessageBoardApplyEO eo1 = getEntity(MessageBoardApplyEO.class,parmas);

        Map<String ,Object> map = new HashMap<String ,Object>();
        map.put("messageBoardId",vo.getMessageBoardId());
        map.put("receiveOrganId",LoginPersonUtil.getUnitId());
        map.put("recordStatus", AMockEntity.RecordStatus.Normal.toString());
        map.put("operationStatus", MessageBoardForwardEO.OperationStatus.Normal.toString());

        MessageBoardForwardEO forwardEO = forwardService.getEntity(MessageBoardForwardEO.class,map);

        if(forwardEO==null){
            throw new BaseRunTimeException(TipsMode.Message.toString(), "申请出错");
        }

        if(eo1 !=null) {
            eo1.setUpdateDate(new Date());
            eo1.setUpdateUserId(LoginPersonUtil.getUserId());
            eo1.setMessageBoardId(vo.getMessageBoardId());
            eo1.setForwardId(forwardEO.getId());
            eo1.setApplyDays(vo.getApplyDays());
            eo1.setApplyName(LoginPersonUtil.getPersonName());
            eo1.setApplyIp(IpUtil.getIpAddr(LoginPersonUtil.getRequest()));
            eo1.setApplyReason(vo.getApplyReason());
            eo1.setApplyOrganName(LoginPersonUtil.getOrganName());
            eo1.setCreateUserId(LoginPersonUtil.getUserId());
            eo1.setDisposeStatus(MessageBoardApplyEO.DisposeStatus.disposeWait.toString());
            updateEntity(eo1);
        }else{
            eo1 = new MessageBoardApplyEO();
            eo1.setForwardId(forwardEO.getId());
            eo1.setMessageBoardId(vo.getMessageBoardId());
            eo1.setCreateDate(new Date());
            eo1.setApplyDays(vo.getApplyDays());
            eo1.setApplyName(LoginPersonUtil.getPersonName());
            eo1.setApplyReason(vo.getApplyReason());
            eo1.setApplyOrganName(LoginPersonUtil.getOrganName());
            eo1.setApplyIp(IpUtil.getIpAddr(LoginPersonUtil.getRequest()));
            eo1.setCreateUserId(LoginPersonUtil.getUserId());
            eo1.setDisposeStatus(MessageBoardApplyEO.DisposeStatus.disposeWait.toString());
            saveEntity(eo1);
        }
        return eo1;
    }

}
