package cn.lonsun.content.messageBoard.controller.vo;

import cn.lonsun.common.vo.PageQueryVO;
import cn.lonsun.core.base.entity.AMockEntity;
import com.alibaba.fastjson.annotation.JSONField;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

/**
 * 申请延期记录
 */
public class MessageBoardApplyVO extends PageQueryVO {

    private Long id;

    //留言表ID
    private Long messageBoardId;

    //申请人ip
    private String ip;

    //申请人名称
    private String applyName;

    //申请天数
    private Integer applyDays;

    //申请原因
    private String applyReason;

    //申请单位名称
    private String applyOrganName;

    private String title;

    private  String columnName;

    private Long columnId;

    private Long siteId;

    private Long baseContentId;

    public Long getBaseContentId() {
        return baseContentId;
    }

    public void setBaseContentId(Long baseContentId) {
        this.baseContentId = baseContentId;
    }

    public Long getSiteId() {
        return siteId;
    }

    public void setSiteId(Long siteId) {
        this.siteId = siteId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getColumnName() {
        return columnName;
    }

    public void setColumnName(String columnName) {
        this.columnName = columnName;
    }

    public Long getColumnId() {
        return columnId;
    }

    public void setColumnId(Long columnId) {
        this.columnId = columnId;
    }

    /**
     * 正常状态：Normal,已删除:Removed
     *
     * @author xujh
     *
     */
    public enum RecordStatus {
        Normal, Removed;
    }

    /**
     * 记录状态：正常：Normal,已删除:Removed
     * */
    private String recordStatus = AMockEntity.RecordStatus.Normal.toString();

    /** 创建人ID */
    private Long createUserId;
    /**
     * 创建组织
     * */
    private Long createOrganId;

    /**
     * 创建时间 用户前端日期类型字符串自动转换 用户Date类型转换Json字符类型的格式化.
     * 注：数据库如果为TimeStamp类型存储的时间，在json输出的时候默认返回格林威治时间，需要增加timezone属性
     * */
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+08:00")
    @JSONField(format = "yyyy-MM-dd HH:mm:ss")
    private Date createDate;

    /**
     * 更新人ID
     * */
    private Long updateUserId;

    /**
     * 更新时间 用户前端日期类型字符串自动转换 用户Date类型转换Json字符类型的格式化.
     * 注：数据库如果为TimeStamp类型存储的时间，在json输出的时候默认返回格林威治时间，需要增加timezone属性
     * */
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+08:00")
    @JSONField(format = "yyyy-MM-dd HH:mm:ss")
    private Date updateDate;//

    //审核状态：disposeWait,待审核:disposeNotPass,审核不通过，disposePass审核通过
    private String disposeStatus;

    //处理人IP
    private String disposeIp;

    //处理结果原因
    private String disposeReason;

    public Long getCreateUserId() {
        return createUserId;
    }

    public void setCreateUserId(Long createUserId) {
        this.createUserId = createUserId;
    }

    public Long getCreateOrganId() {
        return createOrganId;
    }

    public void setCreateOrganId(Long createOrganId) {
        this.createOrganId = createOrganId;
    }

    public Long getUpdateUserId() {
        return updateUserId;
    }

    public void setUpdateUserId(Long updateUserId) {
        this.updateUserId = updateUserId;
    }

    public Integer getApplyDays() {
        return applyDays;
    }

    public void setApplyDays(Integer applyDays) {
        this.applyDays = applyDays;
    }

    public Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    public Date getUpdateDate() {
        return updateDate;
    }

    public void setUpdateDate(Date updateDate) {
        this.updateDate = updateDate;
    }

    public String getRecordStatus() {
        return recordStatus;
    }

    public void setRecordStatus(String recordStatus) {
        this.recordStatus = recordStatus;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getMessageBoardId() {
        return messageBoardId;
    }

    public void setMessageBoardId(Long messageBoardId) {
        this.messageBoardId = messageBoardId;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getApplyName() {
        return applyName;
    }

    public void setApplyName(String applyName) {
        this.applyName = applyName;
    }

    public String getApplyReason() {
        return applyReason;
    }

    public void setApplyReason(String applyReason) {
        this.applyReason = applyReason;
    }

    public String getApplyOrganName() {
        return applyOrganName;
    }

    public void setApplyOrganName(String applyOrganName) {
        this.applyOrganName = applyOrganName;
    }

    public String getDisposeStatus() {
        return disposeStatus;
    }

    public void setDisposeStatus(String disposeStatus) {
        this.disposeStatus = disposeStatus;
    }

    public String getDisposeIp() {
        return disposeIp;
    }

    public void setDisposeIp(String disposeIp) {
        this.disposeIp = disposeIp;
    }

    public String getDisposeReason() {
        return disposeReason;
    }

    public void setDisposeReason(String disposeReason) {
        this.disposeReason = disposeReason;
    }
}
