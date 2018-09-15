/*
 * RegexUtil.java         2015年8月13日 <br/>
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

import cn.lonsun.common.util.AppUtil;
import cn.lonsun.core.util.SpringContextHolder;
import cn.lonsun.site.label.internal.entity.LabelEO;
import cn.lonsun.staticcenter.generate.GenerateConstant;
import cn.lonsun.staticcenter.generate.GenerateException;
import cn.lonsun.staticcenter.generate.special.SpecialProcess;
import cn.lonsun.staticcenter.generate.tag.BeanService;
import cn.lonsun.staticcenter.generate.tag.PPretreatment;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * java正则工具类 <br/>
 *
 * @author fangtinghua <br/>
 * @version v1.0 <br/>
 * @date 2015年8月13日 <br/>
 */
public class RegexUtil {
    // 解析例如ls:list类型的列表
    // 该正则表达式支持两种闭合方式、支持标签嵌套、支持取单独属性，暂不支持相同标签嵌套
    private static final String LIST_REGEX = "\\{ls:([\\w]+)([^\\}]*)(?:/\\}|\\}(.*?)\\{/ls:\\1\\})";
    // 两种模式，为了应对换行的匹配
    public static final Pattern LIST_PATTERN = Pattern.compile(LIST_REGEX, Pattern.CASE_INSENSITIVE | Pattern.DOTALL);
    // 属性解析
    private static final String ATTR_REGEX = "\\[ls:([\\w]+)([^\\]]*)\\]";
    // 只区分大小写
    public static final Pattern ATTR_PATTERN = Pattern.compile(ATTR_REGEX, Pattern.CASE_INSENSITIVE);
    // 日志
    private static final Logger logger = LoggerFactory.getLogger(RegexUtil.class);

    /**
     * 标签体 ADD REASON. <br/>
     *
     * @date: 2016年8月22日 上午11:02:56 <br/>
     * @author fangtinghua
     */
    private static class LabelEntry {
        private String labelName;
        private String labelContent;
        private String param;
        private Object labelResultSet;
        private Boolean isHandled = Boolean.FALSE;

        public LabelEntry(String labelName, String labelContent, String param) {
            super();
            this.labelName = labelName;
            this.labelContent = labelContent;
            this.param = param;
        }
    }

    /**
     * 传入的参数字符串和数据库中的标签字段进行比较分析出对应结果
     *
     * @param param
     * @param labelEO
     * @return
     * @throws GenerateException
     * @author fangtinghua
     */
    public static JSONObject unionParam(String param, LabelEO labelEO) throws GenerateException {
        // 参数map
        Map<String, String> paramMap = null;
        // 如果参数字符串为空，则取标签各默认值进行传递
        if (StringUtils.isNotEmpty(param)) {
            paramMap = DocumentUtil.parseText(param);
        }
        return MapUtil.unionMapToLabel(labelEO, paramMap);
    }

    /**
     * 解析属性，并对属性进行预处理操作
     *
     * @param content
     * @param obj
     * @return
     * @throws GenerateException
     * @author fangtinghua
     */
    public static String parseProperty(String content, Object obj) throws GenerateException {
        // 结果
        StringBuffer buffer = new StringBuffer();
        // 正则匹配
        Matcher m = ATTR_PATTERN.matcher(content);
        // 查找
        while (m.find()) {
            String key = m.group(1).trim();// 字段
            String param = m.group(2).trim();// 参数
            String value = SpecialProcess.getBeanValue(key, obj);// 获取字段值
            if (StringUtils.isNotEmpty(value)) {
                // 参数map
                Map<String, String> paramMap = DocumentUtil.parseText(param);
                // 预处理字段值
                for (Map.Entry<String, String> entry : paramMap.entrySet()) {
                    PPretreatment pretreatment = null;
                    try {
                        pretreatment = SpringContextHolder.getBean(entry.getKey() + "PPretreatment");
                    } catch (Throwable e) {
//                        logger.error("预处理[" + entry.getKey() + "]后台处理逻辑不存在.", e);
                        throw new GenerateException("预处理[" + entry.getKey() + "]后台处理逻辑不存在.", e);
                    }
                    value = pretreatment.getValue(value, entry.getValue());
                }
            }
            // 由于$出现在replacement中时，表示对捕获组的反向引用，所以要对上面替换内容中的$进行替换
            m.appendReplacement(buffer, Matcher.quoteReplacement(value));
        }
        m.appendTail(buffer);
        return buffer.toString();
    }

    /**
     * 根据标签获取查询结果
     *
     * @param service
     * @param labelName
     * @param paramObj
     * @return
     * @throws GenerateException
     * @author fangtinghua
     */
    public static Object parseLabelObject(BeanService service, String labelName, JSONObject paramObj) throws GenerateException {
        try {
            return service.getObject(paramObj);
        } catch (Throwable e) {
//            logger.error("标签[" + labelName + "]获取数据错误.", e);
            throw new GenerateException("标签[" + labelName + "]获取数据错误.", e);
        }
    }

    /**
     * 根据标签获取查询结果
     *
     * @param service
     * @param labelName
     * @param content
     * @param paramObj
     * @return
     * @throws GenerateException
     * @author fangtinghua
     */
    public static String parseLabelHtml(BeanService service, String labelName, String content, JSONObject paramObj) throws GenerateException {
        // 获取内容
        Object resultObj = parseLabelObject(service, labelName, paramObj);
        try {
            return null == resultObj ? StringUtils.EMPTY : service.objToStr(content, resultObj, paramObj);
        } catch (Throwable e) {
//            logger.error("标签[" + labelName + "]内容解析错误.", e);
            throw new GenerateException("标签[" + labelName + "]内容解析错误.", e);
        }
    }

    public static String parseLabelHtml(BeanService service, LabelEntry labelEntry, JSONObject paramObj) throws GenerateException {
        // 获取内容
        try {
            Object resultObj = null;
            if (!labelEntry.isHandled || paramObj.containsKey(GenerateConstant.REFER_LABEL)) {
                resultObj = parseLabelObject(service, labelEntry.labelName, paramObj);
                labelEntry.labelResultSet = resultObj;
                labelEntry.isHandled = Boolean.TRUE;
            } else {
                resultObj = labelEntry.labelResultSet;
            }
            return null == resultObj ? StringUtils.EMPTY : service.objToStr(labelEntry.labelContent, resultObj, paramObj);
        } catch (Throwable e) {
//            logger.error("标签[" + labelEntry.labelName + "]内容解析错误.", e);
            throw new GenerateException("标签[" + labelEntry.labelName + "]内容解析错误.", e);
        }
    }

    /**
     * 解析指定标签，合并相同标签解析，前置规则，查询公共属性参数
     *
     * @return
     * @throws GenerateException
     * @author fangtinghua
     */
    public static String parseContent(String content, List<String> labelNameList) throws GenerateException {
        // 判断空
        if (StringUtils.isEmpty(content)) {
            return StringUtils.EMPTY;
        }
        int index = 0;
        // 结果
        StringBuffer sb = new StringBuffer();
        // 正则匹配，当解析指定标签时， 不预处理文章类型，防止文章标题被替换
        Matcher m = LIST_PATTERN.matcher(null == labelNameList ? LabelUtil.replaceContext(content) : content);
        // 合并标签解析
        List<LabelEntry> labelEntryList = new ArrayList<RegexUtil.LabelEntry>();
        // 查找
        while (m.find()) {
            // 标签名称
            String labelName = m.group(1).trim();
            // 当有指定标签时，必须包含指定标签才解析
            if (null == labelNameList || labelNameList.contains(labelName)) {
                // 参数
                String param = m.group(2).trim();
                // 标签内容，可能为空
                String labelContent = m.group(3);
                // 放入list中
                labelEntryList.add(new RegexUtil.LabelEntry(labelName, labelContent, param));
                // 替换
                m.appendReplacement(sb, "\\{" + labelName + ":" + index++ + "\\}");
            }
        }
        m.appendTail(sb);
        String result = sb.toString();
        // 解析
        if (!labelEntryList.isEmpty()) {
            List<String> doList = new ArrayList<String>();// 已经处理过的列表
            index = 0;// 重新计数
            Map<String, LabelEntry> labelEntryMapWithLabelId = getLabelEntryMapWithLabelId(labelEntryList);
            for (LabelEntry entry : labelEntryList) {
                String labelName = entry.labelName;
                // 获取标签
                LabelEO labelEO = LabelUtil.doCacheLabel(labelName);
                // 转换参数并检查类型
                JSONObject paramObj = unionParam(entry.param, labelEO);
                // 处理当前标签关联的标签,将关联的标签的结果集放入paramObj中
                handleReferLabel(labelEntryMapWithLabelId, paramObj);
                // 获取beanservice
                BeanService service = LabelUtil.getBeanServiceByLabel(labelName);
                if (!doList.contains(labelName) && service.before(paramObj)) {
                    doList.add(labelName);
                }
                // 查询结果
                String r = StringUtils.trim(parseLabelHtml(service, entry, paramObj));
                // 替换默认值
                r = StringUtils.defaultIfBlank(r, StringUtils.defaultIfBlank(paramObj.getString("result"), "正在更新中..."));
                // 由于$出现在replacement中时，表示对捕获组的反向引用，所以要对上面替换内容中的$进行替换
                result = result.replaceFirst("\\{" + labelName + ":" + index++ + "\\}", Matcher.quoteReplacement(r));
            }
        }
        return result;
    }

    /**
     * 获取所有带有LabelId属性的标签Map
     *
     * @param labelEntryList
     * @return
     */
    public static Map<String, LabelEntry> getLabelEntryMapWithLabelId(List<LabelEntry> labelEntryList) {
        Map<String, LabelEntry> map = new HashMap<String, LabelEntry>();
        for (LabelEntry entry : labelEntryList) {
            if (StringUtils.isNotEmpty(entry.param)) {
                Map<String, String> paramMap = DocumentUtil.parseText(entry.param);
                if (!AppUtil.isEmpty(paramMap.get(GenerateConstant.LABEL_ID))) {
                    map.put(paramMap.get(GenerateConstant.LABEL_ID).toString(), entry);
                }
            }
        }
        return map;
    }

    /**
     * 处理关联标签
     *
     * @param labelEntryMap
     * @param paramObj
     */
    public static void handleReferLabel(Map<String, LabelEntry> labelEntryMap, JSONObject paramObj) {
        if (null == labelEntryMap || labelEntryMap.size() == 0)
            return;
        if (!AppUtil.isEmpty(paramObj.get(GenerateConstant.REFER_LABEL))) {
            String[] referLabelArray = paramObj.get(GenerateConstant.REFER_LABEL).toString().split(",");// 关联标签
            LabelEntry entry = null;
            List<Object> resultObjList = new ArrayList<Object>();
            Object resultObj = null;
            for (String labelId : referLabelArray) {
                if (!AppUtil.isEmpty(labelId)) {
                    if (null != (entry = labelEntryMap.get(labelId))) {
                        if (!entry.isHandled) {
                            String labelName = entry.labelName;
                            // 获取标签
                            LabelEO labelEO = LabelUtil.doCacheLabel(labelName);
                            // 转换参数并检查类型
                            JSONObject paramObj1 = unionParam(entry.param, labelEO);
                            // 获取beanservice
                            BeanService service = LabelUtil.getBeanServiceByLabel(labelName);
                            resultObj = parseLabelObject(service, labelName, paramObj1);
                            entry.labelResultSet = resultObj;
                            entry.isHandled = Boolean.TRUE;
                        } else {
                            resultObj = entry.labelResultSet;
                        }
                        resultObjList.add(resultObj);
                    } else {
                        logger.error("关联标签ID" + labelId + "不存在,请检查模板中标签属性" + GenerateConstant.LABEL_ID + "的值");
                    }
                }
            }
            if (resultObjList.size() > 0) {
                paramObj.put(GenerateConstant.REFER_LABEL, resultObjList);
            }
        }
    }

    /**
     * 正则分析模板内容，替换相应标签的内容，需要递归进行判断
     *
     * @return
     * @throws GenerateException
     * @author fangtinghua
     */
    public static String parseContent(String content) throws GenerateException {
        return parseContent(content, null);
    }
}