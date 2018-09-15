/*
 * PathConfig.java         2015年11月27日 <br/>
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

package cn.lonsun.staticcenter.generate;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * 配置参数 <br/>
 *
 * @date 2015年11月27日 <br/>
 * @author fangtinghua <br/>
 * @version v1.0 <br/>
 */
@Component
public class PathConfig {

    @Value("${html.create.path}")
    private String createPath;// 生成静态文件绝对地址
    @Value("${html.file.prefix}")
    private String filePrefix;// 生成文件前缀
    @Value("${fileServerPath}")
    private String fileServerPath;// 文件服务器地址
    @Value("${fileServerNamePath}")
    private String fileServerNamePath;// 文件服务器地址(按照文件名称读取文件)
    @Value("${process.label}")
    private String processLabel; // 预处理标签列表

    public String getCreatePath() {
        return createPath;
    }

    public void setCreatePath(String createPath) {
        this.createPath = createPath;
    }

    public String getFilePrefix() {
        return filePrefix;
    }

    public void setFilePrefix(String filePrefix) {
        this.filePrefix = filePrefix;
    }

    public String getFileServerPath() {
        return fileServerPath;
    }

    public void setFileServerPath(String fileServerPath) {
        this.fileServerPath = fileServerPath;
    }

    public String getProcessLabel() {
        return processLabel;
    }

    public void setProcessLabel(String processLabel) {
        this.processLabel = processLabel;
    }

    public String getFileServerNamePath() {
        return fileServerNamePath;
    }

    public void setFileServerNamePath(String fileServerNamePath) {
        this.fileServerNamePath = fileServerNamePath;
    }
}