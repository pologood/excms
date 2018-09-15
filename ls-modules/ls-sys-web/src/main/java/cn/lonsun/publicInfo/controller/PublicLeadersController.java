/*
 * PublicContentController.java         2015年12月15日 <br/>
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

import cn.lonsun.common.util.AppUtil;
import cn.lonsun.common.util.FileUtil;
import cn.lonsun.core.base.controller.BaseController;
import cn.lonsun.core.util.Pagination;
import cn.lonsun.mongodb.entity.ContentMongoEO;
import cn.lonsun.mongodb.service.ContentMongoServiceImpl;
import cn.lonsun.mongodb.vo.MongoFileVO;
import cn.lonsun.publicInfo.internal.entity.PublicLeadersEO;
import cn.lonsun.publicInfo.internal.service.IPublicLeadersService;
import cn.lonsun.publicInfo.vo.PublicLeadersQueryVO;
import cn.lonsun.rbac.internal.entity.OrganEO;
import cn.lonsun.rbac.internal.service.IOrganService;
import cn.lonsun.site.site.internal.entity.WaterMarkConfigEO;
import cn.lonsun.site.site.internal.service.IWaterMarkConfigService;
import cn.lonsun.system.filecenter.internal.entity.FileCenterEO;
import cn.lonsun.util.FileUploadUtil;
import cn.lonsun.util.ImgHander;
import cn.lonsun.util.LoginPersonUtil;
import cn.lonsun.util.WaterMarkUtil;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 单位领导 <br/>
 *
 * @author liukun <br/>
 * @version v1.0 <br/>
 * @date 2016年9月19日 <br/>
 */
@Controller
@RequestMapping("/public/leaders")
public class PublicLeadersController extends BaseController {

    @Resource
    private IOrganService organService;

    @Resource
    private IPublicLeadersService publicLeadersService;

    @Resource
    private IWaterMarkConfigService waterMarkConfigService;

    @Resource
    private ContentMongoServiceImpl contentMongoService;

    @RequestMapping("list")
    public String index(String type, ModelMap map) {
        map.put("type", type);
        return "/public/leaders/list";
    }

    @RequestMapping("admin_list")
    public String content(String type, Long organId, Long catId, ModelMap map) {
        map.put("type", type);
        map.put("organId", organId);
        map.put("catId", catId);
        return "/public/leaders/admin_list";
    }

    @RequestMapping("addOrEdit")
    public String addOrEdit(Long leadersId, ModelMap map) {
        map.put("leadersId", leadersId);
        map.put("siteId", LoginPersonUtil.getSiteId());
        return "/public/leaders/edit";
    }

    @RequestMapping("admin_addOrEdit")
    public String admin_addOrEdit(Long leadersId, ModelMap map) {
        map.put("leadersId", leadersId);
        map.put("siteId", LoginPersonUtil.getSiteId());
        return "/public/leaders/admin_edit";
    }

    @RequestMapping("getPage")
    @ResponseBody
    public Object getPage(PublicLeadersQueryVO queryVO) {
        queryVO.setOrganId(LoginPersonUtil.getUnitId());
        Pagination page = publicLeadersService.getPagination(queryVO);
        return page;
    }


    @RequestMapping("getAdminPage")
    @ResponseBody
    public Object getAdminPage(PublicLeadersQueryVO queryVO) {
        Pagination page = publicLeadersService.getPagination(queryVO);
        return page;
    }

    @RequestMapping("getLeadersVO")
    @ResponseBody
    public Object getLeadersVO(Long id) {
        Map<String, Object> map = new HashMap<String, Object>();
        PublicLeadersEO leaders = publicLeadersService.getEntity(PublicLeadersEO.class, id);
        Criteria criteria = Criteria.where("_id").is(id);
        Query query = new Query(criteria);
        ContentMongoEO _eo = contentMongoService.queryOne(query);
        String content = "";
        if (!AppUtil.isEmpty(_eo)) {
            content = _eo.getContent();
        }
        leaders.setExperience(content);
        map.put("leaders", leaders);
        return getObject(map);
    }

    @RequestMapping("getOrgans")
    @ResponseBody
    public Object getOrgans() {
        List<OrganEO> organs = organService.getUnits();
        return getObject(organs);
    }


    /**
     * @return
     * @Description 保存领导信息
     * @author liukun
     * @date 2016年9月21日 下午4:10:30
     */
    @RequestMapping("saveLeaders")
    @ResponseBody
    public Object saveLeaders(PublicLeadersEO publicLeadersEO) {

        Long id = publicLeadersService.saveOrUpdateLeaders(publicLeadersEO);

        return getObject(id);
    }

    /**
     * @return
     * @Description 超级管理员保存领导信息
     * @author liukun
     * @date 2016年9月21日 下午4:10:30
     */
    @RequestMapping("admin_saveLeaders")
    @ResponseBody
    public Object admin_saveLeaders(PublicLeadersEO publicLeadersEO) {

        Long id = publicLeadersService.admin_saveOrUpdateLeaders(publicLeadersEO);

        return getObject(id);
    }

    /**
     * @return
     * @Description 启用或者禁用领导信息
     * @author liukun
     * @date 2016年9月21日 下午4:10:30
     */
    @RequestMapping("disOrEnable")
    @ResponseBody
    public Object disOrEnable(Long leadersId) {

        PublicLeadersEO eo = publicLeadersService.getEntity(PublicLeadersEO.class, leadersId);
        String status = eo.getStatus();
        if (!AppUtil.isEmpty(status) && status.equals(PublicLeadersEO.Status.Removed.toString())) {
            eo.setStatus(PublicLeadersEO.Status.Normal.toString());//启用
        } else {
            eo.setStatus(PublicLeadersEO.Status.Removed.toString());//禁用
        }
        publicLeadersService.updateEntity(eo);
        return getObject();
    }

    /**
     * @return
     * @Description 删除领导信息
     * @author liukun
     * @date 2016年9月21日 下午4:10:30
     */
    @RequestMapping("deleteLeaders")
    @ResponseBody
    public Object deleteLeaders(@RequestParam("leadersId[]") Long[] leadersId) {

        publicLeadersService.delete(PublicLeadersEO.class, leadersId);
        return getObject();
    }


    /**
     * @return String 返回类型
     * @throws
     * @Title: normalUploadThumb
     * @Description: 普通上传缩略图
     */
    @RequestMapping("normalUploadThumb")
    @ResponseBody
    public Object normalUploadThumb(MultipartFile Filedata, Long siteId, Long contentId, String imgLink, String sessionId) {
        if (null == siteId) {
            siteId = LoginPersonUtil.getSiteId();
        }
        String fileName = Filedata.getOriginalFilename();
        String suffix = FileUtil.getType(fileName);

        MongoFileVO _vo = null;
        try {
            InputStream in = Filedata.getInputStream();
            WaterMarkConfigEO eo = waterMarkConfigService.getConfigBySiteId(siteId);

            byte[] b = null;
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            if (eo != null && eo.getEnableStatus() == 1) {
                ImgHander.ImgTrans(in, 295, 412, baos, suffix);
                in = new ByteArrayInputStream(baos.toByteArray());
                b = WaterMarkUtil.createWaterMark(in, siteId, suffix);
            } else {
                ImgHander.ImgTrans(in, 295, 412, baos, suffix);
                b = baos.toByteArray();
            }

            _vo =
                FileUploadUtil.uploadUtil(b, fileName, FileCenterEO.Type.Image.toString(), FileCenterEO.Code.ThumbUpload.toString(), siteId, null,
                    contentId, "领导头像", sessionId);// mongoDbFileServer.uploadByteFile(b,
            // fileName,
            // contentId, null);


            baos.flush();
            baos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return _vo;
    }
}