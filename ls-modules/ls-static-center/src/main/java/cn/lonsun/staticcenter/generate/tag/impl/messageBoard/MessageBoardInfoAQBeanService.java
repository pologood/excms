package cn.lonsun.staticcenter.generate.tag.impl.messageBoard;

import cn.lonsun.cache.client.CacheHandler;
import cn.lonsun.common.util.AppUtil;
import cn.lonsun.content.internal.entity.BaseContentEO;
import cn.lonsun.content.internal.service.IBaseContentService;
import cn.lonsun.content.messageBoard.controller.internal.entity.MessageBoardEO;
import cn.lonsun.content.messageBoard.controller.internal.entity.MessageBoardForwardEO;
import cn.lonsun.content.messageBoard.controller.internal.entity.MessageBoardReplyEO;
import cn.lonsun.content.messageBoard.controller.vo.MessageBoardEditVO;
import cn.lonsun.content.messageBoard.controller.vo.MessageBoardForwardVO;
import cn.lonsun.content.messageBoard.controller.vo.MessageBoardReplyVO;
import cn.lonsun.content.messageBoard.dao.IMessageBoarForwardDao;
import cn.lonsun.content.messageBoard.dao.IMessageBoardDao;
import cn.lonsun.content.messageBoard.dao.IMessageBoardReplyDao;
import cn.lonsun.content.messageBoard.service.IMessageBoardForwardService;
import cn.lonsun.content.messageBoard.service.IMessageBoardReplyService;
import cn.lonsun.content.messageBoard.service.IMessageBoardService;
import cn.lonsun.core.base.entity.AMockEntity;
import cn.lonsun.rbac.internal.entity.OrganEO;
import cn.lonsun.site.contentModel.vo.ColumnTypeConfigVO;
import cn.lonsun.staticcenter.generate.tag.impl.AbstractBeanService;
import cn.lonsun.staticcenter.generate.thread.Context;
import cn.lonsun.staticcenter.generate.thread.ContextHolder;
import cn.lonsun.system.datadictionary.vo.DataDictVO;
import cn.lonsun.system.member.internal.service.IMemberService;
import cn.lonsun.util.DataDictionaryUtil;
import cn.lonsun.util.ModelConfigUtil;
import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 */
@Component
public class MessageBoardInfoAQBeanService extends AbstractBeanService {

    @Autowired
    private IBaseContentService contentService;

    @Autowired
    private IMessageBoardService messageBoardService;

    @Autowired
    private IMessageBoardForwardService forwardRecordService;

    @Autowired
    private IMessageBoardReplyService replyService;

    @Override
    public Object getObject(JSONObject paramObj) {
        Context context = ContextHolder.getContext();

        String action = context.getParamMap().get("action");
        if (AppUtil.isEmpty(action)) {
            action = paramObj.getString("action");
        }

        String id = context.getParamMap().get("id");

        Long contentId = null;

        if (org.apache.commons.lang3.StringUtils.isEmpty(id)) {
            contentId = context.getContentId();
        } else {
            contentId = Long.parseLong(id);
        }

        Map<String, Object> result = new HashMap<String, Object>();

        if (AppUtil.isEmpty(contentId) || contentId == 0) {
            return "1";
        }
        BaseContentEO contentEO = contentService.getEntity(BaseContentEO.class, contentId);
        if (null == contentEO) {
            return "1";
        }

        Map<String, Object> map = new HashMap<String, Object>();
        map.put("baseContentId", contentId);
       MessageBoardEO messageBoardEO = messageBoardService.getEntity(MessageBoardEO.class, map);
        if (null == messageBoardEO) {
            return "1";
        }
        MessageBoardEditVO vo = new MessageBoardEditVO();
        AppUtil.copyProperties(vo, messageBoardEO);
        vo.setIsPublish(contentEO.getIsPublish());
        vo.setTitle(contentEO.getTitle());
        vo.setColumnId(contentEO.getColumnId());
        vo.setSiteId(contentEO.getSiteId());
        ColumnTypeConfigVO configVO = ModelConfigUtil.getCongfigVO(contentEO.getColumnId(), contentEO.getSiteId());
        if (configVO != null) {
            if (configVO.getRecType() != null && configVO.getRecType() != 2) {
                if (configVO.getRecType().equals(0)) {
                    if (vo.getReceiveUnitId() != null) {
                        OrganEO organEO = CacheHandler.getEntity(OrganEO.class, vo.getReceiveUnitId());
                        if (organEO != null) {
                            vo.setReceiveUnitName(organEO.getName());
                        }
                    }
                } else {
                    if (!org.apache.commons.lang3.StringUtils.isEmpty(vo.getReceiveUserCode())) {
                        DataDictVO dictVO = DataDictionaryUtil.getItem("guest_book_rec_users", vo.getReceiveUserCode());
                        if (dictVO != null) {
                            vo.setReceiveUserName(dictVO.getKey());
                        }
                    }
                }
            }
            vo.setRecType(configVO.getRecType());
        }
        if (!org.apache.commons.lang3.StringUtils.isEmpty(vo.getCommentCode())) {
            DataDictVO dictVO = DataDictionaryUtil.getItem("guest_comment", vo.getCommentCode());
            if (dictVO != null) {
                vo.setCommentName(dictVO.getKey());
            }
        } else {
            DataDictVO dictVO = DataDictionaryUtil.getDefuatItem("guest_comment", vo.getSiteId());
            if (dictVO != null) {
                vo.setCommentCode(dictVO.getCode());
            }
        }
        if (!org.apache.commons.lang3.StringUtils.isEmpty(vo.getClassCode())) {
            DataDictVO dictVO = DataDictionaryUtil.getItem("petition_purpose", vo.getClassCode());
            if (dictVO != null) {
                vo.setClassName(dictVO.getKey());
            }
        }

        List<MessageBoardForwardVO> forwardVOList = forwardRecordService.getAllForwardByMessageBoardId(messageBoardEO.getId());

        String receiveUnitNames = "";
        String recUserNames = "";
        if (configVO.getRecType().equals(0)) {//选择受理单位
            for (int i = 0; i < forwardVOList.size(); i++) {
                if (i == 0) {
                    receiveUnitNames = receiveUnitNames + forwardVOList.get(i).getReceiveUnitName();
                } else {
                    receiveUnitNames = receiveUnitNames + ',' + forwardVOList.get(i).getReceiveUnitName();
                }
            }
            result.put("receiveUnitNames", receiveUnitNames);
        } else {//选择受理人
            for (int i = 0; i < forwardVOList.size(); i++) {
                if (i == 0) {
                    recUserNames = recUserNames + forwardVOList.get(i).getReceiveUserName();
                } else {
                    recUserNames = recUserNames + ',' + forwardVOList.get(i).getReceiveUserName();
                }
            }
            result.put("recUserNames", recUserNames);
        }

        Map<String, Object> parms = new HashMap<String, Object>();
        parms.put("messageBoardId", vo.getId());
        parms.put("recordStatus", MessageBoardReplyEO.RecordStatus.Normal.toString());
        List<MessageBoardReplyEO> replyVOList  = replyService.getEntities(MessageBoardReplyEO.class, parms);

        result.put("replyVOList", replyVOList);

        if (configVO != null) {
            result.put("isAssess", configVO.getIsAssess());
        }

        List<DataDictVO> commentList = DataDictionaryUtil.getItemList("guest_comment", vo.getSiteId());
        result.put("commentList", commentList);

        result.put("recType", configVO.getRecType());
        result.put("action", action);
        result.put("vo", vo);
        return result;
    }
}
