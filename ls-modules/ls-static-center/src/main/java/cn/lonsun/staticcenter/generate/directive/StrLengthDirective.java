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
 * 长度截取 <br/>
 *
 * @author fangtinghua <br/>
 * @version v1.0 <br/>
 * @date 2015年11月27日 <br/>
 */
public class StrLengthDirective extends Directive {

    @Override
    public String getName() {
        return "strLength";
    }

    @Override
    public int getType() {
        return LINE;
    }

    @Override
    public boolean render(InternalContextAdapter context, Writer writer, Node node) throws IOException, ResourceNotFoundException, ParseErrorException,
        MethodInvocationException {
        int strLen = 0;
        String str = (String) node.jjtGetChild(0).value(context);
        if (str != null) {
            strLen = splitString(str);
        }
        writer.write(String.valueOf(strLen));
        return true;
    }

    /**
     * 字符串按字节截取
     *
     * @param str 字符串
     * @return String
     */
    public static int splitString(String str) {
        char[] chArr = str.toCharArray();
        int lenByte = 0;
        for (int i = 0; i < chArr.length; i++) {
            if (chArr[i] > 255) {
                lenByte += 2;
            } else {
                ++lenByte;
            }
        }
        return lenByte;
    }
}