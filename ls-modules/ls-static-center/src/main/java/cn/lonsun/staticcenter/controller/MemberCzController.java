package cn.lonsun.staticcenter.controller;

import cn.lonsun.cache.client.CacheHandler;
import cn.lonsun.core.base.controller.BaseController;
import cn.lonsun.core.exception.BaseRunTimeException;
import cn.lonsun.core.util.TipsMode;
import cn.lonsun.site.site.internal.entity.SiteMgrEO;
import cn.lonsun.sms.czsms.util.SmsHttpClient;
import cn.lonsun.sms.internal.service.ISendSmsService;
import cn.lonsun.system.member.internal.entity.MemberEO;
import cn.lonsun.system.member.internal.service.IMemberService;
import cn.lonsun.system.member.vo.MemberSessionVO;
import cn.lonsun.system.member.vo.MemberVO;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.sql.SQLException;

@Controller
@RequestMapping(value = "/member/cz", produces = { "application/json;charset=UTF-8" })
public class MemberCzController extends BaseController {

    /**
     * 正则表达式：验证手机号
     */
    public static final String REGEX_MOBILE = "^((12[0-9])|(13[0-9])|(14[0-9])|(15[^4,\\D])|(16[0-9])|(17[0-9])|(19[0-9])|(18[0-9]))\\d{8}$";

    @Autowired
    private IMemberService memberService;

    @Autowired
    private ISendSmsService sendSmsService;


    /**
     * 短信验证
     * @param m
     * @return
     */
    @RequestMapping("smsCheck")
    public String smsCheck(Model m){

        return "sms/sms_check";
    }



    /**
     * 获取滁州短信验证码
     *
     * @param request
     * @param phone   手机号
     * @param  phoneExist 0 验证  1 不验证
     * @return
     */
    @RequestMapping("getSmsCode")
    @ResponseBody
    public Object getSmsCode(@RequestParam(value="phoneExist", defaultValue="0") Integer phoneExist, HttpServletRequest request, Long siteId, String phone, Long memberId) {
        if (siteId == null) {
            return ajaxErr("所属站点不能为空！");
        }
        if (StringUtils.isEmpty(phone)) {
            return ajaxErr("手机号不能为空！");
        }
        if (!phone.matches(REGEX_MOBILE)) {
            return ajaxErr("手机号格式不正确！");
        }

        if(phoneExist == 0){
            Boolean hasPhone = memberService.isExistPhone(phone, siteId, memberId);
            if (hasPhone) {
                return ajaxErr("手机号码已经存在，请重新输入！");
            }
        }
        Integer code = null;
        try {
            SiteMgrEO siteMgrs = CacheHandler.getEntity(SiteMgrEO.class, siteId);
            String siteName = siteMgrs == null ? "滁州市政府" : siteMgrs.getName();
            code = (int) ((Math.random() * 9 + 1) * 100000);
            String content = "您的注册验证码为：" + code + "【" + siteName + "】";
            Boolean isSend = SmsHttpClient.sendSmsUtil(phone, content, code.toString());
            if (!isSend) {
                return ajaxErr("短信发送失败,请重新发送！");
            }
        } catch (Exception e) {
            return ajaxErr("发送短信异常,请重新发送！");
        }
        return getObject();
    }

    /**
     * 验证手机号
     *
     * @param request
     * @param phone   手机号
     * @return
     */
    @RequestMapping("checkSmsCode")
    @ResponseBody
    public Object checkSmsCode(String smsCode, String phone) {
        if (StringUtils.isEmpty(phone) || StringUtils.isEmpty(smsCode)) {
            return ajaxErr("[手机号、短信验证码]不能为空！");
        }
        if (!phone.matches(REGEX_MOBILE)) {
            return ajaxErr("手机号格式不正确！");
        }
        Integer status = sendSmsService.isTimeOut(phone, smsCode);
        if (status == 2) {
            return ajaxErr("短信验证码已失效！");
        }
        if (status == 3) {
            return ajaxErr("短信验证码错误！");
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
    public Object saveRegister(HttpServletRequest request, MemberVO memberVO, String smsCode) {
        if (memberVO.getSiteId() == null || StringUtils.isEmpty(memberVO.getUid()) || StringUtils.isEmpty(memberVO.getName())
                || StringUtils.isEmpty(memberVO.getPhone()) || StringUtils.isEmpty(memberVO.getIdCard()) || StringUtils.isEmpty(memberVO.getQuestion()) || StringUtils.isEmpty(memberVO.getAnswer())) {
            return ajaxErr("[所属站点、注册账号、真实姓名、手机号、身份证号码、密码提示问题、密码提示答案]不能为空!");
        }
        Boolean hasUid = memberService.isExistUid(memberVO.getUid(), memberVO.getSiteId(), null);
        if (hasUid) {
            return ajaxErr("账号已经存在，请重新输入~");
        }
        if (!memberVO.getPhone().matches(REGEX_MOBILE)) {
            return ajaxErr("手机号格式不正确！");
        }
        if (StringUtils.isEmpty(smsCode)) {
            return ajaxErr("手机号注册验证码不能为空~");
        }
        Boolean hasPhone = memberService.isExistPhone(memberVO.getPhone(), memberVO.getSiteId(), null);
        if (hasPhone) {
            return ajaxErr("手机号码已经存在，请重新输入~");
        }
        Integer status = sendSmsService.isTimeOut(memberVO.getPhone(), smsCode);
        if (status == 2) {
            return ajaxErr("短信验证码已失效！");
        }
        if (status == 3) {
            return ajaxErr("短信验证码错误！");
        }
        MemberEO member = new MemberEO();
        String password = memberVO.getPassword();
        BeanUtils.copyProperties(memberVO, member);
        member.setStatus(MemberEO.Status.Enabled.getStatus());
        member.setPlainpw(password);
        member.setPassword(DigestUtils.md5Hex(password));
        //短信已验证
        member.setSmsCheck(1);
        memberService.saveEntity(member);
        //设置session
        MemberSessionVO sessionMember = new MemberSessionVO();
        BeanUtils.copyProperties(member, sessionMember);
        request.getSession().setAttribute("member", sessionMember);
        return getObject(sessionMember);
    }

    /**
     * 更新手机号
     * @param m
     * @return
     */
    @RequestMapping("updatePhone")
    @ResponseBody
    public Object updatePhone(HttpServletRequest request,String phone,String smsCode){
        if (StringUtils.isEmpty(phone) || StringUtils.isEmpty(smsCode)) {
            return ajaxErr("[手机号、验证码]不能为空!");
        }
        MemberSessionVO sessionMember  = (MemberSessionVO) request.getSession().getAttribute("member");
        if(sessionMember == null){
            return ajaxErr("您尚未登录!");
        }
        MemberEO member = memberService.getEntity(MemberEO.class, sessionMember.getId());
        if(member == null){
            return ajaxErr("会员信息不存在");
        }
        Boolean hasPhone = memberService.isExistPhone(phone, member.getSiteId(), member.getId());
        if (hasPhone) {
            return ajaxErr("手机号码已经存在，请重新输入！");
        }
        Integer status = sendSmsService.isTimeOut(phone, smsCode);
        if (status == 2) {
            return ajaxErr("短信验证码已失效！");
        }
        if (status == 3) {
            return ajaxErr("短信验证码错误！");
        }
        member.setPhone(phone);
        member.setSmsCheck(1);
        memberService.updateEntity(member);
        BeanUtils.copyProperties(member, sessionMember);
        request.getSession().setAttribute("member", sessionMember);
        return getObject();
    }
}
