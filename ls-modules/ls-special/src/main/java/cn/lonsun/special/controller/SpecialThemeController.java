package cn.lonsun.special.controller;

import cn.lonsun.GlobalConfig;
import cn.lonsun.common.util.AppUtil;
import cn.lonsun.core.base.controller.BaseController;
import cn.lonsun.core.base.entity.AMockEntity;
import cn.lonsun.core.util.FileUtils;
import cn.lonsun.core.util.Pagination;
import cn.lonsun.core.util.SpringContextHolder;
import cn.lonsun.special.internal.entity.SpecialEO;
import cn.lonsun.special.internal.entity.SpecialThemeEO;
import cn.lonsun.special.internal.service.ISpecialDownloadLogService;
import cn.lonsun.special.internal.service.ISpecialService;
import cn.lonsun.special.internal.service.ISpecialSkinsService;
import cn.lonsun.special.internal.service.ISpecialThemeService;
import cn.lonsun.special.internal.service.impl.SpecialConfig;
import cn.lonsun.special.internal.service.impl.SpecialServiceImpl;
import cn.lonsun.special.internal.vo.SpecialThemeQueryVO;
import cn.lonsun.util.LoginPersonUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by doocal on 2016-10-15.
 */
@Controller
@RequestMapping(value = "/specialTheme")
public class SpecialThemeController extends BaseController {

    private final static Logger logger = LoggerFactory.getLogger(SpecialServiceImpl.class);

    private final static GlobalConfig config = SpringContextHolder.getBean(GlobalConfig.class);

    @Autowired
    private ISpecialThemeService specialThemeService;

    @Autowired
    private ISpecialService specialService;

    @Autowired
    private ISpecialSkinsService specialSkinsService;

    @Autowired
    private ISpecialDownloadLogService specialDownloadLogService;

    @RequestMapping("specialThemeList")
    public String specialThemeList(ModelMap map) {
        map.put("times", SpecialThemeQueryVO.TimesType.values());
        return "/special/specialtheme/special_theme_list";
    }

    @RequestMapping("specialThemeEdit")
    public String specialThemeEdit(Long id, ModelMap map) {
        map.put("id", id);
        return "/special/specialtheme/special_theme_edit";
    }

    /**
     * 根据ID获取专题主题
     *
     * @param id
     * @return
     */
    @RequestMapping("getSpecialTheme")
    @ResponseBody
    public Object getSpecialTheme(Long id) {
        SpecialThemeEO specialTheme = null;
        if (null == id) {
            specialTheme = new SpecialThemeEO();
        } else {
            specialTheme = specialThemeService.getEntity(SpecialThemeEO.class, id);
        }
        return getObject(specialTheme);
    }

    /**
     * 保存专题主题
     *
     * @param specialTheme
     * @return
     */
    @RequestMapping("saveSpecialTheme")
    @ResponseBody
    public Object saveSpecialTheme(SpecialThemeEO specialTheme) {
        if (AppUtil.isEmpty(specialTheme.getName())) {
            return ajaxErr("名称不能为空");
        }
        if (AppUtil.isEmpty(specialTheme.getPath())) {
            return ajaxErr("路径不能为空");
        }
        specialTheme.setSiteId(LoginPersonUtil.getSiteId());
        specialThemeService.saveSpecialTheme(specialTheme);
        return getObject();
    }

    /**
     * 获取分页数据
     *
     * @param queryVO
     * @return
     */
    @RequestMapping("getPagination")
    @ResponseBody
    public Object getPagination(SpecialThemeQueryVO queryVO) {
        //queryVO.setSiteId(LoginPersonUtil.getSiteId());
        Pagination pagination = specialThemeService.getPagination(queryVO);
        return getObject(pagination);
    }

    /**
     * 删除
     *
     * @param id
     * @return
     */
    @RequestMapping("deleteSpecialTheme")
    @ResponseBody
    public Object deleteSpecialTheme(Long id) {
        if (AppUtil.isEmpty(id)) {
            return ajaxErr("参数id不能为空");
        }

        SpecialEO special = specialService.getThemeById(id);
        if (!AppUtil.isEmpty(special)) {
            return ajaxErr("存在关联专题，不能删除此主题！");
        }

        //删除文件夹,和下载的压缩包
        SpecialThemeEO specialThemeEO = specialThemeService.getEntity(SpecialThemeEO.class, id);

        //删除样式
        specialSkinsService.deleteByThemeId(id);

        //删除主题
        specialThemeService.delete(SpecialThemeEO.class, id);

        //删除下载日志
        specialDownloadLogService.deleteByThemeId(id);

        try {
            FileUtils.deleteDirectory(config.getSpecialPath().concat(File.separator).concat(specialThemeEO.getPath()));
            FileUtils.delFile(config.getSpecialPath().concat(File.separator).concat(specialThemeEO.getPath()).concat(SpecialConfig.THEME_SUFFIX));
        } catch (Exception e) {
            e.printStackTrace();
        }

        return getObject();
    }

    /**
     * @return
     */
    @RequestMapping("getSpecialThemeList")
    @ResponseBody
    public Object getSpecialThemeList() {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("siteId", LoginPersonUtil.getSiteId());
        params.put("recordStatus", AMockEntity.RecordStatus.Normal.toString());
        return getObject(specialThemeService.getEntities(SpecialThemeEO.class, params));
    }

    /**
     * 获取压缩包缩略图
     *
     * @return
     */
    @RequestMapping("getSpecialThumb")
    @ResponseBody
    public Object getSpecialThumb(String path) {

        if (AppUtil.isEmpty(path)) {
            return ajaxErr("参数 path 不能为空");
        }

        return getObject(specialThemeService.saveSpecialThumb(path));
    }

}
