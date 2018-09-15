package cn.lonsun.util;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import cn.lonsun.core.base.util.StringUtils;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.web.subject.WebSubject;
import org.apache.shiro.web.util.WebUtils;

import cn.lonsun.rbac.internal.entity.PersonEO;
import cn.lonsun.rbac.login.InternalAccount;

/**
 * Created by wss on 2015-9-21.
 */
public class LoginPersonUtil {
    /**
     * SpringMvc下获取request
     *
     * @return
     */
    public static HttpServletRequest getRequest() {
        return WebUtils.toHttp(((WebSubject) SecurityUtils.getSubject()).getServletRequest());
    }

    /**
     * SpringMvc下获取session
     *
     * @return
     */
    public static HttpSession getSession() {
        return getRequest().getSession();
    }

    /**
     * 设置session属性
     *
     * @return
     */
    public static void setAttribute(String key, Object value) {
        getSession().setAttribute(key, value);
    }

    /**
     * 获取session属性
     *
     * @return
     */
    public static <T> T getAttribute(Class<T> clazz, String key) {
        return cast(clazz, getSession().getAttribute(key));
    }

    /**
     * 获取用户ID
     *
     * @return
     */
    public static Long getUserId() {
        return getAttribute(Long.class, InternalAccount.USER_USERID);
    }

    /**
     * 获取UID
     *
     * @return
     */
    public static String getUserName() {
        return getAttribute(String.class, InternalAccount.USER_USERNAME);
    }

    /**
     * 获取用户姓名
     *
     * @return
     */
    public static String getPersonName() {
        return getAttribute(String.class, InternalAccount.PERSON_PERSONNAME);
    }

    /**
     * 获取用户
     *
     * @return
     */
    public static PersonEO getPerson() {
        return getAttribute(PersonEO.class, InternalAccount.PERSON_PERSON);
    }

    /**
     * 获取组织名称
     *
     * @return
     */
    public static String getOrganName() {
        return getAttribute(String.class, InternalAccount.ORGAN_ORGANNAME);
    }

    /**
     * 获取组织ID
     *
     * @return
     */
    public static Long getOrganId() {
        return getAttribute(Long.class, InternalAccount.ORGAN_ORGANID);
    }

    /**
     * 获取用户信息ID
     *
     * @return
     */
    public static Long getPersonId() {
        return getAttribute(Long.class, InternalAccount.PERSON_PERSONID);
    }

    /**
     * 获取用户单位ID
     *
     * @return
     */
    public static Long getUnitId() {
        return getAttribute(Long.class, InternalAccount.UNIT_UNITID);
    }

    /**
     * 获取用户单位名称
     *
     * @return
     */
    public static String getUnitName() {
        return getAttribute(String.class, InternalAccount.UNIT_UNITNAME);
    }

    /**
     * 获取站点ID
     *
     * @return
     */
    public static Long getSiteId() {
        return getAttribute(Long.class, InternalAccount.USER_SITEID);
    }

    /**
     * 获取站点ID
     *
     * @return
     */
    public static Long getPreSiteId() {
        return getAttribute(Long.class, InternalAccount.PRE_USER_SITEID);
    }

    /**
     * 判断是否为开发商
     * */
    public static boolean isRoot() {
        return InternalAccount.DEVELOPER_ADMIN_CODE.equals(getUserName());
    }

    /**
     * 判断是否为超管账号
     * */
    public static boolean isSuperAdmin() {
        return getAttribute(Boolean.class, InternalAccount.USER_ISSUPERADMIN);
    }

    /**
     * 判断是否为当前站点管理员账号
     * */
    public static boolean isSiteAdmin() {
        //如果是站管，并且管理当前站点
        if(getAttribute(Boolean.class, InternalAccount.USER_ISSITEADMIN)){
            String siteIds = getAttribute(String.class, InternalAccount.USER_MAINTENANCE_SITE);
            if(StringUtils.isEmpty(siteIds)){
                return false;
            }
            return siteIds.contains(","+String.valueOf(getSiteId())+",");
        }
        return false;
    }

    /**
     * 转型
     */
    private static <T> T cast(Class<T> clazz, Object obj) {
        if (null == obj && clazz.equals(Boolean.class)) {
            return clazz.cast(Boolean.FALSE);
        }
        return !clazz.isInstance(obj) ? null : clazz.cast(obj);
    }
}