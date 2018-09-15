package cn.lonsun.content.controller;

import cn.lonsun.activemq.MessageSenderUtil;
import cn.lonsun.cache.client.CacheHandler;
import cn.lonsun.common.util.AppUtil;
import cn.lonsun.common.util.FileUtil;
import cn.lonsun.content.internal.entity.BaseContentEO;
import cn.lonsun.content.internal.service.IBaseContentService;
import cn.lonsun.content.internal.service.IContentPicService;
import cn.lonsun.content.messageBoard.controller.vo.MessageBoardEditVO;
import cn.lonsun.content.messageBoard.service.IMessageBoardService;
import cn.lonsun.content.optrecord.entity.ContentOptRecordEO;
import cn.lonsun.content.optrecord.util.ContentOptRecordUtil;
import cn.lonsun.content.vo.*;
import cn.lonsun.core.base.controller.BaseController;
import cn.lonsun.core.base.util.StringUtils;
import cn.lonsun.core.exception.BaseRunTimeException;
import cn.lonsun.core.util.Pagination;
import cn.lonsun.core.util.ResultVO;
import cn.lonsun.core.util.TipsMode;
import cn.lonsun.indicator.internal.entity.IndicatorEO;
import cn.lonsun.message.internal.entity.MessageEnum;
import cn.lonsun.message.internal.entity.MessageStaticEO;
import cn.lonsun.mongodb.base.IMongoDbFileServer;
import cn.lonsun.mongodb.entity.ContentMongoEO;
import cn.lonsun.mongodb.service.ContentMongoServiceImpl;
import cn.lonsun.mongodb.vo.MongoFileVO;
import cn.lonsun.monitor.words.internal.entity.WordsEasyerrEO;
import cn.lonsun.monitor.words.internal.entity.WordsSensitiveEO;
import cn.lonsun.monitor.words.internal.util.Type;
import cn.lonsun.monitor.words.internal.util.WordsSplitHolder;
import cn.lonsun.rbac.internal.entity.OrganEO;
import cn.lonsun.rbac.internal.service.IOrganService;
import cn.lonsun.site.contentModel.vo.ColumnTypeConfigVO;
import cn.lonsun.site.site.SiteMainController;
import cn.lonsun.site.site.internal.entity.ColumnMgrEO;
import cn.lonsun.site.site.internal.entity.WaterMarkConfigEO;
import cn.lonsun.site.site.internal.service.IColumnConfigService;
import cn.lonsun.site.site.internal.service.IWaterMarkConfigService;
import cn.lonsun.site.words.internal.entity.WordsHotConfEO;
import cn.lonsun.site.words.internal.vo.CheckWordsVO;
import cn.lonsun.solr.SolrFactory;
import cn.lonsun.system.datadictionary.vo.DataDictVO;
import cn.lonsun.system.filecenter.internal.entity.FileCenterEO;
import cn.lonsun.system.role.internal.service.ISiteRightsService;
import cn.lonsun.system.systemlog.internal.entity.CmsLogEO;
import cn.lonsun.util.*;
import cn.lonsun.weibo.entity.vo.SinaWeiboContentVO;
import cn.lonsun.weibo.service.ISinaWeiboService;
import cn.lonsun.weibo.service.ITencentWeiboService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.task.TaskExecutor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

import static cn.lonsun.cache.client.CacheHandler.getEntity;
import static cn.lonsun.util.ModelConfigUtil.getCongfigVO;

/**
 * @author Hewbing
 * @ClassName: ContentController
 * @Description: 新闻内容管理控制器
 * @date 2015年9月14日 下午2:50:34
 */
@Controller
@RequestMapping(value = "content")
public class BaseContentController extends BaseController {

    @Autowired
    private IBaseContentService baseContentService;

    @Autowired
    private IWaterMarkConfigService waterMarkConfigService;

    @Autowired
    private IContentPicService contentPicService;

    @Autowired
    private IMongoDbFileServer mongoDbFileServer;

    @Autowired
    private ContentMongoServiceImpl contentMongoService;

    @Autowired
    private ISiteRightsService siteRightsService;

    @Autowired
    private TaskExecutor taskExecutor;

    @Autowired
    private ISinaWeiboService sinaWeiboService;

    @Autowired
    private ITencentWeiboService tencentWeiboService;

    @Autowired
    private IOrganService organService;

    @Autowired
    private IMessageBoardService messageBoardService;

    @Autowired
    private IColumnConfigService columnConfigService;

    @Autowired
    private SiteMainController siteMainController;

    @RequestMapping("index")
    public String contentList(ModelMap map, HttpServletRequest request) {
        String columnTypeCode = request.getParameter("columnTypeCode");
        map.put("columnTypeCode", columnTypeCode);
        return "/content/index";
    }

    /**
     * @param pageVO
     * @return
     * @Description 获取新闻分页
     * @author Hewbing
     * @date 2015年9月14日 下午2:48:11
     */
    @RequestMapping("getPage")
    @ResponseBody
    public Object getPage(ContentPageVO pageVO) {
        return getObject(baseContentService.getPage(pageVO));
    }

    /**
     * @param pageVO
     * @return
     * @Description 点击父栏目获取新闻分页
     * @author Hewbing
     * @date 2015年9月14日 下午2:48:11
     */
    @RequestMapping("getPageByParentId")
    @ResponseBody
    public Object getPageByParentId(ContentPageVO pageVO) {
        //存放所有叶子节点
        StringBuffer columnIds = new StringBuffer();

        //父节点全部从columnIds中拿,如果没有,则取父节点
        if (AppUtil.isEmpty(pageVO.getColumnIds())) {
            ColumnMgrEO columnEO = getEntity(ColumnMgrEO.class,pageVO.getColumnId());
            if (columnEO.getIsParent() == 1) {
                getAllColumn(columnIds,pageVO.getColumnId());
                return getObject(baseContentService.getPageByParentId(columnIds.toString(),pageVO));
            }else {
                //如果是叶子节点
                return getObject(baseContentService.getPage(pageVO));
            }
        }else {
            Long[] columnArrays = StringUtils.getArrayWithLong(pageVO.getColumnIds(),",");
            ColumnMgrEO columnMgrEO = null;
            for (Long columnId:columnArrays) {
                columnMgrEO = getEntity(ColumnMgrEO.class,columnId);
                if (columnMgrEO.getIsParent() == 1) {
                    getAllColumn(columnIds,columnId);
                }else {
                    //必须是articalNews 才加入
                    if ("articleNews".equals(columnMgrEO.getColumnTypeCode())) {
                        if (columnIds.length() == 0) {
                            columnIds.append(columnMgrEO.getIndicatorId());
                        } else {
                            columnIds.append(",").append(columnMgrEO.getIndicatorId());
                        }
                    }
                }
            }
            return getObject(baseContentService.getPageByParentId(columnIds.toString(),pageVO));
        }
    }

    /**
     * 获取所有叶子节点
     */
    public void getAllColumn(StringBuffer columnIds,Long columnId) {
        Object obj = siteMainController.getColumnTreeBySite(columnId);
        List<ColumnMgrEO> columnMgrEOs = null;
        if (obj instanceof ResultVO) {
            ResultVO obj1 = (ResultVO)obj;
            columnMgrEOs = (List<ColumnMgrEO>)obj1.getData();
        }else {
            columnMgrEOs = (List<ColumnMgrEO>)obj;
        }
        for (ColumnMgrEO columnMgrEO : columnMgrEOs) {
            if (columnMgrEO.getIsParent() == 1) {
                getAllColumn(columnIds,columnMgrEO.getIndicatorId());
            }else {
                //必须是articalNews 才加入
                if ("articleNews".equals(columnMgrEO.getColumnTypeCode())) {
                    if (columnIds.length() == 0) {
                        columnIds.append(columnMgrEO.getIndicatorId());
                    } else {
                        columnIds.append(",").append(columnMgrEO.getIndicatorId());
                    }
                }
            }
        }
    }

    /**
     * 查询通过父栏目获取子栏目
     * @param pageVO
     * @return
     */
    @RequestMapping("getColumnMap")
    @ResponseBody
    public Object getColumnMap(ContentPageVO pageVO) {
        Map<String,String> tempMap = null;
        List<Map<String,String>> result = new ArrayList<Map<String,String>>();
        Long indicatorId = pageVO.getColumnId();
        ColumnMgrEO eo = getEntity(ColumnMgrEO.class, indicatorId);
        //如果是父节点
        if (eo.getIsParent() == 1) {
            //取出子节点
            ResultVO obj = (ResultVO)siteMainController.getColumnTreeBySite(indicatorId);
            List<ColumnMgrEO> columnMgrEOs = (List<ColumnMgrEO>)obj.getData();
//            tempMap = new HashMap<String,String>();
//            tempMap.put("value",indicatorId.toString());
//            tempMap.put("name","所有栏目");
//            result.add(tempMap);
            for (ColumnMgrEO columnMgrEO : columnMgrEOs) {
                //必须是articalNews 才加入
                if ("articleNews".equals(columnMgrEO.getColumnTypeCode())) {
                    tempMap = new HashMap<String,String>();
                    tempMap.put("value",columnMgrEO.getIndicatorId().toString());
                    tempMap.put("name",columnMgrEO.getName());
                    result.add(tempMap);
                }
            }
        }else {
            tempMap = new HashMap<String,String>();
            tempMap.put("value",eo.getIndicatorId().toString());
            tempMap.put("name",eo.getName());
            result.add(tempMap);
        }

        return getObject(result);
    }

    /*
     * @ResponseBody
     *
     * @RequestMapping("getLastNextVOForPublish") public Object
     * getLastNextVOForPublish(Long columnId, Long contentId) { return
     * baseContentService.getLastNextVOForPublish(columnId, contentId); }
     */

    /**
     * @param ids
     * @return
     * @Description 删除新闻
     * @author Hewbing
     * @date 2015年9月14日 下午2:48:53
     */
    @RequestMapping("delete")
    @ResponseBody
    public Object delete(@RequestParam("ids[]") Long[] ids) {
        String type = "删除";
        if(ids!=null&&ids.length>1){
            type = "批量删除";
        }

        List<BaseContentEO> list = baseContentService.getEntities(BaseContentEO.class, ids);
        baseContentService.delContent(ids);
        List<Long> publishIds = new ArrayList<Long>();
        Long siteId = LoginPersonUtil.getSiteId();
        Long columnId = null;
        if (list != null) {
            for (BaseContentEO li : list) {
                String typeCodeName = "内容";
                if(BaseContentEO.TypeCode.articleNews.toString().equals(li.getTypeCode())){
                    typeCodeName = "文章新闻";
                }else if(BaseContentEO.TypeCode.pictureNews.toString().equals(li.getTypeCode())){
                    typeCodeName = "图片新闻";
                }else if(BaseContentEO.TypeCode.videoNews.toString().equals(li.getTypeCode())){
                    typeCodeName = "视频新闻";
                }else if(BaseContentEO.TypeCode.linksMgr.toString().equals(li.getTypeCode())){
                    typeCodeName = "链接";
                }

                if (li.getIsPublish() == 1) {
                    publishIds.add(li.getId());
                    columnId = li.getColumnId();

                }
                SysLog.log(type + typeCodeName+" ：栏目（" + ColumnUtil.getColumnName(li.getColumnId(), li.getSiteId())
                        + "），标题（"+li.getTitle()+"）", "BaseContentEO", CmsLogEO.Operation.Update.toString());
            }
        }
        boolean rel = true;
        if (null != columnId) {
            rel =
                    MessageSenderUtil.publishContent(
                            new MessageStaticEO(siteId, columnId, publishIds.toArray(new Long[publishIds.size()])).setType(MessageEnum.UNPUBLISH.value()), 2);
        }
        return getObject(rel);
    }

    /**
     * @param ids
     * @param status
     * @return
     * @Description 置顶状态
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
        }

        //增加记录
        ContentOptRecordUtil.saveOptRecord(ids, status, ContentOptRecordEO.Type.setTop);
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
     * @Description 批量发布新闻
     * @author Hewbing
     * @date 2015年9月14日 下午2:49:09
     */
    @RequestMapping("publish")
    @ResponseBody
    public Object publish(@RequestParam(value = "ids[]", required = false) Long[] ids, Long siteId, Long columnId) {
        baseContentService.changePublish(new ContentPageVO(siteId, columnId, 2, ids, null));//设置成"发布中"中间状态
        for (int i = 0; i < ids.length; i++) {
            CacheHandler.saveOrUpdate(BaseContentEO.class, baseContentService.getEntity(BaseContentEO.class, ids[i]));
        }
        boolean rel = MessageSenderUtil.publishContent(new MessageStaticEO(siteId, columnId, ids).setType(MessageEnum.PUBLISH.value()), 1);
        //增加记录
        ContentOptRecordUtil.saveOptRecord(ids, 1, ContentOptRecordEO.Type.publish);
        return getObject(rel);
    }

    //取消发布
    @RequestMapping("unPublish")
    @ResponseBody
    public Object unPublish(Long id) {
        BaseContentEO _bc = baseContentService.getEntity(BaseContentEO.class, id);
        baseContentService.changePublish(new ContentPageVO(null, null, 0, new Long[]{id}, null));
        MessageSenderUtil.publishContent(new MessageStaticEO(_bc.getSiteId(), _bc.getColumnId(), new Long[]{id}).setType(MessageEnum.UNPUBLISH.value()),
                2);
        _bc.setIsPublish(2);
        SysLog.log("取消发布内容 >> ID：" + id + ",标题：" + _bc.getTitle(),
                "BaseContentEO", CmsLogEO.Operation.Update.toString());
        CacheHandler.saveOrUpdate(BaseContentEO.class, _bc);
        //增加记录
        ContentOptRecordUtil.saveOptRecord(new Long[]{id}, 0, ContentOptRecordEO.Type.publish);
        return getObject(0);
    }

    // 改变发布状态
    @RequestMapping("changePublish")
    @ResponseBody
    public Object changePublish(Long id) {
        BaseContentEO _bc = baseContentService.getEntity(BaseContentEO.class, id);
        Integer isPublish = _bc.getIsPublish();
        //记录发布的操作人和操作时间
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
            SysLog.log("取消发布内容 >> ID：" + id + ",标题：" + _bc.getTitle(),
                    "BaseContentEO", CmsLogEO.Operation.Update.toString());
        } else {
            MessageSenderUtil
                    .publishContent(new MessageStaticEO(_bc.getSiteId(), _bc.getColumnId(), new Long[]{id}).setType(MessageEnum.PUBLISH.value()), 1);
            SysLog.log("发布内容 >> ID：" + id + ",标题：" + _bc.getTitle(),
                    "BaseContentEO", CmsLogEO.Operation.Update.toString());
        }
        //增加记录
        ContentOptRecordUtil.saveOptRecord(new Long[]{id}, isPublish == 1 ? 0 : 1, ContentOptRecordEO.Type.publish);
        return getObject(isPublish);


    }

    // 改变置顶
    @RequestMapping("changeTop")
    @ResponseBody
    public Object changeTop(Long id) {
        BaseContentEO _bc = baseContentService.getEntity(BaseContentEO.class, id);
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
        ContentOptRecordUtil.saveOptRecord(new Long[]{id}, isTop == 1 ? 0 : 1, ContentOptRecordEO.Type.setTop);
        return getObject(isTop);
    }

    // 改变标题新闻
    @RequestMapping("changeTitle")
    @ResponseBody
    public Object changeTitle(Long id) {
        BaseContentEO _bc = baseContentService.getEntity(BaseContentEO.class, id);
        Integer isTitle = _bc.getIsTitle();
        if (isTitle == 1) {
            baseContentService.changeTitltStatus(new Long[]{id}, 0);
            _bc.setIsTilt(0);
        } else {
            baseContentService.changeTitltStatus(new Long[]{id}, 1);
            _bc.setIsTilt(1);
        }
        if (_bc.getIsPublish() == 1) {
            MessageSenderUtil
                    .publishContent(new MessageStaticEO(_bc.getSiteId(), _bc.getColumnId(), new Long[]{id}).setType(MessageEnum.PUBLISH.value()), 1);
        }
        CacheHandler.saveOrUpdate(BaseContentEO.class, _bc);
        //增加记录
        ContentOptRecordUtil.saveOptRecord(new Long[]{id}, isTitle == 1 ? 0 : 1, ContentOptRecordEO.Type.setTitle);
        return getObject(isTitle);
    }

    // 改变加新
    @RequestMapping("changeNew")
    @ResponseBody
    public Object changeNew(Long id) {
        BaseContentEO _bc = baseContentService.getEntity(BaseContentEO.class, id);
        Integer isNew = _bc.getIsNew();
        if (isNew == 1) {
            baseContentService.changeNewStatus(new Long[]{id}, 0);
            _bc.setIsNew(0);
        } else {
            baseContentService.changeNewStatus(new Long[]{id}, 1);
            _bc.setIsNew(1);
        }
        if (_bc.getIsPublish() == 1) {
            MessageSenderUtil
                    .publishContent(new MessageStaticEO(_bc.getSiteId(), _bc.getColumnId(), new Long[]{id}).setType(MessageEnum.PUBLISH.value()), 1);
        }
        CacheHandler.saveOrUpdate(BaseContentEO.class, _bc);
        //增加记录
        ContentOptRecordUtil.saveOptRecord(new Long[]{id}, isNew == 1 ? 0 : 1, ContentOptRecordEO.Type.setNew);
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
        baseContentService.changePublish(new ContentPageVO(siteId, columnId, 2, ids, null));//改为中间状态"发布中"
        for (int i = 0; i < ids.length; i++) {
            CacheHandler.saveOrUpdate(BaseContentEO.class, baseContentService.getEntity(BaseContentEO.class, ids[i]));
        }
        boolean rel = MessageSenderUtil.publishContent(new MessageStaticEO(siteId, columnId, ids).setType(MessageEnum.UNPUBLISH.value()), 2);
        //增加记录
        ContentOptRecordUtil.saveOptRecord(ids, 0, ContentOptRecordEO.Type.publish);
        return getObject(rel);
    }

    /**
     * @param request
     * @param response
     * @return
     * @throws UnsupportedEncodingException
     * @Description 秀秀上传缩略图
     * @author Hewbing
     * @date 2015年9月14日 下午2:49:32
     */
    @RequestMapping("uploadAttachment")
    @ResponseBody
    public Object uploadAttachment(HttpServletRequest request, HttpServletResponse response, MultipartFile Filedata, Long siteId, Long columnId,
                                   Long contentId, String imgLink) throws UnsupportedEncodingException {
        if (null == siteId) {
            return ajaxErr("站点ID不能为空");
        }
//        if (null == columnId) {
//            return ajaxErr("栏目ID不能为空");
//        }
        if (columnId == null) {
            MongoFileVO mongvo = FileUploadUtil.uploadUtil(Filedata, FileCenterEO.Type.Image.toString(), FileCenterEO.Code.ThumbUpload.toString(), siteId, null, null, "缩略图", request);
            FileUploadUtil.setStatus(imgLink, 0);
            return mongvo.getFileName();
        }

        //上传类型，区分是编辑器上传还是缩略图上传
        String mType = request.getParameter("mType");

        String fileName = Filedata.getOriginalFilename();
        String fileSuffix = FileUtil.getType(fileName);
        System.out.println(request.getRequestURL());
        byte[] by = null;
        InputStream in = null;
        try {
            in = Filedata.getInputStream();
        } catch (IOException e) {
            e.printStackTrace();
        }
        WaterMarkConfigEO eo = waterMarkConfigService.getConfigBySiteId(siteId);
        ColumnTypeConfigVO modelVO = ModelConfigUtil.getCongfigVO(columnId, siteId);
        int width = 0;
        int heigth = 0;

        if (null != modelVO) {
            if (AppUtil.isEmpty(mType)) {
                if (!AppUtil.isEmpty(modelVO.getPicWidth())) {
                    width = modelVO.getPicWidth();
                }
                if (!AppUtil.isEmpty(modelVO.getPicHeight())) {
                    heigth = modelVO.getPicHeight();
                }
            } else {
                if (!AppUtil.isEmpty(modelVO.getContentWidth())) {
                    width = modelVO.getContentWidth();
                }
            }
        }

        byte[] b = null;
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        if (eo != null && eo.getEnableStatus() == 1 && modelVO != null && modelVO.getIsWater() == 1) {
            /*by = WaterMarkUtil.createWaterMark(in, siteId, fileSuffix);
            InputStream inNew = new ByteArrayInputStream(by);
            ImgHander.ImgTrans(inNew, width, heigth, baos, fileSuffix);*/
            ImgHander.ImgTrans(in, width, heigth, baos, fileSuffix);
            in = new ByteArrayInputStream(baos.toByteArray());
            b = WaterMarkUtil.createWaterMark(in, siteId, fileSuffix);
        } else {
            ImgHander.ImgTrans(in, width, heigth, baos, fileSuffix);
            b = baos.toByteArray();
        }

        // MongoFileVO mongvo =
        // FileUploadUtil.uploadUtil(Filedata,
        // FileCenterEO.Type.Image.toString(),
        // FileCenterEO.Code.ThumbUpload.toString(), siteId, columnId,
        // contentId,
        // "新闻缩略图", request);
        // MongoFileVO mongvo = mongoDbFileServer.uploadMultipartFile(Filedata,
        // null);

        MongoFileVO mongvo =
                FileUploadUtil.uploadUtil(b, fileName, FileCenterEO.Type.Image.toString(), FileCenterEO.Code.ThumbUpload.toString(), siteId, columnId,
                        contentId, "新闻缩略图", request);
        FileUploadUtil.setStatus(imgLink, 0);
        return mongvo.getFileName();
    }

    @RequestMapping("getColumnConfig")
    @ResponseBody
    public Object getColumnConfig(Long id) {
        return getObject(getEntity(ColumnMgrEO.class, id));
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
    @RequestMapping("saveCopyNews")
    @ResponseBody
    public Object saveCopyNews(@RequestParam("ids[]") String[] ids, @RequestParam("pIds[]") String[] pIds, Long contentId, String synColumnIsPublishs) {
        if (ids.length != pIds.length) {
            return ajaxErr("参数报错，请重新发送");
        }
        String messges = baseContentService.saveCopy(ids, contentId, null, "CONTENT", synColumnIsPublishs,0);
        if (!"false".equals(messges)) {
            MessageSenderUtil.publishCopyNews(messges);
            ContentOptRecordUtil.saveOptRecord(new Long[]{contentId}, -1, ContentOptRecordEO.Type.copy);
            return getObject();
        } else {
            return ajaxErr("新闻复制失败");
        }
        //增加记录
    }

    /**
     * @param opr
     * @param oprId
     * @param oprSort
     * @param pageVO
     * @return Object 返回类型
     * @throws
     * @Title: contentSort
     * @Description: 排序
     */
    @RequestMapping("contentSort")
    @ResponseBody
    public Object contentSort(String opr, Long oprId, Long oprSort, ContentPageVO pageVO) {
        Long qId = baseContentService.contentSort(opr, oprId, oprSort, pageVO);
        List<BaseContentEO> list = baseContentService.getEntities(BaseContentEO.class, new Long[]{oprId, qId});

        List<Long> publishIdList = new ArrayList<Long>();
        if (null != list && !list.isEmpty()) {
            for(BaseContentEO baseContent : list){
                if(Integer.valueOf(1).equals(baseContent.getIsPublish())){
                    publishIdList.add(baseContent.getId());
                }
            }
            CacheHandler.saveOrUpdate(BaseContentEO.class, list);
            if(publishIdList.size() > 0){
                BaseContentEO eo = list.get(0);
                Long[] idArray = new Long[publishIdList.size()];
                publishIdList.toArray(idArray);
                MessageSenderUtil.publishContent(
                        new MessageStaticEO(eo.getSiteId(), eo.getColumnId(), idArray).setType(MessageEnum.PUBLISH.value()), 1);
            }

        }
        return getObject();
    }

    /**
     * 新闻复制引用
     *
     * @param source
     * @param type
     * @param map
     * @return
     * @author fangtinghua
     */
    @RequestMapping("toCopyRefer")
    public String toCopyRefer(Long[] contentId, Long source, Long type, ModelMap map,
                              String synColumnIds, String synOrganCatIds, String synColumnIsPublishs,
                              String synOrganIsPublishs, String synMsgCatIds, String synWeixin) {
        BaseContentEO eo = getEntity(BaseContentEO.class, contentId[0]);
        map.put("eo", eo);// 文章id
        map.put("source", source);// 来源，内容管理、信息公开
        map.put("type", type);// 类型，复制、引用
        map.put("synColumnIds", synColumnIds);
        map.put("synColumnIsPublishs", synColumnIsPublishs);
        map.put("synOrganCatIds", synOrganCatIds);
        map.put("synOrganIsPublishs", synOrganIsPublishs);
        map.put("synMsgCatIds", synMsgCatIds);
        map.put("synWeixin", synWeixin);
        return "/content/copy_refer_page";
    }

    /**
     * 新闻复制引用
     *
     * @param source
     * @param type
     * @param map
     * @return
     * @author fangtinghua
     */
    @RequestMapping("toMove")
    public String toMove(Long[] contentId, Long source, Long type, ModelMap map) {
        BaseContentEO eo = getEntity(BaseContentEO.class, contentId[0]);
        map.put("eo", eo);// 文章id
        map.put("source", source);// 来源，内容管理、信息公开
        map.put("type", type);// 类型，复制、引用
        return "/content/move_page";
    }

    /**
     * 新闻复制引用
     *
     * @param copyReferVO
     * @return
     * @author fangtinghua
     */
    @ResponseBody
    @RequestMapping("copyRefer")
    public Object copyRefer(CopyReferVO copyReferVO) {

        String messges = baseContentService.copyRefer(copyReferVO);
        MessageSenderUtil.publishCopyNews(messges);
        return getObject();
    }

    /**
     * 新闻移动
     *
     * @param copyReferVO
     * @return
     * @author fangtinghua
     */
    @ResponseBody
    @RequestMapping("move")
    public Object move(CopyReferVO copyReferVO) {

        // 删除原栏目详细页以及索引
        int index = 0;
        Long siteId = null;// 站点id
        Long columnId = null; //栏目id
        List<Long> longList = new ArrayList<Long>();
        String[] contentIds = copyReferVO.getContentId().replace("[", "").replace("]", "").split(",");// 新闻id
        for (String id : contentIds) {
            Long contentId = Long.parseLong(id);
            longList.add(contentId);
            BaseContentEO eo = baseContentService.getEntity(BaseContentEO.class, contentId);
            eo.setIsPublish(2);// 设置为“发布中”中间状态
            baseContentService.updateEntity(eo);
            CacheHandler.saveOrUpdate(BaseContentEO.class, eo);
            if (index++ == 0) {
                siteId = eo.getSiteId();
                columnId = eo.getColumnId();
            }
        }
        try {
            // 删除旧栏目索引
            SolrFactory.deleteIndex(Arrays.asList(contentIds));
            // 移动比较特殊，原来的旧文章无需生成文章页只需生成栏目页即可，因此不需传文章id
            String messges = baseContentService.moveNews(copyReferVO);
            // 生成旧栏目栏目页
            MessageSenderUtil.publishContent(new MessageStaticEO(siteId, columnId, new Long[]{}).setType(MessageEnum.PUBLISH.value()), 1);
            // 生成新栏目详细页及索引
            MessageSenderUtil.publishCopyNews(messges);
        } catch (Throwable e) {
            e.printStackTrace();
            return ajaxErr("索引删除失败！");
        }


        return getObject();
    }

    /**
     * @param id
     * @param map
     * @return String 返回类型
     * @throws
     * @Title: copyNews
     * @Description: 新闻复制页
     */
    @RequestMapping("copyNews")
    public String copyNews(Long id, ModelMap map) {
        BaseContentEO _bc = baseContentService.getEntity(BaseContentEO.class, id);
        map.put("OPR_EO", _bc);
        return "/content/copy_news_page";
    }

    /**
     * @return String 返回类型
     * @throws
     * @Title: normalUploadThumb
     * @Description: 普通上传缩略图
     */
    @RequestMapping("normalUploadThumb")
    @ResponseBody
    public Object normalUploadThumb(MultipartFile Filedata, Long siteId, Long columnId, Long contentId, String imgLink, String sessionId) {
        if (null == columnId) {
            return ajaxErr("栏目不能为空");
        }
        if (null == siteId) {
            return ajaxErr("站点不能为空");
        }
        String fileName = Filedata.getOriginalFilename();
        String fileSuffix = FileUtil.getType(fileName);
        ColumnTypeConfigVO modelVO = null;
        MongoFileVO _vo = null;
        try {
            InputStream in = Filedata.getInputStream();
            WaterMarkConfigEO eo = waterMarkConfigService.getConfigBySiteId(siteId);
            modelVO = ModelConfigUtil.getCongfigVO(columnId, siteId);
            int width = 0;
            int heigth = 0;

            if (modelVO != null && !AppUtil.isEmpty(modelVO.getPicWidth())) {
                width = modelVO.getPicWidth();
            }
            if (modelVO != null && !AppUtil.isEmpty(modelVO.getPicHeight())) {
                heigth = modelVO.getPicHeight();
            }
            byte[] b = null;
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            if (eo != null && eo.getEnableStatus() == 1 && modelVO != null && modelVO.getIsWater() == 1) {
                //byte[] by = WaterMarkUtil.createWaterMark(in, siteId, fileSuffix);
                //InputStream inNew = new ByteArrayInputStream(by);
                ImgHander.ImgTrans(in, width, heigth, baos, fileSuffix);
                in = new ByteArrayInputStream(baos.toByteArray());
                b = WaterMarkUtil.createWaterMark(in, siteId, fileSuffix);

            } else {
                ImgHander.ImgTrans(in, width, heigth, baos, fileSuffix);
                b = baos.toByteArray();
            }

            _vo =
                    FileUploadUtil.uploadUtil(b, fileName, FileCenterEO.Type.Image.toString(), FileCenterEO.Code.ThumbUpload.toString(), siteId, columnId,
                            contentId, "新闻缩略图", sessionId);// mongoDbFileServer.uploadByteFile(b,
            // fileName,
            // contentId, null);

            baos.flush();
            baos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return _vo;
    }

    @ResponseBody
    @RequestMapping("newsSource")
    public Object getNewsSource() {
        List<DataDictVO> voList = new ArrayList<DataDictVO>();
        DataDictVO vo = new DataDictVO();
        vo.setId(0L);
        vo.setKey(LoginPersonUtil.getOrganName());
        vo.setDefault(true);
        voList.add(vo);
        // 添加数据字典
        List<DataDictVO> itemList = DataDictionaryUtil.getItemList("sourceMgr", LoginPersonUtil.getSiteId());
        if (null != itemList && !itemList.isEmpty()) {
            for (DataDictVO v : itemList) {
                v.setDefault(false);
                voList.add(v);
            }
        }
        return getObject(voList);
    }

    // 获取内容模型
    @RequestMapping("getModelConfig")
    @ResponseBody
    public Object getModelConfig(Long columnId) {
        return getObject(getCongfigVO(columnId, LoginPersonUtil.getSiteId()));
    }

    // 内容检测页面跳转
    @RequestMapping("contentCheck")
    public Object contentCheck(String flag) {
        String url = "";
        if ("SENSITIVE".equals(flag)) {
            url = "/content/common/sen_words_list";
        } else if ("EASYERR".equals(flag)) {
            url = "/content/common/error_words_list";
        } else if ("HOT".equals(flag)) {
            url = "/content/common/hot_words_list";
        }
        return url;
    }

    // 内容检测
    @RequestMapping("getCheckList")
    @ResponseBody
    public Object getCheckList(String content, String flag) {
        List<Object> list = WordsSplitHolder.wordsCheck(content, flag);
        Pagination page = new Pagination();
        page.setData(list);
        return getObject(page);
    }

    @RequestMapping("checkPage")
    public Object checkPage() {
        return "/content/common/checkAll";
    }

    /**
     * @param content
     * @param types
     * @return Object return type
     * @throws
     * @Title: isCheck
     * @Description: 判断内容是否存在词汇问题
     */
    @RequestMapping("isCheck")
    @ResponseBody
    public Object isCheck(String content, @RequestParam(value = "types[]", required = false) Integer[] types) {
        Integer res = 0;
        if (types != null) {
            for (int i = 0; i < types.length; i++) {
                if (types[i] == 1) {
                    List<WordsEasyerrEO> list = WordsSplitHolder.wordsCheck(content, Type.EASYERR.toString());
                    if (list.size() > 0 && list != null) {
                        return getObject(1);
                    }
                }
                if (types[i] == 2) {
                    List<WordsSensitiveEO> list = WordsSplitHolder.wordsCheck(content, Type.SENSITIVE.toString());
                    if (list.size() > 0 && list != null) {
                        return getObject(1);
                    }
                }
                if (types[i] == 3) {
                    List<WordsSensitiveEO> list = WordsSplitHolder.wordsCheck(content, Type.HOT.toString());
                    if (list.size() > 0 && list != null) {
                        return getObject(1);
                    }
                }
            }
        }
        return getObject(res);

    }

    /**
     * @param content
     * @param types
     * @return Object return type
     * @throws
     * @Title: checkAll
     * @Description: 获取三类词汇检测结果
     */
    @RequestMapping("checkAll")
    @ResponseBody
    public Object checkAll(String content, @RequestParam(value = "types[]", required = false) Integer[] types) {
        List<CheckWordsVO> _list = new ArrayList<CheckWordsVO>();
        Long id = 1L;
        if (types != null) {
            for (int i = 0; i < types.length; i++) {
                if (types[i] == 1) {
                    List<WordsEasyerrEO> list = WordsSplitHolder.wordsCheck(content, Type.EASYERR.toString());
                    if (null != list) {
                        for (WordsEasyerrEO l : list) {
                            CheckWordsVO _vo = new CheckWordsVO();
                            _vo.setId(id);
                            _vo.setWords(l.getWords());
                            _vo.setTargetWords(l.getReplaceWords());
                            _vo.setDesc("");
                            _vo.setType(types[i]);
                            _list.add(_vo);
                            id++;
                        }
                    }
                }
                if (types[i] == 2) {
                    List<WordsSensitiveEO> list = WordsSplitHolder.wordsCheck(content, Type.SENSITIVE.toString());
                    if (null != list) {
                        for (WordsSensitiveEO l : list) {
                            CheckWordsVO _vo = new CheckWordsVO();
                            _vo.setId(id);
                            _vo.setWords(l.getWords());
                            _vo.setTargetWords(l.getReplaceWords());
                            _vo.setDesc("");
                            _vo.setType(types[i]);
                            _list.add(_vo);
                            id++;
                        }
                    }
                }
                if (types[i] == 3) {
                    List<WordsHotConfEO> list = WordsSplitHolder.wordsCheck(content, Type.HOT.toString());
                    if (null != list) {
                        for (WordsHotConfEO l : list) {
                            CheckWordsVO _vo = new CheckWordsVO();
                            _vo.setId(id);
                            _vo.setWords(l.getHotName());
                            _vo.setTargetWords(l.getHotUrl());
                            _vo.setDesc(l.getUrlDesc());
                            _vo.setType(types[i]);
                            _list.add(_vo);
                            id++;
                        }
                    }
                }
            }
        }
        Pagination page = new Pagination();
        page.setData(_list);
        return getObject(page);
    }

    /* get the contents what you haven the authority and not published */
    // 未审核内容
    @RequestMapping("getUnAuditContents")
    @ResponseBody
    public Object getUnAuditContents(UnAuditContentsVO uaVO, String isDividePublic, Long publicColumnId, HttpServletRequest request) {
        uaVO.setIsPublish(0);
        if (AppUtil.isEmpty(uaVO.getSiteId())) {
            uaVO.setSiteId((Long) request.getSession().getAttribute("siteId"));
        }

        Long[] childs = null;
        if (publicColumnId != null && publicColumnId > 0L) {
            //根据栏目id获取子栏目id，没有则返回本栏目id
            childs = ColumnUtil.getQueryColumnIdByChild(
                    publicColumnId.toString(), true, BaseContentEO.TypeCode.articleNews.toString());
        }


        if (LoginPersonUtil.isSiteAdmin() || LoginPersonUtil.isSuperAdmin()) {
            uaVO.setColumnIds(null);
            //将信息公开从文字信息中分开
            if (!AppUtil.isEmpty(isDividePublic) && isDividePublic.equals("1")) {
                if (uaVO.getTypeCode().equals(BaseContentEO.TypeCode.articleNews.toString())) {
                    //文字信息中剔除信息公开记录
                    uaVO.setExceptColumnIds(childs);
                } else if (uaVO.getTypeCode().equals("publicNews")) {
                    //综合信息-信息公开 数据
                    uaVO.setColumnIds(childs);
                    uaVO.setTypeCode(BaseContentEO.TypeCode.articleNews.toString());
                }
            }

        } else {
            uaVO.setSiteId((Long) request.getSession().getAttribute("siteId"));
            /*
             * List<Long> ids = new ArrayList<Long>(); List<ColumnMgrEO> columns
             * = columnConfigService.getTree((Long)
             * request.getSession().getAttribute("siteId")); List<ColumnMgrEO>
             * myColumns = siteRightsService.getCurUserColumnOpt(columns); for
             * (ColumnMgrEO myColumn : myColumns) { List<FunctionEO> functions =
             * myColumn.getFunctions(); for (FunctionEO function : functions) {
             * if ("publish".equals(function.getAction())) {
             * ids.add(myColumn.getIndicatorId()); } } }
             */

            List<Long> ids = ColumnRightsUtil.getRCurHasColumns();

            List<Long> publicIds = new ArrayList<Long>();
            if (!AppUtil.isEmpty(isDividePublic) && isDividePublic.equals("1")) {
                for (int i = 0; i < childs.length; i++) {
                    if (ids.contains(childs[i])) {
                        publicIds.add(childs[i]);
                    }
                }
                //去除信息公开栏目id
                ids.removeAll(publicIds);
            }

            uaVO.setColumnIds(ids.toArray(new Long[ids.size()]));
            if (uaVO.getTypeCode().equals("publicNews")) {
                //综合信息-信息公开 数据
                uaVO.setColumnIds(publicIds.toArray(new Long[publicIds.size()]));
                uaVO.setTypeCode(BaseContentEO.TypeCode.articleNews.toString());
            }

        }

        Pagination p = null;
        if (ChuZhouMessageBoardOpenUtil.isOpen == 1 && uaVO.getTypeCode().equals(BaseContentEO.TypeCode.messageBoard.toString())) {
            uaVO.setIsPublish(0);
            p = messageBoardService.getUnAuditCount(uaVO);
            List<MessageBoardEditVO> list = (List<MessageBoardEditVO>) p.getData();
            for (MessageBoardEditVO eo : list) {
                if (!LoginPersonUtil.isSiteAdmin() && !LoginPersonUtil.isSuperAdmin()) {
                    eo.setIsSuper(0);
                } else {
                    eo.setIsSuper(1);
                }
                if (eo.getColumnId() != null && getEntity(IndicatorEO.class, eo.getColumnId()) != null) {
                    eo.setColumnName(ColumnUtil.getColumnName(eo.getColumnId(), eo.getSiteId()));
                }
            }
        } else {

            // 获取待审内容分页
            p = baseContentService.getUnAuditContents(uaVO);

            @SuppressWarnings("unchecked")
            List<BaseContentEO> list = (List<BaseContentEO>) p.getData();
            for (BaseContentEO eo : list) {
                if (eo.getColumnId() != null && getEntity(IndicatorEO.class, eo.getColumnId()) != null) {
                    eo.setColumnName(ColumnUtil.getColumnName(eo.getColumnId(), eo.getSiteId()));
                }
                if (null != eo && null != eo.getUnitId()) {
                    OrganEO organ = organService.getEntity(OrganEO.class, eo.getUnitId());
                    eo.setOrganName(organ.getName());
                }
            }
        }

        return getObject(p);
    }

    @RequestMapping("getUnAuditColumns")
    @ResponseBody
    public Object getUnAuditColumns(UnAuditContentsVO uaVO, String isDividePublic, Long publicColumnId, HttpServletRequest request) {
        if (AppUtil.isEmpty(uaVO.getSiteId())) {
            uaVO.setSiteId((Long) request.getSession().getAttribute("siteId"));
        }
        List<ColumnTypeVO> list = new ArrayList<ColumnTypeVO>();
        if (ChuZhouMessageBoardOpenUtil.isOpen == 1 && uaVO.getTypeCode().equals(BaseContentEO.TypeCode.messageBoard.toString())) {
            List<ColumnMgrEO> columnMgrEOList = columnConfigService.getColumnByTypeCode(LoginPersonUtil.getSiteId(), BaseContentEO.TypeCode.messageBoard.toString());
            for (ColumnMgrEO columnMgrEO : columnMgrEOList) {
                ColumnTypeVO columnTypeVO = new ColumnTypeVO();
                columnTypeVO.setColumnId(columnMgrEO.getIndicatorId());
                columnTypeVO.setColumnName(columnMgrEO.getName());
                list.add(columnTypeVO);
            }
        } else {
            if (LoginPersonUtil.isSiteAdmin() || LoginPersonUtil.isSuperAdmin()) {
                uaVO.setColumnIds(null);

                list = baseContentService.getUnAuditColumnIds(uaVO);

                List<ColumnTypeVO> publicList = new ArrayList<ColumnTypeVO>();
                String typeCode = uaVO.getTypeCode();
                if (!AppUtil.isEmpty(isDividePublic) && isDividePublic.equals("1")) {
                    if (publicColumnId != null && publicColumnId > 0L) {
                        //根据栏目id获取子栏目id，没有则返回本栏目id
                        Long[] childs = ColumnUtil.getQueryColumnIdByChild(
                                publicColumnId.toString(), true, BaseContentEO.TypeCode.articleNews.toString());
                        //综合信息-信息公开
                        uaVO.setTypeCode(BaseContentEO.TypeCode.articleNews.toString());
                        uaVO.setColumnIds(childs);
                        publicList = baseContentService.getUnAuditColumnIds(uaVO);
                    }
                }

                if (typeCode.equals(BaseContentEO.TypeCode.articleNews.toString())) {
                    //文字信息中去除信息公开
                    list.removeAll(publicList);
                } else if (typeCode.equals("publicNews")) {
                    list = publicList;
                }

            } else {
                uaVO.setSiteId((Long) request.getSession().getAttribute("siteId"));
                List<Long> ids = ColumnRightsUtil.getRCurHasColumns();

                //综合信息中的信息公开栏目单独拿出来
                List<Long> publicIds = new ArrayList<Long>();
                if (!AppUtil.isEmpty(isDividePublic) && isDividePublic.equals("1")) {
                    if (publicColumnId != null && publicColumnId > 0L) {
                        //根据栏目id获取子栏目id，没有则返回本栏目id
                        Long[] childs = ColumnUtil.getQueryColumnIdByChild(
                                publicColumnId.toString(), true, BaseContentEO.TypeCode.articleNews.toString());
                        if (LoginPersonUtil.isSiteAdmin() || LoginPersonUtil.isSuperAdmin()) {
                            publicIds.addAll(Arrays.asList(childs));
                        } else {
                            for (int i = 0; i < childs.length; i++) {
                                if (ids.contains(childs[i])) {
                                    publicIds.add(childs[i]);
                                }
                            }
                            //去除信息公开栏目id
                            ids.removeAll(publicIds);
                        }
                    }
                }

                //综合信息-信息公开
                if (uaVO.getTypeCode().equals("publicNews")) {
                    uaVO.setTypeCode(BaseContentEO.TypeCode.articleNews.toString());
                    uaVO.setColumnIds(publicIds.toArray(new Long[publicIds.size()]));
                } else {
                    uaVO.setColumnIds(ids.toArray(new Long[ids.size()]));
                }
            /*
             * List<ColumnMgrEO> columns = columnConfigService.getTree((Long)
             * request.getSession().getAttribute("siteId")); List<ColumnMgrEO>
             * myColumns = siteRightsService.getCurUserColumnOpt(columns); for
             * (ColumnMgrEO myColumn : myColumns) { List<FunctionEO> functions =
             * myColumn.getFunctions(); for (FunctionEO function : functions) {
             * if ("publish".equals(function.getAction())) {
             * ids.add(myColumn.getIndicatorId()); } } }
             */

                list = baseContentService.getUnAuditColumnIds(uaVO);
            }

        }
        return getObject(list);
    }


    /* get the contents what you haven the authority and published */
    // 审核内容
    @RequestMapping("getAuditContents")
    @ResponseBody
    public Object getAuditContents(UnAuditContentsVO uaVO, HttpServletRequest request) {
        uaVO.setIsPublish(1);
        if (AppUtil.isEmpty(uaVO.getSiteId())) {
            uaVO.setSiteId((Long) request.getSession().getAttribute("siteId"));
        }

        if (LoginPersonUtil.isSiteAdmin() || LoginPersonUtil.isSuperAdmin()) {
            uaVO.setColumnIds(null);

        } else {
            uaVO.setSiteId((Long) request.getSession().getAttribute("siteId"));

            List<Long> ids = ColumnRightsUtil.getRCurHasColumns();

            uaVO.setColumnIds(ids.toArray(new Long[ids.size()]));

        }

        Pagination p = null;


        // 获取已审内容分页
        p = baseContentService.getUnAuditContents(uaVO);

        @SuppressWarnings("unchecked")
        List<BaseContentEO> list = (List<BaseContentEO>) p.getData();
        for (BaseContentEO eo : list) {
            if (eo.getColumnId() != null && getEntity(IndicatorEO.class, eo.getColumnId()) != null) {
                eo.setColumnName(ColumnUtil.getColumnName(eo.getColumnId(), eo.getSiteId()));
            }
            if (null != eo && null != eo.getUnitId()) {
                OrganEO organ = organService.getEntity(OrganEO.class, eo.getUnitId());
                eo.setOrganName(organ.getName());
            }
        }

        return getObject(p);
    }

    @RequestMapping("updateStatus")
    @ResponseBody
    public Object updateStatus(@RequestParam("ids") Long[] ids, Integer status) {
        if (ids != null && ids.length > 0) {
            Long siteId = null;
            Long columnId = null;
            List<BaseContentEO> baseContentEOs = baseContentService.getEntities(BaseContentEO.class, ids);
            String opt = "";
            if(ids!=null&&ids.length>1){
                opt = "批量";
            }
            for (BaseContentEO content : baseContentEOs) {
                if (content != null) {
                    siteId = content.getSiteId();
                    columnId = content.getColumnId();

                    String publishStr;
                    if (status == 1) {
                        if (content.getVideoStatus().equals(0)) {
                            // 转换视频
                            throw new BaseRunTimeException(content.getTitle() + "内容有未转换视频!");
                        }
                        content.setPublishDate(new Date());
                        content.setIsPublish(2);
                        publishStr = "发布";
                    } else {
                        content.setIsPublish(0);
                        publishStr = "取消发布";
                    }

                    String newsType = getTypeCodeName(content.getTypeCode());
                    SysLog.log(newsType+":"+opt+publishStr+"内容 ("+content.getTitle()+")" ,
                            "BaseContentEO", CmsLogEO.Operation.Update.toString());
                }
            }
            baseContentService.updateEntities(baseContentEOs);


            if (status == 1) {
                MessageSenderUtil.publishContent(new MessageStaticEO(siteId, columnId, ids).setType(MessageEnum.PUBLISH.value()), 1);
            } else {
                MessageSenderUtil.publishContent(new MessageStaticEO(siteId, columnId, ids).setType(MessageEnum.UNPUBLISH.value()), 2);
            }
        }
        return getObject();
    }

    @RequestMapping("updateSort")
    @ResponseBody
    public Object updateSort(SortUpdateVO sortVo) {
        if (StringUtils.isEmpty(sortVo.getOperate()) || sortVo.getId() == null || StringUtils.isEmpty(sortVo.getTypeCode()) || sortVo.getSiteId() == null
                || sortVo.getColumnId() == null || sortVo.getSortNum() == null) {
            throw new BaseRunTimeException(TipsMode.Message.toString(), "参数错误");
        }
        baseContentService.updateSort(sortVo);
        return getObject();
    }

    @RequestMapping("referPage")
    public String referPage(Long id, ModelMap map) {
        BaseContentEO _bc = baseContentService.getEntity(BaseContentEO.class, id);
        map.put("OPR_EO", _bc);
        return "/content/refer_news_page";
    }

    @RequestMapping("referArticle")
    @ResponseBody
    public Object referArticle(@RequestParam("ids[]") String[] ids, @RequestParam("pIds[]") String[] pIds, Long contentId, String synColumnIsPublishs) {
        String messges = baseContentService.saveReferNews(ids, contentId, "CONTENT", synColumnIsPublishs,0);
        MessageSenderUtil.publishCopyNews(messges);
        BaseContentEO baseEO = getEntity(BaseContentEO.class, contentId);
        baseEO.setQuoteStatus(1);
        baseContentService.updateEntity(baseEO);
        CacheHandler.saveOrUpdate(BaseContentEO.class, baseEO);
        //增加记录
        ContentOptRecordUtil.saveOptRecord(new Long[]{contentId}, -1, ContentOptRecordEO.Type.refer);
        return getObject();
    }

    @RequestMapping("sharePage")
    public String sharePage(Long id, ModelMap map) {
        BaseContentEO _bc = baseContentService.getEntity(BaseContentEO.class, id);
        IndicatorEO indicat = getEntity(IndicatorEO.class, LoginPersonUtil.getSiteId());
        String path = null;
        if (BaseContentEO.TypeCode.public_content.toString().equals(_bc.getTitle())) {
            path = indicat.getUri() + "/public/" + _bc.getColumnId() + "/" + _bc.getId() + ".html";
        } else {
            ColumnMgrEO column = getEntity(ColumnMgrEO.class, _bc.getColumnId());
            if (column != null) {
                path = indicat.getUri() + "/" + column.getUrlPath() + "/" + _bc.getId() + ".html";
            } else {
                path = indicat.getUri() + "/" + _bc.getColumnId() + "/" + _bc.getId() + ".html";
            }
        }
        String content = "【" + indicat.getName() + "--" + _bc.getTitle() + "】" + path;
        map.put("Content", content);
        return "/content/share_news_page";
    }

    @RequestMapping("shareNews")
    @ResponseBody
    public Object shareNews(Long id, Integer sinaCheck, Integer tencentCheck, Integer android, Integer ios, String content) {

        if (null == id) {
            return ajaxErr("栏目不能为空");
        }

        if (null == content) {
            return ajaxErr("推送消息内容不能为空");
        }

        String logDetail = "";

        if (sinaCheck == 1) {
            SinaWeiboContentVO vo = new SinaWeiboContentVO();
            vo.setText(content);
            try {
                sinaWeiboService.publishWeibo(vo);
                logDetail = "新浪微博";
            } catch (Exception e) {
                throw new BaseRunTimeException(TipsMode.Message.toString(), "新浪微博分享失败！");
            }
        }

        if (tencentCheck == 1) {
            try {
                tencentWeiboService.publishWeibo(content);
                if(AppUtil.isEmpty(logDetail)){
                    logDetail = "腾讯微博";
                }else{
                    logDetail += "+腾讯微博";
                }
            } catch (Exception e) {
                throw new BaseRunTimeException(TipsMode.Message.toString(), "腾讯微博分享失败！");
            }
        }

        BaseContentEO eo = baseContentService.getEntity(BaseContentEO.class, id);
        IndicatorEO indicatorEO = getEntity(IndicatorEO.class, LoginPersonUtil.getSiteId());
        String msgUrl = indicatorEO.getUri() + "/mobile/articleNews?contentId=" + eo.getId();

        if (android == 1) {
            BaiduPushUtil.pushAndroidMsg(eo.getColumnId(), eo.getTitle(), content, msgUrl);
            if(AppUtil.isEmpty(logDetail)){
                logDetail = "安卓客户端";
            }else{
                logDetail += "+安卓客户端";
            }
        }

        if (ios == 1) {
            BaiduPushUtil.pushIosMsg(eo.getColumnId(), eo.getTitle(), content, msgUrl);
            if(AppUtil.isEmpty(logDetail)){
                logDetail = "苹果客户端";
            }else{
                logDetail += "+苹果客户端";
            }
        }

        //增加记录
        ContentOptRecordUtil.saveOptRecord(new Long[]{id}, -1, ContentOptRecordEO.Type.push);

        //添加推送操作日志
        SysLog.log("推送内容：栏目（"+ColumnUtil.getColumnName(eo.getColumnId(),eo.getSiteId())
                +"），标题（"+eo.getTitle()+"），推送到"+logDetail,
                "BaseContentEO", CmsLogEO.Operation.Update.toString());

        return getObject();
    }

    /**
     * @param pageVO
     * @return Object return type
     * @throws
     * @Title: getQueryPage
     * @Description: 按条件全站查询
     */
    @RequestMapping("getQueryPage")
    @ResponseBody
    public Object getQueryPage(ContentPageVO pageVO, String typeCodes) {
        String[] str = null;
        if (!AppUtil.isEmpty(typeCodes)) {
            str = typeCodes.split(",");
        }
        pageVO.setTypeCodes(str);
        return baseContentService.getQueryPage(pageVO);
    }

    /**
     * 更新排序号
     *
     * @param ids  原ID
     * @param nums 目标ID
     * @return
     */
    @RequestMapping("updateNums")
    @ResponseBody
    public Object updateNums(Long ids[], Long nums[]) {
        if (null == ids || ids.length == 0) {
            return ajaxParamsErr("ids");
        }
        if (null == nums || nums.length == 0) {
            return ajaxParamsErr("nums");
        }
        if (ids.length != nums.length) {
            return ajaxErr("id个数与排序号个数不一致");
        }
        baseContentService.updateNums(ids, nums);
        Long columnId = null;
        if (ids != null && ids.length > 0) {
            BaseContentEO contentEO = CacheHandler.getEntity(BaseContentEO.class, ids[0]);
            columnId = contentEO.getColumnId();
        }
        List<BaseContentEO> list = new ArrayList<BaseContentEO>();
        List<BaseContentEO> list1 = baseContentService.getEntities(BaseContentEO.class, ids);
        if (list1 != null && list1.size() > 0) {
            list.addAll(list1);
        }
        List<BaseContentEO> list2 = baseContentService.getEntities(BaseContentEO.class, nums);
        if (list2 != null && list2.size() > 0) {
            list.addAll(list2);
        }
        String idStr = "";
        for (BaseContentEO eo : list) {
            if (eo != null && eo.getIsPublish() == 1) {
                idStr += eo.getId() + ",";
            }
        }
        if (!StringUtils.isEmpty(idStr)) {
            Long[] idArr = AppUtil.getLongs(idStr, ",");
            MessageSenderUtil.publishContent(new MessageStaticEO(LoginPersonUtil.getSiteId(), columnId, idArr).setType(MessageEnum.PUBLISH.value()), 1);
        }
        return getObject();
    }

    /**
     * 更新主体信息
     *
     * @return
     * @author zhongjun
     */
    @ResponseBody
    @RequestMapping(value = "updateBaseInfo", method = RequestMethod.POST)
    public Object updateBaseInfo(BaseContentEO contentEO, String publishDateStr) {
        if (contentEO.getId() == null || contentEO.getId().longValue() < 1) {
            return ajaxErr("未知的记录id");
        }
        BaseContentEO info = null;
        try {
            info = baseContentService.getEntity(BaseContentEO.class, contentEO.getId());
            if (info == null) {
                return ajaxErr("不存在的记录");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ajaxErr("不存在的记录：" + e.getMessage());
        }
        try {
            contentEO.setPublishDate(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(publishDateStr));
        } catch (ParseException e) {
            e.printStackTrace();
            return ajaxErr("发布时间格式不正确！");
        }

        try {
            ContentMongoEO content = contentMongoService.queryById(info.getId());
            if (content == null) {
                return ajaxErr("内容不存在！");
            }
            if (StringUtils.isEmpty(contentEO.getArticle())) {
                return ajaxErr("内容不能为空");
            }
            content.setContent(contentEO.getArticle());
            contentMongoService.save(content);
        } catch (Exception e) {
            e.printStackTrace();
            return ajaxErr("更新内容出错！");
        }

        info.setTitle(contentEO.getTitle());
        info.setSubTitle(contentEO.getSubTitle());
        info.setIsTilt(contentEO.getIsTilt());
        info.setIsBold(contentEO.getIsBold());
        info.setIsUnderline(contentEO.getIsUnderline());
        info.setTitleColor(contentEO.getTitleColor());
        info.setPublishDate(contentEO.getPublishDate());
        baseContentService.saveEntity(info);
        CacheHandler.saveOrUpdate(BaseContentEO.class, info);

        MessageStaticEO msg = new MessageStaticEO(info.getSiteId(), info.getColumnId(), new Long[]{info.getId()}).setType(MessageEnum.PUBLISH.value());
        MessageSenderUtil.publishContent(msg, 1);
        SysLog.log("发布内容 >> ID：" + info.getId() + ",标题：" + info.getTitle(),
                "BaseContentEO", CmsLogEO.Operation.Update.toString());
        return getObject();
    }

    private String getTypeCodeName(String typeCode){
        String typeCodeName = "内容";
        if(BaseContentEO.TypeCode.articleNews.toString().equals(typeCode)){
            typeCodeName = "文章新闻";
        }else if(BaseContentEO.TypeCode.pictureNews.toString().equals(typeCode)){
            typeCodeName = "图片新闻";
        }else if(BaseContentEO.TypeCode.videoNews.toString().equals(typeCode)){
            typeCodeName = "视频新闻";
        }else if(BaseContentEO.TypeCode.reviewInfo.toString().equals(typeCode)){
            typeCodeName = "网上评议";
        }else if(BaseContentEO.TypeCode.collectInfo.toString().equals(typeCode)){
            typeCodeName = "民意征集";
        }else if(BaseContentEO.TypeCode.survey.toString().equals(typeCode)){
            typeCodeName = "在线调查";
        }else if(BaseContentEO.TypeCode.interviewInfo.toString().equals(typeCode)){
            typeCodeName = "在线访谈";
        }else if(BaseContentEO.TypeCode.linksMgr.toString().equals(typeCode)){
            typeCodeName = "链接";
        }else if(BaseContentEO.TypeCode.public_content.toString().equals(typeCode)){
            typeCodeName = "主动公开";
        }
        return typeCodeName;
    }
}
