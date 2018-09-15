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

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.velocity.context.InternalContextAdapter;
import org.apache.velocity.exception.MethodInvocationException;
import org.apache.velocity.exception.ParseErrorException;
import org.apache.velocity.exception.ResourceNotFoundException;
import org.apache.velocity.runtime.directive.Directive;
import org.apache.velocity.runtime.parser.node.Node;

import java.io.IOException;
import java.io.Writer;

/**
 * 长度截取 <br/>
 *
 * @author fangtinghua <br/>
 * @version v1.0 <br/>
 * @date 2015年11月27日 <br/>
 */
public class CutStrDirective extends Directive {

    @Override
    public String getName() {
        return "cutstr";
    }

    @Override
    public int getType() {
        return LINE;
    }

    @Override
    public boolean render(InternalContextAdapter context, Writer writer, Node node) throws IOException, ResourceNotFoundException, ParseErrorException,
        MethodInvocationException {
        String str = (String) node.jjtGetChild(0).value(context);
        Object obj = node.jjtGetChild(1).value(context);
        if (str != null && null != obj) {
            String lenStr = obj.toString();
            if (NumberUtils.isNumber(lenStr)) {
                int length = NumberUtils.toInt(lenStr);
                Integer numChildren = node.jjtGetNumChildren();
                String elide = "";
                if (numChildren > 2) {
                    elide = (String) node.jjtGetChild(2).value(context);
                }
                str = splitString(str, length * 2, StringUtils.isEmpty(elide) ? "..." : elide);
            }
        }
        writer.write(str);
        return true;
    }

    /**
     * 字符串按字节截取
     *
     * @param str   原字符
     * @param len   截取长度
     * @param elide 省略符
     * @return String
     */
    public static String splitString(String str, int len, String elide) {
        if (str.length() * 2 <= len) {
            return str;
        }
        char[] chArr = str.toCharArray();
        int lenByte = 0;
        for (int i = 0; i < chArr.length; i++) {
            if (chArr[i] > 255) {
                lenByte += 2;
            } else {
                ++lenByte;
            }
            if (lenByte >= len) {
                if (lenByte == len) {
                    return str.substring(0, i) + elide.trim();
                }
                return str.substring(0, i - 1) + elide.trim();
            }
        }
        return str;
    }
}