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

import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.velocity.context.InternalContextAdapter;
import org.apache.velocity.exception.MethodInvocationException;
import org.apache.velocity.exception.ParseErrorException;
import org.apache.velocity.exception.ResourceNotFoundException;
import org.apache.velocity.runtime.directive.Directive;
import org.apache.velocity.runtime.parser.node.Node;

import java.io.IOException;
import java.io.Writer;
import java.util.Date;

/**
 * 日期格式化 <br/>
 *
 * @date 2015年9月23日 <br/>
 * @author fangtinghua <br/>
 * @version v1.0 <br/>
 */
public class MsDateFormatDirective extends Directive {

    @Override
    public String getName() {
        return "ms_date_format";
    }

    @Override
    public int getType() {
        return LINE;
    }

    @Override
    public boolean render(InternalContextAdapter context, Writer writer, Node node) throws IOException, ResourceNotFoundException, ParseErrorException,
            MethodInvocationException {
        String result = "";
        String ms = (String) node.jjtGetChild(0).value(context);
        if(null != ms) {
            Date date = new Date(Long.valueOf(ms));
            String pattern = "yyyy-MM-dd HH:mm:ss";
            if (node.jjtGetNumChildren() > 1) {
                pattern = (String) node.jjtGetChild(1).value(context);
            }
            result = null == date ? "" : DateFormatUtils.format(date, pattern);
            writer.write(result);
        } else {
            writer.write(result);
        }
        return true;
    }
}