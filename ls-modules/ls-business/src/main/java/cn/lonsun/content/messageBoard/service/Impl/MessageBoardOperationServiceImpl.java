package cn.lonsun.content.messageBoard.service.Impl;

import cn.lonsun.common.util.AppUtil;
import cn.lonsun.content.messageBoard.controller.internal.entity.MessageBoardForwardEO;
import cn.lonsun.content.messageBoard.controller.internal.entity.MessageBoardOperationEO;
import cn.lonsun.content.messageBoard.controller.vo.MessageBoardOperationVO;
import cn.lonsun.content.messageBoard.dao.IMessageBoardOperationDao;
import cn.lonsun.content.messageBoard.service.IMessageBoardOperationService;
import cn.lonsun.core.base.service.impl.MockService;
import cn.lonsun.core.util.IpUtil;
import cn.lonsun.core.util.Pagination;
import cn.lonsun.util.ColumnUtil;
import cn.lonsun.util.LoginPersonUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @ClassName: MessageBoardOperationServiceImpl
 * @Description: 留言管理操作记录
 */
@Service("messageBoardOperationService")
public class MessageBoardOperationServiceImpl extends MockService<MessageBoardOperationEO> implements IMessageBoardOperationService {
        @Autowired
        private IMessageBoardOperationDao messageBoardOperationDao;


        @Override
        public void goBack(MessageBoardOperationVO operationVO) {
                MessageBoardOperationEO operationEO = new MessageBoardOperationEO();
                AppUtil.copyProperties(operationEO,operationVO);
                operationEO.setCreateOrganName(LoginPersonUtil.getOrganName());
                operationEO.setIp(IpUtil.getIpAddr(LoginPersonUtil.getRequest()));
                operationEO.setUsername(LoginPersonUtil.getPersonName());
                operationEO.setCreateUserId(LoginPersonUtil.getUserId());
                operationEO.setOperationStatus(MessageBoardForwardEO.OperationStatus.Back.toString());
                saveEntity(operationEO);
        }

        @Override
        public void recover(MessageBoardOperationVO operationVO) {

                MessageBoardOperationEO operationEO = new MessageBoardOperationEO();
                AppUtil.copyProperties(operationEO,operationVO);
                operationEO.setCreateOrganName(LoginPersonUtil.getOrganName());
                operationEO.setIp(IpUtil.getIpAddr(LoginPersonUtil.getRequest()));
                operationEO.setUsername(LoginPersonUtil.getPersonName());
                operationEO.setCreateUserId(LoginPersonUtil.getUserId());
                operationEO.setOperationStatus(MessageBoardForwardEO.OperationStatus.Recover.toString());
                saveEntity(operationEO);

        }

        @Override
        public Pagination getPage(MessageBoardOperationVO pageVO) {
                Pagination page = messageBoardOperationDao.getPage(pageVO);

                if (page != null && page.getData() != null && page.getData().size() > 0) {
                        List<MessageBoardOperationVO> list = (List<MessageBoardOperationVO>) page.getData();
                        if (list!=null &&list.size()>0) {
                                for (int i = 0; i < list.size(); i++) {
                                        list.get(i).setColumnName(ColumnUtil.getColumnName(list.get(i).getColumnId(), list.get(i).getSiteId()));
                                }
                        }
                }
                return page;
        }

        @Override
        public Long getCount(String type) {
                return messageBoardOperationDao.getCount(type);
        }

}
