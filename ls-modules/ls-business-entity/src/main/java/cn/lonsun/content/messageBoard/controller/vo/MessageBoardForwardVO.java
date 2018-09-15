package cn.lonsun.content.messageBoard.controller.vo;

import cn.lonsun.core.base.entity.AMockEntity;
import com.alibaba.fastjson.annotation.JSONField;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.util.Date;

/**
 * 留言管理转办记录
 */
public class MessageBoardForwardVO{

    /**
     * 正常状态：Normal,已删除:Removed
     *
     * @author xujh
     *
     */
    public enum RecordStatus {
        Normal, Removed;
    }

    //操作状态，goBack 退回，Recover 收回，Normal 正常
    public enum OperationStatus {
        Normal, Recover,Back;
    }

    private Long id;

    //留言表ID
    private Long messageBoardId;

    //接受组织单位ID
    private Long receiveOrganId;

    //转办人ip
    private String ip;

    //转办人名称
    private String username;

    //备注
    private String remarks;

    //接受单位名称
    private String receiveUnitName;

    //接受人名称
    private String receiveUserName;

    //最后办理日
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+08:00")
    @JSONField(format = "yyyy-MM-dd HH:mm:ss")
    private Date endReplyDate;


    //接受人编码
    private String receiveUserCode;

    //附件id
    private String attachId;

    //附件名
    private String attachName;

    //操作状态，记录退回，收回，正常的状态
    private  String operationStatus;

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

    private  Integer defaultDays;

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

    public Long getReceiveOrganId() {
        return receiveOrganId;
    }

    public void setReceiveOrganId(Long receiveOrganId) {
        this.receiveOrganId = receiveOrganId;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public Integer getDefaultDays() {
        return defaultDays;
    }

    public void setDefaultDays(Integer defaultDays) {
        this.defaultDays = defaultDays;
    }

    public Date getEndReplyDate() {
        return endReplyDate;
    }

    public void setEndReplyDate(Date endReplyDate) {
        this.endReplyDate = endReplyDate;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    public String getReceiveUnitName() {
        return receiveUnitName;
    }

    public void setReceiveUnitName(String receiveUnitName) {
        this.receiveUnitName = receiveUnitName;
    }

    public String getReceiveUserName() {
        return receiveUserName;
    }

    public void setReceiveUserName(String receiveUserName) {
        this.receiveUserName = receiveUserName;
    }

    public String getReceiveUserCode() {
        return receiveUserCode;
    }

    public void setReceiveUserCode(String receiveUserCode) {
        this.receiveUserCode = receiveUserCode;
    }

    public String getAttachId() {
        return attachId;
    }

    public void setAttachId(String attachId) {
        this.attachId = attachId;
    }

    public String getAttachName() {
        return attachName;
    }

    public void setAttachName(String attachName) {
        this.attachName = attachName;
    }

    public String getRecordStatus() {
        return recordStatus;
    }

    public void setRecordStatus(String recordStatus) {
        this.recordStatus = recordStatus;
    }

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

    public Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    public Long getUpdateUserId() {
        return updateUserId;
    }

    public void setUpdateUserId(Long updateUserId) {
        this.updateUserId = updateUserId;
    }

    public Date getUpdateDate() {
        return updateDate;
    }

    public void setUpdateDate(Date updateDate) {
        this.updateDate = updateDate;
    }

    public String getOperationStatus() {
        return operationStatus;
    }

    public void setOperationStatus(String operationStatus) {
        this.operationStatus = operationStatus;
    }
}
