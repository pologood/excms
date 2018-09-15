package cn.lonsun.core.base.util;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.UUID;

/**
 * 用于表单生成的token的管理类
 * Created by Dzl on 2015-3-12.
 * 在控制器中,打开From页面时,使用model.addAttribute("token",CSRFTokenManager.getTokenForSession(this.session)) ,然后在Form里添加一个<input type="hidden" name="token" value="${token}" />
 */
public final class CSRFTokenManager {

    static final String CSRF_PARAM_NAME="CSRFToken";
    public static final String CSRF_TOKEN4SESSION_ATTRNAME= CSRFTokenManager.class.getName()+".tokenval";
    public static String getTokenForSession(HttpSession session){
        String token=null;
        synchronized (session){
            token = (String)session.getAttribute(CSRF_TOKEN4SESSION_ATTRNAME);
            if(null==token){
                token = UUID.randomUUID().toString();
                session.setAttribute(CSRF_TOKEN4SESSION_ATTRNAME,token);
            }
        }
        return token;
    }

    public static String getTokenFromRequest(HttpServletRequest request){
        return request.getParameter(CSRF_PARAM_NAME);
    }
    private CSRFTokenManager(){}
}
