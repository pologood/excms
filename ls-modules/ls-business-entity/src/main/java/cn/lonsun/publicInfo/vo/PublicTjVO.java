/*
 * PublicTjVO.java         2016年7月19日 <br/>
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
 * 信息公开统计 <br/>
 * 
 * @date 2016年7月19日 <br/>
 * @author fangtinghua <br/>
 * @version v1.0 <br/>
 */
public class PublicTjVO {

    private Long organId;
    private Long catId;// 目录id
    private String organName;
    private Long drivingCount;
    private Long applyCount = 0L;
    private Long replyCount = 0L;
    private Long total = 0L;
    private String replyStatus;// 回复状态

    private Long publishCount = 0L;//

    private Long notPublishCount = 0L;//

    public Long getOrganId() {
        return organId;
    }

    public void setOrganId(Long organId) {
        this.organId = organId;
    }

    public Long getCatId() {
        return catId;
    }

    public void setCatId(Long catId) {
        this.catId = catId;
    }

    public String getOrganName() {
        return organName;
    }

    public void setOrganName(String organName) {
        this.organName = organName;
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

    public String getReplyStatus() {
        return replyStatus;
    }

    public void setReplyStatus(String replyStatus) {
        this.replyStatus = replyStatus;
    }

    public Long getNotPublishCount() {
        return notPublishCount;
    }

    public void setNotPublishCount(Long notPublishCount) {
        this.notPublishCount = notPublishCount;
    }

    public Long getPublishCount() {
        return publishCount;
    }

    public void setPublishCount(Long publishCount) {
        this.publishCount = publishCount;
    }
}