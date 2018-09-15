package cn.lonsun.content.controller;

import cn.lonsun.activemq.MessageSenderUtil;
import cn.lonsun.cache.client.CacheHandler;
import cn.lonsun.common.util.AppUtil;
import cn.lonsun.content.internal.dao.IContentReferRelationDao;
import cn.lonsun.content.internal.entity.BaseContentEO;
import cn.lonsun.content.internal.entity.ContentReferRelationEO;
import cn.lonsun.content.internal.entity.VideoNewsEO;
import cn.lonsun.content.internal.service.IBaseContentService;
import cn.lonsun.content.internal.service.IContentPicService;
import cn.lonsun.content.internal.service.IVideoNewsService;
import cn.lonsun.content.optrecord.entity.ContentOptRecordEO;
import cn.lonsun.content.optrecord.util.ContentOptRecordUtil;
import cn.lonsun.content.vo.ContentPageVO;
import cn.lonsun.content.vo.ContentReferRelationPageVO;
import cn.lonsun.content.vo.CopyReferVO;
import cn.lonsun.core.base.controller.BaseController;
import cn.lonsun.core.base.entity.AMockEntity;
import cn.lonsun.core.enums.SystemCodes;
import cn.lonsun.core.exception.BaseRunTimeException;
import cn.lonsun.core.util.Pagination;
import cn.lonsun.core.util.TipsMode;
import cn.lonsun.job.timingjob.jobimpl.NewsCancelNewTaskImpl;
import cn.lonsun.job.timingjob.jobimpl.NewsIssueTaskImpl;
import cn.lonsun.job.timingjob.jobimpl.NewsTopTaskImpl;
import cn.lonsun.job.util.ScheduleJobUtil;
import cn.lonsun.message.internal.entity.MessageEnum;
import cn.lonsun.message.internal.entity.MessageStaticEO;
import cn.lonsun.mongodb.base.IMongoDbFileServer;
import cn.lonsun.mongodb.entity.ContentMongoEO;
import cn.lonsun.mongodb.service.ContentMongoServiceImpl;
import cn.lonsun.mongodb.vo.MongoFileVO;
import cn.lonsun.publicInfo.internal.entity.PublicCatalogOrganRelEO;
import cn.lonsun.publicInfo.internal.service.IPublicCatalogOrganRelService;
import cn.lonsun.publicInfo.internal.service.IPublicContentService;
import cn.lonsun.publicInfo.vo.PublicContentVO;
import cn.lonsun.rbac.internal.entity.OrganEO;
import cn.lonsun.rbac.internal.entity.PersonEO;
import cn.lonsun.rbac.internal.service.IPersonService;
import cn.lonsun.site.site.internal.entity.ColumnMgrEO;
import cn.lonsun.site.site.internal.entity.SiteMgrEO;
import cn.lonsun.site.site.internal.service.IColumnConfigService;
import cn.lonsun.system.systemlog.internal.entity.CmsLogEO;
import cn.lonsun.util.*;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.*;

import static cn.lonsun.cache.client.CacheHandler.getEntity;

/**
 * @author Hewbing
 * @ClassName: ArticleNewsController
 * @Description: article news
 * @date 2015年11月4日 上午11:06:50
 */
@Controller
@RequestMapping(value = "articleNews")
public class ArticleNewsController extends BaseController {

    @Autowired
    private IBaseContentService baseContentService;

    @Autowired
    private IColumnConfigService columnConfigService;

    @Autowired
    private IContentPicService contentPicService;

    @Autowired
    private IMongoDbFileServer mongoDbFileServer;

    @Autowired
    private IContentReferRelationDao contentReferRelationDao;

    @Autowired
    private ContentMongoServiceImpl contentMongoService;
    @Autowired
    private IVideoNewsService videoService;
    @Autowired
    private IPersonService personService;
    @Autowired
    private IPublicContentService publicContentService;

    @Autowired
    private BaseContentController baseContentController;

    @Value("${ffmpeg.path}")
    private String path;

    @RequestMapping("index")
    public String contentList(Long pageIndex,Long indicatorId, ModelMap map) {
        if (pageIndex == null)
            pageIndex = 0L;
        Integer isParent = null;
        if (indicatorId != null) {
            ColumnMgrEO eo = getEntity(ColumnMgrEO.class, indicatorId);
            isParent = eo.getIsParent();
        }
        //存放 是否为父节点属性
        map.put("isParent",isParent);
        map.put("pageIndex", pageIndex);
        map.put("indicatorId", indicatorId);
        return "/content/articlenews/article_news_list";
    }

    /**
     * @return
     * @Description get into the thumbnail page
     * @author Hewbing
     * @date 2015年9月15日 下午5:37:20
     */
    @RequestMapping("thumbUpload")
    public String thumbUpload() {
        return "/content/news_thumbnail";
    }


    /**
     * 图片
     * 视屏
     *
     * @return
     * @Description get into the thumbnail page
     * @author Hewbing
     * @date 2015年9月15日 下午5:37:20
     */
    @RequestMapping("mtxxUpload")
    public String mtxxUpload() {
        return "/content/mtxx_upload";
    }

    @RequestMapping("videoUpload")
    public String videoUpload() {
        return "/content/video_upload";
    }

    @RequestMapping("getColumnList")
    public String getColumnList(Long siteId, Long id, String columnType, String columnId, ModelMap map) {
        map.put("siteId", siteId);
        map.put("id", id);
        map.put("columnType", columnType);
        map.put("columnId", columnId);
        return "/content/chose_column";

    }

    /**
     * @param id
     * @param map
     * @return String 返回类型
     * @throws
     * @Title: articleNewsEdit
     * @Description: 跳转新闻编辑页
     */
    @RequestMapping("articleNewsEdit")
    public String articleNewsEdit(@RequestParam(value = "id", required = false) Long id, ModelMap map,
                                  @RequestParam(value = "pageIndex", required = false) Long pageIndex, String toolbar,
                                  @RequestParam(value = "type", required = false) String type, String typeCode) {
        SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String str1 = sdf1.format(new Date());
        map.put("articleId", id);
        map.put("nowDate", str1);
        map.put("pageIndex", pageIndex);
        map.put("toolbar", toolbar);
        map.put("type", type);
        map.put("typeCode", typeCode);
        return "/content/articlenews/article_news_edit";
    }

    @RequestMapping("articleNewsEdit_Form")
    public String articleNewsEdit_Form(@RequestParam(value = "id", required = false) Long id, ModelMap map,
                                       @RequestParam(value = "pageIndex", required = false) Long pageIndex) {
        SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String str1 = sdf1.format(new Date());
        map.put("articleId", id);
        map.put("nowDate", str1);
        map.put("pageIndex", pageIndex);
        return "/content/articlenews/article_news_edit_form";
    }

    /**
     * 查看被引用详情
     * @param id
     * @param map
     * @return
     */
    @RequestMapping("viewReferedDetail")
    public String viewReferedDetail(Long id, ModelMap map) {
        map.put("id", id);
        return "/content/view_refered_detail";
    }


    /**
     * 获取被引用详情分页数据
     * @Author: liuk
     * @Date: 2018-5-4 17:26:28
     */
    @RequestMapping("getReferedDetailPage")
    @ResponseBody
    public Object getReferedDetailPage(ContentReferRelationPageVO pageVO) {
        pageVO.setType(ContentReferRelationEO.TYPE.REFER.toString());
        Pagination page = contentReferRelationDao.getPagination(pageVO);
        List<?> dataList = page.getData();
        for (Object obj : dataList) {
            ContentReferRelationEO eo = (ContentReferRelationEO)obj;
            PersonEO personEO = personService.getPersonByUserId(eo.getCreateOrganId(),eo.getCreateUserId());
            eo.setPersonName(personEO.getName());//设置操作人名
            eo.setOrganName(personEO.getOrganName());//设置操作单位名
            String referModelCode = eo.getReferModelCode();
            try{
                //查询信息公开栏目名称
                if(!AppUtil.isEmpty(referModelCode)&&referModelCode.equals(ContentReferRelationEO.ModelCode.PUBLIC.toString())){
                    OrganEO organ = getEntity(OrganEO.class,eo.getColumnId());
                    eo.setReferName(organ.getName()+" > "+PublicCatalogUtil.getCatalogPath(eo.getColumnId(), eo.getCatId()));
                }else{//查询新闻栏目名称
                    BaseContentEO baseContentEO = getEntity(BaseContentEO.class,eo.getReferId());
                    Long siteId = baseContentEO.getSiteId();
                    if(siteId.intValue()!=LoginPersonUtil.getSiteId().intValue()){//栏目不是当前站点，栏目名称前加上站点名称
                        SiteMgrEO siteMgrEO = getEntity(SiteMgrEO.class,siteId);
                        eo.setReferName(siteMgrEO.getName()+" > "+ColumnUtil.getColumnName(eo.getColumnId(), siteId));
                    }else{
                        eo.setReferName(ColumnUtil.getColumnName(eo.getColumnId(), siteId));
                    }
                }
            }catch (Exception e){
                e.printStackTrace();
            }
        }

        return getObject(page);
    }


    /**
     * 查看引用详情
     * @param id
     * @param map
     * @return
     */
    @RequestMapping("viewReferDetail")
    public String viewReferDetail(Long id, ModelMap map) {
        map.put("id", id);
        return "/content/view_refer_detail";
    }


    /**
     * 获取引用详情分页数据
     * @Author: liuk
     * @Date: 2018-5-4 17:26:28
     */
    @RequestMapping("getReferDetailPage")
    @ResponseBody
    public Object getReferDetailPage(ContentReferRelationPageVO pageVO) {
        pageVO.setType(ContentReferRelationEO.TYPE.REFER.toString());
        Pagination page = contentReferRelationDao.getPagination(pageVO);
        List<?> dataList = page.getData();
        for (Object obj : dataList) {
            ContentReferRelationEO eo = (ContentReferRelationEO)obj;
            PersonEO personEO = personService.getPersonByUserId(eo.getCreateOrganId(),eo.getCreateUserId());
            eo.setPersonName(personEO.getName());//设置操作人名
            eo.setOrganName(personEO.getOrganName());//设置操作单位名
            String moduleCode = eo.getModelCode();
            try{
                //查询信息公开栏目名称
                if(!AppUtil.isEmpty(moduleCode)&&moduleCode.equals(ContentReferRelationEO.ModelCode.PUBLIC.toString())){
                    PublicContentVO publicContentVO = publicContentService.getPublicContentByBaseContentId(eo.getCauseById());
                    OrganEO organ = getEntity(OrganEO.class,publicContentVO.getOrganId());
                    eo.setReferName(organ.getName()+" > "+PublicCatalogUtil.getCatalogPath(publicContentVO.getOrganId(), publicContentVO.getCatId()));
                }else{//查询新闻栏目名称
                    BaseContentEO baseContentEO = getEntity(BaseContentEO.class,eo.getCauseById());
                    Long siteId = baseContentEO.getSiteId();
                    if(siteId.intValue()!=LoginPersonUtil.getSiteId().intValue()){//栏目不是当前站点，栏目名称前加上站点名称
                        SiteMgrEO siteMgrEO = getEntity(SiteMgrEO.class,siteId);
                        eo.setReferName(siteMgrEO.getName()+" > "+ColumnUtil.getColumnName(baseContentEO.getColumnId(), siteId));
                    }else{
                        eo.setReferName(ColumnUtil.getColumnName(baseContentEO.getColumnId(), siteId));
                    }
                }
            }catch (Exception e){
                e.printStackTrace();
            }
        }

        return getObject(page);
    }


    @RequestMapping("getArticleContent")
    @ResponseBody
    public Object getArticleContent(Long id, String recordStatus) {
        Map<String, Object> map = new HashMap<String, Object>();
        BaseContentEO article = baseContentService.getContent(id, recordStatus);
        map.put("article", article);
        Criteria criteria = Criteria.where("_id").is(id);
        Query query = new Query(criteria);
        ContentMongoEO _eo = contentMongoService.queryOne(query);
        String content = "";
        if (!AppUtil.isEmpty(_eo)) {
            content = _eo.getContent();
        }
        map.put("content", HotWordsCheckUtil.revertAll(content));
        return getObject(map);
    }

    /**
     * @param pageVO
     * @return
     * @Description get news page by pageVO
     * @author Hewbing
     * @date 2015年9月14日 下午2:48:11
     */
    @RequestMapping("getPage")
    @ResponseBody
    public Object getPage(ContentPageVO pageVO) {
        try {
            String str = new String(pageVO.getTitle().getBytes("ISO-8859-1"), "utf-8");
            pageVO.setTitle(str);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        return getObject(baseContentService.getPage(pageVO));
    }

    /**
     * @param ids
     * @return
     * @Description delete news and mark No reference status
     * 删除新闻并标记为被其他栏目引用的文件的取消引用标记
     * @author Hewbing
     * @date 2015年9月14日 下午2:48:53
     */
    @RequestMapping("delete")
    @ResponseBody
    public Object delete(@RequestParam("ids[]") Long[] ids) {
        for (int i = 0; i < ids.length; i++) {
            List<ContentReferRelationEO> list = contentReferRelationDao.getByCauseId(ids[i], null, ContentReferRelationEO.TYPE.REFER.toString());
            if (list != null && list.size() > 0) {
                List<Long> tempList = Arrays.asList(ids);
                List<Long> idsList = new ArrayList(tempList);
                for(ContentReferRelationEO eo:list){
                    idsList.add(eo.getReferId());//引用新闻也要同步删除
                }
                ids = idsList.toArray(new Long[]{});
            }
        }
        List<BaseContentEO> list = baseContentService.getEntities(BaseContentEO.class, ids);
        baseContentService.delContent(ids);
        List<Long> publishIds = new ArrayList<Long>();
        Long siteId = LoginPersonUtil.getSiteId();
        Long columnId = null;
        if (list != null) {
            for (BaseContentEO li : list) {
                if (li.getIsPublish() == 1) {
                    publishIds.add(li.getId());
                    columnId = li.getColumnId();
                }

                String newsType = getTypeCodeName(li.getTypeCode());

                //添加操作日志
                if(list.size()>1){
                    SysLog.log("批量删除"+newsType+" ：栏目（" + ColumnUtil.getColumnName(li.getColumnId(), li.getSiteId()) + "），标题（" + li.getTitle()+"）", "BaseContentEO",
                            CmsLogEO.Operation.Update.toString());
                }else{
                    SysLog.log("删除"+newsType+" ：栏目（" + ColumnUtil.getColumnName(li.getColumnId(), li.getSiteId()) + "），标题（" + li.getTitle()+"）", "BaseContentEO",
                            CmsLogEO.Operation.Update.toString());
                }

            }
        }
        if (publishIds.size() > 0) {
            boolean rel =
                    MessageSenderUtil.publishContent(
                            new MessageStaticEO(siteId, columnId, publishIds.toArray(new Long[publishIds.size()])).setType(MessageEnum.UNPUBLISH.value()), 2);
        }
        return getObject();
    }

    /**
     * 查询新闻在引用关系表中引用状态
     *
     * @param id
     * @return
     */
    @RequestMapping("getStatus")
    @ResponseBody
    public Object getStatus(@RequestParam("ids") Long id) {
        BaseContentEO bc = baseContentService.getEntity(BaseContentEO.class, id);
        Long causeById = 0l;
        String type = null;
        if (null != bc) {
            causeById = bc.getId();
        }
        List<ContentReferRelationEO> list = contentReferRelationDao.getCauseById(causeById);
        if (null != list && !list.isEmpty()) {
            for (ContentReferRelationEO cr : list) {
                if (!ContentReferRelationEO.TYPE.COPY.toString().equals(cr.getType())) {//复制的不提示
                    type = cr.getType();
                }
            }
        }
        return getObject(type);
    }


    /**
     * 大批量删除
     *
     * @param ids
     * @return
     */
    @RequestMapping("delete1")
    @ResponseBody
    public Object delete1(@RequestParam("ids[]") Long[] ids) {
        List<BaseContentEO> list1 = baseContentService.deleteList(54044L);
        for (int j = 0; j < 41; j++) {
            List<BaseContentEO> list = new ArrayList<BaseContentEO>();
            list = list1.subList(j * 100, (j + 1) * 100);
            Long[] idArr = new Long[list.size()];
            int i = 0;
            for (BaseContentEO eo : list) {
                idArr[i++] = eo.getId();
            }
            baseContentService.delContent(idArr);
            List<Long> publishIds = new ArrayList<Long>();
            Long siteId = LoginPersonUtil.getSiteId();
            Long columnId = null;
            if (list != null) {
                for (BaseContentEO li : list) {
                    if (li.getIsPublish() == 1) {
                        publishIds.add(li.getId());
                        columnId = li.getColumnId();
                    }
                }
            }
            if (publishIds.size() > 0) {
                boolean rel =
                        MessageSenderUtil.publishContent(
                                new MessageStaticEO(siteId, columnId, publishIds.toArray(new Long[publishIds.size()])).setType(MessageEnum.UNPUBLISH.value()), 2);
            }

        }

        return getObject();
    }


    /**
     * @param ids
     * @param status
     * @return
     * @Description set stick status 置顶
     * @author Hewbing
     * @date 2015年10月9日 下午3:59:03
     */
    @RequestMapping("setTop")
    @ResponseBody
    public Object setTop(@RequestParam(value = "ids[]", required = false) Long[] ids, Integer status) {
        baseContentService.changeTopStatus(ids, status);
        List<BaseContentEO> list = baseContentService.getEntities(BaseContentEO.class, ids);
        List<Long> publishIds = new ArrayList<Long>();
        Long siteId = LoginPersonUtil.getSiteId();
        Long columnId = null;
        if (list != null) {
            for (BaseContentEO li : list) {
                if (li.getIsPublish() == 1) {
                    publishIds.add(li.getId());
                    columnId = li.getColumnId();
                }
            }
        }
        boolean rel =
                MessageSenderUtil.publishContent(
                        new MessageStaticEO(siteId, columnId, publishIds.toArray(new Long[publishIds.size()])).setType(MessageEnum.PUBLISH.value()), 1);
        for (int i = 0; i < ids.length; i++) {
            CacheHandler.saveOrUpdate(BaseContentEO.class, baseContentService.getEntity(BaseContentEO.class, ids[i]));

            ContentOptRecordEO recordEO = new ContentOptRecordEO();
            recordEO.setContentId(ids[i]);
            recordEO.setStatus(status);
            recordEO.setOptType(ContentOptRecordEO.Type.setTop.toString());
        }

        //增加记录
        ContentOptRecordUtil.saveOptRecord(ids,status, ContentOptRecordEO.Type.setTop);
        return getObject(rel);

    }

    /**
     * @param ids
     * @param status
     * @return
     * @Description 加热状态
     * @author Hewbing
     * @date 2015年10月9日 下午3:59:25
     */
    @RequestMapping("setHot")
    @ResponseBody
    public Object setHot(@RequestParam(value = "ids[]", required = false) Long[] ids, Integer status) {
        baseContentService.changeHotStatus(ids, status);
        List<BaseContentEO> list = baseContentService.getEntities(BaseContentEO.class, ids);
        List<Long> publishIds = new ArrayList<Long>();
        Long siteId = LoginPersonUtil.getSiteId();
        Long columnId = null;
        if (list != null) {
            for (BaseContentEO li : list) {
                if (li.getIsPublish() == 1) {
                    publishIds.add(li.getId());
                    columnId = li.getColumnId();
                }
            }
        }
        boolean rel =
                MessageSenderUtil.publishContent(
                        new MessageStaticEO(siteId, columnId, publishIds.toArray(new Long[publishIds.size()])).setType(MessageEnum.PUBLISH.value()), 1);
        for (int i = 0; i < ids.length; i++) {
            CacheHandler.saveOrUpdate(BaseContentEO.class, baseContentService.getEntity(BaseContentEO.class, ids[i]));
        }
        return getObject(rel);

    }

    /**
     * @param ids
     * @param siteId
     * @param columnId
     * @return
     * @Description 批量发布状态
     * @author Hewbing
     * @date 2015年9月14日 下午2:49:09
     */
    @RequestMapping("publishs")
    @ResponseBody
    public Object publish(@RequestParam(value = "ids[]", required = false) Long[] ids, Long siteId, Long columnId, Integer type) {
        String optType = "";
        if(ids!=null&&ids.length>1){
            optType = "批量";
        }

        List<BaseContentEO> list = baseContentService.getEntities(BaseContentEO.class, ids);
        if (list != null && list.size() > 0) {
            for (BaseContentEO eo : list) {
                eo.setIsPublish(2);//改为中间状态“发布中”
                if (eo.getPublishDate() == null) {
                    eo.setPublishDate(new Date());
                }

                String typeCodeName = getTypeCodeName(eo.getTypeCode());

                if (type != null && type == 1) {
                    SysLog.log(optType+"发布"+typeCodeName+" ：栏目（" + ColumnUtil.getColumnName(eo.getColumnId(), eo.getSiteId()) + "），标题（" + eo.getTitle()+"）", "BaseContentEO",
                            CmsLogEO.Operation.Update.toString());
                } else if (type != null && type == 0) {
                    SysLog.log(optType+"取消发布"+typeCodeName+" ：栏目（" + ColumnUtil.getColumnName(eo.getColumnId(), eo.getSiteId()) + "），标题（" + eo.getTitle()+"）", "BaseContentEO",
                            CmsLogEO.Operation.Update.toString());
                }

                //同步发布或取消发布引用新闻
                changeReferNewsPublish(eo.getId(),type);

            }
            baseContentService.saveEntities(list);
            CacheHandler.saveOrUpdate(BaseContentEO.class, list);
        }

        boolean rel = false;
        if (type == 1) {
            rel = MessageSenderUtil.publishContent(new MessageStaticEO(siteId, columnId, ids).setType(MessageEnum.PUBLISH.value()), 1);
        } else {
            rel = MessageSenderUtil.publishContent(new MessageStaticEO(siteId, columnId, ids).setType(MessageEnum.UNPUBLISH.value()), 2);
        }
        //增加记录
        ContentOptRecordUtil.saveOptRecord(ids,type, ContentOptRecordEO.Type.publish);
        return getObject(rel);

    }

    // 改变发布状态
    @RequestMapping("changePublish")
    @ResponseBody
    public Object changePublish(Long id,Integer isPublish) {
        BaseContentEO _bc = baseContentService.getEntity(BaseContentEO.class, id);

        String typeCodeName = getTypeCodeName(_bc.getTypeCode());

        // 判断是否有视频未发布
        if (BaseContentEO.TypeCode.articleNews.toString().equals(_bc.getTypeCode())) {// 文章新闻
            if ( Integer.valueOf(0).equals(_bc.getVideoStatus())) {
                return ajaxErr("文章内容中视频未转换完成！");
            }
        } else if (BaseContentEO.TypeCode.videoNews.toString().equals(_bc.getTypeCode())) {
            Map<String, Object> paramMap = new HashMap<String, Object>();
            paramMap.put("contentId", id);// 文章主键
            paramMap.put("recordStatus", AMockEntity.RecordStatus.Normal.toString());
            VideoNewsEO videoNewsEO = videoService.getEntity(VideoNewsEO.class, paramMap);
            if (null != videoNewsEO && videoNewsEO.getStatus().equals(0)) {
                return ajaxErr("视频未转换完成！");
            }
        }
        if(isPublish==null){
            isPublish = _bc.getIsPublish();
        }
        // 记录发布的操作人和操作时间
        _bc.setIsPublish(2);//改为中间状态“发布中”
        _bc.setUpdateUserId(LoginPersonUtil.getUserId());
        _bc.setUpdateDate(new Date());
        if (isPublish != 1) {
            if (_bc.getPublishDate() == null) {
                _bc.setPublishDate(new Date());
            }
        }
        baseContentService.updateEntity(_bc);
        CacheHandler.saveOrUpdate(BaseContentEO.class, _bc);
        if (isPublish == 1) {
            MessageSenderUtil.publishContent(new MessageStaticEO(_bc.getSiteId(), _bc.getColumnId(), new Long[]{id}).setType(MessageEnum.UNPUBLISH.value()),
                    2);
            SysLog.log("取消发布"+typeCodeName+" ：栏目（" + ColumnUtil.getColumnName(_bc.getColumnId(), _bc.getSiteId()) + "），标题（" + _bc.getTitle()+"）", "BaseContentEO",
                    CmsLogEO.Operation.Update.toString());

            //同步取消发布引用新闻
            changeReferNewsPublish(_bc.getId(),0);
        } else {
            if (_bc.getPublishDate() == null) {
                _bc.setPublishDate(new Date());
                baseContentService.saveEntity(_bc);
            }
            MessageSenderUtil
                    .publishContent(new MessageStaticEO(_bc.getSiteId(), _bc.getColumnId(), new Long[]{id}).setType(MessageEnum.PUBLISH.value()), 1);
            SysLog.log("发布"+typeCodeName+" ：栏目（" + ColumnUtil.getColumnName(_bc.getColumnId(), _bc.getSiteId()) + "），标题（" + _bc.getTitle()+"）", "BaseContentEO",
                    CmsLogEO.Operation.Update.toString());

            //同步发布引用新闻
            changeReferNewsPublish(_bc.getId(),1);
        }
        //增加记录
        ContentOptRecordUtil.saveOptRecord(new Long[]{id},isPublish == 0?1:0, ContentOptRecordEO.Type.publish);




        return getObject(isPublish);
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
        List<ContentReferRelationEO> relationEOS = contentReferRelationDao.getByCauseId(causeById,null,ContentReferRelationEO.TYPE.REFER.toString());
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


    // 改变置顶
    @RequestMapping("changeTop")
    @ResponseBody
    public Object changeTop(Long id) {
        BaseContentEO _bc = baseContentService.getEntity(BaseContentEO.class, id);

        String typeCodeName = getTypeCodeName(_bc.getTypeCode());

        Integer isTop = _bc.getIsTop();
        if (isTop == 1) {
            baseContentService.changeTopStatus(new Long[]{id}, 0);
            _bc.setIsTop(0);
        } else {
            baseContentService.changeTopStatus(new Long[]{id}, 1);
            _bc.setIsTop(1);
        }
        if (_bc.getIsPublish() == 1) {
            // MessageSender.sendMessage(new MessageStaticEO(_bc.getSiteId(),
            // _bc.getColumnId(), new
            // Long[]{id}).setType(MessageEnum.PUBLISH.value()));
            MessageSenderUtil
                    .publishContent(new MessageStaticEO(_bc.getSiteId(), _bc.getColumnId(), new Long[]{id}).setType(MessageEnum.PUBLISH.value()), 1);
        }
        CacheHandler.saveOrUpdate(BaseContentEO.class, _bc);
        //增加记录
        ContentOptRecordUtil.saveOptRecord(new Long[]{id},isTop == 0?1:0, ContentOptRecordEO.Type.setTop);

        //添加操作日志
        if (isTop == 1) {
            SysLog.log("取消置顶"+typeCodeName+" ：栏目（" + ColumnUtil.getColumnName(_bc.getColumnId(), _bc.getSiteId()) + "），标题（" + _bc.getTitle()+"）", "BaseContentEO",
                    CmsLogEO.Operation.Update.toString());
        } else {
            SysLog.log("置顶"+typeCodeName+" ：栏目（" + ColumnUtil.getColumnName(_bc.getColumnId(), _bc.getSiteId()) + "），标题（" + _bc.getTitle()+"）", "BaseContentEO",
                    CmsLogEO.Operation.Update.toString());
        }

        return getObject(isTop);
    }

    // 改变标题新闻
    @RequestMapping("changeTitle")
    @ResponseBody
    public Object changeTitle(Long id) {

        BaseContentEO _bc = baseContentService.getEntity(BaseContentEO.class, id);

        String typeCodeName = getTypeCodeName(_bc.getTypeCode());

        Integer isTitle = _bc.getIsTitle();
        if (isTitle == 1) {
            baseContentService.changeTitltStatus(new Long[]{id}, 0);
            _bc.setIsTitle(0);
        } else {
            baseContentService.changeTitltStatus(new Long[]{id}, 1);
            _bc.setIsTitle(1);
        }
        if (_bc.getIsPublish() == 1) {
            MessageSenderUtil
                    .publishContent(new MessageStaticEO(_bc.getSiteId(), _bc.getColumnId(), new Long[]{id}).setType(MessageEnum.PUBLISH.value()), 1);//创建索引
        }
        CacheHandler.saveOrUpdate(BaseContentEO.class, _bc);
        //增加记录
        ContentOptRecordUtil.saveOptRecord(new Long[]{id},isTitle == 0?1:0, ContentOptRecordEO.Type.setTitle);

        //添加操作日志
        if (isTitle == 1) {
            SysLog.log("取消设标"+typeCodeName+" ：栏目（" + ColumnUtil.getColumnName(_bc.getColumnId(), _bc.getSiteId()) + "），标题（" + _bc.getTitle()+"）", "BaseContentEO",
                    CmsLogEO.Operation.Update.toString());
        } else {
            SysLog.log("设标"+typeCodeName+" ：栏目（" + ColumnUtil.getColumnName(_bc.getColumnId(), _bc.getSiteId()) + "），标题（" + _bc.getTitle()+"）", "BaseContentEO",
                    CmsLogEO.Operation.Update.toString());
        }

        return getObject(isTitle);
    }

    // 改变加新
    @RequestMapping("changeNew")
    @ResponseBody
    public Object changeNew(Long id) {
        BaseContentEO _bc = baseContentService.getEntity(BaseContentEO.class, id);
        Integer isNew = _bc.getIsNew();

        String typeCodeName = getTypeCodeName(_bc.getTypeCode());

        if (isNew == 1) {
            baseContentService.changeNewStatus(new Long[]{id}, 0);
            _bc.setIsNew(0);
            ScheduleJobUtil.addOrDelScheduleJob("新闻定时取消标新", NewsCancelNewTaskImpl.class.getName(),
                    null, String.valueOf(id), 0);//删除定时任务
        } else {
            baseContentService.changeNewStatus(new Long[]{id}, 1);
            _bc.setIsNew(1);

            Calendar calendar = Calendar.getInstance();
            calendar.setTime(new Date());
            calendar.set(Calendar.DATE, calendar.get(Calendar.DATE) + 7);//结束日期增加一天
            Date date = calendar.getTime();
            ScheduleJobUtil.addOrDelScheduleJob("新闻定时取消标新", NewsCancelNewTaskImpl.class.getName(),
                    ScheduleJobUtil.dateToCronExpression(date), String.valueOf(id), 1);//添加定时任务
        }
        if (_bc.getIsPublish() == 1) {
            MessageSenderUtil
                    .publishContent(new MessageStaticEO(_bc.getSiteId(), _bc.getColumnId(), new Long[]{id}).setType(MessageEnum.PUBLISH.value()), 1);
        }
        CacheHandler.saveOrUpdate(BaseContentEO.class, _bc);
        //增加记录
        ContentOptRecordUtil.saveOptRecord(new Long[]{id},isNew == 0?1:0, ContentOptRecordEO.Type.setNew);

        //添加操作日志
        if (isNew == 1) {
            SysLog.log("取消加新"+typeCodeName+" ：栏目（" + ColumnUtil.getColumnName(_bc.getColumnId(), _bc.getSiteId()) + "），标题（" + _bc.getTitle()+"）", "BaseContentEO",
                    CmsLogEO.Operation.Update.toString());
        } else {
            SysLog.log("加新"+typeCodeName+" ：栏目（" + ColumnUtil.getColumnName(_bc.getColumnId(), _bc.getSiteId()) + "），标题（" + _bc.getTitle()+"）", "BaseContentEO",
                    CmsLogEO.Operation.Update.toString());
        }


        return getObject(isNew);
    }

    /**
     * @param ids
     * @param siteId
     * @param columnId
     * @return
     * @Description 取消发布
     * @author Hewbing
     * @date 2015年9月14日 下午5:02:00
     */
    @RequestMapping("cancelPublish")
    @ResponseBody
    public Object cancelPublish(@RequestParam(value = "ids[]", required = false) Long[] ids, Long siteId, Long columnId) {
        baseContentService.changePublish(new ContentPageVO(siteId, columnId, 2, ids, null));//先改为中间状态“发布中”
        for (int i = 0; i < ids.length; i++) {
            CacheHandler.saveOrUpdate(BaseContentEO.class, baseContentService.getEntity(BaseContentEO.class, ids[i]));
        }
        boolean rel = MessageSenderUtil.publishContent(new MessageStaticEO(siteId, columnId, ids).setType(MessageEnum.UNPUBLISH.value()), 2);

        //增加记录
        ContentOptRecordUtil.saveOptRecord(ids,0, ContentOptRecordEO.Type.publish);

        return getObject(rel);
    }

    /**
     * @param
     * @param content
     * @return
     * @Description 保存增加或修改的新闻
     * @author Hewbing
     * @date 2015年9月11日 下午4:10:30
     */
    @RequestMapping("saveArticleNews")
    @ResponseBody
    public Object saveArticleNews(BaseContentEO contentEO, String content, CopyReferVO copyReferVO) {
        // content=WordsSplitHolder.wordsRplc(articleText,content,
        // Type.SENSITIVE.toString());

        if (contentEO.getVideoStatus().equals(0) && contentEO.getIsPublish() == 1) {//
            return ajaxErr("文章中有视频未转换，不能进行发布操作！");
        }
        Integer status = contentEO.getIsPublish();// 保存下状态
        Boolean flag = false;
        if (AppUtil.isEmpty(contentEO.getId())) { //只有新增的时候才同步
            flag = true;
        }


        Long olId = contentEO.getId();
        Integer isPublished = 0;
        if (!AppUtil.isEmpty(contentEO.getId())) {
            isPublished = baseContentService.getEntity(BaseContentEO.class, olId).getIsPublish();
        }

        // 替换热词
        content = HotWordsCheckUtil.replaceAll(contentEO.getSiteId(), content);
        Long id = baseContentService.saveArticleNews(contentEO, content, null, null, null, null);

        if (contentEO.getVideoStatus().equals(0)) {
            // 转换视频
            VideoToMp4ConvertUtil.transfer(path, contentEO.getColumnId(), id);
        }

        boolean rel = false;
        if (status == 1) {
            rel =
                    MessageSenderUtil.publishContent(
                            new MessageStaticEO(contentEO.getSiteId(), contentEO.getColumnId(), new Long[]{id}).setType(MessageEnum.PUBLISH.value()), 1);

            SysLog.log("发布文章新闻 ：栏目（" + ColumnUtil.getColumnName(contentEO.getColumnId(), contentEO.getSiteId()) + "），标题（" + contentEO.getTitle()+"）", "BaseContentEO",
                    CmsLogEO.Operation.Update.toString());
        } else if (isPublished == 1) {
            rel =
                    MessageSenderUtil.publishContent(
                            new MessageStaticEO(contentEO.getSiteId(), contentEO.getColumnId(), new Long[]{id}).setType(MessageEnum.UNPUBLISH.value()), 2);
            SysLog.log("取消发布文章新闻 ：栏目（" + ColumnUtil.getColumnName(contentEO.getColumnId(), contentEO.getSiteId()) + "），标题（" + contentEO.getTitle()+"）", "BaseContentEO",
                    CmsLogEO.Operation.Update.toString());
        }
        if (contentEO.getIsTop() == 1 && contentEO.getTopValidDate() != null) {
            ScheduleJobUtil.addScheduleJob("新闻置顶有效期", NewsTopTaskImpl.class.getName(), ScheduleJobUtil.dateToCronExpression(contentEO.getTopValidDate()),
                    String.valueOf(id));
        }

        //新增同步数据
        if (flag) {
            if (copyReferVO == null) {
                copyReferVO = new CopyReferVO();
            }
            copyReferVO.setContentId(id.toString());
            String messges = baseContentService.synCloumnInfos(contentEO.getColumnId(), copyReferVO);
            MessageSenderUtil.publishCopyNews(messges);

            //同步添加引用新闻
            synAddReferNews(contentEO,status);
        }else{//修改
            String referMessges = baseContentService.synEditReferNews(contentEO,content,status);//同步修改引用新闻
            if(!AppUtil.isEmpty(referMessges)){
                if (status == 1) {//引用全部发布
                    MessageSenderUtil.publishCopyNews(referMessges);
                }else{//引用新闻全部取消发布
                    MessageSenderUtil.unPublishCopyNews(referMessges);
                }
            }

        }

        //设置定时发布
        Integer isJob = 0;
        if (contentEO.getIsJob() == 1 && contentEO.getJobIssueDate() != null) {
            isJob = 1;
            ScheduleJobUtil.addOrDelScheduleJob("新闻定时发布日期", NewsIssueTaskImpl.class.getName(), isJob == 0 ? null : ScheduleJobUtil.dateToCronExpression(contentEO.getJobIssueDate()), String.valueOf(id), isJob);
        }
        //增加记录
        ContentOptRecordUtil.saveOptRecord(new Long[]{contentEO.getId()},status, flag?ContentOptRecordEO.Type.add:ContentOptRecordEO.Type.edit);
        return getObject(id);
    }

    /**
     * 同步添加引用新闻
     * @param baseContentEO
     * @param isPublish
     */
    private void synAddReferNews(BaseContentEO baseContentEO,Integer isPublish){
//        isPublish = 0;//全部设置成未发布状态
        Long columnId = baseContentEO.getColumnId();
        ColumnMgrEO columnMgrEO = columnConfigService.getById(columnId);

        if(columnMgrEO!=null){
            String columnIdStr = columnMgrEO.getIndicatorId()+"_"+columnMgrEO.getSiteId();
            if(!AppUtil.isEmpty(columnMgrEO.getReferColumnIds())
                    ||!AppUtil.isEmpty(columnMgrEO.getReferOrganCatIds())){
                Map<String,String> resultMap = baseContentService.getReferColumnCats(columnMgrEO.getReferColumnIds(),columnMgrEO.getReferOrganCatIds(),columnIdStr);
                String referColumnIds = resultMap.get("referColumnIds");
                String referOrganCatIds = resultMap.get("referOrganCatIds");

                CopyReferVO copyReferVO = new CopyReferVO();
                copyReferVO.setIsColumnOpt(1);//栏目之间的引用
                copyReferVO.setPublicSiteId(2653861l);//信息公开站点id
                copyReferVO.setContentId(baseContentEO.getId()+"");
                copyReferVO.setSource(1l);//内容管理
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
                    StringBuffer stringBuffer = new StringBuffer(isPublish);
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
     * @param request
     * @param response
     * @param
     * @return
     * @throws UnsupportedEncodingException
     * @Description 秀秀上传图片
     * @author Hewbing
     * @date 2015年9月14日 下午2:49:32
     */
    @RequestMapping("uploadAttachment")
    @ResponseBody
    public Object uploadAttachment(HttpServletRequest request, HttpServletResponse response, MultipartFile Filedata) throws UnsupportedEncodingException {
        /* 还需旧图片垃圾处理 */
        MongoFileVO mongvo = mongoDbFileServer.uploadMultipartFile(Filedata, null);
        return mongvo.getFileName();
    }

    /**
     * @param request
     * @param response
     * @Description 编辑器上传文件
     * @author Hewbing
     * @date 2015年9月15日 下午3:52:08
     */
    @RequestMapping("upload")
    public void uploadFiles(HttpServletRequest request, HttpServletResponse response) {
        EditorUploadUtil.uploadByKindEditor(SystemCodes.contentMgr, request, response);
    }

    @RequestMapping("uploadNew")
    public void uploadNew(HttpServletRequest request, HttpServletResponse response) {
        EditorUploadUtil.uploadByKindEditorNew(SystemCodes.contentMgr, request, response);
    }

    @RequestMapping("getColumnConfig")
    @ResponseBody
    public Object getColumnConfig(Long id) {
        return getObject(getEntity(ColumnMgrEO.class, id));
    }

    /**
     * @param contentId
     * @param imageLink
     * @return
     * @Description 设置缩略图
     * @author Hewbing
     * @date 2015年10月9日 下午3:44:44
     */
    @RequestMapping("setImgLink")
    @ResponseBody
    public Object setImgLink(Long contentId, String imageLink) {
        int i = baseContentService.changeImg(contentId, imageLink);
        CacheHandler.saveOrUpdate(BaseContentEO.class, baseContentService.getEntity(BaseContentEO.class, contentId));
        return getObject(i);

    }

    /**
     * @param ids       栏目ID
     * @param pIds      站点ID
     * @param contentId 新闻内容ID
     * @return
     * @Description 新闻复制
     * @author Hewbing
     * @date 2015年10月15日 下午4:25:26
     */
    @RequestMapping("copyTo")
    @ResponseBody
    public Object copyTo(@RequestParam("ids[]") String[] ids, @RequestParam("pIds[]") String[] pIds, Long contentId) {
        if (ids.length != pIds.length) {
            return ajaxErr("参数报错，请重新发送");
        }
        String messges = baseContentService.saveCopy(ids, contentId, null, "CONTENT", null,0);
        if (!"false".equals(messges)) {
            MessageSenderUtil.publishCopyNews(messges);
            //增加记录
            ContentOptRecordUtil.saveOptRecord(new Long[]{contentId},-1, ContentOptRecordEO.Type.copy);
            return getObject();
        } else {
            return ajaxErr("新闻复制失败");
        }
        //增加记录
    }

    /**
     * 下载
     *
     * @param request
     * @param response
     * @throws Exception
     */
    @RequestMapping("downContents")
    public void downContents(HttpServletRequest request, HttpServletResponse response, ContentPageVO pageVO) throws Exception {
        String[] headers = new String[]{"标题", "发布时间", "来源", "作者"};
//        //查询此单位下所有部门
        List<Object[]> values = null;
        if (pageVO.getColumnId() == null) {
            throw new BaseRunTimeException(TipsMode.Message.toString(), "栏目id不能为空！");
        }

        List<BaseContentEO> contents = new ArrayList<BaseContentEO>();
        ColumnMgrEO columnEO = getEntity(ColumnMgrEO.class,pageVO.getColumnId());
        if (columnEO.getIsParent() == 1) {
            //取出子节点,可能不止一层
            StringBuffer columnIds = new StringBuffer();
            baseContentController.getAllColumn(columnIds,pageVO.getColumnId());

            pageVO.setColumnId(null);
            pageVO.setColumnIds(columnIds.toString());
            contents.addAll(baseContentService.getList(pageVO));
        }else {
            //如果是叶子节点
            contents.addAll(baseContentService.getList(pageVO));
        }

        SimpleDateFormat simple = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        if (contents != null && contents.size() > 0) {
            values = new ArrayList<Object[]>();
            for (BaseContentEO content : contents) {
                try {
                    Object[] objects = new Object[4];
                    objects[0] = content.getTitle();
                    objects[1] = simple.format(content.getPublishDate());
                    objects[2] = content.getResources();
                    objects[3] = content.getAuthor();
                    values.add(objects);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        PoiExcelUtil.exportExcel("新闻统计", "新闻统计", "xls", headers, values, response);
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
            typeCodeName = "信息公开";
        }
        return typeCodeName;
    }
}
