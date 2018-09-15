/*
 * LableUtil.java         2015年9月18日 <br/>
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

import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cn.lonsun.cache.client.CacheGroup;
import cn.lonsun.cache.client.CacheHandler;
import cn.lonsun.core.util.SpringContextHolder;
import cn.lonsun.site.label.internal.entity.LabelEO;
import cn.lonsun.staticcenter.generate.CallBack;
import cn.lonsun.staticcenter.generate.GenerateException;
import cn.lonsun.staticcenter.generate.GenerateRecord;
import cn.lonsun.staticcenter.generate.tag.BeanService;
import cn.lonsun.staticcenter.generate.tag.impl.article.ArticleNewsDetailBeanService;
import cn.lonsun.staticcenter.generate.thread.Context;
import cn.lonsun.staticcenter.generate.thread.ContextHolder;

/**
 * 标签缓存处理 <br/>
 *
 * @date 2015年9月18日 <br/>
 * @author fangtinghua <br/>
 * @version v1.0 <br/>
 */
public class LabelUtil {
    // 日志
    private static final Logger logger = LoggerFactory.getLogger(LabelUtil.class);
    // 预处理标签列表
    private static List<String> processLabelList = Arrays.asList(StringUtils.split(PathUtil.getPathConfig().getProcessLabel(), ","));

    /**
     * 根据标签找到beanservice
     *
     * @author fangtinghua
     * @param labelName
     * @return
     * @throws GenerateException
     */
    public static final BeanService getBeanServiceByLabel(String labelName) throws GenerateException {
        try {
            return SpringContextHolder.getBean(labelName + "BeanService");
        } catch (Throwable e) {
            logger.error("标签[" + labelName + "]后台处理逻辑不存在.", e);
            throw new GenerateException("标签[" + labelName + "]后台处理逻辑不存在.", e);
        }
    }

    /**
     * 标签缓存
     *
     * @author fangtinghua
     * @param labelEO
     * @return
     * @throws GenerateException
     */
    public static final LabelEO doCacheLabel(String labelName) throws GenerateException {
        // 取出上下文
        Context context = ContextHolder.getContext();
        GenerateRecord generateRecord = context.getGenerateRecord();// 生成记录
        // 从本地缓存中取出标签
        LabelEO labelEO = null;
        if(null != generateRecord){
            labelEO = generateRecord.getLabelEO(labelName);
        }
        if (null == labelEO) {// 从redis取
            labelEO = CacheHandler.getEntity(LabelEO.class, CacheGroup.CMS_NAME, labelName);
        }
        if (null != labelEO) {// 放入本地缓存
            if(null != generateRecord){
                generateRecord.putLabelEO(labelName, labelEO);
            }
            return labelEO;
        }
        throw new GenerateException("标签[" + labelName + "]在系统管理中没有配置.");
    }

    /**
     * 预处理内容，替换公共标签
     *
     * @author fangtinghua
     * @param content
     * @param callBack
     * @return
     * @throws GenerateException
     */
    public static String replaceCommonLabel(String content, CallBack<String> callBack) throws GenerateException {
        // 当动态生成时，所有标签全部要解析
        boolean dynamic = StringUtils.isNotEmpty(ContextHolder.getContext().getModule());
        return callBack.execute(RegexUtil.parseContent(content, dynamic ? null : getProcessLabel()));
    }

    /**
     * 替换当前上下文相关信息
     *
     * @author fangtinghua
     * @param content
     * @return
     * @throws GenerateException
     */
    public static String replaceContext(String content) throws GenerateException {
        Long contentId = ContextHolder.getContext().getContentId();
        if (StringUtils.isEmpty(content) || null == contentId) { // 判断空或者不是处理文章
            return content;
        }
        String typeCode = ContextHolder.getContext().getTypeCode();// 文章类型
        // 拿到处理的beanservice
        BeanService service = null;
        try {
            service = SpringContextHolder.getBean(typeCode + "DetailBeanService");
        } catch (Throwable e) {
            // 当没有找到其他类型文章页预处理逻辑时，默认采用文章新闻
            service = SpringContextHolder.getBean(ArticleNewsDetailBeanService.class);
        }
        Object resultObj = service.getObject(null);
        return null == resultObj ? content : service.objToStr(content, resultObj, null);
    }

    /**
     * 获取预处理标签列表
     *
     * @author fangtinghua
     * @return
     */
    public static List<String> getProcessLabel() {
        return processLabelList;
    }
}