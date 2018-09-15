/*
 * KickUserRunnable.java         2016年6月22日 <br/>
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

package cn.lonsun.shiro.filter;

import cn.lonsun.core.util.SpringContextHolder;
import cn.lonsun.dwr.CMSScriptSessionListener;
import cn.lonsun.message.internal.entity.MessageReceiveEO;
import cn.lonsun.message.internal.entity.MessageSystemEO;
import cn.lonsun.shiro.session.OnlineSession;
import com.alibaba.fastjson.JSON;
import org.apache.shiro.session.Session;
import org.directwebremoting.ScriptBuffer;
import org.directwebremoting.ScriptSession;

import java.util.Collection;

/**
 * 踢出用户 <br/>
 *
 * @author fangtinghua <br/>
 * @version v1.0 <br/>
 * @date 2016年6月22日 <br/>
 */
public class KickUserRunnable implements Runnable {

    private Session currentSession;// 当前用户
    private Collection<Session> sessionList;// 用户列表
    private FormAuthenticationFilter formAuthenticationFilter = SpringContextHolder.getBean(FormAuthenticationFilter.class);

    /**
     * Creates a new instance of KickUserRunnable.
     *
     * @param currentSession
     * @param sessionList
     */
    public KickUserRunnable(Session currentSession, Collection<Session> sessionList) {
        super();
        this.currentSession = currentSession;
        this.sessionList = sessionList;
    }

    @Override
    public void run() {
        for (Session session : sessionList) {
            OnlineSession onlineSession = (OnlineSession) session;
            if (currentSession.equals(onlineSession)) {// 如果session列表中有和当前session一致的，踢出
                // 如果是电脑访问并且开启了登录限制的用户，则需要推送强制下线消息
            //    if (OnlineSession.DeviceType.PC.toString().equals(onlineSession.getDeviceType())) {
                    ScriptSession scriptSession = CMSScriptSessionListener.getScriptSessionByHttpSessionId(onlineSession.getId().toString());
                    // 执行推送
                    if (null != scriptSession) {
                        ScriptBuffer script = new ScriptBuffer();
                        MessageReceiveEO receiveEO = new MessageReceiveEO();
                        // 构建消息对象
                        MessageSystemEO messageSystemEO = new MessageSystemEO();
                        messageSystemEO.setMessageType(4L);// session强制下线
                        messageSystemEO.setContent("您的账号在其他地方登录，如非本人操作，请重新登录或联系管理员处理!");
                        messageSystemEO.setLink(formAuthenticationFilter.getLoginUrl());
                        receiveEO.setMessageSystemEO(messageSystemEO);
                        // 设置要调用的 js及参数
                        script.appendCall("indexMgr.reversCallBack", JSON.toJSON(receiveEO));
                        // 推送
                        scriptSession.addScript(script);
                    }
                //}
                session.setTimeout(0);// 设置session立即失效，即将其踢出系统
            }
        }
    }
}