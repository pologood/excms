package cn.lonsun.datacollect.entity;

import cn.lonsun.core.base.entity.AMockEntity;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.util.Date;

/**
 * @author gu.fei
 * @version 2016-2-17 14:07
 */
@Entity
@Table(name = "CMS_DB_COLLECT_DATA")
public class DBCollectDataEO extends AMockEntity {
    /**
     *
     */
    private static final long serialVersionUID = -1300742296285581640L;

    public enum TypeCode{
        articleNews,//文章新闻
        pictureNews,//图片新闻
        videoNews,//视频新闻
        cmsLinks,//链接
        ordinaryPage,//普通页面
        fileDownload,//文件下载
        survey, // 投票调查
        reviewInfo,// 网上评议
        leaderInfo,//领导之窗
        interviewInfo,//在线调查
        collectInfo,//民意征集
        onlinePetition,//网上信访
        workGuide,//网上办事
        publicInfo,//信息公开
        guestBook//留言
    }

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "ID")
    private Long id;

    @Column(name = "TASK_ID")
    private Long taskId;

    // 标题
    @Column(name = "TITLE")
    private String title;
    // 栏目Id
    @Column(name = "COLUMN_ID")
    private Long columnId;
    // 站点Id
    @Column(name = "SITE_ID")
    private Long siteId;
    // 文章类型（1.转向链接、2.文章页、3.图片新闻）
    @Column(name = "TYPE_CODE")
    private String typeCode;
    // 标题颜色
    @Column(name = "TITLE_COLOR")
    private String titleColor;
    // 副标题
    @Column(name = "SUB_TITLE")
    private String subTitle;
    // 是否加粗
    @Column(name = "IS_BOLD")
    private Integer isBold=0;
    //是否下划线
    @Column(name="IS_UNDERLINE")
    private Integer isUnderline=0;
    //是否倾斜
    @Column(name="IS_TILT")
    private Integer isTilt=0;
    //标新
    @Column(name="IS_NEW")
    private Integer isNew=0;
    // 排序，默认为Id值
    @Column(name = "NUM")
    private Long num;
    // 来源
    @Column(name = "RESOURCES")
    private String resources;
    // 加热
    @Column(name = "IS_HOT")
    private Integer isHot = 0;
    // 置顶
    @Column(name = "IS_TOP")
    private Integer isTop = 0;
    // 置顶有效期
    @Column(name = "TOP_VALID_DATE")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+08:00")
    private Date topValidDate;
    // 链接地址
    @Column(name = "REDIRECT_LINK")
    private String redirectLink;
    // 缩略图
    @Column(name = "IMAGE_LINK")
    private String imageLink;
    // 标题新闻
    @Column(name = "IS_TITLE")
    private Integer isTitle = 0;
    // 发布状态
    @Column(name = "IS_PUBLISH")
    private Integer isPublish = 0;
    // 发布时间
    @Column(name = "PUBLISH_DATE")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+08:00")
    private Date publishDate;
    // 点击量
    @Column(name = "HIT")
    private Long hit = 0L;
    // 作者
    @Column(name = "AUTHOR")
    private String author;
    // 编辑人
    @Column(name = "EDITOR")
    private String editor;
    // 内容地址
    @Column(name = "CONTENT_PATH")
    private String contentPath;
    //备注或摘要
    @Column(name="REMARKS")
    private String remarks;
    //引用状态，2：复制引用 1;单纯引用  0：未引用 默认0
    @Column(name="QUOTE_STATUS")
    private Integer quoteStatus=0;

    @Column(name="IS_ALLOW_COMMENTS")
    private Integer isAllowComments=0;

    // CONTENT
    @Column(name = "CONTENT")
    private String content;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getTaskId() {
        return taskId;
    }

    public void setTaskId(Long taskId) {
        this.taskId = taskId;
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

    public String getTitleColor() {
        return titleColor;
    }

    public void setTitleColor(String titleColor) {
        this.titleColor = titleColor;
    }

    public String getSubTitle() {
        return subTitle;
    }

    public void setSubTitle(String subTitle) {
        this.subTitle = subTitle;
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

    public Integer getIsNew() {
        return isNew;
    }

    public void setIsNew(Integer isNew) {
        this.isNew = isNew;
    }

    public Long getNum() {
        return num;
    }

    public void setNum(Long num) {
        this.num = num;
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

    public String getRedirectLink() {
        return redirectLink;
    }

    public void setRedirectLink(String redirectLink) {
        this.redirectLink = redirectLink;
    }

    public String getImageLink() {
        return imageLink;
    }

    public void setImageLink(String imageLink) {
        this.imageLink = imageLink;
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

    public Long getHit() {
        return hit;
    }

    public void setHit(Long hit) {
        this.hit = hit;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getEditor() {
        return editor;
    }

    public void setEditor(String editor) {
        this.editor = editor;
    }

    public String getContentPath() {
        return contentPath;
    }

    public void setContentPath(String contentPath) {
        this.contentPath = contentPath;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    public Integer getQuoteStatus() {
        return quoteStatus;
    }

    public void setQuoteStatus(Integer quoteStatus) {
        this.quoteStatus = quoteStatus;
    }

    public Integer getIsAllowComments() {
        return isAllowComments;
    }

    public void setIsAllowComments(Integer isAllowComments) {
        this.isAllowComments = isAllowComments;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}