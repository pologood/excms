package cn.lonsun.staticcenter.generate.tag.impl.guestbook;

import cn.lonsun.base.anno.DbInject;
import cn.lonsun.cache.client.CacheHandler;
import cn.lonsun.common.util.AppUtil;
import cn.lonsun.content.internal.dao.IGuestBookDao;
import cn.lonsun.content.vo.GuestBookEditVO;
import cn.lonsun.core.base.entity.AMockEntity;
import cn.lonsun.core.util.Pagination;
import cn.lonsun.rbac.internal.entity.OrganEO;
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
import cn.lonsun.util.DataDictionaryUtil;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * <br/>
 *
 * @author wangss <br/>
 * @version v1.0 <br/>
 * @date 2016-10-8<br/>
 */
@Component
public class GuestBookSearchListBeanService extends AbstractBeanService {

    @Autowired
    private IMemberService memberService;

    @DbInject("guestBook")
    private IGuestBookDao gusetBookDao;

    @Override
    public Object getObject(JSONObject paramObj) {
        Context context = ContextHolder.getContext();
        String idStr=paramObj.getString("id");
        Long[] ids=null;
        if(!StringUtils.isEmpty(idStr)){
            ids=AppUtil.getLongs(idStr,",");
        }
        if (null == ids) {
            return null;
        }
        Integer size = ids.length;
        Long pageIndex = context.getPageIndex() == null ? 0 : (context.getPageIndex() - 1);
        Integer pageSize = paramObj.getInteger(GenerateConstant.PAGE_SIZE);
        Integer isReply = paramObj.getInteger("isReply");

        Map<String, String> map = context.getParamMap();
        String title = map.get("title");
        String startDate = map.get("startDate");
        String endDate = map.get("endDate");
        String idNum=map.get("idNum");
        String rStartDate = map.get("rStartDate");
        String rEndDate = map.get("rEndDate");
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

        String classCode = context.getParamMap().get("classCode");
        Long siteId = paramObj.getLong("siteId");
        if (siteId == null) {
            siteId = context.getSiteId();
        }

        String uidStr=context.getParamMap().get("uid");
        Long uid=null;
        if(!StringUtils.isEmpty(uidStr)){
            uid = Long.parseLong(uidStr);
        }

        StringBuffer hql =
                new StringBuffer("select c.title as title,c.publishDate as publishDate,c.createDate as createDate,c.isPublish as isPublish")
                        .append(",c.id as baseContentId,c.siteId as siteId,c.columnId as columnId,c.hit as hit,g.id as id,g.docNum as docNum")
                        .append(",g.responseContent as responseContent,g.guestBookContent as guestBookContent,g.dealStatus as dealStatus,g.addDate as addDate")
                        .append(",g.personIp as personIp,g.personName as personName,g.replyDate as replyDate,g.userId as userId,g.userName as userName")
                        .append(",g.recType as recType,g.receiveId as receiveId,g.receiveName as receiveName,g.receiveUserCode as receiveUserCode")
                        .append(",g.replyDate as replyDate,g.replyUnitId as replyUnitId,g.replyUnitName as replyUnitName")
                        .append(" from BaseContentEO c,GuestBookEO g where g.baseContentId=c.id and c.recordStatus=? ")
                         .append(" and c.isPublish=1 and g.isPublic=1 and c.siteId=?");
        List<Object> values = new ArrayList<Object>();
        values.add(AMockEntity.RecordStatus.Normal.toString());
        values.add(siteId);
        if(size==1){
            hql.append(" and c.columnId=?");
            values.add(ids[0]);
        }else{
            hql.append(" and c.columnId in("+idStr+")");
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

        Pagination page = gusetBookDao.getPagination(pageIndex, pageSize, hql.toString(), values.toArray(), GuestBookEditVO.class);
        return page;

    }

    public Map<String, Object> doProcess(Object resultObj, JSONObject paramObj) throws GenerateException {
        Map<String, Object> map = super.doProcess(resultObj, paramObj);
        Context context = ContextHolder.getContext();

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
                    if (vo.getReceiveId() != null) {
                        OrganEO organEO = CacheHandler.getEntity(OrganEO.class, vo.getReceiveId());
                        if (organEO != null) {
                            vo.setReceiveName(organEO.getName());
                        }
                    }
                    if (!StringUtils.isEmpty(vo.getReceiveUserCode())) {
                        DataDictVO dictVO = DataDictionaryUtil.getItem("guest_book_rec_users", vo.getReceiveUserCode());
                        if (dictVO != null) {
                            vo.setReceiveUserName(dictVO.getKey());
                        }
                    }
                    if (!StringUtils.isEmpty(vo.getClassCode())) {
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

            String classCode=context.getParamMap().get("classCode");
            if(!StringUtils.isEmpty(classCode)){
                map.put("classCode",classCode);
            }

        }
        return map;
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


}
