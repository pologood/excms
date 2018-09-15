package cn.lonsun.content.interview.controller;


import cn.lonsun.activemq.MessageSenderUtil;
import cn.lonsun.content.internal.dao.IContentPicDao;
import cn.lonsun.content.internal.entity.BaseContentEO;
import cn.lonsun.content.internal.entity.ContentPicEO;
import cn.lonsun.content.internal.service.IBaseContentService;
import cn.lonsun.content.internal.service.IContentPicService;
import cn.lonsun.content.interview.internal.entity.InterviewInfoEO;
import cn.lonsun.content.interview.internal.service.IInterviewInfoService;
import cn.lonsun.content.interview.internal.service.impl.InterviewInfoServiceImpl;
import cn.lonsun.content.interview.vo.InterviewInfoVO;
import cn.lonsun.content.vo.ConvertMsg;
import cn.lonsun.content.vo.QueryVO;
import cn.lonsun.core.base.controller.BaseController;
import cn.lonsun.core.base.util.StringUtils;
import cn.lonsun.core.exception.BaseRunTimeException;
import cn.lonsun.core.util.Pagination;
import cn.lonsun.core.util.TipsMode;
import cn.lonsun.message.internal.entity.MessageEnum;
import cn.lonsun.message.internal.entity.MessageStaticEO;
import cn.lonsun.mongodb.vo.MongoFileVO;
import cn.lonsun.solr.SolrFactory;
import cn.lonsun.solr.vo.SolrIndexVO;
import cn.lonsun.system.filecenter.internal.entity.FileCenterEO;
import cn.lonsun.system.systemlog.internal.entity.CmsLogEO;
import cn.lonsun.util.*;
import org.apache.commons.io.FileUtils;
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

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static cn.lonsun.cache.client.CacheHandler.getEntity;


@Controller
@RequestMapping(value = "interviewInfo", produces = {"application/json;charset=UTF-8"})
public class InterviewInfoController extends BaseController {


    @Autowired
    private IContentPicDao contentPicDao;

    @Autowired
    private IInterviewInfoService interviewInfoService;

    @Autowired
    private IBaseContentService baseContentService;

    @Autowired
    private IContentPicService contentPicService;

    @Autowired
    private TaskExecutor taskExecutor;

    static Logger logger = LoggerFactory.getLogger(InterviewInfoServiceImpl.class);

    @Value("${ffmpeg.path}")
    private String path;


    @RequestMapping("index")
    public String index(Integer pageIndex, Model m) {
        m.addAttribute("pageIndex", pageIndex);
        return "/content/interview/interview_list";
    }

    @RequestMapping("edit")
    public String edit(Long interviewId,Integer pageIndex, Model m) {
        m.addAttribute("interviewId", interviewId);
        m.addAttribute("pageIndex", pageIndex);
        return "/content/interview/interview_edit";
    }

    @SuppressWarnings("unchecked")
    @RequestMapping("getPage")
    @ResponseBody
    public Object getPage(QueryVO query) {

        if (query.getColumnId() == null || query.getSiteId() == null) {
            throw new BaseRunTimeException(TipsMode.Message.toString(), "栏目、站点id不能为空");
        }
        if (query.getPageIndex() == null || query.getPageIndex() < 0) {
            query.setPageIndex(0L);
        }
        Integer size = query.getPageSize();
        if (size == null || size <= 0 || size > Pagination.MAX_SIZE) {
            query.setPageSize(15);
        }
        query.setTypeCode(BaseContentEO.TypeCode.interviewInfo.toString());
        Pagination page = interviewInfoService.getPage(query);
        if (page.getData() != null && page.getData().size() > 0) {
            for (InterviewInfoVO interviewInfo : (List<InterviewInfoVO>) page.getData()) {
                if (interviewInfo.getStartTime() != null && interviewInfo.getEndTime() != null) {
                    interviewInfo.setIsTimeOut(TimeOutUtil.getTimeOut(interviewInfo.getStartTime(), interviewInfo.getEndTime()));
                }
            }
        }
        return getObject(page);
    }

    @RequestMapping("save")
    @ResponseBody
    public Object save(InterviewInfoVO interviewInfoVO, String picList) {
        String prePath = "";
        if (!interviewInfoVO.getVideoStatus().equals(100) && interviewInfoVO.getIssued() == 1) {//
            return ajaxErr("文章内容中有视频未转换，不能进行发布操作！");
        }
        if (interviewInfoVO.getQuoteStatus().equals(100) && interviewInfoVO.getIssued() == 1) {//
            return ajaxErr("上传视频中有视频未转换，不能进行发布操作！");
        }
        if (interviewInfoVO.getContentId() != null) {
            BaseContentEO content = baseContentService.getEntity(BaseContentEO.class, interviewInfoVO.getContentId());
            prePath = StringUtils.isEmpty(content.getContentPath()) ? "" : content.getContentPath();
        }
        BaseContentEO content = interviewInfoService.save(interviewInfoVO, picList);
        if ((content != null && content.getQuoteStatus().equals(100) && !StringUtils.isEmpty(content.getContentPath())
                && !prePath.equals(content.getContentPath())) || !content.getVideoStatus().equals(100)) {
            final Long contentId = content.getId();
            VideoToMp4ConvertUtil.transfer(path, content.getColumnId(), contentId);
        }

        if (content.getIsPublish() == 2) {
            try {
                final Long siteIdR = content.getSiteId();
                final Long columnIdR = content.getColumnId();
                final Long contentIdR = content.getId();
                //创建索引
                SolrIndexVO vo = new SolrIndexVO();
                vo.setId(contentIdR + "");
                vo.setTitle(content.getTitle());
                vo.setTypeCode(BaseContentEO.TypeCode.interviewInfo.toString());
                vo.setColumnId(columnIdR);
                vo.setSiteId(siteIdR);
                vo.setContent(HtmlUtil.getTextFromTHML(interviewInfoVO.getDesc()));
                vo.setCreateDate(content.getPublishDate());
                try {
                    SolrFactory.deleteIndex(contentIdR + "");
                    SolrFactory.createIndex(vo);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                MessageSenderUtil.publishContent(new MessageStaticEO(vo.getSiteId(), vo.getColumnId(), new Long[]{contentIdR}).setType(MessageEnum.PUBLISH.value()), 1);
            } catch (Exception e) {
                e.printStackTrace();
                System.out.println("信息发布失败");
            }
        }
        return getObject();
    }

    @RequestMapping("updateIsOpen")
    @ResponseBody
    public Object updateIsOpen(@RequestParam("ids") Long[] ids,
                               Integer isOpen) {
        if (ids != null && ids.length > 0) {
            for (Long id : ids) {
                InterviewInfoEO interviewInfo = interviewInfoService.getEntity(InterviewInfoEO.class, id);
                if (interviewInfo != null) {
                    if (interviewInfo.getIsOpen() != isOpen) {
                        if (isOpen == 1) {
                            interviewInfo.setOpenTime(new Date());
                        } else {
                            interviewInfo.setOpenTime(null);
                        }
                        interviewInfo.setIsOpen(isOpen);
                        interviewInfoService.updateEntity(interviewInfo);
                    }
                }
            }
        }
        return getObject();
    }

    @RequestMapping("updateType")
    @ResponseBody
    public Object updateType(@RequestParam("ids") Long[] ids,
                             Integer type) {
        if (ids != null && ids.length > 0) {
            for (Long id : ids) {
                InterviewInfoEO interviewInfo = interviewInfoService.getEntity(InterviewInfoEO.class, id);
                if (interviewInfo != null) {
                    if (interviewInfo.getType() != type) {
                        interviewInfo.setType(type);
                        interviewInfoService.updateEntity(interviewInfo);
                    }
                }
            }
        }
        return getObject();
    }

    @RequestMapping("getInterviewInfo")
    @ResponseBody
    public Object getInterviewInfo(Long interviewId, Long siteId, Long columnId) {
        InterviewInfoVO interviewInfo = null;
        if (interviewId == null) {
            interviewInfo = new InterviewInfoVO();
            Long sortNum = baseContentService.getMaxSortNum(siteId, columnId, BaseContentEO.TypeCode.interviewInfo.toString());
            if (sortNum == null) {
                sortNum = 2L;
            } else {
                sortNum = sortNum + 2;
            }
            interviewInfo.setSortNum(sortNum);
            interviewInfo.setPics(new ArrayList<ContentPicEO>());
        } else {
            interviewInfo = interviewInfoService.getInterviewInfoVO(interviewId);
            if (interviewInfo != null && interviewInfo.getContentId() != null) {
                interviewInfo.setPics(contentPicService.getPicsList(interviewInfo.getContentId()));
            }
        }
        return getObject(interviewInfo);
    }


    @RequestMapping("delete")
    @ResponseBody
    public Object delete(@RequestParam("ids") Long[] ids, @RequestParam("contentIds") Long[] contentIds) {
        if (ids == null || ids.length < 1) {
            throw new BaseRunTimeException(TipsMode.Message.toString(), "请选择要删除的项！");
        }

        // 批量删除主表（假删）
        List<BaseContentEO> list = baseContentService.getEntities(BaseContentEO.class, contentIds);
        if (list != null && list.size() > 0) {
            Integer isPublish=0;
            for(BaseContentEO contentEO:list){
                if(contentEO!=null&&contentEO.getIsPublish()!=null&&contentEO.getIsPublish().intValue()==1){
                    isPublish=1;
                    break;
                }
                //添加操作日志
                if(list.size()>1){
                    SysLog.log("在线访谈：批量删除内容（" + contentEO.getTitle()+"）", "InterviewInfoEO",
                            CmsLogEO.Operation.Update.toString());
                }else{
                    SysLog.log("在线访谈：删除内容（" + contentEO.getTitle()+"）", "InterviewInfoEO",
                            CmsLogEO.Operation.Update.toString());
                }
            }
            BaseContentEO baseContentEO = getEntity(BaseContentEO.class, contentIds[0]);
            interviewInfoService.delete(ids, contentIds);
            if(isPublish!=null&&isPublish.intValue()==1){
                MessageSenderUtil.publishContent(
                        new MessageStaticEO(baseContentEO.getSiteId(), baseContentEO.getColumnId(), contentIds).setType(MessageEnum.UNPUBLISH.value()), 2);

            }
        }
        return getObject();
    }


    private void transVideo(Long contentIdR) throws IOException {
        BaseContentEO contentEO = baseContentService.getEntity(BaseContentEO.class, contentIdR);
        if (contentEO != null) {
            InterviewInfoEO interview = interviewInfoService.getInterviewInfoByContentId(contentIdR);
            if (interview != null) {
                String videoPath = contentEO.getContentPath();
                String videoName = interview.getLiveLink();
                File file = new File(videoPath);
                String flvPrefix = videoName;
                if (StringUtils.isEmpty(flvPrefix) || flvPrefix.length() < 3) {
                    flvPrefix = flvPrefix + "_tmp";
                }
                File tempFile = File.createTempFile(flvPrefix, ".mp4");
                // 视频转换
                ConvertMsg msg = LocalConvertVideo.processFLV(videoPath, tempFile.getPath(), 0, path);
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
                    fileCenter.setSiteId(contentEO.getSiteId());
                    fileCenter.setColumnId(contentEO.getColumnId());
                    fileCenter.setContentId(contentIdR);
                    fileCenter.setCreateDate(new Date());
                    fileCenter.setCreateUserId(contentEO.getCreateUserId());
                    fileCenter.setCreateOrganId(contentEO.getCreateOrganId());
                    fileCenter.setUpdateDate(new Date());
                    fileCenter.setDesc("视频上传");
                    fileCenter.setRecordStatus("Normal");
                    logger.error("=============>  开始上传 <=====================");
                    logger.error("====================><=========================" + fileByte.length);
                    if (fileByte.length > 0) {
                        mongvo = FileUploadUtil.uploadUtil(fileByte, fileCenter);
                        if (mongvo.getMongoId() != null) {
                            FileUploadUtil.setStatus(mongvo.getMongoId(), 1, contentIdR, contentEO.getColumnId(), contentEO.getSiteId());
                            logger.error("=============>  mongoId <=====================" + mongvo.getMongoId());
                            contentEO.setQuoteStatus(1);
                            contentEO.setContentPath(mongvo.getMongoId());
                        }
                    } else {
                        contentEO.setQuoteStatus(-1);
                        logger.error("=============>  视频字节为0 <=====================");
                    }
                } else {
                    contentEO.setQuoteStatus(-1);
                }
                baseContentService.updateEntity(contentEO);
                file.delete();//删除保存到本地的文件
                tempFile.delete();//删除在本地生成的转换文件
            }
        }
    }
}
