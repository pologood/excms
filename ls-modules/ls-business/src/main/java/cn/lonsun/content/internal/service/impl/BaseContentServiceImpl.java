package cn.lonsun.content.internal.service.impl;

import cn.lonsun.activemq.MessageSenderUtil;
import cn.lonsun.base.anno.DbInject;
import cn.lonsun.cache.client.CacheGroup;
import cn.lonsun.cache.client.CacheHandler;
import cn.lonsun.common.upload.internal.entity.AttachmentEO;
import cn.lonsun.common.upload.internal.service.IUploadService;
import cn.lonsun.common.util.AppUtil;
import cn.lonsun.content.commentMgr.internal.service.ICommentService;
import cn.lonsun.content.internal.dao.IBaseContentDao;
import cn.lonsun.content.internal.entity.BaseContentEO;
import cn.lonsun.content.internal.entity.ContentPicEO;
import cn.lonsun.content.internal.entity.ContentReferRelationEO;
import cn.lonsun.content.internal.entity.VideoNewsEO;
import cn.lonsun.content.internal.service.IBaseContentService;
import cn.lonsun.content.internal.service.IContentPicService;
import cn.lonsun.content.internal.service.IContentReferRelationService;
import cn.lonsun.content.internal.service.IVideoNewsService;
import cn.lonsun.content.vo.*;
import cn.lonsun.core.base.entity.AMockEntity;
import cn.lonsun.core.base.service.impl.MockService;
import cn.lonsun.core.enums.SystemCodes;
import cn.lonsun.core.exception.BaseRunTimeException;
import cn.lonsun.core.util.Pagination;
import cn.lonsun.core.util.TipsMode;
import cn.lonsun.indicator.internal.entity.FunctionEO;
import cn.lonsun.indicator.internal.entity.IndicatorEO;
import cn.lonsun.message.internal.entity.MessageEnum;
import cn.lonsun.message.internal.entity.MessageStaticEO;
import cn.lonsun.mongodb.entity.ContentMongoEO;
import cn.lonsun.mongodb.service.ContentMongoServiceImpl;
import cn.lonsun.msg.submit.entity.CmsMsgSubmitEO;
import cn.lonsun.msg.submit.service.IMsgSubmitService;
import cn.lonsun.publicInfo.internal.entity.PublicCatalogEO;
import cn.lonsun.publicInfo.internal.entity.PublicCatalogOrganRelEO;
import cn.lonsun.publicInfo.internal.entity.PublicContentEO;
import cn.lonsun.publicInfo.internal.service.IPublicCatalogCountService;
import cn.lonsun.publicInfo.internal.service.IPublicCatalogOrganRelService;
import cn.lonsun.publicInfo.internal.service.IPublicContentService;
import cn.lonsun.publicInfo.vo.PublicContentVO;
import cn.lonsun.rbac.internal.entity.OrganEO;
import cn.lonsun.rbac.internal.entity.PersonEO;
import cn.lonsun.rbac.internal.service.IOrganService;
import cn.lonsun.rbac.internal.service.IPersonService;
import cn.lonsun.site.contentModel.internal.entity.ModelTemplateEO;
import cn.lonsun.site.site.internal.entity.ColumnConfigEO;
import cn.lonsun.site.site.internal.entity.ColumnMgrEO;
import cn.lonsun.site.site.internal.entity.SiteMgrEO;
import cn.lonsun.site.site.internal.service.IColumnConfigService;
import cn.lonsun.statistics.ContentChartVO;
import cn.lonsun.statistics.StatisticsQueryVO;
import cn.lonsun.statistics.WordListVO;
import cn.lonsun.system.datadictionary.vo.DataDictVO;
import cn.lonsun.system.filecenter.internal.entity.FileCenterEO;
import cn.lonsun.system.filecenter.internal.service.impl.FileCenterServiceImpl;
import cn.lonsun.system.role.internal.service.ISiteRightsService;
import cn.lonsun.system.systemlog.internal.entity.CmsLogEO;
import cn.lonsun.util.*;
import cn.lonsun.wechatmgr.internal.entity.WeChatArticleEO;
import cn.lonsun.wechatmgr.internal.service.IWeChatArticleService;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.beanutils.ConvertUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Hewbing
 * @ClassName: ContentServiceImpl
 * @Description: 新闻信息业务逻辑
 * @date 2015年10月15日 上午11:27:45
 */
@Service("baseContentService")
public class BaseContentServiceImpl extends MockService<BaseContentEO> implements IBaseContentService {

    @DbInject("baseContent")
    private IBaseContentDao baseContentDao;
    @Autowired
    private IUploadService uploadService;
    @Autowired
    private IContentPicService contentPicService;
    @Autowired
    private ContentMongoServiceImpl contentMongoService;
    @Autowired
    private ICommentService commentService;
    @Resource
    private ContentMongoServiceImpl mongoService;
    @Autowired
    private IVideoNewsService videoService;
    @Resource
    private IPublicContentService publicContentService;
    @Autowired
    private IOrganService organService;
    @Autowired
    private IContentReferRelationService contentReferRelationService;
    @Autowired
    private IMsgSubmitService msgSubmitService;
    @Autowired
    private ISiteRightsService siteRightsService;
    @Autowired
    private FileCenterServiceImpl fileCenterService;
    @Resource
    private IWeChatArticleService weChatArticleService;
    @Autowired
    private IColumnConfigService columnConfigService;
    @Autowired
    private IPersonService personService;
    @Autowired
    private IPublicCatalogCountService publicCatalogCountService;
    @Autowired
    private IPublicCatalogOrganRelService publicCatalogOrganRelService;

    /**
     * 重写父类方法
     *
     * @param baseContentEO
     * @return
     */
    @Override
    public Long saveEntity(BaseContentEO baseContentEO) {
        try {
            if (AppUtil.isEmpty(baseContentEO.getUnitId())) {
                baseContentEO.setUnitId(LoginPersonUtil.getUnitId());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return super.saveEntity(baseContentEO);
    }

    @Override
    public Pagination getPage(ContentPageVO pageVO) {
        if (pageVO.getSiteId() == null) {
            pageVO.setSiteId(LoginPersonUtil.getSiteId());
        }

        Pagination pagination = baseContentDao.getPage(pageVO);
        //图片新闻没有缩略图时抓取内容中第一张图片作为缩略图
        List<?> list = pagination.getData();
        List<Long> ids = new ArrayList<Long>();

        for (Object obj : list) {
            BaseContentEO eo = (BaseContentEO) obj;
            ids.add(eo.getId());
        }

        List<Long> referedIds = contentReferRelationService.getReferedIds(ids.toArray(new Long[]{}),null);
        List<Long> referIds = contentReferRelationService.getReferIds(ids.toArray(new Long[]{}),null);

        for (Object obj : list) {
            BaseContentEO eo = (BaseContentEO) obj;
            if(referedIds!=null&&referedIds.size()>0){
                eo.setReferedNews(referedIds.contains(eo.getId()));//判断是否被引用新闻
            }
            if(referIds!=null&&referIds.size()>0){
                eo.setReferNews(referIds.contains(eo.getId()));//判断是否引用新闻
            }
            if (BaseContentEO.TypeCode.pictureNews.toString().equals(eo.getTypeCode())) {
                getImageLinkFromContent(eo);
            }
        }

        return pagination;
    }

    /**
     * 获取新闻分页通过父节点
     * @param pageVO
     * @return
     */
    @Override
    public Pagination getPageByParentId(String columnIds,ContentPageVO pageVO) {
        if (pageVO.getSiteId() == null) {
            pageVO.setSiteId(LoginPersonUtil.getSiteId());
        }

        //父节点查询将columnID设为null
        pageVO.setColumnId(null);
        pageVO.setColumnIds(columnIds);

        Pagination pagination;

        //如果父节点下没有任何columnId
        if (AppUtil.isEmpty(pageVO.getColumnIds())) {
            pagination = new Pagination();
            pagination.setData(Collections.emptyList());
            pagination.setTotal(0l);
            pagination.setPageIndex(pageVO.getPageIndex());
            pagination.setPageSize(pageVO.getPageSize());
        }else {
            pagination = baseContentDao.getPage(pageVO);
        }

        //图片新闻没有缩略图时抓取内容中第一张图片作为缩略图
        List<?> list = pagination.getData();
        for (Object obj : list) {
            BaseContentEO eo = (BaseContentEO) obj;
            eo.setReferedNews(contentReferRelationService.checkIsRefered(eo.getId()));//判断是否被引用新闻
            eo.setReferNews(contentReferRelationService.checkIsRefer(eo.getId()));//判断是否引用新闻
            if (eo.getTypeCode().equals(BaseContentEO.TypeCode.pictureNews.toString())) {
                getImageLinkFromContent(eo);
            }

            ColumnMgrEO columnEO = CacheHandler.getEntity(ColumnMgrEO.class,eo.getColumnId());
            eo.setColumnName(columnEO.getName());
        }

        return pagination;
    }

    /**
     * 图片新闻没有缩略图时抓取内容中第一张图片作为缩略图
     *
     * @param eo
     * @return
     */
    public BaseContentEO getImageLinkFromContent(BaseContentEO eo) {
//        if (eo.getTypeCode().equals(BaseContentEO.TypeCode.pictureNews.toString())) {
        Pattern p_image;
        Matcher m_image;
        if (AppUtil.isEmpty(eo.getImageLink())) {
            //去mongoDB查询内容
            Criteria criteria = Criteria.where("_id").is(eo.getId());
            Query query = new Query(criteria);
            ContentMongoEO _eo = contentMongoService.queryOne(query);
            if (_eo != null && !AppUtil.isEmpty(_eo.getContent())) {
                String content = _eo.getContent();
                String img = "";
                //正则获取图片
                p_image = Pattern.compile
                        ("<img.*src\\s*=\\s*(.*?)[^>]*?>", Pattern.CASE_INSENSITIVE);
                m_image = p_image.matcher(content);
                while (m_image.find()) {
                    boolean exitFlag = false;
                    // 得到<img />数据
                    img = m_image.group();
                    // 匹配<img>中的src数据
                    Matcher m = Pattern.compile("src\\s*=\\s*\"?(.*?)(\"|>|\\s+)").matcher(img);
                    while (m.find()) {
                        String imagelink = m.group(1);
                        if (!AppUtil.isEmpty(imagelink) && imagelink.contains("/mongo/")) {
                            imagelink = imagelink.substring(imagelink.lastIndexOf("/") + 1);
                        }
                        eo.setImageLink(imagelink);
                        exitFlag = true;
                        break;//退出当前循环

                    }
                    if (exitFlag) {
                        break;
                    }
                }
            }
        }
//        }
        return eo;
    }

    @Override
    public String uploadFile(MultipartFile[] files, String rootPath) {
        List<AttachmentEO> list = uploadService.uploadFile(files, SystemCodes.contentMgr, rootPath, null);
        if (list != null && list.size() > 0) {
            return list.get(0).getFilePath();
        }
        return null;
    }

    @Override
    public void changePublish(ContentPageVO pageVO) {
        baseContentDao.changePublish(pageVO);
    }

    @Override
    public Long getCountByColumnId(Long columnId) {
        return baseContentDao.getCountByColumnId(columnId);
    }

    @Override
    public Long getCountBySiteId(Long siteId) {
        return baseContentDao.getCountBySiteId(siteId);
    }

    @Override
    public void changeTopStatus(Long[] ids, Integer status) {
        baseContentDao.changeTopStatus(ids, status);
    }

    @Override
    public void changeHotStatus(Long[] ids, Integer status) {
        baseContentDao.changeHotStatus(ids, status);
    }

    @Override
    public int changeImg(Long id, String imgPath) {
        int ret = baseContentDao.changeImg(id, imgPath);
        return ret;
    }

    @Override
    public String saveCopy(final String[] columnIds, Long contentId, BaseContentEO baseEO, String modelCode, String synColumnIsPublishs,Integer isColumnOpt) {
        String returnStr = "";
        BaseContentEO oldEO = null;
        if (null != contentId) {
            oldEO = CacheHandler.getEntity(BaseContentEO.class, contentId);
        } else if (baseEO != null) {
            oldEO = baseEO;
            contentId = baseEO.getId();
        } else {
            throw new BaseRunTimeException("同步数据不正确");
        }
        boolean isPublish = false;
        String[] isPublishs = null;
        if (StringUtils.isNotEmpty(synColumnIsPublishs)) {
            isPublish = true;
            isPublishs = synColumnIsPublishs.split(",");
        }
        boolean exitFlag = false;
        // 循环处理
        for (int i = 0; i < columnIds.length; i++) {
            String[] columnIdSiteId = columnIds[i].split("_");
            final Long columnId = Long.valueOf(columnIdSiteId[0]);
            final Long siteId = Long.valueOf(columnIdSiteId[1]);
            ModelTemplateEO mt = ModelConfigUtil.getTemplateByColumnId(columnId, siteId);
            if (null == mt) {
                throw new BaseRunTimeException(TipsMode.Message.toString(), "复制栏目内容模型没有配置.");
            }
            String columnType = mt.getModelTypeCode();
            BaseContentEO newEO = null;
            try {
                newEO = (BaseContentEO) BeanUtils.cloneBean(oldEO);
                if ("videoNews".equals(columnType)) {
                    newEO.setTypeCode(BaseContentEO.TypeCode.videoNews.toString());
                    newEO.setRemarks(oldEO.getRemarks());
                } else if ("pictureNews".equals(columnType)) {
                    BaseContentEO tempEO = (BaseContentEO) BeanUtils.cloneBean(oldEO);
                    tempEO.setTypeCode(columnType);
                    getImageLinkFromContent(tempEO);//缩略图为空时取内容里第一张图片作为缩略图
                    if (AppUtil.isEmpty(tempEO.getImageLink())) {//缩略图为空
                        exitFlag = true;
                    }
                    newEO.setTypeCode(BaseContentEO.TypeCode.pictureNews.toString());// 设置为内容协同
                } else {
                    newEO.setTypeCode(BaseContentEO.TypeCode.articleNews.toString());// 设置为内容协同
                }
            } catch (Throwable e) {
            }// 复制对象;

            //不能放在try里面
            if (exitFlag) {
                String msg = "文章中没有任何图片，不能复制到" + ColumnUtil.getColumnName(columnId, siteId) + "栏目.";
                throw new BaseRunTimeException(TipsMode.Message.toString(), msg);
            }

            newEO.setSiteId(siteId);
            newEO.setColumnId(columnId);
            newEO.setContentPath("");
            String publishStatus = "0";
            if (isPublishs != null && !AppUtil.isEmpty(isPublishs[i])) {
                publishStatus = isPublishs[i];
            }
            if ("1".equals(publishStatus)) {//发布
                newEO.setIsPublish(2);//设置为“发布中”中间状态
            } else {
                newEO.setIsPublish(0);
            }
            newEO.setQuoteStatus(1);

            // 构建文件复制回调函数
            FileOprUtil.Callback<BaseContentEO> callback = new FileOprUtil.Callback<BaseContentEO>(newEO) {

                @Override
                public void execute(String fileId, BaseContentEO newEO) {
                    // 保存文章
                    newEO.setId(null);
                    newEO.setImageLink(fileId);
                    //将创建人设置为当前用户
                    newEO.setCreateUserId(LoginPersonUtil.getUserId());
                    newEO.setCreateOrganId(LoginPersonUtil.getOrganId());
                    Date publishDate = newEO.getPublishDate();
                    if (publishDate == null) {
                        publishDate = new Date();
                    }
                    Long sort = publishDate.getTime() / 1000;
                    newEO.setNum(sort);
                    baseContentDao.save(newEO);
                    //存储缓存 不然生成静态出问题
                    CacheHandler.saveOrUpdate(BaseContentEO.class, newEO);
                    // 生成静态
//                    if (newEO.getIsPublish() == 1) {// 发布状态
//                        Long contentId = newEO.getId();
//                        if(contentId!=null&&contentId>0L){
//                            MessageSenderUtil.publishContent(
//                                    new MessageStaticEO(siteId, columnId, new Long[]{contentId}).setType(MessageEnum.PUBLISH.value()), 1);
//                        }
//                    }
                }
            };
            if ("articleNews".equals(columnType)) {// 文章新闻处理
                FileOprUtil.copyFile(oldEO.getImageLink(), FileCenterEO.Type.Image.toString(), callback);
                CacheHandler.saveOrUpdate(BaseContentEO.class, newEO);
                FileOprUtil.copyContentFile(oldEO.getId(), newEO.getId(), newEO);
            } else if ("pictureNews".equals(columnType)) {// 图片新闻处理
                FileOprUtil.copyFile(oldEO.getImageLink(), FileCenterEO.Type.Image.toString(), callback);
                CacheHandler.saveOrUpdate(BaseContentEO.class, newEO);
                FileOprUtil.copyContentFile(oldEO.getId(), newEO.getId(), newEO);
                List<ContentPicEO> picList = contentPicService.getPicsList(contentId);
                for (ContentPicEO pic : picList) {
                    ContentPicEO picEO = null;
                    try {
                        picEO = (ContentPicEO) BeanUtils.cloneBean(pic);
                    } catch (Throwable e) {
                    }
                    picEO.setPicId(null);
                    picEO.setContentId(newEO.getId());
                    picEO.setColumnId(columnId);
                    picEO.setSiteId(siteId);
                    contentPicService.saveEntity(picEO);// 保存图片
                }
            } else if ("videoNews".equals(columnType)) {// 视频新闻
                FileOprUtil.copyFile(oldEO.getImageLink(), FileCenterEO.Type.Image.toString(), callback);
                VideoNewsVO oldVideoVO = videoService.getVideoEO(contentId, AMockEntity.RecordStatus.Normal.toString());
                CacheHandler.saveOrUpdate(BaseContentEO.class, newEO);
                FileOprUtil.copyContentFile(oldEO.getId(), newEO.getId(), newEO);
                VideoNewsEO newsVideoEO = new VideoNewsEO();
                newsVideoEO.setContentId(newEO.getId());
                newsVideoEO.setColumnId(columnId);
                newsVideoEO.setSiteId(siteId);
                String newPath =
                        FileOprUtil.copyFile(oldVideoVO.getVideoPath(), FileCenterEO.Type.Video.toString(), new FileOprUtil.Callback<BaseContentEO>(newEO));
                newsVideoEO.setVideoPath(newPath);
                newsVideoEO.setImageName(oldVideoVO.getImageName());
                newsVideoEO.setVideoName(oldVideoVO.getVideoName());
                newsVideoEO.setStatus(100);
                newsVideoEO.setCreateDate(new Date());
                videoService.saveEntity(newsVideoEO);
            } else {
                return "false";
            }
            ContentReferRelationEO relationEO = new ContentReferRelationEO();
            relationEO.setCauseById(contentId);
            relationEO.setCauseByColumnId(oldEO.getColumnId());
            relationEO.setModelCode(modelCode);
            relationEO.setColumnId(columnId);
            relationEO.setReferId(newEO.getId());
            relationEO.setType(ContentReferRelationEO.TYPE.COPY.toString());
            relationEO.setIsColumnOpt(isColumnOpt==null?0:isColumnOpt);

            String optType = "复制到栏目";
            // 生成静态
            if ("1".equals(publishStatus)) {// 发布状态
                returnStr += siteId + "_" + columnId + "_" + newEO.getId() + ",";
                optType += "并发布";
            }

            try {
                String newsType = getTypeCodeName(columnType);
                String columnStr ;
                if("主动公开".equals(newsType)){
                    OrganEO organ = CacheHandler.getEntity(OrganEO.class,oldEO.getColumnId());
                    PublicContentVO publicContentVO = publicContentService.getPublicContentByBaseContentId(oldEO.getId());
                    String columnName = organ.getName()+" > "+PublicCatalogUtil.getCatalogPath(oldEO.getColumnId(), publicContentVO.getCatId());
                    columnStr = "目录（"+columnName+"）";
                    relationEO.setCauseByCatId(publicContentVO.getCatId());//设置源目录id
                }else{
                    columnStr = "栏目（" + ColumnUtil.getColumnName(oldEO.getColumnId(), oldEO.getSiteId())+"）";
                }

                SysLog.log("复制"+newsType+" ："+columnStr+"，标题（"+oldEO.getTitle()+"），"+optType+"：" + ColumnUtil.getColumnName(newEO.getColumnId(),
                        newEO.getSiteId()), "BaseContentEO", CmsLogEO.Operation.Update.toString());
            }catch (Exception e){
                e.printStackTrace();
            }

            //保存引用关系
            contentReferRelationService.saveEntity(relationEO);


        }
        // 标记复制引用
        baseContentDao.markQuote(contentId, 2);

        if (!AppUtil.isEmpty(returnStr)) {//去除最后的逗号
            returnStr = returnStr.substring(0, returnStr.length() - 1);
        }

        return returnStr;
    }

    @Override
    public void changeTitltStatus(Long[] ids, Integer status) {
        /*
         * if(status==1){ Map<String ,Object> map = new HashMap<String,
         * Object>();
         * map.put("recordStatus",AMockEntity.RecordStatus.Normal.toString());
         * map.put("isTitle",1); List<BaseContentEO> list =
         * baseContentDao.getEntities(BaseContentEO.class,map); Long[] list1=
         * new Long[list.size()]; int i=0; if(null!=list&&list.size()>0){
         * for(BaseContentEO bc:list){ list1[i]=bc.getId(); i++; }
         * baseContentDao.changeTitltStatus(list1, 0); } }
         */
        baseContentDao.changeTitltStatus(ids, status);
    }

    @Override
    public void changeNewStatus(Long[] ids, Integer status) {
        baseContentDao.changeNewStatus(ids, status);
    }

    @Override
    public SortVO getMaxNumByColumn(Long columnId) {
        return baseContentDao.getMaxNumByColumn(columnId);
    }

    @Override
    public SortVO getMaxNumBySite(Long siteId) {
        return baseContentDao.getMaxNumBySite(siteId);
    }

    @Override
    public SortVO getNextSort(String opr, Long sortNum, ContentPageVO pageVO) {
        return baseContentDao.getNextSort(opr, sortNum, pageVO).get(0);
    }

    @Override
    public Long contentSort(String opr, Long oprId, Long oprSort, ContentPageVO pageVO) {
        BaseContentEO eo = baseContentDao.getEntity(BaseContentEO.class, oprId);
        BaseContentVO vo = this.getLastNextVO(eo.getColumnId(), eo.getSiteId(), eo.getTypeCode(), oprId, true);
        Long changeId = null;
        if ("up".equals(opr)) {// 置顶
            changeId = vo.getLastId();
            if (changeId.equals(0L)) {
                throw new BaseRunTimeException(TipsMode.Message.toString(), "数据已置顶");
            }
        } else {
            changeId = vo.getNextId();
            if (changeId.equals(0L)) {
                throw new BaseRunTimeException(TipsMode.Message.toString(), "数据已置底");
            }
        }
        BaseContentEO lastEO = baseContentDao.getEntity(BaseContentEO.class, changeId);
        baseContentDao.setNum(changeId, oprSort);
        baseContentDao.setNum(oprId, lastEO.getNum());
        return changeId;
    }

    @Override
    public Pagination getPageAndContent(ContentPageVO pageVO) {
        Pagination page = baseContentDao.getPage(pageVO);
        List<?> list = page.getData();
        if (list.size() > 0 && null != list) {
            for (Object l : list) {
                BaseContentEO _beo = (BaseContentEO) l;
                Criteria criteria = Criteria.where("_id").is(_beo.getId());
                Query query = new Query(criteria);
                ContentMongoEO _eo = contentMongoService.queryOne(query);
                if (_eo != null)
                    _beo.setArticle(_eo.getContent());
            }
        }
        page.setData(list);
        return page;
    }

    @Override
    public Long saveArticleNews(BaseContentEO contentEO, String content, Long[] synColumnIds, Long synMsgCatIds, String publicSynOrganCatIds,
                                String publicSynOrganCatNames) {
        Long id = contentEO.getId();

        //获取时间戳充当排序号，修改新闻后
        Date publishDate = contentEO.getPublishDate();
        if (publishDate == null) {
            publishDate = new Date();
        }
        Long sort = publishDate.getTime() / 1000;

        if (AppUtil.isEmpty(contentEO.getId())) {
            if ((synColumnIds != null && synColumnIds.length > 0) || !AppUtil.isEmpty(publicSynOrganCatIds)) {
                contentEO.setQuoteStatus(2);
            }
            //由于某些栏目页需求调用其他几个栏目的数据，所以这里的排序需要按照站点来
//            SortVO _svo = baseContentDao.getMaxNumBySite(contentEO.getSiteId());
//            Long sort = 1L;
//            if (!AppUtil.isEmpty(_svo.getSortNum()))
//                sort = _svo.getSortNum() + 1L;
            contentEO.setNum(sort);

            if (!AppUtil.isEmpty(contentEO.getEditor()) && "导入".equals(contentEO.getEditor())) {//导数据时不做处理
                id = saveEntity(contentEO);
            } else {
                if (contentEO.getIsPublish() == 1) {
                    contentEO.setIsPublish(2);//设置为中间状态“发布中”
                }
                id = saveEntity(contentEO);
                FileUploadUtil.setStatus(contentEO.getImageLink(), 1, id, contentEO.getColumnId(), contentEO.getSiteId());
            }

            if (BaseContentEO.TypeCode.articleNews.toString().equals(contentEO.getTypeCode()) || BaseContentEO.TypeCode.handleItems.toString().equals(contentEO.getTypeCode())) {// 保存内容
                ContentMongoEO _eo = new ContentMongoEO();
                _eo.setId(id);
                _eo.setContent(content);
                contentMongoService.save(_eo);
            }


            SysLog.log("新增文章新闻 ：栏目（" + ColumnUtil.getColumnName(contentEO.getColumnId(), contentEO.getSiteId()) + "），标题（" + contentEO.getTitle()+"）", "BaseContentEO",
                    CmsLogEO.Operation.Add.toString());
        } else {// 修改
            if (BaseContentEO.TypeCode.articleNews.toString().equals(contentEO.getTypeCode()) || BaseContentEO.TypeCode.handleItems.toString().equals(contentEO.getTypeCode())) {
                ContentMongoEO _eo = new ContentMongoEO();
                _eo.setId(contentEO.getId());
                _eo.setContent(content);
                contentMongoService.save(_eo);
            }

            BaseContentEO newEO = baseContentDao.getEntity(BaseContentEO.class, contentEO.getId());
            if (newEO != null) {
                contentEO.setHit(newEO.getHit());
            }

            if (newEO.getPublishDate() == null || publishDate.getTime() != newEO.getPublishDate().getTime()) {//修改新闻时若发布时间发生变化，则重新生成排序号
                contentEO.setNum(sort);
            }

            if (contentEO.getIsPublish() == 1) {
                contentEO.setIsPublish(2);//设置为中间状态“发布中”
            } else {
                if (newEO.getIsPublish() == 1) {//取消发布的也需要将发布状态设置为中间状态“发布中”
                    contentEO.setIsPublish(2);//设置为中间状态“发布中”
                }
            }

            baseContentDao.merge(contentEO);
            if (contentEO.getQuoteStatus() == 0) {// 没有被引用
                FileUploadUtil.markByContentIds(new Long[]{id}, 0);
            } else {
                List<ContentReferRelationEO> referList =
                        contentReferRelationService.getByCauseId(id, ContentReferRelationEO.ModelCode.CONTENT.toString(),
                                ContentReferRelationEO.TYPE.REFER.toString());
                for (ContentReferRelationEO rl : referList) {
                    BaseContentEO eo = getEntity(BaseContentEO.class, rl.getReferId());
                    if (null != eo) {// 为空表示，那边引用关系已经删除
                        eo.setTitle(contentEO.getTitle());
                        updateEntity(eo);
                        CacheHandler.saveOrUpdate(BaseContentEO.class, eo);
                    }
                }
            }
            FileUploadUtil.setStatus(contentEO.getImageLink(), 1, id, contentEO.getColumnId(), contentEO.getSiteId());
            SysLog.log("修改文章新闻 ：栏目（" + ColumnUtil.getColumnName(contentEO.getColumnId(), contentEO.getSiteId()) + "），标题（" + contentEO.getTitle()+"）", "BaseContentEO",
                    CmsLogEO.Operation.Update.toString());
        }
        CacheHandler.saveOrUpdate(BaseContentEO.class, contentEO);
        return id;
    }


    @Override
    public String synCloumnInfos(Long columnId, CopyReferVO copyReferVO) {
        String returnStr = "";
        if (columnId == null) {
            throw new BaseRunTimeException(TipsMode.Message.toString(), "columnId不能为空");
        }
        //栏目配置
        ColumnConfigEO columnConfigEO = CacheHandler.getEntity(ColumnConfigEO.class, CacheGroup.CMS_INDICATORID, columnId);
        String synColumnIds1 = columnConfigEO.getSynColumnIds();

        if (!AppUtil.isEmpty(synColumnIds1)) {//栏目配置里面的同步到
            Long[] columnIds = cn.lonsun.core.base.util.StringUtils.getArrayWithLong(synColumnIds1, ",");
            String synColumnIds = "";
            String synColumnIsPublishs = "";
            if (copyReferVO != null && !AppUtil.isEmpty(copyReferVO.getSynColumnIds())) {
                synColumnIds = copyReferVO.getSynColumnIds();
                synColumnIsPublishs = copyReferVO.getSynColumnIsPublishs();
            }

            for (int i = 0; i < columnIds.length; i++) {
                if (!synColumnIds.contains(columnIds[i].toString())) {
                    if (AppUtil.isEmpty(synColumnIds)) {
                        synColumnIds = columnIds[i].toString() + "_" + LoginPersonUtil.getSiteId().toString();
                        synColumnIsPublishs = "1";//默认发布
                    } else {
                        synColumnIds = synColumnIds + "," + columnIds[i].toString() + "_" + LoginPersonUtil.getSiteId().toString();
                        synColumnIsPublishs = synColumnIsPublishs + ",1";//默认发布
                    }
                }
            }
            copyReferVO.setSynColumnIds(synColumnIds);
            copyReferVO.setSynColumnIsPublishs(synColumnIsPublishs);
        }

        //同步数据
        if (copyReferVO != null) {
            copyReferVO.setSource(1l);
            copyReferVO.setType(1l);
            if (!AppUtil.isEmpty(copyReferVO.getContentId())) {
                returnStr = copyRefer(copyReferVO);
            } else {
                throw new BaseRunTimeException(TipsMode.Message.toString(), "contentId不能为空");
            }
        }
        return returnStr;
    }


    /**
     * 同步修改引用新闻信息
     * @param contentEO
     * @param isPublish
     * @return
     */
    @Override
    public String synEditReferNews(BaseContentEO contentEO,String content, Integer isPublish) {
        String returnStr = "";
        String publicReturnStr = "";
        if(AppUtil.isEmpty(contentEO)||AppUtil.isEmpty(contentEO.getId())){
            return returnStr;
        }
        List<ContentReferRelationEO> list = contentReferRelationService.getByCauseId(contentEO.getId(), null, ContentReferRelationEO.TYPE.REFER.toString());
        if (list != null && list.size() > 0) {
            for(ContentReferRelationEO referRelationEO:list){
                BaseContentEO eo = getEntity(BaseContentEO.class, referRelationEO.getReferId());
                int status = eo.getIsPublish();//记录原来的发布状态，后边判断是否需要重新生成
                cloneBaseContentEO(contentEO,eo);
                this.updateEntity(eo);

                //修改内容信息
                ContentMongoEO mongoEO = contentMongoService.queryById(eo.getId());
                if(mongoEO!=null){
                    mongoEO.setContent(content);
                    contentMongoService.save(mongoEO);
                }

                if(BaseContentEO.TypeCode.public_content.toString().equals(eo.getTypeCode())){//修改主动公开实体
                    PublicContentVO publicContentVO = publicContentService.getPublicContentByBaseContentId(eo.getId());
                    if(publicContentVO!=null){
                        publicCatalogCountService.updateOrganCatIdCountByStatus(publicContentVO.getOrganId(), publicContentVO.getCatId(), 1L, isPublish, true);
                    }

                    // 生成静态
                    if (isPublish!=null&&isPublish.intValue()==1) {// 发布状态全部重新生成
                        publicReturnStr += eo.getSiteId() + "_" + eo.getColumnId() + "_" + eo.getId() + ",";
                    }else{
                        if (isPublish!=null&&isPublish.intValue()==0&&status==1) {//需要将该新闻取消发布
                            publicReturnStr += eo.getSiteId() + "_" + eo.getColumnId() + "_" + eo.getId() + ",";
                        }
                    }

                }else{
                    // 生成静态
                    if (isPublish!=null&&isPublish.intValue()==1) {// 发布状态全部重新生成
                        returnStr += eo.getSiteId() + "_" + eo.getColumnId() + "_" + eo.getId() + ",";
                    }else{
                        if (isPublish!=null&&isPublish.intValue()==0&&status==1) {//需要将该新闻取消发布
                            returnStr += eo.getSiteId() + "_" + eo.getColumnId() + "_" + eo.getId() + ",";
                        }
                    }
                }


            }
        }
        if (!AppUtil.isEmpty(returnStr)) {//去除最后的逗号
            returnStr = returnStr.substring(0, returnStr.length() - 1);
        }
        if (!AppUtil.isEmpty(publicReturnStr)) {
            returnStr += "&" + publicReturnStr.substring(0, publicReturnStr.length() - 1);
        }

        return returnStr;
    }


    /**
     * 同步修改被引用新闻信息
     * @param oldEO
     * @param newEO
     */
    private void cloneBaseContentEO(BaseContentEO oldEO,BaseContentEO newEO){
        newEO.setTitle(oldEO.getTitle());
        newEO.setTitleColor(oldEO.getTitleColor());
        newEO.setSubTitle(oldEO.getSubTitle());
        newEO.setIsBold(oldEO.getIsBold());
        newEO.setIsUnderline(oldEO.getIsUnderline());
        newEO.setIsTitle(oldEO.getIsTitle());
        newEO.setIsNew(oldEO.getIsNew());
        newEO.setIsTilt(oldEO.getIsTilt());
        newEO.setNum(oldEO.getNum());
        newEO.setResources(oldEO.getResources());
        newEO.setIsHot(oldEO.getIsHot());
        newEO.setIsTop(oldEO.getIsTop());
        newEO.setRedirectLink(oldEO.getRedirectLink());
        newEO.setImageLink(oldEO.getImageLink());
        newEO.setIsPublish(oldEO.getIsPublish());
        newEO.setPublishDate(oldEO.getPublishDate());
        newEO.setHit(oldEO.getHit());
        newEO.setRemarks(oldEO.getRemarks());
        newEO.setAuthor(oldEO.getAuthor());
        newEO.setEditor(oldEO.getEditor());
        newEO.setResponsibilityEditor(oldEO.getResponsibilityEditor());
        newEO.setAttachSavedName(oldEO.getAttachSavedName());
        newEO.setAttachRealName(oldEO.getAttachRealName());
        newEO.setAttachSize(oldEO.getAttachSize());
    }


    @Override
    public String delContent(Long[] ids) {
        String returnStr = "";
        String publicReturnStr = "";
        Map<String, Object> paramsMap = new HashMap<String, Object>();
        paramsMap.put("id", ids);
        List<BaseContentEO> _eos = baseContentDao.getEntities(BaseContentEO.class, paramsMap);

        // 去掉文件引用状态删除
        List<Long> noQuoteId = new ArrayList<Long>();
        for (BaseContentEO _eo : _eos) {
            if (_eo.getQuoteStatus() != 0) {
                noQuoteId.add(_eo.getId());
            }
            if(BaseContentEO.TypeCode.public_content.toString().equals(_eo.getTypeCode())){//删除主动公开实体
                PublicContentVO publicContentVO = publicContentService.getPublicContentByBaseContentId(_eo.getId());
                if(publicContentVO!=null){
                    publicContentService.delete(PublicContentEO.class,publicContentVO.getId());
                    if (_eo.getIsPublish().intValue() == 1) {// 发布状态
                        publicCatalogCountService.updateOrganCatIdCountByStatus(publicContentVO.getOrganId(), publicContentVO.getCatId(), -1L, 1, false);
                    } else {// 编辑，保存
                        publicCatalogCountService.updateOrganCatIdCountByStatus(publicContentVO.getOrganId(), publicContentVO.getCatId(), -1L, 0, false);
                    }
                }
                publicReturnStr += _eo.getSiteId() + "_" + _eo.getColumnId() + "_" + _eo.getId() + ",";
            }else{
                if ("1".equals(_eo.getIsPublish())) {// 发布状态
                    returnStr += _eo.getSiteId() + "_" + _eo.getColumnId() + "_" + _eo.getId() + ",";
                }
            }
        }
        if (noQuoteId.size() > 0 && noQuoteId != null) {
            int size = noQuoteId.size();
            Long[] fileContentId = noQuoteId.toArray(new Long[size]);
            FileUploadUtil.markByContentIds(fileContentId, 0);
        }
        contentReferRelationService.delteByReferId(ids);// 删除引用关系
        commentService.deleteByContent(ids);
        baseContentDao.delete(BaseContentEO.class, ids);
        CacheHandler.delete(BaseContentEO.class, _eos);

        if (!AppUtil.isEmpty(returnStr)) {//去除最后的逗号
            returnStr = returnStr.substring(0, returnStr.length() - 1);
        }

        if (!AppUtil.isEmpty(publicReturnStr)) {//去除最后的逗号
            publicReturnStr = publicReturnStr.substring(0, publicReturnStr.length() - 1);
        }

        if (!AppUtil.isEmpty(publicReturnStr)) {
            returnStr += "&" + publicReturnStr;
        }
        return returnStr;
    }

    @Override
    public String publishs(Long[] ids,Integer isPublish) {
        String returnStr = "";
        String publicReturnStr = "";
        Map<String, Object> paramsMap = new HashMap<String, Object>();
        paramsMap.put("id", ids);
        List<BaseContentEO> _eos = baseContentDao.getEntities(BaseContentEO.class, paramsMap);

        for (BaseContentEO _eo : _eos) {
            Integer oldIsPublish = _eo.getIsPublish();

            if(BaseContentEO.TypeCode.public_content.toString().equals(_eo.getTypeCode())){//主动公开实体
                PublicContentVO publicContentVO = publicContentService.getPublicContentByBaseContentId(_eo.getId());
                if(publicContentVO!=null){
                    if (!isPublish.equals(oldIsPublish)) {//状态不相等才更改
                        _eo.setIsPublish(2);//改成发布中
                        publicCatalogCountService.updateOrganCatIdCountByStatus(publicContentVO.getOrganId(), publicContentVO.getCatId(), 1L, isPublish, true);
                        publicReturnStr += _eo.getSiteId() + "_" + _eo.getColumnId() + "_" + _eo.getId() + ",";
                    }
                }
            }else{
                if (!isPublish.equals(oldIsPublish)) {//状态不相等才发布
                    _eo.setIsPublish(2);//改成发布中
                    returnStr += _eo.getSiteId() + "_" + _eo.getColumnId() + "_" + _eo.getId() + ",";
                }
            }
        }

        if (!AppUtil.isEmpty(returnStr)) {//去除最后的逗号
            returnStr = returnStr.substring(0, returnStr.length() - 1);
        }

        if (!AppUtil.isEmpty(publicReturnStr)) {//去除最后的逗号
            publicReturnStr = publicReturnStr.substring(0, publicReturnStr.length() - 1);
        }

        if (!AppUtil.isEmpty(publicReturnStr)) {
            returnStr += "&" + publicReturnStr;
        }
        return returnStr;
    }

    @Override
    public Long setHit(Long id) {
        baseContentDao.setHit(id);
        Long hit = getEntity(BaseContentEO.class, id).getHit();
        return hit;
    }

    @Override
    public Map<String, BaseContentEO> getPageLink(Long id) {
        BaseContentEO _eo = getEntity(BaseContentEO.class, id);
        Map<String, BaseContentEO> map = new HashMap<String, BaseContentEO>();
        List<BaseContentEO> Before = baseContentDao.getPageLink(_eo.getColumnId(), _eo.getNum(), "Before");
        if (AppUtil.isEmpty(Before)) {
            map.put("Before", null);
        } else {
            map.put("Before", Before.get(0));
        }
        List<BaseContentEO> After = baseContentDao.getPageLink(_eo.getColumnId(), _eo.getNum(), "After");
        if (AppUtil.isEmpty(Before)) {
            map.put("After", null);
        } else {
            map.put("After", After.get(0));
        }
        return map;
    }

    @Override
    public Pagination getUnAuditContents(UnAuditContentsVO contentVO) {
        return baseContentDao.getUnAuditContents(contentVO);
    }

    @Override
    public List<ColumnTypeVO> getUnAuditColumnIds(UnAuditContentsVO contentVO) {
        return baseContentDao.getUnAuditColumnIds(contentVO);
    }

    @Override
    public Pagination getPageBySortNum(QueryVO query) {
        return baseContentDao.getPageBySortNum(query);
    }

    @Override
    public void updateSort(SortUpdateVO sortVo) {
        BaseContentEO content = baseContentDao.getEntity(BaseContentEO.class, sortVo.getId());
        BaseContentEO content1 = baseContentDao.getSort(sortVo);
        if (content != null && content1 != null) {
            Long sortN = content.getNum();
            content.setNum(content1.getNum());
            content1.setNum(sortN);
            baseContentDao.update(content);
            baseContentDao.update(content1);
        }
    }

    @Override
    public Long getMaxSortNum(Long siteId, Long columnId, String typeCode) {
        return baseContentDao.getMaxSortNum(siteId, columnId, typeCode);
    }

    @Override
    public Pagination getOpenCommentContent(ContentPageVO pageVO) {
        if (AppUtil.isEmpty(pageVO.getSiteId()))
            pageVO.setSiteId(LoginPersonUtil.getSiteId());
        return baseContentDao.getOpenCommentContent(pageVO);
    }

    @Override
    public BaseContentEO getRemoved(Long id) {
        return baseContentDao.getRemoved(id);
    }

    @Override
    public Pagination getRecycleContentPage(UnAuditContentsVO contentVO) {
        return baseContentDao.getRecycleContentPage(contentVO);
    }

    @Override
    public Long noAuditCount(Long siteId, String typeCode, List<Long> columnIds) {
        return baseContentDao.noAuditCount(siteId, typeCode, columnIds);
    }

    @Override
    public BaseContentVO getLastNextVO(Long columnId, Long siteId, String typeCode, Long contentId, boolean allOrPublish) {
        return baseContentDao.getLastNextVO(columnId, siteId, typeCode, contentId, allOrPublish);
    }

    @Override
    public void recovery(Long[] ids) {
        baseContentDao.recovery(ids);
        List<BaseContentEO> list = baseContentDao.getEntities(BaseContentEO.class, ids);
        int isp = 0;
        if (null != list && list.size() > 0) {
            for (BaseContentEO bc : list) {
                bc.setRecordStatus(AMockEntity.RecordStatus.Normal.toString());
                CacheHandler.saveOrUpdate(BaseContentEO.class, bc);
                isp = bc.getIsPublish();
                if (isp == 1) {
                    MessageSenderUtil.publishContent(
                            new MessageStaticEO(bc.getSiteId(), bc.getColumnId(), new Long[]{bc.getId()}).setSource(MessageEnum.CONTENTINFO.value())
                                    .setType(MessageEnum.PUBLISH.value()), 1);
                }
            }
        }
        if (null != ids) {
            this.changePublish(new ContentPageVO(null, null, isp, ids, null));
            contentReferRelationService.recoveryByReferIds(ids);
        }
    }

    @Override
    public List<BaseContentEO> getContents(String typeCode, Long siteId) {
        if (siteId == null) {
            siteId = LoginPersonUtil.getSiteId();
        }
        if (AppUtil.isEmpty(typeCode)) {
            typeCode = BaseContentEO.TypeCode.articleNews.toString();
        }
        List<BaseContentEO> list = baseContentDao.getList(new ContentPageVO(siteId, null, null, null, typeCode));
        if (null != list && list.size() > 0) {
            if (BaseContentEO.TypeCode.articleNews.toString().equals(typeCode) || BaseContentEO.TypeCode.pictureNews.toString().equals(typeCode)) {
                for (BaseContentEO li : list) {
                    try {
                        ContentMongoEO contentMongoEO = mongoService.queryById(li.getId());
                        if (null != contentMongoEO) {
                            li.setArticle(contentMongoEO.getContent());
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        return list;
    }

    @Override
    public List<BaseContentEO> getContents(String typeCode) {
        List<BaseContentEO> list = baseContentDao.getList(new ContentPageVO(null, null, null, null, typeCode));
        if (null != list && list.size() > 0) {
            if (BaseContentEO.TypeCode.articleNews.toString().equals(typeCode) || BaseContentEO.TypeCode.pictureNews.toString().equals(typeCode)) {
                List<Long> ids = new ArrayList<Long>();
                for (BaseContentEO li : list) {
                    if (null != li && null != li.getId()) {
                        ids.add(li.getId());
                    }
                    /*
                     * try { ContentMongoEO contentMongoEO =
                     * mongoService.queryById(li.getId()); if (contentMongoEO !=
                     * null) { li.setArticle(contentMongoEO.getContent()); } }
                     * catch (Exception e) { continue; }
                     */
                }

                Long[] idsarr = ids.toArray(new Long[ids.size()]);
                List<ContentMongoEO> mongoEOs = null;
                try {
                    mongoEOs = mongoService.queryListByIds(idsarr);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                Map<Long, String> map = null;
                if (null != mongoEOs) {
                    map = new HashMap<Long, String>();
                    for (ContentMongoEO mongoEO : mongoEOs) {
                        if (null != mongoEO.getContent()) {
                            map.put(mongoEO.getId(), mongoEO.getContent());
                        }
                    }
                }

                if (null != map) {
                    for (BaseContentEO li : list) {
                        String content = map.get(li.getId());
                        if (null != content) {
                            li.setArticle(content);
                        }
                    }
                }
            }
        }
        return list;
    }

    /**
     * 物理删除文章新闻
     */
    @Override
    public void removeArticleNews(Long[] ids) {

        // baseContentDao.remove(BaseContentEO.class, ids);
        removeBaseContent(ids);
        for (Long id : ids) {
            mongoService.deleteById(id);
        }
    }

    @Override
    public void removePictrueNews(Long[] ids) {
        List<BaseContentEO> _eos = baseContentDao.getContentsByIds(ids);
        List<Long> noQuoteId = new ArrayList<Long>();
        if (_eos != null) {
            for (BaseContentEO _eo : _eos) {
                if (_eo != null) {
                    if (_eo.getQuoteStatus() == 0) {
                        noQuoteId.add(_eo.getId());
                    }
                }
            }
            if (noQuoteId.size() > 0 && noQuoteId != null) {
                int size = noQuoteId.size();
                Long[] fileContentId = noQuoteId.toArray(new Long[size]);
                FileUploadUtil.markByContentIds(fileContentId, 0);
            }
            for (Long id : ids) {
                mongoService.deleteById(id);
            }
            removeBaseContent(ids);
            contentPicService.removePic(ids);
        }
    }

    @Override
    public void removeVideoNews(Long[] ids) {
        videoService.removeVideos(ids);
        for (int i = 0; i < ids.length; i++) {
            CacheHandler.delete(BaseContentEO.class, CacheHandler.getEntity(BaseContentEO.class, ids[i]));
        }
    }

    /**
     * 物理删除文章新闻
     */
    @Override
    public void removeBaseContent(Long[] ids) {
        Map<String, Object> paramsMap = new HashMap<String, Object>();
        paramsMap.put("id", ids);
        List<BaseContentEO> _eos = baseContentDao.getEntities(BaseContentEO.class, paramsMap);
        baseContentDao.remove(BaseContentEO.class, ids);
        List<Long> noQuoteId = new ArrayList<Long>();
        List<String> listImg = new ArrayList<String>();
        if (null != _eos && _eos.size() > 0) {
            for (BaseContentEO _eo : _eos) {
                if (_eo.getQuoteStatus() == 1) {
                    noQuoteId.add(_eo.getId());
                }
                if (!AppUtil.isEmpty(_eo.getImageLink())) {
                    listImg.add(_eo.getImageLink());
                }
            }
        }

        Map<String, Object> mapImg = new HashMap<String, Object>();
        if (null != listImg && listImg.size() > 0) {
            mapImg.put("mongoId", listImg);
            List<FileCenterEO> listFC = fileCenterService.getEntities(FileCenterEO.class, mapImg);
            if (null != listFC && listFC.size() > 0) {
                for (FileCenterEO file : listFC) {
                    file.setStatus(0);
                    fileCenterService.updateEntity(file);
                }
            }
        }

        if (noQuoteId.size() > 0 && noQuoteId != null) {
            int size = noQuoteId.size();
            Long[] fileContentId = noQuoteId.toArray(new Long[size]);
            FileUploadUtil.markByContentIds(fileContentId, 0);
        }
        contentReferRelationService.delteByReferId(ids);

        for (Long id : ids) {
            // 被引用体链接
            List<ContentReferRelationEO> cas = contentReferRelationService.getByCauseId(id, null, ContentReferRelationEO.TYPE.REFER.toString());
            for (ContentReferRelationEO c : cas) {
                baseContentDao.remove(BaseContentEO.class, c.getReferId());
                CacheHandler.delete(BaseContentEO.class, CacheHandler.getEntity(BaseContentEO.class, c.getReferId()));
            }
        }
    }

    @Override
    public List<String> getExistTypeCode(Long siteId) {
        return baseContentDao.getExistTypeCode(siteId);
    }

    @Override
    public BaseContentEO getContent(Long id, String status) {
        return baseContentDao.getContent(id, status);
    }

    @Override
    public Pagination getContentForWeChat(String title, String typeCode, Long siteId, Long pageIndex, Integer pageSize) {
        if (siteId == null) {
            return null;
        }
        if (pageIndex == null) {
            pageIndex = 0L;
        }
        if (pageSize == null || pageSize <= 0) {
            pageSize = 9;
        }
        return baseContentDao.getContentForWeChat(title, typeCode, siteId, pageIndex, pageSize);
    }

    @Override
    public List<ContentChartVO> getContentChart(ContentChartQueryVO queryVO) {
        SiteMgrEO siteEO = CacheHandler.getEntity(SiteMgrEO.class, queryVO.getSiteId());
        List<OrganEO> newList = new ArrayList<OrganEO>();
        if (siteEO != null && !StringUtils.isEmpty(siteEO.getUnitIds())) {
            Long[] arr = AppUtil.getLongs(siteEO.getUnitIds(), ",");
            if (arr != null && arr.length > 0) {
                if (!AppUtil.isEmpty(queryVO.getIsOrgan()) && queryVO.getIsOrgan().equals("1")) {// 根据部门统计
                    newList = organService.getOrgansByDn(arr[0], OrganEO.Type.OrganUnit.toString());
                } else {
                    newList = organService.getOrgansByDn(arr[0], OrganEO.Type.Organ.toString());
                }
            }
        } else {
            throw new BaseRunTimeException(TipsMode.Message.toString(), "此站点下没有绑定单位");
        }
        if (newList == null || newList.size() <= 0) {
            return new ArrayList<ContentChartVO>();
        }
        String ids = "";
        for (OrganEO eo : newList) {
            ids += eo.getOrganId() + ",";
        }
        if (StringUtils.isEmpty(ids)) {
            return new ArrayList<ContentChartVO>();
        }
        ids = ids.substring(0, ids.length() - 1);
        List<ContentChartVO> list = baseContentDao.getContentChart(queryVO, ids);

        //按照人员分析
        if (!AppUtil.isEmpty(queryVO.getIsUser()) && queryVO.getIsUser().equals("1")) {
            List<ContentChartVO> list_1 = new ArrayList<ContentChartVO>();
            list_1.addAll(list);
            String unitId = siteEO.getUnitIds();
            List<PersonEO> personEOs = personService.getPersonsByDn(Long.parseLong(unitId));
            for (int i = 0; i < personEOs.size(); i++) {
                //只查询top10数据
                if (list.size() >= 10) {
                    break;
                }

                ContentChartVO vo = new ContentChartVO();
                vo.setOrganName(personEOs.get(i).getName());
                vo.setOrganId(personEOs.get(i).getUserId());
                boolean flag = true;
                for (ContentChartVO contentChartVO : list_1) {
                    //这里的organId里面实际存的是userId的值
                    if (vo.getOrganId().equals(contentChartVO.getOrganId())) {
                        flag = false;
                        //list已存在改用户则跳出循环
                        break;
                    }
                }
                //列表中不存在用户则新增该用户
                if (flag) {
                    list.add(vo);
                }
            }
            //按照栏目分析
        } else if (!AppUtil.isEmpty(queryVO.getIsColumn()) && queryVO.getIsColumn().equals("1")) {
            List<ContentChartVO> list_1 = new ArrayList<ContentChartVO>();
            list_1.addAll(list);
            List<ColumnMgrEO> columnMgrEOs = columnConfigService.getAllColumnTree(LoginPersonUtil.getSiteId(), null);
            for (int i = 0; i < columnMgrEOs.size(); i++) {
                //只查询新闻数据
                if(!columnMgrEOs.get(i).getColumnTypeCode().equals(BaseContentEO.TypeCode.articleNews.toString())){
                    continue;
                }
                //只查询top10数据
                if (list.size() >= 10) {
                    break;
                }

                ContentChartVO vo = new ContentChartVO();
                vo.setOrganName(columnMgrEOs.get(i).getName());
                vo.setOrganId(columnMgrEOs.get(i).getIndicatorId());
                boolean flag = true;
                for (ContentChartVO contentChartVO : list_1) {
                    //这里的organId里面实际存的是columnId的值
                    if (vo.getOrganId().equals(contentChartVO.getOrganId())) {
                        flag = false;
                        //list已存在该栏目则跳出循环
                        break;
                    }
                }
                //列表中不存在栏目则新增该栏目
                if (flag) {
                    list.add(vo);
                }
            }
        } else {
            if (list != null && list.size() >= 10) {
                return list;
            } else {

                if (newList == null || newList.size() == 0) {
                    return list;
                }
                if (list == null) {
                    list = new ArrayList<ContentChartVO>();
                }
                List<ContentChartVO> list_1 = new ArrayList<ContentChartVO>();
                list_1.addAll(list);
                if (list.size() == 0) {
                    for (OrganEO eo : newList) {
                        if (list.size() < 10) {
                            ContentChartVO chartVO = new ContentChartVO();
                            chartVO.setOrganId(eo.getOrganId());
                            chartVO.setOrganName(eo.getName());
                            list.add(chartVO);
                        } else {
                            break;
                        }
                    }
                } else {
                    boolean flag = true;
                    for (OrganEO eo : newList) {
                        flag = true;
                        if (list.size() >= 10) {
                            break;
                        }
                        for (ContentChartVO vo : list_1) {
                            if (eo.getOrganId().equals(vo.getOrganId())) {
                                flag = false;
                                break;
                            }
                        }
                        if (flag) {
                            ContentChartVO chartVO = new ContentChartVO();
                            chartVO.setOrganId(eo.getOrganId());
                            chartVO.setOrganName(eo.getName());
                            list.add(chartVO);
                        }
                    }
                }
            }
            if (list != null && list.size() > 1) {
                for (int i = 1; i < list.size(); i += 2) {
                    list.get(i).setOrganName("\n" + list.get(i).getOrganName());
                }
            }
        }

        return list;
    }

    @Override
    public Long getCountChart(ContentChartQueryVO queryVO) {
        return baseContentDao.getCountChart(queryVO);
    }

    @Override
    public Pagination getWordPage(StatisticsQueryVO queryVO) {
        List<WordListVO> list = getWordList(queryVO);
        Pagination page = new Pagination();
        int index = Integer.parseInt(String.valueOf(queryVO.getPageIndex()));
        int size = queryVO.getPageSize();
        int startRow = index * size;
        int endRow = (index + 1) * size;
        if (list != null) {
            if (list.size() < endRow) {
                endRow = list.size();
            }
        }

        page.setData(list.subList(startRow, endRow));
        page.setTotal(Long.parseLong(String.valueOf(list.size())));
        page.setPageSize(size);
        page.setPageIndex(queryVO.getPageIndex());
        return page;
    }

    @Override
    public Pagination getWordPageByColumn(StatisticsQueryVO queryVO) {
        List<WordListVO> list = getWordListByColumn(queryVO);
        Pagination page = new Pagination();
        int index = Integer.parseInt(String.valueOf(queryVO.getPageIndex()));
        int size = queryVO.getPageSize();
        int startRow = index * size;
        int endRow = (index + 1) * size;
        if (list != null) {
            if (list.size() < endRow) {
                endRow = list.size();
            }
        }

        page.setData(list.subList(startRow, endRow));
        page.setTotal(Long.parseLong(String.valueOf(list.size())));
        page.setPageSize(size);
        page.setPageIndex(queryVO.getPageIndex());
        return page;
    }

    @Override
    public List<WordListVO> getWordList(StatisticsQueryVO queryVO) {
        List<WordListVO> listData = null;
        if (BaseContentEO.TypeCode.public_content.toString().equals(queryVO.getTypeCode())) {
            listData = baseContentDao.getWordList1(queryVO);
        } else {
            listData = baseContentDao.getWordList(queryVO);
        }
        List<WordListVO> newList = new ArrayList<WordListVO>();
        SiteMgrEO siteEO = CacheHandler.getEntity(SiteMgrEO.class, queryVO.getSiteId());
        //按照人员统计
        if (!AppUtil.isEmpty(queryVO.getIsUser()) && queryVO.getIsUser().equals("1")) {
            String unitId = siteEO.getUnitIds();
            List<PersonEO> personEOs = personService.getPersonsByDn(Long.parseLong(unitId));
            for (int i = 0; i < personEOs.size(); i++) {
                WordListVO vo = new WordListVO();
                vo.setOrganName(personEOs.get(i).getName());
                vo.setOrganId(personEOs.get(i).getUserId());
                for (WordListVO wordListVO : listData) {
                    //这里的oeganId里面实际存的是userId的值
                    if (vo.getOrganId().equals(wordListVO.getOrganId())) {
                        vo.setCount(wordListVO.getCount() == null ? 0 : wordListVO.getCount());
                        vo.setPublishCount(wordListVO.getPublishCount() == null ? 0 : wordListVO.getPublishCount());
                        vo.setUnPublishCount(vo.getCount() - vo.getPublishCount());
                        vo.setRate((int) Math.round((100.0 * vo.getPublishCount()) / vo.getCount()));
                        break;
                    }
                }
                newList.add(vo);
                if (!StringUtils.isEmpty(queryVO.getOrganName())) {
                    if (!personEOs.get(i).getName().contains(queryVO.getOrganName())) {
                        newList.remove(vo);
                    }
                }
            }
            //按照栏目统计
        } else if (!AppUtil.isEmpty(queryVO.getIsColumn()) && queryVO.getIsColumn().equals("1")) {
            List<ColumnMgrEO> columnMgrEOs = columnConfigService.getAllColumnTree(LoginPersonUtil.getSiteId(), null);
            for (int i = 0; i < columnMgrEOs.size(); i++) {
                if ((BaseContentEO.TypeCode.articleNews.toString().equals(columnMgrEOs.get(i).getColumnTypeCode())
                        || BaseContentEO.TypeCode.pictureNews.toString().equals(columnMgrEOs.get(i).getColumnTypeCode())) && columnMgrEOs.get(i).getIsParent() == 0) {
                    WordListVO vo = new WordListVO();
                    vo.setOrganName(columnMgrEOs.get(i).getName());
                    vo.setOrganId(columnMgrEOs.get(i).getIndicatorId());
                    for (WordListVO wordListVO : listData) {
                        //这里的oeganId里面实际存的是userId的值
                        if (vo.getOrganId().equals(wordListVO.getOrganId())) {
                            vo.setCount(wordListVO.getCount() == null ? 0 : wordListVO.getCount());
                            vo.setPublishCount(wordListVO.getPublishCount() == null ? 0 : wordListVO.getPublishCount());
                            vo.setUnPublishCount(vo.getCount() - vo.getPublishCount());
                            vo.setRate((int) Math.round((100.0 * vo.getPublishCount()) / vo.getCount()));
                            break;
                        }
                    }
                    newList.add(vo);
                    if (!StringUtils.isEmpty(queryVO.getOrganName())) {
                        if (!columnMgrEOs.get(i).getName().contains(queryVO.getOrganName())) {
                            newList.remove(vo);
                        }
                    }
                }
            }
        } else {//按部门或者单位统计
            List<OrganEO> organList = new ArrayList<OrganEO>();
            if (siteEO != null && !StringUtils.isEmpty(siteEO.getUnitIds())) {
                Long[] arr = AppUtil.getLongs(siteEO.getUnitIds(), ",");
                if (arr != null && arr.length > 0) {
                    if (!AppUtil.isEmpty(queryVO.getIsOrgan()) && queryVO.getIsOrgan().equals("1")) {//根据部门统计
                        organList = organService.getOrgansByDn(arr[0], OrganEO.Type.OrganUnit.toString());// 部门
                    } else {
                        organList = organService.getOrgansByDn(arr[0], OrganEO.Type.Organ.toString());// 单位
                    }
                }
            } else {
                throw new BaseRunTimeException(TipsMode.Message.toString(), "此站点下没有绑定单位");
            }
            if (listData != null && listData.size() >= 0) {

                String organName = queryVO.getOrganName();
                for (int i = 0; i < organList.size(); i++) {
                    WordListVO vo = new WordListVO();
                    vo.setOrganName(organList.get(i).getName());
                    vo.setOrganId(organList.get(i).getOrganId());
                    for (WordListVO wordListVO : listData) {
                        if (vo.getOrganId().equals(wordListVO.getOrganId())) {
                            vo.setCount(wordListVO.getCount() == null ? 0 : wordListVO.getCount());
                            vo.setPublishCount(wordListVO.getPublishCount() == null ? 0 : wordListVO.getPublishCount());
                            vo.setUnPublishCount(vo.getCount() - vo.getPublishCount());
                            vo.setRate((int) Math.round((100.0 * vo.getPublishCount()) / vo.getCount()));
                            break;
                        }
                    }
                    newList.add(vo);
                    if (!StringUtils.isEmpty(organName)) {
                        if (!organList.get(i).getName().contains(organName)) {
                            newList.remove(vo);
                        }
                    }
                }
            }
        }
        return newList;
    }


    @Override
    public List<WordListVO> getWordListByColumn(StatisticsQueryVO queryVO) {
        List<WordListVO> listData = null;

        listData = baseContentDao.getWordListByColumn(queryVO);

        List<WordListVO> newList = new ArrayList<WordListVO>();
        List<ColumnMgrEO> columnList = columnConfigService.getColumnByTypeCode(queryVO.getSiteId(), queryVO.getTypeCode());

        if (listData != null && listData.size() >= 0) {

            String cloumnName = queryVO.getColumnName();
            for (int i = 0; i < columnList.size(); i++) {
                WordListVO vo = new WordListVO();
                vo.setColumnName(columnList.get(i).getName());
                vo.setColumnId(columnList.get(i).getIndicatorId());
                for (WordListVO wordListVO : listData) {
                    if (vo.getColumnId().equals(wordListVO.getColumnId())) {
                        vo.setCount(wordListVO.getCount() == null ? 0 : wordListVO.getCount());
                        vo.setPublishCount(wordListVO.getPublishCount() == null ? 0 : wordListVO.getPublishCount());
                        vo.setUnPublishCount(vo.getCount() - vo.getPublishCount());
                        vo.setRate((int) Math.round((100.0 * vo.getPublishCount()) / vo.getCount()));
                        break;
                    }
                }

                if (!StringUtils.isEmpty(cloumnName)) {
                    if (!columnList.get(i).getName().contains(cloumnName)) {
                        continue;
                    } else if (columnList.get(i).getIsParent() == 1) {//删除父节点
                        continue;
                    }
                }
                newList.add(vo);
            }
        }
        return newList;
    }


    @Override
    public String saveReferNews(String[] columnIds, Long contentId, String modelCode, String synColumnIsPublishs,Integer isColumnOpt) {
        String returnStr = "";
        BaseContentEO oldEO = null;
        BaseContentEO baseEO = null;
        if (null != contentId) {
            List<ContentReferRelationEO> relationEOS = contentReferRelationService.getByReferId(contentId,null,ContentReferRelationEO.TYPE.REFER.toString());
            if(relationEOS!=null&&relationEOS.size()>0){
                contentId = relationEOS.get(0).getCauseById();
            }
            oldEO = CacheHandler.getEntity(BaseContentEO.class, contentId);
        } else {
            throw new BaseRunTimeException("引用数据不正确");
        }
        boolean isPublish = false;
        String[] isPublishs = null;
        if (StringUtils.isNotEmpty(synColumnIsPublishs)) {
            isPublish = true;
            isPublishs = synColumnIsPublishs.split(",");
        }
        boolean exitFlag = false;
        for (int i = 0; i < columnIds.length; i++) {
            String[] columnIdSiteId = columnIds[i].split("_");
            final Long columnId = Long.valueOf(columnIdSiteId[0]);
            final Long siteId = Long.valueOf(columnIdSiteId[1]);
            ModelTemplateEO mt = ModelConfigUtil.getTemplateByColumnId(columnId, siteId);
            if (null == mt) {
                throw new BaseRunTimeException(TipsMode.Message.toString(), "引用栏目内容模型没有配置.");
            }
            if(columnId.intValue()==oldEO.getColumnId().intValue()){
                throw new BaseRunTimeException(TipsMode.Message.toString(), "禁止循环引用");
            }

            String columnType = mt.getModelTypeCode();
            BaseContentEO newEO = null;
            try {
                newEO = (BaseContentEO) BeanUtils.cloneBean(oldEO);
                if ("videoNews".equals(columnType)) {
                    newEO.setTypeCode(BaseContentEO.TypeCode.videoNews.toString());
                    newEO.setRemarks(oldEO.getRemarks());
                } else if ("pictureNews".equals(columnType)) {
                    BaseContentEO tempEO = (BaseContentEO) BeanUtils.cloneBean(oldEO);
                    tempEO.setTypeCode(columnType);
                    getImageLinkFromContent(tempEO);//缩略图为空时取内容里第一张图片作为缩略图
                    if (AppUtil.isEmpty(tempEO.getImageLink())) {//缩略图为空
                        exitFlag = true;
                    }
                    newEO.setTypeCode(BaseContentEO.TypeCode.pictureNews.toString());// 设置为内容协同
                } else {
                    newEO.setTypeCode(BaseContentEO.TypeCode.articleNews.toString());// 设置为内容协同
                }
            } catch (Throwable e) {
            }// 复制对象;

            //不能放在try里面
            if (exitFlag) {
                String msg = "文章中没有任何图片，不能引用到" + ColumnUtil.getColumnName(columnId, siteId) + "栏目.";
                throw new BaseRunTimeException(TipsMode.Message.toString(), msg);
            }

            newEO.setSiteId(siteId);
            newEO.setColumnId(columnId);
            newEO.setContentPath("");
            String publishStatus = "0";
            if(isPublishs.length<=i){
                publishStatus = oldEO.getIsPublish()+"";
            }else{
                if (isPublishs != null && !AppUtil.isEmpty(isPublishs[i])) {
                    publishStatus = isPublishs[i];
                }
            }
            if ("1".equals(publishStatus)) {//发布
                newEO.setIsPublish(2);//设置为“发布中”中间状态
            } else {
                newEO.setIsPublish(0);
            }
            newEO.setQuoteStatus(1);

            // 构建文件复制回调函数
            FileOprUtil.Callback<BaseContentEO> callback = new FileOprUtil.Callback<BaseContentEO>(newEO) {

                @Override
                public void execute(String fileId, BaseContentEO newEO) {
                    // 保存文章
                    newEO.setId(null);
                    newEO.setImageLink(fileId);
                    //将创建人设置为当前用户
                    newEO.setCreateOrganId(LoginPersonUtil.getOrganId());
                    newEO.setCreateUserId(LoginPersonUtil.getUserId());
                    Date publishDate = newEO.getPublishDate();
                    if (publishDate == null) {
                        publishDate = new Date();
                    }
                    Long sort = publishDate.getTime() / 1000;
                    newEO.setNum(sort);
                    baseContentDao.save(newEO);
                    //存储缓存 不然生成静态出问题
                    CacheHandler.saveOrUpdate(BaseContentEO.class, newEO);
                }
            };
            if ("articleNews".equals(columnType)) {// 文章新闻处理
                FileOprUtil.copyFile(oldEO.getImageLink(), FileCenterEO.Type.Image.toString(), callback);
                CacheHandler.saveOrUpdate(BaseContentEO.class, newEO);
                FileOprUtil.copyContentFile(oldEO.getId(), newEO.getId(), newEO);
            } else if ("pictureNews".equals(columnType)) {// 图片新闻处理
                FileOprUtil.copyFile(oldEO.getImageLink(), FileCenterEO.Type.Image.toString(), callback);
                CacheHandler.saveOrUpdate(BaseContentEO.class, newEO);
                FileOprUtil.copyContentFile(oldEO.getId(), newEO.getId(), newEO);
                List<ContentPicEO> picList = contentPicService.getPicsList(contentId);
                for (ContentPicEO pic : picList) {
                    ContentPicEO picEO = null;
                    try {
                        picEO = (ContentPicEO) BeanUtils.cloneBean(pic);
                    } catch (Throwable e) {
                    }
                    picEO.setPicId(null);
                    picEO.setContentId(newEO.getId());
                    picEO.setColumnId(columnId);
                    picEO.setSiteId(siteId);
                    contentPicService.saveEntity(picEO);// 保存图片
                }
            } else if ("videoNews".equals(columnType)) {// 视频新闻
                FileOprUtil.copyFile(oldEO.getImageLink(), FileCenterEO.Type.Image.toString(), callback);
                VideoNewsVO oldVideoVO = videoService.getVideoEO(contentId, AMockEntity.RecordStatus.Normal.toString());
                CacheHandler.saveOrUpdate(BaseContentEO.class, newEO);
                FileOprUtil.copyContentFile(oldEO.getId(), newEO.getId(), newEO);
                VideoNewsEO newsVideoEO = new VideoNewsEO();
                newsVideoEO.setContentId(newEO.getId());
                newsVideoEO.setSiteId(siteId);
                newsVideoEO.setColumnId(columnId);
                String newPath =
                        FileOprUtil.copyFile(oldVideoVO.getVideoPath(), FileCenterEO.Type.Video.toString(), new FileOprUtil.Callback<BaseContentEO>(newEO));
                newsVideoEO.setVideoPath(newPath);
                newsVideoEO.setImageName(oldVideoVO.getImageName());
                newsVideoEO.setVideoName(oldVideoVO.getVideoName());
                newsVideoEO.setStatus(100);
                newsVideoEO.setCreateDate(new Date());
                videoService.saveEntity(newsVideoEO);
            } else {
                return "false";
            }

            ContentReferRelationEO relationEO = new ContentReferRelationEO();
            relationEO.setCauseById(contentId);
            relationEO.setCauseByColumnId(oldEO.getColumnId());
            if(BaseContentEO.TypeCode.public_content.toString().equals(oldEO.getTypeCode())){//主动公开实体
                PublicContentVO publicContentVO = publicContentService.getPublicContentByBaseContentId(oldEO.getId());
                relationEO.setCauseByCatId(publicContentVO.getCatId());
            }
            relationEO.setModelCode(modelCode);
            relationEO.setColumnId(Long.valueOf(columnIdSiteId[0]));
            relationEO.setReferId(newEO.getId());
            relationEO.setType(ContentReferRelationEO.TYPE.REFER.toString());
            relationEO.setIsColumnOpt(isColumnOpt==null?0:isColumnOpt);

            String optType = "引用到栏目";
            // 生成静态
            if ("1".equals(publishStatus)) {// 发布状态
                returnStr += siteId + "_" + Long.valueOf(columnIdSiteId[0]) + "_" + newEO.getId() + ",";
                optType += "并发布";
            }

            try {
                String newsType = getTypeCodeName(columnType);
                String columnStr ;
                if("主动公开".equals(newsType)){
                    OrganEO organ = CacheHandler.getEntity(OrganEO.class,oldEO.getColumnId());
                    PublicContentVO publicContentVO = publicContentService.getPublicContentByBaseContentId(oldEO.getId());
                    relationEO.setCauseByCatId(publicContentVO.getCatId());//设置原信息公开目录id
                    String columnName = organ.getName()+" > "+PublicCatalogUtil.getCatalogPath(oldEO.getColumnId(), publicContentVO.getCatId());
                    columnStr = "目录（"+columnName+"）";
                }else{
                    columnStr = "栏目（" + ColumnUtil.getColumnName(oldEO.getColumnId(), oldEO.getSiteId())+"）";
                }

                SysLog.log("引用"+newsType+" ："+columnStr+"，标题（"+oldEO.getTitle()+"），"+optType+"：" + ColumnUtil.getColumnName(newEO.getColumnId(),
                        newEO.getSiteId()), "BaseContentEO", CmsLogEO.Operation.Update.toString());
            }catch (Exception e){
                e.printStackTrace();
            }
            //保存引用关系
            contentReferRelationService.saveEntity(relationEO);

        }
        // 标记单纯引用
        baseContentDao.markQuote(contentId, 1);
        if (!AppUtil.isEmpty(returnStr)) {//去除最后的逗号
            returnStr = returnStr.substring(0, returnStr.length() - 1);
        }
        return returnStr;
    }

    @Override
    public Long getCountByCondition(Long columnId, Integer isPublish, Date st, Date ed, String recordStatus) {
        if (null == columnId) {
            throw new BaseRunTimeException(TipsMode.Message.toString(), "栏目ID不能为空");
        }
        if (null == isPublish) {
            isPublish = 1;
        }
        if (AppUtil.isEmpty(recordStatus)) {
            recordStatus = AMockEntity.RecordStatus.Normal.toString();
        }
        return baseContentDao.getCountByCondition(columnId, isPublish, st, ed, recordStatus);
    }

    @Override
    public List<ContentTjVO> getCountByCondition(Long siteId, String typeCode, Integer isPublish, Date st, Date ed) {
        return baseContentDao.getCountByCondition(siteId, typeCode, isPublish, st, ed);
    }

    @Override
    public Pagination getQueryPage(ContentPageVO pageVO) {
        List<Long> optColumns = new ArrayList<Long>();
        if (null != pageVO.getColumnId()) {
            optColumns.add(pageVO.getColumnId());
        } else if (null != pageVO.getColumnIds()) {
            optColumns = cn.lonsun.core.base.util.StringUtils.getListWithLong(pageVO.getColumnIds(), ",");
        } else {
            if (!LoginPersonUtil.isSiteAdmin() && !LoginPersonUtil.isRoot() && !LoginPersonUtil.isSuperAdmin()) {
                optColumns = siteRightsService.getCurUserHasColumnIds(LoginPersonUtil.getSiteId());
                List<Long> temp = new ArrayList<Long>();
                temp.addAll(optColumns);
                for (Long columnId : temp) {
                    ColumnMgrEO mgr = CacheHandler.getEntity(ColumnMgrEO.class, columnId);
                    if (null == mgr) {
                        optColumns.remove(columnId);
                    }
                }
            }
        }
        Long[] opts = optColumns.toArray(new Long[optColumns.size()]);
        if (AppUtil.isEmpty(pageVO.getSiteId()))
            pageVO.setSiteId(LoginPersonUtil.getSiteId());
        Pagination page = baseContentDao.getQueryPage(pageVO, opts);
        @SuppressWarnings("unchecked")
        List<BaseContentEO> list = (List<BaseContentEO>) page.getData();
        List<BaseContentByOptVO> voList = new ArrayList<BaseContentByOptVO>();
        for (BaseContentEO li : list) {
            if (BaseContentEO.TypeCode.public_content.toString().equals(li.getTypeCode())) {
                PublicContentVO vo = publicContentService.getPublicContentWithCatNameByBaseContentId(li.getId());
                if (null != vo) {
                    li.setColumnName(vo.getCatName());
                }
            } else {
                li.setColumnName(ColumnUtil.getColumnName(li.getColumnId(), li.getSiteId()));
            }
            BaseContentByOptVO vo = new BaseContentByOptVO();
            try {
                BeanUtils.copyProperties(vo, li);
            } catch (Exception e) {
                throw new BaseRunTimeException(TipsMode.Message.toString(), "全站搜索失败，请稍后再试！");
            }
            List<FunctionEO> oprOpts = siteRightsService.getOptByColumnId(li.getColumnId());
            vo.setOpt(oprOpts);
            voList.add(vo);
        }
        page.setData(voList);
        return page;
    }

    @Override
    public String copyRefer(CopyReferVO copyReferVO) {
        String returnStr = "";
        String publicReturnStr = "";
        String[] contentIds = copyReferVO.getContentId().replace("[", "").replace("]", "").split(",");// 新闻id

        for (String id : contentIds) {
            Long contentId = Long.parseLong(id);
            Long type = copyReferVO.getType();// 复制or引用 1为复制，其余为引用
            Long source = copyReferVO.getSource();// 来源 1为内容管理，2为信息公开
            String synColumnIds = copyReferVO.getSynColumnIds();// 同步到栏目的ID
            String synOrganCatIds = copyReferVO.getSynOrganCatIds();// 同步到单位目录的ID

            if (type != 1L) {//引用时，查询栏目引用关系
                Map<String,String> resultMap = this.getReferColumnCats(synColumnIds,synOrganCatIds,null);
                synColumnIds = resultMap.get("referColumnIds");
                synOrganCatIds = resultMap.get("referOrganCatIds");
            }

            String synMsgCatIds = copyReferVO.getSynMsgCatIds();// 同步到消息分类的ID
            String[] columnIds = StringUtils.isEmpty(synColumnIds) ? null : synColumnIds.split(",");// 同步栏目数组
            String modelCode = source == 1L ? "CONTENT" : source == 2 ? "PUBLIC" : "";
            // 判断类型
            if (null != columnIds && columnIds.length > 0) {
                if (type == 1L) {
                    if (!AppUtil.isEmpty(returnStr)) {
                        returnStr = returnStr + "," + this.saveCopy(columnIds, contentId, null, modelCode, copyReferVO.getSynColumnIsPublishs(),copyReferVO.getIsColumnOpt());// 复制到内容协同
                    } else {
                        returnStr = this.saveCopy(columnIds, contentId, null, modelCode, copyReferVO.getSynColumnIsPublishs(),copyReferVO.getIsColumnOpt());// 复制到内容协同
                    }
                } else {
                    if (!AppUtil.isEmpty(returnStr)) {
                        returnStr = returnStr + "," + this.saveReferNews(columnIds, contentId, modelCode, copyReferVO.getSynColumnIsPublishs(),copyReferVO.getIsColumnOpt());
                    } else {
                        returnStr = this.saveReferNews(columnIds, contentId, modelCode, copyReferVO.getSynColumnIsPublishs(),copyReferVO.getIsColumnOpt());
                    }
                }
            }

            if (type == 1L) {

            }else{//A被引用到B，B在被引用到C，C引用的其实是A
                List<ContentReferRelationEO> relationEOS = contentReferRelationService.getByReferId(contentId,null,ContentReferRelationEO.TYPE.REFER.toString());
                if(relationEOS!=null&&relationEOS.size()>0){
                    contentId = relationEOS.get(0).getCauseById();
                }
            }

            BaseContentEO contentEO = CacheHandler.getEntity(BaseContentEO.class, contentId);
            if (null != contentEO) {// 内容存在
                Date now = new Date();
                ContentMongoEO contentMongoEO = contentMongoService.queryById(contentId);
                if (StringUtils.isNotEmpty(synOrganCatIds)) {
                    PublicContentVO publicContentVO = publicContentService.getPublicContentByBaseContentId(contentId);
                    PublicContentVO vo = new PublicContentVO();

                    try {
                        if (contentEO != null) {
                            BeanUtils.copyProperties(vo, contentEO);
                        }
                        vo.setContent(contentMongoEO.getContent());
                        vo.setType(PublicContentEO.Type.DRIVING_PUBLIC.toString());
                        vo.setSiteId(null == copyReferVO.getPublicSiteId() ? LoginPersonUtil.getSiteId() : copyReferVO.getPublicSiteId());
                        if (contentEO.getPublishDate() == null) {
                            vo.setPublishDate(now);
                        } else {
                            vo.setPublishDate(contentEO.getPublishDate());
                        }

                        if (publicContentVO != null) {
                            BeanUtils.copyProperties(vo, publicContentVO);
                        }
                        String newsType = getTypeCodeName(contentEO.getTypeCode());
                        String logStr = "";
                        if (type == 1L) {
                            logStr = "复制"+newsType+":标题（"+contentEO.getTitle()+"）,";
                        }else{
                            logStr = "引用"+newsType+":标题（"+contentEO.getTitle()+"）,";
                        }
                        if("主动公开".equals(newsType)){
                            OrganEO organ = CacheHandler.getEntity(OrganEO.class,contentEO.getColumnId());
                            String columnName = organ.getName()+" > "+PublicCatalogUtil.getCatalogPath(contentEO.getColumnId(), publicContentVO.getCatId());
                            logStr += "目录（"+columnName+"）";
                        }else{
                            logStr += "栏目（" + ColumnUtil.getColumnName(contentEO.getColumnId(), contentEO.getSiteId())+"）";
                        }
                        vo.setLogStr(logStr);
                        if (!AppUtil.isEmpty(publicReturnStr)) {
                            publicReturnStr = publicReturnStr + "," + publicContentService.saveEntities(vo,contentId,type,modelCode, synOrganCatIds, copyReferVO.getSynOrganIsPublishs(),copyReferVO.getIsColumnOpt());
                        } else {
                            publicReturnStr = publicContentService.saveEntities(vo,contentId,type,modelCode, synOrganCatIds, copyReferVO.getSynOrganIsPublishs(),copyReferVO.getIsColumnOpt());
                        }
                    } catch (Exception e) {
                        if (type == 1L) {
                            throw new BaseRunTimeException(TipsMode.Message.toString(), "复制到信息公开失败.");
                        }else{
                            throw new BaseRunTimeException(TipsMode.Message.toString(), "引用到信息公开失败.");
                        }
                    }
                }
                if (type == 1L) {
                    if (StringUtils.isNotEmpty(synMsgCatIds)) {
                        Long[] msgCatIds = (Long[]) ConvertUtils.convert(synMsgCatIds.split(","), Long.class);
                        int l = msgCatIds.length;
                        List<CmsMsgSubmitEO> msgSubmitList = new ArrayList<CmsMsgSubmitEO>(l);
                        for (Long msgCatId : msgCatIds) {
                            CmsMsgSubmitEO msgSubEO = new CmsMsgSubmitEO();
                            msgSubEO.setSiteId(LoginPersonUtil.getSiteId());
                            msgSubEO.setPublishDate(now);
                            msgSubEO.setCreateUnitId(LoginPersonUtil.getUnitId());
                            msgSubEO.setContent(contentMongoEO.getContent());
                            msgSubEO.setName(contentEO.getTitle());
                            msgSubEO.setImageLink(contentEO.getImageLink());
                            msgSubEO.setAuthor(contentEO.getAuthor());
                            msgSubEO.setProvider(contentEO.getAuthor());
                            msgSubEO.setClassifyId(msgCatId);
                            msgSubEO.setProvider(LoginPersonUtil.getPersonName());
                            msgSubEO.setFromCode(contentEO.getResources());
                            msgSubmitList.add(msgSubEO);
                        }
                        msgSubmitService.saveEntities(msgSubmitList);
                    }
                    // 复制到微信素材
                    if (copyReferVO.isSynWeixin()) {
                        WeChatArticleEO weChatArticleEO = new WeChatArticleEO();
                        weChatArticleEO.setSiteId(LoginPersonUtil.getSiteId());
                        weChatArticleEO.setTitle(contentEO.getTitle());
                        weChatArticleEO.setAuthor(contentEO.getAuthor());
                        weChatArticleEO.setPublishDate(now);
                        if(null != contentMongoEO){
                            weChatArticleEO.setContent(contentMongoEO.getContent());
                        }
                        weChatArticleEO.setThumbImg(contentEO.getImageLink());
                        weChatArticleEO.setDescription(contentEO.getRemarks());
                        weChatArticleEO.setType(1);// 图文
                        weChatArticleService.saveArticle(weChatArticleEO);
                    }
                }
            }
        }
        if (!AppUtil.isEmpty(publicReturnStr)) {
            returnStr += "&" + publicReturnStr;
        }
        return returnStr;
    }


    @Override
    public String moveNews(CopyReferVO copyReferVO) {
        String returnStr = "";
        String synColumnIds = copyReferVO.getSynColumnIds();
        String isPublish = copyReferVO.getSynColumnIsPublishs();
        String[] contentIds = copyReferVO.getContentId().replace("[", "").replace("]", "").split(",");// 新闻id

        if (!AppUtil.isEmpty(synColumnIds)) {
            String[] columnIdSiteId = synColumnIds.split("_");
            Long columnId = Long.valueOf(columnIdSiteId[0]);
            Long siteId = Long.valueOf(columnIdSiteId[1]);
            //查询新栏目下最大排序号
//            SortVO _svo = baseContentDao.getMaxNumByColumn(columnId);
//            Long sort = 1L;
//            if (!AppUtil.isEmpty(_svo.getSortNum()))
//                sort = _svo.getSortNum() + 1L;
            for (String id : contentIds) {
                Long oldColumnId = 0L;
                Long oldSiteId = 0L;
                Long newColumnId = 0L;
                Long newSiteId = 0L;


                ColumnConfigEO configEO = CacheHandler.getEntity(ColumnConfigEO.class, CacheGroup.CMS_INDICATORID, columnId);

                Long contentId = Long.parseLong(id);
                BaseContentEO baseContentEO = baseContentDao.getEntity(BaseContentEO.class, contentId);

                BaseContentEO oldEO = null;
                try {
                    oldEO = (BaseContentEO) BeanUtils.cloneBean(baseContentEO);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                oldColumnId = baseContentEO.getColumnId();
                oldSiteId = baseContentEO.getSiteId();

                newColumnId = columnId;
                newSiteId = siteId;

                //调整排序号，是记录显示在移动后栏目的最前面
//                baseContentEO.setNum(sort);
//                sort += 1;
                baseContentEO.setIsPublish(Integer.valueOf(isPublish));

                baseContentEO.setColumnId(columnId);
                baseContentEO.setSiteId(siteId);
                //设置类型  图片新闻和文章新闻可以相互移动
                baseContentEO.setTypeCode(configEO.getColumnTypeCode());

                if (Integer.valueOf(isPublish) == 1) {
                    baseContentEO.setIsPublish(2);//设置为“发布中”中间状态
                }


                if (configEO.getColumnTypeCode().equals(BaseContentEO.TypeCode.pictureNews.toString())) {
                    oldEO.setTypeCode(configEO.getColumnTypeCode());
                    getImageLinkFromContent(oldEO);//缩略图为空时取内容里第一张图片作为缩略图
                    if (AppUtil.isEmpty(oldEO.getImageLink())) {//缩略图为空
                        String msg = "文章中没有任何图片，不能移动到" + ColumnUtil.getColumnName(newColumnId, newSiteId) + "栏目.";
                        throw new BaseRunTimeException(TipsMode.Message.toString(), msg);
                    }
                }

                updateEntity(baseContentEO);
                // 更新缓存
                CacheHandler.saveOrUpdate(BaseContentEO.class, baseContentEO);

                //更新复制引用关系
                List<ContentReferRelationEO> list = contentReferRelationService.getByReferId(contentId, null, null);
                if(list!=null&&list.size()>0){
                    for(ContentReferRelationEO referRelationEO:list){
                        referRelationEO.setColumnId(newColumnId);
                    }
                    contentReferRelationService.updateEntities(list);
                }

                SysLog.log("移动新闻 ：栏目（" + ColumnUtil.getColumnName(oldColumnId, oldSiteId)
                        + "），标题（"+baseContentEO.getTitle()+"），移动到栏目：" + ColumnUtil.getColumnName(newColumnId,
                        newSiteId), "BaseContentEO", CmsLogEO.Operation.Update.toString());

                // 生成静态
                if (Integer.valueOf(isPublish) == 1) {// 发布状态
                    returnStr += newSiteId + "_" + newColumnId + "_" + baseContentEO.getId() + ",";
                }
            }
        }
        if (!AppUtil.isEmpty(returnStr)) {//去除最后的逗号
            returnStr = returnStr.substring(0, returnStr.length() - 1);
        }

        return returnStr;
    }

    @Override
    public List<BaseContentEO> deleteList(Long columnId) {
        return baseContentDao.deleteList(columnId);
    }

    @Override
    public List<BaseContentVO> getCounts(ContentPageVO vo, Integer limit) {
        return baseContentDao.getCounts(vo, limit);
    }

    @Override
    public Pagination getPageByMobile(ContentPageVO pageVO) {
        return baseContentDao.getPageByMobile(pageVO);
    }

    @Override
    public Pagination getPageByOrganIds(List<Long> organIds, Long siteId, String typeCode, Long pageIndex, Integer pageSize) {
        return baseContentDao.getPageByOrganIds(organIds, siteId, typeCode, pageIndex, pageSize);
    }

    @Override
    public void updateNums(Long[] ids, Long[] nums) {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("id", ids);
        params.put("recordStatus", AMockEntity.RecordStatus.Normal.toString());
        List<BaseContentEO> list = getEntities(BaseContentEO.class, params);
        if (null != list && list.size() > 0) {
            Map<Long, BaseContentEO> contentMap = (Map<Long, BaseContentEO>) AppUtil.parseListToMap(list, "id");
            BaseContentEO eo = null;
            for (int i = 0, l = ids.length; i < l; i++) {
                if (null != (eo = contentMap.get(ids[i]))) {
                    eo.setNum(nums[i]);
                }
            }
            updateEntities(list);
        }
    }

    @Override
    public List<ColumnMgrEO> getStatisticsCount(List<ColumnMgrEO> list) {
        try {
            // 数据字典配置
            List<DataDictVO> dictionarys = DataDictionaryUtil.getItemList("col_statistics", null);
            if (list != null && list.size() > 0 && dictionarys != null && dictionarys.size() > 0) {
                List<Long> dictionaryIds = new ArrayList<Long>();
                // 处理数据字典
                for (DataDictVO d : dictionarys) {
                    if (d.getValue() != null) {
                        try {
                            dictionaryIds.add(Long.parseLong(d.getValue()));
                        } catch (org.springframework.expression.ParseException e) {
                            e.printStackTrace();
                        }
                    }
                }
                // 查询总数
                Map<Long, Long> countMap = null;
                if (dictionaryIds != null && dictionaryIds.size() > 0) {
                    countMap = getStatisticCounts(dictionaryIds);
                }
                // 设置
                if (countMap != null) {
                    for (ColumnMgrEO vo : list) {
                        Long count = countMap.get(vo.getIndicatorId());
                        if (count != null) {
                            vo.setIsCount(1);
                            vo.setCount(count);
                        }
                    }
                }
            }

        } catch (Exception e) {
            // e.printStackTrace();
        }
        return list;
    }

    @Override
    public Map<Long, Long> getStatisticCounts(List<Long> ids) {
        Map<Long, Long> map = null;
        List<Object> objects = baseContentDao.getStatisticCounts(ids);
        if (objects != null && objects.size() > 0) {
            map = new HashMap<Long, Long>();
            Object[] array = (Object[]) objects.get(0);
            if (array != null && array.length > 0) {
                for (int i = 0; i < array.length; i++) {
                    try {
                        Long count = array[i] == null ? 0 : Long.valueOf(array[i].toString());
                        map.put(ids.get(i), count);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        return map;
    }

    @Override
    public List<BaseContentEO> getList(ContentPageVO pageVO) {
        return baseContentDao.getList(pageVO);
    }

    @Override
    public List<BaseContentEO> getBaseContents(Long siteId, Long columnId) {
        return baseContentDao.getBaseContents(siteId, columnId);
    }

    @Override
    public Long getCountByTypeAndStatus(Long siteId, String type, Integer isPublish) {
        //查询当前单位的信息公开信息
        return baseContentDao.getCountByTypeAndStatus(siteId, LoginPersonUtil.getUnitId(), type, isPublish);
    }

    @Override
    public List<BaseContentEO> getBaseContents(Long siteId, String title) {
        return baseContentDao.getBaseContents(siteId, title);
    }

    @Override
    public void deleteNewsByColumnId(Long columnId) {
        baseContentDao.deleteNewsByColumnId(columnId);
    }

    @Override
    public List<BaseContentEO> getWorkGuidXYContent(Long siteId, Long columnId) {
        return baseContentDao.getWorkGuidXYContent(siteId, columnId);
    }

    @Override
    public List<WordListVO> getEmptyColumnList(StatisticsQueryVO queryVO) {
        List<WordListVO> listData = null;
        listData = baseContentDao.getWordList(queryVO);
        List<WordListVO> newList = new ArrayList<WordListVO>();
        if (!AppUtil.isEmpty(queryVO.getIsColumn()) && queryVO.getIsColumn().equals("1")) {
            List<ColumnMgrEO> columnMgrEOs = columnConfigService.getAllColumnTree(LoginPersonUtil.getSiteId(), null);

            List<Long> columnIds = new ArrayList<Long>();
            for (WordListVO wordListVO : listData) {
                //这里的organId里面实际存的是columnId的值
                columnIds.add(wordListVO.getOrganId());
            }

            for (int i = 0; i < columnMgrEOs.size(); i++) {
                if ((BaseContentEO.TypeCode.articleNews.toString().equals(columnMgrEOs.get(i).getColumnTypeCode())
                        || BaseContentEO.TypeCode.pictureNews.toString().equals(columnMgrEOs.get(i).getColumnTypeCode()))
                        && columnMgrEOs.get(i).getIsParent() == 0) {

                    WordListVO vo = new WordListVO();
                    vo.setOrganId(columnMgrEOs.get(i).getIndicatorId());
                    IndicatorEO ind = CacheHandler.getEntity(IndicatorEO.class, columnMgrEOs.get(i).getIndicatorId());
                    IndicatorEO site = CacheHandler.getEntity(IndicatorEO.class, queryVO.getSiteId());
                    StringBuilder sb = new StringBuilder();
                    sb.append("<a href='").append(site.getUri()).append("/").append(ind.getParentNamesPinyin())
                            .append("/").append(ind.getNamePinyin()).append("/index.html'>")
                            .append(ColumnUtil.getColumnName(columnMgrEOs.get(i).getIndicatorId(), LoginPersonUtil.getSiteId()))
                            .append("</a>");
                    vo.setOrganName(sb.toString());
//                    vo.setOrganName(ind.columnPath(site.getUri()));
                    if (!columnIds.contains(vo.getOrganId())) {
                        if (!StringUtils.isEmpty(queryVO.getOrganName())) {
                            if (vo.getOrganName().contains(queryVO.getOrganName())) {
                                newList.add(vo);
                            }
                        } else {
                            newList.add(vo);
                        }
                    }
                }
            }
        }
        return newList;
    }

    @Override
    public int getEmptyColumnCount(StatisticsQueryVO queryVO) {
        List<WordListVO> listData = null;
        listData = baseContentDao.getWordList(queryVO);
        int num = 0;
        if (!AppUtil.isEmpty(queryVO.getIsColumn()) && queryVO.getIsColumn().equals("1")) {
            List<ColumnMgrEO> columnMgrEOs = columnConfigService.getAllColumnTree(LoginPersonUtil.getSiteId(), null);

            List<Long> columnIds = new ArrayList<Long>();
            for (WordListVO wordListVO : listData) {
                //这里的organId里面实际存的是columnId的值
                columnIds.add(wordListVO.getOrganId());
            }

            for (int i = 0; i < columnMgrEOs.size(); i++) {
                if ((BaseContentEO.TypeCode.articleNews.toString().equals(columnMgrEOs.get(i).getColumnTypeCode())
                        || BaseContentEO.TypeCode.pictureNews.toString().equals(columnMgrEOs.get(i).getColumnTypeCode()))
                        && columnMgrEOs.get(i).getIsParent() == 0) {
                    if (!columnIds.contains(columnMgrEOs.get(i).getIndicatorId())) {
                        num = num + 1;
                    }
                }
            }
        }
        return num;
    }


    private String getTypeCodeName(String typeCode){
        String typeCodeName = "内容";
        if(BaseContentEO.TypeCode.articleNews.toString().equals(typeCode)){
            typeCodeName = "文章新闻";
        }else if(BaseContentEO.TypeCode.pictureNews.toString().equals(typeCode)){
            typeCodeName = "图片新闻";
        }else if(BaseContentEO.TypeCode.videoNews.toString().equals(typeCode)){
            typeCodeName = "视频新闻";
        }else if(BaseContentEO.TypeCode.linksMgr.toString().equals(typeCode)){
            typeCodeName = "链接";
        }else if(BaseContentEO.TypeCode.public_content.toString().equals(typeCode)){
            typeCodeName = "主动公开";
        }
        return typeCodeName;
    }

    @Override
    public Long getSummaryCount(StatisticsQueryVO vo) {
        return baseContentDao.getSummaryCount(vo);
    }


    /**
     * 查询引用栏目
     * @param referColumnIds
     * @param referOrganCatIds
     * @param sourceColumnIdStr
     * @return
     */
    @Override
    public Map<String,String> getReferColumnCats(String referColumnIds,String referOrganCatIds,String sourceColumnIdStr){
        Map<String,String> resultMap = new HashMap<String, String>();
        List<String> referColumnList = new ArrayList<String>();
        List<String> referOrganCatList = new ArrayList<String>();
        if(!AppUtil.isEmpty(referColumnIds)
                ||!AppUtil.isEmpty(referOrganCatIds)){
            List<String> newReferColumnList = new ArrayList<String>();
            List<String> newReferOrganCatList = new ArrayList<String>();
            if(!AppUtil.isEmpty(referColumnIds)){
                newReferColumnList.addAll(Arrays.asList(referColumnIds.split(",")));
            }
            if(!AppUtil.isEmpty(referOrganCatIds)){
                newReferOrganCatList.addAll(Arrays.asList(referOrganCatIds.split(",")));
            }

            //递归查询引用栏目
            queryReferColumnCats(referColumnList,referOrganCatList,
                    newReferColumnList,newReferOrganCatList,sourceColumnIdStr);

        }
        resultMap.put("referColumnIds", StringUtils.join(referColumnList, ","));
        resultMap.put("referOrganCatIds", StringUtils.join(referOrganCatList, ","));
        return resultMap;
    }

    /**
     * 递归查询引用栏目
     * @param resultReferColumnList
     * @param resultReferOrganCatList
     * @param newReferColumnList
     * @param newReferOrganCatList
     * @param sourceColumnIdStr
     */
    private void queryReferColumnCats(List<String> resultReferColumnList,List<String> resultReferOrganCatList,
                                      List<String> newReferColumnList,List<String> newReferOrganCatList,String sourceColumnIdStr){
        List<String> newReferColumnList_ = new ArrayList<String>();
        List<String> newReferOrganCatList_ = new ArrayList<String>();
        //查询当前栏目下边的所有引用栏目跟目录
        if(newReferColumnList!=null&&newReferColumnList.size()>0){
            for(String columnIdStr:newReferColumnList){
                String[] columnIdSiteId = columnIdStr.split("_");
                Long columnId = Long.valueOf(columnIdSiteId[0]);
                ColumnMgrEO columnMgrEO = columnConfigService.getById(columnId);
                if(columnMgrEO!=null){
                    //该栏目未被删除时，将栏目引用关系添加到结果列表中
                    if(!resultReferColumnList.contains(columnIdStr)){//去重
                        resultReferColumnList.add(columnIdStr);
                    }

                    if(!AppUtil.isEmpty(columnMgrEO.getReferColumnIds())
                            ||!AppUtil.isEmpty(columnMgrEO.getReferOrganCatIds())){
                        String referColumnIds = columnMgrEO.getReferColumnIds();
                        String referOrganCatIds = columnMgrEO.getReferOrganCatIds();
                        if(!AppUtil.isEmpty(referColumnIds)){
                            newReferColumnList_.addAll(Arrays.asList(referColumnIds.split(",")));
                        }
                        if(!AppUtil.isEmpty(referOrganCatIds)){
                            newReferOrganCatList_.addAll(Arrays.asList(referOrganCatIds.split(",")));
                        }
                    }
                }
            }
        }

        //查询当前信息公开目录下边的所有引用栏目跟目录
        if(newReferOrganCatList!=null&&newReferOrganCatList.size()>0){
            for(String organCatStr:newReferOrganCatList){
                String[] organCatStrId = organCatStr.split("_");
                Long organId = Long.valueOf(organCatStrId[0]);
                Long catId = Long.valueOf(organCatStrId[1]);
                PublicCatalogOrganRelEO catalogOrganRelEO = publicCatalogOrganRelService.getByOrganIdCatId(organId,catId);
                PublicCatalogEO catalogEO = CacheHandler.getEntity(PublicCatalogEO.class, catId);
                if(catalogOrganRelEO!=null||catalogEO!=null){
                    //将目录引用关系添加到结果列表中
                    if(!resultReferOrganCatList.contains(organCatStr)){//去重
                        resultReferOrganCatList.add(organCatStr);
                    }
                }
                if(catalogOrganRelEO!=null){
                    if(!AppUtil.isEmpty(catalogOrganRelEO.getReferColumnIds())
                            ||!AppUtil.isEmpty(catalogOrganRelEO.getReferOrganCatIds())){
                        String referOrganCatIds = catalogOrganRelEO.getReferOrganCatIds();
                        String referColumnIds = catalogOrganRelEO.getReferColumnIds();
                        if(!AppUtil.isEmpty(referColumnIds)){
                            newReferColumnList_.addAll(Arrays.asList(referColumnIds.split(",")));
                        }
                        if(!AppUtil.isEmpty(referOrganCatIds)){
                            newReferOrganCatList_.addAll(Arrays.asList(referOrganCatIds.split(",")));
                        }
                    }
                }
            }
        }



        //判断是否存在闭环
        if(!AppUtil.isEmpty(sourceColumnIdStr)&&resultReferColumnList.contains(sourceColumnIdStr)){
            throw new BaseRunTimeException(TipsMode.Message.toString(),"栏目引用配置存在循环引用，请检查配置");
        }

        //判断是否存在闭环
        if(!AppUtil.isEmpty(sourceColumnIdStr)&&resultReferOrganCatList.contains(sourceColumnIdStr)){
            throw new BaseRunTimeException(TipsMode.Message.toString(),"栏目引用配置存在循环引用，请检查配置");
        }

        //递归查询
        if(newReferColumnList_.size()>0||newReferOrganCatList_.size()>0){
            queryReferColumnCats(resultReferColumnList,resultReferOrganCatList,
                    newReferColumnList_,newReferOrganCatList_,sourceColumnIdStr);
        }


    }

    @Override
    public List<OrganEO> getCreateOrganByColumnIds(List<Long> columnIds, Long siteId) {
        return baseContentDao.getCreateOrganByColumnIds(columnIds,siteId);
    }

    @Override
    public List<Map<String, Object>> statisticsByColumnIdsAndOrganId(StatisticsQueryVO queryVO) {
        return baseContentDao.statisticsByColumnIdsAndOrganId(queryVO);
    }
}