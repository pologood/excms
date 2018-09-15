package cn.lonsun.system.globalconfig.controller;

import cn.lonsun.cache.client.CacheHandler;
import cn.lonsun.common.util.AppUtil;
import cn.lonsun.core.base.controller.BaseController;
import cn.lonsun.core.util.Pagination;
import cn.lonsun.shiro.util.PermissionCacheLoader;
import cn.lonsun.system.globalconfig.internal.entity.GlobConfigCateEO;
import cn.lonsun.system.globalconfig.internal.entity.GlobConfigItemEO;
import cn.lonsun.system.globalconfig.internal.service.IGlobConfigCateService;
import cn.lonsun.system.globalconfig.internal.service.IGlobConfigItemService;
import cn.lonsun.system.globalconfig.vo.CacheVO;
import cn.lonsun.system.globalconfig.vo.ConfigVO;
import cn.lonsun.util.GlobalConfigUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Hewbing
 * @ClassName: GlobConfigController
 * @Description: 全局配置控制层
 * @date 2015年8月25日 上午10:25:34
 */
@Controller
@RequestMapping(value = "globConfig", produces = {"application/json;charset=UTF-8"})
public class GlobConfigController extends BaseController {
    private Logger logger = LoggerFactory.getLogger(GlobConfigController.class);
    @Autowired
    private IGlobConfigItemService globConfigItemService;
    @Autowired
    private IGlobConfigCateService globConfigCateService;

    /**
     * @return
     * @Description 跳转全局配置主页
     * @author Hewbing
     * @date 2015年8月28日 下午2:18:26
     */
    @RequestMapping("index")
    public String index() {
        return "/system/gloconfig/config_index";
    }

    /**
     * @param map
     * @return
     * @Description 进入剧本配置页
     * @author Hewbing
     * @date 2015年8月28日 下午2:19:20
     */
    @RequestMapping("baseConfig")
    public String baseConfig(ModelMap map) {
        map.put("UploadConfig", globConfigItemService.getListByCateKey("upload_config"));
        map.put("SMSCenter", globConfigItemService.getListByCateKey("sms_center"));
        return "/system/gloconfig/baseConfig";
    }

    /**
     * @return
     * @Description 缓存页
     * @author Hewbing
     * @date 2015年8月31日 下午2:19:48
     */
    @RequestMapping("cacheMgr")
    public String cacheMgr() {
        return "/system/gloconfig/cacheMgr";
    }

    /**
     * @param map
     * @return
     * @Description 安全设置页
     * @author Hewbing
     * @date 2015年8月28日 下午2:20:09
     */
    @RequestMapping("limitIP")
    public String sysSafeSet(ModelMap map) {
        map.put("AllowIp", globConfigItemService.getListByCateKey("allow_ip"));
        map.put("BanIp", globConfigItemService.getListByCateKey("ban_ip"));
        return "/system/gloconfig/limitIP";
    }

    /**
     * @param map
     * @return
     * @Description 安全设置添加页
     * @author Doocal
     */
    @RequestMapping("limitIPAdd")
    public String securityIP_Add(ModelMap map) {
        return "/system/gloconfig/limitIP_add";
    }

    /**
     * @param map
     * @return
     * @Description 用户认证设置
     * @author Hewbing
     * @date 2015年8月31日 下午2:20:26
     */
    @RequestMapping("userAuthSet")
    public String userAuthSet(ModelMap map) {
        GlobConfigCateEO userAuth = globConfigCateService.getGlobConfigCateByCode("user_auth");
        map.put("UserAuth", userAuth);
        map.put("AuthItem", globConfigItemService.getListByCateKey("sso_auth"));
        return "/system/gloconfig/userAuthSet";
    }

    /**
     * @param config
     * @return
     * @Description 保存基本配置
     * @author Hewbing
     * @date 2015年8月31日 下午2:21:05
     */
    @RequestMapping("saveBaseConfig")
    @ResponseBody
    public Object saveBaseConfig(@RequestParam("config[]") String[] config) {

        System.out.println(config.length);
        for (int i = 0; i < config.length; i++) {
            String[] str = new String[2];
            str = config[i].split("\\|");
            GlobConfigItemEO item = globConfigItemService.getEOByKey(str[0]);
            if (!AppUtil.isEmpty(item)) {
                item.setValue(str[1]);
                globConfigItemService.saveEntity(item);
            }
        }
        return getObject();
    }

    /**
     * @param itemEO
     * @return
     * @Description 保存IP限制编辑
     * @author Hewbing
     * @date 2015年8月31日 下午2:21:35
     */
    @RequestMapping("saveIpConfig")
    @ResponseBody
    public Object saveIpConfig(GlobConfigItemEO itemEO) {
        Long ret = itemEO.getItemId();
        if (!validateIp(itemEO.getValue())) {
            return ajaxErr("IP格式不正确");
        }
        if ("allow_ip".equals(itemEO.getCateKey())) {
            List<GlobConfigItemEO> ap = globConfigItemService.getEOByKey2(itemEO.getValue(), "allow_ip");
            if (ap.size() > 0) {
                return ajaxErr("该IP已添加！");
            }
            List<GlobConfigItemEO> bp = globConfigItemService.getEOByKey2(itemEO.getValue(), "ban_ip");
            if (bp.size() > 0) {
                return ajaxErr("该IP已被禁止！");
            }
            itemEO.setDataType("string");
            itemEO.setItemName("允许ip");
            itemEO.setCateId(globConfigCateService.getGlobConfigCateByKey(itemEO.getCateKey()).getCategoryId());
        } else if ("ban_ip".equals(itemEO.getCateKey())) {
            List<GlobConfigItemEO> ap = globConfigItemService.getEOByKey2(itemEO.getValue(), "ban_ip");
            if (ap.size() > 0) {
                return ajaxErr("该IP已添加！");
            }
            List<GlobConfigItemEO> bp = globConfigItemService.getEOByKey2(itemEO.getValue(), "allow_ip");
            if (bp.size() > 0) {
                return ajaxErr("该IP已被允许！");
            }
            itemEO.setDataType("string");
            itemEO.setItemName("禁止ip");
            itemEO.setCateId(globConfigCateService.getGlobConfigCateByKey(itemEO.getCateKey()).getCategoryId());
        } else {
            return ajaxErr("参数出错");
        }
        if (AppUtil.isEmpty(itemEO.getItemId())) {
            ret = globConfigItemService.saveEntity(itemEO);
        } else {
            globConfigItemService.updateEntity(itemEO);
        }
        return getObject(ret);
    }

    public boolean validateIp(String ip) {
        String ver = "^((?:(?:25[0-5]|2[0-4]\\d|((1\\d{2})|([1-9]?\\d)))\\.){3}(?:25[0-5]|2[0-4]\\d|((1\\d{2})|([1-9]?\\d))))$";
        Pattern p = Pattern.compile(ver);
        Matcher m = p.matcher(ip);
        boolean result = m.matches();
        return result;
    }


    @RequestMapping("updateIpConfig")
    @ResponseBody
    public Object updateIpConfig() {
        return globConfigCateService;
    }

    /**
     * @param ids
     * @return
     * @Description 删除安全登入设置
     * @author Hewbing
     * @date 2015年8月31日 下午2:22:12
     */
    @RequestMapping("deleteIpConfig")
    @ResponseBody
    public Object deleteIpConfig(@RequestParam("ids[]") Long[] ids) {
        globConfigItemService.delete(GlobConfigItemEO.class, ids);
        return getObject(ids);
    }

    /**
     * @param pageSize
     * @param pageIndex
     * @return
     * @Description 换取系统缓存
     * @author Hewbing
     * @date 2015年8月31日 下午2:22:42
     */
    @RequestMapping("getCachePage")
    @ResponseBody
    public Object getCachePage(Integer pageSize, Long pageIndex) {
        Pagination page = new Pagination();
        List<String> cachePage = CacheHandler.getCacheKeys();
        List<Object> cacheVO = new ArrayList<Object>();
        for (String c : cachePage) {
            CacheVO vo = new CacheVO();
            vo.setCacheKey(c);
            cacheVO.add(vo);
        }
        if (AppUtil.isEmpty(pageSize)) pageSize = 20;
        if (AppUtil.isEmpty(pageIndex)) pageIndex = 0L;
        page.setData(cacheVO);
        page.setTotal((long) cachePage.size());
        page.setPageIndex(pageIndex);
        page.setPageSize(pageSize);
        return getObject(page);
    }

    /**
     * @return
     * @Description 刷新所有缓存
     * @author Hewbing
     * @date 2015年8月31日 下午2:22:56
     */
    @RequestMapping("reloadAllCache")
    @ResponseBody
    public Object reloadAllCache() {
        CacheHandler.reloadAll(null);
        return getObject();
    }

    /**
     * @param cacheKey
     * @return
     * @Description 刷新指定缓存
     * @author Hewbing
     * @date 2015年8月31日 下午2:23:13
     */
    @RequestMapping("reloadCache")
    @ResponseBody
    public Object reloadCache(@RequestParam("cacheKey[]") String[] cacheKey) {
        for (int i = 0; i < cacheKey.length; i++) {
            CacheHandler.reload(cacheKey[i]);
        }
        //刷新权限配置
        PermissionCacheLoader.loadPermissionCache();
        return getObject();
    }

    /**
     * @param config
     * @param type
     * @return
     * @Description 保存用户认证设置
     * @author Hewbing
     * @date 2015年8月31日 下午2:23:43
     */
    @RequestMapping("saveAuthConfig")
    @ResponseBody
    public Object saveAuthConfig(@RequestParam("config[]") String[] config, String type) {
        globConfigItemService.saveUserAuthConfig(type, config);
        return getObject();

    }


    @RequestMapping("getIp")
    @ResponseBody
    public Object getIp(String type) {
        List<ConfigVO> config = GlobalConfigUtil.getUploadConfig(GlobalConfigUtil.UploadType.SIZE, GlobalConfigUtil.UploadType.PIC, GlobalConfigUtil.UploadType.AUDIO);
        return getObject(config);
    }
}
