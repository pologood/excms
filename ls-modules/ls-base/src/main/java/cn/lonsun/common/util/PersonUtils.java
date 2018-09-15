package cn.lonsun.common.util;

import javax.servlet.http.HttpSession;

import cn.lonsun.common.vo.PersonInfoVO;
import cn.lonsun.core.util.SessionUtil;

/**
 * 用户信息获取工具
 *
 * @author xujh
 * @version 1.0
 * 2014年12月17日
 *
 */
public class PersonUtils {
	
	/**
	 * 在session中获取UserInfoVO对象
	 *
	 * @param session
	 * @return
	 */
	public static PersonInfoVO getPersonInfo(HttpSession session){
		Long userId = SessionUtil.getLongProperty(session, "userId");
		String uid = SessionUtil.getStringProperty(session, "uid");
		Long personId = SessionUtil.getLongProperty(session, "personId");
        String personName = SessionUtil.getStringProperty(session, "personName");
        Long organId = SessionUtil.getLongProperty(session, "organId");
        String organName = SessionUtil.getStringProperty(session, "organName");
        Long unitId = SessionUtil.getLongProperty(session, "unitId");
        String unitName = SessionUtil.getStringProperty(session, "unitName");
        PersonInfoVO p = new PersonInfoVO();
        p.setUserId(userId);
        p.setUid(uid);
        p.setPersonId(personId);
        p.setPersonName(personName);
        p.setOrganId(organId);
        p.setOrganName(organName);
        p.setUnitId(unitId);
        p.setUnitName(unitName);
        return p;
	}
	
	/**
	 * 获取用户ID
	 *
	 * @param session
	 * @return
	 */
    public static Long getUserId(HttpSession session) {
        return SessionUtil.getLongProperty(session, "userId");
    }
    /**
     * 获取UID
     *
     * @param session
     * @return
     */
    public static String getUid(HttpSession session) {
        return SessionUtil.getStringProperty(session, "uid");
    }
    /**
     * 获取用户姓名
     *
     * @param session
     * @return
     */
    public static String getPersonName(HttpSession session) {
        return SessionUtil.getStringProperty(session, "personName");
    }
    /**
     * 获取组织名称
     *
     * @param session
     * @return
     */
    public static String getOrganName(HttpSession session) {
        return SessionUtil.getStringProperty(session, "organName");
    }
   /**
    * 获取组织ID
    *
    * @param session
    * @return
    */
    public static Long getOrganId(HttpSession session) {
        return SessionUtil.getLongProperty(session, "organId");
    }
    
    /**
     * 获取用户信息ID
     *
     * @param session
     * @return
     */
    public static Long getPersonId(HttpSession session) {
    	return SessionUtil.getLongProperty(session, "personId");
    }

    /**
     * 获取用户单位ID
     *
     * @param session
     * @return
     */
    public static Long getUnitId(HttpSession session) {
        return SessionUtil.getLongProperty(session, "unitId");
    }

    /**
     * 获取用户单位名称
     *
     * @param session
     * @return
     */
    public static String getUnitName(HttpSession session) {
        return SessionUtil.getStringProperty(session, "unitName");
    }

}
