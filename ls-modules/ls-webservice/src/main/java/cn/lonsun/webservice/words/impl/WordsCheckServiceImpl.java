/*
 * WordsCheckServiceImpl.java         2016年9月19日 <br/>
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

package cn.lonsun.webservice.words.impl;

import java.util.List;

import org.springframework.stereotype.Service;

import cn.lonsun.webservice.core.WebServiceCaller;
import cn.lonsun.webservice.words.IWordsCheckService;

/**
 * TODO <br/>
 * 
 * @date 2016年9月19日 <br/>
 * @author fangtinghua <br/>
 * @version v1.0 <br/>
 */
@Service("WordsCheckServiceImpl")
public class WordsCheckServiceImpl implements IWordsCheckService {

    /**
     * 服务编码 ADD REASON. <br/>
     * 
     * @date: 2016年9月19日 上午11:26:33 <br/>
     * @author fangtinghua
     */
    private enum Codes {
        WordsCheck_getWords
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> List<T> getWords(String content, String type, Long siteId, Class<T> clazz) {
        String code = Codes.WordsCheck_getWords.toString();
        return (List<T>) WebServiceCaller.getList(code, new Object[] { content, type, siteId }, clazz);
    }
}