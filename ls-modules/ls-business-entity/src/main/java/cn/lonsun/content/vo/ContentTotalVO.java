/*
 * PublicTotalVO.java         2016年7月19日 <br/>
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

package cn.lonsun.content.vo;

import java.util.List;

/**
 * 信息公开统计汇总 <br/>
 *
 * @date 2016年8月30日 <br/>
 * @author liuk <br/>
 * @version v1.0 <br/>
 */
public class ContentTotalVO {

    private int organCount;
    private Long thisYearCount;
    private Long lastYearCount;
    private Long thisMonthCount;
    private Long total;

    private List<ContentTjVO> data;

    public int getOrganCount() {
        return organCount;
    }

    public void setOrganCount(int organCount) {
        this.organCount = organCount;
    }

    public List<ContentTjVO> getData() {
        return data;
    }

    public void setData(List<ContentTjVO> data) {
        this.data = data;
    }

    public Long getTotal() {
        return total;
    }

    public void setTotal(Long total) {
        this.total = total;
    }

    public Long getThisYearCount() {
        return thisYearCount;
    }

    public void setThisYearCount(Long thisYearCount) {
        this.thisYearCount = thisYearCount;
    }

    public Long getLastYearCount() {
        return lastYearCount;
    }

    public void setLastYearCount(Long lastYearCount) {
        this.lastYearCount = lastYearCount;
    }

    public Long getThisMonthCount() {
        return thisMonthCount;
    }

    public void setThisMonthCount(Long thisMonthCount) {
        this.thisMonthCount = thisMonthCount;
    }
}