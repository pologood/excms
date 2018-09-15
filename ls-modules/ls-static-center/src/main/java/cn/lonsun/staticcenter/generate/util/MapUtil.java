/*
 * MapUtil.java         2016年5月26日 <br/>
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

package cn.lonsun.staticcenter.generate.util;

import cn.lonsun.site.label.internal.entity.LabelEO;
import cn.lonsun.staticcenter.generate.ConditionVO;
import cn.lonsun.staticcenter.generate.GenerateConstant;
import cn.lonsun.staticcenter.generate.thread.Context;
import cn.lonsun.staticcenter.generate.thread.ContextHolder;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

/**
 * map和标签字段合并 <br/>
 *
 * @author fangtinghua <br/>
 * @version v1.0 <br/>
 * @date 2016年5月26日 <br/>
 */
public class MapUtil {
    private static final Logger logger = LoggerFactory.getLogger(MapUtil.class);

    /**
     * 把参数map合并到标签对象中
     *
     * @param labelEO
     * @param paramMap
     * @return
     * @author fangtinghua
     */
    public static JSONObject unionMapToLabel(LabelEO labelEO, Map<String, String> paramMap) {
        String labelName = labelEO.getLabelName();
        String labelConfig = labelEO.getLabelConfig();
        Context context = ContextHolder.getContext();//上下文对象
        JSONObject paramObj = new JSONObject(); // 返回json对象
        paramObj.put(GenerateConstant.LABEL_NAME, labelName);
        labelConfig = null != labelConfig ? labelConfig.trim() : labelConfig;
        List<ConditionVO> conditionList = JSON.parseArray(labelConfig, ConditionVO.class);
        if (null == conditionList || conditionList.isEmpty()) {// 判断标签参数
            return paramObj;
        }
        //默认静态地址访问
        context.setLinkType(true);
        // 循环标签字段，如果字段从页面传入，则采用页面值，并检查类型
        for (ConditionVO vo : conditionList) {
            String fieldName = vo.getFieldname();
            // 页面没有传入，直接采用默认值
            if (null == paramMap || !paramMap.containsKey(fieldName)) {
                paramObj.put(fieldName, vo.getDefaultval());
                continue;
            }
            // 判断数据类型
            paramObj.put(fieldName, checkFiledType(vo, paramMap.get(fieldName)));
        }
        if (null != paramMap && paramMap.containsKey(GenerateConstant.REFER_LABEL)) {
            paramObj.put(GenerateConstant.REFER_LABEL, paramMap.get(GenerateConstant.REFER_LABEL));
        }
        if (null != paramMap && paramMap.containsKey(GenerateConstant.LABEL_ID)) {
            paramObj.put(GenerateConstant.LABEL_ID, paramMap.get(GenerateConstant.LABEL_ID));
        }
        if (null != paramMap && !paramObj.containsKey(GenerateConstant.FILE)) {
            paramObj.put(GenerateConstant.FILE, paramMap.get(GenerateConstant.FILE));
        }
        if (null != paramMap && !paramObj.containsKey("result")) {
            paramObj.put("result", paramMap.get("result"));
        }
        if (null != paramMap && paramMap.containsKey("isHtml")) {//如果传参包含isHtml参数，以传参为准
            boolean isHtml = Boolean.parseBoolean(paramMap.get("isHtml"));
            paramObj.put("isHtml", isHtml);
            context.setLinkType(isHtml);
        } else if (paramObj.containsKey("isHtml")) {//已标签为准
            context.setLinkType(paramObj.getBooleanValue("isHtml"));
        }
        return paramObj;
    }

    /**
     * 把上下文中参数放入json对象中，标签优先级最高，当标签有键时，依据标签为准
     *
     * @param paramObj
     * @author fangtinghua
     */
    public static void unionContextToJson(JSONObject paramObj) {
        Map<String, ConditionVO> conditionMap = new HashMap<String, ConditionVO>();
        String labelName = paramObj.getString(GenerateConstant.LABEL_NAME);
        if (StringUtils.isNotEmpty(labelName)) {//如果有标签的话，则融合标签，没有就只有上下文和参数对象融合
            LabelEO labelEO = LabelUtil.doCacheLabel(labelName);
            String labelConfig = labelEO.getLabelConfig();
            labelConfig = null != labelConfig ? labelConfig.trim() : labelConfig;
            List<ConditionVO> conditionList = JSON.parseArray(labelConfig, ConditionVO.class);
            if (null != conditionList && !conditionList.isEmpty()) {// 判断标签参数
                for (ConditionVO vo : conditionList) {
                    conditionMap.put(vo.getFieldname(), vo);
                }
            }
        }
        Map<String, String> paramMap = ContextHolder.getContext().getParamMap();
        for (Entry<String, String> entry : paramMap.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();
            if (paramObj.containsKey(key) && StringUtils.isNotBlank(value)) {
                if (conditionMap.containsKey(key)) {
                    paramObj.put(key, checkFiledType(conditionMap.get(key), value));
                } else {
                    paramObj.put(key, value);
                }
            }
        }
    }

    /**
     * 判断类型
     *
     * @param vo
     * @param value
     * @return
     */
    private static Object checkFiledType(ConditionVO vo, String value) {
        if (StringUtils.isEmpty(value)) { // 为空没有判断的必要
            return null;
        }
        if ("integer".equalsIgnoreCase(vo.getDatatype())) {
            return NumberUtils.toLong(value);
        }
        if ("boolean".equalsIgnoreCase(vo.getDatatype())) {
            return BooleanUtils.toBoolean(value);
        }
        return value;
    }
}