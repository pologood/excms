package cn.lonsun.staticcenter.generate.tag.impl.govbbs;

import cn.lonsun.cache.client.CacheHandler;
import cn.lonsun.core.base.util.StringUtils;
import cn.lonsun.core.util.ContextHolderUtils;
import cn.lonsun.core.util.Pagination;
import cn.lonsun.govbbs.internal.dao.IBbsPlateDao;
import cn.lonsun.govbbs.internal.dao.IBbsPostDao;
import cn.lonsun.govbbs.internal.entity.BbsMemberRoleEO;
import cn.lonsun.govbbs.internal.entity.BbsPlateEO;
import cn.lonsun.govbbs.internal.entity.BbsSettingEO;
import cn.lonsun.govbbs.internal.vo.*;
import cn.lonsun.govbbs.util.BbsSettingUtil;
import cn.lonsun.govbbs.util.BbsStaticCenterUtil;
import cn.lonsun.govbbs.util.MemberRoleUtil;
import cn.lonsun.rbac.internal.service.IOrganService;
import cn.lonsun.rbac.internal.vo.OrganVO;
import cn.lonsun.staticcenter.generate.GenerateConstant;
import cn.lonsun.staticcenter.generate.GenerateException;
import cn.lonsun.staticcenter.generate.tag.impl.AbstractBeanService;
import cn.lonsun.staticcenter.generate.thread.Context;
import cn.lonsun.staticcenter.generate.thread.ContextHolder;
import cn.lonsun.staticcenter.generate.util.PathUtil;
import cn.lonsun.staticcenter.service.HtmlEnum;
import cn.lonsun.system.member.internal.dao.IMemberDao;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.beanutils.ConvertUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpSession;
import java.util.*;

/**
 * 主题分页列表
 * Created by zhangchao on 2016/12/27.
 */
@Component
public class GovBbsPostPageListBeanService extends AbstractBeanService {

    @Autowired
    private IBbsPostDao bbsPostDao;

    @Autowired
    private IBbsPlateDao bbsPlateDao;

    @Autowired
    private IOrganService organService;

    @Autowired
    private IMemberDao memberDao;

    @Override
    public Object getObject(JSONObject paramObj) throws GenerateException {
        Context context = ContextHolder.getContext();
        String action  = context.getAction();
        BbsPlateEO plate = (BbsPlateEO) context.getUserData();
        Map<String, Object> objects = new HashMap<String, Object>();
        /** -------------------------标签参数处理- start---------------------------------- */
        //站点id
        Long siteId = paramObj.getLong("siteId");
        if (!(siteId != null && siteId != 0)) {
            siteId = context.getSiteId();
        }
        //默认保存版块id
        Long columnId = context.getColumnId();
        // 需要显示条数.
        Integer pageSize = paramObj.getInteger(GenerateConstant.PAGE_SIZE);
        pageSize = (pageSize == null?16:pageSize);
        Long pageIndex = context.getPageIndex() == null ? 0 : (context.getPageIndex() - 1);
        //版块ids
        List<Long> plateIds = null;
        //信息类型
        List<String> keys = null;
        String plateIdStrs = paramObj.getString("plateIds");
        if (!StringUtils.isEmpty(plateIdStrs)) {
            plateIds = new ArrayList<Long>();
            plateIds.addAll(Arrays.asList((Long[]) (ConvertUtils.convert(plateIdStrs.split(","), Long.class))));
        }
        String keyStrs = paramObj.getString("keys");
        if (!StringUtils.isEmpty(keyStrs)) {
            keys = new ArrayList<String>();
            keys.addAll(Arrays.asList((String[]) (ConvertUtils.convert(keyStrs.split(","), String.class))));
        }
        //默认排序
        String sortField = paramObj.getString("sortField");
        if(StringUtils.isEmpty(sortField)){
            sortField = BbsStaticCenterUtil.defaultPostOrder;
        }
        //是否查询需要办理的
        Boolean isAccept = paramObj.getBoolean("isAccept");
        if(isAccept == null){isAccept = false;}

        //是否查询红牌
        String ry = paramObj.getString("isRedYellow");
        Integer isRedYellow = null;
        if(!StringUtils.isEmpty(ry)){
            isRedYellow = BbsStaticCenterUtil.getLevel(ry);
        }
        //是否影藏一个月没有回复的
        Boolean isHideMon = paramObj.getBoolean("isHideMon");
        if(isHideMon == null){isHideMon = false;}

        //是否影藏一个月没有回复的
        Boolean loadMPics = paramObj.getBoolean("loadMPics");
        if(loadMPics == null){loadMPics = false;}


        /** -------------------------标签参数处理 -end----------------------------------- */
        PostQuerySCVO query = getParamMap(context.getParamMap());
        //当前登录人信息
        HttpSession session = ContextHolderUtils.getSession();
        BbsMemberVO member = (BbsMemberVO) session.getAttribute("bbsUser");
        //版块设置
        BbsSettingEO sett = BbsSettingUtil.getSiteBbsSetting(siteId);
        //获取发帖、快速发帖权限
        PermissionVO permission = getPermissionVO(plate,sett,member,query);

        //办理版块id
        String handelPlateIds = paramObj.getString("handelPlateIds");
        Long plateId_h = query.getId() == null?columnId:query.getId();
        //办理单位
        List<OrganVO> organs = null;
        //是否加载后单位  //是否加载版块下帖子数
        Boolean loadUnits = false,isLoadCount = false;
        //发帖时是否显示办理单位
        if(permission.getFastPost() == 1 && !StringUtils.isEmpty(handelPlateIds) && handelPlateIds.indexOf(plateId_h+"") > -1){
            permission.setIsShowUnits(1);
            organs = organService.getOrgansBySiteId(siteId,true);
            loadUnits = true;
        }
        /** -------------------------链接参数 -start----------------------------------- */
        List<BbsPlateWebVO> plates = getPlateChlids(siteId,plate.getParentIds());
        Long total = 0L;
        if(null == plateIds){
            plateIds = new ArrayList<Long>();
            plateIds.add(plate.getPlateId());
            if(plates != null && plates.size() > 0){
                for(BbsPlateWebVO p:plates){
                    plateIds.add(p.getPlateId());
                }
            }
        }
        if(HtmlEnum.GOVMB.getValue().equals(action)){
            //如果是部门信箱查询所有单位
            organs = getOrganVOs(siteId,plateIds,loadUnits,organs);
            loadUnits = true;
//            //类型
//            List<DataDictVO> infoKeys = DataDictionaryUtil.getDDList("bbs_type");
//            objects.put("infoKeys",infoKeys);
        }else if(HtmlEnum.COLUMN.getValue().equals(action)){
            //查询子版块
            isLoadCount = true;
            Object[] objs= loadPlateCount(plates,siteId,plateIds);
            plates = (List<BbsPlateWebVO>)objs[0];
            total = (Long)objs[1];
        }

        /** -------------------------链接参数 -end----------------------------------- */
        //当前时间戳
        Long times = new Date().getTime()/1000;
        List<Object> values = new ArrayList<Object>();
        StringBuffer hql = new StringBuffer("select" +
                " b.postId as postId,b.plateId as plateId,b.title as title,b.viewCount as viewCount,b.replyCount as replyCount," +
                "b.isHeadTop as isHeadTop,b.isTop as isTop,b.isEssence as isEssence,b.isLock as isLock,b.isAccept as isAccept,b.memberId as memberId," +
                "b.memberName as memberName,b.yellowTimes as yellowTimes,b.redTimes as redTimes,b.lastMemberId as lastMemberId,b.lastMemberName as lastMemberName," +
                "b.lastTime as lastTime,b.hasFile as hasFile,b.support as support,b.createDate as createDate" +
                " from BbsPostEO b where b.siteId = ? and b.recordStatus = 'Normal' and b.isPublish = 1 and b.createDate is not null");
        values.add(siteId);
//        /**------------条件筛选  0-------------------*/
//        if(!StringUtils.isEmpty(parentIds)){
//            hql.append(" and b.parentIds like ?");
//            values.add(parentIds+"%");
//        }
        if(query != null){
            if(query.getId() != null){
                hql.append(" and b.plateId = ?");
                values.add(query.getId());
            }
            if(query.getOrganId() != null){
                hql.append(" and b.acceptUnitId = ?");
                values.add(query.getOrganId());
            }
            if(query.getIsAccept() != null){
                hql.append(" and b.isAccept = ?");
                values.add(query.getIsAccept());
            }
            if(query.getIsTop() != null){
                hql.append(" and b.isTop = ?");
                values.add(query.getIsTop());
            }
            if(query.getIsEssence() != null){
                hql.append(" and b.isEssence = ?");
                values.add(query.getIsEssence());
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
                }else if(query.getLevel() == 3){//红牌、
                    hql.append(" and b.isAccept = 0 ");
                    hql.append(" and b.redTimes <= ?");
                    values.add(times);
                }
            }
        }
        /**------------条件筛选  1-------------------*/

        /**------------------处理标签参数 0------------------**/
        if(plateIds != null && plateIds.size()> 0){
            hql.append(" and (1=0");
            for(Long plateId:plateIds){
                hql.append(" or b.plateId = ?");
                values.add(plateId);
            }
            hql.append(")");
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
        if(keys != null && keys.size()> 0){
            hql.append(" and (1=0");
            for(String key:keys){
                hql.append(" or b.infoKey = ?");
                values.add(key);
            }
            hql.append(")");
        }
        if(isAccept){
            hql.append(" and (b.isAccept = 1 or b.isAccept = 0) ");
        }
        if(null != isRedYellow){
            if(isRedYellow == 1){//正常的
                hql.append(" and (b.yellowTimes > ? or b.yellowTimes is null)");
                values.add(times);
            }else if(isRedYellow == 2){//黄牌
                hql.append(" and b.isAccept = 0 ");
                hql.append(" and b.yellowTimes <= ? and b.redTimes > ?");
                values.add(times);
                values.add(times);
            }else if(isRedYellow == 3){//红牌
                hql.append(" and b.isAccept = 0 ");
                hql.append(" and b.redTimes <= ?");
                values.add(times);
            }
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
            Map<Long,Long> memberIds = null;
            if(loadMPics){ memberIds = new HashMap<Long, Long>();   }
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
                if(post.getCreateDate() != null){
                    Long createTimes = post.getCreateDate().getTime()/1000;
                    if(hotTimes <= createTimes){
                        post.setIsNew(1);
                    }
                }
                //热帖
                if(hotCount != null && post.getReplyCount() != null &&
                        post.getReplyCount() >= hotCount){
                    post.setHot(1);
                }
                if(loadMPics){
                    if(post.getMemberId() != null){
                        memberIds.put(post.getMemberId(),post.getMemberId());
                    }
                }
            }
            //是否加载会员图片
            if(loadMPics){
                pagination = setMemberPics(pagination,memberIds,siteId);
            }
        }
        if(loadUnits){
            objects.put("organs",organs);
        }
        objects.put("total",total);
        objects.put("action",action);
        objects.put("plates",plates);
        objects.put("pages",pagination);
        objects.put("params",query);
        objects.put("member",member);
        objects.put("permission",permission);
        objects.put("linkUrl", PathUtil.getLinkPath(HtmlEnum.GOVBBS.getValue(), action, columnId, null));
        return objects;
    }

    private Pagination setMemberPics(Pagination pagination, Map<Long, Long> memberIds, Long siteId) {
        if(memberIds != null && memberIds.size() > 0){
            Map<Long,String> map = null;
            List<Object> values = new ArrayList<Object>();
            StringBuffer hql = new StringBuffer("select b.id as mId ,b.name as mName ,b.img as mImg " +
                    "from MemberEO b where b.siteId = ? and b.recordStatus = 'Normal'");
            values.add(siteId);
            hql.append(" and (1=0");
            if(memberIds != null && memberIds.size() > 0) {
                for (Map.Entry<Long, Long> entry : memberIds.entrySet()) {
                    hql.append(" or b.id = ?");
                    values.add(entry.getValue());
                }
            }
            hql.append(")");
            List<MemberInfoVO> objects = (List<MemberInfoVO>)memberDao.getBeansByHql(hql.toString(),values.toArray(),MemberInfoVO.class);
            if(objects != null && objects.size() > 0) {
                map = new HashMap<Long, String>();
                for (MemberInfoVO vo : objects) {
                    map.put(vo.getmId(),vo.getmImg());
                }
                for(BbsPostPageVO post:(List<BbsPostPageVO>)pagination.getData()){
                    if(post.getMemberId() != null){
                        post.setMemberImg(map.get(post.getMemberId()));
                    }
                }
            }
        }
        return pagination;
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


    /**
     * 获取所有子版块 和 版块帖子数
     * @param userData
     * @return
     */
    private List<BbsPlateWebVO> getPlateChlids(Long siteId, String parentIds) {
        if (!StringUtils.isEmpty(parentIds)) {
            String hql = "select plateId as plateId,parentId as parentId,name as name,parentIds as parentIds,canThread as canThread" +
                    " from BbsPlateEO where recordStatus = 'Normal' and siteId = ? and status = '1' and parentIds != ? and parentIds like ? order by plateId asc";
            return (List<BbsPlateWebVO>)bbsPlateDao.getBeansByHql(hql,new Object[]{siteId,parentIds,parentIds+"%"}, BbsPlateWebVO.class);
        }
        return null;
    }


    private Object[] loadPlateCount(List<BbsPlateWebVO> plates, Long siteId, List<Long> plateIds) {
        Object[] objs = new Object[2];
        Long allTatal = 0L;
        if(plates != null && plates.size() > 0){
            Map<Long,OrganCountVO> maps = getPlateCount(siteId,plateIds);
            if (maps != null) {
                for (BbsPlateWebVO p : plates) {
                    p.setTotal(0L);
                    if (maps.containsKey(p.getPlateId())) {
                        OrganCountVO vo = maps.get(p.getPlateId());
                        if(vo == null){
                            continue;
                        }
                        Long t = vo.getTotal() == null?0:vo.getTotal();
                        p.setTotal(t);
                        p.setTodayTotal(vo.getTodayTotal());
                        maps.remove(p.getPlateId());
                        allTatal +=t;
                    }
                }
                if(maps != null && maps.size() > 0){
                    for (Map.Entry<Long, OrganCountVO> entry : maps.entrySet()) {
                        OrganCountVO vo = entry.getValue();
                        if(vo == null){
                            continue;
                        }
                        Long t = vo.getTotal() == null?0:vo.getTotal();
                        allTatal +=t;
                    }
                }

            }
        }
        objs[0] = plates;
        objs[1] = allTatal;
        return objs;
    }
    /**
     * 获取所有单位
     * @param siteId
     * @return
     */
    private List<OrganVO> getOrganVOs(Long siteId, List<Long> plateIds, Boolean loadUnits, List<OrganVO> organs) {
        if(!loadUnits){
            organs = organService.getOrgansBySiteId(siteId,true);
        }
        if(organs != null && organs.size() > 0) {
            Map<Long, Long> ocMaps = getOrganCount(siteId,plateIds);
            if (ocMaps != null) {
                for (OrganVO v : organs) {
                    if (ocMaps.containsKey(v.getOrganId())) {
                        v.setTotal(ocMaps.get(v.getOrganId()));
                    }
                }
            }
        }
        return organs;
    }


    /**
     * 按单位统计帖子数
     * @param siteId
     * @return
     */
    private Map<Long,Long> getOrganCount(Long siteId,List<Long> plateIds) {
        Map<Long,Long> map = null;
        List<Object> values = new ArrayList<Object>();
        String hql = "select count(*) as total,b.acceptUnitId as organId from BbsPostEO b where b.recordStatus = 'Normal' and b.isPublish = 1 " +
                "and b.siteId = ? and (b.isAccept = 0 or b.isAccept = 1) ";
        values.add(siteId);
        hql +=" and (1=0";
        if(plateIds != null && plateIds.size()> 0){
            for(Long plateId:plateIds){
                if(plateId != null){
                    hql +=" or b.plateId = ?";
                    values.add(plateId);
                }
            }
        }
        hql +=")";
        hql +=" group by b.acceptUnitId";
        List<OrganCountVO> ocs = (List<OrganCountVO>)bbsPostDao.getBeansByHql(hql,values.toArray(),OrganCountVO.class);
        if(ocs != null && ocs.size() > 0){
            map = new HashMap<Long, Long>();
            for(OrganCountVO oc:ocs){
                map.put(oc.getOrganId(),oc.getTotal());
            }
        }
        return map;
    }

    /**
     * 按版块统计帖子数
     * @param siteId
     * @return
     */
    private Map<Long,OrganCountVO> getPlateCount(Long siteId, List<Long> plateIds) {
        Map<Long,OrganCountVO> map = null;
//        String hql = "select b.plateId as plateId,count(*) as total," +
//                "sum(CASE WHEN to_char(b.createDate,'yyyy-mm-dd') = ? THEN 1 else 0 END) as todayTotal " +
//                "from BbsPostEO b where b.recordStatus = 'Normal' and b.isPublish = 1 " +
//                "and b.siteId = ? and b.parentIds like ? group by b.plateId";
        List<Object> values = new ArrayList<Object>();
        String hql = "select b.plateId as plateId,count(*) as total " +
                "from BbsPostEO b where b.recordStatus = 'Normal' and b.isPublish = 1 " +
                "and b.siteId = ?";
        values.add(siteId);
        hql +=" and (1=0";
        if(plateIds != null && plateIds.size()> 0){
            for(Long plateId:plateIds){
                if(plateId != null){
                    hql +=" or b.plateId = ?";
                    values.add(plateId);
                }
            }
        }
        hql +=")";
        hql +=" group by b.plateId";
        List<OrganCountVO> ocs = (List<OrganCountVO>)bbsPostDao.getBeansByHql(hql,values.toArray(),OrganCountVO.class);
        if(ocs != null && ocs.size() > 0){
            map = new HashMap<Long, OrganCountVO>();
            for(OrganCountVO oc:ocs){
                map.put(oc.getPlateId(),oc);
            }
        }
        return map;
    }


    /**
     * 权限处理
     * @param plate   版块
     * @param sett    设置
     * @param member  会员
     * @param query   其他版块
     * @return
     */
    private static PermissionVO getPermissionVO(BbsPlateEO plate, BbsSettingEO sett, BbsMemberVO member, PostQuerySCVO query) {
        PermissionVO permission = new PermissionVO();
        //如果是选择版块
        if(query != null && query.getId() != null){
            plate = CacheHandler.getEntity(BbsPlateEO.class,query.getId());
        }
        //版块是否可以发帖、回帖
        if(plate != null){
            permission.setIsPost(plate.getCanThread() == null?0:plate.getCanThread());
        }
        //会员判断
        if(permission.getIsPost() == 1 && member != null){
            if(member.getMemberType() ==1){//部门会员
                permission.setIsPost(1);
            }else if (member.getMemberType() == 2) { //判断游客类型
                permission.setIsPost(sett.getVisitorCanThread() == null ? 0 : sett.getVisitorCanThread());
            }else if(member.getMemberType() == 0 && member.getMemberRoleId() != null){//会员
                BbsMemberRoleEO mrole = MemberRoleUtil.getMemberRole(member.getMemberRoleId());
                if(mrole != null){
                    permission.setIsPost(mrole.getCanThread() == null ? 0:mrole.getCanThread());
                }
            }
        }
        //如果是发帖 判断是否可以快速发帖
        if(permission.getIsPost() == 1){
            permission.setFastPost(sett.getFastThreadOn() == null ? 0 : sett.getFastThreadOn());
        }
        return permission;
    }
}
