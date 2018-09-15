/*
 * FontUtil.java         2016年9月18日 <br/>
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

package cn.lonsun.util;

import java.awt.Font;
import java.io.File;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.servlet.ServletContext;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.FileFileFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cn.lonsun.GlobalConfig;
import cn.lonsun.core.util.SpringContextHolder;

/**
 * 字体工具类 <br/>
 * 
 * @date 2016年9月18日 <br/>
 * @author fangtinghua <br/>
 * @version v1.0 <br/>
 */
public class FontUtil {

    // 日志
    private static final Logger logger = LoggerFactory.getLogger(FontUtil.class);
    private static Boolean isInit = false;
    private static GlobalConfig config = SpringContextHolder.getBean(GlobalConfig.class);
    private static Map<String, Font> cacheMap = new HashMap<String, Font>();

    private static void init() {
        if (!isInit) {
            synchronized (isInit) {
                if (!isInit) {
                    try {
                        String fontPath = config.getFontPath();
                        ServletContext context = LoginPersonUtil.getRequest().getServletContext();
                        String filePath = context.getRealPath(fontPath);
                        // 获取文件列表
                        File directory = new File(filePath);
                        Iterator<File> fileList = FileUtils.iterateFiles(directory, FileFileFilter.FILE, null);
                        while (fileList.hasNext()) {
                            File file = fileList.next();
                            Font font = Font.createFont(Font.TRUETYPE_FONT, file);
                            cacheMap.put(file.getName(), font);
                        }
                        isInit = true;
                    } catch (Throwable e) {
                        logger.error("字体初始化失败！", e);
                    }
                }
            }
        }
    }

    public static Font buildFont(String name, int style, int size) {
        init();
        if (!cacheMap.containsKey(name)) {
            name = "simsun.ttc";// 默认字体
        }
        Font font = cacheMap.get(name);
        return font.deriveFont(style, size);
    }
}