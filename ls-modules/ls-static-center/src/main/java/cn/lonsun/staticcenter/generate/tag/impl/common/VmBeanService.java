/*
 * HtmlBeanService.java         2016年7月12日 <br/>
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

package cn.lonsun.staticcenter.generate.tag.impl.common;

import org.springframework.stereotype.Component;

import cn.lonsun.staticcenter.generate.GenerateConstant;
import cn.lonsun.staticcenter.generate.GenerateException;
import cn.lonsun.staticcenter.generate.tag.impl.AbstractBeanService;
import cn.lonsun.staticcenter.generate.util.RegexUtil;
import cn.lonsun.staticcenter.generate.util.VelocityUtil;

import com.alibaba.fastjson.JSONObject;

/**
 * 直接使用后台vm文件解析 <br/>
 *
 * @date 2016年7月12日 <br/>
 * @author fangtinghua <br/>
 * @version v1.0 <br/>
 */
@Component
public class VmBeanService extends AbstractBeanService {

    @Override
    public Object getObject(JSONObject paramObj) throws GenerateException {
        return paramObj.getString(GenerateConstant.FILE);// 模板文件名
    }

    @Override
    public String objToStr(String content, Object resultObj, JSONObject paramObj) throws GenerateException {
        String file = paramObj.getString(GenerateConstant.FILE);
        return RegexUtil.parseContent(VelocityUtil.getTemplateContent(file));
    }
}