package cn.lonsun.staticcenter.generate.tag.impl.guestbook;

import cn.lonsun.base.anno.DbInject;
import cn.lonsun.cache.client.CacheHandler;
import cn.lonsun.common.util.AppUtil;
import cn.lonsun.content.internal.dao.IGuestBookDao;
import cn.lonsun.content.internal.entity.BaseContentEO;
import cn.lonsun.content.vo.GuestBookEditVO;
import cn.lonsun.core.base.entity.AMockEntity;
import cn.lonsun.indicator.internal.entity.IndicatorEO;
import cn.lonsun.rbac.internal.entity.OrganEO;
import cn.lonsun.staticcenter.generate.GenerateConstant;
import cn.lonsun.staticcenter.generate.GenerateException;
import cn.lonsun.staticcenter.generate.tag.impl.AbstractBeanService;
import cn.lonsun.staticcenter.generate.thread.Context;
import cn.lonsun.staticcenter.generate.thread.ContextHolder;
import cn.lonsun.staticcenter.generate.util.PathUtil;
import cn.lonsun.staticcenter.service.HtmlEnum;
import cn.lonsun.system.datadictionary.vo.DataDictVO;
import cn.lonsun.util.DataDictionaryUtil;
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
 * @ClassName: GuestBookListBeanService
 * @Description: 首页数据获取bean
 * @date 2015年12月5日 上午9:41:27
 */

@Component
public class GuestBookListBeanService extends AbstractBeanService {
    @DbInject("guestBook")
    private IGuestBookDao gusetBookDao;

    @Override
    public Object getObject(JSONObject paramObj) {
        Long[] ids = super.getQueryColumnIdByChild(paramObj, BaseContentEO.TypeCode.guestBook.toString());
        if (null == ids) {
            return null;
        }
        Context context = ContextHolder.getContext();
        Integer isReply = paramObj.getInteger("isReply");
        Integer size = ids.length;
        String recUnitId=context.getParamMap().get("organId");
        if(StringUtils.isEmpty(recUnitId)){
            recUnitId = String.valueOf(paramObj.getInteger("organId"));
        }
        Long siteId=paramObj.getLong("stationId");
        if(siteId==null||siteId==0){
            siteId=context.getSiteId();
        }
        Integer orderType  = paramObj.getInteger("orderType");

        StringBuffer hql =
                new StringBuffer("select c.title as title,c.publishDate as publishDate,c.createDate as createDate,c.isPublish as isPublish")
                        .append(",c.id as baseContentId,c.siteId as siteId,c.columnId as columnId,c.hit as hit,g.id as id,g.docNum as docNum")
                        .append(",g.responseContent as responseContent,g.guestBookContent as guestBookContent,g.dealStatus as dealStatus,g.addDate as addDate")
                        .append(",g.personIp as personIp,g.personName as personName,g.replyDate as replyDate,g.userId as userId,g.userName as userName")
                        .append(",g.recType as recType,g.receiveId as receiveId,g.receiveName as receiveName,g.receiveUserCode as receiveUserCode")
                        .append(",g.replyDate as replyDate,g.replyUnitId as replyUnitId,g.replyUnitName as replyUnitName")
                        .append(" from BaseContentEO c,GuestBookEO g where g.baseContentId=c.id and c.recordStatus=:recordStatus ");
        hql.append(" and c.columnId").append(size == 1 ? " =:ids " : " in (:ids) ")
                .append(" and c.siteId=:siteId and c.isPublish=1 and g.isPublic=1");
        Map<String, Object> map = new HashMap<String, Object>();

        map.put("recordStatus", AMockEntity.RecordStatus.Normal.toString());
        if (size == 1) {
            map.put("ids", ids[0]);
        } else {
            map.put("ids", ids);
        }
        map.put("siteId", siteId);
        if (isReply != null && isReply == 1) {
            hql.append(" and g.dealStatus in('handled','replyed') ");
        }
        if (isReply != null && isReply == 2) {
            hql.append(" and g.dealStatus in('handled','replyed','handling') ");
        }
        if(!StringUtils.isEmpty(recUnitId)){
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
     * @see cn.lonsun.staticcenter.generate.tag.impl.AbstractBeanService#(java.lang.Object,
     * java.lang.Object)
     */
    @Override
    @SuppressWarnings("unchecked")
    public Map<String, Object> doProcess(Object resultObj, JSONObject paramObj) throws GenerateException {
        // 预处理数据，处理文章链接问题
        List<GuestBookEditVO> list = (List<GuestBookEditVO>) resultObj;
        if (null != list && !list.isEmpty()) {
            Long siteId=paramObj.getLong("siteId");
            // 处理文章链接
            for (GuestBookEditVO vo : list) {
                if(siteId!=null&&siteId!=0){
                    IndicatorEO indicatorEO=getEntity(IndicatorEO.class,siteId);
                    vo.setLink(indicatorEO.getUri()+"/content/article/"+vo.getBaseContentId());
                }else{
                    //String path = PathUtil.getLinkPath(HtmlEnum.CONTENT.getValue(), HtmlEnum.ACRTILE.getValue(), vo.getColumnId(), vo.getBaseContentId()) + "?id=" + vo.getBaseContentId();
                    String path = PathUtil.getLinkPath(vo.getColumnId(), vo.getBaseContentId());//拿到栏目页和文章页id
                    vo.setLink(path);
                }
                DataDictVO dictVO = DataDictionaryUtil.getItem("petition_purpose", vo.getClassCode());
                if (dictVO != null) {
                    vo.setClassName(dictVO.getKey());
                }
                if (vo.getReceiveId() != null) {
                    OrganEO organEO = CacheHandler.getEntity(OrganEO.class, vo.getReceiveId());
                    if (organEO != null) {
                        vo.setReceiveName(organEO.getName());
                    }
                }
                if (!StringUtils.isEmpty(vo.getReceiveUserCode())) {
                    DataDictVO dictVO1 = DataDictionaryUtil.getItem("guest_book_rec_users", vo.getReceiveUserCode());
                    if (dictVO != null) {
                        vo.setReceiveUserName(dictVO1.getKey());
                    }
                }
                if (!StringUtils.isEmpty(vo.getCommentCode())) {
                    DataDictVO dictVO2 = DataDictionaryUtil.getItem("guest_comment", vo.getCommentCode());
                    if (dictVO2 != null) {
                        vo.setCommentName(dictVO2.getKey());
                    }
                }
            }
        }
        return super.doProcess(resultObj, paramObj);
    }
}