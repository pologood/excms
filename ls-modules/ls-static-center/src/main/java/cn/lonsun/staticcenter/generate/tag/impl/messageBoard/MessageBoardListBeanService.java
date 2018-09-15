package cn.lonsun.staticcenter.generate.tag.impl.messageBoard;

import cn.lonsun.cache.client.CacheHandler;
import cn.lonsun.common.util.AppUtil;
import cn.lonsun.content.internal.entity.BaseContentEO;
import cn.lonsun.content.messageBoard.controller.internal.entity.MessageBoardForwardEO;
import cn.lonsun.content.messageBoard.controller.vo.MessageBoardEditVO;
import cn.lonsun.content.messageBoard.controller.vo.MessageBoardForwardVO;
import cn.lonsun.content.messageBoard.controller.vo.MessageBoardReplyVO;
import cn.lonsun.content.messageBoard.dao.IMessageBoarForwardDao;
import cn.lonsun.content.messageBoard.dao.IMessageBoardDao;
import cn.lonsun.content.messageBoard.service.IMessageBoardForwardService;
import cn.lonsun.content.messageBoard.service.IMessageBoardReplyService;
import cn.lonsun.core.base.entity.AMockEntity;
import cn.lonsun.core.util.ContextHolderUtils;
import cn.lonsun.indicator.internal.entity.IndicatorEO;
import cn.lonsun.rbac.internal.entity.OrganEO;
import cn.lonsun.site.contentModel.vo.ColumnTypeConfigVO;
import cn.lonsun.site.contentModel.vo.ContentModelParaVO;
import cn.lonsun.staticcenter.generate.GenerateConstant;
import cn.lonsun.staticcenter.generate.GenerateException;
import cn.lonsun.staticcenter.generate.tag.impl.AbstractBeanService;
import cn.lonsun.staticcenter.generate.thread.Context;
import cn.lonsun.staticcenter.generate.thread.ContextHolder;
import cn.lonsun.staticcenter.generate.util.PathUtil;
import cn.lonsun.staticcenter.service.HtmlEnum;
import cn.lonsun.system.member.vo.MemberSessionVO;
import cn.lonsun.util.LoginPersonUtil;
import cn.lonsun.util.ModelConfigUtil;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class MessageBoardListBeanService extends AbstractBeanService {

    @Autowired
    private IMessageBoardDao messageBoardDao;

    @Autowired
    private IMessageBoardForwardService forwardService;
    @Autowired
    private IMessageBoardReplyService replyService;

    @SuppressWarnings("unchecked")
    @Override
    public Object getObject(JSONObject paramObj) {
        Long[] ids = super.getQueryColumnIdByChild(paramObj, BaseContentEO.TypeCode.messageBoard.toString());
        if (null == ids) {
            return null;
        }
        Context context = ContextHolder.getContext();
        String recUnitId = context.getParamMap().get("organId");
        Long siteId = paramObj.getLong("siteId");
        if (siteId == null || siteId == 0) {
            siteId = context.getSiteId();
        }
        Integer size = Integer.valueOf(ids.length);
        String id = "";
        if (size > 1) {
            for (int i = 0; i < size; i++) {
                if (i < size - 1) {
                    id = id + ids[i] + ',';
                }
                if (i == size - 1) {
                    id += ids[i];
                }
            }
        }
        //留言类型
        String classCode = context.getParamMap().get("classCode");
        if(StringUtils.isEmpty(classCode)){
            classCode = paramObj.getString("classCode");
        }
        //是否回复
        String isReply = context.getParamMap().get("isReply");
        Integer isReplyed=null;
        if(cn.lonsun.core.base.util.StringUtils.isEmpty(isReply)){
            isReplyed = paramObj.getInteger("isReply");
        }else{
            isReplyed=Integer.parseInt(isReply);
        }
        //排序方式
        String type = context.getParamMap().get("type");
        Integer orderType=null;
        if(cn.lonsun.core.base.util.StringUtils.isEmpty(type)){
            orderType = paramObj.getInteger("type");
        }else{
            orderType=Integer.parseInt(type);
        }
        // 需要显示条数.
        Integer num = paramObj.getInteger(GenerateConstant.NUM);

        StringBuffer hql = new StringBuffer("select c.title as title,c.publishDate as publishDate,c.createDate as createDate, c.isPublish as isPublish,c.columnId as columnId,c.siteId as siteId")
                .append(",c.id as baseContentId,m.id as id,m.messageBoardContent as messageBoardContent,m.addDate as addDate,m.replyDate as replyDate")
                .append(",m.resourceType as resourceType,m.openId as openId,m.personIp as personIp,m.personName as personName,m.dealStatus as dealStatus")
                .append(",m.docNum as docNum,m.className as className,c.hit as hit")
                .append(",m.classCode as classCode,m.randomCode as randomCode")
                .append(",m.isPublic as isPublic,m.isPublicInfo as isPublicInfo")
                .append(" from BaseContentEO c,MessageBoardEO m where m.baseContentId=c.id and c.recordStatus='" + AMockEntity.RecordStatus.Normal.toString() + "'")
                .append(" and m.recordStatus='" + AMockEntity.RecordStatus.Normal.toString() + "'" + " and c.typeCode = '" + BaseContentEO.TypeCode.messageBoard.toString() + "'")
                .append(" and c.isPublish=1  and m.isPublic =1")
                .append(" and c.columnId").append(size == 1 ? "=" + ids[0] : " in (" + id + ") ");

                Map<String, Object> map = new HashMap<String, Object>();

                hql.append(" and c.siteId=:siteId");
                map.put("siteId", siteId);

        if (!org.apache.commons.lang3.StringUtils.isEmpty(recUnitId)) {
            Long[] recUnitIds = AppUtil.getLongs(recUnitId, ",");
            hql.append(" and(m.id in(select f.messageBoardId from MessageBoardForwardEO f where f.operationStatus =:operationStatus and  f.receiveOrganId").append( " in (:recUnitIds) ))");
            map.put("operationStatus", MessageBoardForwardEO.OperationStatus.Normal.toString());
            map.put("recUnitIds", recUnitIds);

        }

        if (!cn.lonsun.core.base.util.StringUtils.isEmpty(classCode)) {
            hql.append(" and m.classCode=:classCode");
            map.put(classCode,classCode);
        }

        if (isReplyed!=null) {
            if(isReplyed==1){//已回复
                hql.append(" and m.dealStatus in('replyed','handled') ");
            }else if(isReplyed==2){//未回复
                hql.append(" and (m.dealStatus is null or m.dealStatus='unreply') ");
            }else{

            }
        }

        if(orderType==null){
            hql.append(" order by m.createDate desc");
        }else{
            if (orderType==1) {
                hql.append(" order by c.hit desc");
            } else if (orderType==2) {
                hql.append(" order by c.publishDate desc");
            }else if (orderType==3) {
                hql.append(" order by m.replyDate desc");
            }else {
                hql.append(" order by m.createDate desc");
            }
        }

        List<MessageBoardEditVO> list = (List<MessageBoardEditVO>) messageBoardDao.getBeansByHql(hql.toString(), map, MessageBoardEditVO.class, num);

        if (null != list && list.size() > 0) {
            for (int i = 0; i < list.size(); i++) {
                list.get(i).setLink(PathUtil.getLinkPath(HtmlEnum.CONTENT.getValue(), HtmlEnum.ACRTILE.getValue(), list.get(i).getColumnId(), list.get(i).getBaseContentId()));
                //转办
                List<MessageBoardForwardVO> forwardVOList = forwardService.getAllUnit(list.get(i).getId());
                //回复
                List<MessageBoardReplyVO> replyVOList = replyService.getAllDealReply(list.get(i).getId());
                String recUnitNames="";
                if(null != replyVOList && replyVOList.size() > 0) {
                    for(MessageBoardReplyVO replyVO:replyVOList){
                        if(!cn.lonsun.core.base.util.StringUtils.isEmpty(replyVO.getReceiveName())&&!recUnitNames.contains(replyVO.getReceiveName()+",")){
                            recUnitNames+=replyVO.getReceiveName()+",";
                        }else{
                            OrganEO organEO = CacheHandler.getEntity(OrganEO.class, replyVO.getCreateOrganId());
                            if (organEO != null) {
                                recUnitNames+=organEO.getName()+",";
                            }
                        }
                    }
                    if(!cn.lonsun.core.base.util.StringUtils.isEmpty(recUnitNames)){
                        recUnitNames=recUnitNames.substring(0,recUnitNames.length()-1);
                        list.get(i).setReceiveUnitName(recUnitNames);
                    }else{
                        list.get(i).setReceiveUnitName("暂无接收单位");
                    }
                }else if (forwardVOList != null && forwardVOList.size() > 0 ) {
                    for(MessageBoardForwardVO forwardVO:forwardVOList){
                        if (!cn.lonsun.core.base.util.StringUtils.isEmpty(forwardVO.getReceiveUnitName())&&!recUnitNames.contains(forwardVO.getReceiveUnitName()+",")) {
                            recUnitNames+=forwardVO.getReceiveUnitName()+",";
                        } else {
                            OrganEO organEO = CacheHandler.getEntity(OrganEO.class, forwardVO.getReceiveOrganId());
                            if (organEO != null) {
                                recUnitNames+=organEO.getName()+",";
                            }
                        }
                    }
                    if(!cn.lonsun.core.base.util.StringUtils.isEmpty(recUnitNames)){
                        recUnitNames=recUnitNames.substring(0,recUnitNames.length()-1);
                        list.get(i).setReceiveUnitName(recUnitNames);
                    }else{
                        list.get(i).setReceiveUnitName("暂无接收单位");
                    }
                }else if(forwardVOList==null||forwardVOList.size()==0) {
                    list.get(i).setReceiveUnitName("暂无接收单位");
                }
            }
        }
        return list;
    }


}


