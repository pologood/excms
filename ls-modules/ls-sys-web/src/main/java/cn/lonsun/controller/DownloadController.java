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

package cn.lonsun.controller;

import java.io.File;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import cn.lonsun.cache.client.CacheHandler;
import cn.lonsun.content.internal.entity.BaseContentEO;
import cn.lonsun.core.exception.BaseRunTimeException;
import cn.lonsun.core.util.TipsMode;
import cn.lonsun.mongodb.base.IMongoDbFileServer;

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

    @RequestMapping("{mongoId}")
    public void download(@PathVariable String mongoId, HttpServletRequest request, HttpServletResponse response) {
        mongoDbFileServer.downloadFile(response, mongoId, null);
    }

    @RequestMapping("/attach/{id}")
    public ResponseEntity<byte[]> download(@PathVariable Long id) {
        try {
            BaseContentEO eo = CacheHandler.getEntity(BaseContentEO.class, id);
            String realName = eo.getAttachRealName();
            String savedName = eo.getAttachSavedName();
            File file = new File(attach + savedName.substring(0, 8) + File.separator + savedName);
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
            headers.setContentDispositionFormData("attachment", new String(realName.getBytes("UTF-8"), "ISO8859-1"));
            return new ResponseEntity<byte[]>(FileUtils.readFileToByteArray(file), headers, HttpStatus.CREATED);
        } catch (Throwable e) {
            throw new BaseRunTimeException(TipsMode.Message.toString(), "文件下载失败！");
        }
    }
}