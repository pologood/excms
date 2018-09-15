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

import org.springframework.stereotype.Component;

import cn.lonsun.staticcenter.generate.GenerateException;
import cn.lonsun.staticcenter.generate.tag.impl.AbstractBeanService;
import cn.lonsun.util.DataDictionaryUtil;

import com.alibaba.fastjson.JSONObject;

/**
 * 身份类型 <br/>
 *
 * @date 2016年7月14日 <br/>
 * @author fangtinghua <br/>
 * @version v1.0 <br/>
 */
@Component
public class CardBeanService extends AbstractBeanService {

    @Override
    public Object getObject(JSONObject paramObj) throws GenerateException {
        // 查询数据字典
        return DataDictionaryUtil.getDDList("system_cardType");
    }
}