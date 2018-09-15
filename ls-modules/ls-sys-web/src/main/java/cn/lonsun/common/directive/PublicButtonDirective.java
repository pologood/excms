/*
 * PublicButtonDirective.java         2016年4月8日 <br/>
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

package cn.lonsun.common.directive;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.apache.velocity.context.InternalContextAdapter;
import org.apache.velocity.runtime.parser.node.Node;
import org.apache.velocity.runtime.parser.node.SimpleNode;

import cn.lonsun.cache.client.CacheHandler;
import cn.lonsun.core.util.SpringContextHolder;
import cn.lonsun.indicator.internal.entity.FunctionEO;
import cn.lonsun.publicInfo.internal.entity.PublicCatalogEO;
import cn.lonsun.system.role.internal.service.IInfoOpenRightsService;
import cn.lonsun.util.LoginPersonUtil;

/**
 * 信息公开按钮权限 <br/>
 *
 * @date 2016年4月8日 <br/>
 * @author fangtinghua <br/>
 * @version v1.0 <br/>
 */
public class PublicButtonDirective extends ButtonDirective {

    private IInfoOpenRightsService infoOpenRightsService = SpringContextHolder.getBean(IInfoOpenRightsService.class);

    @Override
    public String getName() {
        return "PublicButton";
    }

    @Override
    public List<String> getCodeList(InternalContextAdapter context, Node node) {
        if (LoginPersonUtil.isSuperAdmin() || LoginPersonUtil.isSiteAdmin()) {
            return null;
        }
        // 按钮code列表
        List<String> codeList = new ArrayList<String>();
        Long organId = null;
        Long catId = null;
        String code = "";
        if (node.jjtGetNumChildren() > 2) {
            organId = (Long) ((SimpleNode) node).jjtGetChild(0).value(context);
            catId = (Long) ((SimpleNode) node).jjtGetChild(1).value(context);
            code = (String) ((SimpleNode) node).jjtGetChild(2).value(context);
        }
        if (null != organId && null != catId && !StringUtils.isEmpty(code)) {
            if ("DRIVING_PUBLIC".equals(code)) {
                PublicCatalogEO eo = CacheHandler.getEntity(PublicCatalogEO.class, catId);
                if (null != eo) {
                    code = eo.getCode();
                }
            }
            if (!StringUtils.isEmpty(code)) {
                Map<String, Set<FunctionEO>> map = infoOpenRightsService.getCurUserHasRights();
                Set<FunctionEO> functionList = map.get(organId + code);
                if (null != functionList && !functionList.isEmpty()) {
                    for (FunctionEO function : functionList) {
                        codeList.add(function.getAction());
                    }
                }
            }
        }
        return codeList;
    }
}