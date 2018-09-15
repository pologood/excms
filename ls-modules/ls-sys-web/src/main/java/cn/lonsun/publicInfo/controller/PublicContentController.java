/*
 * PublicContentController.java         2015年12月15日 <br/>
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

import cn.lonsun.activemq.MessageSenderUtil;
import cn.lonsun.cache.client.CacheGroup;
import cn.lonsun.cache.client.CacheHandler;
import cn.lonsun.common.util.AppUtil;
import cn.lonsun.content.internal.entity.BaseContentEO;
import cn.lonsun.content.internal.entity.ContentReferRelationEO;
import cn.lonsun.content.internal.service.IBaseContentService;
import cn.lonsun.content.internal.service.IContentReferRelationService;
import cn.lonsun.content.vo.ContentPageVO;
import cn.lonsun.content.vo.CopyReferVO;
import cn.lonsun.content.vo.SortUpdateVO;
import cn.lonsun.core.base.controller.BaseController;
import cn.lonsun.core.base.entity.AMockEntity;
import cn.lonsun.core.exception.BaseRunTimeException;
import cn.lonsun.core.util.Pagination;
import cn.lonsun.core.util.TipsMode;
import cn.lonsun.message.internal.entity.MessageEnum;
import cn.lonsun.message.internal.entity.MessageStaticEO;
import cn.lonsun.publicInfo.internal.entity.OrganConfigEO;
import cn.lonsun.publicInfo.internal.entity.PublicApplyEO;
import cn.lonsun.publicInfo.internal.entity.PublicCatalogEO;
import cn.lonsun.publicInfo.internal.entity.PublicContentEO;
import cn.lonsun.publicInfo.internal.service.IPublicCatalogService;
import cn.lonsun.publicInfo.internal.service.IPublicContentService;
import cn.lonsun.publicInfo.vo.OrganVO;
import cn.lonsun.publicInfo.vo.PublicContentQueryVO;
import cn.lonsun.publicInfo.vo.PublicContentVO;
import cn.lonsun.rbac.internal.entity.OrganEO;
import cn.lonsun.rbac.internal.service.IOrganService;
import cn.lonsun.site.contentModel.internal.entity.ContentModelEO;
import cn.lonsun.site.contentModel.internal.entity.ModelTemplateEO;
import cn.lonsun.site.site.internal.entity.SiteConfigEO;
import cn.lonsun.solr.SolrFactory;
import cn.lonsun.system.systemlog.internal.entity.CmsLogEO;
import cn.lonsun.util.*;
import com.alibaba.fastjson.JSON;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import java.util.*;

/**
 * 公开内容 <br/>
 *
 * @author fangtinghua <br/>
 * @version v1.0 <br/>
 * @date 2015年12月15日 <br/>
 */
@Controller
@RequestMapping("/public/content")
public class PublicContentController extends BaseController {

    @Resource
    private IBaseContentService baseContentService;
    @Resource
    private IPublicContentService publicContentService;
    @Resource
    private IPublicCatalogService publicCatalogService;
    @Resource
    private IOrganService organService;
    @Resource
    private IContentReferRelationService contentReferRelationService;
    @Value("${ffmpeg.path}")
    private String path;

    @RequestMapping("index")
    public String index(String type, String verify, ModelMap map) {
        map.put("type", type);
        map.put("verify", verify);
        return "/public/content/index";
    }

    @RequestMapping("list")
    public String content(String type, Long organId, Long catId, ModelMap map) {
        map.put("type", type);
        map.put("organId", organId);
        map.put("catId", catId);
        return "/public/content/list";
    }

    // 获取内容模型
    @ResponseBody
    @RequestMapping("getModelConfig")
    public Object getModelConfig(Long organId) {
        OrganConfigEO organConfigEO = CacheHandler.getEntity(OrganConfigEO.class, CacheGroup.CMS_ORGAN_ID, organId);
        if (null == organConfigEO || StringUtils.isEmpty(organConfigEO.getContentModelCode())) {
            return ajaxErr("本单位没有配置信息公开内容模型！");
        }
        return getObject(ModelConfigUtil.getCongfigVO(organConfigEO.getContentModelCode(), LoginPersonUtil.getSiteId()));
    }

    /**
     * 获取分页
     *
     * @param queryVO
     * @return
     * @author fangtinghua
     */
    @ResponseBody
    @RequestMapping("getPage")
    public Object getPage(PublicContentQueryVO queryVO) {
        return publicContentService.getPagination(queryVO);
    }

    /**
     * 获取单页面内容
     *
     * @return
     * @author fangtinghua
     */
    @ResponseBody
    @RequestMapping("getPublicContentByVO")
    public Object getPublicContentByVO(PublicContentQueryVO queryVO) {
        PublicContentVO vo = publicContentService.getPublicContent(queryVO);
        if (null == vo) {
            vo = new PublicContentVO();
            Date date = new Date();
            vo.setPublishDate(date);
            vo.setSortDate(date);
        } else {
            vo.setSortDate(new Date(vo.getSortNum()));
        }
        return getObject(vo);
    }

    /**
     * 获取主动公开内容
     *
     * @return
     * @author fangtinghua
     */
    @ResponseBody
    @RequestMapping("getPublicContent")
    public Object getPublicContent(Long contentId, Long organId) {
        PublicContentVO vo = null;
        if (null != contentId) {
            vo = publicContentService.getPublicContent(contentId);
            vo.setReferedNews(contentReferRelationService.checkIsRefered(vo.getContentId()));//判断是否被引用新闻
            vo.setReferNews(contentReferRelationService.checkIsRefer(vo.getContentId()));//判断是否引用新闻
            vo.setSortDate(new Date(vo.getSortNum()));//将排序号转化为排序时间
        } else {
            Date date = new Date();
            vo = new PublicContentVO();
            vo.setVideoStatus(0);
            vo.setIndexNum(publicContentService.getIndexNum(organId));// 设置文号
            vo.setPublishDate(date);
            vo.setSortDate(date);
        }
        return getObject(vo);
    }

    /**
     * 跳转到编辑页面
     *
     * @param map
     * @return
     * @author fangtinghua
     */
    @RequestMapping("edit")
    public String edit(@RequestParam(value = "contentId", required = false) Long contentId, String type, String verify,
                       @RequestParam(value = "isLink", defaultValue = "false") boolean isLink, ModelMap map) {
        map.put("contentId", contentId);
        map.put("type", type);
        map.put("isLink", isLink);
        map.put("verify", verify);
        return "/public/content/edit";
    }

    /**
     * 新增编辑
     *
     * @param vo
     * @return
     * @author fangtinghua
     */
    @ResponseBody
    @RequestMapping("saveOrUpdate")
    public Object saveOrUpdate(PublicContentVO vo) {
        boolean addFlag = false;//是否新增，用于同步引用新闻
        Long id = vo.getId();
        if(vo!=null&&AppUtil.isEmpty(vo.getId())){
            addFlag = true;
        }
        Integer isPublish = vo.getIsPublish();
        if (StringUtils.isEmpty(vo.getTitle())) {
            return ajaxErr("标题不能为空！");
        }
        if(null != vo.getContent() && vo.getContent().contains("class=\"video-player\"")){
            if (vo.getVideoStatus().equals(0) && vo.getIsPublish() == 1) {//
                return ajaxErr("文章中有视频未转换，不能进行发布操作！");
            }
        }else{
            vo.setVideoStatus(100);
        }
        vo.setContent(HotWordsCheckUtil.replaceAll(vo.getSiteId(), vo.getContent()));
        publicContentService.saveEntity(vo);
        if (vo.getVideoStatus().equals(0)) {// 转换视频
            VideoToMp4ConvertUtil.transfer(path, vo.getOrganId(), vo.getContentId());
        }
        MessageStaticEO message = new MessageStaticEO();
        message.setSiteId(vo.getSiteId());
        message.setColumnId(vo.getOrganId());
        message.setContentIds(new Long[]{vo.getContentId()});
        message.setSource(MessageEnum.PUBLICINFO.value());
        if(isPublish.intValue() == 1){
            message.setType(MessageEnum.PUBLISH.value());
            MessageSenderUtil.publishContent(message, Integer.valueOf(1));
        }else{
            message.setType(MessageEnum.UNPUBLISH.value());
            MessageSenderUtil.publishContent(message, Integer.valueOf(2));
        }
        OrganEO organ = organService.getEntity(OrganEO.class, vo.getOrganId());
        if (organ != null) {
            if ((null != id) && (isPublish.intValue() == 1)) {
                if (vo.getType().equals(PublicContentEO.Type.PUBLIC_GUIDE.toString())) {
                    SysLog.log("信息公开指南：修改并发布内容（" + vo.getTitle() + "），目录（" + organ.getName() + "）", "PublicContentEO", CmsLogEO.Operation.Update.toString());
                }
                if (vo.getType().equals(PublicContentEO.Type.PUBLIC_ANNUAL_REPORT.toString())) {
                    SysLog.log("信息公开年报：修改并发布内容（" + vo.getTitle() + "），目录（" + organ.getName() + "-公开年报）", "PublicContentEO", CmsLogEO.Operation.Update.toString());
                }
            } else if ((null != id) && (isPublish.intValue() != 1)) {
                if (vo.getType().equals(PublicContentEO.Type.PUBLIC_GUIDE.toString())) {
                    SysLog.log("信息公开指南：修改内容（" + vo.getTitle() + "），目录（" + organ.getName() + "）", "PublicContentEO", CmsLogEO.Operation.Update.toString());
                }
                if (vo.getType().equals(PublicContentEO.Type.PUBLIC_ANNUAL_REPORT.toString())) {
                    SysLog.log("信息公开年报：修改内容（" + vo.getTitle() + "），目录（" + organ.getName() + "-公开年报）", "PublicContentEO", CmsLogEO.Operation.Update.toString());
                }
            } else if ((null == id) && (isPublish.intValue() != 1)) {
                if (vo.getType().equals(PublicContentEO.Type.PUBLIC_GUIDE.toString())) {
                    SysLog.log("信息公开指南：添加内容（" + vo.getTitle() + "），目录（" + organ.getName() + "）", "PublicContentEO", CmsLogEO.Operation.Add.toString());
                }
                if (vo.getType().equals(PublicContentEO.Type.PUBLIC_ANNUAL_REPORT.toString())) {
                    SysLog.log("信息公开年报：添加内容（" + vo.getTitle() + "），目录（" + organ.getName() + "-公开年报）", "PublicContentEO", CmsLogEO.Operation.Add.toString());
                }
            } else if ((null == id) && (isPublish.intValue() == 1)) {
                if (vo.getType().equals(PublicContentEO.Type.PUBLIC_GUIDE.toString())) {
                    SysLog.log("信息公开指南：添加并发布内容（" + vo.getTitle() + "），目录（" + organ.getName() + "）", "PublicContentEO", CmsLogEO.Operation.Add.toString());
                }
                if (vo.getType().equals(PublicContentEO.Type.PUBLIC_ANNUAL_REPORT.toString())) {
                    SysLog.log("信息公开年报：添加并发布内容（" + vo.getTitle() + "），目录（" + organ.getName() + "-公开年报）", "PublicContentEO", CmsLogEO.Operation.Add.toString());
                }
            }
        }
        BaseContentEO baseContentEO = baseContentService.getEntity(BaseContentEO.class, vo.getContentId());
        if(addFlag){
            //同步添加引用信息
            synAddReferNews(vo,baseContentEO,isPublish);
        }else{
            //同步修改引用信息
            String referMessges = baseContentService.synEditReferNews(baseContentEO,vo.getContent(),isPublish);//同步修改引用新闻
            if(!AppUtil.isEmpty(referMessges)){
                if (isPublish!=null&&isPublish.intValue() == 1) {//引用全部发布
                    MessageSenderUtil.publishCopyNews(referMessges);
                }else{//引用新闻全部取消发布
                    MessageSenderUtil.unPublishCopyNews(referMessges);
                }
            }
        }

        return getObject(vo);
    }

    /**
     * 同步添加引用新闻
     * @param baseContentEO
     * @param isPublish
     */
    private void synAddReferNews(PublicContentVO vo,BaseContentEO baseContentEO,Integer isPublish){
//        isPublish = 0;//全部设置成未发布状态
        //同步添加引用信息
        PublicCatalogEO publicCatalogEO = CacheHandler.getEntity(PublicCatalogEO.class, vo.getCatId());
        PublicCatalogUtil.filterCatalog(publicCatalogEO, vo.getOrganId());
        if(publicCatalogEO!=null){
            String organCats = vo.getOrganId()+"_"+vo.getCatId();
            if(!AppUtil.isEmpty(publicCatalogEO.getReferColumnIds())
                    ||!AppUtil.isEmpty(publicCatalogEO.getReferOrganCatIds())){
                Map<String,String> resultMap = baseContentService.getReferColumnCats(publicCatalogEO.getReferColumnIds(),publicCatalogEO.getReferOrganCatIds(),organCats);
                String referColumnIds = resultMap.get("referColumnIds");
                String referOrganCatIds = resultMap.get("referOrganCatIds");

                CopyReferVO copyReferVO = new CopyReferVO();
                copyReferVO.setIsColumnOpt(1);//栏目之间的引用
                copyReferVO.setPublicSiteId(vo.getSiteId());//信息公开站点id
                copyReferVO.setContentId(baseContentEO.getId()+"");
                copyReferVO.setSource(2l);//信息公开
                copyReferVO.setType(2l);//引用
                copyReferVO.setSynColumnIds(referColumnIds);
                copyReferVO.setSynOrganCatIds(referOrganCatIds);

                if(!AppUtil.isEmpty(referColumnIds)){//设置发布状态
                    int size = referColumnIds.split(",").length;
                    StringBuffer stringBuffer = new StringBuffer();
                    for(int i = 0;i<size;i++){
                        if(i==0){
                            stringBuffer.append(isPublish);
                        }else{
                            stringBuffer.append(","+isPublish);
                        }
                    }
                    copyReferVO.setSynColumnIsPublishs(stringBuffer.toString());
                }

                if(!AppUtil.isEmpty(referOrganCatIds)){//设置发布状态
                    int size = referOrganCatIds.split(",").length;
                    StringBuffer stringBuffer = new StringBuffer();
                    for(int i = 0;i<size;i++){
                        if(i==0){
                            stringBuffer.append(isPublish);
                        }else{
                            stringBuffer.append(","+isPublish);
                        }
                    }
                    copyReferVO.setSynOrganIsPublishs(stringBuffer.toString());
                }
                String messges = baseContentService.copyRefer(copyReferVO);
                MessageSenderUtil.publishCopyNews(messges);
            }
        }
    }

    /**
     * 删除
     *
     * @param columnId
     * @param ids
     * @param contentIds
     * @return
     * @author fangtinghua
     */
    @ResponseBody
    @RequestMapping("delete")
    public Object delete(Long columnId, @RequestParam("ids[]") Long[] ids, @RequestParam("contentIds[]") Long[] contentIds) {
        if (null == ids || ids.length == 0) {
            return ajaxErr("Id不能为空！");
        }
        publicContentService.delete(ids);
        for (int i = 0; i < contentIds.length; i++) {
            List<ContentReferRelationEO> list = contentReferRelationService.getByCauseId(contentIds[i], null, ContentReferRelationEO.TYPE.REFER.toString());
            if (list != null && list.size() > 0) {
                List<Long> tempList = Arrays.asList(contentIds);
                List<Long> idsList = new ArrayList(tempList);
                for(ContentReferRelationEO eo:list){
                    idsList.add(eo.getReferId());//引用新闻也要同步删除
                }
                contentIds = idsList.toArray(new Long[]{});
            }
        }
        MessageSenderUtil.publishContent(
                new MessageStaticEO(LoginPersonUtil.getSiteId(), columnId, contentIds).setType(MessageEnum.UNPUBLISH.value()), 2);
//        MessageStaticEO message = new MessageStaticEO();
//        message.setSiteId(LoginPersonUtil.getSiteId());
//        message.setColumnId(columnId);
//        message.setContentIds(contentIds);
//        message.setSource(MessageEnum.PUBLICINFO.value());
//        message.setType(MessageEnum.UNPUBLISH.value());
//        MessageSenderUtil.publishContent(message, 2);
        return getObject();
    }

    /**
     * 发布、取消发布
     *
     * @param ids
     * @param columnId
     * @param isPublish
     * @return
     * @author fangtinghua
     */
    @ResponseBody
    @RequestMapping("publish")
    public Object publish(@RequestParam("ids[]") Long[] ids, Long columnId, Long catId, Integer isPublish, String type) {
        List<BaseContentEO> baseContentList = baseContentService.getEntities(BaseContentEO.class, ids);
        if (null == baseContentList || baseContentList.isEmpty()) {
            return ajaxErr("文章信息不存在！");
        }
        for (BaseContentEO baseContentEO : baseContentList) {
            if (Integer.valueOf(0).equals(baseContentEO.getVideoStatus())) {
                return ajaxErr("文章内容中视频未转换完成！");
            }
        }
        Long siteId = LoginPersonUtil.getSiteId();
        MessageStaticEO message = new MessageStaticEO();
        message.setSiteId(LoginPersonUtil.getSiteId());
        message.setColumnId(columnId);
        message.setSource(MessageEnum.PUBLICINFO.value());

        if (!PublicApplyEO.PUBLIC_APPLY.toString().equals(type)) {
            String publisType = "";
            message.setContentIds(ids);
            publicContentService.updatePublicStatus(ids, columnId, catId, 2);// 不管发布还是取消发布，状态都改为2中间状态
            if (isPublish == 1) {
                message.setType(MessageEnum.PUBLISH.value());
                MessageSenderUtil.publishContent(message, 1);
            } else {
                publisType = "取消";
                message.setType(MessageEnum.UNPUBLISH.value());
                MessageSenderUtil.publishContent(message, 2);
            }
            PublicCatalogEO catalogEO = publicCatalogService.getEntity(PublicCatalogEO.class,catId);


            for(BaseContentEO baseContentEO:baseContentList){
                Long organId = baseContentEO.getColumnId();//主动公开的columnId存的是单位id
                OrganEO organEO = organService.getEntity(OrganEO.class,organId);
                SysLog.log("主动公开目录：" + publisType +"发布内容（"+baseContentEO.getTitle()+"），目录（"+catalogEO==null?"":catalogEO.getName()
                        +"），公开单位（"+organEO==null?"":organEO.getName()+"）","PublicContentEO", CmsLogEO.Operation.Update.toString());

                //同步发布或取消发布引用新闻
                changeReferNewsPublish(baseContentEO.getId(),isPublish);
            }

        } else {
            baseContentService.changePublish(new ContentPageVO(siteId, columnId, isPublish, ids, null));// 依申请公开生成首页
            message.setType(MessageEnum.PUBLISH.value());
            message.setScope(MessageEnum.INDEX.value());
            MessageSenderUtil.publishContent(message, 3);// 不用删除索引
            OrganEO organ = (OrganEO)this.organService.getEntity(OrganEO.class, columnId);
            if ((!baseContentList.isEmpty()) && (organ != null)) {
                for (BaseContentEO baseContentEO : baseContentList) {
                    if ((baseContentEO != null) && (!StringUtils.isEmpty(baseContentEO.getTitle()))) {
                        if ((isPublish.intValue() == 1) && (type.equals(PublicContentEO.Type.PUBLIC_ANNUAL_REPORT.toString()))) {
                            SysLog.log("信息公开年报：取消发布内容（" + baseContentEO.getTitle() + "），目录（" + organ.getName() + "-公开年报）", "PublicContentEO", CmsLogEO.Operation.Update.toString());
                        } else if ((isPublish.intValue() == 0) && (type.equals(PublicContentEO.Type.PUBLIC_ANNUAL_REPORT.toString()))) {
                            SysLog.log("信息公开年报：发布内容（" + baseContentEO.getTitle() + "），目录（" + organ.getName() + "-公开年报）", "PublicContentEO", CmsLogEO.Operation.Update.toString());
                        } else if ((isPublish.intValue() == 1) && (type.equals(PublicContentEO.Type.PUBLIC_.toString()))) {
                            SysLog.log("依申请公开：取消发布内容（" + baseContentEO.getTitle() + "），接收单位（" + organ.getName() + "）", "PublicContentEO", CmsLogEO.Operation.Update.toString());
                        } else if ((isPublish.intValue() == 0) && (type.equals(PublicContentEO.Type.PUBLIC_.toString()))) {
                            SysLog.log("依申请公开：发布内容（" + baseContentEO.getTitle() + "），接收单位（" + organ.getName() + "）", "PublicContentEO", CmsLogEO.Operation.Update.toString());
                        }
                    }
                }
            }
        }
        return getObject();
    }

    /**
     * 同步发布或取消发布引用新闻
     * @param causeById
     * @param isPublish
     */
    private void changeReferNewsPublish(Long causeById,Integer isPublish){
        if(AppUtil.isEmpty(causeById)||AppUtil.isEmpty(isPublish)){
            return;
        }
        List<ContentReferRelationEO> relationEOS = contentReferRelationService.getByCauseId(causeById,null,ContentReferRelationEO.TYPE.REFER.toString());
        if(relationEOS!=null&&relationEOS.size()>0){
            List<Long> referIds = new ArrayList<Long>();
            for(ContentReferRelationEO relationEO:relationEOS){
                referIds.add(relationEO.getReferId());
            }
            if(referIds.size()>0){
                String msg = baseContentService.publishs(referIds.toArray(new Long[]{}),isPublish);
                if(isPublish.intValue()==0){//取消发布
                    MessageSenderUtil.unPublishCopyNews(msg);
                }else{
                    MessageSenderUtil.publishCopyNews(msg);
                }
            }
        }
    }

    /**
     * 置顶、取消置顶
     *
     * @param ids
     * @param columnId
     * @param isTop
     * @return
     * @author fangtinghua
     */
    @ResponseBody
    @RequestMapping("changeTop")
    public Object changeTop(@RequestParam("ids[]") Long[] ids, Long columnId, Integer isTop) {
        Long siteId = LoginPersonUtil.getSiteId();
        MessageStaticEO message = new MessageStaticEO();
        message.setSiteId(siteId);
        message.setColumnId(columnId);
        //message.setContentIds(ids); 文章id不传，只生成栏目页即可
        message.setSource(MessageEnum.PUBLICINFO.value());
        if (isTop == 1) {
            baseContentService.changeTopStatus(ids, 1);
            message.setType(MessageEnum.PUBLISH.value());
            MessageSenderUtil.publishContent(message, 1);
        } else {
            baseContentService.changeTopStatus(ids, 0);
            message.setType(MessageEnum.UNPUBLISH.value());
            MessageSenderUtil.publishContent(message, 2);
        }
        return getObject();
    }

    /**
     * 移动
     *
     * @return
     * @author fangtinghua
     */
    @RequestMapping("toMove")
    public String toMove() {
        return "/public/content/move_page";
    }

    /**
     * 移动
     *
     * @param organId
     * @param originalId
     * @param targetOrganId
     * @param targetId
     * @param contentIds
     * @return
     * @author fangtinghua
     */
    @ResponseBody
    @RequestMapping("move")
    public Object move(Long organId, Long originalId, Long targetOrganId, Long targetId, @RequestParam(name = "ids[]", required = false) Long[] ids,
                       @RequestParam(name = "contentIds[]", required = false) Long[] contentIds, Integer isPublish) {
        // 更新所有的内容id的catId为targetId
        publicContentService.updateCatIdByContentIds(organId, originalId, targetOrganId, targetId, ids, contentIds, isPublish);
        // 删除元文章索引
        if (null != contentIds && contentIds.length > 0) {
            List<String> indexIdList = new ArrayList<String>();
            for (Long contentId : contentIds) {
                indexIdList.add(contentId.toString());
            }
            try {
                SolrFactory.deleteIndex(indexIdList);
            } catch (Throwable e) {
                e.printStackTrace();
                return ajaxErr("索引删除失败！");
            }
        }
        for (int i = 0; i < ids.length; i++)
        {
            PublicContentEO publicContentEO = (PublicContentEO)this.publicContentService.getEntity(PublicContentEO.class, ids[i]);
            BaseContentEO baseContentEO = (BaseContentEO)this.baseContentService.getEntity(BaseContentEO.class, publicContentEO.getContentId());
            OrganEO organ = (OrganEO)this.organService.getEntity(OrganEO.class, organId);
            OrganEO organ2 = (OrganEO)this.organService.getEntity(OrganEO.class, targetOrganId);
            if (publicContentEO.getType().equals(PublicContentEO.Type.PUBLIC_ANNUAL_REPORT.toString())) {
                SysLog.log("信息公开年报：移动内容（" + baseContentEO.getTitle() + "），目录（" + organ.getName() + "-公开年报）,移动到栏目并发布(" + organ2.getName() + ")", "PublicContentEO", CmsLogEO.Operation.Delete.toString());
            }
        }
        String publishStr = "";
        // 移动的逻辑比较复杂，如果单位一致，生成一个文章页即可，如果单位不一致，删除原来的文章页，构建新的文章页
        if (organId.equals(targetOrganId)) {// 只发送一个消息
            MessageStaticEO message = new MessageStaticEO();
            message.setSiteId(LoginPersonUtil.getSiteId());
            message.setColumnId(organId);
            message.setContentIds(contentIds);
            message.setSource(MessageEnum.PUBLICINFO.value());
            message.setType(isPublish == 1 ? MessageEnum.PUBLISH.value() : MessageEnum.UNPUBLISH.value());
            MessageSenderUtil.publishContent(message, 1);
        } else if (isPublish == 1) {//如果是发布状态，要生成，生成新栏目
            publishStr = "并发布";
            MessageStaticEO target = new MessageStaticEO();
            target.setSiteId(LoginPersonUtil.getSiteId());
            target.setColumnId(targetOrganId);
            target.setSource(MessageEnum.PUBLICINFO.value());
            target.setType(MessageEnum.PUBLISH.value());
            MessageSenderUtil.publishContent(target, 3);
        }

        try {
            String optType = "移动";
            if(contentIds!=null&&contentIds.length>1){
                optType = "批量移动";
            }
            if(contentIds!=null&&contentIds.length>0){
                OrganEO newOrgan = CacheHandler.getEntity(OrganEO.class,targetOrganId);
                String newColumnName = newOrgan.getName()+" > "+PublicCatalogUtil.getCatalogPath(targetOrganId, targetId);

                for(int i=0;i<contentIds.length;i++){
                    Long contentId = contentIds[i];
                    //更新复制引用关系
                    List<ContentReferRelationEO> list = contentReferRelationService.getByReferId(contentId, null, null);
                    if(list!=null&&list.size()>0){
                        for(ContentReferRelationEO referRelationEO:list){
                            referRelationEO.setColumnId(targetOrganId);
                            referRelationEO.setCatId(targetId);
                        }
                        contentReferRelationService.updateEntities(list);
                    }
                    PublicContentVO publicContentVO = publicContentService.getPublicContentByBaseContentId(contentId);
                    OrganEO oldOrgan = CacheHandler.getEntity(OrganEO.class,publicContentVO.getOrganId());
                    String oldColumnName = oldOrgan.getName()+" > "+PublicCatalogUtil.getCatalogPath(publicContentVO.getOrganId(), publicContentVO.getCatId());
                    SysLog.log("主动公开目录："+optType+"内容（"+publicContentVO.getTitle()+"），目录（"+oldColumnName
                            +"），移动到（"+newColumnName+"）"+publishStr,"PublicContentEO", CmsLogEO.Operation.Update.toString());
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return getObject();
    }

    @RequestMapping("updateSort")
    @ResponseBody
    public Object updateSort(SortUpdateVO sortVo) {
        if (StringUtils.isEmpty(sortVo.getOperate()) || sortVo.getId() == null || sortVo.getSiteId() == null || sortVo.getColumnId() == null
                || sortVo.getSortNum() == null) {
            throw new BaseRunTimeException(TipsMode.Message.toString(), "参数错误");
        }
        publicContentService.updateSort(sortVo);
        return getObject();
    }

    /**
     * 更新排序号
     *
     * @param id
     * @param sortNum
     * @return
     */
    @RequestMapping("updateSortNum")
    @ResponseBody
    public Object updateSortNum(Long id, Long sortNum) {
        if (null == id || null == sortNum || sortNum < 0) {
            return ajaxErr("参数错误");
        }
        publicContentService.updateSortNum(id, sortNum);
        return getObject();
    }

    /**
     * 获取未审核内容
     *
     * @return
     * @author fangtinghua
     */
    @RequestMapping("toUnPublic")
    public String toUnPublic() {
        return "/public/content/unPublic";
    }

    /**
     * 获取审核内容
     *
     * @return
     * @author fangtinghua
     */
    @RequestMapping("toPublicPage")
    public String toPublicPage(@RequestParam(defaultValue = "unPublic") String pageName) {
        return "/public/content/" + pageName;
    }

    /**
     * 获取未发布分页
     *
     * @param contentPageVO
     * @return
     * @author fangtinghua
     */
    @ResponseBody
    @RequestMapping("getUnAuditPage")
    public Object getUnAuditPage(ContentPageVO contentPageVO) {
        if (!LoginPersonUtil.isSuperAdmin() && !LoginPersonUtil.isSiteAdmin()) {
            contentPageVO.setColumnId(LoginPersonUtil.getUnitId());//查询当前用户部门的数据
        }
        contentPageVO.setTypeCode(BaseContentEO.TypeCode.public_content.toString());
        //计算栏目名称
        Pagination pagination = baseContentService.getPage(contentPageVO);
        List<?> list = pagination.getData();
        if (null != list && !list.isEmpty()) {
            for (Object obj : list) {
                BaseContentEO bc = (BaseContentEO) obj;
                PublicContentVO vo = publicContentService.getPublicContentByBaseContentId(bc.getId());
                if (null != vo) {
                    bc.setColumnName(vo.getCatName());
                }
            }
        }
        return pagination;
    }

    /**
     * 根据内容id删除信息公开
     *
     * @param columnId
     * @param contentIds
     * @return
     * @author fangtinghua
     */
    @ResponseBody
    @RequestMapping("deleteByContentIds")
    public Object deleteByContentIds(Long columnId, @RequestParam("contentIds[]") Long[] contentIds) {
        if (null == contentIds || contentIds.length == 0) {
            return ajaxErr("contentIds不能为空！");
        }
        Map<String, Object> paramMap = new HashMap<String, Object>();
        paramMap.put("contentId", contentIds);
        List<PublicContentEO> contentList = publicContentService.getEntities(PublicContentEO.class, paramMap);
        if (null == contentList || contentList.isEmpty()) {
            return ajaxErr("contentIds错误，文章不存在！");
        }
        List<Long> idList = new ArrayList<Long>();
        for (PublicContentEO eo : contentList) {
            idList.add(eo.getId());
        }
        publicContentService.delete(idList.toArray(new Long[idList.size()]));
        /*MessageStaticEO message = new MessageStaticEO();
        message.setSiteId(LoginPersonUtil.getSiteId());
        message.setColumnId(columnId);
        message.setContentIds(contentIds);
        message.setSource(MessageEnum.PUBLICINFO.value());
        message.setType(MessageEnum.UNPUBLISH.value());
        MessageSenderUtil.publishContent(message, 2);*/
        return getObject();
    }

    /**
     * 跳转到编辑页面
     *
     * @param contentId
     * @param map
     * @return
     * @author fangtinghua
     */
    @RequestMapping("editUnPublic")
    public String editUnPublic(@RequestParam(value = "contentId", required = false) Long contentId, String verify,
                               @RequestParam(value = "isLink", defaultValue = "false") boolean isLink, ModelMap map) {
        map.put("contentId", contentId);
        map.put("verify", verify);
        map.put("isLink", isLink);
        return "/public/content/editUnPublic";
    }

    /**
     * 获取主动公开内容
     *
     * @param contentId
     * @return
     * @author fangtinghua
     */
    @ResponseBody
    @RequestMapping("getPublicContentByContentId")
    public Object getPublicContent(Long contentId) {
        PublicContentVO vo = publicContentService.getPublicContentByBaseContentId(contentId);

        return getObject(vo);
    }



    /**
     * 根据单位id获取内容模型
     *
     * @param organId
     * @return
     * @author fangtinghua
     */
    @ResponseBody
    @RequestMapping("getContentModelByOrganId")
    public Object getContentModelByOrganId(Long organId) {
        OrganConfigEO organConfigEO = CacheHandler.getEntity(OrganConfigEO.class, CacheGroup.CMS_ORGAN_ID, organId);
        if (null == organConfigEO || StringUtils.isEmpty(organConfigEO.getContentModelCode())) {
            return ajaxErr("本单位没有配置信息公开内容模型！");
        }
        ContentModelEO contentModeEO = CacheHandler.getEntity(ContentModelEO.class, CacheGroup.CMS_CODE, organConfigEO.getContentModelCode());
        if (null != contentModeEO) {
            ModelTemplateEO modelEO = ModelConfigUtil.getTemplateByModelId(contentModeEO.getId());
            // 栏目code借用模型code显示
            if (null != modelEO) {
                contentModeEO.setCode(modelEO.getModelTypeCode());
            }
            contentModeEO.setConfig(JSON.parse(contentModeEO.getContent()));
            contentModeEO.setContent(null);
        }
        return getObject(contentModeEO);
    }

    @RequestMapping("toInvalid")
    public String toInvalid(Long id, ModelMap map) {
        if (id == null) {
            throw new BaseRunTimeException(TipsMode.Message.toString(), "ID不能为空！");
        }
        PublicContentEO publicContentEO = publicContentService.getEntity(PublicContentEO.class, id);
        if (publicContentEO == null) {
            throw new BaseRunTimeException(TipsMode.Message.toString(), "内容不能为空！");
        }
        map.put("id", id);
        map.put("isInvalid", publicContentEO.getIsInvalid());
        map.put("invalidReason", publicContentEO.getInvalidReason());
        if (StringUtils.isEmpty(publicContentEO.getInvalidReason())) {
            map.put("invalidReason", "");
        }
        return "/public/content/invalid";
    }

    @RequestMapping("setInvalid")
    @ResponseBody
    public Object setInvalid(Long id, Integer isInvalid, String invalidReason) {
        if (id == null) {
            throw new BaseRunTimeException(TipsMode.Message.toString(), "ID不能为空！");
        }
        PublicContentEO publicContentEO = publicContentService.getEntity(PublicContentEO.class, id);
        if (publicContentEO == null) {
            throw new BaseRunTimeException(TipsMode.Message.toString(), "内容不能为空！");
        }
        publicContentEO.setIsInvalid(isInvalid);
        if (isInvalid != null) {
            publicContentEO.setInvalidReason(invalidReason);
        }
        publicContentService.updateEntity(publicContentEO);
        CacheHandler.saveOrUpdate(PublicContentEO.class, publicContentEO);

        //同步更新引用新闻的标注
        try {
            List<ContentReferRelationEO> referRelationEOS = contentReferRelationService.getByCauseId(publicContentEO.getContentId(),
                    null,ContentReferRelationEO.TYPE.REFER.toString());
            if(referRelationEOS!=null&&referRelationEOS.size()>0){
                for(ContentReferRelationEO relationEO : referRelationEOS){
                    //引用新闻中含有信息公开，同步修改
                    if(ContentReferRelationEO.ModelCode.PUBLIC.toString().equals(relationEO.getReferModelCode())){
                        Map<String,Object> param = new HashMap<String,Object>();
                        param.put("contentId",relationEO.getReferId());
                        param.put("recordStatus", AMockEntity.RecordStatus.Normal.toString());
                        PublicContentEO publicContentEO1 = publicContentService.getEntity(PublicContentEO.class,param);
                        if(publicContentEO1!=null){
                            publicContentEO1.setIsInvalid(isInvalid);
                            if (isInvalid != null) {
                                publicContentEO1.setInvalidReason(invalidReason);
                            }
                            publicContentService.updateEntity(publicContentEO1);
                            CacheHandler.saveOrUpdate(PublicContentEO.class, publicContentEO1);
                        }
                    }
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }


        return getObject();
    }

    /**
     * 关联文件页面
     *
     * @return
     * @author fangtinghua
     */
    @RequestMapping("relationFile")
    public String relationFile(ModelMap modelMap, Long id, Long contentId, Long organId,String filePath) {
        Long siteId = LoginPersonUtil.getSiteId();
        SiteConfigEO siteConfigEO = CacheHandler.getEntity(SiteConfigEO.class, CacheGroup.CMS_INDICATORID, siteId);
        List<OrganEO> organList = organService.getPublicOrgans(Long.valueOf(siteConfigEO.getUnitIds()));
        List<OrganVO> organVOList = new ArrayList<OrganVO>();
        PublicCatalogUtil.filterOrgan(organList, organVOList);// 过滤单位列表
        PublicCatalogUtil.sortOrgan(organVOList);// 排序
        PublicContentEO eo = publicContentService.getEntity(PublicContentEO.class, id);
        modelMap.put("id",id);
        modelMap.put("organList", organVOList);
        modelMap.put("_contentId", contentId);
        modelMap.put("_organId", organId);
        modelMap.put("relContentId", eo.getRelContentId());
        return "/public/content/relationFile";
    }

    /**
     * 文件与解读关联，自动相互关联，支持关联多个
     *
     * @param id          原信息公开文章Id
     * @param ids_        选择关联的信息公开文章id 数组形式
     * @param _contentId  原信息公开文章的主表id
     * @param contentIds_ 选择关联的信息公开文章的主表id,字符串形式，多个id以,隔开
     * @param _filePath   原信息公开文章的静态页路径
     * @param filePaths_  选择关联的信息公开文章的静态页路径，字符串形式，多个路径以,隔开
     * @return
     * @author caohaitao
     */
    @ResponseBody
    @RequestMapping("relation")
    public Object relation(Long id, Long ids_[], String _contentId, String contentIds_, String _filePath, String filePaths_) {
        PublicContentEO eo = publicContentService.getEntity(PublicContentEO.class, id);

        String relContentId = "";
        String filePath = "";
        if (!AppUtil.isEmpty(eo.getRelContentId()) && !AppUtil.isEmpty(eo.getFilePath()) && !AppUtil.isEmpty(contentIds_)&&!AppUtil.isEmpty(_filePath)) {
            String[] contentIdsStr = StringUtils.split(contentIds_, ",");
            for (int i = 0; i < contentIdsStr.length; i++) {
                if (eo.getRelContentId().contains(contentIdsStr[i])) {
                    return ajaxErr("无法再次关联已关联过的文章！");
                }
            }
            relContentId = eo.getRelContentId() + "," + contentIds_;
            filePath = eo.getFilePath() + "," + filePaths_;

        } else {
            relContentId = contentIds_;
            filePath = filePaths_;
        }

        eo.setRelContentId(relContentId);
        eo.setFilePath(filePath);
        publicContentService.updateEntity(eo);
        CacheHandler.saveOrUpdate(PublicContentEO.class, eo);



        //同步更新引用新闻的关联解读
        try {
            List<Long> idList = Arrays.asList(ids_);
            List<ContentReferRelationEO> referRelationEOS = contentReferRelationService.getByCauseId(eo.getContentId(),
                    null,ContentReferRelationEO.TYPE.REFER.toString());
            if(referRelationEOS!=null&&referRelationEOS.size()>0){
                for(ContentReferRelationEO relationEO : referRelationEOS){
                    //引用新闻中含有信息公开，同步修改
                    if(ContentReferRelationEO.ModelCode.PUBLIC.toString().equals(relationEO.getReferModelCode())){
                        Map<String,Object> param = new HashMap<String,Object>();
                        param.put("contentId",relationEO.getReferId());
                        param.put("recordStatus", AMockEntity.RecordStatus.Normal.toString());
                        PublicContentEO publicContentEO1 = publicContentService.getEntity(PublicContentEO.class,param);
                        if(publicContentEO1!=null){
                            publicContentEO1.setRelContentId(relContentId);
                            publicContentEO1.setFilePath(filePath);
                            publicContentService.updateEntity(publicContentEO1);
                            CacheHandler.saveOrUpdate(PublicContentEO.class, publicContentEO1);
                            idList.add(publicContentEO1.getId());
                        }
                    }
                }
                ids_ = idList.toArray(new Long[]{});
            }
        }catch (Exception e){
            e.printStackTrace();
        }


        for (int i = 0; i < ids_.length; i++) {
            PublicContentEO eo_ = publicContentService.getEntity(PublicContentEO.class, ids_[i]);
            if (!AppUtil.isEmpty(eo_.getRelContentId()) && !AppUtil.isEmpty(eo_.getFilePath())) {
                eo_.setRelContentId(eo_.getRelContentId() + "," + _contentId);
                eo_.setFilePath(eo_.getFilePath() + "," + _filePath);
            } else {
                eo_.setRelContentId(_contentId);
                eo_.setFilePath(_filePath);
            }
            publicContentService.updateEntity(eo_);
            CacheHandler.saveOrUpdate(PublicContentEO.class, eo_);
        }
        return getObject();
    }


    /**
     * 文件与解读取消关联，自动相互取消关联，支持取消关联一个或多个
     *
     * @param id          原信息公开文章Id
     * @param ids_        选择关联的信息公开文章id 数组形式
     * @param _contentId  原信息公开文章的主表id
     * @param contentIds_ 选择关联的信息公开文章的主表id,字符串形式，多个id以,隔开
     * @param _filePath   原信息公开文章的静态页路径
     * @param filePaths_  选择关联的信息公开文章的静态页路径，字符串形式，多个路径以,隔开
     * @return
     * @author caohaitao
     */
    @ResponseBody
    @RequestMapping("cancleRelation")
    public Object cancleRelation(Long id, Long ids_[], String _contentId, String contentIds_, String _filePath, String filePaths_) {
        PublicContentEO eo = publicContentService.getEntity(PublicContentEO.class, id);
        if (!AppUtil.isEmpty(eo.getRelContentId()) && !AppUtil.isEmpty(eo.getFilePath())) {
            String[] contentIds_Arr = StringUtils.split(contentIds_, ",");//将即将取消关联的1条或多条文章的contentId字符串分为数组
            String[] filePaths_Arr = StringUtils.split(filePaths_, ",");//将即将取消关联的1条或多条文章的filePath字符串分为数组
            String newContentId = null;
            String newFilePath = null;
            String[] eoContentIds_Arr = StringUtils.split(eo.getRelContentId(), ",");//将原文章的relContentId字符串分割为数组
            String[] eoFilePaths_Arr = StringUtils.split(eo.getFilePath(), ",");//将原文章的filePath字符串分割为数组
            if (contentIds_Arr.length <= 1 && filePaths_Arr.length <= 1) {//如果选择单条文章取消关联
                if (eoContentIds_Arr.length <= 1 && eoFilePaths_Arr.length <= 1) {//原文章的contentId是否唯一，如果唯一，则直接置空
                    newContentId = eo.getRelContentId().replace(contentIds_, "");
                    newFilePath = eo.getFilePath().replace(filePaths_, "");
                } else {//原文章的relContentId不唯一，判断是否以即将取消关联的文章的contentId/filePath开头
                    if (eo.getRelContentId().startsWith(contentIds_)) {
                        newContentId = eo.getRelContentId().replace(contentIds_ + ",", "");
                    } else {
                        newContentId = eo.getRelContentId().replace("," + contentIds_, "");
                    }
                    if (eo.getFilePath().startsWith(filePaths_)) {
                        newFilePath = eo.getFilePath().replace(filePaths_ + ",", "");
                    } else {
                        newFilePath = eo.getFilePath().replace("," + filePaths_, "");
                    }
                }
            } else {//如果选择多条文章
                if (eoContentIds_Arr.length <= 1) {
                    return ajaxErr("只关联了一篇文章，无法取消关联多篇！");
                }
                for (int i = 0; i < contentIds_Arr.length; i++) {//遍历这多条新闻的contentId
                    if (eo.getRelContentId().startsWith(contentIds_Arr[i])) {//如果原文章中以 遍历的数组中的某一条文章的contentId开头，则将该contentId及后面的,置空
                        if (eo.getRelContentId().contains(",")) {//如果第一个串匹配到，后面还有串，则将该串及后面的,置空
                            newContentId = eo.getRelContentId().replace(contentIds_Arr[i] + ",", "");
                            eo.setRelContentId(newContentId);
                            publicContentService.updateEntity(eo);//因循环替换置空，故及时更新以获取最新的relContentId
                        } else {//如果第一个串匹配到，且仅有这一个串，则直接置空
                            newContentId = eo.getRelContentId().replace(contentIds_Arr[i], "");
                            eo.setRelContentId(newContentId);
                            publicContentService.updateEntity(eo);
                        }

                    } else {//不是第一条则将该contentId及前面的,置空
                        newContentId = eo.getRelContentId().replace("," + contentIds_Arr[i], "");
                        eo.setRelContentId(newContentId);
                        publicContentService.updateEntity(eo);
                    }
                }
                for (int i = 0; i < filePaths_Arr.length; i++) {//遍历这多条新闻的filePath
                    if (eo.getFilePath().startsWith(filePaths_Arr[i])) {
                        if (eo.getFilePath().contains(",")) {
                            newFilePath = eo.getFilePath().replace(filePaths_Arr[i] + ",", "");
                            eo.setFilePath(newFilePath);
                            publicContentService.updateEntity(eo);
                        } else {
                            newFilePath = eo.getFilePath().replace(filePaths_Arr[i], "");
                            eo.setFilePath(newFilePath);
                            publicContentService.updateEntity(eo);
                        }
                    } else {
                        newFilePath = eo.getFilePath().replace("," + filePaths_Arr[i], "");
                        eo.setFilePath(newFilePath);
                        publicContentService.updateEntity(eo);
                    }
                }
            }
            eo.setRelContentId(newContentId);
            eo.setFilePath(newFilePath);
            publicContentService.updateEntity(eo);
            CacheHandler.saveOrUpdate(PublicContentEO.class, eo);
        } else {
            return ajaxErr("未关联任何文章，无法取消关联！");
        }
        publicContentService.updateEntity(eo);//更新原文章
        CacheHandler.saveOrUpdate(PublicContentEO.class, eo);

        //同步更新引用新闻的关联解读
        try {
            List<Long> idList = Arrays.asList(ids_);
            List<ContentReferRelationEO> referRelationEOS = contentReferRelationService.getByCauseId(eo.getContentId(),
                    null,ContentReferRelationEO.TYPE.REFER.toString());
            if(referRelationEOS!=null&&referRelationEOS.size()>0){
                for(ContentReferRelationEO relationEO : referRelationEOS){
                    //引用新闻中含有信息公开，同步修改
                    if(ContentReferRelationEO.ModelCode.PUBLIC.toString().equals(relationEO.getReferModelCode())){
                        Map<String,Object> param = new HashMap<String,Object>();
                        param.put("contentId",relationEO.getReferId());
                        param.put("recordStatus", AMockEntity.RecordStatus.Normal.toString());
                        PublicContentEO publicContentEO1 = publicContentService.getEntity(PublicContentEO.class,param);
                        if(publicContentEO1!=null){
                            publicContentEO1.setRelContentId(eo.getRelContentId());
                            publicContentEO1.setFilePath(eo.getFilePath());
                            publicContentService.updateEntity(publicContentEO1);
                            CacheHandler.saveOrUpdate(PublicContentEO.class, publicContentEO1);
                            idList.add(publicContentEO1.getId());
                        }
                    }
                }
                ids_ = idList.toArray(new Long[]{});
            }
        }catch (Exception e){
            e.printStackTrace();
        }


        for (int i = 0; i < ids_.length; i++) {//一层循环 ：遍历选择关联的文章
            PublicContentEO eo_ = publicContentService.getEntity(PublicContentEO.class, ids_[i]);
            if (!AppUtil.isEmpty(eo_.getRelContentId()) && !AppUtil.isEmpty(eo_.getFilePath())) {
                String newContentId_ = null;
                String newFilePath_ = null;
                String[] eoContentIds_Arr = StringUtils.split(eo_.getRelContentId(), ",");//将一层循环遍历出的文章中的relContentId分割成数组
                String[] eoFilePaths_Arr = StringUtils.split(eo_.getFilePath(), ",");//将一层循环遍历出的文章中的filePath分割成数组
                if (eoContentIds_Arr.length <= 1 && eoFilePaths_Arr.length <= 1) {//如果该文章已关联的文章数不大于1
                    newContentId_ = eo_.getRelContentId().replace(_contentId, "");//直接置空
                    newFilePath_ = eo_.getFilePath().replace(_filePath, "");
                    eo_.setRelContentId(newContentId_);
                    eo_.setFilePath(newFilePath_);
                    publicContentService.updateEntity(eo_);
                } else {//如果该文章已关联了多条文章
                    for (int j = 0; j < eoContentIds_Arr.length; j++) {//二层循环 ：遍历这关联的这多条文章的relContentId
                        if (eo_.getRelContentId().startsWith(_contentId)) {//如果以关联的这多条文章中存在文章在第一个串时
                            if (eo_.getRelContentId().contains(",")) {
                                newContentId_ = eo_.getRelContentId().replace(eoContentIds_Arr[j] + ",", "");//将第一个串及后面的,置空
                                eo_.setRelContentId(newContentId_);
                                publicContentService.updateEntity(eo_);
                            } else {
                                newContentId_ = eo_.getRelContentId().replace(eoContentIds_Arr[j], "");//将第一个串及后面的,置空
                                eo_.setRelContentId(newContentId_);
                                publicContentService.updateEntity(eo_);
                            }

                        } else {
                            newContentId_ = eo_.getRelContentId().replace("," + eoContentIds_Arr[j], "");//不在第一个串，就讲该串及前面的,置空
                            eo_.setRelContentId(newContentId_);
                            publicContentService.updateEntity(eo_);
                        }
                    }
                    for (int j = 0; j < eoFilePaths_Arr.length; j++) {//二层循环 ： 遍历这关联的这多条文章的filePath
                        if (eo_.getFilePath().startsWith(_filePath)) {
                            if (eo_.getFilePath().contains(",")) {
                                newFilePath_ = eo_.getFilePath().replace(eoFilePaths_Arr[j] + ",", "");
                                eo_.setFilePath(newFilePath_);
                                publicContentService.updateEntity(eo_);
                            } else {
                                newFilePath_ = eo_.getFilePath().replace(eoFilePaths_Arr[j], "");
                                eo_.setFilePath(newFilePath_);
                                publicContentService.updateEntity(eo_);
                            }
                        } else {
                            newFilePath_ = eo_.getFilePath().replace("," + eoFilePaths_Arr[j], "");
                            eo_.setFilePath(newFilePath_);
                            publicContentService.updateEntity(eo_);
                        }
                    }
                }
            } else {
                return ajaxErr("选中文章中存在文章未关联任何其他文章，无法取消关联！");
            }
            publicContentService.updateEntity(eo_);
            CacheHandler.saveOrUpdate(PublicContentEO.class, eo_);


        }
        return getObject();
    }
}