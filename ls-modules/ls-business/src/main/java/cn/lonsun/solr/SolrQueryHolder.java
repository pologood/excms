package cn.lonsun.solr;

import cn.lonsun.common.util.AppUtil;
import cn.lonsun.core.base.util.StringUtils;
import cn.lonsun.core.util.SpringContextHolder;
import cn.lonsun.core.util.XSSFilterUtil;
import cn.lonsun.indicator.internal.entity.IndicatorEO;
import cn.lonsun.lsrobot.service.ILsRobotFilterService;
import cn.lonsun.solr.entity.IndexKeyWordsEO;
import cn.lonsun.solr.service.IIndexKeyWordsService;
import cn.lonsun.solr.vo.SolrIndexVO;
import cn.lonsun.solr.vo.SolrPageQueryVO;
import cn.lonsun.util.PropertiesHelper;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.beans.DocumentObjectBinder;
import org.apache.solr.client.solrj.impl.HttpSolrServer;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocumentList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.util.HtmlUtils;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author gu.fei
 * @version 2016-1-28 9:54
 */
public class SolrQueryHolder {

//    private static final String SOLR_URL = "http://192.168.1.206:9090/solr/

    private static Logger logger = LoggerFactory.getLogger(SolrQueryHolder.class);

    private static PropertiesHelper properties = SpringContextHolder.getBean("propertiesHelper");

    private static HttpSolrServer server;

    private static IIndexKeyWordsService indexKeyWordsService;

    private static ILsRobotFilterService lsRobotFilterService;

    private static final int PAGE_INDEX = 0;
    private static final int PAGE_MAX_SIZE = 999999999;

    static {
        indexKeyWordsService = SpringContextHolder.getBean(IIndexKeyWordsService.class);
        lsRobotFilterService = SpringContextHolder.getBean(ILsRobotFilterService.class);
        server = new HttpSolrServer(properties.getSorlUrl());
        server.setConnectionTimeout(5000);
        server.setDefaultMaxConnectionsPerHost(100);
        server.setMaxRetries(1);
        server.setMaxTotalConnections(100);
    }

    /**
     * 全文检索
     * @param queryVO
     * @return
     * @throws SolrServerException
     * @throws IOException
     */
    public static List<SolrIndexVO> query(SolrPageQueryVO queryVO) throws
            SolrServerException, IOException, InvocationTargetException, IllegalAccessException {
        if(null != queryVO.getKeywords() || null != queryVO.getSiteId()) {
            //如果包含特殊字符直接返回
            String words = HtmlUtils.htmlUnescape(queryVO.getKeywords());//:gt这种转移过的恢复后在做检索
            queryVO.setKeywords(words);
            String regEx="[\\\\<>/]";
            Pattern p = Pattern.compile(regEx);
            Matcher m = p.matcher(words);
            if (m.find()) {
                logger.info("搜索关键词有非法字符");
                return Collections.emptyList();
            }

            //将空格间隔开的词语分开，保存和校验
            String keywords = StringUtils.rplcBlank(queryVO.getKeywords(), ",");
            //对keywords做处理,查询之前对字符串做转义
            List<String> kwarr = StringUtils.getListWithString(keywords, ",");
            for (String kwa : kwarr) {
                String filterwords = XSSFilterUtil.stripXSS(kwa);
                if(!AppUtil.isEmpty(filterwords)) {
                    try {
                        //保存关键词
                        IndexKeyWordsEO eo = new IndexKeyWordsEO();
                        eo.setSiteId(queryVO.getSiteId());
                        eo.setKeyWords(kwa);
                        Pattern pa = Pattern.compile("^[a-zA-Z0-9\\u4e00-\\u9fa5\\pP‘’“”]+$");//匹配数字，标点，中英文
                        if(pa.matcher(kwa).find()){
                            indexKeyWordsService.saveEntity(eo);
                        }
                    } catch (Exception e) {
                        logger.error("保存关键词异常");
                        e.printStackTrace();
                    }
                } else {
                    logger.info("搜索关键词有非法字符");
                    return Collections.emptyList();
                }
            }
        }

        Integer pageIndex = Integer.parseInt(String.valueOf(queryVO.getPageIndex()));
        Integer pageSize = queryVO.getPageSize();
        boolean islight = queryVO.getIslight();
        server.setMaxRetries(1);
        server.setConnectionTimeout(5000);
        try {
            //查询需要过滤的关键词
            String[] keyWords = lsRobotFilterService.getAllKeyWords();
            queryVO.setFilterKeyWords(keyWords);
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("获取需要过滤的关键词出现错误：{}", e.getCause());
        }
        SolrQuery query = SolrUtil.getSolrQuery(queryVO);
        //补充query内容
        fullQuery(query,queryVO);

        QueryResponse response;
        SolrDocumentList solrDocuments;
        DocumentObjectBinder binder;
        List<SolrIndexVO> vos;
        Map<String, Map<String, List<String>>> map = new HashMap<String, Map<String, List<String>>>();

        //如果是分组模式
        if (queryVO.getGroup() != null && queryVO.getGroup()) {
            Map<String,String> groupMap = new LinkedHashMap<String,String>(pageSize.intValue());
            int groupIndex = 0;
            int whileCount = 0;
            while(groupIndex < 10) {
                query.setStart(whileCount*pageSize);
                //循环多次去结果，得到10个不同的key
                response = server.query(query);
                solrDocuments = response.getResults();
                if(islight) {
                    map.putAll(response.getHighlighting());
                }
                binder = new DocumentObjectBinder();
                vos = binder.getBeans(SolrIndexVO.class, solrDocuments);
                //重新组织数据，将title相同的组织到一起返回到前台
                for (SolrIndexVO vo : vos) {
                    String title = vo.getTitle();
                    if (groupMap.get(title) == null) {
                        groupMap.put(title,title);
                        groupIndex++;
                        //如果groupMap 个数为pageSize。则不需要继续遍历，直接跳出循环
                        if (groupIndex == pageSize.intValue()) break;
                    }
                }
                //如果查询已经没有后续数据则跳出循环
                if (vos.size() < pageSize) break;
                whileCount++;
            }

            vos = new ArrayList<SolrIndexVO>();
            groupIndex = 0;

            SolrPageQueryVO queryTitleVO = new SolrPageQueryVO();
            AppUtil.copyProperties(queryTitleVO,queryVO);
            queryTitleVO.setColumnId(null);queryTitleVO.setTableColumnId(null);
            queryTitleVO.setIslight(false);
            queryTitleVO.setFromCode(SolrPageQueryVO.FromCode.title.toString());
            //遍历groupMap，取出title查询不同title对应的数据
            for (Map.Entry<String,String> entry : groupMap.entrySet()) {
                int queryIndex = 0;
                int itemCount = 0;//每个title对应内容的个数
                OUT:
                while (true) {
                    String title = entry.getKey();
                    queryTitleVO.setKeywords(title);
                    queryTitleVO.setFromCode(SolrPageQueryVO.FromCode.title.toString());
                    query = SolrUtil.getSolrQuery(queryTitleVO);
                    fullQuery(query,queryTitleVO);
                    //移除排序
                    //query.removeSort(queryVO.getSortField());
                    query.setStart(queryIndex*5);
                    query.setRows(5);
                    response = server.query(query);
                    solrDocuments = response.getResults();
                    binder = new DocumentObjectBinder();
                    List<SolrIndexVO> vos1 = binder.getBeans(SolrIndexVO.class, solrDocuments);
                    for (int i = 0 ; i < vos1.size() ; i++) {
                        SolrIndexVO vo = vos1.get(i);
                        if (vo.getTitle().equals(title)) {
                            vo.setGroupIndex(groupIndex);
                            vo.setCount(null);
                            vos.add(vo);
                            itemCount++;
                            if (itemCount == vos1.size()) {
                                vos.get(vos.size()-itemCount).setCount(itemCount);
                            }
                        }else {
                            if (itemCount > 0) {
                                vos.get(vos.size()-itemCount).setCount(itemCount);
                                break OUT;
                            }
                        }
                    }
                    queryIndex++;
                    //如果查询已经没有后续数据则跳出循环
                    if (vos1.size() < 5) break OUT;
                }
                groupIndex++;
            }
        }else {
            //常规模式
            response = server.query(query);
            solrDocuments = response.getResults();
            binder = new DocumentObjectBinder();
            vos = binder.getBeans(SolrIndexVO.class, solrDocuments);
            if(islight) {
                map.putAll(response.getHighlighting());
            }
        }

        if(islight) {
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
        }

        return vos;
    }

    private static void fullQuery(SolrQuery query,SolrPageQueryVO queryVO) {
        Integer pageIndex = Integer.parseInt(String.valueOf(queryVO.getPageIndex()));
        Integer pageSize = queryVO.getPageSize();
        boolean islight = queryVO.getIslight();
        //添加typeCode排序
        query.addSort("typeSortNum",SolrQuery.ORDER.asc);
        query.addSort(queryVO.getSortField(), (null != queryVO.getSortOrder() && queryVO.getSortOrder().equals("asc") ) ? SolrQuery.ORDER.asc : SolrQuery.ORDER.desc);
        if(islight) {
            query.addHighlightField("title");//高亮字段
            query.addHighlightField("remark");//高亮字段
            query.addHighlightField("content");//高亮字段
            query.setHighlightSimplePre("<font color='red'>");//渲染标签
            query.setHighlightSimplePost("</font>");//渲染标签
            query.setHighlightFragsize(7000);
        }
        query.setStart(pageIndex*pageSize);
        query.setRows(pageSize.intValue() > 0 ? pageSize : 20);

        //nowIds如果存在，则过滤这些ids
        String nowIds = queryVO.getExcIds();
        if (!AppUtil.isEmpty(nowIds)) {
            String[] nowIdsArray = nowIds.split(",");
            StringBuffer sb = new StringBuffer();
            for (String nowId : nowIdsArray) {
                sb.append(" NOT id:\"").append(nowId).append("\"");
            }

            query.setQuery(query.getQuery() + sb.toString());
        }
    }

    /**
     * @param columnId 栏目ID
     * @return
     * @throws SolrServerException
     * @throws IOException
     */
    public static List<SolrIndexVO> query(Long columnId) throws
            SolrServerException, IOException {

        StringBuilder sb = new StringBuilder("columnId:\"" + columnId + "\"");
        SolrQuery query = new SolrQuery();
        query.setQuery(sb.toString());
        query.setStart(PAGE_INDEX);
        query.setRows(PAGE_MAX_SIZE);
        QueryResponse response = server.query(query);
        SolrDocumentList solrDocuments = response.getResults();
        DocumentObjectBinder binder = new DocumentObjectBinder();
        List<SolrIndexVO> vos = binder.getBeans(SolrIndexVO.class, solrDocuments);
        return vos;
    }

    /**
     * 查询数量
     * @param queryVO
     * @return
     * @throws SolrServerException
     * @throws IOException
     */
    public static Long queryCount(SolrPageQueryVO queryVO) throws
            SolrServerException, IOException {
        SolrQuery query = SolrUtil.getSolrQuery(queryVO);
        query.setStart(0);
        query.setRows(0);
        String nowIds = queryVO.getExcIds();
        if (!AppUtil.isEmpty(nowIds)) {
            String[] nowIdsArray = nowIds.split(",");
            StringBuffer sb = new StringBuffer();
            for (String nowId : nowIdsArray) {
                sb.append(" NOT id:\"").append(nowId).append("\"");
            }

            query.setQuery(query.getQuery() + sb.toString());
        }
        QueryResponse response = server.query(query);
        return response.getResults().getNumFound();
    }

    /**
     * 查询所有
     * @return
     * @throws SolrServerException
     * @throws IOException
     */
    public static Long queryAllCount(SolrPageQueryVO queryVO) throws
            SolrServerException, IOException {
        SolrQuery query = SolrUtil.getSolrQuery(queryVO);
        query.setStart(0);
        query.setRows(0);
        QueryResponse response = server.query(query);
        return response.getResults().getNumFound();
    }

    /**
     * 获取solr查询语句
     * @param queryVO
     * @return
     */
    private  static String getSolrQuerySql(SolrPageQueryVO queryVO) {

        queryVO.setKeywords(XSSFilterUtil.stripXSSSolr(queryVO.getKeywords()));
        if(AppUtil.isEmpty(queryVO.getKeywords())) {
            return "";
        }

        StringBuilder sbkw = new StringBuilder();
        if(null != queryVO.getKeywords()) {
            String keywords = StringUtils.rplcBlank(queryVO.getKeywords(),",");
            List<String> kwarr = StringUtils.getListWithString(keywords,",");
            sbkw.append("(");
            int count = 0;
            for(String str : kwarr) {
                if(null != str) {
                    if(count++ > 0) {
                        sbkw.append(",");
                    }
                    sbkw.append("\"" + str +"\"");
                }
            }
            sbkw.append(")");
        }

        StringBuilder sb = new StringBuilder();

        boolean flagA = false;
        if(!isNull(queryVO.getSiteId())) {
            sb.append("siteId:\"" + queryVO.getSiteId() + "\"");
            flagA = true;
        }

        if(!isNull(queryVO.getColumnId())) {
            if(flagA) {
                sb.append(" AND ");
            }
            sb.append(" columnId:\"" + queryVO.getColumnId() + "\"");
        } else if(!isNull(queryVO.getColumnIds())) {
            List<Long> list = StringUtils.getListWithLong(queryVO.getColumnIds(),",");
            if(flagA) {
                sb.append(" AND ");
            }

            int c = 0;
            sb.append("(");
            for(Long l : list) {
                if(c++ == 0) {
                    sb.append(" columnId:\"" + l + "\"");
                } else {
                    sb.append(" OR columnId:\"" + l + "\"");
                }
            }
            sb.append(")");
        } else if(null != queryVO.getIndicatorEOs()) {
            if(flagA) {
                sb.append(" AND ");
            }
            sb.append(" (");
            int count = 1;
            List<IndicatorEO> eos = queryVO.getIndicatorEOs();
            for(IndicatorEO eo : eos) {
                sb.append(" columnId:\"" + eo.getIndicatorId() + "\"");
                if(count ++ < eos.size()) {
                    sb.append(" OR ");
                }
            }
            sb.append(" )");
        }

        if(!isNull(queryVO.getExcColumns())) {
            List<Long> list = StringUtils.getListWithLong(queryVO.getExcColumns(), ",");
            boolean flag = true;
            for(Long l : list) {
                if(flag) {
                    sb.append(" AND !columnId:\"" + l + "\"");
                } else {
                    sb.append(" AND !columnId:\"" + l + "\"");
                }
                flag = false;
            }
        }

        //根据文章类型查询
        if(!isNull(queryVO.getTypeCode())) {

            List<String> typeCodes = StringUtils.getListWithString(queryVO.getTypeCode(),",");
            int c = 1;
            if(null != typeCodes) {
                sb.append(" AND (");
                for(String str : typeCodes) {
                    sb.append("typeCode:\"" + str + "\"");
                    if(c++ < typeCodes.size()) {
                        sb.append(" OR ");
                    }
                }
                sb.append(")");
            }

        } else {
            sb.append(" AND (");
            sb.append(" typeCode:\"" + SolrPageQueryVO.TypeCode.articleNews.toString() + "\"");
            sb.append(" OR typeCode:\"" + SolrPageQueryVO.TypeCode.pictureNews.toString() + "\"");
            sb.append(" OR typeCode:\"" + SolrPageQueryVO.TypeCode.videoNews.toString() + "\"");
            sb.append(" )");
        }

        //根据关键词来源查询
        if(!isNull(queryVO.getKeywords())) {
            if(null != queryVO.getTypeCode() && queryVO.getTypeCode().equals(SolrPageQueryVO.TypeCode.workGuide.toString())) {
//                sb.append(" AND (");
                if(!AppUtil.isEmpty(queryVO.getFromCode())) {

                    List<String> fromCodes = StringUtils.getListWithString(queryVO.getFromCode(),",");
                    int c = 1;
                    if(null != fromCodes && !fromCodes.isEmpty()) {
                        sb.append(" AND (");
                        for(String str : fromCodes) {
                            sb.append(str + ":" + sbkw.toString());
                            if(c++ < fromCodes.size()) {
                                sb.append(" OR ");
                            }
                        }

                        if(queryVO.getFromCode().contains(SolrPageQueryVO.FromCode.content.toString())) {
                            sb.append(" OR setAccord:" + sbkw.toString());
                            sb.append(" OR applyCondition:" + sbkw.toString());
                            sb.append(" OR handleData:" + sbkw.toString());
                            sb.append(" OR handleProcess:" + sbkw.toString());
                        }

                        sb.append(")");
                    }

                    boolean flag = false;

                    /*if(queryVO.getFromCode().contains(SolrPageQueryVO.FromCode.title.toString())) {
                        sb.append(" title:" + sbkw.toString());
                        flag = true;
                    }
                    if(queryVO.getFromCode().contains(SolrPageQueryVO.FromCode.content.toString())) {
                        if(flag) {
                            sb.append(" OR ");
                        }
                        sb.append(" content:" + sbkw.toString());
                        sb.append(" OR setAccord:" + sbkw.toString());
                        sb.append(" OR applyCondition:" + sbkw.toString());
                        sb.append(" OR handleData:" + sbkw.toString());
                        sb.append(" OR handleProcess:" + sbkw.toString());
                    }*/
                } else {
                    sb.append(" AND (");
                    sb.append(" title:" + sbkw.toString());
                    sb.append(" OR content:" + sbkw.toString());
                    sb.append(" OR setAccord:" + sbkw.toString());
                    sb.append(" OR applyCondition:" + sbkw.toString());
                    sb.append(" OR handleData:" + sbkw.toString());
                    sb.append(" OR handleProcess:" + sbkw.toString());
                    sb.append(")");
                }

            } else {
                if(isNull(queryVO.getFromCode()) || queryVO.getFromCode().equals(SolrPageQueryVO.FromCode.all.toString())) {
                    sb.append(" AND (title:" + sbkw.toString() + " OR remark:" + sbkw.toString() + " OR content:" + sbkw.toString() + ")");
                } else {

                    List<String> fromCodes = StringUtils.getListWithString(queryVO.getFromCode(),",");
                    int c = 1;
                    if(null != fromCodes) {
                        sb.append(" AND (");
                        for(String str : fromCodes) {
                            sb.append(str + ":" + sbkw.toString());
                            if(c++ < fromCodes.size()) {
                                sb.append(" OR ");
                            }
                        }
                        sb.append(")");
                    }
                }
            }
        }

        if(!AppUtil.isEmpty(queryVO.getType())) {
            sb.append(" AND type :\"" + queryVO.getType() + "\"");
        }

        //根据时间段检索
        if(null != queryVO.getBeginDate() && null != queryVO.getEndDate()) {
            sb.append(" AND createDate:[" + getSolrDate(queryVO.getBeginDate()) + " TO " + getSolrDate(queryVO.getEndDate()) + "]");
        } else if(null != queryVO.getBeginDate() && null == queryVO.getEndDate()) {
            sb.append(" AND createDate:[" + getSolrDate(queryVO.getBeginDate()) + " TO * ]");
        } else if(null == queryVO.getBeginDate() && null != queryVO.getEndDate()) {
            sb.append(" AND createDate:[ * TO " + getSolrDate(queryVO.getEndDate()) + "]");
        }

        return sb.toString();
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

    private static boolean isNull(Object str) {
        return null == str || "".equals(str) || "null".equals(str);
    }

    public static void main(String[] args){
        try {
            SolrQuery query = new SolrQuery();
            query.set("q", "*:*");
            QueryResponse rsp = server.query(query);
            SolrDocumentList docs = rsp.getResults();
            System.out.println("文档个数：" + docs.getNumFound());
            System.out.println("查询时间：" + rsp.getQTime());

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
