/*
 * FrontPublicController.java         2016年7月12日 <br/>
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

package cn.lonsun.staticcenter.controller;

import cn.lonsun.common.util.AppUtil;
import cn.lonsun.core.base.controller.BaseController;
import cn.lonsun.core.exception.BusinessException;
import cn.lonsun.core.util.Pagination;
import cn.lonsun.core.util.TipsMode;
import cn.lonsun.publicInfo.internal.entity.PublicContentEO;
import cn.lonsun.publicInfo.internal.service.IPublicApplyService;
import cn.lonsun.publicInfo.internal.service.IPublicContentService;
import cn.lonsun.publicInfo.util.PublicUtil;
import cn.lonsun.publicInfo.vo.*;
import cn.lonsun.rbac.utils.ValidateCode;
import cn.lonsun.staticcenter.generate.util.PathUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.List;

/**
 * 信息公开 <br/>
 *
 * @author fangtinghua <br/>
 * @version v1.0 <br/>
 * @date 2016年7月12日 <br/>
 */
@Controller
@RequestMapping(value = "/public")
public class FrontPublicController extends BaseController {

    @Resource
    private IPublicApplyService publicApplyService;
    @Resource
    private IPublicContentService publicContentService;

    /**
     * 获取验证码
     *
     * @param request
     * @param response
     * @throws IOException
     */
    @ResponseBody
    @RequestMapping("getCode")
    public void getCode(HttpServletRequest request, HttpServletResponse response) throws IOException {
        // 设置响应的类型格式为图片格式
        response.setContentType("image/jpeg");
        // 禁止图像缓存。
        response.setHeader("Pragma", "no-cache");
        response.setHeader("Cache-Control", "no-cache");
        response.setDateHeader("Expires", 0);
        HttpSession session = request.getSession();
        ValidateCode vCode = new ValidateCode(120, 40, 4, 10);
        session.setAttribute("webCode", vCode.getCode());
        vCode.write(response.getOutputStream());
    }

    /**
     * 在线申请，返回查询编号
     *
     * @param vo
     * @throws BusinessException
     * @author fangtinghua
     */
    @ResponseBody
    @RequestMapping("applySubmit")
    public Object applySubmit(HttpServletRequest request, PublicApplyVO vo) throws BusinessException {
        String type = vo.getType();// 申请类型
        if (StringUtils.isEmpty(type)) {
            throw new BusinessException(TipsMode.Message.toString(), "申请类型不能为空！");
        }
        if (PublicApplyTypeVO.PERSON.getCode().equals(type)) {// 个人
            if (StringUtils.isEmpty(vo.getName())) {
                throw new BusinessException(TipsMode.Message.toString(), "姓名不能为空！");
            }
            if (StringUtils.isEmpty(vo.getOrganName())) {
                throw new BusinessException(TipsMode.Message.toString(), "工作单位不能为空！");
            }
            if (StringUtils.isEmpty(vo.getCardType()) || StringUtils.isEmpty(vo.getCardTypeName())) {
                throw new BusinessException(TipsMode.Message.toString(), "证件名称不能为空！");
            }
            if (StringUtils.isEmpty(vo.getCardNum())) {
                throw new BusinessException(TipsMode.Message.toString(), "证件号码不能为空！");
            }
            if (StringUtils.isEmpty(vo.getPhone())) {
                throw new BusinessException(TipsMode.Message.toString(), "联系电话不能为空！");
            }
        } else if (PublicApplyTypeVO.ORGAN.getCode().equals(type)) {// 法人
            if (StringUtils.isEmpty(vo.getOrganName())) {
                throw new BusinessException(TipsMode.Message.toString(), "组织机构名称不能为空！");
            }
            if (StringUtils.isEmpty(vo.getLegalName())) {
                throw new BusinessException(TipsMode.Message.toString(), "法人代表不能为空！");
            }
            if (StringUtils.isEmpty(vo.getContactName())) {
                throw new BusinessException(TipsMode.Message.toString(), "联系人姓名不能为空！");
            }
            if (StringUtils.isEmpty(vo.getContactPhone())) {
                throw new BusinessException(TipsMode.Message.toString(), "联系电话不能为空！");
            }
        } else {
            throw new BusinessException(TipsMode.Message.toString(), "申请类型只能为公民或法人/其他组织！");
        }
        if (StringUtils.isEmpty(vo.getAddress())) {
            throw new BusinessException(TipsMode.Message.toString(), "联系地址不能为空！");
        }
        if (StringUtils.isEmpty(vo.getContent())) {
            throw new BusinessException(TipsMode.Message.toString(), "所需信息的内容描述不能为空！");
        }
        String webCode = vo.getWebCode();
        Object sessionCode = request.getSession().getAttribute("webCode");
        if (null == sessionCode) {
            throw new BusinessException(TipsMode.Message.toString(), "验证码已经失效！");
        }
        if (StringUtils.isEmpty(webCode) || !webCode.equalsIgnoreCase(sessionCode.toString())) {
            throw new BusinessException(TipsMode.Message.toString(), "验证码不正确！");
        }
        if (StringUtils.isEmpty(vo.getQueryPassword())) {
            throw new BusinessException(TipsMode.Message.toString(), "查询密码不能为空！");
        }
        if (!vo.getQueryPassword().equals(vo.getConfirmPassword())) {
            throw new BusinessException(TipsMode.Message.toString(), "确认密码必须和查询密码一致！");
        }
        publicApplyService.saveEntity(vo);
        return getObject(vo);
    }

    /**
     * 依申请公开目录查询
     *
     * @param queryVO
     * @author fangtinghua
     */
    @ResponseBody
    @RequestMapping("applyCatalogQuery")
    public Object applyCatalogQuery(HttpServletRequest request, PublicContentQueryVO queryVO) {
        // DataDictVO vo =
        // DataDictionaryUtil.getItem(PublicContentEO.PUBLIC_ITEM_CODE,
        // "PUBLIC_CATALOG");
        // queryVO.setCatId(vo.getId());
        queryVO.setIsPublish(1);
        queryVO.setType(PublicContentEO.Type.PUBLIC_CATALOG.toString());
        Pagination pagination = publicContentService.getPagination(queryVO);
        List<?> list = pagination.getData();
        if (null != list && !list.isEmpty()) {
            for (Object obj : list) {
                PublicContentVO v = (PublicContentVO) obj;
                // v.setCatName(vo.getKey());
                v.setClassNames(PublicUtil.getClassName(v.getClassIds()));//主题分类
                v.setLink(PathUtil.getLinkPath(request, v.getOrganId(), v.getContentId()));
            }
        }
        return getObject(pagination);
    }

    /**
     * 依申请公开目录查询
     *
     * @param queryVO
     * @author fangtinghua
     */
    @ResponseBody
    @RequestMapping("applyCompletionQuery")
    public Object applyCompletionQuery(HttpServletRequest request, PublicContentQueryVO queryVO) {
        // DataDictVO vo =
        // DataDictionaryUtil.getItem(PublicContentEO.PUBLIC_ITEM_CODE,
        // "PUBLIC_COMPLETION");
        // queryVO.setCatId(vo.getId());
        queryVO.setIsPublish(1);
        queryVO.setType(PublicContentEO.Type.PUBLIC_COMPLETION.toString());
        Pagination pagination = publicContentService.getPagination(queryVO);
        List<?> list = pagination.getData();
        if (null != list && !list.isEmpty()) {
            for (Object obj : list) {
                PublicContentVO v = (PublicContentVO) obj;
                // v.setCatName(vo.getKey());
                // v.setClassNames(PublicUtil.getClassName(v.getClassIds()));
                v.setLink(PathUtil.getLinkPath(request, v.getOrganId(), v.getContentId()));
            }
        }
        return getObject(pagination);
    }

    /**
     * 依申请公开查询
     *
     * @param vo
     * @throws BusinessException
     * @author fangtinghua
     */
    @ResponseBody
    @RequestMapping("applyQuery")
    public Object applyQuery(PublicApplyQueryVO vo) throws BusinessException {
        String queryCode = vo.getQueryCode();// 查询编号
        String cardNum = vo.getCardNum();//证件号码
        if (StringUtils.isEmpty(queryCode) && StringUtils.isEmpty(cardNum)) {// 没有查询编号，需要查询出已发布的
            vo.setIsPublish(1);
        }
        Pagination pagination = publicApplyService.getPagination(vo);
        //  处理人名
        List<?> list = (List<?>) pagination.getData();
        for (Object o : list) {
            PublicApplyVO publicApplyVO = (PublicApplyVO) o;
            porcessField(publicApplyVO);
        }
        return getObject(pagination);
    }

    /**
     * 依申请公开目录查询
     *
     * @param id
     * @author fangtinghua
     */
    @ResponseBody
    @RequestMapping("applyDetail")
    public Object applyDetail(Long id) {
        PublicApplyVO publicApplyVO = publicApplyService.getPublicApply(id);
        porcessField(publicApplyVO);
        return getObject(publicApplyVO);
    }

    /**
     * 处理保密字段
     *
     * @param publicApplyVO
     */
    private void porcessField(PublicApplyVO publicApplyVO) {
        if (!AppUtil.isEmpty(publicApplyVO.getName())) {// 人名
            String name = publicApplyVO.getName();
            String regex1 = "(.){1}(.+)";
            name = name.replaceAll(regex1, "$1**");
            publicApplyVO.setName(name);
        }
        if (!AppUtil.isEmpty(publicApplyVO.getLegalName())) {// 法人代表
            String legalName = publicApplyVO.getLegalName();
            String regex1 = "(.){1}(.+)";
            legalName = legalName.replaceAll(regex1, "$1**");
            publicApplyVO.setLegalName(legalName);
        }
        if (!AppUtil.isEmpty(publicApplyVO.getContactName())) {// 联系人
            String contactName = publicApplyVO.getContactName();
            String regex1 = "(.){1}(.+)";
            contactName = contactName.replaceAll(regex1, "$1**");
            publicApplyVO.setContactName(contactName);
        }
        if (!AppUtil.isEmpty(publicApplyVO.getOrganName())) {// 单位
            String organName = publicApplyVO.getOrganName();
            String regex1 = "(.){1}(.+)";
            organName = organName.replaceAll(regex1, "$1******");
            publicApplyVO.setOrganName(organName);
        }
        if (!AppUtil.isEmpty(publicApplyVO.getCardNum())) {// 身份证号码
            String cardNum = publicApplyVO.getCardNum();
            String regex1 = "(\\w{6})(\\w+)(\\w{2})(\\w+)";
            cardNum = cardNum.replaceAll(regex1, "$1********$3**");
            publicApplyVO.setCardNum(cardNum);
        }
        if (!AppUtil.isEmpty(publicApplyVO.getPhone())) {// 电话号码
            String phone = publicApplyVO.getPhone();
            String regex2 = "(\\w{3})(\\w+)(\\w{4})";
            phone = phone.replaceAll(regex2, "$1****$3");
            publicApplyVO.setPhone(phone);
        }
        if (!AppUtil.isEmpty(publicApplyVO.getContactPhone())) {// 联系电话
            String contactPhone = publicApplyVO.getContactPhone();
            String regex2 = "(\\w{4})(\\w+)";
            contactPhone = contactPhone.replaceAll(regex2, "$1*******");
            publicApplyVO.setContactPhone(contactPhone);
        }
        if (!AppUtil.isEmpty(publicApplyVO.getMail())) { // 邮件
            String email = publicApplyVO.getMail();
            String regex3 = "(\\w{3})(\\w+)(@\\w+)";
            email = email.replaceAll(regex3, "$1****$3");
            publicApplyVO.setMail(email);
        }
        if (!AppUtil.isEmpty(publicApplyVO.getOrganCode())) { // 组织机构代码
            publicApplyVO.setOrganCode("*******");
        }
        if (!AppUtil.isEmpty(publicApplyVO.getPostalNum())) { // 邮政编码
            publicApplyVO.setPostalNum("*******");
        }
        if (!AppUtil.isEmpty(publicApplyVO.getFax())) { // 传真
            publicApplyVO.setFax("*******");
        }
        publicApplyVO.setAddress("*******"); // 地址
    }

    /**
     * 依申请公开统计，借用单位id、开始时间、结束时间字段查询
     *
     * @param queryVO
     * @return
     * @author fangtinghua
     */
    @ResponseBody
    @RequestMapping("applyTj")
    public Object applyTj(PublicContentQueryVO queryVO) {
        return getObject(publicApplyService.getPublicTotalVO(queryVO));
    }
}