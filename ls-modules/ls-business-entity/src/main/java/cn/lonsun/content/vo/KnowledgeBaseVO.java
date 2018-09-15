package cn.lonsun.content.vo;

import cn.lonsun.core.base.entity.ABaseEntity;
import com.alibaba.fastjson.annotation.JSONField;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

/**
 * @author zhangmf
 * @version 2018-05-23 17:33
 */
public class KnowledgeBaseVO extends ABaseEntity {
    private static final long serialVersionUID = 1L;

    private Long knowledgeBaseId;

    private Long contentId;

    //所属分类
    private String categoryCode;

    //所属分类名称
    private String categoryName;

    //详细内容
    private String content;
    //答复内容
    private String replyContent;
    //标题
    private String title;
    // 栏目Id
    private Long columnId;
    // 站点Id
    private Long siteId;
    // 文章类型（1.转向链接、2.文章页、3.图片新闻）
    private String typeCode;
    // 副标题
    private String subTitle;
    // 标题颜色
    private String titleColor;
    // 是否加粗
    private Integer isBold = 0;
    // 是否下划线
    private Integer isUnderline = 0;
    // 是否倾斜
    private Integer isTilt = 0;
    // 标新
    private Integer isNew = 0;
    // 来源
    private String resources;
    // 加热
    private Integer isHot = 0;
    // 置顶
    private Integer isTop = 0;
    // 置顶有效期
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+08:00")
    @JSONField(format = "yyyy-MM-dd HH:mm:ss")
    private Date topValidDate;
    // 标题新闻
    private Integer isTitle = 0;
    // 发布状态
    private Integer isPublish = 0;
    // 发布时间
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+08:00")
    @JSONField(format = "yyyy-MM-dd HH:mm:ss")
    private Date publishDate;
    // 作者
    private String author;

    // 链接地址
    private String redirectLink;

    private Integer isAllowComments = 0;

    // 是否定时发布
    private Integer isJob = 0;
    //定时发布时间
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+08:00")
    @JSONField(format = "yyyy-MM-dd HH:mm:ss")
    private Date jobIssueDate;

    public Long getKnowledgeBaseId() {
        return knowledgeBaseId;
    }

    public void setKnowledgeBaseId(Long knowledgeBaseId) {
        this.knowledgeBaseId = knowledgeBaseId;
    }

    public Long getContentId() {
        return contentId;
    }

    public void setContentId(Long contentId) {
        this.contentId = contentId;
    }

    public String getCategoryCode() {
        return categoryCode;
    }

    public void setCategoryCode(String categoryCode) {
        this.categoryCode = categoryCode;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getReplyContent() {
        return replyContent;
    }

    public void setReplyContent(String replyContent) {
        this.replyContent = replyContent;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Long getColumnId() {
        return columnId;
    }

    public void setColumnId(Long columnId) {
        this.columnId = columnId;
    }

    public Long getSiteId() {
        return siteId;
    }

    public void setSiteId(Long siteId) {
        this.siteId = siteId;
    }

    public String getTypeCode() {
        return typeCode;
    }

    public void setTypeCode(String typeCode) {
        this.typeCode = typeCode;
    }

    public String getSubTitle() {
        return subTitle;
    }

    public void setSubTitle(String subTitle) {
        this.subTitle = subTitle;
    }

    public Integer getIsNew() {
        return isNew;
    }

    public void setIsNew(Integer isNew) {
        this.isNew = isNew;
    }

    public String getResources() {
        return resources;
    }

    public void setResources(String resources) {
        this.resources = resources;
    }

    public Integer getIsHot() {
        return isHot;
    }

    public void setIsHot(Integer isHot) {
        this.isHot = isHot;
    }

    public Integer getIsTop() {
        return isTop;
    }

    public void setIsTop(Integer isTop) {
        this.isTop = isTop;
    }

    public Date getTopValidDate() {
        return topValidDate;
    }

    public void setTopValidDate(Date topValidDate) {
        this.topValidDate = topValidDate;
    }

    public Integer getIsTitle() {
        return isTitle;
    }

    public void setIsTitle(Integer isTitle) {
        this.isTitle = isTitle;
    }

    public Integer getIsPublish() {
        return isPublish;
    }

    public void setIsPublish(Integer isPublish) {
        this.isPublish = isPublish;
    }

    public Date getPublishDate() {
        return publishDate;
    }

    public void setPublishDate(Date publishDate) {
        this.publishDate = publishDate;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public Integer getIsAllowComments() {
        return isAllowComments;
    }

    public void setIsAllowComments(Integer isAllowComments) {
        this.isAllowComments = isAllowComments;
    }

    public Integer getIsJob() {
        return isJob;
    }

    public void setIsJob(Integer isJob) {
        this.isJob = isJob;
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

    public String getRedirectLink() {
        return redirectLink;
    }

    public void setRedirectLink(String redirectLink) {
        this.redirectLink = redirectLink;
    }

    public Date getJobIssueDate() {
        return jobIssueDate;
    }

    public void setJobIssueDate(Date jobIssueDate) {
        this.jobIssueDate = jobIssueDate;
    }
}
