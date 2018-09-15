/*
 * ChuZhouXXGKController.java         2016年8月2日 <br/>
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

package cn.lonsun.staticcenter.controller;

import cn.lonsun.base.anno.DbInject;
import cn.lonsun.common.util.AppUtil;
import cn.lonsun.content.ideacollect.internal.dao.ICollectInfoDao;
import cn.lonsun.content.ideacollect.internal.entity.CollectInfoEO;
import cn.lonsun.content.ideacollect.internal.service.ICollectInfoService;
import cn.lonsun.content.internal.dao.IBaseContentDao;
import cn.lonsun.content.internal.dao.IContentPicDao;
import cn.lonsun.content.internal.dao.IVideoNewsDao;
import cn.lonsun.content.internal.entity.BaseContentEO;
import cn.lonsun.content.internal.entity.ContentPicEO;
import cn.lonsun.content.internal.entity.VideoNewsEO;
import cn.lonsun.content.internal.service.IBaseContentService;
import cn.lonsun.content.internal.service.IContentPicService;
import cn.lonsun.content.internal.service.IVideoNewsService;
import cn.lonsun.content.interview.internal.dao.IInterviewInfoDao;
import cn.lonsun.content.interview.internal.entity.InterviewInfoEO;
import cn.lonsun.content.interview.internal.service.IInterviewInfoService;
import cn.lonsun.content.messageBoard.controller.internal.entity.MessageBoardReplyEO;
import cn.lonsun.content.messageBoard.dao.IMessageBoardReplyDao;
import cn.lonsun.content.messageBoard.service.IMessageBoardReplyService;
import cn.lonsun.content.survey.internal.dao.ISurveyThemeDao;
import cn.lonsun.content.survey.internal.entity.SurveyThemeEO;
import cn.lonsun.content.survey.internal.service.ISurveyThemeService;
import cn.lonsun.core.base.controller.BaseController;
import cn.lonsun.indicator.internal.service.IIndicatorService;
import cn.lonsun.mongodb.base.IMongoDbFileServer;
import cn.lonsun.mongodb.entity.ContentMongoEO;
import cn.lonsun.mongodb.service.ContentMongoServiceImpl;
import cn.lonsun.mongodb.vo.MongoFileVO;
import cn.lonsun.msg.submit.entity.CmsMsgSubmitEO;
import cn.lonsun.msg.submit.service.IMsgSubmitService;
import cn.lonsun.net.service.dao.IRelatedRuleDao;
import cn.lonsun.net.service.dao.ITableResourcesDao;
import cn.lonsun.net.service.entity.CmsRelatedRuleEO;
import cn.lonsun.net.service.entity.CmsTableResourcesEO;
import cn.lonsun.net.service.entity.CmsWorkGuideEO;
import cn.lonsun.net.service.service.IRelatedRuleService;
import cn.lonsun.net.service.service.ITableResourcesService;
import cn.lonsun.net.service.service.IWorkGuideService;
import cn.lonsun.site.site.internal.dao.IWaterMarkConfigDao;
import cn.lonsun.site.site.internal.entity.WaterMarkConfigEO;
import cn.lonsun.site.site.internal.service.IWaterMarkConfigService;
import cn.lonsun.staticcenter.util.JdbcUtils;
import cn.lonsun.wechatmgr.internal.dao.IWeChatArticleDao;
import cn.lonsun.wechatmgr.internal.entity.WeChatArticleEO;
import cn.lonsun.wechatmgr.internal.service.IWeChatArticleService;
import cn.lonsun.weibo.dao.IWeiboRadioContentDao;
import cn.lonsun.weibo.entity.WeiboRadioContentEO;
import cn.lonsun.weibo.service.IWeiboRadioContentService;
import com.mongodb.BasicDBObject;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.gridfs.GridFSDBFile;
import org.apache.commons.lang3.StringUtils;
import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author doocal <br/>
 * @version v1.0 <br/>
 * @date 2016年8月29日 <br/>
 */
@Controller
@RequestMapping("/site/convert")
public class MongoConvertController extends BaseController {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private JdbcUtils jdbcUtils;

    @Autowired
    private IIndicatorService indicatorService;

    @Autowired
    private IBaseContentService baseContentService;

    @Autowired
    private IVideoNewsService videoNewsService;

    @Autowired
    private IVideoNewsDao videoNewsDao;

    @DbInject("baseContent")
    private IBaseContentDao baseContentDao;

    @Autowired
    private IContentPicService contentPicService;

    @Autowired
    private IContentPicDao contentPicDao;

    @Autowired
    private IMongoDbFileServer iMongoDbFileServer;

    @Autowired
    private IWaterMarkConfigDao waterMarkConfigDao;

    @Autowired
    private IWaterMarkConfigService waterMarkConfigService;

    @Autowired
    private IWeiboRadioContentDao weiboRadioContentDao;

    @Autowired
    private IWeiboRadioContentService weiboRadioContentService;

    @Autowired
    private IRelatedRuleDao relatedRuleDao;

    @Autowired
    private IMessageBoardReplyDao messageBoardReplyDao;

    @Autowired
    private IMessageBoardReplyService messageBoardReplyService;

    @Autowired
    private IWeChatArticleDao weChatArticleDao;

    @Autowired
    private IWeChatArticleService weChatArticleService;

    @Autowired
    private ContentMongoServiceImpl contentMongoService;

    @Autowired
    private IWorkGuideService workGuideService;

    @Autowired
    private IMsgSubmitService msgSubmitService;

    @Autowired
    private IRelatedRuleService relatedRuleService;

    @Autowired
    private ITableResourcesService tableResourcesService;

    @Autowired
    private ITableResourcesDao tableResourcesDao;

    @ModelAttribute
    public void get(@RequestParam(required = false) String type) {
        /*jdbcUtils = JdbcUtils.getInstance();
        jdbcUtils.setURLSTR("jdbc:jtds:sqlserver://61.191.61.136:31433;DatabaseName=ahzj_newweb;useLOBs=false;");
        jdbcUtils.setUSERNAME("ahzj_newweb");
        jdbcUtils.setUSERPASSWORD("ahzj_newweb");*/
    }

    @RequestMapping("index")
    public String index(ModelMap map) throws ClassNotFoundException, SQLException {
        return "dbconvert/mongconvert";
    }

    private MongoFileVO GetMongoFileNameEO(String mongoId) {
        GridFSDBFile gfs = null;
        try {
            gfs = iMongoDbFileServer.getGridFSDBFile(mongoId, null);
        } catch (Exception e) {
            e.printStackTrace();
        }
        MongoFileVO vo = new MongoFileVO();
        if (gfs != null) {
            DBObject metaData = new BasicDBObject();
            metaData.put("OriginalFilename", gfs.getFilename());
            vo.setMongoId(mongoId);
            if (!AppUtil.isEmpty(gfs.get("caseId")) && gfs.get("caseId") != "null") {
                vo.setCaseId(Long.valueOf((Long) gfs.get("caseId")));
            }
            vo.setFileName(mongoId + "." + gfs.getContentType());
            vo.setMetaData(metaData);
            if (!AppUtil.isEmpty(gfs.get("uploadDate"))) {
                vo.setUploadDate((Date) (gfs.get("uploadDate")));
            }
            vo.setContentType(gfs.get("contentType").toString());
        }
        return vo;
    }

    private void ReplaceMongoContent(Long contentId, String content) {
        if (!AppUtil.isEmpty(contentId) && !AppUtil.isEmpty(content)) {
            ContentMongoEO meo = new ContentMongoEO();
            meo.setId(contentId);
            meo.setContent(content);
            contentMongoService.save(meo);
        }
    }

    @RequestMapping("updateMongoFileName")
    @ResponseBody
    public String updateMongoFileName(ModelMap map) {
        Long s = System.currentTimeMillis();

        ObjectId objId = new ObjectId("56a03169239d5ba330c192e9");
        DBObject query = new BasicDBObject("_id", objId);
        DBCursor cursor = iMongoDbFileServer.GridFSDBFileList(query, null);
        System.out.println(cursor);
        for (DBObject t : cursor) {
            System.out.println(t.get("filename"));
            DBObject db = cursor.next();
            MongoFileVO vo = new MongoFileVO();

            String mongoId = String.valueOf(db.get("_id"));

            System.out.println(db);

            DBObject metaData = new BasicDBObject();
            metaData.put("OriginalFilename", db.get("filename"));
            vo.setFileName(mongoId + "." + db.get("contentType"));
            vo.setMetaData(metaData);

            iMongoDbFileServer.updateByteFile(mongoId, vo, null);
        }
        /*while (cursor.hasNext()) {

        }*/
        Long s2 = System.currentTimeMillis();
        return "更新MongoFileName（" + cursor.size() + " 条，耗时：" + (s2 - s) / 1000 + "秒）！<a href=\"/site/convert/index\">返回</a>";
    }

    @RequestMapping("updateMongoContentFileName")
    @ResponseBody
    public String updateMongoContentFileName(ModelMap map) {
        Long s = System.currentTimeMillis();

        String pattern = "/mongo/([^\"]*)";
        Pattern r = Pattern.compile(pattern);

        //ObjectId objId = new ObjectId("39695");
        //DBObject query = new BasicDBObject("_id", objId);

        DBObject query = new BasicDBObject();
        query.put("_id", "39695");
        //返回所有mongoDB中的内容
        DBCursor cursor = iMongoDbFileServer.MongoAllList(null, null);
        System.out.println(cursor);
        for (DBObject t : cursor) {

            DBObject item = cursor.next();
            Long contentId = (Long) item.get("_id");
            String content = (String) item.get("content");

            if (!StringUtils.isEmpty(content)) {
                //查询字符串是否有mongo 打头的内容
                Matcher m = r.matcher(content);
                if (m.find()) {
                    //得到分组中的MongoID
                    String mongoId = m.group(1);
                    //根据MongId得到File EO
                    MongoFileVO vo = GetMongoFileNameEO(mongoId);
                    //根据判断扩展名来判断文件是否存在
                    if (vo.getContentType() != null && !vo.getContentType().equals("")) {
                        System.out.println(content.replaceAll(m.group(1), m.group(1) + "." + vo.getContentType()));
                    }
                    //this.ReplaceMongoContent(contentId, content);
                }
            }

        }
        Long s2 = System.currentTimeMillis();
        return "更新MongoContentFileName（" + cursor.size() + " 条，耗时：" + (s2 - s) / 1000 + "秒）！<a href=\"/site/convert/index\">返回</a>";
    }

    @RequestMapping("updateImageLink")
    @ResponseBody
    public String updateImageLink(ModelMap map) {

        Long s = System.currentTimeMillis();

        //基础表缩略图更新
        logger.info("新闻基础表缩略图更新>>>");

        /*StringBuffer hql = new StringBuffer("from BaseContentEO c where c.recordStatus='Normal' AND (c.imageLink!='' OR c.imageLink IS NOT NULL)");
        Map<String, Object> hqlmap = new HashMap<String, Object>();
        hql.append(" and c.siteId=:siteId");
        hqlmap.put("columnId2", 2658089L);
        */

        //updateMongo("57e08d2f8efbef345a296b1f");
        System.out.println();

        /*
        根据基础信息表的缩略图字段更新mongoDB里的所有数据
            1、将原真实文件存入 metaData 的 OriginalFilename 扩展字段备用
            2、将老数据的mongoID作为当前文件的唯一文件名，外加当前的扩展名作为此文件的新名称
            2、老的文件访问地址是以mongID作为主键的，在更新内容（缩略图）里面的/mongo/57fcb12d836d6ba84892b407不用更改文件名，只需在当前路径上加上扩散名即可
        */
        StringBuffer hql = new StringBuffer("from BaseContentEO c where (c.imageLink!='' OR c.imageLink IS NOT NULL)");
        Map<String, Object> hqlmap = new HashMap<String, Object>();
        //hql.append(" and c.imageLink=:imageLink");
        //hqlmap.put("imageLink", "57f5bc5be4b10e93657df402");
        List<BaseContentEO> list = baseContentDao.getEntitiesByHql(hql.toString(), hqlmap);
        for (int i = 0, l = list.size(); i < l; i++) {
            String imgId = list.get(i).getImageLink();
            Integer valLen = imgId.length();
            //查找是mongoDB 是的字符串
            if (isValid(imgId) && imgId.indexOf(".") == -1) {
                MongoFileVO vo = GetMongoFileNameEO(imgId);
                logger.info(imgId);
                //更新数据库字段，加上扩展名
                if (vo.getContentType() != null && !vo.getContentType().equals("")) {
                    list.get(i).setImageLink(imgId + "." + vo.getContentType());
                    baseContentService.updateEntity(list.get(i));
                }
            }

        }

        Long s2 = System.currentTimeMillis();

        return "更新新闻缩略图字段（" + list.size() + " 条，耗时：" + (s2 - s) / 1000 + "秒）！<a href=\"/site/convert/index\">返回</a>";
    }

    @RequestMapping("updatePIC")
    @ResponseBody
    public String updatePIC(ModelMap map) {

        Long s = System.currentTimeMillis();

        //基础表缩略图更新
        logger.info("图片新闻更新>>>");

        StringBuffer hql = new StringBuffer("from ContentPicEO c where (c.path!='' OR c.path IS NOT NULL)");
        Map<String, Object> hqlmap = new HashMap<String, Object>();
        List<ContentPicEO> list = contentPicDao.getEntitiesByHql(hql.toString(), hqlmap);
        for (int i = 0, l = list.size(); i < l; i++) {
            String imgId = list.get(i).getPath();
            Integer valLen = imgId.length();
            //查找是mongoDB 是的字符串
            if (isValid(imgId) && imgId.indexOf(".") == -1) {
                MongoFileVO vo = GetMongoFileNameEO(imgId);
                logger.info(imgId);
                //更新数据库字段，加上扩展名
                if (vo.getContentType() != null && !vo.getContentType().equals("")) {
                    list.get(i).setPath(imgId + "." + vo.getContentType());
                    contentPicService.updateEntity(list.get(i));
                }
            }
        }

        Long s2 = System.currentTimeMillis();

        return "更新图片新闻（" + list.size() + " 条，耗时：" + (s2 - s) / 1000 + "秒）！<a href=\"/site/convert/index\">返回</a>";
    }

    @RequestMapping("updateVideoNewsPath")
    @ResponseBody
    public String updateVideoNewsPath(ModelMap map) {

        Long s = System.currentTimeMillis();

        //基础表缩略图更新
        logger.info("视频新闻更新>>>");

        StringBuffer hql = new StringBuffer("from VideoNewsEO c where (c.videoPath!='' OR c.videoPath IS NOT NULL)");
        Map<String, Object> hqlmap = new HashMap<String, Object>();
        List<VideoNewsEO> list = videoNewsDao.getEntitiesByHql(hql.toString(), hqlmap);
        for (int i = 0, l = list.size(); i < l; i++) {
            String imgId = list.get(i).getVideoPath();
            Integer valLen = imgId.length();
            //查找是mongoDB 是的字符串
            if (isValid(imgId) && imgId.indexOf(".") == -1) {
                MongoFileVO vo = GetMongoFileNameEO(imgId);
                logger.info(imgId);
                //更新数据库字段，加上扩展名
                if (vo.getContentType() != null && !vo.getContentType().equals("")) {
                    list.get(i).setVideoPath(imgId + "." + vo.getContentType());
                    videoNewsService.updateEntity(list.get(i));
                }
            }
        }

        Long s2 = System.currentTimeMillis();

        return "更新视频新闻（" + list.size() + " 条，耗时：" + (s2 - s) / 1000 + "秒）！<a href=\"/site/convert/index\">返回</a>";
    }

    @RequestMapping("updateWateMarkPath")
    @ResponseBody
    public String updateWateMarkPath(ModelMap map) {

        Long s = System.currentTimeMillis();

        //基础表缩略图更新
        logger.info("水印图片更新>>>");

        StringBuffer hql = new StringBuffer("from WaterMarkConfigEO c where (c.picPath!='' OR c.picPath IS NOT NULL)");
        Map<String, Object> hqlmap = new HashMap<String, Object>();
        List<WaterMarkConfigEO> list = waterMarkConfigDao.getEntitiesByHql(hql.toString(), hqlmap);
        for (int i = 0, l = list.size(); i < l; i++) {
            String imgId = list.get(i).getPicPath();
            Integer valLen = imgId.length();
            //查找是mongoDB 是的字符串
            if (isValid(imgId) && imgId.indexOf(".") == -1) {
                MongoFileVO vo = GetMongoFileNameEO(imgId);
                logger.info(imgId);
                //更新数据库字段，加上扩展名
                if (vo.getContentType() != null && !vo.getContentType().equals("")) {
                    list.get(i).setPicPath(imgId + "." + vo.getContentType());
                    waterMarkConfigService.updateEntity(list.get(i));
                }
            }
        }

        Long s2 = System.currentTimeMillis();

        return "更新水印图片（" + list.size() + " 条，耗时：" + (s2 - s) / 1000 + "秒）！<a href=\"/site/convert/index\">返回</a>";
    }

    @RequestMapping("updateMessageReply")
    @ResponseBody
    public String updateMessageReply(ModelMap map) {

        Long s = System.currentTimeMillis();

        //基础表缩略图更新
        logger.info("表格留言回复附件更新>>>");

        StringBuffer hql = new StringBuffer("from MessageBoardReplyEO c where (c.attachId!='' OR c.attachId IS NOT NULL)");
        Map<String, Object> hqlmap = new HashMap<String, Object>();
        List<MessageBoardReplyEO> list = messageBoardReplyDao.getEntitiesByHql(hql.toString(), hqlmap);
        for (int i = 0, l = list.size(); i < l; i++) {
            String imgId = list.get(i).getAttachId();
            Integer valLen = imgId.length();
            //查找是mongoDB 是的字符串
            if (isValid(imgId) && imgId.indexOf(".") == -1) {
                MongoFileVO vo = GetMongoFileNameEO(imgId);
                logger.info(imgId);
                //更新数据库字段，加上扩展名
                if (vo.getContentType() != null && !vo.getContentType().equals("")) {
                    list.get(i).setAttachId(imgId + "." + vo.getContentType());
                    messageBoardReplyService.updateEntity(list.get(i));
                }
            }
        }

        Long s2 = System.currentTimeMillis();

        return "更新留言回复附件路径（" + list.size() + " 条，耗时：" + (s2 - s) / 1000 + "秒）！<a href=\"/site/convert/index\">返回</a>";
    }

    /**
     * 微博图片
     *
     * @param map
     * @return
     */
    @RequestMapping("updateWeiboPIC")
    @ResponseBody
    public String updateWeiboPIC(ModelMap map) {

        Long s = System.currentTimeMillis();

        //基础表缩略图更新
        logger.info("微博图片更新>>>");

        StringBuffer hql = new StringBuffer("from WeiboRadioContentEO c where (c.picUrl!='' OR c.picUrl IS NOT NULL)");
        Map<String, Object> hqlmap = new HashMap<String, Object>();
        List<WeiboRadioContentEO> list = weiboRadioContentDao.getEntitiesByHql(hql.toString(), hqlmap);
        for (int i = 0, l = list.size(); i < l; i++) {
            String imgId = list.get(i).getPicUrl();
            Integer valLen = imgId.length();
            //查找是mongoDB 是的字符串
            if (isValid(imgId) && imgId.indexOf(".") == -1) {
                MongoFileVO vo = GetMongoFileNameEO(imgId);
                logger.info(imgId);
                //更新数据库字段，加上扩展名
                if (vo.getContentType() != null && !vo.getContentType().equals("")) {
                    list.get(i).setPicUrl(imgId + "." + vo.getContentType());
                    weiboRadioContentService.updateEntity(list.get(i));
                }
            }
        }

        Long s2 = System.currentTimeMillis();

        return "更新微博图片（" + list.size() + " 条，耗时：" + (s2 - s) / 1000 + "秒）！<a href=\"/site/convert/index\">返回</a>";
    }

    /**
     * 表格资源库
     *
     * @param map
     * @return
     */
    @RequestMapping("updateTableUpload")
    @ResponseBody
    public String updateTableUpload(ModelMap map) {

        Long s = System.currentTimeMillis();

        //基础表缩略图更新
        logger.info("表格资源库更新>>>");

        StringBuffer hql = new StringBuffer("from CmsTableResourcesEO c where (c.uploadUrl!='' OR c.uploadUrl IS NOT NULL)");
        Map<String, Object> hqlmap = new HashMap<String, Object>();
        List<CmsTableResourcesEO> list = tableResourcesDao.getEntitiesByHql(hql.toString(), hqlmap);
        for (int i = 0, l = list.size(); i < l; i++) {
            String imgId = list.get(i).getUploadUrl();
            Integer valLen = imgId.length();
            //查找是mongoDB 是的字符串
            if (isValid(imgId) && imgId.indexOf(".") == -1) {
                MongoFileVO vo = GetMongoFileNameEO(imgId);
                logger.info(imgId);
                //更新数据库字段，加上扩展名
                if (vo.getContentType() != null && !vo.getContentType().equals("")) {
                    list.get(i).setUploadUrl(imgId + "." + vo.getContentType());
                    tableResourcesService.updateEntity(list.get(i));
                }
            }
        }

        Long s2 = System.currentTimeMillis();

        return "更新表格资源库路径（" + list.size() + " 条，耗时：" + (s2 - s) / 1000 + "秒）！<a href=\"/site/convert/index\">返回</a>";
    }

    /**
     * 法规资源库
     *
     * @param map
     * @return
     */
    @RequestMapping("updateRelatedRuleUpload")
    @ResponseBody
    public String updateRelatedRuleUpload(ModelMap map) {

        Long s = System.currentTimeMillis();

        //基础表缩略图更新
        logger.info("法规资源库更新>>>");

        StringBuffer hql = new StringBuffer("from CmsRelatedRuleEO c where (c.uploadUrl!='' OR c.uploadUrl IS NOT NULL)");
        Map<String, Object> hqlmap = new HashMap<String, Object>();
        List<CmsRelatedRuleEO> list = relatedRuleDao.getEntitiesByHql(hql.toString(), hqlmap);
        for (int i = 0, l = list.size(); i < l; i++) {
            String imgId = list.get(i).getUploadUrl();
            Integer valLen = imgId.length();
            //查找是mongoDB 是的字符串
            if (isValid(imgId) && imgId.indexOf(".") == -1) {
                MongoFileVO vo = GetMongoFileNameEO(imgId);
                logger.info(imgId);
                //更新数据库字段，加上扩展名
                if (vo.getContentType() != null && !vo.getContentType().equals("")) {
                    list.get(i).setUploadUrl(imgId + "." + vo.getContentType());
                    relatedRuleService.updateEntity(list.get(i));
                }
            }
        }

        Long s2 = System.currentTimeMillis();

        return "更新法规资源库路径（" + list.size() + " 条，耗时：" + (s2 - s) / 1000 + "秒）！<a href=\"/site/convert/index\">返回</a>";
    }

    /**
     * 网上办事
     *
     * @param map
     * @return
     */
    @RequestMapping("updateWorkGuide")
    @ResponseBody
    public String updateNetWork(ModelMap map) {

        logger.info("更新网上办事附件图片地址>>>>>>");
        Long s = System.currentTimeMillis();
        //返回所有mongoDB中的内容
        List<CmsWorkGuideEO> list = workGuideService.getEOs();
        if (null != list) {
            for (int i = 0; i < list.size(); i++) {
                CmsWorkGuideEO weo = list.get(0);
                weo.setContent(matchAndReplaceMongDbPath(weo.getContent()));
                weo.setSetAccord(matchAndReplaceMongDbPath(weo.getSetAccord()));
                weo.setApplyCondition(matchAndReplaceMongDbPath(weo.getApplyCondition()));
                weo.setHandleData(matchAndReplaceMongDbPath(weo.getHandleData()));
                weo.setHandleProcess(matchAndReplaceMongDbPath(weo.getHandleProcess()));
            }
        }
        workGuideService.updateEntities(list);
        Long s2 = System.currentTimeMillis();
        return "更新MongoFileName（" + (null != list ? list.size() : 0) + " 条，耗时：" + (s2 - s) / 1000 + "秒）！<a href=\"/site/convert/index\">返回</a>";
    }

    /**
     * 信息报送
     *
     * @param map
     * @return
     */
    @RequestMapping("updateMsgSubmit")
    @ResponseBody
    public String updateMsgSubmit(ModelMap map) {

        logger.info("更新信息报送附件图片地址>>>>>>");
        Long s = System.currentTimeMillis();
        //返回所有mongoDB中的内容
        List<CmsMsgSubmitEO> list = msgSubmitService.getEOs();
        if (null != list) {
            for (int i = 0; i < list.size(); i++) {
                CmsMsgSubmitEO mse = list.get(0);
                mse.setImageLink(matchAndReplaceMongDbPath(mse.getImageLink()));
            }
        }
        msgSubmitService.updateEntities(list);
        Long s2 = System.currentTimeMillis();
        return "更新MongoFileName（" + (null != list ? list.size() : 0) + " 条，耗时：" + (s2 - s) / 1000 + "秒）！<a href=\"/site/convert/index\">返回</a>";
    }

    /**
     * 替换匹配mongdb地址公共方法
     *
     * @param content
     * @return
     */
    private String matchAndReplaceMongDbPath(String content) {
        if (!StringUtils.isEmpty(content)) {
            String pattern = "/mongo/([^\"]*)";
            Pattern r = Pattern.compile(pattern);
            //查询字符串是否有mongo 打头的内容
            Matcher m = r.matcher(content);
            if (m.find()) {
                //得到分组中的MongoID
                String mongoId = m.group(1);
                //根据MongId得到File EO
                MongoFileVO vo = GetMongoFileNameEO(mongoId);
                //根据判断扩展名来判断文件是否存在
                if (vo.getContentType() != null && !vo.getContentType().equals("")) {
                    content.replaceAll(m.group(1), m.group(1) + "." + vo.getContentType());
                }
            }
        }

        return content;
    }

    @RequestMapping("updateWeXinPIC")
    @ResponseBody
    public String updateWeXinPIC(ModelMap map) {

        Long s = System.currentTimeMillis();

        //基础表缩略图更新
        logger.info("微信素材缩略图更新>>>");

        StringBuffer hql = new StringBuffer("from WeChatArticleEO c where (c.thumbImg!='' OR c.thumbImg IS NOT NULL)");
        Map<String, Object> hqlmap = new HashMap<String, Object>();
        List<WeChatArticleEO> list = weChatArticleDao.getEntitiesByHql(hql.toString(), hqlmap);
        for (int i = 0, l = list.size(); i < l; i++) {
            String imgId = list.get(i).getThumbImg();
            Integer valLen = imgId.length();
            //查找是mongoDB 是的字符串
            if (isValid(imgId) && imgId.indexOf(".") == -1) {
                logger.info(imgId);
                MongoFileVO vo = GetMongoFileNameEO(imgId);
                //更新数据库字段，加上扩展名
                if (vo.getContentType() != null && !vo.getContentType().equals("")) {
                    list.get(i).setThumbImg(imgId + "." + vo.getContentType());
                    weChatArticleService.updateEntity(list.get(i));
                }
            }
        }

        Long s2 = System.currentTimeMillis();

        return "更新微信素材缩略图路径（" + list.size() + " 条，耗时：" + (s2 - s) / 1000 + "秒）！<a href=\"/site/convert/index\">返回</a>";
    }

    public boolean isValid(String s) {
        if (s == null) {
            return false;
        } else {
            int len = s.length();
            if (len != 24) {
                return false;
            } else {
                for (int i = 0; i < len; ++i) {
                    char c = s.charAt(i);
                    if ((c < 48 || c > 57) && (c < 97 || c > 102) && (c < 65 || c > 70)) {
                        return false;
                    }
                }

                return true;
            }
        }
    }

    /**
     * @param in
     * @Description InputStream转换为byte[]
     */
    public static byte[] inputStreamToByte(InputStream in) {
        byte[] bt = null;
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        byte[] buffer = new byte[2 << 12];
        int n = 0;
        try {
            while ((n = in.read(buffer)) != -1) {
                out.write(buffer, 0, n);
            }
            bt = out.toByteArray();
            out.flush();
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bt;
    }

    /**
     * 字符串转换成日期
     *
     * @param str
     * @return date
     */
    public static Date StrToDate(String str) {

        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = null;
        try {
            date = format.parse(str);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date;
    }




    @Resource
    private ISurveyThemeService surveyThemeService;

    @Resource
    private ISurveyThemeDao surveyThemeDao;

    @Resource
    private ICollectInfoService collectInfoService;

    @Resource
    private ICollectInfoDao collectInfoDao;

    @Resource
    private IInterviewInfoService interviewInfoService;

    @Resource
    private IInterviewInfoDao interviewInfoDao;

    /**
     * 民意征集、网上调查、在线访谈
     * @param map
     * @return
     */
    @RequestMapping("updateZMHD")
    @ResponseBody
    public String updateZMHD(ModelMap map) {

        logger.info("更新民意征集、网上调查、在线访谈备注>>>>>>");
        int c  = 0;
        Long s = System.currentTimeMillis();
        Map<String, Object> hqlmap = new HashMap<String, Object>();
        StringBuffer hql = new StringBuffer("from SurveyThemeEO c where (c.content !='' OR c.content IS NOT NULL)");
        List<SurveyThemeEO> sts = surveyThemeDao.getEntitiesByHql(hql.toString(), hqlmap);
        if(sts != null && sts.size() >0){
            for(int i = 0;i < sts.size();i++) {
                SurveyThemeEO eo = sts.get(i);
                if(!StringUtils.isEmpty(eo.getContent())){
                    eo.setContent(matchAndReplaceMongDbPath(eo.getContent()));
                    surveyThemeService.updateEntity(eo);
                    c++;
                }
            }
        }

        StringBuffer hql2 = new StringBuffer("from CollectInfoEO c where (c.content !='' OR c.content IS NOT NULL)");
        List<CollectInfoEO> cis = collectInfoDao.getEntitiesByHql(hql2.toString(), hqlmap);
        if(cis != null && cis.size() >0){
            for(int i = 0;i < cis.size();i++) {
                CollectInfoEO eo = cis.get(i);
                if(!StringUtils.isEmpty(eo.getContent())){
                    eo.setContent(matchAndReplaceMongDbPath(eo.getContent()));
                    collectInfoService.updateEntity(eo);
                    c++;
                }
            }
        }
        StringBuffer hql3 = new StringBuffer("from InterviewInfoEO c where (c.desc !='' OR c.desc IS NOT NULL)");
        List<InterviewInfoEO> iis = interviewInfoDao.getEntitiesByHql(hql3.toString(), hqlmap);
        if(iis != null && iis.size() >0){
            for(int i = 0;i < iis.size();i++) {
                InterviewInfoEO eo = iis.get(i);
                if(!StringUtils.isEmpty(eo.getDesc())){
                    eo.setContent(matchAndReplaceMongDbPath(eo.getDesc()));
                    interviewInfoService.updateEntity(eo);
                    c++;
                }
            }
        }

        Long s2 = System.currentTimeMillis();
        return "更新MongoFileName（" + c + " 条，耗时：" + (s2 - s) / 1000 + "秒）！<a href=\"/site/convert/index\">返回</a>";
    }

}