package cn.lonsun.datacollect.entity;

import cn.lonsun.core.base.entity.AMockEntity;

import javax.persistence.*;
import java.io.Serializable;

/**
 * @author gu.fei
 * @version 2015-5-14 8:23
 */
@Entity
@Table(name="CMS_HTML_COLLECT_CONTENT")
public class HtmlCollectContentEO extends AMockEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "ID")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(name = "TASK_ID")
    private Long taskId;  //采集任务ID

    @Column(name = "NAME")
    private String name; //采集内容名称

    @Column(name = "COLUMN_NAME")
    private String columnName; //入库字段

    @Column(name = "REGEX_BEGIN")
    private String regexBegin; //正则规则开始字符串

    @Column(name = "REGEX_END")
    private String regexEnd; //正则规则结束字符串

    @Column(name = "REGEX_FILTER")
    private String regexFilter; //正则规则过滤

    @Column(name = "TAG_ID")
    private String tagId; //如果标签有ID则根据ID键查询

    @Column(name="TYPE")
    private String type; //搜索类型 id , regex

    @Column(name="DEFAULT_VALUE")
    private String defaultValue; //默认值 ，假如查询值为空设置的默认值

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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getColumnName() {
        return columnName;
    }

    public void setColumnName(String columnName) {
        this.columnName = columnName;
    }

    public String getRegexBegin() {
        return regexBegin;
    }

    public void setRegexBegin(String regexBegin) {
        this.regexBegin = regexBegin;
    }

    public String getRegexEnd() {
        return regexEnd;
    }

    public void setRegexEnd(String regexEnd) {
        this.regexEnd = regexEnd;
    }

    public String getRegexFilter() {
        return regexFilter;
    }

    public void setRegexFilter(String regexFilter) {
        this.regexFilter = regexFilter;
    }

    public String getTagId() {
        return tagId;
    }

    public void setTagId(String tagId) {
        this.tagId = tagId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDefaultValue() {
        return defaultValue;
    }

    public void setDefaultValue(String defaultValue) {
        this.defaultValue = defaultValue;
    }
}