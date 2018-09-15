/*
 * ColumnButtonDirective.java         2016年5月4日 <br/>
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

import org.apache.commons.lang3.math.NumberUtils;
import org.apache.velocity.context.InternalContextAdapter;
import org.apache.velocity.runtime.parser.node.Node;

import cn.lonsun.core.util.SessionUtil;
import cn.lonsun.core.util.SpringContextHolder;
import cn.lonsun.indicator.internal.entity.FunctionEO;
import cn.lonsun.rbac.login.InternalAccount;
import cn.lonsun.system.role.internal.service.impl.SiteRightsService;
import cn.lonsun.util.LoginPersonUtil;

/**
 * 栏目权限按钮控制 <br/>
 *
 * @date 2016年5月4日 <br/>
 * @author fangtinghua <br/>
 * @version v1.0 <br/>
 */
public class ColumnButtonDirective extends ButtonDirective {

    private SiteRightsService siteRightsService = SpringContextHolder.getBean(SiteRightsService.class);

    @Override
    public String getName() {
        return "ColumnButton";
    }

    @Override
    public List<String> getCodeList(InternalContextAdapter context, Node node) {
        // 按钮code列表
        List<String> codeList = new ArrayList<String>();
        Long indicatorId = null;// 栏目id
        if (node.jjtGetNumChildren() == 0) {
            indicatorId = SessionUtil.getLongProperty(LoginPersonUtil.getSession(), InternalAccount.USER_INDICATORID);
        } else {
            indicatorId = NumberUtils.toLong(node.jjtGetChild(0).value(context).toString());
        }
        if (null != indicatorId) {
            List<FunctionEO> functions = siteRightsService.getOptByColumnId(indicatorId);
            if (null != functions && !functions.isEmpty()) {
                for (FunctionEO functionEO : functions) {
                    codeList.add(functionEO.getAction());
                }
            }
        }
        return codeList;
    }
}