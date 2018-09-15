package cn.lonsun.process.vo;

import com.fasterxml.jackson.annotation.JsonFormat;

import javax.persistence.Column;
import java.util.Date;

/**
 * Created by zhu124866 on 2015-12-24.
 */
public class FormHandleRecordListVO {

    /** 流程表单 */
    private Long processFormId;

    /** 标题 */
    private String title;

    /** 发起人 */
    private String createPersonName;

    /** 流程实例ID */
    private Long procInstId;

    /** 当前活动实例ID */
    private Long curActinstId;

    /** 当前活动名称 */
    private String curActivityName;

    /** 创建日期 */
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss",timezone="GMT+08:00") //用户Date类型转换Json字符类型的格式化
    private Date createDate;

    /** 栏目ID */
    private Long columnId;

    /** 栏目名称 */
    private String columnName;


    public Long getProcessFormId() {
        return processFormId;
    }

    public void setProcessFormId(Long processFormId) {
        this.processFormId = processFormId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getCreatePersonName() {
        return createPersonName;
    }

    public void setCreatePersonName(String createPersonName) {
        this.createPersonName = createPersonName;
    }

    public Long getProcInstId() {
        return procInstId;
    }

    public void setProcInstId(Long procInstId) {
        this.procInstId = procInstId;
    }

    public Long getCurActinstId() {
        return curActinstId;
    }

    public void setCurActinstId(Long curActinstId) {
        this.curActinstId = curActinstId;
    }

    public String getCurActivityName() {
        return curActivityName;
    }

    public void setCurActivityName(String curActivityName) {
        this.curActivityName = curActivityName;
    }

    public Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    public Long getColumnId() {
        return columnId;
    }

    public void setColumnId(Long columnId) {
        this.columnId = columnId;
    }

    public String getColumnName() {
        return columnName;
    }

    public void setColumnName(String columnName) {
        this.columnName = columnName;
    }
}
