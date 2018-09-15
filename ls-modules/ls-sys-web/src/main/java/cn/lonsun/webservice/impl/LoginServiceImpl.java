/*
 * ShiroWebServiceImpl.java         2016年7月18日 <br/>
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

package cn.lonsun.webservice.impl;

import cn.lonsun.core.exception.BaseRunTimeException;
import cn.lonsun.core.exception.util.Jacksons;
import cn.lonsun.core.util.TipsMode;
import cn.lonsun.webservice.to.WebServiceTO;
import cn.lonsun.webservice.utils.WebServiceTOUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import cn.lonsun.rbac.login.InternalAccount;
import cn.lonsun.shiro.session.OnlineSession;
import cn.lonsun.shiro.util.OnlineSessionUtil;
import cn.lonsun.webservice.LoginService;
import cn.lonsun.webservice.vo.SessionVO;

/**
 * 用户鉴权 <br/>
 *
 * @date 2016年7月18日 <br/>
 * @author fangtinghua <br/>
 * @version v1.0 <br/>
 */
@Service("loginService")
public class LoginServiceImpl implements LoginService {

    @Override
    public WebServiceTO getAuthenticationInfo(String sessionId) {
        WebServiceTO to = new WebServiceTO();
        try {
            if (StringUtils.isEmpty(sessionId)) {
                throw new BaseRunTimeException(TipsMode.Message.toString(),"sessionId不能为空");
            }
            SessionVO vo = new SessionVO();
            OnlineSession onlineSession = OnlineSessionUtil.getSessionById(sessionId);
            vo.setUserId((Long) onlineSession.getAttribute(InternalAccount.USER_USERID));
            vo.setUid((String) onlineSession.getAttribute(InternalAccount.USER_UID));
            vo.setPersonId((Long) onlineSession.getAttribute(InternalAccount.PERSON_PERSONID));
            vo.setPersonName((String) onlineSession.getAttribute(InternalAccount.PERSON_PERSONNAME));
            vo.setOrganId((Long) onlineSession.getAttribute(InternalAccount.ORGAN_ORGANID));
            vo.setOrganName((String) onlineSession.getAttribute(InternalAccount.ORGAN_ORGANNAME));
            vo.setUnitId((Long) onlineSession.getAttribute(InternalAccount.UNIT_UNITID));
            vo.setUnitName((String) onlineSession.getAttribute(InternalAccount.UNIT_UNITNAME));
            vo.setSiteId((Long) onlineSession.getAttribute(InternalAccount.USER_SITEID));
            //将返回内容转换为json串
            String json = Jacksons.json().fromObjectToJson(vo);
            to.setJsonData(json);
        } catch (Exception e) {
            WebServiceTOUtil.setErrorInfo(to, e);
        }
        return to;
    }
}