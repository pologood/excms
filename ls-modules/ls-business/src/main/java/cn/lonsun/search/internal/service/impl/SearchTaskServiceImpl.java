/*
 * Powered By zhongjun
 * createtime 2017-11-16 17:55:00
 */

package cn.lonsun.search.internal.service.impl;

import cn.lonsun.common.util.AppUtil;
import cn.lonsun.common.vo.PageQueryVO;
import cn.lonsun.content.internal.service.IBaseContentService;
import cn.lonsun.core.base.service.impl.BaseService;
import cn.lonsun.core.util.Pagination;
import cn.lonsun.indicator.internal.entity.IndicatorEO;
import cn.lonsun.indicator.internal.service.IIndicatorService;
import cn.lonsun.net.service.entity.CmsWorkGuideEO;
import cn.lonsun.net.service.service.IWorkGuideService;
import cn.lonsun.search.internal.dao.ISearchTaskDao;
import cn.lonsun.search.internal.entity.SearchTaskEO;
import cn.lonsun.search.internal.service.ISearchTaskService;
import cn.lonsun.search.internal.util.SearchTaskExcuter;
import cn.lonsun.site.site.internal.service.IColumnConfigService;
import cn.lonsun.solr.SolrUtil;
import cn.lonsun.solr.vo.QueryResultVO;
import cn.lonsun.solr.vo.SolrIndexVO;
import cn.lonsun.solr.vo.SolrPageQueryVO;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.beans.DocumentObjectBinder;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocumentList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.SchedulingTaskExecutor;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @author zhongjun
 * @createtime 2017-11-16 17:55:00
 */
@Service("searchTaskService")
public class SearchTaskServiceImpl extends BaseService<SearchTaskEO> implements ISearchTaskService {

    private Logger log = LoggerFactory.getLogger(getClass());

    @Autowired
    private ISearchTaskDao searchTaskDao;

    @Autowired
    private SchedulingTaskExecutor taskExecutor;

    @Autowired
    private SolrServer solrServer;

    @Autowired
    private IBaseContentService baseContentService;

    @Autowired
    private IIndicatorService indicatorService;

    @Autowired
    private IWorkGuideService workGuideService;
    @Autowired
    private IColumnConfigService columnConfigService;

    @Override
    public Pagination getPage(PageQueryVO pageinfo) {
        return searchTaskDao.getPage(pageinfo);
    }

    @Override
    public void saveOrUpdate(SearchTaskEO eo) {
        if (!AppUtil.isEmpty(eo.getId())) {
            super.updateEntity(eo);
            return;
        }
        super.saveEntity(eo);
    }

    @Override
    public SearchTaskEO getByID(long id) {
        return super.getEntity(SearchTaskEO.class, id);
    }

    @Override
    public void delete(long id) {
        super.delete(SearchTaskEO.class, id);
    }

    @Override
    public void save(final String searchKey) {
        SearchTaskEO eo = new SearchTaskEO(searchKey);
        searchTaskDao.save(eo);
        //创建solr查询任务
        taskExecutor.execute(new SearchTaskExcuter(this, solrServer, eo.getSearchKey(), eo.getId()));
    }

    @Transactional
    @Override
    public void delete(String[] id) {
        Long[] ids = new Long[id.length];
        for (int i = 0; i < ids.length; i++) {
            ids[i] = Long.valueOf(id[i]);
        }
        searchTaskDao.delete(SearchTaskEO.class, ids);
    }

    /**
     * 设置任务已完成
     *
     * @param id
     * @param msg
     */
    @Transactional
    @Override
    public void setFinished(Long id, String msg) {
        SearchTaskEO task = searchTaskDao.getEntity(SearchTaskEO.class, id);
        task.setFinishTime(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
        task.setSearchResult(msg);
        searchTaskDao.save(task);
    }

    @Override
    public Pagination queryFromSolr(String searchKey, Map<String, Object> otherParam, long pageIndex, int pageSize) {
        try {
            //第一次查询，从中获取记录数，然后再根据记录数分页查询，所有数据
            SolrQuery query = getQuery(searchKey, otherParam, pageIndex, pageSize);
            QueryResponse response = solrServer.query(query);
            SolrDocumentList solrDocuments = response.getResults();
            Pagination page = new Pagination();
            page.setTotal(solrDocuments.getNumFound());
            page.setPageIndex(pageIndex);
            page.setPageSize(pageSize);
            DocumentObjectBinder binder = new DocumentObjectBinder();
            List<SolrIndexVO> vos = binder.getBeans(SolrIndexVO.class, solrDocuments);
            Map<String, Map<String, List<String>>> map = response.getHighlighting();
            List<QueryResultVO> data = new ArrayList<QueryResultVO>(vos.size());
            for (SolrIndexVO vo : vos) {
                Map<String, List<String>> _map = map.get(vo.getId());
                if (null != _map && !_map.isEmpty()) {
                    List<String> titles = _map.get("title");
                    if (null != titles && !titles.isEmpty()) {
                        if (!AppUtil.isEmpty(titles.get(0))) {
                            vo.setTitle(titles.get(0));
                        }
                    }
                    List<String> contents = _map.get("content");
                    if (null != contents && !contents.isEmpty()) {
                        if (!AppUtil.isEmpty(contents.get(0))) {
                            vo.setContent(contents.get(0));
                        }
                    }
                }
                QueryResultVO result = formatSolrIndex(vo);
                data.add(result);
            }
            page.setData(data);
            return page;
        } catch (SolrServerException e) {
            e.printStackTrace();
            log.error("==搜索失败：{}=", e.getCause());
        } catch (Exception e) {
            e.printStackTrace();
            log.error("搜索报错：{}", e.getCause());
        }
        return null;
    }

    /**
     * 根据查询类型查询
     *
     * @param vo
     * @return
     */
    private QueryResultVO formatSolrIndex(SolrIndexVO vo) {
        QueryResultVO resultVO = new QueryResultVO();
        resultVO.setId(vo.getId());
        resultVO.setColumnId(vo.getColumnId());
        resultVO.setTitle(vo.getTitle());
        IndicatorEO eo = indicatorService.getEntity(IndicatorEO.class, vo.getColumnId());
        resultVO.setColumnName(eo != null ? eo.getParentNames() + ">" + eo.getName() : "");
        resultVO.setContent(vo.getContent());
        resultVO.setRemark(vo.getRemark());
        resultVO.setCreateDate(dataFormat(vo.getCreateDate()));
        resultVO.setSiteId(vo.getSiteId());
        resultVO.setTypeCode(vo.getTypeCode());

        if (!AppUtil.isEmpty(vo.getTypeCode()) && vo.getTypeCode().contains(SolrPageQueryVO.TypeCode.workGuide.toString())) {
            CmsWorkGuideEO guideEO = workGuideService.getByContentId(Long.valueOf(vo.getId()));
            if (null != guideEO) {
                if (null != guideEO.getLinkUrl() && !"".equals(guideEO.getLinkUrl())) {
                    resultVO.setLink(guideEO.getLinkUrl());
                } else {
                    String link = "/content/article/" + guideEO.getContentId() + "?guideId=" + guideEO.getId();// 拿到栏目页和文章页id
                    resultVO.setLink(link);
                }
            }
        } else if(!AppUtil.isEmpty(vo.getTypeCode()) && vo.getTypeCode().contains(SolrPageQueryVO.TypeCode.public_content.toString())){
            String link = "/public/content/" + vo.getId();// 拿到栏目页和文章页id
            resultVO.setLink(link);
        } else {
            String link = "/content/article/" + vo.getId() ;// 拿到栏目页和文章页id
            resultVO.setLink(link);
        }
        return resultVO;
    }

    /**
     * 格式化时间
     *
     * @param date
     * @return
     */
    private String dataFormat(Date date) {
        if (null == date) {
            return "";
        }
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        return df.format(date);
    }

    private static final long dayMillis = 1000 * 60 * 60 * 24;
    private static final long weekMillis = dayMillis * 7;
    private static final long monthMillis = dayMillis * 30;
    private static final long yearMillis = dayMillis * 365;

    private SolrQuery getQuery(String searchKey, Map<String, Object> otherParam, Long pageIndex, int pageSize) {
        SolrQuery query = new SolrQuery();
        String queryparam = getSolrParmsString(searchKey, SolrUtil.ALL_FIELDS_SOLR);
        if (otherParam.containsKey("timeType") && otherParam.get("timeType") != null) {
            String timeType = otherParam.get("timeType").toString();
            Date endDate = new Date();
            Date startDate = new Date();
            String startDateStr = null;
            String endDateStr = null;
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
            SimpleDateFormat solrdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
            if (timeType.equals("day")) {
                startDate = new Date(endDate.getTime() - dayMillis);
                startDateStr = format.format(startDate);
                endDateStr = format.format(endDate);
            } else if (timeType.equals("2day")) {
                startDate = new Date(endDate.getTime() - dayMillis * 2);
                startDateStr = format.format(startDate);
                endDateStr = format.format(endDate);
            } else if (timeType.equals("3day")) {
                startDate = new Date(endDate.getTime() - dayMillis * 3);
                startDateStr = format.format(startDate);
                endDateStr = format.format(endDate);
            } else if (timeType.equals("week")) {
                startDate = new Date(endDate.getTime() - weekMillis);
                startDateStr = format.format(startDate);
                endDateStr = format.format(endDate);
            } else if (timeType.equals("month")) {
                startDate = new Date(endDate.getTime() - monthMillis);
                startDateStr = format.format(startDate);
                endDateStr = format.format(endDate);
            } else if (timeType.equals("year")) {
                startDate = new Date(endDate.getTime() - yearMillis);
                startDateStr = format.format(startDate);
                endDateStr = format.format(endDate);
            }else if (timeType.equals("other") && (otherParam.containsKey("startTime") && otherParam.get("startTime") != null)
                && (otherParam.containsKey("endTime") && otherParam.get("endTime") != null)) {
                startDateStr = otherParam.get("startTime").toString();
                endDateStr = otherParam.get("endTime").toString();
                try {
                    startDate = format.parse(startDateStr);
                    endDate = format.parse(endDateStr);
                } catch (ParseException e) {
                    e.printStackTrace();
                    log.error("========== 时间格式不正确：{} | {}==========", startDateStr, endDateStr);
                    endDate = null;
                }
            }else{
                log.error("未知的时间类型：{}", timeType);
                endDate = null;
            }
            if(startDate != null && endDate != null){
//                query.addDateRangeFacet("createDate", startDate, endDate, "yyyy-MM-dd'T'");
                queryparam = "("+ queryparam + ") AND createDate:[" + solrdf.format(startDate) + " TO " + solrdf.format(endDate)+"]";
            }
        }
        log.error("===================全局搜索报文start============================");
        log.error(queryparam);
        log.error("===================全局搜索报文end============================");
        query.setQuery(queryparam);
        query.addSort("title", SolrQuery.ORDER.desc);
        query.addHighlightField("title");//高亮字段
//        query.addHighlightField("remark");//高亮字段
        query.addHighlightField("content");//高亮字段
        query.setHighlightSimplePre("<font color='red'>");//渲染标签
        query.setHighlightSimplePost("</font>");//渲染标签
        query.setHighlightFragsize(7000);
        query.setStart(pageIndex.intValue() * pageSize);
        query.setRows(pageSize > 0 ? pageSize : 20);
        return query;
    }

    public static String getSolrParmsString(String value, String... fields) {
        StringBuffer sb = new StringBuffer();
        if (fields != null && fields.length >= 1) {
            for (String field : fields) {
                sb.append(" ").append(field).append(":").append(value);
            }
        }
        return sb.toString();
    }

}
