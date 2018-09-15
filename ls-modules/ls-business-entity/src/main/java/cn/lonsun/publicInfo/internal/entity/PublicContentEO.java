/*
 * PublicContentEO.java         2015年12月15日 <br/>
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

package cn.lonsun.publicInfo.internal.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import cn.lonsun.core.base.entity.AMockEntity;

/**
 * 公开内容 <br/>
 *
 * @author fangtinghua <br/>
 * @version v1.0 <br/>
 * @date 2015年12月15日 <br/>
 */
@Entity
@Table(name = "CMS_PUBLIC_CONTENT")
public class PublicContentEO extends AMockEntity {

    public static final String PUBLIC_ITEM_CODE = "public_info_menu";// 数据字典code
    public static final String PUBLIC_ITEM_INSTITUTION_CODE = "public_institution";// 公开制度规定字典code
    public static final String PUBLIC_ITEM_GUIDE_CODE = "public_guide";// 公开指南分类字典code

    /**
     * 公开类型 ADD REASON. <br/>
     *
     * @author fangtinghua
     * @date: 2015年12月15日 下午2:18:22 <br/>
     */
    public enum Type {
        DRIVING_PUBLIC, // 主动公开
        PUBLIC_GUIDE, // 公开指南
        PUBLIC_ANNUAL_REPORT, // 公开年报
        PUBLIC_INSTITUTION, // 公开制度
        PUBLIC_, // 依申请公开
        PUBLIC_CATALOG, // 依申请公开目录
        PUBLIC_COMPLETION, // 办理情况及办结率
        PUBLIC_POINTS, // 公开要点
        PUBLIC_TREND // 公开动态
    }

    /**
     * serialVersionUID:TODO.
     */
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "ID")
    private Long id;// 主键

    @Column(name = "SITE_ID")
    private Long siteId;// 站点id

    /**
     * 主动公开需要字段 start
     */
    @Column(name = "CAT_ID")
    private Long catId;// 只有主动公开有目录id
    @Column(name = "INDEX_NUM")
    private String indexNum;// 索引号
    @Column(name = "FILE_NUM")
    private String fileNum;// 文号
    @Column(name = "CLASS_IDS")
    private String classIds;// 分类ids
    @Column(name = "PARENT_CLASS_IDS")
    private String parentClassIds;// 父分类ids
    @Column(name = "SYN_COLUMN_IDS")
    private String synColumnIds;// 同步到栏目的ID
    @Column(name = "SYN_ORGAN_CAT_IDS")
    private String synOrganCatIds;// 同步到单位目录的ID
    @Column(name = "SYN_MSG_CAT_IDS")
    private String synMsgCatIds;// 同步到消息分类的ID
    /**
     * 主动公开需要字段 end
     */

    @Column(name = "SUMMARIZE")
    private String summarize;// 内容概述
    @Column(name = "ORGAN_ID")
    private Long organId;// 组织id
    @Column(name = "CONTENT_ID")
    private Long contentId;// 内容主表
    @Column(name = "TYPE")
    private String type;
    @Column(name = "SORT_NUM")
    private Long sortNum;
    @Column(name = "KEY_WORDS")
    private String keyWords;// 关键词

    /**
     * 主动公开增加两个字段生效日期和废止日期 start at 2016年8月31日11:38:26 by fangth
     */
    @Column(name = "EFFECTIVE_DATE")
    private String effectiveDate;// 生效日期
    @Column(name = "REPEAL_DATE")
    private String repealDate;// 废止日期

    /**
     * 主动公开增加两个字段生效日期和废止日期 end
     */
    @Column(name = "FORWARD_URL")
    private String forwardUrl;// 转向地址
    @Column(name = "is_invalid")
    private Integer isInvalid = 0;// 是否失效
    @Column(name = "invalid_reason")
    private String invalidReason;// 失效原因
    @Column(name = "FILE_PATH")
    private String filePath;// 解读文件地址
    @Column(name = "REL_CONTENT_ID")
    private String relContentId;

    @Column(name = "PLATFORM_ID")
    private String platformId;// 第三方平台内容id

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getSiteId() {
        return siteId;
    }

    public void setSiteId(Long siteId) {
        this.siteId = siteId;
    }

    public Long getCatId() {
        return catId;
    }

    public void setCatId(Long catId) {
        this.catId = catId;
    }

    public String getIndexNum() {
        return indexNum;
    }

    public void setIndexNum(String indexNum) {
        this.indexNum = indexNum;
    }

    public String getSummarize() {
        return summarize;
    }

    public void setSummarize(String summarize) {
        this.summarize = summarize;
    }

    public String getFileNum() {
        return fileNum;
    }

    public void setFileNum(String fileNum) {
        this.fileNum = fileNum;
    }

    public String getClassIds() {
        return classIds;
    }

    public void setClassIds(String classIds) {
        this.classIds = classIds;
    }

    public String getParentClassIds() {
        return parentClassIds;
    }

    public void setParentClassIds(String parentClassIds) {
        this.parentClassIds = parentClassIds;
    }

    public String getSynColumnIds() {
        return synColumnIds;
    }

    public void setSynColumnIds(String synColumnIds) {
        this.synColumnIds = synColumnIds;
    }

    public String getSynOrganCatIds() {
        return synOrganCatIds;
    }

    public void setSynOrganCatIds(String synOrganCatIds) {
        this.synOrganCatIds = synOrganCatIds;
    }

    public String getSynMsgCatIds() {
        return synMsgCatIds;
    }

    public void setSynMsgCatIds(String synMsgCatIds) {
        this.synMsgCatIds = synMsgCatIds;
    }

    public Long getOrganId() {
        return organId;
    }

    public void setOrganId(Long organId) {
        this.organId = organId;
    }

    public Long getContentId() {
        return contentId;
    }

    public void setContentId(Long contentId) {
        this.contentId = contentId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Long getSortNum() {
        return sortNum;
    }

    public void setSortNum(Long sortNum) {
        this.sortNum = sortNum;
    }

    public String getKeyWords() {
        return keyWords;
    }

    public void setKeyWords(String keyWords) {
        this.keyWords = keyWords;
    }

    public String getEffectiveDate() {
        return effectiveDate;
    }

    public void setEffectiveDate(String effectiveDate) {
        this.effectiveDate = effectiveDate;
    }

    public String getRepealDate() {
        return repealDate;
    }

    public void setRepealDate(String repealDate) {
        this.repealDate = repealDate;
    }

    public String getForwardUrl() {
        return forwardUrl;
    }

    public void setForwardUrl(String forwardUrl) {
        this.forwardUrl = forwardUrl;
    }

    public Integer getIsInvalid() {
        return isInvalid;
    }

    public void setIsInvalid(Integer isInvalid) {
        this.isInvalid = isInvalid;
    }

    public String getInvalidReason() {
        return invalidReason;
    }

    public void setInvalidReason(String invalidReason) {
        this.invalidReason = invalidReason;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public String getPlatformId() {
        return platformId;
    }

    public void setPlatformId(String platformId) {
        this.platformId = platformId;
    }

    public String getRelContentId() {
        return relContentId;
    }

    public void setRelContentId(String relContentId) {
        this.relContentId = relContentId;
    }
}