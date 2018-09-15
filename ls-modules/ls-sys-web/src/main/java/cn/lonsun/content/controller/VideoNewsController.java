package cn.lonsun.content.controller;

import cn.lonsun.activemq.MessageSender;
import cn.lonsun.activemq.MessageSenderUtil;
import cn.lonsun.cache.client.CacheHandler;
import cn.lonsun.common.util.AppUtil;
import cn.lonsun.common.util.FileUtil;
import cn.lonsun.content.internal.entity.BaseContentEO;
import cn.lonsun.content.internal.entity.VideoNewsEO;
import cn.lonsun.content.internal.service.IVideoNewsService;
import cn.lonsun.content.vo.ContentPageVO;
import cn.lonsun.content.vo.ConvertMsg;
import cn.lonsun.content.vo.VideoNewsVO;
import cn.lonsun.core.base.controller.BaseController;
import cn.lonsun.core.base.entity.AMockEntity;
import cn.lonsun.core.base.util.StringUtils;
import cn.lonsun.core.enums.SystemCodes;
import cn.lonsun.core.exception.BaseRunTimeException;
import cn.lonsun.core.util.Pagination;
import cn.lonsun.core.util.SpringContextHolder;
import cn.lonsun.core.util.TipsMode;
import cn.lonsun.job.timingjob.jobimpl.NewsIssueTaskImpl;
import cn.lonsun.job.util.ScheduleJobUtil;
import cn.lonsun.message.internal.entity.MessageEnum;
import cn.lonsun.message.internal.entity.MessageStaticEO;
import cn.lonsun.message.internal.entity.MessageSystemEO;
import cn.lonsun.mongodb.vo.MongoFileVO;
import cn.lonsun.site.contentModel.vo.ColumnTypeConfigVO;
import cn.lonsun.site.site.internal.entity.WaterMarkConfigEO;
import cn.lonsun.site.site.internal.service.IWaterMarkConfigService;
import cn.lonsun.system.filecenter.internal.entity.FileCenterEO;
import cn.lonsun.util.*;
import org.apache.commons.io.FileUtils;
import org.hibernate.SessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.task.TaskExecutor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * <br/>
 *
 * @author wangss <br/>
 * @version v1.0 <br/>
 * @date 2015-10-21<br/>
 */
@Controller
@RequestMapping(value = "videoNews")
public class VideoNewsController extends BaseController {
    @Autowired
    private IVideoNewsService videoService;

    @Autowired
    private IWaterMarkConfigService waterMarkConfigService;

    @Autowired
    private TaskExecutor taskExecutor;

    static Logger logger = LoggerFactory.getLogger(VideoNewsController.class);

    @Value("${ffmpeg.path}")
    private String path;

    @RequestMapping("index")
    public String getList(Long pageIndex, Model model) {
        if (pageIndex == null) pageIndex = 0L;
        model.addAttribute("pageIndex", pageIndex);
        return "/content/video_news/video_news_list";
    }

    /**
     * 去往编辑页面
     *
     * @param id
     * @param model
     * @return
     */
    @RequestMapping("editVideo")
    public String editVideo(@RequestParam(value = "id", required = false, defaultValue = "") Long id, Long pageIndex, Model model) {
        if (id == null) {
            model.addAttribute("baseId", "");
        } else {
            model.addAttribute("baseId", id);
        }
        model.addAttribute("recordStatus", AMockEntity.RecordStatus.Normal.toString());
        model.addAttribute("pageIndex", pageIndex);
        SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String str1 = sdf1.format(new Date());
        model.addAttribute("nowDate", str1);
        return "/content/video_news/video_news_edit";
    }

    @RequestMapping("getVideoEO")
    @ResponseBody
    public Object getVideoEO(Long id, String status) {
        if (id == null) {//新增
            VideoNewsVO vo = new VideoNewsVO();
            vo.setPublishDate(new Date());
            //vo.setAuthor(LoginPersonUtil.getUserName());
            vo.setTypeCode(BaseContentEO.TypeCode.videoNews.toString());
            return getObject(vo);
        }
        VideoNewsVO vo = videoService.getVideoEO(id, status);
        if (vo == null) {
            throw new BaseRunTimeException(TipsMode.Message.toString(), "该视频新闻不存在");
        }

        return getObject(vo);
    }


    /**
     * 获取视频新闻分页
     *
     * @return
     */
    @RequestMapping("getPage")
    @ResponseBody
    public Object getPage(ContentPageVO pageVO) {

        Pagination page = videoService.getPage(pageVO);
        return getObject(page);
    }

    /**
     * 保存视频新闻
     *
     * @param vo
     * @return
     */
    @RequestMapping("saveVideoNews")
    @ResponseBody
    public Object saveVideoNews(VideoNewsVO vo) {
        String name = vo.getVideoName();
        String videoPath = vo.getVideoPath();
        if (StringUtils.isEmpty(vo.getTitle())) {
            throw new BaseRunTimeException(TipsMode.Message.toString(), "标题不能为空");
        }
        if (StringUtils.isEmpty(name) || StringUtils.isEmpty(videoPath)) {
            throw new BaseRunTimeException(TipsMode.Message.toString(), "视频新闻路径不能为空");
        }
        if (vo.getStatus().equals(0) && vo.getIsPublish() == 1) {//
            return ajaxErr("文章中有视频未转换，不能进行发布操作！");
        }
        String type = vo.getFileType();
        String prePath = "";
        if (vo.getVideoId() != null) {
            VideoNewsEO videoEO = videoService.getEntity(VideoNewsEO.class, vo.getVideoId());
            if (videoEO != null) {
                prePath = videoEO.getVideoPath();
            }
        }
        VideoNewsEO eo = videoService.saveVideo(vo);
        if (!StringUtils.isEmpty(type) && !videoPath.equals(prePath)) {
            VideoToMp4ConvertUtil.transfer(path, eo.getColumnId(), eo.getContentId());
        }

        // 设置定时发布
        Integer isJob = 0;
        if (vo.getIsJob() == 1 && vo.getJobIssueDate() != null) {
            isJob = 1;
        }
        ScheduleJobUtil.addOrDelScheduleJob("新闻定时发布日期", NewsIssueTaskImpl.class.getName(), isJob == 0 ? null : ScheduleJobUtil.dateToCronExpression(vo.getJobIssueDate()),
                String.valueOf(vo.getId()), isJob);
        logger.error("============>  保存成功<====================");

        MessageStaticEO messageStaticEO = new MessageStaticEO(vo.getSiteId(), vo.getColumnId(), new Long[]{eo.getContentId()});
        messageStaticEO.setUserId(LoginPersonUtil.getUserId());
        if (vo.getIsPublish() == 1) {
            MessageSenderUtil.publishContent(messageStaticEO.setType(MessageEnum.PUBLISH.value()), 1);
        } else {
            if (vo.getId() != null) {
                MessageSenderUtil.publishContent(messageStaticEO.setType(MessageEnum.UNPUBLISH.value()), 2);
            }
        }
        return getObject(0);
    }

    /**
     * 删除视频新闻
     *
     * @param id
     * @return
     */
    @RequestMapping("delVideoEO")
    @ResponseBody
    public Object delVideoEO(Long id) {
        if (id == null) {
            throw new BaseRunTimeException(TipsMode.Message.toString(), "请选择要删除的新闻");
        }
        String message = videoService.delVideoEO(id);
        MessageSenderUtil.unPublishCopyNews(message);

        return getObject(0);
    }

    /**
     * 删除视频新闻
     *
     * @param
     * @return
     */
    @RequestMapping("delVideoEOs")
    @ResponseBody
    public Object delVideoEOs(String ids) {
        if (StringUtils.isEmpty(ids)) {
            throw new BaseRunTimeException(TipsMode.Message.toString(), "请选择要删除的新闻");
        }
        Long[] idsarr = StringUtils.getArrayWithLong(ids, ",");
        String returnStr = videoService.delVideoEOs(idsarr);
        if (!AppUtil.isEmpty(returnStr)) {
            String[] message = returnStr.split("_");
            Long siteId = Long.parseLong(message[0]);
            Long columnId = Long.parseLong(message[1]);
            // Long[] publishIds = (Arrays.asList(message[2].split(","))).toArray(new Long[message[2].length()]);
            MessageSenderUtil.publishContent(new MessageStaticEO(siteId, columnId, idsarr).setType(MessageEnum.UNPUBLISH.value()), 2);
        }
        return getObject(0);
    }

    /**
     * 去往图片上传页面
     *
     * @return
     */
    @RequestMapping("uploadPic")
    public String uploadPic() {
        return "/content/video_news/pic_upload";
    }

    /**
     * 上传新闻缩略图
     *
     * @param Filedata
     * @param siteId
     * @param columnId
     * @param contentId
     * @param imageLink
     * @return
     * @throws UnsupportedEncodingException
     */
    @RequestMapping("uploadAttachment")
    @ResponseBody
    public Object uploadAttachment(MultipartFile Filedata, Long siteId, Long columnId, Long contentId, String imageLink)
            throws UnsupportedEncodingException {
        if (null == siteId) {
            return ajaxErr("站点不能为空");
        }
        if (null == columnId) {
            return ajaxErr("栏目不能为空");
        }
        FileUploadUtil.deleteFileCenterEO(imageLink);
        String fileName = Filedata.getOriginalFilename();
        String fileSuffix = FileUtil.getType(fileName);
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

        if (!AppUtil.isEmpty(modelVO.getPicWidth())) {
            width = modelVO.getPicWidth();
        }
        if (!AppUtil.isEmpty(modelVO.getPicHeight())) {
            heigth = modelVO.getPicHeight();
        }

        byte[] b = null;
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        if (eo != null && eo.getEnableStatus() == 1 && modelVO != null && modelVO.getIsWater() == 1) {
            /*byte[] by = WaterMarkUtil.createWaterMark(in, siteId, fileSuffix);
            InputStream inNew = new ByteArrayInputStream(by);
            ImgHander.ImgTrans(inNew, width, heigth, baos, fileSuffix);*/

            ImgHander.ImgTrans(in, width, heigth, baos, fileSuffix);
            in = new ByteArrayInputStream(baos.toByteArray());
            b = WaterMarkUtil.createWaterMark(in, siteId, fileSuffix);

        } else {
            ImgHander.ImgTrans(in, width, heigth, baos, fileSuffix);
            b = baos.toByteArray();
        }

        MongoFileVO mongvo = FileUploadUtil.uploadUtil(b, fileName, FileCenterEO.Type.Image.toString(), FileCenterEO.Code.ThumbUpload.toString(), siteId, columnId,
                contentId, "新闻缩略图", LoginPersonUtil.getRequest());

        return mongvo.getFileName();
    }

    /**
     * 上传视频的页面（废用）
     *
     * @param columnId
     * @param videoPathPre
     * @param model
     * @return
     */
    @RequestMapping("toVideoUpload")
    public String toVideoUpload(Long columnId, String videoPathPre, Model model) {
        model.addAttribute("columnId", columnId);
        if (AppUtil.isEmpty(videoPathPre)) {
            model.addAttribute("videoPathPre", videoPathPre);
        }
        return "/content/video_news/video_upload";
    }


    /**
     * 上传视频
     *
     * @param Filedata
     * @return
     */
    @RequestMapping("uploadVideo")
    @ResponseBody
    public Object uploadVideo(MultipartFile Filedata, Long siteId, Long columnId, Long contentId) {
        String fileName = Filedata.getOriginalFilename();
        File file = VideoToMp4ConvertUtil.writeToLocalFile(Filedata);
        MongoFileVO vo = new MongoFileVO();
        vo.setFileName(fileName);
        vo.setMongoId(file.getPath());
        return vo;
    }

    /**
     * 转换视频
     *
     * @param
     * @throws IOException
     */
    @RequestMapping("transVideo")
    @ResponseBody
    public Object transVideo(Long videoId,Long userId) throws IOException {
        VideoNewsEO videoEO = videoService.getEntity(VideoNewsEO.class, videoId);
        if (videoEO == null) {
            throw new BaseRunTimeException(TipsMode.Message.toString(), "视频配置项不存在！");
        }
        videoEO.setStatus(0);
        videoService.updateEntity(videoEO);
        BaseContentEO contentEO = CacheHandler.getEntity(BaseContentEO.class, videoEO.getContentId());
        if (contentEO == null) {
            throw new BaseRunTimeException(TipsMode.Message.toString(), "视频新闻不存在！");
        }
        String videoPath = videoEO.getVideoPath();
        String videoName = videoEO.getVideoName();
        File file = new File(videoPath);
        String flvPrefix = videoName;
        if (StringUtils.isEmpty(flvPrefix) || flvPrefix.length() < 3) {
            flvPrefix = flvPrefix + "_tmp";
        }
        File tempFile = File.createTempFile(flvPrefix, ".mp4");
        ConvertMsg msg = null;
        MessageSystemEO message = new MessageSystemEO();
        message.setSiteId(videoEO.getSiteId());
        message.setColumnId(videoEO.getColumnId());
        message.setMessageType(MessageSystemEO.TIP);
        message.setModeCode(BaseContentEO.TypeCode.videoNews.toString());
        if (videoEO.getUpdateUserId() != null) {
            message.setRecUserIds(videoEO.getUpdateUserId() + "");
        }
        message.setRecUserIds(userId.toString());
        //message.setRecOrganIds(videoEO.getCreateOrganId() + "");
        //message.setCreateOrganId(videoEO.getCreateOrganId());
        message.setCreateUserId(videoEO.getUpdateUserId());
        message.setLink("/videoNews/videoPlayer?id=" + videoEO.getContentId());
        message.setResourceId(videoEO.getContentId());
        // 视频转换
        try {
            msg = LocalConvertVideo.processFLV(videoPath, tempFile.getPath(), 0, path);
        } catch (Exception e) {
            videoService.failChange(videoId);
            message.setTitle(contentEO.getTitle() + "转换失败");
            message.setMessageStatus(MessageSystemEO.MessageStatus.error.toString());
            MessageSender.sendMessage(message);
            videoService.failChange(videoId);
            return getObject();
        }
        MongoFileVO mongvo = null;
        if (msg.getStatus() == 1) {
            // 上传
            byte[] fileByte = FileUtils.readFileToByteArray(tempFile);

            FileCenterEO fileCenter = new FileCenterEO();
            fileCenter.setStatus(1);
            fileCenter.setFileName(videoName);
            fileCenter.setSuffix("mp4");
            fileCenter.setType(FileCenterEO.Type.Video.toString());
            fileCenter.setCode(BaseContentEO.TypeCode.videoNews.toString());
            fileCenter.setStatus(0);
            fileCenter.setSiteId(videoEO.getSiteId());
            fileCenter.setColumnId(videoEO.getColumnId());
            fileCenter.setContentId(videoEO.getContentId());
            fileCenter.setCreateDate(new Date());
            fileCenter.setCreateUserId(videoEO.getCreateUserId());
            fileCenter.setCreateOrganId(videoEO.getCreateOrganId());
            fileCenter.setUpdateDate(new Date());
            fileCenter.setDesc("视频上传");
            fileCenter.setRecordStatus("Normal");
            logger.error("=============>  开始上传 <=====================");
            logger.error("====================><=========================" + fileByte.length);
            if (fileByte.length > 0) {
                mongvo = FileUploadUtil.uploadUtil(fileByte, fileCenter);
                if (mongvo.getMongoId() != null) {
                    FileUploadUtil.setStatus(mongvo.getMongoId(), 1, videoEO.getContentId(), contentEO.getColumnId(), contentEO.getSiteId());
                    logger.error("=============>  mongoId <=====================" + mongvo.getMongoId());
                }
                videoService.changeStatus(videoId, mongvo.getMongoId());
                logger.error("=============>  状态改变 <=====================");
                message.setTitle(contentEO.getTitle() + "转换成功");
                message.setMessageStatus(MessageSystemEO.MessageStatus.success.toString());
                MessageSender.sendMessage(message);
                file.delete();//删除保存到本地的文件

            } else {
                logger.error("=============>  视频字节为0 <=====================");
                message.setTitle(contentEO.getTitle() + "转换失败");
                message.setMessageStatus(MessageSystemEO.MessageStatus.error.toString());
                MessageSender.sendMessage(message);
                videoService.failChange(videoId);
            }
        } else {
            //videoService.changeStatus(videoId, mongvo.getMongoId());
            message.setTitle(contentEO.getTitle() + "转换失败");
            message.setMessageStatus(MessageSystemEO.MessageStatus.error.toString());
            MessageSender.sendMessage(message);
            videoService.failChange(videoId);
        }
        tempFile.delete();//删除在本地生成的转换文件
        MessageStaticEO messageStaticEO =  new MessageStaticEO(contentEO.getSiteId(), contentEO.getColumnId(), new Long[]{contentEO.getId()});
        messageStaticEO.setUserId(userId);
        if (contentEO.getIsPublish() == 1) {
            //处理保存时发布,正式部署时启用
            boolean rel = MessageSenderUtil.publishContent(messageStaticEO.setType(MessageEnum.PUBLISH.value()), 1);
            if (!rel) {
                throw new BaseRunTimeException(TipsMode.Message.toString(), "保存成功,发布失败");
            }
        } else {
            if (contentEO.getId() != null) {
                MessageSenderUtil.publishContent(messageStaticEO.setType(MessageEnum.UNPUBLISH.value()), 2);
            }
        }
        return getObject(mongvo);
    }

    /**
     * 视频再次转换
     *
     * @param videoId
     * @return
     */
    @RequestMapping("transferById")
    @ResponseBody
    public Object transferById(final Long videoId) {
        final Long userId = LoginPersonUtil.getUserId();
        taskExecutor.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    logger.error("==============>  开始转换 <================");
                    // 绑定session至当前线程中
                    SessionFactory sessionFactory = SpringContextHolder.getBean(SessionFactory.class);
                    boolean participate = ConcurrentUtil.bindHibernateSessionToThread(sessionFactory);
                    transVideo(videoId,userId);
                    // 关闭session
                    ConcurrentUtil.closeHibernateSessionFromThread(participate, sessionFactory);
                    logger.error("=============>  转换完成 <=====================");
                } catch (IOException e) {
                    logger.error("============>  IOException<=====================");
                    e.printStackTrace();
                }
            }
        });
        return getObject();
    }

    /**
     * 视频转换
     *
     * @param
     * @return
     * @throws IOException
     */
    @RequestMapping("videoChange")
    @ResponseBody
    public Object videoChange(String videoPath, String videoName, Long columnId, Long contentId) throws IOException {
        if (StringUtils.isEmpty(videoPath) || StringUtils.isEmpty(videoName)) {
            return ajaxErr("视频路径不能为空！");
        }
        File file = new File(videoPath);
        String flvPrefix = videoName;
        if (StringUtils.isEmpty(flvPrefix) || flvPrefix.length() < 3) {
            flvPrefix = flvPrefix + "_tmp";
        }
        File tempFile = File.createTempFile(flvPrefix, ".mp4");
        // 视频转换
        ConvertMsg msg = LocalConvertVideo.processFLV(videoPath, tempFile.getPath(), 0, path);
        MongoFileVO mongvo = null;
        if (msg.getStatus() == 1) {//转换成功
            // 上传
            byte[] fileByte = FileUtils.readFileToByteArray(tempFile);
            FileCenterEO fileCenter = new FileCenterEO();
            fileCenter.setStatus(1);
            fileCenter.setFileName(videoName);
            fileCenter.setSuffix("mp4");
            fileCenter.setType(FileCenterEO.Type.EditorUpload.toString());
            fileCenter.setCode(BaseContentEO.TypeCode.videoNews.toString());
            fileCenter.setSiteId(LoginPersonUtil.getSiteId());
            fileCenter.setColumnId(columnId);
            fileCenter.setContentId(contentId);
            fileCenter.setCreateDate(new Date());
            fileCenter.setCreateUserId(LoginPersonUtil.getUserId());
            fileCenter.setCreateOrganId(LoginPersonUtil.getOrganId());
            fileCenter.setUpdateDate(new Date());
            fileCenter.setDesc("视频上传");
            fileCenter.setRecordStatus("Normal");
            logger.error("=============>  开始上传 <=====================");
            logger.error("====================><=========================" + fileByte.length);
            if (fileByte.length > 0) {
                mongvo = FileUploadUtil.uploadUtil(fileByte, fileCenter);
                if (mongvo.getMongoId() != null) {
                    logger.error("=============>  mongoId <=====================" + mongvo.getMongoId());
                }
            } else {
                logger.error("=============>  视频字节为0 <=====================");
            }
        } else {
            logger.error("=============>  视频转换失败 <=====================");
        }
        file.delete();//删除保存到本地的文件
        tempFile.delete();//删除在本地生成的转换文件
        return getObject(mongvo);
    }

    /**
     * 转换后在编辑页面上播放视频
     *
     * @param videoPath
     * @param videoName
     * @param model
     * @return
     */
    @RequestMapping("playAfterTrans")
    public String playAfterTrans(String videoPath, String videoName, String editor, Model model) {
        if (videoPath == null || videoPath == "") {
            model.addAttribute("videoPath", "");
            model.addAttribute("videoName", "");
        } else {
            model.addAttribute("videoName", videoName);
            model.addAttribute("videoPath", videoPath);
        }
        if (editor == null || editor.equals("null")) {
            model.addAttribute("editor", "");
        } else {
            model.addAttribute("editor", editor);
        }

        return "content/video_news/video_player";
    }

    /**
     * 列表页上播放视频
     *
     * @param id
     * @param model
     * @return
     */
    @RequestMapping("videoPlayer")
    public String videoPlayer(Long id, Model model) {
        if (id != null) {
            VideoNewsVO vo = videoService.getVideoEO(id, AMockEntity.RecordStatus.Normal.toString());
            // videoService.changeHit(id);
            if (vo != null) {
                if (vo.getVideoPath() == null || "".equals(vo.getVideoPath())) {
                    model.addAttribute("videoPath", "");
                    model.addAttribute("videoName", "");
                    model.addAttribute("editor", "");
                } else {
                    model.addAttribute("videoName", vo.getVideoName());
                    model.addAttribute("videoPath", vo.getVideoPath());
                    model.addAttribute("editor", vo.getEditor());
                }
            }

        }
        return "content/video_news/video_player";
    }

    /**
     * 编辑器上传
     *
     * @param request
     * @param response
     */
    @RequestMapping("upload")
    public void uploadFiles(HttpServletRequest request,
                            HttpServletResponse response) {
        EditorUploadUtil.uploadByKindEditor(SystemCodes.contentMgr, request, response);
    }

    /**
     * 根据contentId获取视频状态
     *
     * @param id
     * @return
     */
    @RequestMapping("getStatusById")
    @ResponseBody
    public Object getStatusById(Long id) {
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("contentId", id);
        VideoNewsEO eo = videoService.getEntity(VideoNewsEO.class, map);
        if (eo != null) {
            return getObject(eo.getStatus());
        }
        return getObject();
    }


    /**
     * 返回当前栏目留言条数
     *
     * @param columnId
     * @return
     */
    @RequestMapping("countData")
    public Long countData(Long columnId) {
        return videoService.countData(columnId);
    }


}



