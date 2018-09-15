package cn.lonsun.net.service.entity;

import cn.lonsun.core.base.entity.AMockEntity;
import com.alibaba.fastjson.annotation.JSONField;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * Created by huangxx on 2017/2/24.
 */
@Entity
@Table(name = "CMS_NET_OFFICE_PUBLICITY")
public class CmsOfficePublicityEO extends AMockEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    public enum OfficeStatus {
        Finished, Unfinished
    }

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "ID")
    private Long id;

    @Column(name = "COLUMN_ID")
    private Long columnId;

    @Column(name = "CONTENT_ID")
    private Long contentId;

    @Column(name = "SITE_ID")
    private Long siteId;

    /*@Column(name = "ORDER_NUMS")
    private Long orderNums;//序号*/

    //受理事项
    @Column(name = "ACCEPTANCE_ITEM")
    private String acceptanceItem;

    //受理部门
    @Column(name = "ACCEPTANCE_DEPARTMENT")
    private String acceptanceDepartment;

    //受理部门id
    @Column(name = "ACCEPTANCE_DEPARTMENT_ID")
    private Long acceptanceDepartmentId;

    //申报人
    @Column(name = "DECLARE_PERSON")
    private String declarePerson;

    //申报人id
    @Column(name = "DECLARE_PERSON_ID")
    private Long declarePersonId;

    //申报日期
    @Column(name = "DECLARE_DATE")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+08:00")
    @JSONField(format = "yyyy-MM-dd HH:mm:ss")
    private Date declareDate;

    //应结办日期
    @Column(name = "SHOULD_FINISH_DATE")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+08:00")
    @JSONField(format = "yyyy-MM-dd HH:mm:ss")
    private Date shouldFinishDate;

    //点击数
    @Column(name = "CLICK_NUMBER")
    private Long clickNumber = 0L;

    //发稿人
    @Column(name = "SEND_PERSON")
    private String sendPerson;

    //发稿人id
    @Column(name = "SEND_PERSON_ID")
    private Long sendPersonId;

    //录入日期
    @Column(name = "INPUT_DATE")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+08:00")
    @JSONField(format = "yyyy-MM-dd HH:mm:ss")
    private Date inputDate;

    //办件状态
    @Column(name = "OFFICE_STATUS")
    private String officeStatus = OfficeStatus.Unfinished.toString();

    //审核状态
    @Column(name = "CHECK_IN")
    private Integer checkIn;

    @Column(name = "TYPE_CODE")
    private String typeCode;

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

    public Long getContentId() {
        return contentId;
    }

    public void setContentId(Long contentId) {
        this.contentId = contentId;
    }

    public Long getSiteId() {
        return siteId;
    }

    public void setSiteId(Long siteId) {
        this.siteId = siteId;
    }

    public String getAcceptanceItem() {
        return acceptanceItem;
    }

    public void setAcceptanceItem(String acceptanceItem) {
        this.acceptanceItem = acceptanceItem;
    }

    public String getAcceptanceDepartment() {
        return acceptanceDepartment;
    }

    public void setAcceptanceDepartment(String acceptanceDepartment) {
        this.acceptanceDepartment = acceptanceDepartment;
    }

    public Long getAcceptanceDepartmentId() {
        return acceptanceDepartmentId;
    }

    public void setAcceptanceDepartmentId(Long acceptanceDepartmentId) {
        this.acceptanceDepartmentId = acceptanceDepartmentId;
    }

    public String getDeclarePerson() {
        return declarePerson;
    }

    public void setDeclarePerson(String declarePerson) {
        this.declarePerson = declarePerson;
    }

    public Long getDeclarePersonId() {
        return declarePersonId;
    }

    public void setDeclarePersonId(Long declarePersonId) {
        this.declarePersonId = declarePersonId;
    }

    public Date getDeclareDate() {
        return declareDate;
    }

    public void setDeclareDate(Date declareDate) {
        this.declareDate = declareDate;
    }

    public Date getShouldFinishDate() {
        return shouldFinishDate;
    }

    public void setShouldFinishDate(Date shouldFinishDate) {
        this.shouldFinishDate = shouldFinishDate;
    }

    public Long getClickNumber() {
        return clickNumber;
    }

    public void setClickNumber(Long clickNumber) {
        this.clickNumber = clickNumber;
    }

    public String getSendPerson() {
        return sendPerson;
    }

    public void setSendPerson(String sendPerson) {
        this.sendPerson = sendPerson;
    }

    public Long getSendPersonId() {
        return sendPersonId;
    }

    public void setSendPersonId(Long sendPersonId) {
        this.sendPersonId = sendPersonId;
    }

    public Date getInputDate() {
        return inputDate;
    }

    public void setInputDate(Date inputDate) {
        this.inputDate = inputDate;
    }

    public String getOfficeStatus() {
        return officeStatus;
    }

    public void setOfficeStatus(String officeStatus) {
        this.officeStatus = officeStatus;
    }

    public String getTypeCode() {
        return typeCode;
    }

    public void setTypeCode(String typeCode) {
        this.typeCode = typeCode;
    }

    public Integer getCheckIn() {
        return checkIn;
    }

    public void setCheckIn(Integer checkIn) {
        this.checkIn = checkIn;
    }
}
