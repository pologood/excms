package cn.lonsun.datacollect.entity;

import cn.lonsun.core.base.entity.AMockEntity;

import javax.persistence.*;

/**
 * @author gu.fei
 * @version 2016-2-17 14:07
 */
@Entity
@Table(name = "CMS_DB_COLLECT_COLUMN")
public class DBCollectColumnEO extends AMockEntity {

    @Id
    @Column(name = "ID")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(name = "TASK_ID")
    private Long taskId;

    @Column(name = "FROM_COLUMN")
    private String fromColumn;

    @Column(name = "TO_COLUMN")
    private String toColumn;

    @Column(name = "FILTER_CONDITION")
    private String filterCondition;

    @Column(name = "DEFAULT_VALUE")
    private String defaultValue;

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

    public String getFromColumn() {
        return fromColumn;
    }

    public void setFromColumn(String fromColumn) {
        this.fromColumn = fromColumn;
    }

    public String getToColumn() {
        return toColumn;
    }

    public void setToColumn(String toColumn) {
        this.toColumn = toColumn;
    }

    public String getFilterCondition() {
        return filterCondition;
    }

    public void setFilterCondition(String filterCondition) {
        this.filterCondition = filterCondition;
    }

    public String getDefaultValue() {
        return defaultValue;
    }

    public void setDefaultValue(String defaultValue) {
        this.defaultValue = defaultValue;
    }
}