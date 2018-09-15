package cn.lonsun.staticcenter.generate.tag.impl.govbbs;

import cn.lonsun.common.util.AppUtil;
import cn.lonsun.core.base.util.StringUtils;
import cn.lonsun.core.util.Pagination;
import cn.lonsun.core.util.SqlUtil;
import cn.lonsun.govbbs.internal.dao.IBbsPostDao;
import cn.lonsun.govbbs.internal.entity.BbsSettingEO;
import cn.lonsun.govbbs.internal.vo.BbsPostPageVO;
import cn.lonsun.govbbs.internal.vo.PostQuerySCVO;
import cn.lonsun.govbbs.util.BbsSettingUtil;
import cn.lonsun.govbbs.util.BbsStaticCenterUtil;
import cn.lonsun.rbac.internal.service.IOrganService;
import cn.lonsun.staticcenter.generate.GenerateConstant;
import cn.lonsun.staticcenter.generate.tag.impl.AbstractBeanService;
import cn.lonsun.staticcenter.generate.thread.Context;
import cn.lonsun.staticcenter.generate.thread.ContextHolder;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.beanutils.ConvertUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by zhangchao on 2017/2/7.
 */
@Component
public class GovSearchPageListBeanService extends AbstractBeanService {

    @Autowired
    private IBbsPostDao bbsPostDao;

    @Autowired
    private IOrganService organService;

    @Override
    public Object getObject(JSONObject paramObj) {
        Map<String, Object> objects = new HashMap<String, Object>();
        Context context = ContextHolder.getContext();
        Long siteId = paramObj.getLong("siteId");
        if (!(siteId != null && siteId != 0)) {
            siteId = context.getSiteId();
        }
        //版块设置
        BbsSettingEO sett = BbsSettingUtil.getSiteBbsSetting(siteId);
        // 需要显示条数.
        Integer pageSize = paramObj.getInteger(GenerateConstant.PAGE_SIZE);
        pageSize = (pageSize == null?16:pageSize);
        Long pageIndex = context.getPageIndex() == null ? 0 : (context.getPageIndex() - 1);

        //版块ids
        List<Long> plateIds = null;
        List<Long> delPlateIds = null;
        //信息类型
        String plateIdStrs = paramObj.getString("plateIds");
        String delPlateIdStrs = paramObj.getString("delPlateIds");
        if (!StringUtils.isEmpty(plateIdStrs)) {
            plateIds = new ArrayList<Long>();
            plateIds.addAll(Arrays.asList((Long[]) (ConvertUtils.convert(plateIdStrs.split(","), Long.class))));
        }
        if (!StringUtils.isEmpty(delPlateIdStrs)) {
            delPlateIds = new ArrayList<Long>();
            delPlateIds.addAll(Arrays.asList((Long[]) (ConvertUtils.convert(delPlateIdStrs.split(","), Long.class))));
        }
        //默认排序
        String sortField = paramObj.getString("sortField");
        if(StringUtils.isEmpty(sortField)){
            sortField = BbsStaticCenterUtil.defaultPostOrder;
        }
        //是否查询需要办理的
        Boolean isAccept = paramObj.getBoolean("isAccept");
        if(isAccept == null){isAccept = false;}
        //是否影藏一个月没有回复的
        Boolean isHideMon = paramObj.getBoolean("isHideMon");
        if(isHideMon == null){isHideMon = false;}

        PostQuerySCVO query = getParamMap(context.getParamMap());

        //关键词
        String keywords = context.getParamMap().get("keywords");
        String startDate = context.getParamMap().get("startDate");
        String endDate = context.getParamMap().get("endDate");

        SimpleDateFormat simple= new SimpleDateFormat("yyyy-MM-dd");
        Long times = new Date().getTime()/1000;
        List<Object> values = new ArrayList<Object>();
        StringBuffer hql = new StringBuffer("select" +
                " b.postId as postId,b.plateId as plateId,b.title as title,b.viewCount as viewCount,b.replyCount as replyCount," +
                "b.isHeadTop as isHeadTop,b.isTop as isTop,b.isEssence as isEssence,b.isLock as isLock,b.isAccept as isAccept,b.memberId as memberId," +
                "b.memberName as memberName,b.yellowTimes as yellowTimes,b.redTimes as redTimes,b.lastMemberId as lastMemberId,b.lastMemberName as lastMemberName," +
                "b.lastTime as lastTime,b.hasFile as hasFile,b.support as support,b.createDate as createDate" +
                " from BbsPostEO b where b.siteId = ? and b.recordStatus = 'Normal' and b.isPublish = 1 and b.createDate is not null");
        values.add(siteId);
        if(query != null){
            if(query.getOrganId() != null){
                hql.append(" and b.acceptUnitId = ?");
                values.add(query.getOrganId());
            }
            if(query.getIsAccept() != null){
                hql.append(" and b.isAccept = ?");
                values.add(query.getIsAccept());
            }
            if(query.getLevel() != null){
                if(query.getLevel() == 1){//正常的
                    hql.append(" and (b.yellowTimes > ? or b.yellowTimes is null)");
                    values.add(times);
                }else if(query.getLevel() == 2){//黄牌
                    hql.append(" and b.isAccept = 0 ");
                    hql.append(" and b.yellowTimes <= ? and b.redTimes > ?");
                    values.add(times);
                    values.add(times);
                }else if(query.getLevel() == 3){//红牌
                    hql.append(" and b.isAccept = 0 ");
                    hql.append(" and b.redTimes <= ?");
                    values.add(times);
                }
            }
        }
        if(plateIds != null && plateIds.size()> 0){
            hql.append(" and (1=0");
            for(Long plateId:plateIds){
                hql.append(" or b.plateId = ?");
                values.add(plateId);
            }
            hql.append(")");
        }
        if(delPlateIds != null && delPlateIds.size() > 0){
            for(Long plateId:delPlateIds){
                hql.append(" and b.plateId != ?");
                values.add(plateId);
            }
        }
        if(isHideMon){
            Date end = new Date();
            Calendar date = Calendar.getInstance();
            date.setTime(end);
            date.set(Calendar.DATE, date.get(Calendar.DATE) - 30);
            end = date.getTime();
            hql.append(" and (b.isAccept is null or b.isAccept = 1 or (b.isAccept = 0 and b.createDate >= ?))");
            values.add(end);
        }
        if(isAccept){
            hql.append(" and (b.isAccept = 1 or b.isAccept = 0) ");
        }
        if(!StringUtils.isEmpty(keywords)){
            hql.append(" and (b.title like ? or b.memberName like ? or b.content like ?)");
            values.add("%"+ SqlUtil.prepareParam4Query(keywords).trim()+"%");
            values.add("%"+ SqlUtil.prepareParam4Query(keywords).trim()+"%");
            values.add("%"+ SqlUtil.prepareParam4Query(keywords).trim()+"%");
        }
        if (!AppUtil.isEmpty(startDate)) {
            Date start = null;
            try{
                start = simple.parse(startDate);
            }catch (Exception e){}
            hql.append(" and b.createDate >= ?");
            values.add(start);
        }
        // 结束时间
        if (!AppUtil.isEmpty(endDate)) {
            Date end = null;
            try{
                end = simple.parse(endDate);
            }catch (Exception e){}
            Calendar date = Calendar.getInstance();
            date.setTime(end);
            date.set(Calendar.DATE, date.get(Calendar.DATE) + 1);// 结束日期增加一天
            end = date.getTime();
            hql.append(" and b.createDate <= ?");
            values.add(end);
        }
        /**------------------处理标签参数 1------------------**/
        hql.append(" order by ");
        if(query.getIsRead() != null){
            hql.append("viewCount desc,");
        }else if(query.getIsReply() != null){
            hql.append("replyCount desc,");
        }
        hql.append(sortField);
        Pagination pagination = bbsPostDao.getPagination(pageIndex, pageSize, hql.toString(), values.toArray(),BbsPostPageVO.class);
        if(pagination.getData() != null && pagination.getData().size() > 0 ){
            Integer newTime = 24;
            Integer hotCount = null;
            if(sett != null){
                newTime = sett.getNewTitle() == null?1:sett.getNewTitle();
                hotCount = sett.getHotTitle();
            }
            Long hotTimes = times - (60*60*newTime);
            for(BbsPostPageVO post:(List<BbsPostPageVO>)pagination.getData()){
                //红黄牌
                if(post.getIsAccept() != null){
                    if(post.getYellowTimes() != null && post.getYellowTimes() <= times && post.getRedTimes() > times){
                        post.setYellowCard(1);
                    }
                    if(post.getRedTimes() != null && post.getRedTimes() <= times){
                        post.setRedCard(1);
                    }
                }
                //火贴
                Long createTimes = post.getCreateDate().getTime()/1000;
                if(hotTimes <= createTimes){
                    post.setIsNew(1);
                }
                //热帖
                if(hotCount != null && post.getReplyCount() != null &&
                        post.getReplyCount() >= hotCount){
                    post.setHot(1);
                }
            }
        }
        objects.put("organs",organService.getOrgansBySiteId(siteId,true));
        objects.put("pages",pagination);
        objects.put("params",query);
        objects.put("startDate",startDate);
        objects.put("endDate",endDate);
        objects.put("keywords",keywords);
        return objects;
    }

    //获取参数
    private PostQuerySCVO getParamMap(Map<String, String> paramMap) {
        PostQuerySCVO pq = null;
        if(paramMap != null){
            pq = new PostQuerySCVO();
            String id = paramMap.get("id");
            if(!StringUtils.isEmpty(id)){
                pq.setId(Long.parseLong(id));
            }
            String organId = paramMap.get("organId");
            if(!StringUtils.isEmpty(organId)){
                pq.setOrganId(Long.parseLong(organId));
            }
            //信息状态
            String infoKey = paramMap.get("infoKey");
            if(!StringUtils.isEmpty(infoKey)){
                pq.setInfoKey(infoKey);
            }
            //信息状态
            String isAccept = paramMap.get("isAccept");
            if(!StringUtils.isEmpty(isAccept)){
                pq.setIsAccept(Integer.parseInt(isAccept));
            }
            String level = paramMap.get("level");
            if(!StringUtils.isEmpty(level)){
                pq.setLevel(Integer.parseInt(level));
            }
            String sortKey = paramMap.get("sortKey");
            if(!StringUtils.isEmpty(sortKey)){
                pq.setSortKey(sortKey);
                if(sortKey.equals("isTop")){
                    pq.setIsTop(1);
                }else  if(sortKey.equals("isEssence")){
                    pq.setIsEssence(1);
                }else  if(sortKey.equals("isRead")){
                    pq.setIsRead(1);
                }else  if(sortKey.equals("isReply")){
                    pq.setIsReply(1);
                }
            }
        }
        return pq;
    }
}
