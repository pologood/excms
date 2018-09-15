/*
 * WordsCheckUtil.java         2016年9月26日 <br/>
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

package cn.lonsun.util;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;

import cn.lonsun.site.words.internal.entity.WordsHotConfEO;
import cn.lonsun.monitor.words.internal.util.Type;
import cn.lonsun.monitor.words.internal.util.WordsSplitHolder;

/**
 * 热词处理 <br/>
 * 
 * @date 2016年9月26日 <br/>
 * @author fangtinghua <br/>
 * @version v1.0 <br/>
 */
public class HotWordsCheckUtil {

    private static final String LINK = "<a class='hot-links' target='_blank' href='%s' title='%s'>%s</a>";
    private static final String REGEX = "<a class='hot-links' target='_blank'[^>]+>(.*?)</a>";
    public static final Pattern PATTERN = Pattern.compile(REGEX, Pattern.CASE_INSENSITIVE | Pattern.DOTALL);

    /**
     * 替换热词
     * 
     * @author fangtinghua
     * @param siteId
     * @param article
     * @return
     */
    public static final String replaceAll(Long siteId, String article) {
        try {
            List<WordsHotConfEO> list = WordsSplitHolder.wordsCheck(article, Type.HOT.toString(), siteId);
            if (null != list && !list.isEmpty()) {
                for (WordsHotConfEO h : list) {
                    String name = h.getHotName();// 热词
                    article = article.replaceAll(name, String.format(LINK, h.getHotUrl(), h.getUrlDesc(), name));
                }
            }
            return article;
        } catch (Throwable e) {
            return article;
        }
    }

    /**
     * 恢复热词
     * 
     * @author fangtinghua
     * @param article
     * @return
     */
    public static final String revertAll(String article) {
        if (StringUtils.isEmpty(article)) {
            return StringUtils.EMPTY;
        }
        StringBuffer buffer = new StringBuffer();
        Matcher m = PATTERN.matcher(article);// 正则匹配
        while (m.find()) {// 查找
            m.appendReplacement(buffer, m.group(1).trim());
        }
        return m.appendTail(buffer).toString();
    }
}