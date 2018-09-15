package cn.lonsun.core.util;

import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

/**
* @ClassName: ContextHolderUtils 
* @Description: (上下文工具类) 
* @author  
* @date  
*
 */
public class ContextHolderUtils {
	/**
	 * SpringMvc下获取request
	 * 
	 * @return
	 */
	public static HttpServletRequest getRequest() {
		HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();

        return request;


	}
	/**
	 * SpringMvc下获取session
	 * 
	 * @return
	 */
	public static HttpSession getSession() {
		HttpSession session = getRequest().getSession();
		return session;

	}
	/**
	 * 获取用户ID
	 * @Time 2014年10月3日 下午4:19:21
	 * @return
	 */
    public static Long getUserId() {
        return (Long)getSession().getAttribute("userId");
    }
    /**
     * 获取UID
     * @Time 2014年10月3日 下午4:19:35
     * @return
     */
    public static String getUid() {
        return (String)getSession().getAttribute("uid");
    }
    /**
     * 获取用户姓名
     * @Time 2014年10月3日 下午4:19:47
     * @return
     */
    public static String getPersonName() {
        return (String)getSession().getAttribute("personName");
    }
    /**
     * 获取组织名称
     * @Time 2014年10月3日 下午4:20:01
     * @return
     */
    public static String getOrganName() {
        return (String)getSession().getAttribute("organName");
    }
    /**
     * 获取组织ID
     * @Time 2014年10月3日 下午4:20:12
     * @return
     */
    public static Long getOrganId() {
        return (Long)getSession().getAttribute("organId");
    }
    
    /**
     * 获取用户信息ID
     * @Time 2014年10月3日 下午4:20:12
     * @return
     */
    public static Long getPersonId() {
    	return (Long)getSession().getAttribute("personId");
    }

    /**
     * 获取用户单位ID
     * @Time 2014年10月3日 下午4:20:12
     * @return
     */
    public static Long getUnitId() {
        return (Long)getSession().getAttribute("unitId");
    }

    /**
     * 获取用户单位名称
     * @Time 2014年10月3日 下午4:20:12
     * @return
     */
    public static String getUnitName() {
        return (String)getSession().getAttribute("unitName");
    }

}
