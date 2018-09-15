package cn.lonsun.staticcenter.generate.tag.impl.govbbs;

import cn.lonsun.core.base.util.StringUtils;
import cn.lonsun.core.util.ContextHolderUtils;
import cn.lonsun.core.util.Pagination;
import cn.lonsun.govbbs.internal.dao.IBbsFileDao;
import cn.lonsun.govbbs.internal.dao.IBbsPostDao;
import cn.lonsun.govbbs.internal.dao.IBbsReplyDao;
import cn.lonsun.govbbs.internal.entity.*;
import cn.lonsun.govbbs.internal.vo.*;
import cn.lonsun.govbbs.util.BbsLogUtil;
import cn.lonsun.govbbs.util.BbsSettingUtil;
import cn.lonsun.govbbs.util.MemberRoleUtil;
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
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpSession;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by zhangchao on 2017/1/4.
 */
@Component
public class GovBbsReplyPageListBeanService extends AbstractBeanService {

    @Autowired
    private IBbsPostDao bbsPostDao;

    @Autowired
    private IBbsReplyDao bbsReplyDao;

    @Autowired
    private IMemberDao memberDao;


    @Autowired
    private IBbsFileDao bbsFileDao;



    @Override
    public Object getObject(JSONObject paramObj) throws GenerateException {
        Map<String, Object> objects = new HashMap<String, Object>();
        Context context = ContextHolder.getContext();
        //获取帖子 版块
        Object[] obs = (Object[])context.getUserData();
        BbsPostEO post = (BbsPostEO)obs[0];
        BbsPlateEO plate = (BbsPlateEO)obs[1];
        Map<Long,Long> memberIds = new HashMap<Long, Long>();
        //站点id
        Long siteId = paramObj.getLong("siteId");
        if (!(siteId != null && siteId != 0)) {
            siteId = context.getSiteId();
        }
        //当前登录人信息
        HttpSession session = ContextHolderUtils.getSession();
        BbsMemberVO member = (BbsMemberVO) session.getAttribute("bbsUser");
//版块设置
        BbsSettingEO sett = BbsSettingUtil.getSiteBbsSetting(siteId);

        //获取发帖、快速发帖权限
        PermissionVO permission = getPermissionVO(plate,post,sett,member);
        //楼层解析
        List<String> floors = null;
        if(!StringUtils.isEmpty(sett.getThreadFloor())){
            floors = new ArrayList<String>();
            String[] strs = sett.getThreadFloor().split(",");
            if(strs != null && strs.length > 0){
                for(String str:strs){
                    floors.add(str);
                }
            }
        }
        //默认保存版块id
        Long columnId = context.getColumnId();
        // 需要显示条数.
        Integer pageSize = paramObj.getInteger(GenerateConstant.PAGE_SIZE);
        pageSize = (pageSize == null?16:pageSize);
        Long pageIndex = context.getPageIndex() == null ? 0 : (context.getPageIndex() - 1);

        //帖子id
        Long postId = 0L;
        if(post != null){
            postId = post.getPostId();
            //会员时 查询会员信息
            if(post.getAddType() == 1 && post.getMemberId() != null){
                memberIds.put(post.getMemberId(),post.getMemberId());
            }
        }
        //版块id
        Long plateId = (plate == null?0L:plate.getPlateId());

        /**------------------链接参数---------------*/
        String midc = context.getParamMap().get("mid");
        Long mid = null;
        if(!StringUtils.isEmpty(midc)){
            mid = Long.parseLong(midc);
        }
        /**-----------------------------标签参数 0---------------------------------------*/
        //版块ids
        List<Long> plateIds = null;
        String plateIdStrs = paramObj.getString("plateIds");
        if (!StringUtils.isEmpty(plateIdStrs)) {
            plateIds = new ArrayList<Long>();
            plateIds.addAll(Arrays.asList((Long[]) (ConvertUtils.convert(plateIdStrs.split(","), Long.class))));
        }
        //默认排序
        String sortField = paramObj.getString("sortField");
        if(StringUtils.isEmpty(sortField)){
            sortField = "b.createDate asc";
        }
        //是否查询需要办理的
        Boolean isHandle = paramObj.getBoolean("isHandle");
        if(isHandle == null){isHandle = false;}
        /**-----------------------------标签参数 1---------------------------------------*/


        List<Object> values = new ArrayList<Object>();
        StringBuffer hql = new StringBuffer("select b.replyId as replyId,b.content as content,b.memberId as mId,b.memberName as mName," +
                "b.createDate as createDate,b.ip as ip,b.isHandle as isHandle,b.handleUnitId as handleUnitId,b.handleUnitName as handleUnitName,b.addType as addType " +
                "from BbsReplyEO b where b.siteId = ? and b.recordStatus = 'Normal' and b.isPublish = 1");
        values.add(siteId);
        if(plateId != null){
            hql.append(" and b.plateId = ?");
            values.add(plateId);
        }
        if(postId != null){
            hql.append(" and b.postId = ?");
            values.add(postId);
        }
        if(mid != null){
            hql.append(" and b.memberId = ?");
            values.add(mid);
        }
        if(plateIds != null && plateIds.size()> 0){
            hql.append(" and (1=0");
            for(Long id:plateIds){
                hql.append(" or b.plateId = ?");
                values.add(id);
            }
            hql.append(")");
        }
        if(isHandle){
            hql.append(" and b.isHandle = 1");
        }
        hql.append(" order by ").append(sortField);
        Pagination pagination = bbsReplyDao.getPagination(pageIndex, pageSize, hql.toString(), values.toArray(), ReplyStaticVO.class);
        if(pagination.getData() != null && pagination.getData().size() > 0){
            for(ReplyStaticVO vo:(List<ReplyStaticVO>)pagination.getData()){
                Long memberId = vo.getmId();
                //会员时
                if(vo.getAddType() == 1 && memberId != null){
                    memberIds.put(memberId,memberId);
                }
            }
        }
        //查询此分页的所有会员信息
        Object memberInfos = getMembers(siteId,memberIds);
        objects = getReturnParams(objects,post,memberInfos,pagination,floors,sett.getHotTitle());
        objects.put("linkUrl", PathUtil.getLinkPath(HtmlEnum.GOVBBS.getValue(), HtmlEnum.CONTENT.getValue(), postId, null));
        objects.put("mid",mid);
        objects.put("permission",permission);
        return objects;
    }

    private Map<String, Object> getReturnParams(Map<String, Object> objects, BbsPostEO post, Object memberInfos, Pagination pagination, List<String> floors, Integer hotCounts) {
        HttpSession session = ContextHolderUtils.getSession();
        BbsMemberVO member = (BbsMemberVO) session.getAttribute("bbsUser");
        Map<Long, MemberInfoVO> memberMap = (Map<Long, MemberInfoVO>)memberInfos;
        PostStaticVO pv = null;
        Integer fsize= 0;
        if(floors != null && floors.size() >0){
            fsize = floors.size();
        }
        //楼主设置
        if(post != null){
            pv = new PostStaticVO();
            if(memberMap != null && memberMap.containsKey(post.getMemberId())){
                BeanUtils.copyProperties(memberMap.get(post.getMemberId()),pv);
            }
            BeanUtils.copyProperties(post,pv);
            //设置所有支持、反对记录
            pv = setLogs(pv,member);
            //设置楼层
            if(fsize > 0){
                pv.setFloor(floors.get(0));
            }
            //设置附件
            pv.setFiles(getBbsFiles(pv.getSiteId(),pv.getPostId()));
            //热帖
            if(hotCounts != null && post.getReplyCount() != null &&
                    post.getReplyCount() >= hotCounts){
                pv.setHot(1);
            }
        }
        //分页设置
        if(pagination.getData() != null && pagination.getData().size() > 0) {
            List<ReplyStaticVO> vos = (List<ReplyStaticVO>) pagination.getData();
            Long pageCount = pagination.getPageIndex()*pagination.getPageSize();
            for (int i = 0;i<vos.size();i++) {
                ReplyStaticVO vo = vos.get(i);
                if(memberMap != null && memberMap.containsKey(vo.getmId())){
                    BeanUtils.copyProperties(memberMap.get(vo.getmId()),vo);
                }
                int j = i+1;
                String floor = (pageCount+j)+"楼";
                if(pagination.getPageIndex() == 0 && fsize > j){
                    floor = floors.get(j);
                }
                vo.setFloor(floor);
                //设置附件
                vo.setFiles(getBbsFiles(post.getSiteId(),vo.getReplyId()));;
            }
        }

        objects.put("member",member);
        objects.put("post",pv);
        objects.put("replyPages",pagination);
        return objects;
    }



    private List<BbsFileStaticVO> getBbsFiles(Long siteId, Long caseId) {
        String hql = "select b.id as id,b.plateId as plateId,b.postId as postId,b.fileName as fileName,b.suffix as suffix" +
                ",b.fileSizeKb as fileSizeKb,b.mongoId as mongoId from BbsFileEO b" +
                " where b.recordStatus='Normal' and b.siteId = ? and b.caseId = ? and b.auditStatus = 1 and b.status = 1 order by b.id asc";
        return (List<BbsFileStaticVO>)bbsFileDao.getBeansByHql(hql,new Object[]{siteId,caseId},BbsFileStaticVO.class);
    }


    /**
     * 设置支持、返回记录
     * @param pv
     * @return
     */
    private PostStaticVO setLogs(PostStaticVO pv, BbsMemberVO member) {
        //获取所有浏览记录
        List<BbsLogEO> logs = BbsLogUtil.getLogs(pv.getPostId());
        Integer supportCount = 0;
        Integer againstCount = 0;
        Integer isVote = 0;
        if(logs != null && logs.size() > 0){
            for(BbsLogEO log:logs){
                if(log.getOperation().equals(BbsLogEO.Operation.View.toString()) && log.getStatus() != null){
                    if(log.getStatus().equals(1)){
                        supportCount ++;
                    }else if(log.getStatus().equals(0)){
                        againstCount ++;
                    }
                    //如果已经投票了
                    if(member != null && member.getId().equals(log.getMemberId())){
                        isVote =1;
                    }
                }
            }
        }
        pv.setSupportCount(supportCount);
        pv.setAgainstCount(againstCount);
        pv.setIsVote(isVote);
        return pv;
    }

    /**
     * 查询帖子信息
     * @param siteId
     * @param memberIds
     * @return
     */
    private Object getMembers(Long siteId,Map<Long, Long> memberIds) {
        //会员详细信息
//        Map<Long, MemberInfoVO> memberMap = getMemberInfos(siteId,memberIds);
//        //获取会员帖子总数
//        Map<Long, Long> postsMap = getMemberPostCount(siteId,memberIds);
//        Map<Long, MemberInfoVO> vos = new HashMap<Long, MemberInfoVO>();
//        if(memberMap != null){
//            for (Map.Entry<Long, Long> entry : memberIds.entrySet()) {
//                Long memberId = entry.getKey();
//                if(memberMap.containsKey(memberId)){
//                    MemberInfoVO rs = memberMap.get(memberId);
//                    if(postsMap != null && postsMap.containsKey(memberId)){
//                        rs.setmPosts(postsMap.get(memberId));
//                    }
//                    vos.put(memberId,rs);
//                }
//            }
//        }
        return getMemberInfos(siteId,memberIds);
    }


    /**
     * 会员  会员角色信息
     * @param siteId
     * @param memberIds
     * @return
     */
    private Map<Long,MemberInfoVO> getMemberInfos(Long siteId, Map<Long, Long> memberIds) {
        Map<Long,MemberInfoVO> map = null;
        List<Object> values = new ArrayList<Object>();
        StringBuffer hql = new StringBuffer("select b.id as mId ,b.name as mName ,b.img as mImg,b.memberPoints as mPoints,b.createDate as mCTime,b.memberType as memberType,b.memberRoleId as memberRoleId,b.postCount as mPosts,b.replyCount as mReplys " +
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
            map = new HashMap<Long, MemberInfoVO>();
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            for (MemberInfoVO vo : objects) {
                vo.setmPoints(vo.getmPoints() == null?0:vo.getmPoints());
                vo.setMemberType(vo.getMemberType() == null?0:vo.getMemberType());
                vo.setmRName("无分组");
                vo.setmRStar(0);
                if(vo.getMemberRoleId() != null){
                    BbsMemberRoleEO mrole =   MemberRoleUtil.getMemberRole(vo.getMemberRoleId());
                    if(mrole != null){
                        vo.setmRName(mrole.getName());
                        vo.setmRStar(mrole.getStarNumber());
                    }
                }
                map.put(vo.getmId(),vo);
            }
        }
        return map;
    }


    /**
     * 按版块统计帖子数
     * @param siteId
     * @return
     */
    private Map<Long, Long> getMemberPostCount(Long siteId, Map<Long, Long> memberIds) {
        Map<Long,Long> map = null;
        List<Object> values = new ArrayList<Object>();
        StringBuffer hql = new StringBuffer("select b.memberId as memberId,count(*) as total " +
                "from BbsPostEO b where b.recordStatus = 'Normal' and b.isPublish = 1 " +
                "and b.siteId = ?");
        values.add(siteId);
        getHql(hql,values,memberIds,"memberId");
        hql.append("group by b.memberId");
        List<Object> objects = (List<Object>)bbsPostDao.getObjects(hql.toString(),values.toArray());
        if(objects != null && objects.size() > 0){
            map = new HashMap<Long, Long>();
            for(Object obj:objects){
                Object[] objs = (Object[])obj;
                Long memberId = objs[0] == null ? 0L : Long.valueOf(objs[0].toString());
                Long total = objs[1] == null ? 0L : Long.valueOf(objs[1].toString());
                map.put(memberId,total);
            }
        }
        return map;
    }

    private void getHql(StringBuffer hql,List<Object> values,Map<Long, Long> memberIds,String alias) {
        if(memberIds != null) {
            hql.append(" and (1=0");
            for (Map.Entry<Long, Long> entry : memberIds.entrySet()) {
                hql.append(" or b."+alias+" = ?");
                values.add(entry.getValue());
            }
            hql.append(")");
        }
    }

    /**
     * 权限处理
     * @param plate   版块
     * @param sett    设置
     * @param member  会员
     * @return
     */
    private static PermissionVO getPermissionVO(BbsPlateEO plate, BbsPostEO post, BbsSettingEO sett, BbsMemberVO member) {
        PermissionVO permission = new PermissionVO();
        //版块是否可以发帖、回帖
        if(plate != null){
            permission.setIsPost(plate.getCanThread() == null?0:plate.getCanThread());
            permission.setIsReply(plate.getCanPost() == null?0:plate.getCanPost());
            //允许上传附件
            if(plate.getCanUpload() == 1){
                permission.setAddFile(1);
            }
        }
        //如果锁定  不允许发帖
        if(post != null && permission.getIsReply() == 1){
            permission.setIsReply(post.getIsLock() == 1?0:1);
        }
        Integer isPost = 0,isReply = 0,download = 0;
        if(member != null){
            if(member.getMemberType() ==1){//部门会员
                isPost = 1;isReply = 1; download = 1;
            }else if (member.getMemberType() == 2) { //判断游客类型
                isPost = sett.getVisitorCanThread() == null ? 0 : sett.getVisitorCanThread();
                isReply = sett.getVisitorCanPost() == null ? 0 : sett.getVisitorCanPost();
                download = sett.getVisitorCanDownload() == null ? 0 : sett.getVisitorCanDownload();
                permission.setAddFile(0);
            }else if(member.getMemberType() == 0 && member.getMemberRoleId() != null){//会员
                BbsMemberRoleEO mrole = MemberRoleUtil.getMemberRole(member.getMemberRoleId());
                if(mrole != null){
                    isPost =  mrole.getCanThread() == null ? 0:mrole.getCanThread();
                    isReply = mrole.getCanPost() == null ? 0:mrole.getCanPost();
                    download = mrole.getCanDownload() == null ? 0:mrole.getCanDownload();
                    if(permission.getAddFile() == 1){
                        permission.setAddFile(mrole.getCanUpload() == null?0:mrole.getCanUpload());
                    }
                }
            }
        }
        //如果版块允许发帖 判断会员
        if(permission.getIsPost() == 1) {
            permission.setIsPost(isPost);
        }
        //如果版块允许回复  判断会员 判断是否可以快速发帖
        if(permission.getIsReply() == 1) {
            permission.setIsReply(isReply);
            if(isReply == 1){
                permission.setFastReply(sett.getFastPostOn() == null?0:sett.getFastPostOn());
            }
        }
        //是否允许下载
        permission.setDownload(download);
        return permission;
    }

}
