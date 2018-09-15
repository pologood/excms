package cn.lonsun.content.notaudit.controller;

import cn.lonsun.activemq.MessageSender;
import cn.lonsun.common.util.AppUtil;
import cn.lonsun.content.internal.entity.BaseContentEO;
import cn.lonsun.content.internal.entity.GuestBookEO;
import cn.lonsun.content.internal.entity.GuestBookForwardRecordEO;
import cn.lonsun.content.internal.service.IBaseContentService;
import cn.lonsun.content.internal.service.IForwardRecordService;
import cn.lonsun.content.internal.service.IGuestBookService;
import cn.lonsun.content.vo.ContentPageVO;
import cn.lonsun.content.vo.GuestBookEditVO;
import cn.lonsun.core.base.controller.BaseController;
import cn.lonsun.core.util.IpUtil;
import cn.lonsun.message.internal.entity.MessageEnum;
import cn.lonsun.message.internal.entity.MessageStaticEO;
import cn.lonsun.site.contentModel.internal.service.IContentModelService;
import cn.lonsun.site.contentModel.vo.ContentModelParaVO;
import cn.lonsun.util.LoginPersonUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * @author hujun
 * @ClassName: NotAuditGuestBookController
 * @Description: 待审留言
 * @date 2015年12月11日 下午3:08:06
 */
@Controller
@RequestMapping(value = "noAudit")
public class NoAuditGuestBookController extends BaseController {
    @Autowired
    private IGuestBookService guestBookService;
    @Autowired
    private IForwardRecordService forwardRecordService;
    @Autowired
    private IBaseContentService baseontentService;
    @Autowired
    private IContentModelService contModelService;

    @RequestMapping("index")
    public String index() {
        return "/content/noaudit/guestbook_index";

    }

	/*//获取未审核留言数据
	@RequestMapping("getNoAuditPage")
	@ResponseBody
	public Object getNoAuditPage(GuestBookPageVO pageVO){
		return guestBookService.getNoAuditPage(pageVO);
		
	}*/

    //打开修改页面
    @RequestMapping("modify")
    public ModelAndView modify(Long id, String key, String condition, Integer status, Long pageIndex) throws UnsupportedEncodingException {
        GuestBookEO eo = guestBookService.getEntity(GuestBookEO.class, id);
        Long baseContentId = eo.getBaseContentId();
        BaseContentEO eo1 = baseontentService.getEntity(BaseContentEO.class, baseContentId);
        ModelAndView mv = new ModelAndView("/content/noaudit/guestbook_modify");
        mv.addObject("title", eo1.getTitle());
        mv.addObject("pageIndex", pageIndex);
        mv.addObject("guestBookContent", eo.getGuestBookContent());
        mv.addObject("responseContent", eo.getResponseContent());
        mv.addObject("isResponse", eo.getIsResponse());
        mv.addObject("id", id);
        //mv.addObject("columnId",columnId);
        mv.addObject("key", new String(key.getBytes("iso8859-1"), "UTF-8"));
        mv.addObject("condition", condition);
        mv.addObject("status", status);
        mv.addObject("guestBookTime", new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(eo.getCreateDate()));
        //mv.addObject("replyTime", new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(eo.getReplyDate()));
        mv.addObject("personIp", eo.getPersonIp());
        mv.addObject("personName", eo.getPersonName());
        return mv;
    }

    //修改后保存
    @RequestMapping("modifySave")
    @ResponseBody
    public Object modifySave(Long id, String content, String title, String guestBookTime, String personIp, String personName) {
        GuestBookEO eo = guestBookService.getEntity(GuestBookEO.class, id);
        Long baseContentId = eo.getBaseContentId();
        BaseContentEO eo1 = baseontentService.getEntity(BaseContentEO.class, baseContentId);
        eo.setGuestBookContent(content);
        eo.setPersonIp(personIp);
        eo.setPersonName(personName);
        //eo.setReplyDate(AppUtil.formatStringToTime(replyTime, "yyyy-MM-dd HH:mm:ss"));
        eo1.setTitle(title);
        eo1.setCreateDate(AppUtil.formatStringToTime(guestBookTime, "yyyy-MM-dd HH:mm:ss"));
        guestBookService.updateEntity(eo);
        baseontentService.updateEntity(eo1);
        return getObject();

    }

    //单个留言发布
    @RequestMapping("changePublish")
    @ResponseBody
    public Object changePublish(Long id) {
        BaseContentEO eo = baseontentService.getEntity(BaseContentEO.class, id);
        Integer isPublish = eo.getIsPublish();
        boolean rel = false;
        if (isPublish == 1) {
            baseontentService.changePublish(new ContentPageVO(null, null, 0, new Long[]{id}, null));
            rel = MessageSender.sendMessage(new MessageStaticEO(eo.getSiteId(), eo.getColumnId(), new Long[]{id}).setType(MessageEnum.UNPUBLISH.value()));
        } else {
            baseontentService.changePublish(new ContentPageVO(null, null, 1, new Long[]{id}, null));
            rel = MessageSender.sendMessage(new MessageStaticEO(eo.getSiteId(), eo.getColumnId(), new Long[]{id}).setType(MessageEnum.PUBLISH.value()));
        }

        return getObject(isPublish);
    }

    //打开回复页面
    @RequestMapping(value = "guestBookReply")
    @ResponseBody
    public ModelAndView guestBookReply(Long id, String key, String condition, Integer status, Long pageIndex) throws UnsupportedEncodingException {
        GuestBookEO eo = guestBookService.getEntity(GuestBookEO.class, id);
        Long baseContentId = eo.getBaseContentId();
        BaseContentEO eo1 = baseontentService.getEntity(BaseContentEO.class, baseContentId);
        ModelAndView mv = new ModelAndView("/content/guestbook_reply");

        mv.addObject("guestBookTime", new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(eo.getCreateDate()));

        if (AppUtil.isEmpty(eo.getResponseContent())) {
            mv.addObject("replyTime", new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(new Date()));
        }

        if (!AppUtil.isEmpty(eo.getReplyDate())) {
            mv.addObject("replyTime", new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(eo.getReplyDate()));
        } else {
            mv.addObject("replyTime", "");
        }

        mv.addObject("title", eo1.getTitle());
        mv.addObject("pageIndex", pageIndex);
        mv.addObject("guestBookContent", eo.getGuestBookContent());
        mv.addObject("responseContent", eo.getResponseContent());
        mv.addObject("isResponse", eo.getIsResponse());
        mv.addObject("id", id);
        //mv.addObject("columnId",columnId);
        mv.addObject("key", new String(key.getBytes("iso8859-1"), "UTF-8"));
        mv.addObject("condition", condition);
        mv.addObject("status", status);
        return mv;
    }

    //进行留言回复
    @RequestMapping("reply")
    @ResponseBody
    public Object reply(Long id, String content, Integer isResponse, String replyTime) {
        if (!AppUtil.isEmpty(content)) {
            GuestBookEditVO eo = new GuestBookEditVO();
            eo.setId(id);
            eo.setResponseContent(content);
            eo.setIsResponse(isResponse);
            eo.setReplyDate(AppUtil.formatStringToTime(replyTime, "yyyy-MM-dd HH:mm:ss"));
            //throw new BaseRunTimeException("回复内容不能为空，请填写回复内容！");
            guestBookService.reply(eo);
            return getObject();
        } else {
            return ajaxErr("留言不可为空！");
        }

    }

    //打开转办页面
    @RequestMapping(value = "guestBookForward")
    public ModelAndView guestBookForward(Long id, String key, String condition, Integer status, Long pageIndex) throws UnsupportedEncodingException {
        GuestBookEO eo = guestBookService.getEntity(GuestBookEO.class, id);
        Long baseContentId = eo.getBaseContentId();
        BaseContentEO eo1 = baseontentService.getEntity(BaseContentEO.class, baseContentId);
        Long columnId = eo1.getColumnId();

        List<ContentModelParaVO> list = contModelService.getParam(columnId, LoginPersonUtil.getSiteId(), null);
        Integer recType = list.get(0).getRecType();
        ModelAndView mv = new ModelAndView("/content/guestbook_forward");
        mv.addObject("list", list);
        mv.addObject("id", id);
        mv.addObject("pageIndex", pageIndex);
        mv.addObject("columnId", columnId);
        mv.addObject("key", new String(key.getBytes("iso8859-1"), "UTF-8"));
        mv.addObject("condition", condition);
        mv.addObject("status", status);
        mv.addObject("recType", recType);
        return mv;
    }

    //保存转办记录同时更改留言表里的接受单位
    @RequestMapping("forward")
    @ResponseBody
    public Object forward(Long id, Long receiveId, String remarks, String receiveName, String localUnitId) {
        if (!AppUtil.isEmpty(receiveId) && !AppUtil.isEmpty(remarks)) {
            guestBookService.forward(id, receiveId, null, 0, localUnitId);
            GuestBookForwardRecordEO eo = new GuestBookForwardRecordEO();
            eo.setIp(IpUtil.getIpAddr(LoginPersonUtil.getRequest()));
            eo.setUserName(LoginPersonUtil.getPersonName());
            eo.setGuestBookId(id);
            eo.setReceiveOrganId(receiveId);
            eo.setRemarks(remarks);
            eo.setReceiveName(receiveName);
            forwardRecordService.saveRecord(eo);
            return getObject();
        } else if (AppUtil.isEmpty(receiveId)) {
            return ajaxErr("请选择单位");
        } else {
            return ajaxErr("请填写备注");
        }
    }

    //删除留言
    @RequestMapping("delete")
    @ResponseBody
    public Object delete(Long id) {
        baseontentService.delete(BaseContentEO.class, id);
        return getObject();
    }

}
