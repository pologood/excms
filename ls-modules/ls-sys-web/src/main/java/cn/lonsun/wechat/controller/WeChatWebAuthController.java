package cn.lonsun.wechat.controller;

import cn.lonsun.common.util.AppUtil;
import cn.lonsun.content.internal.entity.BaseContentEO;
import cn.lonsun.content.internal.entity.GuestBookEO;
import cn.lonsun.content.internal.service.IBaseContentService;
import cn.lonsun.content.internal.service.IGuestBookService;
import cn.lonsun.content.vo.GuestBookEditVO;
import cn.lonsun.content.vo.GuestBookPageVO;
import cn.lonsun.core.base.controller.BaseController;
import cn.lonsun.core.base.entity.AMockEntity;
import cn.lonsun.core.exception.BaseRunTimeException;
import cn.lonsun.core.util.TipsMode;
import cn.lonsun.rbac.internal.entity.OrganEO;
import cn.lonsun.rbac.internal.service.IOrganService;
import cn.lonsun.site.contentModel.vo.ContentModelParaVO;
import cn.lonsun.util.LoginPersonUtil;
import cn.lonsun.util.ModelConfigUtil;
import cn.lonsun.wechatmgr.internal.wechatapiutil.ApiUtil;
import cn.lonsun.wechatmgr.internal.wechatapiutil.MenuType;
import cn.lonsun.wechatmgr.internal.wechatapiutil.SnsUser;
import net.sf.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Hewbing
 * @ClassName: WeChatWebAuthController
 * @Description: 微信登入认证
 * @date 2016年4月1日 下午2:59:52
 */
@RequestMapping("webAuth")
@Controller
public class WeChatWebAuthController extends BaseController {

    @Autowired
    private IGuestBookService guestBookService;

    @Autowired
    private IBaseContentService contentService;

    @Autowired
    private IOrganService organService;

    /**
     * @param siteId
     * @param columnId
     * @param code
     * @param state
     * @param map
     * @param request
     * @return String   return type
     * @throws
     * @Title: login
     * @Description: 微信网页登入认证
     */
    @RequestMapping("{siteId}/{columnId}/login")
    public String login(@PathVariable Long siteId, @PathVariable Long columnId, String code, String state, ModelMap map, HttpServletRequest request) {
        if (AppUtil.isEmpty(code)) {
            map.put("siteId", siteId);
            map.put("columnId", columnId);
        } else {
            //当为互动列表或查询时不获取用户信息，不用时可去掉if过滤
            if (!MenuType.INTERACT_LIST.equals(state) && !MenuType.INQUIRY.equals(state)) {
                JSONObject json = ApiUtil.getSnsUserInfo(code, siteId);
                SnsUser snsUser = (SnsUser) JSONObject.toBean(json, SnsUser.class);
                map.put("nickname", snsUser.getNickname());
                map.put("headimg", snsUser.getHeadimgurl());
                map.put("openId", snsUser.getOpenid());
            }
        }
        String _vm = null;
        if (MenuType.INTERACT_LIST.equals(state)) {
            _vm = "/wechat/interact_list";//互动列表
        } else if (MenuType.INQUIRY.equals(state)) {
            _vm = "/wechat/inquiry";
        } else if (MenuType.CONSULT.equals(state)) {
            map.put("title", "我要咨询");
            map.put("type", "do_consult");
            _vm = "/wechat/interact_form";
        } else if (MenuType.SUGGEST.equals(state)) {
            map.put("title", "我要建议");
            map.put("type", "do_suggest");
            _vm = "/wechat/interact_form";
        } else if (MenuType.COMPLAIN.equals(state)) {
            map.put("title", "我要投诉");
            map.put("type", "do_complain");
            _vm = "/wechat/interact_form";
        } else if (MenuType.REPORT.equals(state)) {
            map.put("title", "我要举报");
            map.put("type", "do_report");
            _vm = "/wechat/interact_form";
        }
        return _vm;
    }


    //互动列表
    @RequestMapping("interactList")
    @ResponseBody
    public Object interactList(GuestBookPageVO pageVO) {
        pageVO.setIsPublish(1);
        if (AppUtil.isEmpty(pageVO.getTypeCode())) {
            pageVO.setTypeCode(BaseContentEO.TypeCode.guestBook.toString());
        }
        return getObject(guestBookService.getPage(pageVO));
    }

    //查询留言
    @RequestMapping("inquiry")
    public String inquiry(Long siteId, String st, String ed, String title, ModelMap map) {
        map.put("siteId", siteId);
        map.put("title", title);
        map.put("st", st);
        map.put("ed", ed);
        return "/wechat/interact_list";
    }

    //留言详细页
    @RequestMapping("detailPage")
    public Object detailPage(Long id, ModelMap modelMap) {
        modelMap.put("id", id);
        return "/wechat/detail_page";
    }

    //获取详细留言
    @RequestMapping("getDetail")
    @ResponseBody
    public Object getDetail(Long id) {
        if (null == id) {
            throw new BaseRunTimeException(TipsMode.Message.toString(), "请选择留言");
        }
        BaseContentEO contentEO = contentService.getEntity(BaseContentEO.class, id);
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("baseContentId", id);
        map.put("recordStatus", AMockEntity.RecordStatus.Normal.toString());
        List<GuestBookEO> list = guestBookService.getEntities(GuestBookEO.class, map);
        if (null == list || list.size() <= 0) {
            throw new BaseRunTimeException(TipsMode.Message.toString(), "留言为空");
        }
        GuestBookEO eo = list.get(0);
        GuestBookEditVO vo = new GuestBookEditVO();
        AppUtil.copyProperties(vo, eo);
        vo.setIsPublish(contentEO.getIsPublish());
        vo.setTitle(contentEO.getTitle());
        vo.setColumnId(contentEO.getColumnId());
        vo.setSiteId(contentEO.getSiteId());
        OrganEO org = organService.getEntity(OrganEO.class, vo.getReceiveId());
        if (org != null) {
            vo.setReceiveName(org.getName());
            ;
        }
        return getObject(vo);
    }

    //保存表单
    @RequestMapping("saveGuestBook")
    @ResponseBody
    public Object saveGuestBook(GuestBookEditVO vo) {
        vo.setResourceType(1);
        if (AppUtil.isEmpty(vo.getOpenId())) {
            return ajaxErr("请通过微信客户端提交表单！");
        }
        guestBookService.saveGusetBook(vo);
        return getObject();
    }

    @RequestMapping("getRecOrgList")
    @ResponseBody
    public Object getRecOrgList(Long columnId) {
        List<ContentModelParaVO> list = ModelConfigUtil.getParam(columnId, LoginPersonUtil.getSiteId(),null);
        return getObject(list);
    }

    @RequestMapping("getType")
    @ResponseBody
    public Object getType(Long columnId) {
        List<ContentModelParaVO> list = null;
        if (columnId == null) {
            throw new BaseRunTimeException(TipsMode.Message.toString(), "栏目为空");
        } else {
            list = ModelConfigUtil.getGuestBookType(columnId,LoginPersonUtil.getSiteId());
        }
        return getObject(list);
    }


}
