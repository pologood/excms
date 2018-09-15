/*
 * LinkListBeanService.java         2015年11月25日 <br/>
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

package cn.lonsun.staticcenter.generate.tag.impl.link;

import cn.lonsun.cache.client.CacheGroup;
import cn.lonsun.cache.client.CacheHandler;
import cn.lonsun.common.util.AppUtil;
import cn.lonsun.core.exception.BaseRunTimeException;
import cn.lonsun.indicator.internal.entity.IndicatorEO;
import cn.lonsun.site.site.internal.entity.ColumnConfigEO;
import cn.lonsun.site.site.internal.entity.ColumnMgrEO;
import cn.lonsun.staticcenter.generate.GenerateConstant;
import cn.lonsun.staticcenter.generate.GenerateException;
import cn.lonsun.staticcenter.generate.tag.impl.AbstractBeanService;
import cn.lonsun.staticcenter.generate.thread.Context;
import cn.lonsun.staticcenter.generate.thread.ContextHolder;
import cn.lonsun.staticcenter.generate.util.AssertUtil;
import cn.lonsun.staticcenter.generate.util.PathUtil;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * 链接管理 <br/>
 *
 * @author fangtinghua <br/>
 * @version v1.0 <br/>
 * @date 2015年11月25日 <br/>
 */
@Component
public class LinkListBeanService extends AbstractBeanService {

    @Resource
    private LinkListJsBeanService beanService;

    @Override
    public Object getObject(JSONObject paramObj) {
        // 获取栏目配置信息
        Context context = ContextHolder.getContext();// 获取栏目id
        Long columnId = paramObj.getLong(GenerateConstant.ID);
        if (null == columnId) {// 如果栏目id为空说明，栏目id是在页面传入的
            columnId = context.getColumnId();
        }
        AssertUtil.isEmpty(columnId, "栏目id不能为空！");
        return CacheHandler.getEntity(ColumnConfigEO.class, CacheGroup.CMS_INDICATORID, columnId);
    }

    /**
     * 重写生成html内容，保存成<script src="/site/columnId.js"></script></br>
     * 当内容不为空时，直接生成html返回
     *
     * @see cn.lonsun.staticcenter.generate.tag.impl.AbstractBeanService#objToStr(java.lang.String,
     * java.lang.Object, com.alibaba.fastjson.JSONObject)
     */
    @Override
    public String objToStr(String content, Object resultObj, JSONObject paramObj) throws GenerateException {
        ColumnConfigEO columnConfigEO = (ColumnConfigEO) resultObj;
        Long num = paramObj.getLong(GenerateConstant.NUM);
        if (null == num || num <= 0L) {
            num = columnConfigEO.getNum();
        }
        AssertUtil.isEmpty(num, "查询条数不能为空！");
        if (!StringUtils.isEmpty(content)) {// 内容不为空时，直接生成
            // 条数可以传入，其他参数按照栏目配置来
            paramObj.put("isLogo", columnConfigEO.getIsLogo());
            paramObj.put("width", columnConfigEO.getWidth());
            paramObj.put("height", columnConfigEO.getHeight());
            paramObj.put("columnId", columnConfigEO.getIndicatorId());
            paramObj.put("num", num);
            // 查询
            resultObj = beanService.getObject(paramObj);
            return beanService.objToStr(content, resultObj, paramObj);
        }
        StringBuffer js = new StringBuffer();
        Context context = ContextHolder.getContext();// 获取栏目id

        String uri = context.getUri();
        Long siteId = paramObj.getLong("siteId");// 如果标签传入站点id，则调用指定站的js
        if (!AppUtil.isEmpty(siteId) && siteId > 0L) {
            IndicatorEO siteEO = CacheHandler.getEntity(IndicatorEO.class, siteId);
            if (null == siteEO) {
                throw new BaseRunTimeException("站点不存在！");
            }
            uri = siteEO.getUri();
        } else {
            siteId = context.getSiteId();
        }
        Long columnId = columnConfigEO.getIndicatorId();
        ColumnMgrEO column = CacheHandler.getEntity(ColumnMgrEO.class, columnId);
        js.append("<script>var ll_" + columnConfigEO.getIndicatorId() + " = " + num + ";</script>");
        uri = StringUtils.defaultString(uri);
        js.append("<script charset=\"utf-8\" src=\"").append(uri);
        if(!uri.endsWith("/")){
            js.append(PathUtil.SEPARATOR);
        }
        js.append(column.getUrlPath());
        js.append(PathUtil.SEPARATOR).append(columnConfigEO.getIndicatorId()).append(".js?").append(GenerateConstant.NUM);
        return js.append("=").append(num).append("\"></script>").toString();
    }
}