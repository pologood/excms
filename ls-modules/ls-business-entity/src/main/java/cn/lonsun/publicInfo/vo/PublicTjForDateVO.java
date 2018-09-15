/*
 * PublicTjForDateVO.java         2016年10月11日 <br/>
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

package cn.lonsun.publicInfo.vo;

/**
 * 信息公开按照时间进行统计 <br/>
 * 
 * @date 2016年10月11日 <br/>
 * @author fangtinghua <br/>
 * @version v1.0 <br/>
 */
public class PublicTjForDateVO {

    private Long organId;
    private String organName;
    private Long monthCount;// 月度
    private Long seasonCount;// 季度
    private Long yearCount;// 年度

    public Long getOrganId() {
        return organId;
    }

    public void setOrganId(Long organId) {
        this.organId = organId;
    }

    public String getOrganName() {
        return organName;
    }

    public void setOrganName(String organName) {
        this.organName = organName;
    }

    public Long getMonthCount() {
        return monthCount;
    }

    public void setMonthCount(Long monthCount) {
        this.monthCount = monthCount;
    }

    public Long getSeasonCount() {
        return seasonCount;
    }

    public void setSeasonCount(Long seasonCount) {
        this.seasonCount = seasonCount;
    }

    public Long getYearCount() {
        return yearCount;
    }

    public void setYearCount(Long yearCount) {
        this.yearCount = yearCount;
    }
}