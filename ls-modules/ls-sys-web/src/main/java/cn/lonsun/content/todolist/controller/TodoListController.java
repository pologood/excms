package cn.lonsun.content.todolist.controller;

import cn.lonsun.activemq.MessageSender;
import cn.lonsun.cache.client.CacheHandler;
import cn.lonsun.common.util.AppUtil;
import cn.lonsun.content.internal.entity.BaseContentEO;
import cn.lonsun.content.internal.service.IBaseContentService;
import cn.lonsun.content.interview.internal.entity.InterviewInfoEO;
import cn.lonsun.content.interview.internal.service.IInterviewInfoService;
import cn.lonsun.content.messageBoard.service.IMessageBoardService;
import cn.lonsun.content.vo.ContentPageVO;
import cn.lonsun.core.base.controller.BaseController;
import cn.lonsun.core.base.util.StringUtils;
import cn.lonsun.indicator.internal.entity.FunctionEO;
import cn.lonsun.indicator.internal.entity.IndicatorEO;
import cn.lonsun.message.internal.entity.MessageEnum;
import cn.lonsun.message.internal.entity.MessageStaticEO;
import cn.lonsun.net.service.entity.CmsWorkGuideEO;
import cn.lonsun.net.service.service.IWorkGuideService;
import cn.lonsun.publicInfo.internal.entity.PublicApplyEO;
import cn.lonsun.site.contentModel.vo.ContentModelParaVO;
import cn.lonsun.site.site.internal.entity.ColumnMgrEO;
import cn.lonsun.site.site.internal.service.IColumnConfigService;
import cn.lonsun.site.template.util.ResponseData;
import cn.lonsun.system.datadictionary.vo.DataDictVO;
import cn.lonsun.system.role.internal.service.ISiteRightsService;
import cn.lonsun.system.sitechart.webutil.StatDateUtil;
import cn.lonsun.util.*;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.*;

/**
 * @author gu.fei
 * @version 2015-12-28 19:21
 */
@Controller
@RequestMapping("/todolist")
public class TodoListController extends BaseController {

    private final static String FILE_BASE = "/content/todolist";

    private final static String SHOW = "articleNews,pictureNews,videoNews,messageBoard,todayPublish";

    private final static String GUEST_BOOK = "guestBook";

    private final static String MESSAGE_BOARD = "messageBoard";

    @Autowired
    private IBaseContentService baseContentService;

    @Autowired
    private IMessageBoardService messageBoardService;

    @Autowired
    private IColumnConfigService columnConfigService;

    @Autowired
    private ISiteRightsService siteRightsService;

    @Resource
    private IInterviewInfoService interviewInfoService;

    @Resource
    private IWorkGuideService workGuideService;

    @RequestMapping("/index")
    public String index(String typeCode, ModelMap map, HttpServletRequest request) {
        Integer isSuper = 0;
        if (ChuZhouMessageBoardOpenUtil.isOpen == 1) {
            isSuper = 1;
        }
        String showType = request.getParameter("showType");
        String isDividePublic = request.getParameter("isDividePublic");
        String publicColumnId = request.getParameter("publicColumnId");
        map.put("showType", showType);
        map.put("typeCode", typeCode);
        map.put("isSuper", isSuper);
        map.put("isDividePublic", isDividePublic);
        map.put("publicColumnId", publicColumnId);
        if ("publicInfo".equals(showType)) {
            return FILE_BASE + "/publicInfo";
        }else if("publishedInfo".equals(showType)){
            return FILE_BASE + "/publishedInfo";
        }
        return FILE_BASE + "/index";
    }

    @RequestMapping("/showDetail")
    public String showDetail(Long columnId, Long id, String typeCode, ModelMap map) {
        Long siteId = LoginPersonUtil.getSiteId();
        if (null != siteId) {
            map.put("site", CacheHandler.getEntity(IndicatorEO.class, siteId));
        }
        String str = null;
        ColumnMgrEO eo = CacheHandler.getEntity(ColumnMgrEO.class, columnId);
        if(null == eo) {
            eo = columnConfigService.getById(columnId);
        }

        if(null == eo) {
            return "";
        }
        if ("videoNews".equals(typeCode)) {
            str = FILE_BASE + "/video";
            map.put("baseId", id);
            map.put("pageIndex", "");
            map.put("toolbar", "hide");
        } else if ("pictureNews".equals(typeCode)) {
            str = FILE_BASE + "/picture";
            map.put("picNewsId", id);
            map.put("toolbar", "hide");
        } else if ("articleNews".equals(typeCode)) {
            str = FILE_BASE + "/article";
            map.put("articleId", id);
            map.put("toolbar", "hide");
        } else if ("guestBook".equals(typeCode)) {
            str = FILE_BASE + "/guestbook";
            map.put("columnId", columnId);
            map.put("baseContentId", id);
            map.put("siteId", siteId);
            map.put("toolbar", "hide");
        } else if ("messageBoard".equals(typeCode)) {
            str = "/content/messageBoard/message_board_modify";
            Integer recType = null;
            List<ContentModelParaVO> recList = ModelConfigUtil.getParam(columnId, LoginPersonUtil.getSiteId(), null);
            if (recList != null && recList.size() > 0) {
                recType = recList.get(0).getRecType();
            }
            map.put("recType", recType);
            Integer codeType = 1;
            List<ContentModelParaVO> codeList = ModelConfigUtil.getGuestBookType(columnId, LoginPersonUtil.getSiteId());
            if (codeList == null || codeList.size() == 0) {
                codeList = null;
                codeType = 0;
            }
            if (recList == null || recList.size() == 0) {
                recList = null;
            }
            map.put("recList", recList);
            map.put("codeList", codeList);
            map.put("codeType", codeType);
            map.put("columnId", columnId);
            map.put("baseContentId", id);
            map.put("type", 0);
            map.put("siteId", siteId);
        } else if ("interviewInfo".equals(typeCode)) {
            InterviewInfoEO interview = interviewInfoService.getInterviewInfoByContentId(id);
            str = FILE_BASE + "/interview";
            map.put("columnId", columnId);
            map.put("interviewId", interview.getInterviewId());
            map.put("siteId", siteId);
            map.put("toolbar", "hide");
        } else if ("workGuide".equals(typeCode)) {
            str = FILE_BASE + "/guide";
            Map<String,Object> params = new HashMap<String, Object>();
            params.put("contentId",id);
            CmsWorkGuideEO guide = workGuideService.getEntity(CmsWorkGuideEO.class,params);
            map.put("columnId", columnId);
            map.put("guide", JSONObject.toJSON(guide));
            map.put("siteId", siteId);
            map.put("toolbar", "hide");
            map.put("typeCode", "workGuide,sceneService");
        }
        map.put("indicatorId", eo.getIndicatorId());
        map.put("node", JSON.toJSON(eo));
        map.put("nowDate", StatDateUtil.getTodayDate());
        return str;
    }

    @RequestMapping("/meitu")
    public String meitu(String typeCode) {
        return FILE_BASE + "/pic_upload";
    }

    @ResponseBody
    @RequestMapping("/loadTodoNum")
    public Object loadTodoNum(String typeCode) {
        Long siteId = LoginPersonUtil.getSiteId();
        List<Long> ids = ColumnRightsUtil.getRCurHasColumns();
        if (ChuZhouMessageBoardOpenUtil.isOpen == 1 && typeCode.equals(BaseContentEO.TypeCode.messageBoard.toString())) {
            return ajaxOk(messageBoardService.getNoDealCount());
        } else {
            return ajaxOk(baseContentService.noAuditCount(siteId, typeCode, ids));
        }
    }

    @ResponseBody
    @RequestMapping("/loadTodoList")
    public Object loadTodoList(String shows, String isDividePublic, Long publicColumnId) {
        List<Long> ids = ColumnRightsUtil.getRCurHasColumns();
        Long siteId = LoginPersonUtil.getSiteId();
        if (LoginPersonUtil.isSiteAdmin() || LoginPersonUtil.isSuperAdmin()) {
        } else {
            List<ColumnMgrEO> columns = columnConfigService.getTree(siteId);
            List<ColumnMgrEO> myColumns = siteRightsService.getCurUserColumnOpt(columns);
            for (ColumnMgrEO myColumn : myColumns) {
                List<FunctionEO> functions = myColumn.getFunctions();
                for (FunctionEO function : functions) {
                    if ("publish".equals(function.getAction())) {
                        ids.add(myColumn.getIndicatorId());
                    }
                }
            }
        }

        //综合信息中的信息公开栏目单独拿出来
        List<Long> publicIds = new ArrayList<Long>();
        if (!AppUtil.isEmpty(isDividePublic) && isDividePublic.equals("1")) {
            if (publicColumnId != null && publicColumnId > 0L) {
                //根据栏目id获取子栏目id，没有则返回本栏目id
                Long[] childs = ColumnUtil.getQueryColumnIdByChild(
                        publicColumnId.toString(), true, BaseContentEO.TypeCode.articleNews.toString());
                if (LoginPersonUtil.isSiteAdmin() || LoginPersonUtil.isSuperAdmin()) {
                    publicIds.addAll(Arrays.asList(childs));
                } else {
                    for (int i = 0; i < childs.length; i++) {
                        if (ids.contains(childs[i])) {
                            publicIds.add(childs[i]);
                        }
                    }
                    //去除信息公开栏目id
                    ids.removeAll(publicIds);
                }
            }
        }


        List<String> _shows = new ArrayList<String>();

        if (ChuZhouMessageBoardOpenUtil.isOpen == 1) {
            shows = shows.replaceAll("guestBook", "messageBoard");
        }

        if (null != shows) {
            _shows = StringUtils.getListWithString(shows, ",");
        }
        List<TodoListVO> vos = new ArrayList<TodoListVO>();
        if (!AppUtil.isEmpty(siteId)) {
            List<String> strings = baseContentService.getExistTypeCode(siteId);
            List<DataDictVO> dictVOs = DataDictionaryUtil.getDDList("column_type");

            Long publicCount = 0L;
            TodoListVO publicVO = null;
            if (!AppUtil.isEmpty(isDividePublic) && isDividePublic.equals("1")) {
                if (publicColumnId != null && publicColumnId > 0L) {
                    publicCount = baseContentService.noAuditCount(siteId, BaseContentEO.TypeCode.articleNews.toString(), publicIds);
                    publicVO = new TodoListVO();
                    publicVO.setTypeCode("publicNews");
                    publicVO.setTypeName("综合信息-信息公开");
                    publicVO.setCount(publicCount);
                }
            }


            for (DataDictVO dictVO : dictVOs) {
                if (strings.contains(dictVO.getCode()) && _shows.contains(dictVO.getCode())) {
                    Long count = 0L;
                    if (ChuZhouMessageBoardOpenUtil.isOpen == 1 && dictVO.getCode().equals(MESSAGE_BOARD)) {
                        count = messageBoardService.getNoDealCount();
                    } else {
                        count = baseContentService.noAuditCount(siteId, dictVO.getCode(), ids);
                    }
                    TodoListVO vo = new TodoListVO();
                    if (ChuZhouMessageBoardOpenUtil.isOpen == 1 && dictVO.getCode().equals(MESSAGE_BOARD)) {
                        vo.setTypeCode(MESSAGE_BOARD);
                    } else {
                        vo.setTypeCode(dictVO.getCode());
                    }
                    vo.setTypeName(dictVO.getKey());
                    //管理员登录 文字信息数量 = 总数量-信息公开
                    if (LoginPersonUtil.isSiteAdmin() || LoginPersonUtil.isSuperAdmin()) {
                        if (dictVO.getCode().equals(BaseContentEO.TypeCode.articleNews.toString())) {
                            count = count - publicCount;
                        }
                    }
                    vo.setCount(count);
                    vos.add(vo);
                }
            }
            //信息公开放在最后
            if (publicVO != null) {
                vos.add(publicVO);
            }

            if (_shows.contains("todayPublish")) {
                Long count = baseContentService.noAuditCount(siteId, "todayPublish", ids);
                TodoListVO vo = new TodoListVO();
                vo.setTypeCode("todayPublish");
                vo.setTypeName("今日发布");
                vo.setCount(count);
                vos.add(vo);
            }
            if (_shows.contains("DRIVING_PUBLIC")) {
                Long count = baseContentService.getCountByTypeAndStatus(siteId, BaseContentEO.TypeCode.public_content.toString(), 0);
                TodoListVO vo = new TodoListVO();
                vo.setTypeCode("DRIVING_PUBLIC");
                vo.setTypeName("待发布信息公开");
                vo.setCount(count);
                vos.add(vo);
            }
            if (_shows.contains("PUBLIC_APPLY")) {
                Long count = baseContentService.getCountByTypeAndStatus(siteId, PublicApplyEO.PUBLIC_APPLY, 0);
                TodoListVO vo = new TodoListVO();
                vo.setTypeCode("PUBLIC_APPLY");
                vo.setTypeName("待发布依申请公开");
                vo.setCount(count);
                vos.add(vo);
            }
        }
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("data", vos);
        return map;
    }

    //批量发布
    @ResponseBody
    @RequestMapping("batchPublish")
    public Object batchPublish(@RequestParam(value = "ids[]", required = false) Long[] ids, Long
            siteId, @RequestParam(value = "columnIds[]", required = false) Long[] columnIds) {
        baseContentService.changePublish(new ContentPageVO(siteId, null, 2, ids, null));
        for (Long columnId : columnIds) {
            MessageSender.sendMessage(new MessageStaticEO(siteId, columnId, ids).setType(MessageEnum.PUBLISH.value()));
        }
        return ResponseData.success("发布成功!");
    }

    //批量取消发布
    @ResponseBody
    @RequestMapping("batchCancelPublish")
    public Object batchCancelPublish(@RequestParam(value = "ids[]", required = false) Long[] ids, Long
            siteId, @RequestParam(value = "columnIds[]", required = false) Long[] columnIds) {
        baseContentService.changePublish(new ContentPageVO(siteId, null, 2, ids, null));
        for (Long columnId : columnIds) {
            MessageSender.sendMessage(new MessageStaticEO(siteId, columnId, ids).setType(MessageEnum.UNPUBLISH.value()));
        }
        return ResponseData.success("取消发布成功!");
    }

    //删除
    @ResponseBody
    @RequestMapping("batchDelete")
    public Object batchDelete(@RequestParam("ids[]") Long[] ids) {
        baseContentService.delContent(ids);
        return ResponseData.success("删除成功!");
    }
}
