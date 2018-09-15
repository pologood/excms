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

import cn.lonsun.cache.client.CacheHandler;
import cn.lonsun.content.internal.entity.BaseContentEO;
import cn.lonsun.core.exception.BaseRunTimeException;
import cn.lonsun.core.util.TipsMode;
import cn.lonsun.mongodb.base.IMongoDbFileServer;
import cn.lonsun.system.filecenter.internal.entity.FileCenterEO;
import cn.lonsun.system.filecenter.internal.service.IFileCenterService;
import com.mongodb.gridfs.GridFSDBFile;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.annotation.Resource;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 文件下载 <br/>
 *
 * @author fangtinghua <br/>
 * @version v1.0 <br/>
 * @date 2016年5月23日 <br/>
 */
@Controller
@RequestMapping(value = "download")
public class DownloadController {

    @Value("${attach.path}")
    private String attach;// 附件文件路径
    @Resource
    private IMongoDbFileServer mongoDbFileServer;
    @Resource
    private IFileCenterService fileCenterService;

    @RequestMapping("{mongoId}")
    public void download(@PathVariable String mongoId, HttpServletRequest request, HttpServletResponse response) {
        GridFSDBFile gridFSDBFile = mongoDbFileServer.getGridFSDBFile(mongoId,null);
        if(gridFSDBFile.getContentType().equals("pdf")) {//pdf预览
            mongoDbFileServer.viewFile(response,gridFSDBFile);
        } else {//文件下载
            mongoDbFileServer.downloadFile(response, mongoId, null);
        }
    }

    @RequestMapping("/mongoname/{mongoname}")
    public void downloadByFileName(@PathVariable String mongoname, HttpServletRequest request, HttpServletResponse response) {
        FileCenterEO fileCenter = fileCenterService.getByMongoName(mongoname);
        if(null != fileCenter){
            GridFSDBFile gridFSDBFile = mongoDbFileServer.getGridFSDBFile(fileCenter.getMongoId(),null);
            if(gridFSDBFile.getContentType().equals("pdf")) {//pdf预览
                mongoDbFileServer.viewFile(response,gridFSDBFile);
            } else {//文件下载
                mongoDbFileServer.downloadFile(response, fileCenter.getMongoId(), null);
            }
        }else{
            throw new BaseRunTimeException(TipsMode.Message.toString(),"文件不存在");
        }
    }

    @RequestMapping("/attach/{id}")
    public void download(@PathVariable Long id, @RequestParam(defaultValue = "") String owner, HttpServletRequest request, HttpServletResponse response) {
        try {
            BaseContentEO eo = CacheHandler.getEntity(BaseContentEO.class, id);
            String realName = eo.getAttachRealName();
            String savedName = eo.getAttachSavedName();
            response.setContentType("application/octet-stream");
            File file = null;
            if ("xiaoxian".equals(owner)) {
                file = new File(attach + File.separator + savedName);
            } else {
                file = new File(attach + savedName.substring(0, 8) + File.separator + savedName);
            }
            String agent = request.getHeader("User-Agent").toLowerCase();
            if (!StringUtils.isEmpty(agent) && agent.indexOf("firefox") > 0) {
                response.addHeader("Content-Disposition", "attachment;filename=" + new String(realName.getBytes("GB2312"), "ISO-8859-1"));
            } else {
                response.addHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode(realName, "UTF-8"));
            }
            FileInputStream inputStream = new FileInputStream(file);
            ServletOutputStream out;
            out = response.getOutputStream();
            int b = 0;
            byte[] buffer = new byte[1024];
            while (b != -1) {
                b = inputStream.read(buffer);
                out.write(buffer, 0, b);
            }
            inputStream.close();
            out.close();
            out.flush();
        } catch (Exception e) {
            throw new BaseRunTimeException(TipsMode.Message.toString(), "文件下载失败！");
        }
    }
}