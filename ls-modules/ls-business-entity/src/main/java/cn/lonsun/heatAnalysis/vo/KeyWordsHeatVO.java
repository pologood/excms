/*
 * KeyWordsHeatVO.java         2016年4月6日 <br/>
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

package cn.lonsun.heatAnalysis.vo;

/**
 * TODO <br/>
 *
 * @date 2016年4月6日 <br/>
 * @author fangtinghua <br/>
 * @version v1.0 <br/>
 */
public class KeyWordsHeatVO {

    private Long siteId;// 站点id
    private String keyWords;// 关键词
    private Long searchTimes;// 搜索次数

    public Long getSiteId() {
        return siteId;
    }

    public void setSiteId(Long siteId) {
        this.siteId = siteId;
    }

    public String getKeyWords() {
        return keyWords;
    }

    public void setKeyWords(String keyWords) {
        this.keyWords = keyWords;
    }

    public Long getSearchTimes() {
        return searchTimes;
    }

    public void setSearchTimes(Long searchTimes) {
        this.searchTimes = searchTimes;
    }
}