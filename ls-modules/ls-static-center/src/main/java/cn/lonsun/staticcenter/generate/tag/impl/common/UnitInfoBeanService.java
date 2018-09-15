/*
 * CardBeanService.java         2016年7月14日 <br/>
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

import cn.lonsun.common.util.AppUtil;
import cn.lonsun.rbac.internal.entity.OrganEO;
import cn.lonsun.rbac.internal.service.IOrganService;
import cn.lonsun.staticcenter.generate.GenerateException;
import cn.lonsun.staticcenter.generate.tag.impl.AbstractBeanService;
import cn.lonsun.staticcenter.generate.thread.Context;
import cn.lonsun.staticcenter.generate.thread.ContextHolder;
import cn.lonsun.util.DataDictionaryUtil;
import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 通过部门Id获取当前部门的直属上级单位信息 <br/>
 *
 * @date 2016年9月12日 <br/>
 * @author liukun <br/>
 * @version v1.0 <br/>
 */
@Component
public class UnitInfoBeanService extends AbstractBeanService {

    @Autowired
    private IOrganService organService;

    @Override
    public Object getObject(JSONObject paramObj) throws GenerateException {
        Context context = ContextHolder.getContext();
        Long organId = paramObj.getLong("organId");

        OrganEO organ = organService.getDirectlyUpLevelUnit(organId);

        return organ;
    }
}