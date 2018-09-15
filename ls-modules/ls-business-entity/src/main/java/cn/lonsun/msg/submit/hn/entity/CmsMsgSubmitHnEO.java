package cn.lonsun.msg.submit.hn.entity;

import cn.lonsun.core.base.entity.AMockEntity;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * @author gu.fei
 * @version 2015-5-14 8:23
 */
@Entity
@Table(name = "CMS_MSG_SUBMIT_HN")
public class CmsMsgSubmitHnEO extends AMockEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "ID")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    //标题
    @Column(name = "TITLE")
    private String title;

    //副标题
    @Column(name = "SUB_TITLE")
    private String subTitle;

    //供稿者
    @Column(name = "PROVIDER")
    private String provider;

    //报送单位
    @Column(name = "SUBMIT_UNIT_NAME")
    private String submitUnitName;

    //作者
    @Column(name = "AUTHOR")
    private String author;

    //来源
    @Column(name = "SOURCES")
    private String sources;

    //发布日期
    @Column(name = "PUBLISH_DATE")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+08:00")
    private Date publishDate;

    //缩略图
    @Column(name = "IMAGE_LINK")
    private String imageLink;

    //摘要
    @Column(name = "REMARK")
    private String remark;

    //内容
    @Column(name = "CONTENT")
    private String content;

    //采用数量
    @Column(name = "USE_COUNT")
    private Integer useCount = 0;

    //状态
    @Column(name = "STATUS")
    private Integer status = 0;

    //退回单位
    @Column(name = "BACK_UNIT_NAME")
    private String backUnitName;

    //退回用户
    @Column(name = "BACK_USER_NAME")
    private String backUserName;

    //退回原因
    @Column(name = "BACK_REASON")
    private String backReason;

    //是否加新
    @Column(name = "IS_NEW")
    private Integer isNew = 0;

    //是否加新
    @Column(name = "IS_TITLE")
    private Integer isTitle = 0;

    //是否置顶
    @Column(name = "IS_TOP")
    private Integer isTop = 0;

    //置顶有效期
    @Column(name = "TOP_VALID_DATE")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+08:00")
    private Date topValidDate;

    //跳转地址
    @Column(name = "REDIRECT_LINK")
    private String redirectLink;

    //是否定时发布
    @Column(name = "IS_JOB")
    private Integer isJob = 0;

    //定时发布时间
    @Column(name = "JOB_ISSUE_DATE")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+08:00")
    private Date jobIssueDate;

    @Column(name = "SITE_ID")
    private Long siteId;

    @Column(name = "CREATE_UNIT_ID")
    private Long createUnitId;

    //关联传阅用户是否已经读取信息
    @Transient
    private Integer msgReadStatus;

    @Transient
    private List<CmsMsgToColumnHnEO> columnHnEOs;

    @Transient
    private List<CmsMsgToUserHnEO> userHnEOs;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSubTitle() {
        return subTitle;
    }

    public void setSubTitle(String subTitle) {
        this.subTitle = subTitle;
    }

    public String getProvider() {
        return provider;
    }

    public void setProvider(String provider) {
        this.provider = provider;
    }

    public String getSubmitUnitName() {
        return submitUnitName;
    }

    public void setSubmitUnitName(String submitUnitName) {
        this.submitUnitName = submitUnitName;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getSources() {
        return sources;
    }

    public void setSources(String sources) {
        this.sources = sources;
    }

    public Date getPublishDate() {
        return publishDate;
    }

    public void setPublishDate(Date publishDate) {
        this.publishDate = publishDate;
    }

    public String getImageLink() {
        return imageLink;
    }

    public void setImageLink(String imageLink) {
        this.imageLink = imageLink;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Integer getUseCount() {
        return useCount;
    }

    public void setUseCount(Integer useCount) {
        this.useCount = useCount;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getBackUnitName() {
        return backUnitName;
    }

    public void setBackUnitName(String backUnitName) {
        this.backUnitName = backUnitName;
    }

    public String getBackUserName() {
        return backUserName;
    }

    public void setBackUserName(String backUserName) {
        this.backUserName = backUserName;
    }

    public String getBackReason() {
        return backReason;
    }

    public void setBackReason(String backReason) {
        this.backReason = backReason;
    }

    public Integer getIsNew() {
        return isNew;
    }

    public void setIsNew(Integer isNew) {
        this.isNew = isNew;
    }

    public Integer getIsTitle() {
        return isTitle;
    }

    public void setIsTitle(Integer isTitle) {
        this.isTitle = isTitle;
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

    public String getRedirectLink() {
        return redirectLink;
    }

    public void setRedirectLink(String redirectLink) {
        this.redirectLink = redirectLink;
    }

    public Integer getIsJob() {
        return isJob;
    }

    public void setIsJob(Integer isJob) {
        this.isJob = isJob;
    }

    public Date getJobIssueDate() {
        return jobIssueDate;
    }

    public void setJobIssueDate(Date jobIssueDate) {
        this.jobIssueDate = jobIssueDate;
    }

    public Long getSiteId() {
        return siteId;
    }

    public void setSiteId(Long siteId) {
        this.siteId = siteId;
    }

    public Long getCreateUnitId() {
        return createUnitId;
    }

    public void setCreateUnitId(Long createUnitId) {
        this.createUnitId = createUnitId;
    }

    public List<CmsMsgToColumnHnEO> getColumnHnEOs() {
        return columnHnEOs;
    }

    public void setColumnHnEOs(List<CmsMsgToColumnHnEO> columnHnEOs) {
        this.columnHnEOs = columnHnEOs;
    }

    public List<CmsMsgToUserHnEO> getUserHnEOs() {
        return userHnEOs;
    }

    public void setUserHnEOs(List<CmsMsgToUserHnEO> userHnEOs) {
        this.userHnEOs = userHnEOs;
    }

    public Integer getMsgReadStatus() {
        return msgReadStatus;
    }

    public void setMsgReadStatus(Integer msgReadStatus) {
        this.msgReadStatus = msgReadStatus;
    }
}