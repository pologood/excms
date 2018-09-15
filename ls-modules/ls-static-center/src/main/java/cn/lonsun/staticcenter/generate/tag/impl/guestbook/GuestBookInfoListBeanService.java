package cn.lonsun.staticcenter.generate.tag.impl.guestbook;

import cn.lonsun.base.anno.DbInject;
import cn.lonsun.common.util.AppUtil;
import cn.lonsun.content.internal.dao.IGuestBookDao;
import cn.lonsun.content.vo.GuestBookEditVO;
import cn.lonsun.core.base.entity.AMockEntity;
import cn.lonsun.indicator.internal.entity.IndicatorEO;
import cn.lonsun.rbac.internal.entity.OrganEO;
import cn.lonsun.site.contentModel.vo.ContentModelParaVO;
import cn.lonsun.staticcenter.generate.GenerateConstant;
import cn.lonsun.staticcenter.generate.GenerateException;
import cn.lonsun.staticcenter.generate.tag.impl.AbstractBeanService;
import cn.lonsun.staticcenter.generate.thread.Context;
import cn.lonsun.staticcenter.generate.thread.ContextHolder;
import cn.lonsun.staticcenter.generate.util.PathUtil;
import cn.lonsun.system.datadictionary.vo.DataDictVO;
import cn.lonsun.system.member.internal.entity.MemberEO;
import cn.lonsun.system.member.internal.service.IMemberService;
import cn.lonsun.util.DataDictionaryUtil;
import cn.lonsun.util.ModelConfigUtil;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static cn.lonsun.cache.client.CacheHandler.getEntity;

/**
 * @author hujun
 * @ClassName: guestBookListBeanService
 * @Description: 生成前台静态页面需要的数据类
 * @date 2015年11月30日 下午3:26:03
 */
@Component
public class GuestBookInfoListBeanService extends AbstractBeanService {

    @DbInject("guestBook")
    private IGuestBookDao gusetBookDao;

    @Autowired
    private IMemberService memberService;

    @Override
    public Object getObject(JSONObject paramObj) {
        Context context = ContextHolder.getContext();
        // 此写法是为了使得在主页面这样调用也能解析
        Long columnId = paramObj.getLong(GenerateConstant.ID);
        if (null == columnId||0==columnId) {// 如果栏目id为空说明，栏目id是在标签页面默认传入的
            columnId = context.getColumnId();
        }
        String recUnitId = context.getParamMap().get("organId");
        if(recUnitId == null){
            recUnitId = paramObj.getString("organId");
        }
        Long siteId = paramObj.getLong("siteId");
        if (siteId == null||0==siteId) {
            siteId = context.getSiteId();
        }
        String docNum = context.getParamMap().get("docNum");
        if(docNum == null) {
            docNum = paramObj.getString("docNum");
        }
        Integer isReply = paramObj.getInteger("isReply");
        String typeStr = context.getParamMap().get("type");
        Integer type = null;
        if (StringUtils.isEmpty(typeStr)) {
            type = paramObj.getInteger("type");
        } else {
            type = Integer.parseInt(typeStr);
        }
        List<ContentModelParaVO> codeList = ModelConfigUtil.getGuestBookType(columnId, context.getSiteId());
        String classCode = null;
        if (codeList != null && codeList.size() > 0) {
            if (type != null) {
                if (type > 0 && type <= codeList.size()) {
                    classCode = codeList.get(type - 1).getClassCode();
                }
            }
        }
        String idNum = context.getParamMap().get("idNum");
        String createUserId = "";
        if (!StringUtils.isEmpty(idNum)) {
            List<MemberEO> mList = memberService.getByNumber(idNum, context.getSiteId());
            if (mList != null && mList.size() > 0) {
                for (MemberEO eo : mList) {
                    createUserId += eo.getId() + ",";
                }
                createUserId = createUserId.substring(0, createUserId.length() - 1);
            }
        }
        Integer orderType  = paramObj.getInteger("orderType");

        StringBuffer hql = new StringBuffer("select c.title as title,c.publishDate as publishDate,c.createDate as createDate,c.isPublish as isPublish")
                .append(",c.id as baseContentId,c.siteId as siteId,c.columnId as columnId,c.hit as hit,g.id as id,g.responseContent as responseContent")
                .append(",g.personIp as personIp,g.personName as personName,g.replyDate as replyDate,g.userId as userId,g.userName as userName")
                .append(",g.dealStatus as dealStatus,g.docNum as docNum")
                .append(",g.recType as recType,g.receiveId as receiveId,g.receiveName as receiveName,g.receiveUserCode as receiveUserCode,g.replyUserName as replyUserName,g.commentCode as commentCode")
                .append(",g.guestBookContent as guestBookContent,g.replyUnitId as replyUnitId,g.replyUnitName as replyUnitName")
                .append(" from BaseContentEO c,GuestBookEO g where g.baseContentId=c.id and c.recordStatus=:recordStatus ");

        hql.append("  and c.siteId=:siteId and c.isPublish=1 and g.isPublic=1");
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("recordStatus", AMockEntity.RecordStatus.Normal.toString());
        if(columnId!=null) {
            hql.append("  and c.columnId =:columnId");
            map.put("columnId", columnId);
        }
        map.put("siteId", siteId);

        if (!StringUtils.isEmpty(classCode)) {
            hql.append(" and g.classCode=:classCode");
            map.put("classCode",classCode);
        }
        if (isReply != null && isReply == 1) {
            hql.append(" and g.dealStatus in('handled','replyed') ");
        }
        if (isReply != null && isReply == 2) {
            hql.append(" and g.dealStatus in('handled','replyed','handling') ");
        }
        if (!StringUtils.isEmpty(createUserId)) {
            hql.append(" and g.createUserId in(" + createUserId + ")");
        }
        if (!StringUtils.isEmpty(docNum)) {
            hql.append(" and g.docNum ="+ docNum);
        }
        if (!StringUtils.isEmpty(recUnitId)) {
            Long[] recUnitIds= AppUtil.getLongs(recUnitId,",");
            hql.append(" and g.receiveId").append(recUnitIds.length == 1 ? " =:recUnitIds " : " in (:recUnitIds) ");
            if (recUnitIds.length == 1) {
                map.put("recUnitIds", recUnitIds[0]);
            } else {
                map.put("recUnitIds", recUnitIds);
            }
        }

        String where = paramObj.getString(GenerateConstant.WHERE);
        hql.append(StringUtils.isEmpty(where) ? "" : " AND " + where);
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


        Integer num = paramObj.getInteger(GenerateConstant.NUM);
        return gusetBookDao.getBeansByHql(hql.toString(), map, GuestBookEditVO.class, num);
    }

    /**
     * 预处理数据
     *
     * @throws GenerateException
     * java.lang.Object)
     */
    @Override
    @SuppressWarnings("unchecked")
    public Map<String, Object> doProcess(Object resultObj, JSONObject paramObj) throws GenerateException {
        // 预处理数据，处理文章链接问题
        List<GuestBookEditVO> list = (List<GuestBookEditVO>) resultObj;
        if (null != list && !list.isEmpty()) {
            // 此写法是为了使得在主页面这样调用也能解析
            Long siteId = paramObj.getLong("siteId");
            IndicatorEO indicatorEO = null;
            if (siteId != null&&siteId!=0) {
                indicatorEO = getEntity(IndicatorEO.class, siteId);
            }
            // 处理文章链接
            for (GuestBookEditVO vo : list) {
                //String path = PathUtil.getLinkPath(vo.getColumn_id(), vo.getBase_content_id());
                if (siteId != null) {
                    vo.setLink(indicatorEO.getUri() + "/content/article/" + vo.getBaseContentId() + "?id=" + vo.getBaseContentId());
                } else {
                    String path = PathUtil.getLinkPath(vo.getColumnId(), vo.getBaseContentId());//拿到栏目页和文章页id
                    //String path = PathUtil.getLinkPath(HtmlEnum.CONTENT.getValue(), HtmlEnum.ACRTILE.getValue(), vo.getColumnId(), vo.getBaseContentId()) + "?id=" + vo.getBaseContentId();
                    vo.setLink(path);
                }
                if (vo.getReceiveId() != null) {
                    OrganEO organEO = getEntity(OrganEO.class, vo.getReceiveId());
                    if (organEO != null) {
                        vo.setReceiveName(organEO.getName());
                    }
                }
                if (!StringUtils.isEmpty(vo.getReplyUserId())) {
                    DataDictVO dictVO = DataDictionaryUtil.getItem("guest_book_rec_users", vo.getReplyUserId());
                    if (dictVO != null) {
                        vo.setReplyUserName(dictVO.getKey());
                    }
                }
                if (!StringUtils.isEmpty(vo.getDealStatus())) {
                    DataDictVO dictVO = DataDictionaryUtil.getItem("deal_status", vo.getDealStatus());
                    if (dictVO != null) {
                        vo.setStatusName(dictVO.getKey());
                    }
                }
                if (StringUtils.isEmpty(vo.getClassCode())) {
                    DataDictVO dictVO = DataDictionaryUtil.getItem("petition_purpose", vo.getClassCode());
                    if (dictVO != null) {
                        vo.setClassName(dictVO.getKey());
                    }
                }
                if (!StringUtils.isEmpty(vo.getCommentCode())) {
                    DataDictVO dictVO = DataDictionaryUtil.getItem("guest_comment", vo.getCommentCode());
                    if (dictVO != null) {
                        vo.setCommentName(dictVO.getKey());
                    }
                }
            }
        }
        return super.doProcess(resultObj, paramObj);
    }

}
