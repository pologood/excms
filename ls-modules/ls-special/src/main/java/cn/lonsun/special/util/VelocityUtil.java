/*
 * VelocityUtil.java         2015年11月27日 <br/>
 *
 * Copyright (c) 1994-1999 AnHui LonSun, Inc. <br/>
 * All rights reserved. <br/>
 *
 * This software is the confidential and proprietary information of AnHui   <br/>
 * LonSun, Inc. ("Confidential Information").  You shall not    <br/>
 * disclose such Confidential Information and shall use it only in  <br/>
 * accordance with the terms of the license agreement you entered into  <br/>
 * with Sun. <br/>
 */
package cn.lonsun.special.util;

import cn.lonsun.core.exception.BaseRunTimeException;
import cn.lonsun.core.util.SpringContextHolder;
import cn.lonsun.velocity.RuntimeServices;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.runtime.resource.Resource;
import org.springframework.ui.velocity.VelocityEngineFactoryBean;

import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;

/**
 * velocity工具类 ADD REASON. <br/>
 *
 * @author fangtinghua
 * @date: 2015年11月27日 上午10:08:26 <br/>
 */
public class VelocityUtil {
    // velocity工厂
    private static VelocityEngineFactoryBean factory = SpringContextHolder.getBean(VelocityEngineFactoryBean.class);
    private static VelocityEngine engine = factory.getObject();

    /**
     * 根据模板生成字符串
     *
     * @param vm
     * @param dataMap
     * @return
     * @author fangtinghua
     */
    public static String mergeTemplate(String vm, Map<String, Object> dataMap) {
        return mergeString(getTemplateContent(vm), dataMap);
    }

    /**
     * 获取模板文件内容
     *
     * @param vm
     * @return
     * @author fangtinghua
     */
    public static String getTemplateContent(String vm) {
        try {
            Resource resource = RuntimeServices.getRuntimeServices().getContent(vm + ".vm", "utf-8");
            return (String) resource.getData();
        } catch (Throwable e) {
            throw new BaseRunTimeException("模板文件不存在！");
        }
    }

    /**
     * 根据字符串生成字符串
     *
     * @param content
     * @param dataMap
     * @return
     * @author fangtinghua
     */
    public static String mergeString(String content, Map<String, Object> dataMap) {
        if (null == dataMap) {
            dataMap = new HashMap<String, Object>();
        }
        VelocityContext velocityContext = new VelocityContext(dataMap);// 构建上下文
        try {
            StringWriter writer = new StringWriter();
            boolean success = engine.evaluate(velocityContext, writer, "String", content);// 解析
            return writer.toString();
        } catch (Throwable e) {
            throw new BaseRunTimeException("velocity内容解析错误！");
        }
    }
}