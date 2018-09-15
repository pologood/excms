package cn.lonsun.datacollect.entity;

import cn.lonsun.core.base.entity.AMockEntity;

import javax.persistence.*;
import java.io.Serializable;

/**
 * @author gu.fei
 * @version 2015-5-14 8:23
 */
@Entity
@Table(name="CMS_HTML_COLLECT_DATA")
public class HtmlCollectDataEO extends AMockEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "ID")
    @GeneratedValue(strategy = GenerationType.AUTO)
//    @GeneratedValue(generator = "tableGenerator")
//    @GenericGenerator(name = "tableGenerator", strategy="cn.lonsun.core.key.SequenceGenerator")
    private Long id;

    @Column(name = "TASK_ID")
    private Long taskId; //采集任务ID

    @Column(name = "TITLE")
    private String title; //标题

    @Column(name = "AUTHOR")
    private String author; //作者

    @Column(name = "FOCUS")
    private String focus; //摘要

    @Column(name = "FROM_NAME")
    private String fromName;     //来源

    @Column(name = "PUBLISH_DATE")
    private String publishDate; //发布日期

    @Column(name = "CREATE_TIME")
    private String createTime; //创建日期

    @Column(name = "CONTENT")
    private String content; //内容

    @Column(name = "CLICKS")
    private Long clicks;//点击数

    @Column(name = "URL")
    private String url; //链接地址

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

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getFocus() {
        return focus;
    }

    public void setFocus(String focus) {
        this.focus = focus;
    }

    public String getFromName() {
        return fromName;
    }

    public void setFromName(String fromName) {
        this.fromName = fromName;
    }

    public String getPublishDate() {
        return publishDate;
    }

    public void setPublishDate(String publishDate) {
        this.publishDate = publishDate;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public Long getClicks() {
        return clicks;
    }

    public void setClicks(Long clicks) {
        this.clicks = clicks;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}