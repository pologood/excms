/*
 * PublicContentVO.java         2015年12月15日 <br/>
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

import org.springframework.format.annotation.DateTimeFormat;

import com.alibaba.fastjson.annotation.JSONField;
import com.fasterxml.jackson.annotation.JsonFormat;

import javax.persistence.Column;

/**
 * 信息公开 <br/>
 *
 * @author fangtinghua <br/>
 * @version v1.0 <br/>
 * @date 2015年12月15日 <br/>
 */
public class PublicContentVO {

    private Long siteId;// 站点id

    /**
     * 公开内容字段 start
     */
    private Long id;// 主键
    private String summarize;// 内容概述
    private Long organId;// 组织id
    private Long contentId;// 内容主表
    private String type;// 公开类型
    private Long sortNum;
    private String attachSavedName;// 附件保存名
    private String attachRealName;// 附件真实名
    private Long attachSize;// 附件大小
    /** 公开内容字段 end */

    /**
     * 主动公开需要字段 start
     */
    private Long catId;// 只有主动公开有目录id
    private String indexNum;// 索引号
    private String fileNum;// 文号
    private String classIds;// 分类ids
    private String parentClassIds;// 父分类ids
    private String classNames;// 名称
    private String synColumnIds;// 同步到栏目的ID
    private String synColumnNames;// 名称
    private String synOrganCatIds;// 同步到单位目录的ID
    private String synOrganCatNames;// 名称
    private String synMsgCatIds;// 同步到消息分类的ID
    private String synMsgCatNames;// 名称
    /** 主动公开需要字段 end */

    /**
     * 主动公开增加两个字段生效日期和废止日期 start at 2016年8月31日11:38:26 by fangth
     */
    private String effectiveDate;// 生效日期
    private String repealDate;// 废止日期
    /** 主动公开增加两个字段生效日期和废止日期 end */

    /**
     * baseContent表内容 start
     */
    private String title;
    private String keyWords;
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+08:00")
    @JSONField(format = "yyyy-MM-dd HH:mm:ss")
    private Date publishDate;
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+08:00")
    @JSONField(format = "yyyy-MM-dd HH:mm:ss")
    private Date createDate;
    private String content;
    private Integer isPublish = 0;
    private Integer isTop = 0;//是否置顶
    private String author;// 作者
    private String resources;// 来源
    private String redirectLink;//跳转链接

    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+08:00")
    @JSONField(format = "yyyy-MM-dd HH:mm:ss")
    private Date sortDate;

    private Long hit;// 点击数

    /**
     * 生成静态时的链接
     */
    private String link;
    private String catName;// 目录名称
    private String organName;// 单位名称

    private Long counts;// 统计用

    /**
     * baseContent表内容 end
     */
    private Integer isInvalid = 0;//是否失效
    private String invalidReason;//失效原因
    private String filePath;// 解读文件地址
    private String relContentId;//关联的文章Id
    private String attribute;//信息公开文章所在目录属性
    private String titleColor;
    private Integer isBold = 0;
    // 是否下划线
    private Integer isUnderline = 0;
    // 是否倾斜
    private Integer isTilt = 0;
    private String subTitle;
    private String article;
    private Integer isAllowComments = 0;
    private Integer videoStatus = 100;//视频转换状态，默认100代表已经转换好，或者不存在视频信息，0为未转换

    private String oldSchemaId;

    private String logStr;//复制时用来传递操作日志

    private boolean referedNews = false; //是否被引用新闻
    private boolean referNews = false; //是否引用新闻

    public Long getId() {
        return id;
    }

    public Long getSiteId() {
        return siteId;
    }

    public void setSiteId(Long siteId) {
        this.siteId = siteId;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getSummarize() {
        return summarize;
    }

    public void setSummarize(String summarize) {
        this.summarize = summarize;
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

    public String getClassNames() {
        return classNames;
    }

    public void setClassNames(String classNames) {
        this.classNames = classNames;
    }

    public String getSynColumnIds() {
        return synColumnIds;
    }

    public void setSynColumnIds(String synColumnIds) {
        this.synColumnIds = synColumnIds;
    }

    public String getSynColumnNames() {
        return synColumnNames;
    }

    public void setSynColumnNames(String synColumnNames) {
        this.synColumnNames = synColumnNames;
    }

    public String getSynOrganCatIds() {
        return synOrganCatIds;
    }

    public void setSynOrganCatIds(String synOrganCatIds) {
        this.synOrganCatIds = synOrganCatIds;
    }

    public String getSynOrganCatNames() {
        return synOrganCatNames;
    }

    public void setSynOrganCatNames(String synOrganCatNames) {
        this.synOrganCatNames = synOrganCatNames;
    }

    public String getSynMsgCatIds() {
        return synMsgCatIds;
    }

    public void setSynMsgCatIds(String synMsgCatIds) {
        this.synMsgCatIds = synMsgCatIds;
    }

    public String getSynMsgCatNames() {
        return synMsgCatNames;
    }

    public void setSynMsgCatNames(String synMsgCatNames) {
        this.synMsgCatNames = synMsgCatNames;
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

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getKeyWords() {
        return keyWords;
    }

    public void setKeyWords(String keyWords) {
        this.keyWords = keyWords;
    }

    public Date getPublishDate() {
        return publishDate;
    }

    public void setPublishDate(Date publishDate) {
        this.publishDate = publishDate;
    }

    public Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Long getSortNum() {
        return sortNum;
    }

    public void setSortNum(Long sortNum) {
        this.sortNum = sortNum;
    }

    public String getAttachSavedName() {
        return attachSavedName;
    }

    public void setAttachSavedName(String attachSavedName) {
        this.attachSavedName = attachSavedName;
    }

    public String getAttachRealName() {
        return attachRealName;
    }

    public void setAttachRealName(String attachRealName) {
        this.attachRealName = attachRealName;
    }

    public Long getAttachSize() {
        return attachSize;
    }

    public void setAttachSize(Long attachSize) {
        this.attachSize = attachSize;
    }

    public Integer getIsPublish() {
        return isPublish;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getResources() {
        return resources;
    }

    public void setResources(String resources) {
        this.resources = resources;
    }

    public String getRedirectLink() {
        return redirectLink;
    }

    public void setRedirectLink(String redirectLink) {
        this.redirectLink = redirectLink;
    }

    public void setIsPublish(Integer isPublish) {
        this.isPublish = isPublish;
    }

    public Long getHit() {
        return hit;
    }

    public void setHit(Long hit) {
        this.hit = hit;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getCatName() {
        return catName;
    }

    public void setCatName(String catName) {
        this.catName = catName;
    }

    public String getOrganName() {
        return organName;
    }

    public void setOrganName(String organName) {
        this.organName = organName;
    }

    public Long getCounts() {
        return counts;
    }

    public void setCounts(Long counts) {
        this.counts = counts;
    }

    public Integer getIsTop() {
        return isTop;
    }

    public void setIsTop(Integer isTop) {
        this.isTop = isTop;
    }

    public Date getSortDate() {
        return sortDate;
    }

    public void setSortDate(Date sortDate) {
        this.sortDate = sortDate;
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

    public String getRelContentId() {
        return relContentId;
    }

    public void setRelContentId(String relContentId) {
        this.relContentId = relContentId;
    }

    public String getTitleColor() {
        return titleColor;
    }

    public void setTitleColor(String titleColor) {
        this.titleColor = titleColor;
    }

    public Integer getIsBold() {
        return isBold;
    }

    public void setIsBold(Integer isBold) {
        this.isBold = isBold;
    }

    public Integer getIsUnderline() {
        return isUnderline;
    }

    public void setIsUnderline(Integer isUnderline) {
        this.isUnderline = isUnderline;
    }

    public Integer getIsTilt() {
        return isTilt;
    }

    public void setIsTilt(Integer isTilt) {
        this.isTilt = isTilt;
    }

    public String getSubTitle() {
        return subTitle;
    }

    public void setSubTitle(String subTitle) {
        this.subTitle = subTitle;
    }

    public String getArticle() {
        return article;
    }

    public void setArticle(String article) {
        this.article = article;
    }

    public Integer getIsAllowComments() {
        return isAllowComments;
    }

    public void setIsAllowComments(Integer isAllowComments) {
        this.isAllowComments = isAllowComments;
    }

    public String getAttribute() {
        return attribute;
    }

    public void setAttribute(String attribute) {
        this.attribute = attribute;
    }

    public Integer getVideoStatus() {
        return videoStatus;
    }

    public void setVideoStatus(Integer videoStatus) {
        this.videoStatus = videoStatus;
    }

    public String getOldSchemaId() {
        return oldSchemaId;
    }

    public void setOldSchemaId(String oldSchemaId) {
        this.oldSchemaId = oldSchemaId;
    }

    public String getLogStr() {
        return logStr;
    }

    public void setLogStr(String logStr) {
        this.logStr = logStr;
    }

    public boolean isReferedNews() {
        return referedNews;
    }

    public void setReferedNews(boolean referedNews) {
        this.referedNews = referedNews;
    }

    public boolean isReferNews() {
        return referNews;
    }

    public void setReferNews(boolean referNews) {
        this.referNews = referNews;
    }
}