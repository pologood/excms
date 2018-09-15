/*
 * 版权所有 (C) 2001-2015 深圳市艾派应用系统有限公司。保留所有权利。
 * 版本：
 * 修改记录：
 *		1、2015-4-17，ypsong创建。
 */
package cn.lonsun.sms.czsms.util;

import java.util.HashMap;
import java.util.Map;

import cn.lonsun.core.base.util.StringUtils;
import cn.lonsun.core.util.SpringContextHolder;
import cn.lonsun.sms.czsms.vo.*;
import cn.lonsun.sms.internal.entity.SendSmsEO;
import cn.lonsun.sms.internal.service.ISendSmsService;
import cn.lonsun.sms.util.SmsPropertiesUtil;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.log4j.Logger;


public class SmsHttpClient
{

    private static ISendSmsService sendSmsService = SpringContextHolder.getBean("sendSmsService");

    private String enterAccount;
    private String userName;
    private String userPwd;
    private String srcExtCode;
    private int userType;
    private String httpUrl;
    private String sessionId;

    public final static String LOGIN_URL = SmsPropertiesUtil.czHttpUrl+"/login";
    public final static String SENDSMS_URL = SmsPropertiesUtil.czHttpUrl+"/submitSM";
    public final static String RECEIVESM_URL = SmsPropertiesUtil.czHttpUrl+"/receiveSM";
    public final static String STATUSREPORT_URL = SmsPropertiesUtil.czHttpUrl+"/statusReport";
    public final static String SENDSMSTEMPLATE_URL = SmsPropertiesUtil.czHttpUrl+"/submitSMTemplate";

    private Logger log = Logger.getLogger("SmsHttpClient");

    public StatusReportRet queryStatusReport(String sessionId, long msgId)
    {

        String loginUrl = this.httpUrl + STATUSREPORT_URL;
        Map m = new HashMap();
        m.put("sessionId", this.sessionId);
        m.put("msgId", msgId + "");
        String jsonRes = HttpTools.sendHttpReqToServer(loginUrl, m);
        JSONObject jo = JSONObject.fromObject(jsonRes);
        int status = jo.getInt("status");
        String desc = jo.getString("desc");
        long count = jo.getLong("count");
        StatusReportRet ret = new StatusReportRet();
        ret.setStatus(status);
        ret.setDesc(desc);
        if (status == RetStatus.E_NOTLOGIN)
        {
            // 重新登录
            boolean loginret = reLogin();
            if (loginret)
            {
                m.put("sessionId", this.sessionId);
                jsonRes = HttpTools.sendHttpReqToServer(loginUrl, m);
                jo = JSONObject.fromObject(jsonRes);
                status = jo.getInt("status");
                desc = jo.getString("desc");
                count = jo.getLong("count");
                ret.setStatus(status);
                ret.setDesc(desc);
            }
            else
            {
                // 登录失败
                ret.setStatus(RetStatus.E_NOTLOGIN);
                ret.setDesc("重登录失败，请稍后重试");
                return ret;
            }

        }
        if (status == 0 && count > 0)
        {
            JSONArray ja = jo.getJSONArray("data");
            if (ja != null && ja.size() > 0)
            {
                for (int i = 0; i < ja.size(); i++)
                {
                    MsgStatus dp = new MsgStatus();
                    dp.setPhone(ja.getJSONObject(i).getString("phone"));
                    dp.setStatusDesc(ja.getJSONObject(i)
                            .getString("statusDesc"));
                    ret.getStatusL().add(dp);
                }
            }
        }
        return ret;
    }

    public ReceiveSmRet receiveSm(String sessionId)
    {
        String loginUrl = this.httpUrl + RECEIVESM_URL;
        Map m = new HashMap();
        m.put("sessionId", this.sessionId);
        String jsonRes = HttpTools.sendHttpReqToServer(loginUrl, m);
        JSONObject jo = JSONObject.fromObject(jsonRes);
        int status = jo.getInt("status");
        String desc = jo.getString("desc");
        long count = jo.getLong("count");
        ReceiveSmRet ret = new ReceiveSmRet();
        ret.setStatus(status);
        ret.setDesc(desc);
        if (status == RetStatus.E_NOTLOGIN)
        {
            // 重新登录
            boolean loginret = reLogin();
            if (loginret)
            {
                m.put("sessionId", this.sessionId);
                jsonRes = HttpTools.sendHttpReqToServer(loginUrl, m);
                jo = JSONObject.fromObject(jsonRes);
                status = jo.getInt("status");
                desc = jo.getString("desc");
                count = jo.getLong("count");
                ret.setStatus(status);
                ret.setDesc(desc);
            }
            else
            {
                // 登录失败
                ret.setStatus(RetStatus.E_NOTLOGIN);
                ret.setDesc("重登录失败，请稍后重试");
                return ret;
            }

        }
        if (status == 0 && count > 0)
        {
            JSONArray ja = jo.getJSONArray("data");
            if (ja != null && ja.size() > 0)
            {
                for (int i = 0; i < ja.size(); i++)
                {
                    DeliverPO dp = new DeliverPO();
                    {
                        JSONObject item = new JSONObject();
                        dp.setDeliverID(ja.getJSONObject(i)
                                .getLong("deliverID"));
                        dp.setDestAddress(ja.getJSONObject(i).getString(
                                "destAddress"));
                        dp.setTimeStamp(ja.getJSONObject(i).getString(
                                "timeStamp"));
                        dp.setSrcAddress(ja.getJSONObject(i).getString(
                                "srcAddress"));
                        dp.setContent(ja.getJSONObject(i).getString("content"));
                        ret.getDeliverL().add(dp);
                    }
                }
            }
        }
        return ret;
    }

    public static SendSmsRet sendSms(String sessionId, String destAddr, String content)
    {
        String loginUrl = SENDSMS_URL;
        Map m = new HashMap();
        m.put("content", content);
        m.put("sessionId", sessionId);
        m.put("destAddr", destAddr);
        String jsonRes = HttpTools.sendHttpReqToServer(loginUrl, m);
        JSONObject jo = JSONObject.fromObject(jsonRes);
        int status = jo.getInt("status");
        String desc = jo.getString("desc");
        long msgId = jo.getLong("msgid");
        SendSmsRet ret = new SendSmsRet();
        ret.setStatus(status);
        ret.setDesc(desc);
        ret.setOnlyID(msgId);
        if (status == RetStatus.E_NOTLOGIN)
        {
//            // 重新登录
//            boolean loginret = reLogin();
//            if (loginret)
//            {
//                m.put("sessionId", this.sessionId);
//                jsonRes = HttpTools.sendHttpReqToServer(loginUrl, m);
//                jo = JSONObject.fromObject(jsonRes);
//                status = jo.getInt("status");
//                desc = jo.getString("desc");
//                ret.setStatus(status);
//                ret.setDesc(desc);
//            }
//            else
//            {
            // 登录失败
            ret.setStatus(RetStatus.E_NOTLOGIN);
            ret.setDesc("重登录失败，请稍后重试");
            return ret;
//            }

        }
        // if (status == 0)
        // {
        JSONArray ja = jo.getJSONArray("data");
        if (ja != null && ja.size() > 0)
        {
            for (int i = 0; i < ja.size(); i++)
            {
                int errorId = ja.getJSONObject(i).getInt("errorId");
                String errorDes = ja.getJSONObject(i).getString("errorDes");
                MsgError error = new MsgError();
                error.setErrorDesc(errorDes);
                error.setErrorCode(errorId);
                ret.getErrorList().add(error);
            }
        }
        // }
        return ret;
    }

    public SendSmsRet sendTemplateMsg(int templateId, String destAddr,
                                      String params)
    {
        String loginUrl = this.httpUrl + SENDSMSTEMPLATE_URL;
        Map m = new HashMap();
        m.put("templateId", templateId + "");
        m.put("paramcontent", params);
        m.put("sessionId", this.sessionId);
        m.put("destAddr", destAddr);
        String jsonRes = HttpTools.sendHttpReqToServer(loginUrl, m);
        JSONObject jo = JSONObject.fromObject(jsonRes);
        int status = jo.getInt("status");
        String desc = jo.getString("desc");
        long msgId = jo.getLong("msgid");
        SendSmsRet ret = new SendSmsRet();
        ret.setStatus(status);
        ret.setDesc(desc);
        if (status == RetStatus.E_NOTLOGIN)
        {
            // 重新登录
            boolean loginret = reLogin();
            if (loginret)
            {
                m.put("sessionId", this.sessionId);
                jsonRes = HttpTools.sendHttpReqToServer(loginUrl, m);
                jo = JSONObject.fromObject(jsonRes);
                status = jo.getInt("status");
                desc = jo.getString("desc");
                ret.setStatus(status);
                ret.setDesc(desc);
            }
            else
            {
                // 登录失败
                ret.setStatus(RetStatus.E_NOTLOGIN);
                ret.setDesc("重登录失败，请稍后重试");
                return ret;
            }

        }
        ret.setOnlyID(msgId);
        // if (status == 0)
        // {
        JSONArray ja = jo.getJSONArray("data");
        if (ja != null && ja.size() > 0)
        {
            for (int i = 0; i < ja.size(); i++)
            {
                int errorId = ja.getJSONObject(i).getInt("errorId");
                String errorDes = ja.getJSONObject(i).getString("errorDes");
                MsgError error = new MsgError();
                error.setErrorDesc(errorDes);
                error.setErrorCode(errorId);
                ret.getErrorList().add(error);
            }
        }
        // }
        return ret;
    }

    public static LoginRet userLogin(String enterAccount, String userName,
                                     String userPwd, String srcExtCode, int userType)
    {
//        this.enterAccount = enterAccount;
//        this.userName = userName;
//        this.userPwd = userPwd;
//        this.srcExtCode = srcExtCode;
//        this.userType = userType;
//        this.httpUrl = url;
        String loginUrl = LOGIN_URL;
        Map m = new HashMap();
        m.put("enterAccount", enterAccount);
        m.put("userName", userName);
        m.put("userPwd", userPwd);
        m.put("userType", "" + userType);
        m.put("srcExtCode", srcExtCode);
        String jsonRes = HttpTools.sendHttpReqToServer(loginUrl, m);
        JSONObject jo = JSONObject.fromObject(jsonRes);
        int status = jo.getInt("status");
        String desc = jo.getString("desc");
        String sessionId = "";
        if (status == 0)
        {
            sessionId = jo.getString("sessionId");
        }
        LoginRet ret = new LoginRet();
        ret.setDesc(desc);
        ret.setSessionId(sessionId);
        ret.setStatus(status);
        return ret;
    }

    private boolean reLogin()
    {
        boolean ret = false;
        log.info("开始重新登录");
        String loginUrl = this.httpUrl + LOGIN_URL;
        Map m = new HashMap();
        m.put("enterAccount", enterAccount);
        m.put("userName", userName);
        m.put("userPwd", userPwd);
        m.put("userType", userType+"");
        m.put("srcExtCode", srcExtCode);
        String jsonRes = HttpTools.sendHttpReqToServer(loginUrl, m);
        JSONObject jo = JSONObject.fromObject(jsonRes);
        int status = jo.getInt("status");
        String desc = jo.getString("desc");
        if (status == 0)
        {
            log.info("重新登录成功");
            this.sessionId = jo.getString("sessionId");
            ret=true;
        }
        else
        {
            log.info("重新登录失败：" + status + ":" + desc);
        }
        return ret;
    }



    /**
     * 发送短信
     * @param phone
     * @param msg
     * @return
     */
    public static Boolean sendSmsUtil(String phone,String msg,String code){
        Boolean isSend = false;
        SendSmsEO sms = new SendSmsEO();
        sms.setCode(code);
        sms.setPhone(phone);
        sms.setDesc(msg);
        try{
            //获取登录SessionId
            LoginRet ret= userLogin(SmsPropertiesUtil.czEnterAccount,
                    SmsPropertiesUtil.czUserName,SmsPropertiesUtil.czUserPwd,"",0);
            if(ret != null && !StringUtils.isEmpty(ret.getSessionId())){
                //发送短信
                SendSmsRet smsRet = sendSms(ret.getSessionId(),phone,msg);
                sms.setSessionId(ret.getSessionId());
                sms.setSmsStatus(SendSmsEO.SmsStatus.E_ERROR.getSmsStatus());
                if(smsRet != null){
                    sms.setSmsStatus(smsRet.getStatus());
                    if(smsRet.getStatus() == 0){isSend = true;}
                }
            }else{
                sms.setSmsStatus(SendSmsEO.SmsStatus.E_NOTLOGIN.getSmsStatus());
            }
        }catch (Exception e){
            sms.setSmsStatus(SendSmsEO.SmsStatus.E_ERROR.getSmsStatus());
            e.printStackTrace();
        }finally {
            sendSmsService.saveEntity(sms);
        }
        return isSend;
    }


}
