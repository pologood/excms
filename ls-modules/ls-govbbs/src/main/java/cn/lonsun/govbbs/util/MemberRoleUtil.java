package cn.lonsun.govbbs.util;

import cn.lonsun.cache.client.CacheHandler;
import cn.lonsun.core.base.util.StringUtils;
import cn.lonsun.core.util.SpringContextHolder;
import cn.lonsun.govbbs.internal.entity.BbsMemberRoleEO;
import cn.lonsun.govbbs.internal.entity.BbsSettingEO;
import cn.lonsun.govbbs.internal.service.IBbsMemberRoleService;
import cn.lonsun.govbbs.internal.service.IBbsPostService;
import cn.lonsun.govbbs.internal.service.IBbsReplyService;
import cn.lonsun.system.member.internal.entity.MemberEO;
import cn.lonsun.system.member.internal.service.IMemberService;

import java.util.List;

/**
 * Created by zhangchao on 2017/1/7.
 */
public class MemberRoleUtil {

    private static IBbsMemberRoleService bbsMemberRoleService= SpringContextHolder.getBean("bbsMemberRoleService");

    private static IMemberService memberService= SpringContextHolder.getBean("memberService");

    private static IBbsReplyService bbsReplyService= SpringContextHolder.getBean("bbsReplyService");

    private static IBbsPostService bbsPostService= SpringContextHolder.getBean("bbsPostService");


    public static BbsMemberRoleEO getMemberRole(Long id){
        if(id == null){
            return null;
        }
        return CacheHandler.getEntity(BbsMemberRoleEO.class,id);
    }

    public static BbsMemberRoleEO getMemberRole(Long siteId, Integer points){
        if(siteId == null || points == null){
            return null;
        }
        return bbsMemberRoleService.getMemberRoleByPoints(points,siteId);
    }

    public static void updateMemberPRCount(Long siteId, List<Long> memberIds, String oper){
        if(siteId != null || memberIds != null && memberIds.size() > 0 && !StringUtils.isEmpty(oper)){
            if(siteId != null || memberIds != null && memberIds.size() > 0 && !StringUtils.isEmpty(oper)){
                for(Long memberId:memberIds){
                    MemberEO m = memberService.getEntity(MemberEO.class,memberId);
                    //計算書
                    if(oper.equals("post")){
                        Long pc = m.getPostCount()==null?0:m.getPostCount();
                        m.setPostCount(pc + 1);
                    }else if(oper.equals("reply")){
                        Long rc = m.getReplyCount()==null?0:m.getReplyCount();
                        m.setReplyCount(rc + 1);
                    }
                    memberService.updateEntity(m);
                }
            }
        }
    }
    public static void updateMemberPoints(Long siteId, List<Long> memberIds, String oper){
        if(siteId != null || memberIds != null && memberIds.size() > 0 && !StringUtils.isEmpty(oper)){
            for(Long memberId:memberIds){
                try{
                    MemberEO m = memberService.getEntity(MemberEO.class,memberId);
                    if(m != null){
                        BbsSettingEO eo = BbsSettingUtil.getSiteBbsSetting(siteId);
                        Long points = m.getMemberPoints() == null?0:m.getMemberPoints();
                        if(eo != null){
                            Integer setPoints = 0;
                            if(oper.equals("post")){//发帖积分
                                setPoints = eo.getPostedNum()== null?0:eo.getPostedNum();
                                points +=setPoints;
                            }else if(oper.equals("reply")){//回帖积分
                                setPoints = eo.getReplyNum() == null?0:eo.getReplyNum();
                                points +=setPoints;
                            }else if(oper.equals("delPost")){//删除帖子积分
                                setPoints = eo.getDelNum() == null?0:eo.getDelNum();
                                points = points - setPoints;
                                points = points < 0 ? 0:points;
                            }else if(oper.equals("delReply")){//删除回复积分
                                setPoints = eo.getDelReply() == null?0:eo.getDelReply();
                                points = points - setPoints;
                                points = points < 0 ? 0:points;
                            }else if(oper.equals("opposePost")){ //屏蔽帖子
                                setPoints = eo.getScreenTitle() == null?0:eo.getScreenTitle();
                                points = points - setPoints;
                                points = points < 0 ? 0:points;
                            }else if(oper.equals("opposeReply")){//屏蔽回复
                                setPoints = eo.getScreenReply() == null?0:eo.getScreenReply();
                                points = points - setPoints;
                                points = points < 0 ? 0:points;
                            }
                        }
                        //計算書
                        if(oper.equals("delPost") || oper.equals("opposePost")){
                            Long pc = m.getPostCount() == null?0:m.getPostCount();
                            if(pc > 0){
                                m.setPostCount(pc - 1);
                            }else{
                                m.setPostCount(0L);
                            }
                        }else if(oper.equals("delReply") || oper.equals("opposeReply")) {
                            Long rc = m.getReplyCount()== null?0:m.getReplyCount();
                            if(rc > 0){
                                m.setReplyCount(rc - 1);
                            }else{
                                m.setReplyCount(0L);
                            }
                        }
                        //积分判断角色
                        BbsMemberRoleEO mr = MemberRoleUtil.getMemberRole(siteId,points.intValue());
                        Long mrId = null;
                        if(mr != null){
                            mrId = mr.getId();
                        }
                        m.setMemberRoleId(mrId);
                        m.setMemberPoints(points);
                        memberService.updateEntity(m);
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }
    }
    public static void updateMemberPoints(Long siteId,Long memberId,Integer points,String type,Boolean isPublish){
        if( memberId != null){
            MemberEO m = memberService.getEntity(MemberEO.class,memberId);
            if(m !=  null){
                Long points_ = m.getMemberPoints() == null?0:m.getMemberPoints();
                points_ += (points == null?0:points);
                //积分判断角色
                BbsMemberRoleEO mr = MemberRoleUtil.getMemberRole(siteId,points_.intValue());
                Long mrId = null;
                if(mr != null){
                    mrId = mr.getId();
                }
                if(isPublish){
                    if("post".equals(type)){
                        //更新发帖数
                        m.setPostCount(m.getPostCount() == null?1:(m.getPostCount()+1));
                    }else if("reply".equals(type)){
                        m.setReplyCount(m.getReplyCount()== null?1:(m.getReplyCount()+1));
                    }
                }
                m.setMemberRoleId(mrId);
                m.setMemberPoints(points_);
                memberService.updateEntity(m);
            }
        }
    }
}
