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

import org.apache.commons.lang.time.DateFormatUtils;
import org.apache.velocity.context.InternalContextAdapter;
import org.apache.velocity.exception.MethodInvocationException;
import org.apache.velocity.exception.ParseErrorException;
import org.apache.velocity.exception.ResourceNotFoundException;
import org.apache.velocity.runtime.directive.Directive;
import org.apache.velocity.runtime.parser.node.Node;

import java.io.IOException;
import java.io.Writer;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 日期格式化 <br/>
 *
 * @date 2015年9月23日 <br/>
 * @author fangtinghua <br/>
 * @version v1.0 <br/>
 */
public class CutDateFormatDirective extends Directive {

    @Override
    public String getName() {
        return "cut_date_format";
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
        String cut = (String) node.jjtGetChild(1).value(context);

        String date = null;
        String format = null;
        if(null != cut && null != ms) {
            boolean flag = false;
            if(cut.contains("yyyy")) {
                date = ms.substring(cut.indexOf("yyyy"),cut.indexOf("yyyy") + 4);
                flag = true;
                format = "yyyy";
            }

            if(cut.contains("MM")) {
                if(flag) {
                    date += "-";
                    format += "-";
                }
                date += ms.substring(cut.indexOf("MM"),cut.indexOf("MM") + 2);
                format += "MM";
            }

            if(cut.contains("dd")) {
                if(flag) {
                    date += "-";
                    format += "-";
                }
                date += ms.substring(cut.indexOf("dd"),cut.indexOf("dd") + 2);
                format += "dd";
            }

            if(cut.contains("HH")) {
                if(flag) {
                    date += " ";
                    format += " ";
                }
                date += ms.substring(cut.indexOf("HH"),cut.indexOf("HH") + 2);
                format += "HH";
            }

            if(cut.contains("mm")) {
                if(flag) {
                    date += ":";
                    format += ":";
                }
                date += ms.substring(cut.indexOf("mm"),cut.indexOf("mm") + 2);
                format += "mm";
            }

            if(cut.contains("ss")) {
                if(flag) {
                    date += ":";
                    format += ":";
                }
                date += ms.substring(cut.indexOf("ss"),cut.indexOf("ss") + 2);
                format += "ss";
            }
        }

        if(null != format){
            SimpleDateFormat sdf = new SimpleDateFormat(format);
            Date rd = null;
            try {
                rd = sdf.parse(date);
            }catch (Exception e) {
                e.printStackTrace();
            }

            if(null != rd) {
                String pattern = "yyyy-MM-dd HH:mm:ss";
                if (node.jjtGetNumChildren() > 1) {
                    pattern = (String) node.jjtGetChild(2).value(context);
                }
                result = null == date ? "" : DateFormatUtils.format(rd, pattern);
                writer.write(result);
            } else {
                writer.write(result);
            }
        }

        return true;
    }
}