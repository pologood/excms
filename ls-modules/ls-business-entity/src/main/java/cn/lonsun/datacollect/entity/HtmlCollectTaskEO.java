package cn.lonsun.datacollect.entity;

import cn.lonsun.core.base.entity.AMockEntity;
import com.alibaba.fastjson.annotation.JSONField;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * @author gu.fei
 * @version 2015-5-14 8:23
 */
@Entity
@Table(name="CMS_HTML_COLLECT_TASK")
public class HtmlCollectTaskEO extends AMockEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    public enum pageType {
        number, //数字分页
        character,//字符分页
        auto //自定义分页
    }

    public enum employType {
        Manual, //手动
        Auto //自动
    }

    @Id
    @Column(name = "ID")
    @GeneratedValue(strategy = GenerationType.AUTO)
//    @GeneratedValue(generator = "tableGenerator")
//    @GenericGenerator(name = "tableGenerator", strategy="cn.lonsun.key.SequenceGenerator")
    private Long id;

    @Column(name = "COLUMN_ID")
    private Long columnId;

    @Column(name = "CSITE_ID")
    private Long cSiteId;

    @Transient
    private String columnName;

    @Column(name = "TASK_NAME")
    private String taskName; //任务名称

    @Column(name = "WEB_DOMAIN")
    private String webDomain; //采集网站域名

    @Column(name = "DEFAULT_URL")
    private String defaultUrl;//主页地址

    @Column(name = "REGEX_URL")
    private String regexUrl; //采集url规则

    @Column(name = "TARGET_BEGIN_DOM")
    private String targetBeginDom;

    @Column(name = "TARGET_END_DOM")
    private String targetEndDom;

    @Column(name = "PAGE_TYPE")
    private String pageType; //分页方式

    @Column(name = "PAGE_BEGIN_NUMBER")
    private Integer pageBeginNumber = 0; //采集开始页

    @Column(name = "PAGE_END_NUMBER")
    private Integer pageEndNumber = 0; //采集结束页

    @Column(name = "ZERO_FILL")
    private String zeroFill = "false"; //采集结束页

    @Column(name = "PAGE_BEGIN_CHAR")
    private String pageBeginChar; //采集开始字符

    @Column(name = "PAGE_END_CHAR")
    private String pageEndChar; //采集结束字符

    @Column(name = "PAGE_LIST")
    private String pageList; //自定义页面列表

    @Column(name = "PAGE_SORT")
    private String pageSort = "ASC"; //采集排序 默认正序

    @Column(name = "REGEX_HREF")
    private String regexHref; //采集href规则

    @Column(name="FILTER_TAG")
    private String filterTag; //过滤标签

    @Column(name="FILTER_REGEX_URL")
    private String filterRegexUrl; //过滤规则URL

    @Column(name = "CRON")
    private String cron; //定时表达式

    @Column(name = "COLLECT_DATE")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+08:00")
    @JSONField(format = "yyyy-MM-dd HH:mm:ss")
    private Date collectDate; //采集时间

    @Column(name = "SITE_ID")
    private Long siteId; //所属站点ID

    @Column(name = "THREADS")
    private Integer threads = 10; //线程数

    //定时任务配置参数
    @Column(name = "SCHED_ID")
    private Long schedId;

    @Column(name = "CRON_ID")
    private Long cronId;

    @Column(name = "CRON_DESC")
    private String cronDesc;

    @Column(name = "RUN_STATUS")
    private Integer runStatus = 0; //0:未执行 1:执行中

    @Column(name = "PREV_RUN_DATE")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+08:00")
    @JSONField(format = "yyyy-MM-dd HH:mm:ss")
    private Date prevRunDate; //上次采集时间

    @Column(name = "NEXT_RUN_DATE")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+08:00")
    @JSONField(format = "yyyy-MM-dd HH:mm:ss")
    private Date nextRunDate; //上次采集时间

    @Column(name = "EMPLOY_TYPE")
    private String employType = "Manual";

    @Column(name = "IS_PUSH")
    private Integer isPush = 0; //0:不发布推送 1:推送

    //推送链接标识
    @Column(name = "PUSH_URL_FLAG")
    private String pushUrlFlag;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getColumnId() {
        return columnId;
    }

    public void setColumnId(Long columnId) {
        this.columnId = columnId;
    }

    public Long getcSiteId() {
        return cSiteId;
    }

    public void setcSiteId(Long cSiteId) {
        this.cSiteId = cSiteId;
    }

    public String getColumnName() {
        return columnName;
    }

    public void setColumnName(String columnName) {
        this.columnName = columnName;
    }

    public String getTaskName() {
        return taskName;
    }

    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }

    public String getWebDomain() {
        return webDomain;
    }

    public void setWebDomain(String webDomain) {
        this.webDomain = webDomain;
    }

    public String getDefaultUrl() {
        return defaultUrl;
    }

    public void setDefaultUrl(String defaultUrl) {
        this.defaultUrl = defaultUrl;
    }

    public String getRegexUrl() {
        return regexUrl;
    }

    public void setRegexUrl(String regexUrl) {
        this.regexUrl = regexUrl;
    }

    public String getTargetBeginDom() {
        return targetBeginDom;
    }

    public void setTargetBeginDom(String targetBeginDom) {
        this.targetBeginDom = targetBeginDom;
    }

    public String getTargetEndDom() {
        return targetEndDom;
    }

    public void setTargetEndDom(String targetEndDom) {
        this.targetEndDom = targetEndDom;
    }

    public String getPageType() {
        return pageType;
    }

    public void setPageType(String pageType) {
        this.pageType = pageType;
    }

    public Integer getPageBeginNumber() {
        return pageBeginNumber;
    }

    public void setPageBeginNumber(Integer pageBeginNumber) {
        this.pageBeginNumber = pageBeginNumber;
    }

    public Integer getPageEndNumber() {
        return pageEndNumber;
    }

    public void setPageEndNumber(Integer pageEndNumber) {
        this.pageEndNumber = pageEndNumber;
    }

    public String getZeroFill() {
        return zeroFill;
    }

    public void setZeroFill(String zeroFill) {
        this.zeroFill = zeroFill;
    }

    public String getPageBeginChar() {
        return pageBeginChar;
    }

    public void setPageBeginChar(String pageBeginChar) {
        this.pageBeginChar = pageBeginChar;
    }

    public String getPageEndChar() {
        return pageEndChar;
    }

    public void setPageEndChar(String pageEndChar) {
        this.pageEndChar = pageEndChar;
    }

    public String getPageList() {
        return pageList;
    }

    public void setPageList(String pageList) {
        this.pageList = pageList;
    }

    public String getPageSort() {
        return pageSort;
    }

    public void setPageSort(String pageSort) {
        this.pageSort = pageSort;
    }

    public String getRegexHref() {
        return regexHref;
    }

    public void setRegexHref(String regexHref) {
        this.regexHref = regexHref;
    }

    public String getFilterTag() {
        return filterTag;
    }

    public void setFilterTag(String filterTag) {
        this.filterTag = filterTag;
    }

    public String getFilterRegexUrl() {
        return filterRegexUrl;
    }

    public void setFilterRegexUrl(String filterRegexUrl) {
        this.filterRegexUrl = filterRegexUrl;
    }

    public String getCron() {
        return cron;
    }

    public void setCron(String cron) {
        this.cron = cron;
    }

    public Date getCollectDate() {
        return collectDate;
    }

    public void setCollectDate(Date collectDate) {
        this.collectDate = collectDate;
    }

    public Long getSiteId() {
        return siteId;
    }

    public void setSiteId(Long siteId) {
        this.siteId = siteId;
    }

    public Integer getThreads() {
        return threads;
    }

    public void setThreads(Integer threads) {
        this.threads = threads;
    }

    public Long getSchedId() {
        return schedId;
    }

    public void setSchedId(Long schedId) {
        this.schedId = schedId;
    }

    public Long getCronId() {
        return cronId;
    }

    public void setCronId(Long cronId) {
        this.cronId = cronId;
    }

    public String getCronDesc() {
        return cronDesc;
    }

    public void setCronDesc(String cronDesc) {
        this.cronDesc = cronDesc;
    }

    public Integer getRunStatus() {
        return runStatus;
    }

    public void setRunStatus(Integer runStatus) {
        this.runStatus = runStatus;
    }

    public Date getPrevRunDate() {
        return prevRunDate;
    }

    public void setPrevRunDate(Date prevRunDate) {
        this.prevRunDate = prevRunDate;
    }

    public Date getNextRunDate() {
        return nextRunDate;
    }

    public void setNextRunDate(Date nextRunDate) {
        this.nextRunDate = nextRunDate;
    }

    public String getEmployType() {
        return employType;
    }

    public void setEmployType(String employType) {
        this.employType = employType;
    }

    public Integer getIsPush() {
        return isPush;
    }

    public void setIsPush(Integer isPush) {
        this.isPush = isPush;
    }

    public String getPushUrlFlag() {
        return pushUrlFlag;
    }

    public void setPushUrlFlag(String pushUrlFlag) {
        this.pushUrlFlag = pushUrlFlag;
    }
}