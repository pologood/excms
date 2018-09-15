/*
 * PublicContentServiceImpl.java         2015年12月15日 <br/>
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

package cn.lonsun.publicInfo.internal.service.impl;

import cn.lonsun.base.anno.DbInject;
import cn.lonsun.cache.client.CacheHandler;
import cn.lonsun.common.util.AppUtil;
import cn.lonsun.content.internal.dao.IBaseContentDao;
import cn.lonsun.content.internal.entity.BaseContentEO;
import cn.lonsun.content.internal.entity.ContentReferRelationEO;
import cn.lonsun.content.internal.service.IBaseContentService;
import cn.lonsun.content.internal.service.IContentReferRelationService;
import cn.lonsun.content.vo.ContentChartQueryVO;
import cn.lonsun.content.vo.ContentPageVO;
import cn.lonsun.content.vo.SortUpdateVO;
import cn.lonsun.content.vo.SortVO;
import cn.lonsun.core.base.entity.AMockEntity;
import cn.lonsun.core.base.service.impl.MockService;
import cn.lonsun.core.exception.BaseRunTimeException;
import cn.lonsun.core.util.Pagination;
import cn.lonsun.core.util.TipsMode;
import cn.lonsun.mongodb.entity.ContentMongoEO;
import cn.lonsun.mongodb.service.ContentMongoServiceImpl;
import cn.lonsun.msg.submit.service.IMsgSubmitService;
import cn.lonsun.publicInfo.internal.dao.IPublicApplyDao;
import cn.lonsun.publicInfo.internal.dao.IPublicContentDao;
import cn.lonsun.publicInfo.internal.entity.PublicCatalogEO;
import cn.lonsun.publicInfo.internal.entity.PublicCatalogOrganRelEO;
import cn.lonsun.publicInfo.internal.entity.PublicClassEO;
import cn.lonsun.publicInfo.internal.entity.PublicContentEO;
import cn.lonsun.publicInfo.internal.service.IPublicCatalogCountService;
import cn.lonsun.publicInfo.internal.service.IPublicCatalogService;
import cn.lonsun.publicInfo.internal.service.IPublicClassService;
import cn.lonsun.publicInfo.internal.service.IPublicContentService;
import cn.lonsun.publicInfo.vo.*;
import cn.lonsun.rbac.internal.entity.OrganEO;
import cn.lonsun.rbac.internal.service.IOrganService;
import cn.lonsun.site.site.internal.entity.ColumnMgrEO;
import cn.lonsun.site.site.internal.entity.SiteMgrEO;
import cn.lonsun.solr.SolrFactory;
import cn.lonsun.solr.vo.SolrIndexVO;
import cn.lonsun.statistics.ContentChartVO;
import cn.lonsun.statistics.PublicListVO;
import cn.lonsun.statistics.StatisticsQueryVO;
import cn.lonsun.statistics.WordListVO;
import cn.lonsun.system.systemlog.internal.entity.CmsLogEO;
import cn.lonsun.util.HotWordsCheckUtil;
import cn.lonsun.util.LoginPersonUtil;
import cn.lonsun.util.PublicCatalogUtil;
import cn.lonsun.util.SysLog;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang.time.DateFormatUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;

/**
 * 公开内容service <br/>
 *
 * @author fangtinghua <br/>
 * @version v1.0 <br/>
 * @date 2015年12月15日 <br/>
 */
@Service
public class PublicContentServiceImpl extends MockService<PublicContentEO> implements IPublicContentService {

    @Resource
    private IBaseContentService baseContentService;
    @Resource
    private ContentMongoServiceImpl contentMongoService;
    @DbInject("publicContent")
    private IPublicContentDao publicContentDao;
    @Resource
    private IPublicApplyDao publicApplyDao;
    @DbInject("baseContent")
    private IBaseContentDao baseContentDao;
    @Resource
    private IOrganService organService;
    @Resource
    private IMsgSubmitService msgSubmitService;
    @Resource
    private IPublicClassService publicClassService;
    @Resource
    private IPublicCatalogService publicCatalogService;
    @Resource
    private IPublicCatalogCountService publicCatalogCountService;
    @Resource
    private IContentReferRelationService contentReferRelationService;

    /**
     * 获取索引号 组织机构编码/年月-流水号(5位)
     *
     * @see cn.lonsun.publicInfo.internal.service.IPublicContentService#getIndexNum(java.lang.Long)
     */
    public String getIndexNum(Long organId) {
        OrganEO organEO = CacheHandler.getEntity(OrganEO.class, organId);
        // 组织机构编码/年月-
        String codeAndMonth = organEO.getCode() + "/" + DateFormatUtils.format(new Date(), "yyyyMM") + "-";
        // 查询当月最大索引号
        String indexNum = publicContentDao.getMaxIndexNumByOrganId(organId, codeAndMonth);
        if (StringUtils.isEmpty(indexNum)) {//为空表示正月该单位没有数据，则从00001开始
            return codeAndMonth + "00001";
        }
        String numberStr = indexNum.substring(indexNum.lastIndexOf("-") + 1);
        int number = Integer.valueOf(numberStr) + 100001;//转换为数字然后自增1
        return codeAndMonth + String.valueOf(number).substring(1);// 截取5位流水号
    }

    @Override
    public void updateCatIdByContentIds(Long organId, Long catId, Long targetOrganId, Long targetId, Long[] ids, Long[] contentIds, Integer isPublish) {
        Long totalCount = new Long((long) contentIds.length);// 操作的文章总数
        Long[] increment = this.computeIncrement(contentIds);// 包含发布数未发布数
        if (null != increment) { // 更改源目录的发布和未发布数
            if (increment[0] > 0L) {
                publicCatalogCountService.updateOrganCatIdCountByStatus(organId, catId, -increment[0], 1, false);// 发布状态
            }
            if (increment[1] > 0L) {
                publicCatalogCountService.updateOrganCatIdCountByStatus(organId, catId, -increment[1], 0, false);// 未发布状态
            }
        }
        publicCatalogCountService.updateOrganCatIdCountByStatus(targetOrganId, targetId, totalCount, isPublish, false);// 目标目录增加多少
        publicContentDao.exchangeColumnId(organId, catId, targetOrganId, contentIds, isPublish);
        publicContentDao.updateCatIdByContentIds(organId, catId, targetOrganId, targetId, ids);
    }

    /**
     * 给内容协同同步使用
     *
     * @param vo
     * @param synOrganCatIds
     * @throws Exception
     * @author fangtinghua
     */
    @Override
    public String saveEntities(PublicContentVO vo, Long contentId,Long type,String modelCode, String synOrganCatIds,
                               String synColumnIsPublishs,Integer isColumnOpt) throws Exception {
        String returnStr = "";
        if (null != vo && !StringUtils.isEmpty(synOrganCatIds)) {
            BaseContentEO oldBaseContentEO = CacheHandler.getEntity(BaseContentEO.class,contentId);
            Long oldCatId = vo.getCatId();
            String[] organCatIds = synOrganCatIds.split(",");
            boolean isPublish = false;
            String[] isPublishs = null;
            if (StringUtils.isNotEmpty(synColumnIsPublishs)) {
                isPublish = true;
                isPublishs = synColumnIsPublishs.split(",");
            }
            for (int i = 0; i < organCatIds.length; i++) {
                String[] arr = organCatIds[i].split("_");

                vo.setOrganId(Long.valueOf(arr[0]));
                vo.setCatId(Long.valueOf(arr[1]));
                if (StringUtils.isEmpty(vo.getIndexNum())) {
                    vo.setIndexNum(getIndexNum(vo.getOrganId()));
                }
                // 保存
                PublicContentVO copyVO = (PublicContentVO) BeanUtils.cloneBean(vo);
                copyVO.setId(null);// 设为空
                copyVO.setSynOrganCatIds(null);
                copyVO.setSynColumnIds(null);
                copyVO.setSynMsgCatIds(null);
                if (isPublish) {
                    copyVO.setIsPublish(Integer.valueOf(isPublishs[i]));
                } else {
                    copyVO.setIsPublish(0);
                }
                this.saveEntity(copyVO);

                String publishStr = "";

                // 生成静态
                if (copyVO.getIsPublish() == 1) {// 发布状态
                    publishStr = "并发布";
                    Long siteId = copyVO.getSiteId();
                    if (AppUtil.isEmpty(siteId)) {
                        siteId = LoginPersonUtil.getSiteId();// 没有传站点id是默认读取当前登录的站点id
                    }
                    returnStr += siteId + "_" + copyVO.getOrganId() + "_" + copyVO.getContentId() + ",";
                }

                ContentReferRelationEO relationEO = new ContentReferRelationEO();
                relationEO.setCauseById(contentId);
                relationEO.setCauseByColumnId(oldBaseContentEO.getColumnId());
                if(!AppUtil.isEmpty(oldCatId)){
                    relationEO.setCauseByCatId(oldCatId);
                }
                relationEO.setModelCode(modelCode);
                relationEO.setColumnId(copyVO.getOrganId());
                relationEO.setReferId(copyVO.getContentId());
                relationEO.setReferModelCode(ContentReferRelationEO.ModelCode.PUBLIC.toString());
                relationEO.setCatId(copyVO.getCatId());
                relationEO.setIsColumnOpt(isColumnOpt==null?0:isColumnOpt);
                if(type == 1L){
                    relationEO.setType(ContentReferRelationEO.TYPE.COPY.toString());
                }else{
                    relationEO.setType(ContentReferRelationEO.TYPE.REFER.toString());
                }
                contentReferRelationService.saveEntity(relationEO);

                //记录操作日志
                OrganEO organ = CacheHandler.getEntity(OrganEO.class,vo.getOrganId());
                String columnName = organ.getName()+" > "+PublicCatalogUtil.getCatalogPath(vo.getOrganId(), vo.getCatId());
                if (type == 1L) {
                    SysLog.log(vo.getLogStr()+",复制到("+columnName+")"+publishStr,"PublicContentEO", CmsLogEO.Operation.Update.toString());
                }else{
                    SysLog.log(vo.getLogStr()+",引用到("+columnName+")"+publishStr,"PublicContentEO", CmsLogEO.Operation.Update.toString());
                }

            }
        }
        return StringUtils.isEmpty(returnStr) ? returnStr : returnStr.substring(0, returnStr.length() - 1);//去除最后的逗号
    }

    @Override
    public Long saveWeServiceEntity(PublicContentVO vo) {
        return saveEntity(vo);
    }

    @Override
    public void delete(Long[] ids) {
        if (null != ids && ids.length > 0) {
            for (Long id : ids) {
                PublicContentEO eo = super.getEntity(PublicContentEO.class, id);
                // 删除内容
                contentMongoService.deleteById(eo.getContentId());
                // 删除主表信息
                BaseContentEO baseContentEO = baseContentService.getEntity(BaseContentEO.class, eo.getContentId());
                Long organId = baseContentEO.getColumnId();
                Long catId = eo.getCatId();
                String title = baseContentEO.getTitle();

                if (PublicContentEO.Type.DRIVING_PUBLIC.toString().equals(eo.getType())) {// 主动公开
                    //删除引用新闻
                    List<ContentReferRelationEO> list = contentReferRelationService.getByCauseId(baseContentEO.getId(), null, ContentReferRelationEO.TYPE.REFER.toString());
                    List<Long> idsList = new ArrayList();
                    if (list != null && list.size() > 0) {
                        for(ContentReferRelationEO eo_:list){
                            idsList.add(eo_.getReferId());//引用新闻也要同步删除
                        }
                    }
                    idsList.add(baseContentEO.getId());
                    Long[] contentIds = idsList.toArray(new Long[]{});
                    baseContentService.delContent(contentIds);

                    if (baseContentEO.getIsPublish() == 1) {// 发布状态
                        publicCatalogCountService.updateOrganCatIdCountByStatus(eo.getOrganId(), eo.getCatId(), -1L, 1, false);
                    } else {// 编辑，保存
                        publicCatalogCountService.updateOrganCatIdCountByStatus(eo.getOrganId(), eo.getCatId(), -1L, 0, false);
                    }
                }else if (PublicContentEO.Type.PUBLIC_ANNUAL_REPORT.toString().equals(eo.getType())) {
                    baseContentService.delete(baseContentEO);
                    OrganEO organ = (OrganEO)this.organService.getEntity(OrganEO.class, organId);
                    if (organ != null) {
                        SysLog.log("信息公开年报：删除内容（" + baseContentEO.getTitle() + "），目录（" + organ.getName() + "-公开年报）", "PublicContentEO", CmsLogEO.Operation.Delete.toString());
                    }
                    // 删除缓存
                    CacheHandler.delete(BaseContentEO.class, baseContentEO);
                    // 删除附表信息
                    super.delete(eo);
                }else{
                    baseContentService.delete(baseContentEO);
                    // 删除缓存
                    CacheHandler.delete(BaseContentEO.class, baseContentEO);
                    // 删除附表信息
                    super.delete(eo);
                }



                //添加操作日志
                try{
                    OrganEO organ = CacheHandler.getEntity(OrganEO.class,organId);
                    String columnName = organ.getName()+" > "+PublicCatalogUtil.getCatalogPath(organId, catId);
                    SysLog.log("主动公开目录：删除内容（"+title+"），目录（"+columnName+"），公开单位（"+organ.getName()+"）","PublicContentEO", CmsLogEO.Operation.Delete.toString());
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    public Long saveEntity(PublicContentVO vo) {
        Long sortNum = null != vo.getSortDate() ? vo.getSortDate().getTime() : new Date().getTime();
        /** 构造内容信息 start */
        ContentMongoEO mongoEO = new ContentMongoEO();// 内容
        mongoEO.setContent(vo.getContent());
        /** 构造内容信息 end */

        /** 构造主表信息 start */
        Integer tempPublish = 0;
        BaseContentEO baseContentEO = new BaseContentEO();// 主表信息
        boolean addFlag = true;
        if (null != vo.getId()) {// 更新
            addFlag = false;
            baseContentEO = baseContentService.getEntity(BaseContentEO.class, vo.getContentId());
            tempPublish = baseContentEO.getIsPublish();
        }
        baseContentEO.setSiteId(vo.getSiteId());
        baseContentEO.setTitle(vo.getTitle());
        baseContentEO.setColumnId(vo.getOrganId());// 部门id当做栏目id使用
        baseContentEO.setTypeCode(BaseContentEO.TypeCode.public_content.toString());// 设置code为信息公开
        baseContentEO.setIsPublish(vo.getIsPublish());
        baseContentEO.setRemarks(vo.getSummarize());
        baseContentEO.setAuthor(vo.getAuthor());
        baseContentEO.setResources(vo.getResources());
        baseContentEO.setCreateDate(vo.getCreateDate());
        baseContentEO.setPublishDate(vo.getPublishDate());
        baseContentEO.setAttachRealName(vo.getAttachRealName());
        baseContentEO.setAttachSavedName(vo.getAttachSavedName());
        baseContentEO.setAttachSize(vo.getAttachSize());
        baseContentEO.setRedirectLink(vo.getRedirectLink());
        baseContentEO.setNum(sortNum);
        baseContentEO.setVideoStatus(vo.getVideoStatus());
        baseContentEO.setOldSchemaId(vo.getOldSchemaId());
        baseContentEO.setVideoStatus(vo.getVideoStatus());
        /** 构造主表信息 end */

        /** 构造附表信息 start */
        PublicContentEO publicContentEO = new PublicContentEO();// 附表信息
        if (null != vo.getId()) {// 更新
            publicContentEO = this.getEntity(PublicContentEO.class, vo.getId());
        }
        publicContentEO.setOrganId(vo.getOrganId());
        publicContentEO.setSiteId(vo.getSiteId());
        publicContentEO.setCatId(vo.getCatId());
        if (StringUtils.isEmpty(vo.getIndexNum())) {
            publicContentEO.setIndexNum(this.getIndexNum(vo.getOrganId()));
        } else {
            publicContentEO.setIndexNum(vo.getIndexNum());
        }
        publicContentEO.setFileNum(vo.getFileNum());
        publicContentEO.setClassIds(vo.getClassIds());
        publicContentEO.setParentClassIds(vo.getParentClassIds());
        publicContentEO.setSynColumnIds(vo.getSynColumnIds());
        publicContentEO.setSynOrganCatIds(vo.getSynOrganCatIds());
        publicContentEO.setSynMsgCatIds(vo.getSynMsgCatIds());
        publicContentEO.setType(vo.getType());
        publicContentEO.setKeyWords(vo.getKeyWords());
        publicContentEO.setSummarize(vo.getSummarize());
        publicContentEO.setCreateDate(vo.getCreateDate());
        publicContentEO.setEffectiveDate(vo.getEffectiveDate());
        publicContentEO.setRepealDate(vo.getRepealDate());
        publicContentEO.setSortNum(sortNum);
        /** 构造附表信息 end */

        Long id = vo.getId();
        if (null != id) {// 更新
            // 更新mongodb内容
            mongoEO.setId(vo.getContentId());
            contentMongoService.save(mongoEO);
            // 更新主表信息
            baseContentEO.setId(vo.getContentId());
            baseContentService.updateEntity(baseContentEO);
            // 更新缓存
            CacheHandler.saveOrUpdate(BaseContentEO.class, baseContentEO);
            // 更新附表信息
            publicContentEO.setContentId(vo.getContentId());
            //更新
            super.updateEntity(publicContentEO);
            if (!vo.getIsPublish().equals(tempPublish)) {//状态不相等才更改
                publicCatalogCountService.updateOrganCatIdCountByStatus(vo.getOrganId(), vo.getCatId(), 1L, vo.getIsPublish(), true);
            }
        } else {
            // 保存主表信息
            Long contentId = baseContentService.saveEntity(baseContentEO);
            // 设置vo的contentId
            vo.setContentId(contentId);
            // 更新缓存
            CacheHandler.saveOrUpdate(BaseContentEO.class, baseContentEO);
            // 保存mongodb内容
            mongoEO.setId(contentId);
            contentMongoService.save(mongoEO);
            // 保存附表信息
            publicContentEO.setContentId(contentId);
            id = super.saveEntity(publicContentEO);
            // 更新
            publicCatalogCountService.updateOrganCatIdCountByStatus(vo.getOrganId(), vo.getCatId(), 1L, vo.getIsPublish(), false);
        }

        Long catId = vo.getCatId();
        PublicCatalogEO catalogEO = publicCatalogService.getEntity(PublicCatalogEO.class,catId);
        Long organId = vo.getOrganId();
        OrganEO organEO = organService.getEntity(OrganEO.class,organId);


        if(addFlag){//新增
            SysLog.log("主动公开目录：添加内容（"+baseContentEO.getTitle()+"），目录（"+(catalogEO==null?"":catalogEO.getName())
                            +"），公开单位（"+organEO==null?"":organEO.getName()+"）","PublicContentEO", CmsLogEO.Operation.Update.toString());
        }else{
            if(catalogEO == null){
                SysLog.log("主动公开目录：修改内容（"+baseContentEO.getTitle()+"），目录（"
                        +"），公开单位（"+(organEO==null?"":organEO.getName())+"）","PublicContentEO", CmsLogEO.Operation.Update.toString());
            }else{
                SysLog.log("主动公开目录：修改内容（"+baseContentEO.getTitle()+"），目录（"+catalogEO.getName()
                        +"），公开单位（"+(organEO==null?"":organEO.getName())+"）","PublicContentEO", CmsLogEO.Operation.Update.toString());
            }
        }
        return id;
    }

    @Override
    public void deleteAll(PublicContentQueryVO queryVO) {
        Map<String, Object> map = new HashMap<String, Object>();
        if (StringUtils.isNotEmpty(queryVO.getType())) {
            map.put("type", queryVO.getType());
        }
        if (null != queryVO.getOrganId()) {
            map.put("organId", queryVO.getOrganId());
        }
        if (null != queryVO.getCatId()) {
            map.put("catId", queryVO.getCatId());
        }
        map.put("recordStatus", AMockEntity.RecordStatus.Normal.toString());
        List<PublicContentEO> allList = super.getEntities(PublicContentEO.class, map);
        if (null != allList && !allList.isEmpty()) {
            int batch = 50;
            List<Object> mongoIdList = new ArrayList<Object>();
            for (PublicContentEO eo : allList) {
                mongoIdList.add(eo.getContentId());
                if (mongoIdList.size() % batch == 0) {
                    // 删除内容
                    contentMongoService.removeByIds(mongoIdList.toArray());
                    mongoIdList.clear();
                }
                // 删除主表信息
                BaseContentEO baseContentEO = baseContentService.getEntity(BaseContentEO.class, eo.getContentId());
                baseContentService.delete(baseContentEO);
                if (baseContentEO.getIsPublish() != null && baseContentEO.getIsPublish() == 1) {// 发布状态
                    publicCatalogCountService.updateOrganCatIdCountByStatus(eo.getOrganId(), eo.getCatId(), -1L, 1, false);
                } else {// 编辑，保存
                    publicCatalogCountService.updateOrganCatIdCountByStatus(eo.getOrganId(), eo.getCatId(), -1L, 0, false);
                }
                // 删除附表信息
                publicContentDao.remove(eo);
            }
            if (!mongoIdList.isEmpty()) {
                // 删除内容
                contentMongoService.removeByIds(mongoIdList.toArray());
            }
        }
    }

    @Override
    public Pagination getPagination(PublicContentQueryVO queryVO) {
        Pagination pagination = publicContentDao.getPagination(queryVO);
        List<?> dataList = pagination.getData();
        if (null != dataList && !dataList.isEmpty()) {
            Map<Long, String> catIdPathMap = new HashMap<Long, String>();//目录全路径缓存
            Map<Long, String> organIdNameMap = new HashMap<Long, String>();//单位名称缓存

            List<Long> ids = new ArrayList<Long>();

            for (Object obj : dataList) {
                PublicContentVO vo = (PublicContentVO) obj;
                ids.add(vo.getContentId());
            }

            List<Long> referedIds = contentReferRelationService.getReferedIds(ids.toArray(new Long[]{}),null);
            List<Long> referIds = contentReferRelationService.getReferIds(ids.toArray(new Long[]{}),null);
            for (Object o : dataList) {// 只查询单位和目录即可
                PublicContentVO vo = (PublicContentVO) o;
                if(referedIds!=null&&referedIds.size()>0){
                    vo.setReferedNews(referedIds.contains(vo.getContentId()));//判断是否被引用新闻
                }
                if(referIds!=null&&referIds.size()>0){
                    vo.setReferNews(referIds.contains(vo.getContentId()));//判断是否引用新闻
                }
                if (queryVO.isQueryDetail()){// 需要查询详情
                    Long catId = vo.getCatId();
                    Long organId = vo.getOrganId();
                    if (!catIdPathMap.containsKey(catId)) {
                        String path = PublicCatalogUtil.getCatalogPath(organId, catId);
                        catIdPathMap.put(catId, path);
                    }
                    if (!organIdNameMap.containsKey(organId)) {
                        OrganEO eo = CacheHandler.getEntity(OrganEO.class, organId);
                        organIdNameMap.put(organId, null == eo ? "" : eo.getName());
                    }
                    if(StringUtils.isNotEmpty(vo.getRelContentId())){
                        String[] relIds = vo.getRelContentId().split(",");
                        StringBuilder filePath = new StringBuilder();
                        for(String id : relIds){
                            BaseContentEO item = CacheHandler.getEntity(BaseContentEO.class, Long.valueOf(id));
                            if(item.getTypeCode().equals(BaseContentEO.TypeCode.public_content.toString())){
                                filePath.append("/").append("public").append("/").append(item.getColumnId());
                            }else{
                                ColumnMgrEO column = CacheHandler.getEntity(ColumnMgrEO.class, item.getColumnId());
                                //如果获取不到栏目，就认为是信息公开的单位
                                if(column != null){
                                    filePath.append("/").append(column.getUrlPath());
                                }
                            }
                            filePath.append("/").append(id);
                            filePath.append(".html,");
                        }
                        vo.setFilePath(filePath.substring(0, filePath.length() - 1));
                    }else{
                        vo.setFilePath("");
                    }
                    vo.setCatName(catIdPathMap.get(catId));
                    vo.setOrganName(organIdNameMap.get(organId));
                }
            }
        }
        return pagination;
    }

    @Override
    public PublicContentVO getPublicContent(Long contentId) {
        PublicContentVO vo = publicContentDao.getPublicContent(contentId);
        this.setValueToContent(vo);
        return vo;
    }

    @Override
    public PublicContentVO getPublicContentByBaseContentId(Long contentId) {
        PublicContentVO vo = publicContentDao.getPublicContentByBaseContentId(contentId);
        this.setValueToContent(vo);
        return vo;
    }

    @Override
    public PublicContentVO getPublicContentWithCatNameByBaseContentId(Long contentId) {
        return this.getPublicContentByBaseContentId(contentId);
    }

    @Override
    public PublicContentVO getPublicContent(PublicContentQueryVO queryVO) {
        PublicContentVO vo = publicContentDao.getPublicContent(queryVO);
        this.setValueToContent(vo);
        return vo;
    }

    /**
     * 设置内容值
     *
     * @param voArray
     * @author fangtinghua
     */
    private void setValueToContent(PublicContentVO... voArray) {
        if (null == voArray || voArray.length == 0) {
            return;
        }
        Set<Long> contentIdSet = new HashSet<Long>();
        Set<Long> classSet = new HashSet<Long>();
        Set<Long> catIdSet = new HashSet<Long>();
        Set<Long> organIdSet = new HashSet<Long>();
        for (PublicContentVO vo : voArray) {// 只有主动公开的时候才需要分类列表、目录
            if (null == vo) {// 为空
                continue;
            }
            contentIdSet.add(vo.getContentId());
            if (PublicContentEO.Type.DRIVING_PUBLIC.toString().equals(vo.getType())) {
                String classIds = vo.getClassIds();
                if (StringUtils.isNotEmpty(classIds)) {
                    classSet.addAll(cn.lonsun.core.base.util.StringUtils.getListWithLong(classIds, ","));
                }
                Long catId = vo.getCatId();
                if (null != catId) {
                    catIdSet.add(catId);
                }
                Long organId = vo.getOrganId();
                if (null != organId) {
                    organIdSet.add(organId);
                }
            }
        }
        // 查询mongodb内容
        Map<Long, String> contentIdMap = new HashMap<Long, String>();
        if (!contentIdSet.isEmpty()) {
            List<ContentMongoEO> contentList = contentMongoService.queryListByIds(contentIdSet.toArray(new Long[]{}));
            if (null != contentList && !contentList.isEmpty()) {
                for (ContentMongoEO mongo : contentList) {
                    if (null != mongo) {
                        contentIdMap.put(mongo.getId(), mongo.getContent());
                    }
                }
            }
        }
        // 查询所属分类
        Map<Long, PublicClassEO> classIdMap = new HashMap<Long, PublicClassEO>();
        if (!classSet.isEmpty()) {
            List<PublicClassEO> classList = publicClassService.getEntities(PublicClassEO.class, classSet.toArray(new Long[]{}));
            if (null != classList && !classList.isEmpty()) {
                for (PublicClassEO clazz : classList) {
                    if (null != clazz) {
                        classIdMap.put(clazz.getId(), clazz);
                    }
                }
            }
        }
        // 设置目录名称
        Map<Long, PublicCatalogEO> catalogMap = new HashMap<Long, PublicCatalogEO>();
        if (!catIdSet.isEmpty()) {
            List<PublicCatalogEO> catalogList = publicCatalogService.getEntities(PublicCatalogEO.class, catIdSet.toArray(new Long[]{}));
            if (null != catalogList && !catalogList.isEmpty()) {
                for (PublicCatalogEO catalog : catalogList) {
                    if (null != catalog) {
                        catalogMap.put(catalog.getId(), catalog);
                    }
                }
            }
        }
        // 设置单位名称
        Map<Long, OrganEO> organMap = new HashMap<Long, OrganEO>();
        if (!organIdSet.isEmpty()) {
            List<OrganEO> organList = organService.getEntities(OrganEO.class, organIdSet.toArray(new Long[]{}));
            if (null != organList && !organList.isEmpty()) {
                for (OrganEO organ : organList) {
                    if (null != organ) {
                        organMap.put(organ.getOrganId(), organ);
                    }
                }
            }
        }
        // 单位目录对应关系缓存
        Map<Long, Map<Long, PublicCatalogOrganRelEO>> cacheMap = new HashMap<Long, Map<Long, PublicCatalogOrganRelEO>>();
        // 设置值
        for (PublicContentVO vo : voArray) {
            if (null == vo) {
                continue;
            }
            // 设置内容
            vo.setContent(HotWordsCheckUtil.revertAll(contentIdMap.get(vo.getContentId())));
            // 设置组配分类、目录、单位名称
            if (PublicContentEO.Type.DRIVING_PUBLIC.toString().equals(vo.getType())) {
                String classIds = vo.getClassIds();
                if (StringUtils.isNotEmpty(classIds)) {
                    Long[] classIdArray = cn.lonsun.core.base.util.StringUtils.getArrayWithLong(classIds, ",");
                    List<String> classNameList = new ArrayList<String>();
                    for (Long classId : classIdArray) {
                        if (classIdMap.containsKey(classId)) {
                            classNameList.add(classIdMap.get(classId).getName());
                        }
                    }
                    vo.setClassNames(StringUtils.join(classNameList, ","));
                }
                Long organId = vo.getOrganId();
                if (null != organId && organMap.containsKey(organId)) {
                    vo.setOrganName(organMap.get(organId).getName());
                    Long catId = vo.getCatId();
                    if (null != catId && catalogMap.containsKey(catId)) {
                        Map<Long, PublicCatalogOrganRelEO> relMap = null;
                        if (cacheMap.containsKey(organId)) {
                            relMap = cacheMap.get(organId);
                        } else {
                            relMap = PublicCatalogUtil.getCatalogRelMap(organId);
                            cacheMap.put(organId, relMap);
                        }
                        if (null != relMap && relMap.containsKey(catId)) {// 存在关系
                            vo.setCatName(relMap.get(catId).getName());
                        } else {
                            vo.setCatName(catalogMap.get(catId).getName());
                        }
                    }
                }
            }
        }
    }

    @Override
    public void updatePublicStatus(Long[] ids, Integer status) {
        if (null != ids && ids.length > 0) {
            Long siteId = null;
            List<Long> idsList = new ArrayList<Long>(ids.length);
            for (Long id : ids) {
                PublicContentEO eo = super.getEntity(PublicContentEO.class, id);
                if (null == siteId) {
                    siteId = eo.getSiteId();
                }
                idsList.add(eo.getContentId());
            }
            // 更新主表发布状态
            baseContentService.changePublish(new ContentPageVO(siteId, null, status, idsList.toArray(new Long[ids.length]), null));
        }
    }

    public Pagination createPublicIndex(Long pageIndex, Integer pageSize) {
        Pagination pagination = publicContentDao.getAllListForPage(pageIndex, pageSize);
        if (null != pagination) {// 设置内容字段
            List<?> data = pagination.getData();
            if (null != data && !data.isEmpty()) {
                this.setValueToContent(data.toArray(new PublicContentVO[]{}));
            }
        }
        return pagination;
    }

    public void createPublicIndex(List<?> data) {
        if (null == data || data.isEmpty()) {
            return;
        }
        Set<SolrIndexVO> voList = new HashSet<SolrIndexVO>();
        for (Object o : data) {
            SolrIndexVO vo = new SolrIndexVO();
            PublicContentVO eo = (PublicContentVO) o;
            vo.setId(eo.getContentId() + "");
            vo.setTitle(eo.getTitle());
            vo.setColumnId(eo.getOrganId());
            vo.setContent(eo.getContent());
            vo.setSiteId(eo.getSiteId());
            vo.setFileNum(eo.getFileNum());
            vo.setIndexNum(eo.getIndexNum());
            vo.setTypeCode(BaseContentEO.TypeCode.public_content.toString());
            vo.setCreateDate(eo.getPublishDate());
            vo.setAuthor(eo.getAuthor());
            voList.add(vo);
        }
        try {
            SolrFactory.createIndex(voList);
        } catch (Throwable e) {
            throw new BaseRunTimeException(TipsMode.Message.toString(), "信息公开索引创建失败！");
        }
    }

    @Override
    public void createPublicIndex() {
        // 删除
        try {
            SolrFactory.deleteIndexByTypeCodeSyn(BaseContentEO.TypeCode.public_content.toString());
        } catch (Throwable e) {
            throw new BaseRunTimeException(TipsMode.Message.toString(), "信息公开索引删除失败！");
        }
        // 分页查询建索引，每次5000;
        Long pageIndex = 0L;
        Integer pageSize = 5000;
        // 先查询一次
        Pagination pagination = this.createPublicIndex(pageIndex, pageSize);
        if (null != pagination) {
            // 创建索引
            this.createPublicIndex(pagination.getData());
            Long pageCount = pagination.getPageCount();
            while (pageIndex < pageCount) {// 继续分页查询
                pagination = this.createPublicIndex(pageIndex++, pageSize);
                if (null != pagination) {
                    this.createPublicIndex(pagination.getData());
                }
            }
        }
    }

    @Override
    public List<PublicContentVO> getList(PublicContentQueryVO queryVO, Integer num) {
        return publicContentDao.getList(queryVO, num);
    }

    @Override
    public List<ContentChartVO> getChartList(ContentChartQueryVO queryVO, String type) {
        List<ContentChartVO> list = baseContentService.getContentChart(queryVO);
        if (list == null) {
            return null;
        }
        List<ContentChartVO> newList = new ArrayList<ContentChartVO>();
        List<ContentChartVO> list1 = new ArrayList<ContentChartVO>();
        if (StringUtils.isEmpty(type)) {
            list1 = publicApplyDao.getChartList(queryVO);
        } else {
            list1 = publicContentDao.getChartList(queryVO, type);
        }
        for (ContentChartVO vo : list) {
            ContentChartVO newVO = new ContentChartVO();
            newVO.setOrganId(vo.getOrganId());
            newVO.setOrganName(vo.getOrganName());
            if (list1 != null && list1.size() > 0) {
                for (ContentChartVO vo1 : list1) {
                    if (vo.getOrganId().equals(vo1.getOrganId())) {
                        newVO.setCount(vo1.getCount());
                    }
                }
            }
            newList.add(newVO);
        }
        return newList;
    }

    @Override
    public Pagination getPublicPage(StatisticsQueryVO queryVO) {
        List<PublicListVO> newList = getPublicList(queryVO);
        Pagination page = new Pagination();
        int index = Integer.parseInt(String.valueOf(queryVO.getPageIndex()));
        int size = queryVO.getPageSize();
        int startRow = index * size;
        int endRow = (index + 1) * size;
        if (newList != null) {
            if (newList.size() < endRow) {
                endRow = newList.size();
            }
        }
        page.setData(newList.subList(startRow, endRow));
        page.setTotal(Long.parseLong(String.valueOf(newList.size())));
        page.setPageSize(size);
        page.setPageIndex(queryVO.getPageIndex());
        return page;
    }

    @Override
    public List<PublicListVO> getPublicList(StatisticsQueryVO queryVO) {
        List<WordListVO> listData = baseContentDao.getWordList1(queryVO);
        List<PublicListVO> newList = new ArrayList<PublicListVO>();
        SiteMgrEO siteEO = CacheHandler.getEntity(SiteMgrEO.class, queryVO.getSiteId());
        List<OrganEO> organList = new ArrayList<OrganEO>();
        if (siteEO != null && !StringUtils.isEmpty(siteEO.getUnitIds())) {
            Long[] arr = AppUtil.getLongs(siteEO.getUnitIds(), ",");
            if (arr != null && arr.length > 0) {
                organList = organService.getOrgansByDn(arr[0], OrganEO.Type.OrganUnit.toString());// 部门
            }
        } else {
            throw new BaseRunTimeException(TipsMode.Message.toString(), "此站点下没有绑定单位");
        }
        if (listData != null && listData.size() >= 0) {
            Map<Long, WordListVO> map = new HashMap<Long, WordListVO>();
            for (WordListVO wordListVO : listData) {
                if (wordListVO.getOrganId() != null) {
                    map.put(wordListVO.getOrganId(), wordListVO);
                }
            }
            String organName = queryVO.getOrganName();
            WordListVO wordListVO = null;
            for (int i = 0; i < organList.size(); i++) {
                PublicListVO vo = new PublicListVO();
                String allName = "";
                OrganEO unit = organService.getUnitByOrganDn(organList.get(i).getDn());
                if (unit != null) {
                    allName = "[" + unit.getName() + "]";
                }
                vo.setOrganName(allName + organList.get(i).getName());
                vo.setOrganId(organList.get(i).getOrganId());
//                for (WordListVO wordListVO : listData) {
//                    if (vo.getOrganId().equals(wordListVO.getOrganId())) {
                wordListVO = map.get(vo.getOrganId());
                if (wordListVO != null) {
                    vo.setCount(wordListVO.getCount() == null ? 0 : wordListVO.getCount());
                    vo.setPublishCount(wordListVO.getPublishCount() == null ? 0 : wordListVO.getPublishCount());
                    vo.setUnPublishCount(wordListVO.getUnPublishCount() == null ? 0 : wordListVO.getUnPublishCount());
                    vo.setRate(wordListVO.getRate() == null ? 0 : (Math.round(wordListVO.getRate())));
                } else {
                    vo.setCount(0L);
                    vo.setPublishCount(0L);
                    vo.setUnPublishCount(0L);
                    vo.setRate(0);
                }
                queryVO.setOrganId(organList.get(i).getOrganId());
                Long count1 = publicContentDao.getTypeCount(queryVO, PublicContentEO.Type.PUBLIC_GUIDE.toString());
                vo.setPublishCount1(count1 == null ? 0 : count1);
                Long count2 = publicContentDao.getTypeCount(queryVO, PublicContentEO.Type.PUBLIC_ANNUAL_REPORT.toString());
                vo.setPublishCount2(count2 == null ? 0 : count2);
                Long count3 = publicContentDao.getTypeCount(queryVO, PublicContentEO.Type.PUBLIC_INSTITUTION.toString());
                vo.setPublishCount3(count3 == null ? 0 : count3);
                Long count4 = publicContentDao.getTypeCount(queryVO, PublicContentEO.Type.DRIVING_PUBLIC.toString());
                vo.setPublishCount4(count4 == null ? 0 : count4);
                Long count5 = publicApplyDao.getTypeCount(queryVO);
                vo.setPublishCount5(count5 == null ? 0 : count5);
//                    }
//                }
                newList.add(vo);
                if (!StringUtils.isEmpty(organName)) {
                    if (!organList.get(i).getName().contains(organName)) {
                        newList.remove(vo);
                    }
                }
            }
        }
        return newList;
    }

    @Override
    public Pagination getRetrievalPagination(PublicContentRetrievalVO queryVO) {
        Long catId = queryVO.getCatId();
        if (null != catId && catId > 0) {// 查询出所有子分类
            List<PublicCatalogEO> publicCatalogList = publicCatalogService.getAllChildListByCatId(catId);
            if (null != publicCatalogList && !publicCatalogList.isEmpty()) {
                int index = 0;
                Long[] catIds = new Long[publicCatalogList.size() + 1];
                catIds[index++] = catId;
                for (PublicCatalogEO eo : publicCatalogList) {
                    catIds[index++] = eo.getId();
                }
                queryVO.setCatIds(catIds);
            }
        }
        return publicContentDao.getRetrievalPagination(queryVO);
    }

    @Override
    public Pagination getPublicGuide(Long siteId, Long organId, Long pageIndex, Integer pageSize) {
        return publicContentDao.getPublicGuide(siteId, organId, pageIndex, pageSize);
    }

    @Override
    public Pagination getPublicAnnualReport(Long siteId, Long organId, Long pageIndex, Integer pageSize) {
        return publicContentDao.getPublicAnnualReport(siteId, organId, pageIndex, pageSize);
    }

    @Override
    public PublicContentVO getPublicContentInfo(Long id) {
        return publicContentDao.getPublicContentInfo(id);
    }

    @Override
    public List<PublicContentVO> getCounts(PublicContentQueryVO queryVO, Integer limit) {
        return publicContentDao.getCounts(queryVO, limit);
    }

    @Override
    public List<PublicTjVO> getPublicTjList(PublicContentQueryVO queryVO) {
        return publicContentDao.getPublicTjList(queryVO);
    }

    @Override
    public List<PublicTjVO> getPublicTjByCatIdList(PublicContentQueryVO queryVO) {
        return publicContentDao.getPublicTjByCatIdList(queryVO);
    }

    @Override
    public List<PublicTjForDateVO> getPublicTjListByDate(PublicContentQueryVO queryVO) {
        return publicContentDao.getPublicTjListByDate(queryVO);
    }

    @Override
    public void updateSort(SortUpdateVO sortVo) {
        PublicContentEO content = publicContentDao.getEntity(PublicContentEO.class, sortVo.getId());
        PublicContentEO content1 = publicContentDao.getSort(sortVo);
        if (content != null && content1 != null) {
            Long sortN = content.getSortNum();
            content.setSortNum(content1.getSortNum());
            content1.setSortNum(sortN);
            publicContentDao.update(content);
            publicContentDao.update(content1);
        }
    }

    @Override
    public void updatePublicStatus(Long[] contentIds, Long organId, Long catId, Integer status) {
        // 这里要根据原文章的发布状态来判断
        Long[] increment = this.computeIncrement(contentIds);// 包含发布数未发布数
        baseContentService.changePublish(new ContentPageVO(LoginPersonUtil.getSiteId(), organId, status, contentIds, null));// 更新状态
        if (null != increment) {
            if (increment[0] > 0L) {// 取消发布时只改已发布的数量，已发布量减少，未发布量增加
                publicCatalogCountService.updateOrganCatIdCountByStatus(organId, catId, increment[0], 0, true);
            }
            if (increment[1] > 0L) {// 发布状态时只改未发布的数量，已发布量增加，未发布量减少
                publicCatalogCountService.updateOrganCatIdCountByStatus(organId, catId, increment[1], 1, true);
            }
        }
    }

    @Override
    public void updateSortNum(Long id, Long sortNum) {
        PublicContentEO content = publicContentDao.getEntity(PublicContentEO.class, id);
        SortVO sort = publicContentDao.getMaxNum(null);
        if (sortNum > sort.getSortNum()) {
            throw new BaseRunTimeException(TipsMode.Message.toString(), "填写的排序号不得大于最大排序号" + sort.getSortNum());
        }
        content.setSortNum(sortNum);
        updateEntity(content);
    }

    @Override
    public List<Long> getCatalogIdByOrganId(Long organId) {
        return publicContentDao.getCatalogIdByOrganId(organId);
    }

    /**
     * 计算出增量，发布和未发布数
     *
     * @param contentIds
     * @return 2位，第一位为发布数，第二位为未发布数
     */
    private Long[] computeIncrement(Long[] contentIds) {
        if (null == contentIds || contentIds.length <= 0) {
            return null;
        }
        List<BaseContentEO> baseContentList = baseContentService.getEntities(BaseContentEO.class, contentIds);
        if (null == baseContentList || baseContentList.isEmpty()) {
            return null;
        }
        Long publishCount = 0L, unPublishCount = 0L;
        for (BaseContentEO eo : baseContentList) {
            if (eo.getIsPublish() == 1) {
                publishCount++;
            } else if (eo.getIsPublish() == 0) {
                unPublishCount++;
            }
        }
        return new Long[]{publishCount, unPublishCount};
    }

    @Override
    public List<Map<String, Object>> getPublicContentStatisByCatalogAndOrganId(PublicContentQueryVO queryVO) {
        return publicContentDao.getPublicContentStatisByCatalogAndOrganId(queryVO);
    }
}