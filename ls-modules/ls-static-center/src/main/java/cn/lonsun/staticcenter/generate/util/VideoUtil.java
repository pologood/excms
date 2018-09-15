/*
 * VideoUtil.java         2016年7月15日 <br/>
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

package cn.lonsun.staticcenter.generate.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;

/**
 * 手机端访问文章视频正则替换 <br/>
 *
 * @date 2016年7月15日 <br/>
 * @author fangtinghua <br/>
 * @version v1.0 <br/>
 */
public class VideoUtil {
    // 正则
    private static final String ARTICLE_REGEX = "<object\\s+from=\"ex8\".*?<param\\s+name=\"flashvars\"\\s+value=\"(.*?)\"\\s+pic=\"(.*?)\".*?</object>";
    // 两种模式，为了应对换行的匹配
    public static final Pattern ARTICLE_PATTERN = Pattern.compile(ARTICLE_REGEX, Pattern.CASE_INSENSITIVE | Pattern.DOTALL);

    /**
     * 处理文章内容，替换文章中的视频播放插件
     *
     * @author fangtinghua
     * @param article
     * @return
     */
    public static String processArticle(String article) {
        if (StringUtils.isEmpty(article)) {
            return StringUtils.EMPTY;
        }
        StringBuffer buffer = new StringBuffer();
        Matcher m = ARTICLE_PATTERN.matcher(article);// 正则匹配
        while (m.find()) {// 查找
            StringBuffer sb = new StringBuffer();
            sb.append("<div id=\"videocont\"><video id=\"movie\" src=\"");
            sb.append(m.group(1).trim());
            sb.append("\" poster=\"");
            sb.append(m.group(2).trim());
            sb.append("\" controls=\"\" autobuffer=\"\"></video>");
            sb.append("<div id=\"buttonbar\"><img src=\"/assets/images/playbutton.png\" id=\"play\" /> ");
            sb.append("</div></div>");
            m.appendReplacement(buffer, sb.toString());
        }
        return m.appendTail(buffer).toString();
    }

    public static void main(String[] args) {
        StringBuffer sb = new StringBuffer();
        sb.append("zxdc<object from=\"ex8\" pluginspage=\"http://www.macromedia.com/go/getflashplayer\"");
        sb.append("<param name=\"flashvars\" value=\"http://www.5397078.cn/mongo/577b6a63e4b0b3f3138ce162&amp;c=0\"");
        sb.append(" pic=\"http://www.5397078.cn/mongo/577b6a63e4b0b3f3138ce162-0\" /><embed allowscriptaccess=\"always\"");
        sb.append(" pluginspage=\"http://www.macromedia.com/go/getflashplayer\" /></object>dada");
        sb.append("xxxxx<object from=\"ex8\" pluginspage=\"http://www.macromedia.com/go/getflashplayer\"");
        sb.append("<param name=\"flashvars\" value=\"http://www.5397078.cn/mongo/577b6a63e4b0b3f3138ce162&amp;c=1\"");
        sb.append(" pic=\"http://www.5397078.cn/mongo/577b6a63e4b0b3f3138ce162-1\" /><embed allowscriptaccess=\"always\"");
        sb.append(" pluginspage=\"http://www.macromedia.com/go/getflashplayer\" /></object>sssss");
        System.out.println(processArticle(sb.toString()));
    }
}