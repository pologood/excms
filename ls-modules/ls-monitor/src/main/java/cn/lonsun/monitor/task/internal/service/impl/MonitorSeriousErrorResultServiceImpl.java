package cn.lonsun.monitor.task.internal.service.impl;

import cn.lonsun.cache.client.CacheHandler;
import cn.lonsun.content.internal.dao.IBaseContentDao;
import cn.lonsun.content.internal.entity.BaseContentEO;
import cn.lonsun.core.base.service.impl.MockService;
import cn.lonsun.core.util.Pagination;
import cn.lonsun.core.util.SpringContextHolder;
import cn.lonsun.mongodb.entity.ContentMongoEO;
import cn.lonsun.mongodb.service.ContentMongoServiceImpl;
import cn.lonsun.monitor.task.internal.dao.IMonitorSeriousErrorResultDao;
import cn.lonsun.monitor.task.internal.entity.MonitorSeriousErrorResultEO;
import cn.lonsun.monitor.task.internal.entity.MonitorSiteStatisEO;
import cn.lonsun.monitor.task.internal.entity.MonitorTaskManageEO;
import cn.lonsun.monitor.task.internal.entity.vo.SeriousErrorQueryVO;
import cn.lonsun.monitor.task.internal.entity.vo.SeriousErrorStatisVO;
import cn.lonsun.monitor.task.internal.service.IMonitorCustomIndexManageService;
import cn.lonsun.monitor.task.internal.service.IMonitorSeriousErrorResultService;
import cn.lonsun.monitor.task.internal.service.IMonitorSiteStatisService;
import cn.lonsun.monitor.task.internal.service.IMonitorTaskManageService;
import cn.lonsun.monitor.task.util.WordsCheckUtils;
import cn.lonsun.site.site.internal.entity.SiteMgrEO;
import cn.lonsun.util.ConcurrentUtil;
import cn.lonsun.webservice.monitor.client.IMonitorSeriousErrorClient;
import cn.lonsun.webservice.monitor.client.vo.SeriousErrorVO;
import org.hibernate.SessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.task.TaskExecutor;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 任务指标管理服务类
 * @author gu.fei
 * @version 2017-09-28 9:24
 */
@Service
public class MonitorSeriousErrorResultServiceImpl extends MockService<MonitorSeriousErrorResultEO> implements IMonitorSeriousErrorResultService {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Resource
    private IBaseContentDao baseContentDao;

    @Resource
    private TaskExecutor taskExecutor;

    @Resource
    private IMonitorSeriousErrorResultDao monitorSeriousErrorResultDao;

    @Resource
    private IMonitorSiteStatisService monitorSiteStatisService;

    @Resource
    private IMonitorTaskManageService monitorTaskManageService;

    @Override
    public Pagination getSeriousErrorPage(SeriousErrorQueryVO vo) {
        return monitorSeriousErrorResultDao.getSeriousErrorPage(vo);
    }

    @Override
    public List<MonitorSeriousErrorResultEO> getSeriousErrorList(SeriousErrorQueryVO vo) {
        return monitorSeriousErrorResultDao.getSeriousErrorList(vo);
    }

    @Override
    public void runMonitor(final Long siteId, final Long taskId,final Long reportId) {
        logger.info(">>>>>>>>>>>>>>>>>>>>>>>严重错误开始检测");
        // 分页查询，每次5000;
        Long pageIndex = 0L;
        Integer pageSize = 1000;
        SiteMgrEO mgr = CacheHandler.getEntity(SiteMgrEO.class,siteId);
        // 先查询一次
        Pagination pagination = getArticleNews(pageIndex++, pageSize, siteId);
        if (null != pagination) {
            // 检测
            checkWords((List<BaseContentEO>) pagination.getData(), taskId, siteId,reportId,mgr.getUri());
            Long pageCount = pagination.getPageCount();
            while (pageIndex < pageCount) {// 继续分页查询
                pagination = getArticleNews(pageIndex++, pageSize, siteId);
                if (null != pagination) {
                    checkWords((List<BaseContentEO>) pagination.getData(), taskId, siteId,reportId,mgr.getUri());
                }
            }
        }
        if(null != reportId) {
            IMonitorTaskManageService taskManageService = SpringContextHolder.getBean(IMonitorTaskManageService.class);
            taskManageService.updateStatus(taskId,"errorStatus",2);
        } else {
            IMonitorCustomIndexManageService customIndexManageService = SpringContextHolder.getBean(IMonitorCustomIndexManageService.class);
            customIndexManageService.updateStatus(taskId,2);
        }
        logger.info(">>>>>>>>>>>>>>>>>>>>>>>>>严重错误检测完成，任务[{}]状态更新完成",taskId);
    }

    @Override
    public Long getCount(Long taskId) {
        return monitorSeriousErrorResultDao.getCount(taskId);
    }

    @Override
    public Long getCount(Long taskId, String checkType) {
        return monitorSeriousErrorResultDao.getCount(taskId,checkType);
    }

    @Override
    public SeriousErrorStatisVO loadSeriousErrorStatis(Long taskId, Long siteId) {
        if(null == taskId) {
            MonitorTaskManageEO task = monitorTaskManageService.getLatestTask(siteId,null);
            taskId = task.getId();
        }
        SeriousErrorStatisVO statis = new SeriousErrorStatisVO();
        Long lcount = this.getCount(taskId);
        boolean flag = true;
        if(null != lcount && lcount > 0) {
            statis.setIsOk(0);
            monitorSiteStatisService.updateError(siteId,taskId,0);
            statis.setCount(lcount);
        } else {
            statis.setIsOk(1);
            monitorSiteStatisService.updateError(siteId,taskId,1);
            statis.setCount(0L);
            flag = false;
        }
        MonitorTaskManageEO secondLatestTask = monitorTaskManageService.getLatestTask(siteId,taskId);
        if(null == secondLatestTask) {
            statis.setLinkRelationStatus(0); // 横杠
        } else {
            MonitorSiteStatisEO siteStatis = monitorSiteStatisService.getSiteStatis(siteId,secondLatestTask.getId());
            if(null == siteStatis) {
                statis.setLinkRelationStatus(0); // 横杠
            } else {
                if (flag) {
                    if (null == siteStatis.getError() || siteStatis.getError() == 0) {
                        statis.setLinkRelationStatus(1); // 斜杠 本期单项否决且上期单项否决
                    } else {
                        statis.setLinkRelationStatus(2); //向上箭头 本期单项否决且上期未单项否决
                    }
                } else {
                    if (null == siteStatis.getError() || siteStatis.getError() == 0) {
                        statis.setLinkRelationStatus(3); // 向下箭头 本期未单项否决且上期单项否决
                    } else {
                        statis.setLinkRelationStatus(4); // 本期且上期均未单项否决，计算y
                        Long scount = this.getCount(siteStatis.getId());
                        if (null == scount || scount == 0) {
                            statis.setLinkRelationRate(new Double(0));
                        } else {
                            double hb = (double) (lcount - scount) * 100 / scount;
                            statis.setLinkRelationRate(getDoubleValue(hb, 2));
                        }
                    }
                }
            }
        }

        return statis;
    }

    @Override
    public List<MonitorSeriousErrorResultEO> getMonitorSiteGroupErrors() {
        return monitorSeriousErrorResultDao.getMonitorSiteGroupErrors();
    }

    private void checkWords(final List<BaseContentEO> contents, final Long taskId, final Long siteId, final Long reportId, final String domain) {
        taskExecutor.execute(new Runnable() {
            @Override
            public void run() {
                SessionFactory sessionFactory = SpringContextHolder.getBean(SessionFactory.class);
                boolean participate = ConcurrentUtil.bindHibernateSessionToThread(sessionFactory);
                List<MonitorSeriousErrorResultEO> errors = new ArrayList<MonitorSeriousErrorResultEO>();
                if(null != contents && !contents.isEmpty()) {
                    for(BaseContentEO eo : contents) {
                        if(null != eo.getTitle()) {
                           WordsCheckUtils.wordsCheck(taskId,eo.getId(),eo.getColumnId(),siteId, eo.getTitle(),"标题",eo.getTitle(),"文章新闻",errors,domain);
                        }

                        //副标题检测
                        if(null != eo.getSubTitle()) {
                            WordsCheckUtils.wordsCheck(taskId,eo.getId(),eo.getColumnId(),siteId, eo.getSubTitle(),"副标题",eo.getTitle(),"文章新闻",errors,domain);
                        }

                        //摘要
                        if(null != eo.getRemarks()) {
                            WordsCheckUtils.wordsCheck(taskId,eo.getId(),eo.getColumnId(),siteId, eo.getRemarks(),"摘要",eo.getTitle(),"文章新闻",errors,domain);
                        }

                        //内容检测
                        if(null != eo.getArticle()) {
                            WordsCheckUtils.wordsCheck(taskId,eo.getId(),eo.getColumnId(),siteId, eo.getArticle(),"内容",eo.getTitle(),"文章新闻",errors,domain);
                        }
                    }
                }
                IMonitorSeriousErrorResultService serious = SpringContextHolder.getBean(IMonitorSeriousErrorResultService.class);
                serious.saveEntities(errors);

                if(null != reportId) {
                    IMonitorSeriousErrorClient seriousErrorClient = SpringContextHolder.getBean(IMonitorSeriousErrorClient.class);
                    //云端数据推送
                    if(null != errors && !errors.isEmpty()) {
                        List<SeriousErrorVO> seriousErrorArray = new ArrayList<SeriousErrorVO>();
                        for(MonitorSeriousErrorResultEO error : errors) {
                            SeriousErrorVO vo = new SeriousErrorVO();
                            vo.setReportId(reportId);
                            vo.setMonitorDate(error.getMonitorDate());
                            vo.setContentId(error.getContentId());
                            vo.setTitle(error.getTitle());
                            vo.setWord(error.getWord());
                            vo.setTypeCode(error.getTypeCode());
                            vo.setFromCode(error.getFromCode());
                            vo.setCheckType(error.getCheckType());
                            vo.setResult(error.getResult());
                            vo.setDomain(domain);
                            vo.setColumnId(error.getColumnId());
                            seriousErrorArray.add(vo);
                        }
                        try {
                            seriousErrorClient.saveSeriousError(seriousErrorArray);
                        } catch (Exception e) {
                            e.printStackTrace();
                            logger.error("推送{}严重错误检测数据到云端报错:{}",siteId,e.getMessage());
                        }
                    }
                }
                ConcurrentUtil.closeHibernateSessionFromThread(participate, sessionFactory);
            }
        });
    }

    /**
     * 获取新闻
     * @param pageIndex
     * @param pageSize
     * @param siteId
     * @return
     */
    private Pagination getArticleNews(Long pageIndex, Integer pageSize, Long siteId) {
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
        if(null != siteId) {
            sb.append(" and siteId = ?");
            values.add(siteId);
        }
        Pagination pagination = baseContentDao.getPagination(pageIndex, pageSize,sb.toString(),values.toArray(),BaseContentEO.class);
        if (null != pagination) {// 设置内容字段
            logger.info("获取文字新闻，总共数量:" + pagination.getTotal());
            List<BaseContentEO> data = (List<BaseContentEO>)pagination.getData();
            setMongData(data);
        }
        return pagination;
    }

    private void setMongData(List<BaseContentEO> data) {
        ContentMongoServiceImpl mongoService = SpringContextHolder.getBean(ContentMongoServiceImpl.class);
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
     * 获取保留小数位数的数据
     * @param v
     * @param i
     * @return
     */
    private Double getDoubleValue(double v,int i) {
        BigDecimal b = new BigDecimal(v);
        return b.setScale(i, BigDecimal.ROUND_HALF_UP).doubleValue();
    }
}
