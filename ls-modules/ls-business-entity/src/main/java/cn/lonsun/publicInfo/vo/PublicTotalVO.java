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

package cn.lonsun.publicInfo.vo;

import java.util.List;

/**
 * 信息公开统计汇总 <br/>
 *
 * @date 2016年7月19日 <br/>
 * @author fangtinghua <br/>
 * @version v1.0 <br/>
 */
public class PublicTotalVO {

    private int organCount;
    private Long drivingCount;
    private Long applyCount;
    private Long replyCount;
    private Long total;

    private List<PublicTjVO> data;

    public int getOrganCount() {
        return organCount;
    }

    public void setOrganCount(int organCount) {
        this.organCount = organCount;
    }

    public Long getApplyCount() {
        return applyCount;
    }

    public void setApplyCount(Long applyCount) {
        this.applyCount = applyCount;
    }

    public Long getReplyCount() {
        return replyCount;
    }

    public void setReplyCount(Long replyCount) {
        this.replyCount = replyCount;
    }

    public List<PublicTjVO> getData() {
        return data;
    }

    public void setData(List<PublicTjVO> data) {
        this.data = data;
    }

    public Long getDrivingCount() {
        return drivingCount;
    }

    public void setDrivingCount(Long drivingCount) {
        this.drivingCount = drivingCount;
    }

    public Long getTotal() {
        return total;
    }

    public void setTotal(Long total) {
        this.total = total;
    }
}