package cn.lonsun.system.systemlogo.util;


import cn.lonsun.core.base.entity.AMockEntity;
import cn.lonsun.core.base.util.StringUtils;
import cn.lonsun.core.util.SpringContextHolder;
import cn.lonsun.system.systemlogo.internal.entity.SystemLogoEO;
import cn.lonsun.system.systemlogo.internal.service.ISystemLogoService;
import cn.lonsun.util.LoginPersonUtil;
import org.springframework.ui.Model;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by zhangchao on 2016/7/25.
 */
public class SystemLogoUtil {

    public static String indexImg = "";
    //登录图片
    public static String loginImg = "";
    //系统图片
    public static String  systemImg = "";

    private static ISystemLogoService systemLogoService = SpringContextHolder.getBean("systemLogoService");

    public static void setLogo(Model m){
        if(StringUtils.isEmpty(indexImg) || StringUtils.isEmpty(loginImg) || StringUtils.isEmpty(systemImg)) {
            Map<String, Object> params = new HashMap<String, Object>();
            params.put("recordStatus", AMockEntity.RecordStatus.Normal.toString());
            List<SystemLogoEO> imgs = systemLogoService.getEntities(SystemLogoEO.class, params);
            SystemLogoEO logo = null;
            if (imgs != null && imgs.size() > 0) {
                logo = imgs.get(0);
                SystemLogoUtil.indexImg = logo.getIndexImg();
                SystemLogoUtil.loginImg = logo.getLoginImg();
                SystemLogoUtil.systemImg = logo.getSystemImg();
            }
        }
        m.addAttribute("indexImg",indexImg);
        m.addAttribute("loginImg",loginImg);
        m.addAttribute("systemImg",systemImg);
    }
}
