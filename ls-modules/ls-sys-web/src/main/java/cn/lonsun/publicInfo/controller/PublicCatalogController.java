/*
 * PublicCatalogController.java         2015年12月5日 <br/>
 *
 * Copyright (c) 1994-1999 AnHui LonSun, Inc. <br/>
 * All rights reserved.	<br/>
 *
 * This software is the confidential and proprietary information of AnHui	<br/>
 * LonSun, Inc. ("Confidential Information").  You shall not	<br/>
 * disclose such Confidential Information and shall use it only in	<br/>
 * accordance with the terms of the license agreement you entered into	<br/>
 * with Sun. <br/>
 */

package cn.lonsun.publicInfo.controller;

import cn.lonsun.activemq.MessageSender;
import cn.lonsun.activemq.MessageSenderUtil;
import cn.lonsun.base.anno.DbInject;
import cn.lonsun.cache.client.CacheGroup;
import cn.lonsun.cache.client.CacheHandler;
import cn.lonsun.common.util.AppUtil;
import cn.lonsun.content.internal.entity.ContentReferRelationEO;
import cn.lonsun.content.internal.service.IBaseContentService;
import cn.lonsun.content.internal.service.IContentReferRelationService;
import cn.lonsun.core.base.controller.BaseController;
import cn.lonsun.core.util.CSVUtils;
import cn.lonsun.core.util.Pagination;
import cn.lonsun.indicator.internal.entity.IndicatorEO;
import cn.lonsun.message.internal.entity.MessageEnum;
import cn.lonsun.message.internal.entity.MessageStaticEO;
import cn.lonsun.publicInfo.internal.dao.IPublicCatalogDao;
import cn.lonsun.publicInfo.internal.entity.OrganConfigEO;
import cn.lonsun.publicInfo.internal.entity.PublicCatalogEO;
import cn.lonsun.publicInfo.internal.entity.PublicCatalogOrganRelEO;
import cn.lonsun.publicInfo.internal.service.IOrganConfigService;
import cn.lonsun.publicInfo.internal.service.IPublicCatalogOrganRelService;
import cn.lonsun.publicInfo.internal.service.IPublicCatalogService;
import cn.lonsun.publicInfo.vo.OrganCatalogQueryVO;
import cn.lonsun.publicInfo.vo.OrganVO;
import cn.lonsun.rbac.internal.entity.OrganEO;
import cn.lonsun.rbac.internal.service.IOrganService;
import cn.lonsun.site.site.internal.entity.SiteConfigEO;
import cn.lonsun.site.site.internal.service.IColumnConfigService;
import cn.lonsun.util.DataDictionaryUtil;
import cn.lonsun.util.ExcelUtil;
import cn.lonsun.util.LoginPersonUtil;
import cn.lonsun.util.PublicCatalogUtil;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.*;

/**
 * 公开目录 <br/>
 *
 * @author fangtinghua <br/>
 * @version v1.0 <br/>
 * @date 2015年12月5日 <br/>
 */
@Controller
@RequestMapping("/public/catalog")
public class PublicCatalogController extends BaseController {

    @Resource
    private IOrganService organService;
    @Resource
    private IOrganConfigService organConfigService;
    @Resource
    private IPublicCatalogService publicCatalogService;
    @Resource
    private IColumnConfigService columnConfigService;
    @Resource
    private IPublicCatalogOrganRelService publicCatalogOrganRelService;
    @DbInject("publicCatalog")
    private IPublicCatalogDao publicCatalogDao;
    @Resource
    private IContentReferRelationService contentReferRelationService;
    @Resource
    private IBaseContentService baseContentService;

    /**
     * 转向公共目录首页
     *
     * @return
     * @author fangtinghua
     */
    @RequestMapping("index")
    public String index(ModelMap map) {
        map.put("attributeList", DataDictionaryUtil.getDDList("public_catalog_attribute"));
        return "public/catalog/index";
    }

    /**
     * 转向单位与目录关系首页
     *
     * @return
     * @author fangtinghua
     */
    @RequestMapping("relIndex")
    public String relIndex() {
        return "public/catalog/relIndex";
    }

    /**
     * 选择栏目
     *
     * @return
     * @author fangtinghua
     */
    @RequestMapping("getPublicCatalogSelect")
    public String getPublicCatalogSelect() {
        return "/public/catalog/catalog_select";
    }

    /**
     * 选择栏目根据单位
     *
     * @return
     * @author fangtinghua
     */
    @RequestMapping("getPublicCatalogSelectWithOrgan")
    public String getPublicCatalogSelectWithOrgan() {
        return "/public/catalog/catalog_organ_select";
    }

    /**
     * 获取引用信息公开目录
     * @param catId
     * @param referOrganCatIds
     * @param map
     * @return
     */
    @RequestMapping("getReferOrganCats")
    public String getReferOrganCats(Long catId,String referOrganCatIds,ModelMap map) {
        map.put("catId",catId);
        map.put("referOrganCatIds",referOrganCatIds);
        return "/public/catalog/refer_organ_cats";
    }

    /**
     * 查询单位目录树
     *
     * @param queryVO
     * @return
     * @author fangtinghua
     */
    @ResponseBody
    @RequestMapping("getOrganCatalogTree")
    public Object getOrganCatalogTree(OrganCatalogQueryVO queryVO) {
        if (null == queryVO.getSiteId()) {// 当没有传入站点时，使用当前站点
            queryVO.setSiteId(LoginPersonUtil.getSiteId());
        }
        return publicCatalogService.getOrganCatalogTree(queryVO);
    }

    /**
     * 公开目录的组织
     *
     * @return
     * @author fangtinghua
     */
    @ResponseBody
    @RequestMapping("getOrgansBySiteId")
    public Object getOrgansBySiteId() {
        Long siteId = LoginPersonUtil.getSiteId();
        SiteConfigEO siteConfigEO = CacheHandler.getEntity(SiteConfigEO.class, CacheGroup.CMS_INDICATORID, siteId);
        List<OrganVO> voList = new ArrayList<OrganVO>();
        if (StringUtils.isEmpty(siteConfigEO.getUnitIds())) {
            return voList;
        }
        List<OrganEO> organList = organService.getPublicOrgans(Long.valueOf(siteConfigEO.getUnitIds()));
        PublicCatalogUtil.filterOrgan(organList, voList);// 过滤单位信息
        PublicCatalogUtil.sortOrgan(voList);// 排序
        return voList;
    }

    /**
     * 根据单位id和父id查询树节点
     *
     * @param blankCat 是否查询所有空白目录
     * @param allCat 是否分页查询空白目录
     * @return
     * @author fangtinghua
     */
    @ResponseBody
    @RequestMapping("getCatalogsByOrganId")
    public Object getCatalogsByOrganId(Long organId, Long parentId, boolean blankCat, boolean allCat, Integer pageIndex, Integer pageSize) {
        // 查询出所有目录信息
        List<PublicCatalogEO> catalogList = CacheHandler.getList(PublicCatalogEO.class, CacheGroup.CMS_PARENTID, parentId);
        // 过滤出显示的
        PublicCatalogUtil.filterCatalogList(catalogList, organId, false);
        // 排序
        PublicCatalogUtil.sortCatalog(catalogList);

        if (allCat) {//分页
            return getBlankCatPage(organId, parentId, pageIndex, pageSize);
        }
        if (blankCat) {//查询所有空白目录 及父目录，前端递归去显示，只显示空白目录及其父目录
            return getAllBlankCat(organId, parentId,true);
        }
        return getObject(catalogList);
    }

    /**
     * 查询顶层目录树
     *
     * @return
     * @author fangtinghua
     */
    @ResponseBody
    @RequestMapping("getTopCatalogList")
    public Object getTopCatalogList() {
        return CacheHandler.getList(PublicCatalogEO.class, CacheGroup.CMS_PARENTID, 0);
    }

    /**
     * 查询公共目录树
     *
     * @return
     * @author fangtinghua
     */
    @ResponseBody
    @RequestMapping("getCatalogs")
    public Object getCatalogs(Long parentId) {
        return CacheHandler.getList(PublicCatalogEO.class, CacheGroup.CMS_TYPE, 1);
    }

    /**
     * 查询公共目录树
     *
     * @return
     * @author fangtinghua
     */
    @ResponseBody
    @RequestMapping("getCatalogsJSON")
    public Object getCatalogsJSON(Long parentId) {
        return getObject(CacheHandler.getList(PublicCatalogEO.class, CacheGroup.CMS_TYPE, 1));
    }

    /**
     * 根据父id查询树
     *
     * @return
     * @author fangtinghua
     */
    @ResponseBody
    @RequestMapping("getCatalogsByParentId")
    public Object getCatalogsByParentId(Long parentId) {
        return CacheHandler.getList(PublicCatalogEO.class, CacheGroup.CMS_PARENTID, parentId);
    }

    /**
     * 新增编辑
     *
     * @return
     * @author fangtinghua
     */
    @ResponseBody
    @RequestMapping("saveOrUpdate")
    public Object saveOrUpdate(PublicCatalogEO publicCatalogEO, Long organId) {
        if (StringUtils.isEmpty(publicCatalogEO.getName())) {
            return ajaxErr("名称不能为空！");
        }
        if (StringUtils.isEmpty(publicCatalogEO.getCode())) {
            return ajaxErr("编码不能为空！");
        }
        if (null == publicCatalogEO.getSortNum()) {
            return ajaxErr("排序号不能为空！");
        }

        boolean editFlag = false;
        if(!AppUtil.isEmpty(publicCatalogEO.getId())){
            editFlag = true;
        }


        //修改的时候，判断栏目引用是否存在闭环（循环引用）
        if(editFlag&&(!AppUtil.isEmpty(publicCatalogEO.getReferColumnIds())||!AppUtil.isEmpty(publicCatalogEO.getReferOrganCatIds()))){
            String organCatStr = organId+"_"+publicCatalogEO.getId();
            Map<String,String> resultMap = baseContentService.getReferColumnCats(publicCatalogEO.getReferColumnIds(),publicCatalogEO.getReferOrganCatIds(),organCatStr);
            String referOrganCatIds = resultMap.get("referOrganCatIds");
            String OrganCatId = organId+"_"+publicCatalogEO.getId();//当前站点的标识串
            //如果查询出来所有的引用栏目中包含当前修改的栏目，则存在循环引用，抛出异常
            if(!AppUtil.isEmpty(referOrganCatIds)&&referOrganCatIds.contains(OrganCatId)){
                return ajaxErr("目录引用配置错误，当前引用关系存在循环引用情况，请重新配置");
            }

        }

        String oldReferColumnIds = "";
        String oldReferOrganCatIds = "";
        if(editFlag){
            PublicCatalogEO oldPublicCatalogEO =  CacheHandler.getEntity(PublicCatalogEO.class, publicCatalogEO.getId());
            PublicCatalogUtil.filterCatalog(oldPublicCatalogEO, organId);
            oldReferColumnIds = oldPublicCatalogEO.getReferColumnIds();
            oldReferOrganCatIds = oldPublicCatalogEO.getReferOrganCatIds();
        }

        publicCatalogService.saveEntity(publicCatalogEO, organId);

        //取消栏目引用关系时删除栏目引用数
        if(editFlag){
            delColumnReferDatas(oldReferColumnIds,oldReferOrganCatIds,publicCatalogEO.getReferColumnIds(),
                    publicCatalogEO.getReferOrganCatIds(),organId,publicCatalogEO.getId());
        }

        // 发送消息
        Long siteId = LoginPersonUtil.getSiteId();
        MessageSender.sendMessage(new MessageStaticEO(siteId, null, null).setType(MessageEnum.PUBLISH.value()).setSource(MessageEnum.PUBLICINFO.value()));
        return getObject(publicCatalogEO);
    }

    /**
     * 取消栏目引用关系时删除栏目引用数
     * @param oldReferColumnIds
     * @param oldReferOrganCatIds
     * @param referColumnIds
     * @param referOrganCatIds
     * @param organId
     * @param catId
     */
    private void delColumnReferDatas(String oldReferColumnIds,String oldReferOrganCatIds,
                                     String referColumnIds,String referOrganCatIds,
                                     Long organId,Long catId){
        try {
            String organCatStr = organId+"_"+catId;
            Map<String,String> resultMap = baseContentService.getReferColumnCats(oldReferColumnIds,oldReferOrganCatIds,organCatStr);
            oldReferColumnIds = resultMap.get("referColumnIds");
            oldReferOrganCatIds = resultMap.get("referOrganCatIds");

            Map<String,String> resultMap_ = baseContentService.getReferColumnCats(referColumnIds,referOrganCatIds,organCatStr);
            referColumnIds = resultMap_.get("referColumnIds");
            referOrganCatIds = resultMap_.get("referOrganCatIds");
            referColumnIds = referColumnIds==null?"":referColumnIds;
            referOrganCatIds = referOrganCatIds==null?"":referOrganCatIds;

            List<Long> ids = new ArrayList<Long>();
            if(!AppUtil.isEmpty(oldReferColumnIds)){
                String[] columnIds = oldReferColumnIds.split(",");
                for(int i=0;i<columnIds.length;i++){
                    if(!referColumnIds.contains(columnIds[i])){//取消栏目引用关系，需删除引用数据
                        String[] columnSiteId = columnIds[i].split("_");
                        Long columnId = Long.valueOf(columnSiteId[0]);

                        Map<String,Object> param = new HashMap<String,Object>();
                        param.put("type", ContentReferRelationEO.TYPE.REFER.toString());
                        param.put("causeByColumnId", organId);
                        param.put("causeByCatId", catId);
                        param.put("columnId", columnId);
                        param.put("isColumnOpt", 1);//栏目引用过去的数据
                        List<ContentReferRelationEO> referRelationEOS = contentReferRelationService.getEntities(ContentReferRelationEO.class,param);
                        if(referRelationEOS!=null&&referRelationEOS.size()>0){
                            for(ContentReferRelationEO relationEO:referRelationEOS){
                                if(!ids.contains(relationEO.getReferId())){
                                    ids.add(relationEO.getReferId());
                                }
                            }
                        }


                        //查询通过上级栏目引用到当前栏目或者目录的数据
                        List<ContentReferRelationEO> referRelationEOS_ = contentReferRelationService.getByParentReferOrganCat(columnId,null,organId,catId);
                        if(referRelationEOS_!=null&&referRelationEOS_.size()>0){
                            for(ContentReferRelationEO relationEO:referRelationEOS_){
                                if(!ids.contains(relationEO.getReferId())){
                                    ids.add(relationEO.getReferId());
                                }
                            }
                        }

                    }
                }
            }

            if(!AppUtil.isEmpty(oldReferOrganCatIds)){
                String[] organCatIds = oldReferOrganCatIds.split(",");
                for(int i=0;i<organCatIds.length;i++){
                    if(!referOrganCatIds.contains(organCatIds[i])){//取消栏目引用关系，需删除引用数据
                        String[] organCatId = organCatIds[i].split("_");
                        Long targetOrganId = Long.valueOf(organCatId[0]);
                        Long targetCatId = Long.valueOf(organCatId[1]);

                        Map<String,Object> param = new HashMap<String,Object>();
                        param.put("type", ContentReferRelationEO.TYPE.REFER.toString());
                        param.put("causeByColumnId", organId);
                        param.put("causeByCatId", catId);
                        param.put("columnId", targetOrganId);
                        param.put("catId", targetCatId);
                        param.put("isColumnOpt", 1);//栏目引用过去的数据
                        List<ContentReferRelationEO> referRelationEOS = contentReferRelationService.getEntities(ContentReferRelationEO.class,param);
                        if(referRelationEOS!=null&&referRelationEOS.size()>0){
                            for(ContentReferRelationEO relationEO:referRelationEOS){
                                ids.add(relationEO.getReferId());
                            }
                        }


                        //查询通过上级栏目引用到当前栏目或者目录的数据
                        List<ContentReferRelationEO> referRelationEOS_ = contentReferRelationService.getByParentReferOrganCat(targetOrganId,targetCatId,organId,catId);
                        if(referRelationEOS_!=null&&referRelationEOS_.size()>0){
                            for(ContentReferRelationEO relationEO:referRelationEOS_){
                                if(!ids.contains(relationEO.getReferId())){
                                    ids.add(relationEO.getReferId());
                                }
                            }
                        }
                    }
                }
            }
            if(ids.size()>0){
                //删除数据
                String msg = baseContentService.delContent(ids.toArray(new Long[]{}));
                MessageSenderUtil.unPublishCopyNews(msg);
            }

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    /**
     * 隐藏或者显示
     *
     * @param relEO
     * @param join
     * @return
     * @author fangtinghua
     */
    @ResponseBody
    @RequestMapping("showOrHide")
    public Object showOrHide(PublicCatalogOrganRelEO relEO, boolean join) {
        List<Long> changeList = publicCatalogService.updateShowOrHideCatalog(relEO, join);
        // 发送消息
        Long siteId = LoginPersonUtil.getSiteId();
        MessageSender.sendMessage(new MessageStaticEO(siteId, null, null).setType(MessageEnum.PUBLISH.value()).setSource(MessageEnum.PUBLICINFO.value()));
        return getObject(changeList);
    }

    /**
     * 批量隐藏或者显示
     */
    @ResponseBody
    @RequestMapping("showsOrHides")
    public Object showOrHide(Long organId,Long parentId) {
        List<PublicCatalogEO> allBlankCat = getAllBlankCat(organId, parentId, false);
        List<PublicCatalogOrganRelEO> relEOList = new ArrayList<PublicCatalogOrganRelEO>();
        for(PublicCatalogEO catalogEO : allBlankCat){
            PublicCatalogOrganRelEO relEO = new PublicCatalogOrganRelEO();
            relEO.setCatId(catalogEO.getId());
            relEO.setOrganId(organId);
            relEO.setIsShow(false);
            relEOList.add(relEO);
        }
        for (PublicCatalogOrganRelEO relEO : relEOList) {
            publicCatalogService.updateShowOrHideCatalog(relEO, true);
        }
        // 发送消息
        Long siteId = LoginPersonUtil.getSiteId();
        MessageSender.sendMessage(new MessageStaticEO(siteId, null, null).setType(MessageEnum.PUBLISH.value()).setSource(MessageEnum.PUBLICINFO.value()));
        return getObject();
    }

    /**
     * 保存单位配置
     *
     * @param organConfigEO
     * @return
     * @author fangtinghua
     */
    @ResponseBody
    @RequestMapping("saveConfig")
    public Object saveConfig(OrganConfigEO organConfigEO) {
        if (null == organConfigEO.getCatId()) {
            return ajaxErr("必须选择单位目录！");
        }
        if (StringUtils.isEmpty(organConfigEO.getContentModelCode())) {
            return ajaxErr("必须选择内容模型！");
        }
        Long id = organConfigEO.getId();
        if (null != id) {
            organConfigService.updateEntity(organConfigEO);
        } else {
            organConfigService.saveEntity(organConfigEO);
        }
        CacheHandler.saveOrUpdate(OrganConfigEO.class, organConfigEO);// 更新缓存
        // 发送消息
        Long siteId = LoginPersonUtil.getSiteId();
        MessageSender.sendMessage(new MessageStaticEO(siteId, null, null).setType(MessageEnum.PUBLISH.value()).setSource(MessageEnum.PUBLICINFO.value()));
        return getObject(organConfigEO);
    }

    /**
     * 删除
     *
     * @param id
     * @param organId
     * @return
     * @author fangtinghua
     */
    @ResponseBody
    @RequestMapping("delete")
    public Object delete(Long id, Long organId) {
        publicCatalogService.delete(id, organId);
        // 发送消息
        Long siteId = LoginPersonUtil.getSiteId();
        MessageSender.sendMessage(new MessageStaticEO(siteId, null, null).setType(MessageEnum.PUBLISH.value()).setSource(MessageEnum.PUBLICINFO.value()));
        return getObject();
    }

    /**
     * 获取对象
     *
     * @param id
     * @return
     * @author fangtinghua
     */
    @ResponseBody
    @RequestMapping("getPublicCatalog")
    public Object getPublicCatalog(Long id, Long organId) {
        PublicCatalogEO publicCatalogEO = null;
        if (null != id) {
            publicCatalogEO = CacheHandler.getEntity(PublicCatalogEO.class, id);
            PublicCatalogUtil.filterCatalog(publicCatalogEO, organId);
            //判断是否是引用栏目
            String columnSiteStr = organId+"_"+publicCatalogEO.getId();
            Long sourceColumnCount = columnConfigService.getSourceOrganCatCount(columnSiteStr);
            if(sourceColumnCount!=null&&sourceColumnCount.intValue()>0){
                publicCatalogEO.setIsReferColumn(1);
            }

            Long sourceCatCount = publicCatalogOrganRelService.getSourceOrganCatCount(columnSiteStr);
            if(sourceCatCount!=null&&sourceCatCount.intValue()>0){
                publicCatalogEO.setIsReferColumn(1);
            }
        } else {
            publicCatalogEO = new PublicCatalogEO();
        }
        return publicCatalogEO;
    }

    /**
     * 转向目录编辑主页
     *
     * @return
     * @author fangtinghua
     */
    @RequestMapping("edit")
    public String edit(Long type, ModelMap map) {
        map.put("type", type);
        map.put("attributeList", DataDictionaryUtil.getDDList("public_catalog_attribute"));
        return "public/catalog/edit";
    }

    /**
     * 转向目录编辑主页
     *
     * @return
     * @author fangtinghua
     */
    @RequestMapping("toUpload")
    public String upload() {
        return "public/catalog/upload";
    }

    @ResponseBody
    @RequestMapping("/upload")
    public Object uploadEE(@RequestParam(value = "file", required = false) MultipartFile file) throws IOException {
        // 配置字段与列对应关系
        Map<Integer, String> map = new HashMap<Integer, String>();
        map.put(0, "name");//名称
        map.put(1, "rowIndex");//父目录对应行号
        int startRow = 1;//开始行号
        Map<String, List<PublicCatalogEO>> resultMap = ExcelUtil.readExcel(PublicCatalogEO.class, file.getOriginalFilename(), file.getInputStream(), map, startRow);
        publicCatalogService.saveImportCatalogList(resultMap, startRow);// 导入
        return getObject();
    }

    /**
     * 转向单位机构配置页面
     *
     * @return
     * @author fangtinghua
     */
    @RequestMapping("config")
    public String config() {
        return "public/catalog/config";
    }

    /**
     * 查询url连接地址
     *
     * @param modelMap
     * @return
     */
    @RequestMapping("urlIndex")
    public String urlIndex(ModelMap modelMap) {
        Long siteId = LoginPersonUtil.getSiteId();
        SiteConfigEO siteConfigEO = CacheHandler.getEntity(SiteConfigEO.class, CacheGroup.CMS_INDICATORID, siteId);
        List<OrganEO> organList = organService.getPublicOrgans(Long.valueOf(siteConfigEO.getUnitIds()));
        List<OrganVO> organVOList = new ArrayList<OrganVO>();
        PublicCatalogUtil.filterOrgan(organList, organVOList);// 过滤单位列表
        PublicCatalogUtil.sortOrgan(organVOList);// 排序
        modelMap.put("organList", organVOList);
        return "/public/catalog/url";
    }

    /**
     * 查询连接地址
     *
     * @param organId
     * @return
     */
    @ResponseBody
    @RequestMapping("getUrlPage")
    public Object getPage(Long organId) {
        Long siteId = LoginPersonUtil.getSiteId();
        IndicatorEO indicatorEO = CacheHandler.getEntity(IndicatorEO.class, siteId);
        String prefixUrl = indicatorEO.getUri() + "/public/column/" + organId;
        List<JSONObject> resultList = new ArrayList<JSONObject>();
        //放入指南
        JSONObject jsonobject = new JSONObject();
        jsonobject.put("name", "政府信息公开指南");
        jsonobject.put("url", prefixUrl + "?type=2");
        resultList.add(jsonobject);
        //放入制度
        jsonobject = new JSONObject();
        jsonobject.put("name", "政府信息公开制度");
        jsonobject.put("url", prefixUrl + "?type=1&action=list");
        resultList.add(jsonobject);
        //放入年报
        jsonobject = new JSONObject();
        jsonobject.put("name", "政府信息公开年报");
        jsonobject.put("url", prefixUrl + "?type=3&action=list");
        resultList.add(jsonobject);
        //放入依申请公开目录
        jsonobject = new JSONObject();
        jsonobject.put("name", "依申请公开目录");
        jsonobject.put("url", prefixUrl + "?type=5&active=0");
        resultList.add(jsonobject);
        //放入收费标准
        jsonobject = new JSONObject();
        jsonobject.put("name", "收费标准");
        jsonobject.put("url", prefixUrl + "?type=5&active=1");
        resultList.add(jsonobject);
        //放入申请信息流程图
        jsonobject = new JSONObject();
        jsonobject.put("name", "申请信息流程图");
        jsonobject.put("url", prefixUrl + "?type=5&active=3");
        resultList.add(jsonobject);
        //放入在线申请
        jsonobject = new JSONObject();
        jsonobject.put("name", "在线申请");
        jsonobject.put("url", prefixUrl + "?type=5&active=4");
        resultList.add(jsonobject);
        //放入依申请公开查询
        jsonobject = new JSONObject();
        jsonobject.put("name", "依申请公开查询");
        jsonobject.put("url", prefixUrl + "?type=5&active=5");
        resultList.add(jsonobject);
        //放入依申请公开统计
        jsonobject = new JSONObject();
        jsonobject.put("name", "依申请公开统计");
        jsonobject.put("url", prefixUrl + "?type=5&active=6");
        resultList.add(jsonobject);
        //查询组配分类
        OrganConfigEO organConfigEO = CacheHandler.getEntity(OrganConfigEO.class, CacheGroup.CMS_ORGAN_ID, organId);
        if (null != organConfigEO && null != organConfigEO.getCatId()) {
            List<PublicCatalogEO> catalogList = publicCatalogService.getAllChildListByCatId(organConfigEO.getCatId());
            PublicCatalogUtil.filterCatalogList(catalogList, organId, true);
            PublicCatalogUtil.sortCatalog(catalogList);
            // 循环放入目录
            if (null != catalogList && !catalogList.isEmpty()) {
                for (PublicCatalogEO eo : catalogList) {
                    jsonobject = new JSONObject();
                    jsonobject.put("name", "公开目录(" + eo.getName() + ")");
                    jsonobject.put("url", prefixUrl + "?type=4&catId=" + eo.getId() + "&action=list");
                    resultList.add(jsonobject);
                }
            }
        }
        return getObject(resultList);
    }


    /**
     * 分页查询空白目录
     * @param organId
     * @param parentId
     * @param pageIndex
     * @param pageSize
     * @return
     */
    private Pagination getBlankCatPage(Long organId, Long parentId, Integer pageIndex, Integer pageSize) {
        List<PublicCatalogEO> catalogList = publicCatalogService.getOrganCatalog(organId);
        Map<Long, PublicCatalogEO> catIdMap = new HashMap<Long, PublicCatalogEO>();
        List<Object> haveContentCatList = publicCatalogService.getAllCatIdHaveContent(organId, parentId);
        for (Object o : haveContentCatList) {
            Object[] obj = (Object[]) o;
            Long catId = Long.valueOf(String.valueOf(obj[0]));
            catIdMap.put(catId, null);
        }
        List<PublicCatalogEO> catalogEOS_exclude = new ArrayList<PublicCatalogEO>();
        for (PublicCatalogEO catalogEO : catalogList) {
            if (!catIdMap.containsKey(catalogEO.getId())) {
                if (!catalogEO.getIsParent()) {
                    catalogEO.setIsBlankCat(1);//设为1表示该目录是空白目录
                    String path = PublicCatalogUtil.getCatalogPath(organId, catalogEO.getId());//设置空白目录的全路径
                    catalogEO.setCatPath(path);
                    catalogEOS_exclude.add(catalogEO);
                    String catalogSortStr = PublicCatalogUtil.getCatalogSortStr(organId, catalogEO.getId());
                    catalogEO.setSortStr(catalogSortStr);//增加排序字符串
                }
            }
        }
        Collections.sort(catalogEOS_exclude, new Comparator<PublicCatalogEO>() {
            @Override
            public int compare(PublicCatalogEO seft, PublicCatalogEO that) {
                return seft.getSortStr().compareTo(that.getSortStr());
            }
        });
        Pagination page = new Pagination();
        int startRow = pageIndex * pageSize;
        int endRow = (pageIndex + 1) * pageSize;
        if (catalogEOS_exclude.size() < endRow) {
            endRow = catalogEOS_exclude.size();
        }
        page.setData(catalogEOS_exclude.subList(startRow, endRow));
        page.setTotal(Long.parseLong(String.valueOf(catalogEOS_exclude.size())));
        page.setPageSize(pageSize);
        page.setPageIndex(Long.valueOf(pageIndex));
        return page;
    }

    /**
     * 获取所有空白目录及所有父目录，前端处理只保留空白目录及其父目录
     * @param organId
     * @param parentId
     * @return
     */
    private List<PublicCatalogEO> getAllBlankCat(Long organId, Long parentId,boolean hasParent) {
        List<PublicCatalogEO> childList = publicCatalogDao.getAllChildListByCatId(parentId);//
        PublicCatalogUtil.filterCatalogList(childList, organId, false);// 过滤目录列表//
        Map<Long, PublicCatalogEO> catIdMap = new HashMap<Long, PublicCatalogEO>();
        List<Object> haveContentCatList = publicCatalogService.getAllCatIdHaveContent(organId, null);
        for (Object o : haveContentCatList) {
            Object[] obj = (Object[]) o;
            Long catId = Long.valueOf(String.valueOf(obj[0]));
            catIdMap.put(catId, null);
        }
        List<PublicCatalogEO> list = new ArrayList<PublicCatalogEO>();//
        for (PublicCatalogEO catalogEO : childList) {
            if(hasParent){
                if (catalogEO.getIsParent()) {
                    list.add(catalogEO);
                }
            }
            if (!catIdMap.containsKey(catalogEO.getId())) {
                if (!catalogEO.getIsParent()) {
                    catalogEO.setIsBlankCat(1);//设为1表示该目录是空白目录
                    String path = PublicCatalogUtil.getCatalogPath(organId, catalogEO.getId());//设置空白目录的全路径
                    catalogEO.setCatPath(path);
                    list.add(catalogEO);//
                }
            }
        }
        PublicCatalogUtil.sortCatalog(list);
        return list;
    }

    /**
     * 导出链接
     * 信息公开目录链接
     * @param organId
     * @param response
     */
    @RequestMapping("exportUrl")
    public void exportUrl(Long organId,String organName, HttpServletResponse response) {
        Long siteId = LoginPersonUtil.getSiteId();
        IndicatorEO indicatorEO = CacheHandler.getEntity(IndicatorEO.class, siteId);
        String prefixUrl = indicatorEO.getUri() + "/public/column/" + organId;
        List<JSONObject> resultList = new ArrayList<JSONObject>();
        //放入指南
        JSONObject jsonobject = new JSONObject();
        jsonobject.put("name", "政府信息公开指南");
        jsonobject.put("url", prefixUrl + "?type=2");
        resultList.add(jsonobject);
        //放入制度
        jsonobject = new JSONObject();
        jsonobject.put("name", "政府信息公开制度");
        jsonobject.put("url", prefixUrl + "?type=1&action=list");
        resultList.add(jsonobject);
        //放入年报
        jsonobject = new JSONObject();
        jsonobject.put("name", "政府信息公开年报");
        jsonobject.put("url", prefixUrl + "?type=3&action=list");
        resultList.add(jsonobject);
        //放入依申请公开目录
        jsonobject = new JSONObject();
        jsonobject.put("name", "依申请公开目录");
        jsonobject.put("url", prefixUrl + "?type=5&active=0");
        resultList.add(jsonobject);
        //放入收费标准
        jsonobject = new JSONObject();
        jsonobject.put("name", "收费标准");
        jsonobject.put("url", prefixUrl + "?type=5&active=1");
        resultList.add(jsonobject);
        //放入申请信息流程图
        jsonobject = new JSONObject();
        jsonobject.put("name", "申请信息流程图");
        jsonobject.put("url", prefixUrl + "?type=5&active=3");
        resultList.add(jsonobject);
        //放入在线申请
        jsonobject = new JSONObject();
        jsonobject.put("name", "在线申请");
        jsonobject.put("url", prefixUrl + "?type=5&active=4");
        resultList.add(jsonobject);
        //放入依申请公开查询
        jsonobject = new JSONObject();
        jsonobject.put("name", "依申请公开查询");
        jsonobject.put("url", prefixUrl + "?type=5&active=5");
        resultList.add(jsonobject);
        //放入依申请公开统计
        jsonobject = new JSONObject();
        jsonobject.put("name", "依申请公开统计");
        jsonobject.put("url", prefixUrl + "?type=5&active=6");
        resultList.add(jsonobject);
        //查询组配分类
        OrganConfigEO organConfigEO = CacheHandler.getEntity(OrganConfigEO.class, CacheGroup.CMS_ORGAN_ID, organId);
        if (null != organConfigEO && null != organConfigEO.getCatId()) {
            List<PublicCatalogEO> catalogList = publicCatalogService.getAllChildListByCatId(organConfigEO.getCatId());
            PublicCatalogUtil.filterCatalogList(catalogList, organId, true);
            PublicCatalogUtil.sortCatalog(catalogList);
            // 循环放入目录
            if (null != catalogList && !catalogList.isEmpty()) {
                for (PublicCatalogEO eo : catalogList) {
                    jsonobject = new JSONObject();
                    jsonobject.put("name", "公开目录(" + eo.getName() + ")");
                    jsonobject.put("url", prefixUrl + "?type=4&catId=" + eo.getId() + "&action=list");
                    resultList.add(jsonobject);
                }
            }
        }
        // 文件头
        String[] titles = new String[] { "上级指标id","指标id", "指标名称", "指标网址" };

        // 内容
        List<String[]> datas = new ArrayList<String[]>();
        if (resultList != null && resultList.size() > 0) {
            int i = 1;
            for (JSONObject jo : resultList) {
                String[] row1 = new String[4];
                row1[0] = "";
                row1[1] = "";
                row1[2] = jo.getString("name") + "";
                row1[3] = jo.getString("url");
                datas.add(row1);
            }
        }
        // 导出
        String name = organName + "";
        try {
            CSVUtils.download(name, titles, datas, response);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}