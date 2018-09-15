package cn.lonsun.govbbs.internal.vo;

import com.alibaba.fastjson.annotation.JSONField;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.Column;
import java.util.Date;

/**
 * Created by lonsun on 2016-12-23.
 */
public class UnitPalteVO {
    public enum Status {
        Yes(1), // 是
        No(0);// 否
        private Integer status;
        private Status(Integer status){
            this.status=status;
        }
        public Integer getStatus(){
            return status;
        }
    }


    private String title;
    private String plateName;
    private String acceptUnitName;
    private String memberName;

    @Column(name = "CREATE_DATE", updatable = false)
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+08:00")
    @JSONField(format = "yyyy-MM-dd HH:mm:ss")
    private Date createDate;

    @Column(name="ACCEPT_TIME")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JSONField(format = "yyyy-MM-dd HH:mm:ss")
    private Date acceptTime;

    @Column(name="HANDLE_TIME")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JSONField(format = "yyyy-MM-dd HH:mm:ss")
    private Date handleTime;

    private Long yellowTimes;
    private Long redTimes;
    private Integer isColse = Status.No.getStatus();
    private String colseDesc;
    private Long postId;
    private Integer isAccept;



    public Integer getIsColse() {
        return isColse;
    }

    public void setIsColse(Integer isColse) {
        this.isColse = isColse;
    }

    public String getColseDesc() {
        return colseDesc;
    }

    public void setColseDesc(String colseDesc) {
        this.colseDesc = colseDesc;
    }

    public Long getPostId() {
        return postId;
    }

    public void setPostId(Long postId) {
        this.postId = postId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Integer getIsAccept() {
        return isAccept;
    }

    public void setIsAccept(Integer isAccept) {
        this.isAccept = isAccept;
    }

    public String getPlateName() {
        return plateName;
    }

    public void setPlateName(String plateName) {
        this.plateName = plateName;
    }

    public String getAcceptUnitName() {
        return acceptUnitName;
    }

    public void setAcceptUnitName(String acceptUnitName) {
        this.acceptUnitName = acceptUnitName;
    }

    public String getMemberName() {
        return memberName;
    }

    public void setMemberName(String memberName) {
        this.memberName = memberName;
    }

    public Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    public Date getAcceptTime() {
        return acceptTime;
    }

    public void setAcceptTime(Date acceptTime) {
        this.acceptTime = acceptTime;
    }

    public Date getHandleTime() {
        return handleTime;
    }

    public void setHandleTime(Date handleTime) {
        this.handleTime = handleTime;
    }

    public Long getYellowTimes() {
        return yellowTimes;
    }

    public void setYellowTimes(Long yellowTimes) {
        this.yellowTimes = yellowTimes;
    }

    public Long getRedTimes() {
        return redTimes;
    }

    public void setRedTimes(Long redTimes) {
        this.redTimes = redTimes;
    }
}
