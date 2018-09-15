/*
 * DateFormatDirective.java         2015年9月23日 <br/>
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

import cn.lonsun.core.util.SpringContextHolder;
import cn.lonsun.staticcenter.generate.GenerateException;
import cn.lonsun.staticcenter.generate.tag.PPretreatment;
import cn.lonsun.staticcenter.generate.tag.impl.FormatPPretreatment;

/**
 * 日期格式化 <br/>
 *
 * @date 2015年9月23日 <br/>
 * @author fangtinghua <br/>
 * @version v1.0 <br/>
 */
public class DateFormatDirective extends Directive {

    @Override
    public String getName() {
        return "date_format";
    }

    @Override
    public int getType() {
        return LINE;
    }

    @Override
    public boolean render(InternalContextAdapter context, Writer writer, Node node) throws IOException, ResourceNotFoundException, ParseErrorException,
            MethodInvocationException {
        String result = "";
        String date = (String) node.jjtGetChild(0).value(context);
        PPretreatment p = SpringContextHolder.getBean(FormatPPretreatment.class);
        String pattern = "yyyy-MM-dd HH:mm:ss";
        if (node.jjtGetNumChildren() > 1) {
            pattern = (String) node.jjtGetChild(1).value(context);
        }
        try {
            result = p.getValue(date, pattern);
        } catch (GenerateException e) {
        }
        writer.write(result);
        return true;
    }
}