/*
 * PublicApplyController.java         2015年12月22日 <br/>
 *
 * Copyright (c) 1994-1999 AnHui LonSun, Inc. <br/>
 * All rights reserved.	<br/>
 *
 * This software is the confidential and proprietary information of AnHui	<br/>
 * LonSun, Inc. ("Confidential Information").  You shall not	<br/>
 * disclose such Confidential Information and shall use it only in	<br/>
 * accordance with the terms of the license agreement you entered into	<br/>
 * with Sun. <br/>
 */

package cn.lonsun.publicInfo.controller;

import cn.lonsun.content.internal.entity.BaseContentEO;
import cn.lonsun.content.internal.service.IBaseContentService;
import cn.lonsun.content.vo.ContentPageVO;
import cn.lonsun.core.base.controller.BaseController;
import cn.lonsun.core.exception.BaseRunTimeException;
import cn.lonsun.core.exception.BusinessException;
import cn.lonsun.core.util.TipsMode;
import cn.lonsun.publicInfo.internal.entity.PublicApplyEO;
import cn.lonsun.publicInfo.internal.entity.PublicContentEO;
import cn.lonsun.publicInfo.internal.service.IPublicApplyService;
import cn.lonsun.publicInfo.internal.service.IPublicContentService;
import cn.lonsun.publicInfo.vo.PublicApplyQueryVO;
import cn.lonsun.publicInfo.vo.PublicApplyVO;
import cn.lonsun.rbac.internal.entity.OrganEO;
import cn.lonsun.rbac.internal.service.IOrganService;
import cn.lonsun.system.datadictionary.vo.DataDictVO;
import cn.lonsun.system.systemlog.internal.entity.CmsLogEO;
import cn.lonsun.util.DataDictionaryUtil;
import cn.lonsun.util.LoginPersonUtil;
import cn.lonsun.util.SysLog;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;

/**
 * 依申请公开 <br/>
 *
 * @author fangtinghua <br/>
 * @version v1.0 <br/>
 * @date 2015年12月22日 <br/>
 */
@Controller
@RequestMapping("/public/apply")
public class PublicApplyController extends BaseController {

    @Resource
    private IBaseContentService baseContentService;
    @Resource
    private IPublicApplyService publicApplyService;
    @Resource
    private IPublicContentService publicContentService;
    @Resource
    private IOrganService organService;
    @RequestMapping("index")
    public String index() {
        return "/public/apply/index";
    }

    /**
     * 获取分页
     *
     * @param queryVO
     * @return
     * @throws BusinessException
     * @author fangtinghua
     */
    @ResponseBody
    @RequestMapping("getPage")
    public Object getPage(PublicApplyQueryVO queryVO) throws BusinessException {
        if (!LoginPersonUtil.isSuperAdmin() && !LoginPersonUtil.isSiteAdmin()) {
            queryVO.setOrganId(LoginPersonUtil.getUnitId());//查询当前用户部门的数据
        }
        return publicApplyService.getPagination(queryVO);
    }

    @RequestMapping("doReply")
    public String doReply(Long id, Model model) {
        if (id == null) {
            throw new BaseRunTimeException(TipsMode.Message.toString(), "ID不能为空！");
        }
        List<DataDictVO> statusList = DataDictionaryUtil.getDDList("public_apply_reply_status");
        model.addAttribute("statusList", statusList);
        model.addAttribute("id", id);
        return "/public/content/reply";
    }

    @ResponseBody
    @RequestMapping("getReplyStatus")
    public Object getReplyStatus() {
        return DataDictionaryUtil.getDDList("public_apply_reply_status");
    }

    @ResponseBody
    @RequestMapping("getPublicApply")
    public Object getPublicApply(Long id) {
        if (id == null) {
            return ajaxErr("ID不能为空！");
        }
        PublicApplyVO vo = publicApplyService.getPublicApply(id);
        if (vo.getReplyDate() == null) {
            vo.setReplyDate(new Date());
        }
        return getObject(vo);
    }

    @RequestMapping("getPublicApplyDetail")
    public Object getPublicApplyDetail(Long id, ModelMap modelMap) {
        if (id == null) {
            throw new BaseRunTimeException(TipsMode.Message.toString(), "ID不能为空！");
        }
        modelMap.put("vo", publicApplyService.getPublicApply(id));
        return "/public/content/detail";
    }

    @ResponseBody
    @RequestMapping("saveReply")
    public Object saveReply(PublicApplyVO vo) {
        if (vo.getId() == null) {
            return ajaxErr("参数错误！");
        }
        PublicApplyEO eo = publicApplyService.getEntity(PublicApplyEO.class, vo.getId());
        if (eo == null) {
            return ajaxErr("参数错误！");
        }
        if (StringUtils.isEmpty(vo.getReplyStatus())) {
            return ajaxErr("回复状态不能为空！");
        }
        eo.setReplyContent(vo.getReplyContent());
        eo.setReplyDate(vo.getReplyDate());
        eo.setReplyStatus(vo.getReplyStatus());
        publicApplyService.updateEntity(eo);
        OrganEO organ = (OrganEO)this.organService.getEntity(OrganEO.class, vo.getOrganId());
        if (organ != null) {
            SysLog.log("依申请公开：回复内容（" + vo.getContent() + "），接收单位（" + organ.getName() + ")", "PublicApplyVO", CmsLogEO.Operation.Update.toString());
        }
        return getObject();
    }

    /**
     * 删除
     *
     * @param columnId
     * @param ids
     * @param contentIds
     * @return
     * @author fangtinghua
     */
    @ResponseBody
    @RequestMapping("delete")
    public Object delete(Long columnId, @RequestParam("ids[]") Long[] ids, @RequestParam("contentIds[]") Long[] contentIds) {
        if (null == ids || ids.length == 0) {
            return ajaxErr("Id不能为空！");
        }
        publicApplyService.delete(ids);
        /*Long siteId = LoginPersonUtil.getSiteId();
        MessageSender.sendMessage(new MessageStaticEO(siteId, columnId, contentIds).setType(MessageEnum.UNPUBLISH.value()).setSource(
                MessageEnum.PUBLICINFO.value()));*/
        for (int i = 0; i < ids.length; i++)
        {
            PublicContentEO publicContentEO = (PublicContentEO)this.publicContentService.getEntity(PublicContentEO.class, ids[i]);
            BaseContentEO baseContentEO = (BaseContentEO)this.baseContentService.getEntity(BaseContentEO.class, publicContentEO.getContentId());
            Long organId = publicContentEO.getOrganId();
            OrganEO organ = (OrganEO)this.organService.getEntity(OrganEO.class, organId);
            if ((publicContentEO.getType().equals(PublicContentEO.Type.PUBLIC_.toString())) && (organ != null)) {
                SysLog.log("依申请公开：删除内容（" + baseContentEO.getTitle() + "），接收单位（" + organ.getName() + "）", "PublicContentEO", CmsLogEO.Operation.Delete.toString());
            }
        }
        return getObject();
    }

    /**
     * 获取未审核内容
     *
     * @return
     * @author fangtinghua
     */
    @RequestMapping("toUnApply")
    public String toUnApply() {
        return "/public/content/unApply";
    }


    /**
     * 发布、取消发布
     *
     * @param ids
     * @param isPublish
     * @return
     */
    @ResponseBody
    @RequestMapping("publish")
    public Object publish(@RequestParam("ids[]") Long[] ids, Integer isPublish) {
        Long siteId = LoginPersonUtil.getSiteId();
        baseContentService.changePublish(new ContentPageVO(siteId, null, isPublish, ids, null));
        for (int i = 0; i < ids.length; i++)
        {
            PublicContentEO publicContentEO = (PublicContentEO)this.publicContentService.getEntity(PublicContentEO.class, ids[i]);
            BaseContentEO baseContentEO = (BaseContentEO)this.baseContentService.getEntity(BaseContentEO.class, publicContentEO.getContentId());
            Long organId = publicContentEO.getOrganId();
            OrganEO organ = (OrganEO)this.organService.getEntity(OrganEO.class, organId);
            if ((publicContentEO.getType().equals(PublicContentEO.Type.PUBLIC_.toString())) && (organ != null)) {
                if (isPublish.intValue() == 1) {
                    SysLog.log("依申请公开：发布内容（" + baseContentEO.getTitle() + "），接收单位（" + organ.getName() + "）", "PublicContentEO", CmsLogEO.Operation.Update.toString());
                } else {
                    SysLog.log("依申请公开：取消发布内容（" + baseContentEO.getTitle() + "），接收单位（" + organ.getName() + "）", "PublicContentEO", CmsLogEO.Operation.Update.toString());
                }
            }
        }
        return getObject();
    }
}