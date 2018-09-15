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

import cn.lonsun.common.vo.PageQueryVO;

/**
 * 查询对象参数 <br/>
 */
public class PublicContentRetrievalVO extends PageQueryVO {

    private Long siteId; // 站点Id
    private String keyWord;// 关键字
    private Long organId;// 单位id
    private String organName;// 部门名称
    private String fileNum;// 文号
    private String classIds;// 分类ids
    private Long catId;// 组配分类
    private Long[] catIds;// 组配分类
    private String startDate;// 开始时间
    private String endDate;// 结束时间

    public String getKeyWord() {
        return keyWord;
    }

    public void setKeyWord(String keyWord) {
        this.keyWord = keyWord;
    }

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

    public String getFileNum() {
        return fileNum;
    }

    public void setFileNum(String fileNum) {
        this.fileNum = fileNum;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public Long getSiteId() {
        return siteId;
    }

    public void setSiteId(Long siteId) {
        this.siteId = siteId;
    }

    public String getClassIds() {
        return classIds;
    }

    public void setClassIds(String classIds) {
        this.classIds = classIds;
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
}