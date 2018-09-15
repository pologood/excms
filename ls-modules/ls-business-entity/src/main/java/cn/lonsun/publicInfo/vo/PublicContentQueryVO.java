/*
 * PublicContentQueryVO.java         2015年12月16日 <br/>
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

import java.util.Date;
import java.util.List;

import org.springframework.format.annotation.DateTimeFormat;

import cn.lonsun.common.vo.PageQueryVO;

import com.alibaba.fastjson.annotation.JSONField;
import com.fasterxml.jackson.annotation.JsonFormat;

/**
 * 查询对象参数 <br/>
 * 
 * @date 2015年12月16日 <br/>
 * @author fangtinghua <br/>
 * @version v1.0 <br/>
 */
public class PublicContentQueryVO extends PageQueryVO {

    private Long siteId;// 站点id
    private Long organId;// 组织id
    private List<Long> organIds;
    private Long catId;// 目录id
    private Long[] catIds; // 查询的目录列表
    private Long classId;// 所属分类id
    private String replyStatus;// 回复状态

    private String title;// 标题
    private String indexNum;// 索引号
    private String fileNum;// 文号
    // 开始时间
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+08:00")
    @JSONField(format = "yyyy-MM-dd HH:mm:ss")
    private Date startDate;// 开始时间
    // 开始时间
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+08:00")
    @JSONField(format = "yyyy-MM-dd HH:mm:ss")
    private Date endDate;// 结束时间

    private String type;// 类型
    private Integer isPublish;// 发布状态
    private String key;// 标题、索引号、文号
    private boolean isOrgan = true;// 是否按单位进行查询

    private boolean queryDetail = false;// 是否查询详情，默认不查询
    private String relContentId;//关联的文章的contentId

    public Long getSiteId() {
        return siteId;
    }

    public void setSiteId(Long siteId) {
        this.siteId = siteId;
    }

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

    public Long[] getCatIds() {
        return catIds;
    }

    public void setCatIds(Long[] catIds) {
        this.catIds = catIds;
    }

    public Long getClassId() {
        return classId;
    }

    public void setClassId(Long classId) {
        this.classId = classId;
    }

    public String getReplyStatus() {
        return replyStatus;
    }

    public void setReplyStatus(String replyStatus) {
        this.replyStatus = replyStatus;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getIndexNum() {
        return indexNum;
    }

    public void setIndexNum(String indexNum) {
        this.indexNum = indexNum;
    }

    public String getFileNum() {
        return fileNum;
    }

    public void setFileNum(String fileNum) {
        this.fileNum = fileNum;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Integer getIsPublish() {
        return isPublish;
    }

    public void setIsPublish(Integer isPublish) {
        this.isPublish = isPublish;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public List<Long> getOrganIds() {
        return organIds;
    }

    public void setOrganIds(List<Long> organIds) {
        this.organIds = organIds;
    }

    public boolean isOrgan() {
        return isOrgan;
    }

    public void setOrgan(boolean isOrgan) {
        this.isOrgan = isOrgan;
    }

    public boolean isQueryDetail() {
        return queryDetail;
    }

    public void setQueryDetail(boolean queryDetail) {
        this.queryDetail = queryDetail;
    }

    public String getRelContentId() {
        return relContentId;
    }

    public void setRelContentId(String relContentId) {
        this.relContentId = relContentId;
    }
}