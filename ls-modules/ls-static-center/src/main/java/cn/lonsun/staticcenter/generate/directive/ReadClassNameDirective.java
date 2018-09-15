/*
 * ReadClassNameDirective.java         2016年3月1日 <br/>
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

import org.apache.commons.lang3.StringUtils;
import org.apache.velocity.context.InternalContextAdapter;
import org.apache.velocity.exception.MethodInvocationException;
import org.apache.velocity.exception.ParseErrorException;
import org.apache.velocity.exception.ResourceNotFoundException;
import org.apache.velocity.runtime.directive.Directive;
import org.apache.velocity.runtime.parser.node.Node;

import cn.lonsun.cache.client.CacheHandler;
import cn.lonsun.publicInfo.internal.entity.PublicClassEO;

/**
 * 根据文章id和分类id查询所属具体分类名称 <br/>
 *
 * @date 2016年3月1日 <br/>
 * @author fangtinghua <br/>
 * @version v1.0 <br/>
 */
public class ReadClassNameDirective extends Directive {

    @Override
    public String getName() {
        return "readClassName";
    }

    @Override
    public int getType() {
        return LINE;
    }

    @Override
    public boolean render(InternalContextAdapter context, Writer writer, Node node) throws IOException, ResourceNotFoundException, ParseErrorException,
            MethodInvocationException {
        String result = "";
        Long id = Long.valueOf((String) node.jjtGetChild(0).value(context));
        String catIds = (String) node.jjtGetChild(1).value(context);
        if (!StringUtils.isEmpty(catIds)) {
            String[] catArr = catIds.split(",");
            for (String catId : catArr) {
                PublicClassEO child = CacheHandler.getEntity(PublicClassEO.class, catId);
                if (null != child && id.equals(child.getParentId())) {// 分类等于父类
                    result = child.getName();
                    break;
                }
            }
        }
        writer.write(result);
        return true;
    }
}