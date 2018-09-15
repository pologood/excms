package cn.lonsun.special.controller;

import cn.lonsun.GlobalConfig;
import cn.lonsun.cache.client.CacheHandler;
import cn.lonsun.common.util.AppUtil;
//import cn.lonsun.content.internal.service.IBaseContentService;
import cn.lonsun.content.internal.service.IBaseContentSpecialService;
import cn.lonsun.core.base.controller.BaseController;
import cn.lonsun.core.base.util.StringUtils;
import cn.lonsun.core.util.Pagination;
import cn.lonsun.core.util.SpringContextHolder;
import cn.lonsun.indicator.internal.entity.IndicatorEO;
import cn.lonsun.mongodb.entity.ContentMongoEO;
import cn.lonsun.mongodb.service.ContentMongoServiceImpl;
import cn.lonsun.site.contentModel.internal.entity.ContentModelEO;
import cn.lonsun.site.contentModel.internal.entity.ModelTemplateEO;
//import cn.lonsun.site.contentModel.internal.service.IModelTemplateService;
import cn.lonsun.site.contentModel.internal.service.IModelTemplateSpecialService;
import cn.lonsun.site.site.internal.entity.ColumnMgrEO;
//import cn.lonsun.site.site.internal.service.IColumnConfigService;
import cn.lonsun.site.site.internal.service.IColumnConfigSpecialService;
import cn.lonsun.site.template.internal.entity.TemplateConfEO;
import cn.lonsun.site.template.internal.entity.TemplateHistoryEO;
//import cn.lonsun.site.template.internal.service.ITplConfService;
import cn.lonsun.site.template.internal.service.ITplConfSpecialService;
//import cn.lonsun.site.template.internal.service.ITplHistoryService;
import cn.lonsun.site.template.internal.service.ITplHistorySpecialService;
import cn.lonsun.special.internal.entity.SpecialEO;
import cn.lonsun.special.internal.entity.SpecialSkinsEO;
import cn.lonsun.special.internal.entity.SpecialThemeEO;
import cn.lonsun.special.internal.service.ISpecialMaterialService;
import cn.lonsun.special.internal.service.ISpecialService;
import cn.lonsun.special.internal.service.ISpecialSkinsService;
import cn.lonsun.special.internal.service.ISpecialThemeService;
import cn.lonsun.special.internal.vo.SpecialMaterialQueryVO;
import cn.lonsun.special.internal.vo.SpecialThemeQueryVO;
import cn.lonsun.special.util.SimpleLabelParseUtil;
import cn.lonsun.util.LoginPersonUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.ui.velocity.VelocityEngineFactoryBean;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.List;

/**
 * Created by doocal on 2016-10-12.
 */
@Controller
@RequestMapping(value = "design")
public class DesignController extends BaseController {

    private static VelocityEngineFactoryBean factory = SpringContextHolder.getBean(VelocityEngineFactoryBean.class);
    private static VelocityEngine engine = factory.getObject();

    @Autowired
    private ITplConfSpecialService tplConfSpecialService;

    @Autowired
    private ITplHistorySpecialService tplHistorySpecialService;

    @Autowired
    private ContentMongoServiceImpl contentMongoService;

    @Autowired
    private ISpecialSkinsService specialSkinsService;

    @Autowired
    private ISpecialService specialService;

    @Autowired
    private ISpecialThemeService specialThemeService;

    @Autowired
    private ISpecialMaterialService specialMaterialService;

    @Autowired
    private IBaseContentSpecialService baseContentSpecialService;

    @Autowired
    private IColumnConfigSpecialService columnConfigSpecialService;

    @Autowired
    private IModelTemplateSpecialService modelTemplateSpecialService;

    private final static GlobalConfig config = SpringContextHolder.getBean(GlobalConfig.class);

    /*@ModelAttribute
    public void modelMap(HttpServletRequest request, ModelMap modelMap) {
        // 当前选中站点id
        Long siteId = LoginPersonUtil.getSiteId();
        modelMap.put("site", CacheHandler.getEntity(IndicatorEO.class, siteId));
    }*/

    @RequestMapping("index")
    public Object index(@RequestParam(defaultValue = "0") Long specialId, ModelMap modelMap) {
        Long siteId = LoginPersonUtil.getSiteId();
        modelMap.put("site", CacheHandler.getEntity(IndicatorEO.class, siteId));
        return "/design/index";
    }

    /**
     * 根据模板ID获取模板
     *
     * @param tplId
     * @param modelMap
     * @return
     */
    @RequestMapping("getTplContentById")
    public Object getTplContentById(@RequestParam(defaultValue = "0") Long tplId, ModelMap modelMap) {

        ContentMongoEO eo = contentMongoService.queryById(tplId);
        String templateContent = "";
        if (!AppUtil.isEmpty(eo)) {
            templateContent = eo.getContent();
            //因暂时模板解析未打成JAR包，此处简单处理include标签
            templateContent = SimpleLabelParseUtil.parseLabel(templateContent);

            VelocityContext velocityContext = new VelocityContext();// 构建上下文
            try {
                StringWriter writer = new StringWriter();
                boolean success = engine.evaluate(velocityContext, writer, "String", templateContent);// 解析
                templateContent = writer.toString();
            } catch (Throwable e) {
                return ajaxErr("参数错误！");
            }
            //templateContent = templateContent.replaceAll("[ls", "");
        }

        modelMap.put("designSpecial", "designSpecial");
        modelMap.put("templateContent", templateContent);

        return "/design/index_iframe";
    }

    /**
     * 根据模板类型获取模板项
     *
     * @param specialId
     * @param tplType
     * @param modelMap
     * @return
     */
    @RequestMapping("getTplItem")
    @ResponseBody
    public Object getTplItem(@RequestParam(defaultValue = "0") Long specialId, @RequestParam(defaultValue = "column") String tplType, ModelMap modelMap) {

        List<TemplateConfEO> list = tplConfSpecialService.getSpecialById(LoginPersonUtil.getSiteId(), specialId, tplType);

        return getObject(list);
    }

    /**
     * 根据专题返回样式列表
     *
     * @param specialId
     * @param modelMap
     * @return
     */
    @RequestMapping("getSpecileSkins")
    @ResponseBody
    public Object getSpecileSkins(@RequestParam(defaultValue = "0") Long specialId, ModelMap modelMap) {

        /**
         * 专题信息
         */
        SpecialEO specialEO = specialService.getEntity(SpecialEO.class, specialId);

        List<SpecialSkinsEO> list = null;
        if (null != specialEO) {
            list = specialSkinsService.getSkinsItem(LoginPersonUtil.getSiteId(), specialEO.getThemeId());
            if (!AppUtil.isEmpty(specialEO)) {
                for (int i = 0, l = list.size(); i < l; i++) {
                    list.get(i).setDefaults(0);
                    if (list.get(i).getPath().equals(specialEO.getDefaultSkin())) {
                        list.get(i).setDefaults(1);
                    }
                }
            }
        }

        return getObject(list);
    }

    /**
     * 根据专题返回样式列表
     *
     * @param specialId
     * @param modelMap
     * @return
     */
    @RequestMapping("updateDefaultSkins")
    @ResponseBody
    public Object updateDefaultSkins(@RequestParam(defaultValue = "0") Long specialId, String skins, ModelMap modelMap) {
        if (AppUtil.isEmpty(skins)) {
            return ajaxErr("skins 参数不能为空！");
        }
        SpecialEO specialEO = specialService.getEntity(SpecialEO.class, specialId);
        specialEO.setDefaultSkin(skins);
        specialService.saveEntity(specialEO);
        return getObject();
    }

    /**
     * 根据模板ID保存内容
     *
     * @param mongoId
     * @param content
     * @param map
     * @return
     */
    @RequestMapping("saveTplContent")
    @ResponseBody
    public Object saveTplContent(@RequestParam(defaultValue = "0") Long mongoId, String content, ModelMap map) {

        if (mongoId == 0) {
            return ajaxErr("参数错误！");
        }

        //根据模板项的ID存入模板内容\21
        ContentMongoEO tplMongoEO = new ContentMongoEO();
        tplMongoEO.setId(mongoId);
        tplMongoEO.setContent(content);
        tplMongoEO.setType("Special");
        TemplateHistoryEO eo = tplHistorySpecialService.saveTplContent(tplMongoEO);

        return getObject();
    }


    /**
     * 保存模板自定义样式
     *
     * @param tplId
     * @param style
     * @param map
     * @return
     */
    @ResponseBody
    @RequestMapping("/saveSpecialModuleSytle")
    public Object saveSpecialModuleSytle(@RequestParam(defaultValue = "0") Long tplId, String style, ModelMap map) {
        if (tplId == 0) {
            return ajaxErr("参数错误！");
        }
        TemplateConfEO eo = tplConfSpecialService.getEntity(TemplateConfEO.class, tplId);
        eo.setSpecialModuleSytle(style);
        tplConfSpecialService.updateEntity(eo);

        return getObject();
    }


    @RequestMapping("mobileIndex")
    public String mobileIndex() {
        return "/design/mobile_index";
    }

    /**
     * 根据获取素材
     *
     * @param queryVO
     * @return
     */
    @RequestMapping("getBackgroundMaterial")
    public Object getBackgroundMaterial(SpecialMaterialQueryVO queryVO) {
        Pagination pagination = specialMaterialService.getPagination(queryVO);
        return getObject(pagination);
    }

    /**
     * 根据获取素材
     *
     * @param queryVO
     * @return
     */
    @RequestMapping(value="previewThumbnail", method = {RequestMethod.POST,RequestMethod.GET})
    public Object previewThumbnail(SpecialThemeQueryVO queryVO, ModelMap map, String imgPath) {
        if(org.apache.commons.lang3.StringUtils.isNotEmpty(imgPath)){
            map.put("data", imgPath);
        }else{
            if (AppUtil.isEmpty(queryVO.getId())) {//为空
                return ajaxErr("参数为空！");
            }
            queryVO.setSiteId(LoginPersonUtil.getSiteId());
            SpecialThemeEO specialThemeEO = specialThemeService.getSpecialThumb(queryVO);
            map.put("data", specialThemeEO.getImgPath());
        }
        return "/design/preview_thumbnail";
    }

    /**
     * 保存栏目信息
     *
     * @param mapList
     * @return
     */
    @ResponseBody
    @RequestMapping("saveColumn")
    public Object saveColumn(String mapList) {
        if (StringUtils.isEmpty(mapList)) {//为空
            return ajaxErr("参数为空！");
        }
        JSONArray jsonarray = JSON.parseArray(mapList);
        int size = jsonarray.size();
        ColumnMgrEO columnMgrEO = new ColumnMgrEO();
        for (int i = 0; i < size; i++) {
            JSONObject jsonObject = jsonarray.getJSONObject(i);
            if (!jsonObject.containsKey("columnName")) {
                return ajaxErr("缺失栏目名称参数，对象下标（" + i + "）！");
            }
            String columnName = jsonObject.get("columnName").toString();
            if (StringUtils.isEmpty(columnName)) {
                return ajaxErr("栏目名称不能为空，对象下标（" + i + "）！");
            }
            if (jsonObject.containsKey("columnId") && !StringUtils.isEmpty(jsonObject.get("columnId").toString())) {//只修改栏目名称
                Long columnId = Long.valueOf(jsonObject.get("columnId").toString());
                columnMgrEO = CacheHandler.getEntity(ColumnMgrEO.class, columnId);
                if (null == columnMgrEO) {
                    return ajaxErr("修改操作栏目不存在，对象下标（" + i + "）！");
                }
                columnMgrEO.setName(columnName);//修改栏目名称
                columnConfigSpecialService.saveEO(columnMgrEO);
            } else if (jsonObject.containsKey("parentId") && !StringUtils.isEmpty(jsonObject.get("parentId").toString())) {//新增
                Long parentId = Long.valueOf(jsonObject.get("parentId").toString());
                Long count = baseContentSpecialService.getCountByColumnId(parentId);
                if (null != count && count > 0L) {
                    return ajaxErr("该栏目下有内容，不能添加子栏目，对象下标（" + i + "）！");
                }
                //添加一个默认的内容模型
                ModelTemplateEO tpl = modelTemplateSpecialService.getFirstModelByColumnCode("articleNews");
                if (null == tpl) {
                    return ajaxErr("没有配置栏目类型为新闻的内容模型，对象下标（" + i + "）！");
                }
                ContentModelEO contentModelEO = CacheHandler.getEntity(ContentModelEO.class, tpl.getModelId());
                if (null == contentModelEO) {
                    return ajaxErr("栏目类型为新闻的内容模型不存在或已被删除，对象下标（" + i + "）！");
                }
                Integer num = columnConfigSpecialService.getNewSortNum(parentId, false);//获得排序值
                columnMgrEO.setSortNum(num);
                columnMgrEO.setName(columnName);
                columnMgrEO.setParentId(parentId);
                columnMgrEO.setType(IndicatorEO.Type.CMS_Section.toString());
                columnMgrEO.setColumnTypeCode("articleNews");
                columnMgrEO.setContentModelCode(contentModelEO.getCode());
                Long columnId = columnConfigSpecialService.saveEO(columnMgrEO);
                jsonObject.put("columnId", columnId.toString());
            } else {
                return ajaxErr("缺失参数columnId或parentId，对象下标（" + i + "）！");
            }
        }
        return getObject(jsonarray);//返回json
    }

    /**
     * 保存模板自定义样式信息
     *
     * @param specialId
     * @param styleList
     * @return
     */
    @ResponseBody
    @RequestMapping("saveCustomStyleList")
    public Object saveCustomStyleList(@RequestParam(defaultValue = "0") Long specialId, String styleList) {

        if (specialId == 0) {
            return ajaxErr("参数为空！");
        }

        SpecialEO specialEO = specialService.getEntity(SpecialEO.class, specialId);
        specialEO.setId(specialId);
        specialEO.setStyleList(styleList);

        specialService.updateEntity(specialEO);
        return getObject();

    }


    /**
     * 保存模板自定义背景图片路径
     *
     * @param specialId
     * @param pageBackground
     * @return
     */
    @ResponseBody
    @RequestMapping("savePageBackground")
    public Object savePageBackground(@RequestParam(defaultValue = "0") Long specialId, String pageBackground) {

        if (specialId == 0) {
            return ajaxErr("参数为空！");
        }

        SpecialEO specialEO = specialService.getEntity(SpecialEO.class, specialId);
        specialEO.setPageBackground(pageBackground);

        specialService.updateEntity(specialEO);
        return getObject();

    }

    /**
     * 获取模板自定义背景图片路径
     *
     * @param specialId
     * @return
     */
    @ResponseBody
    @RequestMapping("getPageBackground")
    public Object getPageBackground(@RequestParam(defaultValue = "0") Long specialId) {

        if (specialId == 0) {
            return ajaxErr("参数为空！");
        }

        String pageBackground = "";
        SpecialEO specialEO = specialService.getEntity(SpecialEO.class, specialId);
        if (!AppUtil.isEmpty(specialEO)) {
            pageBackground = specialEO.getPageBackground();
        }

        return getObject(pageBackground);

    }

    /**
     * 保存模板自定义样式信息
     *
     * @param specialId
     * @return
     */
    @ResponseBody
    @RequestMapping("getSpecialEO")
    public Object getSpecialEO(@RequestParam(defaultValue = "0") Long specialId) {

        if (specialId == 0) {
            return ajaxErr("参数为空！");
        }

        SpecialEO specialEO = specialService.getEntity(SpecialEO.class, specialId);
        return getObject(specialEO);
    }


    /**
     * 获取可用组件列表
     *
     * @param specialId
     * @param modelMap
     * @return
     */
    @RequestMapping("getComponentList")
    @ResponseBody
    public Object getComponentList(@RequestParam(defaultValue = "0") Long specialId, ModelMap modelMap) {

        /**
         * 专题信息
         */
        SpecialEO specialEO = specialService.getEntity(SpecialEO.class, specialId);

        String componentsList = specialEO.getComponents();

        return getObject(componentsList);
    }

    /**
     * 保存可用组件列表
     *
     * @param specialId
     * @param modelMap
     * @return
     */
    @RequestMapping("saveComponentList ")
    @ResponseBody
    public Object saveComponentList(@RequestParam(defaultValue = "0") Long specialId, String componentsList, ModelMap modelMap) {
        if (AppUtil.isEmpty(specialId)) {
            return ajaxErr("specialId 参数不能为空！");
        }

        SpecialEO specialEO = specialService.getEntity(SpecialEO.class, specialId);
        specialEO.setComponents(componentsList);
        specialService.saveEntity(specialEO);
        return getObject();
    }


    /**
     * 检测文件是否存在
     *
     * @param specialId
     * @param filename
     * @param modelMap
     * @return
     */
    @RequestMapping("checkFile")
    @ResponseBody
    public Object checkFile(@RequestParam(defaultValue = "0") Long specialId, String filename, ModelMap modelMap) {
        if (AppUtil.isEmpty(specialId)) {
            return ajaxErr("specialId 参数不能为空！");
        }
        if (AppUtil.isEmpty(filename)) {
            return ajaxErr("filename 参数不能为空！");
        }

        SpecialEO specialEO = specialService.getEntity(SpecialEO.class, specialId);
        Long themeId = specialEO.getThemeId();
        SpecialThemeEO specialThemeEO = specialThemeService.getEntity(SpecialThemeEO.class, themeId);
        String path = specialThemeEO.getPath();//获取专题对应主题文件的存放的文件夹名称
        if (!new File(config.getSpecialPath() + "/" + path).exists()) {
            return ajaxErr("文件不存在");
        } else {
            if (filename.equals("resource.zip")) {//压缩包文件在根目录
                if (!new File(config.getSpecialPath() + "/" + path.trim() + ".zip").exists()) {
                    return ajaxErr("文件不存在");
                }
            } else if (filename.contains(".psd")) {//图片文件
                if (!new File(config.getSpecialPath() + "/" + path.trim() + "/imgs/" + filename).exists()) {
                    return ajaxErr("文件不存在");
                }
            } else {
                return ajaxErr("文件不存在");
            }

//            else if(filename.contains(".html")||filename.contains(".xml")||filename.contains(".md")){//html文件在path文件夹下
//                if(!new File(config.getSpecialPath()+"/"+path.trim()+"/"+filename).exists()){
//                    return ajaxErr("文件不存在");
//                }
//            }else if(filename.contains(".css")){//css文件
//                if(!new File(config.getSpecialPath()+"/"+path.trim()+"/css/"+filename).exists()){
//                    return ajaxErr("文件不存在");
//                }
//            }
        }

        return getObject();
    }


    /**
     * 文件下载
     *
     * @param specialId
     * @param filename
     * @param response
     * @return
     */
    @RequestMapping("downFile")
    @ResponseBody
    public Object downFile(@RequestParam(defaultValue = "0") Long specialId, String filename, HttpServletResponse response) {
        if (AppUtil.isEmpty(specialId)) {
            return ajaxErr("specialId 参数不能为空！");
        }
        if (AppUtil.isEmpty(filename)) {
            return ajaxErr("filename 参数不能为空！");
        }

        SpecialEO specialEO = specialService.getEntity(SpecialEO.class, specialId);
        Long themeId = specialEO.getThemeId();
        SpecialThemeEO specialThemeEO = specialThemeService.getEntity(SpecialThemeEO.class, themeId);
        String path = specialThemeEO.getPath();//获取专题对应主题文件的存放的文件夹名称
        String filepath;
        if (filename.equals("resource.zip")) {//压缩包文件在根目录
            filepath = config.getSpecialPath() + "/" + path.trim() + ".zip";

        } else if (filename.contains(".psd")) {//图片文件
            filepath = config.getSpecialPath() + "/" + path.trim() + "/imgs/" + filename;
        } else {
            return ajaxErr("文件不存在");
        }

        try {
            // path是指欲下载的文件的路径。
            File file = new File(filepath);

            // 以流的形式下载文件。
            InputStream fis = new BufferedInputStream(new FileInputStream(filepath));
            byte[] buffer = new byte[fis.available()];
            fis.read(buffer);
            fis.close();
            // 清空response
            response.reset();
            // 设置response的Header
            response.addHeader("Content-Disposition", "attachment;filename=" + new String(filename.getBytes()));
            response.addHeader("Content-Length", "" + file.length());
            OutputStream toClient = new BufferedOutputStream(response.getOutputStream());
            response.setContentType("application/octet-stream");
            toClient.write(buffer);
            toClient.flush();
            toClient.close();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return getObject();
    }

}
