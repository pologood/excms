/*
 * FileCenterController.java         2016年5月23日 <br/>
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
import cn.lonsun.core.util.IpUtil;
import cn.lonsun.mongodb.base.IMongoDbFileServer;
import cn.lonsun.mongodb.vo.MongoFileVO;
import cn.lonsun.system.filecenter.internal.entity.FileCenterEO;
import cn.lonsun.system.filecenter.internal.service.IFileCenterService;
import cn.lonsun.util.LoginPersonUtil;
import cn.lonsun.util.MD5Util;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 文件上传 <br/>
 *
 * @author doocal <br/>
 * @version v1.0 <br/>
 */
@Controller
@RequestMapping(value = "upload")
public class FrontUploadController extends BaseController {

    private static Logger logger = LoggerFactory.getLogger(FrontUploadController.class);

    @Resource
    private IMongoDbFileServer mongoDbFileServer;

    @Autowired
    private IFileCenterService fileCenterService;

    /**
     * 上传首页
     *
     * @return
     */
    @RequestMapping("index")
    public String index(Long siteId, Long columnId, Model model) {
        model.addAttribute("siteId", siteId);
        model.addAttribute("columnId", columnId);
        return "/uploadPage";
    }

    /**
     * 新上传首页
     *
     * @return
     */
    @RequestMapping("webUploader")
    public String webUploader(Long siteId, Long columnId, Model model) {
        model.addAttribute("siteId", siteId);
        model.addAttribute("columnId", columnId);
        return "/webUploader";
    }

    /**
     * 上传首页
     *
     * @return
     */
    @RequestMapping("index2")
    public String index2(Long siteId, Long columnId, Model model) {
        model.addAttribute("siteId", siteId);
        model.addAttribute("columnId", columnId);
        return "/uploadPage2";
    }

    @RequestMapping("fileUpload")
    @ResponseBody
    public Object fileUpload(MultipartFile Filedata, HttpServletRequest request, Long siteId) {

        /**
         * 参数自动注入失败，现采用手动设置方式
         */
        FileCenterEO fileEO = new FileCenterEO();
        fileEO.setColumnId(AppUtil.getLong(request.getParameter("columnId")));
        fileEO.setSiteId(AppUtil.getLong(request.getParameter("siteId")));
        fileEO.setType(AppUtil.getValue(request.getParameter("type")));
        fileEO.setCode(AppUtil.getValue(request.getParameter("code")));
        fileEO.setContentId(AppUtil.getLong(request.getParameter("contentId")));
        fileEO.setDesc(AppUtil.getValue(request.getParameter("desc")));

        if (Filedata.isEmpty()) {
            return ajaxErr("文件上传失败(File upload failed)");
        }

        if (AppUtil.isEmpty(siteId)) {
            return ajaxErr("站点ID不能为空");
        }

        MongoFileVO mongoEO = mongoDbFileServer.uploadMultipartFile(Filedata, null);
        String fileName = Filedata.getOriginalFilename();
        String singleName = fileName.substring(0, fileName.lastIndexOf("."));
        String suffix = fileName.substring(fileName.lastIndexOf(".") + 1, fileName.length());
        try {
            fileEO.setMd5(MD5Util.getMd5ByByte(Filedata.getBytes()));
        } catch (IOException e) {
            e.printStackTrace();
        }
        fileEO.setFileSize(Filedata.getSize());
        fileEO.setFileName(singleName);
        fileEO.setSuffix(suffix);
        fileEO.setMongoId(mongoEO.getMongoId());
        fileEO.setMongoName(mongoEO.getFileName());
        fileEO.setStatus(0);
        fileEO.setSiteId(siteId);
        //fileEO.setCreateUserId((Long) session.getAttribute("userId"));
        //fileEO.setCreateOrganId((Long) session.getAttribute("organId"));

        if (AppUtil.isEmpty(fileEO.getType())) {
            fileEO.setType(FileCenterEO.Type.NotDefined.toString());
        }
        if (AppUtil.isEmpty(fileEO.getCode())) {
            fileEO.setType(FileCenterEO.Code.Default.toString());
        }
        if (AppUtil.isEmpty(fileEO.getDesc())) {
            fileEO.setDesc("默认端口上传的附件");
        }
        HttpServletRequest request2 = LoginPersonUtil.getRequest();
        fileEO.setIp(IpUtil.getIpAddr(request2));
        fileCenterService.saveEntity(fileEO);
        logger.info("File upload successed ,mongoid: >>> " + mongoEO.getMongoId());
        return mongoEO;
    }

    @RequestMapping("deleteByMongoIds")
    @ResponseBody
    public Object deleteByMongoIds(@RequestParam(value = "mongoIds[]", required = false) String[] mongoIds) {
        fileCenterService.deleteByMongoId(mongoIds);
        return getObject();
    }

    @RequestMapping("deleteByMongoId")
    @ResponseBody
    public Object deleteByMongoId(String mongoId) {
        fileCenterService.deleteByMongoId(new String[]{mongoId});
        return getObject();
    }

    @RequestMapping("download")
    public void download(String mongoId, HttpServletRequest request, HttpServletResponse response) {
        mongoDbFileServer.downloadFile(response, mongoId, null);
    }

}