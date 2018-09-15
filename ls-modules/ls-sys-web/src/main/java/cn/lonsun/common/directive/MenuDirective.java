/*
 * MenuDirect.java         2015年8月19日 <br/>
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

import java.io.IOException;
import java.io.Writer;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.velocity.context.InternalContextAdapter;
import org.apache.velocity.exception.MethodInvocationException;
import org.apache.velocity.exception.ParseErrorException;
import org.apache.velocity.exception.ResourceNotFoundException;
import org.apache.velocity.runtime.directive.Directive;
import org.apache.velocity.runtime.parser.node.Node;
import org.apache.velocity.runtime.parser.node.SimpleNode;

import cn.lonsun.core.util.SpringContextHolder;
import cn.lonsun.rbac.indicator.entity.MenuEO;
import cn.lonsun.rbac.indicator.service.IIndicatorService;

/**
 * 菜单指令 <br/>
 *
 * @date 2015年8月19日 <br/>
 * @author fangtinghua <br/>
 * @version v1.0 <br/>
 */
public class MenuDirective extends Directive {

    private IIndicatorService indicatorService;

    @Override
    public String getName() {
        return "menu";
    }

    @Override
    public int getType() {
        return Directive.LINE;
    }

    @Override
    public boolean render(InternalContextAdapter context, Writer writer, Node node) throws IOException, ResourceNotFoundException, ParseErrorException,
            MethodInvocationException {
        if (null == indicatorService) {
            indicatorService = SpringContextHolder.getBean("ex_8_IndicatorServiceImpl");
        }

        String varName = "_menu";
        if (node.jjtGetNumChildren() > 0) {
            SimpleNode contextNameNode = (SimpleNode) node.jjtGetChild(0);
            String contextName = (String) contextNameNode.value(context);
            if (!StringUtils.isEmpty(contextName)) {
                varName = contextName;
            }
        }
        // 根据用户角色查询菜单
        List<MenuEO> list = indicatorService.getMenu(false, null);
        // velocity上下文
        context.put(varName, list);
        return true;
    }
}