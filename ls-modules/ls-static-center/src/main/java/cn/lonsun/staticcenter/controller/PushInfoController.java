package cn.lonsun.staticcenter.controller;

import cn.lonsun.base.anno.DbInject;
import cn.lonsun.common.util.AppUtil;
import cn.lonsun.content.internal.dao.IBaseContentDao;
import cn.lonsun.content.internal.entity.BaseContentEO;
import cn.lonsun.core.base.controller.BaseController;
import cn.lonsun.core.base.entity.AMockEntity;
import cn.lonsun.core.base.util.StringUtils;
import cn.lonsun.core.util.Pagination;
import cn.lonsun.datacollect.dao.IHtmlCollectDataDao;
import cn.lonsun.datacollect.entity.HtmlCollectDataEO;
import cn.lonsun.datacollect.entity.HtmlCollectTaskEO;
import cn.lonsun.datacollect.service.IHtmlCollectTaskService;
import cn.lonsun.mongodb.entity.ContentMongoEO;
import cn.lonsun.mongodb.service.ContentMongoServiceImpl;
import cn.lonsun.pushinfo.entity.PushInfoEO;
import cn.lonsun.pushinfo.service.IPushInfoService;
import cn.lonsun.staticcenter.eo.CollectVO;
import cn.lonsun.util.ColumnUtil;
import cn.lonsun.util.ModelConfigUtil;
import org.apache.commons.httpclient.util.DateParseException;
import org.apache.commons.httpclient.util.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import java.util.*;

/**
 * @author gu.fei
 * @version 2016-09-20 8:30
 */
@Controller
@RequestMapping(value = "pushinfo")
public class PushInfoController extends BaseController {

    @DbInject("baseContent")
    private IBaseContentDao baseContentDao;

    @Autowired
    private IPushInfoService pushInfoService;

    @Resource
    private ContentMongoServiceImpl mongoService;

    @Resource
    private IHtmlCollectTaskService htmlCollectTaskService;

    @Resource
    private IHtmlCollectDataDao htmlCollectDataDao;

    @ResponseBody
    @RequestMapping(value = "{path}.json")
    public Object pushJsonInfo(@PathVariable String path,Long pageIndex,Integer pageSize,Integer isPage) {
        isPage = (null == isPage)?0:isPage;
        PushInfoEO eo = pushInfoService.getByPath(path);
        Pagination page = null;
        if(null != eo && eo.getIfActive() == 1) {
            page = getContentPage(eo,null == pageIndex?0L:pageIndex, null == pageSize?eo.getPageSize():pageSize);
        } else {
            return ajaxErr("地址不存在或者而没有开放");
        }
        return getObject((isPage == 1)?page:(null != page?page.getData():""));
    }

    @ResponseBody
    @RequestMapping(value = "/collect/{path}.json")
    public Object collectList(@PathVariable String path,Long pageIndex,Integer pageSize,Integer isPage,String dateFormat) {
        HtmlCollectTaskEO task = getCollectTask(path);
        if(null == task) {
            return ajaxErr("数据不存在或者没有开放数据推送");
        }
        Pagination page = getCollectData(task,pageIndex,pageSize);
        List<CollectVO> voList = new ArrayList<CollectVO>();
        List datas = page.getData();
        for(Object obj : datas) {
            if(null != obj) {
                HtmlCollectDataEO data = (HtmlCollectDataEO) obj;
                CollectVO vo = new CollectVO();
                vo.setTitle(data.getTitle());
                vo.setLink(data.getUrl());
                vo.setPubDate(null);
                if(null != data.getPublishDate()) {
                    dateFormat = (null == dateFormat)?"yyyy-MM-dd":dateFormat;
                    try {
                        List<String> formats = new ArrayList<String>();
                        formats.add("yyyy-MM-dd");
                        formats.add("MM-dd");
                        formats.add("yyyy-MM-dd HH:mm");
                        Date date = DateUtil.parseDate(data.getPublishDate(),formats);
                        if(null != date) {
                            vo.setPubDate(DateUtil.formatDate(date,dateFormat));
                        }
                    } catch (DateParseException e) {
                        e.printStackTrace();
                    }
                } else {
                    //发布时间为空默认当前时间
                    vo.setPubDate(DateUtil.formatDate(new Date(),dateFormat));
                }
                voList.add(vo);
            }
        }
        if(null != isPage && isPage == 1) {
            page.setData(voList);
            return page;
        } else {
            Map<String,Object> returnMap = new HashMap<String, Object>();
            if(null != task.getDefaultUrl()) {
                returnMap.put("MoreLink",task.getDefaultUrl());
            } else if(null != task.getRegexUrl()) {
                String pageType = task.getPageType();
                String chars = null;
                if(pageType.equals(HtmlCollectTaskEO.pageType.number.toString())) {
                    chars = null != task.getPageBeginNumber()?String.valueOf(task.getPageBeginNumber()) : "0";
                } else if(pageType.equals(HtmlCollectTaskEO.pageType.character.toString())) {
                    chars = task.getPageBeginChar();
                } else if(pageType.equals(HtmlCollectTaskEO.pageType.auto.toString())) {
                    String pageList = task.getPageList();
                    //自定义采集页面
                    if(null != pageList) {
                        List<String> urls = StringUtils.getListWithString(pageList,",");
                        if(null != urls && !urls.isEmpty()) {
                            chars = urls.get(0);
                        }
                    }
                }
                if(null != chars) {
                    returnMap.put("MoreLink",task.getRegexUrl().replace("[*]",chars));
                } else {
                    returnMap.put("MoreLink",task.getWebDomain());
                }
            }
            returnMap.put("Lists",voList);
            return returnMap;
        }
    }

    @RequestMapping(value = "{path}.html")
    public Object pushHtmlInfo(@PathVariable String path,ModelMap map,Long pageIndex,Integer pageSize) {
        PushInfoEO eo = pushInfoService.getByPath(path);
        Pagination page = null;
        if(null != eo && eo.getIfActive() == 1) {
            page = getContentPage(eo,null == pageIndex?0L:pageIndex, null == pageSize?eo.getPageSize():pageSize);
        } else {
            map.put("msg","接口没有开放或者没有该接口");
        }
        map.put("page",page);
        return "/pushinfo/pushinfo";
    }

    /**
     * 分页获取内容
     * @param eo
     * @param pageIndex
     * @param pageSize
     * @return
     */
    private Pagination getContentPage(PushInfoEO eo,Long pageIndex,Integer pageSize) {
        StringBuffer hql = new StringBuffer();
        hql.append("from BaseContentEO c where c.siteId = :siteId and c.isPublish = :isPublish and c.columnId = :columnId");
        hql.append(" and c.recordStatus = :recordStatus ").
                append(ModelConfigUtil.getOrderByHql(eo.getColumnId(),eo.getSiteId(), BaseContentEO.TypeCode.articleNews.toString()));
        Map<String, Object> paramMap = new HashMap<String, Object>();
        paramMap.put("siteId", eo.getSiteId());
        paramMap.put("isPublish", 1);// 发布
        paramMap.put("columnId", eo.getColumnId());
        paramMap.put("recordStatus", AMockEntity.RecordStatus.Normal.toString());
        Pagination page = baseContentDao.getPagination(null == pageIndex?0L:pageIndex, null == pageSize?eo.getPageSize():pageSize, hql.toString(), paramMap);
        if(null != page) {
            List<BaseContentEO> resultList = (List<BaseContentEO>)page.getData();
            setContentUri(resultList);
            page.setData(resultList);
        }
        return page;
    }

    /**
     * 根据地址获取采集任务
     * @param path
     * @return
     */
    private HtmlCollectTaskEO getCollectTask(String path) {
        Map<String,Object> params = new HashMap<String, Object>();
        params.put("recordStatus",AMockEntity.RecordStatus.Normal.toString());
        params.put("pushUrlFlag",path);
        params.put("isPush",1);
        List<HtmlCollectTaskEO> list = htmlCollectTaskService.getEntities(HtmlCollectTaskEO.class,params);
        return (null != list && !list.isEmpty())?list.get(0):null;
    }

    /**
     * 分页获取采集内容
     * @param task
     * @return
     */
    private Pagination getCollectData(HtmlCollectTaskEO task,Long pageIndex,Integer pageSize) {
        StringBuffer hql = new StringBuffer();
        hql.append("from HtmlCollectDataEO c where taskId = :taskId");
        hql.append(" and c.recordStatus = :recordStatus order by publishDate desc,id desc");
        Map<String, Object> paramMap = new HashMap<String, Object>();
        paramMap.put("recordStatus", AMockEntity.RecordStatus.Normal.toString());
        paramMap.put("taskId", task.getId());
        Pagination page = htmlCollectDataDao.getPagination(null == pageIndex?0L:pageIndex, null == pageSize?10:pageSize, hql.toString(), paramMap);
        return page;
    }

    /**
     * 设置连接地址
     * @param list
     */
    private void setContentUri(List<BaseContentEO> list) {
        // 处理查询结果
        if (null != list && !list.isEmpty()) {
            for (BaseContentEO ceo : list) {
                String link = "";
                if (!AppUtil.isEmpty(ceo.getRedirectLink())) {
                    link = ceo.getRedirectLink();
                } else {
                    link = ColumnUtil.getSiteUrl(ceo.getColumnId(),ceo.getSiteId()).concat("/").concat(String.valueOf(ceo.getColumnId())).concat("/").concat(String.valueOf(ceo.getId())).concat(".html");
                }
                ceo.setLink(link);
                try {
                    ContentMongoEO contentMongoEO = mongoService.queryById(ceo.getId());
                    if (null != contentMongoEO) {
                        ceo.setArticle(contentMongoEO.getContent());
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
