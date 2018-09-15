package cn.lonsun.staticcenter.generate.tag.impl.guestbook;

import cn.lonsun.base.anno.DbInject;
import cn.lonsun.common.util.AppUtil;
import cn.lonsun.content.internal.dao.IGuestBookDao;
import cn.lonsun.content.internal.entity.BaseContentEO;
import cn.lonsun.content.internal.entity.GuestBookEO;
import cn.lonsun.content.internal.entity.GuestBookForwardRecordEO;
import cn.lonsun.content.internal.service.IBaseContentService;
import cn.lonsun.content.internal.service.IForwardRecordService;
import cn.lonsun.content.internal.service.IGuestBookService;
import cn.lonsun.content.leaderwin.internal.service.ILeaderInfoService;
import cn.lonsun.content.leaderwin.vo.LeaderInfoVO;
import cn.lonsun.content.vo.GuestBookEditVO;
import cn.lonsun.core.base.entity.AMockEntity;
import cn.lonsun.core.util.ContextHolderUtils;
import cn.lonsun.core.util.Pagination;
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
import cn.lonsun.system.datadictionary.vo.DataDictVO;
import cn.lonsun.system.member.internal.entity.MemberEO;
import cn.lonsun.system.member.internal.service.IMemberService;
import cn.lonsun.system.member.vo.MemberSessionVO;
import cn.lonsun.util.DataDictionaryUtil;
import cn.lonsun.util.ModelConfigUtil;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

import static cn.lonsun.cache.client.CacheHandler.getEntity;
import static cn.lonsun.util.ModelConfigUtil.getCongfigVO;

/**
 * @author hujun
 * @ClassName: GuestBookPageListBeanService
 * @Description: 分页标签
 * @date 2015年12月1日 下午5:36:41
 */
@Component
public class GuestBookPageListBeanService extends AbstractBeanService {

    /**
     * 查询分页结果对象
     *
     * @see cn.lonsun.staticcenter.generate.tag.impl.AbstractBeanService#getObject(com.alibaba.fastjson.JSONObject)
     */

    @DbInject("guestBook")
    private IGuestBookDao guestBookDao;

    @Autowired
    private IGuestBookService guestBookService;

    @Autowired
    private IBaseContentService contentService;

    @Autowired
    private IForwardRecordService recordService;

    @Autowired
    private IMemberService memberService;

    @Autowired
    private ILeaderInfoService leaderInfoService;

    @Override
    public Object getObject(JSONObject paramObj) {
        Context context = ContextHolder.getContext();
        Long columnId = paramObj.getLong(GenerateConstant.ID);
        if (null == columnId || columnId == 0) {// 如果栏目id为空,说明栏目id是在页面传入的
            columnId = context.getColumnId();
        }
        //如果ID全为空取地址栏参数
        if (AppUtil.isEmpty(columnId)) {
            String columnIdStr=context.getParamMap().get("columnId");
            if(!StringUtils.isEmpty(columnIdStr)){
                columnId = Long.parseLong(columnIdStr);
            }
        }
        String action = context.getParamMap().get("action");
        if (AppUtil.isEmpty(action)) {
            action = paramObj.getString("action");
        }
        String classCode = context.getParamMap().get("classCode");
        if(StringUtils.isEmpty(classCode)){
            String typeStr = context.getParamMap().get("type");
            Integer type = null;
            if (StringUtils.isEmpty(typeStr)) {
                type = paramObj.getInteger("type");
            } else {
                type = Integer.parseInt(typeStr);
            }
            if(columnId!=null&&columnId!=0){
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
        if (!StringUtils.isEmpty(organIdStr)) {
            organId = Long.parseLong(organIdStr);
        }
        if ("detail".equals(action)) {
            String id = context.getParamMap().get("id");
            Long contentId = null;
            if (StringUtils.isEmpty(id)) {
                contentId = context.getContentId();
            } else {
                contentId = Long.parseLong(id);
            }
            return getDetail(contentId);
        } else if ("list".equals(action)) {
            return getPage(paramObj, columnId, classCode);
        } else if ("singleList".equals(action)) {
            return getPage(paramObj, columnId, classCode);
        } else if ("new".equals(action)) {
            return getNew(columnId, classCode, organId);
        } else if ("search".equals(action)) {
            return serachEO();
        } else {
            return getPage(paramObj, columnId, classCode);
        }
    }

    private Object serachEO() {
        Context context = ContextHolder.getContext();
        Long siteId=context.getSiteId();
        if(siteId==null){
            String siteIdStr=context.getParamMap().get("siteId");
            if(!StringUtils.isEmpty(siteIdStr)){
                siteId=Long.parseLong(siteIdStr);
            }
        }
        if(siteId==null){
            return "2";
        }
        String randomCode = context.getParamMap().get("randomCode");
        String docNum = context.getParamMap().get("docNum");
        if (StringUtils.isEmpty(randomCode) || StringUtils.isEmpty(docNum)) {
            return "0";
        }
        randomCode=randomCode.trim();
        docNum=docNum.trim();
        GuestBookEditVO vo = guestBookService.searchEO(randomCode, docNum,siteId);
        if (vo == null || vo.getBaseContentId() == null) {
            return "1";
        }
        return vo;
    }

    private Object getNew(Long columnId, String classCode, Long organId) {

        GuestBookEditVO vo = new GuestBookEditVO();
        IndicatorEO eo = getEntity(IndicatorEO.class, columnId);
        if (eo != null) {
            vo.setSiteId(eo.getSiteId());
        }
        vo.setTypeCode(BaseContentEO.TypeCode.guestBook.toString());
        vo.setColumnId(columnId);
        if (!StringUtils.isEmpty(classCode)) {
            vo.setClassCode(classCode);
        }
        if (organId != null) {
            vo.setReceiveId(organId);
        }
        try{
            MemberSessionVO memberVO = (MemberSessionVO) ContextHolderUtils.getSession().getAttribute("member");
            if(memberVO!=null&&memberVO.getId()!=null){
                vo.setPersonName(memberVO.getName());
                String phone=memberVO.getPhone();
                if(!StringUtils.isEmpty(phone)){
                    Long personPhone=Long.parseLong(phone);
                    vo.setPersonPhone(personPhone);
                }
            }
        }catch (Exception e){

        }
        return vo;
    }

    private Object getDetail(Long contentId) {
        if (AppUtil.isEmpty(contentId) || contentId == 0) {
            return "1";
        }
        BaseContentEO contentEO = contentService.getEntity(BaseContentEO.class, contentId);
        if (null == contentEO) {
            return "1";
        }
       /* Context context = ContextHolder.getContext();
        String isPublish = context.getParamMap().get("isPublish");
        if (StringUtils.isEmpty(isPublish)) {
            if (contentEO.getIsPublish() == null || contentEO.getIsPublish() == 0) {
                return "2";
            }
        }*/

        Map<String, Object> map = new HashMap<String, Object>();
        map.put("baseContentId", contentId);
        map.put("recordStatus", AMockEntity.RecordStatus.Normal.toString());
        List<GuestBookEO> list = guestBookService.getEntities(GuestBookEO.class, map);
        if (null == list || list.size() <= 0) {
            return "1";
        }
        GuestBookEO eo = list.get(0);
        GuestBookEditVO vo = new GuestBookEditVO();
        AppUtil.copyProperties(vo, eo);
        vo.setIsPublish(contentEO.getIsPublish());
        vo.setTitle(contentEO.getTitle());
        vo.setColumnId(contentEO.getColumnId());
        vo.setSiteId(contentEO.getSiteId());
        ColumnTypeConfigVO configVO=ModelConfigUtil.getCongfigVO(contentEO.getColumnId(),contentEO.getSiteId());
        if(configVO!=null){
            if (configVO.getRecType() != null&&configVO.getRecType()!=2) {
                if (configVO.getRecType().equals(0)) {
                    if (vo.getReceiveId() != null) {
                        OrganEO organEO = getEntity(OrganEO.class, vo.getReceiveId());
                        if (organEO != null) {
                            vo.setReceiveName(organEO.getName());
                        }
                    }
                } else {
                    if(vo.getReplyUnitId()!=null){
                        OrganEO organEO = getEntity(OrganEO.class, vo.getReplyUnitId());
                        if (organEO != null) {
                            vo.setReplyUnitName(organEO.getName());
                        }
                    }else {
                        if (vo.getReceiveId() != null) {
                            OrganEO organEO = getEntity(OrganEO.class, vo.getReceiveId());
                            if (organEO != null) {
                                vo.setReceiveName(organEO.getName());
                            }
                        }
                    }
                    if (!StringUtils.isEmpty(vo.getReceiveUserCode())) {
                        DataDictVO dictVO = DataDictionaryUtil.getItem("guest_book_rec_users", vo.getReceiveUserCode());
                        if (dictVO != null) {
                            vo.setReceiveUserName(dictVO.getKey());
                        }
                        if(StringUtils.isEmpty(vo.getReceiveUserName())){
                            LeaderInfoVO infoVO=leaderInfoService.getLeaderInfoVO(Long.parseLong(vo.getReceiveUserCode()));
                            if(infoVO!=null){
                                vo.setReceiveUserName(infoVO.getName());
                            }
                        }
                    }
                }
            }
            vo.setRecType(configVO.getRecType());
        }
        if (!StringUtils.isEmpty(vo.getCommentCode())) {
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
        if(!StringUtils.isEmpty(vo.getClassCode())){
            DataDictVO dictVO = DataDictionaryUtil.getItem("petition_purpose", vo.getClassCode());
            if (dictVO != null) {
                vo.setClassName(dictVO.getKey());
            }
        }
        Pagination page=recordService.getRecord(0L,5,vo.getId());
        if(page!=null&&page.getData()!=null&&page.getData().size()>0){
            List<GuestBookForwardRecordEO> recordEOList=(List<GuestBookForwardRecordEO>)page.getData();
            vo.setRemarks(recordEOList.get(0).getRemarks());
        }
        vo.setCommentCode("satisfactory");
        return vo;
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

    private Object getPage(JSONObject paramObj, Long columnId, String classCode) {
        Context context = ContextHolder.getContext();
        Long pageIndex = context.getPageIndex() == null ? 0 : (context.getPageIndex() - 1);
        Integer pageSize = paramObj.getInteger(GenerateConstant.PAGE_SIZE);
        Integer isReply = paramObj.getInteger("isReply");

        Map<String, String> map = context.getParamMap();
        String organIdStr = map.get("organId");
        Long organId = null;
        if (!StringUtils.isEmpty(organIdStr)) {
            organId = Long.parseLong(organIdStr);
        }
        String title = map.get("title");
        String startDate = map.get("startDate");
        String endDate = map.get("endDate");
        String idNum=map.get("idNum");
        String rStartDate = map.get("rStartDate");
        String rEndDate = map.get("rEndDate");
        String localUnitId=map.get("localUnitId");
        String createUserId="";
        if(!StringUtils.isEmpty(idNum)){
            List<MemberEO> mList=memberService.getByNumber(idNum,context.getSiteId());
            if(mList!=null&&mList.size()>0){
                for(MemberEO eo:mList){
                    createUserId+=eo.getId()+",";
                }
                createUserId=createUserId.substring(0,createUserId.length()-1);
            }
            if(StringUtils.isEmpty(createUserId)){
                Pagination p=new Pagination();
                p.setPageSize(pageSize);
                return p;
            }
        }
        Integer orderType  = paramObj.getInteger("orderType");

        StringBuffer hql = new StringBuffer("select c.title as title,c.publishDate as publishDate,g.addDate as addDate,c.isPublish as isPublish")
            .append(",c.id as baseContentId,c.siteId as siteId,c.columnId as columnId,c.hit as hit,g.id as id,g.responseContent as responseContent")
            .append(",g.guestBookContent as guestBookContent,g.dealStatus as dealStatus,g.personIp as personIp,g.personName as personName")
            .append(",g.replyDate as replyDate,g.userId as userId,g.userName as userName,g.recType as recType,g.receiveId as receiveId,g.docNum as docNum")
            .append(",g.receiveName as receiveName,g.replyUnitId as replyUnitId,g.replyUnitName as replyUnitName,g.receiveUserCode as receiveUserCode,g.classCode as classCode")
            .append(" from BaseContentEO c,GuestBookEO g where g.baseContentId=c.id and c.recordStatus=? and g.recordStatus=? ");

        hql.append(" and c.columnId = ? ");
        hql.append(" and c.isPublish=1 and g.isPublic=1 ");

        List<Object> values = new ArrayList<Object>();
        values.add(AMockEntity.RecordStatus.Normal.toString());
        values.add(AMockEntity.RecordStatus.Normal.toString());
        values.add(columnId);
        String uidStr=context.getParamMap().get("uid");
        Long uid=null;
        if(!StringUtils.isEmpty(uidStr)){
            uid = Long.parseLong(uidStr);
        }

        Long siteId = paramObj.getLong("siteId");
        if (siteId == null) {
            siteId = context.getSiteId();
        }
        if (siteId != null && !AppUtil.isEmpty(siteId)) {
            hql.append(" and c.siteId = ? ");
            values.add(siteId);
        }
        if (organId != null &&!AppUtil.isEmpty(organId)) {
            hql.append(" and g.receiveId = ? ");
            values.add(organId);
            System.out.println("进来了***********"+organId);
        }
        if (!StringUtils.isEmpty(title)) {
            hql.append(" and c.title like ?");
            values.add("%".concat(title.trim()).concat("%"));
        }
        if (!AppUtil.isEmpty(startDate)) {
            hql.append(" and g.addDate>=? ");
            values.add(toDate(startDate));
        }
        if (!AppUtil.isEmpty(endDate)) {
            hql.append(" and g.addDate<=? ");
            Date date = toDate(endDate);
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);
            calendar.set(Calendar.DATE, calendar.get(Calendar.DATE) + 1);//结束日期增加一天
            values.add(calendar.getTime());

        }
        if (!AppUtil.isEmpty(rStartDate)) {
            hql.append(" and g.replyDate>=? ");
            values.add(toDate(rStartDate));
        }
        if (!AppUtil.isEmpty(rEndDate)) {
            hql.append(" and g.replyDate<=? ");
            Date date = toDate(rEndDate);
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);
            calendar.set(Calendar.DATE, calendar.get(Calendar.DATE) + 1);//结束日期增加一天
            values.add(calendar.getTime());
        }
        if(!StringUtils.isEmpty(localUnitId)){
            hql.append(" and g.localUnitId =?");
            values.add(localUnitId);
        }
        if (isReply != null && isReply == 1) {
            hql.append(" and g.dealStatus in('handled','replyed') ");
        }
        if (isReply != null && isReply == 2) {
            hql.append(" and g.dealStatus in('handled','replyed','handling') ");
        }
        if (!StringUtils.isEmpty(classCode)) {
            hql.append(" and g.classCode=?");
            values.add(classCode);
        }

        if(uid!=null){
            hql.append(" and g.receiveId=?");
            values.add(uid);
        }
        if(!StringUtils.isEmpty(createUserId)){
            hql.append(" and g.createUserId in("+createUserId+")");
        }

        if(orderType==null){
            hql.append(" order by g.addDate desc");
        }else if(orderType==1){
            hql.append(" order by g.replyDate desc");
        }else if(orderType==2){
            hql.append(" order by c.publishDate desc");
        }else{
            hql.append(" order by g.addDate desc");
        }
        hql.append(" ,g.docNum desc ");

        Pagination page = guestBookDao.getPagination(pageIndex, pageSize, hql.toString(), values.toArray(), GuestBookEditVO.class);
        return page;
    }


    public Map<String, Object> doProcess(Object resultObj, JSONObject paramObj) throws GenerateException {
        Context context = ContextHolder.getContext();
        Long columnId = context.getColumnId();
        if (null == columnId||0==columnId) {// 如果栏目id为空,说明栏目id是在页面传入的
            columnId = paramObj.getLong(GenerateConstant.ID);
        }
        //如果ID全为空取地址栏参数
        if (AppUtil.isEmpty(columnId)||columnId==0) {
            String columnIdStr=context.getParamMap().get("columnId");
            if(!StringUtils.isEmpty(columnIdStr)){
                columnId = Long.parseLong(columnIdStr);
            }
        }
        Map<String, Object> map = super.doProcess(resultObj, paramObj);
        String action = context.getParamMap().get("action");
        if (AppUtil.isEmpty(action)) {
            action = paramObj.getString("action");
        }
        map.put("action", action);
        map.put("recUserName",context.getParamMap().get("recUser") );
        String typeStr = context.getParamMap().get("type");
        if ("new".equals(action)) {
            List<ContentModelParaVO> list = ModelConfigUtil.getParam(columnId, context.getSiteId(),null);

            if (list != null && list.size() > 0) {
                map.put("recList", list);
                map.put("recType", list.get(0).getRecType());
            }
            List<ContentModelParaVO> list1 = ModelConfigUtil.getGuestBookType(columnId, context.getSiteId());
            if (list1 != null && list1.size() > 0) {
                map.put("typeList", list1);
                map.put("size", list1.size());
            } else {
                map.put("typeList", null);
                map.put("size", 0);
            }

            GuestBookEditVO editVO = (GuestBookEditVO) resultObj;
            String uidStr=context.getParamMap().get("uid");
            if(!StringUtils.isEmpty(uidStr)){
                Long uid = Long.parseLong(uidStr);
                editVO.setReceiveId(uid);
            }
            String recUser=context.getParamMap().get("recUser");
            if(!StringUtils.isEmpty(recUser)){
                editVO.setReceiveUserCode(recUser);
            }
            map.put("guestBookVO", editVO);
            if(!StringUtils.isEmpty(typeStr)){
                map.put("typeStr",typeStr);
            }
            ColumnTypeConfigVO configVO=ModelConfigUtil.getCongfigVO(columnId,context.getSiteId());
            if(configVO!=null&&configVO.getIsLoginGuest()==1){
                Boolean curIsLogin = Boolean.FALSE;
                try {
                    MemberSessionVO memberVO = (MemberSessionVO) ContextHolderUtils.getSession().getAttribute("member");
                    if (memberVO != null && memberVO.getId() != null) {
                        curIsLogin = Boolean.TRUE;
                    }
                }catch (Exception e){}
                map.put("curIsLogin",curIsLogin);
            }
        } else if ("search".equals(action)) {
            if ("1".equals(resultObj)) {
                map.put("message", "<div class=\"guestbook-msg-info\">您查询的信息不存在，请核准后再试！</div>");
            }
            if ("0".equals(resultObj)) {
                map.put("message", "<div class=\"guestbook-msg-info\">查询编号、查询密码不能为空！</div>");
            }
            if ("2".equals(resultObj)) {
                map.put("message", "<div class=\"guestbook-msg-info\">站点ID不能为空！</div>");
            }
            ColumnTypeConfigVO configVO = getCongfigVO(columnId, context.getSiteId());
            if (configVO != null) {
                map.put("isAssess", configVO.getIsAssess());
            }
            List<DataDictVO> commentList = DataDictionaryUtil.getItemList("guest_comment",context.getSiteId());
            map.put("commentList", commentList);

        } else if ("list".equals(action) || "singleList".equals(action)) {
            // 获取分页对象
            List<ContentModelParaVO> list = ModelConfigUtil.getParam(columnId, context.getSiteId(),null);
            if (list != null && list.size() > 0) {
                map.put("recList", list);
                map.put("recType", list.get(0).getRecType());
            }
            List<ContentModelParaVO> codeList = ModelConfigUtil.getGuestBookType(columnId, context.getSiteId());
            if (codeList != null && codeList.size() > 0) {
                map.put("typeList", codeList);
                map.put("size", codeList.size());
                map.put("codeType", 1);
            } else {
                map.put("typeList", null);
                map.put("size", 0);
                map.put("codeType", 0);
            }

            GuestBookEditVO editVO = new GuestBookEditVO();
            IndicatorEO eo = getEntity(IndicatorEO.class, columnId);
            if (eo != null) {
                editVO.setSiteId(eo.getSiteId());
            }
            editVO.setTypeCode(BaseContentEO.TypeCode.guestBook.toString());
            editVO.setColumnId(columnId);
            Integer type = null;
            if (StringUtils.isEmpty(typeStr)) {
                type = paramObj.getInteger("type");
            } else {
                type = Integer.parseInt(typeStr);
                map.put("typeStr",typeStr);
            }
            if (type != null) {
                if (codeList != null && codeList.size() > 0) {
                    if (type > 0 && type <= codeList.size()) {
                        String typeCode = codeList.get(type - 1).getClassCode();
                        if (!StringUtils.isEmpty(typeCode)) {
                            editVO.setClassCode(typeCode);
                        }
                    }
                }
            }
            String uidStr=context.getParamMap().get("uid");
            if(!StringUtils.isEmpty(uidStr)){
                Long uid = Long.parseLong(uidStr);
                editVO.setReceiveId(uid);
            }
            String recUser=context.getParamMap().get("recUser");
            if(!StringUtils.isEmpty(recUser)){
                editVO.setReceiveUserCode(recUser);
            }
            map.put("guestBookVO", editVO);
            ColumnTypeConfigVO configVO=ModelConfigUtil.getCongfigVO(columnId,context.getSiteId());
            if(configVO!=null&&configVO.getIsLoginGuest()==1){
                Boolean curIsLogin = Boolean.FALSE;
                try {
                    MemberSessionVO memberVO = (MemberSessionVO) ContextHolderUtils.getSession().getAttribute("member");
                    if (memberVO != null && memberVO.getId() != null) {
                        curIsLogin = Boolean.TRUE;
                    }
                }catch (Exception e){}
                map.put("curIsLogin",curIsLogin);
            }
            //分页对象
            Pagination pagination = (Pagination) resultObj;
            if (null != pagination) {
                List<?> resultList = pagination.getData();
                // 处理查询结果
                if (null != resultList && !resultList.isEmpty()) {
                    for (Object o : resultList) {
                        GuestBookEditVO vo = (GuestBookEditVO) o;
                        String path = PathUtil.getLinkPath(vo.getColumnId(), vo.getBaseContentId());//拿到栏目页和文章页id
                        //String path = PathUtil.getLinkPath(HtmlEnum.CONTENT.getValue(), HtmlEnum.ACRTILE.getValue(), vo.getColumnId(), vo.getBaseContentId()) + "?id=" + vo.getBaseContentId();
                        vo.setLink(path);
                        if (!StringUtils.isEmpty(vo.getDealStatus())) {
                            DataDictVO dictVO = DataDictionaryUtil.getItem("deal_status", vo.getDealStatus());
                            if (dictVO != null) {
                                vo.setStatusName(dictVO.getKey());
                            }
                        }
                        if (configVO.getRecType() != null&&configVO.getRecType()!=2) {
                            if (configVO.getRecType().equals(0)) {
                                if (vo.getReceiveId() != null) {
                                    OrganEO organEO = getEntity(OrganEO.class, vo.getReceiveId());
                                    if (organEO != null) {
                                        vo.setReceiveName(organEO.getName());
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
                        if (!StringUtils.isEmpty(configVO.getClassCodes())&&!StringUtils.isEmpty(vo.getClassCode())) {
                            DataDictVO dictVO = DataDictionaryUtil.getItem("petition_purpose", vo.getClassCode());
                            if (dictVO != null) {
                                vo.setClassName(dictVO.getKey());
                            }
                        }
                    }
                } else {
                    map.put("msgPage", "<div>暂无相关信息！</div>");
                }

                String path = PathUtil.getLinkPath(HtmlEnum.CONTENT.getValue(), HtmlEnum.COLUMN.getValue(), context.getColumnId(), null);
                pagination.setLinkPrefix(path);
                if(configVO!=null&&configVO.getIsLocalUnit()!=null&&configVO.getIsLocalUnit()==1){
                    List<DataDictVO> localList= DataDictionaryUtil.getItemList("local_unit", context.getSiteId());
                    map.put("localList",localList);
                }
                String option=context.getParamMap().get("opt");
                if(!StringUtils.isEmpty(option)){
                    map.put("opt",option);
                }
                String title = context.getParamMap().get("title");
                if(!StringUtils.isEmpty(title)){
                    map.put("title",title);
                }
                String idNum=context.getParamMap().get("idNum");
                if(!StringUtils.isEmpty(idNum)){
                    map.put("idNum",idNum);
                }
                String rStartDate = context.getParamMap().get("rStartDate");
                if(!StringUtils.isEmpty(rStartDate)){
                    map.put("rStartDate",rStartDate);
                }
                String rEndDate = context.getParamMap().get("rEndDate");
                if(!StringUtils.isEmpty(rEndDate)){
                    map.put("rEndDate",rEndDate);
                }
                String startDate = context.getParamMap().get("startDate");
                if(!StringUtils.isEmpty(startDate)){
                    map.put("startDate",startDate);
                }
                String endDate = context.getParamMap().get("endDate");
                if(!StringUtils.isEmpty(endDate)){
                    map.put("endDate",endDate);
                }
                String localUnitId=context.getParamMap().get("localUnitId");
                if(!StringUtils.isEmpty(localUnitId)){
                    map.put("localUnitId",localUnitId);
                }
                String classCode=context.getParamMap().get("classCode");
                if(!StringUtils.isEmpty(classCode)){
                    map.put("classCode",classCode);
                }

            }

        } else if ("detail".equals(action)) {
            ColumnTypeConfigVO configVO = getCongfigVO(columnId, context.getSiteId());
            if (configVO != null) {
                map.put("isAssess", configVO.getIsAssess());
            }
            List<DataDictVO> commentList = DataDictionaryUtil.getItemList("guest_comment",context.getSiteId());
            map.put("commentList", commentList);
            if ("1".equals(resultObj)) {
                map.put("message", "<div class=\"guestbook-msg-info\">您查询的信息不存在，请核准后再试！</div>");
            }if("2".equals(resultObj)){
                map.put("message", "<div class=\"guestbook-msg-info\">您查询的信息正在审核中！</div>");

            }
        }
        return map;
    }

    @Override
    public String objToStr(String content, Object resultObj, JSONObject paramObj) throws GenerateException {
        return super.objToStr(content, resultObj, paramObj);
    }
}
