/*
 * ColumnNavBeanService.java         2015年12月4日 <br/>
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

import cn.lonsun.cache.client.CacheGroup;
import cn.lonsun.cache.client.CacheHandler;
import cn.lonsun.content.internal.entity.BaseContentEO;
import cn.lonsun.indicator.internal.entity.IndicatorEO;
import cn.lonsun.site.label.internal.entity.LabelEO;
import cn.lonsun.site.site.internal.entity.ColumnConfigEO;
import cn.lonsun.staticcenter.generate.GenerateConstant;
import cn.lonsun.staticcenter.generate.GenerateException;
import cn.lonsun.staticcenter.generate.tag.BeanService;
import cn.lonsun.staticcenter.generate.tag.impl.AbstractBeanService;
import cn.lonsun.staticcenter.generate.thread.Context;
import cn.lonsun.staticcenter.generate.thread.ContextHolder;
import cn.lonsun.staticcenter.generate.util.*;
import cn.lonsun.util.ModelConfigUtil;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;

/**
 * 根据栏目类型来判断具体用哪个标签 <br/>
 *
 * @author fangtinghua <br/>
 * @version v1.0 <br/>
 * @date 2015年12月4日 <br/>
 */
@Component
public class ColumnNavBeanService extends AbstractBeanService {

    @Override
    public Object getObject(JSONObject paramObj) {
        // 从上下文中取出栏目id
        Context context = ContextHolder.getContext();
        Long columnId = context.getColumnId();
        return CacheHandler.getEntity(IndicatorEO.class, columnId);
    }

    /**
     * 当为叶子节点时，直接取出当前栏目类型解析，当为父栏目时，需要当前栏目子栏目，汇总解析输出
     *
     * @see cn.lonsun.staticcenter.generate.tag.BeanService#objToStr(java.lang.String,
     * java.lang.Object, com.alibaba.fastjson.JSONObject)
     */
    @Override
    public String objToStr(String content, Object resultObj, JSONObject paramObj) throws GenerateException {
        if (StringUtils.isEmpty(content)) {
            return StringUtils.EMPTY;
        }
        IndicatorEO indicatorEO = (IndicatorEO) resultObj;
        Map<String, String> regexMap = new HashMap<String, String>();// 标签关系map
        Map<String, String> paramMap = new HashMap<String, String>();// 参数map
        Map<String, String> contentMap = new HashMap<String, String>();// 内容map
        Matcher m = RegexUtil.LIST_PATTERN.matcher(content);// 正则匹配
        while (m.find()) {
            String labelName = m.group(1).trim();
            regexMap.put(labelName, m.group(0));
            paramMap.put(labelName, m.group(2));
            contentMap.put(labelName, m.group(3));
        }
        if (regexMap.isEmpty()) {// 判断是否有符合标签
            return StringUtils.EMPTY;
        }
        Set<String> labelSet = regexMap.keySet();// 所有标签
        if (indicatorEO.getIsParent().equals(0)) {// 叶子节点
            // 栏目类型
            String columnTypeCode =
                    ModelConfigUtil.getTemplateByColumnId(indicatorEO.getIndicatorId(), ContextHolder.getContext().getSiteId()).getModelTypeCode();
            String labelName = this.fuzzyValue(columnTypeCode, labelSet);
            if (StringUtils.isEmpty(labelName)) {
                return StringUtils.EMPTY;
            }
            return RegexUtil.parseContent(regexMap.get(labelName));
        }
        String exclude = paramObj.getString(GenerateConstant.EXCLUDE);// 排除字段，按照逗号隔开
        Boolean showContent = paramObj.getBoolean("showContent");//默认为true，默认显示内容
        StringBuffer sb = new StringBuffer();
        List<IndicatorEO> childrenList = CacheHandler.getList(IndicatorEO.class, CacheGroup.CMS_PARENTID, indicatorEO.getIndicatorId());
        if (null != childrenList && !childrenList.isEmpty()) {
            for (IndicatorEO children : childrenList) {
                ColumnConfigEO columnConfigEO = CacheHandler.getEntity(ColumnConfigEO.class, CacheGroup.CMS_INDICATORID, children.getIndicatorId());
                if (null == columnConfigEO) {
                    continue;
                }else if(columnConfigEO.getIsShow()!=1){//栏目隐藏不查询
                    continue;
                }
                if (columnConfigEO.getIsStartUrl() == 1) {// 跳转链接
                    if (null != showContent && !showContent) {//返回调转连接地址
                        sb.append("<ul class=\"doc_list list-" + children.getIndicatorId() + "\"><li class=\"columnName\"><a class=\"title\" href=\"").append(columnConfigEO.getTransUrl());
                        sb.append("\">").append(children.getName()).append("</a></li></ul>");
                    }
                    continue;
                }
                // 栏目类型
                String columnTypeCode = columnConfigEO.getColumnTypeCode();
                // 排除不处理的栏目类型
                if (StringUtils.isNotEmpty(exclude) && exclude.indexOf(columnTypeCode) > -1) {
                    continue;
                }
                String labelName = this.fuzzyValue(columnTypeCode, labelSet);
                if (StringUtils.isEmpty(labelName)) {// 没有找到标签
                    continue;
                }
                String param = paramMap.get(labelName);
                // 重写标签，父栏目定义的参数覆盖子栏目参数
                Map<String, String> transMap = DocumentUtil.parseText(param);
                for (String key : transMap.keySet()) {
                    String parentKey = columnTypeCode + "_" + key;
                    if (paramObj.containsKey(parentKey)) {
                        transMap.put(key, paramObj.getString(parentKey));
                    }
                }
                BeanService service = LabelUtil.getBeanServiceByLabel(labelName);// 获取beanservice
                LabelEO labelEO = LabelUtil.doCacheLabel(labelName);// 获取标签
                JSONObject unionObj = MapUtil.unionMapToLabel(labelEO, transMap);// 参数合并
                // 强制设置子标签解析参数
                unionObj.put(GenerateConstant.ID, children.getIndicatorId());// 放入当前栏目id
                unionObj.put(GenerateConstant.IS_TITLE, true);// 是否要写标题
                unionObj.put(GenerateConstant.IS_CHILD, true);// 判断父子栏目
                unionObj.put(GenerateConstant.IS_PAGE, false);// 判断分页
                unionObj.put("showContent", showContent);// 是否显示内容
                String result = RegexUtil.parseLabelHtml(service, labelName, contentMap.get(labelName), unionObj);
                if (StringUtils.isNotEmpty(result)) {// 判断内容不为空
                    sb.append(result);
                }
                // 当第一个为单页面或者留言时，直接查询返回了
                if (BaseContentEO.TypeCode.guestBook.toString().equals(columnTypeCode) ||
                        ((null == showContent || showContent) && BaseContentEO.TypeCode.ordinaryPage.toString().equals(columnTypeCode))) {
                    break;
                }
            }
        }
        return sb.toString();
    }

    /**
     * 模糊查询标签
     *
     * @param columnTypeCode
     * @param labelSet
     * @return
     * @author fangtinghua
     */
    private String fuzzyValue(String columnTypeCode, Set<String> labelSet) {
        for (String labelName : labelSet) {
            if (labelName.indexOf(columnTypeCode) > -1) {
                return labelName;
            }
        }
        return StringUtils.EMPTY;
    }
}