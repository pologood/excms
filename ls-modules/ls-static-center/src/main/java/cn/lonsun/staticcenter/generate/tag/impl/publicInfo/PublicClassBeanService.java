/*
 * PublicClassBeanService.java         2015年12月29日 <br/>
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

package cn.lonsun.staticcenter.generate.tag.impl.publicInfo;

import java.util.List;

import org.springframework.stereotype.Component;

import cn.lonsun.cache.client.CacheGroup;
import cn.lonsun.cache.client.CacheHandler;
import cn.lonsun.publicInfo.internal.entity.PublicClassEO;
import cn.lonsun.staticcenter.generate.GenerateConstant;
import cn.lonsun.staticcenter.generate.tag.impl.AbstractBeanService;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

/**
 * TODO <br/>
 *
 * @date 2015年12月29日 <br/>
 * @author fangtinghua <br/>
 * @version v1.0 <br/>
 */
@Component
public class PublicClassBeanService extends AbstractBeanService {

    /**
     * 修改根据父id调用
     * 
     * @see cn.lonsun.staticcenter.generate.tag.BeanService#getObject(com.alibaba.fastjson.JSONObject)
     */
    @Override
    public Object getObject(JSONObject paramObj) {
        Long parentId = paramObj.getLong(GenerateConstant.ID);// 父id
        List<PublicClassEO> obj = null == parentId ? null : CacheHandler.getList(PublicClassEO.class, CacheGroup.CMS_PARENTID, parentId);
        return (null == obj || obj.isEmpty()) ? null : JSON.toJSONString(obj);
    }
}