package cn.lonsun.shiro.util;

import cn.lonsun.cache.client.CacheGroup;
import cn.lonsun.cache.client.CacheHandler;
import cn.lonsun.indicator.internal.entity.IndicatorEO;
import cn.lonsun.shiro.support.PermissionKey;
import org.apache.commons.lang3.StringUtils;

import java.util.*;

/**
 * @auth zhongjun
 * @createDate 2018-08-16 18:07
 */
public class PermissionCacheLoader {

    public static Map<String, String> permissionMap;


    /**
     * 判断是否需要检查权限
     * @param url
     * @return
     */
    public static boolean needCheckPermission(String url){
        if(permissionMap == null){
            loadPermissionCache();
        }
        return permissionMap.containsKey(url);
    }

    /**
     * 判断是否需要检查权限
     * @param url
     * @return
     */
    public static String getPermissionCode(String url){
        if(permissionMap == null){
            loadPermissionCache();
        }
        return permissionMap.get(url);
    }

    /**
     * 加载权限配置文件
     */
    public static void loadPermissionCache(){
        permissionMap = new Hashtable<String, String>();
        List<IndicatorEO> buttonList = CacheHandler.getList(IndicatorEO.class, CacheGroup.CMS_TYPE, IndicatorEO.Type.CMS_Button.toString());
        if(buttonList == null){
            return;
        }
        List<IndicatorEO> menuList = CacheHandler.getList(IndicatorEO.class, CacheGroup.CMS_TYPE, IndicatorEO.Type.CMS_Menu.toString());
        Map<Long, IndicatorEO> menuMap = list2Map(menuList);
        Set<Long> menuIds = new HashSet<Long>();
        for(IndicatorEO btn : buttonList){
            if(StringUtils.isEmpty(btn.getUri())){
                continue;
            }
            String code = permissionMap.get(btn.getUri());
            if(StringUtils.isEmpty(code)){
                code = String.format(PermissionKey.KEY_MENU_ID_BUTTON_CODE, btn.getParentId(), btn.getCode());
            }else{
                code += (";"+String.format(PermissionKey.KEY_MENU_ID_BUTTON_CODE, btn.getParentId(), btn.getCode()));
            }
            permissionMap.put(btn.getUri(), code);
            //加入菜单本身的链接
            if(!menuIds.contains(btn.getParentId())){
                IndicatorEO menu = menuMap.get(btn.getParentId());
                if(menu != null && StringUtils.isNotEmpty(menu.getUri())){
                    String m_code = permissionMap.get(menu.getUri());
                    if(StringUtils.isEmpty(m_code)){
                        m_code = String.format(PermissionKey.KEY_MENU_ID_BUTTON_CODE, menu.getIndicatorId(), url2Code(menu.getUri()));
                    }else{
                        m_code += (";"+String.format(PermissionKey.KEY_MENU_ID_BUTTON_CODE, menu.getIndicatorId(), url2Code(menu.getUri())));
                    }
                    permissionMap.put(menu.getUri(), m_code);
                }
            }

        }
    }

    public static String url2Code(String url){
        if(url.indexOf("/") >= 0){
            url = url.substring(1, url.length());
        }
        if(url.endsWith("/")){
            url = url.substring(0, url.length() - 1);
        }
        return "page_" + url.replaceAll("/", "_");
    }

    public  static  Map<Long, IndicatorEO> list2Map(List<IndicatorEO> list){
        Map<Long, IndicatorEO> result = new HashMap<Long, IndicatorEO>();
        if(list == null){
            return result;
        }
        for(IndicatorEO item : list){
            result.put(item.getIndicatorId(), item);
        }
        return result;
    }

}
