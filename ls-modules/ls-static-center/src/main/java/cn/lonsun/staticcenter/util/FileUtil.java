/*
 * FileUtil.java         2016年1月19日 <br/>
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

package cn.lonsun.staticcenter.util;

import cn.lonsun.message.internal.entity.MessageEnum;
import cn.lonsun.staticcenter.eo.FileEO;
import cn.lonsun.staticcenter.generate.GenerateException;
import cn.lonsun.staticcenter.generate.thread.Context;
import cn.lonsun.staticcenter.generate.thread.ContextHolder;
import cn.lonsun.staticcenter.generate.util.PathUtil;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

/**
 * 文件生成工具类 <br/>
 *
 * @author fangtinghua <br/>
 * @version v1.0 <br/>
 * @date 2016年1月19日 <br/>
 */
public class FileUtil {
    // 日志
    private static final Logger logger = LoggerFactory.getLogger("staticGen");

    /**
     * 生成文件
     *
     * @param fileEO
     * @return
     * @throws GenerateException
     * @author fangtinghua
     */
    public static boolean generateFile(FileEO fileEO) {
        // 结果
        boolean result = true;
        Context context = ContextHolder.getContext();
        String fileType = context.getFileType();// 文件类型 html or js
        Long type = context.getType();// 类型 发布or取消发布
        // 文件分发
        String fileName = fileEO.getName();
        String filePath = fileEO.getPath();
        // 生成文件夹时替换掉文件夹不支持字符
        String[] searchList = {"http://", "https://"};
        String[] replaceList = {"", ""}; // 替换的字符串数组
        String domain = StringUtils.replaceEach(context.getUri(), searchList, replaceList);
        // 构造文件路径
        StringBuffer path = new StringBuffer();
        path.append(PathUtil.getPathConfig().getCreatePath()).append(PathUtil.SEPARATOR);
        path.append(domain).append(PathUtil.SEPARATOR);
        path.append(filePath).append(PathUtil.SEPARATOR);
        path.append(fileName);
        // 创建文件
        File file = new File(path.toString());
        if (MessageEnum.PUBLISH.value().equals(type)) {// 写入本地地址和nfs挂载方式一样，都是在写入本地文件
            try {
                String content = fileEO.getContent();
                if (Context.FileType.JS.toString().equals(fileType)) {// js文件需要替换掉回车换行符
                    content = StringUtils.replacePattern(content, "[\\t\\n\\r]", "");
                }
                FileUtils.writeStringToFile(file, content, "utf-8");
                logger.info("生成文件：{}", path.toString());
            } catch (Throwable e) {
                result = false;
                logger.error("文件生成失败，" + path.toString(), e);
                throw new GenerateException("文件生成失败.", e);
            }
        } else if (MessageEnum.UNPUBLISH.value().equals(type)) {// 表示取消发布、需要删除文件
            // 为文件且不为空则进行删除
            try {
                if (file.isFile()){
                    if(file.exists()){
                        result = file.delete();
                        logger.info("删除文件：{}", path.toString());
                    }
                }
            } catch (Throwable e) {
                result = false;
                logger.error("文件删除失败，" + path.toString(), e);
                throw new GenerateException("文件删除失败.", e);
            }
        }
        return result;
    }
}