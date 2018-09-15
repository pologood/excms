/*
 * IncludeBeanService.java         2015年9月9日 <br/>
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

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import cn.lonsun.staticcenter.generate.GenerateConstant;
import cn.lonsun.staticcenter.generate.GenerateException;
import cn.lonsun.staticcenter.generate.tag.BeanService;
import cn.lonsun.staticcenter.generate.tag.impl.AbstractBeanService;
import cn.lonsun.staticcenter.generate.thread.ContextHolder;
import cn.lonsun.staticcenter.generate.util.LabelUtil;
import cn.lonsun.staticcenter.generate.util.MongoUtil;
import cn.lonsun.staticcenter.generate.util.RegexUtil;

import com.alibaba.fastjson.JSONObject;

/**
 * 文件包含 <br/>
 *
 * @date 2015年9月9日 <br/>
 * @author fangtinghua <br/>
 * @version v1.0 <br/>
 */
@Component
public class IncludeBeanService extends AbstractBeanService {

    @Override
    public Object getObject(JSONObject paramObj) {
        // file填写的值为模板文件id
        return paramObj.getLong(GenerateConstant.ID);
    }

    @Override
    public String objToStr(String content, Object resultObj, JSONObject paramObj) throws GenerateException {
        Long tmpId = (Long) resultObj;
        // 根据id去mongodb读取文件内容
        // 当动态生成时，所有标签全部要解析
        boolean dynamic = StringUtils.isNotEmpty(ContextHolder.getContext().getModule());
        return RegexUtil.parseContent(MongoUtil.queryCacheById(tmpId), dynamic ? null : LabelUtil.getProcessLabel());
    }
}