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

import org.apache.velocity.context.InternalContextAdapter;
import org.apache.velocity.exception.MethodInvocationException;
import org.apache.velocity.exception.ParseErrorException;
import org.apache.velocity.exception.ResourceNotFoundException;
import org.apache.velocity.runtime.directive.Directive;
import org.apache.velocity.runtime.parser.node.Node;

import java.io.IOException;
import java.io.Writer;

/**
 * texthtml 转换HTML <br/>
 *
 * @author doocal <br/>
 * @version v1.0 <br/>
 * @date 2016年7月17日 <br/>
 */
public class TextareaHtmlDirective extends Directive {

    @Override
    public String getName() {
        return "text2html";
    }

    @Override
    public int getType() {
        return LINE;
    }

    @Override
    public boolean render(InternalContextAdapter context, Writer writer, Node node) throws IOException, ResourceNotFoundException, ParseErrorException,
        MethodInvocationException {
        String str = (String) node.jjtGetChild(0).value(context);
        if (null != str) {
            str = Text2Html(str);
        }
        writer.write(str);
        return true;
    }

    /**
     * TextArea文本转换为Html:写入数据库时使用
     *
     * @param str
     * @return
     */
    private static String Text2Html(String str) {
        if (str == null) {
            return "";
        } else if (str.length() == 0) {
            return "";
        }
        str = str.replaceAll(" ", "&nbsp;");
        str = str.replaceAll("\n", "<br/>");
        str = str.replaceAll("\r", "<br/>");
        return str;
    }
}