package cn.lonsun.govbbs.controller;

import cn.lonsun.cache.client.CacheHandler;
import cn.lonsun.core.base.controller.BaseController;
import cn.lonsun.core.base.entity.AMockEntity;
import cn.lonsun.core.util.Pagination;
import cn.lonsun.core.util.RequestUtil;
import cn.lonsun.govbbs.internal.entity.*;
import cn.lonsun.govbbs.internal.service.IBbsFileService;
import cn.lonsun.govbbs.internal.service.IBbsPostService;
import cn.lonsun.govbbs.internal.service.IBbsReplyService;
import cn.lonsun.govbbs.internal.vo.BbsMemberVO;
import cn.lonsun.govbbs.internal.vo.PostQueryVO;
import cn.lonsun.govbbs.internal.vo.PostVO;
import cn.lonsun.govbbs.internal.vo.ReplyVO;
import cn.lonsun.govbbs.util.*;
import cn.lonsun.rbac.internal.service.IOrganService;
import cn.lonsun.system.member.internal.entity.MemberEO;
import cn.lonsun.system.member.internal.service.IMemberService;
import cn.lonsun.system.member.vo.MemberVO;
import cn.lonsun.util.FileUploadUtil;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * 政务会员    session 保存 bbsUser
 * Created by zhangchao on 2017/1/5.
 */
@Controller
@RequestMapping(value = "/govbbs/static", produces = { "application/json;charset=UTF-8" })
public class GovBbsStaticController extends BaseController {


    @Autowired
    private IMemberService memberService;


    @Autowired
    private IOrganService organService;

    @Autowired
    private IBbsPostService bbsPostService;

    @Autowired
    private IBbsReplyService bbsReplyService;

    @Autowired
    private IBbsFileService bbsFileService;

    /**
     * 登录
     *
     * @param uid
     * @param password
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "login")
    @ResponseBody
    public Object login(HttpServletRequest request, String uid, String password, String checkCode, Long siteId) throws Exception {
        String webCode = (String) request.getSession().getAttribute("webCode");
        if(StringUtils.isEmpty(checkCode) || StringUtils.isEmpty(webCode)){
            return ajaxErr("验证码不能为空");
        }
        if(!checkCode.trim().toLowerCase().equals(webCode.toLowerCase())){
            return ajaxErr("验证码不正确，请重新输入");
        }
        String msg = "";
        try {
            // 帐号为空
            if (StringUtils.isEmpty(uid)) {	return ajaxErr("请输入帐号");}
            // 密码为空
            if (StringUtils.isEmpty(password)) {return ajaxErr("请输入密码");}
            // 密码为空
            if (siteId == null) {return ajaxErr("所属站点不能为空");}

            //验证账号是否被禁用
            Map<String, Object> params = new HashMap<String, Object>();
            params.put("uid", uid);
            params.put("recordStatus", AMockEntity.RecordStatus.Normal.toString());
            params.put("siteId", siteId);
            MemberEO member = memberService.getEntity(MemberEO.class, params);
            if(member==null){
                return ajaxErr("用户账号不存在");
            }
            if(!member.getPassword().equals(DigestUtils.md5Hex(password))){
                return ajaxErr("账号密码错误");
            }
            Integer status = member.getStatus();
            if(status == null || MemberEO.Status.Unable.getStatus().equals(status)){
                return ajaxErr("您的账号已被禁用，请联系管理员");
            }
            //设置最后登录ip 和 最后登录时间
            member.setIp(RequestUtil.getIpAddr(request));

            //计算积分 一天只有一次
            Long loginNum = 0L;
            SimpleDateFormat simple = new SimpleDateFormat("yyyy-MM-dd");
            String newTime = simple.format(new Date());
            String loginTime = member.getLastLoginDate() == null?"1":simple.format(member.getLastLoginDate());
            if(!newTime.equals(loginTime)){
                BbsSettingEO eo = BbsSettingUtil.getSiteBbsSetting(siteId);
                Long points = member.getMemberPoints();
                if(eo != null){
                    loginNum = (eo.getLoginNum() == null?0L:eo.getLoginNum());
                    points +=loginNum;
                    msg = loginNum+"";//提示语
                }
                //积分判断角色
                BbsMemberRoleEO mr = MemberRoleUtil.getMemberRole(siteId,points.intValue());
                Long mrId = null;
                if(mr != null){
                    mrId = mr.getId();
                }
                member.setMemberRoleId(mrId);
                member.setMemberPoints(points);
            }
            //登录次数 和最后时间
            Integer loginTimes = (member.getLoginTimes()==null?0:member.getLoginTimes()+1);
            member.setLoginTimes(loginTimes);
            member.setLastLoginDate(new Date());
            memberService.updateEntity(member);

            //设置session
            BbsMemberVO sm = new BbsMemberVO();
            BeanUtils.copyProperties(member, sm);
            request.getSession(true).setAttribute("bbsUser", sm);
        } catch (Exception e) {
            return ajaxErr("系统异常");
        }finally{
        }
        return getObject(msg);
    }

    /**
     * 注册
     *
     * @param request
     * @param memberVO
     * @param checkCode
     * @return
     */
    @RequestMapping("saveRegister")
    @ResponseBody
    public Object saveRegister(HttpServletRequest request, MemberVO memberVO, String checkCode) {
        if (memberVO.getSiteId() == null || StringUtils.isEmpty(memberVO.getUid()) ||StringUtils.isEmpty(memberVO.getPassword()) || StringUtils.isEmpty(memberVO.getName())
                || StringUtils.isEmpty(memberVO.getPhone()) || StringUtils.isEmpty(memberVO.getEmail())) {
            return ajaxErr("[所属站点、昵称、账号、密码、手机号、邮箱]不能为空");
        }
        String webCode = (String) request.getSession().getAttribute("webCode");
        if(StringUtils.isEmpty(checkCode) || StringUtils.isEmpty(webCode)){
            return ajaxErr("验证码不能为空");
        }
        if(!checkCode.trim().toLowerCase().equals(webCode.toLowerCase())){
            return ajaxErr("验证码不正确，请重新输入");
        }
        String msg ="";
        Boolean hasUid = memberService.isExistUid(memberVO.getUid(), memberVO.getSiteId(), null);
        if (hasUid) {
            return ajaxErr("账号已经存在，请重新输入");
        }
        Boolean hasPhone = memberService.isExistPhone(memberVO.getPhone(), memberVO.getSiteId(), null);
        if (hasPhone) {
            return ajaxErr("手机号码已经存在，请重新输入");
        }
        MemberEO member = new MemberEO();
        String password = memberVO.getPassword();
        BeanUtils.copyProperties(memberVO, member);
        member.setStatus(MemberEO.Status.Enabled.getStatus());
        member.setPlainpw(password);
        member.setPassword(DigestUtils.md5Hex(password));

        //设置积分、分组
        Integer points = 0;
        Long defaultGroupId = null;
        BbsSettingEO eo = BbsSettingUtil.getSiteBbsSetting(memberVO.getSiteId());
        if(eo != null){
            points = eo.getRegisterNum()==null?0:eo.getRegisterNum();
            defaultGroupId = eo.getDefaultGroupId();
            //如果默认角色，则积分增长
            if(defaultGroupId != null){
                BbsMemberRoleEO mr = MemberRoleUtil.getMemberRole(defaultGroupId);
                points +=(mr == null?0:mr.getRiches());
            }
            msg = points+"";//提示
        }
        //积分判断角色
        BbsMemberRoleEO mr = MemberRoleUtil.getMemberRole(memberVO.getSiteId(),points);
        if(mr != null){
            defaultGroupId = mr.getId();
        }
        member.setMemberPoints(points.longValue());
        member.setMemberRoleId(defaultGroupId);
        Integer loginTimes = (member.getLoginTimes()==null?0:member.getLoginTimes()+1);
        member.setLoginTimes(loginTimes);
        member.setLastLoginDate(new Date());
        memberService.saveEntity(member);
        //设置session
        //设置session
        BbsMemberVO sm = new BbsMemberVO();
        BeanUtils.copyProperties(member, sm);
        request.getSession(true).setAttribute("bbsUser", sm);
        return getObject(msg);
    }


    /**
     * 保存会员
     * @param request
     * @param checkCode
     * @return
     */
    @RequestMapping("saveMember")
    @ResponseBody
    public Object saveMember(HttpServletRequest request,String name,String img,String idCard,Integer sex,String phone,String email,String address,String checkCode) {
        if(StringUtils.isEmpty(checkCode)){return ajaxErr("验证码不能为空！");}
        Object obj = request.getSession().getAttribute("webCode");
        if(obj == null){return ajaxErr("验证码已经失效！");}
        String webCode = (String)obj;
        if(!checkCode.trim().toLowerCase().equals(webCode.toLowerCase())){return ajaxErr("验证码不正确，请重新输入");}
        BbsMemberVO sessionMember  = (BbsMemberVO) request.getSession().getAttribute("bbsUser");
        if(sessionMember == null){return ajaxErr("您尚未登录!");}
        if(sessionMember !=null){
            MemberEO member = memberService.getEntity(MemberEO.class, sessionMember.getId());
            if(member !=null){
                String srcImg = member.getImg()==null?"":member.getImg();
                member.setName(name);
                member.setAddress(address);
                member.setEmail(email);
                member.setSex(sex);
                member.setImg(img);
                member.setIdCard(idCard);
                member.setPhone(phone);
                memberService.updateEntity(member);
                if(!StringUtils.isEmpty(img) && !srcImg.equals(img)){
                    FileUploadUtil.saveFileCenterEO(member.getImg());
                }
            }
            BeanUtils.copyProperties(member, sessionMember);
            request.getSession().setAttribute("bbsUser", sessionMember);
        }
        return getObject();
    }
    @RequestMapping("updateImg")
    @ResponseBody
    public Object updateImg(HttpServletRequest request,Long memberId,String img){
        BbsMemberVO sessionMember  = (BbsMemberVO) request.getSession().getAttribute("bbsUser");
        if(sessionMember == null){return ajaxErr("您尚未登录!");}
        if(memberId != null){
            MemberEO member = memberService.getEntity(MemberEO.class,memberId);
            if(member != null){
                member.setImg(img);
                memberService.updateEntity(member);
            }
            BeanUtils.copyProperties(member, sessionMember);
            request.getSession().setAttribute("bbsUser", sessionMember);
        }
        return getObject();
    }

    /**
     * 保存安全设置
     * @param request
     * @param password
     * @param question
     * @param answer
     * @param checkCode
     * @return
     */
    @RequestMapping("saveSafe")
    @ResponseBody
    public Object saveSafe(HttpServletRequest request,String password,String question,String answer,String checkCode ) {
        Object obj = request.getSession().getAttribute("webCode");
        if(obj == null){
            return ajaxErr("验证码已经失效！");
        }
        String webCode = (String)obj;
        if(!checkCode.trim().toLowerCase().equals(webCode.toLowerCase())){
            return ajaxErr("验证码不正确，请重新输入");
        }
        BbsMemberVO sessionMember  = (BbsMemberVO) request.getSession().getAttribute("bbsUser");
        if(sessionMember == null){
            return ajaxErr("您尚未登录!");
        }
        MemberEO member = memberService.getEntity(MemberEO.class, sessionMember.getId());
        if(member != null){
            if(!member.getPassword().equals(DigestUtils.md5Hex(password))){
                return ajaxErr("初始密码不正确!");
            }
            member.setQuestion(question);
            member.setAnswer(answer);
            memberService.updateEntity(member);
        }else{
            return ajaxErr("会员不存在!");
        }
        return getObject();
    }

    /**
     * 修改密码
     * @param request
     * @param oldPw
     * @param newPw
     * @param checkCode
     * @return
     */
    @RequestMapping("updatePw")
    @ResponseBody
    public Object updatePw(HttpServletRequest request,String oldPw,String newPw,String checkCode) {
        if(StringUtils.isEmpty(checkCode)){
            return ajaxErr("验证码不能为空！");
        }
        if(StringUtils.isEmpty(oldPw) || StringUtils.isEmpty(newPw)){
            return ajaxErr("初始密码或新密码不能为空！");
        }
        Object obj = request.getSession().getAttribute("webCode");
        if(obj == null){
            return ajaxErr("验证码已经失效！");
        }
        String webCode = (String)obj;
        if(!checkCode.trim().toLowerCase().equals(webCode.toLowerCase())){
            return ajaxErr("验证码不正确，请重新输入");
        }
        BbsMemberVO sessionMember  = (BbsMemberVO) request.getSession().getAttribute("bbsUser");
        if(sessionMember == null){
            return ajaxErr("您尚未登录!");
        }
        MemberEO member = memberService.getEntity(MemberEO.class, sessionMember.getId());
        if(member != null){
            if(!member.getPassword().equals(DigestUtils.md5Hex(oldPw))){
                return ajaxErr("初始密码不正确!");
            }
            member.setPlatCode(null);
            member.setRandKey(null);
            member.setPassword(DigestUtils.md5Hex(newPw));
            member.setPlainpw(newPw);
            memberService.updateEntity(member);
        }
        return getObject();
    }

    /**
     * 退出
     *
     * @param session
     * @return
     */
    @RequestMapping("logout")
    @ResponseBody
    public Object logout(HttpSession session,Long siteId){
        if(siteId == null){
            return ajaxErr("站点id不能为空");
        }
        //设置游客
        BbsSettingEO eo = BbsSettingUtil.getSiteBbsSetting(siteId);
        BbsMemberVO vo = new BbsMemberVO();
        Long touristId = CounterUtil.getTouristId();
        vo.setId(touristId);
        vo.setMemberType(BbsMemberVO.MemberType.TOURIST.getMemberType());//游客
        vo.setName(eo == null?"游客:":eo.getVisitorPrefix()+ touristId);
        session.setAttribute("bbsUser",vo);
        return getObject();
    }


    /**
     * 获取站点下所有单位
     * @return
     */
    @RequestMapping("getSiteOrgans")
    @ResponseBody
    public Object getSiteOrgans(Long siteId){
        if(siteId == null){
            return getObject();
        }
        return getObject(organService.getOrgansBySiteId(siteId,true));
    }


    /**
     * 保存浏览记录
     * @param session
     * @param postId
     * @return
     */
    @RequestMapping("saveViewLog")
    @ResponseBody
    public Object saveViewLog(HttpSession session,Long postId){
        if(postId != null){
            BbsLogUtil.saveStaticLog(postId);
        }
        return getObject();
    }

    /**
     * 保存浏览记录
     * @param session
     * @param postId
     * @return
     */
    @RequestMapping("saveSupportLog")
    @ResponseBody
    public Object saveSupportLog(HttpSession session,Long postId,Integer status){
        if(postId != null){
            BbsLogUtil.saveSupportLog(postId,status);
        }
        return getObject();
    }

    /**
     * 发帖
     * @param session
     * @param postId
     * @return
     */
    @RequestMapping("savePost")
    @ResponseBody
    public Object savePost(HttpServletRequest request,Long plateId,Long unitId,String unitName,String title,String content,Long[] fileIds,String checkCode){
        if(StringUtils.isEmpty(checkCode)){
            return ajaxErr("验证码不能为空！");
        }
        Object obj = request.getSession().getAttribute("webCode");
        if(obj == null){
            return ajaxErr("验证码已经失效！");
        }
        String webCode = (String)obj;
        if(!checkCode.trim().toLowerCase().equals(webCode.toLowerCase())){
            return ajaxErr("验证码不正确，请重新输入");
        }
        BbsMemberVO member = (BbsMemberVO) request.getSession().getAttribute("bbsUser");
        if(member == null){
            return  ajaxErr("请先登录后，才可以发帖");
        }
        String msg = "";
        try{
            BbsPlateEO plate =  CacheHandler.getEntity(BbsPlateEO.class,plateId);
            if(plate == null){
                return  ajaxErr("版块不存在，请刷新后重新发帖");
            }
            Integer canThread = plate.getCanThread() == null?0:plate.getCanThread();
            if(canThread != 1){
                return  ajaxErr("此版块禁止发帖");
            }
            BbsSettingEO bbsSetting = BbsSettingUtil.getSiteBbsSetting(plate.getSiteId());
            if(bbsSetting == null) {
                return  ajaxErr("站点论坛配置未设置，不允许发帖");
            }
            //发帖积分
            Integer postedNum = bbsSetting.getPostedNum()== null?0:bbsSetting.getPostedNum();
            //如果是游客 则判断是否允许发帖
            Boolean isPublish = false;
            Boolean isTourist = false;
            if(member.getMemberType() == 2){
                Integer visitorCanThread = bbsSetting.getVisitorCanThread() == null?0:bbsSetting.getVisitorCanThread();
                if(visitorCanThread == 0){
                    return  ajaxErr("游客不可以发帖，请您先登录.");
                }
                isTourist = true;
            }else if(member.getMemberType() ==1){//部门会员
                isPublish = true;
            }else if(member.getMemberType() == 0){//会员
                //判断会员所属组,设置帖子是否需要审核、发帖
                if(member.getMemberRoleId() != null){
                    BbsMemberRoleEO mrole = MemberRoleUtil.getMemberRole(member.getMemberRoleId());
                    if(mrole == null){
                        return  ajaxErr("您没有发帖的权限");
                    }
                    Integer mcanThread= mrole.getCanThread() == null ? 0:mrole.getCanThread();//是否允许发帖
                    Integer needConfirm= mrole.getNeedConfirm() == null ? 0:mrole.getNeedConfirm();//权限
                    if(mcanThread != 1){
                        return  ajaxErr("您没有发帖的权限");
                    }
                    if(needConfirm == 3 || needConfirm == 2){
                        isPublish = true;
                    }

                }
            }
            Date dayTime = new Date();
            Long siteId = plate.getSiteId();
            BbsPostEO post = new BbsPostEO();
            post.setTitle(title);
            post.setSiteId(siteId);
            post.setContent(content);
            post.setPlateId(plateId);
            post.setPlateName(plate.getName());
            post.setParentIds(plate.getParentIds());
            if(unitId != null){
                post.setIsAccept(BbsPostEO.IsAccept.ToReply.getIsAccept());
                post.setAcceptUnitId(unitId);
                post.setAcceptUnitName(unitName);
                post.setAcceptTime(dayTime);
                post.setCreateDate(dayTime);
                //判断红黄牌
                if(bbsSetting != null){
                    Integer replyDay = bbsSetting.getReplyDay();
                    Integer yellowDay = bbsSetting.getYellowDay();
                    Integer redDay = bbsSetting.getRedDay();
                    //逾期时间
                    Long times = dayTime.getTime()/1000;
                    if(replyDay != null && replyDay != null){
                        post.setOverdueTimes(times+replyDay*24*60*60);
                    }
                    //黄牌日期
                    if(yellowDay != null && yellowDay != null){
                        post.setYellowTimes(times+yellowDay*24*60*60);
                    }
                    //红牌日期
                    if(redDay != null && redDay != null){
                        post.setRedTimes(times+redDay*24*60*60);
                    }
                }
            }
            //系统会员
            post.setAddType(isTourist? BbsPostEO.AddType.TOURIST.getAddType(): BbsPostEO.AddType.Member.getAddType());
            if(isPublish){
                msg = "发帖成功，积分增加："+postedNum;
                post.setIsPublish(1);
                post.setPublishDate(new Date());
                //审核状态
                post.setAuditUserId(member.getId());
                post.setAuditUserName(member.getName());
                post.setAuditTime(new Date());
            }else{
                msg = "发帖成功，等待管理员审核.积分增加："+postedNum;
            }
            post.setMemberId(member.getId());
            post.setMemberName(member.getName());
            post.setMemberPhone(member.getPhone());
            post.setMemberEmail(member.getEmail());
            post.setMemberAddress(member.getAddress());
            post.setIp(RequestUtil.getIpAddr(request));
            //是否有附件
//            post.setHasFile(bbsFileService.getFileSuffix(fileIds));
            bbsPostService.saveEntity(post);
            //设置附件状态
            if(fileIds != null && fileIds.length > 0){
                bbsFileService.setFilesStatus(fileIds,post.getIsPublish() == 1?1:0,post.getPostId(),post.getPostId(),post.getPlateId());
                //更新是否有附件
                BbsFilesUtil.updatePostFilesSuffix(post.getPostId());
            }
            //计算积分
            MemberRoleUtil.updateMemberPoints(siteId,member.getId(),postedNum,"post",isPublish);
        }catch (Exception e){
            e.printStackTrace();
            return  ajaxErr("发帖失败，请重试.");
        }
        return getObject(msg);
    }


    /**
     * 回复
     * @param session
     * @param postId
     * @return
     */
    @RequestMapping("saveReply")
    @ResponseBody
    public Object saveReply(HttpServletRequest request,Long postId,Integer isHandle,String content,Long[] fileIds,String checkCode){
        if(StringUtils.isEmpty(checkCode)){
            return ajaxErr("验证码不能为空！");
        }
        Object obj = request.getSession().getAttribute("webCode");
        if(obj == null){
            return ajaxErr("验证码已经失效！");
        }
        String webCode = (String)obj;
        if(!checkCode.trim().toLowerCase().equals(webCode.toLowerCase())){
            return ajaxErr("验证码不正确，请重新输入");
        }
        BbsMemberVO member = (BbsMemberVO) request.getSession().getAttribute("bbsUser");
        if(member == null){
            return  ajaxErr("请先登录后，才可以发帖.");
        }
        String msg = "";
        try{
            BbsPostEO post =bbsPostService.getEntity(BbsPostEO.class,postId);
            if(post == null){
                return  ajaxErr("帖子不存在，请刷新后重试.");
            }
            if(post.getIsLock() == 1){
                return  ajaxErr("帖子已被锁定，不允许回复.");
            }
            BbsPlateEO plate =  CacheHandler.getEntity(BbsPlateEO.class,post.getPlateId());
            if(plate == null){
                return  ajaxErr("版块不存在，请刷新后重新发帖");
            }
            Integer canPost = plate.getCanPost() == null?0:plate.getCanPost();
            if(canPost != 1){
                return  ajaxErr("此版块禁止回帖");
            }
            BbsSettingEO bbsSetting = BbsSettingUtil.getSiteBbsSetting(post.getSiteId());
            if(bbsSetting == null) {
                return  ajaxErr("站点论坛配置未设置，不允许发帖.");
            }
            //回帖积分
            Integer replyNum = bbsSetting.getReplyNum() == null?0:bbsSetting.getReplyNum();
            Boolean isPublish = false;
            Boolean isTourist = false;
            isHandle = 0;
            if(member.getMemberType() ==1){//如果是部门会员
                isPublish = true;
                Long unitId = member.getUnitId();
                //如果是部门会员、帖子是待办 且待办帖子单位 和 会员部门id 一样时
                if(unitId != null && post.getIsAccept() != null && post.getIsAccept() == 0 &&
                        post.getAcceptUnitId() != null &&post.getAcceptUnitId().equals(unitId)){
                    isHandle = 1;
                }
            }else if(member.getMemberType() == 2){//如果是游客
                Integer  visitorCanPost =  bbsSetting.getVisitorCanPost() == null?0:bbsSetting.getVisitorCanPost();
                if(visitorCanPost == 0){
                    return  ajaxErr("游客不可以回帖，请您先登录.");
                }
                isTourist = true;
            }else if(member.getMemberType() == 0){//会员
                //判断会员所属组,设置帖子是否需要审核、发帖
                if(member.getMemberRoleId() != null){
                    BbsMemberRoleEO mrole = MemberRoleUtil.getMemberRole(member.getMemberRoleId());
                    if(mrole == null) {
                        return  ajaxErr("您没有回帖的权限.");
                    }
                    Integer mcanPost= mrole.getCanPost() == null ? 0:mrole.getCanPost();//是否允许发帖
                    Integer needConfirm= mrole.getNeedConfirm() == null ? 0:mrole.getNeedConfirm();//权限
                    if(mcanPost != 1){
                        return  ajaxErr("您没有回帖的权限.");
                    }
                    if(needConfirm == 3 || needConfirm == 1){
                        isPublish = true;
                    }
                }
            }
            Long siteId = post.getSiteId();
            BbsReplyEO reply = new BbsReplyEO();
            reply.setPostTile("回复:"+post.getTitle());
            reply.setPostId(postId);
            reply.setSiteId(siteId);
            reply.setPlateId(post.getPlateId());
            reply.setPlateName(post.getPlateName());
            reply.setParentIds(post.getParentIds());
            reply.setContent(content);
            reply.setMemberId(member.getId());
            reply.setMemberName(member.getName());
            reply.setMemberPhone(member.getPhone());
            reply.setMemberEmail(member.getEmail());
            reply.setMemberAddress(member.getAddress());
            reply.setIp(RequestUtil.getIpAddr(request));
            //判断会员回复是不是办理信息
            if(isHandle != null && isHandle == 1){
                reply.setIsHandle(1);
                reply.setHandleUserId(member.getId());
                reply.setHandleUnitId(member.getUnitId());
                reply.setHandleUnitName(member.getUnitName());
                reply.setHandleTime(new Date());
            }
            reply.setAddType(isTourist? BbsReplyEO.AddType.TOURIST.getAddType(): BbsReplyEO.AddType.Member.getAddType());
            if(isPublish){
                msg = "回帖成功，积分增加："+replyNum;
                reply.setIsPublish(1);
                reply.setPublishDate(new Date());
                //审核状态
                reply.setAuditUserId(member.getId());
                reply.setAuditUserName(member.getName());
                reply.setAuditTime(new Date());
            }else{
                msg = "回帖成功，等待管理员审核.积分增加："+replyNum;
            }
            bbsReplyService.saveWebEntity(reply,post,member);
            //设置附件状态
            if(fileIds != null && fileIds.length > 0){
                bbsFileService.setFilesStatus(fileIds,reply.getIsPublish() == 1?1:0,reply.getReplyId(),reply.getPostId(),reply.getPlateId());
            }
            //计算积分
            MemberRoleUtil.updateMemberPoints(siteId,member.getId(),replyNum,"reply",isPublish);
        }catch (Exception e){
            e.printStackTrace();
            return  ajaxErr("回复失败，请重试.");
        }
        return getObject(msg);
    }


    /**
     * 获取 我待办的帖子  我的帖子 、 我参与的帖子
     * type   handelpost   mypost  replypost
     * @param type
     * @return
     */
    @RequestMapping("getPosts")
    @ResponseBody
    public Object getPosts(HttpServletRequest request, PostQueryVO query){
        BbsMemberVO member = (BbsMemberVO) request.getSession().getAttribute("bbsUser");
        if(member != null && !StringUtils.isEmpty(query.getType())){
            if (query.getPageIndex() == null || query.getPageIndex()< 0){
                query.setPageIndex(0L);
            }
            if (query.getPageSize() == null ||query.getPageSize()< 0 ||query.getPageSize()> Pagination.MAX_SIZE){
                query.setPageSize(10);
            }
            Boolean queryPost = false,queryReply = false;
            //待办帖子
            if(query.getType().equals("handelpost")){
                if(member.getMemberType() == 1){
                    query.setAcceptUnitId(member.getUnitId());
                    queryPost = true;
                }
            }
            else if(query.getType().equals("mypost")){
                query.setMemberId(member.getId());
                queryPost = true;
            }
            else if(query.getType().equals("myreply")){
                query.setMemberId(member.getId());
                queryReply = true;
            }
            //时间格式化
            query.setDateFormat(StringUtils.isEmpty(query.getDateFormat())?"yyyy-MM-dd HH:mm:ss":query.getDateFormat());
            SimpleDateFormat simple = new SimpleDateFormat(query.getDateFormat());
            if(queryPost){
                Pagination pagination = bbsPostService.getPage(query);
                if(pagination.getData() != null && pagination.getData().size() > 0 ){
                    Long times = new Date().getTime()/1000;
                    for(PostVO post:(List<PostVO>)pagination.getData()){
                        //红黄牌
                        if(post.getIsAccept() != null){
                            if(post.getYellowTimes() != null && post.getYellowTimes() <= times && post.getRedTimes() > times){
                                post.setYellowCard(1);
                            }
                            if(post.getRedTimes() != null && post.getRedTimes() <= times){
                                post.setRedCard(1);
                            }
                        }
                        if(post.getCreateDate() != null){
                            post.setTime(simple.format(post.getCreateDate()));//临时保存时间
                        }
                    }
                }
                return getObject(pagination);
            }else if(queryReply){
                Pagination pagination = bbsReplyService.getPage(query);
                if(pagination.getData() != null && pagination.getData().size() > 0 ){
                    for(ReplyVO reply:(List<ReplyVO>)pagination.getData()){
                        if(reply.getCreateDate() != null){
                            reply.setTime(simple.format(reply.getCreateDate()));//临时保存时间
                        }
                    }
                }
                return getObject(pagination);
            }
        }
        return getObject();
    }

    @RequestMapping("deletePost")
    @ResponseBody
    public Object deletePost(Long[] postIds,Integer isDel) {
        bbsPostService.delete(postIds,isDel);
        return getObject();
    }

    @RequestMapping("delete")
    @ResponseBody
    public Object delete(Long[] replyIds) {
        bbsReplyService.delete(replyIds);
        return getObject();
    }

    @RequestMapping("updateposts")
    @ResponseBody
    public Object updateposts() {
        Map<String,Object>  map = new HashMap<String, Object>();
        List<BbsPostEO> posts = bbsPostService.getEntities(BbsPostEO.class,map);
        if(posts != null && posts.size()>0){
            for(BbsPostEO p:posts){
                if(p.getRecordStatus().equals("Normal") && p.getIsPublish() == 1 &&  p.getMemberId() != null){
                    MemberEO m = memberService.getEntity(MemberEO.class,p.getMemberId());
                    if(m != null){
                        //更新发帖数
                        m.setPostCount(m.getPostCount() == null?1:(m.getPostCount()+1));
//                        m.setReplyCount(m.getReplyCount()== null?1:(m.getReplyCount()+1));
                        memberService.updateEntity(m);
                    }

                }
            }
        }
        List<BbsReplyEO> replys = bbsReplyService.getEntities(BbsReplyEO.class,map);
        if(replys != null && replys.size()>0){
            for(BbsReplyEO r:replys){
                if(r.getRecordStatus().equals("Normal") && r.getIsPublish() == 1 && r.getMemberId() != null){
                    MemberEO m = memberService.getEntity(MemberEO.class,r.getMemberId());
                    if(m != null){
                        //更新发帖数
                        m.setReplyCount(m.getReplyCount()== null?1:(m.getReplyCount()+1));
                        memberService.updateEntity(m);
                    }

                }
            }
        }
        return getObject();
    }
}
