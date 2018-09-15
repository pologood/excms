package cn.lonsun.staticcenter.controller;

import cn.lonsun.cache.client.CacheHandler;
import cn.lonsun.common.util.AppUtil;
import cn.lonsun.content.internal.entity.BaseContentEO;
import cn.lonsun.content.internal.service.IBaseContentService;
import cn.lonsun.content.internal.service.IGuestBookService;
import cn.lonsun.content.messageBoard.controller.internal.entity.MessageBoardForwardEO;
import cn.lonsun.content.messageBoard.controller.internal.entity.MessageBoardReplyEO;
import cn.lonsun.content.messageBoard.controller.vo.*;
import cn.lonsun.content.messageBoard.service.IMessageBoardForwardService;
import cn.lonsun.content.messageBoard.service.IMessageBoardReplyService;
import cn.lonsun.content.messageBoard.service.IMessageBoardService;
import cn.lonsun.content.vo.GuestBookEditVO;
import cn.lonsun.content.vo.GuestBookPageVO;
import cn.lonsun.core.base.controller.BaseController;
import cn.lonsun.core.base.entity.AMockEntity;
import cn.lonsun.core.util.ContextHolderUtils;
import cn.lonsun.core.util.Pagination;
import cn.lonsun.core.util.RequestUtil;
import cn.lonsun.indicator.internal.entity.IndicatorEO;
import cn.lonsun.indicator.internal.service.IIndicatorService;
import cn.lonsun.mongodb.entity.ContentMongoEO;
import cn.lonsun.mongodb.service.ContentMongoServiceImpl;
import cn.lonsun.msg.submit.entity.vo.ReportVO;
import cn.lonsun.rbac.internal.entity.OrganEO;
import cn.lonsun.rbac.utils.ValidateCode;
import cn.lonsun.shiro.util.RSAUtils;
import cn.lonsun.staticcenter.generate.tag.impl.member.util.IsLoginVO;
import cn.lonsun.system.datadictionary.vo.DataDictVO;
import cn.lonsun.system.member.internal.entity.MemberEO;
import cn.lonsun.system.member.internal.service.IMemberService;
import cn.lonsun.system.member.vo.MemberSessionVO;
import cn.lonsun.system.member.vo.MemberVO;
import cn.lonsun.system.sitechart.internal.entity.MyCollectionEO;
import cn.lonsun.system.sitechart.internal.entity.SiteChartMainEO;
import cn.lonsun.system.sitechart.service.IMyCollectionService;
import cn.lonsun.system.sitechart.service.ISiteChartMainService;
import cn.lonsun.system.sitechart.vo.MyCollectionVO;
import cn.lonsun.system.sitechart.vo.SiteCharWebVO;
import cn.lonsun.system.sitechart.vo.VisitDeatilPageVo;
import cn.lonsun.util.DataDictionaryUtil;
import cn.lonsun.util.FileUploadUtil;
import org.apache.commons.codec.binary.Hex;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.security.interfaces.RSAPublicKey;
import java.text.SimpleDateFormat;
import java.util.*;

@Controller
@RequestMapping(value = "/member", produces = {"application/json;charset=UTF-8"})
public class MemberWebController extends BaseController {

    @Autowired
    private IMemberService memberService;

    @Autowired
    private ISiteChartMainService siteChartMainService;

    @Autowired
    private IMyCollectionService myCollectionService;

    @Autowired
    private IGuestBookService guestBookService;

    @Autowired
    private IMessageBoardService messageBoardService;

    @Autowired
    private IMessageBoardReplyService replyService;

    @Autowired
    private IMessageBoardForwardService forwardService;
    @Autowired
    private IBaseContentService baseContentService;
    @Autowired
    private ContentMongoServiceImpl contentMongoService;
    @Autowired
    private IIndicatorService iIndicatorService;

    @RequestMapping("getRSAPublicKey")
    @ResponseBody
    public Object getRSAPublicKey() {
        RSAPublicKey publicKey = RSAUtils.getDefaultPublicKey();
        Map map = new HashMap();
        map.put("modulus", new String(Hex.encodeHex(publicKey.getModulus().toByteArray())));
        map.put("exponent", new String(Hex.encodeHex(publicKey.getPublicExponent().toByteArray())));
        return getObject(map);
    }

    /**
     * 登录
     *
     * @param uid
     * @param password
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "checkLogin")
    @ResponseBody
    public Object checkLogin(HttpServletRequest request, String uid, String password, String checkCode, Long siteId) throws Exception {
        String webCode = (String) request.getSession().getAttribute("webCode");
        if (StringUtils.isEmpty(checkCode) || StringUtils.isEmpty(webCode)) {
            return ajaxErr("验证码不能为空！");
        }
        if (!checkCode.trim().toLowerCase().equals(webCode.toLowerCase())) {
            //验证码验证完成后从session中移除，无论验证成功还是失败都要移除
            request.getSession().removeAttribute("webCode");
            return ajaxErr("验证码不正确，请重新输入");
        }
        //验证码验证完成后从session中移除，无论验证成功还是失败都要移除
        request.getSession().removeAttribute("webCode");
        String ip = RequestUtil.getIpAddr(request);
        try {
            // 帐号为空
            if (StringUtils.isEmpty(uid)) {
                return ajaxErr("请输入帐号！");
            }
            // 密码为空
            if (StringUtils.isEmpty(password)) {
                return ajaxErr("请输入密码！");
            }
            // 密码为空
            if (siteId == null) {
                return ajaxErr("所属站点不能为空！");
            }

            HttpSession session = request.getSession(true);
            //验证账号是否被禁用
            Map<String, Object> params = new HashMap<String, Object>();
            params.put("uid", uid);
            params.put("recordStatus", AMockEntity.RecordStatus.Normal.toString());
            params.put("siteId", siteId);
            MemberEO member = memberService.getEntity(MemberEO.class, params);
            if (member == null) {
                return ajaxErr("账号不存在！");
            }
            password = RSAUtils.decryptStringByJs(password);// 解密
            if (!member.getPassword().equals(DigestUtils.md5Hex(password))) {
                return ajaxErr("密码错误！");
            }
            if (MemberEO.Status.Unable.getStatus().equals(member.getStatus())) {
                return ajaxErr("您的账号已被禁用，请联系管理员！");
            }
            MemberSessionVO sessionMember = new MemberSessionVO();
            BeanUtils.copyProperties(member, sessionMember);
            session.setAttribute("member", sessionMember);
            //设置最后登录ip 和 最后登录时间
            member.setIp(ip);
            Integer loginTimes = (member.getLoginTimes() == null ? 0 : member.getLoginTimes());
            member.setLoginTimes(loginTimes);
            member.setLastLoginDate(new Date());
            memberService.updateEntity(member);
        } catch (Exception e) {
            return ajaxErr("系统异常");
        } finally {
        }
        return getObject();
    }

    /**
     * 获取验证码
     *
     * @param request
     * @param response
     */
    @RequestMapping("getCode")
    @ResponseBody
    public void getCode(HttpServletRequest request, HttpServletResponse response) {
        // 设置响应的类型格式为图片格式
        response.setContentType("image/jpeg");
        // 禁止图像缓存。
        response.setHeader("Pragma", "no-cache");
        response.setHeader("Cache-Control", "no-cache");
        response.setDateHeader("Expires", 0);
        HttpSession session = request.getSession();
        ValidateCode vCode = new ValidateCode(120, 40, 4, 10);
        session.setAttribute("webCode", vCode.getCode());
        try {
            vCode.write(response.getOutputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 退出
     *
     * @param session
     * @return
     */
    @RequestMapping("logout")
    @ResponseBody
    public Object logout(HttpSession session) {
        session.invalidate();
        return getObject();
    }

    /**
     * 保存会员
     *
     * @param request
     * @param checkCode
     * @return
     */
    @RequestMapping("saveMember")
    @ResponseBody
    public Object saveMember(HttpServletRequest request, String name, String img, String idCard, Integer sex, String phone, String email, String address, String checkCode) {
        if (StringUtils.isEmpty(checkCode)) {
            return ajaxErr("验证码不能为空！");
        }
        Object obj = request.getSession().getAttribute("webCode");
        if (obj == null) {
            return ajaxErr("验证码已经失效！");
        }
        String webCode = (String) obj;
        if (!checkCode.trim().toLowerCase().equals(webCode.toLowerCase())) {
            return ajaxErr("验证码不正确，请重新输入");
        }
        if (StringUtils.isEmpty(idCard) || StringUtils.isEmpty(name) || StringUtils.isEmpty(phone)) {
            return ajaxErr("[真实姓名、身份证号码、手机号]不能为空!");
        }
        MemberSessionVO sessionMember = (MemberSessionVO) request.getSession().getAttribute("member");
        if (sessionMember == null) {
            return ajaxErr("您尚未登录!");
        }
        if (sessionMember != null) {
            MemberEO member = memberService.getEntity(MemberEO.class, sessionMember.getId());
            if (member != null) {
                String srcImg = member.getImg() == null ? "" : member.getImg();
                member.setName(name);
                member.setAddress(address);
                member.setEmail(email);
                member.setSex(sex);
                member.setImg(img);
                member.setIdCard(idCard);
                member.setPhone(phone);
                memberService.updateEntity(member);
                if (!StringUtils.isEmpty(img) && !srcImg.equals(img)) {
                    FileUploadUtil.saveFileCenterEO(member.getImg());
                }
            }
            BeanUtils.copyProperties(member, sessionMember);
            request.getSession().setAttribute("member", sessionMember);
        }
        return getObject();
    }

    //更新t图片
    @RequestMapping("updateImg")
    @ResponseBody
    public Object updateImg(Long memberId, String img) {
        //		member.setIp(RequestUtil.getIpAddr(request));
        if (memberId != null) {
            MemberEO member = memberService.getEntity(MemberEO.class, memberId);
            if (member != null) {
                member.setImg(img);
                memberService.updateEntity(member);
            }
        }
        return getObject();
    }

    /**
     * 修改密码
     *
     * @param request
     * @param oldPw
     * @param newPw
     * @param checkCode
     * @return
     */
    @RequestMapping("updatePw")
    @ResponseBody
    public Object updatePw(HttpServletRequest request, String oldPw, String newPw, String checkCode) {
        if (StringUtils.isEmpty(checkCode)) {
            return ajaxErr("验证码不能为空！");
        }
        if (StringUtils.isEmpty(oldPw) || StringUtils.isEmpty(newPw)) {
            return ajaxErr("初始密码或新密码不能为空！");
        }
        Object obj = request.getSession().getAttribute("webCode");
        if (obj == null) {
            return ajaxErr("验证码已经失效！");
        }
        String webCode = (String) obj;
        if (!checkCode.trim().toLowerCase().equals(webCode.toLowerCase())) {
            return ajaxErr("验证码不正确，请重新输入");
        }
        MemberSessionVO sessionMember = (MemberSessionVO) request.getSession().getAttribute("member");
        if (sessionMember == null) {
            return ajaxErr("您尚未登录!");
        }
        MemberEO member = memberService.getEntity(MemberEO.class, sessionMember.getId());
        if (member != null) {
            if (!member.getPassword().equals(DigestUtils.md5Hex(oldPw))) {
                return ajaxErr("初始密码不正确!");
            }
            member.setPassword(DigestUtils.md5Hex(newPw));
            member.setPlainpw(newPw);
            memberService.updateEntity(member);
        }
        return getObject();
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
        if (StringUtils.isEmpty(checkCode)) {
            return ajaxErr("验证码不能为空！");
        }
        Object obj = request.getSession().getAttribute("webCode");
        if (obj == null) {
            return ajaxErr("验证码已经失效！");
        }
        String webCode = (String) obj;
        if (!checkCode.trim().toLowerCase().equals(webCode.toLowerCase())) {
            return ajaxErr("验证码不正确，请重新输入");
        }
        if (memberVO.getSiteId() == null || StringUtils.isEmpty(memberVO.getUid()) || StringUtils.isEmpty(memberVO.getName())
                || StringUtils.isEmpty(memberVO.getPhone()) || StringUtils.isEmpty(memberVO.getQuestion()) || StringUtils.isEmpty(memberVO.getAnswer())) {
            return ajaxErr("[所属站点、注册账号、真实姓名、手机号、密码提示问题、密码提示答案]不能为空!");
        }
        Boolean hasUid = memberService.isExistUid(memberVO.getUid(), memberVO.getSiteId(), null);
        if (hasUid) {
            return ajaxErr("账号已经存在，请重新输入~");
        }
        MemberEO member = new MemberEO();
        String password = memberVO.getPassword();
        BeanUtils.copyProperties(memberVO, member);
        member.setStatus(MemberEO.Status.Enabled.getStatus());
        member.setPlainpw(password);
        member.setPassword(DigestUtils.md5Hex(password));
        memberService.saveEntity(member);
        //设置session
        MemberSessionVO sessionMember = new MemberSessionVO();
        BeanUtils.copyProperties(member, sessionMember);
        request.getSession().setAttribute("member", sessionMember);
        return getObject(sessionMember);
    }


    /**
     * @param request
     * @param
     * @param checkCode
     * @return
     */
    @RequestMapping("isExistUid")
    @ResponseBody
    public Object isExistUid(HttpServletRequest request, String uid, Long siteId, String checkCode) {
        if (StringUtils.isEmpty(uid) || siteId == null) {
            return ajaxErr("用户账号、所属站点不能为空");
        }
        Boolean hasUid = memberService.isExistUid(uid, siteId, null);
        if (hasUid) {
            return ajaxErr("账号已经存在，请重新输入~");
        }
        return getObject();
    }

    /**
     * 获取问题
     *
     * @param uid 账号
     * @return
     */
    @RequestMapping("getQuestion")
    @ResponseBody
    public Object getQuestion(String uid, Long siteId) {
        if (StringUtils.isEmpty(uid)) {
            return ajaxErr("用户账号不能为空");
        }
        MemberEO member = memberService.getMemberByUid(uid, siteId);
        if (null == member) {
            return ajaxErr("账号不存在,请重新输入");
        }
        MemberVO vo = new MemberVO();
        vo.setId(member.getId());
        vo.setQuestion(member.getQuestion());
        return getObject(vo);
    }


    /**
     * 获取问题
     *
     * @param uid 账号
     * @return
     */
    @RequestMapping("getUidByPhone")
    @ResponseBody
    public Object getUidByPhone(String phone, Long siteId) {
        if (StringUtils.isEmpty(phone)) {
            return ajaxErr("手机号不能为空");
        }
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("phone", phone);
        params.put("siteId", siteId);
        params.put("recordStatus", AMockEntity.RecordStatus.Normal.toString());
        List<MemberEO> members = memberService.getEntities(MemberEO.class, params);
        MemberSessionVO member = null;
        List<MemberSessionVO> vos = null;
        if (members != null && members.size() > 0) {
            vos = new ArrayList<MemberSessionVO>();
            for (MemberEO eo : members) {
                member = new MemberSessionVO();
                member.setId(eo.getId());
                member.setUid(eo.getUid());
                vos.add(member);
            }
        } else {
            return ajaxErr("手机号不存在,请重新输入");
        }
        return getObject(vos);
    }

    /**
     * 验证问题答案
     *
     * @param memberId
     * @param answer
     * @return
     */
    @RequestMapping("checkAnswer")
    @ResponseBody
    public Object checkAnswer(Long memberId, String answer) {
        if (memberId == null) {
            return ajaxErr("会员id不能为空");
        }
        if (StringUtils.isEmpty(answer)) {
            return ajaxErr("答案不能为空");
        }
        MemberEO member = memberService.getEntity(MemberEO.class, memberId);
        if (member != null) {
            if (!member.getAnswer().equals(answer.trim())) {
                return ajaxErr("密码验证答案不正确，请重新输入");
            }
        } else {
            return ajaxErr("用户不存在");
        }
        return getObject();
    }

    /**
     * 设置密码
     *
     * @param request
     * @param memberId
     * @param password
     * @param checkCode
     * @return
     */
    @RequestMapping("setPasswordCk")
    @ResponseBody
    public Object setPasswordCk(HttpServletRequest request, Long memberId, String password, String answer, String checkCode) {
        if (memberId == null) {
            return ajaxErr("会员id不能为空");
        }
        if (StringUtils.isEmpty(answer)) {
            return ajaxErr("答案不能为空");
        }
        MemberEO member = memberService.getEntity(MemberEO.class, memberId);
        if (member == null) {
            return ajaxErr("用户不存在");
        }
        if (member != null) {
            if (!member.getAnswer().equals(answer.trim())) {
                return ajaxErr("密码验证答案不正确，请重新输入");
            }
        }
        if (StringUtils.isEmpty(password)) {
            return ajaxErr("密码不能为空");
        }
        if (StringUtils.isEmpty(checkCode)) {
            return ajaxErr("验证码不能为空！");
        }
        Object obj = request.getSession().getAttribute("webCode");
        if (obj == null) {
            return ajaxErr("验证码已经失效！");
        }
        String webCode = (String) obj;
        if (!checkCode.trim().toLowerCase().equals(webCode.toLowerCase())) {
            return ajaxErr("验证码不正确，请重新输入");
        }
        if (member != null) {
            member.setPlainpw(password);
            member.setPassword(DigestUtils.md5Hex(password));
            memberService.updateEntity(member);
        }
        return getObject(member);
    }

    /**
     * 修改密码
     *
     * @param uid
     * @param question
     * @param answer
     * @param newPw
     * @param cfPw
     * @param checkCode
     * @return
     */
    @RequestMapping("setPassword")
    @ResponseBody
    public Object setPassword(HttpServletRequest request, String uid, String question, String answer, String newPw,
                              String cfPw, String checkCode, Long siteId) {
        if (StringUtils.isEmpty(uid)) {
            return ajaxErr("用户账号不能为空");
        }
        if (StringUtils.isEmpty(question)) {
            return ajaxErr("密码验证问题不能为空");
        }
        if (StringUtils.isEmpty(answer)) {
            return ajaxErr("密码验证答案不能为空");
        }
        if (StringUtils.isEmpty(newPw)) {
            return ajaxErr("新密码不能为空");
        }
        if (!newPw.equals(cfPw)) {
            return ajaxErr("确认密码与新密码不一致");
        }
        if (StringUtils.isEmpty(checkCode)) {
            return ajaxErr("验证码不能为空！");
        }
        Object obj = request.getSession().getAttribute("webCode");
        if (obj == null) {
            return ajaxErr("验证码已经失效！");
        }
        String webCode = (String) obj;
        if (!checkCode.trim().toLowerCase().equals(webCode.toLowerCase())) {
            return ajaxErr("验证码不正确，请重新输入");
        }
        MemberEO member = memberService.getMemberByUid(uid, siteId);
        if (null == member) {
            return ajaxErr("账号不存在,请重新输入");
        }
        if (!answer.equals(member.getAnswer())) {
            return ajaxErr("密码验证答案不正确，请重新输入");
        }
        member.setPlainpw(newPw);
        member.setPassword(DigestUtils.md5Hex(newPw));
        memberService.updateEntity(member);
        return getObject();
    }

    /**
     * 保存安全设置
     *
     * @param request
     * @param password
     * @param question
     * @param answer
     * @param checkCode
     * @return
     */
    @RequestMapping("saveSafe")
    @ResponseBody
    public Object saveSafe(HttpServletRequest request, String password, String question, String answer, String checkCode) {
        Object obj = request.getSession().getAttribute("webCode");
        if (obj == null) {
            return ajaxErr("验证码已经失效！");
        }
        String webCode = (String) obj;
        if (!checkCode.trim().toLowerCase().equals(webCode.toLowerCase())) {
            return ajaxErr("验证码不正确，请重新输入");
        }
        if (StringUtils.isEmpty(password) || StringUtils.isEmpty(question) || StringUtils.isEmpty(answer)) {
            return ajaxErr("[初始密码、密码验证问题、密码验证答案]不能为空!");
        }
        MemberSessionVO sessionMember = (MemberSessionVO) request.getSession().getAttribute("member");
        if (sessionMember == null) {
            return ajaxErr("您尚未登录!");
        }
        MemberEO member = memberService.getEntity(MemberEO.class, sessionMember.getId());
        if (member != null) {
            if (!member.getPassword().equals(DigestUtils.md5Hex(password))) {
                return ajaxErr("密码不正确!");
            }
            member.setQuestion(question);
            member.setAnswer(answer);
            memberService.updateEntity(member);
        } else {
            return ajaxErr("用户不存在!");
        }
        return getObject(member);
    }

    /**
     * IsLoginVO  isLogin 1已登录 0未登录
     *
     * @param request
     * @return
     */
    @RequestMapping("isLogin")
    @ResponseBody
    public Object isLogin(HttpServletRequest request) {
        MemberSessionVO sessionMember = (MemberSessionVO) request.getSession().getAttribute("member");
        if (sessionMember == null) {
            return ajaxErr("您尚未登录!");
        }
        IsLoginVO vo = new IsLoginVO();
        if (sessionMember != null) {
            vo.setIsLogin(IsLoginVO.IsLogin.Yes.getIsLogin());
            vo.setUid(sessionMember.getUid());
            vo.setUserId(sessionMember.getId());
            vo.setUserName(sessionMember.getName());
            vo.setLoginDate(new Date());
            vo.setPhone(sessionMember.getPhone());
            vo.setSmsCheck(sessionMember.getSmsCheck());
        }
        return getObject(vo);
    }

    /**
     * 获取会员信息
     *
     * @param
     * @return
     */
    @RequestMapping("getMemberVO")
    @ResponseBody
    public Object getMemberVO(HttpServletRequest request) {
        MemberSessionVO sessionMember = (MemberSessionVO) request.getSession().getAttribute("member");
        if (sessionMember == null) {
            return ajaxErr("您尚未登录!");
        }
        return getObject(sessionMember);
    }


    /**
     * 保存我的收藏
     *
     * @param
     * @return
     */
    @RequestMapping("saveMyCollection")
    @ResponseBody
    public Object saveMyCollection(HttpServletRequest request, Long siteId, String name, String link, String checkCode) {
        MemberSessionVO sessionMember = (MemberSessionVO) request.getSession().getAttribute("member");
        if (sessionMember == null) {
            return ajaxErr("您尚未登录!");
        }
        if (siteId == null || StringUtils.isEmpty(name) || StringUtils.isEmpty(link)) {
            return ajaxErr("[标题、链接]不能为空!");
        }
        MyCollectionEO eo = new MyCollectionEO();
        eo.setSiteId(siteId);
        eo.setName(name);
        eo.setLink(link);
        eo.setMemberId(sessionMember.getId());
        eo.setMemberName(sessionMember.getName());
        myCollectionService.saveEntity(eo);
        return ajaxOk();
    }

    /**
     * @param pageVO
     * @return Object   return type
     * @throws
     * @Title: getVisitDetail
     * @Description: 访问明细分页列表、我的足迹
     */
    @RequestMapping("getSiteChartMainPage")
    @ResponseBody
    public Object getSiteChartMainPage(VisitDeatilPageVo pageVO,HttpServletRequest request) {
        if (pageVO.getPageIndex() == null || pageVO.getPageIndex() < 0) {
            pageVO.setPageIndex(0L);
        }
        Integer size = pageVO.getPageSize();
        if (size == null || size <= 0 || size > Pagination.MAX_SIZE) {
            pageVO.setPageSize(10);
        }
        //会员ID从session中获取，不要从请求参数中获取
        MemberSessionVO sessionMember = (MemberSessionVO) request.getSession().getAttribute("member");
        pageVO.setMemberId(sessionMember.getId());
        Pagination page = siteChartMainService.getVisitPage(pageVO);
        List<SiteCharWebVO> vos = new ArrayList<SiteCharWebVO>();
        for (SiteChartMainEO d : (List<SiteChartMainEO>) page.getData()) {
            SiteCharWebVO vo = new SiteCharWebVO();
            BeanUtils.copyProperties(d, vo);
            String dateFormat = StringUtils.isEmpty(pageVO.getDateFormat()) ? "yyyy/MM/dd HH:mm:ss" : pageVO.getDateFormat();
            SimpleDateFormat simple = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
            try {
                simple = new SimpleDateFormat(dateFormat);
            } catch (Exception e) {
            }
            vo.setCreateTime(simple.format(d.getCreateDate()));
            vos.add(vo);
        }
        page.setData(vos);
        return getObject(page);
    }

    @RequestMapping("getMyGuestBookPage")
    @ResponseBody
    public Object getMyGuestBookPage(VisitDeatilPageVo pageVo,HttpServletRequest request) {
        MemberSessionVO sessionMember = (MemberSessionVO) request.getSession().getAttribute("member");
        pageVo.setMemberId(sessionMember.getId());
        if (pageVo.getMemberId() == null) {
            return ajaxErr("请重新登录");
        }
        GuestBookPageVO vo = new GuestBookPageVO();
        vo.setPageSize(pageVo.getPageSize());
        vo.setPageIndex(pageVo.getPageIndex());
        vo.setCreateUserId(pageVo.getMemberId());
        Pagination page = guestBookService.getMobilePage(vo);
        if (page != null && page.getData() != null && page.getData().size() > 0) {
            List<GuestBookEditVO> list = (List<GuestBookEditVO>) page.getData();
            for (GuestBookEditVO editVO : list) {
                if (editVO.getReceiveId() != null) {
                    OrganEO organEO = CacheHandler.getEntity(OrganEO.class, editVO.getReceiveId());
                    if (organEO != null) {
                        editVO.setReceiveName(organEO.getName());
                    }
                }
                if (!StringUtils.isEmpty(editVO.getReceiveUserCode())) {
                    DataDictVO dictVO = DataDictionaryUtil.getItem("guest_book_rec_users", editVO.getReceiveUserCode());
                    if (dictVO != null) {
                        editVO.setReceiveUserName(dictVO.getKey());
                    }
                }
                if (!StringUtils.isEmpty(editVO.getDealStatus())) {
                    DataDictVO dictVO = DataDictionaryUtil.getItem("deal_status", editVO.getDealStatus());
                    if (dictVO != null) {
                        editVO.setStatusName(dictVO.getKey());
                    }
                } else {
                    editVO.setStatusName("未回复");
                }
                editVO.setLink("/content/article/" + editVO.getBaseContentId() + "?id=" + editVO.getBaseContentId());
                String dateFormat = StringUtils.isEmpty(pageVo.getDateFormat()) ? "yyyy/MM/dd HH:mm:ss" : pageVo.getDateFormat();
                SimpleDateFormat simple = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
                try {
                    simple = new SimpleDateFormat(dateFormat);
                    String dateStr = simple.format(editVO.getAddDate());
                    editVO.setAddDate(simple.parse(dateStr));
                } catch (Exception e) {
                }

            }
        }
        return getObject(page);
    }

    @RequestMapping("getMyMessageBoardPage")
    @ResponseBody
    public Object getMyMessageBoardPage(VisitDeatilPageVo pageVo,HttpServletRequest request) {
        MemberSessionVO sessionMember = (MemberSessionVO) request.getSession().getAttribute("member");
        pageVo.setMemberId(sessionMember.getId());
        if (pageVo.getMemberId() == null) {
            return ajaxErr("请重新登录");
        }
        if (pageVo.getPageIndex() == null || pageVo.getPageIndex() < 0) {
            pageVo.setPageIndex(0L);
        }
        MessageBoardPageVO vo = new MessageBoardPageVO();
        vo.setPageSize(pageVo.getPageSize());
        vo.setPageIndex(pageVo.getPageIndex());
        vo.setCreateUserId(Long.valueOf(pageVo.getMemberId()));
        Pagination page = messageBoardService.getMemberPage(vo);
        if (page != null && page.getData() != null && page.getData().size() > 0) {
            List<MessageBoardEditVO> editVOList = new ArrayList<MessageBoardEditVO>();
            List<MessageBoardVO> list = (List<MessageBoardVO>) page.getData();
            for (MessageBoardVO messageBoardVO : list) {
                MessageBoardEditVO messageBoardEditVO = new MessageBoardEditVO();
                AppUtil.copyProperties(messageBoardEditVO, messageBoardVO);
                editVOList.add(messageBoardEditVO);
            }
            for (MessageBoardEditVO editVO : editVOList) {

//                //根据messageBoardId查询转办表
//                Map<String, Object> forwardMap = new HashMap<String, Object>();
//                forwardMap.put("messageBoardId", editVO.getId());
//                forwardMap.put("createUserId", pageVo.getMemberId());
//                forwardMap.put("operationStatus", MessageBoardForwardEO.OperationStatus.Normal.toString());
//                MessageBoardForwardEO forwardEO = forwardService.getEntity(MessageBoardForwardEO.class, forwardMap);
//
//                if (forwardEO != null) {
//                    if (forwardEO != null && forwardEO.getReceiveOrganId() != null) {
//                        OrganEO organEO = CacheHandler.getEntity(OrganEO.class, forwardEO.getReceiveOrganId());
//                        if (organEO != null) {
//                            editVO.setReceiveUnitName(organEO.getName());
//                        }
//                    }
//                } else {
//                    //根据messageBoardId查询转办表
//                    Map<String, Object> replyMap = new HashMap<String, Object>();
//                    replyMap.put("messageBoardId", editVO.getId());
//                    replyMap.put("isSuper", 1);
//                    MessageBoardReplyEO replyEO = replyService.getEntity(MessageBoardReplyEO.class, replyMap);
//                    if (replyEO != null) {
//                        editVO.setReceiveUnitName(replyEO.getReceiveName());
//                    }
//                }
                //转办
                List<MessageBoardForwardVO> forwardVOList = forwardService.getAllUnit(editVO.getId());
                //回复
                List<MessageBoardReplyVO> replyVOList = replyService.getAllDealReply(editVO.getId());
                String recUnitNames="";
                if(null != replyVOList && replyVOList.size() > 0) {
                    for(MessageBoardReplyVO replyVO:replyVOList){
                        if(!cn.lonsun.core.base.util.StringUtils.isEmpty(replyVO.getReceiveName())){
                            recUnitNames+=replyVO.getReceiveName()+",";
                        }else{
                            OrganEO organEO = CacheHandler.getEntity(OrganEO.class, replyVO.getCreateOrganId());
                            if (organEO != null) {
                                recUnitNames+=organEO.getName()+",";
                            }
                        }
                    }
                    if(!cn.lonsun.core.base.util.StringUtils.isEmpty(recUnitNames)){
                        recUnitNames=recUnitNames.substring(0,recUnitNames.length()-1);
                        editVO.setReceiveUnitName(recUnitNames);
                    }else{
                        editVO.setReceiveUnitName("暂无接收单位");
                    }
                }else if (forwardVOList != null && forwardVOList.size() > 0 ) {
                    for(MessageBoardForwardVO forwardVO:forwardVOList){
                        if (!cn.lonsun.core.base.util.StringUtils.isEmpty(forwardVO.getReceiveUnitName())) {
                            recUnitNames+=forwardVO.getReceiveUnitName()+",";
                        } else {
                            OrganEO organEO = CacheHandler.getEntity(OrganEO.class, forwardVO.getReceiveOrganId());
                            if (organEO != null) {
                                recUnitNames+=organEO.getName()+",";
                            }
                        }
                    }
                    if(!cn.lonsun.core.base.util.StringUtils.isEmpty(recUnitNames)){
                        recUnitNames=recUnitNames.substring(0,recUnitNames.length()-1);
                        editVO.setReceiveUnitName(recUnitNames);
                    }else{
                        editVO.setReceiveUnitName("暂无接收单位");
                    }
                }else if(forwardVOList==null||forwardVOList.size()==0) {
                    editVO.setReceiveUnitName("暂无接收单位");
                }

                if (editVO.getDealStatus() != null && (editVO.getDealStatus().equals("handled") || editVO.getDealStatus().equals("replyed"))) {
                    editVO.setDealName("已回复");
                } else {
                    editVO.setDealName("未回复");
                }
            /*	if (!StringUtils.isEmpty(editVO.getReceiveUserCode())) {
                    DataDictVO dictVO = DataDictionaryUtil.getItem("guest_book_rec_users", editVO.getReceiveUserCode());
					if (dictVO != null) {
						editVO.setReceiveUserName(dictVO.getKey());
					}
				}*/
                /*if (!StringUtils.isEmpty(editVO.getDealStatus())) {
                    DataDictVO dictVO = DataDictionaryUtil.getItem("deal_status",editVO.getDealStatus());
					if (dictVO != null) {
						editVO.setStatusName(dictVO.getKey());
					}
				}else{
					editVO.setStatusName("未回复");
				}*/
                editVO.setLink("/content/article/" + editVO.getBaseContentId() + "?id=" + editVO.getBaseContentId());
                String dateFormat = StringUtils.isEmpty(pageVo.getDateFormat()) ? "yyyy/MM/dd HH:mm:ss" : pageVo.getDateFormat();
                SimpleDateFormat simple = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
                try {
                    simple = new SimpleDateFormat(dateFormat);
                    String dateStr = simple.format(editVO.getAddDate());
                    editVO.setAddDate(simple.parse(dateStr));
                } catch (Exception e) {
                }

            }
            page.setData(editVOList);
        }
        return getObject(page);
    }


    /**
     * 删除足迹
     *
     * @param
     * @return
     */
    @RequestMapping("delSiteChartMain")
    @ResponseBody
    public Object delSiteChartMain(Long scId) {
        if (scId == null) {
            return ajaxErr("Id不能为空!");
        }
        siteChartMainService.delete(SiteChartMainEO.class, scId);
        return getObject();
    }

    //我的收藏列表
    @RequestMapping("getMycollectionPage")
    @ResponseBody
    public Object getMycollectionPage(MyCollectionVO pagevo,HttpServletRequest request) {
        if (pagevo.getPageIndex() == null || pagevo.getPageIndex() < 0) {
            pagevo.setPageIndex(0L);
        }
        if (pagevo.getPageSize() == null || pagevo.getPageSize() < 0 || pagevo.getPageSize() > Pagination.MAX_SIZE) {
            pagevo.setPageSize(10);
        }
        MemberSessionVO sessionMember = (MemberSessionVO) request.getSession().getAttribute("member");
        pagevo.setMemberId(sessionMember.getId());
        Pagination page = myCollectionService.getPage(pagevo);
        List<MyCollectionVO> vos = new ArrayList<MyCollectionVO>();
        for (MyCollectionEO d : (List<MyCollectionEO>) page.getData()) {
            MyCollectionVO vo = new MyCollectionVO();
            BeanUtils.copyProperties(d, vo);
            String dateFormat = StringUtils.isEmpty(pagevo.getDateFormat()) ? "yyyy/MM/dd HH:mm:ss" : pagevo.getDateFormat();
            SimpleDateFormat simple = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
            try {
                simple = new SimpleDateFormat(dateFormat);
            } catch (Exception e) {
            }
            vo.setCreateTime(simple.format(d.getCreateDate()));
            vos.add(vo);
        }
        page.setData(vos);

        return getObject(page);
    }

    //删除我的收藏
    @RequestMapping("delMyCollect")
    @ResponseBody
    public Object delMyCollect(Long scId) {
        if (scId == null) {
            return ajaxErr("Id不能为空!");
        }
        myCollectionService.delete(MyCollectionEO.class, scId);
        return getObject();
    }

    @RequestMapping("addOrEdit")
    public String addOrEdit(Long reportId, Model model) {
        model.addAttribute("reportId", reportId);
        return "/mas/login/memberReport";
    }

    @RequestMapping("getSiteInfo")
    @ResponseBody
    public Object getSiteInfo() {
//      List<SiteMgrEO> siteMgrEOs =(List<SiteMgrEO>) CacheHandler.getEntity(SiteMgrEO.class);
        Map<String, Object> param = new HashMap<String, Object>();
        param.put("type", IndicatorEO.Type.CMS_Site.toString());
        param.put("recordStatus", AMockEntity.RecordStatus.Normal.toString());
        List<IndicatorEO> indicatorEOs = iIndicatorService.getEntities(IndicatorEO.class, param);
        return getObject(indicatorEOs);
    }

    @RequestMapping("getInfo")
    @ResponseBody
    public Object getInfo(Long id, HttpServletRequest request) {
        BaseContentEO memberReportEO = new BaseContentEO();
        Map<String, Object> map = new HashMap<String, Object>();
        if (!AppUtil.isEmpty(id)) {
            memberReportEO = baseContentService.getEntity(BaseContentEO.class, id);
            Criteria criteria = Criteria.where("_id").is(id);
            Query query = new Query(criteria);
            ContentMongoEO _eo = contentMongoService.queryOne(query);
            String content = "";
            if (!AppUtil.isEmpty(_eo)) {
                content = _eo.getContent();
            }
            memberReportEO.setArticle(content);
        }
        map.put("data", memberReportEO);
        return getObject(memberReportEO);
    }

    @RequestMapping("saveVO")
    @ResponseBody
    public Object saveVO(ReportVO reportVO, HttpServletRequest request) {
        MemberSessionVO sessionMember = (MemberSessionVO) request.getSession().getAttribute("member");
        if (sessionMember == null) {
            return ajaxErr("您尚未登录或登录超时!");
        }
        if (StringUtils.isEmpty(reportVO.getCheckCode())) {
            return ajaxErr("验证码不能为空！");
        }
        String webCode = (String) ContextHolderUtils.getSession().getAttribute("webCode");
        if (!reportVO.getCheckCode().trim().toLowerCase().equals(webCode.toLowerCase())) {
            return ajaxErr("验证码不正确，请重新输入！");
        }
        if (AppUtil.isEmpty(reportVO.getContent())) {
            return ajaxErr("内容不能为空！");
        }
        reportVO.setMemberId(sessionMember.getId());
        reportVO.setName(sessionMember.getUid());
        long id;
        if (AppUtil.isEmpty(reportVO.getReportId())) {
            BaseContentEO eo = new BaseContentEO();
            eo.setSiteId(reportVO.getSiteId());
            eo.setTitle(reportVO.getTitle());
            eo.setAuthor(reportVO.getAuthor());
            eo.setTypeCode(BaseContentEO.TypeCode.articleNews.toString());
            eo.setResources(reportVO.getProvider());
            eo.setMember(reportVO.getName());
            eo.setMemberConStu(0);
            eo.setId(reportVO.getReportId());
            eo.setImageLink(reportVO.getImageLink());
            eo.setEditor("会员撰稿");
            eo.setCreateDate(new Date());
            id = baseContentService.saveEntity(eo);
        } else {
            id = reportVO.getReportId();
            BaseContentEO eo = baseContentService.getEntity(BaseContentEO.class, id);
            eo.setMemberConStu(0);
            eo.setImageLink(reportVO.getImageLink());
            eo.setResources(reportVO.getProvider());
            eo.setTitle(reportVO.getTitle());
            eo.setAuthor(reportVO.getAuthor());
            baseContentService.updateEntity(eo);
        }
        ContentMongoEO _eo = new ContentMongoEO();
        _eo.setId(id);
        _eo.setContent(reportVO.getContent());
        contentMongoService.save(_eo);
        return getObject();
    }

    @RequestMapping("delete")
    @ResponseBody
    public Object delete(Long[] ids) {
        if (AppUtil.isEmpty(ids) || ids.length < 1) {
            return ajaxErr("请选择待删除项!");

        }
        List<BaseContentEO> memberReportEOs = baseContentService.getEntities(BaseContentEO.class, ids);
        for (BaseContentEO reportEO : memberReportEOs) {
            if (reportEO.getMemberConStu().equals(3) || reportEO.getMemberConStu().equals(1)) {
                return ajaxErr("被采用或已提交信息不可删除!");
            }

        }
        baseContentService.delete(BaseContentEO.class, ids);
        return getObject();
    }

    @RequestMapping("put")
    @ResponseBody
    public Object put(Long[] ids) {
        if (AppUtil.isEmpty(ids) || ids.length < 1) {
            return ajaxErr("请选择待提交项!");

        }
        List<BaseContentEO> memberReportEOs = baseContentService.getEntities(BaseContentEO.class, ids);
        for (BaseContentEO reportEO : memberReportEOs) {
            reportEO.setMemberConStu(1);
            reportEO.setMemPutDate(new Date());
        }
        baseContentService.updateEntities(memberReportEOs);
        return getObject();
    }
}
