/*
 * ColumnTreeBeanService.java         2015年9月22日 <br/>
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

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;

import cn.lonsun.cache.client.CacheGroup;
import cn.lonsun.cache.client.CacheHandler;
import cn.lonsun.indicator.internal.entity.IndicatorEO;
import cn.lonsun.site.site.internal.entity.ColumnMgrEO;
import cn.lonsun.staticcenter.generate.GenerateConstant;
import cn.lonsun.staticcenter.generate.GenerateException;
import cn.lonsun.staticcenter.generate.tag.impl.AbstractBeanService;
import cn.lonsun.staticcenter.generate.thread.Context;
import cn.lonsun.staticcenter.generate.thread.ContextHolder;
import cn.lonsun.staticcenter.generate.util.PathUtil;

import com.alibaba.fastjson.JSONObject;

/**
 * 栏目左侧属性结构标签 <br/>
 *
 * @date 2015年9月22日 <br/>
 * @author fangtinghua <br/>
 * @version v1.0 <br/>
 */
@Component
public class ColumnTreeBeanService extends AbstractBeanService {

    @Override
    public Object getObject(JSONObject paramObj) {
        Context context = ContextHolder.getContext();
        Long columnId = context.getColumnId();
        boolean isChild = paramObj.getBoolean(GenerateConstant.IS_CHILD);
        if (isChild) {// 需要查询子列表
            return CacheHandler.getList(ColumnMgrEO.class, CacheGroup.CMS_PARENTID, columnId);
        }
        // 查询和本栏目同级别栏目列表
        IndicatorEO eo = CacheHandler.getEntity(IndicatorEO.class, columnId);// 本栏目对象
        return CacheHandler.getList(ColumnMgrEO.class, CacheGroup.CMS_PARENTID, eo.getParentId());
    }

    @Override
    @SuppressWarnings("unchecked")
    public Map<String, Object> doProcess(Object resultObj, JSONObject paramObj) throws GenerateException {
        // 预处理数据，处理栏目页链接问题
        List<ColumnMgrEO> list = (List<ColumnMgrEO>) resultObj;
        if (null != list && !list.isEmpty()) {
            // 处理栏目链接
            for (ColumnMgrEO eo : list) {
                String path ="";
                if(eo.getIsStartUrl()==1){
                    path=eo.getTransUrl();
                }else{
                    PathUtil.getLinkPath(eo.getIndicatorId(), null);
                }
                eo.setUri(path);
            }
        }
        return super.doProcess(resultObj, paramObj);
    }
}