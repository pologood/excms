package cn.lonsun.solr;

import cn.lonsun.content.ideacollect.internal.service.ICollectInfoService;
import cn.lonsun.content.ideacollect.vo.CollectInfoVO;
import cn.lonsun.content.internal.dao.IBaseContentDao;
import cn.lonsun.content.internal.dao.IKnowledgeBaseDao;
import cn.lonsun.content.internal.entity.BaseContentEO;
import cn.lonsun.content.internal.service.IBaseContentService;
import cn.lonsun.content.internal.service.IGuestBookService;
import cn.lonsun.content.interview.internal.service.IInterviewInfoService;
import cn.lonsun.content.interview.vo.InterviewInfoVO;
import cn.lonsun.content.leaderwin.internal.service.ILeaderInfoService;
import cn.lonsun.content.leaderwin.vo.LeaderInfoVO;
import cn.lonsun.content.messageBoard.service.IMessageBoardService;
import cn.lonsun.content.survey.internal.service.ISurveyThemeService;
import cn.lonsun.content.survey.vo.SurveyThemeVO;
import cn.lonsun.content.vo.ContentPageVO;
import cn.lonsun.content.vo.GuestBookSearchVO;
import cn.lonsun.content.vo.KnowledgeBaseVO;
import cn.lonsun.content.vo.MessageBoardSearchVO;
import cn.lonsun.core.base.entity.AMockEntity;
import cn.lonsun.core.exception.BaseRunTimeException;
import cn.lonsun.core.util.Pagination;
import cn.lonsun.core.util.SpringContextHolder;
import cn.lonsun.core.util.TipsMode;
import cn.lonsun.mongodb.entity.ContentMongoEO;
import cn.lonsun.mongodb.service.ContentMongoServiceImpl;
import cn.lonsun.net.service.entity.CmsWorkGuideEO;
import cn.lonsun.net.service.service.IWorkGuideService;
import cn.lonsun.publicInfo.internal.service.IPublicContentService;
import cn.lonsun.site.site.internal.entity.ColumnMgrEO;
import cn.lonsun.solr.vo.SolrIndexVO;
import cn.lonsun.util.HtmlUtil;
import org.apache.solr.client.solrj.SolrServerException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @author gu.fei
 * @version 2017-04-25 8:18
 */
public class SolrBaseIndexUtil {

    private static final Logger logger = LoggerFactory.getLogger(SolrFactory.class);

    private static IBaseContentService baseContentService;
    private static IBaseContentDao baseContentDao;
    private static IMessageBoardService messageBoardService;
    private static IWorkGuideService workGuideService;
    private static IGuestBookService guestBookService;
    private static IPublicContentService publicContentService;
    private static ISurveyThemeService surveyThemeService;
    private static ICollectInfoService collectInfoService;
    private static ILeaderInfoService leaderInfoService;
    private static IInterviewInfoService interviewInfoService;
    private static ContentMongoServiceImpl mongoService;
    private static IKnowledgeBaseDao knowledgeBaseDao;

    static {
        baseContentService = SpringContextHolder.getBean("baseContentService");
        baseContentDao = SpringContextHolder.getBean("baseContentDao");
        workGuideService = SpringContextHolder.getBean("workGuideService");
        guestBookService = SpringContextHolder.getBean("guestBookService");
        messageBoardService = SpringContextHolder.getBean("messageBoardService");
        surveyThemeService = SpringContextHolder.getBean("surveyThemeService");
        collectInfoService = SpringContextHolder.getBean("collectInfoService");
        leaderInfoService = SpringContextHolder.getBean("leaderInfoService");
        interviewInfoService =  SpringContextHolder.getBean("interviewInfoService");
        publicContentService = SpringContextHolder.getBean(IPublicContentService.class);
        mongoService = SpringContextHolder.getBean(ContentMongoServiceImpl.class);
        knowledgeBaseDao = SpringContextHolder.getBean(IKnowledgeBaseDao.class);
    }

    /**
     * 创建图片新闻索引
     */
    public static void createPictureNewsIndex() {
        // 删除
        deleteIndex(BaseContentEO.TypeCode.pictureNews.toString());
        List<BaseContentEO> pictureNews = baseContentService.getContents(BaseContentEO.TypeCode.pictureNews.toString());
        if(null != pictureNews && pictureNews.size() > 0){
            logger.info("获取图片新闻条数=" + pictureNews.size() + "条");
        }

        Set<SolrIndexVO> vos = new HashSet<SolrIndexVO>();
        for(BaseContentEO eo : pictureNews) {
            SolrIndexVO vo = new SolrIndexVO();
            if(null != eo.getSiteId()) {
                vo.setId(eo.getId() + "");
                vo.setColumnId(eo.getColumnId());
                vo.setTitle(eo.getTitle());
                vo.setRemark(eo.getRemarks());
                vo.setContent(eo.getArticle());
                vo.setSiteId(eo.getSiteId());
                vo.setTypeCode(eo.getTypeCode());
                vo.setCreateDate(eo.getPublishDate());
                vo.setAuthor(eo.getAuthor());
                if(null != eo.getRedirectLink()) {
                    vo.setUrl(eo.getRedirectLink());
                }
                vos.add(vo);
            }
        }
        createIndex(vos,"图片新闻");
    }

    /**
     * 创建视频新闻索引
     */
    public static void createVideoNewsIndex() {
        // 删除
        deleteIndex(BaseContentEO.TypeCode.videoNews.toString());
        List<BaseContentEO> videoNews = baseContentService.getContents(BaseContentEO.TypeCode.videoNews.toString());
        if(null != videoNews && videoNews.size() > 0){
            logger.info("获取视频新闻条数=" + videoNews.size() + "条");
        }

        Set<SolrIndexVO> vos = new HashSet<SolrIndexVO>();
        for(BaseContentEO eo : videoNews) {
            SolrIndexVO vo = new SolrIndexVO();
            if(null != eo.getSiteId()) {
                vo.setId(eo.getId() + "");
                vo.setColumnId(eo.getColumnId());
                vo.setTitle(eo.getTitle());
                vo.setRemark(eo.getRemarks());
                vo.setContent(eo.getArticle());
                vo.setSiteId(eo.getSiteId());
                vo.setTypeCode(eo.getTypeCode());
                vo.setCreateDate(eo.getPublishDate());
                vo.setAuthor(eo.getAuthor());
                vos.add(vo);
            }
        }

        createIndex(vos,"视频新闻");
    }

    /**
     * 创建网上办事索引
     */
    public static void createWorkGuidesIndex() {
        // 删除
        deleteIndex(BaseContentEO.TypeCode.workGuide.toString());
        List<CmsWorkGuideEO> workGuideEOs = workGuideService.getEOs();
        if(null != workGuideEOs && workGuideEOs.size() > 0){
            logger.info("获取办事条数=" + workGuideEOs.size() + "条");
        }
        Set<SolrIndexVO> vos = new HashSet<SolrIndexVO>();
        if(null != workGuideEOs) {
            for(CmsWorkGuideEO eo : workGuideEOs) {
                SolrIndexVO vo = new SolrIndexVO();
                vo.setId(eo.getContentId() + "");
                vo.setTitle(eo.getName());
                vo.setColumnId(eo.getColumnId());
                vo.setContent(eo.getContent());
                vo.setSetAccord(HtmlUtil.getTextFromTHML(eo.getSetAccord()));
                vo.setApplyCondition(HtmlUtil.getTextFromTHML(eo.getApplyCondition()));
                vo.setHandleData(HtmlUtil.getTextFromTHML(eo.getHandleData()));
                vo.setHandleProcess(HtmlUtil.getTextFromTHML(eo.getHandleProcess()));
                vo.setSiteId(eo.getSiteId());
                vo.setTypeCode(BaseContentEO.TypeCode.workGuide.toString());
                vo.setCreateDate(eo.getPublishDate());
                if(null != eo.getLinkUrl()) {
                    vo.setUrl(eo.getLinkUrl());
                }
                vos.add(vo);
            }
        }

        createIndex(vos,"网上办事");
    }

    /**
     * 创建留言索引
     */
    public static void createGuestsIndex() {
        // 删除
        deleteIndex(BaseContentEO.TypeCode.guestBook.toString());
        deleteIndex(BaseContentEO.TypeCode.messageBoard.toString());
        List<GuestBookSearchVO> guestBookSearchVOs = guestBookService.getAllGuestBook();
        if(null != guestBookSearchVOs && guestBookSearchVOs.size() > 0){
            logger.info("获取留言条数=" + guestBookSearchVOs.size() + "条");
        }
        List<MessageBoardSearchVO> messageBoardSearchVOs = messageBoardService.getAllPulishMessageBoard();
        if(null != messageBoardSearchVOs && messageBoardSearchVOs.size() > 0){
            logger.info("获取新版留言条数=" + messageBoardSearchVOs.size() + "条");
        }
        Set<SolrIndexVO> vos = new HashSet<SolrIndexVO>();
        if(null != messageBoardSearchVOs) {
            for(MessageBoardSearchVO eo : messageBoardSearchVOs) {
                SolrIndexVO vo = new SolrIndexVO();
                vo.setId(eo.getBaseContentId() + "");
                vo.setTitle(eo.getTitle());
                vo.setColumnId(eo.getColumnId());
                vo.setContent(eo.getMessageBoardContent());
                vo.setSiteId(eo.getSiteId());
                vo.setTypeCode(BaseContentEO.TypeCode.guestBook.toString());
                vo.setCreateDate(eo.getPublishDate());
                vo.setType(eo.getClassCode());
                vos.add(vo);
            }
        }
        if(null != guestBookSearchVOs) {
            for(GuestBookSearchVO eo : guestBookSearchVOs) {
                SolrIndexVO vo = new SolrIndexVO();
                vo.setId(eo.getBaseContentId() + "");
                vo.setTitle(eo.getTitle());
                vo.setColumnId(eo.getColumnId());
                vo.setContent(eo.getGuestBookContent());
                vo.setSiteId(eo.getSiteId());
                vo.setTypeCode(BaseContentEO.TypeCode.guestBook.toString());
                vo.setCreateDate(eo.getPublishDate());
                vo.setType(eo.getClassCode());
                vos.add(vo);
            }
        }

        createIndex(vos,"留言");
    }

    /**
     * 创建调查管理索引
     */
    public static void createSurveysIndex() {
        deleteIndex(BaseContentEO.TypeCode.survey.toString());
        //调查管理
        List<SurveyThemeVO> surveys = surveyThemeService.getSurveyThemeVOS(BaseContentEO.TypeCode.survey.toString());
        if(null != surveys && surveys.size() > 0){
            logger.info("获取调查管理条数=" + surveys.size() + "条");
        }
        Set<SolrIndexVO> vos = new HashSet<SolrIndexVO>();
        for(SurveyThemeVO eo : surveys) {
            SolrIndexVO vo = new SolrIndexVO();
            vo.setId(eo.getContentId() + "");
            vo.setTitle(eo.getTitle());
            vo.setColumnId(eo.getColumnId());
            vo.setContent(HtmlUtil.getTextFromTHML(eo.getContent()));
            vo.setSiteId(eo.getSiteId());
            vo.setTypeCode(BaseContentEO.TypeCode.survey.toString());
            vo.setCreateDate(eo.getCreateDate());
            vos.add(vo);
        }
        createIndex(vos,"调查管理");
    }

    /**
     * 创建网上调查索引
     */
    public static void createReviewsIndex() {
        deleteIndex(BaseContentEO.TypeCode.reviewInfo.toString());
        //网上调查
        List<SurveyThemeVO> reviews = surveyThemeService.getSurveyThemeVOS(BaseContentEO.TypeCode.reviewInfo.toString());
        if(null != reviews && reviews.size() > 0){
            logger.info("获取网上调查条数=" + reviews.size() + "条");
        }
        Set<SolrIndexVO> vos = new HashSet<SolrIndexVO>();
        if(null != reviews && reviews.size() > 0) {
            for(SurveyThemeVO eo : reviews) {
                SolrIndexVO vo = new SolrIndexVO();
                vo.setId(eo.getContentId() + "");
                vo.setTitle(eo.getTitle());
                vo.setColumnId(eo.getColumnId());
                vo.setContent(HtmlUtil.getTextFromTHML(eo.getContent()));
                vo.setSiteId(eo.getSiteId());
                vo.setTypeCode(BaseContentEO.TypeCode.reviewInfo.toString());
                vo.setCreateDate(eo.getCreateDate());
                vos.add(vo);
            }
        }
        createIndex(vos,"网上调查");
    }

    /**
     * 创建在线访谈索引
     */
    public static void createInterviewsIndex() {
        deleteIndex(BaseContentEO.TypeCode.interviewInfo.toString());
        //在线访谈
        List<InterviewInfoVO> interviews = interviewInfoService.getInterviewInfoVOS(BaseContentEO.TypeCode.interviewInfo.toString());
        if(null != interviews && interviews.size() > 0){
            logger.info("获取在线访谈条数=" + interviews.size() + "条");
        }
        Set<SolrIndexVO> vos = new HashSet<SolrIndexVO>();
        if(null != interviews && interviews.size() > 0) {
            for(InterviewInfoVO eo : interviews) {
                SolrIndexVO vo = new SolrIndexVO();
                vo.setId(eo.getContentId() + "");
                vo.setTitle(eo.getTitle());
                vo.setColumnId(eo.getColumnId());
                vo.setContent(HtmlUtil.getTextFromTHML(eo.getDesc()));
                vo.setSiteId(eo.getSiteId());
                vo.setTypeCode(BaseContentEO.TypeCode.interviewInfo.toString());
                vo.setCreateDate(eo.getCreateDate());
                vos.add(vo);
            }
        }
        createIndex(vos,"在线访谈");
    }

    /**
     * 创建民意征集索引
     */
    public static void createCollectInfosIndex() {
        deleteIndex(BaseContentEO.TypeCode.collectInfo.toString());
        //民意征集
        List<CollectInfoVO> collectInfos = collectInfoService.getCollectInfoVOS(BaseContentEO.TypeCode.collectInfo.toString());
        if(null != collectInfos && collectInfos.size() > 0){
            logger.info("获取民意征集条数=" + collectInfos.size() + "条");
        }
        Set<SolrIndexVO> vos = new HashSet<SolrIndexVO>();
        if(null != collectInfos && collectInfos.size() > 0) {
            for(CollectInfoVO eo : collectInfos) {
                SolrIndexVO vo = new SolrIndexVO();
                vo.setId(eo.getContentId() + "");
                vo.setTitle(eo.getTitle());
                vo.setColumnId(eo.getColumnId());
                vo.setContent(HtmlUtil.getTextFromTHML(eo.getDesc()));
                vo.setSiteId(eo.getSiteId());
                vo.setTypeCode(BaseContentEO.TypeCode.collectInfo.toString());
                vo.setCreateDate(eo.getCreateDate());
                vos.add(vo);
            }
        }
        createIndex(vos,"民意征集");
    }

    /**
     * 创建领导之窗索引
     */
    public static void createLeaderInfosIndex() {
        deleteIndex(BaseContentEO.TypeCode.leaderInfo.toString());
        //领导之窗
        List<LeaderInfoVO> leaderInfos = leaderInfoService.getLeaderInfoVOS(BaseContentEO.TypeCode.leaderInfo.toString());
        if(null != leaderInfos && leaderInfos.size() > 0){
            logger.info("获取领导之窗条数=" + leaderInfos.size() + "条");
        }
        Set<SolrIndexVO> vos = new HashSet<SolrIndexVO>();
        if(null != leaderInfos && leaderInfos.size() > 0) {
            for(LeaderInfoVO eo : leaderInfos) {
                SolrIndexVO vo = new SolrIndexVO();
                vo.setId(eo.getContentId() + "");
                vo.setTitle(eo.getName());
                vo.setColumnId(eo.getColumnId());
                vo.setContent(HtmlUtil.getTextFromTHML(eo.getJobResume()));
                vo.setSiteId(eo.getSiteId());
                vo.setTypeCode(BaseContentEO.TypeCode.leaderInfo.toString());
                vo.setCreateDate(eo.getIssuedTime());
                vos.add(vo);
            }
        }
        createIndex(vos,"领导之窗");
    }

    /**
     * 文章新闻索引创建
     */
    public static void createArticleNewsIndex() {
        // 删除
        deleteIndex(BaseContentEO.TypeCode.articleNews.toString());
        // 分页查询建索引，每次5000;
        Long pageIndex = 0L;
        Integer pageSize = 10000;
        // 先查询一次
        Pagination pagination = createArticleNewsIndex(pageIndex++, pageSize);
        if (null != pagination) {
            // 创建索引
            createArticleNewsIndex(pagination.getData());
            Long pageCount = pagination.getPageCount();
            while (pageIndex < pageCount) {// 继续分页查询
                pagination = createArticleNewsIndex(pageIndex++, pageSize);
                if (null != pagination) {
                    createArticleNewsIndex(pagination.getData());
                }
            }
        }
    }

    /**
     * 信息公开索引重建
     */
    public static void createPublicInfoIndex() {
        publicContentService.createPublicIndex();
    }

    private static Pagination createArticleNewsIndex(Long pageIndex, Integer pageSize) {
        StringBuilder sb = new StringBuilder("select id as id,");
        sb.append(" title as title,");
        sb.append(" columnId as columnId,");
        sb.append(" siteId as siteId,");
        sb.append(" author as author,");
        sb.append(" typeCode as typeCode,");
        sb.append(" remarks as remarks,");
        sb.append(" publishDate as publishDate");
        sb.append(" from BaseContentEO where recordStatus='Normal' and typeCode=? and isPublish = 1");
        Pagination pagination = baseContentDao.getPagination(pageIndex, pageSize,sb.toString(),
                new Object[]{BaseContentEO.TypeCode.articleNews.toString()},BaseContentEO.class);
        logger.info("创建文字新闻索引，总共数量:" + pagination.getTotal());
        logger.info("创建文字新闻索引，已完成数量:" + (pageIndex*pageSize + pagination.getData().size()));
        logger.info("创建文字新闻索引，页数：" + pagination.getPageCount());
        logger.info("创建文字新闻索引，已完成页数：" + (pageIndex+1));
        if (null != pagination) {// 设置内容字段
            List<BaseContentEO> data = (List<BaseContentEO>)pagination.getData();
            if (null != data && !data.isEmpty()) {
                List<Long> ids = new ArrayList<Long>();
                for (BaseContentEO li : data) {
                    if (null != li && null != li.getId()) {
                        ids.add(li.getId());
                    }
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
                    for (BaseContentEO li : data) {
                        String content = map.get(li.getId());
                        if (null != content) {
                            li.setArticle(content);
                        }
                    }
                }
            }
        }
        return pagination;
    }

    private static void createArticleNewsIndex(List<?> data) {
        if (null == data || data.isEmpty()) {
            return;
        }
        Set<SolrIndexVO> vos = new HashSet<SolrIndexVO>();
        for (Object o : data) {
            SolrIndexVO vo = new SolrIndexVO();
            BaseContentEO eo = (BaseContentEO) o;
            if(null != eo.getSiteId()) {
                vo.setId(eo.getId() + "");
                vo.setColumnId(eo.getColumnId());
                vo.setTitle(eo.getTitle());
                vo.setRemark(eo.getRemarks());
                vo.setContent(HtmlUtil.getTextFromTHML(eo.getArticle()));
                vo.setSiteId(eo.getSiteId());
                vo.setTypeCode(eo.getTypeCode());
                vo.setCreateDate(eo.getPublishDate());
                vo.setAuthor(eo.getAuthor());
                if(null != eo.getRedirectLink()) {
                    vo.setUrl(eo.getRedirectLink());
                }

                vos.add(vo);
            }
        }
        try {
            SolrFactory.createIndex(vos);
        } catch (Throwable e) {
            throw new BaseRunTimeException(TipsMode.Message.toString(), "文字新闻索引创建失败！");
        }
    }

    /**
     * 问答知识库索引创建
     */
    public static void createKnowledgeBaseIndex() {
        // 删除
        deleteIndex(BaseContentEO.TypeCode.knowledgeBase.toString());
        // 分页查询建索引，每次5000;
        Long pageIndex = 0L;
        Integer pageSize = 99;
        ContentPageVO query = new ContentPageVO();
        query.setColumnId(6331498L);
        query.setIsPublish(1);
        query.setTypeCode(BaseContentEO.TypeCode.knowledgeBase.toString());
        query.setPageIndex(pageIndex);
        query.setPageSize(pageSize);
        query.setNoAuthority(true);
        // 先查询一次
        Pagination pagination = knowledgeBaseDao.getPage(query);
        if (null != pagination) {
            // 创建索引
            createKnowledgeBaseIndex((List<KnowledgeBaseVO>) pagination.getData());
            Long pageCount = pagination.getPageCount();
            while (pageIndex < pageCount) {// 继续分页查询
                query.setPageIndex(++pageIndex);
                pagination = knowledgeBaseDao.getPage(query);
                if (null != pagination) {
                    createKnowledgeBaseIndex((List<KnowledgeBaseVO>) pagination.getData());
                }
            }
        }
    }

    private static void createKnowledgeBaseIndex(List<KnowledgeBaseVO> contents) {
        Set<SolrIndexVO> vos = new HashSet<SolrIndexVO>();
        for (KnowledgeBaseVO eo : contents) {
            SolrIndexVO vo = new SolrIndexVO();
            if (null != eo.getSiteId()) {
                vo.setId(eo.getContentId() + "");
                vo.setColumnId(eo.getColumnId());
                vo.setTitle(eo.getTitle());
                //vo.setRemark(eo.getRemarks());
                vo.setContent(eo.getContent());
                vo.setSiteId(eo.getSiteId());
                vo.setTypeCode(eo.getTypeCode());
                vo.setCreateDate(eo.getCreateDate());
                vo.setAuthor(eo.getAuthor());
                vos.add(vo);
            }
        }
        try {
            SolrFactory.createIndex(vos);
        } catch (Throwable e) {
            throw new BaseRunTimeException(TipsMode.Message.toString(), "问答知识库索引创建失败！");
        }
    }

    /**
     * 根据类型删除索引
     * @param typeCode
     */
    private static void deleteIndex(String typeCode) {
        // 删除
        try {
            SolrFactory.deleteIndexByTypeCodeSyn(typeCode);
        } catch (Throwable e) {
            throw new BaseRunTimeException(TipsMode.Message.toString(), "类型：" + typeCode + "索引删除失败！");
        }
    }

    /**
     * 创建索引
     * @param vos
     * @param msg
     */
    private static void createIndex(Set<SolrIndexVO> vos,String msg) {
        try {
            SolrFactory.createIndex(vos);
        } catch (SolrServerException e) {
            e.printStackTrace();
            throw new BaseRunTimeException(TipsMode.Message.toString(), msg + "：创建索引失败！");
        } catch (IOException e) {
            e.printStackTrace();
            throw new BaseRunTimeException(TipsMode.Message.toString(), msg + "：创建索引失败！");
        }

        logger.info(msg + "：创建索引线程已经启动成功，正在异步创建索引!");
    }

    /**
     * 格式化时间，匹配solr 时间格式，解决时区差异问题
     * @param date
     * @return
     */
    private static String getSolrDate(Date date) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
        format.setTimeZone(TimeZone.getTimeZone("UTC"));
        return format.format(date);
    }

    /**
     * 保存栏目配置时对之下的文章页索引处理
     *
     * @param columnVO
     * @param columnId
     */
    public static void handleIndex4ColumnIsShow(ColumnMgrEO columnVO, Long columnId) {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("siteId", columnVO.getSiteId());
        params.put("columnId", columnId);
        params.put("recordStatus", AMockEntity.RecordStatus.Normal.toString());
        //判断isshow 1则新建该栏目下所有文章页索引，0删除该栏目下所有文章页索引
        if (null != columnVO.getIsShow() && columnVO.getIsShow() == 0) {//前台不显示
            try {
                //删除该栏目下文章页的索引
                SolrFactory.deleteIndex(columnId);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (null != columnVO.getIsShow() && columnVO.getIsShow() == 1) {//前台显示
            List<BaseContentEO> list = baseContentService.getEntities(BaseContentEO.class, params);
            Set<SolrIndexVO> vos = new HashSet<SolrIndexVO>();
            for (BaseContentEO eo : list) {
                ContentMongoEO contentMongoEO = mongoService.queryById(eo.getId());
                SolrIndexVO vo = new SolrIndexVO();
                if (null != eo.getSiteId()) {
                    vo.setId(eo.getId() + "");
                    vo.setColumnId(eo.getColumnId());
                    vo.setRemark(eo.getRemarks());
                    vo.setTitle(eo.getTitle());
                    if (null != contentMongoEO) {
                        vo.setContent(contentMongoEO.getContent());
                    } else {
                        vo.setContent(HtmlUtil.getTextFromTHML(eo.getArticle()));
                    }
                    vo.setSiteId(eo.getSiteId());
                    vo.setTypeCode(eo.getTypeCode());
                    vo.setCreateDate(eo.getCreateDate());
                    if (null != eo.getRedirectLink()) {
                        vo.setUrl(eo.getRedirectLink());
                    }
                    vos.add(vo);
                }
            }
            //先删除再创建
            try {
                SolrFactory.deleteIndex(columnId);
                SolrFactory.createIndex(vos);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
