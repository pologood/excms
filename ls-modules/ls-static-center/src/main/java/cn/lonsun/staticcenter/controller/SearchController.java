package cn.lonsun.staticcenter.controller;

import cn.lonsun.cache.client.CacheHandler;
import cn.lonsun.common.util.AppUtil;
import cn.lonsun.content.internal.entity.BaseContentEO;
import cn.lonsun.content.internal.service.IKnowledgeBaseService;
import cn.lonsun.content.vo.ContentPageVO;
import cn.lonsun.content.vo.KnowledgeBaseVO;
import cn.lonsun.core.base.controller.BaseController;
import cn.lonsun.core.base.util.StringUtils;
import cn.lonsun.core.util.HtmlRegexpUtil;
import cn.lonsun.core.util.Pagination;
import cn.lonsun.heatAnalysis.entity.KeyWordsHeatEO;
import cn.lonsun.heatAnalysis.service.IColumnNewsHeatService;
import cn.lonsun.heatAnalysis.service.IKeyWordsHeatService;
import cn.lonsun.lsrobot.entity.LsRobotParamEO;
import cn.lonsun.lsrobot.entity.LsRobotSourcesEO;
import cn.lonsun.lsrobot.service.ILsRobotParamService;
import cn.lonsun.lsrobot.service.ILsRobotSourcesService;
import cn.lonsun.lsrobot.vo.RobotPageVO;
import cn.lonsun.monitor.words.internal.util.WordsSplitHolder;
import cn.lonsun.net.service.entity.CmsWorkGuideEO;
import cn.lonsun.net.service.service.IWorkGuideService;
import cn.lonsun.publicInfo.internal.service.IPublicContentService;
import cn.lonsun.site.site.internal.entity.SiteMgrEO;
import cn.lonsun.solr.SolrQueryHolder;
import cn.lonsun.solr.vo.SolrIndexVO;
import cn.lonsun.solr.vo.SolrPageQueryVO;
import cn.lonsun.solrManage.SolrUtil;
import cn.lonsun.staticcenter.generate.thread.Context;
import cn.lonsun.staticcenter.generate.thread.ContextHolder;
import cn.lonsun.staticcenter.generate.util.PathUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.*;

/**
 * @author gu.fei
 * @version 2016-6-17 8:41
 */
@Controller
@RequestMapping(value = "/search")
public class SearchController extends BaseController {

    private static final String FILE_BASE = "/search";

    @Autowired
    private IWorkGuideService workGuideService;

    @Autowired
    private ILsRobotParamService lsRobotParamService;

    @Autowired
    private ILsRobotSourcesService lsRobotSourcesService;

    @Autowired
    private IColumnNewsHeatService columnNewsHeatService;

    @Autowired
    private IPublicContentService publicContentService;

    @Autowired
    private IKeyWordsHeatService keyWordsHeatService;

    @Autowired
    private IKnowledgeBaseService knowledgeBaseService;

    @RequestMapping(value = "/index")
    public String index(Long siteId, Long tableColumnId, ModelMap map, String filename, String withHotNews, String withPublicc) {
        map.put("withHotNews", withHotNews);
        map.put("withPublicc", withPublicc);
        if (null != siteId) {
            Context context = new Context();
            context.setSiteId(siteId);
            ContextHolder.setContext(context);

            SiteMgrEO site = CacheHandler.getEntity(SiteMgrEO.class,siteId);
            map.put("siteId", siteId);
            map.put("siteName",site == null ? "":site.getName());
            map.put("tableColumnId", tableColumnId);
            LsRobotParamEO eo = lsRobotParamService.getEntityBySiteId(siteId);
            if (null != eo) {
                map.put("notice", eo.getNotice());
            }

            //智能机器人
            RobotPageVO vo = new RobotPageVO();
            vo.setSiteId(siteId);
            vo.setIfActive("1");
            vo.setIfShow("1");
            vo.setSortField("sortNum,createDate");
            vo.setSortOrder("asc");
            List<LsRobotSourcesEO> sources = lsRobotSourcesService.getEntities(vo);
            map.put("sources", sources);

            if (!"false".equals(withHotNews)) {
                try {
                    //热点新闻
                    ContentPageVO contentPageVO = new ContentPageVO();
                    contentPageVO.setSiteId(siteId);
                    contentPageVO.setPageSize(16);
                    String[] typeCodes =
                            {BaseContentEO.TypeCode.articleNews.toString(), BaseContentEO.TypeCode.pictureNews.toString(), BaseContentEO.TypeCode.videoNews.toString()};
                    contentPageVO.setTypeCodes(typeCodes);
                    List<BaseContentEO> contentEOs = columnNewsHeatService.getNewsHeatList(contentPageVO);
                    for (BaseContentEO baseContentEO : contentEOs) {
                        if (!StringUtils.isEmpty(baseContentEO.getRedirectLink())) {
                            baseContentEO.setLink(baseContentEO.getRedirectLink());
                        } else {
                            String link = PathUtil.getLinkPath(baseContentEO.getColumnId(), baseContentEO.getId());
                            baseContentEO.setLink(link);
                            baseContentEO.setTitle(HtmlRegexpUtil.filterHtml(baseContentEO.getTitle()));
                        }
                    }
                    map.put("hotNews", contentEOs);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            //信息公开热点--改为问答知识库中的数据
            if (!"false".equals(withPublicc)) {
                /*PublicContentQueryVO publicpagevo = new PublicContentQueryVO();
                publicpagevo.setSiteId(siteId);
                publicpagevo.setType(PublicContentEO.Type.DRIVING_PUBLIC.toString());
                publicpagevo.setPageSize(16);
                try {
                    Pagination publiccatalogs = publicContentService.getPagination(publicpagevo);
                    if (null != publiccatalogs.getData() && !publiccatalogs.getData().isEmpty()) {
                        List<PublicContentVO> pvos = (List<PublicContentVO>) publiccatalogs.getData();
                        for (PublicContentVO pvo : pvos) {
//                    String link = PathUtil.getLinkPath(pvo.getOrganId(), pvo.getId());
                            String link = "/public/content/" + pvo.getContentId();
                            pvo.setLink(link);
                        }
                        map.put("publicc", pvos);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }*/
                ContentPageVO query = new ContentPageVO();
                query.setSiteId(siteId);
                query.setTypeCode(BaseContentEO.TypeCode.knowledgeBase.toString());
                query.setColumnId(6331498L);
                query.setNoAuthority(Boolean.TRUE);
                query.setPageSize(16);
                //只取已发布的数据
                query.setIsPublish(1);
                try {
                    Pagination KnowledgeCatalogs = knowledgeBaseService.getPage(query);
                    if (null != KnowledgeCatalogs.getData() && !KnowledgeCatalogs.getData().isEmpty()) {
                        List<KnowledgeBaseVO> kbs = (List<KnowledgeBaseVO>) KnowledgeCatalogs.getData();
                        for (KnowledgeBaseVO kb : kbs) {
                            String link = "/content/article/" + kb.getContentId();
                            kb.setRedirectLink(link);
                        }
                        map.put("publicc", kbs);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        if (null != filename && !"".equals(filename)) {
            return FILE_BASE + "/" + filename;
        }

        return FILE_BASE + "/index-new";
    }

    @ResponseBody
    @RequestMapping(value = "/getHotWords")
    public Object getHotWords(Long siteId) {
        Integer topCount = 12;//显示条数，默认10条
        Integer sort = 1 ;//排序(0:降序 1:升序),默认降序
        List<KeyWordsHeatEO> list = keyWordsHeatService.getKeyWordsHeatList(siteId,topCount,sort);
        return list;
    }

    @ResponseBody
    @RequestMapping(value = "/getAnswers")
    public Object getAnswers(String keywords, Long siteId,Long tableColumnId,Long pageIndex, String tabs,Integer pageSize,Boolean isGroup,String nowIds,boolean fuzzySearch) {

        Long start = System.currentTimeMillis();

        if (null == keywords) {
            return ajaxErr("关键词不能为空");
        }

        if (null == siteId) {
            return ajaxErr("站点ID不能为空");
        }

        if (isGroup == null) {
            isGroup = false;
        }

        Context context = new Context();
        context.setSiteId(siteId);
        ContextHolder.setContext(context);
        Map<String, Object> map = new HashMap<String, Object>();
        //保存查询总个数map
        Map<String,Object> sumMap = new HashMap<String,Object>();
        boolean flag = false;

        map.put("count", new Date().getTime());

        if (!AppUtil.isEmpty(keywords) && null != siteId) {
            keywords = keywords.trim();
            //相关资讯检索
            SolrPageQueryVO vo = new SolrPageQueryVO(keywords);
            //如果pageSize不为空，则设置vo的pageSize
            if (!AppUtil.isEmpty(pageSize) && !isGroup) {
                vo.setPageSize(pageSize);
            }
            if (!AppUtil.isEmpty(pageIndex)) {
                vo.setPageIndex(pageIndex);
            }
            //是否分组
            if (isGroup != null && isGroup) {
                vo.setGroup(isGroup);
            }
            //排除的ids
            if (!AppUtil.isEmpty(nowIds)) {
                vo.setExcIds(nowIds);
            }
            vo.setSiteId(siteId);
            vo.setFuzzySearch(fuzzySearch);
            int tabcount = 0;

            //判断后台设置的类型排序，来过滤不现实的类型
            SolrUtil.resetTypeSort();
            Map<String,Integer> typeSortMap = SolrUtil.getTypeSortMap();

            //带入到前台的排序数组
            List<String> sortList = sortByComparator(typeSortMap);
            map.put("sortList",sortList);
            //如果传入tabs则，根据tabs来加载对应数据
            if (!AppUtil.isEmpty(tabs)) {
                String[] tabArray = tabs.split(",");
                for (String tab : tabArray) {
                    int sort = tab.equals("news") ? typeSortMap.get("articleNews").intValue():typeSortMap.get(tab).intValue();
                    if (sort == SolrUtil.default_sort_num.intValue()) {
                        map.put(tab, Collections.emptyList());
                        sumMap.put(tab, 0);
                        continue;
                    }
                    if ("news".equals(tab)) {
                        try {
                            vo.setTypeCode(SolrPageQueryVO.TypeCode.articleNews.toString().concat(",").concat(SolrPageQueryVO.TypeCode.pictureNews.toString())
                                    .concat(",").concat(SolrPageQueryVO.TypeCode.videoNews.toString()));
                            Long queryCount = SolrQueryHolder.queryCount(vo);
                            List<SolrIndexVO> solrIndexVOs = SolrQueryHolder.query(vo);
                            for (SolrIndexVO indexVO : solrIndexVOs) {
                                if (StringUtils.isEmpty(indexVO.getUrl()))
                                    indexVO.setUri(PathUtil.getLinkPath(indexVO.getColumnId(), Long.valueOf(indexVO.getId())));
                                else
                                    indexVO.setUri(indexVO.getUrl());
                            }
                            map.put("news", solrIndexVOs);
                            sumMap.put("news", queryCount);
                            if (null != solrIndexVOs && !solrIndexVOs.isEmpty()) {
                                flag = true;
                                tabcount++;
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    if ("workGuide".equals(tab)) {
                        try {
                            vo.setTypeCode(SolrPageQueryVO.TypeCode.workGuide.toString());
                            Long queryCount = SolrQueryHolder.queryCount(vo);
                            List<SolrIndexVO> solrIndexVOs = SolrQueryHolder.query(vo);
                            for (SolrIndexVO indexVO : solrIndexVOs) {
                                CmsWorkGuideEO eo = workGuideService.getByContentId(Long.valueOf(indexVO.getId()));
                                if (null != eo) {
                                    indexVO.setUri("/content/article/" + indexVO.getId() + "?guideId=" + eo.getId());
                                }
                            }
                            map.put("workGuide", solrIndexVOs);
                            sumMap.put("workGuide", queryCount);
                            if (null != solrIndexVOs && !solrIndexVOs.isEmpty()) {
                                flag = true;
                                tabcount++;
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    if ("guestBook".equals(tab)) {
                        try {
                            vo.setTypeCode(SolrPageQueryVO.TypeCode.guestBook.toString());
                            Long queryCount = SolrQueryHolder.queryCount(vo);
                            List<SolrIndexVO> solrIndexVOs = SolrQueryHolder.query(vo);
                            for (SolrIndexVO indexVO : solrIndexVOs) {
                                indexVO.setUri(PathUtil.getLinkPath(indexVO.getColumnId(), Long.valueOf(indexVO.getId())));
                            }
                            map.put("guestBook", solrIndexVOs);
                            sumMap.put("guestBook", queryCount);
                            if (null != solrIndexVOs && !solrIndexVOs.isEmpty()) {
                                flag = true;
                                tabcount++;
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    if ("public_content".equals(tab)) {
                        try {
                            vo.setTypeCode(SolrPageQueryVO.TypeCode.public_content.toString());
                            Long queryCount = SolrQueryHolder.queryCount(vo);
                            List<SolrIndexVO> solrIndexVOs = SolrQueryHolder.query(vo);
                            for (SolrIndexVO indexVO : solrIndexVOs) {
                                if (StringUtils.isEmpty(indexVO.getUrl()))
                                    indexVO.setUri(PathUtil.getLinkPath(indexVO.getColumnId(), Long.valueOf(indexVO.getId())));
                                else
                                    indexVO.setUri(indexVO.getUrl());
                            }
                            map.put("public_content", solrIndexVOs);
                            sumMap.put("public_content", queryCount);
                            if (null != solrIndexVOs && !solrIndexVOs.isEmpty()) {
                                flag = true;
                                tabcount++;
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    //常见问题
                    if ("knowledgeBase".equals(tab)) {
                        try {
                            vo.setTypeCode(SolrPageQueryVO.TypeCode.knowledgeBase.toString());
                            Long queryCount = SolrQueryHolder.queryCount(vo);
                            List<SolrIndexVO> solrIndexVOs = SolrQueryHolder.query(vo);
                            for (SolrIndexVO indexVO : solrIndexVOs) {
                                if (StringUtils.isEmpty(indexVO.getUrl()))
                                    indexVO.setUri(PathUtil.getLinkPath(indexVO.getColumnId(), Long.valueOf(indexVO.getId())));
                                else
                                    indexVO.setUri(indexVO.getUrl());
                            }
                            map.put("knowledgeBase", solrIndexVOs);
                            sumMap.put("knowledgeBase", queryCount);
                            if (null != solrIndexVOs && !solrIndexVOs.isEmpty()) {
                                flag = true;
                                tabcount++;
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
            map.put("numMap", sumMap);

            RobotPageVO robotvo = new RobotPageVO();
            robotvo.setSiteId(siteId);
            robotvo.setKeys("seqNum");
            robotvo.setKeyValue(keywords);
            robotvo.setIfActive("1");
            robotvo.setIfShow("1");
            LsRobotSourcesEO sourcesEO = lsRobotSourcesService.getEntity(robotvo);
            if (null != sourcesEO) {
                flag = true;
                map.put("sources", sourcesEO);
                tabcount++;
            }

            map.put("tabcount", tabcount);
        }else {
            //如果字符串为空，或者被过滤
            map.put("tabcount", 0);
            if (!AppUtil.isEmpty(tabs)) {
                String[] tabArray = tabs.split(",");
                for (String tab : tabArray) {
                    map.put(tab, Collections.emptyList());
                    sumMap.put(tab, 0);
                }
            }
            map.put("numMap", sumMap);
        }


        if (flag) {
            map.put("matchrst", "您好，帮您找到了一些相关内容，是您想要的么？如果不是，您可以重新提问。<span style=\"color: #999999\">猜您可能还想知道下面这些内容</span>");
        } else {
            map.put("matchrst", "对不起，没有找到你想要的内容，你可以重新提问。");
        }

        map.put("tableColumnId", tableColumnId);

        Long end = System.currentTimeMillis();

        logger.info("耗时：" + (end-start)/1000 + "秒");

        return getObject(map);
    }

    /**
     * 对map按照value 由小到大排序
     * @param unsortMap
     * @return
     */
    private List sortByComparator(Map unsortMap){
        List list = new LinkedList(unsortMap.entrySet());
        Collections.sort(list, new Comparator()
        {
            public int compare(Object o1, Object o2)
            {
                return ((Comparable) ((Map.Entry) (o1)).getValue())
                        .compareTo(((Map.Entry) (o2)).getValue());
            }
        });
        List sortedList = new ArrayList();
        Map<Object,Object> tempMap = new HashMap<Object, Object>();

        for (Iterator it = list.iterator(); it.hasNext();) {
            Map.Entry entry = (Map.Entry)it.next();
            String key = entry.getKey().toString();

            if (key.equals(BaseContentEO.TypeCode.articleNews.toString()) || key.equals(BaseContentEO.TypeCode.videoNews.toString())
                    || key.equals(BaseContentEO.TypeCode.pictureNews.toString())) {
                if (tempMap.get("news") == null) {
                    sortedList.add("news");
                    tempMap.put("news",entry.getValue());
                }
            }else {
                sortedList.add(entry.getKey());
            }

        }
        return sortedList;

    }

    /**
     * 根据ids 查询对应新闻的来源
     * @param ids
     * @return
     */
    @RequestMapping(value = "/getResources")
    @ResponseBody
    public Object getResources(String ids) {
        Map<String,String> resultMap = new HashMap<String, String>();
        if (!AppUtil.isEmpty(ids)) {
            String[] idArray = ids.split(",");
            for (String id : idArray) {
                BaseContentEO eo = CacheHandler.getEntity(BaseContentEO.class,id);
                if (eo == null) {
                    resultMap.put(id,"未知");
                    continue;
                }
//                if (!AppUtil.isEmpty(eo.getResources())) {
//                    resultMap.put(id,eo.getResources());
//                }else {
//                }
                Long siteId = eo.getSiteId();
                if (!AppUtil.isEmpty(siteId)) {
                    SiteMgrEO site = CacheHandler.getEntity(SiteMgrEO.class,siteId);
                    if (site != null) {
                        resultMap.put(id,site.getName());
                    }else {
                        resultMap.put(id,"未知");
                    }
                }else {
                    resultMap.put(id,"未知");
                }

            }
        }
        return resultMap;
    }

    // 内容检测
    @RequestMapping("getCheckList")
    @ResponseBody
    public Object getCheckList(String content, String flag) {
        List<Object> list = WordsSplitHolder.wordsCheck(content, flag);
        Pagination page = new Pagination();
        page.setData(list);
        return getObject(page);
    }
}
