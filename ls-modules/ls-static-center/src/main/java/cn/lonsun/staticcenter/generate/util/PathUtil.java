/*
 * FileUtil.java         2015年9月8日 <br/>
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

import cn.lonsun.cache.client.CacheHandler;
import cn.lonsun.content.internal.entity.BaseContentEO;
import cn.lonsun.core.util.SpringContextHolder;
import cn.lonsun.indicator.internal.entity.IndicatorEO;
import cn.lonsun.message.internal.entity.MessageEnum;
import cn.lonsun.rbac.internal.entity.OrganEO;
import cn.lonsun.site.site.internal.entity.ColumnMgrEO;
import cn.lonsun.staticcenter.generate.PathConfig;
import cn.lonsun.staticcenter.generate.thread.Context;
import cn.lonsun.staticcenter.generate.thread.Context.From;
import cn.lonsun.staticcenter.generate.thread.ContextHolder;
import org.apache.commons.lang3.StringUtils;

import javax.servlet.http.HttpServletRequest;

/**
 * 文件访问路径工具类 <br/>
 *
 * @author fangtinghua <br/>
 * @version v1.0 <br/>
 * @date 2015年9月8日 <br/>
 */
public class PathUtil {
    // 文件分隔符
    public static final String SEPARATOR = "/";
    // 默认首页名称
    private static final String DEFAULT_INDEX_NAME = "index";
    // 默认文件类型
    private static final String DEFAULT_FILE_SUFFIX = ".html";
    // 文件生成默认前缀
    private static PathConfig pathConfig = SpringContextHolder.getBean(PathConfig.class);

    /**
     * 获取文件名，根据站点/栏目/文章/的规则生成
     *
     * @return
     * @author fangtinghua
     */
    public static String getFileName() {
        Context context = ContextHolder.getContext();
        Long scope = context.getScope();
        Long columnId = context.getColumnId();
        Long contentId = context.getContentId();

        StringBuffer fn = new StringBuffer();
        if (MessageEnum.INDEX.value().equals(scope)) {// 站点首页是特殊情况
            fn.append(DEFAULT_INDEX_NAME);
        } else if (null != contentId) {// 文章页
            fn.append(contentId);
        } else if (null != columnId) {// 栏目页
            fn.append(DEFAULT_INDEX_NAME);
        }
        return fn.append(DEFAULT_FILE_SUFFIX).toString();
    }

    /**
     * 获取文件路径，根据站点/栏目/文章/的规则生成
     *
     * @return
     * @author fangtinghua
     */
    public static String getFilePath() {
        Context context = ContextHolder.getContext();
        String from = context.getFrom();
        Long siteId = context.getSiteId();
        Long scope = context.getScope();
        Long columnId = context.getColumnId();
        Long contentId = context.getContentId();
        Long source = context.getSource();
        boolean subSite = IndicatorEO.Type.SUB_Site.toString().equals(context.getSiteType());

        StringBuffer fp = new StringBuffer();
        if (From.WAP.toString().equals(from)) {
            fp.append(SEPARATOR).append(From.WAP.toString().toLowerCase());// 手机端路径加上wap
        }
        if (subSite) {// 如果是虚拟站点
            fp.append(SEPARATOR).append(siteId);
        }
        if (MessageEnum.INDEX.value().equals(scope)) { // 站点首页是特殊情况
            if (MessageEnum.PUBLICINFO.value().equals(source)) {// 信息公开需要加上前缀，只针对首页
                fp.append(SEPARATOR).append("public");
            }
            //如果是信息公开的文章页
        } else if(BaseContentEO.TypeCode.public_content.toString().equals(context.getTypeCode())){
            fp.append(SEPARATOR).append("public").append(SEPARATOR).append(columnId);
        } else{
            fp.append(SEPARATOR).append(context.getUrlPath());
        }
        return fp.toString();
    }

    public static String getLinkPath(Long columnId, Long contentId) {
        Context context = ContextHolder.getContext();
        String uri = context.getUri();
        Long source = context.getSource();
        Long siteId = context.getSiteId();
        Long id = context.getColumnId();// 栏目id
        if (null != columnId && !columnId.equals(id)) {// 当传入的id与上下文不一致
            if (MessageEnum.CONTENTINFO.value().equals(source)) {// 内容管理
                IndicatorEO indicatorEO = CacheHandler.getEntity(IndicatorEO.class, columnId);
                AssertUtil.isEmpty(indicatorEO, "栏目不存在！", columnId);
                siteId = indicatorEO.getSiteId();
                IndicatorEO siteEO = CacheHandler.getEntity(IndicatorEO.class, siteId);
                AssertUtil.isEmpty(siteEO, "站点不存在！", siteId);
                uri = siteEO.getUri();
            } else if (MessageEnum.PUBLICINFO.value().equals(source)) {
                // 信息公开这边根据单位id查找不了站点信息，默认就是使用当前上下文uri，后续根据实际情况修改
            }
        }
        String from = context.getFrom();
        boolean subSite = IndicatorEO.Type.SUB_Site.toString().equals(context.getSiteType());
        StringBuffer lp = new StringBuffer();
        lp.append(processUri(uri));// 加上域名
        if (From.WAP.toString().equals(from) || !context.getLinkType()) {

            // 手机端路劲加上wap
            if (From.WAP.toString().equals(from)) {
                lp.append(SEPARATOR).append(From.WAP.toString().toLowerCase());
            }

            //信息公开路径
            if ("public".equals(context.getModule()) || MessageEnum.PUBLICINFO.value().equals(source)) {
                lp.append(SEPARATOR).append("public").append(SEPARATOR)
                        .append("content").append(SEPARATOR)
                        .append(contentId);
            } else {
                lp.append(SEPARATOR).append("content");
                if (null != contentId) {// 文章页
                    lp.append(SEPARATOR).append("article")
                            .append(SEPARATOR).append(contentId);
                } else {
                    if (null != columnId) {// 栏目页
                        lp.append(SEPARATOR).append("column")
                                .append(SEPARATOR).append(columnId);
                    }
                }
            }

        } else {
            if (subSite) {// 如果是虚拟站点
                lp.append(SEPARATOR).append(siteId);
            }
            if (null != columnId) {// 拼接栏目路径
                ColumnMgrEO column = CacheHandler.getEntity(ColumnMgrEO.class, columnId);
                if(column == null){ //如果栏目不存在，则查询单位
                    OrganEO eo = CacheHandler.getEntity(OrganEO.class, columnId);
                    if(eo != null){
                        lp.append(SEPARATOR).append("public").append(SEPARATOR).append(columnId);
                    }
                }else{
                    lp.append(SEPARATOR).append(column.getUrlPath());
                }
            }
            if (null != contentId) {// 文章页
                lp.append(SEPARATOR).append(contentId);
            }else{ //不是文章就是栏目页
                lp.append(SEPARATOR).append(DEFAULT_INDEX_NAME);
            }
            lp.append(DEFAULT_FILE_SUFFIX);
        }

        return lp.toString();
    }

    /**
     * 动态页请求
     *
     * @param module
     * @param action
     * @param columnId
     * @param contentId
     * @return
     * @author fangtinghua
     */
    public static String getLinkPath(String module, String action, Long columnId, Long contentId) {
        Context context = ContextHolder.getContext();
        StringBuffer lp = new StringBuffer();

        // 加上域名
        lp.append(processUri(context.getUri()));

        String from = context.getFrom();
        if (From.WAP.toString().equals(from)) {
            lp.append(SEPARATOR).append("wap");
        }
        lp.append(SEPARATOR).append(module);
        lp.append(SEPARATOR).append(action);

        if (null != contentId) {// 文章页
            lp.append(SEPARATOR).append(contentId);
        } else if (null != columnId) {// 栏目页
            lp.append(SEPARATOR).append(columnId);
        }

        return lp.toString();
    }

    /**
     * 获取当前站点静态请求路径
     * @return
     * @author fangtinghua
     * @see PathUtil#getLinkPath(java.lang.Long, java.lang.Long)
     */
    @Deprecated
    public static String getLinkPath(HttpServletRequest request, Long columnId, Long contentId) {
        StringBuffer lp = new StringBuffer();
        ColumnMgrEO column = CacheHandler.getEntity(ColumnMgrEO.class, columnId);
        //如果获取不到栏目，就认为是信息公开的单位
        if(column != null){
            lp.append(SEPARATOR).append(column.getUrlPath());
        }else {
            lp.append(SEPARATOR).append("public").append(SEPARATOR).append(columnId);
        }
        lp.append(SEPARATOR).append(contentId);
        return lp.append(DEFAULT_FILE_SUFFIX).toString();
    }

    /**
     * 处理uri，删除后面的斜杠
     *
     * @param uri
     * @return
     * @author fangtinghua
     */
    private static String processUri(String uri) {
        uri = StringUtils.defaultString(uri);
        if (StringUtils.isEmpty(uri)) {
            return StringUtils.EMPTY;
        }
        int lastLen = uri.lastIndexOf(SEPARATOR);
        return lastLen == uri.length() - 1 ? uri.substring(0, lastLen) : uri;
    }

    /**
     * 获取配置信息
     *
     * @return
     * @author fangtinghua
     */
    public static PathConfig getPathConfig() {
        return pathConfig;
    }

    public static String getUrl(String path){
        if(StringUtils.isEmpty(path)){
            return null;
        }
        if(path.startsWith("http")){
            return path;
        }
        if(path.contains(":")){
            return path;
        }
        if(path.contains("/")){
            return path;
        }
        if(path.contains(".")){
            return pathConfig.getFileServerNamePath() + path;
        }
        return pathConfig.getFileServerPath() + path;
    }
}