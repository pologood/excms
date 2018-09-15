package cn.lonsun.content.internal.service.impl;

import cn.lonsun.activemq.MessageSender;
import cn.lonsun.activemq.MessageSenderUtil;
import cn.lonsun.cache.client.CacheHandler;
import cn.lonsun.common.util.AppUtil;
import cn.lonsun.content.commentMgr.internal.service.ICommentService;
import cn.lonsun.content.internal.dao.IVideoNewsDao;
import cn.lonsun.content.internal.entity.BaseContentEO;
import cn.lonsun.content.internal.entity.VideoNewsEO;
import cn.lonsun.content.internal.service.IBaseContentService;
import cn.lonsun.content.internal.service.IContentReferRelationService;
import cn.lonsun.content.internal.service.IVideoNewsService;
import cn.lonsun.content.vo.ContentPageVO;
import cn.lonsun.content.vo.ConvertMsg;
import cn.lonsun.content.vo.SortVO;
import cn.lonsun.content.vo.VideoNewsVO;
import cn.lonsun.core.base.service.impl.MockService;
import cn.lonsun.core.exception.BaseRunTimeException;
import cn.lonsun.core.util.Pagination;
import cn.lonsun.core.util.SpringContextHolder;
import cn.lonsun.core.util.TipsMode;
import cn.lonsun.job.timingjob.jobimpl.NewsIssueTaskImpl;
import cn.lonsun.job.util.ScheduleJobUtil;
import cn.lonsun.message.internal.entity.MessageEnum;
import cn.lonsun.message.internal.entity.MessageStaticEO;
import cn.lonsun.message.internal.entity.MessageSystemEO;
import cn.lonsun.mongodb.entity.ContentMongoEO;
import cn.lonsun.mongodb.service.ContentMongoServiceImpl;
import cn.lonsun.mongodb.vo.MongoFileVO;
import cn.lonsun.site.site.internal.entity.ColumnMgrEO;
import cn.lonsun.system.filecenter.internal.entity.FileCenterEO;
import cn.lonsun.system.systemlog.internal.entity.CmsLogEO;
import cn.lonsun.util.*;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.SessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.task.TaskExecutor;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 视频新闻Service实现类<br/>
 *
 * @author wangss <br/>
 * @version v1.0 <br/>
 * @date 2015-10-21<br/>
 */
@Service("videoNewsService")
public class VideoNewsServiceImpl extends MockService<VideoNewsEO> implements IVideoNewsService {

    @Autowired
    private IVideoNewsDao videoDao;

    @Autowired
    private IBaseContentService contentService;

    @Autowired
    private ContentMongoServiceImpl contentMongoService;

    @Autowired
    private TaskExecutor taskExecutor;
    @Autowired
    private IContentReferRelationService contentReferRelationService;
    @Autowired
    private ICommentService commentService;


    @Value("${ffmpeg.path}")
    private String path;

    static Logger logger = LoggerFactory.getLogger(VideoNewsServiceImpl.class);


    @Override
    public String delVideoEO(Long id) {
        String returnStr = "";

        BaseContentEO baseEO = contentService.getEntity(BaseContentEO.class, id);
        if(baseEO!=null){

            Long[] idArr=new Long[]{id};
            FileUploadUtil.markByContentIds(idArr, 0);
            contentReferRelationService.delteByReferId(idArr);// 删除引用关系
           // FileUploadUtil.deleteFileCenterEO(baseEO.getImageLink());
            commentService.deleteByContent(idArr);
            if(baseEO.getIsPublish()==1){
                baseEO.setIsPublish(2);//状态改为“发布中”中间状态
                returnStr = baseEO.getSiteId()+"_"+baseEO.getColumnId()+"_"+baseEO.getId();
            }
            contentService.delete(baseEO);

            CacheHandler.delete(BaseContentEO.class,baseEO);
            SysLog.log("删除视频新闻 ：栏目（" + ColumnUtil.getColumnName(baseEO.getColumnId(), baseEO.getSiteId()) + "），标题（" + baseEO.getTitle()+"）",
                    "BaseContentEO", CmsLogEO.Operation.Delete.toString());
        }

        return returnStr;

    }

    @Override
    public Pagination getPage(ContentPageVO pageVO) {
        return videoDao.getPage(pageVO);
    }

    @Override
    public List<VideoNewsVO> getListForPublish(Long columnId) {
        return videoDao.getListForPublish(columnId);
    }

    @Override
    public VideoNewsVO getEntityForPublish(Long contentId) {
        return videoDao.getEntityForPublish(contentId);
    }


    @Override
    public VideoNewsVO getVideoEO(Long id,String status) {
        VideoNewsVO videoNewsVO=videoDao.getVideoEO(id,status);
        if(videoNewsVO!=null){
            Criteria criteria = Criteria.where("_id").is(id);
            Query query = new Query(criteria);
            ContentMongoEO _eo = contentMongoService.queryOne(query);
            String article = "";
            if (!AppUtil.isEmpty(_eo)) {
                article = _eo.getContent();
            }
            videoNewsVO.setArticle(article);
        }
        return videoNewsVO;

    }
    
    @Override
    public VideoNewsVO getRemovedVideo(Long id) {
        return videoDao.getRemovedVideo(id);
    }

    @Override
    public VideoNewsEO saveVideo(VideoNewsVO vo) {
        BaseContentEO baseEO = new BaseContentEO();
        VideoNewsEO videoEO = new VideoNewsEO();
        if (vo.getId() != null) {
            baseEO = contentService.getEntity(BaseContentEO.class, vo.getId());

            baseEO.setTitle(vo.getTitle());
            baseEO.setTitleColor(vo.getTitleColor());
            baseEO.setIsBold(vo.getIsBold());
            baseEO.setIsTilt(vo.getIsTilt());
            baseEO.setIsUnderline(vo.getIsUnderline());
            baseEO.setAuthor(vo.getAuthor());
            baseEO.setImageLink(vo.getImageLink());
            baseEO.setPublishDate(vo.getPublishDate());
            baseEO.setResources(vo.getResources());
            baseEO.setColumnId(vo.getColumnId());
            baseEO.setSiteId(vo.getSiteId());
            baseEO.setIsTop(vo.getIsTop());
            baseEO.setIsNew(vo.getIsNew());
            baseEO.setIsPublish(vo.getIsPublish());
            baseEO.setIsJob(vo.getIsJob());
            baseEO.setJobIssueDate(vo.getJobIssueDate());
            baseEO.setRemarks(vo.getRemarks());
            //责任编辑 by liuk
            baseEO.setResponsibilityEditor(vo.getResponsibilityEditor());

            //baseEO.setIsAllowComments(vo.getIsAllowComments());
            contentService.updateEntity(baseEO);
           // CacheHandler.saveOrUpdate(BaseContentEO.class,baseEO);
            SysLog.log("修改视频新闻：栏目（"+ ColumnUtil.getColumnName(baseEO.getColumnId(),baseEO.getSiteId())+"），标题（" + baseEO.getTitle()+"）",
                    "BaseContentEO", CmsLogEO.Operation.Update.toString());
        } else {//新建新闻
            baseEO.setTitle(vo.getTitle());
            baseEO.setTitleColor(vo.getTitleColor());
            baseEO.setIsBold(vo.getIsBold());
            baseEO.setIsTilt(vo.getIsTilt());
            baseEO.setIsUnderline(vo.getIsUnderline());
            baseEO.setAuthor(vo.getAuthor());
            baseEO.setImageLink(vo.getImageLink());
            baseEO.setPublishDate(vo.getPublishDate());
            baseEO.setResources(vo.getResources());
            baseEO.setRemarks(vo.getRemarks());
            baseEO.setColumnId(vo.getColumnId());
            baseEO.setSiteId(vo.getSiteId());
            baseEO.setIsTop(vo.getIsTop());
            baseEO.setIsNew(vo.getIsNew());
            baseEO.setIsPublish(vo.getIsPublish());
            baseEO.setTypeCode(BaseContentEO.TypeCode.videoNews.toString());
            baseEO.setIsJob(vo.getIsJob());
            baseEO.setJobIssueDate(vo.getJobIssueDate());
            //责任编辑 by liuk
            baseEO.setResponsibilityEditor(vo.getResponsibilityEditor());
            if(vo.getCreateDate()!=null){
                baseEO.setCreateDate(vo.getCreateDate());
            }
            if(vo.getCreateUserId()!=null){
                baseEO.setCreateUserId(vo.getCreateUserId());
            }
            if(vo.getUpdateDate()!=null){
                baseEO.setUpdateDate(vo.getUpdateDate());
            }
            baseEO.setEditor(vo.getEditor());
           // baseEO.setIsAllowComments(vo.getIsAllowComments());
//            BaseContentEO newEO=new BaseContentEO();
//            AppUtil.copyProperties(newEO, baseEO);
            //排序号
            SortVO _svo=contentService.getMaxNumByColumn(vo.getColumnId());
            Long sort=1L;
            if(!AppUtil.isEmpty(_svo.getSortNum())) sort=_svo.getSortNum()+1L;
            baseEO.setNum(sort);
            contentService.saveEntity(baseEO);
            CacheHandler.saveOrUpdate(BaseContentEO.class, baseEO);

            SysLog.log("添加视频新闻：栏目（"+ ColumnUtil.getColumnName(baseEO.getColumnId(),baseEO.getSiteId())+"），标题（" + baseEO.getTitle()+"）",
                    "BaseContentEO", CmsLogEO.Operation.Add.toString());
        }
        if (vo.getVideoId() != null) {
            videoEO = getEntity(VideoNewsEO.class, vo.getVideoId());

            videoEO.setStatus(vo.getStatus());
            videoEO.setVideoPath(vo.getVideoPath());
            videoEO.setVideoName(vo.getVideoName());
            videoEO.setFileType(vo.getFileType());
            if(StringUtils.isEmpty(vo.getFileType())){
               videoEO.setStatus(100);
            }
            updateEntity(videoEO);
        } else {
            videoEO.setVideoPath(vo.getVideoPath());
            videoEO.setVideoName(vo.getVideoName());
            videoEO.setContentId(baseEO.getId());
            videoEO.setStatus(vo.getStatus());
            videoEO.setColumnId(vo.getColumnId());
            videoEO.setSiteId(vo.getSiteId());
            videoEO.setFileType(vo.getFileType());
            if(StringUtils.isEmpty(vo.getFileType())){
                videoEO.setStatus(100);
            }
            if(vo.getCreateDate()!=null){
                videoEO.setCreateDate(vo.getCreateDate());
            }
            if(vo.getCreateUserId()!=null){
                videoEO.setCreateUserId(vo.getCreateUserId());
            }
            if(vo.getUpdateDate()!=null){
                videoEO.setUpdateDate(vo.getUpdateDate());
            }
            saveEntity(videoEO);
        }
        //编辑器上传
        if (BaseContentEO.TypeCode.videoNews.toString().equals(vo.getTypeCode())) {
            ContentMongoEO _eo = new ContentMongoEO();
            _eo.setId(baseEO.getId());
            _eo.setContent(vo.getArticle());
            contentMongoService.save(_eo);
        }
        if (!AppUtil.isEmpty(vo.getVideoPath())&&vo.getStatus().equals(100)) {
            FileUploadUtil.setStatus(vo.getVideoPath(), 1,baseEO.getId(), vo.getColumnId(),vo.getSiteId());
        }
        if (!AppUtil.isEmpty(vo.getImageLink())) {
            FileUploadUtil.setStatus(vo.getImageLink(), 1,baseEO.getId(), vo.getColumnId(),vo.getSiteId());
        }
        return videoEO;
    }

    @Override
    public String delVideoEOs(Long[] ids) {
        String returnStr = "";
        List<BaseContentEO> list = contentService.getEntities(BaseContentEO.class, ids);
        if (list != null && list.size() > 0) {
            List<Long> noQuoteId = new ArrayList<Long>();
            List<Long> publishIds = new ArrayList<Long>();
            for (BaseContentEO _eo : list) {
                if (_eo.getQuoteStatus() != 0) {
                    noQuoteId.add(_eo.getId());
                }
                if (_eo.getIsPublish() == 1) {
                    publishIds.add(_eo.getId());
                    _eo.setIsPublish(2);//改成"发布中"中间状态

                }

                SysLog.log("批量删除视频新闻 ：栏目（" + ColumnUtil.getColumnName(_eo.getColumnId(), _eo.getSiteId()) + "），标题（" + _eo.getTitle()+"）",
                        "BaseContentEO", CmsLogEO.Operation.Delete.toString());

            }
            if (noQuoteId.size() > 0 && noQuoteId != null) {
                int size = noQuoteId.size();
                Long[] fileContentId = noQuoteId.toArray(new Long[size]);
                FileUploadUtil.markByContentIds(fileContentId, 0);
            }

            contentReferRelationService.delteByReferId(ids);// 删除引用关系
            commentService.deleteByContent(ids);

            //删除主表
            contentService.delete(list);
            CacheHandler.delete(BaseContentEO.class, list);

           // FileUploadUtil.deleteFileCenterEO(arr);
            if(publishIds.size()>0){
                returnStr = list.get(0).getSiteId()+"_"+list.get(0).getColumnId()+"_"+StringUtils.join(publishIds.toArray(),",");
            }
        }
        return returnStr;
    }

    @Override
    public void changeStatus(Long videoId,String mongoId) {
        VideoNewsEO eo=getEntity(VideoNewsEO.class, videoId);
        if(eo==null) return;
        if(eo.getVideoId()!=null){
            eo.setStatus(100);
            if(!AppUtil.isEmpty(mongoId)){
                eo.setVideoPath(mongoId);
            }
            updateEntity(eo);
        }
    }

    @Override
    public void changeStatusNew(Long videoId, String path) {

    }

    @Override
    public void failChange(Long videoId) {
        VideoNewsEO eo=getEntity(VideoNewsEO.class, videoId);
        if(eo==null) return;
        eo.setStatus(-1);
        updateEntity(eo);
    }


    @Override
    public Long changeHit(Long id) {
        if(id==null){
            throw new BaseRunTimeException(TipsMode.Message.toString(), "视频新闻不存在");
        }
        BaseContentEO eo=contentService.getEntity(BaseContentEO.class, id);
        if(eo==null){
            throw new BaseRunTimeException(TipsMode.Message.toString(), "视频新闻不存在");
        }
        eo.setHit(eo.getHit() + 1);
        contentService.updateEntity(eo);
        CacheHandler.saveOrUpdate(BaseContentEO.class,eo);
        SysLog.log("修改视频新闻点击数 >> ID：" +id,
                "BaseContentEO", CmsLogEO.Operation.Update.toString());
        return eo.getHit();
    }

    @Override
    public Long countData(Long columnId) {
        return videoDao.countData(columnId);
    }

    @Override
    public void removeVideos(Long[] ids) {
       videoDao.removeVideos(ids);
    }

    @Override
    public List<VideoNewsVO> getVideoList(Long columnId, Long siteId,Integer num) {
        return videoDao.getVideoList(columnId,siteId,num);
    }

    @Override
    public void importVideo(VideoNewsVO vo) {
        String name=vo.getVideoName();
        String videoPath=vo.getVideoPath();
        if (StringUtils.isEmpty(vo.getTitle())) {
            throw new BaseRunTimeException(TipsMode.Message.toString(), "标题不能为空");
        }
        if(StringUtils.isEmpty(name)|| StringUtils.isEmpty(videoPath)){
            throw new BaseRunTimeException(TipsMode.Message.toString(), "视频新闻路径不能为空");
        }
        String type=vo.getFileType();
        String prePath="";
        if(vo.getVideoId()!=null){
            VideoNewsEO videoEO=getEntity(VideoNewsEO.class, vo.getVideoId());
            if(videoEO!=null){
                prePath=videoEO.getVideoPath();
            }
        }
        VideoNewsEO eo=saveVideo(vo);

        //设置定时发布
        Integer isJob = 0;
        if(vo.getIsJob() == 1 && vo.getJobIssueDate() != null){
            isJob = 1;
        }
        ScheduleJobUtil.addOrDelScheduleJob("新闻定时发布日期", NewsIssueTaskImpl.class.getName(), isJob==0?null:ScheduleJobUtil.dateToCronExpression(vo.getJobIssueDate()),
                String.valueOf(vo.getId()),isJob);

        final Long videoId=eo.getVideoId();

        //通过视频上传，非flv格式，视频地址发生了变化，才会转换
        if(!StringUtils.isEmpty(type)&&!videoPath.equals(prePath)){
            //异步执行
            taskExecutor.execute(new Runnable() {
                @Override
                public void run() {
                    try {
                        logger.error("==============>  开始转换 <================");
                        // 绑定session至当前线程中
                        SessionFactory sessionFactory = SpringContextHolder.getBean(SessionFactory.class);
                        boolean participate = ConcurrentUtil.bindHibernateSessionToThread(sessionFactory);
                        transVideo( videoId);
                        // 关闭session
                        ConcurrentUtil.closeHibernateSessionFromThread(participate, sessionFactory);
                        logger.error("=============>  转换完成 <=====================");
                    } catch (IOException e) {
                        logger.error("============>  IOException<=====================");
                        e.printStackTrace();
                    }
                }
            });
        }else{
            if (vo.getIsPublish() == 1) {
                //处理保存时发布,正式部署时启用
                boolean rel= MessageSenderUtil.publishContent(new MessageStaticEO(vo.getSiteId(), vo.getColumnId(), new Long[]{eo.getContentId()}).setType(MessageEnum.PUBLISH.value()), 1);
                if(!rel){
                    throw new BaseRunTimeException(TipsMode.Message.toString(), "保存成功,发布失败");
                }
            }else{
                if(vo.getId()!=null){
                    MessageSenderUtil.publishContent(new MessageStaticEO(vo.getSiteId(), vo.getColumnId(), new Long[]{vo.getId()}).setType(MessageEnum.UNPUBLISH.value()), 2);
                }
            }
        }
        logger.error("============>  保存成功<====================");
    }

    @Override
    public void transVideo(Long videoId) throws IOException {
        VideoNewsEO videoEO=getEntity(VideoNewsEO.class,videoId);
        if(videoEO==null){
            throw new BaseRunTimeException(TipsMode.Message.toString(), "视频配置项不存在！");
        }
        BaseContentEO contentEO= CacheHandler.getEntity(BaseContentEO.class,videoEO.getContentId());
        if(contentEO==null){
            throw new BaseRunTimeException(TipsMode.Message.toString(), "视频新闻不存在！");
        }
        String videoPath=videoEO.getVideoPath();
        String videoName=videoEO.getVideoName();
        File file = new File(videoPath);
        String flvPrefix = videoName;
        if(cn.lonsun.core.base.util.StringUtils.isEmpty(flvPrefix)||flvPrefix.length()<3){
            flvPrefix=flvPrefix+"_tmp";
        }
        File tempFile = File.createTempFile(flvPrefix, ".mp4");
        // 视频转换
        ConvertMsg msg = LocalConvertVideo.processFLV(videoPath, tempFile.getPath(), 0, path);
        MongoFileVO mongvo = null;
        MessageSystemEO message=new MessageSystemEO();
        message.setSiteId(videoEO.getSiteId());
        message.setColumnId(videoEO.getColumnId());
        message.setMessageType(MessageSystemEO.TIP);
        message.setModeCode(BaseContentEO.TypeCode.videoNews.toString());
        message.setRecUserIds(videoEO.getUpdateUserId() + "");
        //message.setRecOrganIds(videoEO.getCreateOrganId() + "");
        //message.setCreateOrganId(videoEO.getCreateOrganId());
        message.setCreateUserId(videoEO.getUpdateUserId());
        message.setLink("/videoNews/videoPlayer?id="+videoEO.getContentId());
        message.setResourceId(videoEO.getContentId());
        if (msg.getStatus() == 1) {
            // 上传
            byte[] fileByte = FileUtils.readFileToByteArray(tempFile);


            FileCenterEO fileCenter=new FileCenterEO();
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
            logger.error("====================><========================="+fileByte.length);
            if(fileByte.length>0){
                mongvo= FileUploadUtil.uploadUtil(fileByte,fileCenter);
                if(mongvo.getMongoId()!=null){
                    FileUploadUtil.setStatus(mongvo.getMongoId(), 1,videoEO.getContentId(), contentEO.getColumnId(),contentEO.getSiteId());
                    logger.error("=============>  mongoId <====================="+mongvo.getMongoId());
                }
                changeStatus(videoId,mongvo.getMongoId());
                logger.error("=============>  状态改变 <=====================");
                message.setTitle(contentEO.getTitle() + "转换成功");
                message.setMessageStatus(MessageSystemEO.MessageStatus.success.toString());
                MessageSender.sendMessage(message);

            }else{
                logger.error("=============>  视频字节为0 <=====================");
                message.setTitle(contentEO.getTitle() + "转换失败");
                message.setMessageStatus(MessageSystemEO.MessageStatus.error.toString());
                MessageSender.sendMessage(message);
                failChange(videoId);
            }
        }else{
            changeStatus(videoId,mongvo.getMongoId());
            message.setTitle(contentEO.getTitle() + "转换失败");
            message.setMessageStatus(MessageSystemEO.MessageStatus.error.toString());
            MessageSender.sendMessage(message);
            failChange(videoId);
        }
        file.delete();//删除保存到本地的文件
        tempFile.delete();//删除在本地生成的转换文件
        if (contentEO.getIsPublish() == 1) {
            //处理保存时发布,正式部署时启用
            boolean rel= MessageSenderUtil.publishContent(new MessageStaticEO(contentEO.getSiteId(), contentEO.getColumnId(), new Long[]{contentEO.getId()}).setType(MessageEnum.PUBLISH.value()), 1);
            if(!rel){
                throw new BaseRunTimeException(TipsMode.Message.toString(), "保存成功,发布失败");
            }
        }else{
            if(contentEO.getId()!=null){
                MessageSenderUtil.publishContent(new MessageStaticEO(contentEO.getSiteId(), contentEO.getColumnId(), new Long[]{contentEO.getId()}).setType(MessageEnum.UNPUBLISH.value()), 2);
            }
        }
    }

    @Override
    public void importHongAn(List<Object> list, Long siteId, ColumnMgrEO columnMgrEO) {

    }
}

