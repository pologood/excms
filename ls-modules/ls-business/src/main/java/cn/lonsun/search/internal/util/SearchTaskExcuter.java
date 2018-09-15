package cn.lonsun.search.internal.util;

import cn.lonsun.common.util.AppUtil;
import cn.lonsun.search.internal.service.ISearchTaskService;
import cn.lonsun.solr.SolrUtil;
import cn.lonsun.solr.vo.SolrIndexVO;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.beans.DocumentObjectBinder;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocumentList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;

/**
 * 搜索任务执行
 * @author zhongjun
 */
public class SearchTaskExcuter implements  Runnable {

    private Logger log = LoggerFactory.getLogger(getClass());

    private ISearchTaskService searchTaskService;

    private String searchKey;

    private SolrServer solrServer;

    private Long id;

    public SearchTaskExcuter(ISearchTaskService searchTaskService, SolrServer solrServer, String searchKey, Long id) {
        this.searchTaskService = searchTaskService;
        this.solrServer = solrServer;
        this.searchKey = searchKey;
        this.id = id;
    }

    @Override
    public void run() {
        log.info("==全局搜索开始：关键字：{}==============", searchKey);
        try {
            long num = 0;
            //第一次查询，从中获取记录数，然后再根据记录数分页查询，所有数据
            QueryResponse response = solrServer.query(getQuery(0,20));
            SolrDocumentList solrDocuments = response.getResults();
            num = solrDocuments.getNumFound();
            DocumentObjectBinder binder = new DocumentObjectBinder();
            List<SolrIndexVO> vos = binder.getBeans(SolrIndexVO.class, solrDocuments);

            Map<String, Map<String, List<String>>> map = response.getHighlighting();
            for (SolrIndexVO vo : vos) {
                Map<String, List<String>> _map = map.get(vo.getId());
                if (null != _map && !_map.isEmpty()) {
                    List<String> titles = _map.get("title");
                    if (null != titles && !titles.isEmpty()) {
                        if (!AppUtil.isEmpty(titles.get(0))) {
                            vo.setTitle(titles.get(0));
                        }
                    }

                    List<String> remarks = _map.get("remark");
                    if (null != remarks && !remarks.isEmpty()) {
                        if (!AppUtil.isEmpty(remarks.get(0))) {
                            vo.setRemark(remarks.get(0));
                        }
                    }

                    List<String> contents = _map.get("content");
                    if (null != contents && !contents.isEmpty()) {
                        if (!AppUtil.isEmpty(contents.get(0))) {
                            vo.setContent(contents.get(0));
                        }
                    }
                }
            }
            log.info("==全局搜索完成：查询到总条数：{}==============", num);

            searchTaskService.setFinished(this.id, "搜索到"+num+"条相关记录");
        } catch (Exception e) {
            e.printStackTrace();
            log.info("==全局搜索失败：{}==============", e.getMessage());
            searchTaskService.setFinished(this.id, "搜索失败");
        }finally {
            searchTaskService = null;
            searchKey = null;
            solrServer = null;
            id = null;
        }
    }

    private SolrQuery getQuery(int pageIndex, int pageSize){
        SolrQuery query = new SolrQuery();
        query.setQuery(SolrUtil.getSolrParmsString(this.searchKey, SolrUtil.ALL_FIELDS_SOLR));
        query.addSort("title", SolrQuery.ORDER.desc);
        query.addHighlightField("title");//高亮字段
        query.addHighlightField("remark");//高亮字段
        query.addHighlightField("content");//高亮字段
        query.setHighlightSimplePre("<font color='red'>");//渲染标签
        query.setHighlightSimplePost("</font>");//渲染标签
        query.setHighlightFragsize(7000);
        query.setStart(pageIndex*pageSize);
        query.setRows(pageSize > 0 ? pageSize : 20);
        return query;
    }

}
