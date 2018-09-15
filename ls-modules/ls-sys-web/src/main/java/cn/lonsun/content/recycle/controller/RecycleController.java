package cn.lonsun.content.recycle.controller;

import cn.lonsun.cache.client.CacheHandler;
import cn.lonsun.content.internal.entity.BaseContentEO;
import cn.lonsun.content.internal.entity.KnowledgeBaseEO;
import cn.lonsun.content.internal.service.IBaseContentService;
import cn.lonsun.content.internal.service.IGuestBookService;
import cn.lonsun.content.internal.service.IKnowledgeBaseService;
import cn.lonsun.content.leaderwin.internal.entity.LeaderInfoEO;
import cn.lonsun.content.leaderwin.internal.service.ILeaderInfoService;
import cn.lonsun.content.messageBoard.controller.vo.MessageBoardEditVO;
import cn.lonsun.content.messageBoard.service.IMessageBoardService;
import cn.lonsun.content.vo.UnAuditContentsVO;
import cn.lonsun.core.base.controller.BaseController;
import cn.lonsun.core.exception.BaseRunTimeException;
import cn.lonsun.core.util.Pagination;
import cn.lonsun.core.util.TipsMode;
import cn.lonsun.indicator.internal.entity.FunctionEO;
import cn.lonsun.indicator.internal.entity.IndicatorEO;
import cn.lonsun.indicator.internal.service.IIndicatorService;
import cn.lonsun.site.site.internal.entity.ColumnMgrEO;
import cn.lonsun.site.site.internal.service.IColumnConfigService;
import cn.lonsun.site.template.util.ResponseData;
import cn.lonsun.system.role.internal.service.ISiteRightsService;
import cn.lonsun.util.LoginPersonUtil;
import com.alibaba.fastjson.JSONArray;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author gu.fei
 * @version 2015-12-29 14:30
 */
@Controller
@RequestMapping("/recycle")
public class RecycleController extends BaseController {

    private final static String FILE_BASE = "/content/recycle";

    @Autowired
    private ISiteRightsService siteRightsService;

    @Autowired
    private IColumnConfigService columnConfigService;

    @Autowired
    private IBaseContentService baseContentService;

    @Autowired
    private IGuestBookService guestBookService;

    @Autowired
    private ILeaderInfoService leaderInfoService;

    @Autowired
    private IMessageBoardService messageBoardService;

    @Autowired
    private IKnowledgeBaseService knowledgeBaseService;

    @Autowired IIndicatorService indicatorService;

    @RequestMapping("/index")
    public ModelAndView index() {
        ModelAndView model = new ModelAndView(FILE_BASE + "/index");
        model.addObject("toolbar", false);
        return model;
    }

    @RequestMapping("/showDetail")
    public String showDetail(Long columnId, Long id, String typeCode,ModelMap map) {
        Long siteId =LoginPersonUtil.getSiteId();
        if (null != siteId) {
            map.put("site", CacheHandler.getEntity(IndicatorEO.class, siteId));
        }
        String str = null;
        IndicatorEO eo = indicatorService.getEntity(IndicatorEO.class, columnId);
        if ("videoNews".equals(typeCode)) {
            str = FILE_BASE + "/video";
            map.put("baseId", id);
            map.put("pageIndex", "");
        } else if ("pictureNews".equals(typeCode)) {
            str = FILE_BASE + "/picture";
            map.put("picNewsId", id);
        } else if ("articleNews".equals(typeCode)) {
            str = FILE_BASE + "/article";
            map.put("articleId", id);
        } else if("guestBook".equals(typeCode)){
            str = FILE_BASE + "/guestbook";
            map.put("columnId", columnId);
            map.put("baseContentId", id);
            map.put("siteId", siteId);
        } else if("messageBoard".equals(typeCode)){
            str = FILE_BASE + "/messageBoard";
            map.put("columnId", columnId);
            map.put("baseContentId", id);
            map.put("siteId", siteId);
            MessageBoardEditVO vo =  messageBoardService.queryRemoved(id);
            map.put("vo",vo);
        } else if("leaderInfo".equals(typeCode)){
            Map<String,Object> queryMap = new HashMap<String, Object>();
            queryMap.put("contentId",id);
            LeaderInfoEO leader = leaderInfoService.getEntity(LeaderInfoEO.class,queryMap);
            str = FILE_BASE + "/leaderInfo";
            map.put("columnId", columnId);
            map.put("infoId", leader.getLeaderInfoId());
            map.put("siteId", siteId);
        } else if("knowledgeBase".equals(typeCode)){
            Map<String,Object> queryMap = new HashMap<String, Object>();
            queryMap.put("contentId",id);
            KnowledgeBaseEO knowledgeBase = knowledgeBaseService.getEntity(KnowledgeBaseEO.class,queryMap);
            str = FILE_BASE + "/knowledgeBase";
            map.put("columnId", columnId);
            map.put("knowledgeBaseId", knowledgeBase.getKnowledgeBaseId());
            map.put("siteId", siteId);
        }
        if(eo!=null){
            map.put("indicatorId", eo.getIndicatorId());
        }
        map.put("node", JSONArray.toJSON(eo));
        map.put("toolbar","hide");
        map.put("uploadBar", "hide");
        map.put("recordStatus", "Removed");
        return str;
    }

    //回收站列表查询
    @ResponseBody
    @RequestMapping("getPageRecycle")
    public Object getPageRecycle(UnAuditContentsVO uaVO, HttpServletRequest request) {
        if (LoginPersonUtil.isSiteAdmin() || LoginPersonUtil.isSuperAdmin()) {
            uaVO.setSiteId((Long) request.getSession().getAttribute("siteId"));
            uaVO.setColumnIds(null);
        } else {
            List<Long> ids = new ArrayList<Long>();
            uaVO.setSiteId((Long) request.getSession().getAttribute("siteId"));
            List<ColumnMgrEO> columns = columnConfigService.getTree((Long) request.getSession().getAttribute("siteId"));
            List<ColumnMgrEO> myColumns = siteRightsService.getCurUserColumnOpt(columns);
            for (ColumnMgrEO myColumn : myColumns) {
                List<FunctionEO> functions = myColumn.getFunctions();
                for (FunctionEO function : functions) {
                    if ("publish".equals(function.getAction())) {
                        ids.add(myColumn.getIndicatorId());
                    }
                }
            }
            uaVO.setColumnIds(ids.toArray(new Long[ids.size()]));
        }
        //获取回收站内容分页
        Pagination page = baseContentService.getRecycleContentPage(uaVO);
        List<BaseContentEO> list = (List<BaseContentEO>) page.getData();
       // List<BaseContentEO> removeList = new ArrayList<BaseContentEO>();
        for (BaseContentEO eo : list) {
            IndicatorEO column = CacheHandler.getEntity(IndicatorEO.class, eo.getColumnId());
            if(column==null){
                //removeList.add(eo);
                eo.setColumnName("该栏目已被删除");
            }else if (eo.getColumnId() != null && column != null) {
                eo.setColumnName(column.getName());
            }
        }

        //已删除栏目的数据不在回收站显示
        //list.removeAll(removeList);
        return page;
    }

    @ResponseBody
    @RequestMapping("/batchRealDelete")
    public Object batchRealDelete(@RequestParam(value="articleIds[]",required=false)Long[] articleIds,
                                  @RequestParam(value="pictureIds[]",required=false)Long[] pictureIds,
                                  @RequestParam(value="videoIds[]",required=false)Long[] videoIds,
                                  @RequestParam(value="guestBookIds[]",required=false)Long[] guestBookIds,
                                  @RequestParam(value="messageBoardIds[]",required=false)Long[] messageBoardIds,
                                  @RequestParam(value="leaderInfoIds[]",required=false)Long[] leaderInfoIds,
                                  @RequestParam(value="knowledgeIds[]",required=false)Long[] knowledgeIds) {

        if(null != articleIds && articleIds.length > 0) {
//            baseContentService.removeBaseContent(articleIds);
            baseContentService.removeArticleNews(articleIds);

        }
        if(null != pictureIds && pictureIds.length > 0) {
           // baseContentService.removeBaseContent(pictureIds);
            baseContentService.removePictrueNews(pictureIds);
        }
        if(null != videoIds && videoIds.length > 0) {
            baseContentService.removeBaseContent(videoIds);
            baseContentService.removeVideoNews(videoIds);
        }
        if(null != guestBookIds && guestBookIds.length > 0) {
            baseContentService.removeBaseContent(guestBookIds);
            guestBookService.batchCompletelyDelete(guestBookIds);
        }
        if(null != messageBoardIds && messageBoardIds.length > 0) {
            baseContentService.removeBaseContent(messageBoardIds);
            messageBoardService.batchCompletelyDelete(messageBoardIds);
        }
        if(null != leaderInfoIds && leaderInfoIds.length > 0) {
            baseContentService.removeBaseContent(leaderInfoIds);
            leaderInfoService.batchCompletelyDelete(leaderInfoIds);
        }
        if (null != knowledgeIds && knowledgeIds.length > 0) {
            baseContentService.removeBaseContent(knowledgeIds);
            knowledgeBaseService.batchCompletelyDelete(knowledgeIds);
        }

        return ResponseData.success("删除成功!");
    }

    @ResponseBody
    @RequestMapping("/batchRecovery")
    public Object batchRecovery(@RequestParam(value="ids[]",required=false)Long[] ids) {
        if(ids==null||ids.length==0){
            throw new BaseRunTimeException(TipsMode.Message.toString(),"请至少选择一项操作,不包含栏目已被删除的内容");
        }
        baseContentService.recovery(ids);
        return ResponseData.success("恢复成功!");
    }
}
