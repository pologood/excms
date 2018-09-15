package cn.lonsun.staticcenter.controller;

import cn.lonsun.cache.client.CacheHandler;
import cn.lonsun.common.util.AppUtil;
import cn.lonsun.content.internal.entity.BaseContentEO;
import cn.lonsun.content.internal.entity.GuestBookEO;
import cn.lonsun.content.internal.service.IBaseContentService;
import cn.lonsun.content.internal.service.IGuestBookService;
import cn.lonsun.content.messageBoard.controller.internal.entity.MessageBoardEO;
import cn.lonsun.content.messageBoard.controller.vo.MessageBoardEditVO;
import cn.lonsun.content.messageBoard.service.IMessageBoardService;
import cn.lonsun.content.vo.GuestBookEditVO;
import cn.lonsun.content.vo.HitVO;
import cn.lonsun.core.base.controller.BaseController;
import cn.lonsun.core.exception.BaseRunTimeException;
import cn.lonsun.core.util.ContextHolderUtils;
import cn.lonsun.core.util.IpUtil;
import cn.lonsun.core.util.TipsMode;
import cn.lonsun.rbac.internal.entity.OrganEO;
import cn.lonsun.site.contentModel.vo.ColumnTypeConfigVO;
import cn.lonsun.site.contentModel.vo.ContentModelParaVO;
import cn.lonsun.system.datadictionary.vo.DataDictVO;
import cn.lonsun.system.member.internal.entity.MemberEO;
import cn.lonsun.system.member.internal.service.IMemberService;
import cn.lonsun.system.member.vo.MemberSessionVO;
import cn.lonsun.util.DataDictionaryUtil;
import cn.lonsun.util.DateUtil;
import cn.lonsun.util.LoginPersonUtil;
import cn.lonsun.util.ModelConfigUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.*;

import static cn.lonsun.common.util.AppUtil.getLongs;

/**
 *
 */

@Controller
@RequestMapping(value = "/frontMessageBoard")
public class FrontMessageBoardController extends BaseController {

    @Autowired
    private IMessageBoardService messageBoardService;

    @Autowired
    private IBaseContentService contentService;
    @Autowired
    private IMemberService memberService;

    @RequestMapping(value = "saveVO")
    @ResponseBody
    public Object saveVO(MessageBoardEditVO vo, String checkCode) {
        ColumnTypeConfigVO configVO = ModelConfigUtil.getCongfigVO(vo.getColumnId(), vo.getSiteId());
        MemberSessionVO memberVO = (MemberSessionVO) ContextHolderUtils.getSession().getAttribute("member");
        if (configVO != null) {
            Integer isLogin = configVO.getIsLoginGuest();
            if (    isLogin != null && isLogin == 1) {
                if (memberVO == null || memberVO.getId() == null) {
                    return ajaxErr("请重新登录！");
                }
            }
        }
        if (StringUtils.isEmpty(vo.getPersonName())) {
            return ajaxErr("您的姓名为空！");
        }
        if (vo.getPersonPhone() == null) {
            return ajaxErr("联系方式为空！");
        }
        if (StringUtils.isEmpty(vo.getTitle())) {
            return ajaxErr("标题为空！");
        }
        if (StringUtils.isEmpty(vo.getMessageBoardContent())) {
            return ajaxErr("内容不能为空！");
        }
        if (StringUtils.isEmpty(checkCode)) {
            return ajaxErr("验证码不能为空！");
        }
        String webCode = (String) ContextHolderUtils.getSession().getAttribute("webCode");
        if (!checkCode.trim().toLowerCase().equals(webCode.toLowerCase())) {
            return ajaxErr("验证码不正确，请重新输入！");
        }

        BaseContentEO contentEO = new BaseContentEO();
        AppUtil.copyProperties(contentEO, vo);
        if (memberVO != null) {
            contentEO.setCreateUserId(memberVO.getId());
        }
        MessageBoardEO messageBoardEO = new MessageBoardEO();
        AppUtil.copyProperties(messageBoardEO, vo);
        Long id = contentService.saveEntity(contentEO);
        CacheHandler.saveOrUpdate(BaseContentEO.class, contentEO);
        messageBoardEO.setPersonIp(IpUtil.getIpAddr(ContextHolderUtils.getRequest()));
        messageBoardEO.setBaseContentId(id);
        messageBoardEO.setAddDate(new Date());
        messageBoardEO.setCreateUnitId(LoginPersonUtil.getUnitId());
        if (memberVO != null) {
            messageBoardEO.setCreateUserId(memberVO.getId());
        }
        DataDictVO dictVO = DataDictionaryUtil.getItem("petition_purpose", messageBoardEO.getClassCode());
        if (dictVO == null) {
            throw new BaseRunTimeException(TipsMode.Message.toString(), "留言类型未配置");
        } else {
            messageBoardEO.setClassName(dictVO.getKey());
        }
        MessageBoardEO eo = messageBoardService.saveMessageBoard(messageBoardEO, vo.getSiteId(), vo.getColumnId());
        return getObject(eo);
    }

    @RequestMapping(value = "saveMessageBoardComment")
    @ResponseBody
    public Object saveMessageBoardComment(Long baseContentId, String docNum, String randomCode, String commentCode) {
        if (StringUtils.isEmpty(docNum)) {
            return ajaxErr("查询编号不能为空！");
        }
        if(StringUtils.isEmpty(randomCode)){
            return ajaxErr("查询密码不能为空！");
        }
        MessageBoardEditVO editVO = messageBoardService.getVO(baseContentId);
        if (editVO == null) {
            return ajaxErr("参数错误！");
        }
        if (!docNum.equals(editVO.getDocNum())) {
            return ajaxErr("查询编号错误！");
        }
        if (!randomCode.equals(editVO.getRandomCode())) {
            return ajaxErr("查询密码错误！");
        }
        if (StringUtils.isEmpty(commentCode)) {
            return ajaxErr("评价结果不能为空！");
        }

        MessageBoardEO messageBoardEO = messageBoardService.getEntity(MessageBoardEO.class, editVO.getId());
        if (messageBoardEO == null) {
            return ajaxErr("参数错误！");
        }
        messageBoardEO.setCommentCode(commentCode);
        DataDictVO dictVO = DataDictionaryUtil.getItem("guest_comment", commentCode);
        String commentName=null;
        if (dictVO != null) {
            commentName=dictVO.getKey();
        }
        messageBoardService.saveComment(messageBoardEO);
        return getObject(commentName);

    }
}
