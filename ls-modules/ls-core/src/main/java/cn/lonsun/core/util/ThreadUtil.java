package cn.lonsun.core.util;

import java.util.Map;

/**
 * 线程工具类
 * 
 * @author angela
 *
 */
public abstract class ThreadUtil {

    private ThreadUtil() {
        throw new Error();
    }

    public enum LocalParamsKey {
        /** 用户的ID */
        UserId,
        /** 用户的帐号 */
        Uid,
        /** 用户的姓名 */
        PersonName,
        /** 组织ID */
        OrganId,
        /** 单位名称 */
        OrganName,
        /** 请求回调函数 */
        Callback,
        /** 用于标识是否脱壳 */
        DataFlag,
        /** IP地址 */
        IP,
        /** 是否为IE6浏览器 */
        ConvertCN,
        /** 操作菜单id */
        OP_MenuId,
        /** 操作栏目id */
        OP_ColumnId,
        /** 操作单位id */
        OP_OrganId,
        /** 操作目录id */
        OP_CatId
    }

    public static final ThreadLocal<Map<String, Object>> session = new ThreadLocal<Map<String, Object>>();

    public static void set(Map<String, Object> map) {
        if (map != null && map.size() > 0) {
            session.set(map);
        }
    }

    /**
     * 获取ThreadLocal中的Object值
     * 
     * @param key
     * @return
     */
    public static Object get(LocalParamsKey key) {
        if (key == null || "".equals(key.toString())) {
            return null;
        }
        Map<String, Object> map = session.get();

        Object value = null;
        if (map != null && map.size() > 0) {
            value = map.get(key.toString());
        }
        return value;
    }

    /**
     * 获取ThreadLocal中的Long值
     * 
     * @param key
     * @return
     */
    public static Long getLong(LocalParamsKey key) {
        Object object = get(key);
        return object == null ? null : Long.valueOf(object.toString());
    }

    /**
     * 获取ThreadLocal中的Long值
     * 
     * @param key
     * @return
     */
    public static Integer getInteger(LocalParamsKey key) {
        Object object = get(key);
        return object == null ? null : Integer.valueOf(object.toString());
    }

    /**
     * 获取ThreadLocal中的String值
     * 
     * @param key
     * @return
     */
    public static String getString(LocalParamsKey key) {
        Object object = get(key);
        return object == null ? null : object.toString();
    }
}
