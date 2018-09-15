package cn.lonsun.solrManage;

import cn.lonsun.activemq.MessageSender;
import cn.lonsun.common.util.AppUtil;
import cn.lonsun.content.ideacollect.internal.dao.ICollectInfoDao;
import cn.lonsun.content.ideacollect.vo.CollectInfoVO;
import cn.lonsun.content.internal.dao.IBaseContentDao;
import cn.lonsun.content.internal.dao.IGuestBookDao;
import cn.lonsun.content.internal.dao.IKnowledgeBaseDao;
import cn.lonsun.content.internal.entity.BaseContentEO;
import cn.lonsun.content.internal.service.IBaseContentService;
import cn.lonsun.content.interview.internal.dao.IInterviewInfoDao;
import cn.lonsun.content.interview.vo.InterviewInfoVO;
import cn.lonsun.content.leaderwin.internal.dao.ILeaderInfoDao;
import cn.lonsun.content.leaderwin.vo.LeaderInfoVO;
import cn.lonsun.content.messageBoard.dao.IMessageBoardDao;
import cn.lonsun.content.survey.internal.dao.ISurveyThemeDao;
import cn.lonsun.content.survey.vo.SurveyThemeVO;
import cn.lonsun.content.vo.ContentPageVO;
import cn.lonsun.content.vo.GuestBookSearchVO;
import cn.lonsun.content.vo.KnowledgeBaseVO;
import cn.lonsun.content.vo.MessageBoardSearchVO;
import cn.lonsun.core.base.entity.AMockEntity;
import cn.lonsun.core.exception.BaseRunTimeException;
import cn.lonsun.core.util.Pagination;
import cn.lonsun.core.util.SpringContextHolder;
import cn.lonsun.message.internal.entity.MessageSystemEO;
import cn.lonsun.mongodb.entity.ContentMongoEO;
import cn.lonsun.mongodb.service.ContentMongoServiceImpl;
import cn.lonsun.net.service.dao.IWorkGuideDao;
import cn.lonsun.net.service.entity.CmsWorkGuideEO;
import cn.lonsun.publicInfo.internal.dao.IPublicContentDao;
import cn.lonsun.publicInfo.internal.entity.PublicCatalogEO;
import cn.lonsun.publicInfo.internal.entity.PublicCatalogOrganRelEO;
import cn.lonsun.publicInfo.internal.entity.PublicClassEO;
import cn.lonsun.publicInfo.internal.entity.PublicContentEO;
import cn.lonsun.publicInfo.internal.service.IPublicCatalogService;
import cn.lonsun.publicInfo.internal.service.IPublicClassService;
import cn.lonsun.publicInfo.vo.PublicContentVO;
import cn.lonsun.rbac.internal.entity.OrganEO;
import cn.lonsun.rbac.internal.service.IOrganService;
import cn.lonsun.solr.SolrFactory;
import cn.lonsun.solr.vo.SolrIndexVO;
import cn.lonsun.util.HotWordsCheckUtil;
import cn.lonsun.util.HtmlUtil;
import cn.lonsun.util.LoginPersonUtil;
import cn.lonsun.util.PublicCatalogUtil;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * @author gu.fei
 * @version 2017-04-25 8:18
 */
public class IndexUtil {

    private static final Logger logger = LoggerFactory.getLogger(SolrFactory.class);

    private static IBaseContentService baseContentService = SpringContextHolder.getBean("baseContentService");
    private static IBaseContentDao baseContentDao = SpringContextHolder.getBean("baseContentDao");
    private static IPublicContentDao publicContentDao = SpringContextHolder.getBean("publicContentDao");
    private static IMessageBoardDao messageBoardDao = SpringContextHolder.getBean("messageBoardDao");
    private static IWorkGuideDao workGuideDao = SpringContextHolder.getBean("workGuideDao");
    private static IGuestBookDao guestBookDao = SpringContextHolder.getBean("guestBookDao");
    private static ISurveyThemeDao surveyThemeDao = SpringContextHolder.getBean(ISurveyThemeDao.class);
    private static ICollectInfoDao collectInfoDao = SpringContextHolder.getBean(ICollectInfoDao.class);
    private static ILeaderInfoDao leaderInfoDao = SpringContextHolder.getBean(ILeaderInfoDao.class);
    private static IKnowledgeBaseDao knowledgeBaseDao = SpringContextHolder.getBean(IKnowledgeBaseDao.class);
    private static IInterviewInfoDao interviewInfoDao = SpringContextHolder.getBean(IInterviewInfoDao.class);
    private static ContentMongoServiceImpl mongoService = SpringContextHolder.getBean(ContentMongoServiceImpl.class);
    private static IOrganService organService = SpringContextHolder.getBean(IOrganService.class);
    private static IPublicClassService publicClassService = SpringContextHolder.getBean(IPublicClassService.class);
    private static IPublicCatalogService publicCatalogService = SpringContextHolder.getBean(IPublicCatalogService.class);

    /**
     * 创建本站所有索引
     *
     * @param siteId
     */
    public static void createAllIndex(Long siteId) {
        createArticleNewsIndex(siteId); //文字新闻
        createPictureNewsIndex(siteId); //图片新闻
        createVideoNewsIndex(siteId); //视频新闻
        createPublicInfoIndex(siteId);//信息公开
        createWorkGuidesIndex(siteId); //网上办事
        createGuestsIndex(siteId); //留言
        createSurveysIndex(siteId); //调查管理
        createReviewsIndex(siteId); //网上调查
        createInterviewsIndex(siteId); //在线访谈
        createCollectInfosIndex(siteId); //民意征集
        createLeaderInfosIndex(siteId); //领导之窗
        createKnowledgeBaseIndex(siteId);//问答知识库
    }

    /**
     * 创建图片新闻索引
     */
    public static void createPictureNewsIndex(Long siteId) {
        // 删除
        if (deleteIndex(BaseContentEO.TypeCode.pictureNews.toString(), siteId)) {
            List<BaseContentEO> contents = null;
            if (null != siteId) {
                contents = baseContentDao.getList(new ContentPageVO(siteId, null, null, null, BaseContentEO.TypeCode.pictureNews.toString()));
            } else {
                contents = baseContentService.getContents(BaseContentEO.TypeCode.pictureNews.toString());
            }
            if (null != contents && contents.size() > 0) {
                logger.info("获取图片新闻条数=" + contents.size() + "条");
                setMongData(contents);
                createBaseContentIndex(contents);
            }
        } else {
            throw new BaseRunTimeException("删除图片新闻索引失败");
        }
    }

    /**
     * 创建视频新闻索引
     */
    public static void createVideoNewsIndex(Long siteId) {
        // 删除
        if (deleteIndex(BaseContentEO.TypeCode.videoNews.toString(), siteId)) {
            List<BaseContentEO> contents = null;
            if (null != siteId) {
                contents = baseContentDao.getList(new ContentPageVO(siteId, null, null, null, BaseContentEO.TypeCode.videoNews.toString()));
            } else {
                contents = baseContentService.getContents(BaseContentEO.TypeCode.videoNews.toString());
            }

            if (null != contents && contents.size() > 0) {
                logger.info("获取视频新闻条数=" + contents.size() + "条");
                createBaseContentIndex(contents);
            }
        } else {
            throw new BaseRunTimeException("删除视频新闻索引失败");
        }
    }

    /**
     * 创建网上办事索引
     */
    public static void createWorkGuidesIndex(Long siteId) {
        // 删除
        if (deleteIndex(BaseContentEO.TypeCode.workGuide.toString(), siteId)) {
            List<Object> values = new ArrayList<Object>();
            StringBuffer hql = new StringBuffer(" select t.id as id,t.contentId as contentId,t.name as name, t.linkType as linkType,");
            hql.append(" t.linkUrl as linkUrl,t.organId as organId,t.joinDate as joinDate,t.turnLink as turnLink,t.publish as publish,t.content as content, ");
            hql.append(" t.zxLink as zxLink ,t.tsLink as tsLink,t.sbLink as sbLink,t.setAccord as setAccord,t.applyCondition as applyCondition,t.handleData as handleData,b.columnId as columnId,");
            hql.append("t.handleProcess as handleProcess,t.handleAddress as handleAddress,t.handleDate as handleDate,t.phone as phone,t.siteId as siteId,b.publishDate as publishDate,t.createDate as createDate");
            hql.append(" from CmsWorkGuideEO t,BaseContentEO b where b.isPublish=1 and t.contentId = b.id and b.recordStatus='Normal'");
            if (null != siteId) {
                hql.append(" and t.siteId = ?");
                values.add(siteId);
            }
            List<CmsWorkGuideEO> workGuideEOs = (List<CmsWorkGuideEO>) workGuideDao.getBeansByHql(hql.toString(), values.toArray(), CmsWorkGuideEO.class);

            if (null != workGuideEOs && workGuideEOs.size() > 0) {
                logger.info("获取办事条数=" + workGuideEOs.size() + "条");
            }
            Set<SolrIndexVO> vos = new HashSet<SolrIndexVO>();
            if (null != workGuideEOs) {
                for (CmsWorkGuideEO eo : workGuideEOs) {
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
                    vo.setCreateDate(eo.getCreateDate());
                    if (null != eo.getLinkUrl()) {
                        vo.setUrl(eo.getLinkUrl());
                    }
                    vos.add(vo);
                }
            }

            SolrUtil.asynCreateIndex(vos);
        } else {
            throw new BaseRunTimeException("删除网上办事索引失败");
        }
    }

    /**
     * 创建留言索引
     */
    public static void createGuestsIndex(Long siteId) {
        // 删除
        if (deleteIndex(BaseContentEO.TypeCode.guestBook.toString(), siteId)) {
            Set<SolrIndexVO> vos = new HashSet<SolrIndexVO>();
            List<Object> values = new ArrayList<Object>();
            StringBuffer hql = new StringBuffer(" select g.id as id, b.id as baseContentId,b.title as title,b.publishDate as publishDate")
                    .append(",b.siteId as siteId,b.columnId as columnId,g.guestBookContent as guestBookContent,g.classCode as classCode")
                    .append(" from BaseContentEO b,GuestBookEO g where b.id=g.baseContentId and g.isPublic=1 and b.isPublish=1 ")
                    .append(" and b.recordStatus='Normal'");
            if (null != siteId) {
                hql.append(" and b.siteId = ?");
                values.add(siteId);
            }
            List<GuestBookSearchVO> guestBookSearchVOs = (List<GuestBookSearchVO>) guestBookDao.getBeansByHql(hql.toString(), values.toArray(), GuestBookSearchVO.class);

            if (null != guestBookSearchVOs && guestBookSearchVOs.size() > 0) {
                logger.info("获取留言条数=" + guestBookSearchVOs.size() + "条");
            }

            if (null != guestBookSearchVOs) {
                for (GuestBookSearchVO eo : guestBookSearchVOs) {
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
            SolrUtil.asynCreateIndex(vos);
        }
        if (deleteIndex(BaseContentEO.TypeCode.messageBoard.toString(), siteId)) {
            Set<SolrIndexVO> vos = new HashSet<SolrIndexVO>();
            List<Object> values = new ArrayList<Object>();
            StringBuffer hql = new StringBuffer(" select g.id as id, b.id as baseContentId,b.title as title,g.addDate as publishDate")
                    .append(",b.siteId as siteId,b.columnId as columnId,g.messageBoardContent as messageBoardContent,g.classCode as classCode")
                    .append(" from BaseContentEO b,MessageBoardEO g where b.id=g.baseContentId and b.isPublish =1 and g.isPublic=1")
                    .append(" and b.recordStatus='" + AMockEntity.RecordStatus.Normal.toString() + "'");
            if (null != siteId) {
                hql.append(" and b.siteId = ?");
                values.add(siteId);
            }
            List<MessageBoardSearchVO> messageBoardSearchVOs = (List<MessageBoardSearchVO>) messageBoardDao.getBeansByHql(hql.toString(), values.toArray(), MessageBoardSearchVO.class);
            if (null != messageBoardSearchVOs && messageBoardSearchVOs.size() > 0) {
                logger.info("获取新版留言条数=" + messageBoardSearchVOs.size() + "条");
            }

            if (null != messageBoardSearchVOs) {
                for (MessageBoardSearchVO eo : messageBoardSearchVOs) {
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
            SolrUtil.asynCreateIndex(vos);
        } else {
            throw new BaseRunTimeException("删除留言索引失败");
        }
    }

    /**
     * 创建调查管理索引
     */
    public static void createSurveysIndex(Long siteId) {
        if (deleteIndex(BaseContentEO.TypeCode.survey.toString(), siteId)) {
            List<Object> values = new ArrayList<Object>();
            StringBuffer hql = new StringBuffer("select b.id as contentId,b.title as title,b.columnId as columnId,b.siteId as siteId,")
                    .append("b.num as sortNum,b.isPublish as isPublish,b.publishDate as issuedTime,")
                    .append("s.themeId as themeId,s.options as options,s.ipLimit as ipLimit,s.ipDayCount as ipDayCount,s.isVisible as isVisible,s.startTime as startTime,")
                    .append("s.endTime as endTime,s.content as content,s.isLink as isLink,s.linkUrl as linkUrl,s.typeIds as typeIds,s.objectIds as objectIds")
                    .append(" from BaseContentEO b,SurveyThemeEO s where b.id = s.contentId and b.recordStatus= ? and b.typeCode = ? and b.isPublish = 1");
            values.add(AMockEntity.RecordStatus.Normal.toString());
            values.add(BaseContentEO.TypeCode.survey.toString());

            if (null != siteId) {
                hql.append(" and b.siteId = ?");
                values.add(siteId);
            }
            //调查管
            List<SurveyThemeVO> surveys = (List<SurveyThemeVO>) surveyThemeDao.getBeansByHql(hql.toString(), values.toArray(), SurveyThemeVO.class);

            if (null != surveys && surveys.size() > 0) {
                logger.info("获取调查管理条数=" + surveys.size() + "条");
                Set<SolrIndexVO> vos = new HashSet<SolrIndexVO>();
                for (SurveyThemeVO eo : surveys) {
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
                SolrUtil.asynCreateIndex(vos);
            }
        } else {
            throw new BaseRunTimeException("删除调查管理索引失败");
        }
    }

    /**
     * 创建网上调查索引
     */
    public static void createReviewsIndex(Long siteId) {
        if (deleteIndex(BaseContentEO.TypeCode.reviewInfo.toString(), siteId)) {
            List<Object> values = new ArrayList<Object>();
            StringBuffer hql = new StringBuffer("select b.id as contentId,b.title as title,b.columnId as columnId,b.siteId as siteId,")
                    .append("b.num as sortNum,b.isPublish as isPublish,b.publishDate as issuedTime,")
                    .append("s.themeId as themeId,s.options as options,s.ipLimit as ipLimit,s.ipDayCount as ipDayCount,s.isVisible as isVisible,s.startTime as startTime,")
                    .append("s.endTime as endTime,s.content as content,s.isLink as isLink,s.linkUrl as linkUrl,s.typeIds as typeIds,s.objectIds as objectIds")
                    .append(" from BaseContentEO b,SurveyThemeEO s where b.id = s.contentId and b.recordStatus= ? and b.typeCode = ? and b.isPublish = 1");
            values.add(AMockEntity.RecordStatus.Normal.toString());
            values.add(BaseContentEO.TypeCode.reviewInfo.toString());

            if (null != siteId) {
                hql.append(" and b.siteId = ?");
                values.add(siteId);
            }
            //网上调查
            List<SurveyThemeVO> reviews = (List<SurveyThemeVO>) surveyThemeDao.getBeansByHql(hql.toString(), values.toArray(), SurveyThemeVO.class);

            if (null != reviews && reviews.size() > 0) {
                logger.info("获取网上调查条数=" + reviews.size() + "条");
                Set<SolrIndexVO> vos = new HashSet<SolrIndexVO>();
                if (null != reviews && reviews.size() > 0) {
                    for (SurveyThemeVO eo : reviews) {
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
                SolrUtil.asynCreateIndex(vos);
            }
        } else {
            throw new BaseRunTimeException("删除网上调查索引失败");
        }
    }

    /**
     * 创建在线访谈索引
     */
    public static void createInterviewsIndex(Long siteId) {
        if (deleteIndex(BaseContentEO.TypeCode.interviewInfo.toString(), siteId)) {
            List<Object> values = new ArrayList<Object>();
            StringBuffer hql = new StringBuffer("select b.id as contentId,b.title as title,b.columnId as columnId,b.siteId as siteId,"
                    + "b.num as sortNum,b.isPublish as issued,b.publishDate as issuedTime,b.imageLink as picUrl,"
                    + "s.interviewId as interviewId,s.presenter as presenter,s.userNames as userNames,s.time as time,s.liveLink as liveLink,"
                    + "s.outLink as outLink,s.summary as summary,s.content as content,s.desc as desc,s.isOpen as isOpen,s.openTime as openTime,s.startTime as startTime,s.endTime as endTime,s.type as type"
                    + " from BaseContentEO b,InterviewInfoEO s where b.id = s.contentId and b.recordStatus= ? and b.typeCode = ? and b.isPublish = 1");
            values.add(AMockEntity.RecordStatus.Normal.toString());
            values.add(BaseContentEO.TypeCode.interviewInfo.toString());
            if (null != siteId) {
                hql.append(" and b.siteId = ?");
                values.add(siteId);
            }
            //在线访谈
            List<InterviewInfoVO> interviews = (List<InterviewInfoVO>) interviewInfoDao.getBeansByHql(hql.toString(), values.toArray(), InterviewInfoVO.class);
            if (null != interviews && interviews.size() > 0) {
                logger.info("获取在线访谈条数=" + interviews.size() + "条");
                Set<SolrIndexVO> vos = new HashSet<SolrIndexVO>();
                if (null != interviews && interviews.size() > 0) {
                    for (InterviewInfoVO eo : interviews) {
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
                SolrUtil.asynCreateIndex(vos);
            }
        } else {
            throw new BaseRunTimeException("删除在线访谈索引失败");
        }
    }

    /**
     * 创建民意征集索引
     */
    public static void createCollectInfosIndex(Long siteId) {
        if (deleteIndex(BaseContentEO.TypeCode.collectInfo.toString(), siteId)) {
            List<Object> values = new ArrayList<Object>();
            StringBuffer hql = new StringBuffer("select b.id as contentId,b.title as title,b.columnId as columnId,b.siteId as siteId,"
                    + "b.num as sortNum,b.isPublish as isIssued,b.publishDate as issuedTime,b.imageLink as picUrl,"
                    + "s.collectInfoId as collectInfoId,s.startTime as startTime,s.endTime as endTime,s.content as content,s.desc as desc,s.ideaCount as ideaCount"
                    + " from BaseContentEO b,CollectInfoEO s where b.id = s.contentId and b.recordStatus= ? and b.typeCode = ? and b.isPublish = 1");
            values.add(AMockEntity.RecordStatus.Normal.toString());
            values.add(BaseContentEO.TypeCode.collectInfo.toString());
            if (null != siteId) {
                hql.append(" and b.siteId = ?");
                values.add(siteId);
            }
            //民意征集
            List<CollectInfoVO> collectInfos = (List<CollectInfoVO>) collectInfoDao.getBeansByHql(hql.toString(), values.toArray(), CollectInfoVO.class);
            if (null != collectInfos && collectInfos.size() > 0) {
                logger.info("获取民意征集条数=" + collectInfos.size() + "条");
                Set<SolrIndexVO> vos = new HashSet<SolrIndexVO>();
                if (null != collectInfos && collectInfos.size() > 0) {
                    for (CollectInfoVO eo : collectInfos) {
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
                SolrUtil.asynCreateIndex(vos);
            }
        } else {
            throw new BaseRunTimeException("删除民意征集索引失败");
        }
    }

    /**
     * 创建领导之窗索引
     */
    public static void createLeaderInfosIndex(Long siteId) {
        if (deleteIndex(BaseContentEO.TypeCode.leaderInfo.toString(), siteId)) {
            List<Object> values = new ArrayList<Object>();
            StringBuffer hql = new StringBuffer("select b.id as contentId,b.title as name,b.columnId as columnId,b.siteId as siteId,"
                    + "b.num as sortNum,b.isPublish as issued,b.publishDate as issuedTime,b.imageLink as picUrl,"
                    + "s.leaderInfoId as leaderInfoId,s.leaderTypeId as leaderTypeId,s.positions as positions,s.work as work,s.jobResume as jobResume"
                    + " from BaseContentEO b,LeaderInfoEO s where b.id = s.contentId and b.recordStatus= ?  and b.typeCode = ? and b.isPublish = 1");
            values.add(AMockEntity.RecordStatus.Normal.toString());
            values.add(BaseContentEO.TypeCode.leaderInfo.toString());
            if (null != siteId) {
                hql.append(" and b.siteId = ?");
                values.add(siteId);
            }
            //领导之窗
            List<LeaderInfoVO> leaderInfos = (List<LeaderInfoVO>) leaderInfoDao.getBeansByHql(hql.toString(), values.toArray(), LeaderInfoVO.class);
            if (null != leaderInfos && leaderInfos.size() > 0) {
                logger.info("获取领导之窗条数=" + leaderInfos.size() + "条");
                Set<SolrIndexVO> vos = new HashSet<SolrIndexVO>();
                if (null != leaderInfos && leaderInfos.size() > 0) {
                    for (LeaderInfoVO eo : leaderInfos) {
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
                SolrUtil.asynCreateIndex(vos);
            }
        } else {
            throw new BaseRunTimeException("删除领导之窗索引失败");
        }
    }

    /**
     * 文章新闻索引创建
     */
    public static void createArticleNewsIndex(Long siteId) {
        // 删除
        if (deleteIndex(BaseContentEO.TypeCode.articleNews.toString(), siteId)) {
            // 分页查询建索引，每次5000;
            Long pageIndex = 0L;
            Integer pageSize = 10000;
            // 先查询一次
            Pagination pagination = createArticleNewsIndex(pageIndex++, pageSize, siteId);
            if (null != pagination) {
                // 创建索引
                createBaseContentIndex((List<BaseContentEO>) pagination.getData());
                Long pageCount = pagination.getPageCount();
                while (pageIndex < pageCount) {// 继续分页查询
                    pagination = createArticleNewsIndex(pageIndex++, pageSize, siteId);
                    if (null != pagination) {
                        createBaseContentIndex((List<BaseContentEO>) pagination.getData());
                    }
                }
            }
        } else {
            throw new BaseRunTimeException("删除新闻索引失败");
        }
    }

    private static Pagination createArticleNewsIndex(Long pageIndex, Integer pageSize, Long siteId) {
        List<Object> values = new ArrayList<Object>();
        StringBuilder sb = new StringBuilder("select id as id,");
        sb.append(" title as title,");
        sb.append(" columnId as columnId,");
        sb.append(" siteId as siteId,");
        sb.append(" author as author,");
        sb.append(" typeCode as typeCode,");
        sb.append(" remarks as remarks,");
        sb.append(" createDate as createDate");
        sb.append(" from BaseContentEO where recordStatus='Normal' and typeCode=? and isPublish = 1");
        values.add(BaseContentEO.TypeCode.articleNews.toString());
        if (null != siteId) {
            sb.append(" and siteId = ?");
            values.add(siteId);
        }
        Pagination pagination = baseContentDao.getPagination(pageIndex, pageSize, sb.toString(), values.toArray(), BaseContentEO.class);
        logger.info("创建文字新闻索引，总共数量:" + pagination.getTotal());
        logger.info("创建文字新闻索引，已完成数量:" + (pageIndex * pageSize + pagination.getData().size()));
        logger.info("创建文字新闻索引，页数：" + pagination.getPageCount());
        logger.info("创建文字新闻索引，已完成页数：" + (pageIndex + 1));
        if (null != pagination) {// 设置内容字段
            List<BaseContentEO> data = (List<BaseContentEO>) pagination.getData();
            setMongData(data);
        }
        return pagination;
    }

    /**
     * 问答知识库索引创建
     */
    public static void createKnowledgeBaseIndex(Long siteId) {
        // 删除
        if (deleteIndex(BaseContentEO.TypeCode.knowledgeBase.toString(), siteId)) {
            // 分页查询建索引，每次5000;
            Long pageIndex = 0L;
            Integer pageSize = 99;
            ContentPageVO query = new ContentPageVO();
            if (!AppUtil.isEmpty(siteId)) {
                query.setSiteId(siteId);
            }
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
        } else {
            throw new BaseRunTimeException("删除新闻索引失败");
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
        SolrUtil.asynCreateIndex(vos);
    }

    /**
     * 信息公开索引重建
     */
    public static void createPublicInfoIndex(Long siteId) {
        if (SolrUtil.deleteIndexByTypeCode(BaseContentEO.TypeCode.public_content.toString(), siteId)) {
            // 分页查询建索引，每次5000;
            Long pageIndex = 0L;
            Integer pageSize = 5000;
            // 先查询一次
            Pagination pagination = createPublicIndex(pageIndex++, pageSize, siteId);
            if (null != pagination) {
                // 创建索引
                createPublicIndex(pagination.getData());
                Long pageCount = pagination.getPageCount();
                while (pageIndex < pageCount) {// 继续分页查询
                    pagination = createPublicIndex(pageIndex++, pageSize, siteId);
                    if (null != pagination) {
                        createPublicIndex(pagination.getData());
                    }
                }
            }
        } else {
            throw new BaseRunTimeException("删除信息公开索引失败");
        }
    }

    private static Pagination createPublicIndex(Long pageIndex, Integer pageSize, Long siteId) {
        List<Object> values = new ArrayList<Object>();
        StringBuffer hql = new StringBuffer();
        hql.append(getPublicQueryHql());
        hql.append(" and p.type = '").append(PublicContentEO.Type.DRIVING_PUBLIC.toString());

        hql.append("' and p.recordStatus = 'Normal' and b.recordStatus = 'Normal'  and b.isPublish = 1");
        if (null != siteId) {
            hql.append(" and b.siteId = ?");
            values.add(siteId);
        }
        // 放入参数对象
        Pagination pagination = publicContentDao.getPagination(pageIndex, pageSize, hql.toString(), values.toArray(), PublicContentVO.class);
        if (null != pagination) {// 设置内容字段
            List<?> data = pagination.getData();
            if (null != data && !data.isEmpty()) {
                setValueToContent(data.toArray(new PublicContentVO[]{}));
            }
        }
        return pagination;
    }

    /**
     * 获取信息公开查询hql
     *
     * @return
     * @author fangtinghua
     */
    private static String getPublicQueryHql() {
        StringBuffer hql = new StringBuffer();
        hql.append("select b.title as title,b.createDate as createDate,b.publishDate as publishDate,b.siteId as siteId,b.isPublish as isPublish,p.catId as catId,b.author as author,b.isTop as isTop,");
        hql.append("p.sortNum as sortNum,p.parentClassIds as parentClassIds,p.classIds as classIds,b.isPublish as isPublish,p.effectiveDate as effectiveDate,p.repealDate as repealDate,");
        hql.append("p.contentId as contentId,p.synColumnIds as synColumnIds,p.synOrganCatIds as synOrganCatIds,p.synMsgCatIds as synMsgCatIds,p.organId as organId,b.resources as resources,");
        hql.append("p.classIds as classIds,p.indexNum as indexNum,p.fileNum as fileNum,p.keyWords as keyWords,p.summarize as summarize,p.id as id,p.type as type,");
        hql.append("b.attachSavedName as attachSavedName,b.attachRealName as attachRealName,b.attachSize as attachSize ");
        hql.append(" from PublicContentEO as p,BaseContentEO as b where p.contentId = b.id");
        return hql.toString();
    }

    private static void createPublicIndex(List<?> data) {
        if (null == data || data.isEmpty()) {
            return;
        }
        Set<SolrIndexVO> voList = new HashSet<SolrIndexVO>();
        for (Object o : data) {
            SolrIndexVO vo = new SolrIndexVO();
            PublicContentVO eo = (PublicContentVO) o;
            vo.setId(eo.getContentId() + "");
            vo.setTitle(eo.getTitle());
            vo.setColumnId(eo.getOrganId());
            vo.setContent(eo.getContent());
            vo.setSiteId(eo.getSiteId());
            vo.setFileNum(eo.getFileNum());
            vo.setIndexNum(eo.getIndexNum());
            vo.setTypeCode(BaseContentEO.TypeCode.public_content.toString());
            vo.setCreateDate(eo.getPublishDate());
            vo.setAuthor(eo.getAuthor());
            voList.add(vo);
        }
        SolrUtil.asynCreateIndex(voList);
    }

    /**
     * 设置内容值
     *
     * @param voArray
     * @author fangtinghua
     */
    private static void setValueToContent(PublicContentVO... voArray) {
        if (null == voArray || voArray.length == 0) {
            return;
        }
        Set<Long> contentIdSet = new HashSet<Long>();
        Set<Long> classSet = new HashSet<Long>();
        Set<Long> catIdSet = new HashSet<Long>();
        Set<Long> organIdSet = new HashSet<Long>();
        for (PublicContentVO vo : voArray) {// 只有主动公开的时候才需要分类列表、目录
            if (null == vo) {// 为空
                continue;
            }
            contentIdSet.add(vo.getContentId());
            if (PublicContentEO.Type.DRIVING_PUBLIC.toString().equals(vo.getType())) {
                String classIds = vo.getClassIds();
                if (StringUtils.isNotEmpty(classIds)) {
                    classSet.addAll(cn.lonsun.core.base.util.StringUtils.getListWithLong(classIds, ","));
                }
                Long catId = vo.getCatId();
                if (null != catId) {
                    catIdSet.add(catId);
                }
                Long organId = vo.getOrganId();
                if (null != organId) {
                    organIdSet.add(organId);
                }
            }
        }
        // 查询mongodb内容
        Map<Long, String> contentIdMap = new HashMap<Long, String>();
        if (!contentIdSet.isEmpty()) {
            List<ContentMongoEO> contentList = mongoService.queryListByIds(contentIdSet.toArray(new Long[]{}));
            if (null != contentList && !contentList.isEmpty()) {
                for (ContentMongoEO mongo : contentList) {
                    if (null != mongo) {
                        contentIdMap.put(mongo.getId(), mongo.getContent());
                    }
                }
            }
        }
        // 查询所属分类
        Map<Long, PublicClassEO> classIdMap = new HashMap<Long, PublicClassEO>();
        if (!classSet.isEmpty()) {
            List<PublicClassEO> classList = publicClassService.getEntities(PublicClassEO.class, classSet.toArray(new Long[]{}));
            if (null != classList && !classList.isEmpty()) {
                for (PublicClassEO clazz : classList) {
                    if (null != clazz) {
                        classIdMap.put(clazz.getId(), clazz);
                    }
                }
            }
        }
        // 设置目录名称
        Map<Long, PublicCatalogEO> catalogMap = new HashMap<Long, PublicCatalogEO>();
        if (!catIdSet.isEmpty()) {
            List<PublicCatalogEO> catalogList = publicCatalogService.getEntities(PublicCatalogEO.class, catIdSet.toArray(new Long[]{}));
            if (null != catalogList && !catalogList.isEmpty()) {
                for (PublicCatalogEO catalog : catalogList) {
                    if (null != catalog) {
                        catalogMap.put(catalog.getId(), catalog);
                    }
                }
            }
        }
        // 设置单位名称
        Map<Long, OrganEO> organMap = new HashMap<Long, OrganEO>();
        if (!organIdSet.isEmpty()) {
            List<OrganEO> organList = organService.getEntities(OrganEO.class, organIdSet.toArray(new Long[]{}));
            if (null != organList && !organList.isEmpty()) {
                for (OrganEO organ : organList) {
                    if (null != organ) {
                        organMap.put(organ.getOrganId(), organ);
                    }
                }
            }
        }
        // 单位目录对应关系缓存
        Map<Long, Map<Long, PublicCatalogOrganRelEO>> cacheMap = new HashMap<Long, Map<Long, PublicCatalogOrganRelEO>>();
        // 设置值
        for (PublicContentVO vo : voArray) {
            if (null == vo) {
                continue;
            }
            // 设置内容
            vo.setContent(HotWordsCheckUtil.revertAll(contentIdMap.get(vo.getContentId())));
            // 设置组配分类、目录、单位名称
            if (PublicContentEO.Type.DRIVING_PUBLIC.toString().equals(vo.getType())) {
                String classIds = vo.getClassIds();
                if (StringUtils.isNotEmpty(classIds)) {
                    Long[] classIdArray = cn.lonsun.core.base.util.StringUtils.getArrayWithLong(classIds, ",");
                    List<String> classNameList = new ArrayList<String>();
                    for (Long classId : classIdArray) {
                        if (classIdMap.containsKey(classId)) {
                            classNameList.add(classIdMap.get(classId).getName());
                        }
                    }
                    vo.setClassNames(StringUtils.join(classNameList, ","));
                }
                Long organId = vo.getOrganId();
                if (null != organId && organMap.containsKey(organId)) {
                    vo.setOrganName(organMap.get(organId).getName());
                    Long catId = vo.getCatId();
                    if (null != catId && catalogMap.containsKey(catId)) {
                        Map<Long, PublicCatalogOrganRelEO> relMap = null;
                        if (cacheMap.containsKey(organId)) {
                            relMap = cacheMap.get(organId);
                        } else {
                            relMap = PublicCatalogUtil.getCatalogRelMap(organId);
                            cacheMap.put(organId, relMap);
                        }
                        if (null != relMap && relMap.containsKey(catId)) {// 存在关系
                            vo.setCatName(relMap.get(catId).getName());
                        } else {
                            vo.setCatName(catalogMap.get(catId).getName());
                        }
                    }
                }
            }
        }
    }

    private static void createBaseContentIndex(List<BaseContentEO> contents) {
        Set<SolrIndexVO> vos = new HashSet<SolrIndexVO>();
        for (BaseContentEO eo : contents) {
            SolrIndexVO vo = new SolrIndexVO();
            if (null != eo.getSiteId()) {
                vo.setId(eo.getId() + "");
                vo.setColumnId(eo.getColumnId());
                vo.setTitle(eo.getTitle());
                vo.setRemark(eo.getRemarks());
                vo.setContent(eo.getArticle());
                vo.setSiteId(eo.getSiteId());
                vo.setTypeCode(eo.getTypeCode());
                vo.setCreateDate(eo.getCreateDate());
                vo.setAuthor(eo.getAuthor());
                vos.add(vo);
            }
        }
        SolrUtil.asynCreateIndex(vos);
    }

    /**
     * 根据类型删除索引
     *
     * @param typeCode
     */
    private static boolean deleteIndex(String typeCode, Long siteId) {
        return SolrUtil.deleteIndexByTypeCode(typeCode, siteId);
    }

    private static void setMongData(List<BaseContentEO> data) {
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

    /**
     * 索引信息
     *
     * @param title
     * @param content
     */
    private static void sendSolrSuccessMessage(String title, String content) {
        MessageSystemEO eo = new MessageSystemEO();
        eo.setTitle(title);
        eo.setModeCode("solr");
        eo.setRecUserIds(LoginPersonUtil.getUserId() + "");
        eo.setContent(content);
        eo.setMessageStatus(MessageSystemEO.MessageStatus.success.toString());
        MessageSender.sendMessage(eo);
    }
}
