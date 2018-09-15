package cn.lonsun.staticcenter.controller;

import cn.lonsun.common.util.AppUtil;
import cn.lonsun.content.internal.entity.BaseContentEO;
import cn.lonsun.content.vo.ContentPageVO;
import cn.lonsun.core.base.controller.BaseController;
import cn.lonsun.core.base.util.StringUtils;
import cn.lonsun.core.util.Pagination;
import cn.lonsun.heatAnalysis.service.IColumnNewsHeatService;
import cn.lonsun.lsrobot.entity.LsRobotParamEO;
import cn.lonsun.lsrobot.entity.LsRobotSourcesEO;
import cn.lonsun.lsrobot.service.ILsRobotParamService;
import cn.lonsun.lsrobot.service.ILsRobotSourcesService;
import cn.lonsun.lsrobot.vo.RobotPageVO;
import cn.lonsun.net.service.entity.CmsWorkGuideEO;
import cn.lonsun.net.service.service.IWorkGuideService;
import cn.lonsun.publicInfo.internal.entity.PublicContentEO;
import cn.lonsun.publicInfo.internal.service.IPublicContentService;
import cn.lonsun.publicInfo.vo.PublicContentQueryVO;
import cn.lonsun.publicInfo.vo.PublicContentVO;
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
 * Created by zhushouyong on 2017-11-3.
 */
@Controller
@RequestMapping(value = "/search/smart")
public class SmartSearchController extends BaseController {

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

    @RequestMapping(value = "/index")
    public String index(Long siteId, Long tableColumnId, ModelMap map, String filename) {
        if (null != siteId) {
            map.put("siteId", siteId);
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

            //热点新闻
            ContentPageVO contentPageVO = new ContentPageVO();
            contentPageVO.setSiteId(siteId);
            contentPageVO.setPageSize(16);
            String[] typeCodes =
                    {BaseContentEO.TypeCode.articleNews.toString(), BaseContentEO.TypeCode.pictureNews.toString(), BaseContentEO.TypeCode.videoNews.toString()};
            contentPageVO.setTypeCodes(typeCodes);

            try {
                Pagination news = columnNewsHeatService.getNewsHeatPage(contentPageVO);
                Context context = new Context();
                context.setSiteId(siteId);
                ContextHolder.setContext(context);
                if (null != news.getData() && !news.getData().isEmpty()) {
                    List<BaseContentEO> contentEOs = (List<BaseContentEO>) news.getData();
                    for (BaseContentEO baseContentEO : contentEOs) {
                        if (null != baseContentEO.getRedirectLink()) {
                            baseContentEO.setLink(baseContentEO.getRedirectLink());
                        } else {
                            String link = PathUtil.getLinkPath(baseContentEO.getColumnId(), baseContentEO.getId());
                            baseContentEO.setLink(link);
                        }
                    }
                    map.put("hotNews", contentEOs);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            //信息公开热点
            PublicContentQueryVO publicpagevo = new PublicContentQueryVO();
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
            }
        }

        if (null != filename && !"".equals(filename)) {
            return FILE_BASE + "/" + filename;
        }

        return FILE_BASE + "/index";
    }

    @ResponseBody
    @RequestMapping(value = "/getAnswers")
    public Object getAnswers(String keywords, Long siteId, Long tableColumnId) {

        if (null == keywords) {
            return ajaxErr("关键词不能为空");
        }

        if (null == siteId) {
            return ajaxErr("站点ID不能为空");
        }

        Context context = new Context();
        context.setSiteId(siteId);
        ContextHolder.setContext(context);
        Map<String, Object> map = new HashMap<String, Object>();
        boolean flag = false;
        if (!AppUtil.isEmpty(keywords) && null != siteId) {
            keywords = keywords.trim();
            //相关资讯检索
            SolrPageQueryVO vo = new SolrPageQueryVO(keywords);
            vo.setSiteId(siteId);
            map.put("count", new Date().getTime());
            int tabcount = 0;
            try {
                vo.setTypeCode(SolrPageQueryVO.TypeCode.articleNews.toString().concat(",").concat(SolrPageQueryVO.TypeCode.pictureNews.toString())
                        .concat(",").concat(SolrPageQueryVO.TypeCode.videoNews.toString()));
                List<SolrIndexVO> solrIndexVOs = SolrQueryHolder.query(vo);
                for (SolrIndexVO indexVO : solrIndexVOs) {
                    indexVO.setUri(PathUtil.getLinkPath(indexVO.getColumnId(), Long.valueOf(indexVO.getId())));
                }
                map.put("news", solrIndexVOs);
                if (null != solrIndexVOs && !solrIndexVOs.isEmpty()) {
                    flag = true;
                    tabcount++;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            try {
                vo.setTypeCode(SolrPageQueryVO.TypeCode.workGuide.toString());
                List<SolrIndexVO> solrIndexVOs = SolrQueryHolder.query(vo);
                for (SolrIndexVO indexVO : solrIndexVOs) {
                    CmsWorkGuideEO eo = workGuideService.getByContentId(Long.valueOf(indexVO.getId()));
                    if (null != eo) {
                        indexVO.setUri("/content/article/" + indexVO.getId() + "?guideId=" + eo.getId());
                    }
                }
                map.put("workGuide", solrIndexVOs);
                if (null != solrIndexVOs && !solrIndexVOs.isEmpty()) {
                    flag = true;
                    tabcount++;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            try {
                vo.setTypeCode(SolrPageQueryVO.TypeCode.guestBook.toString());
                List<SolrIndexVO> solrIndexVOs = SolrQueryHolder.query(vo);
                for (SolrIndexVO indexVO : solrIndexVOs) {
                    indexVO.setUri(PathUtil.getLinkPath(indexVO.getColumnId(), Long.valueOf(indexVO.getId())));
                }
                map.put("guestBook", solrIndexVOs);
                if (null != solrIndexVOs && !solrIndexVOs.isEmpty()) {
                    flag = true;
                    tabcount++;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            try {
                vo.setTypeCode(SolrPageQueryVO.TypeCode.public_content.toString());
                List<SolrIndexVO> solrIndexVOs = SolrQueryHolder.query(vo);
                for (SolrIndexVO indexVO : solrIndexVOs) {
                    indexVO.setUri(PathUtil.getLinkPath(indexVO.getColumnId(), Long.valueOf(indexVO.getId())));
                }
                map.put("public_content", solrIndexVOs);
                if (null != solrIndexVOs && !solrIndexVOs.isEmpty()) {
                    flag = true;
                    tabcount++;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

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
        }
        if (flag) {
            map.put("matchrst", "您好，帮您找到了一些相关内容，是您想要的么？如果不是，您可以重新提问。<span style=\"color: #999999\">猜您可能还想知道下面这些内容</span>");
        } else {
            map.put("matchrst", "对不起，没有找到你想要的内容，你可以重新提问。");
        }
        map.put("tableColumnId", tableColumnId);
        return getObject(map);
    }

    /**
     * 获取搜索提示
     *
     * @param keywords
     * @param siteId
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/getSearchTips")
    public Object getSearchTips(String keywords, Long siteId,boolean fuzzySearch) {

        List<Map<String, Object>> tipList = new ArrayList<Map<String, Object>>();
        if (AppUtil.isEmpty(keywords) || null == siteId) {
            return tipList;
        }
        keywords = keywords.trim();
        Context context = new Context();
        context.setSiteId(siteId);
        context.setLinkType(false);
        ContextHolder.setContext(context);
        SolrPageQueryVO vo = new SolrPageQueryVO(keywords);
        vo.setSiteId(siteId);
        vo.setFuzzySearch(fuzzySearch);

        //判断后台设置的类型排序，来过滤不现实的类型
        SolrUtil.resetTypeSort();
        Map<String,Integer> typeSortMap = SolrUtil.getTypeSortMap();

        StringBuffer querySb = new StringBuffer();
        if (typeSortMap.get("articleNews").intValue() != SolrUtil.default_sort_num.intValue()) {
            querySb.append(SolrPageQueryVO.TypeCode.articleNews.toString()).append(",")
                .append(SolrPageQueryVO.TypeCode.pictureNews.toString()).append(",")
                .append(SolrPageQueryVO.TypeCode.videoNews.toString()).append(",");
        }
        if (typeSortMap.get("workGuide").intValue() != SolrUtil.default_sort_num.intValue()) {
            querySb.append(SolrPageQueryVO.TypeCode.workGuide.toString()).append(",");
        }
        if (typeSortMap.get("guestBook").intValue() != SolrUtil.default_sort_num.intValue()) {
            querySb.append(SolrPageQueryVO.TypeCode.guestBook.toString()).append(",");
        }
        if (typeSortMap.get("public_content").intValue() != SolrUtil.default_sort_num.intValue()) {
            querySb.append(SolrPageQueryVO.TypeCode.public_content.toString()).append(",");
        }
        if (typeSortMap.get("knowledgeBase").intValue() != SolrUtil.default_sort_num.intValue()) {
            querySb.append(SolrPageQueryVO.TypeCode.knowledgeBase.toString()).append(",");
        }


        try {
            vo.setTypeCode(querySb.substring(0,querySb.length()-1));
            //这里只需要放一起查询即可，两次查询会出重复数据
            //vo.setFromCode("title,content");
            List<SolrIndexVO> solrIndexVOs = SolrQueryHolder.query(vo);
//            if (null == solrIndexVOs || solrIndexVOs.size() < vo.getPageSize()) {
//                vo.setFromCode("content");
//                vo.setPageSize(vo.getPageSize() - solrIndexVOs.size());
//                List<SolrIndexVO> solrIndexVOsContent = SolrQueryHolder.query(vo);
//                if (null != solrIndexVOsContent && !solrIndexVOsContent.isEmpty()) {
//                    solrIndexVOs.addAll(solrIndexVOsContent);
//                }
//            }

            Map<String, Object> resultEleMap = null;
            for (SolrIndexVO indexVO : solrIndexVOs) {
                resultEleMap = new HashMap<String, Object>();
                resultEleMap.put("title", indexVO.getTitle());
                resultEleMap.put("typeCode", indexVO.getTypeCode());
                if (SolrPageQueryVO.TypeCode.articleNews.toString().equals(indexVO.getTypeCode())
                        || SolrPageQueryVO.TypeCode.pictureNews.toString().equals(indexVO.getTypeCode())
                        || SolrPageQueryVO.TypeCode.videoNews.toString().equals(indexVO.getTypeCode())
                        || SolrPageQueryVO.TypeCode.guestBook.toString().equals(indexVO.getTypeCode())
                        || SolrPageQueryVO.TypeCode.knowledgeBase.toString().equals(indexVO.getTypeCode())) {
                    context.setModule("");
                    resultEleMap.put("uri", PathUtil.getLinkPath(indexVO.getColumnId(), Long.valueOf(indexVO.getId())));
                }
                if (SolrPageQueryVO.TypeCode.workGuide.toString().equals(indexVO.getTypeCode())) {
                    CmsWorkGuideEO eo = workGuideService.getByContentId(Long.valueOf(indexVO.getId()));
                    if (null != eo) {
                        resultEleMap.put("uri", "/content/article/" + indexVO.getId() + "?guideId=" + eo.getId());
                    }
                }
                if (SolrPageQueryVO.TypeCode.public_content.toString().equals(indexVO.getTypeCode())) {
                    context.setModule("public");
                    resultEleMap.put("uri", PathUtil.getLinkPath(indexVO.getColumnId(), Long.valueOf(indexVO.getId())));
                }
                if (!StringUtils.isEmpty(indexVO.getUrl()))
                    resultEleMap.put("uri", indexVO.getUrl());
                tipList.add(resultEleMap);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return getObject(tipList);
    }
}
