package cn.lonsun.special.internal.service.impl;

import cn.lonsun.GlobalConfig;
import cn.lonsun.cache.client.CacheHandler;
import cn.lonsun.common.util.AppUtil;
import cn.lonsun.common.util.FileUtil;
import cn.lonsun.content.internal.entity.BaseContentEO;
import cn.lonsun.core.base.service.impl.MockService;
import cn.lonsun.core.exception.BaseRunTimeException;
import cn.lonsun.core.util.Pagination;
import cn.lonsun.core.util.SpringContextHolder;
import cn.lonsun.core.util.TipsMode;
import cn.lonsun.indicator.internal.entity.IndicatorEO;
import cn.lonsun.mongodb.entity.ContentMongoEO;
import cn.lonsun.site.contentModel.internal.entity.ContentModelEO;
import cn.lonsun.site.contentModel.internal.service.IContentModelSpecialService;
import cn.lonsun.site.contentModel.vo.ContentModelVO;
import cn.lonsun.site.site.internal.entity.ColumnMgrEO;
import cn.lonsun.site.site.internal.entity.SiteConfigEO;
import cn.lonsun.site.site.internal.entity.SiteMgrEO;
import cn.lonsun.site.site.internal.service.IColumnConfigSpecialService;
import cn.lonsun.site.site.internal.service.ISiteConfigSpecialService;
import cn.lonsun.site.template.internal.entity.TemplateConfEO;
import cn.lonsun.site.template.internal.entity.TemplateHistoryEO;
import cn.lonsun.site.template.internal.service.ITplConfSpecialService;
import cn.lonsun.site.template.internal.service.ITplHistorySpecialService;
import cn.lonsun.special.internal.dao.ISpecialDao;
import cn.lonsun.special.internal.entity.SpecialEO;
import cn.lonsun.special.internal.entity.SpecialSkinsEO;
import cn.lonsun.special.internal.entity.SpecialThemeEO;
import cn.lonsun.special.internal.service.ISpecialService;
import cn.lonsun.special.internal.service.ISpecialSkinsService;
import cn.lonsun.special.internal.service.ISpecialThemeService;
import cn.lonsun.special.internal.vo.*;
import cn.lonsun.special.util.Dom4jUtil;
import cn.lonsun.system.datadictionary.vo.DataDictVO;
import cn.lonsun.util.DataDictionaryUtil;
import cn.lonsun.util.Dom4jUtils;
import cn.lonsun.util.LoginPersonUtil;
import com.alibaba.fastjson.JSONArray;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.*;

//import cn.lonsun.site.contentModel.internal.service.IContentModelService;
//import cn.lonsun.site.site.internal.service.IColumnConfigService;
//import cn.lonsun.site.site.internal.service.ISiteConfigService;
//import cn.lonsun.site.template.internal.service.ITplConfService;
//import cn.lonsun.site.template.internal.service.ITplHistoryService;

/**
 * @author Doocal
 * @title: ${todo}
 * @package cn.lonsun.special.internal.vo
 * @description: 代码根据原模块的业务逻辑编写，目前表设计方便存在一定的不合理性。
 * @date 2016/12/9
 */
@Service
public class SpecialServiceImpl extends MockService<SpecialEO> implements ISpecialService {

    private final static Logger logger = LoggerFactory.getLogger(SpecialServiceImpl.class);

    private final static GlobalConfig config = SpringContextHolder.getBean(GlobalConfig.class);

    private SpecialParseVO specialParseVO = new SpecialParseVO();

    @Autowired
    private ISpecialDao specialDao;

    @Autowired
    private ISpecialThemeService specialThemeService;

    @Autowired
    private ITplConfSpecialService tplConfSpecialService;

    @Autowired
    private ITplHistorySpecialService tplHistorySpecialService;

    @Autowired
    private IContentModelSpecialService contentModelSpecialService;

    @Autowired
    private IColumnConfigSpecialService columnConfigSpecialService;

    @Autowired
    private ISpecialSkinsService specialSkinsService;

    @Autowired
    private ISiteConfigSpecialService siteConfigSpecialService;

    /**
     * 获取字典项
     */
    private DataDictVO getDictVO(String pCode, String cCode) {
        //获取模板列表字典项
        DataDictVO dictVO = DataDictionaryUtil.getItem(pCode, cCode);
        if (AppUtil.isEmpty(dictVO.getId())) {
            throw new BaseRunTimeException(TipsMode.Message.toString(), "数据字典缺少专题模板项！");
        }
        return dictVO;
    }

    /**
     * 专题 ： 分析模板公共部分,写入模板表
     * <p>
     * 创建模板项并同时写入内容，根据setting.xml配置的TemplateCode 做为模板的唯一键（此值和模型的NavCode、NewsTplCode关联）
     * 用于下一步添加模型时候找到添加的模板的对象
     */
    public ISpecialService parseCommonTemplate() {

        /*String dictCode = "special";
        if (!AppUtil.isEmpty(specialEO.getSpecialType()) && specialEO.getSpecialType() == 1) {
            dictCode = "common";
        }*/

        //获取模板列表字典项
        DataDictVO dictVO = getDictVO("temp_type", "special");

        if (AppUtil.isEmpty(specialParseVO.getSpecialEO().getId())) {
            throw new BaseRunTimeException(TipsMode.Message.toString(), "获取主题ID失败！");
        }

        //创建模板项,根据当前选定主题，创建一个根目录
        TemplateConfEO rootEO = new TemplateConfEO();
        rootEO.setName(specialParseVO.getSpecialEO().getName());
        rootEO.setTempType("special");
        rootEO.setPid(dictVO.getId());
        rootEO.setSiteId(specialParseVO.getSpecialEO().getSiteId());
        rootEO.setType("Special");
        rootEO.setLeaf(1);
        rootEO.setSpecialId(specialParseVO.getSpecialEO().getId());
        rootEO = tplConfSpecialService.saveEO(rootEO).get(0);

        //构建模板对象，用于下一步建模板时使用
        SpecialTemplateVO rootVO = new SpecialTemplateVO();
        rootVO.setId(rootEO.getId());
        rootVO.setTemplateCode("special");
        rootVO.setTemplateType("special");
        specialParseVO.getTemplateMap().put("special", rootVO);

        //提取配置模板的节点
        List<Element> item = specialParseVO.getElementRoot().selectNodes(SpecialConfig.COMMONTEMPLATE_NODE);
        for (Element ele : item) {

            String title = ele.selectSingleNode("Title").getText();
            String fileName = ele.selectSingleNode("FileName").getText();
            String templateCode = ele.selectSingleNode("TemplateCode").getText();
            String templateType = ele.selectSingleNode("TemplateType").getText();
            if (fileName != null) {

                //构建文件路径
                File tplPath = new File(SpecialConfig.getUnzipFilePath(specialParseVO.getSpecialThemeEO().getPath()).concat(fileName));
                String tplContent = FileUtil.readFile(tplPath);

                //创建模板项
                TemplateConfEO tplEO = new TemplateConfEO();
                tplEO.setName(title);
                tplEO.setTempType(templateType);
                tplEO.setPid(rootEO.getId());
                tplEO.setSiteId(specialParseVO.getSpecialEO().getSiteId());
                tplEO.setType("Special");
                tplEO.setSpecialId(specialParseVO.getSpecialEO().getId());
                tplEO = tplConfSpecialService.saveEO(tplEO).get(0);

                //根据模板项的ID存入模板内容
                ContentMongoEO tplMongoEO = new ContentMongoEO();
                tplMongoEO.setId(tplEO.getId());
                tplMongoEO.setContent(tplContent);
                tplMongoEO.setType("Special");
                tplHistorySpecialService.saveTplContent(tplMongoEO);

                //构建模板对象，用于下一步建模型时使用
                SpecialTemplateVO vo = new SpecialTemplateVO();
                vo.setId(tplEO.getId());
                vo.setFileName(fileName);
                vo.setTemplateCode(templateCode);
                vo.setTemplateType(templateType);
                vo.setContent(tplContent);
                //以模板唯一编码做为主键
                //写入VO，用于后面方法取值
                specialParseVO.getTemplateMap().put(templateCode, vo);
            }

        }

        return this;
    }


    /**
     * 子站 ： 分析模板公共部分,写入模板表
     * <p>
     * 创建模板项并同时写入内容，根据setting.xml配置的TemplateCode 做为模板的唯一键（此值和模型的NavCode、NewsTplCode关联）
     * 用于下一步添加模型时候找到添加的模板的对象
     */
    public ISpecialService parseSiteCommonTemplate() {

        //获取模板列表字典项
        DataDictVO dictVO = getDictVO("temp_type", "common");

        if (AppUtil.isEmpty(specialParseVO.getSpecialEO().getId())) {
            throw new BaseRunTimeException(TipsMode.Message.toString(), "获取主题ID失败！");
        }

        //提取配置模板的节点
        List<Element> item = specialParseVO.getElementRoot().selectNodes(SpecialConfig.COMMONTEMPLATE_NODE);
        for (Element ele : item) {

            String title = ele.selectSingleNode("Title").getText();
            String fileName = ele.selectSingleNode("FileName").getText();
            String templateType = ele.selectSingleNode("TemplateType").getText();
            String templateCode = ele.selectSingleNode("TemplateCode").getText();
            if (fileName != null) {

                //构建文件路径
                File tplPath = new File(SpecialConfig.getUnzipFilePath(specialParseVO.getSpecialThemeEO().getPath()).concat(fileName));
                String tplContent = FileUtil.readFile(tplPath);

                //创建模板项
                TemplateConfEO tplEO = new TemplateConfEO();
                tplEO.setName(title);
                tplEO.setTempType(templateType);
                tplEO.setPid(dictVO.getId());
                tplEO.setSiteId(specialParseVO.getSpecialEO().getSiteId());
                tplEO.setType("Special");
                tplEO.setSpecialId(specialParseVO.getSpecialEO().getId());
                tplEO = tplConfSpecialService.saveEO(tplEO).get(0);

                //根据模板项的ID存入模板内容
                ContentMongoEO tplMongoEO = new ContentMongoEO();
                tplMongoEO.setId(tplEO.getId());
                tplMongoEO.setContent(tplContent);
                tplMongoEO.setType("Special");
                TemplateHistoryEO eo = tplHistorySpecialService.saveTplContent(tplMongoEO);

                //构建模板对象，用于下一步建模型时使用
                SpecialTemplateVO vo = new SpecialTemplateVO();
                vo.setId(tplEO.getId());
                vo.setFileName(fileName);
                vo.setTemplateCode(templateCode);
                vo.setTemplateType(templateType);
                vo.setContent(tplContent);

                //以模板唯一编码做为主键
                //写入VO，用于后面方法取值
                specialParseVO.getTemplateMap().put(templateCode, vo);
            }

        }

        return this;
    }

    /**
     * 创建专题虚拟模型模板
     * 创建专题虚拟栏目模板和文章页模板，只在第一次添加专题的时候写入
     * 虚拟栏目也要有模板，不然生成静态报错
     */
    public ISpecialService parseVirtualModelTemplate() {

        //解析添加模板
        if (!AppUtil.isEmpty(specialParseVO.getSpecialEO().getSpecialType()) && specialParseVO.getSpecialEO().getSpecialType() == 1) {
            parseSiteTemplate();
        } else {
            parseTemplate();
        }

        List<TemplateConfEO> tplList = tplConfSpecialService.getSpecialById(specialParseVO.getSpecialEO().getSiteId(), specialParseVO.getSpecialEO().getId(), SpecialConfig.tplCode.special_column.toString());

        //获取栏目（二级）模板对应字典项ID
        Long dictColumnPid = getDictVO("temp_type", "column").getId();
        if (AppUtil.isEmpty(dictColumnPid)) {
            throw new BaseRunTimeException(TipsMode.Message.toString(), "数据字典缺少专题模板项！");
        }
        //创建模板虚拟--栏目项
        TemplateConfEO tplColumnEO = new TemplateConfEO();
        tplColumnEO.setName("专题子站-虚拟模型模板");
        tplColumnEO.setTempType(SpecialConfig.tplCode.special_column.toString());
        tplColumnEO.setPid(0L);
        tplColumnEO.setSiteId(specialParseVO.getSpecialEO().getSiteId());
        tplColumnEO.setType("Special");
        tplColumnEO = tplConfSpecialService.saveEO(tplColumnEO).get(0);

        //构建模板对象，用于下一步建模型时使用
        SpecialTemplateVO specialTemplateVO_Column = new SpecialTemplateVO();
        specialTemplateVO_Column.setId(tplColumnEO.getId());
        specialTemplateVO_Column.setFileName("");
        specialTemplateVO_Column.setTemplateCode(SpecialConfig.tplCode.specialVirtual_Column.toString());
        specialTemplateVO_Column.setTemplateType(SpecialConfig.tplCode.specialVirtual_Column.toString());
        specialTemplateVO_Column.setContent("");
        //写入VO，用于后面方法取值
        specialParseVO.getTemplateMap().put(SpecialConfig.tplCode.specialVirtual_Column.toString(), specialTemplateVO_Column);

        //获取详细模板对应字典项ID
        Long dictContentPid = getDictVO("temp_type", "content").getId();
        if (AppUtil.isEmpty(dictContentPid)) {
            throw new BaseRunTimeException(TipsMode.Message.toString(), "数据字典缺少专题模板项！");
        }
        //创建模板虚拟--详细页
        TemplateConfEO templateConfEO = new TemplateConfEO();
        templateConfEO.setName("专题子站-虚拟模型模板");
        templateConfEO.setTempType(SpecialConfig.tplCode.special_content.toString());
        templateConfEO.setPid(0L);
        templateConfEO.setSiteId(specialParseVO.getSpecialEO().getSiteId());
        templateConfEO.setType("Special");
        templateConfEO = tplConfSpecialService.saveEO(templateConfEO).get(0);

        //构建模板对象，用于下一步建模型时使用
        SpecialTemplateVO specialTemplateVO_Content = new SpecialTemplateVO();
        specialTemplateVO_Content.setId(templateConfEO.getId());
        specialTemplateVO_Content.setFileName("");
        specialTemplateVO_Content.setTemplateCode(SpecialConfig.tplCode.specialVirtual_Content.toString());
        specialTemplateVO_Content.setTemplateType(SpecialConfig.tplCode.specialVirtual_Content.toString());
        specialTemplateVO_Content.setContent("");
        //写入VO，用于后面方法取值
        specialParseVO.getTemplateMap().put(SpecialConfig.tplCode.specialVirtual_Content.toString(), specialTemplateVO_Content);

        return this;
    }

    /**
     * 专题 : 分析模板,提取内容写入模板表
     * <p>
     * 创建模板项并同时写入内容，根据setting.xml配置的TemplateCode 做为模板的唯一键（此值和模型的NavCode、NewsTplCode关联）
     * 用于下一步添加模型时候找到添加的模板的对象
     */
    public ISpecialService parseTemplate() {

        //获取模板列表字典项
        DataDictVO dictVO = getDictVO("temp_type", "special");

        if (AppUtil.isEmpty(specialParseVO.getSpecialEO().getId())) {
            throw new BaseRunTimeException(TipsMode.Message.toString(), "获取主题ID失败！");
        }

        //如果公共目录已经创建根目录，此处不再创建
        Long rootId = 0L;
        if (specialParseVO.getTemplateMap().get("special") != null && AppUtil.isEmpty(specialParseVO.getTemplateMap().get("special").getId())) {
            //创建模板项
            TemplateConfEO rootEO = new TemplateConfEO();
            rootEO.setName(specialParseVO.getSpecialEO().getName());
            rootEO.setTempType("special");
            rootEO.setPid(dictVO.getId());
            rootEO.setSiteId(specialParseVO.getSpecialEO().getSiteId());
            rootEO.setType("Special");
            rootEO.setLeaf(1);
            rootId = tplConfSpecialService.saveEO(rootEO).get(0).getId();
        } else {
            rootId = specialParseVO.getTemplateMap().get("special").getId();
        }

        //构建模板根对象，用于下一步建模型时使用
        SpecialTemplateVO specialTemplateVO = new SpecialTemplateVO();
        specialTemplateVO.setId(rootId);

        specialParseVO.getTemplateMap().put("rootTemplateID", specialTemplateVO);

        //提取配置模板的节点
        List<Element> item = specialParseVO.getElementRoot().selectNodes(SpecialConfig.TEMPLATE_NODE);
        for (Element ele : item) {

            String title = ele.selectSingleNode("Title").getText();
            String fileName = ele.selectSingleNode("FileName").getText();
            String templateCode = ele.selectSingleNode("TemplateCode").getText();
            String templateType = ele.selectSingleNode("TemplateType").getText();
            if (fileName != null) {

                //构建文件路径
                File tplPath = new File(SpecialConfig.getUnzipFilePath(specialParseVO.getSpecialThemeEO().getPath()).concat(fileName));
                String tplContent = FileUtil.readFile(tplPath);

                //创建模板项
                TemplateConfEO tplEO = new TemplateConfEO();
                tplEO.setName(title);
                tplEO.setTempType(templateType);
                tplEO.setPid(rootId);
                tplEO.setSiteId(specialParseVO.getSpecialEO().getSiteId());
                tplEO.setType("Special");
                tplEO.setSpecialId(specialParseVO.getSpecialEO().getId());
                tplEO = tplConfSpecialService.saveEO(tplEO).get(0);

                //根据模板项的ID存入模板内容
                ContentMongoEO tplMongoEO = new ContentMongoEO();
                tplMongoEO.setId(tplEO.getId());
                tplMongoEO.setContent(tplContent);
                tplMongoEO.setType("Special");
                TemplateHistoryEO eo = tplHistorySpecialService.saveTplContent(tplMongoEO);

                //构建模板对象，用于下一步建模型时使用
                SpecialTemplateVO vo = new SpecialTemplateVO();
                vo.setId(tplEO.getId());
                vo.setFileName(fileName);
                vo.setTemplateCode(templateCode);
                vo.setTemplateType(templateType);
                vo.setContent(tplContent);

                //以模板唯一编码做为主键
                //写入VO，用于后面方法取值
                specialParseVO.getTemplateMap().put(templateCode, vo);
            }

        }

        return this;
    }

    /**
     * 专题 : 分析模板,提取内容写入模板表
     * <p>
     * 创建模板项并同时写入内容，根据setting.xml配置的TemplateCode 做为模板的唯一键（此值和模型的NavCode、NewsTplCode关联）
     * 用于下一步添加模型时候找到添加的模板的对象
     */
    public ISpecialService parseSiteTemplate() {

        if (AppUtil.isEmpty(specialParseVO.getSpecialEO().getId())) {
            throw new BaseRunTimeException(TipsMode.Message.toString(), "获取主题ID失败！");
        }

        //提取配置模板的节点
        List<Element> item = specialParseVO.getElementRoot().selectNodes(SpecialConfig.TEMPLATE_NODE);
        for (Element ele : item) {

            //TODO
            //以下节点取法对 setting.xml 配置相对严格，缺失配置项将直接抛出空指针异常
            String title = ele.selectSingleNode("Title").getText();
            String fileName = ele.selectSingleNode("FileName").getText();
            String templateType = ele.selectSingleNode("TemplateType").getText();
            String templateCode = ele.selectSingleNode("TemplateCode").getText();
            if (fileName != null) {

                //构建文件路径
                File tplPath = new File(SpecialConfig.getUnzipFilePath(specialParseVO.getSpecialThemeEO().getPath()).concat(fileName));
                String tplContent = FileUtil.readFile(tplPath);

                //创建模板项
                TemplateConfEO tplEO = new TemplateConfEO();
                tplEO.setName(title);
                tplEO.setTempType(templateType);
                if ("index".equals(templateType) || "search".equals(templateType)) {
                    tplEO.setPid(getDictVO("temp_type", "site_index").getId());
                    tplEO.setTempType("site_index");
                } else if ("column".equals(templateType)) {
                    tplEO.setPid(getDictVO("temp_type", "column").getId());
                } else {
                    tplEO.setPid(getDictVO("temp_type", "content").getId());
                }
                tplEO.setSiteId(specialParseVO.getSpecialEO().getSiteId());
                tplEO.setType("Special");
                tplEO.setSpecialId(specialParseVO.getSpecialEO().getId());
                tplEO = tplConfSpecialService.saveEO(tplEO).get(0);

                //根据模板项的ID存入模板内容
                ContentMongoEO tplMongoEO = new ContentMongoEO();
                tplMongoEO.setId(tplEO.getId());
                tplMongoEO.setContent(tplContent);
                tplMongoEO.setType("Special");
                tplHistorySpecialService.saveTplContent(tplMongoEO);

                //构建模板对象，用于下一步建模型时使用
                SpecialTemplateVO vo = new SpecialTemplateVO();
                vo.setId(tplEO.getId());
                vo.setFileName(fileName);
                vo.setTemplateCode(templateCode);
                vo.setTemplateType(templateType);
                vo.setContent(tplContent);

                //以模板唯一编码做为主键
                //写入VO，用于后面方法取值
                specialParseVO.getTemplateMap().put(templateCode, vo);
            }

        }

        return this;
    }

    /**
     * 分析模型
     * <p>
     * 根据配置的 NewsTplCode,NavCode 到 tplMap 找对应的项，并将自己的 ModelCode 做为 map 的key
     * 用于下一下添加栏目的要找的模型对象
     */
    public ISpecialService parseModel() {

        Map<String, SpecialModelVO> map = new HashMap<String, SpecialModelVO>();

        //取虚拟模型，为空就自动创建
        ContentModelEO contentModelEO = contentModelSpecialService.getByCode(SpecialConfig.modelCode.specialVirtual.toString());
        if (AppUtil.isEmpty(contentModelEO)) {
            //创建模型
            ContentModelVO modelVO = new ContentModelVO();
            modelVO.setName("专题子站-虚拟模块");
            modelVO.setModelTypeCode(SpecialConfig.modelCode.specialVirtual.toString());
            modelVO.setColumnTempId(specialParseVO.getTemplateMap().get(SpecialConfig.tplCode.specialVirtual_Column.toString()).getId());
            modelVO.setArticalTempId(specialParseVO.getTemplateMap().get(SpecialConfig.tplCode.specialVirtual_Content.toString()).getId());
            modelVO.setSiteId(specialParseVO.getSpecialEO().getSiteId());
            contentModelSpecialService.saveEO(modelVO);
        }

        List<SpecialModelVO> list = new ArrayList<SpecialModelVO>();
        //模型节点
        List<Element> item = specialParseVO.getElementRoot().selectNodes(SpecialConfig.MODEL_NODE);
        for (Element ele : item) {

            String title = ele.selectSingleNode("Title").getText();
            String columnType = ele.selectSingleNode("ColumnType").getText();
            String navCode = ele.selectSingleNode("NavCode").getText();
            String newsTplCode = ele.selectSingleNode("NewsTplCode").getText();
            String modelCode = ele.selectSingleNode("ModelCode").getText();

            if (title != null) {

                //根据规则，创建模型
                ContentModelVO modelVO = new ContentModelVO();
                if (!AppUtil.isEmpty(specialParseVO.getSpecialEO().getSpecialType()) && specialParseVO.getSpecialEO().getSpecialType() == 1) {
                    modelVO.setName(title);
                } else {
                    modelVO.setName(specialParseVO.getSpecialEO().getName().concat("-").concat(title));
                }
                modelVO.setModelTypeCode(columnType);
                modelVO.setColumnTempId(specialParseVO.getTemplateMap().get(navCode).getId());
                modelVO.setArticalTempId(specialParseVO.getTemplateMap().get(newsTplCode).getId());
                modelVO.setSiteId(specialParseVO.getSpecialEO().getSiteId());
                ContentModelEO eo = contentModelSpecialService.saveEO(modelVO);

                //模型MAP对象，用于后续的栏目绑定模型时使用
                SpecialModelVO vo = new SpecialModelVO();
                vo.setId(eo.getId());
                vo.setTitle(title);
                vo.setColumnType(columnType);
                vo.setNavCode(navCode);
                vo.setNewsTplCode(newsTplCode);
                vo.setModelCode(eo.getCode());

                //以模型唯一编码为主键
                //写入VO，用于后面方法取值
                specialParseVO.getModelMap().put(modelCode, vo);
            }
        }
        return this;
    }

    /**
     * 专题：分析栏目
     */
    public ISpecialService parseColumn() {

        //创建专题专栏虚拟根目录
        List<ColumnMgrEO> list = columnConfigSpecialService.getColumnByContentModelCode(specialParseVO.getSpecialEO().getSiteId(), SpecialConfig.modelCode.specialVirtual.toString());
        Long pId = 0L;
        if (list == null || list.size() == 0) {
            ColumnMgrEO rootEO = new ColumnMgrEO();
            rootEO.setName("专题专栏");
            rootEO.setShortName("");
            rootEO.setSortNum(2000);
            rootEO.setSiteId(specialParseVO.getSpecialEO().getSiteId());
            rootEO.setParentId(specialParseVO.getSpecialEO().getSiteId());
            rootEO.setType(IndicatorEO.Type.CMS_Section.toString());
            rootEO.setColumnTypeCode(SpecialConfig.modelCode.specialVirtual.toString());
            rootEO.setContentModelCode(SpecialConfig.modelCode.specialVirtual.toString());
            pId = columnConfigSpecialService.saveEO(rootEO);
        } else {
            pId = list.get(0).getIndicatorId();
        }

        //创建专题根栏目
        ColumnMgrEO columnMgrEO = new ColumnMgrEO();
        columnMgrEO.setName(specialParseVO.getSpecialEO().getName());
        columnMgrEO.setShortName("");
        columnMgrEO.setSortNum(2);
        columnMgrEO.setSiteId(specialParseVO.getSpecialEO().getSiteId());
        columnMgrEO.setParentId(pId);
        columnMgrEO.setType(IndicatorEO.Type.CMS_Section.toString());
        columnMgrEO.setColumnTypeCode(BaseContentEO.TypeCode.articleNews.toString());
        columnMgrEO.setContentModelCode(specialParseVO.getModelMap().get("rootColumnModel").getModelCode());
        Long rootIndicatorId = columnConfigSpecialService.saveEO(columnMgrEO);

        //构建MAP对象，用于后续专题绑定栏目根目录使用
        SpecialColumnVO columnVO = new SpecialColumnVO();
        columnVO.setId(rootIndicatorId);
        columnVO.setTitle(specialParseVO.getSpecialEO().getName());
        columnVO.setModelCode("rootColumnID");
        columnVO.setColumnCode("rootColumnID");
        specialParseVO.getColumnMap().put("rootColumnID", columnVO);

        //提取配置模板的节点
        List<Element> item = specialParseVO.getElementRoot().selectNodes(SpecialConfig.COLUMN_NODE);
        saveColumns(item, rootIndicatorId);
        return this;
    }

    /**
     * 递归保存配置节点的栏目以及子栏目
     *
     * @param item
     * @param parentId
     */
    private void saveColumns(List<Element> item, Long parentId) {

        for (Element ele : item) {
            String title = ele.selectSingleNode("Title").getText();
            String shortName = ele.selectSingleNode("ShortName").getText();
            String sortNum = ele.selectSingleNode("SortNum").getText();
            String modelCode = ele.selectSingleNode("ModelCode").getText();
            String columnCode = ele.selectSingleNode("ColumnCode").getText();

            if (title != null) {

                //创建栏目
                ColumnMgrEO eo = new ColumnMgrEO();
                eo.setName(title);
                eo.setShortName(shortName);
                eo.setParentId(parentId);
                eo.setSortNum(Integer.valueOf(sortNum));
                if (modelCode.equals(BaseContentEO.TypeCode.linksMgr.toString())) {
                    String isLogo = Dom4jUtil.selectSingleNode(ele, "IsLogo");
                    String num = Dom4jUtil.selectSingleNode(ele, "Num");
                    String logoWidth = Dom4jUtil.selectSingleNode(ele, "LogoWidth");
                    String logoHeight = Dom4jUtil.selectSingleNode(ele, "LogoHeight");
                    String linkCode = Dom4jUtil.selectSingleNode(ele, "LinkCode");
                    String titleCount = Dom4jUtil.selectSingleNode(ele, "TitleCount");
                    String remarksCount = Dom4jUtil.selectSingleNode(ele, "RemarksCount");
                    eo.setIsLogo(AppUtil.isEmpty(isLogo) ? 1 : Integer.valueOf(isLogo));
                    eo.setNum(AppUtil.isEmpty(isLogo) ? 0L : Long.valueOf(num));
                    if (!AppUtil.isEmpty(logoWidth)) {
                        eo.setWidth(Integer.valueOf(logoWidth));
                    }
                    if (!AppUtil.isEmpty(logoWidth)) {
                        eo.setHeight(Integer.valueOf(logoHeight));
                    }
                    //模板名称
                    if (!AppUtil.isEmpty(linkCode)) {
                        eo.setLinkCode(linkCode);
                    }
                    //链接管理标题字数
                    if (!AppUtil.isEmpty(titleCount)) {
                        eo.setTitleCount(Integer.valueOf(titleCount));
                    }
                    //链接管理摘要字数
                    if (!AppUtil.isEmpty(remarksCount)) {
                        eo.setRemarksCount(Integer.valueOf(remarksCount));
                    }
                }
                eo.setSiteId(specialParseVO.getSpecialEO().getSiteId());
                eo.setColumnTypeCode(specialParseVO.getModelMap().get(modelCode).getColumnType());
                eo.setContentModelCode(specialParseVO.getModelMap().get(modelCode).getModelCode());
                eo.setType(IndicatorEO.Type.CMS_Section.toString());
                Long indicatorId = columnConfigSpecialService.saveEO(eo);

                //模型MAP对象，用于后续的栏目绑定模型时使用
                SpecialColumnVO vo = new SpecialColumnVO();
                vo.setId(indicatorId);
                vo.setSiteId(eo.getSiteId());
                vo.setTitle(eo.getName());
                vo.setModelCode(modelCode);
                vo.setColumnCode(columnCode);

                //以模型唯一编码为主键
                //写入VO，用于后面方法取值
                specialParseVO.getColumnMap().put(columnCode, vo);


                //提取子节点
                List<Element> subItem = ele.selectNodes("SubItem");
                if (subItem.size() > 0) {
                    saveColumns(subItem, indicatorId);
                }

            }

        }
    }

    /**
     * 子站：分析栏目
     * 目前栏目深度只能创建 1 级
     */
    public ISpecialService parseSiteColumn() {

        //提取配置模板的节点
        List<Element> item = specialParseVO.getElementRoot().selectNodes(SpecialConfig.COLUMN_NODE);
        saveColumns(item, specialParseVO.getSpecialEO().getSiteId());
//        for (Element ele : item) {
//
//            String title = ele.selectSingleNode("Title").getText();
//            String shortName = ele.selectSingleNode("ShortName").getText();
//            String sortNum = ele.selectSingleNode("SortNum").getText();
//            String modelCode = ele.selectSingleNode("ModelCode").getText();
//            String columnCode = ele.selectSingleNode("ColumnCode").getText();
//
//            if (title != null) {
//
//                //创建栏目
//                ColumnMgrEO eo = new ColumnMgrEO();
//                eo.setName(title);
//                eo.setParentId(specialParseVO.getSpecialEO().getSiteId());
//                eo.setShortName(shortName);
//                eo.setSortNum(Integer.valueOf(sortNum));
//                if (modelCode.equals("linksMgr")) {
//                    String isLogo = ele.selectSingleNode("IsLogo").getText();
//                    String num = ele.selectSingleNode("Num").getText();
//                    eo.setIsLogo(Integer.valueOf(isLogo));
//                    eo.setNum(Long.valueOf(num));
//                }
//                eo.setSiteId(specialParseVO.getSpecialEO().getSiteId());
//                eo.setColumnTypeCode(specialParseVO.getModelMap().get(modelCode).getColumnType());
//                eo.setContentModelCode(specialParseVO.getModelMap().get(modelCode).getModelCode());
//                eo.setType(IndicatorEO.Type.CMS_Section.toString());
//                Long indicatorId = columnConfigService.saveEO(eo);
//
//                //模型MAP对象，用于后续的栏目绑定模型时使用
//                SpecialColumnVO vo = new SpecialColumnVO();
//                vo.setId(indicatorId);
//                vo.setTitle(eo.getName());
//                vo.setModelCode(modelCode);
//                vo.setColumnCode(columnCode);
//
//                //以模型唯一编码为主键
//                //写入VO，用于后面方法取值
//                specialParseVO.getColumnMap().put(columnCode, vo);
//            }
//        }
        return this;
    }

    /**
     * 分析标签，绑定到栏目
     *
     * @return
     */
    public ISpecialService parseLabelBindColumn() {

        String tplContent = "";

        //替换公共模板标识，用于前端可以找到要保存的公共模板ID
        Iterator it = specialParseVO.getTemplateMap().entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry entry = (Map.Entry) it.next();
            SpecialTemplateVO tvo = (SpecialTemplateVO) entry.getValue();
            String content = tvo.getContent();
            if (!AppUtil.isEmpty(content)) {

                //替换模板标签ID
                tplContent = replaceLabelId(tvo.getContent());
                //替换模板公共部分对应的模板ID
                tplContent = replaceCommonTplID(tplContent);
                //替换模板id
                tplContent = tplContent.replaceAll("#\\{tplId\\}", String.valueOf(tvo.getId()));
                //重写模板内容
                ContentMongoEO tplMongoEO = new ContentMongoEO();
                tplMongoEO.setId(tvo.getId());
                tplMongoEO.setContent(tplContent);
                tplMongoEO.setType("Special");
                tplHistorySpecialService.saveTplContent(tplMongoEO);
            }
        }
        return this;
    }

    /**
     * 1、替换模板标签ID为真实栏目的ID
     * 2、替换专题相关的变量
     *
     * @param content
     */
    public String replaceLabelId(String content) {
        String tempContent = content;
        if (!AppUtil.isEmpty(tempContent)) {
            Iterator it = specialParseVO.getColumnMap().entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry entry = (Map.Entry) it.next();
                SpecialColumnVO columnVO = (SpecialColumnVO) entry.getValue();
                tempContent = tempContent.replace("#{" + columnVO.getColumnCode() + "}", columnVO.getId() + "");
            }
            String path = specialParseVO.getSpecialThemeEO().getPath();
            if(null != path && path.indexOf(".") > -1){
                path = path.replaceAll("\\.","_");
            }
            //替换主题路径
            tempContent = tempContent.replaceAll("#\\{specialPath\\}",path );

            //替换站点ID
            tempContent = tempContent.replaceAll("#\\{siteId\\}", specialParseVO.getSpecialEO().getSiteId().toString());

            //子站绑定
            if (!AppUtil.isEmpty(specialParseVO.getSpecialEO().getSpecialType()) && specialParseVO.getSpecialEO().getSpecialType() == 0) {
                //模板首页链接
                tempContent = tempContent.replaceAll("#\\{specialIndexUrl\\}", "/content/column/" + specialParseVO.getColumnMap().get("rootColumnID").getId());
            }
            //模板专题ID
            //tempContent = tempContent.replaceAll("#\\{specialThemId\\}", String.valueOf(specialParseVO.getSpecialEO().getId()));
            tempContent = tempContent.replaceAll("#\\{specialId\\}", String.valueOf(specialParseVO.getSpecialEO().getId()));
            //区分专题类型 0 专题 1 子站
            tempContent = tempContent.replaceAll("#\\{specialType\\}", String.valueOf(specialParseVO.getSpecialEO().getSpecialType()));
            //将匹配不到的标签ID全置为0，防止出错。
            tempContent.replaceAll("#\\{[\\s\\S]*?\\}", "0");
        }
        return tempContent;
    }

    /**
     * 替换模板公部部分对应的模板块ID
     *
     * @param content
     */
    public String replaceCommonTplID(String content) {
        String tempContent = content;
        if (!AppUtil.isEmpty(tempContent)) {
            Iterator it = specialParseVO.getTemplateMap().entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry entry = (Map.Entry) it.next();
                SpecialTemplateVO specialTemplateVO = (SpecialTemplateVO) entry.getValue();
                tempContent = tempContent.replace("#{" + specialTemplateVO.getTemplateCode() + "}", specialTemplateVO.getId() + "");
            }
        }
        return tempContent;
    }

    /**
     * 保存专题主题
     * <p>
     * 根据模板配置的唯一code找到新添加的栏目ID，进行替换
     *
     * @param specialEO
     */
    @Override
    public void saveSpecial(SpecialEO specialEO) {

        if (AppUtil.isEmpty(config.getSpecialPath())) {
            throw new BaseRunTimeException(TipsMode.Message.toString(), "主题模板路径不存在或未配置！");
        }

        //获取主题EO
        SpecialThemeEO specialThemeEO = specialThemeService.getEntity(SpecialThemeEO.class, specialEO.getThemeId());
        if (specialThemeEO == null) {
            throw new BaseRunTimeException(TipsMode.Message.toString(), "未选中主题或绑定丢失！");
        }

        //写入专题列表
        if (null == specialEO.getId()) {

            //获取默认皮肤
            SpecialSkinsEO specialSkinsEO = specialSkinsService.getDefaultSkins(specialEO.getSiteId(), specialThemeEO.getId());
            if (!AppUtil.isEmpty(specialSkinsEO)) {
                specialEO.setDefaultSkin(specialSkinsEO.getPath());
            }

            specialEO.setId(saveEntity(specialEO));

            //写入 specialEO 专题对象
            specialParseVO.setSpecialEO(specialEO);
            //写入 specialThemeEO 主题对象
            specialParseVO.setSpecialThemeEO(specialThemeEO);

            //读取配置文件
            File xmlPath = new File(SpecialConfig.getSettingPath(specialThemeEO.getPath()));
            if (xmlPath.exists()) {
                //root节点
                try {
                    specialParseVO.setElementRoot(Dom4jUtils.getXmlRoot(xmlPath.getPath()));

                    //解析模板..解析模型.解析栏目.模板内容标签绑定栏目ID
                    parseCommonTemplate().parseVirtualModelTemplate().parseModel().parseColumn().parseLabelBindColumn();

                    //获取首页模板ID，更新到主题表
                    specialEO.setIndexId(specialParseVO.getTemplateMap().get("index").getId());
                    //获取专题对应的栏目根ID
                    specialEO.setColumnRootId(specialParseVO.getColumnMap().get("rootColumnID").getId());
                    //获取专题对模板项的根ID
                    specialEO.setTemplateRootId(specialParseVO.getTemplateMap().get("rootTemplateID").getId());
                    /*//写入全部模板JSON字符串
                    specialEO.setTemplateList(JSON.toJSONString(specialParseVO.getTemplateMap()));
                    //写入栏目模板JSON字符串
                    specialEO.setColumnList(JSON.toJSONString(specialParseVO.getTemplateMap()));
                    //写入全部模型JSON字符串
                    specialEO.setColumnList(JSON.toJSONString(specialParseVO.getTemplateMap()));*/
                    updateEntity(specialEO);


                } catch (DocumentException e) {
                    e.printStackTrace();
                }
            } else {
                throw new BaseRunTimeException(TipsMode.Message.toString(), "当前专题模板配置文件丢失！");
            }

        } else {
            updateEntity(specialEO);
            //同步修改栏目名称
            ColumnMgrEO columnMgrEO = CacheHandler.getEntity(ColumnMgrEO.class, specialEO.getColumnRootId());
            columnMgrEO.setName(specialEO.getName());
            columnConfigSpecialService.saveEO(columnMgrEO);
        }
    }

    /**
     * 保存专题子站
     * <p>
     * 根据模板配置的唯一code找到新添加的栏目ID，进行替换
     *
     * @param siteMgrEO
     */
    @Override
    public SiteConfigEO saveSiteSpecial(SiteMgrEO siteMgrEO) {

        if (AppUtil.isEmpty(config.getSpecialPath())) {
            throw new BaseRunTimeException(TipsMode.Message.toString(), "主题模板路径不存在或未配置！");
        }

        //添加站点信息，必须先添加，后续添加的信息要拿到站点ID
        SiteConfigEO siteConfigEO = siteConfigSpecialService.saveEO(siteMgrEO);

        //添加专题信息
        SpecialEO specialEO = new SpecialEO();
        specialEO.setSpecialType(siteMgrEO.getSiteType());
        specialEO.setName(siteMgrEO.getName());
        specialEO.setThemeId(siteMgrEO.getThemeId());
        specialEO.setSiteId(siteConfigEO.getIndicatorId());

        //获取主题EO
        SpecialThemeEO specialThemeEO = specialThemeService.getEntity(SpecialThemeEO.class, siteMgrEO.getThemeId());
        if (specialThemeEO == null) {
            throw new BaseRunTimeException(TipsMode.Message.toString(), "未选中主题或绑定丢失！");
        }

        //获取默认皮肤
        SpecialSkinsEO specialSkinsEO = specialSkinsService.getDefaultSkins(specialEO.getSiteId(), specialThemeEO.getId());
        if (!AppUtil.isEmpty(specialSkinsEO)) {
            specialEO.setDefaultSkin(specialSkinsEO.getPath());
        }

        //添加
        specialEO.setId(saveEntity(specialEO));

        //写入 specialEO 专题对象
        specialParseVO.setSpecialEO(specialEO);
        //写入 specialThemeEO 主题对象
        specialParseVO.setSpecialThemeEO(specialThemeEO);

        //读取配置文件
        File xmlPath = new File(SpecialConfig.getSettingPath(specialThemeEO.getPath()));
        if (xmlPath.exists()) {
            try {
                specialParseVO.setElementRoot(Dom4jUtils.getXmlRoot(xmlPath.getPath()));
                //模板公共解析.模板解析.解析模型.解析栏目.模板内容标签绑定栏目ID
                parseSiteCommonTemplate().parseVirtualModelTemplate().parseModel().parseSiteColumn().parseLabelBindColumn();

                //获取首页模板ID，更新到主题表
                specialEO.setIndexId(specialParseVO.getTemplateMap().get("index").getId());
                //获取专题对应的栏目根ID
                specialEO.setColumnRootId(specialParseVO.getSpecialEO().getSiteId());
                //获取专题对模板项的根ID
                specialEO.setTemplateRootId(specialParseVO.getTemplateMap().get("index").getId());
                /*
                //写入全部模板JSON字符串
                specialEO.setTemplateList(JSON.toJSONString(specialParseVO.getTemplateMap()));
                //写入栏目模板JSON字符串
                specialEO.setColumnList(JSON.toJSONString(specialParseVO.getTemplateMap()));
                //写入全部模型JSON字符串
                specialEO.setColumnList(JSON.toJSONString(specialParseVO.getTemplateMap()));
                */
                updateEntity(specialEO);

                siteMgrEO.setSiteTitle(siteMgrEO.getName());
                siteMgrEO.setIndicatorId(siteConfigEO.getIndicatorId());
                siteMgrEO.setSiteConfigId(siteConfigEO.getSiteConfigId());
                siteMgrEO.setSpecialId(specialEO.getId());
                //更新首页模板
                siteMgrEO.setIndexTempId(Long.valueOf(specialParseVO.getTemplateMap().get("index").getId()));
                //更新搜索页模板
                SpecialTemplateVO specialTemplateVO = specialParseVO.getTemplateMap().get("search");
                if (!AppUtil.isEmpty(specialTemplateVO)) {
                    siteMgrEO.setSearchTempId(specialTemplateVO.getId());
                }
                siteConfigSpecialService.updateEO(siteMgrEO);

            } catch (DocumentException e) {
                e.printStackTrace();
            }
        } else {
            throw new BaseRunTimeException(TipsMode.Message.toString(), "当前专题模板配置文件丢失！");
        }
        return siteConfigEO;
    }

    /**
     * 获取分页数据
     *
     * @param queryVO
     * @return
     */
    @Override
    public Pagination getPagination(SpecialQueryVO queryVO) {

        Pagination page = specialDao.getPagination(queryVO);
        List<SpecialVO> pageList = (List<SpecialVO>) page.getData();
        for (SpecialVO specialVO : pageList) {
            if (!AppUtil.isEmpty(specialVO.getThemeImgPath())) {
                specialVO.setThumb((List<SpecialThumbVO>) JSONArray.parse(specialVO.getThemeImgPath()));
            }
            specialVO.setSkins(specialSkinsService.getSkinsItem(queryVO.getSiteId(), specialVO.getThemeId()));
        }

        return page;
    }

    @Override
    public void changeSpecial(Long id, Long specialStatus) {

        if (AppUtil.isEmpty(id)) {
            throw new BaseRunTimeException(TipsMode.Message.toString(), "参数不能为空！");
        }

        //修改专题状态
        specialDao.changeSpecial(id, specialStatus);
        SpecialEO specialEO = getEntity(SpecialEO.class, id);

        //同步修改栏目名称
        ColumnMgrEO columnMgrEO = CacheHandler.getEntity(ColumnMgrEO.class, specialEO.getColumnRootId());
        if (specialStatus == 1) {
            columnMgrEO.setIsShow(0);
            columnMgrEO.setName(columnMgrEO.getName() + "-关闭");
        } else {
            columnMgrEO.setIsShow(1);
            columnMgrEO.setName(columnMgrEO.getName().replaceAll("-关闭", ""));
        }
        columnConfigSpecialService.saveEO(columnMgrEO);
    }

    @Override
    public void deleteSpecial(Long id) {

        SpecialEO specialEO = specialDao.getEntity(SpecialEO.class, id);
        if (specialEO == null) {
            throw new BaseRunTimeException(TipsMode.Message.toString(), "专题不存在或已被删除！");
        }

        //删除专题
        specialDao.delete(SpecialEO.class, id);

        //删除栏目,目前专题只支持平行栏目
        List<ColumnMgrEO> columnMgrEO = columnConfigSpecialService.getAllColumnBySite(specialEO.getColumnRootId());
        //构建数组，执行删除，数据量不大，直接嵌套删除
        String[] codeArr = new String[columnMgrEO.size()];
        for (int i = 0, l = columnMgrEO.size(); i < l; i++) {
            codeArr[i] = columnMgrEO.get(i).getContentModelCode();
            columnConfigSpecialService.deleteComEO(columnMgrEO.get(i).getIndicatorId());
        }

        //删除模型
        List<ContentModelVO> conModel = contentModelSpecialService.getByCodes(LoginPersonUtil.getSiteId(), codeArr);
        for (int i = 0, l = conModel.size(); i < l; i++) {
            contentModelSpecialService.delEO(conModel.get(i).getId());
        }

        //删除模板
        List<TemplateConfEO> tplList = tplConfSpecialService.getSpecialById(LoginPersonUtil.getSiteId(), specialEO.getId(), "");
        for (int i = 0, l = tplList.size(); i < l; i++) {
            tplConfSpecialService.delEO(tplList.get(i).getId());
        }

        //删除专题下所有模块添加的信息，如：新闻，留言，图片新闻，视频等。
        //TODO 涉及删除的模块较多，且未提供统一的删除方法
        //demoService service = SpringContextHolder.getBean(moduleType + "Service");
        //service.deleteColumnInfo(columnId);

    }

    @Override
    public SpecialEO getThemeById(Long id) {
        return specialDao.getThemeById(id);
    }

    @Override
    public SpecialEO getById(Long id) {
        return specialDao.getById(id);
    }


}
