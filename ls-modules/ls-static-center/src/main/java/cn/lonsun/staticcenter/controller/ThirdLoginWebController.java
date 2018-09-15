package cn.lonsun.staticcenter.controller;

import cn.lonsun.common.util.AppUtil;
import cn.lonsun.core.base.controller.BaseController;
import cn.lonsun.core.base.entity.AMockEntity;
import cn.lonsun.core.util.RequestUtil;
import cn.lonsun.shiro.util.RSAUtils;
import cn.lonsun.site.thirdLoginManage.internal.entity.ThirdLoginMgrEO;
import cn.lonsun.site.thirdLoginManage.internal.service.IThirdLoginMgrService;
import cn.lonsun.staticcenter.util.QqLoginUtil;
import cn.lonsun.staticcenter.util.WeiBoLoginUtil;
import cn.lonsun.system.member.internal.entity.MemberEO;
import cn.lonsun.system.member.internal.service.IMemberService;
import cn.lonsun.system.member.vo.MemberSessionVO;
import cn.lonsun.system.member.vo.MemberVO;
import cn.lonsun.util.HttpRequestUtil;
import com.alibaba.fastjson.JSONObject;
import com.qq.connect.QQConnectException;
import com.qq.connect.api.OpenID;
import com.qq.connect.javabeans.AccessToken;
import com.qq.connect.javabeans.qzone.UserInfoBean;
import com.qq.connect.utils.QQConnectConfig;
import com.qq.connect.utils.RandomStatusGenerator;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import weibo4j.model.WeiboException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * 第三方登录
 * @Author: liuk
 * @Date: 2018-4-24 11:27:38
 */
@RequestMapping(value="thirdLogin", produces = {"application/json;charset=UTF-8"})
@Controller
public class ThirdLoginWebController extends BaseController {

    Logger logger = LoggerFactory.getLogger(ThirdLoginWebController.class);

    @Autowired
    private IThirdLoginMgrService thirdLoginMgrService;

    @Autowired
    private IMemberService memberService;

    @Value("${qqAuthorizeURL}")
    private String qqAuthorizeURL;

    @Value("${qqRedirectURI}")
    private String qqRedirectURI;

    @Value("${weChatAuthorizeURL}")
    private String weChatAuthorizeURL;

    @Value("${weChatRedirectURI}")
    private String weChatRedirectURI;

    @Value("${weChatAccessTokenURL}")
    private String weChatAccessTokenURL;

    @Value("${weChatRefreshTokenURL}")
    private String weChatRefreshTokenURL;

    @Value("${weChatUserInfoURL}")
    private String weChatUserInfoURL;

    @Value("${weiBoAuthorizeURL}")
    private String weiBoAuthorizeURL;

    @Value("${weiBoAccessTokenURL}")
    private String weiBoAccessTokenURL;

    @Value("${weiBoRedirectURI}")
    private String weiBoRedirectURI;

    @Value("${thirdBindURL}")
    private String thirdBindURL;

    @Value("${memberCenterUrl}")
    private String memberCenterUrl;


    /**
     * 检验第三方登录功能是否正常
     * @Author: liuk
     * @Date: 2018-4-23 17:34:214
     */
    @RequestMapping("checkThirdLogin")
    @ResponseBody
    public Object checkThirdLogin(Long siteId,String type){
        if(AppUtil.isEmpty(siteId)){
            return ajaxErr("站点id不能为空");
        }
        if(AppUtil.isEmpty(type)){
            return ajaxErr("type不能为空");
        }
        ThirdLoginMgrEO thirdLoginMgrEO = thirdLoginMgrService.getMgrInfoByType(siteId,type);
        if(thirdLoginMgrEO==null){
            return ajaxErr("相关api信息尚未配置，请联系管理员");
        }else{
            Integer status = thirdLoginMgrEO.getStatus();
            if(status!=null&&status.intValue()==1){
                if(AppUtil.isEmpty(thirdLoginMgrEO.getAppId())||AppUtil.isEmpty(thirdLoginMgrEO.getAppSecret())){
                    return ajaxErr("相关api信息尚未配置，请联系管理员");
                }
            }else{
                return ajaxErr(type+"登录功能尚未启用");
            }
        }
        return getObject();
    }

    /**
     * 检验第三方登录是否已登录
     * @Author: liuk
     * @Date: 2018-4-23 17:34:214
     */
    @RequestMapping("checkThirdLoginStatus")
    @ResponseBody
    public Object checkThirdLogin(String type,HttpServletRequest request){
        if(AppUtil.isEmpty(type)){
            return ajaxErr("type不能为空");
        }
        String openID = "";
        if(ThirdLoginMgrEO.Type.QQ.toString().equals(type)){
            openID = (String)request.getSession().getAttribute("qq_openID");

        }else if(ThirdLoginMgrEO.Type.WeiBo.toString().equals(type)){
            openID = (String)request.getSession().getAttribute("weiBo_openID");

        }else if(ThirdLoginMgrEO.Type.WeChat.toString().equals(type)){
            openID = (String)request.getSession().getAttribute("weChat_openID");

        }
        if(AppUtil.isEmpty(openID)){
            return ajaxErr("尚未登录");
        }
        return getObject();
    }

    /**
     * qq登录
     * @Author: liuk
     * @Date: 2018-4-23 17:34:214
     */
    @RequestMapping("qqLogin")
    public void qqLogin(Long siteId,HttpServletRequest request, HttpServletResponse response){
        ThirdLoginMgrEO qq = thirdLoginMgrService.getMgrInfoByType(siteId,ThirdLoginMgrEO.Type.QQ.toString());
        String appId = qq.getAppId();//获取后台配置的应用id
//        String appSecret = qq.getAppSecret();

        String state = RandomStatusGenerator.getUniqueState();
        HttpSession session = request.getSession();
        session.setAttribute("qq_connect_state", state);
        session.setAttribute("siteId",siteId);//将siteId放入session中，后边获取qq用户信息的时候用
        String scope = QQConnectConfig.getValue("scope");
        //qq登录跳转地址
        String url = QQConnectConfig.getValue("authorizeURL").trim() + "?client_id=" + appId.trim() + "&redirect_uri=" + qqRedirectURI + "&response_type=code&state=" + state + "&scope=" + scope;
        response.setContentType("text/html;charset=utf-8");
        try {
            response.sendRedirect(url);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    /**
     * qq登录回调
     * @Author: liuk
     * @Date: 2018-4-23 17:34:214
     */
    @RequestMapping("qqLoginCallback")
    public void qqLoginCallback(String code,String state,HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("text/html; charset=utf-8");
        try {
            HttpSession session = request.getSession();
            Long siteId = (Long)session.getAttribute("siteId");
            ThirdLoginMgrEO qq = thirdLoginMgrService.getMgrInfoByType(siteId,ThirdLoginMgrEO.Type.QQ.toString());
            String appId = qq.getAppId();//获取后台配置的应用id
            String appSecret = qq.getAppSecret();//获取后台配置的应用id
            AccessToken accessTokenObj = QqLoginUtil.getAccessTokenByRequest(appId,appSecret,code,state,qqRedirectURI,request);

            String accessToken = null;
            String openID = null;
            long tokenExpireIn = 0L;
            if (accessTokenObj.getAccessToken().equals("")) {
//                我们的网站被CSRF攻击了或者用户取消了授权
//                做一些数据统计工作
                System.out.print("没有获取到响应参数");
            } else {
                accessToken = accessTokenObj.getAccessToken();
                tokenExpireIn = accessTokenObj.getExpireIn();
                session.setAttribute("qq_access_token", accessToken);
                session.setAttribute("qq_token_expirein", String.valueOf(tokenExpireIn));

                // 利用获取到的accessToken 去获取当前用的openid -------- start
                OpenID openIDObj =  new OpenID(accessToken);
                openID = openIDObj.getUserOpenID();
                session.setAttribute("qq_openID", openID);//将openId放入session中，绑定用户的时候会用到

                UserInfoBean userInfoBean = QqLoginUtil.getUserInfo(openID,appId,accessToken);
                String name = "",img="";
                if (userInfoBean.getRet() == 0) {
                    name = userInfoBean.getNickname();
                    img = userInfoBean.getAvatar().getAvatarURL100();
                }
                MemberSessionVO sessionMember = (MemberSessionVO) request.getSession().getAttribute("member");

                Map<String,Object> param = new HashMap<String,Object>();
                param.put("qqOpenId",openID);
                param.put("siteId",siteId);
                param.put("recordStatus", AMockEntity.RecordStatus.Normal.toString());
                MemberEO memberEO = memberService.getEntity(MemberEO.class,param);

                if(memberEO!=null){//已经绑定过用户
                    //会员中心添加绑定账号时，如果登录QQ号已经与其他会员账号绑定，则提示添加绑定出错
                    if(sessionMember!=null&&memberEO.getId().intValue()!=sessionMember.getId().intValue()){
                        response.getWriter().write("<script>alert('此QQ账号已绑定其他会员账号');" +
                                "location.href='"+memberCenterUrl+"';</script>");
//                        response.sendRedirect(memberCenterUrl);//重定向到会员中心
                    }else{
                        memberEO.setQqName(name);//更新昵称
                        memberEO.setQqImg(img);//更新qq头像
                        //设置最后登录ip 和 最后登录时间
                        String ip = RequestUtil.getIpAddr(request);
                        memberEO.setIp(ip);
                        Integer loginTimes = (memberEO.getLoginTimes() == null ? 0 : memberEO.getLoginTimes());
                        memberEO.setLoginTimes(loginTimes);
                        memberEO.setLastLoginDate(new Date());
                        memberService.updateEntity(memberEO);
                        sessionMember = new MemberSessionVO();
                        BeanUtils.copyProperties(memberEO, sessionMember);
                        session.setAttribute("member", sessionMember);
                        response.sendRedirect(memberCenterUrl);//重定向到会员中心
                    }
                }else{
                    if(sessionMember!=null&&sessionMember.getId()!=null){//用户已登录，添加绑定时直接与当前登录用户绑定
                        memberEO = memberService.getEntity(MemberEO.class,sessionMember.getId());
                        if(AppUtil.isEmpty(memberEO.getQqOpenId())){
                            memberEO.setQqOpenId(openID);//保存每个QQ对应的唯一标识
                            memberEO.setQqName(name);//保存QQ昵称
                            memberEO.setQqImg(img);//保存QQ头像地址
                            memberService.updateEntity(memberEO);
                            sessionMember = new MemberSessionVO();
                            BeanUtils.copyProperties(memberEO, sessionMember);
                            session.setAttribute("member", sessionMember);
                            response.sendRedirect(memberCenterUrl);//重定向到会员中心
                        }else if(memberEO.getQqOpenId().equals(openID)){//QQ绑定的账号与当前账号一致，直接跳到会员中心
                            response.sendRedirect(memberCenterUrl);//重定向到会员中心
                        }else{//跳到绑定界面
                            session.setAttribute("qq_img", img);
                            session.setAttribute("qq_name", name);
                            response.sendRedirect(thirdBindURL +"?name_="+ java.net.URLEncoder.encode(name,"utf-8")+"&img="+img+"&type_=QQ");//重定向到用户绑定界面
                        }
                    }else{
                        session.setAttribute("qq_img", img);
                        session.setAttribute("qq_name", name);
                        response.sendRedirect(thirdBindURL +"?name_="+ java.net.URLEncoder.encode(name,"utf-8")+"&img="+img+"&type_=QQ");//重定向到用户绑定界面
                    }
                }


            }

        } catch (QQConnectException e) {
            e.printStackTrace();
        }

    }


    /**
     * 微信登录
     * @Author: liuk
     * @Date: 2018-4-23 17:34:214
     */
    @RequestMapping("weChatLogin")
    public void weChatLogin(Long siteId,HttpServletRequest request, HttpServletResponse response){
        ThirdLoginMgrEO weChat = thirdLoginMgrService.getMgrInfoByType(siteId,ThirdLoginMgrEO.Type.WeChat.toString());
        String appId = weChat.getAppId();//获取后台配置的应用id
//        String appSecret = qq.getAppSecret();

        String state = RandomStatusGenerator.getUniqueState();
        HttpSession session = request.getSession();
        session.setAttribute("weChat_connect_state", state);
        session.setAttribute("siteId",siteId);//将siteId放入session中，后边获取qq用户信息的时候用
        String scope = "snsapi_login";
        //微信登录跳转地址
        String url = weChatAuthorizeURL.trim() + "?appid=" + appId.trim() + "&redirect_uri="
                + URLEncoder.encode(weChatRedirectURI) + "&response_type=code&state=" + state
                + "&scope=" + scope+"#wechat_redirect";
        response.setContentType("text/html;charset=utf-8");
        try {
            response.sendRedirect(url);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 微信登录回调
     * @Author: liuk
     * @Date: 2018-4-23 17:34:214
     */
    @RequestMapping("weChatLoginCallback")
    public void weChatLoginCallback(String code,String state,HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("text/html; charset=utf-8");
        try {
            HttpSession session = request.getSession();
            Long siteId = (Long)session.getAttribute("siteId");
            ThirdLoginMgrEO weChat = thirdLoginMgrService.getMgrInfoByType(siteId,ThirdLoginMgrEO.Type.WeChat.toString());
            String appId = weChat.getAppId();//获取后台配置的应用id
            String appSecret = weChat.getAppSecret();//获取后台配置的应用id
            String accesstTokenParam = "appid="+appId+"&secret="+appSecret+"&code="+code+"&grant_type=authorization_code";
            JSONObject accessTokenJson = HttpRequestUtil.sendGet(weChatAccessTokenURL,accesstTokenParam);//获取accessToken信息

            System.out.println("accessTokenJson:"+accessTokenJson.toString());

            String accessToken = null;
            String openID = null;
            if (!AppUtil.isEmpty(accessTokenJson.getString("errcode"))) {
//                我们的网站被CSRF攻击了或者用户取消了授权
//                做一些数据统计工作
                String errmsg = accessTokenJson.getString("errmsg");
                response.getWriter().write("<script>alert('获取access_token出错，错误信息："+errmsg+"');" +
                        "location.href='"+memberCenterUrl+"';</script>");
            } else {
                accessToken = accessTokenJson.getString("access_token");
                openID = accessTokenJson.getString("openid");
                session.setAttribute("weChat_access_token", accessToken);
                session.setAttribute("weChat_openID", openID);//将openId放入session中，绑定用户的时候会用到

                //刷新token
                String refresh_token = accessTokenJson.getString("refresh_token");
                session.setAttribute("weChat_refresh_token", refresh_token);
                String refreshTokenParam = "appid="+appId+"&grant_type=refresh_token&refresh_token="+refresh_token;
                JSONObject refreshTokenJson = HttpRequestUtil.sendGet(weChatRefreshTokenURL,refreshTokenParam);
                if(refreshTokenJson!=null&&!AppUtil.isEmpty(refreshTokenJson.getString("access_token"))){
                    accessToken = accessTokenJson.getString("access_token");
                    session.setAttribute("weChat_access_token", accessToken);
                    refresh_token = accessTokenJson.getString("refresh_token");
                    session.setAttribute("weChat_refresh_token", refresh_token);
                }

                String userInfoParam = "access_token="+accessToken+"&openid="+openID;
                JSONObject userInfoJson = HttpRequestUtil.sendGet(weChatUserInfoURL,userInfoParam);//获取用户信息
                System.out.println("userInfoJson:"+userInfoJson.toString());
                String name = "",img="";
                if(userInfoJson!=null&&!AppUtil.isEmpty(userInfoJson.getString("nickname"))){//成功获取到用户信息
                    name = userInfoJson.getString("nickname");//昵称
                    img = userInfoJson.getString("headimgurl");//头像
                }

                MemberSessionVO sessionMember = (MemberSessionVO) request.getSession().getAttribute("member");
                Map<String,Object> paramMap = new HashMap<String,Object>();
                paramMap.put("weChatOpenId",openID);
                paramMap.put("siteId",siteId);
                paramMap.put("recordStatus", AMockEntity.RecordStatus.Normal.toString());
                MemberEO memberEO = memberService.getEntity(MemberEO.class,paramMap);

                if(memberEO!=null){//已经绑定过用户
                    //会员中心添加绑定账号时，如果登录QQ号已经与其他会员账号绑定，则提示添加绑定出错
                    if(sessionMember!=null&&memberEO.getId().intValue()!=sessionMember.getId().intValue()){
                        response.getWriter().write("<script>alert('此微信账号已绑定其他会员账号');" +
                                "location.href='"+memberCenterUrl+"';</script>");
//                        response.sendRedirect(memberCenterUrl);//重定向到会员中心
                    }else{
                        memberEO.setWeChatName(name);//更新昵称
                        memberEO.setWeChatImg(img);//更新qq头像
                        //设置最后登录ip 和 最后登录时间
                        String ip = RequestUtil.getIpAddr(request);
                        memberEO.setIp(ip);
                        Integer loginTimes = (memberEO.getLoginTimes() == null ? 0 : memberEO.getLoginTimes());
                        memberEO.setLoginTimes(loginTimes);
                        memberEO.setLastLoginDate(new Date());
                        memberService.updateEntity(memberEO);
                        sessionMember = new MemberSessionVO();
                        BeanUtils.copyProperties(memberEO, sessionMember);
                        session.setAttribute("member", sessionMember);
                        response.sendRedirect(memberCenterUrl);//重定向到会员中心
                    }
                }else{
                    if(sessionMember!=null&&sessionMember.getId()!=null){//用户已登录，添加绑定时直接与当前登录用户绑定
                        memberEO = memberService.getEntity(MemberEO.class,sessionMember.getId());
                        if(AppUtil.isEmpty(memberEO.getWeChatOpenId())){
                            memberEO.setWeChatOpenId(openID);//保存每个WeChat对应的唯一标识
                            memberEO.setWeChatName(name);//保存WeChat昵称
                            memberEO.setWeChatImg(img);//保存WeChat头像地址
                            memberService.updateEntity(memberEO);
                            sessionMember = new MemberSessionVO();
                            BeanUtils.copyProperties(memberEO, sessionMember);
                            session.setAttribute("member", sessionMember);
                            response.sendRedirect(memberCenterUrl);//重定向到会员中心
                        }else if(memberEO.getWeChatOpenId().equals(openID)){//QQ绑定的账号与当前账号一致，直接跳到会员中心
                            response.sendRedirect(memberCenterUrl);//重定向到会员中心
                        }else{//跳到绑定界面
                            session.setAttribute("weChat_img", img);
                            session.setAttribute("weChat_name", name);
                            response.sendRedirect(thirdBindURL +"?name_="+ java.net.URLEncoder.encode(name,"utf-8")+"&img="+img+"&type_=WeChat");//重定向到用户绑定界面
                        }
                    }else{
                        session.setAttribute("weChat_img", img);
                        session.setAttribute("weChat_name", name);
                        response.sendRedirect(thirdBindURL +"?name_="+ java.net.URLEncoder.encode(name,"utf-8")+"&img="+img+"&type_=WeChat");//重定向到用户绑定界面
                    }
                }


            }

        } catch (Exception e) {
            e.printStackTrace();
        }


    }


    /**
     * 微博登录
     * @Author: liuk
     * @Date: 2018-4-23 17:34:214
     */
    @RequestMapping("weiBoLogin")
    public void weiBoLogin(Long siteId,HttpServletRequest request, HttpServletResponse response){
        ThirdLoginMgrEO weiBo = thirdLoginMgrService.getMgrInfoByType(siteId,ThirdLoginMgrEO.Type.WeiBo.toString());
        String appId = weiBo.getAppId();//获取后台配置的应用id

        HttpSession session = request.getSession();
        session.setAttribute("siteId",siteId);//将siteId放入session中，后边获取qq用户信息的时候用
        //微博登录跳转地址
        String url = weiBoAuthorizeURL.trim() + "?client_id="+ appId + "&redirect_uri="+ weiBoRedirectURI.trim()+ "&response_type=code&forcelogin=true";
        response.setContentType("text/html;charset=utf-8");
        try {
            response.sendRedirect(url);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 微博登录回调
     * @Author: liuk
     * @Date: 2018-4-23 17:34:214
     */
    @RequestMapping("weiBoLoginCallback")
    public void weiBoLoginCallback(String code,HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("text/html; charset=utf-8");
        try {
            HttpSession session = request.getSession();
            Long siteId = (Long)session.getAttribute("siteId");
            ThirdLoginMgrEO qq = thirdLoginMgrService.getMgrInfoByType(siteId,ThirdLoginMgrEO.Type.WeiBo.toString());
            String appId = qq.getAppId();//获取后台配置的应用id
            String appSecret = qq.getAppSecret();//获取后台配置的应用id

            if(!AppUtil.isEmpty(code)){//登录成功
                weibo4j.http.AccessToken accessToken = WeiBoLoginUtil.getAccessTokenByCode(code,weiBoAccessTokenURL,appId,appSecret,weiBoRedirectURI);
                String token = accessToken.getAccessToken();
                String uid = accessToken.getUid();
                session.setAttribute("weiBo_openID", uid);//将uid放入session中，绑定用户的时候会用到


                weibo4j.Users um = new weibo4j.Users(token);
                weibo4j.model.User user = um.showUserById(uid);
                String name = "",img="";
                if (user != null) {
                    name = user.getScreenName();
                    img = user.getAvatarLarge();
                }

                MemberSessionVO sessionMember = (MemberSessionVO) request.getSession().getAttribute("member");

                Map<String,Object> param = new HashMap<String,Object>();
                param.put("weiBoOpenId",uid);
                param.put("siteId",siteId);
                param.put("recordStatus", AMockEntity.RecordStatus.Normal.toString());
                MemberEO memberEO = memberService.getEntity(MemberEO.class,param);
                if(memberEO!=null){//已经绑定过用户

                    //会员中心添加绑定账号时，如果登录QQ号已经与其他会员账号绑定，则提示添加绑定出错
                    if(sessionMember!=null&&memberEO.getId().intValue()!=sessionMember.getId().intValue()){
                        response.getWriter().write("<script>alert('此微博账号已绑定其他会员账号');" +
                                "location.href='"+memberCenterUrl+"';</script>");
//                        response.sendRedirect(memberCenterUrl);//重定向到会员中心
                    }else{
                        memberEO.setWeiBoName(name);//更新昵称
                        memberEO.setWeiBoImg(img);//更新头像
                        //设置最后登录ip 和 最后登录时间
                        String ip = RequestUtil.getIpAddr(request);
                        memberEO.setIp(ip);
                        Integer loginTimes = (memberEO.getLoginTimes() == null ? 0 : memberEO.getLoginTimes());
                        memberEO.setLastLoginDate(new Date());
                        memberEO.setLoginTimes(loginTimes);
                        memberService.updateEntity(memberEO);
                        //设置session
                        sessionMember = new MemberSessionVO();
                        BeanUtils.copyProperties(memberEO, sessionMember);
                        request.getSession().setAttribute("member", sessionMember);
                        response.sendRedirect(memberCenterUrl);//重定向到会员中心
                     }
                }else{
                    if(sessionMember!=null&&sessionMember.getId()!=null){//用户已登录
                        memberEO = memberService.getEntity(MemberEO.class,sessionMember.getId());
                        if(AppUtil.isEmpty(memberEO.getWeiBoOpenId())){//添加绑定时直接与当前登录用户绑定
                            memberEO.setWeiBoOpenId(uid);//保存每个微博对应的唯一标识
                            memberEO.setWeiBoName(name);//保存微博昵称
                            memberEO.setWeiBoImg(img);//保存微博头像地址
                            memberService.updateEntity(memberEO);
                            sessionMember = new MemberSessionVO();
                            BeanUtils.copyProperties(memberEO, sessionMember);
                            session.setAttribute("member", sessionMember);
                            response.sendRedirect(memberCenterUrl);//重定向到会员中心
                        }else if(memberEO.getWeiBoOpenId().equals(uid)){//微博绑定的账号与当前账号一致，直接跳到会员中心
                            response.sendRedirect(memberCenterUrl);//重定向到会员中心
                        }else{//跳到绑定界面
                            session.setAttribute("weiBo_img", img);
                            session.setAttribute("weiBo_name", name);
                            response.sendRedirect(thirdBindURL +"?name_="+ java.net.URLEncoder.encode(name,"utf-8")+"&img="+img+"&type_=WeiBo");//重定向到用户绑定界面
                        }

                    }else{
                        session.setAttribute("weiBo_name", name);
                        session.setAttribute("weiBo_img", img);
                        response.sendRedirect(thirdBindURL +"?name_="+java.net.URLEncoder.encode(name,"utf-8")+"&img="+img+"&type_=WeiBo");//重定向到用户绑定界面
                    }
                }
            }else{
                response.sendRedirect(memberCenterUrl);//重定向到会员中心
            }

        } catch (WeiboException e) {
            if(401 == e.getStatusCode()){
                logger.info("Unable to get the access token.");
            }else{
                e.printStackTrace();
            }
        }

    }




    /**
     * 绑定已有账号
     * @Author: liuk
     * @Date: 2018-4-23 17:34:214
     */
    @RequestMapping("bindThirdLogin")
    @ResponseBody
    public Object bindThirdLogin(String uid, String password, String checkCode, Long siteId,String type,
                               HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("text/html; charset=utf-8");
        String webCode = (String) request.getSession().getAttribute("webCode");
        if (StringUtils.isEmpty(checkCode) || StringUtils.isEmpty(webCode)) {
            return ajaxErr("验证码不能为空！");
        }
        if (!checkCode.trim().toLowerCase().equals(webCode.toLowerCase())) {
            return ajaxErr("验证码不正确，请重新输入");
        }

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
            //设置最后登录ip 和 最后登录时间
            String ip = RequestUtil.getIpAddr(request);
            if(ThirdLoginMgrEO.Type.QQ.toString().equals(type)){//QQ绑定
                String openId = (String)session.getAttribute("qq_openID");
                String name = (String)session.getAttribute("qq_name");
                String img = (String)session.getAttribute("qq_img");
                if(!AppUtil.isEmpty(member.getQqOpenId())){
                    return ajaxErr("当前账号已绑定其他QQ号，请重新输入账号密码");
                }
                member.setQqOpenId(openId);//保存每个QQ对应的唯一标识
                member.setQqName(name);//保存QQ昵称
                member.setQqImg(img);//保存QQ头像地址

            }else if(ThirdLoginMgrEO.Type.WeChat.toString().equals(type)){//绑定微信
                String openId = (String)session.getAttribute("weChat_openID");
                String name = (String)session.getAttribute("weChat_name");
                String img = (String)session.getAttribute("weChat_img");
                if(!AppUtil.isEmpty(member.getWeChatOpenId())){
                    return ajaxErr("当前账号已绑定其他微信号，请重新输入账号密码");
                }
                member.setWeChatOpenId(openId);//保存每个WeChat对应的唯一标识
                member.setWeChatName(name);//保存WeChat昵称
                member.setWeChatImg(img);//保存WeChat头像地址
            }else if(ThirdLoginMgrEO.Type.WeiBo.toString().equals(type)){//绑定微博
                String openId = (String)session.getAttribute("weiBo_openID");
                String name = (String)session.getAttribute("weiBo_name");
                String img = (String)session.getAttribute("weiBo_img");
                if(!AppUtil.isEmpty(member.getWeiBoOpenId())){
                    return ajaxErr("当前账号已绑定其他微博，请重新输入账号密码");
                }
                member.setWeiBoOpenId(openId);
                member.setWeiBoName(name);
                member.setWeiBoImg(img);
            }

            member.setIp(ip);
            Integer loginTimes = (member.getLoginTimes() == null ? 0 : member.getLoginTimes());
            member.setLoginTimes(loginTimes);
            member.setLastLoginDate(new Date());
            memberService.updateEntity(member);
            //设置session
            MemberSessionVO sessionMember = new MemberSessionVO();
            BeanUtils.copyProperties(member, sessionMember);
            request.getSession().setAttribute("member", sessionMember);

        } catch (Exception e) {
            return ajaxErr("系统异常");
        } finally {
        }

        return getObject();
    }


    /**
     * 注册新账号并账号
     * @Author: liuk
     * @Date: 2018-4-23 17:34:214
     */
    @RequestMapping("registerAndBindThirdLogin")
    @ResponseBody
    public Object registerAndBindThirdLogin(MemberVO memberVO, String checkCode, String type,
                                          HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("text/html; charset=utf-8");
        if (StringUtils.isEmpty(checkCode)) {
            return ajaxErr("验证码不能为空！");
        }

        HttpSession session = request.getSession(true);

        if(AppUtil.isEmpty(memberVO.getSiteId())){
            memberVO.setSiteId((Long)session.getAttribute("siteId"));
        }

        Object obj = session.getAttribute("webCode");
        if (obj == null) {
            return ajaxErr("验证码已经失效！");
        }
        String webCode = (String) obj;
        if (!checkCode.trim().toLowerCase().equals(webCode.toLowerCase())) {
            return ajaxErr("验证码不正确，请重新输入");
        }
        if (memberVO.getSiteId() == null || StringUtils.isEmpty(memberVO.getUid())
                || StringUtils.isEmpty(memberVO.getPassword())) {
            return ajaxErr("[所属站点、注册账号、密码]不能为空!");
        }
        Boolean hasUid = memberService.isExistUid(memberVO.getUid(), memberVO.getSiteId(), null);
        if (hasUid) {
            return ajaxErr("账号已经存在，请重新输入~");
        }
        MemberEO member = new MemberEO();
        String password = memberVO.getPassword();
        BeanUtils.copyProperties(memberVO, member);
        member.setName(member.getUid());//将账号设置成用户姓名
        member.setStatus(MemberEO.Status.Enabled.getStatus());
        member.setPlainpw(password);
        member.setPassword(DigestUtils.md5Hex(password));

        //设置最后登录ip 和 最后登录时间
        String ip = RequestUtil.getIpAddr(request);
        if(ThirdLoginMgrEO.Type.QQ.toString().equals(type)){//QQ绑定
            String openId = (String)session.getAttribute("qq_openID");
            String img = (String)session.getAttribute("qq_img");
            String name = (String)session.getAttribute("qq_name");
            member.setQqOpenId(openId);
            member.setQqName(name);
            member.setQqImg(img);
            member.setImg(img);
        }else if(ThirdLoginMgrEO.Type.WeChat.toString().equals(type)){//绑定微信
            String openId = (String)session.getAttribute("weChat_openID");
            String img = (String)session.getAttribute("weChat_img");
            String name = (String)session.getAttribute("weChat_name");
            member.setWeChatOpenId(openId);
            member.setWeChatName(name);
            member.setWeChatImg(img);
            member.setImg(img);
        }else if(ThirdLoginMgrEO.Type.WeiBo.toString().equals(type)){//绑定微博
            String openId = (String)session.getAttribute("weiBo_openID");
            String name = (String)session.getAttribute("weiBo_name");
            String img = (String)session.getAttribute("weiBo_img");
            member.setWeiBoOpenId(openId);
            member.setWeiBoName(name);
            member.setWeiBoImg(img);
            member.setImg(img);
        }
        member.setIp(ip);
        Integer loginTimes = 1;
        member.setLoginTimes(loginTimes);
        member.setLastLoginDate(new Date());
        memberService.saveEntity(member);
        //设置session
        MemberSessionVO sessionMember = new MemberSessionVO();
        BeanUtils.copyProperties(member, sessionMember);
        request.getSession().setAttribute("member", sessionMember);

        return getObject();
    }



    /**
     * 绑定已有账号
     * @Author: liuk
     * @Date: 2018-4-23 17:34:214
     */
    @RequestMapping("unBindThirdLogin")
    @ResponseBody
    public Object unBindThirdLogin(String type,HttpServletRequest request){
        MemberSessionVO sessionMember = (MemberSessionVO) request.getSession().getAttribute("member");
        if(sessionMember!=null){
            Long memberId = sessionMember.getId();
            if(AppUtil.isEmpty(memberId)){
                return ajaxErr("系统异常，请联系管理员");
            }else{
                MemberEO memberEO = memberService.getEntity(MemberEO.class,memberId);
                if(memberEO!=null){
                    if(ThirdLoginMgrEO.Type.QQ.toString().equals(type)){//解绑qq
                        memberEO.setQqOpenId(null);
                        memberEO.setQqName(null);
                        memberEO.setQqImg(null);
                    }else if(ThirdLoginMgrEO.Type.WeChat.toString().equals(type)){//解绑微信
                        memberEO.setWeChatOpenId(null);
                        memberEO.setWeChatImg(null);
                        memberEO.setWeChatName(null);
                    }else if(ThirdLoginMgrEO.Type.WeiBo.toString().equals(type)){//解绑新浪微博
                        memberEO.setWeiBoOpenId(null);
                        memberEO.setWeiBoName(null);
                        memberEO.setWeiBoImg(null);
                    }
                    memberService.updateEntity(memberEO);
                    //设置session
                    sessionMember = new MemberSessionVO();
                    BeanUtils.copyProperties(memberEO, sessionMember);
                    request.getSession().setAttribute("member", sessionMember);
                }
            }
        }else{
            return ajaxErr("登录超时，请重新登录");
        }

        return getObject();
    }

}
