/*
 * LoginController.java         2015年8月10日 <br/>
 *
 * Copyright (c) 1994-1999 AnHui LonSun, Inc. <br/>
 * All rights reserved.	<br/>
 *
 * This software is the confidential and proprietary information of AnHui	<br/>
 * LonSun, Inc. ("Confidential Information").  You shall not	<br/>
 * disclose such Confidential Information and shall use it only in	<br/>
 * accordance with the terms of the license agreement you entered into	<br/>
 * with Sun. <br/>
 */

package cn.lonsun.rbac.controller;

import cn.lonsun.GlobalConfig;
import cn.lonsun.cache.client.CacheHandler;
import cn.lonsun.common.util.AppUtil;
import cn.lonsun.core.base.controller.BaseController;
import cn.lonsun.core.util.IpUtil;
import cn.lonsun.core.util.SpringContextHolder;
import cn.lonsun.rbac.internal.entity.PersonEO;
import cn.lonsun.rbac.login.InternalAccount;
import cn.lonsun.rbac.utils.ValidateCode;
import cn.lonsun.shiro.filter.MgrFormAuthenticationFilter;
import cn.lonsun.shiro.util.GeetestLib;
import cn.lonsun.shiro.security.UsernamePasswordToken;
import cn.lonsun.shiro.util.RSAUtils;
import cn.lonsun.system.systemlogo.util.SystemLogoUtil;
import cn.lonsun.util.LoginPersonUtil;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.codec.binary.Hex;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.web.util.WebUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.util.HashMap;
import java.util.Map;

/**
 * 用户登录 <br/>
 *
 * @author fangtinghua <br/>
 * @version v1.0 <br/>
 * @date 2015年8月10日 <br/>
 */
@Controller
@RequestMapping("/")
public class LoginController extends BaseController {

    @Value("${user.login.simple.code:false}")
    private boolean simpleCode;// 是否开启简单验证码
    @Value("${gee.geetest_id:gee.geetest_id}")
    private String geetest_id;
    @Value("${gee.geetest_key:gee.geetest_key}")
    private String geetest_key;
    @Value("${gee.gtServerStatusSessionKey:gee.gtServerStatusSessionKey}")
    private String gtServerStatusSessionKey;
    @Resource
    private MgrFormAuthenticationFilter filter;

    private static GlobalConfig globalConfig = SpringContextHolder.getBean(GlobalConfig.class);

    /**
     * 获取公钥
     *
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "getPublicKey")
    public Object getPublicKey() {
        Map<String, String> publicKeyMap = new HashMap<String, String>();
        RSAPublicKey publicKey = RSAUtils.getDefaultPublicKey();
        publicKeyMap.put("modulus", new String(Hex.encodeHex(publicKey.getModulus().toByteArray())));
        publicKeyMap.put("exponent", new String(Hex.encodeHex(publicKey.getPublicExponent().toByteArray())));
        return getObject(publicKeyMap);
    }

    /**
     * oa登录接口
     *
     * @param info
     * @return
     */
    @RequestMapping(value = "loginForOA")
    public void loginForOA(HttpServletRequest request, HttpServletResponse response, String info) throws IOException {// rsa加密信息
        RSAPrivateKey privateKey = RSAUtils.getDefaultPrivateKey();
        String userInfo = RSAUtils.decryptString(privateKey, info);
        if (StringUtils.isEmpty(userInfo)) {
            WebUtils.issueRedirect(request, response, filter.getLoginUrl());
        }
        String code = "_login_from_oa_";
        String[] arr = userInfo.split(",");
        Subject currentUser = SecurityUtils.getSubject();
        currentUser.getSession().setAttribute(InternalAccount.LOGIN_CODE, code);//模拟验证码
        UsernamePasswordToken token = new UsernamePasswordToken(arr[0], DigestUtils.md5Hex(arr[1]),"","","");
        currentUser.login(token);
        WebUtils.redirectToSavedRequest(request, response, filter.getSuccessUrl());
    }

    /**
     * 登录页
     *
     * @return
     * @author fangtinghua
     */
    @RequestMapping(value = "login")
    public String login(HttpServletRequest request, Model m, ModelMap mm) {
        SystemLogoUtil.setLogo(m);
        RSAPublicKey publicKey = RSAUtils.getDefaultPublicKey();
        mm.put("modulus", new String(Hex.encodeHex(publicKey.getModulus().toByteArray())));
        mm.put("exponent", new String(Hex.encodeHex(publicKey.getPublicExponent().toByteArray())));
        mm.put("version", globalConfig.getSoftVersion());
//        getStartCaptchaCode(request);
        if(AppUtil.isEmpty(geetest_id) || geetest_id.equals("gee.geetest_id")){
            return "system/login";
        }
        return "system/loginJY";
    }

    @ResponseBody
    @RequestMapping("getPersonInfo")
    public Object getPersonInfo() {
        Long personId = LoginPersonUtil.getPersonId();
        if (personId == null) {
            return getObject();
        }
        PersonEO personEO = CacheHandler.getEntity(PersonEO.class, personId);
        return getObject(personEO);
    }

    /**
     * 获取验证码
     *
     * @param request
     * @throws IOException
     */
   /* @ResponseBody
    @RequestMapping("/login/getCode")
    public void getCode(HttpServletRequest request, HttpServletResponse response) throws IOException {
        // 设置响应的类型格式为图片格式
        response.setContentType("image/jpeg");
        // 禁止图像缓存。
        response.setHeader("Pragma", "no-cache");
        response.setHeader("Cache-Control", "no-cache");
        response.setDateHeader("Expires", 0);
        HttpSession session = request.getSession();
        ValidateCode vCode = null;
        simpleCode=true;
        if (simpleCode) {
            vCode = new ValidateCode(108, 38, 4, 0);
        } else {
            vCode = new ValidateCode(108, 38, 4, 100);
        }
        session.setAttribute(InternalAccount.LOGIN_CODE, vCode.getCode());
        vCode.write(response.getOutputStream());
    }
*/
//    @ResponseBody
    //@RequestMapping("/login/getSmartCode")
    @RequestMapping("/login/getCode")
    public void getStartCaptchaCode(HttpServletRequest request, HttpServletResponse response) throws IOException{
        if(AppUtil.isEmpty(geetest_id) || geetest_id.equals("gee.geetest_id")){//如果没有配置则使用传统验证码
            // 设置响应的类型格式为图片格式
            response.setContentType("image/jpeg");
            // 禁止图像缓存。
            response.setHeader("Pragma", "no-cache");
            response.setHeader("Cache-Control", "no-cache");
            response.setDateHeader("Expires", 0);
            HttpSession session = request.getSession();
            ValidateCode vCode = null;
            simpleCode=true;
            if (simpleCode) {
                vCode = new ValidateCode(108, 38, 4, 0);
            } else {
                vCode = new ValidateCode(108, 38, 4, 100);
            }
            session.setAttribute(InternalAccount.LOGIN_CODE, vCode.getCode());
            vCode.write(response.getOutputStream());
            return;
        }
        GeetestLib gtSdk = new GeetestLib(geetest_id,geetest_key, true);
        String resStr = "{}";
        String userid = request.getSession().getId();
        //自定义参数,可选择添加
        HashMap<String, String> param = new HashMap<String, String>();
        param.put("user_id", userid); //网站用户id
        param.put("client_type", "web"); //web:电脑上的浏览器；h5:手机上的浏览器，包括移动应用内完全内置的web_view；native：通过原生SDK植入APP应用的方式
        param.put("ip_address", IpUtil.getIpAddr(request)); //传输用户请求验证时所携带的IP
        //进行验证预处理
        int gtServerStatus = gtSdk.preProcess(param);
        //将服务器状态设置到session中
        request.getSession().setAttribute(gtSdk.gtServerStatusSessionKey, gtServerStatus);
        //将userid设置到session中
        request.getSession().setAttribute("userid", userid);
        response.getWriter().write(gtSdk.getResponseStr());
    }
}