/*
 * NavBeanService.java         2015年9月15日 <br/>
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

import cn.lonsun.cache.client.CacheHandler;
import cn.lonsun.common.util.AppUtil;
import cn.lonsun.content.internal.entity.BaseContentEO;
import cn.lonsun.indicator.internal.entity.IndicatorEO;
import cn.lonsun.site.site.internal.entity.ColumnMgrEO;
import cn.lonsun.staticcenter.generate.GenerateConstant;
import cn.lonsun.staticcenter.generate.tag.impl.AbstractBeanService;
import cn.lonsun.staticcenter.generate.thread.Context;
import cn.lonsun.staticcenter.generate.thread.ContextHolder;
import cn.lonsun.staticcenter.generate.util.PathUtil;
import cn.lonsun.staticcenter.service.HtmlEnum;
import com.alibaba.fastjson.JSONObject;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 导航 <br/>
 *
 * @author fangtinghua <br/>
 * @version v1.0 <br/>
 * @date 2015年9月15日 <br/>
 */
@Component
public class NavBeanService extends AbstractBeanService {

    @Override
    public Object getObject(JSONObject paramObj) {
        Context context = ContextHolder.getContext();
        Long columnId = paramObj.getLong(GenerateConstant.ID);
        if (null == columnId) {// 如果栏目id为空说明，栏目id是在页面传入的
            columnId = context.getColumnId();
        }
        List<ColumnMgrEO> resultList = new ArrayList<ColumnMgrEO>();
        Boolean isSiteName = paramObj.getBooleanValue("isSiteName");
        this.getParent(resultList, columnId, isSiteName);// 获取所有父栏目包括本栏目
        Collections.reverse(resultList);// 反转
        return resultList;
    }

    @Override
    @SuppressWarnings("unchecked")
    public String objToStr(String content, Object resultObj, JSONObject paramObj) {
        StringBuffer sb = new StringBuffer();
        String s = "";
        Boolean isStatic = paramObj.getBoolean("isStatic");
        if (isStatic == null) {
            isStatic = true;
        }
        List<ColumnMgrEO> list = (List<ColumnMgrEO>) resultObj;
        // 预处理处理栏目连接
        if (null != list && !list.isEmpty()) {

            // 如果父级是专题专栏模型，当前位置去掉专题名称的一级，因为当前位置已经有首页
            ColumnMgrEO columnMgrEO = CacheHandler.getEntity(ColumnMgrEO.class, list.get(0).getParentId());
            if (!AppUtil.isEmpty(columnMgrEO)) {
                if ("specialVirtual".equals(columnMgrEO.getColumnTypeCode())) {
                    list.remove(list.get(0));
                }
            }

            for (ColumnMgrEO eo : list) {
                sb.append(" > <a title='").append(eo.getName()).append("'");
                if (eo.getIsStartUrl() == 1) {
                    if (eo.getTransWindow() != null && eo.getTransWindow() == 1) {//新窗口打开
                        sb.append(" target='_blank' ");
                    }
                    sb.append(" href='").append(eo.getTransUrl()).append("'>");
                } else {
                    if (isStatic && !BaseContentEO.TypeCode.collectInfo.toString().equals(eo.getColumnTypeCode())) {
                        if (eo.getTransWindow() != null && eo.getTransWindow() == 1) {//新窗口打开
                            sb.append(" target='_blank' ");
                        }
                        sb.append(" href='").append(PathUtil.getLinkPath(eo.getIndicatorId(), null)).append("'>");
                    } else {
                        sb.append(" href='").append(PathUtil.getLinkPath(HtmlEnum.CONTENT.getValue(), HtmlEnum.COLUMN.getValue(), eo.getIndicatorId(), null)).append("'>");
                    }
                }
                sb.append(eo.getName()).append("</a>");
            }
            s = sb.substring(sb.indexOf(">") + 1);
        }
        String s1 = "<a href='/index.html'>首页 > </a>";
        Boolean isIndex = paramObj.getBooleanValue("isIndex");
        if (isIndex == null) {
            return s;
        }
        if (isIndex) {
            return s1 + s;
        }
        return s;
    }

    /**
     * 获取父栏目
     *
     * @param resultList
     * @param parentId
     * @author fangtinghua
     */
    private void getParent(List<ColumnMgrEO> resultList, Long parentId, Boolean isSiteName) {
        if (null == parentId) {
            return;
        }
        ColumnMgrEO eo = CacheHandler.getEntity(ColumnMgrEO.class, parentId);
        if (null != eo && (eo.getType().equals(IndicatorEO.Type.CMS_Section.toString()) ||
            eo.getType().equals(IndicatorEO.Type.CMS_Site.toString()))) {
            if (isSiteName) {
                resultList.add(eo);
                getParent(resultList, eo.getParentId(), isSiteName);
            } else {
                if (!"specialVirtual".equals(eo.getColumnTypeCode()) && IndicatorEO.Type.CMS_Section.toString().equals(eo.getType()) && eo.getIsShow().equals(1)) {// 这里的导航，站点不参与
                    resultList.add(eo);
                    getParent(resultList, eo.getParentId(), isSiteName);
                }
            }
        }
    }
}