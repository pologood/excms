/*
 * IWordsCheckService.java         2016年9月19日 <br/>
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

package cn.lonsun.webservice.words;

import java.util.List;

/**
 * 获得分词结果 <br/>
 * 
 * @date 2016年9月19日 <br/>
 * @author fangtinghua <br/>
 * @version v1.0 <br/>
 */
public interface IWordsCheckService {

    <T> List<T> getWords(String content, String type, Long siteId, Class<T> clazz);
}