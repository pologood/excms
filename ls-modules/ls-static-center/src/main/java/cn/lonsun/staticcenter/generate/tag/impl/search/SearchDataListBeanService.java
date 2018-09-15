package cn.lonsun.staticcenter.generate.tag.impl.search;

import cn.lonsun.common.util.AppUtil;
import cn.lonsun.content.internal.entity.BaseContentEO;
import cn.lonsun.core.util.XSSFilterUtil;
import cn.lonsun.indicator.internal.entity.IndicatorEO;
import cn.lonsun.indicator.internal.service.IIndicatorService;
import cn.lonsun.net.service.entity.CmsWorkGuideEO;
import cn.lonsun.net.service.service.IWorkGuideService;
import cn.lonsun.solr.SolrQueryHolder;
import cn.lonsun.solr.entity.IndexKeyWordsEO;
import cn.lonsun.solr.service.IIndexKeyWordsService;
import cn.lonsun.solr.vo.QueryResultVO;
import cn.lonsun.solr.vo.SolrIndexVO;
import cn.lonsun.solr.vo.SolrPageQueryVO;
import cn.lonsun.staticcenter.generate.tag.impl.AbstractBeanService;
import cn.lonsun.staticcenter.generate.tag.impl.search.util.SearchUtil;
import cn.lonsun.staticcenter.generate.thread.Context;
import cn.lonsun.staticcenter.generate.thread.ContextHolder;
import cn.lonsun.staticcenter.generate.util.PathUtil;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang.StringEscapeUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Pattern;

/**
 * @author gu.fei
 * @version 2016-3-14 16:19
 */
@Component
public class SearchDataListBeanService extends AbstractBeanService {

    private static final Logger logger = LoggerFactory.getLogger(SearchDataListBeanService.class);

    @Autowired
    private IIndexKeyWordsService indexKeyWordsService;

    @Autowired
    private IIndicatorService indicatorService;

    @Autowired
    private IWorkGuideService workGuideService;

    @Override
    public Object getObject(JSONObject paramObj) {
        Context context = ContextHolder.getContext();
        Map<String, String> map = context.getParamMap();

        //分页条数
        Integer pageSize = paramObj.getInteger("pageSize");

        Long siteId = paramObj.getLong("siteId");
        if (siteId == null) {
            //来自url参数
            siteId = context.getSiteId();
        }
        //高亮显示
        Boolean islight = paramObj.getBoolean("islight");
        //页码
        Long pageIndex = context.getPageIndex();

        //关键词
        String keywords = paramObj.getString("keywords");

        String flag = "";
        Long tableColumnId = null;
        if (!AppUtil.isEmpty(map) && !map.isEmpty()) {
            if (AppUtil.isEmpty(keywords)) {
                keywords = map.get("keywords");
            }

            flag = String.valueOf(map.get("flag"));
            if (!SearchUtil.isNull(map.get("pageSize"))) {
                pageSize = Integer.parseInt(map.get("pageSize"));
            }

            if (!AppUtil.isEmpty(map.get("tableColumnId"))) {
                tableColumnId = Long.valueOf(map.get("tableColumnId"));
            }

            //搜索类型，区分是专题（子站）搜索
            String searchType = String.valueOf(map.get("searchType"));
            if (!AppUtil.isEmpty(searchType)) {
                searchType = SearchUtil.paternStr(searchType);
                paramObj.put("searchType", Integer.parseInt(searchType));
            }
        }

        if (!AppUtil.isEmpty(keywords) && SearchUtil.isNull(flag)) {
            IndexKeyWordsEO keyWordsEO = new IndexKeyWordsEO();
            keywords = SearchUtil.paternStr(keywords);
            keyWordsEO.setKeyWords(keywords);
            keyWordsEO.setSiteId(siteId);
            Pattern pa = Pattern.compile("^[a-zA-Z0-9\\u4e00-\\u9fa5\\pP‘’“”]+$");//匹配数字，标点，中英文
            if (pa.matcher(keywords).find()) {
                indexKeyWordsService.saveEntity(keyWordsEO);
            }
        }

        SolrPageQueryVO vo = SearchUtil.setPageVal(map);
        vo.setSiteId(siteId);
        vo.setKeywords(keywords);
        vo.setPageSize(pageSize);
        vo.setPageIndex(pageIndex - 1);

        //网上办事搜索相关表格资源下载栏目ID，不做查询用的参数
        if (null == tableColumnId) {
            tableColumnId = paramObj.getLong("tableColumnId");
        }
        vo.setTableColumnId(tableColumnId);
        if (islight != null) {
            //来自url参数
            vo.setIslight(islight);
        }

        paramObj.put("pageSize", pageSize);
        paramObj.put("typeCode", vo.getTypeCode());
        try {
            Long count = SolrQueryHolder.queryCount(vo);
            paramObj.put("count", count);
            paramObj.put("pageCount", (count % pageSize == 0 ? count / pageSize : (count / pageSize + 1)));
        } catch (Exception e) {
            paramObj.put("count", 0);
            paramObj.put("pageCount", 0);
        }

        paramObj.put("keywords", StringEscapeUtils.escapeHtml(keywords));

        return formatSolrIndex(vo);
    }

    /**
     * 预处理结果
     *
     * @param resultObj
     * @param paramObj
     * @return
     */
    public Map<String, Object> doProcess(Object resultObj, JSONObject paramObj) {

        // 数据预处理
        Map<String, Object> map = new HashMap<String, Object>();

        //标题长度
        String titleLength = paramObj.getString("titleLength");
        map.put("titleLength", titleLength);
        //内容长度
        String contentLength = paramObj.getString("contentLength");
        map.put("contentLength", contentLength);

        Context context = ContextHolder.getContext();
        Long siteId = context.getSiteId();
        map.put("siteId", siteId);
        String path = PathUtil.getLinkPath("site", "search", context.getSiteId(), null);
        map.put("linkPrefix", path);

        map.put("pageIndex", context.getPageIndex());

        //来自url参数
        Map<String, String> cmap = context.getParamMap();
        //关键词
        if (!AppUtil.isEmpty(cmap) && !cmap.isEmpty()) {
            map.put("keywords", XSSFilterUtil.stripXSS(cmap.get("keywords")));
            map.put("sort", cmap.get("sort"));
            map.put("datecode", cmap.get("datecode"));
            map.put("columnId", cmap.get("columnId"));
            map.put("columnIds", cmap.get("columnIds"));
            map.put("typeCode", cmap.get("typeCode"));
            map.put("fromCode", cmap.get("fromCode"));
            map.put("type", cmap.get("type"));
            map.put("tableColumnId", cmap.get("tableColumnId"));
            map.put("excColumns", cmap.get("excColumns"));
            //信息公开公文号
            map.put("fileNum", cmap.get("fileNum"));
            //信息公开索引号
            map.put("indexNum", cmap.get("indexNum"));

            map.put("beginDate", cmap.get("beginDate"));
            map.put("endDate", cmap.get("endDate"));
        }
        return map;
    }

    /**
     * 根据查询类型查询
     *
     * @param queryVO
     * @return
     */
    private Object formatSolrIndex(SolrPageQueryVO queryVO) {
        Context context = ContextHolder.getContext();
        List<SolrIndexVO> vos = null;
        try {
            vos = SolrQueryHolder.query(queryVO);
        } catch (Exception e) {
            e.printStackTrace();
        }

        List<QueryResultVO> resultVOs = new ArrayList<QueryResultVO>();
        if (null != vos) {
            for (SolrIndexVO vo : vos) {
                QueryResultVO resultVO = new QueryResultVO();
                resultVO.setId(vo.getId());
                resultVO.setColumnId(vo.getColumnId());
                resultVO.setTitle(vo.getTitle());
                IndicatorEO eo = indicatorService.getEntity(IndicatorEO.class, vo.getColumnId());
                resultVO.setColumnName(eo != null ? eo.getName() : "");
                resultVO.setContent(vo.getContent());
                resultVO.setRemark(vo.getRemark());
                resultVO.setCreateDate(dataFormat(vo.getCreateDate()));
                resultVO.setSiteId(vo.getSiteId());
                resultVO.setTypeCode(vo.getTypeCode());

                if (!AppUtil.isEmpty(queryVO.getTypeCode()) && vo.getTypeCode().contains(SolrPageQueryVO.TypeCode.workGuide.toString())) {
                    CmsWorkGuideEO guideEO = workGuideService.getByContentId(Long.valueOf(vo.getId()));
                    if (null != guideEO) {
                        if (null != guideEO.getLinkUrl() && !"".equals(guideEO.getLinkUrl())) {
                            resultVO.setLink(guideEO.getLinkUrl());
                        } else {
                            String link = context.getUri() + "/content/article/" + guideEO.getContentId() + "?guideId=" + guideEO.getId() + "&tableColumnId=" + (null == queryVO.getTableColumnId() ? "" : queryVO.getTableColumnId());// 拿到栏目页和文章页id
                            resultVO.setLink(link);
                        }
                    }
                //信息公开
                } else if (vo.getTypeCode() != null && vo.getTypeCode().contains(BaseContentEO.TypeCode.public_content.toString())) {
                    String link = context.getUri() + "/public/" + vo.getColumnId() + "/" + vo.getId() + ".html";// 拿到栏目页和文章页id
                    resultVO.setLink(link);
                } else {
                    if (null != vo.getUrl() && !"".equals(vo.getUrl())) {
                        resultVO.setLink(vo.getUrl());
                    } else {
                        String link = null;
                        try {
                            link = PathUtil.getLinkPath(vo.getColumnId(), Long.valueOf(vo.getId()));// 拿到栏目页和文章页id
                        } catch (Exception e) {
                            logger.error("获取栏目链接错误");
                            e.printStackTrace();
                        }

                        if (null == link && !AppUtil.isEmpty(vo.getColumnId())) {
                            link = context.getUri() + "/" + vo.getColumnId() + "/" + vo.getId() + ".html";// 拿到栏目页和文章页id
                        }
                        resultVO.setLink(link);
                    }
                }
                resultVOs.add(resultVO);
            }
        }
        return resultVOs;
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
}
