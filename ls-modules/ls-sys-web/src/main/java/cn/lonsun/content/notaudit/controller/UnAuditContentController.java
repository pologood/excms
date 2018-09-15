package cn.lonsun.content.notaudit.controller;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import cn.lonsun.content.vo.ContentPageVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import cn.lonsun.activemq.MessageSender;
import cn.lonsun.cache.client.CacheHandler;
import cn.lonsun.common.util.AppUtil;
import cn.lonsun.content.internal.entity.BaseContentEO;
import cn.lonsun.content.internal.entity.GuestBookEO;
import cn.lonsun.content.internal.service.IBaseContentService;
import cn.lonsun.content.internal.service.IContentPicService;
import cn.lonsun.content.internal.service.IGuestBookService;
import cn.lonsun.content.internal.service.IVideoNewsService;
import cn.lonsun.core.base.controller.BaseController;
import cn.lonsun.message.internal.entity.MessageEnum;
import cn.lonsun.message.internal.entity.MessageStaticEO;
import cn.lonsun.mongodb.service.ContentMongoServiceImpl;
import cn.lonsun.site.site.internal.entity.ColumnMgrEO;
import cn.lonsun.site.site.internal.service.IColumnConfigService;
import cn.lonsun.system.role.internal.service.ISiteRightsService;
import cn.lonsun.util.ColumnRightsUtil;


@Controller
@RequestMapping(value = "unAuditContent")
public class UnAuditContentController extends BaseController {
    @Autowired
    private IVideoNewsService videoService;

    @Autowired
    private ISiteRightsService siteRightsService;
    @Autowired
    private IColumnConfigService columnConfigService;

    @Autowired
    private ContentMongoServiceImpl contentMongoService;
    @Autowired
    private IGuestBookService guestBookService;
    @Autowired
    private IContentPicService contentPicService;

    @Autowired
    private IBaseContentService baseContentService;

	/*@RequestMapping("auditCount")
    @ResponseBody
	public Map unAuditCount(Long siteId){
		Map map = new HashMap<String, Long>();
		map.put("videoNews",baseContentService.noAuditCount(siteId, "videoNews"));
		map.put("onlinePetition",baseContentService.noAuditCount(siteId, "onlinePetition"));
		map.put("pictureNews",baseContentService.noAuditCount(siteId, "pictureNews"));
		map.put("articleNews",baseContentService.noAuditCount(siteId, "articleNews"));
		map.put("guestBook",baseContentService.noAuditCount(siteId, "guestBook"));
		return map;
	}*/

    //进入待审首页
    @RequestMapping("index")
    public String index(HttpServletRequest request, ModelMap map) {
        List<Long> ids = ColumnRightsUtil.getRCurHasColumns();
        Long siteId = (Long) request.getSession().getAttribute("siteId");
        map.put("videoNews", baseContentService.noAuditCount(siteId, "videoNews", ids));
        map.put("onlinePetition", baseContentService.noAuditCount(siteId, "onlinePetition", ids));
        map.put("pictureNews", baseContentService.noAuditCount(siteId, "pictureNews", ids));
        map.put("articleNews", baseContentService.noAuditCount(siteId, "articleNews", ids));
        map.put("guestBook", baseContentService.noAuditCount(siteId, "guestBook", ids));
        return "/content/noaudit/unaudit_content";
    }

    //改变发布状态
    @RequestMapping("publish")
    @ResponseBody
    public Object changePublish(Long id) {
        BaseContentEO _bc = baseContentService.getEntity(BaseContentEO.class, id);
        Integer isPublish = _bc.getIsPublish();
        boolean msg;
        if (isPublish == 1) {
            baseContentService.changePublish(new ContentPageVO(null, null, 0, new Long[]{id}, null));
            msg = MessageSender.sendMessage(new MessageStaticEO(_bc.getSiteId(), _bc.getColumnId(), new Long[]{id}).setType(MessageEnum.UNPUBLISH.value()));
        } else {
            baseContentService.changePublish(new ContentPageVO(null, null, 1, new Long[]{id}, null));
            msg = MessageSender.sendMessage(new MessageStaticEO(_bc.getSiteId(), _bc.getColumnId(), new Long[]{id}).setType(MessageEnum.PUBLISH.value()));
        }
        //LoginPersonUtil.getSession().getAttribute("siteId")
        return getObject(isPublish);
    }

    //删除
    @RequestMapping("delete")
    @ResponseBody
    public Object delete(@RequestParam("ids[]") Long[] ids) {
        baseContentService.delContent(ids);
        return getObject();
    }

    //批量发布
    @RequestMapping("publishs")
    @ResponseBody
    public Object publish(@RequestParam(value = "ids[]", required = false) Long[] ids, Long siteId, Long columnId) {
        baseContentService.changePublish(new ContentPageVO(siteId, columnId, 1, ids, null));
        boolean rel = MessageSender.sendMessage(new MessageStaticEO(siteId, columnId, ids).setType(MessageEnum.PUBLISH.value()));
        return getObject(rel);

    }

    //待审内容详情
    @RequestMapping("details")
    public ModelAndView details(Long id, String typeCode, Long pageIndex, Long columnId) {
        if ("guestBook".equals(typeCode)) {
            BaseContentEO eo = baseContentService.getEntity(BaseContentEO.class, id);
            GuestBookEO geo = guestBookService.noAuditGuestBook(id);
            ModelAndView mv = new ModelAndView("/content/noaudit/guestbook_details");
            mv.addObject("typeCode", typeCode);
            mv.addObject("eo", eo);
            mv.addObject("geo", geo);
            mv.addObject("pageIndex", pageIndex);
            mv.addObject("id", id);
            return mv;
        } else if ("articleNews".equals(typeCode)) {
            ModelAndView mv = new ModelAndView("/content/noaudit/article_news_details");
            ColumnMgrEO eo = CacheHandler.getEntity(ColumnMgrEO.class, columnId);
            SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String str1 = sdf1.format(new Date());
            mv.addObject("eo", eo);
            mv.addObject("columnId", columnId);
            mv.addObject("articleId", id);
            mv.addObject("nowDate", str1);
            mv.addObject("pageIndex", pageIndex);
            return mv;
        } else if ("pictureNews".equals(typeCode)) {
            ModelAndView mv = new ModelAndView("/content/noaudit/article_news_details");
            ColumnMgrEO eo = CacheHandler.getEntity(ColumnMgrEO.class, columnId);
            SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String str1 = sdf1.format(new Date());
            mv.addObject("eo", eo);
            mv.addObject("columnId", columnId);
            mv.addObject("articleId", id);
            mv.addObject("nowDate", str1);
            mv.addObject("pageIndex", pageIndex);
            return mv;
        } else if ("videoNews".equals(typeCode)) {
            ModelAndView mv = new ModelAndView("/content/noaudit/video_news_details");
            ColumnMgrEO eo = CacheHandler.getEntity(ColumnMgrEO.class, columnId);
            //SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            //String str1 = sdf1.format(new Date());
            mv.addObject("eo", eo);
            mv.addObject("columnId", columnId);
            mv.addObject("baseId", id);
            //mv.addObject("nowDate", str1);
            mv.addObject("pageIndex", pageIndex);
            return mv;
        }
        return null;
    }

    //留言保存或保存发布
    @RequestMapping("detailSave")
    @ResponseBody
    public Object detailSave(BaseContentEO eo1, GuestBookEO eo2) {
        if (AppUtil.isEmpty(eo1.getId())) {
            return ajaxErr("传参错误(ID:Error)");
        }
        BaseContentEO eo4 = baseContentService.getEntity(BaseContentEO.class, eo1.getId());
        GuestBookEO eo3 = guestBookService.noAuditGuestBook(eo1.getId());
        if (eo3 == null) {
            return ajaxErr("数据库出错，请联系管理员！");
        }
        //保存发布
        if (eo1.getIsPublish().equals(1)) {
            eo4.setIsPublish(1);
        }

        eo3.setPersonName(eo2.getPersonName());
        eo3.setPersonPhone(eo2.getPersonPhone());
        eo3.setPersonIp(eo2.getPersonIp());
        eo3.setGuestBookContent(eo2.getGuestBookContent());
        eo3.setCreateDate(eo2.getCreateDate());
        eo3.setReplyDate(eo2.getReplyDate());
        eo3.setResponseContent(eo2.getResponseContent());

        eo4.setTitle(eo1.getTitle());
        eo4.setCreateDate(eo1.getCreateDate());
        baseContentService.updateEntity(eo4);
        guestBookService.updateEntity(eo3);
        return getObject();
    }


}
