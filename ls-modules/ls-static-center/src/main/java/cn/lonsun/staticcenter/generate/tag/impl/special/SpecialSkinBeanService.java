/*
 * NavBeanService.java         2015年9月15日 <br/>
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

package cn.lonsun.staticcenter.generate.tag.impl.special;

import cn.lonsun.special.internal.entity.SpecialEO;
import cn.lonsun.special.internal.service.ISpecialService;
import cn.lonsun.staticcenter.generate.tag.impl.AbstractBeanService;
import cn.lonsun.staticcenter.generate.thread.Context;
import cn.lonsun.staticcenter.generate.thread.ContextHolder;
import cn.lonsun.staticcenter.generate.util.AssertUtil;
import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 专题皮肤 <br/>
 *
 * @author doocal <br/>
 * @version v1.0 <br/>
 * @date 2017年4月17日 <br/>
 */
@Component
public class SpecialSkinBeanService extends AbstractBeanService {

    @Autowired
    private ISpecialService specialService;

    @Override
    public Object getObject(JSONObject paramObj) {
        Context context = ContextHolder.getContext();
        Long specialId = paramObj.getLong("specialId");
        AssertUtil.isEmpty(specialId, "专题皮肤标签ID配置信息不能为空！");
        SpecialEO specialEO = specialService.getById(specialId);
        return specialEO;
    }

    @Override
    @SuppressWarnings("unchecked")
    public String objToStr(String content, Object resultObj, JSONObject paramObj) {
        SpecialEO specialEO = (SpecialEO) resultObj;
        return specialEO.getDefaultSkin();
    }
}