package cn.lonsun.supervise.columnupdate.internal.entity;

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
@Table(name="CMS_SUPERVISE_UNREPLY_GUEST")
public class UnreplyGuestEO extends AMockEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    public enum GuestType {
        guestBook, //留言
        messageBoard //多回复留言
    }

    @Id
    @Column(name = "ID")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id; //主键ID

    @Column(name = "COLUMN_ID")
    private Long columnId; //栏目ID

    @Column(name = "CONTENT_ID")
    private Long contentId;

    @Column(name = "GUEST_ID")
    private Long guestId; //留言ID

    @Column(name = "COLUMN_NAME")
    private String columnName; //栏目名称

    @Column(name = "TITLE")
    private String title; //留言标题

    @Column(name = "type")
    private Integer type; // 留言类型,0表示咨询，1表示投诉，2表示建议，3表示其他

    @Column(name = "PERSON_NAME")
    private String personName; //留言人名称

    @Column(name = "SITE_ID")
    private Long siteId; //站点ID

    @Column(name = "GUEST_TYPE")
    private String guestType; //留言类型

    @Column(name = "ADD_DATE")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+08:00")
    @JSONField(format = "yyyy-MM-dd HH:mm:ss")
    private Date addDate;

    //接受单位ID
    @Column(name = "RECEIVE_ID")
    private Long receiveId;

    @Column(name = "RECEIVE_NAME")
    private String receiveName;

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

    public Long getGuestId() {
        return guestId;
    }

    public void setGuestId(Long guestId) {
        this.guestId = guestId;
    }

    public String getColumnName() {
        return columnName;
    }

    public void setColumnName(String columnName) {
        this.columnName = columnName;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public String getPersonName() {
        return personName;
    }

    public void setPersonName(String personName) {
        this.personName = personName;
    }

    public Long getSiteId() {
        return siteId;
    }

    public void setSiteId(Long siteId) {
        this.siteId = siteId;
    }

    public String getGuestType() {
        return guestType;
    }

    public void setGuestType(String guestType) {
        this.guestType = guestType;
    }

    public Date getAddDate() {
        return addDate;
    }

    public void setAddDate(Date addDate) {
        this.addDate = addDate;
    }

    public Long getReceiveId() {
        return receiveId;
    }

    public void setReceiveId(Long receiveId) {
        this.receiveId = receiveId;
    }

    public String getReceiveName() {
        return receiveName;
    }

    public void setReceiveName(String receiveName) {
        this.receiveName = receiveName;
    }
}