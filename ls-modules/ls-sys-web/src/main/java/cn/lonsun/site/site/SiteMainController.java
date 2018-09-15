package cn.lonsun.site.site;

import cn.lonsun.cache.client.CacheGroup;
import cn.lonsun.cache.client.CacheHandler;
import cn.lonsun.common.util.AppUtil;
import cn.lonsun.content.commentMgr.internal.service.ICommentService;
import cn.lonsun.content.internal.entity.BaseContentEO;
import cn.lonsun.content.internal.service.IBaseContentService;
import cn.lonsun.core.base.controller.BaseController;
import cn.lonsun.core.base.entity.AMockEntity;
import cn.lonsun.core.exception.BaseRunTimeException;
import cn.lonsun.core.util.TipsMode;
import cn.lonsun.indicator.internal.entity.FunctionEO;
import cn.lonsun.indicator.internal.entity.IndicatorEO;
import cn.lonsun.indicator.internal.service.IIndicatorService;
import cn.lonsun.rbac.internal.entity.OrganEO;
import cn.lonsun.rbac.internal.service.IOrganService;
import cn.lonsun.site.contentModel.internal.entity.ContentModelEO;
import cn.lonsun.site.contentModel.internal.entity.ModelTemplateEO;
import cn.lonsun.site.site.internal.entity.*;
import cn.lonsun.site.site.internal.service.IColumnConfigRelService;
import cn.lonsun.site.site.internal.service.IColumnConfigService;
import cn.lonsun.site.site.internal.service.ISiteConfigService;
import cn.lonsun.site.site.vo.ColumnMgrVO;
import cn.lonsun.site.site.vo.ColumnVO;
import cn.lonsun.site.template.internal.entity.TemplateConfEO;
import cn.lonsun.site.template.internal.service.ITplConfService;
import cn.lonsun.system.role.internal.service.ISiteRightsService;
import cn.lonsun.util.LoginPersonUtil;
import cn.lonsun.util.ModelConfigUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static cn.lonsun.cache.client.CacheHandler.getEntity;
import static cn.lonsun.cache.client.CacheHandler.getList;

/**
 * 用户查询站点和栏目树的控制层<br/>
 *
 * @author wangss <br/>
 * @version v1.0 <br/>
 * @date 2015-8-25 <br/>
 */

@Controller
@RequestMapping("siteMain")
public class SiteMainController extends BaseController {

    @Autowired
    private IColumnConfigService columnConfigService;

    @Autowired
    private ISiteConfigService siteConfigService;

    @Autowired
    private ISiteRightsService siteRightsService;
    @Autowired
    private ICommentService commentService;

    @Autowired
    private IColumnConfigRelService relService;

    @Autowired
    private ITplConfService tplConfService;


    @Autowired
    private IOrganService organService;

    @Autowired
    private IBaseContentService baseContentService;

    @Autowired
    private IIndicatorService iIndicatorService;

    /**
     * 去往站点管理页面
     *
     * @return
     */
    @RequestMapping("listSiteTree")
    public String listSiteTree(Model model) {
        Long siteId = LoginPersonUtil.getSiteId();
        //厂商账号
        if (siteId == null) {
            return "site/site/site_tree";
        } else {
            // 获取公共模板
            Object tplObj = tplConfService.getVrtpls();
            model.addAttribute("tplObj", tplObj);
            IndicatorEO eo = getEntity(IndicatorEO.class, siteId);
            //虚拟站点
            if (IndicatorEO.Type.SUB_Site.toString().equals(eo.getType())) {
                return "site/site/sub_siteInfo";
            }
        }
        //标准站点
        return "site/site/site_tree";
    }

    /**
     * 去往栏目管理页面
     *
     * @return
     */
    @RequestMapping("listColumnTree")
    public String listColumnTree(@RequestParam(value = "indicatorId", required = false, defaultValue = "") Long indicatorId
        , Model model) {
        Long siteId = LoginPersonUtil.getSiteId();
        if (indicatorId == null) {
            model.addAttribute("indicatorId", siteId);
        } else {
            model.addAttribute("indicatorId", indicatorId);
        }
        IndicatorEO eo = getEntity(IndicatorEO.class, siteId);
        //虚拟子站
        if (IndicatorEO.Type.SUB_Site.toString().equals(eo.getType())) {
            return "site/site/column_treeInfo";
        } else {//标准站点
            return "site/site/column_tree";
        }
    }

    /**
     * 打开站点树的查询页面
     *
     * @return
     */
    @RequestMapping("toSearchSiteTree")
    public String toSearchSiteTree() {
        return "site/site/search_site_tree";
    }

    /**
     * 去往栏目管理中“同步到栏目”和“生成关联页面”
     *
     * @return
     */
    @RequestMapping("toSelectColumnTree")
    public String toSelectColumnTree(Long siteId, @RequestParam(value = "synColumnIds", required = false, defaultValue = "") String synColumnIds,
                                     @RequestParam(value = "genePageIds", required = false, defaultValue = "") String genePageIds,
                                     Integer flag, String contentModelCode, Long indicatorId, Model model) {
        model.addAttribute("siteId", siteId);
        model.addAttribute("flag", flag);
        if (flag == 0) {//同步到栏目
            model.addAttribute("synColumnIds", synColumnIds);
            model.addAttribute("genePageIds", "");
            if (AppUtil.isEmpty(contentModelCode)) {
                model.addAttribute("columnTypeCode", "");
            } else {
                List<ModelTemplateEO> list = ModelConfigUtil.getTemplateListByCode(contentModelCode, siteId);
                if (list == null || list.size() <= 0) {
                    throw new BaseRunTimeException(TipsMode.Message.toString(), "该内容模型未配置栏目类型");
                } else {
                    for (ModelTemplateEO eo : list) {
                        if (eo.getType() == 1) {//type为1，表示主的模型模板关联
                            model.addAttribute("columnTypeCode", eo.getModelTypeCode());
                            break;
                        } else {
                            model.addAttribute("columnTypeCode", "");
                        }
                    }
                }
            }
        }
        if (flag == 1) {//生成关联页面
            model.addAttribute("synColumnIds", "");
            model.addAttribute("genePageIds", genePageIds);
            model.addAttribute("columnTypeCode", "");
        }
        if (indicatorId == null) {
            model.addAttribute("indicatorId", "");
        } else {
            model.addAttribute("indicatorId", indicatorId);
        }
        return "site/site/choose_column";
    }


    /**
     * 去往栏目管理中“引用到栏目”页面
     *
     * @return
     */
    @RequestMapping("getReferColumns")
    public String getReferColumns(Long siteId, @RequestParam(value = "synColumnIds", required = false, defaultValue = "") String synColumnIds,
                                     String contentModelCode, Long indicatorId, Model model) {
        model.addAttribute("siteId", siteId);
        model.addAttribute("synColumnIds", synColumnIds);
        if (AppUtil.isEmpty(contentModelCode)) {
            model.addAttribute("columnTypeCode", BaseContentEO.TypeCode.articleNews.toString());
        } else {
            List<ModelTemplateEO> list = ModelConfigUtil.getTemplateListByCode(contentModelCode, siteId);
            if (list == null || list.size() <= 0) {
                throw new BaseRunTimeException(TipsMode.Message.toString(), "该内容模型未配置栏目类型");
            } else {
                for (ModelTemplateEO eo : list) {
                    if (eo.getType() == 1) {//type为1，表示主的模型模板关联
                        model.addAttribute("columnTypeCode", eo.getModelTypeCode());
                        break;
                    } else {
                        model.addAttribute("columnTypeCode", "");
                    }
                }
            }
        }
        if (indicatorId == null) {
            model.addAttribute("indicatorId", "");
        } else {
            model.addAttribute("indicatorId", indicatorId);
        }
        return "site/site/refer_columns";
    }


    @RequestMapping("linkModel")
    public String linkModel(Model model, boolean isAddModel) {
        model.addAttribute("isFlag", "1");
        model.addAttribute("id", "");
        //不是公共内容模型
        model.addAttribute("isPublic", 0);
        model.addAttribute("isAddModel", isAddModel);
        Long siteId = LoginPersonUtil.getSiteId();
        ///获取栏目页模板
        List<TemplateConfEO> columnList = (List<TemplateConfEO>) tplConfService.getByTemptype(siteId, "column");
        //获取文章页模板
        List<TemplateConfEO> contentList = (List<TemplateConfEO>) tplConfService.getByTemptype(siteId, "content");
        model.addAttribute("columnList", columnList);
        model.addAttribute("contentList", contentList);
        return "site/site/link_model";
    }

    /**
     * 得到第一个站点
     * 1、 站点管理中加载树之后调用
     *
     * @return
     */
    @RequestMapping("getFirstSiteEO")
    @ResponseBody
    public SiteMgrEO getFirstSiteEO(Long indicatorId) {
        SiteMgrEO siteVO = new SiteMgrEO();
        if (indicatorId == null) {
            return siteVO;
        }
        siteVO = getEntity(SiteMgrEO.class, indicatorId);
        if (siteVO != null) {
            //站点下是否含有子栏目
            Boolean isHave = columnConfigService.getIsHaveColumn(indicatorId);
            siteVO.setIsHave(isHave);
            //获取绑定单位的名字
            if (!StringUtils.isEmpty(siteVO.getUnitIds())) {
                Long[] idArr = AppUtil.getLongs(siteVO.getUnitIds(), ",");
                OrganEO organEO = organService.getEntity(OrganEO.class, idArr[0]);
                if (organEO != null) {
                    siteVO.setUnitNames(organEO.getName());
                } else {
                    siteVO.setUnitNames(null);
                }
            }
        }
        return siteVO;
    }

    /**
     * 得到站点下的第一个栏目
     * 1、栏目管理中加载树之后调用
     *
     * @param indicatorId
     * @return
     */
    @RequestMapping("getFirstColumnEO")
    @ResponseBody
    public ColumnMgrEO getFirstColumnEO(Long indicatorId, boolean isCom) {
        if (indicatorId == null) {
            throw new BaseRunTimeException(TipsMode.Message.toString(), "参数错误");
        }
        ColumnMgrEO columnVO = new ColumnMgrEO();
        //判断栏目下是否有内容
        Long columnContentNum = baseContentService.getCountByColumnId(indicatorId);
        boolean isHave = columnContentNum != null && columnContentNum > 0;
        columnVO.setIsHave(isHave);
        //如果是公共栏目管理获取第一个栏目
        if (isCom) {
            IndicatorEO indicatorEO = getEntity(IndicatorEO.class, indicatorId);
            Map<String, Object> map = new HashMap<String, Object>();
            map.put("indicatorId", indicatorEO.getIndicatorId());
            map.put("recordStatus", AMockEntity.RecordStatus.Normal.toString());
            List<ColumnConfigEO> list = columnConfigService.getEntities(ColumnConfigEO.class, map);
            if (list != null && list.size() > 0) {
                ColumnConfigEO configEO = list.get(0);
                AppUtil.copyProperties(columnVO, indicatorEO);
                columnVO.setColumnTypeCode(configEO.getColumnTypeCode());
                columnVO.setIndicatorId(indicatorEO.getIndicatorId());
                columnVO.setContentModelCode(configEO.getContentModelCode());
                columnVO.setIsStartUrl(configEO.getIsStartUrl());
                columnVO.setKeyWords(configEO.getKeyWords());
                columnVO.setDescription(configEO.getDescription());
                columnVO.setColumnConfigId(configEO.getColumnConfigId());
                columnVO.setIsLogo(configEO.getIsLogo());
                columnVO.setNum(configEO.getNum());
                //启用Logo
                if (configEO.getIsLogo() != null && configEO.getIsLogo() == 1) {
                    columnVO.setHeight(configEO.getHeight());
                    columnVO.setWidth(configEO.getWidth());
                }
                //开启转链
                if (configEO.getIsStartUrl() != null && configEO.getIsStartUrl() == 1) {
                    columnVO.setTransUrl(configEO.getTransUrl());
                    columnVO.setTransWindow(configEO.getTransWindow());
                }
            }


        } else {//如果是栏目管理获取第一个栏目
            IndicatorEO indicatorEO = CacheHandler.getEntity(IndicatorEO.class, indicatorId);
            if (indicatorEO != null) {
                if (IndicatorEO.Type.CMS_Section.toString().equals(indicatorEO.getType())) {
                    columnVO = CacheHandler.getEntity(ColumnMgrEO.class, indicatorId);
                    if (columnVO == null) {
                        columnVO = new ColumnMgrEO();
                    } else {
                        if(!StringUtils.isEmpty(columnVO.getContentModelCode())){
                            ContentModelEO modelEO= ModelConfigUtil.getEOByCode(columnVO.getContentModelCode(),columnVO.getSiteId());
                            if(modelEO!=null){
                                columnVO.setContentModelName(modelEO.getName());
                            }

                        }
                        return columnVO;
                    }
                }
                //如果不是标准栏目
                columnVO.setIndicatorId(indicatorEO.getIndicatorId());
                columnVO.setName(indicatorEO.getName());
                columnVO.setSortNum(indicatorEO.getSortNum());
                columnVO.setType(indicatorEO.getType());
                columnVO.setParentId(indicatorEO.getParentId());
                columnVO.setIsParent(indicatorEO.getIsParent());
                ColumnConfigEO configEO = CacheHandler.getEntity(ColumnConfigEO.class, CacheGroup.CMS_INDICATORID, indicatorEO.getIndicatorId());
                if (configEO != null) {
                    columnVO.setContentModelCode(configEO.getContentModelCode());
                    columnVO.setColumnTypeCode(configEO.getColumnTypeCode());
                    columnVO.setKeyWords(configEO.getKeyWords());
                    columnVO.setDescription(configEO.getDescription());
                    columnVO.setIsShow(configEO.getIsShow());
                    columnVO.setTransUrl(configEO.getTransUrl());
                    columnVO.setTransWindow(configEO.getTransWindow());
                    columnVO.setGenePageIds(configEO.getGenePageIds());
                    columnVO.setGenePageNames(configEO.getGenePageNames());
                    columnVO.setSynColumnIds(configEO.getSynColumnIds());
                    columnVO.setSynColumnNames(configEO.getSynColumnNames());
                    columnVO.setColumnConfigId(configEO.getColumnConfigId());
                }
                //如果是公共栏目
                if (IndicatorEO.Type.COM_Section.toString().equals(columnVO.getType())) {
                    Map<String, Object> map = new HashMap<String, Object>();
                    map.put("isHide", false);
                    map.put("indicatorId", indicatorId);
                    map.put("siteId", LoginPersonUtil.getSiteId());
                    List<ColumnConfigRelEO> relList = relService.getEntities(ColumnConfigRelEO.class, map);
                    if (relList != null && relList.size() > 0) {
                        columnVO.setIndicatorId(relList.get(0).getIndicatorId());
                        columnVO.setName(relList.get(0).getName());
                        columnVO.setSortNum(relList.get(0).getSortNum());
                        columnVO.setContentModelCode(relList.get(0).getContentModelCode());
                        columnVO.setColumnTypeCode(relList.get(0).getColumnTypeCode());
                        columnVO.setKeyWords(relList.get(0).getKeyWords());
                        columnVO.setDescription(relList.get(0).getDescription());
                        columnVO.setIsShow(relList.get(0).getIsShow());
                        columnVO.setTransUrl(relList.get(0).getTransUrl());
                        columnVO.setTransWindow(relList.get(0).getTransWindow());
                        columnVO.setRelId(relList.get(0).getId());

                    }
                }
            }
        }
        if(!StringUtils.isEmpty(columnVO.getColumnTypeCode())){
            ContentModelEO modelEO= ModelConfigUtil.getEOByCode(columnVO.getContentModelCode(),columnVO.getSiteId());
            if(modelEO!=null){
                columnVO.setContentModelName(modelEO.getName());
            }

        }

        if(!StringUtils.isEmpty(columnVO.getColumnClassCode())){
            Long[] codeArr= AppUtil.getLongs(columnVO.getColumnClassCode(),",");
            if(codeArr!=null&&codeArr.length>0){
                String names="";
                for(Long code:codeArr){
                    ColumnTypeEO columnTypeEO=CacheHandler.getEntity(ColumnTypeEO.class,code);
                    if(columnTypeEO!=null){
                        names+=columnTypeEO.getTypeName()+",";
                    }
                }
                if(!StringUtils.isEmpty(names)){
                    columnVO.setColumnClassName(names.substring(0,names.length()-1));
                }
            }
        }else{
            columnVO.setColumnClassName("");
        }

        return columnVO;
    }

    /**
     * 获取站点树的结构（异步加载）
     * 1、给站点管理提供站点树
     *
     * @return String JSON格式
     */
    @RequestMapping("getSiteTree")
    @ResponseBody
    public Object getSiteTree(@RequestParam(value = "indicatorId", required = false, defaultValue = "") Long indicatorId) {

        //List<SiteVO> list = siteConfigService.getSiteTree(indicatorId);
        //获取所有的标准站点
        List<SiteMgrEO> list = getList(SiteMgrEO.class, CacheGroup.CMS_TYPE, IndicatorEO.Type.CMS_Site.toString());
        SiteMgrEO siteVO = new SiteMgrEO();
        siteVO.setIndicatorId(1L);
        siteVO.setName("站点列表");
        siteVO.setParentId(0L);
        siteVO.setSortNum(0);
        siteVO.setType(IndicatorEO.Type.CMS_Site.toString());
        List<SiteMgrEO> newList = new ArrayList<SiteMgrEO>();
        boolean rootFlag = LoginPersonUtil.isRoot();
        boolean adminFlag = LoginPersonUtil.isSuperAdmin();
        if (list == null) {//站点为空
            if (rootFlag) {//lonsun_root账号,能够添加站点,不走权限判断
                siteVO.setOpt("add");
                siteVO.setIsParent(0);
                newList.add(siteVO);
                return getObject(newList);
            } else {
                siteVO.setOpt(null);
                siteVO.setIsParent(0);
                newList.add(siteVO);
                return getObject(newList);
            }
        } else {//站点不为空
            for (SiteMgrEO eo : list) {
                eo.setIsParent(0);
            }
            if (rootFlag) {//lonsun_root账号,能够添加站点,不走权限判断
                siteVO.setIsParent(1);
                siteVO.setOpt("add");
                list.add(siteVO);
                return getObject(list);
            }
            if (adminFlag) {//超管，不能添加站点
                siteVO.setIsParent(1);
                siteVO.setOpt(null);
                list.add(siteVO);
                return getObject(list);
            }
            list = getSiteOpt(list);//其他账号走权限判断
            if (list == null || list.size() == 0) {
                siteVO.setIsParent(0);
                siteVO.setOpt(null);
                newList.add(siteVO);
                return getObject(newList);
            } else {
                siteVO.setIsParent(1);
                siteVO.setOpt(null);
                list.add(siteVO);
                return getObject(list);
            }
        }
    }

    /**
     * 获取站点树的结构（异步加载）
     * 1、给站点管理提供站点树
     *
     * @return String JSON格式
     */
    @RequestMapping("getSiteTree2")
    @ResponseBody
    public Object getSiteTree2(@RequestParam(value = "indicatorId", required = false, defaultValue = "") Long indicatorId) {

        //获取所有的标准站点
        List<SiteMgrEO> list = getList(SiteMgrEO.class, CacheGroup.CMS_TYPE, IndicatorEO.Type.CMS_Site.toString());
        for (SiteMgrEO eo : list) {
            eo.setIsParent(0);
        }
        return getObject(list);

    }

    /**
     * 获取栏目树的结构（异步加载）
     * 1、给栏目管理提供栏目树
     *
     * @param indicatorId
     * @return
     */
    @RequestMapping("getColumnTree")
    @ResponseBody
    public Object getColumnTree(@RequestParam(value = "indicatorId", required = false, defaultValue = "") Long indicatorId,
                                @RequestParam(value = "siteId", required = false, defaultValue = "") Long siteId) {
        if (indicatorId == null) {
            return getObject(new ArrayList<ColumnVO>());
        }
        //异步获取栏目
        List<ColumnMgrEO> list = columnConfigService.getColumnTree(indicatorId, null);
        List<ColumnMgrEO> newList = new ArrayList<ColumnMgrEO>();

        if (list == null || list.size() == 0 && indicatorId.equals(siteId)) {//没有栏目
            return getObject(newList);
        } else {
            boolean flag1 = LoginPersonUtil.isSuperAdmin() || LoginPersonUtil.isRoot();
            if (flag1 && siteId.equals(indicatorId)) {
                return getObject(list);
            }
            if (flag1) {
                return getObject(list);
            }
            List<ColumnMgrEO> list1 = getColumnOpt(list);
            if (list1 == null && list1.size() == 0 && siteId.equals(indicatorId)) {
                return getObject(newList);
            }
//            if (siteId.equals(indicatorId)) {
//                // list1.add(vo);
//            }
            return getObject(list1);
        }
    }


    /**
     * 根据站点ID查询站点下的栏目树（异步加载）
     * 1、为内容协同提供接口
     *
     * @param indicatorId
     * @return
     */
    @RequestMapping("getColumnTreeBySite")
    @ResponseBody
    public Object getColumnTreeBySite(@RequestParam(value = "indicatorId", required = false, defaultValue = "") Long indicatorId) {

        if (indicatorId == null) {
            return getObject();
        }
        //异步获取栏目
        List<ColumnMgrEO> list = columnConfigService.getColumnTreeBySite(indicatorId);
        if (list == null || list.size() == 0) {
            return getObject();
        }
        //处理统计信息
        baseContentService.getStatisticsCount(list);
        boolean flag1 = LoginPersonUtil.isSuperAdmin() || LoginPersonUtil.isRoot() || LoginPersonUtil.isSiteAdmin();
        if (flag1) {//厂商、超管、站点管理员不走权限判断
            return getObject(list);
        } else {
            List<ColumnMgrEO> list1 = getColumnOpt(list);
            if (list1 == null || list1.size() == 0) {
                return getObject();
            }
            return getObject(list1);
        }
    }

    @RequestMapping("getHNColumnTree")
    @ResponseBody
    public Object getHNColumnTree(@RequestParam(value = "indicatorId", required = false, defaultValue = "") Long indicatorId,
                                  @RequestParam(value = "siteId", required = false, defaultValue = "") Long siteId) {

        List<ColumnMgrVO> columnMgrVOs = new ArrayList<ColumnMgrVO>();
        if (AppUtil.isEmpty(indicatorId)) {
            List<IndicatorEO> indicatorEOs = iIndicatorService.getAllStieInfo();
            if (null != indicatorEOs && indicatorEOs.size() > 0) {
                for (IndicatorEO indicatorEO : indicatorEOs) {
                    ColumnMgrVO vo = new ColumnMgrVO();
                    AppUtil.copyProperties(vo, indicatorEO);
                    columnMgrVOs.add(vo);
                }

                return getObject(columnMgrVOs);
            }

        }
        //异步获取栏目
        List<ColumnMgrEO> list = columnConfigService.getColumnTree(indicatorId, null);

        if (list == null || list.size() == 0 && indicatorId.equals(siteId)) {//没有栏目
            return getObject(new ArrayList<ColumnMgrVO>());
        } else {
//            list = getColumnOpt(list);
            for (ColumnMgrEO columnMgrEO : list) {
                ColumnMgrVO vo = new ColumnMgrVO();
                List<FunctionEO> functionEOs = columnMgrEO.getFunctions();
                if (null != functionEOs && functionEOs.size() > 0) {
                    for (FunctionEO functionEO : functionEOs) {
                        vo.getFunctions().add(functionEO);
                    }
                }
//                AppUtil.copyProperties(vo, columnMgrEO);
//                columnMgrVOs.add(vo);
                dealHaveArticle(columnMgrEO,columnMgrVOs,null);

            }
            return getObject(columnMgrVOs);
        }
    }
    public  Boolean  dealHaveArticle(ColumnMgrEO columnMgrEO,List<ColumnMgrVO> columnMgrVOs,Long indicatorId){
        Boolean flag =false;
        if(columnMgrEO.getIsParent().equals(0)&&AppUtil.isEmpty(indicatorId)){
            if(columnMgrEO.getColumnTypeCode().equals(BaseContentEO.TypeCode.articleNews.toString())&&columnMgrEO.getIsSubmit().equals(1)){
                ColumnMgrVO vo = new ColumnMgrVO();
                AppUtil.copyProperties(vo, columnMgrEO);
                columnMgrVOs.add(vo);
            }

        }
        else {
            List<ColumnMgrEO> list = columnConfigService.getColumnTree(columnMgrEO.getIndicatorId(), null);
            for( ColumnMgrEO columnMgr:   list){
                if(columnMgr.getIsParent().equals(1)){
                    flag =  dealHaveArticle(columnMgr,columnMgrVOs,columnMgr.getIndicatorId());
                    if(AppUtil.isEmpty(indicatorId)&&flag){
                        ColumnMgrVO vo = new ColumnMgrVO();
                        AppUtil.copyProperties(vo, columnMgrEO);
                        Boolean has =false;
                        for( ColumnMgrVO mgrVO:columnMgrVOs){
                            if(vo.getIndicatorId().equals(mgrVO.getIndicatorId())){
                                has=true;
                                break;
                            }


                        }
                        if(!has){
                            columnMgrVOs.add(vo);

                        }
                    }

                }
                else if(columnMgr.getIsParent().equals(0)){
                    if(columnMgr.getColumnTypeCode().equals(BaseContentEO.TypeCode.articleNews.toString())&&columnMgr.getIsSubmit().equals(1)){
                        if(AppUtil.isEmpty(indicatorId)){
                            ColumnMgrVO vo = new ColumnMgrVO();
                            AppUtil.copyProperties(vo, columnMgrEO);
                            Boolean has =false;
                            for( ColumnMgrVO mgrVO:columnMgrVOs){
                                if(vo.getIndicatorId().equals(mgrVO.getIndicatorId())){
                                    has=true;
                                    break;
                                }


                            }
                            if(!has){
                                columnMgrVOs.add(vo);

                            }
                        }else {
                            return true;

                        }

                    }
                }

            }
        }
        return flag;
    }

    @RequestMapping("getHNSeracher")
    @ResponseBody
    public Object getHNSeracher() {
        List<ColumnMgrVO> columnMgrVOs = new ArrayList<ColumnMgrVO>();
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("recordStatus", AMockEntity.RecordStatus.Normal.toString());
        map.put("type", IndicatorEO.Type.CMS_Site.toString());
        List<IndicatorEO> indicatorEOs = iIndicatorService.getEntities(IndicatorEO.class, map);
        Long[] siteIds;
        List<ColumnMgrEO> columnList = new ArrayList<ColumnMgrEO>();
        List<ColumnMgrEO> list = new ArrayList<ColumnMgrEO>();

        if (null != indicatorEOs && indicatorEOs.size() > 0) {
            siteIds = new Long[indicatorEOs.size()];
            for (int i = 0; i < indicatorEOs.size(); i++) {
                ColumnMgrVO vo = new ColumnMgrVO();
                AppUtil.copyProperties(vo, indicatorEOs.get(i));
                columnMgrVOs.add(vo);
                list = CacheHandler.getList(ColumnMgrEO.class, CacheGroup.CMS_SITE_ID, indicatorEOs.get(i).getIndicatorId());
                if (null != list &&list.size()>0) {
                    columnList.addAll(list);
                }

            }

        }
        for (ColumnMgrEO columnMgrEO : columnList) {
            ColumnMgrVO vo = new ColumnMgrVO();
            List<FunctionEO> functionEOs = columnMgrEO.getFunctions();
            if (null != functionEOs && functionEOs.size() > 0) {
                for (FunctionEO functionEO : functionEOs) {
                    vo.getFunctions().add(functionEO);

                }

            }
            if (columnMgrEO.getColumnTypeCode().equals(BaseContentEO.TypeCode.articleNews.toString())&&columnMgrEO.getIsSubmit().equals(1)) {
                AppUtil.copyProperties(vo, columnMgrEO);
                columnMgrVOs.add(vo);
            }
        }

        //返还数组
        List<Object[]> arrayColumn = new ArrayList<Object[]>();

        dealName(columnMgrVOs, null, arrayColumn);
        //返还数组
        return arrayColumn;
    }
    public String dealName(List<ColumnMgrVO> columnMgrVOs, Long parentId, List<Object[]> arrayColumn) {
        String treeName = "";
        for (ColumnMgrVO columnMgrVO : columnMgrVOs) {
            if (!AppUtil.isEmpty(parentId) && columnMgrVO.getIsParent() == 1 && columnMgrVO.getIndicatorId().equals(parentId)) {
                treeName = treeName + ">" + columnMgrVO.getName();

                if (!columnMgrVO.getParentId().equals(1L)) {
                    treeName = dealName(columnMgrVOs, columnMgrVO.getParentId(), arrayColumn) + treeName;
                }
                return treeName;
            }

            //栏目不是父栏目
            if (AppUtil.isEmpty(parentId)) {
                treeName = dealName(columnMgrVOs, columnMgrVO.getParentId(), arrayColumn);
                columnMgrVO.setTreeName(treeName + ">" + columnMgrVO.getName());
                Object[] columns = new Object[10];
                columns[0] = columnMgrVO.getIndicatorId();
                columns[1] = columnMgrVO.getName();
                columns[2] = columnMgrVO.getShortName();
                columns[3] = columnMgrVO.getTreeName().substring(1, columnMgrVO.getTreeName().length());
                columns[4] = columnMgrVO.getParentId();
                columns[5] = columnMgrVO.getSortNum();
                columns[6] = columnMgrVO.getIsParent();
                columns[7] = columnMgrVO.getSiteId();
                columns[8] = columnMgrVO.getType();
                columns[9] = columnMgrVO.getOpt();
                arrayColumn.add(columns);
            }
        }
        return "";
    }

    /**
     * 根据栏目类型获取该站点下的栏目树
     * 1、为内容管理提供接口
     *
     * @param columnId
     * @param columnTypeCode
     * @return
     */
    @RequestMapping("getColumnTreeByType")
    @ResponseBody
    public Object getColumnTreeByType(Long columnId, String columnTypeCode) {
        if (AppUtil.isEmpty(columnTypeCode)) {
            return getObject();
        }
        List<ColumnMgrEO> list = new ArrayList<ColumnMgrEO>();
        //厂商或者超管账号
        if (LoginPersonUtil.isSuperAdmin() || LoginPersonUtil.isRoot()) {
            //获取所有标准站点
            List<IndicatorEO> siteList = getList(IndicatorEO.class, CacheGroup.CMS_TYPE.toString(), IndicatorEO.Type.CMS_Site.toString());
            //获取所有虚拟站点
            List<IndicatorEO> siteList1 = getList(IndicatorEO.class, CacheGroup.CMS_TYPE.toString(), IndicatorEO.Type.SUB_Site.toString());
            if (siteList != null && siteList1 != null) {
                siteList.addAll(siteList1);
            } else if (siteList == null && siteList1 != null) {
                siteList = new ArrayList<IndicatorEO>();
                siteList.addAll(siteList1);
            }
            Long[] ids = new Long[siteList.size()];
            int i = 0;
            for (IndicatorEO eo : siteList) {
                ids[i++] = eo.getIndicatorId();
            }
            //图片新闻和文字新闻之间可以相互转换
            if (!AppUtil.isEmpty(columnTypeCode) &&
                (columnTypeCode.equals(BaseContentEO.TypeCode.articleNews.toString()) || columnTypeCode.equals(BaseContentEO.TypeCode.pictureNews.toString()))) {
                List<ColumnMgrEO> templist1 = columnConfigService.getColumnTreeByType(ids, columnId, BaseContentEO.TypeCode.articleNews.toString(), true);
                List<ColumnMgrEO> templist2 = columnConfigService.getColumnTreeByType(ids, columnId, BaseContentEO.TypeCode.pictureNews.toString(), true);

                list.addAll(templist1);
                //去除重复对象
                for (ColumnMgrEO eo2 : templist2) {
                    boolean flag = true;
                    for (ColumnMgrEO eo1 : templist1) {
                        if (eo1.getColumnStrId().equals(eo2.getColumnStrId())) {
                            flag = false;
                        }
                    }
                    if (flag) {
                        list.add(eo2);
                    }
                }

            } else {
                list = columnConfigService.getColumnTreeByType(ids, columnId, columnTypeCode, true);
            }
        } else {
            Long[] siteIds = siteRightsService.getCurUserSiteIds();
            if (siteIds == null || siteIds.length < 0) {
                throw new BaseRunTimeException(TipsMode.Message.toString(), "站点权限为空");
            }
            //图片新闻和文字新闻之间可以相互复制
            if (!AppUtil.isEmpty(columnTypeCode) &&
                (columnTypeCode.equals(BaseContentEO.TypeCode.articleNews.toString()) || columnTypeCode.equals(BaseContentEO.TypeCode.pictureNews.toString()))) {
                List<ColumnMgrEO> templist1 = columnConfigService.getColumnTreeByType(siteIds, columnId, BaseContentEO.TypeCode.articleNews.toString(), true);
                List<ColumnMgrEO> templist2 = columnConfigService.getColumnTreeByType(siteIds, columnId, BaseContentEO.TypeCode.pictureNews.toString(), true);

                list.addAll(templist1);
                //去除重复对象
                for (ColumnMgrEO eo2 : templist2) {
                    boolean flag = true;
                    for (ColumnMgrEO eo1 : templist1) {
                        if (eo1.getColumnStrId().equals(eo2.getColumnStrId())) {
                            flag = false;
                        }
                    }
                    if (flag) {
                        list.add(eo2);
                    }
                }


            } else {
                list = columnConfigService.getColumnTreeByType(siteIds, columnId, columnTypeCode, true);
            }
        }

        return getObject(resetList(list));
    }

    /**
     * 过滤脏数据
     * @param list
     * @return
     */
    private List<ColumnMgrEO> resetList(List<ColumnMgrEO> list) {
        //对list做脏数据过滤,如果没有找到父节点的则不加到list中
        Map<Long,Object> keyMap = new HashMap<Long, Object>(list.size());
        List<ColumnMgrEO> newList = new ArrayList<ColumnMgrEO>(list.size());

        for (ColumnMgrEO mgr:list) {
            keyMap.put(mgr.getIndicatorId(),true);
        }
        //遍历list
        for (ColumnMgrEO mgr:list) {
            if (mgr.getIsParent() == 1 || keyMap.get(mgr.getParentId()) != null) {
                newList.add(mgr);
            }
        }

        return newList;
    }

    /**
     * 根据栏目类型获取该站点下的栏目树-新闻移动专用
     * 1、为内容管理提供接口
     *
     * @param columnId
     * @param columnTypeCode
     * @return
     */
    @RequestMapping("getMoveColumnTreeByType")
    @ResponseBody
    public Object getMoveColumnTreeByType(Long columnId, String columnTypeCode) {
        if (AppUtil.isEmpty(columnTypeCode)) {
            return getObject();
        }
        List<ColumnMgrEO> list = new ArrayList<ColumnMgrEO>();
        if (LoginPersonUtil.isSuperAdmin() || LoginPersonUtil.isRoot()) {
            List<IndicatorEO> siteList = getList(IndicatorEO.class, CacheGroup.CMS_TYPE.toString(), IndicatorEO.Type.CMS_Site.toString());
            List<IndicatorEO> siteList1 = getList(IndicatorEO.class, CacheGroup.CMS_TYPE.toString(), IndicatorEO.Type.SUB_Site.toString());
            if (siteList != null && siteList1 != null) {
                siteList.addAll(siteList1);
            } else if (siteList == null && siteList1 != null) {
                siteList = new ArrayList<IndicatorEO>();
                siteList.addAll(siteList1);
            }
            Long[] ids = new Long[siteList.size()];
            int i = 0;
            for (IndicatorEO eo : siteList) {
                ids[i++] = eo.getIndicatorId();
            }
            //图片新闻和文字新闻之间可以相互转换
            if (!AppUtil.isEmpty(columnTypeCode) &&
                (columnTypeCode.equals(BaseContentEO.TypeCode.articleNews.toString()) || columnTypeCode.equals(BaseContentEO.TypeCode.pictureNews.toString()))) {
                List<ColumnMgrEO> templist1 = columnConfigService.getColumnTreeByType(ids, columnId, BaseContentEO.TypeCode.articleNews.toString(), true);
                List<ColumnMgrEO> templist2 = columnConfigService.getColumnTreeByType(ids, columnId, BaseContentEO.TypeCode.pictureNews.toString(), true);

                list.addAll(templist1);
                //去除重复对象
                for (ColumnMgrEO eo2 : templist2) {
                    boolean flag = true;
                    for (ColumnMgrEO eo1 : templist1) {
                        if (eo1.getColumnStrId().equals(eo2.getColumnStrId())) {
                            flag = false;
                        }
                    }
                    if (flag) {
                        list.add(eo2);
                    }
                }


            } else {
                list = columnConfigService.getColumnTreeByType(ids, columnId, columnTypeCode, true);
            }
        } else {
            Long[] siteIds = siteRightsService.getCurUserSiteIds();
            if (siteIds == null || siteIds.length < 0) {
                throw new BaseRunTimeException(TipsMode.Message.toString(), "站点权限为空");
            }
            //图片新闻和文字新闻之间可以相互转换
            if (!AppUtil.isEmpty(columnTypeCode) &&
                (columnTypeCode.equals(BaseContentEO.TypeCode.articleNews.toString()) || columnTypeCode.equals(BaseContentEO.TypeCode.pictureNews.toString()))) {
                List<ColumnMgrEO> templist1 = columnConfigService.getColumnTreeByType(siteIds, columnId, BaseContentEO.TypeCode.articleNews.toString(), true);
                List<ColumnMgrEO> templist2 = columnConfigService.getColumnTreeByType(siteIds, columnId, BaseContentEO.TypeCode.pictureNews.toString(), true);

                list.addAll(templist1);
                //去除重复对象
                for (ColumnMgrEO eo2 : templist2) {
                    boolean flag = true;
                    for (ColumnMgrEO eo1 : templist1) {
                        if (eo1.getColumnStrId().equals(eo2.getColumnStrId())) {
                            flag = false;
                        }
                    }
                    if (flag) {
                        list.add(eo2);
                    }
                }


            } else {
                list = columnConfigService.getColumnTreeByType(siteIds, columnId, columnTypeCode, true);
            }
        }
        return getObject(resetList(list));
    }


    /**
     * 获取站点下某栏目类型的栏目树
     *
     * @param siteIds
     * @param columnTypeCode
     * @return
     */
    @RequestMapping("getSiteTreeByType")
    @ResponseBody
    public Object getSiteTreeByType(Long[] siteIds, String columnTypeCode) {
        if (siteIds == null || siteIds.length < 0) {
            throw new BaseRunTimeException(TipsMode.Message.toString(), "站点权限为空");
        }
        List<ColumnMgrEO> list = columnConfigService.getColumnTreeByType(siteIds, null, columnTypeCode, false);
        return getObject(list);
    }


    /**
     * 权限管理：获取所有的站点和栏目
     *
     * @return
     */
    @RequestMapping("getTree")
    @ResponseBody
    public List<ColumnMgrEO> getTree() {
        List<ColumnMgrEO> list = columnConfigService.getTree(null);
        if (list == null || list.size() == 0) {
            return new ArrayList();
        }
        return list;
    }


    /**
     * 得到一个站点下的所有栏目
     * 1、为栏目管理的"同步到栏目"提供栏目树
     *
     * @param name
     * @param siteId
     * @return
     */
    @RequestMapping("getAllColumn")
    @ResponseBody
    public Object getAllColumn(Long siteId, String columnTypeCode, String name) {
        if (siteId == null) {
            siteId = LoginPersonUtil.getSiteId();
        }
        List<ColumnMgrEO> list = columnConfigService.getAllColumnTree(siteId, name);
        if (list == null || list.size() == 0) {
            return getObject();
        }
        boolean flag1 = LoginPersonUtil.isSuperAdmin() || LoginPersonUtil.isRoot() || LoginPersonUtil.isSiteAdmin();
        if (flag1) {
            return getObject(list);
        } else {
            list = getColumnOpt(list);
        }
        return getObject(list);
    }

    @RequestMapping("getAllSites")
    @ResponseBody
    public List<IndicatorEO> getAllSites() {
        return siteConfigService.getAllSites();
    }

    /**
     * 得到一个站点下的所有栏目和该站点
     * 1、为栏目管理的"生成页面"提供栏目树
     *
     * @param name
     * @param siteId
     * @return
     */
    @RequestMapping("searchColumnTree")
    @ResponseBody
    public Object searchColumnTree(@RequestParam(value = "name", required = false, defaultValue = "") String name, @RequestParam(value = "siteId", required = false, defaultValue = "") Long siteId) {
        if (siteId == null) {
            siteId = LoginPersonUtil.getSiteId();
        }
        List<ColumnMgrEO> list = columnConfigService.searchColumnTree(siteId, name);
        if (list == null || list.size() == 0) {
            return getObject();
        }
        boolean flag1 = LoginPersonUtil.isSuperAdmin() || LoginPersonUtil.isRoot();
        if (flag1) {
            return getObject(list);
        } else {
            list = getColumnOpt(list);
        }
        return getObject(list);
    }

    /**
     * 根据站点Id，查找允许评论的栏目树
     *
     * @param indicatorId
     * @return
     */
    @RequestMapping("getColumnByIsComment")
    @ResponseBody
    public Object getColumnByIsComment(Long indicatorId) {
        List<ColumnMgrEO> list = columnConfigService.getColumnByIsComment(indicatorId);
        for (ColumnMgrEO li : list) {
            Long vo = commentService.getNumByColumn(0, li.getIndicatorId());
            if (vo != null) {
                li.setCommentNum(vo);
            }
        }
        return getObject(list);
    }

    /**
     * 获取政民互动或在线办事下的栏目
     *
     * @param indicatorId
     * @param columnTypeCode
     * @return
     */
    @RequestMapping("getVirtualColumn")
    @ResponseBody
    public Object getVirtualColumn(Long indicatorId, String columnTypeCode) {
        if (StringUtils.isEmpty(columnTypeCode)) {
            throw new BaseRunTimeException(TipsMode.Message.toString(), "参数错误");
        }
        Long siteId = LoginPersonUtil.getSiteId();
        System.out.println("站点ID>>>" + siteId);
        List<ColumnMgrEO> list = null;
        if (indicatorId == null) {
            List<ColumnMgrEO> plist = null;
            IndicatorEO siteEO = CacheHandler.getEntity(IndicatorEO.class, siteId);
            System.out.println("siteEO>>>" + AppUtil.isEmpty(siteEO));
            if (IndicatorEO.Type.CMS_Site.toString().equals(siteEO.getType())) {
                plist = CacheHandler.getList(ColumnMgrEO.class, CacheGroup.CMS_COLUMN_TYPE_CODE, columnTypeCode);
            } else {
                String[] columnTypeCodes = {columnTypeCode};
                indicatorId = LoginPersonUtil.getSiteId();
                plist = columnConfigService.getVirtualColumn(indicatorId, true, columnTypeCodes);
            }
            Long pId = null;
            if (plist != null && plist.size() > 0) {
                for (ColumnMgrEO eo : plist) {
                    if (siteId.equals(eo.getSiteId())) {
                        pId = eo.getIndicatorId();
                        break;
                    }
                }
            }
            if (pId != null) {
                list = columnConfigService.getColumnByParentId(pId, false);
            }

        } else {
            list = columnConfigService.getColumnByParentId(indicatorId, false);
        }

        boolean flag1 = LoginPersonUtil.isSuperAdmin() || LoginPersonUtil.isRoot() || LoginPersonUtil.isSiteAdmin();
        if (flag1) {
            return getObject(list);
        } else {
            list = getColumnOpt(list);
        }
        return getObject(list);
    }

    /**
     * 移动栏目的栏目树
     *
     * @param indicatorId
     * @param columnId
     * @return
     */
    @RequestMapping("getMoveTree")
    @ResponseBody
    public Object getMoveTree(Long indicatorId, Long columnId) {
        List<ColumnMgrEO> list = new ArrayList<ColumnMgrEO>();
        if (indicatorId == null) {
            return getObject(list);
        }
        list = columnConfigService.getMoveTree(indicatorId, columnId);
        return getObject(list);
    }

    /**
     * 根据栏目类型code值获取某站点下的栏目
     *
     * @param codes
     * @param siteId
     * @return
     */
    @RequestMapping("getByColumnTypeCodes")
    @ResponseBody
    public Object getByColumnTypeCodes(String codes, Long siteId) {
        String[] codesArr = null;
        if (!StringUtils.isEmpty(codes)) {
            codesArr = AppUtil.getStrings(codes, ",");
        }
        return getObject(columnConfigService.getByColumnTypeCodes(codesArr, siteId, null));
    }

    /**
     * 获取当前用户的栏目权限
     *
     * @param list
     * @return
     */
    public List<ColumnMgrEO> getColumnOpt(List<ColumnMgrEO> list) {
        if (list == null || list.size() == 0) {
            return list;
        }
        return siteRightsService.getCurUserColumnOpt(list);
    }

    /**
     * 获取当前用户的站点权限
     *
     * @param list
     * @return
     */
    public List<SiteMgrEO> getSiteOpt(List<SiteMgrEO> list) {
        if (list == null || list.size() == 0) {
            return list;
        }
        boolean b = LoginPersonUtil.isRoot();
        if (b) {//lonsun_root账号
            return list;
        }
        return siteRightsService.getCurUserSiteOpt(list);
    }

    /**
     * 根据父栏目获取所有子栏目
     *
     * @param columnId
     * @return
     */
    @RequestMapping("getColumnByParentId")
    @ResponseBody
    public Object getColumnByParentId(Long columnId) {
        List<ColumnMgrEO> list = null;
        list = columnConfigService.getColumnByParentId(columnId, false);
        return getObject(list);
    }

}