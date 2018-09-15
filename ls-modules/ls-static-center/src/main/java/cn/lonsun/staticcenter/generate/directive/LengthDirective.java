/*
 * LengthDirective.java         2015年11月27日 <br/>
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

package cn.lonsun.staticcenter.generate.directive;

import java.io.IOException;
import java.io.Writer;

import org.apache.velocity.context.InternalContextAdapter;
import org.apache.velocity.exception.MethodInvocationException;
import org.apache.velocity.exception.ParseErrorException;
import org.apache.velocity.exception.ResourceNotFoundException;
import org.apache.velocity.runtime.directive.Directive;
import org.apache.velocity.runtime.parser.node.Node;

/**
 * 长度截取 <br/>
 *
 * @date 2015年11月27日 <br/>
 * @author fangtinghua <br/>
 * @version v1.0 <br/>
 */
public class LengthDirective extends Directive {

    @Override
    public String getName() {
        return "length";
    }

    @Override
    public int getType() {
        return LINE;
    }

    @Override
    public boolean render(InternalContextAdapter context, Writer writer, Node node) throws IOException, ResourceNotFoundException, ParseErrorException,
            MethodInvocationException {
        String title = (String) node.jjtGetChild(0).value(context);
        if (node.jjtGetNumChildren() == 1) {
            writer.write(title);
            return true;
        }
        int length = Integer.valueOf((String) node.jjtGetChild(1).value(context));
        if (title.length() > length) {
            writer.write(title.substring(0, length));
        } else {
            writer.write(title);
        }
        return true;
    }
}