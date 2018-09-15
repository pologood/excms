package cn.lonsun.content.controller;

import cn.lonsun.activemq.MessageSenderUtil;
import cn.lonsun.common.util.AppUtil;
import cn.lonsun.content.internal.entity.BaseContentEO;
import cn.lonsun.content.internal.entity.ContentPicEO;
import cn.lonsun.content.internal.service.IBaseContentService;
import cn.lonsun.content.internal.service.IContentPicService;
import cn.lonsun.content.vo.ContentPageVO;
import cn.lonsun.content.vo.SynColumnVO;
import cn.lonsun.core.base.controller.BaseController;
import cn.lonsun.core.exception.BaseRunTimeException;
import cn.lonsun.core.hibernate.SnowFlakeIdGenerater;
import cn.lonsun.core.util.TipsMode;
import cn.lonsun.job.timingjob.jobimpl.NewsIssueTaskImpl;
import cn.lonsun.job.timingjob.jobimpl.NewsTopTaskImpl;
import cn.lonsun.job.util.ScheduleJobUtil;
import cn.lonsun.message.internal.entity.MessageEnum;
import cn.lonsun.message.internal.entity.MessageStaticEO;
import cn.lonsun.mongodb.base.IMongoDbFileServer;
import cn.lonsun.mongodb.entity.ContentMongoEO;
import cn.lonsun.mongodb.service.ContentMongoServiceImpl;
import cn.lonsun.mongodb.vo.MongoFileVO;
import cn.lonsun.site.contentModel.vo.ColumnTypeConfigVO;
import cn.lonsun.site.site.internal.entity.WaterMarkConfigEO;
import cn.lonsun.site.site.internal.service.IWaterMarkConfigService;
import cn.lonsun.system.filecenter.internal.entity.FileCenterEO;
import cn.lonsun.util.*;
import com.alibaba.fastjson.JSONArray;
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
import java.io.ByteArrayOutputStream;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Hewbing
 * @ClassName: PicNewsController
 * @Description:新闻图片控制层
 * @date 2015年10月15日 上午8:22:39
 *
 */
@Controller
@RequestMapping(value = "pictureNews")
public class PicNewsController extends BaseController {
    @Autowired
    private IContentPicService contentPicService;
    @Autowired
    private IBaseContentService baseContentService;
    @Autowired
    private IMongoDbFileServer mongoDbFileServer;
    @Autowired
    private ContentMongoServiceImpl contentMongoService;

    @Autowired
    private IWaterMarkConfigService waterMarkConfigService;
    @Value("${ffmpeg.path}")
    private String path;

    @RequestMapping("index")
    public String index(Long pageIndex, ModelMap map) {
        if (pageIndex == null) pageIndex = 0L;
        map.put("pageIndex", pageIndex);
        return "/content/picturenews/pic_news_list";
    }

    @RequestMapping("picNewsEdit")
    public String picNewsEdit(@RequestParam(value = "id", required = false) Long id, ModelMap map, @RequestParam(value = "pageIndex", required = false) Long pageIndex) {
        SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String str1 = sdf1.format(new Date());
        map.put("picNewsId", id);
        map.put("nowDate", str1);
        map.put("pageIndex", pageIndex);
        return "/content/picturenews/pic_news_edit";
    }

    /**
     * @param id
     * @return Object    返回类型
     * @throws
     * @Title: getPicContent
     * @Description: 获取图片新闻
     */
    @RequestMapping("getPicContent")
    @ResponseBody
    public Object getPicContent(Long id, String recordStatus) {
        Map<String, Object> map = new HashMap<String, Object>();
        BaseContentEO picture = baseContentService.getContent(id, recordStatus);
        Criteria criteria = Criteria.where("_id").is(id);
        Query query = new Query(criteria);
        ContentMongoEO _eo = contentMongoService.queryOne(query);
        map.put("picture", picture);
        String content = "";
        if (!AppUtil.isEmpty(_eo)) {
            content = _eo.getContent();
        }
        map.put("content", content);
        map.put("picList", contentPicService.getPicsList(id));
        return getObject(map);
    }

    @RequestMapping("savePictureNews")
    @ResponseBody
    public Object savePictureNews(BaseContentEO contentEO, String content, @RequestParam(value = "articleText", required = false) String articleText, String picList, @RequestParam(value = "synColumnIds[]", required = false) Long[] synColumnIds) {
        Integer isPublished = 0;
        Integer isPublish = contentEO.getIsPublish();
        if (!AppUtil.isEmpty(contentEO.getId())) {
            isPublished = baseContentService.getEntity(BaseContentEO.class, contentEO.getId()).getIsPublish();
        }
        if (AppUtil.isEmpty(contentEO.getTitle())) {
            return ajaxErr("标题不能为空");
        }
        if (contentEO.getVideoStatus().equals(0) && contentEO.getIsPublish() == 1) {//
            return ajaxErr("文章中有视频未转换，不能进行发布操作！");
        }
        Long oldId = contentEO.getId();
        //content=WordsSplitHolder.wordsRplc(content, Type.SENSITIVE.toString());
        SynColumnVO _vo = contentPicService.savePicNews(contentEO, content, picList, synColumnIds);
        if (contentEO.getVideoStatus().equals(0)) {
            // 转换视频
            VideoToMp4ConvertUtil.transfer(path, contentEO.getColumnId(), _vo.getContentId());
        }
        if (oldId == null && synColumnIds != null) {
            contentPicService.synToColumn(_vo.getPicList(), contentEO, content, synColumnIds, null);
        }
        boolean rel = false;
        if (isPublish == 1) {
            rel = MessageSenderUtil.publishContent(
                new MessageStaticEO(contentEO.getSiteId(), contentEO.getColumnId(), new Long[]{_vo.getContentId()})
                    .setType(MessageEnum.PUBLISH.value()), 1);
        } else if (isPublished == 1) {
            rel = MessageSenderUtil.publishContent(
                new MessageStaticEO(contentEO.getSiteId(), contentEO.getColumnId(), new Long[]{_vo.getContentId()})
                    .setType(MessageEnum.UNPUBLISH.value()), 2);
        }
        if (contentEO.getIsTop() == 1 && contentEO.getTopValidDate() != null) {
            ScheduleJobUtil.addScheduleJob("新闻置顶有效期", NewsTopTaskImpl.class.getName(), ScheduleJobUtil.dateToCronExpression(contentEO.getTopValidDate()), String.valueOf(_vo.getContentId()));
        }
        //设置定时发布
        Integer isJob = 0;
        if (contentEO.getIsJob() == 1 && contentEO.getJobIssueDate() != null) {
            isJob = 1;
        }
        ScheduleJobUtil.addOrDelScheduleJob("新闻定时发布日期", NewsIssueTaskImpl.class.getName(), isJob == 0 ? null : ScheduleJobUtil.dateToCronExpression(contentEO.getJobIssueDate()),
            String.valueOf(contentEO.getId()), isJob);
        return getObject(rel);
    }

    /**
     * @param contentId
     * @return
     * @Description 图片列表
     * @author Hewbing
     * @date 2015年9月23日 上午11:15:32
     */
    @RequestMapping("getPicList")
    @ResponseBody
    public Object getPicList(Long contentId) {
        List<ContentPicEO> picList = contentPicService.getPicsList(contentId);
        return getObject(picList);
    }

    /**
     * @param picId
     * @return
     * @Description 图片编辑页
     * @author Hewbing
     * @date 2015年9月23日 上午11:24:11
     */
    @RequestMapping("editPic")
    public String editPic(Long picId, ModelMap map) {
        map.put("picEO", contentPicService.getEntity(ContentPicEO.class, picId));
        return "/content/pic_edit";

    }

    /**
     * @param contentId
     * @param map
     * @return
     * @Description 图片批量上传
     * @author Hewbing
     * @date 2015年9月23日 上午11:16:05
     */
    @RequestMapping("picUpload")
    public String picUpload(Long contentId, Long siteId, Long columnId,
                            ModelMap map) {
        map.addAttribute("contentId", contentId);
        map.addAttribute("siteId", siteId);
        map.addAttribute("columnId", columnId);
        return "/content/pic_upload";

    }

    @RequestMapping("getListByPath")
    @ResponseBody
    public Object getListByPath(@RequestParam(value = "paths[]", required = false) String[] paths, @RequestParam(value = "contentId", required = false) Long contentId) {
        return getObject(contentPicService.getListByPath(paths));
    }


    /**
     * @param Filedata
     * @param contentId
     * @return
     * @Description 保存上传新闻图片
     * @author Hewbing
     * @date 2015年9月22日 上午9:47:46
     */
    @RequestMapping("saveNewsPic")
    @ResponseBody
    public Object savePic(MultipartFile Filedata, Long contentId, Long picId,
                          HttpServletRequest request, String sessionId, Long siteId, Long columnId, String desc) {
        String path = "";
        String thumbPath = "";
        if (Filedata.isEmpty()) {
            return ajaxErr("文件上传失败");
        }
        WaterMarkConfigEO eo = waterMarkConfigService.getConfigBySiteId(siteId);
        ColumnTypeConfigVO modelVO = ModelConfigUtil.getCongfigVO(columnId, siteId);
        try {
            String fileName = Filedata.getOriginalFilename();
            String picName = fileName.substring(0, fileName.lastIndexOf("."));
            String suffix = fileName.substring(fileName.lastIndexOf(".") + 1,
                fileName.length()).toLowerCase();
            MongoFileVO mfvo = null;
            // 将原图保存打水印至文件服务器
            if (eo != null && eo.getEnableStatus() == 1 && modelVO != null && modelVO.getIsWater() == 1) {
                byte[] bt = WaterMarkUtil.createWaterMark(Filedata.getInputStream(), siteId,suffix);
                mfvo = FileUploadUtil.uploadUtil(bt, fileName, FileCenterEO.Type.Image.toString(), FileCenterEO.Code.PicNewsUpload.toString(), siteId, columnId, contentId, desc, sessionId);// mongoDbFileServer.uploadByteFile(bt, fileName, contentId, null);
            } else {
                mfvo = FileUploadUtil.uploadUtil(Filedata.getBytes(), fileName, FileCenterEO.Type.Image.toString(), FileCenterEO.Code.PicNewsUpload.toString(), siteId, columnId, contentId, desc, sessionId);// mongoDbFileServer.uploadByteFile(bt, fileName, contentId, null);
            }

            path = mfvo.getFileName();
            if ("gif".equals(suffix)) {
                thumbPath = path;
            } else {
                // 生成缩略图
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                ImgHander.ImgTrans(Filedata.getInputStream(), 160, 0, baos, suffix);
                byte[] b = baos.toByteArray();
                MongoFileVO _vo = FileUploadUtil.uploadUtil(b, fileName, FileCenterEO.Type.Image.toString(), FileCenterEO.Code.ThumbUpload.toString(), siteId, columnId, contentId, "轮播新闻缩略图", sessionId);//mongoDbFileServer.uploadByteFile(b, fileName, contentId, null);
                thumbPath = _vo.getFileName();
                baos.flush();
                baos.close();
            }
            ContentPicEO ts = new ContentPicEO();
            ts.setPicId(picId);
            ts.setCreateDate(new Date());
            ts.setCreateOrganId((Long) request.getSession().getAttribute("organId"));
            ts.setContentId(contentId);
            ts.setCreateUserId((Long) request.getSession().getAttribute("userId"));
            ts.setPicTitle(picName);
            ts.setRecordStatus("Normal");
            ts.setUpdateDate(new Date());
            ts.setPath(path);
            ts.setSiteId(siteId);
            ts.setThumbPath(thumbPath);
            ts.setColumnId(columnId);
            ts.setNum(SnowFlakeIdGenerater.instance.nextId());
            contentPicService.saveOrUpdateEntity(ts);
            if (contentId != null) {
                FileUploadUtil.saveFileCenterEO(new String[]{path, thumbPath});
            }
            return mfvo;
        } catch (Exception e) {
            e.printStackTrace();
            throw new BaseRunTimeException(TipsMode.Message.toString(), "失败");
        }
    }

    /**
     * @param picId
     * @return
     * @Description 删除图片
     * @author Hewbing
     * @date 2015年9月23日 上午11:35:36
     */
    @RequestMapping("delPic")
    @ResponseBody
    public Object delPic(Long picId) {
        contentPicService.delPic(picId);
        return getObject();

    }

    @RequestMapping("previewPic")
    public String previewPic(Long picId, ModelMap map) {
        map.put("picEO", contentPicService.getEntity(ContentPicEO.class, picId));
        return "/content/picturenews/pic_preview";
    }

    /**
     * @return
     * @Description 图片美化页
     * @author Hewbing
     * @date 2015年9月18日 下午2:14:58
     */
    @RequestMapping("beautifyPic")
    public String beautifyPic(Long picId, ModelMap map) {
        map.put("picEO", contentPicService.getEntity(ContentPicEO.class, picId));
        return "/content/picturenews/pic_beautify";
    }

    /**
     * @param Filedata
     * @param picId
     * @return
     * @Description 保存美化
     * @author Hewbing
     * @date 2015年9月23日 上午11:17:01
     */
    @RequestMapping("picBeautify")
    @ResponseBody
    public Object picBeautify(MultipartFile Filedata, Long siteId, Long columnId, Long contentId, Long picId, HttpServletRequest request) {
        String thumbPath = contentPicService.picBeautify(Filedata, siteId, columnId, contentId, picId, request);
        return thumbPath;
    }

    /**
     * @param picEO
     * @return
     * @Description 保存图片信息
     * @author Hewbing
     * @date 2015年9月24日 下午3:18:32
     */
    @RequestMapping("updatePicInfo")
    @ResponseBody
    public Object updatePicInfo(ContentPicEO picEO) {
        picEO.setUpdateDate(new Date());
        picEO.setUpdateUserId(LoginPersonUtil.getUserId());
        contentPicService.updatePicInfo(picEO);
        return getObject();
    }

    @SuppressWarnings("unchecked")
    @RequestMapping("savePicNews")
    @ResponseBody
    public Object savePicNews(String picList) {
        System.out.println(picList);
        List<ContentPicEO> list = JSONArray.parseArray(picList, ContentPicEO.class);
        contentPicService.allSavePic(list);
        return getObject();

    }

    @RequestMapping("getPage")
    @ResponseBody
    public Object getPage(ContentPageVO pageVO) {
        if (null != pageVO.getTitle()) {
            try {
                String str = new String(pageVO.getTitle().getBytes("ISO-8859-1"), "utf-8");
                pageVO.setTitle(str);
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }
        return getObject(baseContentService.getPageAndContent(pageVO));
    }

    @RequestMapping("updateNums")
    @ResponseBody
    public Object updateNums(Long picIds[],Long sortNums[]){
        if (null == picIds || picIds.length == 0) {
            return ajaxParamsErr("ids");
        }
        if (null == sortNums || sortNums.length == 0) {
            return ajaxParamsErr("nums");
        }
        if (picIds.length != sortNums.length) {
            return ajaxErr("id个数与排序号个数不一致");
        }
        contentPicService.updateNums(picIds,sortNums);
        return getObject();
    }

}
