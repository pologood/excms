package cn.lonsun.statictask.internal.entity;

import cn.lonsun.core.base.entity.AMockEntity;

import javax.persistence.*;

/**
 * 生成静态任务失败具体信息<br/>
 * 
 * @author wangss <br/>
 * @version v1.0 <br/>
 * @date 2016-3-3<br/>
 */
@Entity
@Table(name = "cms_task_info")
public class TaskInfoEO extends AMockEntity {
    /**
     * serialVersionUID
     */
    private static final long serialVersionUID = -2964053661110225063L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    private Long id;

    @Column(name = "column_id")
    private Long columnId;

    @Column(name = "article_id")
    private Long articleId;

    @Column(name = "task_id")
    private Long taskId;

    @Column(name = "link")
    private String link;

    @Column(name = "title")
    private String title;

    @Column(name = "log")
    private String log;
    @Column(name = "detail")
    private String detail;// 详细堆栈信息

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

    public Long getArticleId() {
        return articleId;
    }

    public void setArticleId(Long articleId) {
        this.articleId = articleId;
    }

    public Long getTaskId() {
        return taskId;
    }

    public void setTaskId(Long taskId) {
        this.taskId = taskId;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getLog() {
        return log;
    }

    public void setLog(String log) {
        this.log = log;
    }

    public String getDetail() {
        return detail;
    }

    public void setDetail(String detail) {
        this.detail = detail;
    }
}