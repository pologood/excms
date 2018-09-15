/*
 * AbstactBeanService.java         2015年11月24日 <br/>
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

package cn.lonsun.staticcenter.generate.tag.impl;

import cn.lonsun.cache.client.CacheHandler;
import cn.lonsun.indicator.internal.entity.IndicatorEO;
import cn.lonsun.message.internal.entity.MessageEnum;
import cn.lonsun.staticcenter.generate.GenerateConstant;
import cn.lonsun.staticcenter.generate.GenerateException;
import cn.lonsun.staticcenter.generate.tag.BeanService;
import cn.lonsun.staticcenter.generate.thread.Context;
import cn.lonsun.staticcenter.generate.thread.ContextHolder;
import cn.lonsun.staticcenter.generate.util.VelocityUtil;
import cn.lonsun.util.ColumnUtil;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 标签处理抽象类，用来实现默认的预处理对象 <br/>
 *
 * @author fangtinghua <br/>
 * @version v1.0 <br/>
 * @date 2015年11月24日 <br/>
 */
public abstract class AbstractBeanService implements BeanService {

    /**
     * 根据isChild属性过滤出需要查询的栏目数组，传入的是需要过滤的栏目类型
     *
     * @param paramObj
     * @param typeCode
     * @return
     * @author fangtinghua
     */
    public Long[] getQueryColumnIdByChild(JSONObject paramObj, String typeCode) {
        Context context = ContextHolder.getContext();
        Long columnId = context.getColumnId();
        // 栏目id要以标签传入的优先级最大
        String id = paramObj.getString(GenerateConstant.ID);
        boolean idEmpty = StringUtils.isEmpty(id);
        if (idEmpty && null == columnId) {
            return null;
        }
        Boolean isChild = paramObj.getBoolean(GenerateConstant.IS_CHILD);
        return ColumnUtil.getQueryColumnIdByChild(idEmpty ? String.valueOf(columnId) : id, isChild, typeCode);
    }

    /**
     * 根据isChild属性过滤出需要查询的栏目数组，传入的是需要过滤的栏目类型
     *
     * @param columnId
     * @param typeCode
     * @return
     * @author fangtinghua
     */
    public Long[] getQueryColumnIdByChild(String columnId, String typeCode) {
        return ColumnUtil.getQueryColumnIdByChild(columnId, true, typeCode);
    }

    /**
     * 前置查询
     * 
     * @param paramObj
     * 
     * @see cn.lonsun.staticcenter.generate.tag.BeanService#before(com.alibaba.fastjson.JSONObject)
     */
    @Override
    public boolean before(JSONObject paramObj) throws GenerateException {
        return false;// 默认没有查询
    }

    /**
     * 预处理数据结果，包括查询其他数据
     *
     * @param resultObj
     * @param paramObj
     * @return
     * @author fangtinghua
     */
    public Map<String, Object> doProcess(Object resultObj, JSONObject paramObj) throws GenerateException {
        return new HashMap<String, Object>();
    }

    /**
     * 生成页面默认逻辑
     *
     * @throws GenerateException
     * @see cn.lonsun.staticcenter.generate.tag.BeanService#objToStr(java.lang.String,
     *      java.lang.Object, com.alibaba.fastjson.JSONObject)
     */
    @Override
    public String objToStr(String content, Object resultObj, JSONObject paramObj) throws GenerateException {
        // 当前上下文
        Context context = ContextHolder.getContext();
        // 获取数据
        Map<String, Object> map = this.doProcess(resultObj, paramObj);
        map.put("resultObj", resultObj);
        map.put("paramObj", paramObj);
        map.put("context", context);
        // 放入当前栏目信息，并且只有当生成栏目页时才放入
        Long columnId = null;
        String id = null == paramObj?null:paramObj.getString(GenerateConstant.ID);
        if (StringUtils.isEmpty(id)) {
            columnId = context.getColumnId();
        } else if (NumberUtils.isNumber(id)) {
            columnId = Long.valueOf(id);
        }
        if (null != columnId && (context.getScope() == null || MessageEnum.COLUMN.value().equals(context.getScope()))) {
            map.put("indicatorEO", CacheHandler.getEntity(IndicatorEO.class, columnId));
        }
        // 生成页面
        if (StringUtils.isNotEmpty(StringUtils.trim(content))) {
            return VelocityUtil.mergeString(content, map);// 取中间内容去解析
        }
        String file = paramObj.getString(GenerateConstant.FILE);
        String vm = StringUtils.isEmpty(file) ? paramObj.getString(GenerateConstant.LABEL_NAME) : file;
        return VelocityUtil.mergeTemplate(vm, map);// 判断如果自定义页面了，则使用自定义模板文件
    }

    /**
     * 获取多个关联标签结果集
     * @param paramObj
     * @return
     */
    public List<Object> getReferLabels(JSONObject paramObj){
        Object referLabels = paramObj.get(GenerateConstant.REFER_LABEL);
        if(referLabels instanceof List){
            return (List<Object>)referLabels;
        }
        return null;
    }

    /**
     * 获取第一个关联标签结果集
     * @param paramObj
     * @return
     */
    public Object getReferLabel(JSONObject paramObj){
        List<Object> referLabels = getReferLabels(paramObj);
        if(null != referLabels && referLabels.size() > 0){
            return referLabels.get(0);
        }
        return null;
    }
}