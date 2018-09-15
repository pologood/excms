package cn.lonsun.dbimport.internal.entity;

import cn.lonsun.core.base.entity.ABaseEntity;

import javax.persistence.*;

@Entity
@Table(name = "db_import_backup")
public class DbImportBackupEO extends ABaseEntity {

    private static final long serialVersionUID = -1300742296285581640L;

    //主键ID
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name="id")
    private Long id;

    @Column(name="task_id")
    private Long taskId;

    @Column(name="data")
    private String data;

    @Column(name="type")
    private String type;

    @Column(name="memo")
    private String memo;

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

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getMemo() {
        return memo;
    }

    public void setMemo(String memo) {
        this.memo = memo;
    }
}
