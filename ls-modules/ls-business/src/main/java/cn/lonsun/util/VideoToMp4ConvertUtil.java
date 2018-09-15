package cn.lonsun.util;

import cn.lonsun.activemq.MessageSender;
import cn.lonsun.common.util.AppUtil;
import cn.lonsun.content.internal.entity.BaseContentEO;
import cn.lonsun.content.internal.entity.VideoNewsEO;
import cn.lonsun.content.internal.service.IBaseContentService;
import cn.lonsun.content.internal.service.IVideoNewsService;
import cn.lonsun.content.interview.internal.entity.InterviewInfoEO;
import cn.lonsun.content.interview.internal.service.IInterviewInfoService;
import cn.lonsun.content.survey.internal.entity.SurveyThemeEO;
import cn.lonsun.content.survey.internal.service.ISurveyThemeService;
import cn.lonsun.core.base.entity.AMockEntity;
import cn.lonsun.core.exception.BaseRunTimeException;
import cn.lonsun.core.util.SpringContextHolder;
import cn.lonsun.core.util.TipsMode;
import cn.lonsun.message.internal.entity.MessageSystemEO;
import cn.lonsun.mongodb.entity.ContentMongoEO;
import cn.lonsun.mongodb.service.ContentMongoServiceImpl;
import cn.lonsun.mongodb.vo.MongoFileVO;
import cn.lonsun.system.filecenter.internal.entity.FileCenterEO;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.task.TaskExecutor;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 视频转换为mp4格式工具类，支持消息输出到进度条 <br/>
 * 在保存文章信息之后，传入文章id回写视频转换的实际地址并且更新数据库中文章视频的转换状态
 *
 * @author fth <br/>
 * @version v1.0 <br/>
 * @date 2017/7/24 <br/>
 */
public class VideoToMp4ConvertUtil {
    // ffmpeg支持的格式
    private static final List<String> CONVERT_LIST = Arrays.asList("asx", "asf", "mpg", "wmv", "3gp", "mp4", "mov", "avi", "flv");
    private static final String REGEX = "<div class=\"video-player\" data-url=\".*?/mongo[1]?/([^\"]*)\" data-width=\"([^\"]*)\" data-height=\"([^\"]*)\"";
    private static final Pattern PATTERN = Pattern.compile(REGEX, Pattern.CASE_INSENSITIVE | Pattern.DOTALL);// 正则找出文章中视频位置
    private static TaskExecutor taskExecutor = SpringContextHolder.getBean(TaskExecutor.class);//异步线程
    private static IVideoNewsService videoService = SpringContextHolder.getBean(IVideoNewsService.class);// 视频service
    private static IBaseContentService baseContentService = SpringContextHolder.getBean(IBaseContentService.class);// 主表
    private static IInterviewInfoService interviewInfoService = SpringContextHolder.getBean(IInterviewInfoService.class);// 在线访谈;
    private static ContentMongoServiceImpl contentMongoService = SpringContextHolder.getBean(ContentMongoServiceImpl.class);// 文章内容
    private static ISurveyThemeService surveyThemeService = SpringContextHolder.getBean(ISurveyThemeService.class);// 在线访谈;

    /**
     * 写入本地文件
     *
     * @param file 上传的文件
     * @return
     */
    public static File writeToLocalFile(MultipartFile file) {
        try {
            File inFile = File.createTempFile(UUID.randomUUID().toString() + "_in", ".mp4");//创建临时文件
            file.transferTo(inFile);// 数据写入到临时文件中
            return inFile;
        } catch (Throwable e) {
            throw new BaseRunTimeException(TipsMode.Message.toString(), "写入本地文件失败，请稍后重试！");
        }
    }

    /**
     * 视频处理，当传入的为视频新闻类型时，需要根据状态判断视频是否转换完毕
     * 其他类型处理文章内容
     *
     * @param convertPath 视频转换插件安装路径
     * @param columnId    栏目id
     * @param contentId   文章主表
     */
    public static void transfer(final String convertPath, final Long columnId, final Long contentId) {
        final Long siteId = LoginPersonUtil.getSiteId();
        final Long userId = LoginPersonUtil.getUserId();
        final Long organId = LoginPersonUtil.getOrganId();
        // 异步转换，并发送消息至前台页面
        taskExecutor.execute(new Runnable() {

            @Override
            public void run() {
                // hibernate需要绑定线程
                HibernateSessionUtil.execute(new HibernateHandler<String>() {

                    private String articleTitle;// 文章标题

                    @Override
                    public String execute() {
                        BaseContentEO contentEO = baseContentService.getEntity(BaseContentEO.class, contentId);
                        if (null == contentEO) {
                            throw new BaseRunTimeException(TipsMode.Message.toString(), "文章新闻基础表数据不存在！");
                        }
                        this.articleTitle = contentEO.getTitle();// 设置文章标题
                        if (BaseContentEO.TypeCode.videoNews.toString().equals(contentEO.getTypeCode())) {
                            this.processVideoNews(); // 视频新闻要处理视频地址
                        } else if (BaseContentEO.TypeCode.interviewInfo.toString().equals(contentEO.getTypeCode())) {// 在线访谈
                            InterviewInfoEO interview = interviewInfoService.getInterviewInfoByContentId(contentId);
                            if (contentEO.getQuoteStatus().equals(100)) {
                                String videoPath = contentEO.getContentPath();
                                String videoName = interview.getLiveLink();
                                String mongoPath = convertToMongo(videoPath, convertPath, videoName);
                                // 更新路径
                                contentEO.setQuoteStatus(1);// 转换成功
                                contentEO.setContentPath(mongoPath);
                            }
                            if (contentEO.getVideoStatus().equals(0)) {
                                contentEO.setArticle(interview.getDesc());
                                interview.setDesc(processEditor(contentEO));
                                interviewInfoService.updateEntity(interview);
                            }
                            baseContentService.updateEntity(contentEO);

                        }else if(BaseContentEO.TypeCode.reviewInfo.toString().equals(contentEO.getTypeCode())){
                            SurveyThemeEO surveyThemeEO = surveyThemeService.getSurveyThemeByContentId(contentId);
                            if(null != surveyThemeEO){
                                String content = surveyThemeEO.getContent();
                                if (StringUtils.isNotEmpty(content)) {
                                    StringBuffer sb = new StringBuffer();
                                    Matcher m = PATTERN.matcher(content);
                                    while (m.find()) { // 一个个慢慢转换，找不到方法合并进度
                                        String group = m.group();// 总的字符串
                                        String filePath = m.group(1);// 临时文件路径
                                        // 这里的临时文件路径取出实际的文件名称
                                        String tempPath = filePath.substring(0, filePath.indexOf("?"));
                                        String realName = filePath.substring(filePath.indexOf("?") + 1);
                                        String mongoPath = convertToMongo(tempPath, convertPath, realName);
                                        m.appendReplacement(sb, group.replace(filePath, mongoPath));

                                    }
                                    m.appendTail(sb);
                                    String newContent = sb.toString();
                                    if (!newContent.equals(content)) {// 更新内容
                                        surveyThemeEO.setContent(newContent);
                                        surveyThemeService.updateEntity(surveyThemeEO);
                                        contentEO.setVideoStatus(100);
                                        baseContentService.updateEntity(contentEO);// 更新视频的转换状态
                                    }
                                }
                            }
                        }else if (contentEO.getVideoStatus().equals(0)) { // 或者在文本编辑器中存在未转换的视频
                            this.processEditorVideo(contentEO);
                        }
                        return StringUtils.EMPTY;
                    }

                    /**
                     * 处理视频新闻
                     */
                    private void processVideoNews() {
                        Map<String, Object> paramMap = new HashMap<String, Object>();
                        paramMap.put("contentId", contentId);//文章主键
                        paramMap.put("recordStatus", AMockEntity.RecordStatus.Normal.toString());
                        VideoNewsEO videoNewsEO = videoService.getEntity(VideoNewsEO.class, paramMap);
                        if (null == videoNewsEO) {
                            throw new BaseRunTimeException(TipsMode.Message.toString(), "视频新闻数据不存在！");
                        }
                        if (videoNewsEO.getStatus().equals(0)) {// 如果没有转换
                            String path = videoNewsEO.getVideoPath();
                            String mongoPath = convertToMongo(path, convertPath, videoNewsEO.getVideoName());
                            videoNewsEO.setStatus(100);
                            videoNewsEO.setVideoPath(mongoPath);
                            videoService.updateEntity(videoNewsEO);
                        }
                    }

                    /**
                     * 处理文章编辑器中的视频
                     */
                    private void processEditorVideo(BaseContentEO contentEO) {
                        ContentMongoEO contentMongoEO = contentMongoService.queryById(contentId);// 取出文章页内容
                        if (null == contentMongoEO) {
                            return;
                        }
                        String content = contentMongoEO.getContent();
                        if (StringUtils.isNotEmpty(content)) {
                            StringBuffer sb = new StringBuffer();
                            Matcher m = PATTERN.matcher(content);
                            while (m.find()) { // 一个个慢慢转换，找不到方法合并进度
                                String group = m.group();// 总的字符串
                                String filePath = m.group(1);// 临时文件路径
                                // 这里的临时文件路径取出实际的文件名称
                                String tempPath = filePath.substring(0, filePath.indexOf("?"));
                                String realName = filePath.substring(filePath.indexOf("?") + 1);
                                String mongoPath = convertToMongo(tempPath, convertPath, realName);
                                m.appendReplacement(sb, group.replace(filePath, mongoPath));
                            }
                            m.appendTail(sb);
                            String newContent = sb.toString();
                            if (!newContent.equals(content)) {// 更新内容
                                contentMongoEO.setContent(newContent);
                                contentMongoService.save(contentMongoEO);
                                contentEO.setVideoStatus(100);
                                baseContentService.updateEntity(contentEO);// 更新视频的转换状态
                            }
                        }
                    }

                    /**
                     * 处理文章编辑器中的视频
                     */
                    private String processEditor(BaseContentEO contentEO) {
                        String content = contentEO.getArticle();
                        String newContent = content;
                        if (StringUtils.isNotEmpty(content)) {
                            StringBuffer sb = new StringBuffer();
                            Matcher m = PATTERN.matcher(content);
                            while (m.find()) { // 一个个慢慢转换，找不到方法合并进度
                                String group = m.group();// 总的字符串
                                String filePath = m.group(1);// 临时文件路径
                                // 这里的临时文件路径取出实际的文件名称
                                String tempPath = filePath.substring(0, filePath.indexOf("?"));
                                String realName = filePath.substring(filePath.indexOf("?") + 1);
                                String mongoPath = convertToMongo(tempPath, convertPath, realName);
                                m.appendReplacement(sb, group.replace(filePath, mongoPath));
                            }
                            m.appendTail(sb);
                            newContent = sb.toString();
                            if (AppUtil.isEmpty(newContent)) {
                                newContent = content;
                            }
                        }
                        contentEO.setVideoStatus(100);
                        return newContent;
                    }

                    private String convertToMongo(String tempPath, String convertPath, String realName) {
                        if (StringUtils.isEmpty(tempPath)) {
                            throw new BaseRunTimeException(TipsMode.Message.toString(), "视频路径为空，请检查！");
                        }
                        String suffix = tempPath.substring(tempPath.lastIndexOf(".") + 1).toLowerCase();
                        if (!CONVERT_LIST.contains(suffix)) {
                            throw new BaseRunTimeException(TipsMode.Message.toString(), String.format("视频文件格式[%s]不支持！", suffix));
                        }
                        File file = new File(tempPath);
                        if (null == file || !file.isFile()) {
                            throw new BaseRunTimeException(TipsMode.Message.toString(), "视频新闻本地文件不存在或已删除，请重新上传！");
                        }
                        File outMp4File = null;// 转出的mp4文件
                        BufferedReader buf = null; // 保存ffmpeg的输出结果流
                        try {
                            outMp4File = File.createTempFile(UUID.randomUUID().toString() + "_out", ".mp4");//创建临时文件
                            //AVC中质量和大小
                            List<String> commend = new ArrayList<String>();
                            String inPath = tempPath.replaceAll("\\\\", "/");// 处理路径问题
                            String outPath = outMp4File.getPath().replaceAll("\\\\", "/");// 处理路径问题

                            commend.add(convertPath);
                            commend.add("-i");
                            commend.add(inPath);
                            commend.add("-b");
                            commend.add("768k");// 设置视频比特率
                            commend.add("-ar");
                            commend.add("44100");// 设置音频采样率

                            commend.add("-ab");
                            commend.add("128k");// 设置音频比特率
                            commend.add("-ac");
                            commend.add("2");// 设置声道

                            commend.add("-vcodec");
                            commend.add("libx264");
                            commend.add("-acodec");
                            commend.add("libfaac");
                            commend.add("-movflags");

                            commend.add("+faststart");
                            commend.add("-y");  //覆盖
                            commend.add(outPath);
                            //转换开始时间
                            Long start = System.currentTimeMillis();
                            ProcessBuilder builder = new ProcessBuilder();
                            builder.command(commend);
                            builder.redirectErrorStream(true);
                            Process p = builder.start();

                            int totalTime = 0;
                            String line = null;// 解析
                            String complete = ""; // 转换进度
                            buf = new BufferedReader(new InputStreamReader(p.getInputStream()));
                            while ((line = buf.readLine()) != null) {
                                System.out.println(line);
                                int positionTime = line.indexOf("time=");
                                int positionDuration = line.indexOf("Duration:");

                                if (positionDuration > 0) {// 计算总时长
                                    String dur = line.replace("Duration:", "");
                                    dur = dur.trim().substring(0, 8);
                                    int h = Integer.parseInt(dur.substring(0, 2));
                                    int m = Integer.parseInt(dur.substring(3, 5));
                                    int s = Integer.parseInt(dur.substring(6, 8));
                                    totalTime = h * 3600 + m * 60 + s;// 得到总共的时间秒数
                                }
                                if (positionTime > 0) {// 如果所用时间字符串存在
                                    String time = line.substring(positionTime, line.indexOf("bitrate") - 1);
                                    time = time.substring(time.indexOf("=") + 1, time.indexOf("."));
                                    int h = Integer.parseInt(time.substring(0, 2));
                                    int m = Integer.parseInt(time.substring(3, 5));
                                    int s = Integer.parseInt(time.substring(6, 8));
                                    int currentTime = h * 3600 + m * 60 + s;
                                    float t = (float) currentTime / (float) totalTime;// 计算所用时间与总共需要时间的比例
                                    int prop = (int) Math.ceil(t * 100);
                                    String newComplete = String.format("%s", prop > 100 ? 100 : prop) + "%";// 计算完成进度百分比
                                    System.out.print("转换进度：" + complete);
                                    // 发送消息，每三秒发一次消息，为了切合dwr消息推送
                                    if (!complete.equals(newComplete)) {// 进度不一样时发送消息
                                        complete = newComplete; // 置换
                                        Map<String, Object> data = new HashMap<String, Object>();
                                        data.put("currentTime", currentTime);// 当前时间
                                        data.put("totalTime", totalTime);// 总时间
                                        data.put("complete", complete);// 放入完成率
                                        this.sendMessage(data, complete, MessageSystemEO.MessageStatus.info.toString());
                                    }
                                }
                            }
                            byte[] bytes = FileUtils.readFileToByteArray(outMp4File);
                            return this.writeToFileCenter(bytes, realName);
                        } catch (Throwable e) {
                            e.printStackTrace();
                            throw new BaseRunTimeException(TipsMode.Message.toString(), "转码失败，请稍后重试！");
                        } finally {
                            if (null != outMp4File) {//说明已经转换成功，删除临时文件
                                try {
                                    file.delete();
                                    outMp4File.delete();
                                } catch (Throwable e) {
                                    e.printStackTrace();
                                }
                            }
                            if (null != buf) {
                                try {
                                    buf.close();
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    }

                    /**
                     * 写入文件管理中心
                     * 加上后缀，方便在手机端访问
                     * 文件资源中心存储的文件名是根据类型来的
                     */
                    private String writeToFileCenter(byte[] fileByte, String realName) {
                        String suffix = "mp4";// 后缀统一使用MP4结尾
                        /*String fileName = realName.substring(0, realName.lastIndexOf(".")); // 文件名*/
                        String fileName = realName; // 文件名

                        // 文件资源中心存储的文件名是根据类型来的
                        FileCenterEO fileCenter = new FileCenterEO();
                        fileCenter.setFileName(fileName);
                        fileCenter.setSuffix(suffix);
                        fileCenter.setType(FileCenterEO.Type.Video.toString());
                        fileCenter.setCode(BaseContentEO.TypeCode.videoNews.toString());
                        fileCenter.setStatus(1);// 引用状态
                        fileCenter.setSiteId(siteId);
                        fileCenter.setColumnId(columnId);
                        fileCenter.setContentId(contentId);
                        fileCenter.setCreateDate(new Date());
                        fileCenter.setCreateUserId(userId);
                        fileCenter.setCreateOrganId(organId);
                        fileCenter.setUpdateDate(new Date());
                        fileCenter.setDesc("视频上传");
                        fileCenter.setRecordStatus(AMockEntity.RecordStatus.Normal.toString());

                        MongoFileVO mongoVo = FileUploadUtil.uploadUtil(fileByte, fileCenter);
                        return mongoVo.getFileName();
                    }

                    private void sendMessage(Map<String, Object> dataMap, String content, String messageStatus) {
                        MessageSystemEO message = new MessageSystemEO();
                        message.setSiteId(siteId);
                        message.setColumnId(columnId);
                        message.setMessageType(5L);//视频转换消息类型
                        message.setRecUserIds(userId.toString());
                        message.setResourceId(contentId);
                        message.setTitle(content);
                        message.setContent(content);
                        message.setMessageStatus(messageStatus);
                        message.setData(dataMap);// null
                        message.setTodb(false);// 不需要入库
                        MessageSender.sendMessage(message);
                    }

                    @Override
                    public String complete(String result, Throwable exception) {
                        String tips = "文章[%s]视频转换完成！";
                        String messageStatus = MessageSystemEO.MessageStatus.info.toString();
                        if (null != exception) {
                            tips = "文章[%s]视频转换出错，请稍后重试！";
                            if (exception instanceof BaseRunTimeException) {
                                tips = "文章[%s]视频转换出错，" + ((BaseRunTimeException) exception).getTipsMessage();
                            }
                            messageStatus = MessageSystemEO.MessageStatus.error.toString();
                        }
                        String message = String.format(tips, this.articleTitle);
                        this.sendMessage(null, message, messageStatus);// 发送消息
                        return message;
                    }
                });
            }
        });
    }
}
