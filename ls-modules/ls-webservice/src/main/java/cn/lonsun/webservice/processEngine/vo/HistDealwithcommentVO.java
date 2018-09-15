package cn.lonsun.webservice.processEngine.vo;

import java.util.Date;

/**
 * Created by lonsun on 2014/12/16.
 */
public class HistDealwithcommentVO {
    public HistDealwithcommentVO(){}
    /**
     * 主键
     */
    private Long commentId;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 用户名称
     */
    private String userName;



    /**
     * 批示意见类型
     */
    private Long commentType =0L;

    /**
     * 批示意见字段
     */
    private String commentField;

    /**
     * 批示意见内容
     */
    private String commentText;

    public Long getCommentId() {
        return commentId;
    }

    public void setCommentId(Long commentId) {
        this.commentId = commentId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }



    public Long getCommentType() {
        return commentType;
    }

    public void setCommentType(Long commentType) {
        this.commentType = commentType;
    }

    public String getCommentField() {
        return commentField;
    }

    public void setCommentField(String commentField) {
        this.commentField = commentField;
    }

    public String getCommentText() {
        return commentText;
    }

    public void setCommentText(String commentText) {
        this.commentText = commentText;
    }
}
