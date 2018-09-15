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

import cn.lonsun.common.util.AppUtil;
import org.apache.velocity.context.InternalContextAdapter;
import org.apache.velocity.exception.MethodInvocationException;
import org.apache.velocity.exception.ParseErrorException;
import org.apache.velocity.exception.ResourceNotFoundException;
import org.apache.velocity.runtime.directive.Directive;
import org.apache.velocity.runtime.parser.node.Node;

import java.io.IOException;
import java.io.Writer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 过滤html <br/>
 *
 * @author tangtao
 * @version v1.0
 * @date 2016年5月31日
 */
public class FilterHtmlDirective extends Directive {

    @Override
    public String getName() {
        return "filterHtml";
    }

    @Override
    public int getType() {
        return LINE;
    }

    @Override
    public boolean render(InternalContextAdapter context, Writer writer, Node node) throws IOException,
        ResourceNotFoundException,
        ParseErrorException,
        MethodInvocationException {

        StringBuffer sb = new StringBuffer();

        String str = (String) node.jjtGetChild(0).value(context);
        if (!AppUtil.isEmpty(str)) {
            Pattern pattern = Pattern.compile("<([^>]*)>");
            Matcher matcher = pattern.matcher(str);
            boolean result1 = matcher.find();
            while (result1) {
                matcher.appendReplacement(sb, "");
                result1 = matcher.find();
            }
            matcher.appendTail(sb);
        }
        writer.write(sb.toString());
        return true;
    }

}