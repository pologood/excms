package cn.lonsun.content.internal.entity;

import cn.lonsun.core.base.entity.AMockEntity;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.util.Date;

/**
 * @author Hewbing
 * @ClassName: ContentEO
 * @Description: 新闻信息类
 * @date 2015年9月7日 上午11:19:10
 */
@Entity
@Table(name = "CMS_BASE_CONTENT")
public class BaseContentEO extends AMockEntity {

    /**
     *
     */
    private static final long serialVersionUID = -1300742296285581640L;

    public enum TypeCode {
        articleNews, // 文章新闻
        pictureNews, // 图片新闻
        videoNews, // 视频新闻
        linksMgr, // 链接
        ordinaryPage, // 普通页面
        fileDownload, // 文件下载
        survey, // 投票调查
        reviewInfo, // 网上评议
        leaderInfo, // 领导之窗
        interviewInfo, // 在线访谈
        collectInfo, // 民意征集
        onlinePetition, // 网上信访
        workGuide, // 网上办事
        sceneService, // 场景式服务
        public_content, // 信息公开
        guestBook, // 留言
        messageBoard, // 多回復留言
        bbs, // 政务论坛
        onlineDeclaration, // 在线申请
        commonProblem, // 常见问题
        relatedRule, // 相关资源
        journal,//电子报刊
        securityMateria,//安全生产素材
        handleItems, //办事办件事项
        readilyTake,//随手拍
        specialNews,//专题
        officePublicity,//办事结果公示
        accountPublic,//预决算公开
        knowledgeBase,//问答知识库
        dataOpen //数据开放
    }

    public enum MemConStu {
        noPut(0),//未提交
        isPut(1),//已提交
        isBack(2),//退回
        isUse(3);//采用
        private Integer sta;

        private MemConStu(Integer sta) {
            this.sta = sta;
        }

        public Integer getMemConStu() {
            return sta;
        }
    }

    public static Integer STATUS_NOSTART = -1;// 未开始

    public static Integer STATUS_HANDLING = 0;// 办理中

    public static Integer STATUS_STOP = 1;// 终止

    public static Integer STATUS_BANJIE = 2;// 已办结

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    // @GeneratedValue(generator ="tableGenerator")
    // @GenericGenerator(name ="tableGenerator", strategy ="hilo",parameters =
    // {@Parameter(name = "max_lo", value =
    // HiloConfig.hibernatePrimarykeyMaxLo)})
    @Column(name = "ID")
    private Long id;
    // 标题
    @Column(name = "title")
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
    private Integer isBold = 0;
    // 是否下划线
    @Column(name = "IS_UNDERLINE")
    private Integer isUnderline = 0;
    // 是否倾斜
    @Column(name = "IS_TILT")
    private Integer isTilt = 0;
    // 标新
    @Column(name = "IS_NEW")
    private Integer isNew = 0;
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
    // 责任编辑
    @Column(name = "RESPONSIBILITY_EDITOR")
    private String responsibilityEditor;
    // 内容地址
    @Column(name = "CONTENT_PATH")
    private String contentPath;
    // 备注或摘要
    @Column(name = "REMARKS")
    private String remarks;
    // 引用状态，2：复制引用 1;单纯引用 0：未引用 默认0
    @Column(name = "QUOTE_STATUS")
    private Integer quoteStatus = 0;

    @Column(name = "IS_ALLOW_COMMENTS")
    private Integer isAllowComments = 0;

    // 工作流状态 0-未开始 1-办理中 2-已办结 3-已终止
    @Column(name = "WORK_FLOW_STATUS")
    private Integer workFlowStatus = STATUS_NOSTART;

    // 是否定时发布
    @Column(name = "IS_JOB")
    private Integer isJob = 0;

    // 是否定时时间
    @Column(name = "JOB_ISSUE_DATE")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm", timezone = "GMT+08:00")
    private Date jobIssueDate;

    @Column(name = "UNIT_ID")
    private Long unitId;

    @Column(name = "ATTACH_SAVED_NAME")
    private String attachSavedName;// 附件保存名
    @Column(name = "ATTACH_REAL_NAME")
    private String attachRealName;// 附件真实名
    @Column(name = "ATTACH_SIZE")
    private Long attachSize;// 附件大小

    @Column(name = "HANDLE_STATUS")
    private Integer handleStatus = 0;// 附件大小

    @Column(name = "VIDEO_STATUS")
    private Integer videoStatus = 100;//视频转换状态，默认100代表已经转换好，或者不存在视频信息，0为未转换

    @Column(name = "old_schema_id")
    private String oldSchemaId;

    @Column(name = "is_tofile")
    private Integer isToFile = 0;//是否归档
    @Column(name = "to_filedate")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date toFileDate;//归档时间
    @Column(name = "to_fileid")
    private String toFileId;
    @Column(name = "MEMBER")//会员名
    private String member;
    @Column(name = "MEMBER_CONTENT_STATUS")//会员稿件状态
    private Integer memberConStu = MemConStu.noPut.getMemConStu();
    @Column(name = "BACK_REASON")//会员稿件退回原因
    private String backReason;
    @Column(name = "MEMBER_PUT_DATE")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+08:00")
    private Date memPutDate;
    @Column(name = "SHOW_START_TIME")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+08:00")
    private Date showStartTime;// 广告位显示开始时间
    @Column(name = "SHOW_END_TIME")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+08:00")
    private Date showEndTime;// 广告位显示结束时间



    @Transient
    private String article;
    @Transient
    private String contentName;
    @Transient
    private String link;// 生成静态时使用
    @Transient
    private String columnName;// 待审内容获取栏目名使用
    @Transient
    private String uri;// 手机端使用
    @Transient
    private Long organId;// 单位热度排行
    @Transient
    private String organName;// 单位热度排行
    @Transient
    private Long infoCount;// 信息条数排行
    @Transient
    private boolean referedNews = false; //是否被引用新闻
    @Transient
    private boolean referNews = false; //是否引用新闻

    public String getMember() {
        return member;
    }

    public void setMember(String member) {
        this.member = member;
    }

    public Integer getMemberConStu() {
        return memberConStu;
    }

    public void setMemberConStu(Integer memberConStu) {
        this.memberConStu = memberConStu;
    }

    public String getBackReason() {
        return backReason;
    }

    public void setBackReason(String backReason) {
        this.backReason = backReason;
    }

    public Date getMemPutDate() {
        return memPutDate;
    }

    public void setMemPutDate(Date memPutDate) {
        this.memPutDate = memPutDate;
    }

    public Integer getIsToFile() {
        return isToFile;
    }

    public void setIsToFile(Integer isToFile) {
        this.isToFile = isToFile;
    }

    public Date getToFileDate() {
        return toFileDate;
    }

    public void setToFileDate(Date toFileDate) {
        this.toFileDate = toFileDate;
    }

    public String getToFileId() {
        return toFileId;
    }

    public void setToFileId(String toFileId) {
        this.toFileId = toFileId;
    }

    public String getColumnName() {
        return columnName;
    }

    public void setColumnName(String columnName) {
        this.columnName = columnName;
    }

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

    public String getArticle() {
        return article;
    }

    public void setArticle(String article) {
        this.article = article;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
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

    public String getContentName() {
        return contentName;
    }

    public void setContentName(String contentName) {
        this.contentName = contentName;
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public String getOrganName() {
        return organName;
    }

    public void setOrganName(String organName) {
        this.organName = organName;
    }

    public Long getInfoCount() {
        return infoCount;
    }

    public void setInfoCount(Long infoCount) {
        this.infoCount = infoCount;
    }

    @Override
    public int hashCode() {
        return getId().hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            // 如果引用地址相同，即引用的是同一个对象，就返回true
            return true;
        }

        // 如果obj不是BaseContentEO类的实例，返回false
        if (!(obj instanceof BaseContentEO)) {
            return false;
        }

        BaseContentEO baseContentEO = (BaseContentEO) obj;
        return this.getId().equals(baseContentEO.getId());
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

    public Integer getWorkFlowStatus() {
        return workFlowStatus;
    }

    public void setWorkFlowStatus(Integer workFlowStatus) {
        this.workFlowStatus = workFlowStatus;
    }

    public Long getOrganId() {
        return organId;
    }

    public void setOrganId(Long organId) {
        this.organId = organId;
    }

    public Long getUnitId() {
        return unitId;
    }

    public void setUnitId(Long unitId) {
        this.unitId = unitId;
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

    public Integer getHandleStatus() {
        return handleStatus;
    }

    public void setHandleStatus(Integer handleStatus) {
        this.handleStatus = handleStatus;
    }

    public String getResponsibilityEditor() {
        return responsibilityEditor;
    }

    public void setResponsibilityEditor(String responsibilityEditor) {
        this.responsibilityEditor = responsibilityEditor;
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

    public Date getShowStartTime() {
        return showStartTime;
    }

    public void setShowStartTime(Date showStartTime) {
        this.showStartTime = showStartTime;
    }

    public Date getShowEndTime() {
        return showEndTime;
    }

    public void setShowEndTime(Date showEndTime) {
        this.showEndTime = showEndTime;
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