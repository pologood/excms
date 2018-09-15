/*
 * ButtonDirective.java         2015年8月20日 <br/>
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

import org.apache.velocity.context.InternalContextAdapter;
import org.apache.velocity.exception.MethodInvocationException;
import org.apache.velocity.exception.ParseErrorException;
import org.apache.velocity.exception.ResourceNotFoundException;
import org.apache.velocity.runtime.directive.Directive;
import org.apache.velocity.runtime.parser.node.Node;

/**
 * 按钮权限 <br/>
 *
 * @date 2015年8月20日 <br/>
 * @author fangtinghua <br/>
 * @version v1.0 <br/>
 */
public abstract class ButtonDirective extends Directive {

    @Override
    public int getType() {
        return Directive.LINE;
    }

    /**
     * 获取权限列表
     *
     * @author fangtinghua
     * @param context
     * @param node
     * @return
     */
    public abstract List<String> getCodeList(InternalContextAdapter context, Node node);

    @Override
    public boolean render(InternalContextAdapter context, Writer writer, Node node) throws IOException, ResourceNotFoundException, ParseErrorException,
            MethodInvocationException {
        String var = "_button_code_list_";
        // velocity上下文
        context.put(var, this.getCodeList(context, node));
        return true;
    }
}