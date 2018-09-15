package cn.lonsun.webservice.processEngine.util;


import cn.lonsun.webservice.processEngine.enums.SSOPlatform;
import cn.lonsun.webservice.processEngine.enums.SSOSessionKey;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

/**
 * Created by roc on 2016-7-18.
 */
public class SSOManager {
    private HttpServletRequest request;
    private SSOPlatform ssoPlatform;
    private Long id;


    public SSOManager(HttpServletRequest request) {
        this.request = request;
        HttpSession session = request.getSession();
        if(null !=session && null !=session.getAttribute(SSOSessionKey.type.name())){
            ssoPlatform = (SSOPlatform)session.getAttribute(SSOSessionKey.type.name());
        }
        if(null !=session && null !=ssoPlatform && !ssoPlatform.equals(SSOPlatform.GOA)){
            id = AppValueUtil.getLong(session.getAttribute("unitId"));
        }else if(null !=session && null !=session.getAttribute(SSOSessionKey.id.name())){
            id = Long.valueOf(session.getAttribute(SSOSessionKey.id.name()).toString());
        }
    }

    public SSOPlatform getSsoPlatform() {
        return ssoPlatform;
    }

    public Long getId() {
        return id;
    }
}
