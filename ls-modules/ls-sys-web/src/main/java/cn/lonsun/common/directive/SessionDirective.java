/*
 * SessionDirective.java         2015年11月10日 <br/>
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

import org.apache.commons.lang3.StringUtils;
import org.apache.velocity.context.InternalContextAdapter;
import org.apache.velocity.exception.MethodInvocationException;
import org.apache.velocity.exception.ParseErrorException;
import org.apache.velocity.exception.ResourceNotFoundException;
import org.apache.velocity.runtime.directive.Directive;
import org.apache.velocity.runtime.parser.node.Node;
import org.apache.velocity.runtime.parser.node.SimpleNode;

import cn.lonsun.util.LoginPersonUtil;

/**
 * 取session值 <br/>
 *
 * @date 2015年11月10日 <br/>
 * @author fangtinghua <br/>
 * @version v1.0 <br/>
 */
public class SessionDirective extends Directive {

    @Override
    public String getName() {
        return "session";
    }

    @Override
    public int getType() {
        return Directive.LINE;
    }

    @Override
    public boolean render(InternalContextAdapter context, Writer writer, Node node) throws IOException, ResourceNotFoundException, ParseErrorException,
            MethodInvocationException {
        String key = "";
        if (node.jjtGetNumChildren() > 0) {
            SimpleNode keyNode = (SimpleNode) node.jjtGetChild(0);
            key = (String) keyNode.value(context);
        }
        if (StringUtils.isEmpty(key)) {
            writer.write(key);
        } else {
            Object v = LoginPersonUtil.getSession().getAttribute(key);
            writer.write(null == v ? "" : String.valueOf(v));
        }
        return true;
    }
}