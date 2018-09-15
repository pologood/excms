package cn.lonsun.govbbs.internal.vo;

import com.alibaba.fastjson.annotation.JSONField;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

/**
 * Created by zhangchao on 2017/2/17.
 */
public class BbsReplyListVO implements java.io.Serializable{

    private static final long serialVersionUID = 1L;


    private Long replyId;

    private String postTile;

    private Long postId;

    private Long plateId;

    private Long memberId;

    //(发帖人)
    private String memberName;

    private Long handleUnitId;

    //办理单位名称
    private String handleUnitName;

    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JSONField(format = "yyyy-MM-dd HH:mm:ss")
    private Date handleTime;

    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+08:00")
    @JSONField(format = "yyyy-MM-dd HH:mm:ss")
    private Date createDate;


    public Long getReplyId() {
        return replyId;
    }

    public void setReplyId(Long replyId) {
        this.replyId = replyId;
    }

    public String getPostTile() {
        return postTile;
    }

    public void setPostTile(String postTile) {
        this.postTile = postTile;
    }

    public Long getPlateId() {
        return plateId;
    }

    public void setPlateId(Long plateId) {
        this.plateId = plateId;
    }

    public Long getMemberId() {
        return memberId;
    }

    public void setMemberId(Long memberId) {
        this.memberId = memberId;
    }

    public String getMemberName() {
        return memberName;
    }

    public void setMemberName(String memberName) {
        this.memberName = memberName;
    }

    public Long getHandleUnitId() {
        return handleUnitId;
    }

    public void setHandleUnitId(Long handleUnitId) {
        this.handleUnitId = handleUnitId;
    }

    public String getHandleUnitName() {
        return handleUnitName;
    }

    public void setHandleUnitName(String handleUnitName) {
        this.handleUnitName = handleUnitName;
    }

    public Date getHandleTime() {
        return handleTime;
    }

    public void setHandleTime(Date handleTime) {
        this.handleTime = handleTime;
    }

    public Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    public Long getPostId() {
        return postId;
    }

    public void setPostId(Long postId) {
        this.postId = postId;
    }
}
