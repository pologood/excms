package cn.lonsun.solr;

import cn.lonsun.activemq.MessageSenderUtil;
import cn.lonsun.cache.client.CacheGroup;
import cn.lonsun.cache.client.CacheHandler;
import cn.lonsun.core.base.controller.BaseController;
import cn.lonsun.core.base.entity.AMockEntity;
import cn.lonsun.core.base.util.StringUtils;
import cn.lonsun.core.util.Pagination;
import cn.lonsun.indicator.internal.entity.IndicatorEO;
import cn.lonsun.publicInfo.internal.entity.PublicContentEO;
import cn.lonsun.publicInfo.internal.service.impl.PublicContentServiceImpl;
import cn.lonsun.publicInfo.vo.PublicContentVO;
import cn.lonsun.solrManage.IndexUtil;
import cn.lonsun.system.role.internal.service.ISiteRightsService;
import cn.lonsun.util.LoginPersonUtil;
import org.apache.solr.client.solrj.SolrServerException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static cn.lonsun.cache.client.CacheHandler.getEntity;

/**
 * @author gu.fei
 * @version 2016-09-06 16:01
 */
@Controller
@RequestMapping(value = "solr")
public class SolrController extends BaseController {

    private static final String FILE_BASE = "/solr/";

    @Resource
    private PublicContentServiceImpl publicContentService;

    @Resource
    private ISiteRightsService siteRightsService;

    /**
     * 主页
     * @return
     */
    @RequestMapping(value = "index")
    public String index() {
        return FILE_BASE + "index";
    }

    /**
     * 获取配置信息
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "getSiteSolrInfo")
    public Object getSiteControl() {
        // 获取站点列表
        List<IndicatorEO> siteList = getCurUserSiteList();
        return getObject(siteList);
    }

    /**
     * 重启全站索引
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/restart")
    public Object restart() {
        try {
            SolrFactory.syncIndexFromDb();
        } catch (SolrServerException e) {
            e.printStackTrace();
            return ajaxErr("重启索引异常：" + e.getMessage());
        } catch (IOException e) {
            e.printStackTrace();
            return ajaxErr("重启索引异常：" + e.getMessage());
        }
        return ajaxOk("重启索引成功，正在建立索引！");
    }

    /**
     * 重启索引
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/start")
    public Object start(Integer type,Long siteId) {
        if(LoginPersonUtil.isRoot()) {
            switch (type) {
                case 1 : //新闻
                    IndexUtil.createArticleNewsIndex(siteId);
                    IndexUtil.createPictureNewsIndex(siteId);
                    IndexUtil.createVideoNewsIndex(siteId);
                    break;
                case 2://信息公开
                    IndexUtil.createPublicInfoIndex(siteId);
                    break;
                case 3://网上办事
                    IndexUtil.createWorkGuidesIndex(siteId);
                    break;
                case 4://留言
                    IndexUtil.createGuestsIndex(siteId);
                    break;
                case 5://调查管理
                    IndexUtil.createSurveysIndex(siteId);
                    break;
                case 6://网上调查
                    IndexUtil.createReviewsIndex(siteId);
                    break;
                case 7://在线访谈
                    IndexUtil.createInterviewsIndex(siteId);
                    break;
                case 8://民意征集
                    IndexUtil.createCollectInfosIndex(siteId);
                    break;
                case 9://领导之窗
                    IndexUtil.createLeaderInfosIndex(siteId);
                    break;
                case 10://问答知识库
                    IndexUtil.createKnowledgeBaseIndex(siteId);
                    break;
                default: //所有
                    IndexUtil.createAllIndex(siteId);
                    break;
            }
            return ajaxOk("重启索引成功，正在建立索引！");
        }
        return ajaxErr("无权限");
    }

    @ResponseBody
    @RequestMapping(value = "/create")
    public Object create(String contentIds, Long columnId) {
        try {
            MessageSenderUtil.createOrUpdateIndex(StringUtils.getArrayWithLong(contentIds, ","), columnId);
        } catch (SolrServerException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return ajaxOk("创建索引成功！");
    }

    @ResponseBody
    @RequestMapping(value = "/delete")
    public Object delete(String id) {
        if (null == id) {
            return ajaxErr("id参数不能为空!");
        }
        try {
            SolrFactory.deleteIndexSyn(id);
        } catch (SolrServerException e) {
            e.printStackTrace();
            return ajaxErr("删除索引异常：" + e.getMessage());
        } catch (IOException e) {
            e.printStackTrace();
            return ajaxErr("删除索引异常：" + e.getMessage());
        }
        return ajaxOk("删除索引成功！");
    }

    @ResponseBody
    @RequestMapping(value = "/createBySiteId")
    public Object delete(Long siteId) {
        if (null == siteId) {
            return ajaxErr("siteId参数不能为空!");
        }
        StringBuffer hql = new StringBuffer();
        hql.append(" select b.title as title,b.createDate as createDate,b.publishDate as publishDate,b.siteId as siteId,b.isPublish as isPublish,p.catId as catId,b.author as author,b.isTop as isTop, ");
        hql.append(" p.sortNum as sortNum,p.parentClassIds as parentClassIds,p.classIds as classIds,b.isPublish as isPublish,b.redirectLink as redirectLink,p.effectiveDate as effectiveDate,p.repealDate as repealDate, ");
        hql.append(" p.contentId as contentId,p.synColumnIds as synColumnIds,p.synOrganCatIds as synOrganCatIds,p.synMsgCatIds as synMsgCatIds,p.organId as organId,b.resources as resources, ");
        hql.append(" p.classIds as classIds,p.indexNum as indexNum,p.fileNum as fileNum,p.keyWords as keyWords,p.summarize as summarize,p.id as id,p.type as type, ");
        hql.append(" b.attachSavedName as attachSavedName,b.attachRealName as attachRealName,b.attachSize as attachSize ");
        hql.append(" from PublicContentEO as p,BaseContentEO as b where p.contentId = b.id ");

        try {
            // 分页查询建索引，每次5000;
            Long pageIndex = 0L;
            Integer pageSize = 5000;
            hql.append(" and p.type = '").append(PublicContentEO.Type.DRIVING_PUBLIC.toString());
            hql.append("' and p.recordStatus = ? and b.recordStatus = ? and b.isPublish = 1 and p.siteId = ? ");
            // 先查询一次
            Object[] values = new Object[]{AMockEntity.RecordStatus.Normal.toString(), AMockEntity.RecordStatus.Normal.toString(), siteId};
            Pagination pagination = publicContentService.getMockDao().getPagination(pageIndex, pageSize, hql.toString(), values, PublicContentVO.class);
            if (null != pagination) {
                // 创建索引
                publicContentService.createPublicIndex(pagination.getData());
                Long pageCount = pagination.getPageCount();
                while (pageIndex < pageCount) {// 继续分页查询
                    pagination = publicContentService.getMockDao().getPagination(pageIndex, pageSize, hql.toString(), values, PublicContentVO.class);
                    if (null != pagination) {
                        publicContentService.createPublicIndex(pagination.getData());
                    }
                }
            }
        } catch (Throwable e) {
            e.printStackTrace();
            return ajaxErr("创建索引异常：" + e.getMessage());
        }
        return ajaxOk("创建索引成功！");
    }

    /**
     * 获取当前用户站点列表
     *
     * @return
     * @author fangtinghua
     */
    private List<IndicatorEO> getCurUserSiteList() {
        List<IndicatorEO> siteList = new ArrayList<IndicatorEO>();
        if (LoginPersonUtil.isSuperAdmin() || LoginPersonUtil.isRoot()) {// 超级管理员获取所有站点
            List<IndicatorEO> list = CacheHandler.getList(IndicatorEO.class, CacheGroup.CMS_TYPE, IndicatorEO.Type.CMS_Site.toString());
            if (null != list && !list.isEmpty()) {
                siteList.addAll(list);
            }
            list = CacheHandler.getList(IndicatorEO.class, CacheGroup.CMS_TYPE, IndicatorEO.Type.SUB_Site.toString());
            if (null != list && !list.isEmpty()) {
                siteList.addAll(list);
            }
        } else {
            Long[] siteIds = siteRightsService.getCurUserSiteIds(true);
            if (null != siteIds && siteIds.length > 0) {
                for (Long id : siteIds) {
                    IndicatorEO indicatorEO = getEntity(IndicatorEO.class, id);
                    if (indicatorEO != null) {
                        siteList.add(indicatorEO);
                    }
                }
            }
        }
        return siteList;
    }
}