package cn.lonsun.staticcenter.generate.tag.impl.messageBoard;

import cn.lonsun.cache.client.CacheHandler;
import cn.lonsun.common.util.AppUtil;
import cn.lonsun.content.internal.entity.BaseContentEO;
import cn.lonsun.content.internal.service.IBaseContentService;
import cn.lonsun.content.messageBoard.controller.internal.entity.MessageBoardEO;
import cn.lonsun.content.messageBoard.controller.internal.entity.MessageBoardReplyEO;
import cn.lonsun.content.messageBoard.controller.vo.MessageBoardEditVO;
import cn.lonsun.content.messageBoard.controller.vo.MessageBoardForwardVO;
import cn.lonsun.content.messageBoard.controller.vo.MessageBoardReplyVO;
import cn.lonsun.content.messageBoard.dao.IMessageBoarForwardDao;
import cn.lonsun.content.messageBoard.dao.IMessageBoardDao;
import cn.lonsun.content.messageBoard.service.IMessageBoardForwardService;
import cn.lonsun.content.messageBoard.service.IMessageBoardReplyService;
import cn.lonsun.content.messageBoard.service.IMessageBoardService;
import cn.lonsun.core.base.entity.AMockEntity;
import cn.lonsun.core.base.util.StringUtils;
import cn.lonsun.core.util.ContextHolderUtils;
import cn.lonsun.core.util.Pagination;
import cn.lonsun.core.util.SqlUtil;
import cn.lonsun.indicator.internal.entity.IndicatorEO;
import cn.lonsun.rbac.internal.entity.OrganEO;
import cn.lonsun.site.contentModel.vo.ColumnTypeConfigVO;
import cn.lonsun.site.contentModel.vo.ContentModelParaVO;
import cn.lonsun.staticcenter.generate.GenerateConstant;
import cn.lonsun.staticcenter.generate.tag.impl.AbstractBeanService;
import cn.lonsun.staticcenter.generate.thread.Context;
import cn.lonsun.staticcenter.generate.thread.ContextHolder;
import cn.lonsun.staticcenter.generate.util.PathUtil;
import cn.lonsun.staticcenter.service.HtmlEnum;
import cn.lonsun.system.datadictionary.vo.DataDictVO;
import cn.lonsun.system.member.internal.service.IMemberService;
import cn.lonsun.system.member.vo.MemberSessionVO;
import cn.lonsun.util.DataDictionaryUtil;
import cn.lonsun.util.ModelConfigUtil;
import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

@Component
public class MessageBoardPageListBeanService extends AbstractBeanService {

    @Autowired
    private IMessageBoardDao messageBoardDao;

    @Autowired
    private IMessageBoarForwardDao forwardDao;

    @Autowired
    private IMemberService memberService;

    @Autowired
    private IBaseContentService contentService;

    @Autowired
    private IMessageBoardService messageBoardService;

    @Autowired
    private IMessageBoardForwardService forwardRecordService;

    @Autowired
    private IMessageBoardReplyService replyService;

    @SuppressWarnings("unchecked")
    @Override
    public Object getObject(JSONObject paramObj) {
        Context context = ContextHolder.getContext();
        List<Long> ids = new ArrayList<Long>();
        String typeStr = context.getParamMap().get("type");
        Long columnId = paramObj.getLong(GenerateConstant.ID);
        if (null == columnId || columnId == 0) {// 如果栏目id为空,说明栏目id是在页面传入的
            columnId = context.getColumnId();
        }

        //如果ID全为空取地址栏参数
        if (AppUtil.isEmpty(columnId)) {
            String columnIdStr = context.getParamMap().get("columnId");
            if (!org.apache.commons.lang3.StringUtils.isEmpty(columnIdStr)) {
                columnId = Long.parseLong(columnIdStr);
            }
        }

        String classCode = context.getParamMap().get("classCode");
        if (org.apache.commons.lang3.StringUtils.isEmpty(classCode)) {
            String typeS = context.getParamMap().get("type");
            Integer type = null;
            if (org.apache.commons.lang3.StringUtils.isEmpty(typeS)) {
                type = paramObj.getInteger("type");
            } else {
                type = Integer.parseInt(typeS);
            }
            if (columnId != null && columnId != 0) {
                List<ContentModelParaVO> codeList = ModelConfigUtil.getGuestBookType(columnId, context.getSiteId());
                if (codeList != null && codeList.size() > 0) {
                    if (type != null) {
                        if (type > 0 && type <= codeList.size()) {
                            classCode = codeList.get(type - 1).getClassCode();
                        }
                    }
                }
            }
        }
        String organIdStr = context.getParamMap().get("organId");
        Long organId = null;
        if (!org.apache.commons.lang3.StringUtils.isEmpty(organIdStr)) {
            organId = Long.parseLong(organIdStr);
        }

        // 默认查询本栏目
        ids.add(columnId);

        String action = context.getParamMap().get("action");
        if (AppUtil.isEmpty(action)) {
            action = paramObj.getString("action");
        }

        if ("singleList".equals(action)) {
            return getPage(paramObj, columnId, null, action);
        }
        if ("detail".equals(action)) {
            String id = context.getParamMap().get("id");
            String isPublish = context.getParamMap().get("isPublish");
            Long contentId = null;
            if (org.apache.commons.lang3.StringUtils.isEmpty(id)) {
                contentId = context.getContentId();
            } else {
                contentId = Long.parseLong(id);
            }
            return getDetail(contentId, action, isPublish);
        }
        if ("new".equals(action)) {
            String recUserCode = context.getParamMap().get("recUserCode");
            return getNew(columnId, classCode, organId, action ,recUserCode);
        }
        if ("search".equals(action)) {
            return serachEO();
        } else {
            return getPage(paramObj, columnId, null, action);
        }
    }


    private Object serachEO() {

        Map<String, Object> result = new HashMap<String, Object>();
        Context context = ContextHolder.getContext();
        Long siteId = context.getSiteId();
        if (siteId == null) {
            String siteIdStr = context.getParamMap().get("siteId");
            if (!org.apache.commons.lang3.StringUtils.isEmpty(siteIdStr)) {
                siteId = Long.parseLong(siteIdStr);
            }
        }
        if (siteId == null) {
            return "2";
        }

        String randomCode = context.getParamMap().get("randomCode");
        String docNum = context.getParamMap().get("docNum");
        if (org.apache.commons.lang3.StringUtils.isEmpty(randomCode) || org.apache.commons.lang3.StringUtils.isEmpty(docNum)) {
            return "0";
        }
        randomCode = randomCode.trim();
        docNum = docNum.trim();
        MessageBoardEditVO vo = messageBoardService.searchEO(randomCode, docNum, siteId);

        String receiveUnitNames = "";
        if (vo != null) {
            List<MessageBoardForwardVO> forwardVOList = forwardRecordService.getAllUnit(vo.getId());
            for (int i = 0; i < forwardVOList.size(); i++) {
                if (i == 0) {
                    receiveUnitNames = receiveUnitNames + forwardVOList.get(i).getReceiveUnitName();
                } else {
                    receiveUnitNames = receiveUnitNames + ',' + forwardVOList.get(i).getReceiveUnitName();
                }
            }
            result.put("receiveUnitNames", receiveUnitNames);
        }
        if (vo == null || vo.getBaseContentId() == null) {
            return "0";
        }

        result.put("vo", vo);
        return result;
    }

    private Object getPage(JSONObject paramObj, Long columnId, String classCode, String action) {

        Map<String, Object> result = new HashMap<String, Object>();
        Context context = ContextHolder.getContext();
        Long pageIndex = context.getPageIndex() == null ? 0 : (context.getPageIndex() - 1);
        Integer pageSize = paramObj.getInteger(GenerateConstant.PAGE_SIZE);

        Map<String, String> map = context.getParamMap();
        String type = map.get("type");
        Integer orderType=null;
        if(StringUtils.isEmpty(type)){
            orderType = paramObj.getInteger("type");
        }else{
            orderType=Integer.parseInt(type);
        }
        String isReply = map.get("isReply");
        Integer isReplyed=null;
        if(StringUtils.isEmpty(isReply)){
            isReplyed = paramObj.getInteger("isReply");
        }else{
            isReplyed=Integer.parseInt(isReply);
        }

        String organId = map.get("organId");
        String title = map.get("title");
        String startDate = map.get("startDate");
        String endDate = map.get("endDate");
        String idNum = map.get("idNum");
        classCode = map.get("classCode");
        String localUnitId = map.get("localUnitId");
//        String createUserId = "";
//        if (!org.apache.commons.lang3.StringUtils.isEmpty(idNum)) {
//            List<MemberEO> mList = memberService.getByNumber(idNum, context.getSiteId());
//            if (mList != null && mList.size() > 0) {
//                for (MemberEO eo : mList) {
//                    createUserId += eo.getId() + ",";
//                }
//                createUserId = createUserId.substring(0, createUserId.length() - 1);
//            }
//            if (org.apache.commons.lang3.StringUtils.isEmpty(createUserId)) {
//                Pagination p = new Pagination();
//                p.setPageSize(pageSize);
//                result.put("msgPage", "<div>暂无相关信息！</div>");
//                result.put("idNum", idNum);
//                result.put("action", action);
//                return result;
//            }
//        }

        StringBuffer hql = new StringBuffer("select c.title as title,c.publishDate as publishDate,c.createDate as createDate, c.isPublish as isPublish,c.columnId as columnId,c.siteId as siteId")
                .append(",c.id as baseContentId,m.id as id,m.messageBoardContent as messageBoardContent,m.addDate as addDate,m.replyDate as replyDate")
                .append(",m.resourceType as resourceType,m.openId as openId,m.personIp as personIp,m.personName as personName,m.dealStatus as dealStatus")
                .append(",m.docNum as docNum,m.className as className,c.hit as hit")
                .append(",m.classCode as classCode,m.randomCode as randomCode")
                .append(",m.isPublic as isPublic,m.isPublicInfo as isPublicInfo")
                .append(" from BaseContentEO c,MessageBoardEO m where m.baseContentId=c.id and c.recordStatus='" + AMockEntity.RecordStatus.Normal.toString() + "'")
                .append(" and c.isPublish=1 and m.isPublic =1")
                .append(" and m.recordStatus='" + AMockEntity.RecordStatus.Normal.toString() + "'")
                .append(" and c.typeCode = '" + BaseContentEO.TypeCode.messageBoard.toString() + "'");
        List<Object> values = new ArrayList<Object>();
        Long siteId = paramObj.getLong("siteId");
        if (siteId == null) {
            siteId = context.getSiteId();
        }
        if (siteId != null && !AppUtil.isEmpty(siteId)) {
            hql.append(" and c.siteId =?");
            values.add(siteId);
        }
        if (columnId != null && !AppUtil.isEmpty(columnId)) {
            hql.append(" and c.columnId =?");
            values.add(columnId);
        }
        if (!org.apache.commons.lang3.StringUtils.isEmpty(localUnitId)) {
            hql.append(" and m.id in(select f.messageBoardId from MessageBoardForwardEO f where f.localUnitId = ?)");
            values.add(localUnitId);
        }
        if (!org.apache.commons.lang3.StringUtils.isEmpty(organId)) {
            hql.append(" and m.id in(select f.messageBoardId from MessageBoardForwardEO f where f.receiveOrganId =? ")
                    .append(" and f.operationStatus=? and f.recordStatus=? )");
            values.add(Long.valueOf(organId));
            values.add(AMockEntity.RecordStatus.Normal.toString());
            values.add(AMockEntity.RecordStatus.Normal.toString());
        }



        if (!org.apache.commons.lang3.StringUtils.isEmpty(title)) {
            hql.append(" and c.title like ? ");
            values.add("%" + title + "%");
        }
        if (!StringUtils.isEmpty(startDate)) {
            hql.append(" and m.createDate>=? ");
            values.add(toDate(startDate));
        }
        if (!StringUtils.isEmpty(endDate)) {
            hql.append(" and m.createDate<= ? ");
            values.add(toDate(endDate));
//            Date date = toDate(endDate);
//            Calendar calendar = Calendar.getInstance();
//            calendar.setTime(date);
//            calendar.set(Calendar.DATE, calendar.get(Calendar.DATE) + 1);//结束日期增加一天
//            values.add(calendar.getTime());
        }

        if (!StringUtils.isEmpty(classCode)) {
            hql.append(" and m.classCode=?");
            values.add(classCode);
        }
        if (isReplyed!=null) {
            if(isReplyed==1){//已回复
                hql.append(" and m.dealStatus in('replyed','handled') ");
            }else if(isReplyed==2){//未回复
                hql.append(" and (m.dealStatus is null or m.dealStatus='unreply') ");
            }
        }

        if (!StringUtils.isEmpty(idNum)) {
//            hql.append(" and m.createUserId in(" + createUserId + ")");
            hql.append(" and m.createUserId in(select u.id from MemberEO u " +
                    "where u.recordStatus = ? and u.siteId = ? and (u.idCard = ? or u.phone = ?))");
            values.add(AMockEntity.RecordStatus.Normal.toString());
            values.add(siteId);
            values.add(SqlUtil.prepareParam4Query(idNum));
            values.add(SqlUtil.prepareParam4Query(idNum));
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

        Pagination page = messageBoardDao.getPagination(pageIndex, pageSize, hql.toString(), values.toArray(), MessageBoardEditVO.class);
        List<MessageBoardEditVO> list = (List<MessageBoardEditVO>) page.getData();
        if(!(list != null && list.size() > 0)){
            result.put("msgPage", "<div>暂无相关信息！</div>");
            result.put("idNum", idNum);
            result.put("action", action);
            return result;
        }
        String path = PathUtil.getLinkPath(HtmlEnum.CONTENT.getValue(), HtmlEnum.COLUMN.getValue(), context.getColumnId(), null);
        page.setLinkPrefix(path);
        if (null != list && list.size() > 0) {
            for (int i = 0; i < list.size(); i++) {
                //链接地址
                list.get(i).setLink(PathUtil.getLinkPath(HtmlEnum.CONTENT.getValue(), HtmlEnum.ACRTILE.getValue(), list.get(i).getColumnId(), list.get(i).getBaseContentId()));
                //转办
                List<MessageBoardForwardVO> forwardVOList = forwardRecordService.getAllUnit(list.get(i).getId());
                //回复
                List<MessageBoardReplyVO> replyVOList = replyService.getAllDealReply(list.get(i).getId());
                String recUnitNames="";
                if(null != replyVOList && replyVOList.size() > 0) {
                    for(MessageBoardReplyVO replyVO:replyVOList){
                        if(!StringUtils.isEmpty(replyVO.getReceiveName())&&!recUnitNames.contains(replyVO.getReceiveName()+",")){
                            recUnitNames+=replyVO.getReceiveName()+",";
                        }else{
                            OrganEO organEO = CacheHandler.getEntity(OrganEO.class, replyVO.getCreateOrganId());
                            if (organEO != null&&!recUnitNames.contains(organEO.getName()+",")) {
                                recUnitNames+=organEO.getName()+",";
                            }
                        }
                    }
                    if(!StringUtils.isEmpty(recUnitNames)){
                        recUnitNames=recUnitNames.substring(0,recUnitNames.length()-1);
                        list.get(i).setReceiveUnitName(recUnitNames);
                    }else{
                        list.get(i).setReceiveUnitName("暂无接收单位");
                    }
                }else if (forwardVOList != null && forwardVOList.size() > 0 ) {
                    for(MessageBoardForwardVO forwardVO:forwardVOList){
                        if (!StringUtils.isEmpty(forwardVO.getReceiveUnitName())&&!recUnitNames.contains(forwardVO.getReceiveUnitName()+",")) {
                            recUnitNames+=forwardVO.getReceiveUnitName()+",";
                        } else {
                            OrganEO organEO = CacheHandler.getEntity(OrganEO.class, forwardVO.getReceiveOrganId());
                            if (organEO != null&&!recUnitNames.contains(organEO.getName()+",")) {
                                recUnitNames+=organEO.getName()+",";
                            }
                        }
                    }
                    if(!StringUtils.isEmpty(recUnitNames)){
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

        List<ContentModelParaVO> list1 = ModelConfigUtil.getMessageBoardType(columnId, context.getSiteId());
        if (list1 != null && list1.size() > 0) {
            result.put("typeList", list1);
        } else {
            result.put("typeList", null);
        }
        ColumnTypeConfigVO configVO = ModelConfigUtil.getCongfigVO(columnId, siteId);
        if (configVO != null && configVO.getIsLocalUnit() != null && configVO.getIsLocalUnit() == 1) {
            List<DataDictVO> localList = DataDictionaryUtil.getItemList("local_unit", context.getSiteId());
            result.put("localList", localList);
        }
        if (!org.apache.commons.lang3.StringUtils.isEmpty(localUnitId)) {
            result.put("localUnitId", localUnitId);
        }
        if (!org.apache.commons.lang3.StringUtils.isEmpty(title)) {
            result.put("title", title);
        }
        if (!org.apache.commons.lang3.StringUtils.isEmpty(startDate)) {
            result.put("startDate", startDate);
        }
        if (!org.apache.commons.lang3.StringUtils.isEmpty(endDate)) {
            result.put("endDate", endDate);
        }
        if (!org.apache.commons.lang3.StringUtils.isEmpty(classCode)) {
            result.put("classCode", classCode);
        }
        if (!org.apache.commons.lang3.StringUtils.isEmpty(organId)) {
            result.put("organId", organId);
        }
        if (!org.apache.commons.lang3.StringUtils.isEmpty(type)) {
            result.put("type", type);
        }
        result.put("action", action);
        result.put("page", page);
        result.put("idNum", idNum);

        return result;
    }


    private static Date toDate(String date) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        try {
            return sdf.parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    private Object getNew(Long columnId, String classCode, Long organId, String action, String recUserCode) {
        Context context=ContextHolder.getContext();
        ColumnTypeConfigVO configVO=ModelConfigUtil.getCongfigVO(columnId,context.getSiteId());
        MessageBoardEditVO vo = new MessageBoardEditVO();
        Map<String, Object> result = new HashMap<String, Object>();
        if(configVO!=null&&configVO.getIsLoginGuest()!=null&&configVO.getIsLoginGuest().intValue()==1){
            try {
                MemberSessionVO memberVO = (MemberSessionVO) ContextHolderUtils.getSession().getAttribute("member");
                if (memberVO != null && memberVO.getId() != null) {
                    vo.setPersonName(memberVO.getName());
                    vo.setPersonPhone(memberVO.getPhone());
                }else{
                    result.put("action", action);
                    result.put("isLogin", 0);
                    return result;//请先登陆
                }
            } catch (Exception e) {

            }
        }
        IndicatorEO columnEO = CacheHandler.getEntity(IndicatorEO.class, columnId);
        if (columnEO != null) {
            vo.setSiteId(columnEO.getSiteId());
        }
        vo.setTypeCode(BaseContentEO.TypeCode.messageBoard.toString());
        vo.setColumnId(columnId);
        if (!org.apache.commons.lang3.StringUtils.isEmpty(classCode)) {
            vo.setClassCode(classCode);
        }
        if (organId != null) {
            result.put("organId", organId);
            vo.setReceiveUnitId(organId);
        }
        if(!AppUtil.isEmpty(recUserCode)){
            vo.setReceiveUserCode(recUserCode);
        }
        List<ContentModelParaVO> list = ModelConfigUtil.getParam(columnId, context.getSiteId(), null);
        if (list != null && list.size() > 0) {
            result.put("recList", list);
            result.put("recType", list.get(0).getRecType());
            result.put("firstUnitName",list.get(0).getRecUnitName());
            result.put("firstUnitId",list.get(0).getRecUnitId());
        }
        List<ContentModelParaVO> list1 = ModelConfigUtil.getMessageBoardType(columnId, context.getSiteId());
        if (list1 != null && list1.size() > 0) {
            result.put("typeList", list1);
            result.put("size", list1.size());
            result.put("codeType", 1);
        } else {
            result.put("typeList", null);
            result.put("size", 0);
            result.put("codeType", 0);
        }
        result.put("vo", vo);
        result.put("action", action);
        return result;
    }


    private Object getDetail(Long contentId, String action, String isPublish) {

        Map<String, Object> result = new HashMap<String, Object>();

        if (AppUtil.isEmpty(contentId) || contentId == 0) {
            return "1";
        }
        BaseContentEO contentEO = contentService.getEntity(BaseContentEO.class, contentId);
        if (null == contentEO) {
            return "1";
        }
        ColumnTypeConfigVO configVO = ModelConfigUtil.getCongfigVO(contentEO.getColumnId(), contentEO.getSiteId());
        if(configVO==null){
            return "1";
        }
       /* if (org.apache.commons.lang3.StringUtils.isEmpty(isPublish)) {
            if (contentEO.getIsPublish() == null || contentEO.getIsPublish() == 0) {
                return "2";
            }
        }*/

        Map<String, Object> map = new HashMap<String, Object>();
        map.put("baseContentId", contentId);
        map.put("recordStatus", AMockEntity.RecordStatus.Normal.toString());
        List<MessageBoardEO> list = messageBoardService.getEntities(MessageBoardEO.class, map);
        if (null == list || list.size() <= 0) {
            return "1";
        }
        MessageBoardEO eo = list.get(0);
        MessageBoardEditVO vo = new MessageBoardEditVO();
        AppUtil.copyProperties(vo, eo);
        vo.setIsPublish(contentEO.getIsPublish());
        vo.setTitle(contentEO.getTitle());
        vo.setColumnId(contentEO.getColumnId());
        vo.setSiteId(contentEO.getSiteId());
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
        List<DataDictVO> commentList = DataDictionaryUtil.getItemList("guest_comment", vo.getSiteId());
        result.put("commentList", commentList);
        result.put("isAssess", configVO.getIsAssess());
        if (configVO.getRecType() != null && configVO.getRecType() != 2) {
            if (configVO.getRecType().equals(0)||configVO.getTurn().equals(1)) {
                if (vo.getReceiveUnitId() != null) {
                    OrganEO organEO = CacheHandler.getEntity(OrganEO.class, vo.getReceiveUnitId());
                    if (organEO != null) {
                        vo.setReceiveUnitName(organEO.getName());
                    }
                }
            } else {
                if (!StringUtils.isEmpty(vo.getReceiveUserCode())) {
                    DataDictVO dictVO = DataDictionaryUtil.getItem("guest_book_rec_users", vo.getReceiveUserCode());
                    if (dictVO != null) {
                        vo.setReceiveUserName(dictVO.getKey());
                    }
                }
            }
        }
        vo.setRecType(configVO.getRecType());
        List<MessageBoardForwardVO> forwardVOList = forwardRecordService.getAllForwardByMessageBoardId(eo.getId());
        List<MessageBoardReplyVO> replyVOList = replyService.getAllDealReply(eo.getId());
        result.put("replyVOList", replyVOList);
        String receiveUnitNames = "";
        String recUserNames = "";
        if (configVO.getRecType()==0||(configVO.getTurn()!=null&&configVO.getTurn()==1)) {//选择受理单位
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

        result.put("recType", configVO.getRecType());
        result.put("action", action);
        result.put("vo", vo);
        return result;
    }

}


